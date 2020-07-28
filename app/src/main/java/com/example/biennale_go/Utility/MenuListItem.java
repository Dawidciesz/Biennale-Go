package com.example.biennale_go.Utility;

import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.AppCompatImageView;

public class MenuListItem {

    private String name;
    private Drawable image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuListItem(String name, Drawable image, int score, double distanceTraveled) {
        this.name = name;
        this.image = image;
        this.score = score;
        this.distanceTraveled = distanceTraveled;
    }

    private int score;

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    private double distanceTraveled;

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
