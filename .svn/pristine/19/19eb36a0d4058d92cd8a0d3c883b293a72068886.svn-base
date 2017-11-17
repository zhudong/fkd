package com.fuexpress.kr.bean;

import android.graphics.Bitmap;

import com.fuexpress.kr.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2017/8/4.
 */

public class ShareFriendsBean {
    private String mBitmap;
    private String title;// = .getString(R.string.share_friend_title);
    private String info;//= .getString(R.string.share_friend_info);
    private String url = "http://www.baidu.com";
    private List<String> imgs = new ArrayList<>();

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        if (imgs != null && imgs.size() > 0) {
            return imgs.get(0);
        } else {
            return "";
        }
    }

    public String getBitmap() {
        return mBitmap;
    }

    public void setBitmap(String bitmap) {
        mBitmap = bitmap;
    }
}
