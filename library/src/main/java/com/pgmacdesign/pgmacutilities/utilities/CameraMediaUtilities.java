package com.pgmacdesign.pgmacutilities.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.enhancedphotoclasses.TakePhotoActivity;
import com.pgmacdesign.pgmacutilities.enhancedphotoclasses.TakePhotoActivityAPI21;
import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * Created by pmacdowell on 8/16/2016.
 */
public class CameraMediaUtilities {

    /*
    //This code goes in every activity calling this class for photo stuff

     1) Up top as a global variable
     private CameraMediaUtilities cameraUtilities;

     2) In the init method somewhere. Adjust the true params as needed
        cameraUtilities = new CameraMediaUtilities(NAME_OF_ACTIVITY.this,
            NAME_OF_ACTIVITY.this, true, true, NAME_OF_ACTIVITY.this);

        Or, like this if using a photo from the web

        cameraUtilities = new CameraMediaUtilities(NAME_OF_ACTIVITY.this,
            NAME_OF_ACTIVITY.this, true, STRING_WEB_IMAGE_URL_GOES_HERE, NAME_OF_ACTIVITY.this);

     3) It's own method. If already in class, make sure that the if check is near the top
     @Override
     protected void onActivityResult(int requestcode, int resultcode, Intent data) {

        if(CameraMediaUtilities.doesCodeBelongToUtility(requestcode)){
            cameraUtilities.afterOnActivityResult(requestcode, resultcode, data);
            return;
        }
     }

     4) Once they click the button to open the photo / video / gallery intent, put this code there.
        Need to make sure to change the last one (The enum variable) to decide which is to be done
            cameraUtilities.startPhotoProcess(
                CameraMediaUtilities.SourceType.CAMERA);

     5) Lastly, need to implement the onTaskComplete listener
            @Override
            public void onTaskComplete(Object result) {
                //Do stuff here
            }

            @Override
            public void onTaskComplete(Object result, int customTag) {
                if(result != null) {
                    switch (customTag) {
                        //Crop was success, no upload, PhotoObject sent back
                        case CameraMediaUtilities.TAG_CROP_SUCCESS:
                            CameraMediaUtilities.PhotoObject photoObject =
                                    (CameraMediaUtilities.PhotoObject) result;
                            break;

                        //Cropping error, caused by something like file not found error, String sent back
                        case CameraMediaUtilities.TAG_CROP_ERROR:
                            String cropErrorString = (String) result;
                            break;

                        //They canceled the select / take photo by exiting out before finishing
                        case CameraMediaUtilities.TAG_PHOTO_CANCEL:
                            String cancelString = (String) result;
                            break;

                        //The file did not upload properly, string error sent back
                        case CameraMediaUtilities.TAG_UPLOAD_ERROR:
                            String uploadErrorString = (String) result;
                            break;

                        //The File uploaded successfully, String Image URL sent back
                        case CameraMediaUtilities.TAG_UPLOAD_SUCCESS:
                            String imageUrl = (String) result;
                            break;

                        //Some unknown error. Display a toast to the user of problems
                        case CameraMediaUtilities.TAG_PHOTO_UNKNOWN_ERROR:
                            String unknownErrorString = (String) result;
                            break;

                        //Some bad url error. Display a toast to the user of problems
                        case CameraMediaUtilities.TAG_PHOTO_BAD_URL:
                            String badUrlString = (String) result;
                            break;
                    }
                }
            }

     */
    //Vars
    private static final int DURATION_LIMIT_FOR_VIDEOS = 30; //Alter this for longer videos
    private static final String ERROR_STRING = "An error occurred while processing your photo. ";

    //Tags used for OnActivityResult
    public static final int TAG_TAKE_PICTURE_WITH_CAMERA = PGMacUtilitiesConstants.TAG_TAKE_PICTURE_WITH_CAMERA;
    public static final int TAG_PHOTO_FROM_GALLERY = PGMacUtilitiesConstants.TAG_PHOTO_FROM_GALLERY;
    public static final int TAG_CROP_PHOTO = PGMacUtilitiesConstants.TAG_CROP_PHOTO;
    public static final int TAG_TAKE_VIDEO_WITH_RECORDER = PGMacUtilitiesConstants.TAG_TAKE_VIDEO_WITH_RECORDER;
    public static final int TAG_RETURN_IMAGE_URL = PGMacUtilitiesConstants.TAG_RETURN_IMAGE_URL; //Should be URI
    public static final int TAG_MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = PGMacUtilitiesConstants.TAG_MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
    public static final int TAG_MY_PERMISSIONS_REQUEST_CAMERA = PGMacUtilitiesConstants.TAG_MY_PERMISSIONS_REQUEST_CAMERA;
    public static final int TAG_PHOTO_UNKNOWN_ERROR = PGMacUtilitiesConstants.TAG_PHOTO_UNKNOWN_ERROR;
    public static final int TAG_PHOTO_BAD_URL = PGMacUtilitiesConstants.TAG_PHOTO_BAD_URL;
    public static final int TAG_FILE_DOWNLOADED = PGMacUtilitiesConstants.TAG_FILE_DOWNLOADED;
    public static final int TAG_CROP_ERROR = PGMacUtilitiesConstants.TAG_CROP_ERROR;
    public static final int TAG_CROP_SUCCESS = PGMacUtilitiesConstants.TAG_CROP_SUCCESS;
    public static final int TAG_PHOTO_CANCEL = PGMacUtilitiesConstants.TAG_PHOTO_CANCEL;
    public static final int TAG_UPLOAD_ERROR = PGMacUtilitiesConstants.TAG_UPLOAD_ERROR;
    public static final int TAG_UPLOAD_SUCCESS = PGMacUtilitiesConstants.TAG_UPLOAD_SUCCESS;
    public static final int TAG_TAKE_SELF_PHOTO = PGMacUtilitiesConstants.TAG_TAKE_SELF_PHOTO;
    public static final int TAG_TAKE_SELF_PHOTO_SUCCESS = PGMacUtilitiesConstants.TAG_TAKE_SELF_PHOTO_SUCCESS;
    public static final int TAG_TAKE_SELF_PHOTO_FAILURE = PGMacUtilitiesConstants.TAG_TAKE_SELF_PHOTO_FAILURE;


    //Tags used for passing to other activities via intents
    public static final String TAG_FILE_PATH = "tag_file_path";
    public static final String TAG_FILE_NAME = "tag_file_name";
    public static final String TAG_FILE_EXTENSION = "tag_file_extension";
    public static final String TAG_USE_FLASH = "tag_use_flash";
    public static final String TAG_USE_FRONT_FACING_CAMERA = "tag_use_front_facing_camera";
    public static final String TAG_SELF_PHOTO_URI = PGMacUtilitiesConstants.TAG_SELF_PHOTO_URI;

    //UCrop Variables
    private UCrop.Options options;

    //Variables passed in via constructor
    private Activity activity;
    private Context context;
    private OnTaskCompleteListener listener;

    //Variables set by Flags and Options
    private boolean shouldUploadPhoto, shouldDeletePhoto, useFrontFacingCamera;
    private CameraUtilityOptionsAndFlags optionsAndFlags;
    private String userSentNameOfFile, userSentPathToFile, photoExtension;
    private Integer maxDurationForVideo;

    //Misc Variables
    private Dialog alertDialog;
    private String webImageUrl;
    private SupportedPhotoFileExtensions photoFileExtension;
    private SupportedVideoFileExtensions videoFileExtension;

    //Uri / File to hold while photo is being taken
    private Uri takePhotoUri, takeVideoUri, downloadedPhotoUri;
    private File fileToPassAround, tempFile;

    /**
     * Class for handling Photo Objects
     */
    public class PhotoObject{
        public URI javaUri;
        public Uri androidUri;
        public String stringPath;
        public File photoFile;
    }

    /**
     * Source Type enum to be used for photo setting / getting
     */
    public enum SourceType {
        CAMERA, VIDEO, GALLERY, WEB_URL, CAMERA_SELF_PHOTO
    }

    /**
     * Supported Video File extensions. Link for list below:
     * https://developer.android.com/guide/appendix/media-formats.html
     */
    public enum SupportedVideoFileExtensions {
        GPP3(".3gp"),
        MP4(".mp4");

        public String videoExtensionName;

        SupportedVideoFileExtensions(String videoExtensionName){
            this.videoExtensionName = videoExtensionName;
        }

        /**
         * Checks if the type passed in is a supported type.
         * @param str Extension to check. IE, ".jpg", ".gif"
         * @return Boolean. True if it is supported, false if not
         */
        public static boolean isSupportedType(String str){
            if(StringUtilities.isNullOrEmpty(str)){
                return false;
            }
            if(str.equals(GPP3.videoExtensionName) ||
                    str.equals(MP4.videoExtensionName)){
                return true;
            } else {
                return false;
            }
        }
    }
    /**
     * Supported Photo File extensions. Link for list below:
     * https://developer.android.com/guide/appendix/media-formats.html
     */
    public enum SupportedPhotoFileExtensions {
        JPEG(".jpg"),
        GIF(".gif"),
        PNG(".png"),
        BMP(".bmp"),
        WEBP(".webp");

        public String photoExtensionName;

        SupportedPhotoFileExtensions(String photoExtensionName){
            this.photoExtensionName = photoExtensionName;
        }

        /**
         * Checks if the type passed in is a supported type.
         * @param str Extension to check. IE, ".jpg", ".gif"
         * @return Boolean. True if it is supported, false if not
         */
        public static boolean isSupportedType(String str){
            if(StringUtilities.isNullOrEmpty(str)){
                return false;
            }
            if(str.equals(JPEG.photoExtensionName) ||
                    str.equals(GIF.photoExtensionName) ||
                    str.equals(PNG.photoExtensionName) ||
                    str.equals(BMP.photoExtensionName) ||
                    str.equals(WEBP.photoExtensionName)){
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Flags and options class for specific things to happen within the Camera Utilities calls
     */
    public static class CameraUtilityOptionsAndFlags {
        private Integer maxVideoRecordingTime;
        private String nameOfFile;
        private String pathToFile;
        private boolean shouldCropPhoto;
        private boolean shouldUploadPhoto;
        private boolean shouldDeletePhotoAfter;
        private boolean useDefaultToFrontFacingCamera;
        private Dialog alertDialog;
        private String webImageUrlToDownload;
        private SupportedVideoFileExtensions videoExtension;
        private SupportedPhotoFileExtensions photoExtension;

        public CameraUtilityOptionsAndFlags(Context context){
            this.maxVideoRecordingTime = null;
            this.nameOfFile = null;
            this.pathToFile = null;
            this.shouldCropPhoto = true;
            this.shouldUploadPhoto = false;
            this.shouldDeletePhotoAfter = false;
            this.useDefaultToFrontFacingCamera = false;
            //Removed on 2017-07-05 Due to problems with compiling
            //this.alertDialog = PGMacCustomProgressBar.buildSVGDialog(context);
            this.alertDialog = new AlertDialog.Builder(context).create();
            this.webImageUrlToDownload = null;
            this.videoExtension = SupportedVideoFileExtensions.MP4;
            this.photoExtension = SupportedPhotoFileExtensions.PNG;
        }

        /**
         * Constructor
         * @param maxVideoRecordingTime If recording a video, max recording time. (IN SECONDS! NOT
         *                              IN MILLISECONDS!)
         * @param nameOfFile The name of the file
         * @param pathToFile The path to the file
         * @param shouldUploadPhoto Boolean, should the photo be uploaded or not
         * @param shouldDeletePhotoAfter Boolean, should the photo be deleted or not afterwards
         * @param useDefaultToFrontFacingCamera Boolean, should the camera default to the front
         *                                      facing camera or not
         * @param alertDialog Alert dialog. If none selected, default one (PGMacCustom SVG)
         *                    will be selected
         * @param webImageUrlToDownload The web imageURL String if included
         * @param videoExtension The video extension format {@link SupportedVideoFileExtensions}
         * @param photoExtension The photo extension format {@link SupportedPhotoFileExtensions}
         */
        public CameraUtilityOptionsAndFlags(Integer maxVideoRecordingTime, String nameOfFile,
                                            String pathToFile, boolean shouldCropPhoto,
                                            boolean shouldUploadPhoto,
                                            boolean shouldDeletePhotoAfter,
                                            boolean useDefaultToFrontFacingCamera,
                                            AlertDialog alertDialog, String webImageUrlToDownload,
                                            SupportedVideoFileExtensions videoExtension,
                                            SupportedPhotoFileExtensions photoExtension) {
            this.videoExtension = videoExtension;
            this.photoExtension = photoExtension;
            this.maxVideoRecordingTime = maxVideoRecordingTime;
            this.nameOfFile = nameOfFile;
            this.pathToFile = pathToFile;
            this.shouldUploadPhoto = shouldUploadPhoto;
            this.shouldCropPhoto = shouldCropPhoto;
            this.shouldDeletePhotoAfter = shouldDeletePhotoAfter;
            this.useDefaultToFrontFacingCamera = useDefaultToFrontFacingCamera;
            this.alertDialog = alertDialog;
            this.webImageUrlToDownload = webImageUrlToDownload;
        }

        public boolean isShouldCropPhoto() {
            return shouldCropPhoto;
        }

        public void setShouldCropPhoto(boolean shouldCropPhoto) {
            this.shouldCropPhoto = shouldCropPhoto;
        }

        public SupportedVideoFileExtensions getVideoExtension() {
            return videoExtension;
        }

        public void setVideoExtension(SupportedVideoFileExtensions videoExtension) {
            this.videoExtension = videoExtension;
        }

        public SupportedPhotoFileExtensions getPhotoExtension() {
            return photoExtension;
        }

        public void setPhotoExtension(SupportedPhotoFileExtensions photoExtension) {
            this.photoExtension = photoExtension;
        }

        public String getWebImageUrlToDownload() {
            return webImageUrlToDownload;
        }

        public void setWebImageUrlToDownload(String webImageUrlToDownload) {
            this.webImageUrlToDownload = webImageUrlToDownload;
        }

        public Integer getMaxVideoRecordingTime() {
            return maxVideoRecordingTime;
        }

        public void setMaxVideoRecordingTime(Integer maxVideoRecordingTime) {
            this.maxVideoRecordingTime = maxVideoRecordingTime;
        }

        public String getNameOfFile() {
            return nameOfFile;
        }

        public void setNameOfFile(String nameOfFile) {
            this.nameOfFile = nameOfFile;
        }

        public String getPathToFile() {
            return pathToFile;
        }

        public void setPathToFile(String pathToFile) {
            this.pathToFile = pathToFile;
        }

        public boolean getShouldUploadPhoto() {
            return shouldUploadPhoto;
        }

        public void setShouldUploadPhoto(boolean shouldUploadPhoto) {
            this.shouldUploadPhoto = shouldUploadPhoto;
        }

        public boolean getShouldDeletePhotoAfter() {
            return shouldDeletePhotoAfter;
        }

        public void setShouldDeletePhotoAfter(boolean shouldDeletePhotoAfter) {
            this.shouldDeletePhotoAfter = shouldDeletePhotoAfter;
        }

        public boolean isUseDefaultToFrontFacingCamera() {
            return useDefaultToFrontFacingCamera;
        }

        public void setUseDefaultToFrontFacingCamera(boolean useDefaultToFrontFacingCamera) {
            this.useDefaultToFrontFacingCamera = useDefaultToFrontFacingCamera;
        }

        public Dialog getAlertDialog() {
            return alertDialog;
        }

        public void setAlertDialog(Dialog alertDialog) {
            this.alertDialog = alertDialog;
        }
    }

    /**
     * Constructor For Camera Image Utilities Class. Note! This will set the OptionsAndFlags
     * variable to defaults.
     * @param context Context
     * @param activity Activity
     * @param listener listener to send data back on
     */
    public CameraMediaUtilities(Context context, Activity activity, OnTaskCompleteListener listener){
        if(context == null){
            return;
        }
        this.context = context;
        this.activity = activity;
        this.listener = listener;
        this.optionsAndFlags = new CameraUtilityOptionsAndFlags(context);
    }

    /**
     * Overloaded method to allow for options and flags to be passed in
     * @param context
     * @param activity
     * @param listener
     * @param optionsAndFlags {@link CameraUtilityOptionsAndFlags}
     */
    public CameraMediaUtilities(Context context, Activity activity, OnTaskCompleteListener listener,
                                CameraUtilityOptionsAndFlags optionsAndFlags){
        this.context = context;
        this.activity = activity;
        this.listener = listener;
        this.optionsAndFlags = optionsAndFlags;
    }
    //Setter for optionsAndFlags
    public void setCameraUtilityOptionsAndFlags(@NonNull CameraUtilityOptionsAndFlags optionsAndFlags){
        this.optionsAndFlags = optionsAndFlags;
    }

    /**
     * This checks against the request code in the activityResult to the ones in this class.
     * If it matches one here, it returns true, else, returns false
     * @param requestCode request code to compare against from the onActivityResult method
     * @return Boolean, true if it should be send back to this class' afterOnActivityResult method
     */
    public static boolean doesCodeBelongToUtility(int requestCode){
        switch (requestCode){
            case TAG_TAKE_PICTURE_WITH_CAMERA:
            case TAG_PHOTO_FROM_GALLERY:
            case TAG_CROP_PHOTO:
            case TAG_TAKE_VIDEO_WITH_RECORDER:
            case TAG_RETURN_IMAGE_URL:
            case TAG_MY_PERMISSIONS_REQUEST_CAMERA:
            case TAG_MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
            case TAG_TAKE_SELF_PHOTO:
            case TAG_TAKE_SELF_PHOTO_SUCCESS:
            case TAG_TAKE_SELF_PHOTO_FAILURE:
                return true;

            default:
                return false;
        }
    }
    /**
     * Shows or hides the alert dialog depending on the boolean passed
     * @param bool true for show, false for hide
     */
    public void showAlertOrNot(boolean bool){
        if(bool){
            try {
                alertDialog.show();
            } catch (Exception e){}
        } else {
            try {
                alertDialog.dismiss();
            } catch (Exception e){}
        }
    }

    /**
     * Starts the photo process depending on what is passed in
     * @param typeOfPicture Type of action to be taken depending on the SourceType
     */
    public void startPhotoProcess(SourceType typeOfPicture){
        //First request permissions
        boolean canUseCamera, canUserStorage;
        canUseCamera = getCameraPermissions();
        canUserStorage = getStoragePermissions();
        if(!canUseCamera || !canUserStorage){
            return;
        }

        //Set variables from options
        this.alertDialog = optionsAndFlags.getAlertDialog();
        this.shouldDeletePhoto = optionsAndFlags.getShouldDeletePhotoAfter();
        this.shouldUploadPhoto = optionsAndFlags.getShouldUploadPhoto();
        this.userSentNameOfFile = optionsAndFlags.getNameOfFile();
        this.userSentPathToFile = optionsAndFlags.getPathToFile();
        this.maxDurationForVideo = optionsAndFlags.getMaxVideoRecordingTime();
        this.photoFileExtension = optionsAndFlags.getPhotoExtension();
        this.videoFileExtension = optionsAndFlags.getVideoExtension();

        if(!SupportedPhotoFileExtensions.isSupportedType(photoFileExtension.photoExtensionName)){
            photoFileExtension = SupportedPhotoFileExtensions.PNG;
        }
        this.photoExtension = photoFileExtension.photoExtensionName;

        if(!SupportedVideoFileExtensions.isSupportedType(videoFileExtension.videoExtensionName)){
            videoFileExtension = SupportedVideoFileExtensions.MP4;
        }

        if(StringUtilities.isNullOrEmpty(userSentNameOfFile)){
            userSentNameOfFile = "PGMacUtilities";
        }
        userSentNameOfFile = StringUtilities.removeSpaces(userSentNameOfFile);

        if(StringUtilities.isNullOrEmpty(userSentPathToFile)){
            userSentPathToFile = Environment.getExternalStorageDirectory() + "/DCIM/";
        }
        userSentPathToFile = StringUtilities.removeSpaces(userSentPathToFile);

        if(this.maxDurationForVideo == null){
            this.maxDurationForVideo = 0;
        }
        if(this.maxDurationForVideo > 1000){
            //Doing this in case someone mistakenly sends in milliseconds when the system has it in seconds
            this.maxDurationForVideo /= 1000;
        }
        this.webImageUrl = optionsAndFlags.getWebImageUrlToDownload();

        //Clear from last run
        takePhotoUri = null;
        takeVideoUri = null;
        fileToPassAround = null;

        switch (typeOfPicture){

            case CAMERA:
                this.takePhotoWithCamera();
                break;

            case VIDEO:
                this.takeVideoWithCamera();
                break;

            case GALLERY:
                this.getPhotoFromGallery();
                break;

            case WEB_URL:
                this.cropPhotoFromWeb();
                break;

            case CAMERA_SELF_PHOTO:
                this.startSelfPhoto();
                break;
        }
    }

    /**
     * Requests Camera Permissions
     * @return boolean, true if granted, false if not
     */
    public boolean getCameraPermissions(){
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA},
                            TAG_MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e){}
        return false;
    }

    /**
     * Requests Write Storage Permissions
     * @return boolean, true if granted, false if not
     */
    public boolean getStoragePermissions(){
        try {
            if (Build.VERSION.SDK_INT >= 23){
                //Storage Permissions Next
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            TAG_MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e){}
        return false;
    }

    /**
     * From a web url, crop the image and upload or return it
     */
    private void cropPhotoFromWeb() {
        if (webImageUrl == null) {
            listener.onTaskComplete("URL was invalid", TAG_PHOTO_BAD_URL);
            return;
        }

        final ImageUtilities.DownloadImageFromWeb downloadImageFromWeb =
                new ImageUtilities.DownloadImageFromWeb(context, webImageUrl, null,
                        new OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(Object result, int customTag) {
                        showAlertOrNot(false);
                        if (customTag == TAG_PHOTO_BAD_URL) {
                            listener.onTaskComplete("URL was invalid", TAG_PHOTO_BAD_URL);
                        } else if (customTag == TAG_FILE_DOWNLOADED) {
                            try {
                                fileToPassAround = (File) result;
                                tempFile = fileToPassAround;
                                downloadedPhotoUri = Uri.fromFile(fileToPassAround);

                                if (fileToPassAround != null && downloadedPhotoUri != null) {
                                    CameraMediaUtilities.this.startCropping(
                                            downloadedPhotoUri, downloadedPhotoUri);
                                } else {
                                    listener.onTaskComplete("An Error Occured", TAG_PHOTO_UNKNOWN_ERROR);
                                }
                            } catch (Exception e) {
                                listener.onTaskComplete("An Error Occured", TAG_PHOTO_UNKNOWN_ERROR);
                            }
                        } else {
                            listener.onTaskComplete("An Error Occurred", TAG_PHOTO_UNKNOWN_ERROR);
                        }
                    }
                }, null);

        downloadImageFromWeb.execute();

        return;

    }

    /**
     * Start the activity for result to get a camera image
     */
    private void takePhotoWithCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = this.generateImageUri(context);
        if(uri != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            L.m("invalid file data passed. Check your file Path, name, and extension");
            listener.onTaskComplete("Error", TAG_PHOTO_UNKNOWN_ERROR);
            return;
        }
        if(useFrontFacingCamera){
            int frontFacing = this.doesUserHaveFrontFacingCamera(context);
            if(frontFacing != -1){
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", frontFacing);
            }
        }
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, TAG_TAKE_PICTURE_WITH_CAMERA);
        }else{
            L.m("Image uri is null. Permissions error maybe?");
            L.toast(context, ERROR_STRING);
        }
    }

    /**
     * Take a self photo (Again, I refuse to say selfie)
     */
    private void startSelfPhoto(){
        //Get their build version first
        Intent intent;
        if(SystemUtilities.userHasMarshmallowOrHigher()){
            intent = new Intent(activity, TakePhotoActivityAPI21.class);
        } else {
            intent = new Intent(activity, TakePhotoActivity.class);
        }

        intent.putExtra(CameraMediaUtilities.TAG_FILE_PATH, userSentPathToFile);
        intent.putExtra(CameraMediaUtilities.TAG_FILE_NAME, userSentNameOfFile);
        intent.putExtra(CameraMediaUtilities.TAG_FILE_EXTENSION, photoExtension);
        intent.putExtra(CameraMediaUtilities.TAG_USE_FLASH, true);

        activity.startActivityForResult(intent, TAG_TAKE_SELF_PHOTO);
    }

    /**
     * Take a video
     */
    private void takeVideoWithCamera(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(maxDurationForVideo > 0){
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, maxDurationForVideo);
        }
        Uri uri = generateVideoUri(context);
        if(uri == null){
            L.m("invalid file data passed. Check your file Path, name, and extension");
            listener.onTaskComplete("Error", TAG_PHOTO_UNKNOWN_ERROR);
        } else {
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(takeVideoIntent, TAG_TAKE_VIDEO_WITH_RECORDER);
        }
    }

    /**
     * Get a Photo from the gallery
     */
    private void getPhotoFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        activity.startActivityForResult(galleryIntent, TAG_PHOTO_FROM_GALLERY);
    }

    /**
     * To be called right after onActivityResult gets hit in the activity
     * @param requestcode
     * @param resultcode
     * @param data
     */
    public void afterOnActivityResult(int requestcode, int resultcode, Intent data){

        if (resultcode != activity.RESULT_CANCELED) {

            Uri resultUri;

            //Photo is from Camera
            if (requestcode == TAG_TAKE_PICTURE_WITH_CAMERA
                    && resultcode == activity.RESULT_OK) {
                if(data == null){
                    startCropping(takePhotoUri, takePhotoUri);
                } else {
                    listener.onTaskComplete(ERROR_STRING, TAG_PHOTO_UNKNOWN_ERROR);
                }
            }

            //Photo is from Gallery
            else if (requestcode == TAG_PHOTO_FROM_GALLERY
                    && resultcode == activity.RESULT_OK) {
                resultUri = data.getData();
                resultUri = this.fixImageUri(context, resultUri);
                try {
                    //This try section is to alleviate Permission issues on KitKat Devices
                    File tempFile = null;
                    if(fileToPassAround == null){
                        fileToPassAround = new File(
                                StringUtilities.convertAndroidUriToString(resultUri));
                    }
                    tempFile = File.createTempFile("pgmac_photo", ".jpg", context.getCacheDir());
                    FileUtilities.copyFile(fileToPassAround, tempFile);
                    resultUri = StringUtilities.convertJavaURIToAndroidUri(tempFile.toURI());
                    tempFile.deleteOnExit();
                } catch (Exception e){}
                startCropping(resultUri, resultUri);
            }

            //Video is from Recording (Video)
            else if (requestcode == TAG_TAKE_VIDEO_WITH_RECORDER) {
                listener.onTaskComplete(takeVideoUri, TAG_TAKE_VIDEO_WITH_RECORDER);
            }

            //Picture is from self photo (I refuse to call it a selfie)
            else if(requestcode == TAG_TAKE_SELF_PHOTO) {
                try {
                    String androidUri = data.getStringExtra(PGMacUtilitiesConstants.TAG_SELF_PHOTO_URI);
                    Uri uri = StringUtilities.convertStringToAndroidUri(androidUri);
                    if(uri != null){
                        if(optionsAndFlags.shouldCropPhoto){
                            startCropping(uri, uri);
                        } else {

                            String pathUri = androidUri;
                            URI javaUri = StringUtilities.convertStringToJavaUri(pathUri);
                            Uri androidUriUri = StringUtilities.convertStringToAndroidUri(pathUri);

                            PhotoObject photoObject = new PhotoObject();
                            photoObject.androidUri = androidUriUri;
                            photoObject.javaUri = javaUri;
                            photoObject.stringPath = pathUri;
                            photoObject.photoFile = fileToPassAround;
                            listener.onTaskComplete(photoObject, TAG_RETURN_IMAGE_URL);
                        }
                    } else {
                        listener.onTaskComplete("An unknown error occurred", TAG_PHOTO_CANCEL);
                    }
                } catch (Exception e){
                    listener.onTaskComplete("An unknown error occurred", TAG_PHOTO_CANCEL);
                }
            }

            //Photo from Crop Photo
            else if (requestcode == TAG_CROP_PHOTO) {

                //Check for result code before moving on
                if(resultcode == activity.RESULT_OK){
                    resultUri = UCrop.getOutput(data);
                    PhotoObject photoObject = new PhotoObject();
                    photoObject.androidUri = resultUri;
                    photoObject.stringPath = StringUtilities.convertAndroidUriToString(resultUri);
                    photoObject.javaUri = StringUtilities.convertStringToJavaUri(photoObject.stringPath);
                    photoObject.photoFile = fileToPassAround;

                    listener.onTaskComplete(photoObject, TAG_CROP_SUCCESS);

                } else if(resultcode == UCrop.RESULT_ERROR){
                    final Throwable cropError = UCrop.getError(data);
                    listener.onTaskComplete(ERROR_STRING + cropError.toString(), TAG_CROP_ERROR);
                }
            }

            //Camera Permission Request
            else if (requestcode == TAG_MY_PERMISSIONS_REQUEST_CAMERA
                    && resultcode == activity.RESULT_OK
                    && data != null) {
                // TODO: 8/17/2016 nothing here currently, will implement soon

            //Storage Permission Request
            } else if (requestcode == TAG_MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                    && resultcode == activity.RESULT_OK
                    && data != null) {
                // TODO: 8/17/2016 nothing here currently, will implement soon

            //In this scenario, sending the image URI back to the activity
            } else if (requestcode == TAG_RETURN_IMAGE_URL) {

                resultUri = data.getData();
                String pathUri = StringUtilities.convertAndroidUriToString(resultUri);
                URI javaUri = StringUtilities.convertStringToJavaUri(pathUri);

                PhotoObject photoObject = new PhotoObject();
                photoObject.androidUri = resultUri;
                photoObject.javaUri = javaUri;
                photoObject.stringPath = pathUri;
                photoObject.photoFile = fileToPassAround;
                listener.onTaskComplete(photoObject, TAG_RETURN_IMAGE_URL);

            //Else, something weird happened
            } else {
                listener.onTaskComplete("An unknown error occurred", TAG_PHOTO_CANCEL);
            }
        } else {
            listener.onTaskComplete("Photo Select was canceled", TAG_PHOTO_CANCEL);
        }
    }

    /**
     * Start the cropping activity via Yalantis UCrop
     * @param sourceUri Source URI to crop
     * @param destinationUri Destination URI
     */
    public void startCropping(Uri sourceUri, Uri destinationUri){
        //Build options
        options = CameraMediaUtilities.buildUCropOptions();

        try {
            UCrop cropping = UCrop.of(sourceUri, destinationUri);
            //cropping.withMaxResultSize(maxWidth, maxHeight);
            if(options != null){
                cropping.withOptions(options);
            }
            cropping.start(activity, TAG_CROP_PHOTO);
        } catch (Exception e){
            e.printStackTrace();
            listener.onTaskComplete("An error occurred while processing your photo", TAG_CROP_ERROR);
        }
    }

    /**
     * Build uCrop Options
     * @param frameColor int color for the frame. Pass -100 to default them to regular colors
     * @param statusBarColor int color for the status bar. Pass -100 to default them to regular colors
     * @param toolbarColor int color for the toolbar. Pass -100 to default them to regular colors
     * @return Return UCrop.Options object
     */
    private static UCrop.Options buildUCropOptions(int frameColor, int statusBarColor,
                                                             int toolbarColor){
        UCrop.Options options = new UCrop.Options();
        options.useSourceImageAspectRatio();
        //https://github.com/Yalantis/uCrop/issues/173
        //options.withAspectRatio(1, 1);
        if(frameColor != -100){
            options.setCropFrameColor(frameColor);
        }
        if(statusBarColor != -100){
            options.setCropFrameColor(statusBarColor);
        }
        if(toolbarColor != -100){
            options.setCropFrameColor(toolbarColor);
        }

        return options;
    }
    /**
     * Build uCrop Options
     * @param frameColor String color for the frame. Pass -100 or null to default them to
     *                   regular colors
     * @param statusBarColor String color for the status bar. Pass -100 or null to default
     *                       them to regular colors
     * @param toolbarColor String color for the toolbar. Pass -100 or null to default them to
     *                     regular colors
     * @return Return UCrop.Options object
     */
    private static UCrop.Options buildUCropOptions(String frameColor, String statusBarColor,
                                                             String toolbarColor){
        int frameColorInt, statusBarColorInt, toolbarColorInt;

        frameColorInt = ColorUtilities.parseMyColor(frameColor);
        statusBarColorInt = ColorUtilities.parseMyColor(statusBarColor);
        toolbarColorInt = ColorUtilities.parseMyColor(toolbarColor);

        return (CameraMediaUtilities.buildUCropOptions(frameColorInt, statusBarColorInt, toolbarColorInt));
    }
    //Overloaded method
    private static UCrop.Options buildUCropOptions(){
        return (CameraMediaUtilities.buildUCropOptions(null, null, null));
    }
    /**
     * Generate an ImageUri
     * @param mContext
     * @return
     */
    public Uri generateImageUri(Context mContext){
        fileToPassAround = FileUtilities.generateFileForImage(userSentPathToFile,
                userSentNameOfFile, photoFileExtension.photoExtensionName);
        takePhotoUri = FileUtilities.convertFileToUri(fileToPassAround);
        return takePhotoUri;
    }

    /**
     * Generates a URI to use for taking photos
     * @param mContext
     * @return
     */
    public Uri generateVideoUri(Context mContext){
        if(mContext == null){
            mContext = this.context;
        }
        if(mContext == null){
            return null;
        }
        Uri vidUri = null;
        String nameOfFile = userSentNameOfFile;
        String myPath = userSentPathToFile;

        File file = null;
        try {
            file = new File(myPath,
                    nameOfFile +
                    "_" +
                    DateUtilities.getCurrentDateLong() +
                    videoFileExtension.videoExtensionName);
        } catch (Exception e){}
        if(file == null){
            return null;
        }
        vidUri = Uri.fromFile(file);
        fileToPassAround = file;

        takeVideoUri = vidUri;
        return vidUri;
    }


    /**
     * Not used atm
     * @param photoObjects Photo objects to delete
     */
    private void uploadPhotosViaDefaultMethod(List<PhotoObject> photoObjects){
        //Not used for now as there is no mass upload option. If needed in the future, implement here
    }

    /**
     * Use this method to fix URIs that are not usable or are in a format not readable. An example
     * would be one that starts with content://.......... This tries to make a file and when it
     * succeeds, it means that the URI was correct. The main purpose of this method is to handle
     * how some phone makers handle this differently (IE Motorola vs HTC vs Samsung)
     * @param context Context
     * @param selectedImageUri The Uri to work with
     * @return
     */
    private Uri fixImageUri(Context context, android.net.Uri selectedImageUri){

        //Attempt 1
        try {
            String selectedImageUriString = selectedImageUri.toString();
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 2
        try {
            String toAppend = "file://";
            String selectedImageUriString = selectedImageUri.toString();
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 3
        try {
            String selectedImageUriString = StringUtilities.getPath(context, selectedImageUri);
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 4
        try {
            String toAppend = "file://";
            String selectedImageUriString = StringUtilities.getPath(context, selectedImageUri);
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 5
        try {
            String selectedImageUriString = StringUtilities.getAbsolutePath(context, selectedImageUri);
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 6
        try {
            String toAppend = "file://";
            String selectedImageUriString = StringUtilities.getAbsolutePath(context, selectedImageUri);
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 7
        try {
            String toAppend = "file:/";
            String selectedImageUriString = StringUtilities.getAbsolutePath(context, selectedImageUri);
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 8
        try {
            String toAppend = "file:/";
            String selectedImageUriString = selectedImageUri.toString();
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //If none have worked by this point, file will likely not work. Maybe permission issues
        return null;
    }

    /**
     * Checks if user has front facing camera.
     * @param context Context used to check
     * @return 0 For front facing, 1 for back facing, -1 for if it does not have front facing at all
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static int doesUserHaveFrontFacingCamera(Context context) {

        if(Build.VERSION.SDK_INT >= 21) {
            try {
                CameraManager cManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                for (int j = 0; j < cManager.getCameraIdList().length; j++) {
                    String[] cameraId = cManager.getCameraIdList();
                    CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraId[j]);
                    int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (cOrientation == CameraCharacteristics.LENS_FACING_FRONT)
                        return j;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        return -1; // No front-facing camera found
    }
}