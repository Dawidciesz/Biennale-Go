package com.example.biennale_go.Utility;

public class RankingItem {

    private String name;
    private int score;
    private double distanceTraveled;
    private boolean expanded;
    private String profilPictureColor;
     private String profilPictureId;

    public  String getProfilPictureColor() {
        return profilPictureColor;
    }


    public  String getProfilPictureId() {
        return profilPictureId;

    }
    public RankingItem(String name, int score, double distanceTraveled, String profilPictureId, String profilPictureColor) {
        this.name = name;
        this.score = score;
        this.distanceTraveled = distanceTraveled;
        this.profilPictureId = profilPictureId;
        this.profilPictureColor = profilPictureColor;
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

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
