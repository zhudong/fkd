package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.ui.uiutils.UIUtils;

/**
 * Created by Longer on 2016/10/26.
 */
public class TitleBarView extends RelativeLayout {


    private TextView mTv_in_title;
    private ImageView mIv_in_title_back;
    private TextView mTv_in_title_back_tv;
    private TextView mTv_in_title_right;
    private ImageView mIv_in_title_right;
    private RelativeLayout mRl_title_container;
    private TextView mTv_in_title_left;
    private SwitcherText mSwitcherText;
    private FrameLayout mFl_title_cart;
    private TextView mTv_title_cart_amount_note;
    private ImageView iv_title_more;

    public TitleBarView(Context context) {
        this(context, null);
        //initView(context);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        //initView(context);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.titlebar_layout, this);
        mTv_in_title = (TextView) findViewById(R.id.tv_in_title);
        mIv_in_title_back = (ImageView) findViewById(R.id.iv_in_title_back);
        mTv_in_title_back_tv = (TextView) findViewById(R.id.tv_in_title_back_tv);
        mTv_in_title_right = (TextView) findViewById(R.id.tv_in_title_right);
        mIv_in_title_right = (ImageView) findViewById(R.id.iv_in_title_right);
        mRl_title_container = (RelativeLayout) findViewById(R.id.rl_title_container);
        mTv_in_title_left = (TextView) findViewById(R.id.tv_in_title_left);
        mSwitcherText = (SwitcherText) findViewById(R.id.sw_category_hot_album);
        mFl_title_cart = (FrameLayout) findViewById(R.id.fl_title_cart);
        mTv_title_cart_amount_note = (TextView) findViewById(R.id.tv_title_cart_amount_note);
        iv_title_more = (ImageView) findViewById(R.id.iv_title_more);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.mytitlebar, defStyleAttr, 0);
        String text = a.getString(R.styleable.mytitlebar_text);
        a.recycle(); // 回收资源
        mTv_in_title.setText(text);
        //initView(context);
    }

    private void initView(Context context) {

    }

    public TextView getTv_in_title_left() {
        mTv_in_title_left.setVisibility(VISIBLE);
        return mTv_in_title_left;
    }

    public RelativeLayout getRl_title_container() {
        return mRl_title_container;
    }


    public void setTitleText(String titleText) {
        mTv_in_title.setText(titleText);
    }

    public ImageView getIv_in_title_back() {
        mIv_in_title_back.setVisibility(VISIBLE);
        return mIv_in_title_back;
    }

    public TextView getmTv_in_title_back_tv() {
        mTv_in_title_back_tv.setVisibility(VISIBLE);
        return mTv_in_title_back_tv;
    }

    public TextView getTv_in_title_right() {
        mTv_in_title_right.setVisibility(VISIBLE);
        return mTv_in_title_right;
    }

    public ImageView getIv_in_title_right() {
        mIv_in_title_right.setVisibility(VISIBLE);
        return mIv_in_title_right;
    }

    public SwitcherText getSwitcherText() {
        mSwitcherText.setVisibility(VISIBLE);
        return mSwitcherText;
    }

    public FrameLayout getFl_title_cart() {
        mFl_title_cart.setVisibility(VISIBLE);
        return mFl_title_cart;
    }

    public TextView getTv_title_cart_amount_note() {
        return mTv_title_cart_amount_note;
    }

    public ImageView getIv_title_more() {
        iv_title_more.setVisibility(VISIBLE);
        return iv_title_more;
    }

    public TextView getTv_in_title() {
        return mTv_in_title;
    }
}