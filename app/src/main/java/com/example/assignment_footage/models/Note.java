package com.example.assignment_footage.models;

public class Note {
    private int id;
    private int locationRecordId;
    private String content;
    private String photoPath;

    public Note() {}
    public Note(int id, int locationRecordId, String content, String photoPath) {
        this.id = id;
        this.locationRecordId = locationRecordId;
        this.content = content;
        this.photoPath = photoPath;
    }
    public Note(int locationRecordId, String content, String photoPath) {
        this.locationRecordId = locationRecordId;
        this.content = content;
        this.photoPath = photoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocationRecordId() {
        return locationRecordId;
    }

    public void setLocationRecordId(int locationRecordId) {
        this.locationRecordId = locationRecordId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
