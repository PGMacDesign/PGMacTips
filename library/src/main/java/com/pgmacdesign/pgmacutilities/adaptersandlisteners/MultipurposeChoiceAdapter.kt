package com.pgmacdesign.pgmacutilities.adaptersandlisteners

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.pgmacdesign.pgmacutilities.R
import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants
import com.pgmacdesign.pgmacutilities.utilities.*


/**
 * Created by Patrick-SSD2 on 11/4/2017.
 */
class MultipurposeChoiceAdapter(
        val context: Context,
        val type: MultipurposeChoiceType,
        val longClickLink: CustomLongClickCallbackLink,
        val clickLink: CustomClickCallbackLink,
        val selectedImageResourceId : Int,
        val unselectedImageResourceId : Int):
        RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    //Type of Adapter
    public enum class MultipurposeChoiceType {
        SINGLE_SELECT, MULTI_SELECT
    }

    //Variables
    private val mInflater: LayoutInflater;
    private val dmu: DisplayManagerUtilities;
    private val selectedCircle: Drawable;
    private val unselectedCircle: Drawable;

    //Init
    init {
        this.mInflater = LayoutInflater.from(context);
        this.dmu = DisplayManagerUtilities(context);
        this.unselectedCircle = ContextCompat.getDrawable(context, unselectedImageResourceId);
        this.selectedCircle = ContextCompat.getDrawable(context, selectedImageResourceId);
    }


    //Vars
    private val listener: OnTaskCompleteListener? = null

    //Dataset List
    private var mListObjects: MutableList<MultipurposeChoiceObject>? = null

    //Misc
    private var oneSelectedAnimate: Boolean = false



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (type) {
            MultipurposeChoiceAdapter.MultipurposeChoiceType.MULTI_SELECT -> {
                view = mInflater.inflate(R.layout.selection_adapter_multi_layout, parent, false)
                return MultipleSelectChoice(view)
            }

            MultipurposeChoiceAdapter.MultipurposeChoiceType.SINGLE_SELECT -> {
                view = mInflater.inflate(R.layout.selection_adapter_single_layout, parent, false)
                return SingleSelectChoice(view)
            }
            else -> {
                view = mInflater.inflate(R.layout.selection_adapter_single_layout, parent, false)
                return SingleSelectChoice(view)
            }
        }
    }

    override fun onBindViewHolder(holder0: RecyclerView.ViewHolder, position: Int) {

        val currentObj = mListObjects!![position]

        val clickListener = CustomClickListener(clickLink,
                PGMacUtilitiesConstants.TAG_MULTIPURPOSE_CHOICE_CLICK_ADAPTER, currentObj)
        val longClickListener = CustomLongClickListener(longClickLink,
                PGMacUtilitiesConstants.TAG_MULTIPURPOSE_CHOICE_LONG_CLICK_ADAPTER, currentObj)

        if (currentObj == null) {
            return
        }

        val imageUrl = currentObj.imageUrl
        val desc = currentObj.description

        if (type == MultipurposeChoiceType.MULTI_SELECT) {
            val holder = holder0 as MultipleSelectChoice
            holder.selection_adapter_multi_tv.text = desc
            if (currentObj.isSelected) {
                holder.selection_adapter_multi_faux_checkbox.setImageDrawable(selectedCircle)
            } else {
                holder.selection_adapter_multi_faux_checkbox.setImageDrawable(unselectedCircle)
            }
            if (oneSelectedAnimate) {
                AnimationUtilities.animateMyView(
                        holder.selection_adapter_multi_faux_checkbox, 250, Techniques.Pulse)
            }
            holder.selection_adapter_multi_main_layout.visibility = View.VISIBLE
            holder.selection_adapter_multi_main_layout.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    L.m("action up hit within multipurpose choice adapter. Currently at POS " + position)
                    clickLink.itemClicked(currentObj,
                            PGMacUtilitiesConstants.TAG_MULTIPURPOSE_CHOICE_CLICK_ADAPTER, position)
                }
                true
            }
            holder.selection_adapter_multi_main_layout.setOnClickListener(clickListener)
            holder.selection_adapter_multi_main_layout.setOnLongClickListener(longClickListener)

        } else if (type == MultipurposeChoiceType.SINGLE_SELECT) {
            val holder = holder0 as SingleSelectChoice
            holder.selection_adapter_single_tv.text = desc
            if (currentObj.isSelected) {
                holder.selection_adapter_single_faux_radiobutton.setImageDrawable(selectedCircle)
            } else {
                holder.selection_adapter_single_faux_radiobutton.setImageDrawable(unselectedCircle)
            }
            holder.selection_adapter_single_main_layout.visibility = View.VISIBLE
            holder.selection_adapter_single_main_layout.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    clickLink.itemClicked(currentObj,
                            PGMacUtilitiesConstants.TAG_MULTIPURPOSE_CHOICE_CLICK_ADAPTER, position)
                }
                true
            }
            //holder.selection_adapter_single_main_layout.setOnClickListener(clickListener);
            holder.selection_adapter_single_main_layout.setOnLongClickListener(longClickListener)

        }

        if (oneSelectedAnimate) {
            //
        } else {
            //
        }
        this.oneSelectedAnimate = false
    }

    override fun getItemCount(): Int {
        return if (MiscUtilities.isListNullOrEmpty(mListObjects)) {
            0
        } else {
            mListObjects!!.size
        }
    }

    /**
     * Update one object in the list
     * @param position Position to update
     * @param mObject Single object to update. If null, will be ignored (Call remove instead).
     * [MultipurposeChoiceObject]
     */
    fun updateOneObject(position: Int, mObject: MultipurposeChoiceObject?) {
        if (mObject == null) {
            return;
        }
        if (!MiscUtilities.isListNullOrEmpty(this.mListObjects)) {
            try {
                this.oneSelectedAnimate = true;
                this.mListObjects!![position] = mObject;
                notifyItemChanged(position);
            } catch (aio: ArrayIndexOutOfBoundsException) {
                aio.printStackTrace();
            }

        }
    }

    /**
     * Remove a single object from the list
     * @param position Position to remove
     */
    fun removeOneObject(position: Int) {
        if (!MiscUtilities.isListNullOrEmpty(this.mListObjects)) {
            try {
                this.mListObjects!!.removeAt(position);
                notifyItemChanged(position);
            } catch (aio: ArrayIndexOutOfBoundsException) {
                aio.printStackTrace();
            }

        }
    }

    /**
     * Set data list
     * @param mListObjects [MultipurposeChoiceObject]
     */
    fun setListObjects(mListObjects: MutableList<MultipurposeChoiceObject>) {
        this.mListObjects = mListObjects;
        this.notifyDataSetChanged();
    }

    internal inner class MultipleSelectChoice(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val selection_adapter_multi_main_layout: LinearLayout;
        val selection_adapter_multi_faux_checkbox: ImageView;
        val selection_adapter_multi_tv: TextView;

        init {
            this.selection_adapter_multi_main_layout = itemView.findViewById<LinearLayout>(
                    R.id.selection_adapter_multi_main_layout);
            this.selection_adapter_multi_faux_checkbox = itemView.findViewById<ImageView>(
                    R.id.selection_adapter_multi_faux_checkbox);
            this.selection_adapter_multi_tv = itemView.findViewById<TextView>(
                    R.id.selection_adapter_multi_tv);
        }
    }

    internal inner class SingleSelectChoice(view: View) : RecyclerView.ViewHolder(view) {

        val selection_adapter_single_main_layout: LinearLayout;
        val selection_adapter_single_faux_radiobutton: ImageView;
        val selection_adapter_single_tv: TextView;

        init {
            this.selection_adapter_single_main_layout = view.findViewById<LinearLayout>(
                    R.id.selection_adapter_single_main_layout);
            this.selection_adapter_single_faux_radiobutton = view.findViewById<ImageView>(
                    R.id.selection_adapter_single_faux_radiobutton);
            this.selection_adapter_single_tv = view.findViewById<TextView>(
                    R.id.selection_adapter_single_tv);
        }
    }

    companion object {
        class MultipurposeChoiceObject {
            var description: String? = null;
            var imageUrl: String? = null;
            var isSelected: Boolean = false;
        }

        /**
         * Simple utility for building a list if you do not have image URLs to use, only titles
         * @param objs
         * @return
         */
        fun buildSimpleObjectList(objs: List<String>): List<MultipurposeChoiceObject> {
            return buildSimpleObjectList(objs, null);
        }

        /**
         * Simple utility for building a list if you do not have image URLs to use, only titles
         * @param objs
         * @return
         */
        fun buildSimpleObjectList(objs: List<String>,
                                  selectedStrings: List<String>?): List<MultipurposeChoiceObject> {
            if (MiscUtilities.isListNullOrEmpty(objs)) {
                return ArrayList();
            }
            val aList = mutableListOf<MultipurposeChoiceObject>();
            for (str in objs) {
                val o = MultipurposeChoiceObject();
                o.description = str;
                if (!MiscUtilities.isListNullOrEmpty(selectedStrings)) {
                    for (ss in selectedStrings!!) {
                        if (!StringUtilities.isNullOrEmpty(ss)) {
                            if (ss == str) {
                                o.isSelected = true;
                            }
                        }
                    }
                }
                aList.add(o)
            }
            return aList
        }

    }

}