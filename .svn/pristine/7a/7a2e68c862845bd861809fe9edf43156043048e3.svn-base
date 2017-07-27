package com.fuexpress.kr.ui.activity.choose_address;

import java.util.List;

import fksproto.CsAddress;

/**
 * Created by Longer on 2017/4/27.
 */
public class AddressResponBean {
    List<CsAddress.DirectoryRegionInfo> regionInfoList;//地区列表
    List<CsAddress.DirectoryCountryInfo> mDirectoryCountryInfos;//国家列表
    String drop;//more(有更多可以下拉) nomore(没有更多)
    String currentRegionId;//当前地区ID(新增不需要,编辑才需要)
    String currentRegionName;//当前地区名称(新增不需要,编辑才需要)
    String subRegion;//more(有子地区) nomore(没有子地区)
    boolean hasMore = false;

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<CsAddress.DirectoryCountryInfo> getDirectoryCountryInfos() {
        return mDirectoryCountryInfos;
    }

    public void setDirectoryCountryInfos(List<CsAddress.DirectoryCountryInfo> directoryCountryInfos) {
        mDirectoryCountryInfos = directoryCountryInfos;
    }

    public List<CsAddress.DirectoryRegionInfo> getRegionInfoList() {
        return regionInfoList;
    }

    public void setRegionInfoList(List<CsAddress.DirectoryRegionInfo> regionInfoList) {
        this.regionInfoList = regionInfoList;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getCurrentRegionId() {
        return currentRegionId;
    }

    public void setCurrentRegionId(String currentRegionId) {
        this.currentRegionId = currentRegionId;
    }

    public String getCurrentRegionName() {
        return currentRegionName;
    }

    public void setCurrentRegionName(String currentRegionName) {
        this.currentRegionName = currentRegionName;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public void setSubRegion(String subRegion) {
        this.subRegion = subRegion;
    }
}
