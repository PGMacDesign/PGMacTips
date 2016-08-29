package com.pgmacdesign.pgmacutilities.retrofitutilities;

import com.google.gson.annotations.SerializedName;

/**
 * This class can be used for a server passing back a String with no correct JSON formatting to it.
 * Created by pmacdowell on 8/29/2016.
 */
public class GenericStringResponse {
    @SerializedName("responseString")
    private String responseString;

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
}
