package com.pgmacdesign.pgmacutilities.datamodels;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by pmacdowell on 2017-10-18.
 */

public class FacebookDataObject {

    @SerializedName("facebookAccessToken")
    private String facebookAccessToken; //String representation of the access token
    @SerializedName("facebookUserId")
    private String facebookUserId;
    @SerializedName("userEmail")
    private String userEmail;
    @SerializedName("tempAlbumId")
    private String tempAlbumId; //Used for passing Album ID between activities
    //@SerializedName("accessToken")
    //private AccessToken accessToken;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("facebookPhotoObjects")
    private List<FacebookPhotoObject> facebookPhotoObjects;
    @SerializedName("facebookPhotoPlace")
    private List<FacebookPhotoPlace> facebookPhotoPlace;
    @SerializedName("facebookLocationObject")
    private List<FacebookLocationObject> facebookLocationObject;
    @SerializedName("facebookPhotoAlbums")
    private List<FacebookPhotoAlbum> facebookPhotoAlbums;
    @SerializedName("facebookPhotoIDUrls")
    private Map<String, String> facebookPhotoIDUrls;
    @SerializedName("facebookPhotoAlbum")
    private FacebookPhotoAlbum facebookPhotoAlbum;
    @SerializedName("facebookAlbumObject")
    private FacebookAlbumObject facebookAlbumObject;
    @SerializedName("facebookPhotoObject")
    private FacebookPhotoObject facebookPhotoObject;
    @SerializedName("facebookPhotoParsingData")
    private FacebookPhotoParsingData facebookPhotoParsingData;
    @SerializedName("facebookUserData")
    private FacebookUserData facebookUserData;

    /*
    These are all part of the Access token object
     */
    @SerializedName("token")
    private String token;
    @SerializedName("applicationId")
    private String applicationId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("permissions")
    private Collection<String> permissions;
    @SerializedName("deniedPermissions")
    private Collection<String> deniedPermissions;
    @SerializedName("expirationTime")
    private Date expirationTime;
    @SerializedName("lastRefreshTime")
    private Date lastRefreshTime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = permissions;
    }

    public Collection<String> getDeniedPermissions() {
        return deniedPermissions;
    }

    public void setDeniedPermissions(Collection<String> deniedPermissions) {
        this.deniedPermissions = deniedPermissions;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Date getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(Date lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public Map<String, String> getFacebookPhotoIDUrls() {
        return facebookPhotoIDUrls;
    }

    public void setFacebookPhotoIDUrls(Map<String, String> facebookPhotoIDUrls) {
        this.facebookPhotoIDUrls = facebookPhotoIDUrls;
    }

    public FacebookPhotoAlbum getFacebookPhotoAlbum() {
        return facebookPhotoAlbum;
    }

    public void setFacebookPhotoAlbum(FacebookPhotoAlbum facebookPhotoAlbum) {
        this.facebookPhotoAlbum = facebookPhotoAlbum;
    }

    public FacebookPhotoObject getFacebookPhotoObject() {
        return facebookPhotoObject;
    }

    public void setFacebookPhotoObject(FacebookPhotoObject facebookPhotoObject) {
        this.facebookPhotoObject = facebookPhotoObject;
    }

    public String getTempAlbumId() {
        return tempAlbumId;
    }

    public void setTempAlbumId(String tempAlbumId) {
        this.tempAlbumId = tempAlbumId;
    }

    public List<FacebookPhotoAlbum> getFacebookPhotoAlbums() {
        return facebookPhotoAlbums;
    }

    public void setFacebookPhotoAlbums(List<FacebookPhotoAlbum> facebookPhotoAlbums) {
        this.facebookPhotoAlbums = facebookPhotoAlbums;
    }

    public FacebookUserData getFacebookUserData() {
        return facebookUserData;
    }

    public void setFacebookUserData(FacebookUserData facebookUserData) {
        this.facebookUserData = facebookUserData;
    }

    public List<FacebookPhotoObject> getFacebookPhotoObjects() {
        return facebookPhotoObjects;
    }

    public FacebookPhotoParsingData getFacebookPhotoParsingData() {
        return facebookPhotoParsingData;
    }

    public void setFacebookPhotoParsingData(FacebookPhotoParsingData facebookPhotoParsingData) {
        this.facebookPhotoParsingData = facebookPhotoParsingData;
    }

    public FacebookAlbumObject getFacebookAlbumObject() {
        return facebookAlbumObject;
    }

    public void setFacebookAlbumObject(FacebookAlbumObject facebookAlbumObject) {
        this.facebookAlbumObject = facebookAlbumObject;
    }

    public List<FacebookPhotoPlace> getFacebookPhotoPlace() {
        return facebookPhotoPlace;
    }

    public void setFacebookPhotoPlace(List<FacebookPhotoPlace> facebookPhotoPlace) {
        this.facebookPhotoPlace = facebookPhotoPlace;
    }

    public List<FacebookLocationObject> getFacebookLocationObject() {
        return facebookLocationObject;
    }

    public void setFacebookLocationObject(List<FacebookLocationObject> facebookLocationObject) {
        this.facebookLocationObject = facebookLocationObject;
    }

    public void setFacebookPhotoObjects(List<FacebookPhotoObject> facebookPhotoObjects) {
        this.facebookPhotoObjects = facebookPhotoObjects;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public String getFacebookAccessToken() {
        return facebookAccessToken;
    }

    public void setFacebookAccessToken(String facebookAccessToken) {
        this.facebookAccessToken = facebookAccessToken;
    }

    public String getFacebookUserId() {
        return facebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }

    /**
     * For parsing the user object
     * https://developers.facebook.com/docs/graph-api/reference/user
     */
    public static class FacebookUserData extends FacebookDataObject{
        @SerializedName("id")
        private String id;
        @SerializedName("about")
        private String about;
        @SerializedName("age_range")
        private FacebookAgeRange ageRange;
        @SerializedName("birthday")
        private String birthday;
        @SerializedName("email")
        private String email;
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("last_name")
        private String lastName;
        @SerializedName("relationship_status")
        private String relationshipStatus;
        @SerializedName("gender")
        private String gender; //male or female. no abbreviation
        @SerializedName("source")
        private String source; //Image URL for their profile image

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAbout() {
            return about;
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public FacebookAgeRange getAgeRange() {
            return ageRange;
        }

        public void setAgeRange(FacebookAgeRange ageRange) {
            this.ageRange = ageRange;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getRelationshipStatus() {
            return relationshipStatus;
        }

        public void setRelationshipStatus(String relationshipStatus) {
            this.relationshipStatus = relationshipStatus;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }

    /**
     * Age range... because just giving us the age would be wayyyyy to difficult FB.
     * https://developers.facebook.com/docs/graph-api/reference/age-range/
     */
    public static class FacebookAgeRange extends FacebookDataObject{
        //This may be an int or an enum, trying the int first
        @SerializedName("max")
        private int max;
        @SerializedName("min")
        private int min;

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }
    }

    /**
     * Facebook parsing class
     */
    public static class FacebookPhotoParsingData extends FacebookDataObject{
        @SerializedName("data")
        private FacebookPhotoObject[] data;
        @SerializedName("paging")
        private Object paging;
        @SerializedName("next")
        private String next;

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public FacebookPhotoObject[] getData() {
            return data;
        }

        public void setData(FacebookPhotoObject[] data) {
            this.data = data;
        }

        public Object getPaging() {
            return paging;
        }

        public void setPaging(Object paging) {
            this.paging = paging;
        }
    }

    public static class FacebookAlbumObject extends FacebookDataObject{
        @SerializedName("data")
        private FacebookPhotoAlbum[] data;
        @SerializedName("paging")
        private Object paging;

        public FacebookPhotoAlbum[] getData() {
            return data;
        }

        public void setData(FacebookPhotoAlbum[] data) {
            this.data = data;
        }

        public Object getPaging() {
            return paging;
        }

        public void setPaging(Object paging) {
            this.paging = paging;
        }
    }

    /**
     * Facebook's Photo pojo
     * GET /v2.6/{photo-id}
     */
    public static class FacebookPhotoObject extends FacebookDataObject {
        @SerializedName("url")
        private String imageUrl;
        @SerializedName("id")
        private String id;
        @SerializedName("album")
        private FacebookPhotoAlbum album;
        @SerializedName("Place")
        private FacebookPhotoPlace place;
        @SerializedName("link")
        private String link;
        @SerializedName("datetime")
        private Date dateCreated;
        @SerializedName("picture")
        private String picture; //100 pixel wide representation of the photo. Really tiny
        @SerializedName("images")
        private FacebookImage[] images;

        public FacebookImage[] getImages() {
            return images;
        }

        public void setImages(FacebookImage[] images) {
            this.images = images;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public FacebookPhotoAlbum getAlbum() {
            return album;
        }

        public void setAlbum(FacebookPhotoAlbum album) {
            this.album = album;
        }

        public FacebookPhotoPlace getPlace() {
            return place;
        }

        public void setPlace(FacebookPhotoPlace place) {
            this.place = place;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public Date getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(Date dateCreated) {
            this.dateCreated = dateCreated;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }
    }

    public static class FacebookImage extends FacebookDataObject{
        @SerializedName("height")
        private int height;
        @SerializedName("width")
        private int width;
        @SerializedName("source")
        private String source;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }
    /**
     * Facebook's Album pojo
     * Album /{album-id}
     */
    public static class FacebookPhotoAlbum extends FacebookDataObject {
        @SerializedName("id")
        private String id;
        @SerializedName("count")
        private int count; //Number of photos in the album
        @SerializedName("description")
        private String description;
        @SerializedName("link")
        private String linkToAlbum;
        @SerializedName("name")
        private String nameOfAlbum;
        @SerializedName("created_time")
        private Date created_time;

        /*
        Here, the cover_photo variable is NOT a String (like everywhere else), but is in fact an
        object. If implemented here, change up data model entirely, otherwise, leave it out.
         */

        public Date getCreated_time() {
            return created_time;
        }

        public void setCreated_time(Date updated_time) {
            this.created_time = updated_time;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLinkToAlbum() {
            return linkToAlbum;
        }

        public void setLinkToAlbum(String linkToAlbum) {
            this.linkToAlbum = linkToAlbum;
        }

        public String getNameOfAlbum() {
            return nameOfAlbum;
        }

        public void setNameOfAlbum(String nameOfAlbum) {
            this.nameOfAlbum = nameOfAlbum;
        }
    }

    /**
     * Facebook's Place pojo
     * GET /v2.6/{place-id}
     */
    public static class FacebookPhotoPlace extends FacebookDataObject {
        @SerializedName("id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("location")
        private FacebookLocationObject location;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public FacebookLocationObject getLocation() {
            return location;
        }

        public void setLocation(FacebookLocationObject location) {
            this.location = location;
        }
    }


    public static class FacebookLocationObject extends FacebookDataObject {
        @SerializedName("street")
        private String street;
        @SerializedName("city")
        private String city;
        @SerializedName("state")
        private String state;
        @SerializedName("zip")
        private String zip;
        @SerializedName("country")
        private String country;
        @SerializedName("latitude")
        private float latitude;
        @SerializedName("longitude")
        private float longitude;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public float getLatitude() {
            return latitude;
        }

        public void setLatitude(float latitude) {
            this.latitude = latitude;
        }

        public float getLongitude() {
            return longitude;
        }

        public void setLongitude(float longitude) {
            this.longitude = longitude;
        }
    }
}



