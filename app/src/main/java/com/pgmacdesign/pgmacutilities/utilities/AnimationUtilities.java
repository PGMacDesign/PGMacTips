package com.pgmacdesign.pgmacutilities.utilities;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;

/**
 * Created by pmacdowell on 8/16/2016.
 */
public class AnimationUtilities {


    /*
    private static final int[] ENTER_ANIMATIONS = {
            Constants.ANIM_ANDROID_ACTIVITY_OPEN_ENTER,
            Constants.ANIM_ANDROID_FRAGMENT_OPEN_ENTER,
            Constants.ANIM_ANDROID_MODAL_OPEN_ENTER
    };
    private static final int[] EXIT_ANIMATIONS = {
            Constants.ANIM_ANDROID_MODAL_CLOSE_EXIT,
            Constants.ANIM_ANDROID_ACTIVITY_CLOSE_EXIT,
            Constants.ANIM_ANDROID_FRAGMENT_CLOSE_EXIT
    };
    */
    private static final Techniques[] ENTER_ANIMATIONS = {
            PGMacUtilitiesConstants.IN_ZOOM_UP, PGMacUtilitiesConstants.IN_ROLL,
            PGMacUtilitiesConstants.IN_PULSE, PGMacUtilitiesConstants.IN_RUBBERBAND,
            PGMacUtilitiesConstants.IN_FLIP_X, PGMacUtilitiesConstants.IN_RIGHT_SLIDE,
            PGMacUtilitiesConstants.IN_LEFT_SLIDE, PGMacUtilitiesConstants.IN_SLIDE,
            PGMacUtilitiesConstants.IN_FADE_DOWN, PGMacUtilitiesConstants.IN_FADE_UP,
            PGMacUtilitiesConstants.IN_DROP, PGMacUtilitiesConstants.IN_TADA
    };
    private static final Techniques[] EXIT_ANIMATIONS = {
            PGMacUtilitiesConstants.OUT_ZOOM_DOWN, PGMacUtilitiesConstants.OUT_HINGE,
            PGMacUtilitiesConstants.OUT_FLIP_Y, PGMacUtilitiesConstants.OUT_SLIDE,
            PGMacUtilitiesConstants.OUT_FLIP_X, PGMacUtilitiesConstants.OUT_ROLL,
            PGMacUtilitiesConstants.OUT_ZOOM
    };

    /**
     * This is to be called when a startActivity() is called. It will add an animation to the
     * screens entering and exiting
     * @param activity Activity that this is being called from. Note, if in a fragment, please
     *                 make sure to cast it accordingly [IE: ((MainActivity)getActivity) ]
     * @param entryAnimation The animation for the NEW activity to display when coming in / entering
     * @param exitAnimation The animation for the OLD activity to display when leaving / exiting
     */
    public static void overridePendingTransaction(final Activity activity,
                                                  int entryAnimation,
                                                  int exitAnimation){
        if(activity == null){
            return;
        }
        try {
            activity.overridePendingTransition(entryAnimation, exitAnimation);
        } catch (Exception e){
            L.m("An error occured when animation was attempted");
        }
    }

    /**
     * Return a random Enter Animation
     * @return Techniques (From Animation library)
     */
    private static Techniques getRandomEnterAnimation(){
        int x = NumberUtilities.getRandomInt(0, (ENTER_ANIMATIONS.length - 1));
        try {
            return ENTER_ANIMATIONS[x];
        } catch (ArrayIndexOutOfBoundsException e){
            return ENTER_ANIMATIONS[(0)];
        }
    }

    /**
     * Return a random Enter Animation
     * @return Techniques (From Animation library)
     */
    private static Techniques getRandomExitAnimation(){
        int x = NumberUtilities.getRandomInt(0, (EXIT_ANIMATIONS.length - 1));
        try {
            return EXIT_ANIMATIONS[x];
        } catch (ArrayIndexOutOfBoundsException e){
            return EXIT_ANIMATIONS[(0)];
        }
    }

    /**
     * Animates a view.
     * @param view View to be animated
     * @param mDuration Duration (in milliseconds) of animation. 1000 would be 1 second
     * @param tech The animation to actually be done. Obtain technique like so: Techniques tech1 = Techniques.Wobble;
     * @param <E> A View object (IE EditText, TextView, Button, View, etc)
     */
    public static <E extends View> void animateMyView(E view, Integer mDuration, Techniques tech){
        //If the view is null, stop here
        if(view == null){
            return;
        }
        if(mDuration == null){
            mDuration = 500;
        }
        //In case something small like 5 is set, this will make it at least a 10th of a second
        if(mDuration < 100){
            mDuration = 100;
        }

        //If the animation is not null, move forward with the animation
        if(tech != null){
            YoYo.with(tech).duration(mDuration).playOn(view);
        }
    }
    /**
     * Animates a holder within a recyclerview
     * @param holder The holder to animate
     * @param duration Duration in milliseconds. IF null passed, will default to 500 milliseconds
     * @param techniques The technique to use in the animation
     */
    public static void animateMyView(RecyclerView.ViewHolder holder,
                                     Integer duration, Techniques techniques){
        if(holder == null){
            return;
        }
        if(techniques == null){
            return;
        }
        if(duration == null){
            duration = 500;
        }
        if(duration < 100 || duration > 10000){
            duration = 100;
        }
        try{
            YoYo.with(techniques).duration(duration).playOn(holder.itemView);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
