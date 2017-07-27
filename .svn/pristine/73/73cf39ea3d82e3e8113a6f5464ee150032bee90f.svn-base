package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.MemberBean;

import java.util.List;

/**
 * Created by Longer on 2017/5/18.
 * 会员组的Adapter
 */
public class MemberAdapter extends BaseAdapter {

    private List<MemberBean> mDataList;
    private Context mContext;
    private int mChoosedMemberID = -1;


    public MemberAdapter(Context context, @NonNull List<MemberBean> memberBeans) {
        mDataList = memberBeans;
        mContext = context;
    }


    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList == null ? 0 : mDataList.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_for_member, null);
            viewHolder.Tv_memberName = (TextView) convertView.findViewById(R.id.tv_member_name);
            viewHolder.Iv_selected = (ImageView) convertView.findViewById(R.id.iv_select_state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MemberBean memberBean = mDataList.get(position);
        String noteText = memberBean.getMemberName() + memberBean.getSingAndPrice();
        viewHolder.Tv_memberName.setText(noteText);

        viewHolder.Iv_selected.setImageResource(memberBean.isSelected() ? R.mipmap.select_for_uid : R.mipmap.unselect_for_uid);

        return convertView;
    }

    static class ViewHolder {

        TextView Tv_memberName;

        ImageView Iv_selected;

    }


    public List<MemberBean> getDataList() {
        return mDataList;
    }

    public void setDataList(List<MemberBean> dataList) {
        mDataList = dataList;
    }

    public void setChoosedMemberID(int choosedMemberID) {
        mChoosedMemberID = mDataList.get(choosedMemberID).getMemberId();
    }

    public int getChoosedMemberID() {
        if (mChoosedMemberID == -1)
            mChoosedMemberID = mDataList.get(0).getMemberId();
        return mChoosedMemberID;
    }
}
