package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.SimpleAlbumBean;

import java.util.List;

/**
 * Created by longer on 2017/7/7.
 */

public class AlbumListDialogAdapter extends BaseAdapter {
    List<SimpleAlbumBean> mList;
    Context mContext;


    public AlbumListDialogAdapter(Context context, List<SimpleAlbumBean> list) {
        mList = list;
        mContext = context;
    }


    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mList != null) {
            return mList.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_for_add_album, null);
            viewHolder.tv_album_name_for_add_album = (TextView) convertView.findViewById(R.id.tv_album_name_for_add_album);
            viewHolder.tv_tick_for_add_album = (TextView) convertView.findViewById(R.id.tv_tick_for_add_album);
            viewHolder.tv_tick_for_add_album.setVisibility(View.GONE);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SimpleAlbumBean album = mList.get(position);

        viewHolder.tv_album_name_for_add_album.setText(album.getTitle());
        //LogUtils.e("这是图集的类型:" + album.getCategory());
        return convertView;
    }

    class ViewHolder {
        TextView tv_album_name_for_add_album;
        TextView tv_tick_for_add_album;
    }

    public void setmList(List<SimpleAlbumBean> mList) {
        this.mList = mList;
    }
}
