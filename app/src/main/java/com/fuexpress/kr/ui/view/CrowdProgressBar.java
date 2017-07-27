package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.UIUtils;

/**
 * Created by yuan on 2016-4-5.
 */
public class CrowdProgressBar extends RelativeLayout {

    private RelativeLayout mRootView;
    private ImageView mImgStart;
    private ImageView mImgMiddle;
    private ImageView mImgEnd;
    private ProgressBar mProgressBar;
    private int mProgress;

    public CrowdProgressBar(Context context) {
        this(context, null);
    }

    public CrowdProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mRootView = (RelativeLayout) View.inflate(getContext(), R.layout.view_for_crowd_progress, this);
        mRootView = (RelativeLayout) mRootView.findViewById(R.id.rl_root);
        mImgStart = (ImageView) mRootView.findViewById(R.id.img_start);
        mImgMiddle = (ImageView) mRootView.findViewById(R.id.img_middle);
        mImgEnd = (ImageView) mRootView.findViewById(R.id.img_end);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.corwd_progressBar);
    }

    public void setProgressMax(int max) {
        if (max > mProgressBar.getMax()) {
            max = mProgressBar.getMax();
        }
        mProgressBar.setMax(max);
    }

    public void setProgressHeight(float height) {
        final ViewGroup.LayoutParams layoutParams = mProgressBar.getLayoutParams();
        layoutParams.height = UIUtils.dip2px(height);
        mRootView.updateViewLayout(mProgressBar, layoutParams);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        if (progress > mProgressBar.getMax()) {
            progress = mProgressBar.getMax();
        }
        mProgressBar.setProgress(progress);
        if (progress > 0) {
            mImgStart.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.start));
        } else {
            mImgStart.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.unstart));
        }

//       int v = (int) ((progress / mProgressBar.getMax()) * 100 + 0.5);
        if (progress >= 50) {
            mImgMiddle.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.finish));
        } else {
            mImgMiddle.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.unfinish));
        }

        if (progress >= mProgressBar.getMax()) {
            mImgEnd.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.finish));
        } else {
            mImgEnd.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.unfinish));
        }
    }

    public void hideMiddle(boolean isHide) {
        if (isHide) {
            mImgMiddle.setVisibility(View.GONE);
        } else {
            mImgMiddle.setVisibility(View.VISIBLE);
        }
    }


    public void showIndicator(boolean hint) {
        if (!hint) {
            mImgStart.setVisibility(GONE);
            mImgMiddle.setVisibility(GONE);
            mImgEnd.setVisibility(GONE);
        } else {
            mImgStart.setVisibility(VISIBLE);
            mImgMiddle.setVisibility(VISIBLE);
            mImgEnd.setVisibility(VISIBLE);
        }
    }

    public int getProgress() {
        return mProgress;
    }
}
