package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fksproto.CsAddress;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Longer on 2017/6/6.
 */
public class NewCountryAdapter extends BaseAdapter {


    private List<CsAddress.DirectoryCountryInfo> mDatas;

    private Context mContext;
    private ArrayMap<String, Integer> mArrayMap;
    private boolean mIsComplete = false;

    public NewCountryAdapter(@NonNull Context context, @NonNull List<CsAddress.DirectoryCountryInfo> countryRegionInfos) {
        mDatas = countryRegionInfos;
        mContext = context;
        mArrayMap = new ArrayMap<>();
    }


    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null || null == convertView.getTag()) {
            convertView = View.inflate(mContext, R.layout.item_for_country, null);
            viewHolder = new ViewHolder();
            viewHolder.catalog = (TextView) convertView.findViewById(R.id.catalog);
            viewHolder.countryText = (TextView) convertView.findViewById(R.id.countryText);
            viewHolder.numberText = (TextView) convertView.findViewById(R.id.numberText);
            viewHolder.select_iv = (ImageView) convertView.findViewById(R.id.select_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CsAddress.DirectoryCountryInfo countryRegionInfo = mDatas.get(position);
        String initial = countryRegionInfo.getInitial();
        boolean isContain = mArrayMap.containsKey(initial);
        boolean isShowCat = true;
        if (isContain) isShowCat = mArrayMap.get(initial) == position;
        else mArrayMap.put(initial, position);
        viewHolder.catalog.setText(initial);
        viewHolder.catalog.setVisibility(isShowCat ? View.VISIBLE : View.GONE);
        viewHolder.numberText.setText("+" + countryRegionInfo.getAreaCode());

        viewHolder.countryText.setText(countryRegionInfo.getDirectoryCountryName());
        viewHolder.select_iv.setVisibility(View.GONE);

        if (mDatas.size() == position) mIsComplete = true;

        return convertView;
    }

    class ViewHolder {

        TextView catalog;
        TextView countryText;
        TextView numberText;
        ImageView select_iv;

    }

    public void setData(List<CsAddress.DirectoryCountryInfo> datas) {
        mIsComplete = false;
        mDatas = datas;
    }

    /**
     * 根据首字母获得其所在的第一位位置
     *
     * @param initial 首字母
     * @return 其第一位位置
     * @throws Exception 假如不存在,则跑出异常,请自己处理
     */
    public int getInitialPosit(String initial) throws Exception {
        checkNotNull(initial, "getInitialPosit():intial not null!");
        boolean isContain = mArrayMap.containsKey(initial);
        if (isContain)
            return mArrayMap.get(initial);
        else
            throw new Exception("not Have this inital! please check!");
    }

    /**
     * 获得当前所有的首字母集合
     * 注意需要在所有Item都加载完之后才能调用
     *
     * @return 当前所有的首字母集合
     */
    public List<String> getAllinitialList() {
        checkArgument(mIsComplete, "data not ready!");
        Set<String> strings = mArrayMap.keySet();
        List<String> list = new ArrayList<>();
        if (strings.size() > 0) list.addAll(strings);
        return list;
    }

}
