import org.apache.spark.sql.types._
import org.apache.spark.sql.Column
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

def time[T](proc: => T): T = {
    val start=System.nanoTime()
    val res = proc // call the code
    val end = System.nanoTime()
    println("Time elapsed: " + (end-start)/1000 + " microsecs")
    res
}

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

val tripsDF = spark.read.schema(schema).option("header", true).csv("data/yellow_tripdata_2016-01.short.csv")
val trips = tripsDF.where($"pickup_longitude" =!= 0 && $"pickup_latitude" =!= 0 && $"dropoff_longitude" =!= 0 && $"dropoff_latitude" =!= 0)
