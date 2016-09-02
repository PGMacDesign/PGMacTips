package com.pgmacdesign.pgmacutilities.utilities;

import com.google.android.gms.vision.face.Landmark;

import java.util.List;

/**
 * Utilities for working with the vision.face library. For more info, see their docs at:
 * https://developers.google.com/android/reference/com/google/android/gms/vision/face/package-summary
 * Created by pmacdowell on 9/1/2016.
 */
public class GoogleVisionFaceUtilities {

    /*
    (0, 0) represents the top-left of the camera field of view,
    and (?, ?) represents the bottom-right of the field of view.
    Top left = ()
    Top Right = ()
    Bottom Left = ()
    Bottom Right = ()
    Center = (0, 0)
     */

    /**
     * Pulling from:
     * https://developers.google.com/android/reference/com/google/android/gms/vision/face/Landmark
     */
    public static enum LandmarkFaceIds {
        BOTTOM_MOUTH(0),
        LEFT_CHEEK(1),
        LEFT_EAR_TIP(2),
        LEFT_EAR(3),
        LEFT_EYE(4),
        LEFT_MOUTH(5),
        NOSE_BASE(6),
        RIGHT_CHEEK(7),
        RIGHT_EAR_TIP(8),
        RIGHT_EAR(9),
        RIGHT_EYE(10),
        RIGHT_MOUTH(11),
        UNKNOWN(-1);

        public int id;
        LandmarkFaceIds(int id){
            this.id = id;
        }

        public static LandmarkFaceIds whichIsThis(int id){
            switch (id){
                case 0:
                    return BOTTOM_MOUTH;
                case 1:
                    return LEFT_CHEEK;
                case 2:
                    return LEFT_EAR_TIP;
                case 3:
                    return LEFT_EAR;
                case 4:
                    return LEFT_EYE;
                case 5:
                    return LEFT_MOUTH;
                case 6:
                    return NOSE_BASE;
                case 7:
                    return RIGHT_CHEEK;
                case 8:
                    return RIGHT_EAR_TIP;
                case 9:
                    return RIGHT_EAR;
                case 10:
                    return RIGHT_EYE;
                case 11:
                    return RIGHT_MOUTH;
                default:
                    return UNKNOWN;
            }
        }
    }

    public static boolean isCentered(List<Landmark> landmarkList){
        boolean isItCentered = false;
        float averageX, averageY;
        int totalXObjects, totalYObjects;
        if(MiscUtilities.isListNullOrEmpty(landmarkList)){
            isItCentered = false;
        }
        for(Landmark landmark : landmarkList){
            int x = landmark.getType();
//https://developers.google.com/android/reference/com/google/android/gms/vision/face/Landmark.html#getPosition()
        }

        return false;
    }

}
