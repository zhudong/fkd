package com.fuexpress.kr.ui.activity.crowd;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.AddressManagerActivity;
import com.fuexpress.kr.ui.activity.ContractServiceActivity;
import com.fuexpress.kr.ui.adapter.OrderCommodityAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.CustomToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fksproto.CsAddress;
import fksproto.CsCart;
import fksproto.CsOrder;
import fksproto.CsShipping;


/**
 * Created by Administrator on 2016/4/18.
 */
public class CrowdDeliveryActivity extends BaseActivity {

    @BindView(R.id.title_tv_center)
    TextView mTitleTvCenter;
    @BindView(R.id.merge_order_title)
    TextView mMergeOrderTitle;
    @BindView(R.id.delivery_merge_order_help_iv)
    ImageView mDeliveryMergeOrderHelpIv;
    @BindView(R.id.delivery_merge_order_iv)
    ImageView mDeliveryMergeOrderIv;
    @BindView(R.id.delivery_merge_order_layout)
    RelativeLayout mDeliveryMergeOrderLayout;
    @BindView(R.id.direct_mail_title)
    TextView mDirectMailTitle;
    @BindView(R.id.delivery_direct_mail_help_iv)
    ImageView mDeliveryDirectMailHelpIv;
    @BindView(R.id.delivery_direct_mail_iv)
    ImageView mDeliveryDirectMailIv;
    @BindView(R.id.delivery_direct_mail_layout)
    RelativeLayout mDeliveryDirectMailLayout;
    @BindView(R.id.address_pin_iv)
    ImageView mAddressPinIv;
    @BindView(R.id.delivery_name_and_phone_tv)
    TextView mDeliveryNameAndPhoneTv;
    @BindView(R.id.delivery_default_address_iv)
    TextView mDeliveryDefaultAddressIv;
    @BindView(R.id.delivery_phone_tv)
    TextView mDeliveryPhoneTv;
    @BindView(R.id.delivery_address_layout)
    RelativeLayout mDeliveryAddressLayout;
    @BindView(R.id.delivery_commodity_layout)
    LinearLayout mDeliveryCommodityLayout;
    @BindView(R.id.delivery_confirm_btn)
    Button mDeliveryConfirmBtn;
    @BindView(R.id.tv_ware_house_name)
    TextView mTvWareHouseName;
    @BindView(R.id.img_arrow)
    ImageView mImgArrow;
    @BindView(R.id.ll_item_container)
    LinearLayout mLlItemContainer;
    @BindView(R.id.tv_real_price_notice)
    TextView mTvRealPriceNotice;
    @BindView(R.id.tv_real_price)
    TextView mTvRealPrice;
    @BindView(R.id.tv_product_rate_notice)
    TextView mTvProductRateNotice;
    @BindView(R.id.tv_product_tate)
    TextView mTvProductTate;
    @BindView(R.id.tv_shipping_rate_notice)
    TextView mTvShippingRateNotice;
    @BindView(R.id.tv_shipping_tate)
    TextView mTvShippingTate;
    @BindView(R.id.rl_rate)
    RelativeLayout mRlRate;
    @BindView(R.id.delivery_direct_fu_iv)
    ImageView mDeliveryDirectFuIv;
    @BindView(R.id.tv_real_price_text)
    TextView mTvRealPriceText;
    @BindView(R.id.tv_product_rate_text)
    TextView mTvProductRateText;
    @BindView(R.id.tv_shipping_rate_text)
    TextView mTvShippingRateText;
    @BindView(R.id.title_iv_right)
    ImageView mTitleIvRight;

    private int mOrderShippingScheme = CrowdCartOrderActivity.mShippingScheme;
    private CsAddress.CustomerAddress mAddress = CrowdCartOrderActivity.mAddress;
    private CrowdOrderInfo mOrderInfo = CrowdCartOrderActivity.mOrderInfo;
    private CsShipping.Shipping mShipping = CrowdCartOrderActivity.mShipping;

    @Override
    public View setInitView() {
        return View.inflate(this, R.layout.activity_crowd_delivery, null);
    }

    @Override
    public void initView() {
        initTitle();
        if (mAddress != null) {
            setAddress(mAddress);
        }
        showOrderScheme();
        showOrderItem();
    }

    private void initTitle() {
        mTitleTvCenter.setText(getString(R.string.String_delivery_method));
    }

    private void showOrderItem() {
        mTvWareHouseName.setText(mOrderInfo.getWarehouse().getName());
        ArrayList<CsCart.SalesCartItem> list = new ArrayList<>();
        list.add(mOrderInfo.getSalesCartItem());
        OrderCommodityAdapter adapter = new OrderCommodityAdapter(this, list);
        for (int i = 0; i < list.size(); i++) {
            View view = adapter.getView(i, null, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, UIUtils.dip2px(1), 0, 0);
            mLlItemContainer.addView(view, layoutParams);
        }
        mImgArrow.setImageResource(R.mipmap.arrow_up);
    }

    private void showOrderScheme() {
        switch (mOrderShippingScheme) {
            case CsOrder.ShippingScheme.SHIPPING_SCHEME_DIRECT_VALUE:
                mDeliveryDirectMailIv.setImageResource(R.mipmap.cart_select);
                mDeliveryMergeOrderIv.setImageResource(R.mipmap.cart_unselect);
                mDeliveryDirectFuIv.setImageResource(R.mipmap.cart_unselect);
                mDeliveryCommodityLayout.setVisibility(View.VISIBLE);
                mDeliveryAddressLayout.setVisibility(View.VISIBLE);
                getCrowdShipping(mAddress);
                break;
            case CsOrder.ShippingScheme.SHIPPING_SCHEME_MERGE_VALUE:
                mDeliveryDirectMailIv.setImageResource(R.mipmap.cart_unselect);
                mDeliveryMergeOrderIv.setImageResource(R.mipmap.cart_select);
                mDeliveryDirectFuIv.setImageResource(R.mipmap.cart_unselect);
                mDeliveryCommodityLayout.setVisibility(View.GONE);
                mDeliveryAddressLayout.setVisibility(View.VISIBLE);
                mRlRate.setVisibility(View.GONE);
                mShipping = null;
                break;
            case 3:
                mDeliveryDirectMailIv.setImageResource(R.mipmap.cart_unselect);
                mDeliveryMergeOrderIv.setImageResource(R.mipmap.cart_unselect);
                mDeliveryDirectFuIv.setImageResource(R.mipmap.cart_select);
                mDeliveryCommodityLayout.setVisibility(View.GONE);
                mDeliveryAddressLayout.setVisibility(View.GONE);
                mRlRate.setVisibility(View.GONE);
                mShipping = null;
                break;
        }
    }

    private void getCrowdShipping(CsAddress.CustomerAddress address) {
        if (mOrderShippingScheme == CsOrder.ShippingScheme.SHIPPING_SCHEME_MERGE_VALUE) return;
        CsShipping.GetCrowdShippingMethodListRequest.Builder builder = CsShipping.GetCrowdShippingMethodListRequest.newBuilder();
        final CrowdOrderInfo mOrderInfo = CrowdCartOrderActivity.mOrderInfo;
        ItemBean itemBean = mOrderInfo.getItemBean();
        AccountManager instance = AccountManager.getInstance();
        builder.setCrowdId(itemBean.getCrowdId())
                .setItemId(itemBean.getItemid())
                .setQty(mOrderInfo.getCount())
                .setUserinfo(instance.getBaseUserRequest())
                .setCurrencycode(instance.getCurrencyCode())
                .setCurrencyid(instance.getCurrencyId())
                .setLocalecode(instance.getLocaleCode());

        if (address != null) {
            builder.setShippingAddressId(address.getAddressId());
        } else {
            return;
        }
        builder.setWarehouseId(mOrderInfo.getWarehouse().getWarehouseId());
//        builder.setWarehouseId(1);
        NetEngine.postRequest(builder, new INetEngineListener<CsShipping.GetCrowdShippingMethodListResponse>() {

            @Override
            public void onSuccess(CsShipping.GetCrowdShippingMethodListResponse response) {

                final List<CsShipping.Shipping> shippingsList = response.getShippingsList();
                if (shippingsList != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeLoading();
                           /* crowdPairList.clear();
                            for (int i = 0; i < shippingsList.size(); i++) {
                                CsBase.PairIntInt buildInt = CsBase.PairIntInt.newBuilder().setK(mOrderInfo.getWarehouse().getWarehouseId()).setV(shippingsList.get(i).getShippingId()).build();
                                crowdPairList.add(buildInt);
                            }*/
                            setListAdapter(shippingsList);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
            }
        });
    }

    private void setListAdapter(final List<CsShipping.Shipping> shippingsList) {
        mDeliveryCommodityLayout.removeAllViews();
        for (int i = 0; i < shippingsList.size(); i++) {
            CsShipping.Shipping shipping = shippingsList.get(i);
            View inflate = View.inflate(this, R.layout.item_crowd_shipping, null);
            CheckedTextView checkedTextView = (CheckedTextView) inflate.findViewById(R.id.checked_tv);
            if (mShipping != null && mShipping.getShippingId() == shipping.getShippingId()) {
                checkedTextView.setChecked(true);
                showRate(mShipping);
            }
            TextView tvName = (TextView) inflate.findViewById(R.id.tv_shipping_name);
            TextView tvInfo = (TextView) inflate.findViewById(R.id.tv_shipping_info);
            TextView tvPrice = (TextView) inflate.findViewById(R.id.tv_price);
            SpannableStringBuilder spannableString = new SpannableStringBuilder();
//            spannableString.append(shipping.getTitle() + "\n");
            String[] split = (shipping.getTitle() + "\n" + shipping.getInfo()).split("\n");
            for (int j = 0; j < split.length; j++) {
                String aSplit = split[j];
                if (!TextUtils.isEmpty(aSplit)) {
                    spannableString.append(aSplit);
                    if (j < split.length - 1) spannableString.append("\n");
                }
            }
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);
            spannableString.setSpan(colorSpan, 0, shipping.getTitle().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.gray_999));
            spannableString.setSpan(colorSpan2, shipping.getTitle().length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            tvName.setText(spannableString);
            tvPrice.setText(UIUtils.getCurrency(this, (float) shipping.getFee()));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, UIUtils.dip2px(1), 0, 0);
            mDeliveryCommodityLayout.addView(inflate, layoutParams);
            inflate.setTag(i);
            inflate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CsShipping.Shipping shipping1 = shippingsList.get((int) v.getTag());
                    String alertMsg = shipping1.getAlertMsg();
                    if (!TextUtils.isEmpty(alertMsg)) {
                        showAlertDialog(alertMsg);
                        return;
                    }

                    if (mOrderInfo.getCount() * mOrderInfo.getSalesCartItem().getUnitPrice() > shipping1.getParcelSubtotalQuota()) {
                        showAlertDialog(getString(R.string.out_of_range_note_text, UIUtils.getCurrency(CrowdDeliveryActivity.this, shipping1.getParcelSubtotalQuota())));
                        return;
                    }

                    for (int j = 0; j < shippingsList.size(); j++) {
                        View child = mDeliveryCommodityLayout.getChildAt(j);
                        CheckedTextView checkedTextView = (CheckedTextView) child.findViewById(R.id.checked_tv);
                        if (j == (int) v.getTag()) {
                            checkedTextView.setChecked(true);
                            mShipping = shippingsList.get(j);
                            showRate(mShipping);
                        } else {
                            checkedTextView.setChecked(false);
                        }
                    }
                }
            });
        }
        if (mShipping == null
                && mDeliveryCommodityLayout.getChildCount() > 0
                && TextUtils.isEmpty(shippingsList.get(0).getAlertMsg())
                && mOrderInfo.getCount() * mOrderInfo.getSalesCartItem().getUnitPrice() <= shippingsList.get(0).getParcelSubtotalQuota()) {
            mDeliveryCommodityLayout.getChildAt(0).callOnClick();
        }
    }

    private void showAlertDialog(String alertMsg) {
        CustomDialog.Builder builder = new CustomDialog.Builder(CrowdDeliveryActivity.this);
        builder.setMessage(alertMsg).setPositiveButton(R.string.pick_up_right_confrim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showRate(CsShipping.Shipping shipping) {
        if (shipping.getIsNeedDuty()) {
            mRlRate.setVisibility(View.VISIBLE);
            mTvRealPriceNotice.setText(getString(R.string.package_product_realy_price_note, UIUtils.getCurrency(this, shipping.getParcelSubtotalQuota())));
            mTvRealPrice.setText(UIUtils.getCurrency(this, mOrderInfo.getCount() * mOrderInfo.getSalesCartItem().getUnitPrice()));
            mTvProductRateNotice.setText(getString(R.string.package_product_tarrif_note, shipping.getDutyRate()));
            mTvProductTate.setText(UIUtils.getCurrency(this, mOrderInfo.getCount() * mOrderInfo.getSalesCartItem().getUnitPrice() * shipping.getDutyRate() / 100));
            mTvShippingRateNotice.setText(getString(R.string.package_product_tarrif_note, shipping.getDutyRate()));
            mTvShippingTate.setText(UIUtils.getCurrency(this, shipping.getShippingDuty()));
        } else {
            mRlRate.setVisibility(View.GONE);
        }

    }


    @OnClick({R.id.title_iv_left, R.id.title_tv_left, R.id.delivery_address_layout,
            R.id.delivery_merge_order_help_iv, R.id.delivery_direct_mail_help_iv,
            R.id.delivery_merge_order_iv, R.id.delivery_direct_mail_iv, R.id.img_arrow,
            R.id.delivery_confirm_btn, R.id.delivery_direct_fu_layout, R.id.delivery_direct_fu_help_iv,
            R.id.title_iv_right})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                finish();
                break;
            case R.id.delivery_merge_order_help_iv:
                showDialog(getString(R.string.String_merge_order), getString(R.string.dialog_merge_order_help_msg));
                break;
            case R.id.delivery_direct_mail_help_iv:
                showDialog(getString(R.string.String_direct_mail_1), getString(R.string.dialog_direct_mail_help_msg));
                break;
            case R.id.delivery_direct_fu_help_iv:
                showDialog(getString(R.string.String_direct_fu), getString(R.string.dialog_gift_help_msg));
                break;
            case R.id.delivery_direct_fu_layout:
                mOrderShippingScheme = 3;
                showOrderScheme();
                break;
            case R.id.delivery_merge_order_iv:
                mOrderShippingScheme = CsOrder.ShippingScheme.SHIPPING_SCHEME_MERGE_VALUE;
                showOrderScheme();
                break;
            case R.id.delivery_direct_mail_iv:
                mOrderShippingScheme = CsOrder.ShippingScheme.SHIPPING_SCHEME_DIRECT_VALUE;
                showOrderScheme();
                break;
            case R.id.img_arrow:
                switchItemLayout();
                break;
            case R.id.delivery_address_layout:
                intent = new Intent(this, AddressManagerActivity.class);
                intent.putExtra(AddressManagerActivity.KEY_IS_CHOOSE_TYPE, true);
                startActivityForResult(intent, Constants.ADDRESS_REQUEST_CODE);
                break;
            case R.id.delivery_confirm_btn:
                if (mOrderShippingScheme == CsOrder.ShippingScheme.SHIPPING_SCHEME_DIRECT_VALUE & mShipping == null) {
                    Toast.makeText(this, mOrderInfo.getWarehouse().getName() + getString(R.string.delivery_toast_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                CrowdCartOrderActivity.mShippingScheme = mOrderShippingScheme;
                CrowdCartOrderActivity.mAddress = mAddress;
                CrowdCartOrderActivity.mShipping = mShipping;
                setResult(0);
                finish();
                break;
            case R.id.title_iv_right:
                switchMenu();
                break;
        }
    }

    private void switchItemLayout() {
        if (mLlItemContainer.getChildCount() > 0) {
            for (int i = 0; i < mLlItemContainer.getChildCount(); i++)
                mLlItemContainer.removeViewAt(i);
            mImgArrow.setImageResource(R.mipmap.arrow_down);
        } else {
            showOrderItem();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADDRESS_REQUEST_CODE) {
            if (data != null) {
                mAddress = (CsAddress.CustomerAddress) data.getExtras().getSerializable("address");
                mShipping = null;
                setAddress(mAddress);
            }
        }
    }

    private void setAddress(CsAddress.CustomerAddress address) {
        mRlRate.setVisibility(View.GONE);
        mAddress = address;
        if (address != null) {
            String str = getResources().getString(R.string.String_cart_consignee_msg);
            str = String.format(str, address.getName(), address.getPhone());
            mDeliveryNameAndPhoneTv.setText(str);
            String region = address.getFullRegionName();
            mDeliveryPhoneTv.setText(address.getStreet() + "," + region);
            if (address.getIsDefault()) {
                mDeliveryDefaultAddressIv.setVisibility(View.VISIBLE);
            } else {
                mDeliveryDefaultAddressIv.setVisibility(View.GONE);
            }
        }
        showOrderScheme();
    }

    public void showDialog(String title, String msg) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.cart_order_dialog_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startDDMActivity(ContractServiceActivity.class, true);
//                MeiqiaManager.getInstance(DeliveryActivity.this).contactCustomerService();
            }
        });
        builder.setNegativeButton(getString(R.string.cart_dialog_btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
