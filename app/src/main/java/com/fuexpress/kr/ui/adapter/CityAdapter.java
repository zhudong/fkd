package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fuexpress.kr.ui.activity.ChooseCityActivity;
import com.fuexpress.kr.ui.activity.IntegralActivity;
import com.fuexpress.kr.ui.activity.coupon.CouponsActivity;

import java.util.List;

/**
 * Created by root on 17-3-28.
 */

public class CityAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private List<String> mData;

    public CityAdapter(FragmentManager fm,Context context, List<String> list){
        super(fm);
        this.mContext=context;
        this.mData=list;
    }
    @Override
    public Fragment getItem(int position) {
        if(mContext instanceof ChooseCityActivity){
            return ((ChooseCityActivity) mContext).getFragment(position);
        }else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mData.get(position);
    }
}
