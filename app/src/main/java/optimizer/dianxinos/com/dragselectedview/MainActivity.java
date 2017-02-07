package optimizer.dianxinos.com.dragselectedview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import optimizer.dianxinos.com.library.TreasureBean;
import optimizer.dianxinos.com.library.TreasureBoxAdapter;
import optimizer.dianxinos.com.library.TreasureBoxView;

public class MainActivity extends AppCompatActivity {

    private TreasureBoxView mTreasureBoxView;
    private TreasureBoxAdapter mTreasureBoxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTreasureBox();
    }

    public void initTreasureBox() {
        mTreasureBoxView = (TreasureBoxView) findViewById(R.id.treasure_box);
        mTreasureBoxAdapter = new TreasureBoxAdapter(this,
                getTags(),
                getResources().getInteger(R.integer.column_count), 0);
        mTreasureBoxView.setAdapter(mTreasureBoxAdapter);
        mTreasureBoxView.setOnDropListener(new TreasureBoxView.OnDropListener() {
            @Override
            public void onActionDrop() {
                mTreasureBoxView.stopEditMode();
            }
        });
        mTreasureBoxView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {
                mTreasureBoxView.startEditMode(position);
                return true;
            }
        });
        mTreasureBoxView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        mTreasureBoxView
                .setOnEditModeChangeListener(new TreasureBoxView.OnEditModeChangeListener() {
                    @Override
                    public void onEditModeChanged(boolean inEditMode) {
                    }
                });
        mTreasureBoxView.setOnDragListener(new TreasureBoxView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {

            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {

            }
        });
    }

    private List<TreasureBean> getTags() {
        ArrayList<TreasureBean> list = new ArrayList<>();
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        list.add(new TreasureBean());
        return list;
    }
}
