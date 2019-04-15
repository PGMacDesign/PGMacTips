package com.pgmacdesign.pgmactips.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.pgmacdesign.pgmactips.R;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;

import androidx.core.content.ContextCompat;

/**
 * This class is used for getting system icons and coloring them (IE a back button)
 * Created by pmacdowell on 8/12/2016.
 */
public class SystemDrawableUtilities {

    /**
     * Get the hamburger icon and return it (Used for setting colors to it)
     * @param activity activity referencing
     * @return
     */
    public static View getToolbarHamburgerButton(Activity activity){
        try {
            View view = ((View) activity.getWindow().getDecorView().findViewById(android.R.id.home));
            return view;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Overloaded method for passing in String colors
     * @param context Context used to reference the resources
     * @param colorToSet String of a color to parse and then set
     * @return Drawable to be set. Use null to set to default color
     */
    public static Drawable getToolbarBackArrow(Context context, String colorToSet){
        int colorToSetInt = ColorUtilities.parseMyColor(colorToSet);
        if(colorToSetInt == -100){
            colorToSetInt = ColorUtilities.parseMyColor(PGMacTipsConstants.COLOR_BLACK);
        }
        return (SystemDrawableUtilities.getToolbarBackArrow(context, colorToSetInt));
    }

    /**
     * Gets the toolbar's standard back-arrow icon, sets the color to whatever is passed in, and returns it
     * @param context Context used to reference the resources
     * @param colorToSet int of a color to set
     * @return Drawable to be set. Use -1 to set to default color
     */
    public static Drawable getToolbarBackArrow(Context context, int colorToSet){
        //final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        Drawable upArrow;
        try {
            upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        } catch (Exception e){
            e.printStackTrace();
            upArrow = null;
        }
        if(upArrow == null){
            return null;
        }
        if(colorToSet != -2){
            upArrow.setColorFilter(colorToSet, PorterDuff.Mode.SRC_ATOP);
        }
        return upArrow;
    }

    /**
     * Gets the searchview's standard plate that is used by the system. References the searchview
     * @param searchView Searchview being altered
     * @return A View which can be altered
     */
    private static View getSearchPlate(androidx.appcompat.widget.SearchView searchView){
        View view = (View)searchView.findViewById(androidx.appcompat.R.id.search_plate);
        return view;
    }

    /**
     * Sets the searchplate via getting it first by passing searchview and then setting it via the
     * passed int (IE a drawable formatted like this: R.drawable.textfield_searchview_holo_light )
     * @param searchView The searchview to be altering
     * @param drawableToSet The drawable to set
     */
    private static void setSearchPlate(androidx.appcompat.widget.SearchView searchView, int drawableToSet){
        View searchPlate = getSearchPlate(searchView);
        searchPlate.setBackgroundResource(drawableToSet);
    }

    /**
     * Gets the searchview's standard close icon that is used by the system. References the searchview
     * @param searchView Searchview being altered
     * @return An ImageView which can be altered
     */
    private static ImageView getSearchCloseIcon(androidx.appcompat.widget.SearchView searchView){
        ImageView searchCloseIcon = (ImageView)searchView.findViewById(
                androidx.appcompat.R.id.search_close_btn);
        return searchCloseIcon;
    }

    /**
     * Sets the close icon via getting it first by passing searchview and then setting it via the
     * passed int (IE a drawable formatted like this: R.drawable.textfield_searchview_holo_light )
     * @param searchView The searchview to be altering
     * @param drawableToSet The drawable to set (Image resource)
     */
    private static void setSearchCloseIcon(androidx.appcompat.widget.SearchView searchView, int drawableToSet){
        ImageView iv = getSearchCloseIcon(searchView);
        iv.setImageResource(drawableToSet);
    }

    /**
     * Gets the searchview's standard voice search icon that is used by the system. References the searchview
     * @param searchView Searchview being altered
     * @return A View which can be altered
     */
    private static ImageView getSearchVoiceIcon(androidx.appcompat.widget.SearchView searchView){
        ImageView voiceIcon = (ImageView)searchView.findViewById(
                androidx.appcompat.R.id.search_voice_btn);
        return voiceIcon;
    }

    /**
     * Sets the searchview voice icon via getting it first by passing searchview and then setting it via the
     * passed int (IE a drawable formatted like this: R.drawable.textfield_searchview_holo_light )
     * @param searchView The searchview to be altering
     * @param drawableToSet The drawable to set
     */
    private static void setSearchVoiceIcon(androidx.appcompat.widget.SearchView searchView, int drawableToSet){
        ImageView voiceIcon = getSearchVoiceIcon(searchView);
        voiceIcon.setImageResource(drawableToSet);
    }

    /**
     * Gets the searchview's standars search magnifying glass icon that is used by the system. References the searchview
     * @param searchView Searchview being altered
     * @return A View which can be altered
     */
    public static ImageView getSearchMagnifierIcon(androidx.appcompat.widget.SearchView searchView){
        ImageView searchIcon = (ImageView)searchView.findViewById(
                androidx.appcompat.R.id.search_mag_icon);
        return searchIcon;
    }

    /**
     * Sets the searchview's magnifier icon via getting it first by passing searchview and then setting it via the
     * passed int (IE a drawable formatted like this: R.drawable.textfield_searchview_holo_light )
     * @param searchView The searchview to be altering
     * @param resIdToSet The resource id to set
     */
    public static void setSearchMagnifierIcon(androidx.appcompat.widget.SearchView searchView,
                                              int resIdToSet){
        ImageView searchIcon = getSearchMagnifierIcon(searchView);
        searchIcon.setImageResource(resIdToSet);
    }
}
