package com.fuexpress.kr.bean;

/**
 * Created by andy on 2017/6/27.
 */
public class BottomStateBean {
    int requirecount;
    int parcelcount;
    boolean showResource;
    boolean showGroup;


    public BottomStateBean() {
    }

    public BottomStateBean(int requirecount, int parcelcount, String showResource, String showGroup) {
        this.requirecount = requirecount;
        this.parcelcount = parcelcount;
        this.showResource = "1".equals(showResource);
        this.showGroup = "1".equals(showGroup);
    }

    public int getRequirecount() {
        return requirecount;
    }

    public void setRequirecount(int requirecount) {
        this.requirecount = requirecount;
    }

    public int getParcelcount() {
        return parcelcount;
    }

    public void setParcelcount(int parcelcount) {
        this.parcelcount = parcelcount;
    }

    public boolean isShowResource() {
        return showResource;
//        return true;
    }

    public void setShowResource(String showResource) {
//        = 4;//1代表显示货源,0代表不显示货源
        this.showResource = "1".equals(showResource);
    }

    public boolean isShowGroup() {
        //同上
        return showGroup;
    }

}
