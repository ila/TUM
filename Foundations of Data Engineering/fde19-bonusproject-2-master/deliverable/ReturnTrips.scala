import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Column
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.broadcast.Broadcast

// do not use udfs
// lit(dist) has only 150 possible values, precompute them (50-200)
// calculate the join with 200 before filtering
// lit(lit(dist))

object ReturnTrips {

  val R : Double = 6371.0  // kilometres
  val hours = 8 * 60 * 60  // converting 8 hours to seconds

	val haversine = (lat1 : Double, lon1 : Double, lat2 : Double, lon2 : Double) => {

  		// taken from RosettaCode
  		// everything has to be double!!!
  		// not converting to radians since values are radians already

		  import scala.math._

      val dLat = abs(lat2 - lat1)
			val dLon = abs(lon2 - lon1)
 
			val a = pow(sin(dLat / 2.0), 2.0) + pow(sin(dLon/ 2.0 ), 2.0) * cos(lat1) * cos(lat2)
			val c : Double = 2 * asin(sqrt(a))

			R * c * 1000

  }

  val toRadians = (degree : Double) => {

    import scala.math._  // double import meh

    degree * Pi / 180

  }


	def compute(trips : Dataset[Row], dist : Double, spark : SparkSession) : Dataset[Row] = {

		import spark.implicits._
		spark.conf.set("spark.default.parallelism", 6)
		
		def distance = udf(haversine)
		def radians = udf(toRadians)

    // let's compute the new needed columns
    // first of all latitude and longitude to radians
    // then timestamp to epoch seconds
    // trying to cast to Long (8 bits) might be faster

    // working with expr("INTERVAL 8 HOURS") and +8 does not work
    // floor is required for type conversion?

    val tripsExtended = trips.withColumn("pickup_latitude_a", radians('pickup_latitude)).withColumn("pickup_longitude_a", radians('pickup_longitude)).drop("pickup_longitude").withColumn("dropoff_longitude_a", radians('dropoff_longitude)).withColumn("dropoff_latitude_a", radians('dropoff_latitude)).withColumn("pickup_time", unix_timestamp($"tpep_pickup_datetime")).withColumn("dropoff_time", unix_timestamp($"tpep_dropoff_datetime"))

		// now let's reduce the dataset dimension

		val tripsSubset = tripsExtended.select("pickup_time","dropoff_time","pickup_longitude_a","pickup_latitude_a","dropoff_longitude_a","dropoff_latitude_a")

		// and make further calculations for hours
    // might be faster to add them at the beginning?

		val tripsSubsetExtended = tripsSubset.withColumn("pickup_time_bucket",floor($"pickup_time"/ hours))
			.withColumn("dropoff_time_bucket",floor($"dropoff_time" / hours)).withColumn("pickup_latitude_b",floor($"pickup_latitude_a"/ (lit(dist) / (R * 1000))))
			.withColumn("dropoff_latitude_b",floor($"dropoff_latitude_a"/ (lit(dist) / (R * 1000))))

    // it is possible now to create buckets (values now should have the same unit)
    // caching slows down the result

		val tripsBucket = broadcast(tripsSubsetExtended
				.withColumn("dropoff_time_bucket", explode(array($"dropoff_time_bucket",$"dropoff_time_bucket" + 1)))
				.withColumn("pickup_latitude_b", explode(array($"pickup_latitude_b" - 1,$"pickup_latitude_b",$"pickup_latitude_b" + 1)))
				.withColumn("dropoff_latitude_b", explode(array($"dropoff_latitude_b" - 1, $"dropoff_latitude_b",$"dropoff_latitude_b" + 1)))).cache()

		// now let's join on buckets

		val tripsJoin = tripsBucket.as("a").join(tripsSubsetExtended.as("b"), ($"a.pickup_latitude_b" === $"b.dropoff_latitude_b") && ($"a.dropoff_latitude_b"=== $"b.pickup_latitude_b") && ($"a.dropoff_time_bucket" === $"b.pickup_time_bucket"))

    // applying constraints
    // filtering after adding all columns somehow makes it faster?

    val result = tripsJoin.filter(distance($"a.dropoff_latitude_a", $"a.dropoff_longitude_a", $"b.pickup_latitude_a", $"b.pickup_longitude_a") < lit(dist) && distance($"b.dropoff_latitude_a", $"b.dropoff_longitude_a", $"a.pickup_latitude_a", $"a.pickup_longitude_a") < lit(dist)).filter(($"a.dropoff_time" + hours) > $"b.pickup_time").filter($"a.dropoff_time" < $"b.pickup_time")
		
		result
		
	}
} 

