package com.pgmacdesign.pgmactips.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pgmacdesign.pgmactips.R;
import com.pgmacdesign.pgmactips.adaptersandlisteners.CustomClickCallbackLink;
import com.pgmacdesign.pgmactips.adaptersandlisteners.TextIconAdapter;
import com.pgmacdesign.pgmactips.datamodels.SimpleTextIconObject;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;

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
    public static final int NO_RESPONSE = -1;
    public static final int OTHER_RESPONSE = 0;
    public static final int SIMPLE_CLOSE_RESPONSE = 0;
    public static final int SUCCESS_RESPONSE = 1;
    public static final int YES_RESPONSE = 1;
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

    /**
     * Sets the flag to dim the back of the dialog
     * {@link WindowManager.LayoutParams#FLAG_DIM_BEHIND}
     * @param dialog
     * @return
     */
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

    /**
     * Simple date object POJO to be used in some of the dialogs in this class
     */
    public static class SimpleDateObject {
        public int year;
        public int monthOfYear;
        public int dayOfMonth;
    }

    /**
     * Simple Date Picker dialog for choose a date
     * @param context
     * @param listener
     * @return
     */
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

    /**
     * Web dialog
     * @param context
     * @param listener
     * @param webUrlToLoad
     * @param title
     * @return
     */
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
        if(Build.VERSION.SDK_INT >= 23) {
            webView.setForegroundGravity(
                    Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        }
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

    /**
     * Simple alert dialog with only one option
     * @param context
     * @param listener
     * @param okText
     * @param title
     * @param message
     * @return
     */
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

    /**
     * Simple alert dialog with yes and no options as well as message + title
     * @param context
     * @param listener
     * @param yesText
     * @param noText
     * @param title
     * @param message
     * @return
     */
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
                listener.dialogFinished(true, YES_RESPONSE);
            }
        });
        myBuilder.setNegativeButton(noText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.dialogFinished(false, NO_RESPONSE);
            }
        });
        myBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.dialogFinished(false, NO_RESPONSE);
            }
        });
        myBuilder.setMessage(message);
        myBuilder.setTitle(title);

        return myBuilder.create();
    }

    /**
     * Build a simple alert dialog. (Nearly identical to
     * {@link DialogUtilities#buildOptionDialog(Context, DialogFinishedListener, String, String, String, String, String)}
     * other than this returns an alert dialog for a different viewing experience)
     * @param context Context
     * @param listener listener to send data back upon. Answer will be one of 2 things:
     *                 1) {@link DialogUtilities#NO_RESPONSE} if no is clicked or dialog dismissed
     *                 2) {@link DialogUtilities#YES_RESPONSE} if yes is clicked.
     * @param title Title, can be null, but either title or message must not be null; not both
     * @param message Message (body) to display, can be null, but either title or message must not be null; not both
     * @param yesConfirmText Yes (confirm) text. If null, will default to "Yes"
     * @param noDenyText No (cancel) text. If null, will default to "No"
     * @return {@link AlertDialog}
     */
    public static AlertDialog buildSimpleAlertDialog(@NonNull final Context context,
                                                     @NonNull final DialogFinishedListener listener,
                                                     @Nullable final String title,
                                                     @NonNull final String message,
                                                     @Nullable final String yesConfirmText,
                                                     @Nullable final String noDenyText){
        if(context == null || listener == null){
            return null;
        }
        if(StringUtilities.isNullOrEmpty(title) && StringUtilities.isNullOrEmpty(message)){
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(StringUtilities.isNullOrEmpty(title) ? "" : title);
        builder.setMessage(StringUtilities.isNullOrEmpty(message) ? "" : message);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.dialogFinished(false, DialogUtilities.NO_RESPONSE);
            }
        });
        builder.setPositiveButton(((StringUtilities.isNullOrEmpty(yesConfirmText)
                        ? "Yes" : yesConfirmText)),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.dialogFinished(true, DialogUtilities.YES_RESPONSE);
                    }
                });
        builder.setNegativeButton(((StringUtilities.isNullOrEmpty(noDenyText)
                        ? "No" : noDenyText)),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.dialogFinished(false, DialogUtilities.NO_RESPONSE);
                    }
                });
        return builder.create();
    }

    /**
     * Simple dialog with 3 options on it
     * @param context
     * @param listener
     * @param yesText
     * @param laterText
     * @param neverText
     * @param title
     * @param message
     * @return
     */
    public static Dialog buildOptionDialog(@NonNull final Context context,
                                           @NonNull final DialogFinishedListener listener,
                                           @Nullable String yesText, @Nullable String laterText,
                                           @Nullable String neverText, @Nullable String title, @Nullable String message) {
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
        try {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.custom_background_white_back_black_edges_heavy_rounding));
        } catch (Exception e){e.printStackTrace();}
        return dialog;
    }

    /**
     * Build a Center Image Dialog with an image in the center and a close (x) button in the top right
     * @param context Context
     * @param listener listener to send response back on
     * @param title Title (if null, will have visibility set to {@link View#GONE})
     * @param message Message / body (if null, will have visibility set to {@link View#GONE})
     * @param bitmapToSet Bitmap to set into the main IV.
     * @param bitmapToSet Scaletype to be used. If null, scaleType == {@link android.widget.ImageView.ScaleType#FIT_CENTER}
     * @return
     */
    public static Dialog buildCenterImageDialog(@NonNull final Context context,
                                                @NonNull final DialogFinishedListener listener,
                                                @Nullable final String title,
                                                @Nullable final String message,
                                                @NonNull final Bitmap bitmapToSet,
                                                @Nullable final ImageView.ScaleType scaleTypeToUse){
        return new CenterImageDialog(context, listener, title, message, bitmapToSet, scaleTypeToUse);
    }

    /**
     * Simple EditText dialog where a user can enter text into the ET available
     * @param context
     * @param listener
     * @param doneText
     * @param cancelText
     * @param title
     * @param message
     * @param editTextHint
     * @param textInputType
     * @return
     */
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

    public static Dialog buildSimpleTextIconDialog(@NonNull final Context context,
                                                   @NonNull final DialogFinishedListener listener,
                                                   @NonNull List<SimpleTextIconObject> simpleTextIconObjects){
        return buildSimpleTextIconDialog(context, listener, simpleTextIconObjects, null,
                null, null, null);
    }

    public static Dialog buildSimpleTextIconDialog(@NonNull final Context context,
                                                   @NonNull final DialogFinishedListener listener,
                                                   @NonNull List<SimpleTextIconObject> simpleTextIconObjects,
                                                   @Nullable String title, @Nullable String message,
                                                   @Nullable Integer backgroundColor, @Nullable Integer textColor){
        Dialog d = new TextIconSelectDialog(context, listener, title, message,
                simpleTextIconObjects, backgroundColor, textColor);
        return d;
    }

    /**
     * Simple dialog with an image in the center and the message / body right above it
     */
    private static class CenterImageDialog extends Dialog {

        //Objects
        private Context context;
        private String title, message;
        private DialogFinishedListener listener;
        private Bitmap bitmapToSet;
        private ImageView.ScaleType scaleType;

        //UI
        private RelativeLayout center_image_dialog_root;
        private ImageView center_image_dialog_iv, center_image_dialog_close_x;
        private TextView center_image_dialog_tv;

        CenterImageDialog(@NonNull final Context context,
                          @NonNull final DialogFinishedListener listener,
                          @Nullable String title, @Nullable String message,
                          @NonNull Bitmap bitmapToSet, @Nullable ImageView.ScaleType scaleType){
            super(context);
            this.title = title;
            this.context = context;
            this.message = message;
            this.listener = listener;
            this.bitmapToSet = bitmapToSet;
            this.scaleType = (scaleType == null) ? ImageView.ScaleType.FIT_CENTER : scaleType;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if(StringUtilities.isNullOrEmpty(title)){
                this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            setCanceledOnTouchOutside(false);
            setContentView(R.layout.center_image_dialog);

            checkForNulls();
            initUIFields();
            setUIFields();
            setAdapters();
        }

        private void checkForNulls() {
            if (StringUtilities.isNullOrEmpty(title)) {
                this.title = "";
            }
            if (StringUtilities.isNullOrEmpty(message)) {
                this.message = "";
            }
        }

        private void initUIFields() {
            this.center_image_dialog_root = (RelativeLayout) this.findViewById(
                    R.id.center_image_dialog_root);
            this.center_image_dialog_iv = (ImageView) this.findViewById(
                    R.id.center_image_dialog_iv);
            this.center_image_dialog_close_x = (ImageView) this.findViewById(
                    R.id.center_image_dialog_close_x);
            this.center_image_dialog_tv = (TextView) this.findViewById(
                    R.id.center_image_dialog_tv);
            this.center_image_dialog_iv.setScaleType(this.scaleType);
        }

        private void setUIFields(){
            if(!StringUtilities.isNullOrEmpty(title)) {
                this.setTitle(title);
            }
            if(this.bitmapToSet != null){
                this.center_image_dialog_iv.setImageBitmap(this.bitmapToSet);
            } else {
                this.center_image_dialog_iv.setImageDrawable(
                        new ColorDrawable(context.getResources().getColor(R.color.gray)));
            }
            if(!StringUtilities.isNullOrEmpty(this.message)){
                this.center_image_dialog_tv.setText(this.message);
                this.center_image_dialog_tv.setVisibility(View.VISIBLE);
            } else {
                this.center_image_dialog_tv.setText("");
                this.center_image_dialog_tv.setVisibility(View.GONE);
            }

            this.center_image_dialog_close_x.bringToFront();
        }

        private void setAdapters(){
            this.center_image_dialog_close_x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.dialogFinished(null, DialogUtilities.SIMPLE_CLOSE_RESPONSE);
                }
            });
        }
    }

    private static class TextIconSelectDialog extends Dialog {

        //Objects
        private Context context;
        private String title, message;
        private DialogFinishedListener listener;
        private List<SimpleTextIconObject> mListObjects;
        private Integer optionalBackgroundColor, optionalTextColor;

        //UI
        private RelativeLayout simple_text_image_dialog_root;
        private ListView simple_text_image_dialog_title_list_view;
        private TextView simple_text_image_dialog_message_tv, simple_text_image_dialog_title_tv;

        TextIconSelectDialog(@NonNull final Context context,
                             @NonNull final DialogFinishedListener listener,
                             @Nullable String title, @Nullable String message,
                             @NonNull List<SimpleTextIconObject> mListObjects,
                             @Nullable Integer optionalBackgroundColor,
                             @Nullable Integer optionalTextColor){
            super(context);
            this.title = title;
            this.message = message;
            this.context = context;
            this.listener = listener;
            this.mListObjects = mListObjects;
            this.optionalTextColor =optionalTextColor;
            this.optionalBackgroundColor = optionalBackgroundColor;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if(StringUtilities.isNullOrEmpty(title)){
                this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            setCanceledOnTouchOutside(false);
            setContentView(R.layout.simple_text_image_dialog);

            checkForNulls();
            initUIFields();
            setUIFields();
            setAdapters();
        }

        private void checkForNulls() {
            if (StringUtilities.isNullOrEmpty(title)) {
                this.title = "";
            }
            if (StringUtilities.isNullOrEmpty(message)) {
                this.message = "";
            }
        }

        private void initUIFields() {
            simple_text_image_dialog_root = (RelativeLayout) this.findViewById(
                    R.id.simple_text_image_dialog_root);
            simple_text_image_dialog_title_list_view = (ListView) this.findViewById(
                    R.id.simple_text_image_dialog_title_list_view);
            simple_text_image_dialog_title_tv = (TextView) this.findViewById(
                    R.id.simple_text_image_dialog_title_tv);
            simple_text_image_dialog_message_tv = (TextView) this.findViewById(
                    R.id.simple_text_image_dialog_message_tv);
        }

        private void setUIFields() {
            simple_text_image_dialog_title_tv.setText(title);
            simple_text_image_dialog_message_tv.setText(message);

            if(this.optionalTextColor != null){
                try {
                    this.simple_text_image_dialog_title_tv.setTextColor(ContextCompat.getColor(
                            context, optionalTextColor));
                    this.simple_text_image_dialog_message_tv.setTextColor(ContextCompat.getColor(
                            context, optionalTextColor));
                } catch (Resources.NotFoundException e){
                    this.simple_text_image_dialog_title_tv.setTextColor(optionalTextColor);
                    this.simple_text_image_dialog_message_tv.setTextColor(optionalTextColor);
                }
            }
            if(this.optionalBackgroundColor != null){
                try {
                    this.simple_text_image_dialog_root.setBackgroundColor(ContextCompat.getColor(
                            context, optionalBackgroundColor));
                } catch (Resources.NotFoundException e){
                    this.simple_text_image_dialog_root.setBackgroundColor(optionalBackgroundColor);
                }
            }
        }

        private void setAdapters(){
            TextIconAdapter adapter = new TextIconAdapter(context, R.layout.simple_text_icon_item, mListObjects, new CustomClickCallbackLink() {
                @Override
                public void itemClicked(@Nullable Object object, @Nullable Integer customTag, @Nullable Integer positionIfAvailable) {
                    if(customTag != null){
                        if(customTag == PGMacTipsConstants.TAG_SIMPLE_TEXT_ICON_ADAPTER_CLICK){
                            TextIconSelectDialog.this.dismiss();
                            listener.dialogFinished(object,
                                    PGMacTipsConstants.TAG_SIMPLE_TEXT_ICON_ADAPTER_CLICK);
                        }
                    }
                }
            }, PGMacTipsConstants.TAG_SIMPLE_TEXT_ICON_ADAPTER_CLICK);
            try {
                adapter.setBackgroundColor(ContextCompat.getColor(context,
                        optionalBackgroundColor));
            } catch (Resources.NotFoundException e){
                adapter.setBackgroundColor(optionalBackgroundColor);
            }
            try {
                adapter.setTextColor(ContextCompat.getColor(context,
                        optionalTextColor));
            } catch (Resources.NotFoundException e){
                adapter.setTextColor(optionalTextColor);
            }

            simple_text_image_dialog_title_list_view.setAdapter(adapter);
        }
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

            //Set Visibilities
            int yesVisibility = (StringUtilities.isNullOrEmpty(yesText)) ? View.GONE : View.VISIBLE;
            int neverVisibility = (StringUtilities.isNullOrEmpty(neverText)) ? View.GONE : View.VISIBLE;
            int laterVisibility = (StringUtilities.isNullOrEmpty(laterText)) ? View.GONE : View.VISIBLE;
            three_button_dialog_option_yes.setVisibility(yesVisibility);
            three_button_dialog_option_never.setVisibility(neverVisibility);
            three_button_dialog_option_later.setVisibility(laterVisibility);
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
