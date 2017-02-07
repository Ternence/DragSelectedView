package optimizer.dianxinos.com.library.utils;

import java.text.DecimalFormat;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

public class StringUtils {
    /**
     * Format bytes count in proper suffix.
     * @param size Bytes count in bytes (B)
     * @return Formatted string in "B" or "KB" or "MB" or "GB"
     */
    public static String formatBytes(long size) {
        return formatBytes(size, true);
    }

    /**
     * format bytes to suitable value and unit. For example, show them on different views.
     * @param size Bytes count in bytes (B)
     * @return array with two element. the first is value,the second is unit.
     */
    public static String[] formatBytesSplitUnit(long size, boolean hasByteChar) {
        String[] strs = new String[2];

        // TODO: try the following two methods
        // Formatter.formatShortFileSize(context, size);
        // Formatter.formatFileSize(context, size);
        DecimalFormat formatter = new DecimalFormat("#0.0");
        if (size >= 1024 * 1024 * 1024l) {
            // in GB
            strs[0] = formatter.format(size / (1024 * 1024 * 1024f));
            strs[1] = "G" + (hasByteChar? "B" : "");
        } else if (size >= 1024 * 1024l) {
            // in MB
            strs[0] = formatter.format(size / (1024 * 1024f));
            strs[1] =  "M" + (hasByteChar? "B" : "");
        } else if (size >= 1024) {
            // in KB
            strs[0] = formatter.format(size / 1024f);
            strs[1] =  "K" + (hasByteChar? "B" : "");
        } else {
            strs[0] = String.valueOf(size);
            strs[1] = hasByteChar? "B" : "";
        }
        return strs;
    }


    public static String formatBytes(long size, boolean hasByte) {
        String[] strs = formatBytesSplitUnit(size, hasByte);
        return strs[0] + strs[1];
    }

    /**
     * Format bytes count in proper suffix.
     * @param size Bytes count in KB
     * @return Formatted string in "KB" or "MB" or "GB"
     */
    public static String formatBytesInKB(long size) {
        return formatBytes(size * 1024, true);
    }

    public static String formatBytesInK(long size) {
        return formatBytes(size * 1024, false);
    }

    public static String formatFloat(float f, int pos) {
        float p = 1f;
        StringBuilder format = new StringBuilder("#0");
        for (int i = 0; i < pos; i++) {
            if (i == 0) {
                format.append('.');
            }
            p *= 10f;
            format.append('0');
        }
        f = Math.round(f * p) / p;
        DecimalFormat formatter = new DecimalFormat(format.toString());
        return formatter.format(f);
    }

    public static String formatDouble(double f, int pos) {
        double p = 1d;
        StringBuilder format = new StringBuilder("#0");
        for (int i = 0; i < pos; i++) {
            if (i == 0) {
                format.append('.');
            }
            p *= 10d;
            format.append('0');
        }
        f = Math.round(f * p) / p;
        DecimalFormat formatter = new DecimalFormat(format.toString());
        return formatter.format(f);
    }

    /**
     * Extract the decimal positive integer from specified string.
     * @param str The string to extract.
     * @return
     */
    public static int extractPositiveInteger(String str, int defValue) {
        final int N = str.length();
        int index = 0;

        // Search the first digit character
        while (index < N) {
            char curCh = str.charAt(index);
            if (curCh >= '0' && curCh <= '9') {
                int start = index;
                // Search the first non-digit character
                index++;
                while (index < N) {
                    curCh = str.charAt(index);
                    if (curCh >= '0' && curCh <= '9') {
                        index++;
                    } else {
                        break;
                    }
                }
                //May be more than the Integer.MAX_VALUE
                try {
                    String numberStr = str.substring(start, index);
                    return Integer.parseInt(numberStr);
                } catch (NumberFormatException e) {
                    return defValue;
                }
            }
            index++;
        }
        return defValue;
    }

    /**
     * Highlight part of a string.
     */
    public static SpannableStringBuilder highlight(String text, int start, int end, int color) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static int parseInt(String s, int def) {
        try {
            int i = Integer.parseInt(s);
            return i;
        } catch (Exception e) {
            return def;
        }
    }

    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    public static long parseLong(String value, long def) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return def;
        }
    }

    public static float parseFloat(String s) {
        if (s != null) {
            try {
                float i = Float.parseFloat(s);
                return i;
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public static boolean isEmpty(String s) {
        if (s != null) {
            for (int i = 0, count = s.length(); i < count; i++) {
                char c = s.charAt(i);
                if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                    return false;
                }
            }
        }
        return true;
    }

    public static String trimAppName(String str) {
        int length = str.length();
        int index = 0;
        while (index < length && (str.charAt(index) <= '\u0020' || str.charAt(index) == '\u00a0'))
            index++;
        if (index > 0)
            return str.substring(index);
        return str;
    }

    /**
     * 检测是否有emoji字符
     */
    public static boolean containsEmoji(String source) {
        if (TextUtils.isEmpty(source)) {
            return false;
        }
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isEmojiCharacter(codePoint)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    /**
     * 过滤emoji字符
     */
    public static String filterEmoji(String source) {
        if (TextUtils.isEmpty(source)) {
            return source;
        }
        StringBuilder buf = new StringBuilder();
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) {
                buf.append(codePoint);
            }
        }
        return buf.toString();
    }
}
