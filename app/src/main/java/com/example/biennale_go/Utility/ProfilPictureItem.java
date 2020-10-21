package com.example.biennale_go.Utility;

import android.graphics.drawable.Drawable;

public class ProfilPictureItem {
    Drawable image;

    public ProfilPictureItem(Drawable image, String imageName) {
        this.image = image;
        this.imageName = imageName;
    }

    String imageName;
    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
