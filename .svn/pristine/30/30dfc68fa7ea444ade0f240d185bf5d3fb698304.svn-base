package com.fuexpress.kr.ui.activity.transport_address;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Longer on 2017/5/23.
 * 转运地址列表的pagerAdapter
 */
public class TraSpoAddrPageAdapter extends FragmentPagerAdapter {

    private List<TranSportSubAdFragment> mSubAdFragments;

    public TraSpoAddrPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public TraSpoAddrPageAdapter(FragmentManager fm, @NonNull List<TranSportSubAdFragment> subAdFragmentList) {
        super(fm);
        mSubAdFragments = subAdFragmentList;
    }



    @Override
    public Fragment getItem(int position) {
        return mSubAdFragments.get(position);
    }

    @Override
    public int getCount() {
        return mSubAdFragments.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mSubAdFragments.get(position).getTitle();
    }
}
