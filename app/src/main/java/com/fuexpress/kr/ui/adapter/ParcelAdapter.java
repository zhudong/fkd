package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import java.util.List;

import fksproto.CsCard;


/**
 * Created by Administrator on 2016/7/4.
 */
public class ParcelAdapter extends BaseAdapter {
    private Context mCtx;
    private List<CsCard.ParcelListData> list;
    private String currencyCode;

    public ParcelAdapter(Context context, List<CsCard.ParcelListData> list){
        this.mCtx = context;
        this.list = list;
    }

    public ParcelAdapter(Context context, List<CsCard.ParcelListData> list, String currencyCode){
        this.mCtx = context;
        this.list = list;
        this.currencyCode = currencyCode;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.parcel_item, null);
            holder = new Holder();
            holder.parcelNameTv = (TextView) convertView.findViewById(R.id.card_order_detail_parcel_name_tv);
//            holder.couponLayout = (LinearLayout) convertView.findViewById(R.id.recharge_order_detail_coupon_layout);
//            holder.couponTitleTv = (TextView) convertView.findViewById(R.id.recharge_order_detail_coupon_title_tv);
//            holder.couponTv = (TextView) convertView.findViewById(R.id.card_order_detail_coupon_tv);
            holder.freightTv = (TextView) convertView.findViewById(R.id.card_order_detail_freight_tv);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        CsCard.ParcelListData parcel = list.get(position);
        holder.parcelNameTv.setText(parcel.getParcelName());
//        if(parcel.getDicountamount() > 0){
//            holder.couponLayout.setVisibility(View.VISIBLE);
//            holder.couponTv.setText(mCtx.getString(R.string.String_price, parcel.getDicountamount()));
//        }else {
//            holder.couponLayout.setVisibility(View.GONE);
//            holder.couponTv.setVisibility(View.GONE);
//        }
          holder.freightTv.setText(UIUtils.getCurrency(mCtx, currencyCode, parcel.getEstimateShippingFee()));
//        if(!TextUtils.isEmpty(currencyCode)){
//        }else {
//            holder.freightTv.setText(UIUtils.getCurrency(mCtx, currencyCode, parcel.getEstimateShippingFee()));
//        }
        return convertView;
    }

    class Holder{
        TextView parcelNameTv;
//        LinearLayout couponLayout;
//        TextView couponTitleTv;
//        TextView couponTv;
        TextView freightTv;
    }
}
