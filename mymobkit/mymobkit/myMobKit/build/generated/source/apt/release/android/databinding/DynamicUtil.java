package android.databinding;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
public class DynamicUtil {
    @SuppressWarnings("deprecation")
    public static int getColorFromResource(final android.view.View view, final int resourceId) {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            return view.getContext().getColor(resourceId);
        }
        return view.getResources().getColor(resourceId);
    }
    @SuppressWarnings("deprecation")
    public static android.content.res.ColorStateList getColorStateListFromResource(final android.view.View view, final int resourceId) {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            return view.getContext().getColorStateList(resourceId);
        }
        return view.getResources().getColorStateList(resourceId);
    }
    @SuppressWarnings("deprecation")
    public static android.graphics.drawable.Drawable getDrawableFromResource(final android.view.View view, final int resourceId) {
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            return view.getContext().getDrawable(resourceId);
        }
        return view.getResources().getDrawable(resourceId);
    }
    public static boolean parse(String str, boolean fallback) {
        if (str == null) {
            return fallback;
        }
        return Boolean.parseBoolean(str);
    }
    public static byte parse(String str, byte fallback) {
        try {
            return Byte.parseByte(str);
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }
    public static short parse(String str, short fallback) {
        try {
            return Short.parseShort(str);
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }
    public static int parse(String str, int fallback) {
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }
    public static long parse(String str, long fallback) {
        try {
            return Long.parseLong(str);
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }
    public static float parse(String str, float fallback) {
        try {
            return Float.parseFloat(str);
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }
    public static double parse(String str, double fallback) {
        try {
            return Double.parseDouble(str);
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }
    public static char parse(String str, char fallback) {
        if (str == null || str.isEmpty()) {
            return fallback;
        }
        return str.charAt(0);
    }
}