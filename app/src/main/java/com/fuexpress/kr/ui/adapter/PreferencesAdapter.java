package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.PreferencesBean;

import java.util.List;

/**
 * Created by Longer on 2016/11/1.
 */
public class PreferencesAdapter extends BaseAdapter {

    private List<PreferencesBean> mData;
    private Context mContext;
    public int mLastPosition;

    public PreferencesAdapter(Context context, List<PreferencesBean> data) {
        mData = data;
        mContext = context;
    }


    @Override
    public int getCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_for_choose_to_preferences, null);
            viewHolder.tv_in_item_choose_for_preferences = (TextView) convertView.findViewById(R.id.tv_in_item_choose_for_preferences);
            viewHolder.iv_in_item_choose_for_preferences_tick = (ImageView) convertView.findViewById(R.id.iv_in_item_choose_for_preferences_tick);
            viewHolder.tv_in_item_choose_for_sign = (TextView) convertView.findViewById(R.id.tv_in_item_choose_for_sign);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mData.get(position).getBeanSign() != null) {
            viewHolder.tv_in_item_choose_for_sign.setVisibility(View.VISIBLE);
            //viewHolder.tv_in_item_choose_for_sign.setText(mContext.getString(R.string.search_result_num_befor) + mData.get(position).getBeanSign() + mContext.getString(R.string.search_result_num_end));
            viewHolder.tv_in_item_choose_for_sign.setText(mContext.getString(R.string.kuo_hao, mData.get(position).getBeanSign()));
            if ("RUB".equals(mData.get(position).getBeanCode())) {
                Typeface fromAsset = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");
                viewHolder.tv_in_item_choose_for_sign.setTypeface(fromAsset);
                viewHolder.tv_in_item_choose_for_sign.setText(mContext.getString(R.string.kuo_hao, mContext.getString(R.string.russia_sign)));
            }

        }
        viewHolder.tv_in_item_choose_for_preferences.setText(mData.get(position).getBeanString());
        if (mData.get(position).isSelected()) {
            mLastPosition = position;
            viewHolder.tv_in_item_choose_for_preferences.setTextColor(Color.RED);
            viewHolder.iv_in_item_choose_for_preferences_tick.setVisibility(View.VISIBLE);
            viewHolder.tv_in_item_choose_for_sign.setTextColor(Color.RED);
        } else {
            viewHolder.tv_in_item_choose_for_preferences.setTextColor(Color.GRAY);
            viewHolder.iv_in_item_choose_for_preferences_tick.setVisibility(View.GONE);
            viewHolder.tv_in_item_choose_for_sign.setTextColor(Color.GRAY);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_in_item_choose_for_preferences;
        ImageView iv_in_item_choose_for_preferences_tick;
        TextView tv_in_item_choose_for_sign;
    }
}
