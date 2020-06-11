package com.example.biennale_go.Classes;

public class poiClass {
    private String name;
    private String description;
    private String address;
    private String image;
    private Double latitude;
    private Double longitude;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public poiClass(){
        super();
    }

    public poiClass(String name, String description, String address, String image, Double latitude, Double longitude) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
