package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.bean.ParcelItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.ParcelItemView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import fksproto.CsParcel;


/**
 * Created by yuan on 2016-6-15.
 */
public class ParcelItemAdapter extends SimpleBaseAdapter<CsParcel.ParcelItemList> {

    public ParcelItemAdapter(Activity content, List<CsParcel.ParcelItemList> data) {
        super(content, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      /*  convertView = View.inflate(mContent, R.layout.item_parcel_item, null);
        TextView title = (TextView) convertView.findViewById(R.id.tv_parcle_tiel);
        ImageView icon = (ImageView) convertView.findViewById(R.id.iv_parcel_icon);
        icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        TextView count = (TextView) convertView.findViewById(R.id.tv_parcle_items);*/
        if (convertView == null) {
            convertView = new ParcelItemView(mContent, 1);
        }

        ParcelItemView parcelItemView = (ParcelItemView) convertView;
        CsParcel.ParcelItemList item = (CsParcel.ParcelItemList) getItem(position);
        parcelItemView.setTitle(item.getName());
        parcelItemView.setCount("×" + item.getQty());
        parcelItemView.setIvIcon(item.getImageurl());
        parcelItemView.setPrice(UIUtils.getCurrency(mContent, item.getPriceCurrencyCode(), item.getPrice()));
        return convertView;
    }

}
