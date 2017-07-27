package com.fuexpress.kr.ui.activity.product_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.fuexpress.kr.R;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.activity.PhotoViewActivity;
import com.fuexpress.kr.ui.adapter.ItemImgsAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.base.BaseHolder;
import com.fuexpress.kr.ui.view.ViewPagerFixed;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;


/**
 * Created by yuan on 2016-4-1.
 */
public class ProductDetailHead extends BaseHolder<List<CsBase.ItemImage>> {

    ViewPagerFixed mVpItemImgs;
    LinearLayout mLlVpGraypoints;
    View mVGuidRedpoint;
    RelativeLayout mRlDetailContainer;
    protected List<CsBase.ItemImage> mData;
    private int mGrayPointsDis;
    private FrameLayout.LayoutParams mLayoutParams;
    private ArrayList<String> mUrls;
    private View mPosition;
    private AutoLunbo mAutoLunbo;
    private int mLength;

    public ProductDetailHead(Activity context) {
        super(context);
    }

    @Override
    public View initView() {
        View rootView = View.inflate(mContext, R.layout.view_for_product_head, null);
        mVpItemImgs = (ViewPagerFixed) rootView.findViewById(R.id.vp_item_imgs);
        mLlVpGraypoints = (LinearLayout) rootView.findViewById(R.id.ll_vp_graypoints);
        mRlDetailContainer = (RelativeLayout) rootView.findViewById(R.id.rl_detail_container);
        mVGuidRedpoint = rootView.findViewById(R.id.v_guid_redpoint);
        mPosition = rootView.findViewById(R.id.fl_pointer_positon);
        return rootView;
    }


    @Override
    public void initData(List<CsBase.ItemImage> data) {
        mData = data;
        initPoint(data);

        mUrls = new ArrayList<>();
        for (CsBase.ItemImage image : mData) {
            mUrls.add(image.getImageUrl());
        }
        mVpItemImgs.setAdapter(new ItemImgsAdapter(mContext, mUrls, Constants.ImgUrlSuffix.mob_list));
        mVpItemImgs.setCurrentItem(5000 - 5000 % mUrls.size());
        initEvent(mUrls);
    }

    protected void initPoint(List data) {
        mLength = data.size();
        if (data.size() >= 2) {
            mVGuidRedpoint.setVisibility(View.VISIBLE);
            mLlVpGraypoints.setVisibility(View.VISIBLE);
        }
        mLlVpGraypoints.removeAllViews();

        for (int i = 0; i < data.size(); i++) {
            View v_graypoint = new View(mContext);
            v_graypoint.setBackgroundResource(R.drawable.graypoint);
            int dip = 6;
            /*点之间的距离*/
            int pix = UIUtils.dip2px(dip);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(pix, pix);// 10pix
            if (i != 0) {
                lp.leftMargin = pix;
            }
            v_graypoint.setLayoutParams(lp);
            mLlVpGraypoints.addView(v_graypoint);
        }
    }

    @Override
    public void initEvent(final List date) {
        setOnPageClick();
        if (date.size() < 2) return;

        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mGrayPointsDis = mLlVpGraypoints.getChildAt(1).getLeft() - mLlVpGraypoints.getChildAt(0).getLeft();
                mVGuidRedpoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });


        mVpItemImgs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                position = position % date.size();
                mLayoutParams = (FrameLayout.LayoutParams) mVGuidRedpoint.getLayoutParams();
                mLayoutParams.leftMargin = Math.round(mGrayPointsDis * (position));
                mVGuidRedpoint.setLayoutParams(mLayoutParams);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public void setImageSize(double ration) {
        int width = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
        int height = (int) (width / ration);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        mRlDetailContainer.updateViewLayout(mVpItemImgs, params);
    }

    protected void setOnPageClick() {
        mVpItemImgs.setOnTouchListener(new View.OnTouchListener() {

            private float downX;
            private float downY;
            private long downTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        downTime = System.currentTimeMillis();
                        if (mAutoLunbo != null) {
                            mAutoLunbo.stopLunbo();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mAutoLunbo != null) {
                            mAutoLunbo.startLunbo();
                        }

                        float upX = event.getX();
                        float upY = event.getY();
                        long upTime = System.currentTimeMillis();
                        if (Math.abs((upX - downX)) < 5 &&
                                Math.abs(upY - downY) < 5) {
                            if (upTime - downTime < 300) {
                                goActivity();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (mAutoLunbo != null) {
                            mAutoLunbo.startLunbo();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public void setPosttionRitht() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPosition.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mRlDetailContainer.updateViewLayout(mPosition, layoutParams);
    }

    protected void goActivity() {
        Intent intent = new Intent(mContext, PhotoViewActivity.class);
        intent.putExtra(PhotoViewActivity.PAGE_INDEX, mVpItemImgs.getCurrentItem());
        ArrayList<String> imgs = new ArrayList<String>();
        for (CsBase.ItemImage itemImage : mData) {
            imgs.add(itemImage.getImageUrl());
        }
        intent.putStringArrayListExtra(PhotoViewActivity.PAGE_IMAGES, imgs);
        intent.putExtra(PhotoViewActivity.PAGE_INDEX, mVpItemImgs.getCurrentItem());
        mContext.startActivity(intent);
    }


    protected void startAutoLunbo() {
        if (mAutoLunbo == null) {
            mAutoLunbo = new AutoLunbo();
        }
        mAutoLunbo.startLunbo();
    }


    private class AutoLunbo extends Handler implements Runnable {

        @Override
        public void run() {
            mVpItemImgs.setCurrentItem((mVpItemImgs.getCurrentItem() + 1)
                    % mLength);
            postDelayed(this, 4000);
        }

        public void startLunbo() {
            stopLunbo();
            postDelayed(this, 4000);
        }

        public void stopLunbo() {
            removeCallbacksAndMessages(null);
        }
    }
}
