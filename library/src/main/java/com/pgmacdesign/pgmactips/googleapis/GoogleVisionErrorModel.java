package com.pgmacdesign.pgmactips.googleapis;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pmacdowell on 2018-04-02.
 */

public class GoogleVisionErrorModel {

    @SerializedName("error")
    private VisionError error;

    public VisionError getError() {
        return error;
    }

    public void setError(VisionError error) {
        this.error = error;
    }

    public static class VisionError {
        @SerializedName("code")
        private int code;
        @SerializedName("message")
        private String message;
        @SerializedName("status")
        private String status;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
