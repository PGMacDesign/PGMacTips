package com.pgmacdesign.pgmacutilities.adaptersandlisteners;

/**
 * This class serves as an interface link between long click events and an activity/ fragment.
 * Created by pmacdowell on 8/16/2016.
 */
public interface CustomLongClickCallbackLink {
    /**
     * For sending data between the event where this is long clicked and the activity / fragment
     * @param object Object
     * @param customTag int custom tag.
     * @param positionIfAvailable int position. This will be sent back as not null when there is
     *                            a reason to include it. That reason may be that the item selected
     *                            was chosen from a listview/ Recyclerview, in which case the
     *                            position can be helpful. If this is not set, it will send back
     *                            null instead.
     */
    public void itemLongClicked(Object object, Integer customTag, Integer positionIfAvailable);
}
