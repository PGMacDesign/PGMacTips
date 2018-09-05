package com.pgmacdesign.pgmactips.utilities;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
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

    public static <T extends TextView>  void setShadow(@NonNull T textView,
                                                       float translationZ, float elevation){
        try {
            if(Build.VERSION.SDK_INT >= 21) {
                textView.setTranslationZ(translationZ);
                textView.setElevation(elevation);
            }
        } catch (Exception e){}//Can trigger on apis < 21
    }

    /**
     * Gets text from an editText, checks if it is valid, trims it, and if it is not = null,
     * returns a boolean of true/ false
     * @param et
     * @return
     */
    public static boolean isFieldValid(EditText et){
        if(et == null){
            return false;
        }
        if(et.getText() == null){
            return false;
        }
        return true;
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
        if (!StringUtilities.isNullOrEmpty(str)) {
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
     * Set the textview with HTML style text IE, <b>Words</b> or
     * "<i><small><font color='#c5c5c5'>" + "Words Go Here" + "</font></small></i>"
     * @param viewToSet The view to set
     * @param stringToSet The string to set
     * @param <T> T extends TextView
     */
    public static <T extends TextView> void setTextWithHtml(@NonNull T viewToSet, String stringToSet){
        TextFieldUtilities.setTextWithHtml(viewToSet, stringToSet, null);
    }
    /**
     * Set the textview with HTML style text IE, <b>Words</b> or
     * "<i><small><font color='#c5c5c5'>" + "Words Go Here" + "</font></small></i>"
     * @param viewToSet The view to set
     * @param stringToSet The string to set
     * @param hasUrlLinks Boolean, if true, will set the individual URL links to web clickable
     *                    options. IE, if you have something like, "By continuing, you agree to
     *                    our terms and conditions and privacy policy" where the 'terms and
     *                    conditions' are one URL and the 'privacy policy' is another url, this
     *                    will set each of those to open to their respective url links in the
     *                    default web browser (IE chrome) so long as they are set with properly
     *                    encoded HTML Tags. An example would be:
     *            String str = "Do you want to search on " + "<a href=http//www.google.com>" +
                  "Google" + "</a>" + " or " + "<a href=http//www.yahoo.com>" +
                  "Yahoo" + "</a>" + "?";
                          NOTE! This will auto underlign and highlight the URL links with your
                          app's "Primary color" (or accent color), so customize those colors
                          if desired.
     * @param <T> T extends TextView
     */
    public static <T extends TextView> void setTextWithHtml(@NonNull T viewToSet, String stringToSet,
                                                            Boolean hasUrlLinks){
        if(hasUrlLinks == null){
            hasUrlLinks = false;
        }

        if(viewToSet == null || StringUtilities.isNullOrEmpty(stringToSet)){
            return;
        }
        if(Build.VERSION.SDK_INT >= 24) {
            viewToSet.setText(Html.fromHtml(stringToSet, Html.FROM_HTML_MODE_LEGACY));
        } else {
            viewToSet.setText(Html.fromHtml(stringToSet));
        }
        //ignore me
        if(hasUrlLinks){
            try {
                viewToSet.setMovementMethod(LinkMovementMethod.getInstance());
            } catch (Exception e){}
        }
    }

    /**
     * Checks if String in fields match one another. Does NOT compare case.
     * Note, will return false if either is null or empty
     * @param et1
     * @param et2
     * @return
     */
    public static boolean doFieldsMatch(@NonNull EditText et1, @NonNull EditText et2){
        String str1 = getFromEditText(et1);
        String str2 = getFromEditText(et2);
        if(StringUtilities.isNullOrEmpty(str1) || StringUtilities.isNullOrEmpty(str2)){
            return false;
        }
        if(str1.equalsIgnoreCase(str2)){
            return true;
        }
        return false;
    }

    /**
     * Checks if String in fields match one another. Does compare case
     * Note, will return false if either is null or empty
     * @param et1
     * @param et2
     * @return
     */
    public static boolean doFieldsMatchWithCase(EditText et1, EditText et2){
        String str1 = getFromEditText(et1);
        String str2 = getFromEditText(et2);
        if(StringUtilities.isNullOrEmpty(str1) || StringUtilities.isNullOrEmpty(str2)){
            return false;
        }
        if(str1.equals(str2)){
            return true;
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
            TextWatcher textWatcher, Editable s, T et){
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
     * Handles the credit card expiration formatting with the edit text.
     * Takes in a textwatcher, the editable, and the EditText
     * @param textWatcher
     * @param s
     * @param et
     * @param <T>
     */
    public static <T extends EditText> void handleCreditCardExpFormatting(TextWatcher textWatcher,
                                                                         Editable s, T et){
        if(s == null || textWatcher == null || et == null){
            return;
        }

        String ss = StringUtilities.formatNumbersAsCreditCardExpiration(s.toString());
        et.removeTextChangedListener(textWatcher);
        et.setText(ss);
        et.setSelection(ss.length());
        et.addTextChangedListener(textWatcher);
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
