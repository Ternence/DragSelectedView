package optimizer.dianxinos.com.library;

import java.util.LinkedList;


public interface TreasureBoxGridAdapterInterface {
    void reorderItems(int originalPosition, int newPosition);

    void swapItems(int originalPosition, int newPosition);

//    void add(int position, Object item);
//
//    void remove(Object item);

    int getColumnCount();

    boolean canReorder(int position);

    int getCount();

//    LinkedList<Object> getItems();
}
