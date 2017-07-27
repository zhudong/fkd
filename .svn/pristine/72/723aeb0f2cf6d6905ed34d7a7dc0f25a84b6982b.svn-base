package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import fksproto.CsCart;

/**
 * Created by Administrator on 2016/4/14.
 */
public class OrderCommodityAdapter extends BaseAdapter {
    private static final String TAG = "OrderCommodityAdapter";

    private Context context;
    private List<CsCart.SalesCartItem> cartList;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    public OrderCommodityAdapter(Context context, List<CsCart.SalesCartItem> list){
        this.context = context;
        this.cartList = list;
        this.options = ImageLoaderHelper.getInstance(context).getDisplayOptions();
        this.imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return cartList != null ? cartList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return cartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CartItemHolder cartHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.order_commodity_item, null);
            cartHolder = new CartItemHolder();
            cartHolder.orderCommodityImgIV = (ImageView) convertView.findViewById(R.id.order_commodity_img_iv);
            cartHolder.orderCommodityTitleTV = (TextView) convertView.findViewById(R.id.order_commodity_titile_tv);
            cartHolder.orderCommoditySellerTV = (TextView) convertView.findViewById(R.id.order_commodity_seller_tv);
            cartHolder.orderCommodityBuyFromTV = (TextView) convertView.findViewById(R.id.order_commodity_buyfrom_tv);
            cartHolder.orderCommoditySizeTitleTv = (TextView) convertView.findViewById(R.id.commodity_size_title_tv);
            cartHolder.orderCommoditySizeTV = (TextView) convertView.findViewById(R.id.order_commodity_size_tv);
            cartHolder.orderCommodityColorTitleTV = (TextView) convertView.findViewById(R.id.commodity_color_title_tv);
            cartHolder.orderCommodityColorTV = (TextView) convertView.findViewById(R.id.order_commodity_color_tv);
            cartHolder.orderCommodityPriceTV = (TextView) convertView.findViewById(R.id.order_commodity_price_tv);
            cartHolder.orderCommodityNoteTV = (TextView) convertView.findViewById(R.id.order_commodity_note_tv);
            cartHolder.orderCommoditySizeLL = (LinearLayout) convertView.findViewById(R.id.order_size_ll);
            cartHolder.orderCommodityNoteLL = (LinearLayout) convertView.findViewById(R.id.order_note_ll);
            cartHolder.orderCommodityCounterTV = (TextView) convertView.findViewById(R.id.order_commodity_counter_tv);
            convertView.setTag(cartHolder);
        }else{
            cartHolder = (CartItemHolder) convertView.getTag();
        }

        CsCart.SalesCartItem item = cartList.get(position);
        imageLoader.displayImage(item.getImageUrl() + Constants.ImgUrlSuffix.dp_list, cartHolder.orderCommodityImgIV, options);
        cartHolder.orderCommodityTitleTV.setText(item.getTitle());
        cartHolder.orderCommoditySellerTV.setText(item.getSeller());
        cartHolder.orderCommodityBuyFromTV.setText(item.getBuyfrom());
        if(item.getExtendsCount() > 0){
            cartHolder.orderCommoditySizeTV.setVisibility(View.VISIBLE);
            cartHolder.orderCommoditySizeTV.setText(item.getExtends(0).getAttr().getAttrName() + ": " + item.getExtends(0).getOptions(0).getOptionName());
        }else {
            cartHolder.orderCommoditySizeTV.setVisibility(View.GONE);
        }

//            cartHolder.orderCommoditySizeLL.setVisibility(View.VISIBLE);
//            if(item.getExtends(0).getOptionsCount() > 0){
//                cartHolder.orderCommoditySizeTitleTv.setVisibility(View.VISIBLE);
//                cartHolder.orderCommoditySizeTV.setText(item.getExtends(0).getOptions(0).getOptionName());
//            }
//            if(item.getExtendsList().size() > 1){
//                cartHolder.orderCommodityColorTitleTV.setVisibility(View.VISIBLE);
//                cartHolder.orderCommodityColorTV.setText(item.getExtends(1).getOptions(0).getOptionName());
//            }else {
//                cartHolder.orderCommodityColorTitleTV.setVisibility(View.GONE);
//            }
//        else{
//            cartHolder.orderCommoditySizeLL.setVisibility(View.GONE);
//            cartHolder.orderCommoditySizeTitleTv.setVisibility(View.GONE);
//            cartHolder.orderCommodityColorTitleTV.setVisibility(View.GONE);
//        }
        cartHolder.orderCommodityPriceTV.setText(UIUtils.getCurrency(context, item.getUnitPrice()));
        if(item.getNote() != null && item.getNote().length() > 0){
            cartHolder.orderCommodityNoteTV.setText(item.getNote());
            cartHolder.orderCommodityNoteLL.setVisibility(View.VISIBLE);
        }else{
            cartHolder.orderCommodityNoteLL.setVisibility(View.GONE);
        }

        String countStr = context.getResources().getString(R.string.String_order_commodity_counter);
        countStr = String.format(countStr, item.getQty() + "");
        cartHolder.orderCommodityCounterTV.setText(countStr);
        return convertView;
    }

    class CartItemHolder{
        ImageView orderCommodityImgIV;
        TextView orderCommodityTitleTV;
        TextView orderCommoditySellerTV;
        TextView orderCommodityBuyFromTV;
        TextView orderCommoditySizeTitleTv;
        TextView orderCommoditySizeTV;
        TextView orderCommodityColorTitleTV;
        TextView orderCommodityColorTV;
        TextView orderCommodityPriceTV;
        TextView orderCommodityNoteTV;
        LinearLayout orderCommoditySizeLL;
        LinearLayout orderCommodityNoteLL;
        TextView orderCommodityCounterTV;
    }

}
