package com.mymobkit.common;

import android.database.Cursor;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class ValidationUtils {

    /**
     * Tag for output.
     */
    private static final String TAG = makeLogTag(ValidationUtils.class);

    /**
     * API level.
     */
    private static int iApi = -1;

    /**
     * Valid email pattern.
     */
    private static final Pattern emailPattern = android.util.Patterns.EMAIL_ADDRESS;

    /**
     * Parse {@link Integer}.
     *
     * @param value    value a {@link String}
     * @param defValue default value
     * @return parsed {@link Integer}
     */
    public static int parseInt(final String value, final int defValue) {
        int ret = defValue;
        if (value == null || value.length() == 0) {
            return ret;
        }
        try {
            ret = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGW(TAG, "parseInt(" + value + ") failed: " + e.toString());
        }
        return ret;
    }

    /**
     * Is API supported?
     *
     * @param api Android's API version
     * @return true, if api <= current API version
     */
    @SuppressWarnings("deprecation")
    public static boolean isApi(final int api) {
        if (iApi < 0) {
            iApi = Integer.parseInt(Build.VERSION.SDK);
        }
        return iApi >= api;
    }

    public static Long getLong(Cursor c, String col) {
        return c.getLong(c.getColumnIndex(col));
    }

    public static int getInt(Cursor c, String col) {
        return c.getInt(c.getColumnIndex(col));
    }

    public static String getString(Cursor c, String col) {
        return c.getString(c.getColumnIndex(col));
    }

    public static boolean getBoolean(String val) {
        return "1".equals(val) || "true".equalsIgnoreCase(val);
    }

    public static Date getDateMilliSeconds(Cursor c, String col) {
        return new Date(Long.parseLong(getString(c, col)));
    }

    public static String encodeSql(String s) {
        return s.replaceAll("'", "''");
    }

    public static String getString(final String value) {
        if (TextUtils.isEmpty(value)) return "";
        return value;
    }

    public static String getStringValue(final String key, final Map<String, String> values, String defaultValue) {
        if (values.containsKey(key.toLowerCase())) {
            return values.get(key.toLowerCase());
        }
        return defaultValue;
    }

    public static int getIntegerValue(final String key, final Map<String, String> values, final int defaultValue) {
        try {
            if (values.containsKey(key.toLowerCase())) {
                return Integer.valueOf(values.get(key.toLowerCase()));
            }
            return defaultValue;
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static boolean getBooleanValue(final String key, final Map<String, String> values, final boolean defaultValue) {
        try {
            if (values.containsKey(key.toLowerCase())) {
                final String value = values.get(key.toLowerCase());
                if (!TextUtils.isEmpty(value) && ("0".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))) {
                    return false;
                } else {
                    return true;
                }
            }
            return defaultValue;
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static long getLongValue(final String key, final Map<String, String> values, final long defaultValue) {
        try {
            if (values.containsKey(key.toLowerCase())) {
                return Long.valueOf(values.get(key.toLowerCase()));
            }
            return defaultValue;
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static long getLong(String value, long defaultValue) {
        try {
            return Long.valueOf(value);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static boolean isNumberString(final String text) {
        if (TextUtils.isEmpty(text)) return false;
        return (text.matches("[0-9]+") && text.length() > 0);
    }

    public static boolean isValidEmail(String number) {
        Matcher matcher = emailPattern.matcher(number);
        return matcher.matches();
    }

    public static boolean isValidSmsOrEmail(String number) {
        return PhoneNumberUtils.isWellFormedSmsAddress(number) || isValidEmail(number);
    }

    public static int byteArrayToIntLittleEndian(byte[] bytes, int offset) {
        return
                (bytes[offset + 3] & 0xff) << 24 |
                        (bytes[offset + 2] & 0xff) << 16 |
                        (bytes[offset + 1] & 0xff) << 8 |
                        (bytes[offset] & 0xff);
    }

    /**
     * Convert a list to string array.
     *
     * @param values
     * @return
     */
    public static String[] toStringArray(final List<String> values) {
        String[] s = new String[values.size()];
        s = values.toArray(s);
        return s;
    }
}
