package com.fuexpress.kr.bean;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;

import java.io.Serializable;

/**
 * Created by Longer on 2017/1/17.
 * 选择分类的Bean
 */
public class CategoryBean implements Serializable {

    private String parentName;

    private int parentID = -1;

    private String subName;

    private int subID = -1;


    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public int getSubID() {
        return subID;
    }

    public void setSubID(int subID) {
        this.subID = subID;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryBean that = (CategoryBean) o;

        if (parentID != that.parentID) return false;
        if (subID != that.subID) return false;
        if (parentName != null ? !parentName.equals(that.parentName) : that.parentName != null)
            return false;
        return !(subName != null ? !subName.equals(that.subName) : that.subName != null);

    }

    @Override
    public int hashCode() {
        int result = parentName != null ? parentName.hashCode() : 0;
        result = 31 * result + parentID;
        result = 31 * result + (subName != null ? subName.hashCode() : 0);
        result = 31 * result + subID;
        return result;
    }
}
