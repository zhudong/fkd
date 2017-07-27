package com.fuexpress.kr.ui.activity.product_detail;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.model.ItemBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by andy on 2017/7/3.
 */
public class ProductInfoMiddleView extends LinearLayout {
    ItemBean mItemBean;


    @BindView(R.id.tv_product_name)
    TextView mTvProductName;
    @BindView(R.id.item_price_des)
    TextView mTvItemPriceDes;

    @BindView(R.id.tv_first_price_new)
    TextView mTvFirstPriceNew;

    @BindView(R.id.tv_first_price_old)
    TextView mTvFirstPriceOld;

    @BindView(R.id.tv_item_price_end)
    TextView mTvItemPriceEnd;
    @BindView(R.id.btn_buy_now)
    TextView mBtnBuyNow;

    @BindView(R.id.img_item_tag)
    ImageView mImgItemTag;
    private ProductDetailActivity mContext;


    public ProductInfoMiddleView(Context context) {
        this(context, null);
    }

    public ProductInfoMiddleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductInfoMiddleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.view_product_info, this);
        ButterKnife.bind(this);
        mContext = ((ProductDetailActivity) getContext());
    }

    public void notifyDataChange() {
        mTvProductName.setText(mItemBean.getTitle());
        ProductManage productManage = mContext.getProductManage();
        List<String[]> salePrices = productManage.getSalePrices();
        String[] rawPrice = salePrices.get(0);
        if (rawPrice.length > 1) {
            mTvFirstPriceOld.setVisibility(VISIBLE);
            mTvFirstPriceOld.setText(rawPrice[0]);
            mTvFirstPriceOld.setVisibility(View.VISIBLE);
            mTvFirstPriceOld.setTextColor(getResources().getColor(R.color.text_color_999));
            mTvFirstPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mTvFirstPriceOld.getPaint().setAntiAlias(true);
            mTvFirstPriceNew.setText(rawPrice[1]);

            mTvItemPriceEnd.setText(salePrices.get(1)[1]);
        } else {
            mTvFirstPriceOld.setVisibility(GONE);
            mTvFirstPriceNew.setText(rawPrice[0]);
            mTvItemPriceEnd.setText(salePrices.get(1)[0]);
        }
        mImgItemTag.setImageResource(productManage.isSales() ? R.mipmap.tag1 : R.mipmap.tag0);
//        if (!productManage.isBuyNow() /*& !productManage.isCrowd()*/)
//            mImgItemTag.setImageResource(R.mipmap.tag2);
    }

    public TextView getBtnBuyNow() {
        return mBtnBuyNow;
    }

    public void setmItemBean(ItemBean mItemBean) {
        this.mItemBean = mItemBean;
    }
}
