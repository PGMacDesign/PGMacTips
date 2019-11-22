package com.pgmacdesign.pgmactips.utilities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class ColorUtilities {
	
	//region Static Vars
	private static final String HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
	/**
	 * Magic Number pulled from: https://stackoverflow.com/a/40964456/2480714
	 */
	private static final int MATERIAL_DESIGN_GUIDELINES_DARKEN_AMOUNT = 12;
	
	//region Color Palette Static Values
	/**
	 * For more info, see these links:
	 * 1) http://mcg.mbitson.com/#!?mcgpalette0=%233f51b5#%2F
	 * 2) https://material.io/design/color/#tools-for-picking-colors
	 * 3) https://www.materialui.co/colors
	 */
	public static final String MATERIAL_PALETTE_LIGHT_50 = "50";
	public static final int MATERIAL_PALETTE_LIGHT_50_VALUE = 52;
	public static final String MATERIAL_PALETTE_LIGHT_100 = "100";
	public static final int MATERIAL_PALETTE_LIGHT_100_VALUE = 37;
	public static final String MATERIAL_PALETTE_LIGHT_200 = "200";
	public static final int MATERIAL_PALETTE_LIGHT_200_VALUE = 26;
	public static final String MATERIAL_PALETTE_LIGHT_300 = "300";
	public static final int MATERIAL_PALETTE_LIGHT_300_VALUE = 12;
	public static final String MATERIAL_PALETTE_LIGHT_400 = "400";
	public static final int MATERIAL_PALETTE_LIGHT_400_VALUE = 6;
	public static final String MATERIAL_PALETTE_NEUTRAL_500 = "500";
	public static final int MATERIAL_PALETTE_NEUTRAL_500_VALUE = 0;
	public static final String MATERIAL_PALETTE_DARK_600 = "600";
	public static final int MATERIAL_PALETTE_DARK_600_VALUE = 6;
	public static final String MATERIAL_PALETTE_DARK_700 = "700";
	public static final int MATERIAL_PALETTE_DARK_700_VALUE = 12;
	public static final String MATERIAL_PALETTE_DARK_800 = "800";
	public static final int MATERIAL_PALETTE_DARK_800_VALUE = 18;
	public static final String MATERIAL_PALETTE_DARK_900 = "900";
	public static final int MATERIAL_PALETTE_DARK_900_VALUE = 24;
	//endregion
	//endregion
	
	//region Static Getters
	
	/**
	 * Static method to retrieve the color palette values as defined in the Material Design
	 * Guidelines standard. For more info, see:
	 * 1) http://mcg.mbitson.com/#!?mcgpalette0=%233f51b5#%2F
	 * 2) https://material.io/design/color/#tools-for-picking-colors
	 * 3) https://www.materialui.co/colors
	 * @return
	 */
	public static final Map<String, Integer> getColorPaletteStaticValues(){
		Map<String, Integer> map = new HashMap<>();
		map.put(MATERIAL_PALETTE_LIGHT_50, MATERIAL_PALETTE_LIGHT_50_VALUE);
		map.put(MATERIAL_PALETTE_LIGHT_100, MATERIAL_PALETTE_LIGHT_100_VALUE);
		map.put(MATERIAL_PALETTE_LIGHT_200, MATERIAL_PALETTE_LIGHT_200_VALUE);
		map.put(MATERIAL_PALETTE_LIGHT_300, MATERIAL_PALETTE_LIGHT_300_VALUE);
		map.put(MATERIAL_PALETTE_LIGHT_400, MATERIAL_PALETTE_LIGHT_400_VALUE);
		map.put(MATERIAL_PALETTE_NEUTRAL_500, MATERIAL_PALETTE_NEUTRAL_500_VALUE);
		map.put(MATERIAL_PALETTE_DARK_600, MATERIAL_PALETTE_DARK_600_VALUE);
		map.put(MATERIAL_PALETTE_DARK_700, MATERIAL_PALETTE_DARK_700_VALUE);
		map.put(MATERIAL_PALETTE_DARK_800, MATERIAL_PALETTE_DARK_800_VALUE);
		map.put(MATERIAL_PALETTE_DARK_900, MATERIAL_PALETTE_DARK_900_VALUE);
		return map;
	}
	
	//endregion
	
	//region Misc Color Utilities
	
	/**
	 * Checks if the passed in String is a valid hex color String.
	 * Do not pass a hex color with alpha, remove the alpha value first and then send!
	 * @param hexColor Hex color, IE #FFFFFF or 000000 or #FAFAFA or A12222
	 * @return true if it is a valid hex color, false if it is not
	 */
	public static boolean isValidHexColor(String hexColor){
		if(StringUtilities.isNullOrEmpty(hexColor)){
			return false;
		}
		if(hexColor.length() < 6 || hexColor.length() > 7){
			return false;
		}
		if(!hexColor.startsWith("#")){
			hexColor = "#" + hexColor;
		}
		if(hexColor.length() > 7){
			return false;
		}
		Pattern pattern = Pattern.compile(HEX_PATTERN);
		try {
			return pattern.matcher(hexColor).matches();
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Determine if color is light or dark. Values pulled from these links:
	 * 1) https://en.wikipedia.org/wiki/Luma_%28video%29
	 * 2) https://stackoverflow.com/a/24261119/2480714
	 *
	 * @param color color to parse
	 * @return boolean, if true, color is dark, if false, it's a light color
	 */
	public static boolean isColorDark(@ColorInt int color) {
		double darkness = 1 - (0.299 * Color.red(color)
				+ 0.587 * Color.green(color)
				+ 0.114 * Color.blue(color)) / 255;
		if (darkness < 0.5) {
			return false; // It's a light color
		} else {
			return true; // It's a dark color
		}
	}
	
	/**
	 * Determine if color is light or dark. Values pulled from these links:
	 * 1) https://en.wikipedia.org/wiki/Luma_%28video%29
	 * 2) https://stackoverflow.com/a/24261119/2480714
	 *
	 * @param red Red component of the color
	 * @param green Green component of the color
	 * @param blue Blue component of the color
	 * @return boolean, if true, color is dark, if false, it's a light color
	 */
	public static boolean isColorDark(int red, int green, int blue) {
		double darkness = 1 - (0.299 * red + 0.587 * green + 0.114 * blue) / 255;
		if (darkness < 0.5) {
			return false; // It's a light color
		} else {
			return true; // It's a dark color
		}
	}
	
	/**
	 * Determine if color is light or dark. Values pulled from these links:
	 * 1) https://en.wikipedia.org/wiki/Luma_%28video%29
	 * 2) https://stackoverflow.com/a/24261119/2480714
	 *
	 * @param color color to parse
	 * @return boolean, if true, color is dark, if false, it's a light color
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static boolean isColorDark(Color color) {
		double darkness = 1 - (0.299 * color.red() + 0.587 * color.green()
				+ 0.114 * color.blue()) / 255;
		if (darkness < 0.5) {
			return false; // It's a light color
		} else {
			return true; // It's a dark color
		}
	}
	
	/**
	 * Determine if color is light or dark. Values pulled from these links:
	 * 1) https://en.wikipedia.org/wiki/Luma_%28video%29
	 * 2) https://stackoverflow.com/a/24261119/2480714
	 *
	 * @param hexColor hexColor to parse
	 * @return boolean, if true, color is dark, if false, it's a light color
	 */
	public static boolean isColorDark(@NonNull String hexColor) {
		if(StringUtilities.isNullOrEmpty(hexColor)){
			return false;
		} else {
			return isColorDark(Color.parseColor(hexColor));
		}
	}
	
	/**
	 * Parse a color (Handles the parsing errors)
	 *
	 * @param color String color to parse
	 * @return If not parsable or an error occurs, it will send back -100.
	 */
	public static int parseMyColor(String color) {
		try {
			return Color.parseColor(color);
		} catch (Exception e){
			e.printStackTrace();
			return -100;
		}
	}
	
	/**
	 * Parse a color (Handles the parsing errors)
	 *
	 * @param color String color to parse
	 * @return If not parsable or an error occurs, it will send back -100.
	 */
	@Deprecated
	public static int parseMyColorOLD(String color) {
		if (StringUtilities.isNullOrEmpty(color)) {
			return -100;
		}
		try {
			int x = Color.parseColor(color);
			return x;
		} catch (Exception e) {
			return -100;
		}
	}
	
	//endregion
	
	//region Gradient Drawables
	
	/**
	 * Build and return a Gradient Drawable
	 *
	 * @param colorDirection Direction of gradient. If null, will default to left --> right
	 *                       {@link android.graphics.drawable.GradientDrawable.Orientation}
	 * @param colors         Array of color ints. Cannot be null or empty
	 * @return {@link GradientDrawable}
	 */
	public static GradientDrawable buildGradientDrawable(GradientDrawable.Orientation colorDirection,
	                                                     @NonNull int[] colors) {
		if (colors.length <= 0) {
			return null;
		}
		if (colorDirection == null) {
			colorDirection = GradientDrawable.Orientation.LEFT_RIGHT;
		}
		GradientDrawable gradient = new GradientDrawable(
				colorDirection, colors);
		return gradient;
	}
	
	/**
	 * Build and return a Gradient Drawable
	 * Overloaded to allow for hex values to be passed
	 *
	 * @param colorDirection Direction of gradient. If null, will default to left --> right
	 *                       {@link android.graphics.drawable.GradientDrawable.Orientation}
	 * @param colors         Array of String color Hex values . Cannot be null or empty
	 * @return {@link GradientDrawable}
	 */
	public static GradientDrawable buildGradientDrawable(GradientDrawable.Orientation colorDirection,
	                                                     @NonNull String[] colors) {
		if (colors.length <= 0) {
			return null;
		}
		if (colorDirection == null) {
			colorDirection = GradientDrawable.Orientation.LEFT_RIGHT;
		}
		int[] arr = new int[colors.length];
		for (int i = 0; i < colors.length; i++) {
			arr[i] = parseMyColor(colors[i]);
		}
		return buildGradientDrawable(colorDirection, arr);
	}
	
	//endregion
	
	//region Material Design Utilities
	
	/**
	 * Create the status bar color as per the material guidelines
	 * 	 * {@link #MATERIAL_DESIGN_GUIDELINES_DARKEN_AMOUNT}
	 * If it cannot create it, it will return the original color
	 * @param color Color to darken
	 * @return darkened color, but will return -100 if parsing fails on Hex String
	 */
	public static int createStatusBarColor(@ColorInt int color){
		try {
			return darkenColor(color, 12);
		} catch (Exception e){
			e.printStackTrace();
			return color;
		}
	}
	
	/**
	 * Create the status bar color as per the material guidelines
	 * {@link #MATERIAL_DESIGN_GUIDELINES_DARKEN_AMOUNT}
	 * If it cannot create it, it will return the original color
	 * @param hexColor
	 * @return darkened color, but will return -100 if parsing fails on Hex String
	 */
	public static int createStatusBarColor(@NonNull String hexColor){
		try {
			return darkenColor(Color.parseColor(hexColor), MATERIAL_DESIGN_GUIDELINES_DARKEN_AMOUNT);
		} catch (Exception e){
			e.printStackTrace();
			try {
				return Color.parseColor(hexColor);
			} catch (Exception ee){
				return -100;
			}
		}
	}
	
	/**
	 * Create the status bar color as per the material guidelines
	 * {@link #MATERIAL_DESIGN_GUIDELINES_DARKEN_AMOUNT}
	 * If it cannot create it, it will return the original color
	 * @param red R value
	 * @param green G Values
	 * @param blue B Value
	 * @return darkened color, but will return -100 if parsing fails
	 */
	public static int createStatusBarColor(int red, int green, int blue){
		try {
			int color = convertRGBToColor(red, green, blue);
			return darkenColor(color, MATERIAL_DESIGN_GUIDELINES_DARKEN_AMOUNT);
		} catch (Exception e){
			return -100;
		}
	}

	//region Full Color Palette Utilities
	
	/**
	 * Creates a full Material Design color palette for the color value passed.
	 * @param color color value to adjust
	 * @return A Map of String Integer values that matches a full Material Design color palette.
	 *         Will always return a Hashmap, though it may be empty if values cannot be parsed.
	 */
	public static Map<String, Integer> createFullColorPalette(@ColorInt int color){
		Map<String, Integer> toReturn = new HashMap<>();
		try {
			toReturn.put(ColorUtilities.MATERIAL_PALETTE_LIGHT_50, ColorUtilities.lightenColor(
					color, ColorUtilities.MATERIAL_PALETTE_LIGHT_50_VALUE));
			toReturn.put(ColorUtilities.MATERIAL_PALETTE_LIGHT_100, ColorUtilities.lightenColor(
					color, ColorUtilities.MATERIAL_PALETTE_LIGHT_100_VALUE));
			toReturn.put(ColorUtilities.MATERIAL_PALETTE_LIGHT_200, ColorUtilities.lightenColor(
					color, ColorUtilities.MATERIAL_PALETTE_LIGHT_200_VALUE));
			toReturn.put(ColorUtilities.MATERIAL_PALETTE_LIGHT_300, ColorUtilities.lightenColor(
					color, ColorUtilities.MATERIAL_PALETTE_LIGHT_300_VALUE));
			toReturn.put(ColorUtilities.MATERIAL_PALETTE_LIGHT_400, ColorUtilities.lightenColor(
					color, ColorUtilities.MATERIAL_PALETTE_LIGHT_400_VALUE));
			toReturn.put(ColorUtilities.MATERIAL_PALETTE_NEUTRAL_500, color);
			toReturn.put(ColorUtilities.MATERIAL_PALETTE_DARK_600, ColorUtilities.darkenColor(
					color, ColorUtilities.MATERIAL_PALETTE_DARK_600_VALUE));
			toReturn.put(ColorUtilities.MATERIAL_PALETTE_DARK_700, ColorUtilities.darkenColor(
					color, ColorUtilities.MATERIAL_PALETTE_DARK_700_VALUE));
			toReturn.put(ColorUtilities.MATERIAL_PALETTE_DARK_800, ColorUtilities.darkenColor(
					color, ColorUtilities.MATERIAL_PALETTE_DARK_800_VALUE));
			toReturn.put(ColorUtilities.MATERIAL_PALETTE_DARK_900, ColorUtilities.darkenColor(
					color, ColorUtilities.MATERIAL_PALETTE_DARK_900_VALUE));
		} catch (Exception e){
			e.printStackTrace();
		}
		return toReturn;
	}
	
	/**
	 * Creates a full Material Design color palette for the color value passed.
	 * @param hexColor Hex Color String to adjust
	 * @return A Map of String Integer values that matches a full Material Design color palette.
	 *         Will always return a Hashmap, though it may be empty if values cannot be parsed.
	 */
	public static Map<String, Integer> createFullColorPalette(String hexColor){
		Map<String, Integer> toReturn = new HashMap<>();
		try {
			return createFullColorPalette(Color.parseColor(hexColor));
		} catch (Exception e){
			return toReturn;
		}
	}
	
	/**
	 * Creates a full Material Design color palette for the color value passed.
	 * @param red Red Value
	 * @param green Green Value
	 * @param blue Blue value
 	 * @return A Map of String Integer values that matches a full Material Design color palette.
	 *         Will always return a Hashmap, though it may be empty if values cannot be parsed.
	 */
	public static Map<String, Integer> createFullColorPalette(int red, int green, int blue){
		Map<String, Integer> toReturn = new HashMap<>();
		try {
			return createFullColorPalette(convertRGBToColor(red, green, blue));
		} catch (Exception e){
			return toReturn;
		}
	}
	
	//endregion
	
	//endregion
	
	//region Color Lighten and Darkening
	
	/**
	 * Darkens a given color.
	 * Credit goes to: https://stackoverflow.com/a/40964456/2480714
	 * @param color base color
	 * @param amount amount between 0 and 100
	 * @return darken color
	 */
	public static int darkenColor(@ColorInt int color, int amount) {
		// TODO: 2019-11-14 need to adjust code here as it is shading down colors like white (000000) to red (FF000000). May need range checking
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		float[] hsl = hsv2hsl(hsv);
		hsl[2] -= amount / 100f;
		if (hsl[2] < 0)
			hsl[2] = 0f;
		hsv = hsl2hsv(hsl);
		return Color.HSVToColor(hsv);
	}
	
	/**
	 * lightens a given color
	 * Credit goes to: https://stackoverflow.com/a/40964456/2480714
	 * @param color base color
	 * @param amount amount between 0 and 100
	 * @return lightened
	 */
	public static int lightenColor(@ColorInt int color, int amount) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		float[] hsl = hsv2hsl(hsv);
		hsl[2] += amount / 100f;
		if (hsl[2] > 1)
			hsl[2] = 1f;
		hsv = hsl2hsv(hsl);
		return Color.HSVToColor(hsv);
	}
	
	/**
	 * Converts HSV (Hue, Saturation, Value) color to HSL (Hue, Saturation, Lightness)
	 * Credit goes to xpansive
	 * https://gist.github.com/xpansive/1337890
	 * @param hsv HSV color array
	 * @return hsl
	 */
	private static float[] hsv2hsl(float[] hsv) {
		float hue = hsv[0];
		float sat = hsv[1];
		float val = hsv[2];
		
		//Saturation is very different between the two color spaces
		//If (2-sat)*val < 1 set it to sat*val/((2-sat)*val)
		//Otherwise sat*val/(2-(2-sat)*val)
		//Conditional is not operating with hue, it is reassigned!
		// sat*val/((hue=(2-sat)*val)<1?hue:2-hue)
		float nhue = (2f - sat) * val;
		float nsat = sat * val / (nhue < 1f ? nhue : 2f - nhue);
		if (nsat > 1f)
			nsat = 1f;
		
		return new float[]{
				//[hue, saturation, lightness]
				//Range should be between 0 - 1
				hue, //Hue stays the same
				
				// check nhue and nsat logic
				nsat,
				
				nhue / 2f //Lightness is (2-sat)*val/2
				//See reassignment of hue above
		};
	}
	
	/**
	 * Reverses hsv2hsl
	 * Credit goes to xpansive
	 * https://gist.github.com/xpansive/1337890
	 * @param hsl HSL color array
	 * @return hsv color array
	 */
	private static float[] hsl2hsv(float[] hsl) {
		float hue = hsl[0];
		float sat = hsl[1];
		float light = hsl[2];
		
		sat *= light < .5 ? light : 1 - light;
		
		return new float[]{
				//[hue, saturation, value]
				//Range should be between 0 - 1
				
				hue, //Hue stays the same
				2f * sat / (light + sat), //Saturation
				light + sat //Value
		};
	}
	//endregion
	
	//region RGB, Color, and Hex Converters
	
	/**
	 * Convert a color to a hex value
	 * @param color Color to convert
	 * @return The hex value of the color, can return null
	 */
	public static String convertColorToHex(@ColorInt int color){
		try {
			return String.format("#%06X", (0xFFFFFF & color));
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Convert a color to a hex value while retaining the alpha values
	 * @param color Color to convert
	 * @return The hex value of the color, can return null
	 */
	public static String convertColorToHexWithAlpha(@ColorInt int color){
		try {
			return ("#" + Integer.toHexString(color).toUpperCase());
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Convert RDB colors to a 6 hex String.
	 * IE, (111, 111, 111) becomes #6F6F6F
	 * Credit for code - https://stackoverflow.com/a/3607942/2480714
	 *
	 * @param r Red value (Ranged from 0 - 255)
	 * @param g Green value (Ranged from 0 - 255)
	 * @param b Blue value (Ranged from 0 - 255)
	 * @return int color value. Will return -100 if parsing values fail
	 */
	public static int convertRGBToColor(@IntRange(from = 0, to = 255) int r,
	                                     @IntRange(from = 0, to = 255) int g,
	                                     @IntRange(from = 0, to = 255) int b) {
		if ((r < 0 || r > 255) || (g < 0 || g > 255) || (b < 0 || b > 255)) {
			return -100;
		}
		String hex;
		try {
			hex = convertRGBToHex(r, g, b);
			return Color.parseColor(hex);
		} catch (Exception e){
			try {
				String r1 = Integer.toHexString(r);
				String g1 = Integer.toHexString(g);
				String b1 = Integer.toHexString(b);
				hex = ("#" + r1 + g1 + b1);
				return Color.parseColor(hex);
			} catch (Exception e1){
				e1.printStackTrace();
				return -100;
			}
		}
	}
	
	/**
	 * Convert RDB colors to a 6 hex String.
	 * IE, (111, 111, 111) becomes #6F6F6F
	 * Credit for code - https://stackoverflow.com/a/3607942/2480714
	 *
	 * @param r Red value (Ranged from 0 - 255)
	 * @param g Green value (Ranged from 0 - 255)
	 * @param b Blue value (Ranged from 0 - 255)
	 * @return Hex Color String with no alpha. If values are incorrect, returns null
	 */
	public static String convertRGBToHex(@IntRange(from = 0, to = 255) int r,
	                                     @IntRange(from = 0, to = 255) int g,
	                                     @IntRange(from = 0, to = 255) int b) {
		if ((r < 0 || r > 255) || (g < 0 || g > 255) || (b < 0 || b > 255)) {
			return null;
		}
		try {
			return String.format("#%02X%02X%02X", r, g, b);
		} catch (Exception e){
			try {
				String r1 = Integer.toHexString(r);
				String g1 = Integer.toHexString(g);
				String b1 = Integer.toHexString(b);
				return ("#" + r1 + g1 + b1);
			} catch (Exception e1){
				e1.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * Convert RDB colors to an 8 digit hex String.
	 * IE, (111, 111, 111, 1.0) becomes #FF6F6F6F
	 * IE, (111, 111, 111, 0.5) becomes #806F6F6F
	 * IE, (111, 111, 111, 0.0) becomes #006F6F6F
	 * Credit for code - https://stackoverflow.com/a/3607942/2480714 && https://stackoverflow.com/a/11019879/2480714
	 *
	 * @param r               Red value (Ranged from 0 - 255)
	 * @param g               Green value (Ranged from 0 - 255)
	 * @param b               Blue value (Ranged from 0 - 255)
	 * @param alphaPercentage float value (transparency) (Ranged from 0.0 to 1.0)
	 * @return Hex Color String with alpha. If values are incorrect, returns null
	 */
	public static String convertRGBToHexWithAlpha(@IntRange(from = 0, to = 255) int r,
	                                              @IntRange(from = 0, to = 255) int g,
	                                              @IntRange(from = 0, to = 255) int b,
	                                              @FloatRange(from = 0.0, to = 1.0) float alphaPercentage) {
		if ((r < 0 || r > 255) || (g < 0 || g > 255) || (b < 0 || b > 255) || (alphaPercentage < 0 || alphaPercentage > 1)) {
			return null;
		}
		int alpha = (int) (alphaPercentage * 255F);
		return String.format("#%02X%02X%02X%02X", alpha, r, g, b);
	}
	
	/**
	 * Convert a Hex String to RGB Values.
	 * Credit for code - https://stackoverflow.com/a/4129692/2480714
	 * IE, #6F6F6F becomes [111, 111, 111]
	 *
	 * @param hexColor String hex color (6 in length. If 8 in length,
	 *                 will ignore first 2 characters as they are likely alpha)
	 * @return int array of types: [red, green, blue]. Note that if it fails or the String
	 * passed is invalid for any reason, it will return an array of size 3 filled with zeros.
	 */
	public static int[] convertHexToRGB(@NonNull String hexColor) {
		int[] toReturn = new int[]{0, 0, 0};
		if (StringUtilities.isNullOrEmpty(hexColor)) {
			return toReturn;
		}
		hexColor = hexColor.replace("#", "");
		if (hexColor.length() == 8) {
			hexColor = hexColor.substring(2);
		}
		if (hexColor.length() != 6) {
			return toReturn;
		}
		try {
			int r = NumberUtilities.getInt(Integer.valueOf(hexColor.substring(0, 2), 16));
			int g = NumberUtilities.getInt(Integer.valueOf(hexColor.substring(2, 4), 16));
			int b = NumberUtilities.getInt(Integer.valueOf(hexColor.substring(4), 16));
			toReturn[0] = r;
			toReturn[1] = g;
			toReturn[2] = b;
		} catch (Exception e) {}
		return toReturn;
	}
 
	/**
	 * Overloaded for simpler naming
	 * Will return -100 if the value fails to parse
	 */
	public static int convertHexToColor(@NonNull String hexColor) {
		try {
			return Color.parseColor(hexColor);
		} catch (Exception e){
			e.printStackTrace();
			return -100;
		}
	}
 
	//endregion
	
    //region Color List
    /*
    Color lists below:

    <color name="Semi_Transparent1">#20111111</color>
    <color name="Semi_Transparent2">#30111111</color>
    <color name="Semi_Transparent3">#40111111</color>
    <color name="Semi_Transparent4">#50111111</color>
    <color name="Semi_Transparent5">#69111111</color>
    <color name="Semi_Transparent6">#79111111</color>
    <color name="Semi_Transparent7">#89111111</color>
    <color name="Semi_Transparent8">#99111111</color>
    <color name="Semi_Transparent9">#A9111111</color>



    <color name="Transparent">#00000000</color>
    <color name="White">#FFFFFF</color>
    <color name="Ivory">#FFFFF0</color>
    <color name="LightYellow">#FFFFE0</color>
    <color name="Yellow">#FFFF00</color>
    <color name="Snow">#FFFAFA</color>
    <color name="FloralWhite">#FFFAF0</color>
    <color name="LemonChiffon">#FFFACD</color>
    <color name="Cornsilk">#FFF8DC</color>
    <color name="Seashell">#FFF5EE</color>
    <color name="LavenderBlush">#FFF0F5</color>
    <color name="PapayaWhip">#FFEFD5</color>
    <color name="BlanchedAlmond">#FFEBCD</color>
    <color name="MistyRose">#FFE4E1</color>
    <color name="Bisque">#FFE4C4</color>
    <color name="Moccasin">#FFE4B5</color>
    <color name="NavajoWhite">#FFDEAD</color>
    <color name="PeachPuff">#FFDAB9</color>
    <color name="Gold">#FFD700</color>
    <color name="Pink">#FFC0CB</color>
    <color name="LightPink">#FFB6C1</color>
    <color name="Orange">#FFA500</color>
    <color name="LightSalmon">#FFA07A</color>
    <color name="DarkOrange">#FF8C00</color>
    <color name="Coral">#FF7F50</color>
    <color name="HotPink">#FF69B4</color>
    <color name="Tomato">#FF6347</color>
    <color name="OrangeRed">#FF4500</color>
    <color name="DeepPink">#FF1493</color>
    <color name="Fuchsia">#FF00FF</color>
    <color name="Magenta">#FF00FF</color>
    <color name="Red">#FF0000</color>
    <color name="OldLace">#FDF5E6</color>
    <color name="LightGoldenrodYellow">#FAFAD2</color>
    <color name="Linen">#FAF0E6</color>
    <color name="AntiqueWhite">#FAEBD7</color>
    <color name="Salmon">#FA8072</color>
    <color name="GhostWhite">#F8F8FF</color>
    <color name="MintCream">#F5FFFA</color>
    <color name="WhiteSmoke">#F5F5F5</color>
    <color name="Beige">#F5F5DC</color>
    <color name="Wheat">#F5DEB3</color>
    <color name="SandyBrown">#F4A460</color>
    <color name="Azure">#F0FFFF</color>
    <color name="Honeydew">#F0FFF0</color>
    <color name="AliceBlue">#F0F8FF</color>
    <color name="Khaki">#F0E68C</color>
    <color name="LightCoral">#F08080</color>
    <color name="PaleGoldenrod">#EEE8AA</color>
    <color name="Violet">#EE82EE</color>
    <color name="DarkSalmon">#E9967A</color>
    <color name="Lavender">#E6E6FA</color>
    <color name="LightCyan">#E0FFFF</color>
    <color name="BurlyWood">#DEB887</color>
    <color name="Plum">#DDA0DD</color>
    <color name="Gainsboro">#DCDCDC</color>
    <color name="Crimson">#DC143C</color>
    <color name="PaleVioletRed">#DB7093</color>
    <color name="Goldenrod">#DAA520</color>
    <color name="Orchid">#DA70D6</color>
    <color name="Thistle">#D8BFD8</color>
    <color name="LightGrey">#D3D3D3</color>
    <color name="LightGreyFaded">#77D3D3D3</color>
    <color name="Tan">#D2B48C</color>
    <color name="Chocolate">#D2691E</color>
    <color name="Peru">#CD853F</color>
    <color name="IndianRed">#CD5C5C</color>
    <color name="MediumVioletRed">#C71585</color>
    <color name="Silver">#C0C0C0</color>
    <color name="DarkKhaki">#BDB76B</color>
    <color name="RosyBrown">#BC8F8F</color>
    <color name="MediumOrchid">#BA55D3</color>
    <color name="DarkGoldenrod">#B8860B</color>
    <color name="FireBrick">#B22222</color>
    <color name="PowderBlue">#B0E0E6</color>
    <color name="LightSteelBlue">#B0C4DE</color>
    <color name="PaleTurquoise">#AFEEEE</color>
    <color name="GreenYellow">#ADFF2F</color>
    <color name="LightBlue">#ADD8E6</color>
    <color name="DarkGray">#A9A9A9</color>
    <color name="Brown">#A52A2A</color>
    <color name="Sienna">#A0522D</color>
    <color name="YellowGreen">#9ACD32</color>
    <color name="DarkOrchid">#9932CC</color>
    <color name="PaleGreen">#98FB98</color>
    <color name="DarkViolet">#9400D3</color>
    <color name="MediumPurple">#9370DB</color>
    <color name="LightGreen">#90EE90</color>
    <color name="DarkSeaGreen">#8FBC8F</color>
    <color name="SaddleBrown">#8B4513</color>
    <color name="DarkMagenta">#8B008B</color>
    <color name="DarkRed">#8B0000</color>
    <color name="BlueViolet">#8A2BE2</color>
    <color name="LightSkyBlue">#87CEFA</color>
    <color name="SkyBlue">#87CEEB</color>
    <color name="Gray">#808080</color>
    <color name="Olive">#808000</color>
    <color name="Purple">#800080</color>
    <color name="Maroon">#800000</color>
    <color name="Aquamarine">#7FFFD4</color>
    <color name="Chartreuse">#7FFF00</color>
    <color name="LawnGreen">#7CFC00</color>
    <color name="MediumSlateBlue">#7B68EE</color>
    <color name="LightSlateGray">#778899</color>
    <color name="SlateGray">#708090</color>
    <color name="OliveDrab">#6B8E23</color>
    <color name="SlateBlue">#6A5ACD</color>
    <color name="DimGray">#696969</color>
    <color name="MediumAquamarine">#66CDAA</color>
    <color name="CornflowerBlue">#6495ED</color>
    <color name="CadetBlue">#5F9EA0</color>
    <color name="DarkOliveGreen">#556B2F</color>
    <color name="Indigo">#4B0082</color>
    <color name="MediumTurquoise">#48D1CC</color>
    <color name="DarkSlateBlue">#483D8B</color>
    <color name="SteelBlue">#4682B4</color>
    <color name="RoyalBlue">#4169E1</color>
    <color name="Turquoise">#40E0D0</color>
    <color name="MediumSeaGreen">#3CB371</color>
    <color name="LimeGreen">#32CD32</color>
    <color name="DarkSlateGray">#2F4F4F</color>
    <color name="SeaGreen">#2E8B57</color>
    <color name="ForestGreen">#228B22</color>
    <color name="LightSeaGreen">#20B2AA</color>
    <color name="DodgerBlue">#1E90FF</color>
    <color name="MidnightBlue">#191970</color>
    <color name="Aqua">#00FFFF</color>
    <color name="Cyan">#00FFFF</color>
    <color name="SpringGreen">#00FF7F</color>
    <color name="Lime">#00FF00</color>
    <color name="MediumSpringGreen">#00FA9A</color>
    <color name="DarkTurquoise">#00CED1</color>
    <color name="DeepSkyBlue">#00BFFF</color>
    <color name="DarkCyan">#008B8B</color>
    <color name="Teal">#008080</color>
    <color name="Green">#008000</color>
    <color name="DarkGreen">#006400</color>
    <color name="Blue">#0000FF</color>
    <color name="MediumBlue">#0000CD</color>
    <color name="DarkBlue">#00008B</color>
    <color name="Navy">#000080</color>
    <color name="Black">#000000</color>

    <color name="white">#FFFFFF</color>
    <color name="black">#000000</color>
    <color name="yellow">#FFFF00</color>
    <color name="fuchsia">#FF00FF</color>
    <color name="red">#FF0000</color>
    <color name="silver">#C0C0C0</color>
    <color name="gray">#808080</color>
    <color name="olive">#808000</color>
    <color name="purple">#800080</color>
    <color name="maroon">#800000</color>
    <color name="aqua">#00FFFF</color>
    <color name="lime">#00FF00</color>
    <color name="teal">#008080</color>
    <color name="green">#008000</color>
    <color name="blue">#0000FF</color>
    <color name="navy">#000080</color>
     */
    
    //endregion
	
	//region Color Transparency
    /*
    Color Transparency --> Hex Codes. Originates from: https://gist.github.com/lopspower/03fb1cc0ac9f32ef38f4
		100% — FF
		99% — FC
		98% — FA
		97% — F7
		96% — F5
		95% — F2
		94% — F0
		93% — ED
		92% — EB
		91% — E8
		90% — E6
		89% — E3
		88% — E0
		87% — DE
		86% — DB
		85% — D9
		84% — D6
		83% — D4
		82% — D1
		81% — CF
		80% — CC
		79% — C9
		78% — C7
		77% — C4
		76% — C2
		75% — BF
		74% — BD
		73% — BA
		72% — B8
		71% — B5
		70% — B3
		69% — B0
		68% — AD
		67% — AB
		66% — A8
		65% — A6
		64% — A3
		63% — A1
		62% — 9E
		61% — 9C
		60% — 99
		59% — 96
		58% — 94
		57% — 91
		56% — 8F
		55% — 8C
		54% — 8A
		53% — 87
		52% — 85
		51% — 82
		50% — 80
		49% — 7D
		48% — 7A
		47% — 78
		46% — 75
		45% — 73
		44% — 70
		43% — 6E
		42% — 6B
		41% — 69
		40% — 66
		39% — 63
		38% — 61
		37% — 5E
		36% — 5C
		35% — 59
		34% — 57
		33% — 54
		32% — 52
		31% — 4F
		30% — 4D
		29% — 4A
		28% — 47
		27% — 45
		26% — 42
		25% — 40
		24% — 3D
		23% — 3B
		22% — 38
		21% — 36
		20% — 33
		19% — 30
		18% — 2E
		17% — 2B
		16% — 29
		15% — 26
		14% — 24
		13% — 21
		12% — 1F
		11% — 1C
		10% — 1A
		9% — 17
		8% — 14
		7% — 12
		6% — 0F
		5% — 0D
		4% — 0A
		3% — 08
		2% — 05
		1% — 03
		0% — 00
     */
    
    //endregion
}
