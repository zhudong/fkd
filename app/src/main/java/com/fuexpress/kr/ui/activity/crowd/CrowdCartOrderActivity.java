package com.fuexpress.kr.ui.activity.crowd;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.OrderConstants;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.PayMethodManager;
import com.fuexpress.kr.model.RedPointCountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.AddNewAddressActivity;
import com.fuexpress.kr.ui.activity.AddressManagerActivity;
import com.fuexpress.kr.ui.activity.ParcelPayMethodActivity;
import com.fuexpress.kr.ui.activity.shopping_cart.ConfirmPaymentActivity;
import com.fuexpress.kr.ui.activity.shopping_cart.DeliveryActivity;
import com.fuexpress.kr.ui.activity.shopping_cart.PaymentSuccessActivity;
import com.fuexpress.kr.ui.adapter.OrderCommodityAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fksproto.CsAddress;
import fksproto.CsBase;
import fksproto.CsCart;
import fksproto.CsOrder;
import fksproto.CsShipping;
import fksproto.CsUser;


/**
 * Created by Administrator on 2016/4/13.
 */
public class CrowdCartOrderActivity extends BaseActivity implements View.OnClickListener {

    public static final String Crowd_order_info = "Crowd_order_info";
    public static CrowdOrderInfo mOrderInfo;
    public static CsAddress.CustomerAddress mAddress;
    public static int mShippingScheme = CsOrder.ShippingScheme.SHIPPING_SCHEME_MERGE_VALUE;
    public static CsShipping.Shipping mShipping;
    public static String mFuShippingUrl;


    @BindView(R.id.fu_direct_layout)
    TextView mFuDirectLayout;
    private int mPurchaseScheme = CsOrder.PurchaseScheme.PURCHASE_SCHEME_BOOK_VALUE;

    @BindView(R.id.megre_order_layout)
    LinearLayout mMegreOrderLayout;
    @BindView(R.id.direct_layout)
    LinearLayout mDirectLayout;
    @BindView(R.id.delivery_layout)
    LinearLayout mDeliveryLayout;
    @BindView(R.id.order_address_title_tv)
    TextView mOrderAddressTitleTv;
    @BindView(R.id.order_default_address_iv)
    TextView mOrderDefaultAddressIv;
    @BindView(R.id.order_name_and_phone_tv)
    TextView mOrderNameAndPhoneTv;
    @BindView(R.id.order_package_address)
    TextView mOrderPackageAddress;
    @BindView(R.id.address_arrow_iv)
    ImageView mAddressArrowIv;
    @BindView(R.id.order_package_address_layout)
    LinearLayout mOrderPackageAddressLayout;
    @BindView(R.id.shipping_left_tv)
    TextView mShippingLeftTv;
    @BindView(R.id.order_shipping_content_layout)
    LinearLayout mOrderShippingContentLayout;
    @BindView(R.id.order_shipping_layout)
    RelativeLayout mOrderShippingLayout;
    @BindView(R.id.order_payment_layout)
    RelativeLayout mOrderPaymentLayout;
    @BindView(R.id.order_content_layout)
    LinearLayout mOrderContentLayout;
    @BindView(R.id.cart_order_sv)
    ScrollView mCartOrderSv;
    @BindView(R.id.order_gandtotal_tv)
    TextView mOrderGandtotalTv;
    @BindView(R.id.order_grand_total_msg_tv)
    TextView mOrderGrandTotalMsgTv;
    @BindView(R.id.order_submit_btn)
    Button mOrderSubmitBtn;
    @BindView(R.id.title_tv_center)
    TextView mTitleTvCenter;
    @BindView(R.id.tv_warehose_name)
    TextView mTvWarehoseName;
    @BindView(R.id.payment_method)
    TextView mPaymentMethod;
    PayMethodManager mPayMethodManager;
    public final int MPAYMETHODCODE = 12334;

    @Override
    public View setInitView() {
        return View.inflate(this, R.layout.activity_crowd_cart_order, null);
    }

    @Override
    public void initView() {
        reset();
        mPayMethodManager = PayMethodManager.getInstance(this);
        mOrderInfo = (CrowdOrderInfo) getIntent().getExtras().getSerializable(Crowd_order_info);
        mTitleTvCenter.setText(getString(R.string.cart_order_title_bar_title));
        showShippingScheme();
        getCustomerAddressList();
        showItem();
        showPayMethod();
    }

    private void reset() {
        mFuShippingUrl = "";
        mOrderInfo = null;
        mAddress = null;
        mShippingScheme = (int) SPUtils.get(this, Constants.KEY_SELECTED_DELIVERY, Constants.SHIPPING_SCHEME_GIFT);
        mShipping = null;
        mPurchaseScheme = CsOrder.PurchaseScheme.PURCHASE_SCHEME_BOOK_VALUE;
    }

    public String mPayCode;

    private void showPayMethod() {
        mPayMethodManager.reSet();
        mPayMethodManager.getCurrentPayMethod(new PayMethodManager.PayMethodResultListener() {
            @Override
            public void onResult(final String payCode, final String result) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mPayCode = payCode;
                        mPaymentMethod.setText(result);
                    }
                });
            }

            @Override
            public void onMethodList(List<CsUser.PaymentList> paymantlistList, String balance, int couponCount) {

            }
        }, getPayTotal(), AccountManager.getInstance().getCurrencyCode());

    }

    private float getPayTotal() {
        if (mShippingScheme == CsOrder.ShippingScheme.SHIPPING_SCHEME_DIRECT_VALUE & mShipping != null) {
            return (float) (mOrderInfo.getTotal() + mShipping.getFee());
        } else {
            return mOrderInfo.getTotal();
        }
    }

    private void showItem() {
        mTvWarehoseName.setText(mOrderInfo.getWarehouse().getName());
        ArrayList<CsCart.SalesCartItem> list = new ArrayList<>();
        list.add(mOrderInfo.getSalesCartItem());
        OrderCommodityAdapter adapter = new OrderCommodityAdapter(this, list);
        View view = adapter.getView(0, null, null);
        mOrderContentLayout.addView(view);
    }

    @OnClick({R.id.title_iv_left, R.id.title_tv_left,
            R.id.delivery_layout, R.id.order_payment_layout,
            R.id.order_package_address_layout, R.id.order_submit_btn})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                finish();
                break;
            case R.id.order_package_address_layout:
                toSelectAddress();
                break;
            case R.id.delivery_layout:
                toDelivery();
                break;
            case R.id.order_payment_layout:
                intent = new Intent(this, ParcelPayMethodActivity.class);
                intent.putExtra(ParcelPayMethodActivity.CURRENCY_CODE, AccountManager.getInstance().getCurrencyCode());
                intent.putExtra(ParcelPayMethodActivity.SHIPPING_FEE, getPayTotal());
                intent.putExtra(ParcelPayMethodActivity.USE_COUPON, false);
                intent.putExtra(ParcelPayMethodActivity.USE_CHARGE, true);
                startActivityForResult(intent, MPAYMETHODCODE);
                break;
            case R.id.order_submit_btn:
                submitCrowdOrder();
                break;
        }
    }

    private void toSelectAddress() {
        Intent intent;
        intent = new Intent(this, AddressManagerActivity.class);
        intent.putExtra(AddressManagerActivity.KEY_IS_CHOOSE_TYPE, true);
        startActivityForResult(intent, Constants.ADDRESS_REQUEST_CODE);
    }

    private void toDelivery() {
        Intent intent;
        intent = new Intent(this, CrowdDeliveryActivity.class);
        startActivityForResult(intent, Constants.DELIVERY_REQUEST_CODE);
    }

    public void getCustomerAddressList() {
//        showLoading(5000);
        CsAddress.GetCustomerAddressListExRequest.Builder builder = CsAddress.GetCustomerAddressListExRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setPageNum(1);
        NetEngine.postRequest(builder, new INetEngineListener<CsAddress.GetCustomerAddressListExResponse>() {

            @Override
            public void onSuccess(CsAddress.GetCustomerAddressListExResponse response) {
//                closeLoading();
                LogUtils.d("getCustomerAddress: " + response.toString());
                final List<CsAddress.CustomerAddress> addressesList = response.getAddressesList();
                if (addressesList != null && addressesList.size() > 0) {
                    for (int i = 0; i < addressesList.size(); i++) {
                        CsAddress.CustomerAddress address = addressesList.get(i);
                        if (address.getIsDefault()) mAddress = address;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAddress();
                        if (addressesList == null || addressesList.size() == 0) {
                            setEmptyAddress();
                        }

                        if ((mShippingScheme == Constants.SHIPPING_SCHEME_DIRECT
                                || mShippingScheme == Constants.SHIPPING_SCHEME_GIFT))
                            toDelivery();
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }

    private void setEmptyAddress() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(CrowdCartOrderActivity.this);
        builder.setMessage(getString(R.string.delivert_dialog_msg));
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mOrderPackageAddressLayout.callOnClick();
            }
        });
        builder.create().show();
    }


    private void showAddress() {
        if (mAddress == null) {
            return;
        }
        switch (mShippingScheme) {
            case CsOrder.ShippingScheme.SHIPPING_SCHEME_DIRECT_VALUE:
                mOrderPackageAddressLayout.setClickable(false);
                mAddressArrowIv.setVisibility(View.GONE);
                break;
            case CsOrder.ShippingScheme.SHIPPING_SCHEME_MERGE_VALUE:
                mOrderPackageAddressLayout.setClickable(true);
                mAddressArrowIv.setVisibility(View.VISIBLE);
                break;
        }
        String str = getResources().getString(R.string.String_cart_consignee_msg);
        str = String.format(str, mAddress.getName(), mAddress.getPhone());
        mOrderNameAndPhoneTv.setText(str);
        mOrderPackageAddress.setText(mAddress.getStreet() + "," + mAddress.getFullRegionName());
        if (mAddress.getIsDefault()) {
            mOrderDefaultAddressIv.setVisibility(View.VISIBLE);
        } else {
            mOrderDefaultAddressIv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MPAYMETHODCODE) {
            showPayMethod();
        }
        if (requestCode == Constants.ADDRESS_REQUEST_CODE) {
            if (data != null) {
                mAddress = (CsAddress.CustomerAddress) data.getExtras().getSerializable("address");
                showAddress();
            }
        }
        if (requestCode == Constants.DELIVERY_REQUEST_CODE) {
            showShippingScheme();
            showAddress();
            showPayMethod();
        }
    }

    private void showShippingScheme() {
        SPUtils.put(this, Constants.KEY_SELECTED_DELIVERY, mShippingScheme);

        switch (mShippingScheme) {
            case CsOrder.ShippingScheme.SHIPPING_SCHEME_DIRECT_VALUE:
                mFuDirectLayout.setVisibility(View.GONE);
                mMegreOrderLayout.setVisibility(View.GONE);
                mDirectLayout.setVisibility(View.VISIBLE);
                mOrderPackageAddressLayout.setVisibility(View.VISIBLE);
                showShipping(true);
                showPrice(mShipping);
                break;
            case CsOrder.ShippingScheme.SHIPPING_SCHEME_MERGE_VALUE:
                mFuDirectLayout.setVisibility(View.GONE);
                mMegreOrderLayout.setVisibility(View.VISIBLE);
                mDirectLayout.setVisibility(View.GONE);
                mOrderPackageAddressLayout.setVisibility(View.VISIBLE);
                showPrice(null);
                showShipping(false);
                break;
            case 3:
                mFuDirectLayout.setVisibility(View.VISIBLE);
                mMegreOrderLayout.setVisibility(View.GONE);
                mDirectLayout.setVisibility(View.GONE);
                mOrderPackageAddressLayout.setVisibility(View.GONE);
                showPrice(null);
                showShipping(false);
                break;
        }
    }

    private void showPrice(CsShipping.Shipping shipping) {
        if (shipping != null) {
            mOrderGandtotalTv.setText(UIUtils.getCurrency(this, (float) (mOrderInfo.getTotal() + shipping.getFee())));
            mOrderGrandTotalMsgTv.setText(getString(R.string.cart_order_grand_total_msg, UIUtils.getCurrency(this, (float) shipping.getFee())));
        } else {
            mOrderGandtotalTv.setText(UIUtils.getCurrency(this, mOrderInfo.getTotal()));
            mOrderGrandTotalMsgTv.setText(getString(R.string.String_submit_hint));
        }
    }

    private void showShipping(boolean show) {
        if (show & mShipping != null) {
            mOrderShippingLayout.setVisibility(View.VISIBLE);
            mOrderShippingContentLayout.removeAllViews();
            LinearLayout shippingChildLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.order_shipping_content_child_layout, null);
            TextView orderShippingTitleTv = (TextView) shippingChildLayout.findViewById(R.id.order_shipping_title_tv);
            TextView orderShippingInfoTv = (TextView) shippingChildLayout.findViewById(R.id.order_shipping_info_tv);
            TextView orderShippingFeeTv = (TextView) shippingChildLayout.findViewById(R.id.order_shipping_fee_tv);
            orderShippingTitleTv.setText(mOrderInfo.getWarehouse().getName());
            orderShippingInfoTv.setText(mShipping.getTitle());
            String mFee = getString(R.string.String_order_price, UIUtils.getCurrency(this, (float) mShipping.getFee()));
            orderShippingFeeTv.setText(mFee);
            mOrderShippingContentLayout.addView(shippingChildLayout);
        } else {
            mOrderShippingLayout.setVisibility(View.GONE);
        }
    }

    public void submitCrowdOrder() {
        CsOrder.SubmitCrowdOrderRequest.Builder builder = CsOrder.SubmitCrowdOrderRequest.newBuilder();
        if (mAddress != null) {
            builder.setShippingAddressId(mAddress.getAddressId());
        }
        builder.setUseGiftCard(mPayMethodManager.isUseBalance());
        builder.setPayMethod(OrderConstants.getPayType(mPayCode, -1));
        builder.setPurchaseScheme(mPurchaseScheme);
        builder.setShippingScheme(mShippingScheme);
        if (mShippingScheme == Constants.SHIPPING_SCHEME_DIRECT) {
            builder.addPairs(CsBase.PairIntInt.newBuilder().setK(mOrderInfo.getWarehouse().getWarehouseId()).setV(mShipping.getShippingId()));
        }
        builder.setItemId(mOrderInfo.getItemBean().getItemid());
        builder.setQty(mOrderInfo.getCount());
        builder.setNote(mOrderInfo.getSalesCartItem().getNote());
        builder.setCrowdId(mOrderInfo.getItemBean().getCrowdId());

        final AccountManager instance = AccountManager.getInstance();
        builder.addAllAttrs(mOrderInfo.getSelectedAttrs())
                .setUserinfo(instance.getBaseUserRequest())
                .setCurrencycode(instance.getCurrencyCode())
                .setCurrencyid(instance.getCurrencyId())
                .setLocalecode(instance.getLocaleCode());
        showLoading(5000);
        NetEngine.postRequest(builder, new INetEngineListener<CsOrder.SubmitCrowdOrderResponse>() {

            @Override
            public void onSuccess(CsOrder.SubmitCrowdOrderResponse response) {
                mFuShippingUrl = response.getInitaddressurl();
                LogUtils.d(response.toString());
                RedPointCountManager.getOrderCount();
                SysApplication app = (SysApplication) CrowdCartOrderActivity.this.getApplication();
                app.setOrderNumber(response.getOrderNumber());
                app.setOrderType(1);
                app.setShippingScheme(mShippingScheme);
                Intent intent = new Intent();
                if (response.getTotalPaid() <= 0) {
                    app.setPaymentRequestCode(Constants.PAYMENT_REQUEST_CODE_SHOP_CART);
                    intent.putExtra(PaymentSuccessActivity.IS_CROWD, true);
                    intent.setClass(CrowdCartOrderActivity.this, PaymentSuccessActivity.class);
                    startActivity(intent);
                } else {
                    intent.setClass(CrowdCartOrderActivity.this, ConfirmPaymentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("orderNumber", response.getOrderNumber());
                    bundle.putLong("orderId", response.getOrderId());
                    bundle.putFloat("subTotal", mOrderInfo.getCount() * mOrderInfo.getSalesCartItem().getUnitPrice());
                    bundle.putFloat("grandTotal", mOrderInfo.getCount() * mOrderInfo.getSalesCartItem().getUnitPrice());
                    bundle.putInt("payType", OrderConstants.getPayType(mPayCode, -1));
                    bundle.putFloat("totalpaid", response.getTotalPaid());
                    String paymentString = "";
                    String[] split = mPaymentMethod.getText().toString().split("\n");
                    paymentString = split[0];
                    if (split.length > 1) {
                        paymentString = split[1];
                    }
                    bundle.putString("payMethod", paymentString);
                    bundle.putInt("purchaseScheme", mPurchaseScheme);
                    bundle.putInt("shippingScheme", mShippingScheme);
                    bundle.putFloat("balance", mPayMethodManager.getFreeBalance());
                    bundle.putString("fee", mOrderGandtotalTv.getText().toString());
                    List<CsShipping.Shipping> shippings = new ArrayList<>();
                    if (mShipping != null)
                        shippings.add(mShipping);
                    bundle.putSerializable("shippingList", (Serializable) shippings);
                    bundle.putInt("commodityCount", mOrderInfo.getCount());
                    bundle.putBoolean("isUseBalance", mPayMethodManager.isUseBalance());
                    intent.putExtras(bundle);
                    intent.putExtra(ConfirmPaymentActivity.IS_CROWD_ORDER, true);
                    startActivity(intent);
                }
                closeLoading();
                finish();
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
                Looper.prepare();
                Toast.makeText(CrowdCartOrderActivity.this, getResources().getString(R.string.cart_order_submit_order_failed) + errMsg + ":" + ret, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }
}
