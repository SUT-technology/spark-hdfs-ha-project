package com.example.application;


import com.example.config.HdfsPaths;
import com.example.repository.TaxiDataRepository;
import com.example.service.TaxiAnalysisService;
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

        // 1. راه‌اندازی SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("Spark Taxi Analysis with Clean Architecture")
                .getOrCreate();

        // 2. ایجاد نمونه از کلاس‌ها
        HdfsPaths paths = new HdfsPaths(hdfsBaseUri);
        TaxiDataRepository repository = new TaxiDataRepository(spark);
        TaxiAnalysisService service = new TaxiAnalysisService();

        // 3. خواندن داده‌های ورودی
        Dataset<Row> tripData = repository.readTripData(paths.getTripDataPath());
        Dataset<Row> zoneLookup = repository.readZoneLookupData(paths.getZoneLookupPath());

        // 4. اجرای تحلیل‌ها و ذخیره نتایج
        Dataset<Row> q1Result = service.analyzeLongTrips(tripData);
        repository.writeData(q1Result, paths.getQ1OutputPath());
        System.out.println("Analysis 1 completed.");

        Dataset<Row> q2Result = service.analyzeAvgFareByZone(tripData, zoneLookup);
        repository.writeData(q2Result, paths.getQ2OutputPath());
        System.out.println("Analysis 2 completed.");
        
        // ... فراخوانی سایر تحلیل‌ها

        // 5. بستن SparkSession
        spark.stop();
    }
}