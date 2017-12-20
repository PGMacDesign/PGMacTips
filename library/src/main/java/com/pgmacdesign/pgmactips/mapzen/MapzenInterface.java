package com.pgmacdesign.pgmactips.mapzen;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by pmacdowell on 2017-02-17.
 */

public interface MapzenInterface {

    static final String VERSION_STRING = "/v1";

    /**
     * Search Map with a query
     *
     * @param api_key      String API Key
     * @param localizedLat Centralized Latitude, this will make it so that the search starts or
     *                     is focused on that latitude first. An example would be if you set the
     *                     localizedLat to a LosAngeles and query for "Paris", rather than it
     *                     returning Paris, France, it will return Saint George Parish School,
     *                     Ontario, CA, USA as that is closer and fits the Paris prefix.
     * @param localizedLng Centralized Longitude, this will make it so that the search starts or
     *                     is focused on that latitude first. An example would be if you set the
     *                     localizedLat to a LosAngeles and query for "Paris", rather than it
     *                     returning Paris, France, it will return Saint George Parish School,
     *                     Ontario, CA, USA as that is closer and fits the Paris prefix.
     * @param searchQuery  Query to search
     * @return {@link MapzenPOJO}
     */
    @GET(VERSION_STRING + "/autocomplete")
    Call<MapzenPOJO> searchMap(@Query("api_key") String api_key,
                                 @Query("focus.point.lat") double localizedLat,
                                 @Query("focus.point.lon") double localizedLng,
                                 @Query("text") String searchQuery);

    /**
     * Search Map with an added filter (See below for filters)
     *
     * @param api_key      String API Key
     * @param localizedLat Centralized Latitude, this will make it so that the search starts or
     *                     is focused on that latitude first. An example would be if you set the
     *                     localizedLat to a LosAngeles and query for "Paris", rather than it
     *                     returning Paris, France, it will return Saint George Parish School,
     *                     Ontario, CA, USA as that is closer and fits the Paris prefix.
     * @param localizedLng Centralized Longitude, this will make it so that the search starts or
     *                     is focused on that latitude first. An example would be if you set the
     *                     localizedLat to a LosAngeles and query for "Paris", rather than it
     *                     returning Paris, France, it will return Saint George Parish School,
     *                     Ontario, CA, USA as that is closer and fits the Paris prefix.
     * @param searchQuery  Query to search
     * @param searchFilter The filter options include:
     *                     -openstreetmap or osm
     *                     -openaddresses or oa
     *                     -geonames or gn
     *                     -whosonfirst or wof
     *                     Their details are covered here:
     *                     https://mapzen.com/documentation/search/autocomplete/
     *                     Short explanation is, including 'oa' for a query of "Pennsylvania"
     *                     will only return results that are on Pennsylvania ave, Pennsylvania st,
     *                     etc. Exclude it and you could receive Pennsylvania, USA, or
     *                     Pennsylvania Ave, Washington DC.
     * @return {@link MapzenPOJO}
     */
    @GET(VERSION_STRING + "/autocomplete")
    Call<MapzenPOJO> searchMapWithFilter(@Query("api_key") String api_key,
                                           @Query("focus.point.lat") double localizedLat,
                                           @Query("focus.point.lon") double localizedLng,
                                           @Query("text") String searchQuery,
                                           @Query("text") String searchFilter);

    /**
     * Search with a layer attached to narrow down results
     *
     * @param api_key      String API Key
     * @param localizedLat Centralized Latitude, this will make it so that the search starts or
     *                     is focused on that latitude first. An example would be if you set the
     *                     localizedLat to a LosAngeles and query for "Paris", rather than it
     *                     returning Paris, France, it will return Saint George Parish School,
     *                     Ontario, CA, USA as that is closer and fits the Paris prefix.
     * @param localizedLng Centralized Longitude, this will make it so that the search starts or
     *                     is focused on that latitude first. An example would be if you set the
     *                     localizedLat to a LosAngeles and query for "Paris", rather than it
     *                     returning Paris, France, it will return Saint George Parish School,
     *                     Ontario, CA, USA as that is closer and fits the Paris prefix.
     * @param searchQuery  Query to search
     * @param searchLayer  Layer options for narrowing down results. List includes:
     *                     -venue: points of interest, businesses, things with walls
     *                     -address:	places with a street address
     *                     -street:	streets,roads,highways
     *                     -country:	places that issue passports, nations, nation-states
     *                     -macroregion:	a related group of regions. Mostly in Europe
     *                     -region:	states and provinces
     *                     -macrocounty:	a related group of counties. Mostly in Europe.
     *                     -county:	official governmental area; usually bigger than a locality, almost always smaller than a region
     *                     -locality:	towns, hamlets, cities
     *                     -localadmin:	local administrative boundaries
     *                     -borough:	a local administrative boundary, currently only used for New York City
     *                     -neighbourhood:	social communities, neighbourhoods
     *                     -coarse:	alias for simultaneously using all administrative layers (everything except venue and address)
     * @return {@link MapzenPOJO}
     */
    @GET(VERSION_STRING + "/autocomplete")
    Call<MapzenPOJO> searchMapWithLayer(@Query("api_key") String api_key,
                                          @Query("focus.point.lat") double localizedLat,
                                          @Query("focus.point.lon") double localizedLng,
                                          @Query("text") String searchQuery,
                                          @Query("text") String searchLayer);

    /**
     * Search Map with a query
     *
     * @param api_key        String API Key
     * @param localizedLat   Centralized Latitude, this will make it so that the search starts or
     *                       is focused on that latitude first. An example would be if you set the
     *                       localizedLat to a LosAngeles and query for "Paris", rather than it
     *                       returning Paris, France, it will return Saint George Parish School,
     *                       Ontario, CA, USA as that is closer and fits the Paris prefix.
     * @param localizedLng   Centralized Longitude, this will make it so that the search starts or
     *                       is focused on that latitude first. An example would be if you set the
     *                       localizedLat to a LosAngeles and query for "Paris", rather than it
     *                       returning Paris, France, it will return Saint George Parish School,
     *                       Ontario, CA, USA as that is closer and fits the Paris prefix.
     * @param searchQuery    Query to search
     * @param countryISOCode Country ISO code. United States, IE, is 'USA' or 'US'
     *                       Full list can be found here:
     *                       https://en.wikipedia.org/wiki/ISO_3166-1
     * @return {@link MapzenPOJO}
     */
    @GET(VERSION_STRING + "/autocomplete")
    Call<MapzenPOJO> searchMapViaCountry(@Query("api_key") String api_key,
                                           @Query("focus.point.lat") double localizedLat,
                                           @Query("focus.point.lon") double localizedLng,
                                           @Query("text") String searchQuery,
                                           @Query("country") String countryISOCode);

    /**
     * Search Map with a query
     *
     * @param api_key      String API Key
     * @param localizedLat Centralized Latitude, this will make it so that the search starts or
     *                     is focused on that latitude first. An example would be if you set the
     *                     localizedLat to a LosAngeles and query for "Paris", rather than it
     *                     returning Paris, France, it will return Saint George Parish School,
     *                     Ontario, CA, USA as that is closer and fits the Paris prefix.
     * @param localizedLng Centralized Longitude, this will make it so that the search starts or
     *                     is focused on that latitude first. An example would be if you set the
     *                     localizedLat to a LosAngeles and query for "Paris", rather than it
     *                     returning Paris, France, it will return Saint George Parish School,
     *                     Ontario, CA, USA as that is closer and fits the Paris prefix.
     * @param searchQuery  Query to search
     * @param minLat       The lowest boundary of latitude to search
     * @param maxLat       The highest boundary of latitude to search
     * @param minLng       The lowest boundary of longitude to search
     * @param maxLng       The highest boundary of longitude to search
     * @return {@link MapzenPOJO}
     */
    @GET(VERSION_STRING + "/autocomplete")
    Call<MapzenPOJO> searchMapWithinBounds(@Query("api_key") String api_key,
                                             @Query("focus.point.lat") double localizedLat,
                                             @Query("focus.point.lon") double localizedLng,
                                             @Query("text") String searchQuery,
                                             @Query("boundary.rect.min_lat") double minLat,
                                             @Query("boundary.rect.max_lat") double maxLat,
                                             @Query("boundary.rect.min_lon") double minLng,
                                             @Query("boundary.rect.max_lon") double maxLng);


}
