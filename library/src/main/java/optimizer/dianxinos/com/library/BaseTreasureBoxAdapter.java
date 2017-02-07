package optimizer.dianxinos.com.library;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;

public abstract class BaseTreasureBoxAdapter<T> extends AbstractTreasureAdapter {
    private Context mContext;
    private LinkedList<T> mItems = new LinkedList<>();
    private int mColumnCount;

    public BaseTreasureBoxAdapter(Context context, List<T> items, int columnCount) {
        mContext = context;
        mColumnCount = columnCount;
        init(items);
    }

    private void init(List<T> items) {
        addAllStableId(items);
        this.mItems.addAll(items);
    }


    public void set(List<T> items) {
        clear();
        init(items);
        notifyDataSetChanged();
    }

    public void clear() {
        clearStableIdMap();
        mItems.clear();
        notifyDataSetChanged();
    }

    public void add(T item) {
        addStableId(item);
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void add(List<T> items) {
        addAllStableId(items);
        this.mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void add(int position, T item) {
        addStableId(item);
        mItems.add(position, item);
        notifyDataSetChanged();
    }

    public void remove(T item) {
        mItems.remove(item);
        removeStableID(item);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getColumnCount() {
        return mColumnCount;
    }

    public void setColumnCount(int columnCount) {
        this.mColumnCount = columnCount;
        notifyDataSetChanged();
    }

    @Override
    public void reorderItems(int originalPosition, int newPosition) {
        if (newPosition < getCount()) {
            TreasureBoxUtils.reorder(mItems, originalPosition, newPosition);
            notifyDataSetChanged();
        }
    }

    @Override
    public void swapItems(int originalPosition, int newPosition) {
        if (newPosition < getCount()) {
            TreasureBoxUtils.swap(mItems, originalPosition, newPosition);
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean canReorder(int position) {
        return true;
    }

    public LinkedList<T> getItems() {
        return (LinkedList<T>) mItems;
    }

    protected Context getContext() {
        return mContext;
    }
}
