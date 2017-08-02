package com.fuexpress.kr.ui.activity.crowd;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.CommoditysBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.RedPointCountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.AddressManagerActivity;
import com.fuexpress.kr.ui.activity.ContractServiceActivity;
import com.fuexpress.kr.ui.activity.PaymentActivity;
import com.fuexpress.kr.ui.activity.help_signed.data.WareHouseBean;
import com.fuexpress.kr.ui.activity.shopping_cart.ConfirmPaymentActivity;
import com.fuexpress.kr.ui.activity.shopping_cart.DeliveryActivity;
import com.fuexpress.kr.ui.activity.shopping_cart.PaymentSuccessActivity;
import com.fuexpress.kr.ui.adapter.OrderCommodityAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.CustomListView;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.socks.library.KLog;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fksproto.CsAddress;
import fksproto.CsBase;
import fksproto.CsCard;
import fksproto.CsCart;
import fksproto.CsOrder;
import fksproto.CsShipping;
import fksproto.CsUser;


/**
 * Created by Administrator on 2016/4/13.
 */
public class CrowdCartOrderActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CrowdCartOrderActivity";
    public static final String FLAG_FROM_CROWD = "flag_from_crowd";
    private View rootView;
    private LinearLayout orderContentLayout;
    private LinearLayout deliveryLayout;
    private RelativeLayout shippingLayout;
    private RelativeLayout paymentLayout;
    private LinearLayout packageAddressLayout;
    private LinearLayout shippingContentLayout;
    private LinearLayout megreOrderLayout;
    private LinearLayout directLayout;
    private TextView nameAndPhoneTv;
    private TextView packageAddress;
    private TextView textViewTitleLeftShopCart;
    private TextView payTypeTv;
    //    private RadioGroup reseveRG;
    private ImageView reseveRB;
    private ImageView cancelRB;
    private Button orderSubmitBtn;
    private TextView directPriceTv;

    private TextView grandTotalTv;
    private TextView defaultAddressIv;
    private ImageView addressArrowIv;
    private ImageView reseveIv;
    private ImageView cancelIv;
    private TextView paymentTypeBalanceTv;
    private TextView grandTotalMsgTv;


    private CommoditysBean commoditysBean;
    private List<CommoditysBean> list;
    private WareHouseBean whBean;
    private CommoditysBean cb;
    private ScrollView orderSv;

    private CsAddress.CustomerAddress address;
    private int payType = -1;
    private int purchaseScheme = 2;
    private int shippingScheme = 2;
    private boolean isUseBalance;
    private int paymentPos;
    private float balance;
    private float grandTotal;
    private String fee;
    private int qtyCount;
    private List<CsBase.PairIntInt> pairList;
    private boolean mIsCrowdOrder;
    private CsCart.GetSalesCartListResponse cartResponse;
    private CsCart.SalesCartItem mCrowdSalesCartItem;
    private List<CsShipping.Shipping> shippingList;
    private List<CsCart.SalesCartItem> itemList;
    private List<CsBase.Warehouse> warehouseList;
    private List<CsBase.PairIntInt> crowdPairList;
    private SharedPreferences shared;

    private String paymentString = "";//默认支付方式
    private String payCode = "";//默认支付方式
    private float needPay;

    private float dutyRate;

    @Override
    public View setInitView() {
        rootView = View.inflate(this, R.layout.activity_cart_order, null);
        TitleBarView titleBarView = (TitleBarView) rootView.findViewById(R.id.cart_order_titlebar);
        titleBarView.setTitleText(getResources().getString(R.string.cart_order_title_bar_title));
        ImageView toBackIv = titleBarView.getIv_in_title_back();

        textViewTitleLeftShopCart = titleBarView.getmTv_in_title_back_tv();
        textViewTitleLeftShopCart.setText(getString(R.string.shop_cart_title_txt));

        Intent intent = getIntent();
//        list = (List<CommoditysBean>) intent.getExtras().getSerializable("commoditysBean");
//        cartResponse = (CsCart.GetSalesCartListResponse) intent.getExtras().getSerializable("salesCartResponse");
        qtyCount = intent.getIntExtra("qtyCount", 0);
        mIsCrowdOrder = intent.getBooleanExtra(FLAG_FROM_CROWD, false);


        orderContentLayout = (LinearLayout) rootView.findViewById(R.id.order_content_layout);
        deliveryLayout = (LinearLayout) rootView.findViewById(R.id.delivery_layout);
        shippingLayout = (RelativeLayout) rootView.findViewById(R.id.order_shipping_layout);
        packageAddressLayout = (LinearLayout) rootView.findViewById(R.id.order_package_address_layout);
        megreOrderLayout = (LinearLayout) rootView.findViewById(R.id.megre_order_layout);
        directLayout = (LinearLayout) rootView.findViewById(R.id.direct_layout);
        paymentLayout = (RelativeLayout) rootView.findViewById(R.id.order_payment_layout);
        shippingContentLayout = (LinearLayout) rootView.findViewById(R.id.order_shipping_content_layout);
//        reseveRG = (RadioGroup) rootView.findViewById(R.id.order_reseve_rg);
        reseveRB = (ImageView) rootView.findViewById(R.id.reseve_rb);
        cancelRB = (ImageView) rootView.findViewById(R.id.cancel_order_rb);
        orderSv = (ScrollView) rootView.findViewById(R.id.cart_order_sv);
        nameAndPhoneTv = (TextView) rootView.findViewById(R.id.order_name_and_phone_tv);
        packageAddress = (TextView) rootView.findViewById(R.id.order_package_address);
        payTypeTv = (TextView) rootView.findViewById(R.id.payment_type_tv);
        orderSubmitBtn = (Button) rootView.findViewById(R.id.order_submit_btn);
        directPriceTv = (TextView) rootView.findViewById(R.id.direct_price_tv);
        shared = getSharedPreferences("payShare", Constants.PAY_TYPE_SHARE_CODE);

        grandTotalTv = (TextView) rootView.findViewById(R.id.order_gandtotal_tv);
        defaultAddressIv = (TextView) rootView.findViewById(R.id.order_default_address_iv);
        addressArrowIv = (ImageView) rootView.findViewById(R.id.address_arrow_iv);
        paymentTypeBalanceTv = (TextView) rootView.findViewById(R.id.payment_type_balance_tv);
        reseveIv = (ImageView) rootView.findViewById(R.id.reseve_iv);
        cancelIv = (ImageView) rootView.findViewById(R.id.cancel_iv);
        grandTotalMsgTv = (TextView) rootView.findViewById(R.id.order_grand_total_msg_tv);
        if (mIsCrowdOrder) {
            rootView.findViewById(R.id.ll_buy_type_container).setVisibility(View.GONE);
            rootView.findViewById(R.id.rl_buy_type_item).setVisibility(View.GONE);
//            CommoditysBean commoditysBean = list.get(1);
//            List<CsCart.SalesCartItem> salesCartItems = commoditysBean.getSalesCartItems();
//            grandTotal = intent.getFloatExtra("grandTotal", 0.00f);
            String sGrand = getString(R.string.cart_order_grand_total, grandTotal);
            needPay = grandTotal;
            grandTotalTv.setText(UIUtils.getCurrency(this, grandTotal));
            List<CsCart.SalesCartItem> listData = getListData();
            if (listData != null)
                setListAdapter(listData);
        }

        giftCardBalanceList();
        getCustomerAddressList();
        int i = (int) SPUtils.get(this, AccountManager.getInstance().getCurrencyCode(), -1);
        if (i == -1) {
            getFKDPaymentListRequest();
        } else {
            getFKDPaymentListRequest();

            reseveRB.setOnClickListener(this);
            cancelRB.setOnClickListener(this);
            reseveIv.setOnClickListener(this);
            cancelIv.setOnClickListener(this);
            deliveryLayout.setOnClickListener(this);
            paymentLayout.setOnClickListener(this);
            orderSubmitBtn.setOnClickListener(this);
            packageAddressLayout.setOnClickListener(this);
            toBackIv.setOnClickListener(this);
            textViewTitleLeftShopCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        return rootView;
    }


    public void setListAdapter(CsCart.GetSalesCartListResponse response) {

        final List<CsCart.SalesCartItem> commodityList = response.getItemsList();
        final List<CsBase.Warehouse> warehouseList = response.getWarehousesList();
        orderContentLayout.removeAllViews();

        for (int i = 0; i < warehouseList.size(); i++) {

            TextView wareHouseTextView = new TextView(this);
            wareHouseTextView.setText(warehouseList.get(i).getName());
            wareHouseTextView.setPadding(30, 30, 30, 30);
            wareHouseTextView.setTextColor(Color.BLACK);
            wareHouseTextView.setTextSize(14);
            wareHouseTextView.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 30, 0, 0);
            wareHouseTextView.setLayoutParams(lp);

            CustomListView commodityListView = new CustomListView(this);
            orderSv.scrollTo(0, 0);
            commodityListView.setFocusable(false);
            commodityListView.setBackgroundColor(Color.WHITE);

            ImageView line = new ImageView(this);
            line.setImageResource(R.color.line_gray);
            LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            line.setLayoutParams(lineLp);


            orderContentLayout.addView(wareHouseTextView);
            orderContentLayout.addView(line);
            List<CsCart.SalesCartItem> list = new ArrayList<>();
//                    HashMap<Integer, Boolean> isSelecetdMap =
            wareHouseTextView.setVisibility(View.GONE);
            for (int j = 0; j < commodityList.size(); j++) {
                if (warehouseList.get(i).getWarehouseId() == commodityList.get(j).getWarehouseId()) {
                    if (commodityList.get(j).getIsSelected()) {
                        qtyCount += commodityList.get(j).getQty();
                        wareHouseTextView.setVisibility(View.VISIBLE);
                        list.add(commodityList.get(j));
                    } else {

                    }
                }
            }
            OrderCommodityAdapter adapter = new OrderCommodityAdapter(this, list);
            commodityListView.setAdapter(adapter);
            orderContentLayout.addView(commodityListView);
        }
    }

    public void setListAdapter(List<CsCart.SalesCartItem> list) {
        ListView commodityListView = new ListView(this);
        orderSv.scrollTo(0, 0);
        commodityListView.setFocusable(false);
        commodityListView.setBackgroundColor(Color.WHITE);
        OrderCommodityAdapter adapter = new OrderCommodityAdapter(this, list);
        commodityListView.setAdapter(adapter);
        for (int i = 0; i < cb.getWarehouses().size(); i++) {
            TextView wareHouseTextView = new TextView(this);
            wareHouseTextView.setText(cb.getWarehouses().get(i).getName());
            wareHouseTextView.setPadding(30, 30, 30, 30);
            wareHouseTextView.setTextColor(Color.BLACK);
            wareHouseTextView.setTextSize(14);
            wareHouseTextView.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 30, 0, 0);
            wareHouseTextView.setLayoutParams(lp);
            setListViewHeightBasedOnChildren(commodityListView);
            ImageView line = new ImageView(this);
            line.setImageResource(R.color.line_gray);
            LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            line.setLayoutParams(lineLp);
            orderContentLayout.removeAllViews();
            orderContentLayout.addView(wareHouseTextView);
            orderContentLayout.addView(line);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            orderContentLayout.addView(commodityListView, params);
        }
    }

    public void getSaleCartItem() {
//        qtyCount = 0;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoading(5000);
            }
        });
        CsCart.GetSalesCartListRequest.Builder builder = CsCart.GetSalesCartListRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.GetSalesCartListResponse>() {

            @Override
            public void onSuccess(CsCart.GetSalesCartListResponse response) {
                closeLoading();
                LogUtils.d(TAG, "response: " + response.toString());
                cartResponse = response;
                itemList = response.getItemsList();
                warehouseList = response.getWarehousesList();
                Message msg = Message.obtain();
                msg.obj = response;
                cartHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
                LogUtils.d(TAG, "ErrorMsg: " + errMsg);
            }
        });
    }

    private Handler cartHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CsCart.GetSalesCartListResponse response = (CsCart.GetSalesCartListResponse) msg.obj;
            final List<CsCart.SalesCartItem> commodityList = response.getItemsList();
            final List<CsBase.Warehouse> warehouseList = response.getWarehousesList();

            list = getCommodityData(warehouseList, commodityList);
            grandTotal = response.getGrandtotal();
            needPay = grandTotal;
            grandTotalTv.setText(UIUtils.getCurrency(CrowdCartOrderActivity.this, grandTotal));
//            List<CsCart.SalesCartItem> dataList = getListData(response);
            setListAdapter(response);
        }
    };


    public List<CommoditysBean> getCommodityData(List<CsBase.Warehouse> whList, List<CsCart.SalesCartItem> commodityList) {
        List<CommoditysBean> list = new ArrayList<CommoditysBean>();
        for (int i = 0; i < whList.size(); i++) {
            CommoditysBean warehousecb = new CommoditysBean();
            warehousecb.setWarehouses(whList);
            warehousecb.setType(0);
            list.add(warehousecb);
            for (int j = 0; j < commodityList.size(); j++) {
                if (whList.get(i).getWarehouseId() == commodityList.get(j).getWarehouseId()) {
                    CommoditysBean commoditycb = new CommoditysBean();
                    commoditycb.setSalesCartItems(commodityList);
                    commoditycb.setType(1);
                    list.add(commoditycb);
                }
            }
        }
        return list;
    }

    public List<CsCart.SalesCartItem> getListData(CsCart.GetSalesCartListResponse response) {
        List<CsBase.Warehouse> wareHouseList = response.getWarehousesList();
        List<CsCart.SalesCartItem> salesCartList = response.getItemsList();

        List<CsCart.SalesCartItem> salesTempList = new ArrayList<CsCart.SalesCartItem>();
        List<CsBase.Warehouse> wareHouseTempList = new ArrayList<CsBase.Warehouse>();

        for (int i = 0; i < wareHouseList.size(); i++) {
            cb = new CommoditysBean();
            wareHouseTempList.add(wareHouseList.get(i));

            for (int j = 0; j < salesCartList.size(); j++) {
                if (wareHouseList.get(i).getWarehouseId() == salesCartList.get(j).getWarehouseId()) {
                    salesTempList.add(salesCartList.get(j));
                }
            }
        }
        cb.setWarehouses(wareHouseTempList);
        cb.setSalesCartItems(salesTempList);
        return salesTempList;

    }

    /**
     * 获取收货地址请求
     */
    public void getCustomerAddressList() {
        showLoading(5000);
        CsAddress.GetCustomerAddressListExRequest.Builder builder = CsAddress.GetCustomerAddressListExRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setPageNum(1);
        NetEngine.postRequest(builder, new INetEngineListener<CsAddress.GetCustomerAddressListExResponse>() {

            @Override
            public void onSuccess(CsAddress.GetCustomerAddressListExResponse response) {
                closeLoading();
                LogUtils.d("getCustomerAddress: " + response.toString());
                List<CsAddress.CustomerAddress> addressesList = response.getAddressesList();
                if (addressesList != null && addressesList.size() > 0) {
                    for (int i = 0; i < addressesList.size(); i++) {
                        CsAddress.CustomerAddress address = addressesList.get(i);
                        if (address.getIsDefault()) {
                            Message msg = Message.obtain();
                            msg.obj = address;
                            mHandler.sendMessage(msg);
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            packageAddressLayout.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            address = (CsAddress.CustomerAddress) msg.obj;
            if (address == null) {
                return;
            }
            String str = getResources().getString(R.string.String_cart_consignee_msg);
            str = String.format(str, address.getName(), address.getPhone());
            nameAndPhoneTv.setText(str);
            //AssetFileManager assetFileManager = new AssetFileManager(CrowdCartOrderActivity.this);
            ///String region = assetFileManager.readFile(address.getRegion());
            //String region = AssetFileManager.getInstance().readFilePlus(address.getRegion(), AssetFileManager.ADDRESS_TYPE);
            packageAddress.setText(address.getStreet() + "," + address.getFullRegionName());
            if (address.getIsDefault()) {
                defaultAddressIv.setVisibility(View.VISIBLE);
            } else {
                defaultAddressIv.setVisibility(View.GONE);
            }
        }
    };

    public void submitSalesOrder() {
        CsOrder.SubmitSalesOrderRequest.Builder builder = CsOrder.SubmitSalesOrderRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());

        if (address != null) {
            builder.setShippingAddressId(address.getAddressId());
        }
        builder.setUseGiftCard(isUseBalance ? true : false);
        builder.setPayMethod(payType);
        builder.setPurchaseScheme(purchaseScheme);
        builder.setShippingScheme(shippingScheme);
        if (shippingScheme == Constants.SHIPPING_SCHEME_DIRECT) {
            for (int i = 0; i < cb.getWarehouses().size(); i++) {

            }
            for (int i = 0; i < pairList.size(); i++) {
                CsBase.PairIntInt pair = pairList.get(i);
                builder.addPairs(pair);
            }
        }
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());

        NetEngine.postRequest(builder, new INetEngineListener<CsOrder.SubmitSalesOrderResponse>() {

                    @Override
                    public void onSuccess(CsOrder.SubmitSalesOrderResponse response) {
                        LogUtils.d(response.toString());
                        RedPointCountManager.getOrderCount();
                        SysApplication app = (SysApplication) CrowdCartOrderActivity.this.getApplication();
                        app.setOrderNumber(response.getOrderNumber());
                        app.setShippingScheme(shippingScheme);
                        app.setOrderType(0);
                        Intent intent = new Intent();
                        if (isUseBalance && balance >= grandTotal) {
                            app.setPaymentRequestCode(Constants.PAYMENT_REQUEST_CODE_SHOP_CART);
                            intent.setClass(CrowdCartOrderActivity.this, PaymentSuccessActivity.class);
                            startActivity(intent);
                        } else {
                            intent.setClass(CrowdCartOrderActivity.this, ConfirmPaymentActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("orderNumber", response.getOrderNumber());
                            bundle.putLong("orderId", response.getOrderId());
//                            bundle.putSerializable("commoditysBean", (Serializable) list);
                            bundle.putFloat("subTotal", cartResponse.getSubtotal());
                            bundle.putFloat("grandTotal", cartResponse.getGrandtotal());
                            bundle.putInt("payType", payType);
                            bundle.putString("payMethod", SPUtils.get(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_NAME, "").toString());
                            bundle.putInt("purchaseScheme", purchaseScheme);
                            bundle.putInt("shippingScheme", shippingScheme);
                            bundle.putFloat("balance", balance);
                            bundle.putSerializable("shippingList", (Serializable) shippingList);
                            bundle.putString("fee", fee);
                            bundle.putInt("commodityCount", qtyCount);
                            bundle.putBoolean("isUseBalance", isUseBalance);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, Constants.CONFIRM_REQUEST_CODE);
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(int ret, String errMsg) {
                        Looper.prepare();
                        LogUtils.d(errMsg);
                        Toast.makeText(CrowdCartOrderActivity.this, getResources().getString(R.string.cart_order_submit_order_failed) + errMsg + " ret=" + ret, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
        );
    }

    public void submitCrowdOrder() {
        CsOrder.SubmitCrowdOrderRequest.Builder builder = CsOrder.SubmitCrowdOrderRequest.newBuilder();
        if (address != null) {
            builder.setShippingAddressId(address.getAddressId());
        }
        builder.setUseGiftCard(isUseBalance ? true : false);
        builder.setPayMethod(payType);
        builder.setPurchaseScheme(purchaseScheme);
        builder.setShippingScheme(shippingScheme);
        if (shippingScheme == Constants.SHIPPING_SCHEME_DIRECT) {
            builder.addPairs(pairList.get(0));
        }
        CommoditysBean commoditysBean = list.get(1);
        List<CsBase.PairIntInt> extendAttrs = commoditysBean.getExtendAttrs();
        mCrowdSalesCartItem = commoditysBean.getSalesCartItems().get(0);
        builder.setItemId(mCrowdSalesCartItem.getItemId());
        builder.setQty(mCrowdSalesCartItem.getQty());
        builder.setNote(mCrowdSalesCartItem.getNote());
        builder.setCrowdId(commoditysBean.crowdId);

        for (CsBase.PairIntInt pairIntInt : extendAttrs) {
            builder.addAttrs(pairIntInt);

        }
        showLoading(5000);
        NetEngine.postRequest(builder, new INetEngineListener<CsOrder.SubmitCrowdOrderResponse>() {

            @Override
            public void onSuccess(CsOrder.SubmitCrowdOrderResponse response) {
                LogUtils.d(response.toString());
                RedPointCountManager.getOrderCount();
                SysApplication app = (SysApplication) CrowdCartOrderActivity.this.getApplication();
                app.setOrderNumber(response.getOrderNumber());
                app.setOrderType(1);
                app.setShippingScheme(shippingScheme);
                Intent intent = new Intent();
                if (isUseBalance && balance >= grandTotal) {
                    app.setPaymentRequestCode(Constants.PAYMENT_REQUEST_CODE_SHOP_CART);
                    intent.setClass(CrowdCartOrderActivity.this, PaymentSuccessActivity.class);
                    startActivity(intent);
                } else {
                    intent.setClass(CrowdCartOrderActivity.this, ConfirmPaymentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("orderNumber", response.getOrderNumber());
                    bundle.putLong("orderId", response.getOrderId());
//                    bundle.putSerializable("commoditysBean", (Serializable) list);
                    //                bundle.putSerializable("salesCartResponse", cartResponse);
                    bundle.putFloat("subTotal", mCrowdSalesCartItem.getQty() * mCrowdSalesCartItem.getUnitPrice());
                    bundle.putFloat("grandTotal", mCrowdSalesCartItem.getQty() * mCrowdSalesCartItem.getUnitPrice());
                    bundle.putInt("payType", payType);
                    bundle.putString("payMethod", paymentString);
                    bundle.putInt("purchaseScheme", purchaseScheme);
                    bundle.putInt("shippingScheme", shippingScheme);
                    bundle.putFloat("balance", balance);
                    bundle.putString("fee", fee);
                    bundle.putSerializable("shippingList", (Serializable) shippingList);
                    bundle.putInt("commodityCount", qtyCount);
                    bundle.putBoolean("isUseBalance", isUseBalance);
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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.delivery_layout:
                intent = new Intent(this, DeliveryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("itemList", (Serializable) itemList);
                bundle.putSerializable("commoditysBean", (Serializable) list);
                bundle.putSerializable("warehouseList", (Serializable) warehouseList);
                bundle.putInt("shippingScheme", shippingScheme);
                bundle.putSerializable("address", address);
                bundle.putSerializable("shipList", (Serializable) shippingList);
                intent.putExtras(bundle);
                intent.putExtra(DeliveryActivity.ORDER_TYPE, mIsCrowdOrder);
                startActivityForResult(intent, Constants.DELIVERY_REQUEST_CODE);
                break;
            case R.id.order_payment_layout:
                intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("payType", payType);
                int i = (int) SPUtils.get(this, AccountManager.getInstance().getCurrencyCode() + "paymentPos", 0);
                intent.putExtra("paymentPos", i);
                intent.putExtra(PaymentActivity.IS_SHOW_GIFTLAYOUT, true);
                intent.putExtra(PaymentActivity.CURRENCY_CODE, AccountManager.getInstance().getCurrencyCode());
                intent.putExtra(PaymentActivity.NEED_PAY, needPay);
                intent.putExtra("paymentString", paymentString);
                intent.putExtra("payCode", payCode);
                startActivityForResult(intent, Constants.PAYMENT_REQUEST_CODE);
                break;
            case R.id.order_submit_btn:
                if (payType == -1 && !isUseBalance) {
                    showPaymentDialog();
                } else {
                    if (!mIsCrowdOrder) {
                        submitSalesOrder();
                    } else {
                        submitCrowdOrder();
                    }
                }

                break;
            case R.id.order_package_address_layout:
                /*intent = new Intent(this, PackageAddressActivity.class);
                if (address != null) {
                    intent.putExtra("addressID", address.getAddressId());
                }
                startActivityForResult(intent, Constants.ADDRESS_REQUEST_CODE);*/
                intent = new Intent(this, AddressManagerActivity.class);
                intent.putExtra(AddressManagerActivity.KEY_IS_CHOOSE_TYPE, true);
                startActivityForResult(intent, Constants.ADDRESS_REQUEST_CODE);
                break;
            case R.id.reseve_iv:
                showDialog(getString(R.string.dialog_reseve_title), getString(R.string.dialog_reseve_msg));
                break;
            case R.id.cancel_iv:
                showDialog(getString(R.string.dialog_cancel_title), getString(R.string.dialog_cancel_msg));
                break;
            case R.id.iv_in_title_back:
                finish();
                break;
            case R.id.reseve_rb:
                reseveRB.setImageDrawable(ContextCompat.getDrawable(CrowdCartOrderActivity.this, R.mipmap.select_for_uid));
                cancelRB.setImageDrawable(ContextCompat.getDrawable(CrowdCartOrderActivity.this, R.mipmap.unselect_for_uid));
                purchaseScheme = Constants.PURCHASE_SCHEME_BOOK;
                break;
            case R.id.cancel_order_rb:
                reseveRB.setImageDrawable(ContextCompat.getDrawable(CrowdCartOrderActivity.this, R.mipmap.unselect_for_uid));
                cancelRB.setImageDrawable(ContextCompat.getDrawable(CrowdCartOrderActivity.this, R.mipmap.select_for_uid));
                purchaseScheme = Constants.PURCHASE_SCHEME_STOCK;
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            closeLoading();
        }
        return super.onKeyDown(keyCode, event);

    }

    public void showPaymentDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getString(R.string.app_tip));
        builder.setMessage(getString(R.string.card_order_payment_title_bar_title2));
        builder.setPositiveButton(getResources().getString(R.string.my_gift_card_order_binding_dialog_config), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CrowdCartOrderActivity.this, PaymentActivity.class);
                intent.putExtra("payType", payType);
                intent.putExtra("paymentPos", paymentPos);
                intent.putExtra(PaymentActivity.NEED_PAY, needPay);
                startActivityForResult(intent, Constants.PAYMENT_REQUEST_CODE);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cart_dialog_btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void showDialog(String title, String msg) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getResources().getString(R.string.cart_order_dialog_confirm), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "click");
                dialog.dismiss();
//                MeiqiaManager.getInstance(CrowdCartOrderActivity.this).contactCustomerService();
                startDDMActivity(ContractServiceActivity.class, true);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cart_dialog_btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == Constants.DELIVERY_REQUEST_CODE) {
                shippingScheme = data.getIntExtra("shippingScheme", 2);
                CsAddress.CustomerAddress address = (CsAddress.CustomerAddress) data.getExtras().getSerializable("callBackAddress");
                Message msg = Message.obtain();
                msg.obj = address;
                mHandler.sendMessage(msg);
                if (address != null) {
                    packageAddressLayout.setVisibility(View.VISIBLE);
                }
                if (shippingScheme == Constants.SHIPPING_SCHEME_MERGE) {
                    shippingLayout.setVisibility(View.GONE);
                    packageAddressLayout.setClickable(true);
                    directLayout.setVisibility(View.GONE);
                    megreOrderLayout.setVisibility(View.VISIBLE);
                    addressArrowIv.setVisibility(View.VISIBLE);
//                    packageAddressLayout.setVisibility(View.GONE);
                    needPay = grandTotal;
                    grandTotalTv.setText(UIUtils.getCurrency(this, grandTotal));
                    grandTotalMsgTv.setText(getString(R.string.String_submit_hint));
                    if (shippingList != null) {
                        shippingList.clear();
                    }
                } else if (shippingScheme == Constants.SHIPPING_SCHEME_DIRECT) {
                    pairList = (List<CsBase.PairIntInt>) data.getExtras().getSerializable("pairList");
                    crowdPairList = (List<CsBase.PairIntInt>) data.getExtras().getSerializable("crowdPairList");
                    shippingList = (List<CsShipping.Shipping>) data.getExtras().get("shippingList");
                    Map<Long, Double> feeMap = (Map<Long, Double>) data.getExtras().get("feeMap");
                    Double fee = (Double) data.getExtras().get("fee");
                    cb = (CommoditysBean) data.getExtras().get("cb");
                    dutyRate = (Float) data.getExtras().get("dutyRate");
//                    dutyMap = (Map<Integer, Float>) data.getExtras().getSerializable("dutyMap");
                    shippingLayout.setVisibility(View.VISIBLE);
                    packageAddressLayout.setClickable(false);
                    directLayout.setVisibility(View.VISIBLE);
                    megreOrderLayout.setVisibility(View.GONE);
                    addressArrowIv.setVisibility(View.GONE);
                    packageAddressLayout.setVisibility(View.VISIBLE);

                    String price = getResources().getString(R.string.String_order_price);
                    String grandMsg = getResources().getString(R.string.cart_order_grand_total_msg);
                    if (shippingList != null) {
                        shippingLayout.setVisibility(View.VISIBLE);
                        directPriceTv.setVisibility(View.VISIBLE);
                        getShippingFee(shippingList, cb);
                    } else {
                        shippingLayout.setVisibility(View.GONE);
                        directPriceTv.setVisibility(View.GONE);
                        needPay = grandTotal;
                        grandTotalTv.setText(UIUtils.getCurrency(this, grandTotal));
//                        grandMsg = String.format(grandMsg, UIUtils.getCurrency(this) ,0.00f);
                        grandMsg = UIUtils.getCurrency(this, 0.00f);
                        grandTotalMsgTv.setText(grandMsg);
                    }
                }
            }
            if (requestCode == Constants.PAYMENT_REQUEST_CODE) {
                payType = data.getIntExtra("payType", Constants.PAYMENT_ADYEN);
                KLog.i("pay_type = " + payType);
                paymentPos = data.getIntExtra("paymentPos", paymentPos);
                shared.edit().putInt("payType", payType).commit();
                balance = data.getFloatExtra("account", 0.00f);
                isUseBalance = data.getBooleanExtra(PaymentActivity.IS_USE_BALANCE, false);
                shared.edit().putBoolean("isUseBalance", isUseBalance).commit();
                shared.edit().putFloat("balance", balance).commit();

                if (isUseBalance) {
                    paymentTypeBalanceTv.setVisibility(View.VISIBLE);
                    String str = getString(R.string.package_split_balance_first, UIUtils.getCurrency(this, balance));
//                    str = String.format(str, balance);
                    paymentTypeBalanceTv.setText(str);
                } else {
                    paymentTypeBalanceTv.setVisibility(View.GONE);
                }
                paymentString = data.getStringExtra("paymentString");
                if (!TextUtils.isEmpty(paymentString)) {
                    payTypeTv.setVisibility(View.VISIBLE);
                    if (TextUtils.equals(paymentString, Constants.GIFT_CARD_PAYMENT_KRBANK)) {
                        payTypeTv.setText(getString(R.string.String_krbank_pay2));
                    } else {
                        payTypeTv.setText(paymentString);
                    }
                }
//                if (payType == Constants.PAYMENT_ALIPAY) {
//
//                }
//                if (payType == Constants.PAYMENT_WECHAT) {
//                    payTypeTv.setText(getResources().getString(R.string.String_wechat_pay));
//                }
            }

            if (requestCode == Constants.ADDRESS_REQUEST_CODE) {
                if (data != null) {
                    Message msg = Message.obtain();
                    msg.obj = data.getExtras().getSerializable("address");
                    mHandler.sendMessage(msg);
                }
            }
            if (requestCode == Constants.CONFIRM_REQUEST_CODE) {
                if (data != null) {
                    boolean isCancelPay = data.getBooleanExtra("isCancelPay", false);
                    if (isCancelPay) {
                        finish();
                    }
                }
            }
        }
    }

    public void getShippingFee(List<CsShipping.Shipping> shippingList, CommoditysBean cb) {
        Set<CsBase.Warehouse> whSet = cb.getWarehouseSet();
        List<CsCart.SalesCartItem> mCIList = cb.getSalesCartItems();
        Iterator<CsBase.Warehouse> iter = whSet.iterator();
        String price = getResources().getString(R.string.String_order_price);
        String grandMsg = getResources().getString(R.string.cart_order_grand_total_msg);
        float grandFee = 0.00f;
        shippingContentLayout.removeAllViews();
        String wareHouseName = "";
        String title = "";
        float duty = 0;
        for (int i = 0; i < shippingList.size(); i++) {
            for (int j = 0; j < cartResponse.getItemsList().size(); j++) {
                if (cartResponse.getItems(j).getWarehouseId() == pairList.get(i).getK()
                        && pairList.get(i).getV() == shippingList.get(i).getShippingId()) {
                    if (shippingList.get(i).getIsNeedDuty()) {
                        duty += cartResponse.getItems(j).getUnitPrice() * dutyRate / 100;
                    }
                }
            }
            CsShipping.Shipping shipping = shippingList.get(i);
            grandFee += shipping.getFee();
            if (!mIsCrowdOrder) {
                wareHouseName = getWareHouseName(shipping.getShippingId(), pairList);
            } else {
                wareHouseName = list.get(0).getWarehouses().get(0).getName();
            }
            title = shipping.getTitle();
            LogUtils.d("------title " + title);
            LinearLayout shippingChildLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.order_shipping_content_child_layout, null);
            TextView orderShippingTitleTv = (TextView) shippingChildLayout.findViewById(R.id.order_shipping_title_tv);
            TextView orderShippingInfoTv = (TextView) shippingChildLayout.findViewById(R.id.order_shipping_info_tv);
            TextView orderShippingFeeTv = (TextView) shippingChildLayout.findViewById(R.id.order_shipping_fee_tv);
            orderShippingTitleTv.setText(wareHouseName);
            orderShippingInfoTv.setText(title);
            String mFee = getString(R.string.String_order_price, UIUtils.getCurrency(CrowdCartOrderActivity.this, (float) shipping.getFee()));
            orderShippingFeeTv.setText(mFee);
            shippingContentLayout.addView(shippingChildLayout);
        }

//        grandMsg = String.format(grandMsg, grandFee);
        grandMsg = getString(R.string.cart_order_grand_total_msg, UIUtils.getCurrency(this, grandFee + duty));
        grandTotalMsgTv.setText(grandMsg);
//                        price = String.format(price, grandTotal + grandFee);
//        String feeTotal = String.format(price, grandTotal + grandFee);
        DecimalFormat df = new DecimalFormat("#.00");
        needPay = grandTotal + grandFee + duty;
        grandTotalTv.setText(UIUtils.getCurrency(this, needPay));
        fee = getString(R.string.String_order_price, UIUtils.getCurrency(this, grandFee + duty));
        directPriceTv.setText(getString(R.string.String_order_price, UIUtils.getCurrency(this, grandFee)));

    }

    public String getWareHouseName(int shippingId, List<CsBase.PairIntInt> pairList) {
        String wareHouseName = "";
        List<CsBase.Warehouse> whList = cartResponse.getWarehousesList();
        for (int i = 0; i < pairList.size(); i++) {
            if (pairList.get(i).getV() == shippingId) {
                for (int j = 0; j < whList.size(); j++) {
                    if (whList.get(j).getWarehouseId() == pairList.get(i).getK()) {
                        wareHouseName = whList.get(j).getName();

                    }
                }
            }

        }
        return wareHouseName;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null)
            return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    public List<CsCart.SalesCartItem> getListData() {
//        if (AttendCrowdActivity.cartItems == null || AttendCrowdActivity.cartItems.size() == 0)
//            return null;
//        list = AttendCrowdActivity.cartItems;


        warehouseList = list.get(0).getWarehouses();
        List<CsCart.SalesCartItem> salesCartList = list.get(1).getSalesCartItems();
        itemList = salesCartList;
        List<CsCart.SalesCartItem> salesTempList = new ArrayList<CsCart.SalesCartItem>();
        List<CsBase.Warehouse> wareHouseTempList = new ArrayList<CsBase.Warehouse>();

        for (int i = 0; i < warehouseList.size(); i++) {
            cb = new CommoditysBean();
            wareHouseTempList.add(warehouseList.get(i));

            for (int j = 0; j < salesCartList.size(); j++) {
                if (warehouseList.get(i).getWarehouseId() == salesCartList.get(j).getWarehouseId()) {
                    Log.d(TAG, "Title: " + salesCartList.get(j).getTitle());
                    salesTempList.add(salesCartList.get(j));
                    //                    whBean = new WareHouseBean();
                    //                    whBean.setWarehouseID(wareHouseList.get(i).getWarehouseId());
                    //                    whBean.setName(wareHouseList.get(i).getName());
                    //                    whBean.setDesc(wareHouseList.get(i).getDesc());

                }
            }
        }
        cb.setWarehouses(wareHouseTempList);
        cb.setSalesCartItems(salesTempList);
        return salesTempList;

    }

    public void getFKDPaymentListRequest() {
        CsUser.GetFKDPaymentListRequest.Builder builder = CsUser.GetFKDPaymentListRequest.newBuilder();
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetFKDPaymentListResponse>() {

            @Override
            public void onSuccess(CsUser.GetFKDPaymentListResponse response) {
                KLog.i(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                paymentHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
            }
        });
    }

    public void getFKDPaymentListRequest2() {
        CsUser.GetFKDPaymentListRequest.Builder builder = CsUser.GetFKDPaymentListRequest.newBuilder();
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetFKDPaymentListResponse>() {

            @Override
            public void onSuccess(CsUser.GetFKDPaymentListResponse response) {
                KLog.i(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                paymentHandler2.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
            }
        });
    }

    private Handler paymentHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CsUser.GetFKDPaymentListResponse response = (CsUser.GetFKDPaymentListResponse) msg.obj;
            List<CsUser.PaymentList> list = response.getPaymantlistList();
            String payString = SPUtils.get(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_NAME, "").toString();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPayname().equals(payString)) {
                    payType = (int) SPUtils.get(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode(), -1);
                    payTypeTv.setText(SPUtils.get(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_NAME, "").toString());
                    payCode = SPUtils.get(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_CODE, "").toString();
                    return;
                } else {
                    if (list.get(i).getSortorder().equals("1")) {
                        paymentPos = i;
                        payCode = list.get(i).getPaycode();
                        payTypeTv.setText(list.get(i).getPayname());
                        paymentString = list.get(i).getPayname();
                        if (TextUtils.equals(list.get(i).getPaycode(), Constants.GIFT_CARD_PAYMENT_KRBANK)) {
                            payType = Constants.PAYMENT_KRBANK;
                        }
                        if (TextUtils.equals(list.get(i).getPaycode(), Constants.GIFT_CARD_PAYMENT_ADYEN)) {
                            payType = Constants.PAYMENT_ADYEN;

                        }
                        if (TextUtils.equals(list.get(i).getPaycode(), Constants.GIFT_CARD_PAYMENT_ALIPAY)) {
                            payType = Constants.PAYMENT_ALIPAY;

                        }
                        if (TextUtils.equals(list.get(i).getPaycode(), Constants.GIFT_CARD_PAYMENT_WXCHAT)) {
                            payType = Constants.PAYMENT_WECHAT;

                        }
                        if (TextUtils.equals(list.get(i).getPaycode(), Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
                            payType = Constants.PAYMENT_DAOUPAY;

                        }
                        SPUtils.put(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_NAME, paymentString);
                        SPUtils.put(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_CODE, payCode);
                        SPUtils.put(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode(), payType);

                    }
                }

            }
        }
    };

    private Handler paymentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CsUser.GetFKDPaymentListResponse response = (CsUser.GetFKDPaymentListResponse) msg.obj;
            List<CsUser.PaymentList> list = response.getPaymantlistList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getSortorder().equals("1")) {
                    paymentPos = i;
                    payCode = list.get(i).getPaycode();
                    payTypeTv.setText(list.get(i).getPayname());
                    paymentString = list.get(i).getPayname();
                    if (TextUtils.equals(list.get(i).getPaycode(), Constants.GIFT_CARD_PAYMENT_KRBANK)) {
                        payType = Constants.PAYMENT_KRBANK;
                    }
                    if (TextUtils.equals(list.get(i).getPaycode(), Constants.GIFT_CARD_PAYMENT_ADYEN)) {
                        payType = Constants.PAYMENT_ADYEN;

                    }
                    if (TextUtils.equals(list.get(i).getPaycode(), Constants.GIFT_CARD_PAYMENT_ALIPAY)) {
                        payType = Constants.PAYMENT_ALIPAY;

                    }
                    if (TextUtils.equals(list.get(i).getPaycode(), Constants.GIFT_CARD_PAYMENT_WXCHAT)) {
                        payType = Constants.PAYMENT_WECHAT;

                    }
                    if (TextUtils.equals(list.get(i).getPaycode(), Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
                        payType = Constants.PAYMENT_DAOUPAY;

                    }
                    SPUtils.put(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_NAME, paymentString);
                    SPUtils.put(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_CODE, payCode);
                    SPUtils.put(CrowdCartOrderActivity.this, AccountManager.getInstance().getCurrencyCode(), payType);

                }
            }
        }
    };

    public void giftCardBalanceList() {
        showLoading(5000);
        CsCard.GiftCardBalanceListRequest.Builder builder = CsCard.GiftCardBalanceListRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setPage(1);
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GiftCardBalanceListReponse>() {

            @Override
            public void onSuccess(CsCard.GiftCardBalanceListReponse response) {
                closeLoading();
                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                balanceHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }

    private Handler balanceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CsCard.GiftCardBalanceListReponse response = (CsCard.GiftCardBalanceListReponse) msg.obj;
            if (response.getGiftcardaccount() > 0) {
                isUseBalance = true;
                balance = response.getGiftcardaccount();
                String str = getString(R.string.package_split_balance_first, UIUtils.getCurrency(CrowdCartOrderActivity.this, response.getGiftcardaccount()));
//                    str = String.format(str, balance);
                paymentTypeBalanceTv.setText(str);
                paymentTypeBalanceTv.setVisibility(View.VISIBLE);
                payTypeTv.setVisibility(View.GONE);
            } else {
                paymentTypeBalanceTv.setVisibility(View.GONE);
                payTypeTv.setVisibility(View.VISIBLE);
            }
        }
    };


}
