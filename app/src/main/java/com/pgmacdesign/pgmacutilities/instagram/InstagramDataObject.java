package com.pgmacdesign.pgmacutilities.instagram;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by pmacdowell on 2017-04-19.
 */

public class InstagramDataObject {


    @SerializedName("instagramPhotoIDUrls")
    private Map<String, String> instagramPhotoIDUrls;
    @SerializedName("nextMaxId")
    private String nextMaxId;
    @SerializedName("access_token")
    private String access_token;
    @SerializedName("user")
    private UserData user;
    @SerializedName("mediaDataObject")
    private MediaDataObject mediaDataObject;

    public MediaDataObject getMediaDataObject() {
        return mediaDataObject;
    }

    public void setMediaDataObject(MediaDataObject mediaDataObject) {
        this.mediaDataObject = mediaDataObject;
    }

    public String getNextMaxId() {
        return nextMaxId;
    }

    public void setNextMaxId(String nextMaxId) {
        this.nextMaxId = nextMaxId;
    }

    public Map<String, String> getInstagramPhotoIDUrls() {
        return instagramPhotoIDUrls;
    }

    public void setInstagramPhotoIDUrls(Map<String, String> instagramPhotoIDUrls) {
        this.instagramPhotoIDUrls = instagramPhotoIDUrls;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public static class MediaDataObject{
        @SerializedName("data")
        private MediaData[] data;
        @SerializedName("pagination")
        private Pagination pagination;

        public MediaData[] getData() {
            return data;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }

        public void setData(MediaData[] data) {
            this.data = data;
        }
    }

    public static class Pagination {
        @SerializedName("next_max_id")
        private String next_max_id;
        @SerializedName("next_url")
        private String next_url;

        public String getNext_url() {
            return next_url;
        }

        public void setNext_url(String next_url) {
            this.next_url = next_url;
        }

        public String getNext_max_id() {
            return next_max_id;
        }

        public void setNext_max_id(String next_max_id) {
            this.next_max_id = next_max_id;
        }
    }

    public static class MediaData{
        @SerializedName("link")
        private String link;
        @SerializedName("id")
        private String id;
        @SerializedName("images")
        private InstagramDataObject.Images images;
        @SerializedName("videos")
        private InstagramDataObject.Images videos;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Images getImages() {
            return images;
        }

        public void setImages(Images images) {
            this.images = images;
        }

        public Images getVideos() {
            return videos;
        }

        public void setVideos(Images videos) {
            this.videos = videos;
        }
    }

    public static class UserDataObject{
        @SerializedName("data")
        private UserData data;

        public UserData getData() {
            return data;
        }

        public void setData(UserData data) {
            this.data = data;
        }
    }
    public static class UserData{
        @SerializedName("id")
        private String id;
        @SerializedName("username")
        private String username;
        @SerializedName("full_name")
        private String full_name;
        @SerializedName("profile_picture")
        private String profile_picture;
        @SerializedName("bio")
        private String bio;
        @SerializedName("website")
        private String website;
        @SerializedName("counts")
        private Counts counts;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getProfile_picture() {
            return profile_picture;
        }

        public void setProfile_picture(String profile_picture) {
            this.profile_picture = profile_picture;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public Counts getCounts() {
            return counts;
        }

        public void setCounts(Counts counts) {
            this.counts = counts;
        }
    }

    public static class Counts{
        @SerializedName("media")
        private long media;
        @SerializedName("follows")
        private long follows;
        @SerializedName("followed_by")
        private long followed_by;

        public long getMedia() {
            return media;
        }

        public void setMedia(long media) {
            this.media = media;
        }

        public long getFollows() {
            return follows;
        }

        public void setFollows(long follows) {
            this.follows = follows;
        }

        public long getFollowed_by() {
            return followed_by;
        }

        public void setFollowed_by(long followed_by) {
            this.followed_by = followed_by;
        }
    }

    public static class Images{
        @SerializedName("low_resolution")
        private Resolution low_resolution;
        @SerializedName("standard_resolution")
        private Resolution standard_resolution;
        @SerializedName("thumbnail")
        private Resolution thumbnail;

        public Resolution getLow_resolution() {
            return low_resolution;
        }

        public void setLow_resolution(Resolution low_resolution) {
            this.low_resolution = low_resolution;
        }

        public Resolution getStandard_resolution() {
            return standard_resolution;
        }

        public void setStandard_resolution(Resolution standard_resolution) {
            this.standard_resolution = standard_resolution;
        }

        public Resolution getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(Resolution thumbnail) {
            this.thumbnail = thumbnail;
        }
    }

    public static class Resolution{
        @SerializedName("url")
        private String url;
        @SerializedName("width")
        private int width;
        @SerializedName("height")
        private int height;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}



