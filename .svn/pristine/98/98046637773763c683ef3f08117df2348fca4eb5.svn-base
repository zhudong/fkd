package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.utils.LogUtils;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by Administrator on 2016/8/3.
 */
public class CustomPtrHeader extends FrameLayout implements PtrUIHandler {
    private TextView titleTv;
    private TextView dateTv;
    private View rootView;
    private ImageView arrowIv;
    private RotateAnimation mUp2Down;
    private RotateAnimation mDown2Up;
    private ProgressBar mHeaderViewPb;

    public CustomPtrHeader(Context context) {
        super(context);
        init();
    }

    public CustomPtrHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomPtrHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.inflate_refreshlistview_headerview, this);
        titleTv = (TextView) rootView.findViewById(R.id.refresh_headerview_state);
        arrowIv = (ImageView) rootView.findViewById(R.id.refresh_headerview_arrow);
        mHeaderViewPb = (ProgressBar) rootView.findViewById(R.id.refresh_headerview_pb);
        dateTv = (TextView) rootView.findViewById(R.id.refresh_headerview_updatetime);
        dateTv.setVisibility(GONE);
        mUp2Down = new RotateAnimation(180, 360, RotateAnimation.RELATIVE_TO_SELF, .5f, RotateAnimation.RELATIVE_TO_SELF, .5f);
        mUp2Down.setDuration(400);
        mUp2Down.setFillAfter(true);
        mDown2Up = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, .5f, RotateAnimation.RELATIVE_TO_SELF, .5f);
        mDown2Up.setDuration(400);
        mDown2Up.setFillAfter(true);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        titleTv.setText("onUIReset");
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        titleTv.setText("onUIRefreshPrepare");
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        titleTv.setText(getContext().getString(R.string.pull_refresh_loading_msg));
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        titleTv.setText(getContext().getString(R.string.pull_refresh_complete_msg));
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        int currentPos = ptrIndicator.getCurrentPosY();
        int lastPos = ptrIndicator.getLastPosY();

        LogUtils.d("-----mOffsetToRefresh " + mOffsetToRefresh + " currentPos " + currentPos + " lastPos " + lastPos);
        if (ptrIndicator.getCurrentPosY() < mOffsetToRefresh) {
            if (isUnderTouch && status == frame.PTR_STATUS_PREPARE) {
                ptrIndicator.crossRefreshLineFromTopToBottom();
                titleTv.setText(getContext().getString(R.string.pull_refresh_pull_msg));
                arrowIv.setVisibility(VISIBLE);
                mHeaderViewPb.setVisibility(GONE);
                arrowIv.startAnimation(mUp2Down);
            }
        } else if (ptrIndicator.getCurrentPosY() > mOffsetToRefresh && ptrIndicator.getLastPosY() <= mOffsetToRefresh) {
            if (isUnderTouch && status == frame.PTR_STATUS_PREPARE) {
                ptrIndicator.crossRefreshLineFromTopToBottom();
                titleTv.setText(getContext().getString(R.string.pull_refresh_release_msg));
                arrowIv.startAnimation(mDown2Up);
                arrowIv.setVisibility(VISIBLE);
                mHeaderViewPb.setVisibility(GONE);
            }
        }

        invalidate();
    }
}
