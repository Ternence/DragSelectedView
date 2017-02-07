package optimizer.dianxinos.com.library;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import optimizer.dianxinos.com.library.utils.SystemPropertiesCompat;

public class TreasureBoxUtils {
    private static final String TAG = "TreasureBoxUtils";

    public static void reorder(List list, int indexFrom, int indexTwo) {
        Object obj = list.remove(indexFrom);
        list.add(indexTwo, obj);
    }

    public static void swap(List list, int firstIndex, int secondIndex) {
        Object firstObject = list.get(firstIndex);
        Object secondObject = list.get(secondIndex);
        list.set(firstIndex, secondObject);
        list.set(secondIndex, firstObject);
    }

    public static float getViewX(View view) {
        return (float) Math.abs(0.5 * (view.getLeft() + view.getRight()));
    }

    public static float getViewY(View view) {
        return (float) Math.abs(0.5 * (view.getTop() + view.getBottom()));
    }

    public static boolean isPostHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isPreHoneycomb() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 是否是5.0以及以上版本OS
     * @return
     */
    public static boolean isPostLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * OS Version <= 4.2
     *
     * @return
     */
    public static boolean isPreIceCream() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean isPreLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 获取屏幕宽度
     *
     * @param mActivity
     * @return
     */
    public static int getScreenWidth(Activity mActivity) {
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param mActivity
     * @return
     */
    public static int getScreenHeight(Activity mActivity) {
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    /**
     * 获取拖动小方格的宽度
     * @param mActivity
     * @return
     */
    public static int getHoverViewWidth(Activity mActivity) {
        return getScreenWidth(mActivity) / 3;
    }

    /**
     * 判断是否是魅族系统
     * @return
     */
     public static boolean isFlyme() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            return isMeizuFlymeOSPreLollipop();
        } else {
            return isMeizuFlymeOSPostLollipop();
        }
    }

    /**
     * 5.1以及之后版本判断是否是魅族手机
     * @return
     */
    private static boolean isMeizuFlymeOSPostLollipop() {
        try {
            Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * 5.1之前版本判断是否是魅族手机
     *
     * @return
     */
    public static boolean isMeizuFlymeOSPreLollipop() {
        String meizuFlymeOSFlag = SystemPropertiesCompat.getString("ro.build.display.id", "");
        if (TextUtils.isEmpty(meizuFlymeOSFlag)) {
            return false;
        } else if (meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")) {
            return true;
        } else {
            return false;
        }
    }
}
