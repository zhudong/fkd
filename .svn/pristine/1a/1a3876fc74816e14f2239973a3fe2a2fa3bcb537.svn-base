package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.widget.BaseAdapter;

import com.fuexpress.kr.model.AccountManager;

import java.util.List;

import fksproto.CsCrowd;

/**
 * Created by yuan on 2016-7-10.
 */
public abstract class CrowdAdapter extends BaseAdapter {
    protected Activity mContent;
    protected List<CsCrowd.Crowd> mCrowds;

    public CrowdAdapter(Activity content, List<CsCrowd.Crowd> crowds) {
        mContent = content;
        mCrowds = crowds;
    }


    public abstract void setData(List<CsCrowd.Crowd> data);

    public List<CsCrowd.Crowd> getData() {
        return mCrowds;
    }

    ;

    public void notiRefresh() {
        this.notifyDataSetChanged();
    }

    public float formateNum(float num) {
        String currencyCode = AccountManager.getInstance().getCurrencyCode();
        if (currencyCode != null && currencyCode.contains("en")) {
            return num * 10;
        } else {
            return num;
        }
    }
}
