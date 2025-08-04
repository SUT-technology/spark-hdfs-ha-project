package com.example.service;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.*;

public class TaxiAnalysisService {

    public Dataset<Row> analyzeLongTrips(Dataset<Row> tripData) {
        return tripData
                .filter(col("passenger_count").gt(2).and(col("trip_distance").gt(5)))
                .withColumn("duration_minutes",
                        (col("tpep_dropoff_datetime").cast("long") - col("tpep_pickup_datetime").cast("long")) / 60)
                .orderBy(col("duration_minutes").desc());
    }

    public Dataset<Row> analyzeAvgFareByZone(Dataset<Row> tripData, Dataset<Row> zoneLookup) {
        return tripData
                .join(zoneLookup, tripData.col("PULocationID").equalTo(zoneLookup.col("LocationID")))
                .groupBy("Zone", "Borough")
                .agg(avg("fare_amount").as("average_fare"))
                .orderBy(col("average_fare").desc());
    }
    
    // ... سایر متدهای تحلیلی (analyzeRevenueByBorough, analyzeMaxTipPerDay)
}