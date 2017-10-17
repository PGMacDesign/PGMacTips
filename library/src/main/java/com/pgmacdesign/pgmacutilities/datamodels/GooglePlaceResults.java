package com.pgmacdesign.pgmacutilities.datamodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data model class that is used for Google Places
 * Created by pmacdowell on 2017-10-17.
 */
public class GooglePlaceResults {

    @SerializedName("geometry")
    private Geometry geometry;
    @SerializedName("id")
    private String id;
    @SerializedName("place_id")
    private String placeId;
    @SerializedName("reference")
    private String reference;
    @SerializedName("formatted_address")
    private String address;
    @SerializedName("formatted_phone_number")
    private String phoneNumber;
    @SerializedName("name")
    private String name;
    @SerializedName("rating")
    private float rating;
    @SerializedName("reviews")
    private List<GooglePlaceReview> reviews;
    @SerializedName("photos")
    private List<PlacePhoto> photos;
    @SerializedName("types")
    private String[] types;
    @SerializedName("vicinity")
    private String vicinity;
    @SerializedName("website")
    private String website;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<GooglePlaceReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<GooglePlaceReview> reviews) {
        this.reviews = reviews;
    }

    public List<PlacePhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PlacePhoto> photos) {
        this.photos = photos;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public static class Geometry {
        @SerializedName("location")
        private GooglePlacesLocation location;

        public GooglePlacesLocation getLocation() {
            return location;
        }

        public void setLocation(GooglePlacesLocation location) {
            this.location = location;
        }
    }


    public static class GooglePlacesLocation {
        @SerializedName("lat")
        private Double lat;
        @SerializedName("lng")
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }
    }

    public static class GooglePlaceReview {
        @SerializedName("author_name")
        private String author;
        @SerializedName("language")
        private String language; //IE 'en'
        @SerializedName("profile_photo_url")
        private String userPhotoUrl;
        @SerializedName("rating")
        private float rating;
        @SerializedName("relative_time_description")
        private String whenReviewedString;
        @SerializedName("text")
        private String reviewText;
        @SerializedName("time")
        private long dateOfReview;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getUserPhotoUrl() {
            return userPhotoUrl;
        }

        public void setUserPhotoUrl(String userPhotoUrl) {
            this.userPhotoUrl = userPhotoUrl;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public String getWhenReviewedString() {
            return whenReviewedString;
        }

        public void setWhenReviewedString(String whenReviewedString) {
            this.whenReviewedString = whenReviewedString;
        }

        public String getReviewText() {
            return reviewText;
        }

        public void setReviewText(String reviewText) {
            this.reviewText = reviewText;
        }

        public long getDateOfReview() {
            return dateOfReview;
        }

        public void setDateOfReview(long dateOfReview) {
            this.dateOfReview = dateOfReview;
        }
    }

    public static class PlacePhoto {
        @SerializedName("height")
        private float height;
        @SerializedName("width")
        private float width;
        @SerializedName("html_attributions")
        private List<String> attributions;
        @SerializedName("photo_reference")
        private String reference;

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public List<String> getAttributions() {
            return attributions;
        }

        public void setAttributions(List<String> attributions) {
            this.attributions = attributions;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }
    }
}

