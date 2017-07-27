package com.fuexpress.kr.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @创建者 ${user}
 * @创时间 2016/6/13
 * @描述
 * @版本 $Rev:
 * @更新者 $Author:
 * @更新时间 $Date:
 * @更新描述 TODO
 */

public class RefreshListView extends ListView implements AbsListView.OnScrollListener, View.OnClickListener {

    public static final String TAG = RefreshListView.class.getSimpleName();

    private ImageView mHeaderViewArrow;
    private FrameLayout mHeaderViewCustomContainer;
    private ProgressBar mHeaderViewPb;
    private TextView mHeaderViewState;
    private TextView mHeaderViewUpdateTime;
    public View mHeaderView;
    private RelativeLayout mRefreshHeaderView;
    private int mInitRefreshHeaderViewPaddingTop;                                    // 下拉刷新头布局初始的paddingTop
    private float mDownX;
    private float mDownY;

    // 下拉刷新
    public static final int STATE_PULL_REFRESH = 0;
    // 松开刷新
    public static final int STATE_RELEASE_REFRESH = 1;
    // 正在刷新
    public static final int STATE_REFRESHING = 2;
    public static final int STATE_STOP = 3;
    // 记录当前的状态
    public int mCurState = STATE_PULL_REFRESH;

    private RotateAnimation mUp2Down;

    private RotateAnimation mDown2Up;

    private int mRefreshHeaderViewHeight;

    private boolean isStartScroll = true;

    private OnItemClickListener mListener;
    public View mFooterView;

    private ProgressBar mFooterViewPb;

    private TextView mFooterViewState;

    private boolean isLoadMoreSuccess;

    private boolean isLoadingMore;
    private FrameLayout mFootContainer;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView(context);
        initFooterView(context);
        initAnimation();

    }

    private void initFooterView(Context context) {
        mFooterView = View.inflate(context, R.layout.inflate_refreshlistview_footerview, null);
        mFooterViewPb = (ProgressBar) mFooterView.findViewById(R.id.refresh_footerview_pb);
        mFooterViewState = (TextView) mFooterView.findViewById(R.id.refresh_footerview_state);
        mFootContainer = new FrameLayout(getContext());
        addFooterView(mFootContainer);
        addFooterView(mFooterView);
        this.setOnScrollListener(this);
        mFooterView.setOnClickListener(this);
    }

    public void appendFoot(View v) {
        mFootContainer.addView(v);
    }


    public void initHeaderView(Context context) {
        mHeaderView = View.inflate(context, R.layout.inflate_refreshlistview_headerview, null);
        mHeaderViewArrow = (ImageView) mHeaderView.findViewById(R.id.refresh_headerview_arrow);
        mHeaderViewPb = (ProgressBar) mHeaderView.findViewById(R.id.refresh_headerview_pb);
        mHeaderViewState = (TextView) mHeaderView.findViewById(R.id.refresh_headerview_state);
        mHeaderViewUpdateTime = (TextView) mHeaderView.findViewById(R.id.refresh_headerview_updatetime);
        mHeaderViewCustomContainer = (FrameLayout) mHeaderView.findViewById(R.id.refresh_headerview_customheaderview);

        mRefreshHeaderView = (RelativeLayout) mHeaderView.findViewById(R.id.refresh_headerView);

        mRefreshHeaderView.measure(0, 0);
        mRefreshHeaderViewHeight = mRefreshHeaderView.getMeasuredHeight();

        mInitRefreshHeaderViewPaddingTop = -mRefreshHeaderViewHeight;

        mHeaderView.setPadding(0, mInitRefreshHeaderViewPaddingTop, 0, 0);
        addHeaderView(mHeaderView);
    }


    private void initAnimation() {
        mUp2Down =
                new RotateAnimation(180, 360, RotateAnimation.RELATIVE_TO_SELF, .5f, RotateAnimation.RELATIVE_TO_SELF,
                        .5f);
        mUp2Down.setDuration(400);
        mUp2Down.setFillAfter(true);

        mDown2Up =
                new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, .5f, RotateAnimation.RELATIVE_TO_SELF,
                        .5f);
        mDown2Up.setDuration(400);
        mDown2Up.setFillAfter(true);
    }

    public void addCustomHeader(View customHeaderView) {
        mHeaderViewCustomContainer.addView(customHeaderView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                if (mCurState != STATE_REFRESHING) {
                    super.setOnItemClickListener(mListener);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDownX == 0 && mDownY == 0) {
                    mDownX = ev.getRawX();
                    mDownY = ev.getRawY();
                }

                if (mCurState == STATE_REFRESHING) {
                    return super.onTouchEvent(ev);
                }
                float moveX = ev.getRawX();
                float moveY = ev.getRawY();
                int diffX = (int) (moveX - mDownX + .5f);
                int diffY = (int) (moveY - mDownY + .5f);
                int[] refreshHeaderViewLocation = new int[2];
                mRefreshHeaderView.getLocationInWindow(refreshHeaderViewLocation);

                int refreshHeaderViewTopY = refreshHeaderViewLocation[1];
                int refreshHeaderViewBottomY = refreshHeaderViewTopY + mRefreshHeaderViewHeight;

                int[] listViewLocation = new int[2];
                this.getLocationInWindow(listViewLocation);
                int listViewY = listViewLocation[1];

                if (refreshHeaderViewBottomY >= listViewY && diffY > 0) {

                    if (isStartScroll) {

                        mDownY = moveY;
                        diffY = (int) (moveY - mDownY + .5f);

                        isStartScroll = false;
                    }

                    int paddingTop = mInitRefreshHeaderViewPaddingTop + diffY;


                    if (paddingTop > 0 && mCurState != STATE_RELEASE_REFRESH) {
                        mCurState = STATE_RELEASE_REFRESH;

                        refreshHeaderViewUI();
                    } else if (paddingTop <= 0 && mCurState != STATE_PULL_REFRESH) {
                        mCurState = STATE_PULL_REFRESH;
                        refreshHeaderViewUI();
                    }

                    mHeaderView.setPadding(0, paddingTop, 0, 0);

                    super.setOnItemClickListener(null);
                    return true;
                } else {
                    return super.onTouchEvent(ev);
                }

            case MotionEvent.ACTION_UP:
                mDownX = 0;
                mDownY = 0;
                isStartScroll = true;
                if (mCurState == STATE_PULL_REFRESH) {
                    mCurState = STATE_PULL_REFRESH;
                    refreshHeaderViewUI();
                    changePaddingTopAnimation(mHeaderView.getPaddingTop(), mInitRefreshHeaderViewPaddingTop);

                } else if (mCurState == STATE_RELEASE_REFRESH) {
                    mCurState = STATE_REFRESHING;
                    refreshHeaderViewUI();
                    changePaddingTopAnimation(mHeaderView.getPaddingTop(), 0);
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefresh(this);
                    }
                }

                break;

            default:
                break;
        }
        //super.setOnItemClickListener(mListener);
        return super.onTouchEvent(ev);
    }

    /**
     * 刷新头布局的ui(3种状态)
     */
    private void refreshHeaderViewUI() {
        switch (mCurState) {
            case STATE_PULL_REFRESH:// 下拉刷新
                mHeaderViewArrow.setVisibility(View.VISIBLE);
                mHeaderViewArrow.startAnimation(mUp2Down);
                mHeaderViewPb.setVisibility(View.INVISIBLE);
                mHeaderViewState.setText(getResources().getString(R.string.pull_down));
                break;

            case STATE_STOP:// 停止刷新
                mHeaderViewArrow.setVisibility(View.INVISIBLE);
                mHeaderViewPb.setVisibility(View.INVISIBLE);
                mCurState = STATE_PULL_REFRESH;
                break;
            case STATE_RELEASE_REFRESH:// 松开刷新
                mHeaderViewArrow.setVisibility(View.INVISIBLE);
                mHeaderViewArrow.startAnimation(mDown2Up);
                mHeaderViewPb.setVisibility(View.INVISIBLE);
                mHeaderViewState.setText(getResources().getString(R.string.release_to_refresh));
                break;
            case STATE_REFRESHING:// 正在刷新
                mHeaderViewArrow.clearAnimation();
                mHeaderViewArrow.setVisibility(View.INVISIBLE);
                mHeaderViewPb.setVisibility(View.VISIBLE);
                mHeaderViewState.setText(getResources().getString(R.string.updating));

                mHeaderViewUpdateTime.setText(getUpdateTimeStr());

                break;

            default:
                break;
        }
    }

    public String getUpdateTimeStr() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public void changePaddingTopAnimation(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(400);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int paddintTop = (Integer) valueAnimator.getAnimatedValue();
                mHeaderView.setPadding(0, paddintTop, 0, 0);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCurState == STATE_PULL_REFRESH) {
                    refreshHeaderViewUI();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
        super.setOnItemClickListener(listener);
    }

    public void startRefresh() {
        mCurState = STATE_REFRESHING;
        refreshHeaderViewUI();
        changePaddingTopAnimation(mInitRefreshHeaderViewPaddingTop, 0);
    }

    public void autoRefresh() {
        mCurState = STATE_REFRESHING;
        refreshHeaderViewUI();
        changePaddingTopAnimation(mInitRefreshHeaderViewPaddingTop, 0);
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh(this);
        }
    }

    public void stopRefresh() {
        if (mCurState == STATE_REFRESHING) {
            mCurState = STATE_STOP;
            refreshHeaderViewUI();
            changePaddingTopAnimation(0, mInitRefreshHeaderViewPaddingTop);
        }
    }

    public interface OnRefreshListener {
        void onRefresh(RefreshListView refreshListView);

        void onLoadMore(RefreshListView refreshListView);
    }

    OnRefreshListener mOnRefreshListener;


    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (isLoadingMore) {
            return;
        }

        if (getAdapter() == null)
            return;
        if (getLastVisiblePosition() == getAdapter().getCount() - 1) {
            if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onLoadMore(this);
                    isLoadingMore = true;
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // TODO

    }


    public void stopLoadMore(boolean isLoadMoreSuccess) {
        this.isLoadMoreSuccess = isLoadMoreSuccess;
        isLoadingMore = false;
        if (isLoadMoreSuccess) {
            mFooterViewPb.setVisibility(View.VISIBLE);
            mFooterViewState.setText(getResources().getString(R.string.loading));
        } else {// 失败
            mFooterViewPb.setVisibility(View.INVISIBLE);
            mFooterViewState.setText(getResources().getString(R.string.reloading));
        }
    }

    public void setHasLoadMore(boolean isLoadMore) {
        if (isLoadMore) {
            mFooterViewPb.setVisibility(View.VISIBLE);
            mFooterViewState.setText(getResources().getString(R.string.loading));
            mFooterView.setVisibility(View.VISIBLE);
        } else {
            mFooterViewPb.setVisibility(View.INVISIBLE);
            mFooterViewState.setText(getResources().getString(R.string.loading));
            mFooterView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if (!isLoadMoreSuccess) {
            if (mOnRefreshListener != null) {
                mOnRefreshListener.onLoadMore(this);

                mFooterViewPb.setVisibility(View.VISIBLE);
                mFooterViewState.setText(getResources().getString(R.string.loading));
            }
        }
    }

    public void setHeadViewTextColor(int color) {
        mHeaderViewState.setTextColor(color);
        mHeaderViewUpdateTime.setTextColor(color);
    }

    public void setHeaderViewHide() {
        mHeaderView.setVisibility(GONE);
    }

    public void setFooterViewHide() {
        mFooterView.setVisibility(GONE);
    }

    public void setHeaderViewShow() {
        mHeaderView.setVisibility(VISIBLE);
    }

    public void setFooterViewShow() {
        mFooterView.setVisibility(VISIBLE);
    }
}
