package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.StringUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsCard;


/**
 * Created by Longer on 2016/11/1.
 */
public class FillingOrderListAdapter extends BaseAdapter implements View.OnClickListener {

    private List<CsCard.GiftCardOrderItem> mGiftcarditemsList;
    private Context context;

    public FillingOrderListAdapter(List<CsCard.GiftCardOrderItem> giftcarditemsList, Context context) {
        mGiftcarditemsList = giftcarditemsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (mGiftcarditemsList != null && mGiftcarditemsList.size() != 0) {
            return mGiftcarditemsList.size();
        } else if (mGiftcarditemsList.size() == 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mGiftcarditemsList != null) {
            return mGiftcarditemsList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mGiftcarditemsList != null) {
            return position;
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mGiftcarditemsList.size() == 0) {
            convertView = View.inflate(SysApplication.getContext(), R.layout.item_for_gift_card_order_list_empty, null);
            TextView tv_in_gift_card_order_list_empty = (TextView) convertView.findViewById(R.id.tv_in_gift_card_order_list_empty);
            tv_in_gift_card_order_list_empty.setOnClickListener(this);
            return convertView;
        }
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(SysApplication.getContext(), R.layout.item_for_gift_order_list, null);
            viewHolder.tv_in_item_for_gift_card_order_list_number = (TextView) convertView.findViewById(R.id.tv_in_item_for_gift_card_order_list_number);
            viewHolder.tv_in_item_for_gift_card_order_list_time = (TextView) convertView.findViewById(R.id.tv_in_item_for_gift_card_order_list_time);
            viewHolder.tv_in_item_for_gift_card_order_list_total = (TextView) convertView.findViewById(R.id.tv_in_item_for_gift_card_order_list_total);
            viewHolder.tv_in_item_for_gift_card_order_list_status = (TextView) convertView.findViewById(R.id.tv_in_item_for_gift_card_order_list_status);
            viewHolder.tv_in_item_for_gift_card_order_list_line = (TextView) convertView.findViewById(R.id.tv_in_item_for_gift_card_order_list_line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CsCard.GiftCardOrderItem giftCardOrderItem = mGiftcarditemsList.get(position);
        viewHolder.tv_in_item_for_gift_card_order_list_number.setText(SysApplication.getContext().getString(R.string.my_gift_card_order_adapter_order_num) + giftCardOrderItem.getGiftCardOrderNo());
        viewHolder.tv_in_item_for_gift_card_order_list_time.setText(SysApplication.getContext().getString(R.string.my_gift_card_order_adapter_order_time) + giftCardOrderItem.getGiftCardCreateTime());

        // TODO: 2016/11/1 因为接口还没弄完,暂时先注释掉
        String currency = UIUtils.getCurrency(SysApplication.getContext(), giftCardOrderItem.getCurrencycode(), Float.valueOf(giftCardOrderItem.getTotal()));
        viewHolder.tv_in_item_for_gift_card_order_list_total.setText(SysApplication.getContext().getString(R.string.my_gift_card_order_adapter_order_total) + currency);
        int status = giftCardOrderItem.getStatus();
        // 0:充值卡订单-全部;  1: 充值卡订单-待付款; 2: 充值卡订单-已支付;  3: 充值卡订单-已取消
        //if (1 == status) {
        viewHolder.tv_in_item_for_gift_card_order_list_status.setText(giftCardOrderItem.getStatusMsg());
        /*} else if (2 == status) {
            viewHolder.tv_in_item_for_gift_card_order_list_status.setText(SysApplication.getContext().getString(R.string.my_gift_card_order_tab_02));
        } else if (3 == status) {
            viewHolder.tv_in_item_for_gift_card_order_list_status.setText(SysApplication.getContext().getString(R.string.my_gift_card_order_tab_03));
        }*/

        if (position == mGiftcarditemsList.size() - 1) {
            //viewHolder.tv_in_item_for_gift_card_order_list_line.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_in_gift_card_order_list_empty:
                /*Intent intent = new Intent(SysApplication.getContext(), GiftCardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SysApplication.getContext().startActivity(intent);*/
                EventBus.getDefault().post(new BusEvent(BusEvent.GO_HOME_CODE, null));
                break;
        }
    }

    class ViewHolder {
        TextView tv_in_item_for_gift_card_order_list_number;
        TextView tv_in_item_for_gift_card_order_list_time;
        TextView tv_in_item_for_gift_card_order_list_total;
        TextView tv_in_item_for_gift_card_order_list_status;
        TextView tv_in_item_for_gift_card_order_list_line;

    }
}
