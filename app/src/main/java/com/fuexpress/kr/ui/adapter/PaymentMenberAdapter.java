package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.activity.PaymentActivity;

import java.util.List;

import fksproto.CsCard;

/**
 * Created by andy on 2017/8/9.
 */

public class PaymentMenberAdapter  extends BaseAdapter{
        private Context context;
        private List<CsCard.MemberGroupList> list;
        private int pos;

        public PaymentMenberAdapter(Context context, List<CsCard.MemberGroupList> list){
            this.context = context;
            this.list = list;
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
                convertView = LayoutInflater.from(context).inflate(R.layout.vip_item, null);
                holder = new ViewHolder();
                holder.titleTv = (TextView) convertView.findViewById(R.id.vip_item_title_txt);
                holder.ct = (CheckedTextView) convertView.findViewById(R.id.vip_item_ct);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            CsCard.MemberGroupList item = list.get(position);
            holder.titleTv.setText(item.getMembergroupname() + " " + item.getSignandfee());
            holder.ct.setCheckMarkDrawable(R.drawable.selector_payment_checkbox);
            if (pos == position) {
                holder.ct.setChecked(true);
            } else {
                holder.ct.setChecked(false);
            }

            return convertView;
        }
        public void setCheckedAtPosition(int pos) {
            this.pos = pos;
        }

        class ViewHolder{
            TextView titleTv;
            CheckedTextView ct;
        }
    
}
