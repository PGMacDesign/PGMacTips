package com.pgmacdesign.pgmacutilities.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pgmacdesign.pgmacutilities.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Patrick MacDowell (PGMacDesign) on 2016-11-17.
 */
public class DialogUtilities {

    public static final int FAIL_RESPONSE = -1;
    public static final int OTHER_RESPONSE = 0;
    public static final int SUCCESS_RESPONSE = 1;
    public static final int LATER_RESPONSE = 2;
    public static final int NEVER_RESPONSE = 3;

    public static interface DialogFinishedListener {
        public void dialogFinished(Object object, int tag);
    }

    /**
     * Set the dialog transparency
     *
     * @param dialog          Dialog to alter
     * @param percentOutOf100 Percent of Transparency. if 0 is passed, not transparent
     *                        at all. If 100 is passed, completely transparent.
     *                        Pass values from 0 - 100.
     * @return
     */
    public static Dialog setDialogTransparency(Dialog dialog, float percentOutOf100) {
        try {
            ColorDrawable cd = new ColorDrawable(Color.TRANSPARENT);
            if (percentOutOf100 < 0) {
                percentOutOf100 = 0;
            }
            if (percentOutOf100 > 100) {
                percentOutOf100 = 100;
            }
            float outOf255 = percentOutOf100 / 100 * 255;
            cd.setAlpha((int) outOf255);
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(cd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    public static Dialog dimDialog(Dialog dialog) {
        try {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }


    //Mirrors the Calendar class
    public static class SimpleDateObject {
        public int year;
        public int monthOfYear;
        public int dayOfMonth;
    }

    //Date Picker Dialog
    public static DatePickerDialog buildDatePickerDialog(final Context context,
                                                         final DialogFinishedListener listener) {

        TimeZone tz = null;
        try {
            tz = TimeZone.getDefault();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar rightNow;
        try {
            rightNow = Calendar.getInstance(tz);
        } catch (Exception e) {
            rightNow = Calendar.getInstance();
        }
        DatePickerDialog mDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(android.widget.DatePicker view,
                                          int year, int monthOfYear, int dayOfMonth) {

                        SimpleDateObject sdo = new SimpleDateObject();
                        sdo.dayOfMonth = dayOfMonth;
                        sdo.monthOfYear = monthOfYear;
                        sdo.year = year;
                        listener.dialogFinished(sdo, SUCCESS_RESPONSE);
                        return;
                    }

                }, rightNow.get(Calendar.YEAR),
                rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH));

        return mDialog;
    }

    //Web Dialog
    public static AlertDialog buildWebDialog(@NonNull final Context context,
                                             @NonNull final DialogFinishedListener listener,
                                             @NonNull final String webUrlToLoad,
                                             @Nullable final String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(StringUtilities.isNullOrEmpty(title)){
            builder.setTitle("");
        } else {
            builder.setTitle(title);
        }
        final WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(webUrlToLoad);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                webView.loadUrl(webUrlToLoad);
                return true;
            }
        });
//        try {
//            webView.setWebChromeClient(new WebChromeClient(){
//                @Override
//                public void onProgressChanged(WebView view, int newProgress) {
//                    try {
//
//                    } catch (Exception e){
//                        e.printStackTrace();
//                        //Will trigger if user has chrome disabled
//                    }
//                }
//            });
//        } catch (Exception e){
//            e.printStackTrace();
//            //Will trigger if user has chrome disabled
//        }
        webView.loadUrl(webUrlToLoad);
        webView.setForegroundGravity(
                Gravity.CENTER_HORIZONTAL|Gravity.CENTER_HORIZONTAL|Gravity.CENTER);
        builder.setView(webView);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.dialogFinished(null, DialogUtilities.SUCCESS_RESPONSE);
            }
        });
        builder.setCancelable(false);
        return builder.create();
    }

    //Date Picker Dialog  // TODO: 2017-10-18  
//    public static DatePickerDialog buildNumberPickerDialog(final Context context,
//                                                           final DialogFinishedListener listener,
//                                                           final long minimumNum,
//                                                           final long maximumNum){
//
//        TimeZone tz = null;
//        try {
//            tz = TimeZone.getDefault();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        Calendar rightNow;
//        try {
//            rightNow = Calendar.getInstance(tz);
//        } catch (Exception e){
//            rightNow = Calendar.getInstance();
//        }
//        DatePickerDialog mDialog = new DatePickerDialog(context,
//                new DatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(android.widget.DatePicker view,
//                                          int year, int monthOfYear, int dayOfMonth) {
//
//                        SimpleDateObject sdo = new SimpleDateObject();
//                        sdo.dayOfMonth = dayOfMonth;
//                        sdo.monthOfYear = monthOfYear;
//                        sdo.year = year;
//                        listener.dialogFinished(sdo, SUCCESS_RESPONSE);
//                        return;
//                    }
//
//                }, rightNow.get(Calendar.YEAR),
//                rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH));
//
//        return mDialog;
//    }

    //Simple Alert Dialog
    public static AlertDialog buildSimpleOkDialog(final Context context,
                                                  final DialogFinishedListener listener,
                                                  String okText, String title, String message) {
        if (StringUtilities.isNullOrEmpty(message)) {
            return null;
        }
        if (context == null || listener == null) {
            return null;
        }
        if (StringUtilities.isNullOrEmpty(okText)) {
            okText = "Ok";
        }
        if (StringUtilities.isNullOrEmpty(title)) {
            title = "";
        }

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(context);
        myBuilder.setCancelable(true);
        myBuilder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.dialogFinished(true, SUCCESS_RESPONSE);
            }
        });
        myBuilder.setMessage(message);
        myBuilder.setTitle(title);

        return myBuilder.create();
    }

    //2 Option Dialog Dialog
    public static AlertDialog buildOptionDialog(final Context context,
                                                final DialogFinishedListener listener,
                                                String yesText, String noText,
                                                String title, String message) {
        if (StringUtilities.isNullOrEmpty(message)) {
            return null;
        }
        if (context == null || listener == null) {
            return null;
        }
        if (StringUtilities.isNullOrEmpty(yesText)) {
            yesText = "Yes";
        }
        if (StringUtilities.isNullOrEmpty(noText)) {
            noText = "No";
        }
        if (StringUtilities.isNullOrEmpty(title)) {
            title = "";
        }

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(context);
        myBuilder.setCancelable(true);
        myBuilder.setPositiveButton(yesText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.dialogFinished(true, SUCCESS_RESPONSE);
            }
        });
        myBuilder.setNegativeButton(noText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.dialogFinished(false, FAIL_RESPONSE);
            }
        });
        myBuilder.setMessage(message);
        myBuilder.setTitle(title);

        return myBuilder.create();
    }

    //3 Option Dialog Dialog
    public static Dialog buildOptionDialog(final Context context,
                                           final DialogFinishedListener listener,
                                           String yesText, String laterText,
                                           String neverText, String title, String message) {
        if (StringUtilities.isNullOrEmpty(message)) {
            return null;
        }
        if (context == null || listener == null) {
            return null;
        }
        if (StringUtilities.isNullOrEmpty(yesText)) {
            yesText = "";
        }
        if (StringUtilities.isNullOrEmpty(laterText)) {
            laterText = "";
        }
        if (StringUtilities.isNullOrEmpty(neverText)) {
            neverText = "";
        }
        if (StringUtilities.isNullOrEmpty(title)) {
            title = "";
        }

        ThreeButtonDialog dialog = new ThreeButtonDialog(
                context, listener, yesText, laterText, neverText, message, title
        );
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    //Edit Text dialog
    public static Dialog buildEditTextDialog(final Context context,
                                             final DialogFinishedListener listener,
                                             String doneText, String cancelText,
                                             String title, String message,
                                             String editTextHint, Integer textInputType) {
        if (StringUtilities.isNullOrEmpty(message)) {
            return null;
        }
        if (context == null || listener == null) {
            return null;
        }

        EditTextDialog dialog = new EditTextDialog(
                context, listener, doneText, cancelText, title, editTextHint, textInputType
        );
        if (StringUtilities.isNullOrEmpty(title)) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;

    }

    /**
     * Build a Timer Picker Dialog with custom intervals
     *
     * @param context        {@link Context}
     * @param tsl            {@link android.app.TimePickerDialog.OnTimeSetListener}
     * @param hourStart      Hours to start at. If null, if 24 hour, starts at 0, else, starts at 1.
     * @param minuteStart    Minutes to start at. If null, defaults to 0
     * @param is24Hour       If null is passed, it will attempt to get system preferred. If it cannot
     *                       grab that, it will default to false and show 12 hour format.
     * @param hourInterval   Hour interval. IE, if you send 4, it would only show 4, 8, 12, etc.
     * @param minuteInterval Minute Interval. IE, if you send 30, it would only show 30, 0.
     * @return {@link CustomTimePickerDialog}
     */
    public static TimePickerDialog buildTimePicker(Context context,
                                                   TimePickerDialog.OnTimeSetListener tsl,
                                                   @Nullable Integer hourStart,
                                                   @Nullable Integer minuteStart,
                                                   @Nullable Boolean is24Hour,
                                                   @Nullable Integer hourInterval,
                                                   @Nullable Integer minuteInterval) {
        if (hourStart == null) {
            if (is24Hour) {
                hourStart = 0;
            } else {
                hourStart = 1;
            }
        }
        if (minuteStart == null) {
            minuteStart = 0;
        }
        if (hourInterval == null) {
            hourInterval = 1;
        } else {
            if (is24Hour) {
                if (hourInterval < 1 || hourInterval > 24) {
                    hourInterval = 1;
                }
            } else {
                if (hourInterval < 1 || hourInterval > 12) {
                    hourInterval = 1;
                }
            }
        }
        if (minuteInterval == null) {
            minuteInterval = 1;
        } else {
            if (minuteInterval < 0 || minuteInterval > 60) {
                minuteInterval = 1;
            }
        }
        boolean checkIs24;
        if (is24Hour != null) {
            checkIs24 = is24Hour;
        } else {
            try {
                checkIs24 = DateFormat.is24HourFormat(context);
            } catch (Exception e) {
                checkIs24 = false;
            }
        }
        TimePickerDialog timePicker = new CustomTimePickerDialog(
                context, tsl, hourStart, minuteStart, checkIs24, hourInterval, minuteInterval);
        return timePicker;
    }

    //3 Button Dialog
    private static class ThreeButtonDialog extends Dialog implements View.OnClickListener {

        private LinearLayout three_button_dialog_buttons_layout;
        private TextView three_button_dialog_title, three_button_dialog_body;
        private Button three_button_dialog_option_never, three_button_dialog_option_later,
                three_button_dialog_option_yes;

        private String yesText, laterText, neverText, title, bodyText;
        private DialogFinishedListener listener;

        public ThreeButtonDialog(@NonNull final Context context,
                                 @NonNull final DialogFinishedListener listener,
                                 String yesText, String laterText, String neverText,
                                 String bodyText, String title) {
            super(context);
            this.listener = listener;
            this.yesText = yesText;
            this.laterText = laterText;
            this.neverText = neverText;
            this.title = title;
            this.bodyText = bodyText;
        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setCanceledOnTouchOutside(false);
            setContentView(R.layout.three_button_dialog);

            checkForNulls();
            initUIFields();
            setUIFields();
        }

        private void checkForNulls() {
            if (StringUtilities.isNullOrEmpty(yesText)) {
                this.yesText = "";
            }
            if (StringUtilities.isNullOrEmpty(neverText)) {
                this.neverText = "";
            }
            if (StringUtilities.isNullOrEmpty(laterText)) {
                this.laterText = "";
            }
            if (StringUtilities.isNullOrEmpty(title)) {
                this.title = "";
            }
            if (StringUtilities.isNullOrEmpty(bodyText)) {
                this.bodyText = "";
            }
        }

        private void initUIFields() {

            three_button_dialog_buttons_layout = (LinearLayout) this.findViewById(
                    R.id.three_button_dialog_buttons_layout);
            three_button_dialog_title = (TextView) this.findViewById(
                    R.id.three_button_dialog_title);
            three_button_dialog_body = (TextView) this.findViewById(
                    R.id.three_button_dialog_body);
            three_button_dialog_option_never = (Button) this.findViewById(
                    R.id.three_button_dialog_option_never);
            three_button_dialog_option_later = (Button) this.findViewById(
                    R.id.three_button_dialog_option_later);
            three_button_dialog_option_yes = (Button) this.findViewById(
                    R.id.three_button_dialog_option_yes);

            three_button_dialog_option_yes.setTag("yes");
            three_button_dialog_option_later.setTag("later");
            three_button_dialog_option_never.setTag("never");

            three_button_dialog_option_yes.setTransformationMethod(null);
            three_button_dialog_option_later.setTransformationMethod(null);
            three_button_dialog_option_never.setTransformationMethod(null);

            three_button_dialog_option_yes.setOnClickListener(this);
            three_button_dialog_option_later.setOnClickListener(this);
            three_button_dialog_option_never.setOnClickListener(this);
        }

        private void setUIFields() {

            L.m("yes text being set = " + yesText);
            three_button_dialog_option_yes.setText(yesText);
            three_button_dialog_option_never.setText(neverText);
            three_button_dialog_option_later.setText(laterText);

            three_button_dialog_title.setText(title);
            three_button_dialog_body.setText(bodyText);

        }

        @Override
        public void onClick(View v) {

            String tag = null;
            try {
                tag = (String) v.getTag();
            } catch (Exception e) {
            }

            if (tag == null) {
                tag = "";
            }

            if (tag.equals("yes")) {
                this.dismiss();
                listener.dialogFinished(true, SUCCESS_RESPONSE);
            } else if (tag.equals("never")) {
                this.dismiss();
                listener.dialogFinished(true, NEVER_RESPONSE);
            } else if (tag.equals("later")) {
                this.dismiss();
                listener.dialogFinished(true, LATER_RESPONSE);
            } else {
                this.dismiss();
            }

        }

    }

    //Edit Text custom Dialog
    public static class EditTextDialog extends Dialog implements View.OnClickListener, TextWatcher {

        private RelativeLayout edit_text_dialog_main_layout, edit_text_dialog_sub_layout,
                edit_text_dialog_sub_layout_2;
        private LinearLayout edit_text_dialog_buttons_layout;
        private TextView edit_text_dialog_title;
        private EditText edit_text_dialog_et;
        private Button edit_text_dialog_cancel_button, edit_text_dialog_confirm_button;

        private String doneText, cancelText, title, editTextHint;
        private DialogFinishedListener listener;
        private Context context;
        private Integer textInputType;

        public EditTextDialog(@NonNull final Context context,
                              @NonNull final DialogFinishedListener listener,
                              String doneText, String cancelText,
                              String title, String editTextHint,
                              Integer textInputType) {
            super(context);
            this.context = context;
            this.listener = listener;
            this.doneText = doneText;
            this.cancelText = cancelText;
            this.title = title;
            this.editTextHint = editTextHint;
            this.textInputType = textInputType;

        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setCanceledOnTouchOutside(false);
            setContentView(R.layout.edit_text_dialog);

            checkForNulls();
            initUIFields();
            setUIFields();
        }

        private void checkForNulls() {
            if (StringUtilities.isNullOrEmpty(doneText)) {
                this.doneText = "Done";
            }
            if (StringUtilities.isNullOrEmpty(cancelText)) {
                this.cancelText = "Cancel";
            }
            if (StringUtilities.isNullOrEmpty(title)) {
                this.title = "";
            }
            if (textInputType == null) {
                this.textInputType = InputType.TYPE_CLASS_TEXT; //TYPE_TEXT_VARIATION_NORMAL?
            }
            if (StringUtilities.isNullOrEmpty(editTextHint)) {
                this.editTextHint = "Enter Information Here";
            }
        }

        private void initUIFields() {
            edit_text_dialog_main_layout = (RelativeLayout) this.findViewById(
                    R.id.edit_text_dialog_main_layout);
            edit_text_dialog_sub_layout = (RelativeLayout) this.findViewById(
                    R.id.edit_text_dialog_sub_layout);
            edit_text_dialog_sub_layout_2 = (RelativeLayout) this.findViewById(
                    R.id.edit_text_dialog_sub_layout_2);
            edit_text_dialog_buttons_layout = (LinearLayout) this.findViewById(
                    R.id.edit_text_dialog_buttons_layout);
            edit_text_dialog_title = (TextView) this.findViewById(
                    R.id.edit_text_dialog_title);
            edit_text_dialog_et = (EditText) this.findViewById(
                    R.id.edit_text_dialog_et);
            edit_text_dialog_cancel_button = (Button) this.findViewById(
                    R.id.edit_text_dialog_cancel_button);
            edit_text_dialog_confirm_button = (Button) this.findViewById(
                    R.id.edit_text_dialog_confirm_button);
        }

        private void setUIFields() {

            this.edit_text_dialog_title.setText(title);
            this.edit_text_dialog_et.setHint(editTextHint);
            this.edit_text_dialog_cancel_button.setText(cancelText);
            this.edit_text_dialog_confirm_button.setText(doneText);
            try {
                this.edit_text_dialog_et.setInputType(textInputType);
            } catch (Exception e) {
                this.edit_text_dialog_et.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            edit_text_dialog_cancel_button.setTag("cancel");
            edit_text_dialog_confirm_button.setTag("confirm");

            edit_text_dialog_cancel_button.setTransformationMethod(null);
            edit_text_dialog_confirm_button.setTransformationMethod(null);

            edit_text_dialog_cancel_button.setOnClickListener(this);
            edit_text_dialog_confirm_button.setOnClickListener(this);

            edit_text_dialog_et.addTextChangedListener(this);

        }

        @Override
        public void onClick(View v) {

            String tag = null;
            try {
                tag = (String) v.getTag();
            } catch (Exception e) {
                tag = "";
            }

            if (tag.equals("cancel")) {
                this.dismiss();
                listener.dialogFinished(null, FAIL_RESPONSE);
                return;
            }

            String str = edit_text_dialog_et.getText().toString();
            if (StringUtilities.isNullOrEmpty(str)) {
                //Problem
                return;
            }

            if (tag.equals("confirm")) {
                listener.dialogFinished(str, SUCCESS_RESPONSE);
            } else {
                //?
            }
            this.dismiss();

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (StringUtilities.isNullOrEmpty(str)) {
                edit_text_dialog_confirm_button.setEnabled(false);
            } else {
                edit_text_dialog_confirm_button.setEnabled(true);
            }
        }
    }

    private static class CustomTimePickerDialog extends TimePickerDialog {

        private int hourInterval, minuteInterval;
        private TimePicker mTimePicker;
        private final OnTimeSetListener mTimeSetListener;
        private boolean is24HourView;

        private CustomTimePickerDialog(Context context,
                                       OnTimeSetListener listener,
                                       int hourOfDay,
                                       int minute, boolean is24HourView,
                                       int hourInterval, int minuteInterval) {
            super(context, listener, (hourOfDay / hourInterval),
                    (minute / minuteInterval), is24HourView);
            this.hourInterval = hourInterval;
            this.minuteInterval = minuteInterval;
            this.mTimeSetListener = listener;
            this.is24HourView = is24HourView;
        }

        public CustomTimePickerDialog(Context context, int themeResId,
                                      OnTimeSetListener listener, int hourOfDay,
                                      int minute, boolean is24HourView,
                                      int hourInterval, int minuteInterval) {
            super(context, themeResId, listener, (hourOfDay / hourInterval),
                    (minute / minuteInterval), is24HourView);
            this.hourInterval = hourInterval;
            this.minuteInterval = minuteInterval;
            this.mTimeSetListener = listener;
            this.is24HourView = is24HourView;
        }

        @Override
        public void updateTime(int hourOfDay, int minuteOfHour) {
            if (Build.VERSION.SDK_INT >= 23) {
                mTimePicker.setHour((hourOfDay / hourInterval));
                mTimePicker.setMinute((minuteOfHour / minuteInterval));
            } else {
                mTimePicker.setCurrentHour((hourOfDay / hourInterval));
                mTimePicker.setCurrentMinute((minuteOfHour / minuteInterval));
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE:
                    if (Build.VERSION.SDK_INT >= 23) {
                        mTimeSetListener.onTimeSet(mTimePicker,
                                (mTimePicker.getHour() * hourInterval),
                                (mTimePicker.getMinute() * minuteInterval));
                    } else {
                        mTimeSetListener.onTimeSet(mTimePicker,
                                (mTimePicker.getCurrentHour() * hourInterval),
                                (mTimePicker.getCurrentMinute() * minuteInterval));
                    }
                    break;
                case BUTTON_NEGATIVE:
                    cancel();
                    break;
            }
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            try {
                Class<?> classForid = Class.forName("com.android.internal.R$id");
                Field timePickerField = classForid.getField("timePicker");
                mTimePicker = (TimePicker) findViewById(timePickerField.getInt(null));
                Field minuteField = classForid.getField("minute");
                Field hourField = classForid.getField("hour");

                if (minuteField != null) {
                    NumberPicker minuteSpinner = (NumberPicker) mTimePicker
                            .findViewById(minuteField.getInt(null));
                    minuteSpinner.setMinValue(0);
                    minuteSpinner.setMaxValue((60 / minuteInterval) - 1);
                    List<String> displayedValues = new ArrayList<>();
                    for (int i = 0; i < 60; i += minuteInterval) {
                        displayedValues.add(String.format("%02d", i));
                    }
                    minuteSpinner.setDisplayedValues(displayedValues
                            .toArray(new String[displayedValues.size()]));
                }

                if (hourField != null) {
                    NumberPicker hourSpinner = (NumberPicker) mTimePicker
                            .findViewById(hourField.getInt(null));
                    hourSpinner.setMinValue(0);
                    List<String> displayedValues = new ArrayList<>();
                    if (is24HourView) {
                        hourSpinner.setMaxValue((24 / minuteInterval) - 1);
                        for (int i = 0; i < 24; i += minuteInterval) {
                            displayedValues.add(String.format("%02d", i));
                        }
                    } else {
                        hourSpinner.setMaxValue((12 / minuteInterval) - 1);
                        for (int i = 0; i < 12; i += minuteInterval) {
                            displayedValues.add(String.format("%02d", i));
                        }
                    }
                    hourSpinner.setDisplayedValues(displayedValues
                            .toArray(new String[displayedValues.size()]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
