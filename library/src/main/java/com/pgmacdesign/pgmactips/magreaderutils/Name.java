
package com.pgmacdesign.pgmactips.magreaderutils;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.pgmacdesign.pgmactips.misc.TempString;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import static com.pgmacdesign.pgmactips.utilities.StringUtilities.isNullOrEmpty;


/**
 * POJO for use in the {@link MagneticTrackMagReader} class
 */
public class Name extends BaseTempData {

    @SerializedName("firstName")
    private transient final TempString firstName;
    @SerializedName("lastName")
    private transient final TempString lastName;

    /**
     * Empty constructor
     */
    public Name() {
        this(null);
    }

    /**
     * Overloaded constructor to allow for parsing
     * @param rawName
     */
    public Name(String rawName) {
        super(rawName);
        if(StringUtilities.isNullOrEmpty(rawName)){
            firstName = new TempString("");
            lastName = new TempString("");
            return;
        }
        final String[] splitName = rawName.split("/");
        //final String[] splitName = StringUtilities.removeSpaces(rawName).split("/");
        firstName = new TempString(name(splitName, 1));
        lastName = new TempString(name(splitName, 0));
    }

    /**
     * Overloaded constructor to allow for manual setting
     * @param firstName
     * @param lastName
     */
    public Name(String firstName, String lastName) {
        super(null);
        this.firstName = new TempString(firstName);
        this.lastName = new TempString(lastName);
    }

    @Override
    public boolean tempStringTooLong() {
        String str = this.getTempString();
        str = StringUtilities.removeSpaces(str);
        if (isNullOrEmpty(str)) {
            return true;
        }
        return (str.length() > 26);
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (!isNullOrEmpty(firstName)) {
            sb.append(firstName.getTempStringData().trim());
        }
        if ((!isNullOrEmpty(firstName.getTempStringData()))
                && (!(isNullOrEmpty(lastName.getTempStringData().trim())))) {
            sb.append(" ");
        }
        if (!isNullOrEmpty(lastName)) {
            sb.append(lastName.getTempStringData());
        }
        return sb.toString();
    }

    public boolean objectHasName() {
        if (!isNullOrEmpty(firstName.getTempStringData())) {
            return true;
        }
        if (!isNullOrEmpty(lastName.getTempStringData())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    private String name(@NonNull String[] splitName, final int position) {
        String name = null;
        StringBuilder sb = new StringBuilder();
        if (splitName.length < 1) {
            return null;
        }
        if(splitName.length > position){
            try {
                name = splitName[position];
                name = name.trim();
                name = StringUtilities.toUpperCase(name);
                return name;
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if (name == null) {
            name = "";
        }

        return name;
    }

    public String getFirstName() {
        if(firstName != null){
            String str = firstName.getTempStringData();
            if(!isNullOrEmpty(str)){
                return str.trim();
            }
        }
        return null;
    }

    public String getLastName() {
        if(lastName != null){
            String str = lastName.getTempStringData();
            if(!isNullOrEmpty(str)){
                return str.trim();
            }
        }
        return null;
    }

    public void dispose(){
        if(this.firstName != null) this.firstName.disposeData();
        if(this.lastName != null) this.lastName.disposeData();
    }
}
