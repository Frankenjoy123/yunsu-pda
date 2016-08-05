package com.yunsoo.unity;

import android.graphics.drawable.Drawable;

/**
 * Created by Chen Jerry on 3/9/2015.
 */
public class ListItem {
    private Drawable image;
    private String title;
    private boolean isPackage;

	public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getPackage()
    {
        return isPackage;
    }

    public void setPackage(boolean isPackage)
    {
        this.isPackage = isPackage;
    }
}