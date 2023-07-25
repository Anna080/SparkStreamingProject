package com.power.powerconsumption

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object StructuredStreamingApp {
  def main(args: Array[String]) {
    val spark = SparkSession.builder
      .appName("StructuredStreamingApp")
      .master("local[*]") // Add this line
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    val schema = new StructType()
      .add("date", StringType)
      .add("time", StringType)
      .add("globalActivePower", DoubleType)
      .add("globalReactivePower", DoubleType)
      .add("voltage", DoubleType)
      .add("globalIntensity", DoubleType)
      .add("subMetering1", IntegerType)
      .add("subMetering2", IntegerType)
      .add("subMetering3", IntegerType)

    val df = spark.readStream
      .option("sep", ",")
      .option("header", "true")
      .schema(schema)
      .csv("resources/")

    df.writeStream
      .outputMode("append")
      .format("console")
      .start()
      .awaitTermination()
  }
}

