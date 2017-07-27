package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;

import fksproto.CsBase;

/**
 * Created by Longer on 2016/12/8.
 * 仓库视图的自定义布局类
 */
public class WareHouseInfoView extends LinearLayout {

    private LinearLayout mLl_wh_overall;
    private TextView mTv_wh_address;
    private TextView mTv_wh_phone;
    private TextView mTv_wh_post_code;
    private TextView mTv_wh_receiver;

    public WareHouseInfoView(Context context) {
        this(context, null);
    }

    public WareHouseInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WareHouseInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.ware_info_layout, this);
        mLl_wh_overall = (LinearLayout) findViewById(R.id.ll_wh_overall);
        mTv_wh_address = (TextView) findViewById(R.id.tv_wh_address);
        mTv_wh_phone = (TextView) findViewById(R.id.tv_wh_phone);
        mTv_wh_post_code = (TextView) findViewById(R.id.tv_wh_post_code);
        mTv_wh_receiver = (TextView) findViewById(R.id.tv_wh_receiver);
    }


    public void setWareHouseShow(@NonNull CsBase.Warehouse wareHouseShow) {
        mLl_wh_overall.setVisibility(VISIBLE);
        mTv_wh_address.setText(wareHouseShow.getFulladdress());
        mTv_wh_phone.setText(wareHouseShow.getPhone());
        mTv_wh_post_code.setText(wareHouseShow.getPostcode());
        mTv_wh_receiver.setText(wareHouseShow.getReceiver());
    }
}
