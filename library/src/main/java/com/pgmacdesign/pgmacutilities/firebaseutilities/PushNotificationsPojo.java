package com.pgmacdesign.pgmacutilities.firebaseutilities;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Class for de/serializing push notification objects
 * Created by pmacdowell on 10/19/2016.
 *

 //Sample Below
 {
    "data":{
        "customTag1":"5817d057ccc869088c3c8c82",
        "customTag2":"testing",
        "customTag3":1482190375,
        "customTag4":"lovelabChat",
        "customTag5":"581cbd34ccc869090c35579a"
    },
    "notification":{
        "badge":23,
        "body":"testing",
        "sound":"default",
        "title":"My Title"
    },
    "priority":"high",
    "to":"firebase_cloud_messaging_id_goes_here
 }

 */
public class PushNotificationsPojo {

    public PushNotificationsPojo(){
        //This needs to default to high for IOS Devices
        this.priority = "high";
    }

    //Variables
    @SerializedName("to")
    private String to;
    @SerializedName("data")
    private Map<String, Object> mapData;
    @SerializedName("notification")
    private CustomNotificationObject notification;
    @SerializedName("priority")
    private String priority;

    public CustomNotificationObject getNotification() {
        return notification;
    }

    public void setNotification(CustomNotificationObject notification) {
        this.notification = notification;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Map<String, Object> getMapData() {
        return mapData;
    }

    public void setMapData(Map<String, Object> mapData) {
        this.mapData = mapData;
    }

    /**
     * Custom Notifications Object
     */
    public static class CustomNotificationObject {
        private String body;
        private String title;
        private String sound;
        private Integer badge;

        public Integer getBadge() {
            if(badge == null){
                badge = 0;
            }
            return badge;
        }

        public void setBadge(Integer badge) {
            this.badge = badge;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }
    }

}
