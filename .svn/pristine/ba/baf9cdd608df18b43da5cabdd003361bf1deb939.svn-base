package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.bean.SortCountrtyModel;
import com.fuexpress.kr.utils.CharacterParser;

import java.util.List;

/**
 * Created by root on 17-2-27.
 */

public class UserPhoneNumberAdapter extends BaseAdapter{
    private List<String> mDates;
    private Context mContext;

    public UserPhoneNumberAdapter(){

    }
    public UserPhoneNumberAdapter(BaseActivity context,List<String> lists){
        this.mContext=context;
        this.mDates=lists;
    }
    @Override
    public int getCount() {
        if (mDates != null) {
            return mDates.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mDates != null) {
            return mDates.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_phone, null);
            holder = new ViewHolder();
            holder.phoneTv = (TextView) convertView.findViewById(R.id.phone_tv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.phoneTv.setText("+"+mDates.get(position));
        return convertView;
    }
    static class ViewHolder{
        TextView phoneTv;
    }
}
