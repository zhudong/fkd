package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;

import java.util.List;

import fksproto.CsUser;

/**
 * Created by Longer on 2017/1/17.
 * 子分类的的Adapter
 */
public class ClassifyChildAdapter extends BaseAdapter {

    private Context mCtx;
    private List<CsUser.MatchItemCategory> list;
    private int subMatchItemId;

    public ClassifyChildAdapter(Context context, List<CsUser.MatchItemCategory> list, int subMatchItemId) {
        this.mCtx = context;
        this.list = list;
        this.subMatchItemId = subMatchItemId;
    }

    public void setSubMatchItemId(int subMatchItemId) {
        this.subMatchItemId = subMatchItemId;
    }

    public void setList(List<CsUser.MatchItemCategory> list) {
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
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.classify_child_item, null);
            holder = new Holder();
            holder.nameTv = (TextView) convertView.findViewById(R.id.classify_child_name_tv);
            holder.selectIv = (ImageView) convertView.findViewById(R.id.classify_child_select_iv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        CsUser.MatchItemCategory item = list.get(position);
        holder.nameTv.setText(item.getMatchItemCategoryName());
        holder.selectIv.setImageResource(R.mipmap.tick);
        if (item.getMatchItemCategoryId() == subMatchItemId) {
            holder.selectIv.setVisibility(View.VISIBLE);
            holder.nameTv.setTextColor(ContextCompat.getColor(mCtx, R.color.the_red));
        } else {
            holder.selectIv.setVisibility(View.GONE);
            holder.nameTv.setTextColor(ContextCompat.getColor(mCtx, R.color.black));
        }
        return convertView;
    }

    class Holder {
        TextView nameTv;
        ImageView selectIv;
    }
}