package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import java.util.List;

import fksproto.CsOrder;

/**
 * Created by k550 on 2016/9/27.
 */

public class ShippingAdapter extends SimpleBaseAdapter<CsOrder.SalesOrderShipping> {
    private String code;
    public ShippingAdapter(Activity activity, List<CsOrder.SalesOrderShipping> mDatas){
        super(activity,mDatas);
    }
    public ShippingAdapter(Activity activity, List<CsOrder.SalesOrderShipping> mDatas, String code){
        super(activity,mDatas);
        this.code=code;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CsOrder.SalesOrderShipping item=(CsOrder.SalesOrderShipping)getItem(position);
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContent, R.layout.adapter_shipping, null);
            holder.warehouseTv =(TextView)convertView.findViewById(R.id.shipping_warehouse_tv);
            holder.methodTv=(TextView)convertView.findViewById(R.id.shipping_method_tv);
            holder.feeTv=(TextView)convertView.findViewById(R.id.shipping_fee_tv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.warehouseTv.setText(item.getWarehouseName());
        holder.methodTv.setText(item.getShippingMethod());
        holder.feeTv.setText(UIUtils.getCurrency(mContent,code,item.getShippingFee()));
        return convertView;
    }
    static class Holder {
        TextView warehouseTv;
        TextView methodTv;
        TextView feeTv;
    }
}