package com.example.biennale_go.Utility;

public class MenuListItem {

    private String name;
    private int score;
    private double distanceTraveled;

    public MenuListItem(String name, int score, double distanceTraveled) {
        this.name = name;
        this.score = score;
        this.distanceTraveled = distanceTraveled;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

}
