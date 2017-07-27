package com.fuexpress.kr.ui.activity.transport_address;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.bean.CountryBean;
import com.fuexpress.kr.bean.TraspoAddressBean;
import com.fuexpress.kr.ui.activity.help_signed.data.WareHouseBean;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.SlidingTabLayout;
import com.fuexpress.kr.ui.view.TitleBarView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TranSportAddressActivity extends BaseActivity {

    private View mRootView;
    private SlidingTabLayout mTab;
    private ViewPager mPager;
    private LinkedList<TranSportSubAdFragment> mSubAdFragments;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_tran_sport, null);
        return mRootView;
    }


    private TraspoAdRemoteListener mTraspoAdRemoteListener = new TraspoAdRemoteListener() {
        @Override
        public void success(List<WareHouseBean> wareHouseBeans, boolean hasMore) {
            initFragment(wareHouseBeans, hasMore);
        }

        @Override
        public void fail(String errStr) {
            CustomToast.makeText(TranSportAddressActivity.this, errStr, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void initView() {
        TitleBarView title = (TitleBarView) mRootView.findViewById(R.id.title);
        mTab = (SlidingTabLayout) mRootView.findViewById(R.id.tab);
        mPager = (ViewPager) mRootView.findViewById(R.id.pager);

        title.getIv_in_title_back().setOnClickListener(this);
        TextView textView = title.getmTv_in_title_back_tv();
        textView.setText(getString(R.string.main_me_tab_string));
        textView.setOnClickListener(this);

        mTab.setTabTitleTextSize(15);
        mTab.setTitleTextColor(Color.BLACK, Color.GRAY);
        mTab.setSelectedIndicatorColors(Color.alpha(0));
        mTab.setDistributeEvenly(true);
        mTab.setTheTextAllCaps(false);

        initData();
    }

    private void initData() {
        mSubAdFragments = new LinkedList<>();
        TranSportAdManager.getInstance().getTransportAddressDataRemote(1, "", mTraspoAdRemoteListener);
        //initFragment();
    }

    private void initFragment(List<WareHouseBean> wareHouseBeans, boolean hasMore) {
        mSubAdFragments = new LinkedList<>();
        String countryCode = TranSportAdManager.getInstance().getCountryCode();
        List<CountryBean> countryBeanList = TranSportAdManager.getInstance().getCountryBeanList();
        for (CountryBean countryBean : countryBeanList) {
            Bundle bundle = new Bundle();
            TraspoAddressBean traspoAddressBean = new TraspoAddressBean();
            traspoAddressBean.setCountryCode(countryBean.getCountryCode());
            traspoAddressBean.setCountryName(countryBean.getCountryName());
            if (countryBean.getCountryCode().equals(countryCode)) {
                traspoAddressBean.setWareHouseBeanList(wareHouseBeans);
                traspoAddressBean.setHasMore(hasMore);
                bundle.putSerializable("bean", traspoAddressBean);
                TranSportSubAdFragment instance = TranSportSubAdFragment.getInstance(bundle);
                mSubAdFragments.addFirst(instance);
            } else {
                bundle.putSerializable("bean", traspoAddressBean);
                TranSportSubAdFragment instance = TranSportSubAdFragment.getInstance(bundle);
                mSubAdFragments.add(instance);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TraSpoAddrPageAdapter traSpoAddrPageAdapter = new TraSpoAddrPageAdapter(getSupportFragmentManager(), mSubAdFragments);
                mPager.setAdapter(traSpoAddrPageAdapter);
                mTab.setViewPager(mPager);
            }
        });
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_in_title_back_tv:
                finish();
                break;
        }
    }
}
