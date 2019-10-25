package com.pgmacdesign.pgmactips.magreaderutils;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.pgmacdesign.pgmactips.misc.TempString;
import com.pgmacdesign.pgmactips.utilities.DateUtilities;

import java.util.Calendar;
import java.util.Date;

/**
 * POJO for use in the {@link MagneticTrackMagReader} class
 */
public final class BirthDateObject {

    @SerializedName("birthDate")
    private transient Date birthDate;
    @SerializedName("birthYear")
    private transient TempString birthYear;
    @SerializedName("birthMonth")
    private transient TempString birthMonth;
    @SerializedName("birthDay")
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

    /**
     * Overloaded on name due to me never remembering the other method
     */
    public void dispose(){
        clearTempString();
    }

    /**
     * Clear all temp strings
     */
    public void clearTempString() {
        if(this.birthYear != null) this.birthYear.disposeData();
        if(this.birthMonth != null) this.birthMonth.disposeData();
        if(this.birthDay != null) this.birthDay.disposeData();
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
