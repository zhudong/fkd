package com.fuexpress.kr.bean;

import java.util.List;

/**
 * Created by Administrator on 2016-10-27.
 */
public class NeedBean {
    String date;
    String state;
    String type;
    String []imgs;

    public NeedBean(String date, String state, String type, String [] imgs) {
        this.date = date;
        this.state = state;
        this.type = type;
        this.imgs = imgs;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String [] getImgs() {
        return imgs;
    }

    public void setImgs(String []imgs) {
        this.imgs = imgs;
    }
}
