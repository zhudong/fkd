package com.fuexpress.kr.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.utils.LogUtils;


/**
 * Created by Administrator on 2016/6/13.
 */
public class CustomListView extends ListView implements AbsListView.OnScrollListener{

    public boolean isMeasure = true;

    /** 底部显示正在加载的页面 */
    private View footerView = null;
    private Context context;
    /** 记录第一行Item的数值 */
    private int firstVisibleItem;

    public CustomListView(Context context) {
        super(context);
        this.context = context;
        initListView();
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initListView();
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initListView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initListView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        if(isMeasure){
            super.onMeasure(widthMeasureSpec, expandSpec);
        }else {
            LogUtils.d("============onMeasure" + heightMeasureSpec);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec +80);
        }
    }

    private void initListView() {

        // 为ListView设置滑动监听
        setOnScrollListener(this);
        // 去掉底部分割线
        setFooterDividersEnabled(false);
    }

    /**
     * 初始化话底部页面
     */
    public void initBottomView() {

        if (footerView == null) {
            footerView = LayoutInflater.from(this.context).inflate(
                    R.layout.listview_loadbar, null);
        }
        addFooterView(footerView);
    }

    public void remvoeBottomView(){
//        footerView.setVisibility(View.GONE);
        removeFooterView(footerView);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

        //当滑动到底部时
//        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
//                && firstVisibleItem != 0) {
//            LogUtils.d("onScrollStateChanged"+ scrollState);
//            if(onPullUpListener != null){
//                onPullUpListener.onPullUp(scrollState);
//            }
//        }

        switch (scrollState) {
            // 当不滚动时
            case OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部
                if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    if(onPullUpListener != null){
                        onPullUpListener.onPullUp(scrollState);
                    }
                }
                break;
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;

        if (footerView != null) {
            //判断可视Item是否能在当前页面完全显示
            if (visibleItemCount == totalItemCount) {
                // removeFooterView(footerView);
                footerView.setVisibility(View.GONE);//隐藏底部布局
            } else {
//                 addFooterView(footerView);
                footerView.setVisibility(View.VISIBLE);//显示底部布局
            }
        }

    }

    private OnPullUpListener onPullUpListener;

    public void setOnPullUpListener(OnPullUpListener onPullUpListener) {
        this.onPullUpListener = onPullUpListener;
    }

    public interface OnPullUpListener{
        void onPullUp(int scrollState);
    }

    private float mDX, mDY, mLX, mLY;
    int mLastAct = -1;
    boolean mIntercept = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN :
                mDX = mDY = 0f;
                mLX = ev.getX();
                mLY = ev.getY();

                break;

            case MotionEvent.ACTION_MOVE :
                final float curX = ev.getX();
                final float curY = ev.getY();
                mDX += Math.abs(curX - mLX);
                mDY += Math.abs(curY - mLY);
                mLX = curX;
                mLY = curY;

                if (mIntercept && mLastAct == MotionEvent.ACTION_MOVE) {
                    return false;
                }

                if (mDX > mDY) {

                    mIntercept = true;
                    mLastAct = MotionEvent.ACTION_MOVE;
                    return false;
                }

        }
        mLastAct = ev.getAction();
        mIntercept = false;
        return super.onInterceptTouchEvent(ev);
    }

}