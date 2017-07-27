package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fuexpress.kr.R;

import java.util.List;

/**
 * Created by Administrator on 2017/3/6.
 */

public class AutoEdAdapter<T> extends ArrayAdapter {

    public AutoEdAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public AutoEdAdapter(Context context, int resource) {
        super(context, resource);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       /* MyHolder holder = null;
        if (convertView == null) {
            holder = new MyHolder();
            convertView = View.inflate(getContext(), R.layout.auto_complete_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.text_brand);
            holder.divide = (TextView) convertView.findViewById(R.id.tv_divide);
            convertView.setTag(holder);
        } else {
            holder = (MyHolder) convertView.getTag();
            String item = (String) getItem(position);
            holder.name.setText(item);
            if (position == 0) {
                holder.divide.setVisibility(View.INVISIBLE);
            } else {
                holder.divide.setVisibility(View.VISIBLE);
            }
        }*/
        convertView = View.inflate(getContext(), R.layout.auto_complete_item, null);
        TextView name = (TextView) convertView.findViewById(R.id.text_brand);
        TextView divide = (TextView) convertView.findViewById(R.id.tv_divide);
        String item = (String) getItem(position);
        name.setText(item);
        if (position == 0) {
            divide.setVisibility(View.INVISIBLE);
        } else {
            divide.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    static class MyHolder {
        TextView name;
        TextView divide;
    }
}
