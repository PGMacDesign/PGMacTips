package com.pgmacdesign.pgmacutilities.datamodels;

import android.graphics.drawable.Drawable;

/**
 * Created by pmacdowell on 2017-11-17.
 */

public class SimpleTextIconObject {
    private String text;
    private Integer imageResource;
    private Drawable imageDrawable;

    public SimpleTextIconObject(String text, Integer imageResource){
        this.imageResource = imageResource;
        this.text = text;
    }

    public SimpleTextIconObject(String text, Drawable imageDrawable){
        this.imageDrawable = imageDrawable;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Integer getImageResource() {
        return imageResource;
    }

    public Drawable getImageDrawable() {
        return imageDrawable;
    }

}
