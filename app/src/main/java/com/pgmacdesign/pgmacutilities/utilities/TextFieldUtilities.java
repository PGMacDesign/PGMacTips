package com.pgmacdesign.pgmacutilities.utilities;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This class is used to work on Edit Texts, TextViews, and other misc UI Elements that match
 * extend certain views (IE Spinners)
 * Created by pmacdowell on 8/15/2016.
 */
public class TextFieldUtilities {

    /**
     * Gets text from an editText, checks if it is valid, trims it, and if it is not = null,
     * returns a boolean of true/ false
     * @param et
     * @return
     */
    public static boolean isFieldValid(EditText et){
        String str = et.getText().toString();
        if (!str.equals(null)){
            str = str.trim();
            if(!str.equalsIgnoreCase("")){
                if(!str.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets a String from an edit text. It also trims off any leading or ending white space
     * @param et
     * @return
     */
    public static String getFromEditText(EditText et){
        if(et == null){
            return null;
        }

        String str = et.getText().toString();
        if (!str.equals(null)) {
            str = str.trim();
            return str;
        }
        return str;
    }

    /**
     * Gets text from a spinner, checks if it is valid, trims it, and if it is not = null,
     * returns a boolean of true/ false
     * @param spinner
     * @return
     */
    public static boolean isFieldValid(Spinner spinner){
        String str = spinner.getSelectedItem().toString();
        if (!str.equals(null)){
            str = str.trim();
            if(!str.equalsIgnoreCase("")){
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a String from a spinner. It also trims off any leading or ending white space
     * @param spinner
     * @return
     */
    public static String getFromSpinner(Spinner spinner){
        String str = spinner.getSelectedItem().toString();
        if (!str.equals(null)) {
            str = str.trim();
            return str;
        }
        return str;
    }

    /**
     * Checks if String in fields match one another. Does NOT compare case
     * @param et1
     * @param et2
     * @return
     */
    public static boolean doFieldsMatch(EditText et1, EditText et2){
        String str1 = getFromEditText(et1);
        String str2 = getFromEditText(et2);
        if(str1.equalsIgnoreCase(str2)){
            return true;
        }
        if(str1 == null && str2 == null){
            return false;
        }
        return false;
    }

    /**
     * Checks if String in fields match one another. Does compare case
     * @param et1
     * @param et2
     * @return
     */
    public static boolean doFieldsMatchWithCase(EditText et1, EditText et2){
        String str1 = getFromEditText(et1);
        String str2 = getFromEditText(et2);
        if(str1.equals(str2)){
            return true;
        }
        if(str1 == null && str2 == null){
            return false;
        }
        return false;
    }

    /**
     * Handles the phone number formatting with the edit text. Takes in a textwatcher, the
     * editable, and the EditText
     * @param textWatcher
     * @param s
     * @param et
     * @param <T>
     */
    public static <T extends EditText> void handlePhoneNumberFormatting(TextWatcher textWatcher,
                                                                        Editable s, T et){
        if(s == null || textWatcher == null || et == null){
            return;
        }

        String ss = StringUtilities.formatStringLikePhoneNumber(s.toString());
        et.removeTextChangedListener(textWatcher);
        et.setText(ss);
        et.setSelection(ss.length());
        et.addTextChangedListener(textWatcher);
    }

    /**
     * Handles the phone number formatting like above method but also returns the String
     * (of the phone number) while editing
     * @param textWatcher
     * @param s
     * @param et
     * @param <T>
     * @return
     */
    public static <T extends EditText> String handlePhoneNumberFormattingReturn(
            TextWatcher textWatcher,
            Editable s, T et){
        if(s == null || textWatcher == null || et == null){
            return null;
        }

        String ss = StringUtilities.formatStringLikePhoneNumber(s.toString());
        et.removeTextChangedListener(textWatcher);
        et.setText(ss);
        et.setSelection(ss.length());
        et.addTextChangedListener(textWatcher);
        return ss;
    }

    /**
     * Handles the credit card formatting with the edit text. Takes in a textwatcher, the
     * editable, and the EditText
     * @param textWatcher
     * @param s
     * @param et
     * @param <T>
     */
    public static <T extends EditText> void handleCreditCardFormatting(TextWatcher textWatcher,
                                                                        Editable s, T et){
        if(s == null || textWatcher == null || et == null){
            return;
        }

        String ss = StringUtilities.formatNumbersAsCreditCard(s.toString());
        et.removeTextChangedListener(textWatcher);
        et.setText(ss);
        et.setSelection(ss.length());
        et.addTextChangedListener(textWatcher);
    }

    /**
     * Handles the credit card formatting like above method but also returns the String
     * (of the phone number) while editing
     * @param textWatcher
     * @param s
     * @param et
     * @param <T>
     * @return
     */
    public static <T extends EditText> String handleCreditCardFormattingReturn(
            TextWatcher textWatcher,
            Editable s, T et){
        if(s == null || textWatcher == null || et == null){
            return null;
        }

        String ss = StringUtilities.formatNumbersAsCreditCard(s.toString());
        et.removeTextChangedListener(textWatcher);
        et.setText(ss);
        et.setSelection(ss.length());
        et.addTextChangedListener(textWatcher);
        return ss;
    }

    /**
     * Used to alter the edit text so that when they click the bottom right button on their,
     * keyboard, it will simulate an enter press
     * @param et Edit text being altered
     * @param listener Listener to decide the reaction to said click
     * @param <T> T extends Edit Text
     */
    private static <T extends EditText> void addIMEFunctionality(T et, TextView.
            OnEditorActionListener listener){
        et.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        et.setImeActionLabel("Enter", KeyEvent.KEYCODE_ENTER);
        et.setOnEditorActionListener(listener);
        /*
            et.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            et.setImeActionLabel("Enter", KeyEvent.KEYCODE_ENTER);
            et.setOnEditorActionListener(listener);

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == KeyEvent.KEYCODE_ENTER){
                    doStuff();
                }
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    doStuff();
                }
                return false;
            }



        //Make sure to include this code below in class calling this method/ function

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId == KeyEvent.KEYCODE_ENTER){
                //Action happens here, IE Submit or enter
            }
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                //Action happens here, IE Submit or enter
            }
            return false;
        }
         */
    }

}
