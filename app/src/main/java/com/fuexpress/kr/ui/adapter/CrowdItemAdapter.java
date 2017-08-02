package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.ui.activity.crowd.AttendCrowdActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.ClassUtil;
import com.fuexpress.kr.utils.LgUitl;
import com.fuexpress.kr.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;


/**
 * Created by yuan on 2016-4-6.
 */
public class CrowdItemAdapter extends BaseAdapter {

    Activity mContext;
    private List<CsBase.Item> mItems;
    ArrayList<Float> mDiscountList;

    public CrowdItemAdapter(Activity context, List<CsBase.Item> items, ArrayList<Float> discountList) {
        this.mContext = context;
        this.mItems = items;
        this.mDiscountList = discountList;
    }

    @Override
    public int getCount() {
        if (mItems != null && mDiscountList != null) {
            return mItems.size() > mDiscountList.size() ? mDiscountList.size() : mItems.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.item_for_crowd_item, null);
            holder.mCover = (ImageView) convertView.findViewById(R.id.img_crowd_item_cover);
            holder.mName = (TextView) convertView.findViewById(R.id.tv_crowd_item_name);
            holder.mPrice = (TextView) convertView.findViewById(R.id.tv_crowd_item_price);
            holder.mDisCount = (TextView) convertView.findViewById(R.id.tv_dicount);
            holder.mAttendCrowd = (TextView) convertView.findViewById(R.id.btn_addend_crowd);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final CsBase.Item item = mItems.get(position);
//        boolean en = AccountManager.getInstance().getLocaleCode().contains("en");
        boolean en = LgUitl.isOtherLanguage(AccountManager.getInstance().getLocaleCode());
        float discount = mDiscountList.get(position);
        boolean hasDiscount = discount > 0 && discount < 1;
        if (en) {
            discount = mDiscountList.get(position) * 100;
        } else {
            discount = (1 - mDiscountList.get(position)) * 10;
        }

        if (isInt(discount)) {
            String sCount = mContext.getResources().getString(R.string.String_discount_tag_int2);
            holder.mDisCount.setText(String.format(sCount, (int) discount));
        } else {
            String sCount = mContext.getResources().getString(R.string.String_discount_tag2);
            holder.mDisCount.setText(String.format(sCount, discount));
        }

        if (!hasDiscount) {
            holder.mDisCount.setText(mContext.getString(R.string.String_no_discount));
        }


        /*if (en) {
            if (discount <= 0) {
                holder.mDisCount.setText(mContext.getString(R.string.String_no_discount));
            }
        } else {
            if (discount >= 10) {
                holder.mDisCount.setText(mContext.getString(R.string.String_no_discount));
            }
        }*/

        float price;
        if (hasDiscount) {
            price = item.getDefaultPrice() * item.getExchangeRate() * (1 - mDiscountList.get(position));
        } else {
            price = item.getDefaultPrice() * item.getExchangeRate() - mDiscountList.get(position);
        }
        String sPrice = UIUtils.getCurrency(mContext, price);
        holder.mPrice.setText(String.format(sPrice, price));
        String uri = item.getImageUrl() + Constants.ImgUrlSuffix.small_9;
        LogUtils.d(uri);
        ImageLoader.getInstance().displayImage(uri, holder.mCover, ImageLoaderHelper.getInstance(mContext).getDisplayOptions());
        holder.mName.setText(item.getTitle());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean userLogin = AccountManager.getInstance().isUserLogin(mContext);
                if (!userLogin) return;
                Intent intent = new Intent(mContext, AttendCrowdActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(AttendCrowdActivity.ITEM_BEAN, ClassUtil.convertItem2ItemBean(item));
                intent.putExtra(AttendCrowdActivity.ITEM_BEAN, bundle);
                mContext.startActivity(intent);
            }
        };
        holder.mAttendCrowd.setOnClickListener(listener);
        convertView.setOnClickListener(listener);
        return convertView;
    }


    static class Holder {
        ImageView mCover;
        TextView mName;
        TextView mDisCount;
        TextView mPrice;
        TextView mAttendCrowd;
    }

    private boolean isInt(float num) {
        return num == (int) num;

    }
}


