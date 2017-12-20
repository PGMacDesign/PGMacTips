package com.pgmacdesign.pgmactips.adaptersandlisteners;

import android.support.annotation.NonNull;
import android.view.View;

import com.pgmacdesign.pgmactips.misc.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmactips.utilities.L;

/**
 * This class serves as a long click listener that can be implemented instead of just adding on the
 * standard long click listener. It is mostly useful when using tools like recyclerviews and listviews
 * as it sends back the object as well as other things (like position) if you deem it necessary.
 * Created by pmacdowell on 8/16/2016.
 */
public class CustomLongClickListener implements View.OnLongClickListener {

    private CustomLongClickCallbackLink link;
    private Integer customTag;
    private Integer position;
    private Object obj;

    /**
     * Constructor (overloaded). See description below
     */
    public CustomLongClickListener(@NonNull CustomLongClickCallbackLink link, Integer customTag){
        this.link = link;
        this.customTag = customTag;
        this.obj = null;
        this.position = null;
    }

    /**
     * Constructor (overloaded). See description below
     */
    public CustomLongClickListener(@NonNull CustomLongClickCallbackLink link, Integer customTag, Object obj){
        this.link = link;
        this.customTag = customTag;
        this.obj = obj;
        this.position = null;
    }

    /**
     * Constructor
     * @param link The CustomCallbackLink object to send data back on to
     * @param customTag The integer custom tag. Use this for sending back specific tags that you
     *                  want to reference and differentiate between.
     * @param obj Object being sent back. if you want, you can use if(obj instanceof XX) and use
     *            that as a separator.
     * @param position The integer position. This would be utilized in a recyclerview or listview
     *                 where you want to know the position of the list in order to re-update it
     *                 somehow. (IE calling adapter.updateOneObject() and passing in the position.
     */
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
