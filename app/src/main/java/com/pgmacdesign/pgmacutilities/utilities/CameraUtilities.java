package com.pgmacdesign.pgmacutilities.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.pgmacdesign.pgmacutilities.R;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.List;

/**
 * Created by pmacdowell on 8/16/2016.
 */
public class CameraUtilities {

    /*
    //This code goes in every activity calling this class for photo stuff

     1) Up top as a global variable
     private CameraUtilities cameraUtilities;

     2) In the init method somewhere. Adjust the true params as needed
        cameraUtilities = new CameraUtilities(NAME_OF_ACTIVITY.this,
            NAME_OF_ACTIVITY.this, true, true, NAME_OF_ACTIVITY.this);

        Or, like this if using a photo from the web

        cameraUtilities = new CameraUtilities(NAME_OF_ACTIVITY.this,
            NAME_OF_ACTIVITY.this, true, STRING_WEB_IMAGE_URL_GOES_HERE, NAME_OF_ACTIVITY.this);

     2.5) If in Love lab and using a TrustLevelDialog, don't forget to call dialog.setCameraImageUtility(obj)

     3) It's own method. If already in class, make sure that the if check is near the top
     @Override
     protected void onActivityResult(int requestcode, int resultcode, Intent data) {

        if(CameraUtilities.doesCodeBelongToUtility(requestcode)){
            cameraUtilities.afterOnActivityResult(requestcode, resultcode, data);
            return;
        }
     }

     4) Once they click the button to open the photo / video / gallery intent, put this code there.
        Need to make sure to change the last one (The enum variable) to decide which is to be done
            cameraUtilities.startPhotoProcess(
                CameraUtilities.sourceType.CAMERA);

     5) Lastly, need to implement the onTaskComplete listener
            @Override
            public void onTaskCompleteV2(Object result) {
                //Do stuff here
            }

            @Override
            public void onTaskCompleteV2(Object result, int customTag) {
                if(result != null) {
                    switch (customTag) {
                        //Crop was success, no upload, PhotoObject sent back
                        case CameraUtilities.TAG_CROP_SUCCESS:
                            CameraUtilities.PhotoObject photoObject =
                                    (CameraUtilities.PhotoObject) result;
                            break;

                        //Cropping error, caused by something like file not found error, String sent back
                        case CameraUtilities.TAG_CROP_ERROR:
                            String cropErrorString = (String) result;
                            break;

                        //They canceled the select / take photo by exiting out before finishing
                        case CameraUtilities.TAG_PHOTO_CANCEL:
                            String cancelString = (String) result;
                            break;

                        //The file did not upload properly, string error sent back
                        case CameraUtilities.TAG_UPLOAD_ERROR:
                            String uploadErrorString = (String) result;
                            break;

                        //The File uploaded successfully, String Image URL sent back
                        case CameraUtilities.TAG_UPLOAD_SUCCESS:
                            String imageUrl = (String) result;
                            break;

                        //Some unknown error. Display a toast to the user of problems
                        case CameraUtilities.TAG_PHOTO_UNKNOWN_ERROR:
                            String unknownErrorString = (String) result;
                            break;

                        //Some bad url error. Display a toast to the user of problems
                        case CameraUtilities.TAG_PHOTO_BAD_URL:
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
    public static final int TAG_RETURN_IMAGE_URL = PGMacUtilitiesConstants.TAG_RETURN_IMAGE_URL;
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

    //UCrop Variables
    private int maxWidth, maxHeight;
    private UCrop.Options options; //Empty for now

    //String Constants
    public final String BASE_IMAGE_STRING = "LoveLab_"; //RENAME THIS WITH WHATEVER APP IS BEING RUN

    //Variables passed in via constructor
    private Activity activity;
    private Context context;
    private OnTaskCompleteListener listener;
    private boolean shouldUploadPhoto, shouldDeletePhoto, useFrontFacingCamera;

    //Misc Variables
    private ProgressDialog alertDialog;
    private String webImageUrl;

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
    public enum sourceType {
        CAMERA, VIDEO, GALLERY, WEB_URL
    }

    /**
     * Constructor For Camer Image Utilities Class
     * @param context Context
     * @param activity Activity
     * @param shouldUploadPhoto boolean, if true, photo will auto upload to default chosen
     *                          photo service (on a per app basis). If false, it will not upload.
     *                          If null is passed, defaults to false
     * @param listener listener to send data back on
     */
    public CameraUtilities(Context context, Activity activity, Boolean shouldUploadPhoto,
                                  Boolean useFrontFacingCamera, OnTaskCompleteListener listener){
        this.context = context;
        this.activity = activity;
        this.listener = listener;
        this.alertDialog = PatsCustomAlertDialog.buildSVGDialog(context);

        if(shouldUploadPhoto == null){
            this.shouldUploadPhoto = false;
        } else {
            this.shouldUploadPhoto = shouldUploadPhoto;
        }

        if(useFrontFacingCamera == null){
            this.useFrontFacingCamera = false;
        } else {
            this.useFrontFacingCamera = useFrontFacingCamera;
        }
    }

    /**
     * This overloaded constructor is used for skipping the camera and gallery and cropping /
     * uploading an image directly from a web url
     * @param context
     * @param activity
     * @param webImageUrl Web URL of the photo
     * @param listener
     */
    public CameraUtilities(Context context, Activity activity, Boolean shouldUploadPhoto,
                                  String webImageUrl, OnTaskCompleteListener listener){
        this.context = context;
        this.activity = activity;
        this.listener = listener;
        this.alertDialog = PatsCustomAlertDialog.buildSVGDialog(context);
        if(shouldUploadPhoto == null){
            this.shouldUploadPhoto = false;
        } else {
            this.shouldUploadPhoto = shouldUploadPhoto;
        }
        this.webImageUrl = webImageUrl;
    }

    /**
     * This checks against the request code in the activityResult to the ones in this class.
     * If it matches one here, it returns true, else, returns false
     * @param requestCode request code to compare against from the onActivityResult method
     * @return Boolean, true if it should be send back to this class' afterOnActivityResult method
     */
    public boolean doesCodeBelongToUtility(int requestCode){
        switch (requestCode){
            case TAG_TAKE_PICTURE_WITH_CAMERA:
            case TAG_PHOTO_FROM_GALLERY:
            case TAG_CROP_PHOTO:
            case TAG_TAKE_VIDEO_WITH_RECORDER:
            case TAG_RETURN_IMAGE_URL:
            case TAG_MY_PERMISSIONS_REQUEST_CAMERA:
            case TAG_MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
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
     * @param typeOfPicture Type of action to be taken depending on the sourceType
     */
    public void startPhotoProcess(sourceType typeOfPicture){
        //First request permissions
        boolean canUseCamera, canUserStorage;
        canUseCamera = getCameraPermissions();
        canUserStorage = getStoragePermissions();
        if(!canUseCamera || !canUserStorage){
            return;
        }

        //Clear from last run
        takePhotoUri = null;
        takeVideoUri = null;
        fileToPassAround = null;

        //To Delete or not
        if(typeOfPicture == sourceType.CAMERA || typeOfPicture == sourceType.WEB_URL){
            shouldDeletePhoto = true;
        } else {
            shouldDeletePhoto = false;
        }

        switch (typeOfPicture){

            case CAMERA:
                this.takePhotoWithCamera();
                break;

            case VIDEO:
                this.takeVideoWithCamera(DURATION_LIMIT_FOR_VIDEOS); //Hard coding this for now, add int param
                break;

            case GALLERY:
                this.getPhotoFromGallery();
                break;

            case WEB_URL:
                this.cropPhotoFromWeb();
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
            listener.onTaskCompleteV2("URL was invalid", TAG_PHOTO_BAD_URL);
            return;
        }
        L.m("webImageUrl = " + webImageUrl);

        final ImageUtilities.DownloadImageFromWeb downloadImageFromWeb =
                new ImageUtilities.DownloadImageFromWeb(context, webImageUrl, new OnTaskCompleteListener() {
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
                                    CameraUtilities.this.startCropping(
                                            downloadedPhotoUri, downloadedPhotoUri);
                                } else {
                                    listener.onTaskCompleteV2("An Error Occured", TAG_PHOTO_UNKNOWN_ERROR);
                                }
                            } catch (Exception e) {
                                listener.onTaskCompleteV2("An Error Occured", TAG_PHOTO_UNKNOWN_ERROR);
                            }
                        } else {
                            listener.onTaskCompleteV2("An Error Occurred", TAG_PHOTO_UNKNOWN_ERROR);
                        }
                    }
                });

        downloadImageFromWeb.execute();

        return;

    }

    /**
     * Start the activity for result to get a camera image
     */
    private void takePhotoWithCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.generateImageUri(context));
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
     * Take a video
     * @param optionalDurationLimit Optional duration limit (In seconds)
     */
    private void takeVideoWithCamera(int optionalDurationLimit){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(optionalDurationLimit > 0){
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, optionalDurationLimit);
        }
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, generateVideoUri(context));
        activity.startActivityForResult(takeVideoIntent, TAG_TAKE_VIDEO_WITH_RECORDER);
    }

    /**
     * Get a Photo from the gallery
     */
    private void getPhotoFromGallery(){
        L.l(447);
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        L.l(449);
        galleryIntent.setType("image/*");
        L.l(451);
        activity.startActivityForResult(galleryIntent, TAG_PHOTO_FROM_GALLERY);
        L.l(453);
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
                    L.m("take photo with camera, sending uri");
                    L.m("uri = " + takePhotoUri);
                    //This means I need to use the Uri set by the generateUri function
                    startCropping(takePhotoUri, takePhotoUri);
                } else {
                    //
                    L.m("Data is NOT null within afterOnActivityResult & Tag = TAG_TAKE_PICTURE_WITH_CAMERA");
                    listener.onTaskCompleteV2(ERROR_STRING, TAG_PHOTO_UNKNOWN_ERROR);
                }
            }

            //Photo is from Gallery
            else if (requestcode == TAG_PHOTO_FROM_GALLERY
                    && resultcode == activity.RESULT_OK) {
                L.l(486);
                resultUri = data.getData();
                resultUri = this.fixImageUri(context, resultUri);
                startCropping(resultUri, resultUri);
                L.l(490);
            }

            //Video is from Recording (Video)
            else if (requestcode == TAG_TAKE_VIDEO_WITH_RECORDER) {

                // TODO: 7/27/2016  Decide what to do with this
                listener.onTaskCompleteV2(takeVideoUri, TAG_TAKE_VIDEO_WITH_RECORDER);

            }

            //Photo from Crop Photo
            else if (requestcode == TAG_CROP_PHOTO) {

                //Check for result code before moving on
                if(resultcode == activity.RESULT_OK){
                    resultUri = UCrop.getOutput(data);

                    L.m("result from cropping = " + resultUri);
                    PhotoObject photoObject = new PhotoObject();
                    photoObject.androidUri = resultUri;
                    photoObject.stringPath = StringUtilities.convertAndroidUriToString(resultUri);
                    photoObject.javaUri = StringUtilities.convertStringToJavaUri(photoObject.stringPath);
                    photoObject.photoFile = fileToPassAround;

                    if(shouldUploadPhoto){
                        this.uploadPhotosViaDefaultMethod(photoObject);
                    } else {
                        listener.onTaskCompleteV2(photoObject, TAG_CROP_SUCCESS);
                    }

                } else if(resultcode == UCrop.RESULT_ERROR){
                    final Throwable cropError = UCrop.getError(data);
                    listener.onTaskCompleteV2(ERROR_STRING + cropError.toString(), TAG_CROP_ERROR);
                }
            }

            //Camera Permission Request
            else if (requestcode == TAG_MY_PERMISSIONS_REQUEST_CAMERA
                    && resultcode == activity.RESULT_OK
                    && data != null) {
                L.m("Request Camera Permissions");


                //Storage Permission Request
            } else if (requestcode == TAG_MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                    && resultcode == activity.RESULT_OK
                    && data != null) {
                L.m("Request Storage Permissions");

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
                L.m("return Image url @ 558");
                listener.onTaskCompleteV2(photoObject, TAG_RETURN_IMAGE_URL);

                //Else, something weird happened
            } else {
                L.m("Something else happened, request code = " + requestcode);
                listener.onTaskCompleteV2("Photo Select was canceled", TAG_PHOTO_CANCEL);
            }
        } else {
            listener.onTaskCompleteV2("Photo Select was canceled", TAG_PHOTO_CANCEL);
        }
    }

    /**
     * Start the cropping activity via Yalantis UCrop
     * @param sourceUri Source URI to crop
     * @param destinationUri Destination URI
     */
    public void startCropping(Uri sourceUri, Uri destinationUri){
        //Build options for Love Lab Custom
        options = LoveLabCustomUtilities.buildUCropOptionsForLoveLab();

        try {
            UCrop cropping = UCrop.of(sourceUri, destinationUri);
            if(!(maxHeight == 0 || maxWidth == 0)){
                cropping.withMaxResultSize(maxWidth, maxHeight);
            }
            if(options != null){
                cropping.withOptions(options);
            }
            L.m("cropping start");
            cropping.start(activity, TAG_CROP_PHOTO);
        } catch (Exception e){
            L.m("cropping error");
            e.printStackTrace();
            listener.onTaskCompleteV2("An error occurred while processing your photo", TAG_CROP_ERROR);
        }
    }

    /**
     * Generate an ImageUri
     * @param mContext
     * @return
     */
    public Uri generateImageUri(Context mContext){
        fileToPassAround = ImageUtilities.generateFileForImage(mContext);
        takePhotoUri = ImageUtilities.convertFileToUri(fileToPassAround);
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
            mContext = MyApplication.getAppContext();
        }
        Uri vidUri = null;
        String state = Environment.getExternalStorageState();
        String appName = mContext.getString(R.string.app_name_for_uri);
        appName = StringUtilities.removeSpaces(appName);
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", appName +
                    "_Video_" + DateUtilities.getCurrentDateLong() + ".mp4");
            vidUri = Uri.fromFile(file);
            fileToPassAround = file;
        }else {
            File file = new File(mContext.getFilesDir() , appName + Calendar.getInstance().getTimeInMillis()+ ".png");
            vidUri = Uri.fromFile(file);
            fileToPassAround = file;
        }
        takeVideoUri = vidUri;
        return vidUri;
    }

    /**
     * Upload a photo object
     * @param photoObject Photo object to upload
     */
    private void uploadPhotosViaDefaultMethod(final PhotoObject photoObject){
        showAlertOrNot(true);
        CloudineryAPI cloudineryAPI = new CloudineryAPI(context, photoObject.photoFile,
                new OnTaskCompleteListner() {
                    @Override
                    public void onTaskComplete(Object result) {
                        //NOTE! IN THIS PROJECT, DELETING THE IMAGE AFTERWARDS.
                        //REMOVE THIS CODE FOR OTHER PROJECTS
                        try {
                            if(shouldDeletePhoto) {
                                try {
                                    photoObject.photoFile.delete();
                                } catch (Exception e){}
                                try {
                                    fileToPassAround.delete();
                                } catch (Exception e){}
                                try {
                                    tempFile.delete();
                                } catch (Exception e){}
                            }
                        } catch (Exception e){}
                        showAlertOrNot(false);

                        if(result == null){
                            String str = LoveLabCustomUtilities.getErrorString(null);
                            listener.onTaskCompleteV2(str, TAG_UPLOAD_ERROR);
                        }

                        if(result instanceof LovelabError){
                            String str = LoveLabCustomUtilities.getErrorString((LovelabError)result);
                            listener.onTaskCompleteV2(str, TAG_UPLOAD_ERROR);
                        }

                        if(result instanceof String){
                            String str = (String) result;
                            listener.onTaskCompleteV2(str, TAG_UPLOAD_SUCCESS);
                        }
                    }
                });
        cloudineryAPI.execute();
    }

    /**
     * Overloaded method, see description above for method information
     * @param photoObjects Photo objects to delete
     */
    private void uploadPhotosViaDefaultMethod(List<PhotoObject> photoObjects){
        //Not used for now as there is no mass upload option. If needed in the future, implement here
    }

    /**
     * Use this method to fix URIs that are not usable or are in a format not readable. An example
     * would be one that starts with content://..........
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
    public static int doesUserHaveFrontFacingCamera(Context context) {
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


        return -1; // No front-facing camera found
    }
}