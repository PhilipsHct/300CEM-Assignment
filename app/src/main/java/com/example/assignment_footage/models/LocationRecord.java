package com.example.assignment_footage.models;

import java.sql.Time;

public class LocationRecord {
    private int id;
    private int footageId;
    private double longitude;
    private double latitude;
    private Time recordTime;

    public LocationRecord(){}

    public LocationRecord(int footageId, double longitude, double latitude, Time recordTime) {
        this.footageId = footageId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.recordTime = recordTime;
    }

    public LocationRecord(int id, int footageId, double longitude, double latitude, Time recordTime) {
        this.id = id;
        this.footageId = footageId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.recordTime = recordTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFootageId() {
        return footageId;
    }

    public void setFootageId(int footageId) {
        this.footageId = footageId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Time getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Time recordTime) {
        this.recordTime = recordTime;
    }
}
