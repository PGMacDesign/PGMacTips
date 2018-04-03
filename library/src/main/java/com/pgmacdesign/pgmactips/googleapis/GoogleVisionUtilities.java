package com.pgmacdesign.pgmactips.googleapis;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConfig;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.RetrofitClient;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.RetrofitParser;
import com.pgmacdesign.pgmactips.utilities.ImageUtilities;
import com.pgmacdesign.pgmactips.utilities.NumberUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;

/**
 * Created by pmacdowell on 2018-04-02.
 */

public class GoogleVisionUtilities {

    public static final String BASE_URL_FOR_GOOGLE_VISION = "https://vision.googleapis.com/";

    public static final int TAG_INVALID_BASE_64_IMAGE =
            PGMacTipsConstants.TAG_INVALID_BASE_64_IMAGE;
    public static final int TAG_INVALID_BITMAP_IMAGE =
            PGMacTipsConstants.TAG_INVALID_BITMAP_IMAGE;
    public static final int TAG_BITMAP_BASE64_CONVERSION_FAIL =
            PGMacTipsConstants.TAG_BITMAP_BASE64_CONVERSION_FAIL;
    public static final int TAG_GOOGLE_VISION_SUCCESS_RESULT =
            PGMacTipsConstants.TAG_GOOGLE_VISION_SUCCESS_RESULT;
    public static final int TAG_GOOGLE_VISION_FAIL_RESULT =
            PGMacTipsConstants.TAG_GOOGLE_VISION_FAIL_RESULT;
    public static final int TAG_GOOGLE_VISION_UNKNOWN_ERROR =
            PGMacTipsConstants.TAG_GOOGLE_VISION_UNKNOWN_ERROR;


    private String apiKey;
    private GoogleVisionServiceInterface serviceInterface;

    ////////////////
    //Constructors//
    ////////////////

    /**
     * Overloaded to allow for simpler calls
     *
     * @param apiKey
     */
    public GoogleVisionUtilities(@NonNull final String apiKey) {
        this.apiKey = "=" + apiKey;
        init(null, null);
    }

    /**
     * Overloaded to allow for simpler calls
     *
     * @param apiKey
     * @param timeoutInMilliseconds
     */
    public GoogleVisionUtilities(@NonNull final String apiKey,
                                 @NonNull Long timeoutInMilliseconds) {
        this.apiKey = "=" + apiKey;
        init(timeoutInMilliseconds, null);
    }

    /**
     * Constructor for the calls
     *
     * @param apiKey                Google Vision API Key
     * @param timeoutInMilliseconds Timeout in milliseconds for the call. As this is dealing with
     *                              images, it can be a lengthy call so I would recommend more than
     *                              the usual 1 minute I use. This wil default to 2 minutes if
     *                              left alone
     * @param level                 {@link HttpLoggingInterceptor.Level} This log level will print in the logcat
     *                              what is happening. If null is passed, will default from the values in
     *                              {@link PGMacTipsConfig#isLiveBuild} where a live build means logging
     *                              and non-live means no logging.
     */
    public GoogleVisionUtilities(@NonNull final String apiKey,
                                 @NonNull Long timeoutInMilliseconds,
                                 @NonNull HttpLoggingInterceptor.Level level) {
        this.apiKey = "=" + apiKey;
        init(timeoutInMilliseconds, level);
    }

    ////////////////
    //Init Methods//
    ////////////////

    private void init(@Nullable Long millisecondsOnTimeout,
                      @Nullable HttpLoggingInterceptor.Level logLevel) {

        if (NumberUtilities.getLong(millisecondsOnTimeout) <= 0) {
            millisecondsOnTimeout = (long) (2 * PGMacTipsConstants.ONE_MINUTE);
        }
        RetrofitClient.Builder builder = new RetrofitClient.Builder(
                GoogleVisionServiceInterface.class, BASE_URL_FOR_GOOGLE_VISION);
        builder.setTimeouts(millisecondsOnTimeout, millisecondsOnTimeout);
        try {
            if (logLevel != null) {
                builder.setLogLevel(logLevel);
            } else {
                builder.setLogLevel((PGMacTipsConfig.getInstance().getIsLiveBuild()) ?
                        HttpLoggingInterceptor.Level.NONE : HttpLoggingInterceptor.Level.BODY);
            }
        } catch (Exception e) {
            builder.setLogLevel(HttpLoggingInterceptor.Level.NONE);
        }
        this.serviceInterface = builder.build().buildServiceClient();
    }

    /////////////
    //Web Calls//
    /////////////

    /**
     * Call to the Google Vision Detect Text endpoint
     *
     * @param listener listener to send data back on
     * @param bitmap   Image (bitmap) to be converted into a base64 and sent. Will run on async thread
     */
    public void detectText(@NonNull final OnTaskCompleteListener listener,
                           @NonNull final Bitmap bitmap) {
        if (listener == null) {
            return;
        }
        if (bitmap == null) {
            listener.onTaskComplete(null, TAG_INVALID_BITMAP_IMAGE);
            return;
        }
        ImageUtilities.encodeImage(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                switch (customTag) {
                    case PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS:
                        String base64String = (String) result;
                        GoogleVisionUtilities.this.detectText(listener, base64String);
                        break;

                    default:
                    case PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL:
                        listener.onTaskComplete(null, TAG_BITMAP_BASE64_CONVERSION_FAIL);
                        break;
                }
            }
        }, bitmap);
    }

    /**
     * Call to the Google Vision Detect Text endpoint
     *
     * @param listener    listener to send data back on
     * @param base64Image Base64 encoded image string
     */
    public void detectText(@NonNull final OnTaskCompleteListener listener,
                           @NonNull final String base64Image) {
        if (listener == null) {
            return;
        }
        if (StringUtilities.isNullOrEmpty(base64Image)) {
            listener.onTaskComplete(null, TAG_INVALID_BASE_64_IMAGE);
            return;
        }
        if (this.serviceInterface == null) {
            listener.onTaskComplete(null, TAG_GOOGLE_VISION_UNKNOWN_ERROR);
            return;
        }

        GoogleVisionRequestModel model = new GoogleVisionRequestModel();
        List<GoogleVisionRequestModel.VisionRequests> body = buildRequest(base64Image, null,
                GoogleVisionRequestModel.VisionFeatures.DetectionTypes.TEXT_DETECTION);
        model.setRequests(body);
        Call<ResponseBody> call = serviceInterface.visionCall(this.apiKey, model);
        RetrofitParser.parse(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                if (customTag == 1) {
                    try {
                        GoogleVisionResponseModel successModel = (GoogleVisionResponseModel) result;
                        listener.onTaskComplete(successModel, TAG_GOOGLE_VISION_SUCCESS_RESULT);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        GoogleVisionErrorModel errorModel = (GoogleVisionErrorModel) result;
                        listener.onTaskComplete(errorModel, TAG_GOOGLE_VISION_FAIL_RESULT);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listener.onTaskComplete(null, TAG_GOOGLE_VISION_UNKNOWN_ERROR);
            }
        }, call, GoogleVisionResponseModel.class, GoogleVisionErrorModel.class, 1, 0, false);
    }


    /**
     * Call to the Google Vision Detect Document Text endpoint
     *
     * @param listener listener to send data back on
     * @param bitmap   Image (bitmap) to be converted into a base64 and sent. Will run on async thread
     */
    public void detectDocumentText(@NonNull final OnTaskCompleteListener listener,
                                   @NonNull final Bitmap bitmap) {
        if (listener == null) {
            return;
        }
        if (bitmap == null) {
            listener.onTaskComplete(null, TAG_INVALID_BITMAP_IMAGE);
            return;
        }
        ImageUtilities.encodeImage(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                switch (customTag) {
                    case PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS:
                        String base64String = (String) result;
                        GoogleVisionUtilities.this.detectDocumentText(listener, base64String);
                        break;

                    default:
                    case PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL:
                        listener.onTaskComplete(null, TAG_BITMAP_BASE64_CONVERSION_FAIL);
                        break;
                }
            }
        }, bitmap);
    }

    /**
     * Call to the Google Vision Detect Document Text endpoint
     *
     * @param listener    listener to send data back on
     * @param base64Image Base64 encoded image string
     */
    public void detectDocumentText(@NonNull final OnTaskCompleteListener listener,
                                   @NonNull final String base64Image) {
        if (listener == null) {
            return;
        }
        if (StringUtilities.isNullOrEmpty(base64Image)) {
            listener.onTaskComplete(null, TAG_INVALID_BASE_64_IMAGE);
            return;
        }
        if (this.serviceInterface == null) {
            listener.onTaskComplete(null, TAG_GOOGLE_VISION_UNKNOWN_ERROR);
            return;
        }

        GoogleVisionRequestModel model = new GoogleVisionRequestModel();
        List<GoogleVisionRequestModel.VisionRequests> body = buildRequest(base64Image, null,
                GoogleVisionRequestModel.VisionFeatures.DetectionTypes.DOCUMENT_TEXT_DETECTION);
        model.setRequests(body);
        Call<ResponseBody> call = serviceInterface.visionCall(this.apiKey, model);
        RetrofitParser.parse(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                if (customTag == 1) {
                    try {
                        GoogleVisionResponseModel successModel = (GoogleVisionResponseModel) result;
                        listener.onTaskComplete(successModel, TAG_GOOGLE_VISION_SUCCESS_RESULT);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        GoogleVisionErrorModel errorModel = (GoogleVisionErrorModel) result;
                        listener.onTaskComplete(errorModel, TAG_GOOGLE_VISION_FAIL_RESULT);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listener.onTaskComplete(null, TAG_GOOGLE_VISION_UNKNOWN_ERROR);
            }
        }, call, GoogleVisionResponseModel.class, GoogleVisionErrorModel.class, 1, 0, false);
    }


    /**
     * Call to the Google Vision Crop Hints endpoint
     *
     * @param listener listener to send data back on
     * @param bitmap   Image (bitmap) to be converted into a base64 and sent. Will run on async thread
     */
    public void getCropHints(@NonNull final OnTaskCompleteListener listener,
                             @NonNull final Bitmap bitmap) {
        if (listener == null) {
            return;
        }
        if (bitmap == null) {
            listener.onTaskComplete(null, TAG_INVALID_BITMAP_IMAGE);
            return;
        }
        ImageUtilities.encodeImage(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                switch (customTag) {
                    case PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS:
                        String base64String = (String) result;
                        GoogleVisionUtilities.this.getCropHints(listener, base64String);
                        break;

                    default:
                    case PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL:
                        listener.onTaskComplete(null, TAG_BITMAP_BASE64_CONVERSION_FAIL);
                        break;
                }
            }
        }, bitmap);
    }

    /**
     * Call to the Google Vision Crop Hints endpoint
     *
     * @param listener    listener to send data back on
     * @param base64Image Base64 encoded image string
     */
    public void getCropHints(@NonNull final OnTaskCompleteListener listener,
                             @NonNull final String base64Image) {
        if (listener == null) {
            return;
        }
        if (StringUtilities.isNullOrEmpty(base64Image)) {
            listener.onTaskComplete(null, TAG_INVALID_BASE_64_IMAGE);
            return;
        }
        if (this.serviceInterface == null) {
            listener.onTaskComplete(null, TAG_GOOGLE_VISION_UNKNOWN_ERROR);
            return;
        }

        GoogleVisionRequestModel model = new GoogleVisionRequestModel();
        List<GoogleVisionRequestModel.VisionRequests> body = buildRequest(base64Image, null,
                GoogleVisionRequestModel.VisionFeatures.DetectionTypes.CROP_HINTS);
        model.setRequests(body);
        Call<ResponseBody> call = serviceInterface.visionCall(this.apiKey, model);
        RetrofitParser.parse(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                if (customTag == 1) {
                    try {
                        GoogleVisionResponseModel successModel = (GoogleVisionResponseModel) result;
                        listener.onTaskComplete(successModel, TAG_GOOGLE_VISION_SUCCESS_RESULT);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        GoogleVisionErrorModel errorModel = (GoogleVisionErrorModel) result;
                        listener.onTaskComplete(errorModel, TAG_GOOGLE_VISION_FAIL_RESULT);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listener.onTaskComplete(null, TAG_GOOGLE_VISION_UNKNOWN_ERROR);
            }
        }, call, GoogleVisionResponseModel.class, GoogleVisionErrorModel.class, 1, 0, false);
    }


    /**
     * Call to the Google Vision Web Detection endpoint
     *
     * @param listener      listener to send data back on
     * @param bitmap        Image (bitmap) to be converted into a base64 and sent. Will run on async thread
     * @param maxNumResults Max number of results; defaults to 1
     */
    public void checkWebDetection(@NonNull final OnTaskCompleteListener listener,
                                  @NonNull final Bitmap bitmap,
                                  @Nullable final Integer maxNumResults) {
        if (listener == null) {
            return;
        }
        if (bitmap == null) {
            listener.onTaskComplete(null, TAG_INVALID_BITMAP_IMAGE);
            return;
        }
        ImageUtilities.encodeImage(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                switch (customTag) {
                    case PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS:
                        String base64String = (String) result;
                        GoogleVisionUtilities.this.checkWebDetection(listener, base64String, maxNumResults);
                        break;

                    default:
                    case PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL:
                        listener.onTaskComplete(null, TAG_BITMAP_BASE64_CONVERSION_FAIL);
                        break;
                }
            }
        }, bitmap);
    }

    /**
     * Call to the Google Vision Web Detection endpoint
     *
     * @param listener      listener to send data back on
     * @param base64Image   Base64 encoded image string
     * @param maxNumResults Max number of results; defaults to 1
     */
    public void checkWebDetection(@NonNull final OnTaskCompleteListener listener,
                                  @NonNull final String base64Image,
                                  @Nullable Integer maxNumResults) {
        if (NumberUtilities.getInt(maxNumResults) <= 0) {
            maxNumResults = 1;
        }
        if (listener == null) {
            return;
        }
        if (StringUtilities.isNullOrEmpty(base64Image)) {
            listener.onTaskComplete(null, TAG_INVALID_BASE_64_IMAGE);
            return;
        }
        if (this.serviceInterface == null) {
            listener.onTaskComplete(null, TAG_GOOGLE_VISION_UNKNOWN_ERROR);
            return;
        }

        GoogleVisionRequestModel model = new GoogleVisionRequestModel();
        List<GoogleVisionRequestModel.VisionRequests> body = buildRequest(base64Image, maxNumResults,
                GoogleVisionRequestModel.VisionFeatures.DetectionTypes.WEB_DETECTION);
        model.setRequests(body);
        Call<ResponseBody> call = serviceInterface.visionCall(this.apiKey, model);
        RetrofitParser.parse(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                if (customTag == 1) {
                    try {
                        GoogleVisionResponseModel successModel = (GoogleVisionResponseModel) result;
                        listener.onTaskComplete(successModel, TAG_GOOGLE_VISION_SUCCESS_RESULT);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        GoogleVisionErrorModel errorModel = (GoogleVisionErrorModel) result;
                        listener.onTaskComplete(errorModel, TAG_GOOGLE_VISION_FAIL_RESULT);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listener.onTaskComplete(null, TAG_GOOGLE_VISION_UNKNOWN_ERROR);
            }
        }, call, GoogleVisionResponseModel.class, GoogleVisionErrorModel.class, 1, 0, false);
    }


    /**
     * Call to the Google Vision Web Detection endpoint
     *
     * @param listener      listener to send data back on
     * @param bitmap        Image (bitmap) to be converted into a base64 and sent. Will run on async thread
     * @param maxNumResults Max number of results; defaults to 1
     */
    public void detectFaces(@NonNull final OnTaskCompleteListener listener,
                            @NonNull final Bitmap bitmap,
                            @Nullable final Integer maxNumResults) {
        if (listener == null) {
            return;
        }
        if (bitmap == null) {
            listener.onTaskComplete(null, TAG_INVALID_BITMAP_IMAGE);
            return;
        }
        ImageUtilities.encodeImage(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                switch (customTag) {
                    case PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS:
                        String base64String = (String) result;
                        GoogleVisionUtilities.this.detectFaces(listener, base64String, maxNumResults);
                        break;

                    default:
                    case PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL:
                        listener.onTaskComplete(null, TAG_BITMAP_BASE64_CONVERSION_FAIL);
                        break;
                }
            }
        }, bitmap);
    }

    /**
     * Call to the Google Vision Web Detection endpoint
     *
     * @param listener      listener to send data back on
     * @param base64Image   Base64 encoded image string
     * @param maxNumResults Max number of results; defaults to 1
     */
    public void detectFaces(@NonNull final OnTaskCompleteListener listener,
                            @NonNull final String base64Image,
                            @Nullable Integer maxNumResults) {
        if (NumberUtilities.getInt(maxNumResults) <= 0) {
            maxNumResults = 1;
        }
        if (listener == null) {
            return;
        }
        if (StringUtilities.isNullOrEmpty(base64Image)) {
            listener.onTaskComplete(null, TAG_INVALID_BASE_64_IMAGE);
            return;
        }
        if (this.serviceInterface == null) {
            listener.onTaskComplete(null, TAG_GOOGLE_VISION_UNKNOWN_ERROR);
            return;
        }

        GoogleVisionRequestModel model = new GoogleVisionRequestModel();
        List<GoogleVisionRequestModel.VisionRequests> body = buildRequest(base64Image, maxNumResults,
                GoogleVisionRequestModel.VisionFeatures.DetectionTypes.FACE_DETECTION);
        model.setRequests(body);
        Call<ResponseBody> call = serviceInterface.visionCall(this.apiKey, model);
        RetrofitParser.parse(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                if (customTag == 1) {
                    try {
                        GoogleVisionResponseModel successModel = (GoogleVisionResponseModel) result;
                        listener.onTaskComplete(successModel, TAG_GOOGLE_VISION_SUCCESS_RESULT);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        GoogleVisionErrorModel errorModel = (GoogleVisionErrorModel) result;
                        listener.onTaskComplete(errorModel, TAG_GOOGLE_VISION_FAIL_RESULT);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listener.onTaskComplete(null, TAG_GOOGLE_VISION_UNKNOWN_ERROR);
            }
        }, call, GoogleVisionResponseModel.class, GoogleVisionErrorModel.class, 1, 0, false);
    }


    //////////////////
    //Misc Utilities//
    //////////////////

    /**
     * Simple builder to prevent duplication of code
     */
    private static List<GoogleVisionRequestModel.VisionRequests> buildRequest(String base64Image,
                                                                              Integer maxNumResults,
                                                                              GoogleVisionRequestModel.VisionFeatures.DetectionTypes type) {
        GoogleVisionRequestModel.VisionImage visionImage = new GoogleVisionRequestModel.VisionImage();
        GoogleVisionRequestModel.VisionFeatures visionFeatures = new GoogleVisionRequestModel.VisionFeatures();
        GoogleVisionRequestModel.VisionRequests req = new GoogleVisionRequestModel.VisionRequests();
        visionImage.setBase64ImageString(base64Image);
        if (NumberUtilities.getInt(maxNumResults) > 0) {
            visionFeatures.setMaxResults(maxNumResults);
        }
        visionFeatures.setType(type);
        req.setFeatures(visionFeatures);
        req.setImage(visionImage);
        List<GoogleVisionRequestModel.VisionRequests> requests = new ArrayList<>();
        requests.add(req);
        return requests;
    }

}
