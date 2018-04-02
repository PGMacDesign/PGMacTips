package com.pgmacdesign.pgmactips.googleapis;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pmacdowell on 2018-04-02.
 */

public class GoogleVisionRequestModel {

    @SerializedName("requests")
    private List<VisionRequests> requests;

    public List<VisionRequests> getRequests() {
        return requests;
    }

    public void setRequests(List<VisionRequests> requests) {
        this.requests = requests;
    }



    /**
     * VisionRequests
     */
    public static class VisionRequests {
        @SerializedName("image")
        private VisionImage image;
        @SerializedName("features")
        private VisionFeatures features;

        public VisionFeatures getFeatures() {
            return features;
        }

        public void setFeatures(VisionFeatures features) {
            this.features = features;
        }

        public VisionImage getImage() {
            return image;
        }

        public void setImage(VisionImage image) {
            this.image = image;
        }
    }

    /**
     * Vision Image
     */
    public static class VisionImage {
        @SerializedName("content")
        private String base64ImageString;

        public String getBase64ImageString() {
            return base64ImageString;
        }

        public void setBase64ImageString(String base64ImageString) {
            this.base64ImageString = base64ImageString;
        }
    }

    /**
     * VisionFeatures
     */
    public static class VisionFeatures {
        public static enum DetectionTypes {
            DOCUMENT_TEXT_DETECTION, CROP_HINTS, FACE_DETECTION, WEB_DETECTION
        }

        @SerializedName("type")
        private DetectionTypes type;
        @SerializedName("maxResults")
        private Integer maxResults;

        public DetectionTypes getType() {
            return type;
        }

        public void setType(DetectionTypes type) {
            this.type = type;
        }

        public Integer getMaxResults() {
            return maxResults;
        }

        public void setMaxResults(Integer maxResults) {
            this.maxResults = maxResults;
        }
    }
}
