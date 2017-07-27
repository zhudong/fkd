package com.fuexpress.kr.ui.activity.my_order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.bean.SalesOrderBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.activity.order_detail.OrderDetailCanceledActivity;
import com.fuexpress.kr.ui.activity.order_detail.OrderDetailPandingActivity;
import com.fuexpress.kr.ui.activity.order_detail.OrderDetailPayedActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import fksproto.CsBase;
import fksproto.CsOrder;

/**
 * Created by longer on 2017/7/6.
 */

public class OrderItemAdapter extends SimpleBaseAdapter<SalesOrderBean> {
    List<CsBase.PairIntStr> mUrls;
    String[] tags = {"", UIUtils.getString(R.string.wait_to_pay),
            UIUtils.getString(R.string.wait_cancel),
            UIUtils.getString(R.string.card_order_detail_title_bar_canceled),
            UIUtils.getString(R.string.card_order_detail_title_bar_payed),
            UIUtils.getString(R.string.part_send),
            UIUtils.getString(R.string.all_send),
            UIUtils.getString(R.string.crowding)};

    public OrderItemAdapter(Activity context, List<SalesOrderBean> mDatas, List<CsBase.PairIntStr> urls) {
        super(context, mDatas);
        this.mUrls = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SalesOrderBean item=(SalesOrderBean)getItem(position);
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContent, R.layout.item_for_order, null);
            holder.mTvTime =(TextView)convertView.findViewById(R.id.timeTv);
            holder.mTvPrice=(TextView)convertView.findViewById(R.id.priceTv);
            holder.mTAGTv = (View) convertView.findViewById(R.id.TAGTv);
            holder.childs[0]=(ImageView)convertView.findViewById(R.id.icon_01);
            holder.childs[1]=(ImageView)convertView.findViewById(R.id.icon_02);
            holder.childs[2]=(ImageView)convertView.findViewById(R.id.icon_03);
            holder.childs[3]=(ImageView)convertView.findViewById(R.id.icon_04);
            holder.childs[4]=(ImageView)convertView.findViewById(R.id.icon_05);
            holder.mStateTv=(TextView)convertView.findViewById(R.id.stateTv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.mTvTime.setText(mContent.getString(R.string.card_order_detail_order_date)+ TimeUtils.getDateStyle(item.create_time));
        final String string = mContent.getResources().getString(R.string.String_price);
        String price = String.format(string, item.grand_total);
        price=UIUtils.getCurrency(mContent,item.currencyCode,item.grand_total);
        holder.mTvPrice.setText(mContent.getString(R.string.card_order_detail_order_grand_total)+price);
        if(item.is_crowd){
            holder.mTAGTv.setVisibility(View.VISIBLE);
        }else{
            holder.mTAGTv.setVisibility(View.GONE);
        }
        holder.mStateTv.setText("state = "+item.state);
        if(0<=item.state&&item.state<=7){
            holder.mStateTv.setText(tags[item.state]);
        }
        CsBase.PairIntStr pairIntStr = mUrls.get(position);
        String[] split = pairIntStr.getV().split(",");
        addCover(holder.childs,split);
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Bundle bundle;
                switch (item.state){
                    case CsOrder.SalesOrderState.SALES_ORDER_STATE_PENDING_VALUE:
                    case CsOrder.SalesOrderState.SALES_ORDER_STATE_AWAITING_CANCEL_VALUE:
                        intent=new Intent(mContent, OrderDetailPandingActivity.class);
                        bundle = new Bundle();
                        bundle.putSerializable(OrderAll.BEAN,item);
                        intent.putExtra(OrderAll.BEAN,bundle);
                        mContent.startActivity(intent);
                        break;
                    case CsOrder.SalesOrderState.SALES_ORDER_STATE_CANCELED_VALUE:
                        intent=new Intent(mContent, OrderDetailCanceledActivity.class);
                        bundle = new Bundle();
                        bundle.putSerializable(OrderAll.BEAN,item);
                        intent.putExtra(OrderAll.BEAN,bundle);
                        mContent.startActivity(intent);
                        break;
//                    case 0:
                    case CsOrder.SalesOrderState.SALES_ORDER_STATE_PAYED_VALUE:
                    case CsOrder.SalesOrderState.SALES_ORDER_STATE_PART_SHIPPED_VALUE:
                    case CsOrder.SalesOrderState.SALES_ORDER_STATE_SHIPPED_VALUE:
                        intent=new Intent(mContent, OrderDetailPayedActivity.class);

                        intent.putExtra(OrderAll.BEAN,item.order_id);
                        mContent.startActivity(intent);
                        break;
                    case CsOrder.SalesOrderState.SALES_ORDER_STATE_CROWDING_VALUE:
                        intent=new Intent(mContent, OrderDetailPayedActivity.class);
                        intent.putExtra(OrderAll.BEAN,item.order_id);
                        mContent.startActivity(intent);
                        break;

                }
            }
        };
        convertView.setOnClickListener(listener);
        return convertView;
      /*  convertView = View.inflate(mContent, R.layout.item_for_order, null);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContent, "", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;*/

    }

    private void addCover(ImageView[] childs, String[] split) {
        for (int i = 0; i < childs.length; i++) {
            childs[i].setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage("", childs[i], ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
        }
        for (int i = 0; i < childs.length; i++) {
            if (i >= split.length) {
                return;
            }
            childs[i].setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(split[i] + Constants.ImgUrlSuffix.small_9, childs[i], ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
        }
    }

    static class Holder {
        TextView mTvTime;
        TextView mTvPrice;
        View mTAGTv;
        TextView mStateTv;
        ImageView[] childs = new ImageView[5];
    }
}
