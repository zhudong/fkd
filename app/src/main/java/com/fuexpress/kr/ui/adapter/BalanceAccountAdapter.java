package com.fuexpress.kr.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;

import java.util.List;

import fksproto.CsCard;


/**
 * Created by Longer on 2016/11/1.
 */
public class BalanceAccountAdapter extends BaseAdapter {

    public static final String FAIL_OR_EMPTY_TYPE = "fail_or_empty";
    List<CsCard.GiftCardBalanceList> dataListList;
    public String mType;


    public BalanceAccountAdapter(String type) {
        mType = type;
    }

    public BalanceAccountAdapter(List<CsCard.GiftCardBalanceList> listList) {
        dataListList = listList;
    }


    @Override
    public int getCount() {
        if (dataListList != null && dataListList.size() != 0) {
            return dataListList.size();
        } else if (FAIL_OR_EMPTY_TYPE.equals(mType)) {
            return 0;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (dataListList != null && dataListList.size() != 0) {
            return dataListList.get(position);
        } else if (FAIL_OR_EMPTY_TYPE.equals(mType)) {
            return null;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (dataListList != null && dataListList.size() != 0) {
        } else if (FAIL_OR_EMPTY_TYPE.equals(mType)) {
            return position;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(SysApplication.getContext(), R.layout.item_for_gift_card_account_lv, null);
            viewHolder.tv_in_item_for_gift_card_account_time = (TextView) convertView.findViewById(R.id.tv_in_item_for_gift_card_account_time);
            viewHolder.tv_in_item_for_gift_card_account_title_02 = (TextView) convertView.findViewById(R.id.tv_in_item_for_gift_card_account_title_02);
            viewHolder.tv_in_item_for_gift_card_account_money = (TextView) convertView.findViewById(R.id.tv_in_item_for_gift_card_account_money);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CsCard.GiftCardBalanceList giftCardBalanceList = dataListList.get(position);
        String time = giftCardBalanceList.getTime();
        String desc = giftCardBalanceList.getDesc();
        String price = giftCardBalanceList.getPrice();

        viewHolder.tv_in_item_for_gift_card_account_time.setText(time);
        viewHolder.tv_in_item_for_gift_card_account_title_02.setText(desc);
        viewHolder.tv_in_item_for_gift_card_account_money.setText(price);

        return convertView;
    }

    class ViewHolder {
        TextView tv_in_item_for_gift_card_account_time;
        TextView tv_in_item_for_gift_card_account_title_02;
        TextView tv_in_item_for_gift_card_account_money;
    }
}