package com.pgmacdesign.pgmacutilities.adaptersandlisteners;

import android.support.annotation.NonNull;
import android.view.View;

import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.L;

/**
 * Created by pmacdowell on 8/16/2016.
 */
public class CustomClickListener  implements View.OnClickListener{

    private CustomClickCallbackLink link;
    private Integer customTag;
    private Integer position;
    private Object obj;

    public CustomClickListener(@NonNull CustomClickCallbackLink link, Integer customTag){
        this.link = link;
        this.customTag = customTag;
        this.obj = null;
        this.position = null;
    }
    public CustomClickListener(@NonNull CustomClickCallbackLink link, Integer customTag, Object obj){
        this.link = link;
        this.customTag = customTag;
        this.obj = obj;
        this.position = null;
    }
    public CustomClickListener(@NonNull CustomClickCallbackLink link, Integer customTag, Object obj, Integer position){
        this.link = link;
        this.customTag = customTag;
        this.obj = obj;
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        if(link == null){
            L.m("Null Custom Click Callback Link. Did you forget to initialize it?");
            return;
        }
        if(customTag == null){
            this.customTag = PGMacUtilitiesConstants.TAG_CLICK_NO_TAG_SENT;
        }
        link.itemClicked(obj, customTag, position);
    }
}
