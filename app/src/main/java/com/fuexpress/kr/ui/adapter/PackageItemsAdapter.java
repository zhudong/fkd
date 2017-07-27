package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.activity.package_detail.PackageDetailActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.google.protobuf.ProtocolStringList;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;
import fksproto.CsParcel;


/**
 * Created by yuan on 2016-5-3.
 */
public class PackageItemsAdapter extends SimpleBaseAdapter<CsParcel.Parcel> implements View.OnClickListener {
    private final int mSize;
    private final String sToSend;
    private final String sSended;
    private final String sWaitReceive;
    private final int mWidth;
    private final int mMarginLeft;
    private final String sWaitOut;
    private final String sTopay;

    public PackageItemsAdapter(Activity context, List<CsParcel.Parcel> parcels) {
        super(context, parcels);
        mWidth = UIUtils.dip2px((float) 57.3);
        mMarginLeft = UIUtils.dip2px((float) 10);
        int padding = UIUtils.dip2px((float) 1);
        int screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
        sWaitReceive = context.getString(R.string.String_waite_receive);
        sTopay = context.getString(R.string.String_parcel_goto_pay);
        sSended = context.getString(R.string.String_sended);
        sToSend = context.getString(R.string.String_parcel_goto_send);
        sWaitOut = context.getString(R.string.String_waite_out);

        mSize = (screenWidth - 2 * mMarginLeft) / (mWidth + mMarginLeft) + 1;

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holer holer = null;
        if (convertView == null) {
            holer = new Holer();
            convertView = View.inflate(mContent, R.layout.item_for_packages, null);
            holer.mLlCovers = (LinearLayout) convertView.findViewById(R.id.ll_pckage_covers);
            holer.mTvNumber = (TextView) convertView.findViewById(R.id.tv_package_number);
            holer.mTvState = (TextView) convertView.findViewById(R.id.tv_parcle_state);
            convertView.setTag(holer);
        } else {
            holer = (Holer) convertView.getTag();
        }

        CsParcel.Parcel parcel = (CsParcel.Parcel) getItem(position);
        String status = parcel.getStatus();
//                状态,{Submitted: 已提交,Payed: 待入库,Instore： 已入库,Notice：待出库,Checked: 已审核,Shipped: 已发货}

        if ("Submitted".equals(status)) {
            holer.mTvState.setText(sTopay);
        } else if ("Payed".equals(status)) {
            holer.mTvState.setText(sWaitReceive);
        } else if ("Instore".equals(status)) {
            holer.mTvState.setText(sToSend);
        } else if ("Notice".equals(status)) {
            holer.mTvState.setText(sWaitOut);
        } else if ("Checked".equals(status)) {
            holer.mTvState.setText(sWaitOut);
        } else if ("Shipped".equals(status)) {
            holer.mTvState.setText(sSended);
        }

        holer.mTvNumber.setText(parcel.getParcelName());

        ProtocolStringList imageListList = parcel.getImageListList();
        List<String> imgs = new ArrayList<>();
        for (int i = 0; i < imageListList.size(); i++) {
            String s = imageListList.get(i);
            String[] split = s.split(",");
            for (int j = 0; j < split.length; j++) {
                imgs.add(split[j]);
            }
        }

        addCovers(holer.mLlCovers, imgs);
        convertView.setTag(R.id.ll_pckage_covers, position);
        convertView.setOnClickListener(this);
        return convertView;
    }

    private void addCovers(LinearLayout llCovers, List<String> split) {
        int len = mSize < split.size() ? mSize : split.size();
        llCovers.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mWidth, mWidth);
        params.setMargins(0, 0, mMarginLeft, 0);
        for (int i = 0; i < len; i++) {
            ImageView imageView = new ImageView(mContent);
            ImageLoader.getInstance().displayImage(split.get(i), imageView, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
            llCovers.addView(imageView, params);
        }
    }

    private void toDetail(int position) {

//        Bundle bundle = new Bundle();
//        bundle.putSerializable(PackageDetailActivity.PARCEL_BEAN, parcel);
//        intent.putExtra(PackageDetailActivity.PARCEL_BEAN, bundle);
        Intent intent = new Intent(mContent, PackageDetailActivity.class);
        CsParcel.Parcel parcel = mData.get(position);
        intent.putExtra(PackageDetailActivity.PARCEL_ID, parcel.getParcelId());
//        intent.putExtra(PackageDetailActivity.FROM_WHERE, "");
        mContent.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int postion = (int) v.getTag(R.id.ll_pckage_covers);
        toDetail(postion);
    }


    static class Holer {
        TextView mTvNumber;
        TextView mTvState;
        LinearLayout mLlCovers;
    }
}

