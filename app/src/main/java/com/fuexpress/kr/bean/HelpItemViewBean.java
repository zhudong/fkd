package com.fuexpress.kr.bean;

import android.animation.AnimatorListenerAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Longer on 2016/12/5.
 * 用来封装动画对象的item的视图View类
 */
public class HelpItemViewBean {
    private static HelpItemViewBean sHelpItemViewBean = new HelpItemViewBean();
    private ImageView mImageView;
    private LinearLayout mLinearLayout;
    private long mDuration;
    private AnimatorListenerAdapter mAnimatiorListener;

    private HelpItemViewBean() {
    }

    public static HelpItemViewBean create() {
        if (sHelpItemViewBean != null)
            return sHelpItemViewBean;
        else return new HelpItemViewBean();
    }

    public HelpItemViewBean setLLView(LinearLayout llView) {
        mLinearLayout = llView;
        return sHelpItemViewBean;
    }

    public HelpItemViewBean setImageView(ImageView imageView) {
        mImageView = imageView;
        return sHelpItemViewBean;
    }

    public HelpItemViewBean setDuration(long duration) {
        mDuration = duration;
        return sHelpItemViewBean;
    }

    public HelpItemViewBean setAnimatiorAdapter(AnimatorListenerAdapter animatiorAdapter) {
        mAnimatiorListener = animatiorAdapter;
        return sHelpItemViewBean;
    }

    public LinearLayout getLinearLayout() {
        return mLinearLayout;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public long getDuration() {
        return mDuration;
    }

    public AnimatorListenerAdapter getAnimatiorListener() {
        return mAnimatiorListener;
    }

}
