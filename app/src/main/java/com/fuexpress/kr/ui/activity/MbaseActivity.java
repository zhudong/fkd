package com.fuexpress.kr.ui.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;


/**
 * Created by yuan on 2016-6-13.
 */
public abstract class MbaseActivity extends BaseActivity {

    protected View mRootView;
    private ImageView mImgLeft;
    private ImageView mImgRight;
    private TextView mTvTitle;
    private TextView mTvleft;
    protected TextView mTvRight;


    @Override
    public View setInitView() {
//        mRootView = View.inflate(this, R.layout.activity_base, null);
//        FrameLayout container = (FrameLayout) mRootView.findViewById(R.id.fl_container);
        mRootView = View.inflate(this, getViewResId(), null);
//        container.addView(content);
//        mLoadingView = mRootView.findViewById(R.id.rl_loading);
        mImgLeft = (ImageView) mRootView.findViewById(R.id.title_iv_left);
        mImgLeft.setOnClickListener(this);
        mImgRight = (ImageView) mRootView.findViewById(R.id.title_iv_right);
        mImgRight.setOnClickListener(this);
        mTvTitle = (TextView) mRootView.findViewById(R.id.title_tv_center);
        mTvleft = (TextView) mRootView.findViewById(R.id.title_tv_left);
        mTvleft.setOnClickListener(this);
        mTvRight = (TextView) mRootView.findViewById(R.id.title_tv_right);
        mTvRight.setOnClickListener(this);
        return mRootView;
    }

    protected abstract int getViewResId();

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.title_tv_left:
                finish();
                break;
            case R.id.title_tv_right:
                onRightTVClick();
                break;
            case R.id.title_iv_right:
                onRightIVClick();
                break;
        }
    }

    protected void initTitle(String left, String title, String right) {
        setTitle(title);
        setTvRight(right);
        setTvleft(left);
    }

    protected void onRightTVClick() {
    }

    ;

    protected void onRightIVClick() {
    }

    ;

    protected void setTitle(String title) {
        mTvTitle.setText(title);
    }

    protected void setTvleft(String tvleft) {
        mTvleft.setText(tvleft);
        mTvleft.setVisibility(View.VISIBLE);
        mImgLeft.setVisibility(View.VISIBLE);
    }

    protected void setTvRight(String tvRight) {
        mTvRight.setText(tvRight);
        mTvRight.setVisibility(View.VISIBLE);
        mImgRight.setVisibility(View.VISIBLE);
    }

    protected void hintIVRight() {
        mImgRight.setVisibility(View.GONE);
    }

    protected void hintTVRight() {
        mTvRight.setVisibility(View.GONE);
    }

}
