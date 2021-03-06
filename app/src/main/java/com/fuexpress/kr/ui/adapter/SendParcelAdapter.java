package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.bean.HelpSendParcelBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.activity.append_parcel.AppendParcelActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


/**
 * Created by yuan on 2016-6-13.
 */
public class SendParcelAdapter extends SimpleBaseAdapter<HelpSendParcelBean> implements View.OnClickListener {

    private final int mWeight;
    private final int mMargn;

    public SendParcelAdapter(Activity content, List<HelpSendParcelBean> data) {
        super(content, data);
        mWeight = UIUtils.dip2px(54);
        mMargn = UIUtils.dip2px(10);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContent, R.layout.item_send_parcel, null);
            holder.mTvTitle = (TextView) convertView.findViewById(R.id.tv_parcel_title);
            holder.mTvShipping = (TextView) convertView.findViewById(R.id.tv_parcel_shipping_type);
            holder.mTvWeight = (TextView) convertView.findViewById(R.id.tv_parcel_weight);
            holder.mTvFee = (TextView) convertView.findViewById(R.id.tv_parcel_shipping_fee);
            holder.mLlImgs = (LinearLayout) convertView.findViewById(R.id.ll_parcels_container);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        HelpSendParcelBean item = (HelpSendParcelBean) getItem(position);
        holder.mTvTitle.setText(mContent.getString(R.string.String_parcle_name) + item.getParcelName());

        if (item.getShippingmethodid() != 0) {
            holder.mTvShipping.setText(item.getShippingTitle());
            holder.mTvWeight.setText(mContent.getString(R.string.String_parcel_weight_count) + mContent.getString(R.string.package_weight, item.getWeight()));
            holder.mTvFee.setText(mContent.getString(R.string.String_parcel_shipping_fee) + UIUtils.getCurrency(mContent, item.getShippingFee()));
        } else {
            holder.mTvShipping.setText("请选择物流方式，");
            holder.mTvWeight.setText("否则提交时将忽略此包裹。");
            holder.mTvFee.setText("");
        }


        String images = item.getImages();
        if (!TextUtils.isEmpty(images)) {
            String[] split = images.split(" ");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    ImageView imageView = (ImageView) holder.mLlImgs.getChildAt(i);
                    if (imageView != null) {
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ImageLoader.getInstance().displayImage(split[i] + Constants.ImgUrlSuffix.dp_list, imageView, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
                    }
                }
            }

            if (split.length < 4) {
                for (int i = split.length; i < 4; i++) {
                    holder.mLlImgs.getChildAt(i).setVisibility(View.INVISIBLE);
                }
            }
        }
        convertView.setOnClickListener(this);
        convertView.setTag(R.id.tv_parcel_title, item);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        HelpSendParcelBean item = (HelpSendParcelBean) v.getTag(R.id.tv_parcel_title);
        Intent intent = new Intent(mContent, AppendParcelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppendParcelActivity.BEAN, item);
        intent.putExtra(AppendParcelActivity.BEAN, bundle);
        mContent.startActivity(intent);
    }

    static class Holder {
        TextView mTvTitle;
        TextView mTvShipping;
        TextView mTvWeight;
        TextView mTvFee;
        LinearLayout mLlImgs;
    }
}
