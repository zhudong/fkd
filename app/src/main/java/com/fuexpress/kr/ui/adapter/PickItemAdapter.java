package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.PickUpItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.activity.AddRequireActivity;
import com.fuexpress.kr.ui.activity.ReplenishActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;

import java.io.File;
import java.util.List;

import fksproto.CsParcel;

/**
 * Created by Administrator on 2016-12-20.
 */

public class PickItemAdapter extends BaseAdapter {

    private Context context;
    private List<CsParcel.ProductDataList> productDataList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<PickUpItemBean> pickList;
    private boolean isPickUp = false;
    private String cityCurrencyCode;

    public PickItemAdapter(Context context, List<CsParcel.ProductDataList> productDataList) {
        this.context = context;
        this.productDataList = productDataList;
        this.options = ImageLoaderHelper.getInstance(context).getDisplayOptions();
        this.imageLoader = ImageLoader.getInstance();
    }

    public PickItemAdapter(Context context, List<PickUpItemBean> pickList, boolean isPickUp) {
        this.context = context;
        this.pickList = pickList;
        this.isPickUp = isPickUp;
        this.options = ImageLoaderHelper.getInstance(context).getDisplayOptions();
        this.imageLoader = ImageLoader.getInstance();
    }

    public PickItemAdapter(Context context, List<PickUpItemBean> pickList, boolean isPickUp, String cityCurrencyCode) {
        this.context = context;
        this.pickList = pickList;
        this.isPickUp = isPickUp;
        this.options = ImageLoaderHelper.getInstance(context).getDisplayOptions();
        this.imageLoader = ImageLoader.getInstance();
        this.cityCurrencyCode = cityCurrencyCode;
    }

    @Override
    public int getCount() {
        if (isPickUp) {
            return pickList != null ? pickList.size() : 0;
        }
        return productDataList != null ? productDataList.size() : 0;

    }

    @Override
    public Object getItem(int position) {
        if(isPickUp){
            return pickList.get(position);

        }
        return productDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pick_item_layout, null);
            holder = new ViewHolder();
            holder.logoiv = (ImageView) convertView.findViewById(R.id.pick_item_iv);
            holder.titleTv = (TextView) convertView.findViewById(R.id.pick_item_title_tv);
            holder.priceTv = (TextView) convertView.findViewById(R.id.pick_item_price_tv);
            holder.countTv = (TextView) convertView.findViewById(R.id.pick_item_count_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(isPickUp){
            PickUpItemBean item = pickList.get(position);
            setData(holder, item);
        }else {
            CsParcel.ProductDataList item = productDataList.get(position);
            String url=item.getExtraList().get(0).getImageUrl();
            if(url.contains(",")){
                String urls[]=url.split(",");
                imageLoader.displayImage(urls[0],holder.logoiv,options);
            }
        }

        return convertView;
    }

    public void setData(ViewHolder holder, PickUpItemBean item){
        holder.logoiv.setCropToPadding(true);
        holder.logoiv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 120);
        String[] https = item.getPathList().get(0).split("http");
        if (https.length > 1) {
            Glide.with(context).load(item.getPathList().get(0).split("!")[0] + Constants.ImgUrlSuffix.dp_list).into(holder.logoiv);
        } else {
            File file = new File(item.getPathList().get(0));
            Glide.with(context).load(file).into(holder.logoiv);
        }

        holder.logoiv.setLayoutParams(params);
        holder.titleTv.setText(item.getItemNote());
        holder.priceTv.setText(UIUtils.getCurrency(context, cityCurrencyCode, Float.parseFloat(item.getItemPrice())));
        if(context instanceof ReplenishActivity){
            holder.priceTv.setText(UIUtils.getCurrency(context,cityCurrencyCode, Float.parseFloat(item.getItemPrice())));
        }
        holder.countTv.setText(context.getString(R.string.pick_item_count_msg, item.getItemCount()));
    }

    public void setData(ViewHolder holder, CsParcel.ProductDataList item) {
        if(item!=null&&item.getImageNum()>0){
            imageLoader.displayImage(item.getExtra(0).getImageUrl() + Constants.ImgUrlSuffix.small_9, holder.logoiv, options);
        }
        holder.titleTv.setText(item.getProductdescription());
        holder.priceTv.setText(UIUtils.getCurrency(context, cityCurrencyCode,Float.parseFloat(item.getPrice())));
        holder.countTv.setText(context.getString(R.string.pick_item_count_msg, item.getNum()));
    }

        class ViewHolder {
        ImageView logoiv;
        TextView titleTv;
        TextView priceTv;
        TextView countTv;
    }
}
