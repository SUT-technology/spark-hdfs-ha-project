package com.example.service;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.*;

public class TaxiAnalysisService {

    public Dataset<Row> analyzeLongTrips(Dataset<Row> tripData) {
    return tripData
            .filter(col("passenger_count").gt(2).and(col("trip_distance").gt(5)))
            .withColumn(
                "duration_minutes",
                unix_timestamp(col("tpep_dropoff_datetime"))
                    .minus(unix_timestamp(col("tpep_pickup_datetime")))
                    .divide(60)
            )
            .orderBy(col("duration_minutes").desc());
}


    public Dataset<Row> analyzeAvgFareByZone(Dataset<Row> tripData, Dataset<Row> zoneLookup) {
        return tripData
                .join(zoneLookup, tripData.col("PULocationID").equalTo(zoneLookup.col("LocationID")))
                .groupBy("Zone", "Borough")
                .agg(avg("fare_amount").as("average_fare"))
                .orderBy(col("average_fare").desc());
    }
    

    public Dataset<Row> analyzeRevenueByBorough(Dataset<Row> tripData, Dataset<Row> zoneLookup) {
    return tripData
            .join(zoneLookup, tripData.col("DOLocationID").equalTo(zoneLookup.col("LocationID")))
            .groupBy("Borough")
            .agg(
                round(sum("total_amount"), 2).as("total_revenue"),
                count("*").as("trip_count")
            )
            .orderBy(col("total_revenue").desc());
}

    public Dataset<Row> analyzeMaxTipPerDay(Dataset<Row> tripData) {
        return tripData
                .withColumn("pickup_date", to_date(col("tpep_pickup_datetime")))
                .groupBy("pickup_date")
                .agg(max("tip_amount").as("max_tip"))
                .orderBy(col("pickup_date"));
    }

}