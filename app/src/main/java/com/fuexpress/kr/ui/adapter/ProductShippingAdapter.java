package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SimpleBaseAdapter;

import java.util.List;

import fksproto.CsItem;


/**
 * Created by andy on 2017/6/12.
 */
public class ProductShippingAdapter extends SimpleBaseAdapter<CsItem.MatchShippingInfo> {
    public ProductShippingAdapter(Activity content, List<CsItem.MatchShippingInfo> data) {
        super(content, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = View.inflate(mContent, R.layout.item_product_shipping, null);
        TextView title = (TextView) root.findViewById(R.id.tv_title);
        TextView info = (TextView) root.findViewById(R.id.tv_info);
        CsItem.MatchShippingInfo item = getItem(position);
        title.setText(item.getTitle());
        if (TextUtils.isEmpty(item.getAttention())) {
            info.setText(item.getShippingInfo());
        }
        info.setText(item.getShippingInfo() + "\n" + item.getAttention());
        return root;
    }
}
