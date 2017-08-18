
package com.pgmacdesign.pgmacutilities.creditcardutils;


import android.support.annotation.NonNull;

import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.io.Serializable;

import static com.pgmacdesign.pgmacutilities.utilities.StringUtilities.isNullOrEmpty;
import static com.pgmacdesign.pgmacutilities.utilities.StringUtilities.keepLettersOnly;
import static com.pgmacdesign.pgmacutilities.utilities.StringUtilities.toUpperCase;

/**
 * Parses and represents the cardholder's name.
 */
public class Name extends BaseTempData implements Serializable {

    private final String firstName;
    private final String lastName;

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
        final String[] splitName = StringUtilities.removeSpaces(rawName).split("/");
        firstName = name(splitName, 1);
        lastName = name(splitName, 0);
    }

    /**
     * Overloaded constructor to allow for manual setting
     * @param firstName
     * @param lastName
     */
    public Name(String firstName, String lastName) {
        super(null);
        this.firstName = firstName;
        this.lastName = lastName;
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
            sb.append(StringUtilities.removeSpaces(firstName));
        }
        if ((!isNullOrEmpty(firstName)) && (!(isNullOrEmpty(lastName)))) {
            sb.append(" ");
        }
        if (!isNullOrEmpty(lastName)) {
            sb.append(StringUtilities.removeSpaces(lastName));
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {


        final int prime = 31;
        int result = 1;
        result = prime * result + (firstName == null ? 0 : firstName.hashCode());
        result = prime * result + (lastName == null ? 0 : lastName.hashCode());
        return result;
    }

    public boolean objectHasName() {
        if (!isNullOrEmpty(firstName)) {
            return true;
        }
        if (!isNullOrEmpty(lastName)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    private String name(@NonNull String[] splitName, final int position) {
        String name;
        StringBuilder sb = new StringBuilder();
        // TODO: 2017-08-03 look  back into if the name order is reversed
        int counter = 0;
        if (splitName.length < 1) {
            return null;
        }
        for (String str : splitName) {
            if (!isNullOrEmpty(str)) {
                if (counter > 0 && counter < (splitName.length - 1)) {
                    sb.append(" ");
                }
                sb.append(str);
            }
            counter++;
        }
        name = sb.toString();
        name = keepLettersOnly(name);
        name = toUpperCase(name);

        if (name == null) {
            name = "";
        }

        return name;
    }

}
