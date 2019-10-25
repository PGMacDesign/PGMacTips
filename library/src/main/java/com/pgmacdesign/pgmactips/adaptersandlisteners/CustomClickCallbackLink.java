package com.pgmacdesign.pgmactips.adaptersandlisteners;

import androidx.annotation.Nullable;

/**
 * Created by pmacdowell on 8/16/2016.
 */
public interface CustomClickCallbackLink {
    /**
     * For sending data between the event where this is clicked and the activity / fragment
     * @param object Object
     * @param customTag Object(s) being sent back. if you want, you can use if(obj instanceof XX)
     *                  and use that as a separator.
     * @param positionIfAvailable int position. This will be sent back as not null when there is
     *                            a reason to include it. That reason may be that the item selected
     *                            was chosen from a listview/ Recyclerview, in which case the
     *                            position can be helpful. If this is not set, it will send back
     *                            null instead.
     */
    public void itemClicked(@Nullable Object object,
                            @Nullable Integer customTag,
                            @Nullable Integer positionIfAvailable);
}
