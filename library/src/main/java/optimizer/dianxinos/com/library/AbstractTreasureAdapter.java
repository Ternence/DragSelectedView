package optimizer.dianxinos.com.library;

import java.util.HashMap;
import java.util.List;

import android.widget.BaseAdapter;

/**
 * Abstract adapter for with sable items id;
 */

public abstract class AbstractTreasureAdapter extends BaseAdapter implements TreasureBoxGridAdapterInterface {
    public static final int INVALID_ID = -1;

    protected int nextStableId = 0;

    public HashMap<Object, Integer> mIdMap = new HashMap<Object, Integer>();

    /**
     * Adapter must have stable id
     *
     * @returnH
     */
    @Override
    public final boolean hasStableIds() {
        return false;
    }

    /**
     * creates stable id for object
     *
     * @param item
     */
    protected void addStableId(Object item) {
        mIdMap.put(item, nextStableId++);
    }

    /**
     * creates stable id for object
     *
     * @param item
     */
    protected void setStableId(Object item, int pos) {
        mIdMap.put(item, pos);
    }

    /**
     * create stable ids for list
     *
     * @param items
     */
    protected void addAllStableId(List<?> items) {
        for (Object item : items) {
            addStableId(item);
        }
    }

    /**
     * get id for position
     *
     * @param position
     * @return
     */
    @Override
    public final long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        Object item = getItem(position);
        return mIdMap.get(item);
    }

    /**
     * clear stable id map
     * should called when clear adapter data;
     */
    protected void clearStableIdMap() {
        mIdMap.clear();
    }

    /**
     * remove stable id for <code>item</code>. Should called on remove data item from adapter
     *
     * @param item
     */
    protected void removeStableID(Object item) {
        mIdMap.remove(item);
    }
}
