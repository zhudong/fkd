package com.fuexpress.kr.ui.view;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseHolder;
import com.fuexpress.kr.ui.adapter.PhotoViewAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import java.util.List;

/**
 * Created by Longer on 2017/6/26.
 */
public class PhotoHead extends BaseHolder<List<String>> {


    ViewPager mVpItemImgs;
    LinearLayout mLlVpGraypoints;
    View mVGuidRedpoint;
    RelativeLayout mRlDetailContainer;
    private List<String> mData;
    private int mGrayPointsDis;
    private FrameLayout.LayoutParams mLayoutParams;
    private List<String> mUrls;
    private int defaultIndex;

    public PhotoHead(Activity context) {
        super(context);
    }

    @Override
    public View initView() {
        View rootView = View.inflate(mContext, R.layout.view_for_product_head, null);
        mVpItemImgs = (ViewPager) rootView.findViewById(R.id.vp_item_imgs);
        mLlVpGraypoints = (LinearLayout) rootView.findViewById(R.id.ll_vp_graypoints);
        mRlDetailContainer = (RelativeLayout) rootView.findViewById(R.id.rl_detail_container);
        mVGuidRedpoint = rootView.findViewById(R.id.v_guid_redpoint);
        setImageSize();
        return rootView;
    }

    @Override
    public void initData(List<String> data) {
        mData = data;
        initPoint(data);
        mUrls = data;
        mVpItemImgs.setAdapter(new PhotoViewAdapter(mContext, mUrls));
        mVpItemImgs.setCurrentItem(5000 - 5000 % mUrls.size());
        initEvent(mUrls);
    }

    public void setImageIndex(int index){
        mVpItemImgs.setCurrentItem(index);
    }

    public void setPointIndex(int index){
        this.defaultIndex = index;
    }

    protected void initPoint(List data) {
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
        if (date.size() < 2) return;

        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mGrayPointsDis = mLlVpGraypoints.getChildAt(1).getLeft() - mLlVpGraypoints.getChildAt(0).getLeft();
                setCurrentPointer(defaultIndex,date);
                mVGuidRedpoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });


        mVpItemImgs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                setCurrentPointer(position, date);
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

    public void setCurrentPointer(int position, List date) {
        position = position % date.size();
        mLayoutParams = (FrameLayout.LayoutParams) mVGuidRedpoint.getLayoutParams();
        mLayoutParams.leftMargin = Math.round(mGrayPointsDis * (position));
        mVGuidRedpoint.setLayoutParams(mLayoutParams);
    }


    public void setImageSize() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRlDetailContainer.updateViewLayout(mVpItemImgs, params);
    }
}