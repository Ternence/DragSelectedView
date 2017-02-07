package optimizer.dianxinos.com.library;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 百宝箱Adapter
 */
public class TreasureBoxAdapter extends BaseTreasureBoxAdapter {
    private final int mHeight;
    private final int mBottomMargin;

    public TreasureBoxAdapter(Context context, List<TreasureBean> items, int columnCount,
                              int bottomMargin) {
        super(context, items, columnCount);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mHeight = wm.getDefaultDisplay().getWidth() / context.getResources()
                .getInteger(R.integer.column_count);
        mBottomMargin = bottomMargin;
    }

    /**
     * 低端机(4.x 以下)会出现使用viewHolder在过于复杂的业务出现item的显示错误
     * 表现：第一个 item 展示一半的情况，和 item views 闪动现象
     * 原因：
     * 1、动态适配高度有关(重复绘制问题)
     * 2、在卫士作为功能入口的，不涉及过多多屏幕刷新前提下，停用viewholder解决(position <= 2 设置前三个BEAN
     * 停用viewholder)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.treasure_box_grid_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(position + "");
//        setLayoutParams(position, convertView);

        return convertView;
    }

//    public void setLayoutParams(int position, View convertView) {
//        AbsListView.LayoutParams params =
//                new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight);
//        if (position < getColumnCount()) {
//            params.height = mHeight - mBottomMargin;
//            if ((TreasureConstants.FILTER_HOME_TOP_CENTER) == position) {
//                ViewsApiCompat.setBackground(convertView, getContext().getResources()
//                        .getDrawable(R.drawable.treasure_box_home_bottom_center_selector));
//            } else {
//                ViewsApiCompat.setBackground(convertView, getContext().getResources()
//                        .getDrawable(R.drawable.treasure_box_home_bottom_selector));
//            }
//        }
//        convertView.setLayoutParams(params);
//    }

    public class ViewHolder {
        public LinearLayout item;
        public TextView title;
        public ImageView image;

        public ViewHolder(View view) {
            item = (LinearLayout) view.findViewById(R.id.treasure_item);
            title = (TextView) view.findViewById(R.id.item_title);
            image = (ImageView) view.findViewById(R.id.item_img);
        }
    }

}