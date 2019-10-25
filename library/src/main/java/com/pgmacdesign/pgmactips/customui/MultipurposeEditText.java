package com.pgmacdesign.pgmactips.customui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.EditText;

import com.pgmacdesign.pgmactips.R;

/**
 * Custom EditText that allows for state changes to alert users to errors as well as focus
 * Created by pmacdowell on 2017-03-07.
 */
@SuppressLint("AppCompatCustomView")
public class MultipurposeEditText extends EditText { //AppCompatEditText

    private Context context;

    public static enum EditTextState {
        FOCUSED, NOT_FOCUSED, ERROR
    }

    public MultipurposeEditText(Context context) {
        super(context);
        this.context = context;
    }

    public MultipurposeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MultipurposeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * Set a custom state
     * @param state
     */
    public void setState(EditTextState state){
        switch (state){
            case FOCUSED:
                if(Build.VERSION.SDK_INT >= 16) {
                    this.setBackground(ContextCompat.getDrawable(
                            context, R.drawable.custom_background_white_back_black_edges));
                }
                break;

            case NOT_FOCUSED:
                if(Build.VERSION.SDK_INT >= 16) {
                    this.setBackground(ContextCompat.getDrawable(
                            context, R.drawable.custom_background_white_back_lightgrey_edges));
                }
                break;

            case ERROR:
                if(Build.VERSION.SDK_INT >= 16) {
                    this.setBackground(ContextCompat.getDrawable(
                            context, R.drawable.custom_background_white_back_red_edges));
                }
                break;
        }
    }
}
