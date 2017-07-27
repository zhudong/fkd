package com.fuexpress.kr.ui.activity.product_detail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;

public class ProductManage {
    private CsBase.ItemProduct mItemProduct;
    private CsBase.ItemOffer mItemOffer = CsBase.ItemOffer.newBuilder().setOfferType(1).setRate(0.2f).setRateType(1).build();
    private List<CsBase.ItemOfferGroup> mItemOfferGroupsList = new ArrayList<CsBase.ItemOfferGroup>();
    private ItemBean mItem;
    private Context mContext;
    private CsBase.Seller mSeller = CsBase.Seller.newBuilder().setNickname("nike").setUin(123).build();

    {
        mItemOfferGroupsList.add(CsBase.ItemOfferGroup.newBuilder().setRate(0.3f).build());
    }

    public ProductManage(Context context, ItemBean item) {
        mContext = context;
        mItem = item;
    }

    public void getServiceDes(TextView mResultString) {
        //会员
        int sDiscount = (int) (mItemOffer.getRate() * 100);

        //服务费  buy4you
        if (mItemOffer.getOfferType() == 2) {
            float serviceFee = mItem.getDefaultPrice() * mItem.getExchangeRate() * (mItemOfferGroupsList.size() > 0 ? mItemOfferGroupsList.get(0).getRate() : mItemOffer.getRate());
            //服务费：**。
            SpannableStringBuilder sServiceFee = new SpannableStringBuilder();
            sServiceFee.append(mContext.getString(R.string.String_service_charge));
            sServiceFee.append(UIUtils.getCurrency(mContext, serviceFee));
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);
            sServiceFee.setSpan(colorSpan, 0, mContext.getString(R.string.String_service_charge).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mResultString.append(sServiceFee);


            //买手
            String nickname = mSeller.getNickname();
            String sellerDes = mContext.getString(R.string.String_service_seller, nickname, sDiscount);
            SpannableStringBuilder sSSeller_1 = new SpannableStringBuilder(sellerDes);
//           由YISS认证买手 %1$s 提供代采购服务，服务费为单价×%2$d%%
            sSSeller_1.setSpan(new StyleSpan(Typeface.BOLD), sellerDes.indexOf(nickname), sellerDes.indexOf(nickname) + nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString sSVipRate_2 = null;
            if (mItemOfferGroupsList.size() > 0) {
                CsBase.ItemOfferGroup group = mItemOfferGroupsList.get(0);
                int count = group.getQty();
                float rate = (group.getRate() * 100);
                String sRate = formatFloat(rate);
                String sVipRate = mContext.getString(R.string.String_vip_rate, getMenberGroupName(), count, sRate);
                sSVipRate_2 = new SpannableString(sVipRate);
                int start = sVipRate.indexOf(count + "");
                sSVipRate_2.setSpan(new StyleSpan(Typeface.BOLD), start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                int start1 = sVipRate.indexOf(sRate + "%");
//               会员价格
                sSVipRate_2.setSpan(new StyleSpan(Typeface.BOLD), start1, start1 + (sRate + "%").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                原来的优惠划线掉
                sSSeller_1.setSpan(new StrikethroughSpan(), sellerDes.indexOf(sDiscount + "%"), sellerDes.indexOf(sDiscount + "%") + (sDiscount + "%").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mResultString.append("\n");
            mResultString.append(sSSeller_1);
            if (sSVipRate_2 != null) {
                mResultString.append("\n");
                mResultString.append(sSVipRate_2);
            }
        } else {//促销 buy now
            SpannableStringBuilder saletPrice = null;
            String promotion = mContext.getString(R.string.String_promotion);
            if (mItemOffer.getRate() != 0) {
                float rPrice = mItem.getDefaultPrice() * mItem.getExchangeRate() * (1 - mItemOffer.getRate());
/*
                float wPrice = mItem.getDefaultPrice() * (1 - mItemOffer.getRate());
                String currentPrice = CommonUtils.formatMoenyWidthCommaLocal((int) wPrice, mItem.getCurrency_code(), mContext);
                String sOldPrice = CommonUtils.formatMoenyWidthCommaLocal((int) mItem.getDefaultPrice(), mItem.getCurrency_code(), mContext);

                SpannableString oldPrice = new SpannableString(sOldPrice);
                oldPrice.setSpan(new StrikethroughSpan(), 0, oldPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//                实际售价
                String sDefaultPrice = UIUtils.getCurrency(mContext, mItem.getDefaultPrice() * mItem.getExchangeRate());
                String sNewPrice = UIUtils.getCurrency(mContext, rPrice);*/

//               每件%1$s，为您节省%2$d%%
                saletPrice = new SpannableStringBuilder(promotion);
                saletPrice.append(String.format(mContext.getString(R.string.String_promotion_rate), UIUtils.getCurrency(mContext, rPrice), sDiscount));
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);
                saletPrice.setSpan(colorSpan, 0, promotion.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

//                saletPrice = new SpannableStringBuilder(promotion + saletPrice);
            }

            SpannableString saletPriceNew = null;
            if (mItemOfferGroupsList.size() > 0) {//会员
                for (CsBase.ItemOfferGroup group : mItemOfferGroupsList) {

                    float finalPrice = mItem.getDefaultPrice() * mItem.getExchangeRate() * (1 - group.getRate());

//                  ▶ %1$s，一次购买%2$d件单价%3$s，为您节省%4$d%%
                    int vipRate = (int) (group.getRate() * 100);
                    String vipString = mContext.getString(R.string.String_vip_promotion, getMenberGroupName(), group.getQty(), UIUtils.getCurrency(mContext, finalPrice), vipRate);
                    saletPriceNew = new SpannableString(vipString);
                    saletPriceNew.setSpan(new StyleSpan(Typeface.BOLD), 13, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    String achor = UIUtils.getCurrency(mContext, finalPrice);
                    int endlength = vipString.indexOf(achor) + achor.length();
                    int start = vipString.indexOf(achor);
                    saletPriceNew.setSpan(new StyleSpan(Typeface.BOLD), start, endlength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    String achorRate = mContext.getString(R.string.String_number_oney, vipRate);
                    saletPriceNew.setSpan(new StyleSpan(Typeface.BOLD), vipString.indexOf(achorRate), vipString.indexOf(achorRate) + achorRate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    saletPrice.setSpan(new StrikethroughSpan(), sellerDes.indexOf(sDiscount + "%"), sellerDes.indexOf(sDiscount + "%") + (sDiscount + "%").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//                    原来的促销价格划线
                    saletPrice.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.gray_999)), promotion.length(), saletPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    saletPrice.setSpan(new StrikethroughSpan(), promotion.length(), saletPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            String seller2 = mContext.getString(R.string.String_service_seller2, mSeller.getNickname());
            SpannableString sString = new SpannableString(seller2);
            String nickname = mSeller.getNickname();
            sString.setSpan(new StyleSpan(Typeface.BOLD), seller2.indexOf(nickname), seller2.indexOf(nickname) + nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (saletPrice != null)
                mResultString.append(saletPrice);

            if (saletPriceNew != null) {
                mResultString.append("\n");
                mResultString.append(saletPriceNew);
            }
            mResultString.append("\n");
            mResultString.append(sString);
        }

//        return mResultString;
    }


    /*private void IsCrowdBuy() {
        if (isBuyNow()) {
            autoOpenButtom();
        } else {
            if (mIsOpenBottom) {
                Toast.makeText(ProductDetailActivity.this, getString(R.string.String_item_time_out), Toast.LENGTH_SHORT).show();
            }
        }
        if (isBuyNow() && isCrowd()) {
            mBtnBuyNow.setVisibility(View.VISIBLE);
            mBtnCrowd.setVisibility(View.VISIBLE);
            mImgAddCart.setVisibility(View.VISIBLE);
        } else if (isBuyNow() && !isCrowd()) {
            mBtnBuyNow.setVisibility(View.VISIBLE);
            mBtnCrowd.setVisibility(View.GONE);
            mImgAddCart.setVisibility(View.VISIBLE);
        } else if (!isBuyNow() && isCrowd()) {
            mBtnBuyNow.setVisibility(View.GONE);
            mBtnCrowd.setVisibility(View.VISIBLE);
            mImgCrowd.setVisibility(View.VISIBLE);
            mImgAddCart.setVisibility(View.GONE);
        } else if (!isBuyNow() && !isCrowd()) {
            mBtnBuyNow.setVisibility(View.GONE);
            mBtnCrowd.setVisibility(View.GONE);
            mImgAddCart.setVisibility(View.GONE);
            mImgItemTag.setImageResource(getTagRes(2));
        }
    }*/

    List<String[]> getSalePrices() {
        List<String[]> prices = new ArrayList<>();
        String[] rowPrices;
        String[] currentPrices;
        if (mItemOffer.getOfferType() != 2 & mItemOffer.getRate() != 0) {
            rowPrices = new String[2];
            currentPrices = new String[2];
            rowPrices[0] = CommonUtils.formatMoenyWidthCommaLocal(mItem.getDefaultPrice(), mItem.getCurrency_code(), mContext);
            rowPrices[1] = CommonUtils.formatMoenyWidthCommaLocal(mItem.getDefaultPrice() * (1 - mItemOffer.getRate()), mItem.getCurrency_code(), mContext);

            currentPrices[0] = UIUtils.getCurrency(mContext, mItem.getDefaultPrice() * mItem.getExchangeRate());
            currentPrices[1] = UIUtils.getCurrency(mContext, mItem.getDefaultPrice() * mItem.getExchangeRate() * (1 - mItemOffer.getRate()));

            if (mItemOfferGroupsList.size() > 0) {
                CsBase.ItemOfferGroup offerGroup = mItemOfferGroupsList.get(0);
                rowPrices[1] = CommonUtils.formatMoenyWidthCommaLocal(mItem.getDefaultPrice() * (1 - offerGroup.getRate()), mItem.getCurrency_code(), mContext);
                currentPrices[1] = UIUtils.getCurrency(mContext, mItem.getDefaultPrice() * mItem.getExchangeRate() * (1 - offerGroup.getRate()));
            }
        } else {
            rowPrices = new String[1];
            currentPrices = new String[1];
            rowPrices[0] = CommonUtils.formatMoenyWidthCommaLocal(mItem.getDefaultPrice(), mItem.getCurrency_code(), mContext);
            currentPrices[0] = UIUtils.getCurrency(mContext, mItem.getDefaultPrice() * mItem.getExchangeRate());
        }
        prices.add(rowPrices);
        prices.add(currentPrices);
        return prices;
    }


    boolean isBuyNow() {
        return !mItem.getCannotBuyit();
    }

    boolean isCrowd() {
        return mItem.getAlertType() != 0;
    }

    int getMinQty() {
        return mItemProduct.getMinQty();
    }

    int getMaxQty() {
        return mItemProduct.getMaxQty() > mItemOffer.getQty() ? (int) mItemOffer.getQty() : mItemProduct.getMaxQty();
    }

    boolean storyEnough() {
        return mItemOffer.getQty() > mItemProduct.getMinQty();
    }


    //    是不是促销
//    true 促销
//    false： 0 加价
    boolean isSales() {
        CsBase.ItemOfferGroup offerGroup = null;
        boolean hasRate = false;
        if (mItemOfferGroupsList.size() > 0) {
            offerGroup = mItemOfferGroupsList.get(0);
            hasRate = offerGroup.hasRate();
        }
//        hasRate：对特定会员打折
//        mItemOffer.getRate() != 0 商品本身打折
        return mItemOffer.getOfferType() == 1 && (mItemOffer.getRate() != 0 || hasRate);
    }


//    void showAttr(){
//
//        if (mItemExtendsList != null && mItemExtendsList.size() > 0) {
//            mllExtends.removeAllViews();
//            for (int i = 0; i < mItemExtendsList.size(); i++) {
//                CsBase.ItemExtend itemExtend = mItemExtendsList.get(i);
//                ProductExtendView extendView = new ProductExtendView(ProductDetailActivity.this);
//                extendView.showOption(itemExtend);
//                mllExtends.addView(extendView);
//            }
//        }
//    }

    String getMenberGroupName() {
        CsBase.UserInfo userInfo = AccountManager.getInstance().userInfo;
        int memberGroup = 0;
        if (userInfo != null)
            memberGroup = userInfo.getMemberGroup();
        String sMenberGroup = "";
        if (3 == memberGroup) {
            sMenberGroup = mContext.getString(R.string.String_super_member);
        } else if (2 == memberGroup) {
            sMenberGroup = mContext.getString(R.string.String_vip_member);
        } else {
            sMenberGroup = mContext.getString(R.string.String_norme_member);
        }
        return sMenberGroup;
    }

    private String formatFloat(float num) {
        String sNum = mContext.getString(R.string.String_float_1, num);
//        String start = ((int) num) + "";
//        String suffix = sNum.substring(start.length() + 1, sNum.length());
//        Integer value = Integer.valueOf(suffix);
        String[] split = sNum.split("\\.");
        Integer value = Integer.valueOf(split[1]);
        if (value > 0) {
            return sNum;
        } else {
            return split[0];
        }
    }

    public ProductManage setItemProduct(CsBase.ItemProduct itemProduct) {
        mItemProduct = itemProduct;
        return this;
    }

    public ProductManage setItemOffer(CsBase.ItemOffer itemOffer) {
        mItemOffer = itemOffer;
        return this;

    }

    public ProductManage setItemOfferGroupsList(List<CsBase.ItemOfferGroup> itemOfferGroupsList) {
        mItemOfferGroupsList = itemOfferGroupsList;
        return this;

    }

  /*  public ProductManage setItemExtendsList(List<CsBase.ItemExtend> itemExtendsList) {
        mItemExtendsList = itemExtendsList;
        return this;

    }*/

    public ProductManage setItem(ItemBean item) {
        mItem = item;
        return this;

    }

    public ProductManage setContext(Context context) {
        mContext = context;
        return this;

    }

    public ProductManage setSeller(CsBase.Seller seller) {
        mSeller = seller;
        return this;
    }
}
