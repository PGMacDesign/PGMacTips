package com.pgmacdesign.pgmacutilities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;

/**
 * This class is used for getting system icons and coloring them (IE a back button)
 * Created by pmacdowell on 8/12/2016.
 */
public class SystemDrawableUtilities {

    /**
     * Overloaded method for passing in String colors
     * @param context Context used to reference the resources
     * @param colorToSet String of a color to parse and then set
     * @return Drawable to be set. Use null to set to default color
     */
    public static Drawable getToolbarBackArrow(Context context, String colorToSet){
        int colorToSetInt = ColorUtilities.parseMyColor(colorToSet);
        if(colorToSetInt == -100){
            colorToSetInt = ColorUtilities.parseMyColor(PGMacUtilitiesConstants.COLOR_BLACK);
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
            upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);

        }
        if(upArrow == null){
            upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
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
    private static View getSearchPlate(android.support.v7.widget.SearchView searchView){
        View view = (View)searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        return view;
    }

    /**
     * Sets the searchplate via getting it first by passing searchview and then setting it via the
     * passed int (IE a drawable formatted like this: R.drawable.textfield_searchview_holo_light )
     * @param searchView The searchview to be altering
     * @param drawableToSet The drawable to set
     */
    private static void setSearchPlate(android.support.v7.widget.SearchView searchView, int drawableToSet){
        View searchPlate = getSearchPlate(searchView);
        searchPlate.setBackgroundResource(drawableToSet);
    }

    /**
     * Gets the searchview's standard close icon that is used by the system. References the searchview
     * @param searchView Searchview being altered
     * @return An ImageView which can be altered
     */
    private static ImageView getSearchCloseIcon(android.support.v7.widget.SearchView searchView){
        ImageView searchCloseIcon = (ImageView)searchView.findViewById(
                android.support.v7.appcompat.R.id.search_close_btn);
        return searchCloseIcon;
    }

    /**
     * Sets the close icon via getting it first by passing searchview and then setting it via the
     * passed int (IE a drawable formatted like this: R.drawable.textfield_searchview_holo_light )
     * @param searchView The searchview to be altering
     * @param drawableToSet The drawable to set (Image resource)
     */
    private static void setSearchCloseIcon(android.support.v7.widget.SearchView searchView, int drawableToSet){
        ImageView iv = getSearchCloseIcon(searchView);
        iv.setImageResource(drawableToSet);
    }

    /**
     * Gets the searchview's standard voice search icon that is used by the system. References the searchview
     * @param searchView Searchview being altered
     * @return A View which can be altered
     */
    private static ImageView getSearchVoiceIcon(android.support.v7.widget.SearchView searchView){
        ImageView voiceIcon = (ImageView)searchView.findViewById(
                android.support.v7.appcompat.R.id.search_voice_btn);
        return voiceIcon;
    }

    /**
     * Sets the searchview voice icon via getting it first by passing searchview and then setting it via the
     * passed int (IE a drawable formatted like this: R.drawable.textfield_searchview_holo_light )
     * @param searchView The searchview to be altering
     * @param drawableToSet The drawable to set
     */
    private static void setSearchVoiceIcon(android.support.v7.widget.SearchView searchView, int drawableToSet){
        ImageView voiceIcon = getSearchVoiceIcon(searchView);
        voiceIcon.setImageResource(drawableToSet);
    }

    /**
     * Gets the searchview's standars search magnifying glass icon that is used by the system. References the searchview
     * @param searchView Searchview being altered
     * @return A View which can be altered
     */
    public static ImageView getSearchMagnifierIcon(android.support.v7.widget.SearchView searchView){
        ImageView searchIcon = (ImageView)searchView.findViewById(
                android.support.v7.appcompat.R.id.search_mag_icon);
        return searchIcon;
    }

    /**
     * Sets the searchview's magnifier icon via getting it first by passing searchview and then setting it via the
     * passed int (IE a drawable formatted like this: R.drawable.textfield_searchview_holo_light )
     * @param searchView The searchview to be altering
     * @param resIdToSet The resource id to set
     */
    public static void setSearchMagnifierIcon(android.support.v7.widget.SearchView searchView,
                                              int resIdToSet){
        ImageView searchIcon = getSearchMagnifierIcon(searchView);
        searchIcon.setImageResource(resIdToSet);
    }
}
