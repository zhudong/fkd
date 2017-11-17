package com.fuexpress.kr.bean;

import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by longer on 02/08/2017.
 */

public class AnimationBean {
    private static AnimationBean sHelpItemViewBean = new AnimationBean();
    private ImageView mImageView;
    private View tranYView;
    private View rotateView;
    private long mDuration;
    private AnimatorListenerAdapter mAnimatiorListener;
    private float span;
    private float fromRotateAngle;
    private float toRotateAngle;

    private AnimationBean() {
    }

    public static AnimationBean create() {
        if (sHelpItemViewBean != null)
            return sHelpItemViewBean;
        else return new AnimationBean();
    }

    public AnimationBean setWantAniView(View llView) {
        tranYView = llView;
        return sHelpItemViewBean;
    }

    public AnimationBean setRotateView(View llView) {
        rotateView = llView;
        return sHelpItemViewBean;
    }

    public AnimationBean setImageView(ImageView imageView) {
        mImageView = imageView;
        return sHelpItemViewBean;
    }

    public AnimationBean setDuration(long duration) {
        mDuration = duration;
        return sHelpItemViewBean;
    }

    public AnimationBean setSpan(float span) {
        this.span = span;
        return sHelpItemViewBean;
    }

    public AnimationBean setFromRotateAngle(float angle) {
        fromRotateAngle = angle;
        return sHelpItemViewBean;
    }

    public AnimationBean setToRotateAngle(float angle) {
        toRotateAngle = angle;
        return sHelpItemViewBean;
    }

    public AnimationBean setAnimatiorAdapter(AnimatorListenerAdapter animatiorAdapter) {
        mAnimatiorListener = animatiorAdapter;
        return sHelpItemViewBean;
    }

    public View getTranYView() {
        return tranYView;
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

    public float getSpan() {
        return span;
    }

    public View getRotateView() {
        return rotateView;
    }

    public float getFromRotateAngle() {
        return fromRotateAngle;
    }

    public float getToRotateAngle() {
        return toRotateAngle;
    }
}
