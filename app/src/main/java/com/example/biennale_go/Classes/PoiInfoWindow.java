package com.example.biennale_go.Classes;

import android.graphics.Bitmap;

public class PoiInfoWindow {

    private String name;
    private String description;
    private String Image;
    private Bitmap bitMapImage;
    private String address;
    private Double longitude;
    private Double latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }


    public Bitmap getBitMapImage() {
        return bitMapImage;
    }

    public void setBitMapImage(Bitmap bitMapImage) {
        this.bitMapImage = bitMapImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}