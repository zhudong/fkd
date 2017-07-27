package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.bean.MaterialsBean;
import com.fuexpress.kr.ui.view.MaterialItemLayout;

import java.util.List;

/**
 * Created by andy on 2017/1/17.
 */
public class MaterialsAdapter extends SimpleBaseAdapter<MaterialsBean> {

    private int mSelectID;

    public MaterialsAdapter(Activity content, List<MaterialsBean> data, int selectID) {
        super(content, data);
        mSelectID = selectID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = new MaterialItemLayout(mContent);
            holder.mItemLayout = (MaterialItemLayout) convertView;
            convertView.setTag(holder);
        }
        holder = (Holder) convertView.getTag();
        holder.mItemLayout.setText(getItem(position).getName());
        holder.mItemLayout.setSelect(false);
        if (mSelectID == getItem(position).getId()) {
            holder.mItemLayout.setSelect(true);
        }
        return convertView;
    }


    static class Holder {
        MaterialItemLayout mItemLayout;
    }
}
