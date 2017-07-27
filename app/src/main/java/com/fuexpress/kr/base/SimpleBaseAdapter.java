package com.fuexpress.kr.base;

import android.app.Activity;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by yuan on 2016-5-3.
 */
public abstract class SimpleBaseAdapter<T> extends BaseAdapter {
    protected Activity mContent;
    protected List<T> mData;

    public SimpleBaseAdapter(Activity content, List<T> data) {
        mContent = content;
        mData = data;
    }

    public void setData(List<T> data) {
        this.mData = data;
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
