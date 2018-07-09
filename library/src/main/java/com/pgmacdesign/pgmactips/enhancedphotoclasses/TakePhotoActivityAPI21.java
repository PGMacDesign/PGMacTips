package com.pgmacdesign.pgmactips.enhancedphotoclasses;


/**
 * Most pulled from:
 * https://developer.android.com/samples/Camera2Basic/src/com.example.android.camera2basic/Camera2BasicFragment.html#l167
 *
 * Created by pmacdowell on 9/19/2016.
 */
@Deprecated
class TakePhotoActivityAPI21 { // extends AppCompatActivity implements View.OnClickListener, CustomPhotoListener {
//
//    //UI
//    private ImageView take_photo_activity_api21_shutter_button;
//    private CoordinatorLayout take_photo_activity_api21_top_coordinator_layout;
//    private TextView take_photo_activity_api21_top_textview,
//            take_photo_activity_api21_center_countdown_textview;
//    private RelativeLayout take_photo_activity_api21_relative_layout,
//            take_photo_activity_api21_top_text_layout;
//    private TextureView take_photo_activity_api21_textureview;
//    private CircleOverlayView circleOverlayView;
//
//    //Misc
//    private long timeActivityOpened;
//    private static final long TIME_IN_MILLISECONDS_FOR_INITIAL_DELAY =
//            (PGMacTipsConstants.ONE_SECOND * 4);
//    private boolean okToTake;
//    private File file;
//    private String userSentPathToFile, userSentNameOfFile, photoExtensionName;
//    private boolean useFlash, useFrontFacingCamera;
//    private boolean postedSmileText, postedNot1FaceText, postedInitialText, blockPosts;
//
//    //Camera2 Stuff
//    private String cameraId;
//    protected CameraDevice cameraDevice;
//    protected CameraCaptureSession cameraCaptureSessions;
//    protected CaptureRequest.Builder captureRequestBuilder;
//    private Size imageDimension;
//    private ImageReader imageReader;
//    private boolean mFlashSupported;
//    private Handler mBackgroundHandler;
//    private HandlerThread mBackgroundThread;
//    private boolean swappedDimensions = false;
//    private TextureView.SurfaceTextureListener textureListener;
//    private CameraSource mCameraSource = null;
//
//    //Custom UI VisionFeatures
//    private GraphicOverlay graphic_face_overlay;
//    private FaceTrackerWithGraphic faceTracker;
//    private TakePhotoWithCountdownAsync async;
//
//    /**
//     * Orientation of the camera sensor
//     */
//    private int mSensorOrientation;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        preInit();
//        setContentView(R.layout.take_photo_activity_api21);
//        initUI();
//        initIntentData();
//        initVariables();
//        initLastCalls();
//    }
//
//    /**
//     * Called before the UI is set to make it full screen
//     */
//    private void preInit(){
//        //No title here. Full screen
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
//                        View.SYSTEM_UI_FLAG_LOW_PROFILE |
//                        View.SYSTEM_UI_FLAG_FULLSCREEN);
//
//    }
//
//    /**
//     * initialize the ui
//     */
//    private void initUI() {
//        take_photo_activity_api21_textureview = (TextureView) this.findViewById(
//                R.id.take_photo_activity_api21_textureview);
//        take_photo_activity_api21_textureview.setTag(
//                "take_photo_activity_api21_textureview");
//
//        take_photo_activity_api21_relative_layout = (RelativeLayout) this.findViewById(
//                R.id.take_photo_activity_api21_relative_layout);
//        take_photo_activity_api21_relative_layout.setTag(
//                "take_photo_activity_api21_relative_layout");
//
//        take_photo_activity_api21_top_textview = (TextView) this.findViewById(
//                R.id.take_photo_activity_api21_top_textview);
//        take_photo_activity_api21_top_textview.setTag(
//                "take_photo_activity_api21_top_textview");
//
//        take_photo_activity_api21_center_countdown_textview = (TextView) this.findViewById(
//                R.id.take_photo_activity_api21_center_countdown_textview);
//        take_photo_activity_api21_center_countdown_textview.setTag(
//                "take_photo_activity_api21_center_countdown_textview");
//
//        take_photo_activity_api21_top_text_layout = (RelativeLayout) this.findViewById(
//                R.id.take_photo_activity_api21_top_text_layout);
//
//        take_photo_activity_api21_top_coordinator_layout = (CoordinatorLayout) this.findViewById(
//                R.id.take_photo_activity_api21_top_coordinator_layout);
//        take_photo_activity_api21_top_coordinator_layout.setTag(
//                "take_photo_activity_api21_top_coordinator_layout");
//
//        take_photo_activity_api21_shutter_button = (ImageView) this.findViewById(
//                R.id.take_photo_activity_api21_shutter_button);
//        take_photo_activity_api21_shutter_button.setTag(
//                "take_photo_activity_api21_shutter_button");
//
//        graphic_face_overlay = (GraphicOverlay) this.findViewById(
//                R.id.graphic_face_overlay);
//
//        setupOverlay();
//    }
//
//    private void setupOverlay(){
//        //Local, for use in setting the overlay view
//        // TODO: 9/21/2016 refactor this into constructor preferences
//        FrameLayout take_photo_activity_api21_overlay_layout = (FrameLayout) this.findViewById(
//                R.id.take_photo_activity_api21_overlay_layout);
//        take_photo_activity_api21_overlay_layout.setVisibility(View.VISIBLE);
//        DisplayManagerUtilities dmu = new DisplayManagerUtilities(this);
//        int width = (int) (dmu.getWidthRadius() * 0.83);
//        CircleOverlayView.CircleOverlayParams params = new CircleOverlayView.CircleOverlayParams();
//        params.setShapeType(CircleOverlayView.CircleOverlayParams.ShapeTypes.OVAL);
//        params.setColorToSet(ColorUtilities.parseMyColor(PGMacTipsConstants.COLOR_BLACK));
//        params.setAlphaToUse(99);
//        params.setShapeRadius(width);
//        circleOverlayView = new CircleOverlayView(this, params);
//        take_photo_activity_api21_overlay_layout.addView(circleOverlayView);
//
//        take_photo_activity_api21_shutter_button.bringToFront();
//    }
//
//    /**
//     * Load up the data from the intent
//     */
//    private void initIntentData(){
//        Intent intent = getIntent();
//        userSentPathToFile = intent.getStringExtra(CameraMediaUtilities.TAG_FILE_PATH);
//        userSentNameOfFile = intent.getStringExtra(CameraMediaUtilities.TAG_FILE_NAME);
//        photoExtensionName = intent.getStringExtra(CameraMediaUtilities.TAG_FILE_EXTENSION);
//        //Both booleans default to true unless specified otherwise
//        useFlash = intent.getBooleanExtra(CameraMediaUtilities.TAG_USE_FLASH, true);
//        useFrontFacingCamera = intent.getBooleanExtra(
//                CameraMediaUtilities.TAG_USE_FRONT_FACING_CAMERA, true);
//
//        if(StringUtilities.anyNullsOrEmptyInStrings(new String[]{userSentPathToFile,
//                userSentNameOfFile, photoExtensionName})){
//            L.toast(this, "An error occurred, please try again");
//            this.finish();
//        }
//    }
//
//    /**
//     * Init any Misc variables
//     */
//    private void initVariables(){
//        okToTake = true;
//
//        //Check current API level. Should be only 21+ here
//        if(!SystemUtilities.userHasMarshmallowOrHigher()){
//            L.toast(this, "Your phone does not support this feature");
//            this.finish();
//        }
//
//        //Check camera feature (people have phones without cameras?)
//        if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
//            L.toast(this, "Your phone does not support this feature");
//            this.finish();
//        }
//
//        //Check camera permissions
//        PermissionUtilities perm = PermissionUtilities.getInstance(this);
//        if(!perm.startPermissionsRequest(PermissionUtilities.permissionsEnum.CAMERA)){
//            //No permissions given
//            L.toast(this, "You must enable camera permissions to use this feature");
//            this.finish();
//        }
//
//        textureListener = new TextureView.SurfaceTextureListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//                //open your camera here
//                postAMessage(AlertMessages.INITIAL);
//                openCamera();
//
//            }
//            @Override
//            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//                // Transform you image captured size according to the surface width and height
//            }
//            @Override
//            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//                return false;
//            }
//            @Override
//            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            }
//        };
//    }
//
//    /**
//     * Last calls happen here
//     */
//    private void initLastCalls(){
//        timeActivityOpened = DateUtilities.getCurrentDateLong();
//        assert take_photo_activity_api21_textureview != null;
//        take_photo_activity_api21_shutter_button.setOnClickListener(this);
//        take_photo_activity_api21_textureview.setSurfaceTextureListener(textureListener);
//    }
//
//    /**
//     * On Click
//     * @param view
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onClick(View view) {
//        String str = null;
//        try {
//            str = (String) view.getTag();
//        } catch (Exception e){}
//        if(str != null){
//            if(str.equalsIgnoreCase("take_photo_activity_api21_shutter_button")){
//                if(okToTake) {
//                    // TODO: 9/20/2016 add code for face recognition to auto take picture
//                    takePicture();
//                    okToTake = false;
//                }
//            }
//        }
//    }
//
//
//    @SuppressLint("NewApi")
//    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
//        @Override
//        public void onOpened(CameraDevice camera) {
//            //This is called when the camera is open
//            cameraDevice = camera;
//            createCameraPreview();
//        }
//        @Override
//        public void onDisconnected(CameraDevice camera) {
//            cameraDevice.close();
//        }
//        @Override
//        public void onError(CameraDevice camera, int error) {
//            cameraDevice.close();
//            cameraDevice = null;
//        }
//    };
//
//
//    /**
//     * Gets called when the screen opens
//     */
//    protected void startBackgroundThread() {
//        L.m("BACKGROUND THREAD STARTED");
//        mBackgroundThread = new HandlerThread("Camera Background");
//        mBackgroundThread.start();
//        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
//    }
//
//    /**
//     * Gets called when the screen closes (onPause or onStop)
//     */
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//    protected void stopBackgroundThread() {
//        L.m("BACKGROUND THREAD STOPPED");
//        mBackgroundThread.quitSafely();
//        try {
//            mBackgroundThread.join();
//            mBackgroundThread = null;
//            mBackgroundHandler = null;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Take the actual picture
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    protected void takePicture() {
//        if(null == cameraDevice) {
//            L.m("cameraDevice is null");
//            return;
//        }
//        stopPhotoCountdown();
//        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//
//        try {
//            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
//
//            Size[] jpegSizes = null;
//            if (characteristics != null) {
//                jpegSizes = characteristics.get(CameraCharacteristics.
//                        SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
//            }
//            //Default width and height if nothing is received
//            int width = 640;
//            int height = 480;
//            if (jpegSizes != null && 0 < jpegSizes.length) {
//                //Set highest sizes
//                int counter = 0;
//                /*
//                For details on number here, see at the bottom
//                 */
//                width = jpegSizes[0].getWidth();
//                height = jpegSizes[0].getHeight();
//            }
//            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
//            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
//            outputSurfaces.add(reader.getSurface());
//            outputSurfaces.add(new Surface(take_photo_activity_api21_textureview.getSurfaceTexture()));
//            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//
//            captureBuilder.addTarget(reader.getSurface());
//            this.setCaptureRequestDetails(captureBuilder);
//
//            file = FileUtilities.generateFileForImage(userSentPathToFile,
//                    userSentNameOfFile, photoExtensionName);
//            //file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
//            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
//                @Override
//                public void onImageAvailable(ImageReader reader) {
//                    Image image = null;
//                    try {
//                        image = reader.acquireLatestImage();
//                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//                        byte[] bytes = new byte[buffer.capacity()];
//                        buffer.get(bytes);
//                        save(bytes);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        if (image != null) {
//                            image.close();
//                        }
//                    }
//                }
//                private void save(byte[] bytes) throws IOException {
//                    OutputStream output = null;
//                    try {
//                        output = new FileOutputStream(file);
//                        output.write(bytes);
//                    } finally {
//                        if (null != output) {
//                            output.close();
//                        }
//                    }
//                }
//            };
//            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
//
//            //Show the spinner
//            ProgressBarUtilities.showSVGProgressDialog(this);
//
//            //For some unknown reason this had to be local and not global. Just for future reference
//            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
//                @Override
//                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
//                    super.onCaptureCompleted(session, request, result);
//                    createCameraPreview();
//                    if(file != null) {
//                        successMethod();
//                    }
//                }
//            };
//
//            //Set the capture session
//            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
//                @Override
//                public void onConfigured(CameraCaptureSession session) {
//                    try {
//                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//                @Override
//                public void onConfigureFailed(CameraCaptureSession session) {
//
//                }
//            }, mBackgroundHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * This is called when the camera finishes taking a photo and the file is written
//     */
//    private void successMethod(){
//        android.net.Uri uri = FileUtilities.convertFileToUri(file);
//        Intent resultIntent = new Intent();
//        resultIntent.putExtra(CameraMediaUtilities.TAG_SELF_PHOTO_URI,
//                StringUtilities.convertAndroidUriToString(uri));
//        setResult(Activity.RESULT_OK, resultIntent);
//        TakePhotoActivityAPI21.this.finish();
//    }
//    /**
//     * For setting capture request details. For list of them, see:
//     * https://developer.android.com/reference/android/hardware/camera2/CaptureRequest.html
//     * @param captureBuilder
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void setCaptureRequestDetails(CaptureRequest.Builder captureBuilder){
//
//        //Control mode
//        if(SystemUtilities.userHasKitKatOrHigher());
//        if(SystemUtilities.userHasOrIsHigherThan(15)){
//            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//
//            //Flash
//            if(mFlashSupported) {
//                captureBuilder.set(CaptureRequest.FLASH_MODE, useFlash ?
//                        CameraMetadata.FLASH_MODE_TORCH : CameraMetadata.FLASH_MODE_OFF);
//
//            }
//            captureBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,
//                    CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL);
//
//            /*
//            Can also set AutoFlash
//                        requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
//                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
//             */
//        }
//
//        // Orientation
//        int rotation = getWindowManager().getDefaultDisplay().getRotation();
//        Integer rotationValue = getOrientation(rotation);
//        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotationValue);
//        L.m("CAPTURE REQUEST ROATION = " + rotationValue);
//
//
//
//    }
//
//    //Pass in one, get the other out
//    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
//    static {
//        ORIENTATIONS.append(Surface.ROTATION_0, 90);
//        ORIENTATIONS.append(Surface.ROTATION_90, 0);
//        ORIENTATIONS.append(Surface.ROTATION_180, 270);
//        ORIENTATIONS.append(Surface.ROTATION_270, 180);
//    }
//    /*
//    *   Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
//    *   We have to take that into account and rotateImage JPEG properly.
//    *   For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
//    *   For devices with orientation of 270, we need to rotateImage the JPEG 180 degrees.
//    */
//    private int getOrientation(int rotation) {
//        int orient1 = ORIENTATIONS.get(rotation);
//        orient1 += mSensorOrientation + 270;
//        L.m("orient1 after adding in mSensor = " + orient1);
//        if(swappedDimensions){
//            //orient1 += 180;
//        }
//        L.m("orient1 after swappedDimensions = " + orient1);
//        orient1 = (orient1 % 360);
//        L.m("orient1 after modulus = " + orient1);
//        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    protected void createCameraPreview() {
//        try {
//            SurfaceTexture texture = take_photo_activity_api21_textureview.getSurfaceTexture();
//            assert texture != null;
//            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
//            Surface surface = new Surface(texture);
//            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            captureRequestBuilder.addTarget(surface);
//            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
//                @Override
//                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
//                    //The camera is already closed
//                    if (null == cameraDevice) {
//                        return;
//                    }
//                    // When the session is ready, we start displaying the preview.
//                    cameraCaptureSessions = cameraCaptureSession;
//                    updatePreview();
//                    okToTake = true;
//                }
//                @Override
//                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
//                    Toast.makeText(TakePhotoActivityAPI21.this, "Configuration change", Toast.LENGTH_SHORT).show();
//                }
//            }, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void openCamera() {
//        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        try {
//            //Here is where the camera ID is retrieved
//            cameraId = this.getWhichCamera(manager);
//
//            //Get the characteristics
//            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
//
//            //Adjust rotation stuff
//            adjustImageRotation(characteristics);
//
//            StreamConfigurationMap map = characteristics.get(
//                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//            assert map != null;
//            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
//            // Add permission for camera and let user grant the permission
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(TakePhotoActivityAPI21.this,
//                        new String[]{Manifest.permission.CAMERA,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        CameraMediaUtilities.TAG_MY_PERMISSIONS_REQUEST_CAMERA);
//                return;
//            }
//            manager.openCamera(cameraId, stateCallback, null);
//            setupFaceTracker();
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//        L.m("openCamera X");
//    }
//
//    /**
//     * Setup the face tracker
//     */
//    private void setupFaceTracker(){
//        //Move on to Face graphic overlay
//        try {
//            faceTracker = new FaceTrackerWithGraphic(graphic_face_overlay, this);
//            FaceDetector detector = new FaceDetector.Builder(this)
//                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
//                    .build();
//            detector.setProcessor(faceTracker.buildDetector());
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void adjustImageRotation(CameraCharacteristics characteristics){
//        //Set Sensor info
//        try {
//            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
//            L.m("mSensorOrientation = " + mSensorOrientation);
//        } catch (NullPointerException e){}
//        // Find out if we need to swap dimension to get the preview size relative to sensor
//        // coordinate.
//        int displayRotation = TakePhotoActivityAPI21.this.getWindowManager().getDefaultDisplay().getRotation();
//        L.m("displayRotation = " + displayRotation);
//
//        switch (displayRotation) {
//            case Surface.ROTATION_0:
//            case Surface.ROTATION_180:
//                if (mSensorOrientation == 90 || mSensorOrientation == 270) {
//                    swappedDimensions = true;
//                }
//                break;
//            case Surface.ROTATION_90:
//            case Surface.ROTATION_270:
//                if (mSensorOrientation == 0 || mSensorOrientation == 180) {
//                    swappedDimensions = true;
//                }
//                break;
//            default:
//                L.m("Display rotation is invalid: " + displayRotation);
//        }
//        try {
//            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
//            mFlashSupported = available == null ? false : available;
//        } catch (Exception e){
//            mFlashSupported = false;
//        }
//    }
//    /**
//     * This is where front or back-facing camera is set
//     * @param manager
//     * @return
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private String getWhichCamera(CameraManager manager){
//        if(manager == null){
//            return null;
//        }
//        try {
//            for(final String cameraId : manager.getCameraIdList()){
//                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
//                if(useFrontFacingCamera){
//                    int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
//                    if(cOrientation == CameraCharacteristics.LENS_FACING_FRONT){
//                        return cameraId;
//                    }
//                } else {
//                    int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
//                    if(cOrientation == CameraCharacteristics.LENS_FACING_BACK){
//                        return cameraId;
//                    }
//                }
//
//            }
//            //In the event nothing hits, return 0
//            return (manager.getCameraIdList()[0]);
//        } catch (CameraAccessException e){
//            e.printStackTrace();
//            L.toast(this, e.getLocalizedMessage());
//        }
//
//        return null;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    protected void updatePreview() {
//        if(null == cameraDevice) {
//            L.m("updatePreview error, return");
//        }
//        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//        try {
//            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null,
//                    mBackgroundHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void closeCamera() {
//        if (null != cameraDevice) {
//            cameraDevice.close();
//            cameraDevice = null;
//        }
//        if (null != imageReader) {
//            imageReader.close();
//            imageReader = null;
//        }
//    }
//
//    /**
//     * Enable or disable the camera functionality
//     * @param bool true for enable false for disable
//     */
//    private void enableCamera(boolean bool){
//        if(bool){
//            okToTake = true;
//            take_photo_activity_api21_shutter_button.setImageResource(R.drawable.shutter_blue);
//        } else {
//            stopPhotoCountdown();
//            okToTake = false;
//            take_photo_activity_api21_shutter_button.setImageResource(R.drawable.shutter_grey);
//        }
//    }
//
//    @Override
//    public void facesChanged(int numberOfFaces) {
//        if(numberOfFaces == 1){
//            enableCamera(true);
//            if(isPastInitialStartupTime()){
//                startPhotoCountdown();
//            }
//        } else {
//            enableCamera(false);
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void countdownFinished(boolean bool) {
//        if(bool){
//            //Photo is ready to go
//            takePicture();
//        } else {
//            //Photo was cancelled (maybe they moved?) popup here with text
//        }
//    }
//
//    /**
//     * Checks if the initial time has passed on startup in order to give the user
//     * some extra time to get situated before the auto timer starts
//     * @return
//     */
//    private boolean isPastInitialStartupTime(){
//        long currentTime = DateUtilities.getCurrentDateLong();
//        if(currentTime - timeActivityOpened > TIME_IN_MILLISECONDS_FOR_INITIAL_DELAY){
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Start the auto photo countdown
//     */
//    private void startPhotoCountdown(){
//        if(async != null){
//            return;
//        }
//        async = new TakePhotoWithCountdownAsync(this, 4,
//                take_photo_activity_api21_center_countdown_textview);
//        async.execute();
//    }
//
//    /**
//     * Stop the auto photo countdown
//     */
//    private void stopPhotoCountdown(){
//        if(async != null){
//            async.cancel(false);
//        }
//        async = null;
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//
//        if (requestCode == CameraMediaUtilities.TAG_MY_PERMISSIONS_REQUEST_CAMERA) {
//            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                // close the app
//                try {
//                    L.toast(TakePhotoActivityAPI21.this,
//                            "This feature cannot be used without granting camera permissions first");
//                } catch (Exception e){}
//                finish();
//            }
//        }
//    }
//
//    /**
//     * Post a message on the top section
//     * @param whichMessage
//     */
//    private void postAMessage(AlertMessages whichMessage){
//        if(blockPosts){
//            return;
//        }
//        switch (whichMessage){
//            case INITIAL:
//                if(!postedInitialText){
//                    postedInitialText = true;
//                    String message = whichMessage.message;
//                    int lengthOfDisplay = whichMessage.lengthOfDisplay;
//                    new MessageUserTop(message, lengthOfDisplay).execute();
//                }
//                break;
//
//            case BAD_FACES:
//                if(!postedNot1FaceText){
//                    postedNot1FaceText = true;
//                    String message = whichMessage.message;
//                    int lengthOfDisplay = whichMessage.lengthOfDisplay;
//                    new MessageUserTop(message, lengthOfDisplay).execute();
//                }
//                break;
//        }
//    }
//
//    /**
//     * For sending a message up top, fading in and out, and making it (in)visible
//     */
//    private class MessageUserTop extends AsyncTask<Void, Void, Void> {
//        String message;
//        long seconds;
//
//        /**
//         * Loads a message
//         * @param message String message to show
//         * @param seconds length of time
//         */
//        MessageUserTop(String message, long seconds){
//            this.seconds = seconds;
//            this.message = message;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            try {
//                fadeInTextSlow(message);
//            } catch (Exception e){e.printStackTrace();}
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                Thread.sleep(seconds);
//            } catch (InterruptedException e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            blockPosts = false;
//            try {
//                fadeOutTextSlow();
//            } catch (Exception e){e.printStackTrace();}
//        }
//    }
//    private void fadeInTextSlow(String message){
//        take_photo_activity_api21_top_textview.setText(Html.fromHtml(message));
//        take_photo_activity_api21_relative_layout.setVisibility(View.VISIBLE);
//        take_photo_activity_api21_top_text_layout.setVisibility(View.VISIBLE);
//        AnimationUtilities.animateMyView(take_photo_activity_api21_top_textview,
//                (int)(PGMacTipsConstants.ONE_SECOND * 0.4),
//                Techniques.FadeInUp);
//        take_photo_activity_api21_top_textview.bringToFront();
//    }
//    private void fadeOutTextSlow(){
//        AnimationUtilities.animateMyView(take_photo_activity_api21_top_text_layout,
//                (int)(PGMacTipsConstants.ONE_SECOND * 0.4),
//                Techniques.FadeOutDown);
//    }
//
//    /**
//     * Alert messages to show the user.
//     */
//    private static enum AlertMessages{
//        INITIAL("Center your face in circle and give us your best smile!",
//                (int)(PGMacTipsConstants.ONE_SECOND * 3)),
//        BAD_FACES("We can't see your face!",
//                (int)(PGMacTipsConstants.ONE_SECOND * 2.5));
//
//        String message;
//        int lengthOfDisplay;
//        AlertMessages(String message, int lengthOfDisplay){
//            this.message = message;
//            this.lengthOfDisplay = lengthOfDisplay;
//        }
//    }
//    //post gist here
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onResume() {
//        super.onResume();
//        L.m("onResume");
//        startBackgroundThread();
//        if (take_photo_activity_api21_textureview.isAvailable()) {
//            openCamera();
//        } else {
//            take_photo_activity_api21_textureview.setSurfaceTextureListener(textureListener);
//        }
//    }
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onPause() {
//        L.m("onPause");
//        ProgressBarUtilities.dismissProgressDialog();
//        closeCamera();
//        stopBackgroundThread();
//        super.onPause();
//    }
//
//
//
//
//
//    /*
//    When printing out the sizes of the Size[] array, it will print out the available sizes
//    in descending order. For example, printing them out on a Motorola Droid Turbo2 prints:
//        WIDTH at position 0 = 2592
//        HEIGHT at position 0 = 1944
//        WIDTH at position 1 = 2592
//        HEIGHT at position 1 = 1458
//        WIDTH at position 2 = 1920
//        HEIGHT at position 2 = 1080
//        WIDTH at position 3 = 1440
//        HEIGHT at position 3 = 1080
//        WIDTH at position 4 = 1280
//        HEIGHT at position 4 = 960
//        WIDTH at position 5 = 1280
//        HEIGHT at position 5 = 720
//        WIDTH at position 6 = 640
//        HEIGHT at position 6 = 480
//        WIDTH at position 7 = 352
//        HEIGHT at position 7 = 288
//        WIDTH at position 8 = 320
//        HEIGHT at position 8 = 240
//     */
}
