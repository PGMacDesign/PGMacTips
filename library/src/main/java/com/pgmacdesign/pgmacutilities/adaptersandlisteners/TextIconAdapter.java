package com.pgmacdesign.pgmacutilities.adaptersandlisteners;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgmacdesign.pgmacutilities.R;
import com.pgmacdesign.pgmacutilities.datamodels.SimpleTextIconObject;
import com.pgmacdesign.pgmacutilities.utilities.MiscUtilities;

import java.util.List;

/**
 * Simple Adapter for text on the left and icon on the right.
 * Created by pmacdowell on 2017-11-17.
 */
public class TextIconAdapter extends ArrayAdapter<SimpleTextIconObject>{

    //Global Vars
    private Context context;
    private List<SimpleTextIconObject> simpleTextIconObjects;
    private CustomClickListener clickListener;
    private CustomClickCallbackLink clickCallbackLink;
    private Integer callbackTag;

    //Custom Set Variables
    private Integer textColor, backgroundColor;
    private Drawable backgroundDrawable;
    private Float textSize;

    public TextIconAdapter(@NonNull Context context, int resource,
                           @NonNull List<SimpleTextIconObject> simpleTextIconObjects,
                           CustomClickCallbackLink clickCallbackLink,
                           Integer callbackTag) {
        super(context, resource, simpleTextIconObjects);
        this.context = context;
        this.simpleTextIconObjects = simpleTextIconObjects;
        this.clickCallbackLink = clickCallbackLink;
        this.callbackTag = callbackTag;
        this.init();
    }

    public void setSimpleTextIconObjects(List<SimpleTextIconObject> simpleTextIconObjects){
        this.simpleTextIconObjects = simpleTextIconObjects;
    }

    @Override
    public int getCount() {
        if(!MiscUtilities.isListNullOrEmpty(this.simpleTextIconObjects)){
            return this.simpleTextIconObjects.size();
        }
        return super.getCount();
    }

    private void init(){
        this.textColor = ContextCompat.getColor(context, R.color.black);
        this.backgroundColor = ContextCompat.getColor(context, R.color.white);
        this.backgroundDrawable = null;
        this.textSize = null;
    }

    public void setTextColor(int textColor) {
        this.textColor = ContextCompat.getColor(context, textColor);
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = ContextCompat.getColor(context, backgroundColor);
    }

    public void setBackgroundDrawable(Drawable backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
    }

    @Nullable
    @Override
    public SimpleTextIconObject getItem(int position) {
        if(!MiscUtilities.isListNullOrEmpty(this.simpleTextIconObjects)){
            if(position >= 0 && position < this.simpleTextIconObjects.size()) {
                return this.simpleTextIconObjects.get(position);
            }
        }
        return super.getItem(position);
    }

    private class ViewHolder {
        ImageView simple_text_icon_item_iv;
        TextView simple_text_icon_item_tv;
        RelativeLayout simple_text_icon_item_root;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        SimpleTextIconObject sdo = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.simple_text_icon_item, null);
            holder = new ViewHolder();
            holder.simple_text_icon_item_tv = (TextView) convertView.findViewById(
                    R.id.simple_text_icon_item_tv);
            holder.simple_text_icon_item_iv = (ImageView) convertView.findViewById(
                    R.id.simple_text_icon_item_iv);
            holder.simple_text_icon_item_root = (RelativeLayout) convertView.findViewById(
                    R.id.simple_text_icon_item_root);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(this.textColor != null){
            holder.simple_text_icon_item_tv.setTextColor(textColor);
        }
        if(this.backgroundColor != null){
            holder.simple_text_icon_item_root.setBackgroundColor(textColor);
        }
        if(this.backgroundDrawable != null){
            holder.simple_text_icon_item_root.setBackground(backgroundDrawable);
        }
        if(this.textSize != null){
            holder.simple_text_icon_item_tv.setTextSize(textSize);
        }

        if(sdo == null){
            return convertView;
        }

        if(callbackTag != null &&  clickCallbackLink != null){
            clickListener = new CustomClickListener(clickCallbackLink, callbackTag);
            holder.simple_text_icon_item_root.setOnClickListener(clickListener);
        }

        holder.simple_text_icon_item_tv.setText(sdo.getText());
        Integer resource = sdo.getImageResource();
        Drawable drawable = sdo.getImageDrawable();
        if(drawable == null && resource == null){
            return convertView;
        }
        if(resource != null) {
            holder.simple_text_icon_item_iv.setImageResource(resource);
        }
        if(drawable != null){
            holder.simple_text_icon_item_iv.setImageDrawable(drawable);
        }

        return convertView;
    }



}
