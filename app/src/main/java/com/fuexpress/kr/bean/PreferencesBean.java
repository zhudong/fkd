package com.fuexpress.kr.bean;

/**
 * Created by Longer on 2016/11/1.
 */
public class PreferencesBean {
    private String beanString;
    private boolean isSelected;
    private String beanCode;
    private int beanID;
    private String beanSign;

    public int getBeanID() {
        return beanID;
    }

    public void setBeanID(int beanID) {
        this.beanID = beanID;
    }

    public String getBeanSign() {
        return beanSign;
    }

    public void setBeanSign(String beanSign) {
        this.beanSign = beanSign;
    }

    public String getBeanCode() {
        return beanCode;
    }

    public void setBeanCode(String beanCode) {
        this.beanCode = beanCode;
    }

    public String getBeanString() {
        return beanString;
    }

    public void setBeanString(String beanString) {
        this.beanString = beanString;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
