package com.pgmacdesign.pgmactips.datamodels;

import android.graphics.drawable.Drawable;

/**
 * Created by pmacdowell on 2017-11-17.
 */

public class SimpleTextIconObject {
    private String text;
    private Integer imageResource;
    private Drawable imageDrawable;

    public SimpleTextIconObject(String text){
        this.text = text;
        this.imageDrawable = null;
        this.imageResource = null;
    }

    public SimpleTextIconObject(String text, Integer imageResource){
        this.imageResource = imageResource;
        this.imageDrawable = null;
        this.text = text;
    }

    public SimpleTextIconObject(String text, Drawable imageDrawable){
        this.imageDrawable = imageDrawable;
        this.imageResource = null;
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
