package optimizer.dianxinos.com.library;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

public class TreasureBoxView extends GridView {
    private static final String TAG = "TreasureBoxView";

    private final int MOVE_DURATION = 300;
    private final int BOUND_DURATION = MOVE_DURATION / 2;
    private final int SMOOTH_SCROLL_AMOUNT_AT_EDGE = 8;

    private float MoveEventX;
    private float MoveEventY;
    private float mHoverViewWidth;
    private float mHoverViewHeight;

    private BitmapDrawable mHoverCell;
    private View mMobileView;
    private Rect mHoverCellCurrentBounds;
    private Rect mHoverCellOriginalBounds;
    private List<Long> idList = new ArrayList<Long>();

    private int mTotalOffsetY = 0;
    private int mTotalOffsetX = 0;
    private int mDownX = -1;
    private int mDownY = -1;
    private int mLastEventY = -1;
    private int mLastEventX = -1;
    private int mOverlapIfSwitchStraightLine;   //区分相接和对角的替换
    private int mActivePointerId = TreasureConstants.INVALID_ID;
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    private int mSmoothScrollAmountAtEdge = 0;
    private int mCurSlideStatus = TreasureConstants.ORIENTATION_SLIDE_DEFAULT; //当前slideView上滑的方向
    private long mMobileItemId = TreasureConstants.INVALID_ID;

    private boolean mCellIsMobile = false;
    private boolean mIsMobileScrolling;
    private boolean mIsWaitingForScrollFinish = false;
    private boolean mIsEditMode = false;
    private boolean mHoverAnimation;
    private boolean mReorderAnimation;
    private boolean mIsEditModeEnabled = true;

    private OnScrollListener mUserScrollListener;
    private OnDropListener mDropListener;
    private OnDragListener mDragListener;
    private OnEditModeChangeListener mEditModeChangeListener;
    private OnItemClickListener mUserItemClickListener;
    private OnSelectedItemBitmapCreationListener mSelectedItemBitmapCreationListener;

    public TreasureBoxView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public TreasureBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public TreasureBoxView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public void init(Context context) {
        super.setOnScrollListener(mScrollListener);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mSmoothScrollAmountAtEdge = (int) (SMOOTH_SCROLL_AMOUNT_AT_EDGE * metrics.density + 0.5f);
        mOverlapIfSwitchStraightLine = getResources().getDimensionPixelSize(R.dimen.treasure_box_dgv_overlap_if_switch_straight_line);
        mHoverViewWidth = TreasureBoxUtils.getHoverViewWidth((Activity) context);
        mHoverViewHeight = mHoverViewWidth;
    }

    @Override
    public void setOnScrollListener(OnScrollListener scrollListener) {
        this.mUserScrollListener = scrollListener;
    }

    public void setOnDropListener(OnDropListener dropListener) {
        this.mDropListener = dropListener;
    }

    public void setOnDragListener(OnDragListener dragListener) {
        this.mDragListener = dragListener;
    }

    /**
     * 开启编辑模式
     * <p/>
     * 编辑模式可进行拖动交换操作
     * {@link OnItemClickListener}
     * {@link OnItemLongClickListener}
     */
    public void startEditMode(int position) {
        if (!mIsEditModeEnabled) {
            return;
        }
        requestDisallowInterceptTouchEvent(true);
        if (position != -1) {
            startDragAtPosition(position, null);
        }
        mIsEditMode = true;
        if (mEditModeChangeListener != null)
            mEditModeChangeListener.onEditModeChanged(true);
    }

    /**
     * 停止编辑模式
     */
    public void stopEditMode() {
        mIsEditMode = false;
        requestDisallowInterceptTouchEvent(false);
        if (mEditModeChangeListener != null)
            mEditModeChangeListener.onEditModeChanged(false);
        //结束指引动画
        if (getAdapterInterface() == null) {
            return;
        }
    }

    public boolean isEditModeEnabled() {
        return mIsEditModeEnabled;
    }

    public void setEditModeEnabled(boolean enabled) {
        this.mIsEditModeEnabled = enabled;
    }

    public void setOnEditModeChangeListener(OnEditModeChangeListener editModeChangeListener) {
        this.mEditModeChangeListener = editModeChangeListener;
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mUserItemClickListener = listener;
        super.setOnItemClickListener(mLocalItemClickListener);
    }

    /**
     * 编辑中生成拖动 bitmap 监听
     *
     * @param selectedItemBitmapCreationListener
     */
    public void setOnSelectedItemBitmapCreationListener(OnSelectedItemBitmapCreationListener selectedItemBitmapCreationListener) {
        this.mSelectedItemBitmapCreationListener = selectedItemBitmapCreationListener;
    }

    /**
     * 普通交换
     *
     * @param deltaY
     * @param deltaX
     * @param originalPosition
     * @param targetPosition
     */
    private void reorderElements(int deltaY, int deltaX, int originalPosition, int targetPosition) {
        //数据交换
        if (originalPosition > TreasureConstants.TOP_RIGHT_VIEW && targetPosition > TreasureConstants.TOP_RIGHT_VIEW) {
            getAdapterInterface().reorderItems(originalPosition, targetPosition);
            if (mDragListener != null) {
                mDragListener.onDragPositionsChanged(originalPosition, targetPosition);
            }
            //视图动画
            mDownY = mLastEventY;
            mDownX = mLastEventX;

            updateNeighborViewsForId(mMobileItemId);

            SwitchCellAnimator switchCellAnimator;
            if (TreasureBoxUtils.isPostHoneycomb()) {
                switchCellAnimator = new NormalSwitchCellAnimator(deltaX, deltaY);
            } else {
                switchCellAnimator = new PreHoneyCellAnimator(deltaX, deltaY);
            }
            switchCellAnimator.animateSwitchCell(originalPosition, targetPosition);
        }
    }

    private int getColumnCount() {
        return getAdapterInterface().getColumnCount();
    }

    private TreasureBoxGridAdapterInterface getAdapterInterface() {
        return ((TreasureBoxGridAdapterInterface) getAdapter());
    }

    /**
     * 获得正在拖动的 view 的 拖动image
     *
     * @param v     被编辑的 view
     * @param point 位置
     * @param position  为了良好视觉感受,前三个顶部item的hover宽度为1.0倍,其余1.1倍hover
     * @return
     */
    private BitmapDrawable getAndAddHoverView(View v, Point point, int position) {
        int w = (int) (mHoverViewWidth * 1.1);
        int h = (int) (mHoverViewHeight * (position <= 2 ? 1.0 : 1.1));
        int top;
        int left;
        if (point == null) {
            //第一次 touch
            top = (v.getTop() + (v.getBottom() - v.getTop()) / 2 - h / 2);
            left = (v.getLeft() + (v.getRight() - v.getLeft()) / 2 - w / 2);
        } else {
            top = (point.y - h / 2);
            left = (point.x - w / 2);
        }

        Bitmap b = getBitmapFromView(v);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), b);

        mHoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
        mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);

        drawable.setBounds(mHoverCellCurrentBounds);

        return drawable;
    }

    /**
     * 获得正在编辑 view 的screenshot
     */
    private Bitmap getBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }


    /**
     * 更新 views
     *
     * @param itemId
     */
    private void updateNeighborViewsForId(long itemId) {
        idList.clear();
        int draggedPos = getPositionForID(itemId);
        for (int pos = getFirstVisiblePosition(); pos <= getLastVisiblePosition(); pos++) {
            if (draggedPos != pos && getAdapterInterface().canReorder(pos)) {
                idList.add(getId(pos));
            }
        }
    }

    public int getPositionForID(long itemId) {
        View v = getViewForId(itemId);
        if (v == null) {
            return -1;
        } else {
            return getPositionForView(v);
        }
    }

    public View getViewForId(long itemId) {
        int firstVisiblePosition = getFirstVisiblePosition();
        ListAdapter adapter = getAdapter();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            int position = firstVisiblePosition + i;
            long id = adapter.getItemId(position);
            if (id == itemId) {
                return v;
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                mActivePointerId = event.getPointerId(0);
                if (mIsEditMode && isEnabled()) {
                    layoutChildren();
                    int position = pointToPosition(mDownX, mDownY);
                    startDragAtPosition(position, new Point(mDownX, mDownY));
                } else if (!isEnabled()) {
                    return false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                MoveEventX = event.getRawX();
                MoveEventY = event.getRawY();
                if (mActivePointerId == TreasureConstants.INVALID_ID) {
                    break;
                }
                int pointerIndex = event.findPointerIndex(mActivePointerId);

                mLastEventY = (int) event.getY(pointerIndex);
                mLastEventX = (int) event.getX(pointerIndex);
                int deltaY = mLastEventY - mDownY;
                int deltaX = mLastEventX - mDownX;

                if (mCellIsMobile) {
                    mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left + deltaX + mTotalOffsetX,
                            mHoverCellOriginalBounds.top + deltaY + mTotalOffsetY);
                    mHoverCell.setBounds(mHoverCellCurrentBounds);
                    invalidate();
                    handleCellSwitch();
                    mIsMobileScrolling = false;
                    handleMobileCellScroll();
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
                touchEventsEnded();
                if (mHoverCell != null) {
                    if (mDropListener != null) {
                        mDropListener.onActionDrop();
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                touchEventsCancelled();
                if (mHoverCell != null) {
                    if (mDropListener != null) {
                        mDropListener.onActionDrop();
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                //多点触摸处理
                pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                        MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    touchEventsEnded();
                    stopEditMode();
                } else {
                    return true;
                }
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void startDragAtPosition(int position, Point touchPoint) {
        mTotalOffsetY = 0;
        mTotalOffsetX = 0;
        int itemNum = position - getFirstVisiblePosition();
        View selectedView = getChildAt(itemNum);
        if (selectedView != null) {
            mMobileItemId = getAdapter().getItemId(position);
            if (mSelectedItemBitmapCreationListener != null)
                mSelectedItemBitmapCreationListener.onPreSelectedItemBitmapCreation(selectedView, position, mMobileItemId);
            mHoverCell = getAndAddHoverView(selectedView, touchPoint, position);
            //检查顶部是否有empty item,以及获取他们position
            //若有，empty 显示guide 虚线
            //stop edit检查如果有 empty 关闭 guide
            if (mSelectedItemBitmapCreationListener != null)
                mSelectedItemBitmapCreationListener.onPostSelectedItemBitmapCreation(selectedView, position, mMobileItemId);
            mCellIsMobile = true;
            updateNeighborViewsForId(mMobileItemId);
            if (mDragListener != null) {
                mDragListener.onDragStarted(position);
            }
        }
    }



    private void handleMobileCellScroll() {
        mIsMobileScrolling = handleMobileCellScroll(mHoverCellCurrentBounds);
    }

    public boolean handleMobileCellScroll(Rect r) {
        int offset = computeVerticalScrollOffset();
        int height = getHeight();
        int extent = computeVerticalScrollExtent();
        int range = computeVerticalScrollRange();
        int hoverViewTop = r.top;
        int hoverHeight = r.height();

        if (hoverViewTop <= 0 && offset > 0) {
            smoothScrollBy(-mSmoothScrollAmountAtEdge, 0);
            return true;
        }

        if (hoverViewTop + hoverHeight >= height && (offset + extent) < range) {
            smoothScrollBy(mSmoothScrollAmountAtEdge, 0);
            return true;
        }

        return false;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    private void touchEventsEnded() {
        final View mobileView = getViewForId(mMobileItemId);
        if (mobileView != null && (mCellIsMobile || mIsWaitingForScrollFinish)) {
            mCellIsMobile = false;
            mIsWaitingForScrollFinish = false;
            mIsMobileScrolling = false;
            mActivePointerId = TreasureConstants.INVALID_ID;

            mHoverCellCurrentBounds.offsetTo(mobileView.getLeft(), mobileView.getTop());
            animateBounds(mobileView);
        } else {
            touchEventsCancelled();
        }
    }

    private void animateBounds(final View v) {
        TypeEvaluator<Rect> sBoundEvaluator = new TypeEvaluator<Rect>() {
            public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
                return new Rect(interpolate(startValue.left, endValue.left, fraction),
                        interpolate(startValue.top, endValue.top, fraction),
                        interpolate(startValue.right, endValue.right, fraction),
                        interpolate(startValue.bottom, endValue.bottom, fraction));
            }

            public int interpolate(int start, int end, float fraction) {
                return (int) (start + fraction * (end - start));
            }
        };
        int w = (int) (v.getWidth());
        int h = (int) (v.getHeight());
        int top = (v.getTop() + (v.getBottom() - v.getTop()) / 2 - h / 2);
        int left = (v.getLeft() + (v.getRight() - v.getLeft()) / 2 - w / 2);
        Rect rect = new Rect(left, top, left + w, top + h);
        ObjectAnimator hoverViewAnimator = ObjectAnimator.ofObject(mHoverCell, "bounds",
                sBoundEvaluator, rect);
        hoverViewAnimator.setDuration(BOUND_DURATION);
        hoverViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        hoverViewAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mHoverAnimation = true;
                updateEnableState();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHoverAnimation = false;
                updateEnableState();
                reset(v);
            }
        });
        hoverViewAnimator.start();
    }

    private void reset(View mobileView) {
        idList.clear();
        mMobileItemId = TreasureConstants.INVALID_ID;
        mobileView.setVisibility(View.VISIBLE);
        mHoverCell = null;
        for (int i = 0; i < getLastVisiblePosition() - getFirstVisiblePosition(); i++) {
            View child = getChildAt(i);
            if (child != null) {
                child.setVisibility(View.VISIBLE);
            }
        }
        invalidate();
    }

    private void updateEnableState() {
        setEnabled(!mHoverAnimation && !mReorderAnimation);
    }

    private void touchEventsCancelled() {
        View mobileView = getViewForId(mMobileItemId);
        if (mCellIsMobile) {
            reset(mobileView);
        }
        mCellIsMobile = false;
        mIsMobileScrolling = false;
        mActivePointerId = TreasureConstants.INVALID_ID;
    }

    private void handleCellSwitch() {
        final int deltaY = mLastEventY - mDownY;
        final int deltaX = mLastEventX - mDownX;
        final int deltaYTotal = mHoverCellOriginalBounds.centerY() + mTotalOffsetY + deltaY;
        final int deltaXTotal = mHoverCellOriginalBounds.centerX() + mTotalOffsetX + deltaX;
        mMobileView = getViewForId(mMobileItemId);
        mMobileView.setVisibility(INVISIBLE);
        View targetView = null;
        float vX = 0;
        float vY = 0;
        Point mobileColumnRowPair = getColumnAndRowForView(mMobileView);
        for (Long id : idList) {
            View view = getViewForId(id);
            if (view != null) {
                Point targetColumnRowPair = getColumnAndRowForView(view);
                if ((aboveRight(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal < view.getBottom() && deltaXTotal > view.getLeft()
                        || aboveLeft(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal < view.getBottom() && deltaXTotal < view.getRight()
                        || belowRight(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal > view.getTop() && deltaXTotal > view.getLeft()
                        || belowLeft(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal > view.getTop() && deltaXTotal < view.getRight()
                        || above(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal < view.getBottom() - mOverlapIfSwitchStraightLine
                        || below(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal > view.getTop() + mOverlapIfSwitchStraightLine
                        || right(targetColumnRowPair, mobileColumnRowPair)
                        && deltaXTotal > view.getLeft() + mOverlapIfSwitchStraightLine
                        || left(targetColumnRowPair, mobileColumnRowPair)
                        && deltaXTotal < view.getRight() - mOverlapIfSwitchStraightLine)) {
                    float xDiff = Math.abs(TreasureBoxUtils.getViewX(view) - TreasureBoxUtils.getViewX(mMobileView));
                    float yDiff = Math.abs(TreasureBoxUtils.getViewY(view) - TreasureBoxUtils.getViewY(mMobileView));
                    if (xDiff >= vX && yDiff >= vY) {
                        vX = xDiff;
                        vY = yDiff;
                        targetView = view;
                    }
                }
            }
        }
        if (targetView != null) {
            int originalPosition = getPositionForView(mMobileView);
            int targetPosition = getPositionForView(targetView);

            final TreasureBoxGridAdapterInterface adapter = getAdapterInterface();
            if (targetPosition == INVALID_POSITION || !adapter.canReorder(originalPosition) || !adapter.canReorder(targetPosition)) {
                updateNeighborViewsForId(mMobileItemId);
                return;
            }

            reorderElements(deltaY, deltaX, originalPosition, targetPosition);

            /*switch (originalPosition) {
                case TreasureConstants.TOP_LEFT_VIEW:
                    switch (targetPosition) {
                        case TreasureConstants.TOP_LEFT_VIEW:
                            break;
                        case TreasureConstants.TOP_CENTER_VIEW:
                            break;
                        case TreasureConstants.TOP_RIGHT_VIEW:
                            //顶部 0，2item 互换
                            swapHeaderElements(deltaX, deltaY, originalPosition, targetPosition);
                            break;
                        default:
                            //顶部 item 插入下部 target 位置，其余 items 后移
                            reorderhHeader2BottomElements(deltaY, deltaX, originalPosition, targetPosition);
                            break;
                    }
                    break;
                case TreasureConstants.TOP_CENTER_VIEW:
                    break;
                case TreasureConstants.TOP_RIGHT_VIEW:
                    switch (targetPosition) {
                        case TreasureConstants.TOP_LEFT_VIEW:
                            swapHeaderElements(deltaX, deltaY, originalPosition, targetPosition);
                            break;
                        case TreasureConstants.TOP_CENTER_VIEW:
                            break;
                        case TreasureConstants.TOP_RIGHT_VIEW:
                            break;
                        default:
                            reorderhHeader2BottomElements(deltaY, deltaX, originalPosition, targetPosition);
                            break;
                    }
                    break;
                default:
                    switch (targetPosition) {
                        case TreasureConstants.TOP_LEFT_VIEW:
                            swapBottom2HeaderElements(deltaY, deltaX, originalPosition, targetPosition);
                            break;
                        case TreasureConstants.TOP_CENTER_VIEW:
                            if (MoveEventX < TreasureBoxUtils.getViewX(targetView)) {
                                swapBottom2HeaderElements(deltaY, deltaX, originalPosition, TreasureConstants.TOP_LEFT_VIEW);
                            } else {
                                swapBottom2HeaderElements(deltaY, deltaX, originalPosition, TreasureConstants.TOP_RIGHT_VIEW);
                            }
                            break;
                        case TreasureConstants.TOP_RIGHT_VIEW:
                            swapBottom2HeaderElements(deltaY, deltaX, originalPosition, targetPosition);
                            break;
                        default:
                            reorderElements(deltaY, deltaX, originalPosition, targetPosition);
                            break;
                    }
                    break;
            }*/
        }
    }

    private interface SwitchCellAnimator {
        void animateSwitchCell(final int originalPosition, final int targetPosition);
    }

    /**
     * 百宝箱主区域交换动画
     */
    private class NormalSwitchCellAnimator implements SwitchCellAnimator {

        private int mDeltaY;
        private int mDeltaX;

        public NormalSwitchCellAnimator(int deltaX, int deltaY) {
            mDeltaX = deltaX;
            mDeltaY = deltaY;
        }

        @Override
        public void animateSwitchCell(final int originalPosition, final int targetPosition) {
            getViewTreeObserver().addOnPreDrawListener(new AnimateSwitchViewOnPreDrawListener(mMobileView, originalPosition, targetPosition));
            mMobileView = getViewForId(mMobileItemId);
        }

        private class AnimateSwitchViewOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

            private final View mPreviousMobileView;
            private final int mOriginalPosition;
            private final int mTargetPosition;

            AnimateSwitchViewOnPreDrawListener(final View previousMobileView, final int originalPosition, final int targetPosition) {
                mPreviousMobileView = previousMobileView;
                mOriginalPosition = originalPosition;
                mTargetPosition = targetPosition;
            }

            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);

                mTotalOffsetY += mDeltaY;
                mTotalOffsetX += mDeltaX;

                mPreviousMobileView.setVisibility(View.VISIBLE);

                if (mMobileView != null) {
                    mMobileView.setVisibility(View.INVISIBLE);
                }

                animateReorder(mOriginalPosition, mTargetPosition);

                return true;
            }
        }
    }

    /**
     * 3.0以下百宝箱主区域交换动画
     */
    private class PreHoneyCellAnimator implements SwitchCellAnimator {
        private int mDeltaY;
        private int mDeltaX;

        public PreHoneyCellAnimator(int deltaX, int deltaY) {
            mDeltaX = deltaX;
            mDeltaY = deltaY;
        }

        @Override
        public void animateSwitchCell(int originalPosition, int targetPosition) {
            mTotalOffsetY += mDeltaY;
            mTotalOffsetX += mDeltaX;
        }
    }

    public void setAlpha(ImageView img, float alpha) {
        if (alpha >= 0.0f && alpha <= 1.0f) {
            ViewHelper.setAlpha(img, alpha);
        } else if (alpha < 0.0f) {
            ViewHelper.setAlpha(img, 0.0f);
        } else {
            ViewHelper.setAlpha(img, 1.0f);
        }
    }

    private boolean belowLeft(Point targetColumnRowPair, Point mobileColumnRowPair) {
        return targetColumnRowPair.y > mobileColumnRowPair.y && targetColumnRowPair.x < mobileColumnRowPair.x;
    }

    private boolean belowRight(Point targetColumnRowPair, Point mobileColumnRowPair) {
        return targetColumnRowPair.y > mobileColumnRowPair.y && targetColumnRowPair.x > mobileColumnRowPair.x;
    }

    private boolean aboveLeft(Point targetColumnRowPair, Point mobileColumnRowPair) {
        return targetColumnRowPair.y < mobileColumnRowPair.y && targetColumnRowPair.x < mobileColumnRowPair.x;
    }

    private boolean aboveRight(Point targetColumnRowPair, Point mobileColumnRowPair) {
        return targetColumnRowPair.y < mobileColumnRowPair.y && targetColumnRowPair.x > mobileColumnRowPair.x;
    }

    private boolean above(Point targetColumnRowPair, Point mobileColumnRowPair) {
        return targetColumnRowPair.y < mobileColumnRowPair.y && targetColumnRowPair.x == mobileColumnRowPair.x;
    }

    private boolean below(Point targetColumnRowPair, Point mobileColumnRowPair) {
        return targetColumnRowPair.y > mobileColumnRowPair.y && targetColumnRowPair.x == mobileColumnRowPair.x;
    }

    private boolean right(Point targetColumnRowPair, Point mobileColumnRowPair) {
        return targetColumnRowPair.y == mobileColumnRowPair.y && targetColumnRowPair.x > mobileColumnRowPair.x;
    }

    private boolean left(Point targetColumnRowPair, Point mobileColumnRowPair) {
        return targetColumnRowPair.y == mobileColumnRowPair.y && targetColumnRowPair.x < mobileColumnRowPair.x;
    }

    private Point getColumnAndRowForView(View view) {
        int pos = getPositionForView(view);
        int columns = getColumnCount();
        int column = pos % columns;
        int row = pos / columns;
        return new Point(column, row);
    }

    private long getId(int position) {
        return getAdapter().getItemId(position);
    }


    private void animateReorder(final int oldPosition, final int newPosition) {
        boolean isForward = newPosition > oldPosition;
        List<Animator> resultList = new LinkedList<Animator>();
        if (isForward) {
            for (int pos = Math.min(oldPosition, newPosition); pos < Math.max(oldPosition, newPosition); pos++) {
                View view = getViewForId(getId(pos));
                if ((pos + 1) % getColumnCount() == 0) {
                    resultList.add(createTranslationAnimations(view, -view.getWidth() * (getColumnCount() - 1), 0,
                            view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view, view.getWidth(), 0, 0, 0));
                }
            }
        } else {
            for (int pos = Math.max(oldPosition, newPosition); pos > Math.min(oldPosition, newPosition); pos--) {
                View view = getViewForId(getId(pos));
                if ((pos + getColumnCount()) % getColumnCount() == 0) {
                    resultList.add(createTranslationAnimations(view, view.getWidth() * (getColumnCount() - 1), 0,
                            -view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view, -view.getWidth(), 0, 0, 0));
                }
            }
        }

        AnimatorSet resultSet = new AnimatorSet();
        resultSet.playTogether(resultList);
        resultSet.setDuration(MOVE_DURATION);
        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
        resultSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mReorderAnimation = true;
                updateEnableState();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mReorderAnimation = false;
                updateEnableState();
            }
        });
        resultSet.start();
    }


    private AnimatorSet createTranslationAnimations(View view, float startX, float endX,
                                                    float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHoverCell != null) {
            mHoverCell.draw(canvas);
        }
    }


    public interface OnDropListener {
        void onActionDrop();
    }

    public interface OnDragListener {

        public void onDragStarted(int position);

        public void onDragPositionsChanged(int oldPosition, int newPosition);
    }

    public interface OnEditModeChangeListener {
        public void onEditModeChanged(boolean inEditMode);
    }

    public interface OnSelectedItemBitmapCreationListener {
        public void onPreSelectedItemBitmapCreation(View selectedView, int position, long itemId);

        public void onPostSelectedItemBitmapCreation(View selectedView, int position, long itemId);
    }


    /**
     * 滚屏监听
     * <p/>
     * 当拖动item至屏幕顶部或底部时触发，进行scroll后进行交换items操作
     */
    private OnScrollListener mScrollListener = new OnScrollListener() {

        private int mPreviousFirstVisibleItem = -1;
        private int mPreviousVisibleItemCount = -1;
        private int mCurrentFirstVisibleItem;
        private int mCurrentVisibleItemCount;
        private int mCurrentScrollState;

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            mCurrentFirstVisibleItem = firstVisibleItem;
            mCurrentVisibleItemCount = visibleItemCount;

            mPreviousFirstVisibleItem = (mPreviousFirstVisibleItem == -1) ? mCurrentFirstVisibleItem
                    : mPreviousFirstVisibleItem;
            mPreviousVisibleItemCount = (mPreviousVisibleItemCount == -1) ? mCurrentVisibleItemCount
                    : mPreviousVisibleItemCount;

            checkAndHandleFirstVisibleCellChange();
            checkAndHandleLastVisibleCellChange();

            mPreviousFirstVisibleItem = mCurrentFirstVisibleItem;
            mPreviousVisibleItemCount = mCurrentVisibleItemCount;
            if (mUserScrollListener != null) {
                mUserScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mCurrentScrollState = scrollState;
            mScrollState = scrollState;
            isScrollCompleted();
            if (mUserScrollListener != null) {
                mUserScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        private void isScrollCompleted() {
            if (mCurrentVisibleItemCount > 0 && mCurrentScrollState == SCROLL_STATE_IDLE) {
                if (mCellIsMobile && mIsMobileScrolling) {
                    handleMobileCellScroll();
                } else if (mIsWaitingForScrollFinish) {
                    touchEventsEnded();
                }
            }
        }

        public void checkAndHandleFirstVisibleCellChange() {
            if (mCurrentFirstVisibleItem != mPreviousFirstVisibleItem) {
                if (mCellIsMobile && mMobileItemId != TreasureConstants.INVALID_ID) {
                    updateNeighborViewsForId(mMobileItemId);
                }
            }
        }

        public void checkAndHandleLastVisibleCellChange() {
            int currentLastVisibleItem = mCurrentFirstVisibleItem + mCurrentVisibleItemCount;
            int previousLastVisibleItem = mPreviousFirstVisibleItem + mPreviousVisibleItemCount;
            if (currentLastVisibleItem != previousLastVisibleItem) {
                if (mCellIsMobile && mMobileItemId != TreasureConstants.INVALID_ID) {
                    updateNeighborViewsForId(mMobileItemId);
                }
            }
        }
    };

    private OnItemClickListener mLocalItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!isEditMode() && isEnabled() && mUserItemClickListener != null) {
                mUserItemClickListener.onItemClick(parent, view, position, id);
            }
        }
    };

}

