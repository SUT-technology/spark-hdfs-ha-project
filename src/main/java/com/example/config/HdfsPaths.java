package com.example.config;

public class HdfsPaths {
    private final String baseUri;

    public HdfsPaths(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getTripDataPath() {
        return baseUri + "/data/taxi/yellow_tripdata_2022-01.parquet";
    }

    public String getZoneLookupPath() {
        return baseUri + "/data/taxi/taxi_zone_lookup.csv";
    }

    public String getQ1OutputPath() {
        return baseUri + "/output/q1_long_trips.parquet";
    }

    public String getQ2OutputPath() {
        return baseUri + "/output/q2_avg_fare_by_zone.parquet";
    }

    public String getQ3OutputPath() {
        return baseUri + "/output/q3_revenue_by_borough.parquet";
    }

    public String getQ4OutputPath() {
        return baseUri + "/output/q4_max_tip_per_day.parquet";
    }
}