package com.pgmacdesign.pgmacutilities.datamodels

import com.google.gson.annotations.SerializedName

/**
 * Created by Patrick-SSD2 on 11/5/2017.
 */
class GooglePlaceResults {

    @SerializedName("geometry")
    private var geometry: Geometry? = Geometry();
    @SerializedName("id")
    private var id: String? = null
    @SerializedName("place_id")
    private var placeId: String? = null
    @SerializedName("reference")
    private var reference: String? = null
    @SerializedName("formatted_address")
    private var address: String? = null
    @SerializedName("formatted_phone_number")
    private var phoneNumber: String? = null
    @SerializedName("name")
    private var name: String? = null
    @SerializedName("rating")
    private var rating: Float = 0.toFloat()
    @SerializedName("reviews")
    private var reviews: List<GooglePlaceReview>? = null
    @SerializedName("photos")
    private var photos: List<PlacePhoto>? = null
    @SerializedName("types")
    private var types: Array<String>? = null
    @SerializedName("vicinity")
    private var vicinity: String? = null
    @SerializedName("website")
    private var website: String? = null

    fun getGeometry(): Geometry? {
        return geometry
    }

    fun setGeometry(geometry: Geometry) {
        this.geometry = geometry
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getPlaceId(): String? {
        return placeId
    }

    fun setPlaceId(placeId: String) {
        this.placeId = placeId
    }

    fun getReference(): String? {
        return reference
    }

    fun setReference(reference: String) {
        this.reference = reference
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String) {
        this.address = address
    }

    fun getPhoneNumber(): String? {
        return phoneNumber
    }

    fun setPhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getRating(): Float {
        return rating
    }

    fun setRating(rating: Float) {
        this.rating = rating
    }

    fun getReviews(): List<GooglePlaceReview>? {
        return reviews
    }

    fun setReviews(reviews: List<GooglePlaceReview>) {
        this.reviews = reviews
    }

    fun getPhotos(): List<PlacePhoto>? {
        return photos
    }

    fun setPhotos(photos: List<PlacePhoto>) {
        this.photos = photos
    }

    fun getTypes(): Array<String>? {
        return types
    }

    fun setTypes(types: Array<String>) {
        this.types = types
    }

    fun getVicinity(): String? {
        return vicinity
    }

    fun setVicinity(vicinity: String) {
        this.vicinity = vicinity
    }

    fun getWebsite(): String? {
        return website
    }

    fun setWebsite(website: String) {
        this.website = website
    }

    class Geometry {
        @SerializedName("location")
        var location: GooglePlacesLocation? = GooglePlacesLocation()
    }


    class GooglePlacesLocation {
        @SerializedName("lat")
        var lat: Double? = 0.0;
        @SerializedName("lng")
        var lng: Double? = 0.0;

    }

    class GooglePlaceReview {
        @SerializedName("author_name")
        var author: String? = null
        @SerializedName("language")
        var language: String? = null //IE 'en'
        @SerializedName("profile_photo_url")
        var userPhotoUrl: String? = null
        @SerializedName("rating")
        var rating: Float = 0.toFloat()
        @SerializedName("relative_time_description")
        var whenReviewedString: String? = null
        @SerializedName("text")
        var reviewText: String? = null
        @SerializedName("time")
        var dateOfReview: Long = 0
    }

    class PlacePhoto {
        @SerializedName("height")
        var height: Float = 0.toFloat()
        @SerializedName("width")
        var width: Float = 0.toFloat()
        @SerializedName("html_attributions")
        var attributions: List<String>? = null
        @SerializedName("photo_reference")
        var reference: String? = null
    }
}