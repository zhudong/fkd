
package com.fuexpress.kr.ui.activity.merchant_detail;

import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.model.MerChantBean;
import com.fuexpress.kr.ui.view.TitleBarView;


/**
 * Created by Longer on 2017/6/26.
 */

public class MerchantBusinessActivity02 extends BaseActivity {


    private View mRootView;
    private String mDesc;
    private String mMainBusiness;
    private String mAddress;
    private String mContact;
    private TextView mTv_in_merchant_bussiness_localtion;
    private TextView mTv_in_merchant_bussiness_tel;
    private TextView mTv_in_merchant_bussiness_desc_01;
    private TextView mTv_in_merchant_bussiness_desc_02;
    private TextView mTv_in_merchant_bussiness_main_bussiness_detail;
    private TitleBarView mTitleBarView;
    //private ImageView mImageViewLeft;
    private TextView mTv_in_merchant_detail_info_is_working;
    private LinearLayout mLl_in_merchant_business_shop_time;
    private TitleBarView mTitle_in_merbusiness;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_merchant_business, null);
        //mTitleBarView = new TitleBarView(mRootView);
        mTitle_in_merbusiness = (TitleBarView) mRootView.findViewById(R.id.title_in_merbusiness);
        return mRootView;
    }

    @Override
    public void initView() {
        mTv_in_merchant_bussiness_localtion = (TextView) mRootView.findViewById(R.id.tv_in_merchant_bussiness_localtion);
        //mTv_in_merchant_bussiness_tel = (TextView) mRootView.findViewById(R.id.tv_in_merchant_bussiness_tel);
        mTv_in_merchant_bussiness_desc_01 = (TextView) mRootView.findViewById(R.id.tv_in_merchant_bussiness_desc_01);
        mTv_in_merchant_bussiness_desc_02 = (TextView) mRootView.findViewById(R.id.tv_in_merchant_bussiness_desc_02);
        mTv_in_merchant_bussiness_main_bussiness_detail = (TextView) mRootView.findViewById(R.id.tv_in_merchant_bussiness_main_bussiness_detail);
        mTv_in_merchant_detail_info_is_working = (TextView) mRootView.findViewById(R.id.tv_in_merchant_detail_info_is_working);
        mLl_in_merchant_business_shop_time = (LinearLayout) mRootView.findViewById(R.id.ll_in_merchant_business_shop_time);
        mTitle_in_merbusiness.getIv_in_title_back().setOnClickListener(this);
        //mImageViewLeft.setOnClickListener(this);
        initData();
    }

    private void initData() {
        MerChantBean merChantBean = MerchantDetailManager.getInstance().mMerChantBean;
        String mainBusiness = MerchantDetailManager.getInstance().mMainBusiness;
        mDesc = merChantBean.getDesc();
        /*if (!TextUtils.isEmpty(mDesc)) {
            if (mDesc.equals("<br />")) {
                mSplit = mDesc.split("<br />");
            } else if (mDesc.equals("<br/>")) {
                mSplit = mDesc.split("< br/>");
            }

        }*/
        mMainBusiness = mainBusiness;
        mAddress = merChantBean.getAddress();
        //mContact = merChantBean.getContact();
        //mTitleBarView.setTitle(merChantBean.getTitle());
        mTitle_in_merbusiness.setTitleText(merChantBean.getTitle());
        setTextView();
    }


    public void setTextView() {

        mTv_in_merchant_bussiness_localtion.setText(mAddress);
        // mTv_in_merchant_bussiness_tel.setText(mContact);
        mTv_in_merchant_bussiness_main_bussiness_detail.setText(mMainBusiness);
        if (!TextUtils.isEmpty(mDesc)) {
            mTv_in_merchant_bussiness_desc_01.setText(Html.fromHtml(mDesc));
        } else {
            mTv_in_merchant_bussiness_desc_01.setVisibility(View.GONE);
            mTv_in_merchant_bussiness_desc_02.setVisibility(View.GONE);
        }
        //mTv_in_merchant_bussiness_desc_01.setText(Html.fromHtml(mDesc));
        //mTv_in_merchant_detail_info_is_working.setText(mIsWorking);
        mTv_in_merchant_detail_info_is_working.setText(MerchantDetailManager.getInstance().mStatus);

        for (String s : MerchantDetailManager.getInstance().mShopTimeData) {
            if (s.equals(getString(R.string.merchant_detail_business_working_none)) || s.equals(getString(R.string.merchant_detail_business_working))) {
                //mTv_in_merchant_detail_info_is_working.setText(s);
            } else {
                TextView textView = new TextView(this);
                textView.setTextSize(11);
                textView.setTextColor(ContextCompat.getColor(this, R.color.text_color_for_store));
                textView.setText(s);
                mLl_in_merchant_business_shop_time.addView(textView);
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
                finish();
                break;
        }
    }
}