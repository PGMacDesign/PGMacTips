package com.pgmacdesign.pgmacutilities.mapzen;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pmacdowell on 2017-02-17.
 */

public class MapzenPOJO {

    @SerializedName("type")
    private String type;
    @SerializedName("geocoding")
    private Geocoding geocoding;
    @SerializedName("features")
    private List<MapzenFeatures> features;
    //2 sets of coords, first set of lat, lng == bottom left. 2nd set lat, lng == top right.
    //LONGITUDE IS FIRST (lng, lat, lng, lat)
    @SerializedName("bbox")
    private double[] bbox;

    public double[] getBbox() {
        return bbox;
    }

    public void setBbox(double[] bbox) {
        this.bbox = bbox;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geocoding getGeocoding() {
        return geocoding;
    }

    public void setGeocoding(Geocoding geocoding) {
        this.geocoding = geocoding;
    }

    public List<MapzenFeatures> getFeatures() {
        return features;
    }

    public void setFeatures(List<MapzenFeatures> features) {
        this.features = features;
    }

    public static class Geocoding {
        @SerializedName("query")
        private GeocodingQuery query;
        @SerializedName("version")
        private String version;
        @SerializedName("timeStamp")
        private long timeStamp;

        public GeocodingQuery getQuery() {
            return query;
        }

        public void setQuery(GeocodingQuery query) {
            this.query = query;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }


    public static class GeocodingQuery {
        //Size of returned resulsts array
        @SerializedName("size")
        private int size;
        @SerializedName("private")
        private boolean isThisPrivate;
        @SerializedName("focus.point.lat")
        private double focusPointLat;
        @SerializedName("focus.point.lon")
        private boolean focusPointLng;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public boolean isThisPrivate() {
            return isThisPrivate;
        }

        public void setThisPrivate(boolean thisPrivate) {
            isThisPrivate = thisPrivate;
        }

        public double getFocusPointLat() {
            return focusPointLat;
        }

        public void setFocusPointLat(double focusPointLat) {
            this.focusPointLat = focusPointLat;
        }

        public boolean isFocusPointLng() {
            return focusPointLng;
        }

        public void setFocusPointLng(boolean focusPointLng) {
            this.focusPointLng = focusPointLng;
        }
    }

    public static class MapzenFeatures {
        @SerializedName("type")
        private String type;
        @SerializedName("geometry")
        private MapzenGeometry geometry;
        @SerializedName("properties")
        private MapzenProperties properties;
        //2 sets of coords, first set of lat, lng == bottom left. 2nd set lat, lng == top right.
        //LONGITUDE IS FIRST (lng, lat, lng, lat)
        @SerializedName("bbox")
        private double[] bbox;

        public double[] getBbox() {
            return bbox;
        }

        public void setBbox(double[] bbox) {
            this.bbox = bbox;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public MapzenGeometry getGeometry() {
            return geometry;
        }

        public void setGeometry(MapzenGeometry geometry) {
            this.geometry = geometry;
        }

        public MapzenProperties getProperties() {
            return properties;
        }

        public void setProperties(MapzenProperties properties) {
            this.properties = properties;
        }


    }

    public static class MapzenGeometry {
        @SerializedName("type")
        private String type;
        //Structured with lat, lng separated by array positions where LONGITUDE IS FIRST,
        // IE [-117.222, 33.999]
        @SerializedName("coordinates")
        private double[] coordinates;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double[] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[] coordinates) {
            this.coordinates = coordinates;
        }
    }

    public static class MapzenProperties {
        @SerializedName("id")
        private String id;
        @SerializedName("gid")
        private String gid;
        @SerializedName("layer")
        private String layer;
        @SerializedName("source")
        private String source;
        @SerializedName("source_id")
        private String source_id;
        @SerializedName("name")
        private String name;
        @SerializedName("distance")
        private double distance;
        @SerializedName("accuracy")
        private String accuracy;
        @SerializedName("country")
        private String country;
        //Region == state (IE California)
        @SerializedName("region")
        private String state;
        //State abbreviation (IE Calirofnia == CA)
        @SerializedName("region_a")
        private String stateAbbreviation;
        @SerializedName("county")
        private String county;
        //locality == city
        @SerializedName("locality")
        private String city;
        //Zip code
        @SerializedName("postalcode")
        private String postalcode;
        @SerializedName("neighbourhood")
        private String neighbourhood;
        //String structured like this: "Saint George Parish School, Ontario, CA, USA"
        @SerializedName("label")
        private String label;

        public String getPostalcode() {
            return postalcode;
        }

        public void setPostalcode(String postalcode) {
            this.postalcode = postalcode;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getLayer() {
            return layer;
        }

        public void setLayer(String layer) {
            this.layer = layer;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getSource_id() {
            return source_id;
        }

        public void setSource_id(String source_id) {
            this.source_id = source_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public String getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(String accuracy) {
            this.accuracy = accuracy;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStateAbbreviation() {
            return stateAbbreviation;
        }

        public void setStateAbbreviation(String stateAbbreviation) {
            this.stateAbbreviation = stateAbbreviation;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getNeighbourhood() {
            return neighbourhood;
        }

        public void setNeighbourhood(String neighbourhood) {
            this.neighbourhood = neighbourhood;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

}
