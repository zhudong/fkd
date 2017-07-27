package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import java.util.List;

import fksproto.CsCard;


/**
 * Created by Administrator on 2016/5/11.
 */
public class CardOrderDetailAdapter extends BaseAdapter {
    private Context context;
    private List<CsCard.GiftCardInfoItem> list;
    private String currencyCode;

    public CardOrderDetailAdapter(Context context, List<CsCard.GiftCardInfoItem> list, String currencyCode){
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
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.card_order_detail_card_item, null);
            holder = new ViewHolder();
            holder.mailTv = (TextView) convertView.findViewById(R.id.card_order_detail_item_mail_tv);
            holder.titlePriceTv = (TextView) convertView.findViewById(R.id.card_order_detail_item_title_price_tv);
            holder.faceValueTv = (TextView) convertView.findViewById(R.id.card_order_detail_item_face_value_tv);
            holder.priceTv = (TextView) convertView.findViewById(R.id.card_order_detail_item_price_tv);
            holder.countTv = (TextView) convertView.findViewById(R.id.card_order_detail_item_count_tv);
            holder.subTotalTv = (TextView) convertView.findViewById(R.id.card_order_detail_item_sub_total_tv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        CsCard.GiftCardInfoItem item = list.get(position);
        holder.mailTv.setText(item.getRecipimentEmail());
//        String Price = context.getResources().getString(R.string.gift_card_item_price, UIUtils.getCurrency(context, currencyCode), item.getAmount());
        String amoutPrice = context.getResources().getString(R.string.gift_card_item_price, UIUtils.getCurrency(context, currencyCode), item.getAmount());
        holder.priceTv.setText(UIUtils.getCurrency(context, currencyCode, item.getAmount()));

        String titlePrice = context.getResources().getString(R.string.gift_card_item_price, UIUtils.getCurrency(context, currencyCode), item.getFaceAmount());
        holder.titlePriceTv.setText(UIUtils.getCurrency(context, currencyCode, item.getFaceAmount()));

//        String facePrice = UIUtils.getCurrency(context, currencyCode) + item.getFaceAmount();
        String facePrice = context.getResources().getString(R.string.gift_card_item_price, UIUtils.getCurrency(context, currencyCode), item.getFaceAmount());
        holder.faceValueTv.setText(UIUtils.getCurrency(context, currencyCode, item.getFaceAmount()));

        holder.countTv.setText(item.getQty() + "");
//        String subPrice = UIUtils.getCurrency(context, currencyCode) + (item.getAmount() * item.getQty());
        String subPrice = context.getResources().getString(R.string.gift_card_item_price, UIUtils.getCurrency(context, currencyCode), (item.getAmount() * item.getQty()));
        holder.subTotalTv.setText(UIUtils.getCurrency(context, currencyCode, (item.getAmount() * item.getQty())));
        return convertView;
    }

    class ViewHolder{
        TextView mailTv;
        TextView titlePriceTv;
        TextView faceValueTv;
        TextView priceTv;
        TextView countTv;
        TextView subTotalTv;
    }
}
