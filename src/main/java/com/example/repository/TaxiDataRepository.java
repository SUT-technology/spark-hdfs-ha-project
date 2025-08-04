package com.example.repository;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

public class TaxiDataRepository {
    private final SparkSession spark;

    public TaxiDataRepository(SparkSession spark) {
        this.spark = spark;
    }

    public Dataset<Row> readTripData(String path) {
        return spark.read().parquet(path);
    }

    public Dataset<Row> readZoneLookupData(String path) {
        return spark.read().option("header", "true").csv(path);
    }

    public void writeData(Dataset<Row> dataset, String path) {
        dataset.write().mode(SaveMode.Overwrite).parquet(path);
    }
}