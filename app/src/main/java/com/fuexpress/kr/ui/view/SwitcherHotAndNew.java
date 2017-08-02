package com.fuexpress.kr.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;

/**
 * Created by yuan on 2016/3/7.
 */
public class SwitcherHotAndNew extends LinearLayout implements View.OnClickListener {

    private LinearLayout mRootView;
    private TextView mHot;
    private TextView mNew;
    private View mIndicator;
    private OnSwitchListener mSwitchListener;
    private TextView mTvLeftCount;
    private TextView mTvRightCount;
    private AnimaListener mAnimaListener;
    private int mWidth;
    private View left;
    private View right;
    boolean isLeft = true;
    private ImageView mRightArrow;
    private ImageView mLeftArrow;
    private boolean mArrowVisable;

    public interface OnSwitchListener {
        public void switchLeft(SwitcherHotAndNew view);

        public void switchRight(SwitcherHotAndNew view);
    }

    public void setmOnArrowStateListener(OnArrowStateListener mOnArrowStateListener) {
        this.mOnArrowStateListener = mOnArrowStateListener;
    }

    OnArrowStateListener mOnArrowStateListener;

    public interface OnArrowStateListener {
        public void onSwitch(boolean open, boolean isLeft);
    }

    public interface AnimaListener {
        public void animaStop();
    }

    public void setAnimaListener(AnimaListener listener) {
        this.mAnimaListener = listener;
    }

    public SwitcherHotAndNew(Context context) {
        this(context, null);
    }

    public SwitcherHotAndNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth() / 2;
        mRootView = (LinearLayout) View.inflate(context, R.layout.view_hot_new_layout, this);
        mHot = (TextView) mRootView.findViewById(R.id.tv_category_hot);
        mNew = (TextView) mRootView.findViewById(R.id.tv_category_new);

        mTvLeftCount = (TextView) mRootView.findViewById(R.id.tv_left_count);
        mTvRightCount = (TextView) mRootView.findViewById(R.id.tv_right_count);

        mRightArrow = (ImageView) mRootView.findViewById(R.id.img_arrow_right);
        mLeftArrow = (ImageView) mRootView.findViewById(R.id.img_arrow_left);

        left = mRootView.findViewById(R.id.ll_left);
        left.setOnClickListener(this);
        right = mRootView.findViewById(R.id.ll_right);
        right.setOnClickListener(this);
        mIndicator = mRootView.findViewById(R.id.view_indicator);
        //mIndicator.measure(0,0);
        //mMeasuredWidth = mIndicator.getMeasuredWidth();
        mHot.setEnabled(false);
    }

    public void setArrowEnable(boolean enable) {
        if (enable) {
            mArrowVisable = true;
            if (isLeft) {
                mLeftArrow.setVisibility(VISIBLE);
            } else {
                mRightArrow.setVisibility(GONE);
            }
        } else {
            mLeftArrow.setVisibility(GONE);
            mRightArrow.setVisibility(GONE);
            mArrowVisable = false;
        }
    }

    public void closeToast() {
        if (mArrowState)
            switchArrow(isLeft);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_left:
                if (isLeft) {
                    switchArrow(true);
                } else {
                    switchHot(mIndicator, true);
                    if (mSwitchListener != null) {
                        mSwitchListener.switchLeft(this);
                    }
                    if (mArrowState)
                        switchArrow(false);
                }
                isLeft = true;
                break;
            case R.id.ll_right:
                if (!isLeft) {
                    switchArrow(false);
                } else {
                    switchNew(mIndicator, true);
                    if (mSwitchListener != null) {
                        mSwitchListener.switchRight(this);
                    }
                    if (mArrowState)
                        switchArrow(true);
                }
                isLeft = false;
                break;
        }
    }

    boolean mArrowState;

    private void switchArrow(boolean isleft) {
        if (isleft) {
            if (mArrowState) {//close
                mLeftArrow.setEnabled(true);
            } else {//open
                mLeftArrow.setEnabled(false);
            }
        } else {
//右边
            if (mArrowState) {//close
                mRightArrow.setEnabled(true);
            } else {//open
                mRightArrow.setEnabled(false);
            }
        }
        mArrowState = !mArrowState;
        if (mOnArrowStateListener != null && mArrowVisable) {
            mOnArrowStateListener.onSwitch(mArrowState, isleft);
        }
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        this.mSwitchListener = listener;
    }

    private void switchHot(View view, Boolean widthAnmi) {
        mHot.setEnabled(false);
        mNew.setEnabled(true);
        isLeft = true;
//        left.setEnabled(false);
//        right.setEnabled(true);
        setmArrowVisable(true);
        startAnima(mIndicator, mWidth, 0, widthAnmi);
    }

    private void switchNew(View v, Boolean widthAnmi) {
        mHot.setEnabled(true);
        mNew.setEnabled(false);
        isLeft = false;
//        left.setEnabled(true);
//        right.setEnabled(false);
        setmArrowVisable(false);
        startAnima(mIndicator, 0, mWidth, widthAnmi);
    }

    public void setmArrowVisable(boolean isLeft) {
        if (!mArrowVisable) {
            mLeftArrow.setVisibility(GONE);
            mRightArrow.setVisibility(GONE);
            return;
        }
        if (isLeft) {
            mLeftArrow.setVisibility(VISIBLE);
            mRightArrow.setVisibility(GONE);
        } else {
            mLeftArrow.setVisibility(GONE);
            mRightArrow.setVisibility(VISIBLE);
        }

    }


    public void startAnima(final View view, int start, int end) {
        startAnima(view, start, end, true);
    }

    public void startAnima(final View view, int start, int end, boolean widthAnim) {
//        LogUtils.d("sw", start + ":" + end);

        if (!widthAnim) {
            view.setX(end);
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAnimaListener != null) {
                    mAnimaListener.animaStop();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int temp = (int) animation.getAnimatedValue();
                view.setX(temp);
            }
        });
        animator.start();
    }


    public void setLeftSlected() {
//        if (mHot.isEnabled()) {
        switchHot(this, false);
      /*  mHot.setEnabled(false);
        mNew.setEnabled(true);*/
    }

    public void setRightSlected() {
//        if (mNew.isEnabled()) {
        switchNew(this, false);
//        }
       /* mHot.setEnabled(true);
        mNew.setEnabled(false);*/
    }

    public void setLeftTextAndCount(String text, String count) {
        mHot.setText(text);
        if (!TextUtils.isEmpty(count)) {
            mTvLeftCount.setText(count);
        }
    }

    public void setRightTextCount(String text, String count) {
        mNew.setText(text);
        if (!TextUtils.isEmpty(count)) {
            mTvRightCount.setText(count);
        }
    }
}