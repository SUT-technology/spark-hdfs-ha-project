package com.example.application;


import com.example.config.HdfsPaths;
import com.example.repository.TaxiDataRepository;
import com.example.service.TaxiAnalysisService;

import java.util.ArrayList;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Main {
    public static void main(String[] args) {
    if (args.length < 1) {
        System.err.println("Usage: SparkTaxiAnalysis <hdfs-base-uri>");
        System.exit(1);
    }
    String hdfsBaseUri = args[0];

    SparkSession spark = null;
    try {
        spark = SparkSession.builder()
                .appName("Spark Taxi Analysis with Clean Architecture")
                .master("local[*]")
                .getOrCreate();

        HdfsPaths paths = new HdfsPaths(hdfsBaseUri);
        TaxiDataRepository repository = new TaxiDataRepository(spark);
        TaxiAnalysisService service = new TaxiAnalysisService();

        Dataset<Row> tripData = repository.readTripData(paths.getTripDataPath());
        Dataset<Row> zoneLookup = repository.readZoneLookupData(paths.getZoneLookupPath());

        Dataset<Row> q1Result = service.analyzeLongTrips(tripData);
        repository.writeData(q1Result, paths.getQ1OutputPath());
        System.out.println("Analysis 1 completed.");

        Dataset<Row> q2Result = service.analyzeAvgFareByZone(tripData, zoneLookup);
        repository.writeData(q2Result, paths.getQ2OutputPath());
        System.out.println("Analysis 2 completed.");

        Dataset<Row> q3Result = service.analyzeRevenueByBorough(tripData, zoneLookup);
        repository.writeData(q3Result, paths.getQ3OutputPath());
        System.out.println("Analysis 3 completed.");

        Dataset<Row> q4Result = service.analyzeMaxTipPerDay(tripData);
        repository.writeData(q4Result, paths.getQ4OutputPath());
        System.out.println("Analysis 4 completed.");
    } catch (Exception e) {
        System.err.println("Error during Spark job execution: " + e.getMessage());
        e.printStackTrace();
    } finally {
        if (spark != null) {
            spark.stop();
        }
    }
}

}