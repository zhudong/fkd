package com.fuexpress.kr.ui.adapter;

import android.content.Context;
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
 * 父分类的Adapter
 */
public class ClassifyGroupAdapter extends BaseAdapter {

    private Context mCtx;
    private List<CsUser.MatchItemCategory> matchList;
    private int matchItemId;

    public ClassifyGroupAdapter(Context context, List<CsUser.MatchItemCategory> matchList, int matchItemId) {
        this.mCtx = context;
        this.matchList = matchList;
        this.matchItemId = matchItemId;
    }

    public void setMatchList(List<CsUser.MatchItemCategory> list) {
        matchList = list;
    }

    public void setMatchItemId(int matchItemId) {
        this.matchItemId = matchItemId;
    }

    @Override
    public int getCount() {
        return matchList != null ? matchList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return matchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.classify_group_item, null);
            holder = new Holder();
            holder.nameTv = (TextView) convertView.findViewById(R.id.classify_group_name_tv);
            holder.arrowIv = (ImageView) convertView.findViewById(R.id.classify_group_arrow_iv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        CsUser.MatchItemCategory item = matchList.get(position);
        holder.nameTv.setText(item.getMatchItemCategoryName());
        if (item.getMatchItemCategoryId() == matchItemId) {
            holder.nameTv.setTextColor(mCtx.getResources().getColor(R.color.the_red));
            holder.arrowIv.setImageResource(R.mipmap.arrow_right_red);
        } else {
            holder.nameTv.setTextColor(mCtx.getResources().getColor(R.color.black));
            holder.arrowIv.setImageResource(R.mipmap.arrow_right);
        }

        return convertView;
    }

    class Holder {
        TextView nameTv;
        ImageView arrowIv;
    }
}
