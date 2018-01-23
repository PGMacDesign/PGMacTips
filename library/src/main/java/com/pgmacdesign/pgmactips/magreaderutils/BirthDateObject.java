package com.pgmacdesign.pgmactips.magreaderutils;

import android.support.annotation.NonNull;

import com.pgmacdesign.pgmactips.misc.TempString;
import com.pgmacdesign.pgmactips.utilities.DateUtilities;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by pmacdowell on 2017-08-23.
 */

public final class BirthDateObject {

    private transient Date birthDate;
    private transient TempString birthYear;
    private transient TempString birthMonth;
    private transient TempString birthDay;

    public BirthDateObject(@NonNull String year, @NonNull String month, @NonNull String day){
        Calendar calendar = Calendar.getInstance();
        int yearInt, monthInt, dayInt;
        try {
            yearInt = Integer.parseInt(year);
            monthInt = (Integer.parseInt(month) - 1);
            dayInt = Integer.parseInt(day);
        } catch (Exception e){
            return;
        }
        calendar.set(yearInt, monthInt, dayInt);
        setVars(calendar);
    }

    public BirthDateObject(@NonNull Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setVars(calendar);
    }

    private void setVars(Calendar calendar){
        int year = calendar.get(Calendar.YEAR);
        int month = (calendar.get(Calendar.MONTH) + 1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        this.birthYear = new TempString(year + "");
        this.birthMonth = new TempString(month + "");
        this.birthDay = new TempString(day + "");
        this.birthDate = calendar.getTime();
    }

    public void clearTempString() {
        this.birthYear.disposeData();
        this.birthMonth.disposeData();
        this.birthDay.disposeData();
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public TempString getBirthYear() {
        return birthYear;
    }

    public TempString getBirthMonth() {
        return birthMonth;
    }

    public TempString getBirthDay() {
        return birthDay;
    }

    public boolean is18OrOver(){
        if(TempString.isTempStringEmptyOrNull(birthYear)){
            return false;
        }
        if(TempString.isTempStringEmptyOrNull(birthMonth)){
            return false;
        }
        if(TempString.isTempStringEmptyOrNull(birthDay)){
            return false;
        }
        Calendar dob = Calendar.getInstance();
        dob.set(Integer.parseInt(birthYear.getTempStringData()),
                Integer.parseInt(birthMonth.getTempStringData()),
                Integer.parseInt(birthDay.getTempStringData()));

        int age = 0;

        age = DateUtilities.getAge(dob);

        if(age >= 18){
            return true;
        } else {
            return false;
        }
    }

    public boolean is21OrOver(){
        if(TempString.isTempStringEmptyOrNull(birthYear)){
            return false;
        }
        if(TempString.isTempStringEmptyOrNull(birthMonth)){
            return false;
        }
        if(TempString.isTempStringEmptyOrNull(birthDay)){
            return false;
        }
        Calendar dob = Calendar.getInstance();
        dob.set(Integer.parseInt(birthYear.getTempStringData()),
                Integer.parseInt(birthMonth.getTempStringData()),
                Integer.parseInt(birthDay.getTempStringData()));

        int age = 0;

        age = DateUtilities.getAge(dob);

        if(age >= 21){
            return true;
        } else {
            return false;
        }
    }
}
