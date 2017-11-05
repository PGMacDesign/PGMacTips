package com.pgmacdesign.pgmacutilities.customui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by pmacdowell on 2017-02-16.
 */

@SuppressLint("AppCompatCustomView")
public class StateSelectedEditText extends EditText { //android.support.v7.widget.AppCompatEditText {

    public static enum EditTextState {
        FOCUSED, NOT_FOCUSED, ERROR, VALIDATED
    }

    public StateSelectedEditText(Context context) {
        super(context);
    }

    public StateSelectedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StateSelectedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setState(EditTextState state){
        switch (state){
            case FOCUSED:
                //this.setBackground(ContextCompat.getDrawable(
                        //MyApplication.getContext(),
                        //R.drawable.set_custom_background_here));
                break;

            case NOT_FOCUSED:
                //this.setBackground(ContextCompat.getDrawable(
                        //MyApplication.getContext(),
                        //R.drawable.set_custom_background_here));
                break;

            case ERROR:
                //this.setBackground(ContextCompat.getDrawable(
                        //MyApplication.getContext(),
                        //R.drawable.set_custom_background_here));
                break;

            case VALIDATED:
                //this.setBackground(ContextCompat.getDrawable(
                        //MyApplication.getContext(),
                        //R.drawable.set_custom_background_here));
                break;
        }
    }

}
