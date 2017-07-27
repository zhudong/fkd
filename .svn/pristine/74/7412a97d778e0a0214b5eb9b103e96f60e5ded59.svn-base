package com.fuexpress.kr.bean;

import com.fuexpress.kr.ui.activity.help_signed.data.WareHouseBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longer on 2017/5/25.
 * 转运地址的封装Bean
 */
public class TraspoAddressBean implements Serializable {

    boolean hasMore;

    List<WareHouseBean> mWareHouseBeanList;

    String countryName = "";

    String countryCode = "";

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<WareHouseBean> getWareHouseBeanList() {
        if (mWareHouseBeanList == null)
            mWareHouseBeanList = new ArrayList<>();
        return mWareHouseBeanList;
    }

    public void setWareHouseBeanList(List<WareHouseBean> wareHouseBeanList) {
        mWareHouseBeanList = wareHouseBeanList;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
