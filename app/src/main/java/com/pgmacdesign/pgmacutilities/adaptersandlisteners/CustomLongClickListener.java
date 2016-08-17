package com.pgmacdesign.pgmacutilities.adaptersandlisteners;

import android.support.annotation.NonNull;
import android.view.View;

import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.L;

/**
 * Created by pmacdowell on 8/16/2016.
 */
public class CustomLongClickListener implements View.OnLongClickListener {

    private CustomLongClickCallbackLink link;
    private Integer customTag;
    private Integer position;
    private Object obj;

    public CustomLongClickListener(@NonNull CustomLongClickCallbackLink link, Integer customTag){
        this.link = link;
        this.customTag = customTag;
        this.obj = null;
        this.position = null;
    }
    public CustomLongClickListener(@NonNull CustomLongClickCallbackLink link, Integer customTag, Object obj){
        this.link = link;
        this.customTag = customTag;
        this.obj = obj;
        this.position = null;
    }
    public CustomLongClickListener(@NonNull CustomLongClickCallbackLink link, Integer customTag, Object obj, Integer position){
        this.link = link;
        this.customTag = customTag;
        this.obj = obj;
        this.position = position;
    }

    @Override
    public boolean onLongClick(View view) {
        if(link == null){
            L.m("Null Custom Long Click Callback Link. Did you forget to initialize it?");
            return false;
        }
        if(customTag == null) {
            this.customTag = PGMacUtilitiesConstants.TAG_LONG_CLICK_NO_TAG_SENT;
        }
        link.itemLongClicked(obj, customTag, position);
        return false;
    }

    /*

     */
}
