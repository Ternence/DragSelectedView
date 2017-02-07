package optimizer.dianxinos.com.library;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

public class ViewsApiCompat {
    /**
     * Compat for {@link View#setBackground(Drawable)} and {@link View#setBackgroundDrawable(Drawable)}
     * @param view
     * @param drawable
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    /**
     * Compat for {@link ImageView#setAlpha(int)} and {@link ImageView#setImageAlpha(int)}
     * @param view
     * @param alpha (0 ~ 255)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setImageAlpha(ImageView view, int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setImageAlpha(alpha);
        } else {
            view.setAlpha(alpha);
        }
    }
}
