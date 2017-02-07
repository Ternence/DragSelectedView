package optimizer.dianxinos.com.library;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by zhangtengyuan on 16/1/24.
 */
public abstract class TreasureViewHolder {
    public LinearLayout item;
    public TextView title;
    public ImageView image;

    abstract public void build(int pos);
}
