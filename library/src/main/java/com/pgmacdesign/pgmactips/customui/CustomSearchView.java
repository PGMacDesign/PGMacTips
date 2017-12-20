package com.pgmacdesign.pgmactips.customui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import com.pgmacdesign.pgmactips.utilities.StringUtilities;

/**
 * Created by pmacdowell on 2017-10-18.
 */

public class CustomSearchView extends SearchView {

    private Context context;
    private LayoutInflater inflater;

    public CustomSearchView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        this.inflater = LayoutInflater.from(context);
        //Automatically auto expand the Searchbar
        this.setIconified(false);
        this.clearFocus(); // TODO: 2017-10-17 if this triggers on touch, remove and add interim
    }

    //////////////////////////////////////////
    //Setters for the Various Icons / Colors//
    //////////////////////////////////////////

    /**
     * Set the text color
     * @param color Don't forget you need to use
     * {@link android.support.v4.content.ContextCompat#getColor(Context, int)}
     * and cannot just send R.color.A_COLOR
     */
    public void setTextColor(int color){
        try {
            getEditText().setTextColor(color);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Set the text hint color
     * @param color Don't forget you need to use
     * {@link android.support.v4.content.ContextCompat#getColor(Context, int)}
     * and cannot just send R.color.A_COLOR
     */
    public void setTextHintColor(int color){
        try {
            getEditText().setHintTextColor(color);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method would be used if this extended searchAutoComplete instead of searchview
     public void setTextColor(int color){
     SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)
     this.findViewById(android.support.v7.appcompat.R.id.search_src_text);
     searchAutoComplete.setTextColor(color);
     }
     */
    /**
     * This method would be used if this extended searchAutoComplete instead of searchview
     public void setTextHintColor(int color){
     SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)
     this.findViewById(android.support.v7.appcompat.R.id.search_src_text);
     searchAutoComplete.setHintTextColor(color);
     }
     */

    public void setSearchbarMainBackground(@NonNull Drawable drawable){
        View searchbarMainBackground = (View) this.findViewById(
                android.support.v7.appcompat.R.id.search_plate);
        searchbarMainBackground.setBackground(drawable);
    }

    public void setSearchbarMainBackground(int drawableResource){
        View searchbarMainBackground = (View) this.findViewById(
                android.support.v7.appcompat.R.id.search_plate);
        searchbarMainBackground.setBackgroundResource(drawableResource);
    }

    public void setCloseXIcon(@NonNull Drawable drawable){
        ImageView searchCloseIcon = (ImageView)this.findViewById(
                android.support.v7.appcompat.R.id.search_close_btn);
        searchCloseIcon.setImageDrawable(drawable);
    }

    public void setCloseXIcon(int drawableResource){
        ImageView searchCloseIcon = (ImageView)this.findViewById(
                android.support.v7.appcompat.R.id.search_close_btn);
        searchCloseIcon.setImageResource(drawableResource);
    }

    public void setVoiceIcon(@NonNull Drawable drawable){
        ImageView voiceIcon = (ImageView)this.findViewById(
                android.support.v7.appcompat.R.id.search_voice_btn);
        voiceIcon.setImageDrawable(drawable);
    }

    public void setVoiceIcon(int drawableResource){
        ImageView voiceIcon  = (ImageView)this.findViewById(
                android.support.v7.appcompat.R.id.search_voice_btn);
        voiceIcon.setImageResource(drawableResource);
    }

    public void setSearchMagnifyingGlassIcon(@NonNull Drawable drawable){
        ImageView searchIcon = (ImageView)this.findViewById(
                android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageDrawable(drawable);
    }

    public void setSearchMagnifyingGlassIcon(int drawableResource){
        ImageView searchIcon = (ImageView)this.findViewById(
                android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(drawableResource);
    }

    /////////////////
    //Other Setters//
    /////////////////

    public void setDefaultHint(@Nullable String hint){
        if(!StringUtilities.isNullOrEmpty(hint)){
            this.setQueryHint(hint);
        } else {
            this.setQueryHint("Search");
        }
    }

    public void setFocusChangeListener(OnFocusChangeListener focusChangeListener){
        this.setOnQueryTextFocusChangeListener(focusChangeListener);
    }

    public void setQueryTextWatcher(OnQueryTextListener queryTextListener){
        this.setOnQueryTextListener(queryTextListener);
    }

    ///////////
    //Getters//
    ///////////

    public String getText(){
        try {
            return this.getQuery().toString();
        } catch (Exception e){
            return "";
        }
    }

    public EditText getEditText(){
        EditText et = (EditText) this.findViewById(
                android.support.v7.appcompat.R.id.search_src_text);
        return et;
    }
}

