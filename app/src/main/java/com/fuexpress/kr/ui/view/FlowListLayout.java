package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * Created by andy on 2016/11/23.
 */
public class FlowListLayout extends FlowLayout implements View.OnClickListener {
    BaseAdapter adapter;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    OnItemClickListener listener;

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (listener != null) {
            listener.onItemClick(this, v, position, adapter.getItemId(position));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(FlowListLayout parent, View view, int position, long id);
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if(adapter==null)return;
        this.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, null, null);
            view.setTag(i);
            this.addView(view);
            view.setOnClickListener(this);
        }
    }


    public FlowListLayout(Context context) {
        super(context);
    }

    public FlowListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
