package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.SysNoticeBean;

import java.util.List;


/**
 * Created by k550 on 2016/6/13.
 */
public class SysNoticeAdapter extends BaseAdapter {
    private List<SysNoticeBean> mDatas;
    private Context mContext;
    public SysNoticeAdapter(){}
    public SysNoticeAdapter(Context context, List<SysNoticeBean> mDatas){
        this.mDatas=mDatas;
        this.mContext=context;
    }
    @Override
    public int getCount() {
        if(mDatas!=null){
            return mDatas.size();
        }
        return  0;
    }

    @Override
    public Object getItem(int position) {
        if(mDatas!=null){
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SysNoticeBean item =(SysNoticeBean)getItem(position);
        View view=convertView;
        final ViewHolder holder;
        if(view==null){
            view = View.inflate(mContext, R.layout.item_sys_notice, null);
            holder=new ViewHolder();
            holder.mPoint=(ImageView)view.findViewById(R.id.noticesRedPoint);
            holder.timeTv=(TextView)view.findViewById(R.id.timeTv);
            holder.titleTv=(TextView)view.findViewById(R.id.titleTv);
            view.setTag(holder);
        }else{
            holder=(ViewHolder)view.getTag();
        }
        if(item.isRead!=1){
            holder.mPoint.setVisibility(View.VISIBLE);
        }else{
            holder.mPoint.setVisibility(View.INVISIBLE);
        }
        holder.timeTv.setText(item.sendTimeStr);
        holder.titleTv.setText(item.title);
        return view;
    }
    static class ViewHolder{
        ImageView mPoint;
        TextView titleTv,timeTv;
    }
}
