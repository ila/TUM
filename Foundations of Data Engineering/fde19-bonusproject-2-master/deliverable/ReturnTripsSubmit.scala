import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Column
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

// time function is defined for usage
// input must be the first argument
// zero values are removed, rows are counted (to make sure the dataset has been fully read by Spark)

/**
 * Usage: TaxiTest [taxidataset]
 */
object ReturnTripSubmit {

  def time[T](proc: => T): T = {
    val start=System.nanoTime()
    val res = proc  // call the code
    val end = System.nanoTime()
    println("Time elapsed: " + (end-start) / 1000 + " microsecs")
    res
  }

  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("ReturnTripFinder Test")
      .getOrCreate()
    import spark.implicits._


    val taxifile = args(0)
    val schema = StructType(Array(
        StructField("VendorID", DataTypes.StringType,false),
        StructField("tpep_pickup_datetime", DataTypes.TimestampType,false),
        StructField("tpep_dropoff_datetime", DataTypes.TimestampType,false),
        StructField("passenger_count", DataTypes.IntegerType,false),
        StructField("trip_distance", DataTypes.DoubleType,false),
        StructField("pickup_longitude", DataTypes.DoubleType,false),
        StructField("pickup_latitude", DataTypes.DoubleType,false),
        StructField("RatecodeID", DataTypes.IntegerType,false),
        StructField("store_and_fwd_flag", DataTypes.StringType,false),
        StructField("dropoff_longitude", DataTypes.DoubleType,false),
        StructField("dropoff_latitude", DataTypes.DoubleType,false),
        StructField("payment_type", DataTypes.IntegerType,false),
        StructField("fare_amount", DataTypes.DoubleType,false),
        StructField("extra", DataTypes.DoubleType,false),
        StructField("mta_tax", DataTypes.DoubleType,false),
        StructField("tip_amount", DataTypes.DoubleType,false),
        StructField("tolls_amount", DataTypes.DoubleType,false),
        StructField("improvement_surcharge", DataTypes.DoubleType,false),
        StructField("total_amount", DataTypes.DoubleType, false)
    ))

    val tripsDF = spark.read.schema(schema).option("header", true).csv(taxifile)
    val trips = tripsDF.where($"pickup_longitude" =!= 0 && $"pickup_latitude" =!= 0 && $"dropoff_longitude" =!= 0 && $"dropoff_latitude" =!= 0).cache()
    // go through trips to make sure its in memory
    val rnd = trips.agg(count("*")).collect()

    println("R")// send ready signal to driver
    var dist = readLine().toInt // get dist in meters from driver
    var result = ReturnTrips.compute(trips, dist, spark).agg(count("*")).first.getLong(0)
    println(result)

    spark.stop()
  }
}
