package com.fuexpress.kr.ui.activity.produ_source;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.ui.activity.transport_address.TranSportSubAdFragment;

import java.util.List;

/**
 * Created by Longer on 2017/6/26.
 * 货源的主页ViewPagerAdapter(用以实现滑动页面)
 */
public class ProduSrcMainViewPagerAdapter extends FragmentPagerAdapter {

    private List<ProduSrcSubFragment> mSubAdFragments;

    public ProduSrcMainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ProduSrcMainViewPagerAdapter(FragmentManager fm, @NonNull List<ProduSrcSubFragment> subAdFragmentList) {
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
