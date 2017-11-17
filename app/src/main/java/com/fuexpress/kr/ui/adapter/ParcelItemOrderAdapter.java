package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.bean.ParcelItemBean;
import com.fuexpress.kr.ui.view.ParceOrderItemView;
import com.fuexpress.kr.ui.view.ParcelItemView;
import com.fuexpress.kr.ui.view.ParcelItemView2;

import java.util.List;

import fksproto.CsParcel;


/**
 * Created by yuan on 2016-6-15.
 */
public class ParcelItemOrderAdapter extends SimpleBaseAdapter<CsParcel.ParcelItemList> {

    public ParcelItemOrderAdapter(Activity content, List<CsParcel.ParcelItemList> data) {
        super(content, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if ("0".equals(getItem(position).getType())) {
                convertView = new ParcelItemView2(mContent);
            } else {
                convertView = new ParceOrderItemView(mContent);
            }
        }
        CsParcel.ParcelItemList item = getItem(position);

        if ("0".equals(getItem(position).getType())) {
            ParcelItemView2 parcelItemView = (ParcelItemView2) convertView;
            parcelItemView.setItem(item);
        } else {
            ParceOrderItemView parcelItemView = (ParceOrderItemView) convertView;
            parcelItemView.initData(item);
        }
        return convertView;
    }
}
