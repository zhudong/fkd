package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.KuaiDiBaen;
import com.fuexpress.kr.bean.KuaiDiGroup;
import com.google.protobuf.ProtocolStringList;

import java.util.List;

import fksproto.CsParcel;


/**
 * Created by yuan on 2016-5-17.
 */
public class ExpressageAdapter extends BaseExpandableListAdapter {
    List<KuaiDiGroup> mGroup;
    //    private ProtocolStringList shippingNumbersList;
    Activity mContent;
    Handler handler;
    ArrayMap<Integer, KuaiDiBaen> childs;

    public ExpressageAdapter(List<KuaiDiGroup> group, ArrayMap<Integer, KuaiDiBaen> childs, Activity content) {
//        this.shipping = shipping;
        this.mContent = content;
//        shippingNumbersList = shipping.getShippingNumbersList();
        this.mGroup = group;
        this.childs = childs;
        handler = new Handler();

    }
/*
    快递单当前的状态 ：　
            0：在途，即货物处于运输过程中；
            1：揽件，货物已由快递公司揽收并且产生了第一条跟踪信息；
            2：疑难，货物寄送过程出了问题；
            3：签收，收件人已签收；
            4：退签，即货物由于用户拒签、超区等原因退回，而且发件人已经签收；
            5：派件，即快递正在进行同城派件；
            6：退回，货物正处于退回发件人的途中；*/

    public String formateState(String state) {
        Integer integer = Integer.valueOf(state);
        String value = "";
        switch (integer) {
            case 0:
                value = "";
                break;
            case 1:
                value = mContent.getString(R.string.shipping_state_pickup);
                break;
            case 2:
                value = mContent.getString(R.string.shipping_state_problem);
                break;
            case 3:
                value = mContent.getString(R.string.shipping_state_receiving);
                break;
            case 4:
                value = "在途";
                break;
            case 5:
                value = mContent.getString(R.string.shipping_state_delivered);
                break;
            case 6:
                value = mContent.getString(R.string.shipping_state_returned);
                break;
        }
        return value;
    }


    @Override
    public int getGroupCount() {
        if (mGroup != null) {
            return mGroup.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        KuaiDiBaen kuaiDiBaen = childs.get(groupPosition);
        if (kuaiDiBaen != null) {
            List<KuaiDiBaen.DataBean> data = kuaiDiBaen.data;
            if (data != null) {
                return data.size();
            }
        }

        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(groupPosition).data.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        HeaderHolder holder;
        if (convertView == null) {
            holder = new HeaderHolder();
            convertView = View.inflate(mContent, R.layout.view_item_kuaidi_header, null);
            holder.Number = (TextView) convertView.findViewById(R.id.tv_kuaidi_number);
            holder.type = (TextView) convertView.findViewById(R.id.tv_kuaidi_type);
            holder.state = (TextView) convertView.findViewById(R.id.tv_kuaidi_state);
            holder.arrow = (ImageView) convertView.findViewById(R.id.img_arrow);
            convertView.setTag(holder);
        } else {
            holder = (HeaderHolder) convertView.getTag();
        }

        if (isExpanded) {
            holder.arrow.setBackgroundResource(R.mipmap.arrow_up);
        } else {
            holder.arrow.setBackgroundResource(R.mipmap.arrow_down);
        }


        KuaiDiGroup group = (KuaiDiGroup) getGroup(groupPosition);
        holder.Number.setText(group.shippingNumber);
        holder.type.setText(group.shippingName);
        holder.state.setText(formateState(group.state));
        return convertView;
    }

    private String formateType(String shippingCode, KuaiDiGroup group) {
        if (!TextUtils.isEmpty(group.shippingNumber)) return group.shippingNumber;
        if ("ems".equals(shippingCode)) {
            return mContent.getString(R.string.shipping_method_ems);
        } else if ("shunfeng".endsWith(shippingCode)) {
            return mContent.getString(R.string.shipping_method_sf);
        }
        return shippingCode;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = View.inflate(mContent, R.layout.view_item_kuaid_child, null);
            holder.text = (TextView) convertView.findViewById(R.id.tv_title);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.imageView = (ImageView) convertView.findViewById(R.id.img_icon);
            holder.lineup = (TextView) convertView.findViewById(R.id.tv_line_above);
            holder.lineDown = (TextView) convertView.findViewById(R.id.tv_line_below);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        KuaiDiBaen.DataBean dataBean = (KuaiDiBaen.DataBean) getChild(groupPosition, childPosition);
        holder.text.setText(dataBean.context);
        holder.time.setText(dataBean.time);

        int childrenCount = getChildrenCount(groupPosition);

        if (childPosition == childrenCount - 1) {
            holder.lineDown.setVisibility(View.GONE);
        } else {
            holder.lineDown.setVisibility(View.VISIBLE);
        }

        if (childPosition == 0) {
            holder.lineup.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.mipmap.package_step3);
            int color = mContent.getResources().getColor(R.color.progress_finish_green);
            holder.text.setTextColor(color);
            holder.time.setTextColor(color);
        } else {
            holder.lineup.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(R.mipmap.package_step1);
            int color = mContent.getResources().getColor(R.color.text_color_999);
            holder.text.setTextColor(color);
            holder.time.setTextColor(color);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class HeaderHolder {
        TextView type;
        TextView Number;
        TextView state;
        ImageView arrow;
    }

    static class ChildHolder {
        ImageView imageView;
        TextView text;
        TextView time;
        TextView lineup;
        TextView lineDown;
    }
}
