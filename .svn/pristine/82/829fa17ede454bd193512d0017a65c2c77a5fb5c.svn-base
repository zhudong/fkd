package com.fuexpress.kr.ui.view.wheel.wheeladapter;

import android.content.Context;

import com.fuexpress.kr.bean.RegionBean;

import java.util.List;

/**
 * Created by Longer on 2017/3/1.
 * 地址Bean的专用WheelViewAdapter
 */
public class RegionWheelAdapter extends AbstractWheelTextAdapter {

    List<RegionBean> mRegionBeanList;

    public RegionWheelAdapter(Context context, List<RegionBean> regionBeans) {
        super(context);
        mRegionBeanList = regionBeans;
    }

    @Override
    protected CharSequence getItemText(int index) {
        if (index >= 0 && index < mRegionBeanList.size()) {
            //T item = items[index];
            RegionBean regionBean = mRegionBeanList.get(index);
            return regionBean.getRegionName();
        }
        return "";
    }

    @Override
    public int getItemsCount() {
        return mRegionBeanList.size();
    }
}
