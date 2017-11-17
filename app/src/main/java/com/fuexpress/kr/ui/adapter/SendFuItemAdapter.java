package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SimpleBaseAdapter;
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
public class SendFuItemAdapter extends SimpleBaseAdapter<CsParcel.ParcelItemList> {

    private final DisplayImageOptions options;
    private final ImageLoader imageLoader;

    public SendFuItemAdapter(Activity content, List<CsParcel.ParcelItemList> data) {
        super(content, data);
        options = ImageLoaderHelper.getInstance(mContent).getDisplayOptions();
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = View.inflate(mContent, R.layout.view_send_fu_item, null);
        ImageView IvIcon = (ImageView) convertView.findViewById(R.id.img_icon);
        IvIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        TextView mTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView mCount = (TextView) convertView.findViewById(R.id.tv_count);

        CsParcel.ParcelItemList item = (CsParcel.ParcelItemList) getItem(position);
        mTitle.setText(item.getName());
        mCount.setText("×" + item.getQtyPack());
        imageLoader.displayImage(item.getImageurl()+ Constants.ImgUrlSuffix.dp_list,IvIcon,options);
        return convertView;
    }
}
