package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fuexpress.kr.bean.CouponCurrencyInfoData;
import com.fuexpress.kr.model.CouponDataManager;
import com.fuexpress.kr.ui.activity.IntegralActivity;
import com.fuexpress.kr.ui.activity.coupon.CouponsActivity;

import java.util.List;

import fksproto.CsUser;


/**
 * Created by kevin.xie on 2016/10/25.
 */

public class CouponCurrencyDataAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private List<CouponCurrencyInfoData> mDatas;
    private List<CsUser.CurrencyList> mCurrencyDatas;

    public CouponCurrencyDataAdapter(FragmentManager fm, Context context, List<CouponCurrencyInfoData> data) {
        super(fm);
        this.mContext = context;
        this.mDatas = data;
    }

    public CouponCurrencyDataAdapter(FragmentManager fm, Context context, List<CsUser.CurrencyList> data, boolean isIntegral) {
        super(fm);
        this.mContext = context;
        this.mCurrencyDatas = data;
    }

    @Override
    public Fragment getItem(int position) {
        if (mContext instanceof CouponsActivity) {
            return ((CouponsActivity) mContext).getFragment(position);
        } else if (mContext instanceof IntegralActivity) {
            return ((IntegralActivity) mContext).getFragment(position);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if (mContext instanceof CouponsActivity) {
            return mDatas.size();
        } else if (mContext instanceof IntegralActivity) {
            return mCurrencyDatas.size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mContext instanceof CouponsActivity) {
            return mDatas.get(position).currencyName +
                    "(" + mDatas.get(position).count + ")";
        } else if (mContext instanceof IntegralActivity) {
            return mCurrencyDatas.get(position).getCurrencyname();
        } else {
            return null;
        }
    }
}