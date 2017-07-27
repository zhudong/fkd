package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import java.util.List;

import fksproto.CsCard;
import fksproto.CsUser;


/**
 * Created by Administrator on 2016-10-25.
 */
public class CouponAdapter extends BaseAdapter {

    private Context context;
    private List<CsUser.CouponList> list;
    private String currencyCode;

    public CouponAdapter(Context context, List<CsUser.CouponList> list){
        this.context = context;
        this.list = list;
    }

    public CouponAdapter(Context context, List<CsUser.CouponList> list, String currencyCode){
        this.context = context;
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
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.coupon_item, null);
            holder = new ViewHolder();
            holder.titleTv = (TextView) convertView.findViewById(R.id.coupon_title_tv);
            holder.currencyTv = (TextView) convertView.findViewById(R.id.coupon_item_currency_tv);
            holder.amountTv = (TextView) convertView.findViewById(R.id.coupon_item_amount_tv);
            holder.usesTv = (TextView) convertView.findViewById(R.id.coupon_item_uses_tv);
            holder.descTv = (TextView) convertView.findViewById(R.id.coupon_item_desc_tv);
            holder.startTimeTv = (TextView) convertView.findViewById(R.id.coupon_item_start_time_tv);
            holder.endTimeTv = (TextView) convertView.findViewById(R.id.coupon_item_end_time_tv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        CsUser.CouponList item = list.get(position);
        holder.titleTv.setText(item.getShippingcouponname());
        if(!TextUtils.isEmpty(currencyCode)){
            holder.currencyTv.setText(UIUtils.getCurrency(context, currencyCode));
        }
        String amount = UIUtils.getCurrency(context, currencyCode, item.getDiscountamount(), 1001,true);
        holder.amountTv.setText(amount);
        holder.usesTv.setText(context.getString(R.string.coupon_item_uses_msg, item.getUses()));
        holder.descTv.setText(item.getDescription());
        holder.startTimeTv.setText(item.getStarttime());
        holder.endTimeTv.setText(item.getEndtime());

        return convertView;
    }

    class ViewHolder{
        TextView titleTv;
        TextView currencyTv;
        TextView amountTv;
        TextView usesTv;
        TextView descTv;
        TextView startTimeTv;
        TextView endTimeTv;
    }
}