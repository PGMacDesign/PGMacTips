package com.pgmacdesign.pgmacutilities.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants;

/**
 * Created by pmacdowell on 8/15/2016.
 */
public class GUIUtilities {


    /**
     * This takes in a view and converts it to an object (Bitmap Drawable), then returns it
     * Useful via this link: http://stackoverflow.com/questions/10812316/contact-bubble-edittext
     * @param view
     * @return
     */
    public static Bitmap convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        //return new BitmapDrawable(viewBmp);
        return viewBmp;
    }

    /**
     * This will hide the keyboard
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Send a view to the back
     * @param child
     */
    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    /**
     * This will adjust the theme of the activity passed and restart the activity
     * @param activity
     * @param theme
     */
    public static void changeTheme(Activity activity, int theme){
        activity.finish();
        activity.setTheme(theme);
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /**
     * Set the back-button content description. This is used for accessibility options;
     * specifically, with regards to the back button (nav up button)
     * @param activity Activity
     */
    public static void setBackButtonContentDescription(Activity activity){
        try {
            ((View) activity.getWindow()
                    .getDecorView()
                    .findViewById(android.R.id.home)
                    .getParent()
                    .getParent())
                    .setContentDescription("Back Button");
        } catch (Exception e){}
    }

    /**
     * Creates an AlertDialog
     * @param activity The activity this is being called from
     * @param yes What you want it to say in the "Yes" button (Generally just Yes). If null passed,
     *            it will remove the option entirely. IE, if you only want "Ok" as an option
     * @param no What you want it to say in the "No" button (Generally just No). If null passed,
     *            it will remove the option entirely. IE, if you only want "Ok" as an option
     * @param title the String title to be displayed. If null, no title will be set
     * @param message the message you want displayed in the dialog popup. If null, no message
     *                will be set.
     * @param dismissable Boolean, if true, it will be allowed dismissable/ cancelable, if false, it will not
     * @param listener The onTaskComplete listener that will send back the results to the activity
     */
    public static AlertDialog createAlertDialogPopup(final Activity activity, final String yes,
                                              final String no, final String title,
                                              final String message, final boolean dismissable,
                                              final OnTaskCompleteListener listener){
        //Dialog Click listener
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        try {
                            listener.onTaskComplete("No",
                                    PGMacUtilitiesConstants.TAG_DIALOG_POPUP_NO);
                            dialog.dismiss();
                        } catch (Exception e) {}
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        try{
                            listener.onTaskComplete("Yes",
                                    PGMacUtilitiesConstants.TAG_DIALOG_POPUP_YES);
                            dialog.dismiss();
                        } catch (Exception e) {
                        }
                        break;
                }
            }
        };

        //Cancel Listener for sending back cancel hits
        DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                try{
                    listener.onTaskComplete("Yes",
                            PGMacUtilitiesConstants.TAG_DIALOG_POPUP_CANCEL);
                } catch (Exception e) {
                }
            }
        };

        //Context
        Context context = activity.getApplicationContext();
        AlertDialog.Builder builder;
        try {
            builder = new AlertDialog.Builder(context);
        } catch (Exception e){
            return null;
        }
        if(message != null) {
            builder.setMessage(message);
        }
        if(no == null && yes == null && dismissable){
            L.m("must create at least one way to exit the dialog");
            return null;
        }
        if(no != null){
            builder.setNegativeButton(no, dialogClickListener);
        }
        if(yes != null){
            builder.setPositiveButton(yes, dialogClickListener);
        }
        if(title != null) {
            builder.setTitle(title);
        }
        builder.setCancelable(dismissable);
        builder.setOnCancelListener(cancelListener);
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }


}
