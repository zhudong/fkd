package com.fuexpress.kr.ui.activity.produ_source;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.ShopCartManager;
import com.fuexpress.kr.ui.activity.shopping_cart.ShopCartActivity;
import com.fuexpress.kr.ui.view.SlidingTabLayout;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longer on 2017/6/23.
 */
public class ProduSrcMainFragment extends BaseFragment<MainActivity> {

    private View mRootView;
    private TextView mTv_title_cart_amount_note;
    private SlidingTabLayout mTab;
    private ViewPager mPager;
    private List<ProduSrcSubFragment> mProduSrcSubFragments;
    private TitleBarView titleBarView;

    @Override
    protected void initTitleBar() {
        titleBarView = (TitleBarView) mRootView.findViewById(R.id.title);
        titleBarView.getFl_title_cart().setOnClickListener(this);
        mTv_title_cart_amount_note = titleBarView.getTv_title_cart_amount_note();

    }

    @Override
    public View initView() {
        mRootView = View.inflate(getActivity(), R.layout.layout_produ_src_main, null);
        mTab = (SlidingTabLayout) mRootView.findViewById(R.id.tab);
        mPager = (ViewPager) mRootView.findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);

        mTab.setTabTitleTextSize(15);
        mTab.setTheTextAllCaps(false);
        mTab.setTitleTextColor(Color.BLACK, Color.GRAY);
        mTab.setSelectedIndicatorColors(Color.alpha(0));
        mTab.setDistributeEvenly(true);
        mTab.setTheTextAllCaps(false);
        return mRootView;
    }

    @Override
    public void initData() {
        initFragment();
        //ShopCartManager.getInstance().getSaleCartCount();
    }

    private void initFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("key", ProduSrcSubFragment.KEY_ALL);
        ProduSrcSubFragment produSrcSubFragmentInstanceAll = ProduSrcSubFragment.getProduSrcSubFragmentInstance(bundle);
        if (mProduSrcSubFragments == null) mProduSrcSubFragments = new ArrayList<>();
        mProduSrcSubFragments.add(produSrcSubFragmentInstanceAll);

        bundle = new Bundle();
        bundle.putString("key", ProduSrcSubFragment.KEY_HOT);
        ProduSrcSubFragment produSrcSubFragmentInstanceHot = ProduSrcSubFragment.getProduSrcSubFragmentInstance(bundle);
        mProduSrcSubFragments.add(produSrcSubFragmentInstanceHot);

        bundle = new Bundle();
        bundle.putString("key", ProduSrcSubFragment.KEY_FOLLOWED);
        ProduSrcSubFragment produSrcSubFragmentInstanceFollowed = ProduSrcSubFragment.getProduSrcSubFragmentInstance(bundle);
        mProduSrcSubFragments.add(produSrcSubFragmentInstanceFollowed);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProduSrcMainViewPagerAdapter traSpoAddrPageAdapter = new ProduSrcMainViewPagerAdapter(getChildFragmentManager(), mProduSrcSubFragments);
                mPager.setAdapter(traSpoAddrPageAdapter);
                mTab.setViewPager(mPager);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_title_cart:
                // TODO: 2017/7/10 跳转去购物车
                Intent intent = new Intent(mContext, ShopCartActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.SHOP_CART_COMMODITY_COUNT:
                TextView tv_title_cart_amount_note = titleBarView.getTv_title_cart_amount_note();
                if (event.getIntParam() > 0) {
                    tv_title_cart_amount_note.setVisibility(View.VISIBLE);
                    tv_title_cart_amount_note.setText(String.valueOf(event.getIntParam()));
                } else tv_title_cart_amount_note.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("我被显示出来了");
        ShopCartManager.getInstance().getSaleCartCount();
    }
}
