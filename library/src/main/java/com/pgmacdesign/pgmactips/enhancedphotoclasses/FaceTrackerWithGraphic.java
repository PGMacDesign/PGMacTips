package com.pgmacdesign.pgmactips.enhancedphotoclasses;

/**
 * Created by PatrickSSD2 on 9/21/2016.
 */
@Deprecated
class FaceTrackerWithGraphic {// extends Tracker<Face>  {

//
//
//
//    //Graphic and tracker Vars
//    private FaceGraphic mFaceGraphic;
//    private CustomPhotoListener listener;
//    private GraphicOverlay mOverlay;
//
//    //Vars
//    private boolean numFacesOk, isBlocked;
//    private long timeEnding;
//    // TODO: 9/21/2016 refactor this into dynamic changeable vars
//    private static final long TIME_BETWEEN_ADJUSTMENTS = (long) (PGMacTipsConstants.ONE_SECOND * 1.5);
//
//    public FaceTrackerWithGraphic(GraphicOverlay mOverlay, CustomPhotoListener listener) {
//        this.mOverlay = mOverlay;
//        this.mFaceGraphic = new FaceGraphic(this.mOverlay);
//        this.listener = listener;
//    }
//
//    //Override methods
//
//    @Override
//    public void onNewItem(int faceId, Face face) {
//        if (face != null) {
//            mFaceGraphic.setId(faceId);
//        }
//    }
//
//    @Override
//    public void onUpdate(Detector.Detections<Face> detectionResults, Face face) {
//        //Add the face
//        if (face != null) {
//            mOverlay.add(mFaceGraphic);
//            mFaceGraphic.updateFace(face);
//        }
//
//        //Check num faces
//        int x = detectionResults.getDetectedItems().size();
//        updateFaces(x);
//    }
//
//    @Override
//    public void onMissing(Detector.Detections<Face> detectionResults) {
//        mOverlay.remove(mFaceGraphic);
//        int x = detectionResults.getDetectedItems().size();
//        updateFaces(x);
//    }
//
//    private void updateFaces(int numFaces) {
//        if (!checkIfBlocked()) {
//            listener.facesChanged(numFaces);
//        }
//    }
//
//    /**
//     * For checking if the 'timeout' is in place
//     *
//     * @return
//     */
//    private boolean checkIfBlocked() {
//        long myTime = DateUtilities.getCurrentDateLong();
//        if (myTime <= timeEnding) {
//            isBlocked = true;
//            return isBlocked;
//        } else {
//            timeEnding = myTime + TIME_BETWEEN_ADJUSTMENTS;
//            isBlocked = false;
//            return isBlocked;
//        }
//    }
//
//    /**
//     * Builder to return the Face Tracker
//     * @return
//     */
//    public Detector.Processor<Face> buildDetector(){
//        return new MultiProcessor.Builder<>(new FaceTrackerWithGraphicFactory())
//                .build();
//    }
//
//    private class FaceTrackerWithGraphicFactory implements MultiProcessor.Factory<Face> {
//        /*
//        * Face tracker for each detected individual. This maintains a face graphic within the app's
//        * associated face overlay.
//        */
//        @Override
//        public Tracker<Face> create(Face face) {
//            return new FaceTrackerWithGraphic(mOverlay, listener);
//        }
//    }
//    /**
//     * This is used for settings within the Face Tracker.
//     * todo for now this is not used, will eventually refactor this in
//
//     private static class FaceTrackerOptions{
//     boolean showTrackerCircle, autoTakePhoto, showShutterIcon;
//     int maxNumFaces, minNumFaces, countdownTimer, customShutterImageId;
//     ShutterColors stockShutterColor;
//     enum ShutterColors {BLUE, WHITE, BLACK, GREY};
//     }
//
//     public static class AutoPhotoOptionsBuilder{
//     private static FaceTrackerOptions options;
//
//     //Defaults
//     static {
//     options = new FaceTrackerOptions();
//     options.autoTakePhoto = true;
//     options.showTrackerCircle = true;
//     options.showShutterIcon = true;
//     options.maxNumFaces = 1;
//     options.minNumFaces = 1;
//     options.countdownTimer = 3;
//     options.stockShutterColor = FaceTrackerOptions.ShutterColors.BLUE;
//     options.customShutterImageId = R.drawable.shutter_blue;
//     }
//
//     public void setStockShutterColor(FaceTrackerOptions.ShutterColors stockShutterColor) {
//     options.stockShutterColor = stockShutterColor;
//     }
//
//     public void setShowTrackerCircle(boolean showTrackerCircle) {
//     options.showTrackerCircle = showTrackerCircle;
//     }
//
//     public void setAutoTakePhoto(boolean autoTakePhoto) {
//     options.autoTakePhoto = autoTakePhoto;
//     }
//
//     public void setShowShutterIcon(boolean showShutterIcon) {
//     options.showShutterIcon = showShutterIcon;
//     }
//
//     public void setMaxNumFaces(int maxNumFaces) {
//     options.maxNumFaces = maxNumFaces;
//     }
//
//     public void setMinNumFaces(int minNumFaces) {
//     options.minNumFaces = minNumFaces;
//     }
//
//     public void setCustomShutterImageId(int customShutterImageId) {
//     options.customShutterImageId = customShutterImageId;
//     }
//
//     public void setCountdownTimer(int countdownTimer) {
//     options.countdownTimer = countdownTimer;
//     }
//
//     public FaceTrackerOptions build(){
//     return options;
//     }
//     }
//     */
}
