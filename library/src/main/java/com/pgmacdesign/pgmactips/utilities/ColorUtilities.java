package com.pgmacdesign.pgmactips.utilities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class ColorUtilities {
	
	/**
	 * Determine if color is light or dark. Values pulled from these links:
	 * 1) https://en.wikipedia.org/wiki/Luma_%28video%29
	 * 2) https://stackoverflow.com/a/24261119/2480714
	 *
	 * @param color color to parse
	 * @return boolean, if true, color is dark, if false, it's a light color
	 */
	public static boolean isColorDark(int color) {
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
	
	/**
	 * Parse a color (Handles the parsing errors)
	 *
	 * @param color String color to parse
	 * @return If not parseable or an error occurrs, it will send back -100.
	 */
	public static int parseMyColor(String color) {
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
		return String.format("#%02X%02X%02X", r, g, b);
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
}
