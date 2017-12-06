package com.pgmacdesign.pgmacutilities.adaptersandlisteners;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.pgmacdesign.pgmacutilities.R;
import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.AnimationUtilities;
import com.pgmacdesign.pgmacutilities.utilities.DisplayManagerUtilities;
import com.pgmacdesign.pgmacutilities.utilities.MiscUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pmacdowell on 2017-11-13.
 */

public class MultipurposeChoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {




    //Type of Adapter
    public static enum MultipurposeChoiceType {
        SINGLE_SELECT, MULTI_SELECT
    }

    private Drawable selectedCircle, unselectedCircle;
    private Integer selectedCircleResource, unselectedCircleResource;

    //Vars
    private OnTaskCompleteListener listener;
    private CustomClickCallbackLink clickLink;
    private CustomLongClickCallbackLink longClickLink;
    private MultipurposeChoiceType type;

    //Dataset List
    private List<MultipurposeChoiceObject> mListObjects;

    //UI
    private LayoutInflater mInflater;

    //Misc
    private Context context;
    private boolean oneSelectedAnimate;
    private DisplayManagerUtilities dmu;
    private int customClickLinkTag, customLongClickLinkTag;

    public MultipurposeChoiceAdapter(@NonNull Context context,
                                     @NonNull MultipurposeChoiceType type1,
                                     CustomClickCallbackLink clickLink,
                                     CustomLongClickCallbackLink longClickLink,
                                     int selectedImageResource, int unselectedImageResource){
        this.type = type1;
        this.context = context;
        this.clickLink = clickLink;
        this.longClickLink = longClickLink;
        this.mInflater = LayoutInflater.from(context);
        this.customClickLinkTag = this.customLongClickLinkTag = -1;
        this.dmu = new DisplayManagerUtilities(context);
        try {
            this.selectedCircle = ContextCompat.getDrawable(context, selectedImageResource);
            this.unselectedCircle = ContextCompat.getDrawable(context, unselectedImageResource);
            this.selectedCircleResource = null;
            this.unselectedCircleResource = null;
        } catch (Resources.NotFoundException e){
            this.selectedCircle = null;
            this.unselectedCircle = null;
            this.selectedCircleResource = selectedImageResource;
            this.unselectedCircleResource = unselectedImageResource;
        } catch (Exception e){
            e.printStackTrace();
            this.selectedCircle = null;
            this.unselectedCircle = null;
            this.selectedCircle = ContextCompat.getDrawable(context, R.drawable.shutter_black);
            this.unselectedCircle = ContextCompat.getDrawable(context, R.drawable.shutter_white);
        }
    }

    /**
     * Set the custom click listener callback int tag to reference.
     * @param customClickLinkTag Tag to compare against on click results. If -1 is
     *                           passed, will be ignored.
     */
    public void setCustomClickLinkTag(int customClickLinkTag) {
        this.customClickLinkTag = customClickLinkTag;
    }

    /**
     * Set the custom long click listener callback int tag to reference.
     * @param customLongClickLinkTag Tag to compare against on long click results.
     *                               If -1 is passed, will be ignored.
     */
    public void setCustomLongClickLinkTag(int customLongClickLinkTag) {
        this.customLongClickLinkTag = customLongClickLinkTag;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (type){
            case MULTI_SELECT:
                view = mInflater.inflate(R.layout.selection_adapter_multi_layout, 
                        parent, false);
                MultipleSelectChoice viewHolder = new MultipleSelectChoice(view);
                return viewHolder;

            case SINGLE_SELECT:
            default:
                this.type = MultipurposeChoiceType.SINGLE_SELECT;
                view = mInflater.inflate(R.layout.selection_adapter_single_layout, 
                        parent, false);
                SingleSelectChoice viewHolder1 = new SingleSelectChoice(view);
                return viewHolder1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder0, final int position) {

        final MultipurposeChoiceObject currentObj = mListObjects.get(position);

        final CustomClickListener clickListener = new CustomClickListener(
                clickLink, (customClickLinkTag == -1) ?
                        PGMacUtilitiesConstants.TAG_MULTIPURPOSE_CHOICE_CLICK_ADAPTER :
                        customClickLinkTag, currentObj);
        final CustomLongClickListener longClickListener = new CustomLongClickListener(
                longClickLink, (customLongClickLinkTag == -1) ?
                        PGMacUtilitiesConstants.TAG_MULTIPURPOSE_CHOICE_LONG_CLICK_ADAPTER :
                        customLongClickLinkTag, currentObj);

        if(currentObj == null){
            return;
        }

        String imageUrl = currentObj.getImageUrl();
        String desc = currentObj.getDescription();

        if(type == MultipurposeChoiceType.MULTI_SELECT){
            final MultipleSelectChoice holder = (MultipleSelectChoice) holder0;
            holder.selection_adapter_multi_tv.setText(desc);
            // TODO: 2017-06-15 NOTE! This will slow performance in future if not scaled correctly
            if(currentObj.isSelected()){
                if(selectedCircle != null) {
                    holder.selection_adapter_multi_left_iv.setImageDrawable(selectedCircle);
                } else if (selectedCircleResource != null){
                    holder.selection_adapter_multi_left_iv.setImageResource(selectedCircleResource);
                } else {
                    holder.selection_adapter_multi_left_iv.setImageDrawable(null);
                }
            } else {
                holder.selection_adapter_multi_left_iv.setImageDrawable(unselectedCircle);
                if(selectedCircle != null) {
                    holder.selection_adapter_multi_left_iv.setImageDrawable(selectedCircle);
                } else if (unselectedCircleResource != null){
                    holder.selection_adapter_multi_left_iv.setImageResource(unselectedCircleResource);
                } else {
                    holder.selection_adapter_multi_left_iv.setImageDrawable(null);
                }
            }
            if(oneSelectedAnimate){
                AnimationUtilities.animateMyView(
                        holder.selection_adapter_multi_left_iv, 250, Techniques.Pulse);
            }
            holder.selection_adapter_multi_main_layout.setVisibility(View.VISIBLE);
            holder.selection_adapter_multi_main_layout.setOnClickListener(clickListener);
            holder.selection_adapter_multi_main_layout.setOnLongClickListener(longClickListener);

        } else if (type == MultipurposeChoiceType.SINGLE_SELECT){
            final SingleSelectChoice holder = (SingleSelectChoice) holder0;
            holder.selection_adapter_single_tv.setText(desc);
            if(currentObj.isSelected()){
                if(selectedCircle != null) {
                    holder.selection_adapter_single_left_icon.setImageDrawable(selectedCircle);
                } else if (unselectedCircleResource != null){
                    holder.selection_adapter_single_left_icon.setImageResource(selectedCircleResource);
                } else {
                    holder.selection_adapter_single_left_icon.setImageDrawable(null);
                }
            } else {
                if(unselectedCircle != null) {
                    holder.selection_adapter_single_left_icon.setImageDrawable(unselectedCircle);
                } else if (unselectedCircleResource != null){
                    holder.selection_adapter_single_left_icon.setImageResource(unselectedCircleResource);
                } else {
                    holder.selection_adapter_single_left_icon.setImageDrawable(null);
                }
            }
            holder.selection_adapter_single_main_layout.setVisibility(View.VISIBLE);
            holder.selection_adapter_single_main_layout.setOnClickListener(clickListener);
            holder.selection_adapter_single_main_layout.setOnLongClickListener(longClickListener);

        }

        if(oneSelectedAnimate){
            //
        } else {
            //
        }
        this.oneSelectedAnimate = false;
    }

    @Override
    public int getItemCount() {
        if(MiscUtilities.isListNullOrEmpty(mListObjects)){
            return 0;
        } else {
            return mListObjects.size();
        }
    }

    /**
     * Update one object in the list
     * @param position Position to update
     * @param mObject Single object to update. If null, will be ignored (Call remove instead).
     *                {@link MultipurposeChoiceObject}
     */
    public void updateOneObject(int position,  MultipurposeChoiceObject mObject){
        if(mObject == null){
            return;
        }
        if(!MiscUtilities.isListNullOrEmpty(this.mListObjects)){
            try {
                this.oneSelectedAnimate = true;
                this.mListObjects.set(position, mObject);
                notifyItemChanged(position);
            } catch (ArrayIndexOutOfBoundsException aio){
                aio.printStackTrace();
            }
        }
    }

    /**
     * Remove a single object from the list
     * @param position Position to remove
     */
    public void removeOneObject(int position){
        if(!MiscUtilities.isListNullOrEmpty(this.mListObjects)){
            try {
                this.mListObjects.remove(position);
                notifyItemChanged(position);
            } catch (ArrayIndexOutOfBoundsException aio){
                aio.printStackTrace();
            }
        }
    }

    /**
     * Set data list
     * @param mListObjects {@link MultipurposeChoiceObject}
     */
    public void setListObjects(List<MultipurposeChoiceObject> mListObjects){
        this.mListObjects = mListObjects;
        this.notifyDataSetChanged();
    }

    class MultipleSelectChoice extends RecyclerView.ViewHolder {

        private LinearLayout selection_adapter_multi_main_layout;
        private ImageView selection_adapter_multi_left_iv;
        private TextView selection_adapter_multi_tv;

        public MultipleSelectChoice(View itemView) {
            super(itemView);
            this.selection_adapter_multi_main_layout = (LinearLayout) itemView.findViewById(
                    R.id.selection_adapter_multi_main_layout);
            this.selection_adapter_multi_left_iv = (ImageView) itemView.findViewById(
                    R.id.selection_adapter_multi_left_iv);
            this.selection_adapter_multi_tv = (TextView) itemView.findViewById(
                    R.id.selection_adapter_multi_tv);
        }
    }

    class SingleSelectChoice extends RecyclerView.ViewHolder {

        private LinearLayout selection_adapter_single_main_layout;
        private ImageView selection_adapter_single_left_icon;
        private TextView selection_adapter_single_tv;

        public SingleSelectChoice(View itemView) {
            super(itemView);
            this.selection_adapter_single_main_layout = (LinearLayout) itemView.findViewById(
                    R.id.selection_adapter_single_main_layout);
            this.selection_adapter_single_left_icon = (ImageView) itemView.findViewById(
                    R.id.selection_adapter_single_left_icon);
            this.selection_adapter_single_tv = (TextView) itemView.findViewById(
                    R.id.selection_adapter_single_tv);
        }
    }

    public static class MultipurposeChoiceObject {
        private String description;
        private String imageUrl;
        private boolean isSelected;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    /**
     * Simple utility for building a list if you do not have image URLs to use, only titles
     * @param objs
     * @return
     */
    public static List<MultipurposeChoiceObject> buildSimpleObjectList(List<String> objs){
        return buildSimpleObjectList(objs, null);
    }

    /**
     * Simple utility for building a list if you do not have image URLs to use, only titles
     * @param objs
     * @return
     */
    public static List<MultipurposeChoiceObject> buildSimpleObjectList(List<String> objs,
                                                                       List<String> selectedStrings){
        if(MiscUtilities.isListNullOrEmpty(objs)){
            return new ArrayList<>();
        }
        List<MultipurposeChoiceObject> aList = new ArrayList<>();
        for(String str : objs){
            MultipurposeChoiceObject o = new MultipurposeChoiceObject();
            o.setDescription(str);
            if(!MiscUtilities.isListNullOrEmpty(selectedStrings)) {
                for (String ss : selectedStrings) {
                    if (!StringUtilities.isNullOrEmpty(ss)) {
                        if (ss.equals(str)) {
                            o.setSelected(true);
                        }
                    }
                }
            }
            aList.add(o);
        }
        return aList;
    }

}
