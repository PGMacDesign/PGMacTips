package com.pgmacdesign.pgmacutilities.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgmacdesign.pgmacutilities.R;

import java.util.Calendar;

/**
 * Created by Patrick MacDowell (PGMacDesign) on 2016-11-17.
 */

public class DialogUtilities {

    public static final int FAIL_RESPONSE = -1;
    public static final int OTHER_RESPONSE = 0;
    public static final int SUCCESS_RESPONSE = 1;

    public static interface DialogFinishedListener{
        public void dialogFinished(Object object, int tag);
    }

    public static  Dialog dimDialog(Dialog dialog){
        try {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
        } catch (Exception e){}
        return dialog;
    }

    public static AlertDialog dimDialog(AlertDialog dialog){
        try {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
        } catch (Exception e){}
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
                                                         final DialogFinishedListener listener){

        final Calendar rightNow = Calendar.getInstance();
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


    //Simple Alert Dialog
    public static AlertDialog buildSimpleOkDialog(final Context context,
                                                final DialogFinishedListener listener,
                                                String okText, String title, String message){
        if(StringUtilities.isNullOrEmpty(message)){
            return null;
        }
        if(context == null || listener == null){
            return null;
        }
        if(StringUtilities.isNullOrEmpty(okText)){
            okText = "Ok";
        }
        if(StringUtilities.isNullOrEmpty(title)){
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

    //Option Dialog Dialog
    public static AlertDialog buildOptionDialog(final Context context,
                                                final DialogFinishedListener listener,
                                                String yesText, String noText,
                                                String title, String message){
        if(StringUtilities.isNullOrEmpty(message)){
            return null;
        }
        if(context == null || listener == null){
            return null;
        }
        if(StringUtilities.isNullOrEmpty(yesText)){
            yesText = "Yes";
        }
        if(StringUtilities.isNullOrEmpty(noText)){
            noText = "No";
        }
        if(StringUtilities.isNullOrEmpty(title)){
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
                listener.dialogFinished(false, SUCCESS_RESPONSE);
            }
        });
        myBuilder.setMessage(message);
        myBuilder.setTitle(title);

        return myBuilder.create();
    }

    public static Dialog buildEditTextDialog(final Context context,
                                                  final DialogFinishedListener listener,
                                                  String doneText, String cancelText,
                                                  String title, String message,
                                                  String editTextHint, Integer textInputType){
        if(StringUtilities.isNullOrEmpty(message)){
            return null;
        }
        if(context == null || listener == null){
            return null;
        }

        EditTextDialog dialog = new EditTextDialog(
                context, listener, doneText, cancelText, title, editTextHint, textInputType
        );
        return dialog;

    }

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

        private void checkForNulls(){
            if(StringUtilities.isNullOrEmpty(doneText)){
                this.doneText = "Done";
            }
            if(StringUtilities.isNullOrEmpty(cancelText)){
                this.cancelText = "Cancel";
            }
            if(StringUtilities.isNullOrEmpty(title)){
                this.title = "";
            }
            if(textInputType == null){
                this.textInputType = InputType.TYPE_CLASS_TEXT; //TYPE_TEXT_VARIATION_NORMAL?
            }
            if(StringUtilities.isNullOrEmpty(editTextHint)){
                this.editTextHint = "Enter Information Here";
            }
        }

        private void initUIFields(){
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

        private void setUIFields(){

            this.edit_text_dialog_title.setText(title);
            this.edit_text_dialog_et.setHint(editTextHint);
            this.edit_text_dialog_cancel_button.setText(cancelText);
            this.edit_text_dialog_confirm_button.setText(doneText);
            try {
                this.edit_text_dialog_et.setInputType(textInputType);
            } catch (Exception e){
                this.edit_text_dialog_et.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            edit_text_dialog_cancel_button.setTag("cancel");
            edit_text_dialog_confirm_button.setTag("confirm");
            edit_text_dialog_cancel_button.setOnClickListener(this);
            edit_text_dialog_confirm_button.setOnClickListener(this);

            edit_text_dialog_et.addTextChangedListener(this);

        }

        @Override
        public void onClick(View v) {

            String tag = null;
            try {
                tag = (String) v.getTag();
            } catch (Exception e){
                tag = "";
            }

            if(tag.equals("cancel")){
                this.dismiss();
                listener.dialogFinished(null, FAIL_RESPONSE);
                return;
            }

            String str = edit_text_dialog_et.getText().toString();
            if(StringUtilities.isNullOrEmpty(str)){
                //Problem
                return;
            }

            if(tag.equals("confirm")){
                listener.dialogFinished(str, SUCCESS_RESPONSE);
            } else {
                //?
            }
            this.dismiss();

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if(StringUtilities.isNullOrEmpty(str)){
                edit_text_dialog_confirm_button.setEnabled(false);
            } else {
                edit_text_dialog_confirm_button.setEnabled(true);
            }
        }
    }


}
