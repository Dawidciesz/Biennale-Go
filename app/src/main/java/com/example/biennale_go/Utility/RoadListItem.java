package com.example.biennale_go.Utility;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class RoadListItem {

    private String name;
    private Bitmap image;
    private String description;
    private boolean expanded;
    private ArrayList polyline;
    private String color;
    public RoadListItem(String name, String description, ArrayList polyline, String color) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.polyline = polyline;
        this.color = color;
    }
    public RoadListItem(String name, Bitmap image, String description, ArrayList polyline, String color) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.polyline = polyline;
        this.color = color;
    }

    public ArrayList getPolyline() {
        return polyline;
    }

    public void setPolyline(ArrayList polyline) {
        this.polyline = polyline;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
