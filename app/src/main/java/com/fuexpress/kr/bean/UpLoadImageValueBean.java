package com.fuexpress.kr.bean;

import com.fuexpress.kr.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longer on 2016/12/15.
 * 上传图片需要用到的参数
 */
public class UpLoadImageValueBean {

    List<String> imagePathList;

    int type;

    int index;

    String onlyOneImagePath;

    List<String> tempCompleteList = new ArrayList<>();


    public List<String> getTempCompleteList() {
        return tempCompleteList;
    }

    private UpLoadImageValueBean() {

    }


    public List<String> getImagePathList() {
        return imagePathList;
    }

    /*public void setImagePathList(List<String> imagePathList) {
        this.imagePathList = imagePathList;
    }*/

    public int getType() {
        return type;
    }

    /*public void setType(int type) {
        this.type = type;
    }*/

    public int getIndex() {
        return index;
    }

    /*public void setIndex(int index) {
        this.index = index;
    }*/

    public String getOnlyOneImagePath() {
        return onlyOneImagePath;
    }

    /*public void setOnlyOneImagePath(String onlyOneImagePath) {
        this.onlyOneImagePath = onlyOneImagePath;
    }*/

    public static class Builder {
        private UpLoadImageValueBean mUpLoadImageValueBean = new UpLoadImageValueBean();

        public Builder setImagePathList(List<String> imagePathList) {
            mUpLoadImageValueBean.imagePathList = imagePathList;
            return this;
        }

        public Builder() {

        }

        public Builder setType(int type) {
            mUpLoadImageValueBean.type = type;
            return this;
        }

        public Builder setIndex(int index) {
            mUpLoadImageValueBean.index = index;
            return this;
        }

        public Builder setOneImagePath(String imagePath) {
            mUpLoadImageValueBean.onlyOneImagePath = imagePath;
            return this;
        }

        public UpLoadImageValueBean build() {
            return mUpLoadImageValueBean;
        }

    }
}
