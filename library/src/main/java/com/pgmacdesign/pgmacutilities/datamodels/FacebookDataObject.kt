package com.pgmacdesign.pgmacutilities.datamodels

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Patrick-SSD2 on 11/5/2017.
 */
class FacebookDataObject {


    @SerializedName("facebookAccessToken")
    private var facebookAccessToken: String? = null //String representation of the access token
    @SerializedName("facebookUserId")
    private var facebookUserId: String? = null
    @SerializedName("userEmail")
    private var userEmail: String? = null
    @SerializedName("tempAlbumId")
    private var tempAlbumId: String? = null //Used for passing Album ID between activities
    //@SerializedName("accessToken")
    //private AccessToken accessToken;
    @SerializedName("firstName")
    private var firstName: String? = null
    @SerializedName("lastName")
    private var lastName: String? = null
    @SerializedName("imageUrl")
    private var imageUrl: String? = null
    @SerializedName("facebookPhotoObjects")
    private var facebookPhotoObjects: List<FacebookPhotoObject>? = null
    @SerializedName("facebookPhotoPlace")
    private var facebookPhotoPlace: List<FacebookPhotoPlace>? = null
    @SerializedName("facebookLocationObject")
    private var facebookLocationObject: List<FacebookLocationObject>? = null
    @SerializedName("facebookPhotoAlbums")
    private var facebookPhotoAlbums: List<FacebookPhotoAlbum>? = null
    @SerializedName("facebookPhotoIDUrls")
    private var facebookPhotoIDUrls: Map<String, String>? = null
    @SerializedName("facebookPhotoAlbum")
    private var facebookPhotoAlbum: FacebookPhotoAlbum? = null
    @SerializedName("facebookAlbumObject")
    private var facebookAlbumObject: FacebookAlbumObject? = null
    @SerializedName("facebookPhotoObject")
    private var facebookPhotoObject: FacebookPhotoObject? = null
    @SerializedName("facebookPhotoParsingData")
    private var facebookPhotoParsingData: FacebookPhotoParsingData? = null
    @SerializedName("facebookUserData")
    private var facebookUserData: FacebookUserData? = null

    /*
    These are all part of the Access token object
     */
    @SerializedName("token")
    private var token: String? = null
    @SerializedName("applicationId")
    private var applicationId: String? = null
    @SerializedName("userId")
    private var userId: String? = null
    @SerializedName("permissions")
    private var permissions: Collection<String>? = null
    @SerializedName("deniedPermissions")
    private var deniedPermissions: Collection<String>? = null
    @SerializedName("expirationTime")
    private var expirationTime: Date? = null
    @SerializedName("lastRefreshTime")
    private var lastRefreshTime: Date? = null

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }

    fun getApplicationId(): String? {
        return applicationId
    }

    fun getFirstName(): String? {
        return firstName
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun getLastName(): String? {
        return lastName
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun setApplicationId(applicationId: String) {
        this.applicationId = applicationId
    }

    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun getPermissions(): Collection<String>? {
        return permissions
    }

    fun setPermissions(permissions: Collection<String>) {
        this.permissions = permissions
    }

    fun getDeniedPermissions(): Collection<String>? {
        return deniedPermissions
    }

    fun setDeniedPermissions(deniedPermissions: Collection<String>) {
        this.deniedPermissions = deniedPermissions
    }

    fun getExpirationTime(): Date? {
        return expirationTime
    }

    fun setExpirationTime(expirationTime: Date) {
        this.expirationTime = expirationTime
    }

    fun getLastRefreshTime(): Date? {
        return lastRefreshTime
    }

    fun setLastRefreshTime(lastRefreshTime: Date) {
        this.lastRefreshTime = lastRefreshTime
    }

    fun getFacebookPhotoIDUrls(): Map<String, String>? {
        return facebookPhotoIDUrls
    }

    fun setFacebookPhotoIDUrls(facebookPhotoIDUrls: Map<String, String>) {
        this.facebookPhotoIDUrls = facebookPhotoIDUrls
    }

    fun getFacebookPhotoAlbum(): FacebookPhotoAlbum? {
        return facebookPhotoAlbum
    }

    fun setFacebookPhotoAlbum(facebookPhotoAlbum: FacebookPhotoAlbum) {
        this.facebookPhotoAlbum = facebookPhotoAlbum
    }

    fun getFacebookPhotoObject(): FacebookPhotoObject? {
        return facebookPhotoObject
    }

    fun setFacebookPhotoObject(facebookPhotoObject: FacebookPhotoObject) {
        this.facebookPhotoObject = facebookPhotoObject
    }

    fun getTempAlbumId(): String? {
        return tempAlbumId
    }

    fun setTempAlbumId(tempAlbumId: String) {
        this.tempAlbumId = tempAlbumId
    }

    fun getFacebookPhotoAlbums(): List<FacebookPhotoAlbum>? {
        return facebookPhotoAlbums
    }

    fun setFacebookPhotoAlbums(facebookPhotoAlbums: List<FacebookPhotoAlbum>) {
        this.facebookPhotoAlbums = facebookPhotoAlbums
    }

    fun getFacebookUserData(): FacebookUserData? {
        return facebookUserData
    }

    fun setFacebookUserData(facebookUserData: FacebookUserData) {
        this.facebookUserData = facebookUserData
    }

    fun getFacebookPhotoObjects(): List<FacebookPhotoObject>? {
        return facebookPhotoObjects
    }

    fun getFacebookPhotoParsingData(): FacebookPhotoParsingData? {
        return facebookPhotoParsingData
    }

    fun setFacebookPhotoParsingData(facebookPhotoParsingData: FacebookPhotoParsingData) {
        this.facebookPhotoParsingData = facebookPhotoParsingData
    }

    fun getFacebookAlbumObject(): FacebookAlbumObject? {
        return facebookAlbumObject
    }

    fun setFacebookAlbumObject(facebookAlbumObject: FacebookAlbumObject) {
        this.facebookAlbumObject = facebookAlbumObject
    }

    fun getFacebookPhotoPlace(): List<FacebookPhotoPlace>? {
        return facebookPhotoPlace
    }

    fun setFacebookPhotoPlace(facebookPhotoPlace: List<FacebookPhotoPlace>) {
        this.facebookPhotoPlace = facebookPhotoPlace
    }

    fun getFacebookLocationObject(): List<FacebookLocationObject>? {
        return facebookLocationObject
    }

    fun setFacebookLocationObject(facebookLocationObject: List<FacebookLocationObject>) {
        this.facebookLocationObject = facebookLocationObject
    }

    fun setFacebookPhotoObjects(facebookPhotoObjects: List<FacebookPhotoObject>) {
        this.facebookPhotoObjects = facebookPhotoObjects
    }

    fun getUserEmail(): String? {
        return userEmail
    }

    fun setUserEmail(userEmail: String) {
        this.userEmail = userEmail
    }

//
//    public AccessToken getAccessToken() {
//        //return accessToken;
//        try {
//            return (new AccessToken(
//                    token,
//                    applicationId,
//                    userId,
//                    permissions,
//                    deniedPermissions,
//                    accessTokenSource,
//                    expirationTime,
//                    lastRefreshTime));
//        } catch (Exception e){
//            try {
//                return this.accessToken;
//            } catch (Exception e1){
//                return  null;
//            }
//        }
//
//    }
//
//    public void setAccessToken(AccessToken accessToken) {
//        this.setAccessToken(
//                accessToken.getToken(),
//                accessToken.getApplicationId(),
//                accessToken.getUserId(),
//                accessToken.getPermissions(),
//                accessToken.getDeclinedPermissions(),
//                accessToken.getSource(),
//                accessToken.getExpires(),
//                accessToken.getLastRefresh()
//        );
//    }
//
//    public void setAccessToken(String token, String applicationId, String userId,
//                               Collection<String> permissions, Collection<String> deniedPermissions,
//                               AccessTokenSource accessTokenSource, Date expirationTime,
//                               Date lastRefreshTime) {
//        //For now, removing the setter as this is causing URI Issues. Each item is set individually
//        //this.accessToken = new AccessToken(token, applicationId, userId, permissions,
//        // deniedPermissions, accessTokenSource, expirationTime, lastRefreshTime);
//        this.token = token;
//        this.applicationId = applicationId;
//        this.userId = userId;
//        this.permissions = permissions;
//        this.deniedPermissions = deniedPermissions;
//        this.accessTokenSource = accessTokenSource;
//        this.expirationTime = expirationTime;
//        this.lastRefreshTime = lastRefreshTime;
//    }

    /*
    public Profile getProfile() {
        return profile;
    }
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    */

    fun getFacebookAccessToken(): String? {
        return facebookAccessToken
    }

    fun setFacebookAccessToken(facebookAccessToken: String) {
        this.facebookAccessToken = facebookAccessToken
    }

    fun getFacebookUserId(): String? {
        return facebookUserId
    }

    fun setFacebookUserId(facebookUserId: String) {
        this.facebookUserId = facebookUserId
    }

    /**
     * For parsing the user object
     * https://developers.facebook.com/docs/graph-api/reference/user
     */
    class FacebookUserData  {
        @SerializedName("id")
        var id: String? = null
        @SerializedName("about")
        var about: String? = null
        @SerializedName("age_range")
        var ageRange: FacebookAgeRange? = null
        @SerializedName("birthday")
        var birthday: String? = null
        @SerializedName("email")
        var email: String? = null
        @SerializedName("first_name")
        private var firstName: String? = null
        @SerializedName("last_name")
        private var lastName: String? = null
        @SerializedName("relationship_status")
        var relationshipStatus: String? = null
        @SerializedName("gender")
        var gender: String? = null //male or female. no abbreviation
        @SerializedName("source")
        var source: String? = null //Image URL for their profile image
    }

    /**
     * Age range... because just giving us the age would be wayyyyy to difficult FB.
     * https://developers.facebook.com/docs/graph-api/reference/age-range/
     */
    class FacebookAgeRange {
        //This may be an int or an enum, trying the int first
        @SerializedName("max")
        var max: Int = 0
        @SerializedName("min")
        var min: Int = 0
    }

    /**
     * Facebook parsing class
     */
    class FacebookPhotoParsingData {
        @SerializedName("data")
        var data: Array<FacebookPhotoObject>? = null
        @SerializedName("paging")
        var paging: Any? = null
        @SerializedName("next")
        var next: String? = null
    }

    class FacebookAlbumObject {
        @SerializedName("data")
        var data: Array<FacebookPhotoAlbum>? = null
        @SerializedName("paging")
        var paging: Any? = null
    }

    /**
     * Facebook's Photo pojo
     * GET /v2.6/{photo-id}
     */
    class FacebookPhotoObject {
        @SerializedName("url")
        private var imageUrl: String? = null
        @SerializedName("id")
        var id: String? = null
        @SerializedName("album")
        var album: FacebookPhotoAlbum? = null
        @SerializedName("Place")
        var place: FacebookPhotoPlace? = null
        @SerializedName("link")
        var link: String? = null
        @SerializedName("datetime")
        var dateCreated: Date? = null
        @SerializedName("picture")
        var picture: String? = null //100 pixel wide representation of the photo. Really tiny
        @SerializedName("images")
        var images: Array<FacebookImage>? = null

    }

    class FacebookImage {
        @SerializedName("height")
        var height: Int = 0
        @SerializedName("width")
        var width: Int = 0
        @SerializedName("source")
        var source: String? = null
    }

    /**
     * Facebook's Album pojo
     * Album /{album-id}
     */
    class FacebookPhotoAlbum {
        @SerializedName("id")
        var id: String? = null
        @SerializedName("count")
        var count: Int = 0 //Number of photos in the album
        @SerializedName("description")
        var description: String? = null
        @SerializedName("link")
        var linkToAlbum: String? = null
        @SerializedName("name")
        var nameOfAlbum: String? = null
        /*
        Here, the cover_photo variable is NOT a String (like everywhere else), but is in fact an
        object. If implemented here, change up data model entirely, otherwise, leave it out.
         */

        @SerializedName("created_time")
        var created_time: Date? = null
    }

    /**
     * Facebook's Place pojo
     * GET /v2.6/{place-id}
     */
    class FacebookPhotoPlace {
        @SerializedName("id")
        var id: String? = null
        @SerializedName("name")
        var name: String? = null
        @SerializedName("location")
        var location: FacebookLocationObject? = null
    }


    class FacebookLocationObject  {
        @SerializedName("street")
        var street: String? = null
        @SerializedName("city")
        var city: String? = null
        @SerializedName("state")
        var state: String? = null
        @SerializedName("zip")
        var zip: String? = null
        @SerializedName("country")
        var country: String? = null
        @SerializedName("latitude")
        var latitude: Float = 0.toFloat()
        @SerializedName("longitude")
        var longitude: Float = 0.toFloat()
    }
}