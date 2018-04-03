package com.pgmacdesign.pgmactips.googleapis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by pmacdowell on 2018-04-02.
 */

public interface GoogleVisionServiceInterface {

    final String API_SUFFIX_STRING = "";
    final String VERSION_STRING = "/v1";
    final String IMAGES = "/images:annotate";

    @POST(API_SUFFIX_STRING + VERSION_STRING + IMAGES)
    Call<ResponseBody> visionCall(@Query("key") String authKey,
                                  @Body GoogleVisionRequestModel visionDataModel);

}
