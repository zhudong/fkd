package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.activity.DemandsDetailActivity;
import com.fuexpress.kr.ui.activity.my_order.OrderAll;
import com.fuexpress.kr.ui.activity.order_detail.OrderDetailPayedActivity;
import com.fuexpress.kr.ui.activity.package_detail.PackageDetailActivity;
import com.fuexpress.kr.ui.view.ParceOrderItemView;
import com.fuexpress.kr.ui.view.ParcelItemView2;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsParcel;
import fksproto.CsUser;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


public class TestBaseAdapter1 extends BaseAdapter implements
        StickyListHeadersAdapter {

    private final Context mContext;
    List<CsParcel.ParcelItemList> salesOrderItems;

    private LayoutInflater inflater;

    public TestBaseAdapter1(Context context, List<CsParcel.ParcelItemList> salesOrderItems) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.salesOrderItems = salesOrderItems;
    }

    public void setData(ArrayList<CsParcel.ParcelItemList> salesOrderItems) {
        this.salesOrderItems = salesOrderItems;
    }

    @Override
    public int getCount() {
        if (salesOrderItems != null) {
            return salesOrderItems.size();
        }
        return 0;
    }

    @Override
    public CsParcel.ParcelItemList getItem(int position) {
        return salesOrderItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            ParceOrderItemView parcelItemView = new ParceOrderItemView(mContext);
            convertView = parcelItemView;
            holder.mParcelItemView = parcelItemView;
            convertView.setTag(holder);
        }
        holder = (Holder) convertView.getTag();
        holder.mParcelItemView.initData(getItem(position));
        convertView.setTag(R.id.img_parcle_item_icon, position);
        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.item_parcle_item_header, parent, false);
            holder.mTvOrderNumber = (TextView) convertView.findViewById(R.id.tv_order_number);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        CsParcel.ParcelItemList salesOrderItem = salesOrderItems.get(position);
        String orderNumber = salesOrderItem.getOrderNumber();
        holder.mTvOrderNumber.setText(orderNumber);
        ViewGroup parent1 = (ViewGroup) holder.mTvOrderNumber.getParent();
        for (int i = 0; i < parent1.getChildCount(); i++) {
            parent1.getChildAt(i).setVisibility(TextUtils.isEmpty(orderNumber) ? View.GONE : View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
       CsParcel.ParcelItemList salesOrderItem = salesOrderItems.get(position);
        return salesOrderItem.getSalesOrderId();
    }



    class HeaderViewHolder {
        TextView mTvOrderNumber;
    }


    static class Holder {
        ParceOrderItemView mParcelItemView;
    }

}
