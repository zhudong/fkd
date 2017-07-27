package com.fuexpress.kr.ui.activity.choose_address;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fuexpress.kr.R;

import java.util.List;

import fksproto.CsAddress;

/**
 * Created by Longer on 2017/4/27.
 */
public class ChooseAddressAdapter extends BaseAdapter {

    public static final String KEY_COUNTRY_TYPE = "Country";

    public static final String KEY_REGION_DATA = "REGION_DATA";

    private Context mContext;

    public List<CsAddress.DirectoryCountryInfo> mCountryDataList;

    public List<CsAddress.DirectoryRegionInfo> mRegionDataList;

    public boolean mIsCountryType = true;

    public ChooseAddressAdapter(Context context, String type, List<CsAddress.DirectoryCountryInfo> countryDataList,
                                List<CsAddress.DirectoryRegionInfo> regionDataList) {
        mContext = context;
        if (KEY_COUNTRY_TYPE.equals(type))
            mCountryDataList = countryDataList;
        else {
            mIsCountryType = false;
            mRegionDataList = regionDataList;
        }
    }

    public void reLoadCountryDataList(List<CsAddress.DirectoryCountryInfo> countryDataList) {
        mCountryDataList = countryDataList;
    }

    public void reLoadRegionDataList(List<CsAddress.DirectoryRegionInfo> regionDataList) {
        mRegionDataList = regionDataList;
    }


    @Override
    public int getCount() {
        if (mIsCountryType)
            return mCountryDataList == null ? 0 : mCountryDataList.size();
        else
            return mRegionDataList == null ? 0 : mRegionDataList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mIsCountryType)
            return mCountryDataList == null ? null : mCountryDataList.get(position);
        else
            return mRegionDataList == null ? null : mRegionDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_choose_address, null);
            viewHolder.tv_choose_address = (TextView) convertView.findViewById(R.id.tv_in_item_choose_for_address);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_choose_address.setText(mIsCountryType ? mCountryDataList.get(position).getDirectoryCountryName() :
                mRegionDataList.get(position).getDirectoryCountryRegionName());
        return convertView;
    }

    class ViewHolder {
        TextView tv_choose_address;
    }
}
