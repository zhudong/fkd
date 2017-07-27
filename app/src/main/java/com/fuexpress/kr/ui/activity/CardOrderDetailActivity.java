package com.fuexpress.kr.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.PayResultBaen;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ActivityController;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.adapter.CardOrderDetailAdapter;
import com.fuexpress.kr.ui.adapter.ParcelAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import fksproto.CsCard;
import fksproto.CsParcel;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/5/11.
 */
public class CardOrderDetailActivity extends BaseActivity {
    public static final String CURRENCY_CODE = "currencyCode";
    public static final String CURRENCY_SGIN = "currency_sgin";
    public static final String IS_ORDER_DETAILS = "is_order_details";

    private View rootView;
    private TextView orderNumberTv;
    private TextView orderDateTv;
    private TextView orderGrandTotalTv;
    private TextView orderStateTv;
    private TextView orderTypeTv;
    private TextView typeTv;
    private TextView parcelNameTv;
    private TextView freightTv;
    private TextView balancePayTv;
    private TextView needPayTv;
    private TextView freightTitleTv;

    private Button orderCancelBtn;
    private TextView paymentTv;
    private LinearLayout paymentLayout;
    private LinearLayout payLayout;
    private LinearLayout rechargeOrderLayout;
    private TextView payGrandTotalTv;
    private Button payBtn;
    private ListView cardListView;
    private ImageView backImgView;
    private TextView backTv;
    private ScrollView contentSv;
    private TitleBarView titleBar;
    private ImageView arrowIv;
    private int giftCardOrderId;
    private String cardOrderNo;
    private int giftCardOrderType;
    private int giftCardOrderState;
    private int orderType;
    //    private long parcelId;
    private int paymentPos;
    private float orderAmount;

    private String payment;
    private String paymentString;
    private String currencyCode;
    private String currencySgin;
    private String paymentAmount;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_card_order_detail, null);
        titleBar = (TitleBarView) rootView.findViewById(R.id.card_order_detail_titlebar);

        backImgView = titleBar.getIv_in_title_back();
        backTv = titleBar.getmTv_in_title_back_tv();
        typeTv = (TextView) rootView.findViewById(R.id.recharge_order_detail_type_tv);
        parcelNameTv = (TextView) rootView.findViewById(R.id.recharge_order_detail_parcel_name_tv);
        freightTv = (TextView) rootView.findViewById(R.id.recharge_order_detail_freight_tv);
        balancePayTv = (TextView) rootView.findViewById(R.id.recharge_order_detail_balance_pay_tv);
        needPayTv = (TextView) rootView.findViewById(R.id.recharge_order_detail_need_pay_tv);
        orderNumberTv = (TextView) rootView.findViewById(R.id.card_order_detail_order_number_tv);
        orderDateTv = (TextView) rootView.findViewById(R.id.card_order_detail_date_tv);
        orderGrandTotalTv = (TextView) rootView.findViewById(R.id.card_order_detail_grand_total_tv);
        orderStateTv = (TextView) rootView.findViewById(R.id.card_order_detail_state_tv);
        orderTypeTv = (TextView) rootView.findViewById(R.id.card_order_detail_type_tv);
        orderCancelBtn = (Button) rootView.findViewById(R.id.card_order_detail_cancel_btn);
        paymentTv = (TextView) rootView.findViewById(R.id.card_order_detail_payment_tv);
        freightTitleTv = (TextView) rootView.findViewById(R.id.recharge_order_detail_freight_title_tv);
        paymentLayout = (LinearLayout) rootView.findViewById(R.id.card_order_detail_payment_layout);
        payGrandTotalTv = (TextView) rootView.findViewById(R.id.card_order_detail_pay_grand_total_tv);
        arrowIv = (ImageView) rootView.findViewById(R.id.card_order_detail_arrow_iv);
        payLayout = (LinearLayout) rootView.findViewById(R.id.card_order_detail_pay_layout);
        rechargeOrderLayout = (LinearLayout) rootView.findViewById(R.id.recharge_order_detail_layout);
        payBtn = (Button) rootView.findViewById(R.id.card_order_detail_pay_btn);
        contentSv = (ScrollView) rootView.findViewById(R.id.card_order_detail_sv);

        cardListView = (ListView) rootView.findViewById(R.id.card_order_detail_list_view);
        ActivityController.addActivity(this);
        Intent intent = getIntent();
        giftCardOrderType = intent.getIntExtra(Constants.GIFT_CARD_ORDER_TYPE, -1);
        giftCardOrderState = intent.getIntExtra(Constants.GIFT_CARD_ORDER_STATE, -1);
        giftCardOrderId = intent.getIntExtra(Constants.GIFT_CARD_ORDER_ID, -1);
        currencyCode = intent.getStringExtra(CURRENCY_CODE);
        currencySgin = intent.getStringExtra(CURRENCY_SGIN);
        LogUtils.d("giftCardOrderType: " + giftCardOrderType + " giftCardOrderState: " + giftCardOrderState);
        if (giftCardOrderType != -1) {
            initData(giftCardOrderType, giftCardOrderState, giftCardOrderId);
        }
        paymentLayout.setOnClickListener(this);
        backImgView.setOnClickListener(this);
        payBtn.setOnClickListener(this);
        orderCancelBtn.setOnClickListener(this);
        backTv.setOnClickListener(this);
        return rootView;
    }

    public void initData(int giftCardOrderType, int giftCardOrderState, int giftCardOrderId) {
        switch (giftCardOrderState) {
            case Constants.GIFT_CARD_ORDER_STATE_PENDING:
                titleBar.setTitleText(getString(R.string.card_order_detail_title_bar_wait_pay));
                orderCancelBtn.setVisibility(View.VISIBLE);
                arrowIv.setVisibility(View.VISIBLE);
                payLayout.setVisibility(View.VISIBLE);
                paymentLayout.setEnabled(true);

                break;
            case Constants.GIFT_CARD_ORDER_STATE_PAID:
                titleBar.setTitleText(getString(R.string.card_order_detail_title_bar_payed));
                orderCancelBtn.setVisibility(View.GONE);
                arrowIv.setVisibility(View.GONE);
                payLayout.setVisibility(View.GONE);
                paymentLayout.setEnabled(false);
                break;
            case Constants.GIFT_CARD_ORDER_STATE_CANCELED:
                titleBar.setTitleText(getString(R.string.card_order_detail_title_bar_canceled));
                orderCancelBtn.setVisibility(View.GONE);
                arrowIv.setVisibility(View.GONE);
                payLayout.setVisibility(View.GONE);
                paymentLayout.setEnabled(false);
                break;
        }

        switch (giftCardOrderType) {
            case Constants.GIFT_CARD_ORDER_TYPE_RECHARGE:
                if (giftCardOrderState != -1) {
                    getGiftCardOrderDetail(giftCardOrderId);
//                    rechargeOrderLayout.setVisibility(View.GONE);
//                    cardListView.setVisibility(View.VISIBLE);
                }
                break;
            case Constants.GIFT_CARD_ORDER_TYPE_PREPAID:
                if (giftCardOrderState != -1) {
                    getRechargeOrderDetail(giftCardOrderId);
//                    rechargeOrderLayout.setVisibility(View.VISIBLE);
//                    cardListView.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.card_order_detail_payment_layout:
                if (giftCardOrderState == Constants.GIFT_CARD_ORDER_STATE_PENDING) {
                    Intent intent = new Intent(this, PaymentActivity.class);
                    intent.putExtra("giftCardOrderId", giftCardOrderId);
                    intent.putExtra("payCode", payment);
                    intent.putExtra("paymentString", paymentString);
                    intent.putExtra("paymentPos", paymentPos);
                    intent.putExtra(CURRENCY_CODE, currencyCode);
                    intent.putExtra(IS_ORDER_DETAILS, true);
                    startActivityForResult(intent, Constants.CARD_ORDER_PAYMENT_REQUEST_CODE);
                }
                break;
            case R.id.card_order_detail_pay_btn:
                switch (giftCardOrderType) {
                    case Constants.GIFT_CARD_ORDER_TYPE_RECHARGE:
                        payGiftCardOrder(cardOrderNo, giftCardOrderId, payment, orderAmount);
                        break;
                    case Constants.GIFT_CARD_ORDER_TYPE_PREPAID:
                        showDialog(giftCardOrderId, payment);
                        break;
                }
                break;
            case R.id.card_order_detail_cancel_btn:
                cancelGiftCardOrder(giftCardOrderId);
                break;
        }
    }

    /**
     * 支付的提示对话框
     *
     * @param giftCardOrderId
     * @param payment
     */
    public void showDialog(final int giftCardOrderId, final String payment) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        switch (orderType) {
            case 2:
                builder.setMessage(getResources().getString(R.string.card_order_detail_dialog_msg));
                break;
            case 3:
                builder.setMessage(getResources().getString(R.string.card_order_detail_dialog_split_msg));
                break;
        }
        builder.setTitle(getString(R.string.cart_dialog_title));
        builder.setNegativeButton(getString(R.string.cart_dialog_btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.cart_dialog_btn_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                payGiftCardOrder(cardOrderNo, giftCardOrderId, payment, orderAmount);
            }
        });
        CustomDialog customDialog = builder.create();
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.PAYMENT_REQUEST_CODE) {
            if (data != null) {
                payment = data.getStringExtra("payment");
                int payType = data.getIntExtra("payType", 1);
                paymentPos = data.getIntExtra("paymentPos", paymentPos);
                if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_ADYEN)) {
                    paymentString = getString(R.string.String_adyen_pay);
                    paymentTv.setText(paymentString);
//                        paymentTv.setText(getString(R.string.String_ali_pay));
                }
                if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_KRBANK)) {
                    paymentString = getString(R.string.String_krbank_pay);
                    paymentTv.setText(paymentString);
//                        paymentTv.setText(getString(R.string.String_wechat_pay));
                }
                if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_WXPAY)) {
                    paymentString = getString(R.string.String_wechat_pay);
                    paymentTv.setText(paymentString);
//                        paymentTv.setText(getString(R.string.String_wechat_pay));
                }
                if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_ALIPAY)) {
                    paymentString = getString(R.string.String_ali_pay);
                    paymentTv.setText(paymentString);
//                        paymentTv.setText(getString(R.string.String_wechat_pay));
                }
                if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
                    paymentString = getString(R.string.dao_u_pay);
                    paymentTv.setText(paymentString);
//                        paymentTv.setText(getString(R.string.String_wechat_pay));
                }
            }
        }
    }

    /**
     * 获取直充订单详情
     *
     * @param giftCardOrderId
     */
    public void getRechargeOrderDetail(int giftCardOrderId) {
        showLoading(5000);
        CsCard.GetRechargeOrderDetailRequest.Builder builder = CsCard.GetRechargeOrderDetailRequest.newBuilder();
        builder.setGiftCardOrderId(giftCardOrderId);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GetRechargeOrderDetialResponse>() {

            @Override
            public void onSuccess(CsCard.GetRechargeOrderDetialResponse response) {
                closeLoading();
                LogUtils.d(response.toString());
                orderType = response.getGiftCardOrder().getOrderType();
//                parcelId = response.getGiftCardOrder().getParcelId();
                Message msg = Message.obtain();
                msg.obj = response;
                msg.arg1 = Constants.GIFT_CARD_ORDER_TYPE_PREPAID;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }

    /**
     * 获取充值卡订单详情
     *
     * @param giftCardOrderId
     */
    public void getGiftCardOrderDetail(int giftCardOrderId) {
        showLoading(5000);
        CsCard.GetGiftCardOrderDetailRequest.Builder builder = CsCard.GetGiftCardOrderDetailRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setGiftCardOrderId(giftCardOrderId);
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GetGiftCardOrderDetialResponse>() {

            @Override
            public void onSuccess(CsCard.GetGiftCardOrderDetialResponse response) {
                closeLoading();
                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                msg.arg1 = Constants.GIFT_CARD_ORDER_TYPE_RECHARGE;
                mHandler.sendMessage(msg);
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
            contentSv.scrollTo(0, 0);
            String str = getResources().getString(R.string.cart_order_grand_total);
            switch (msg.arg1) {
                case Constants.GIFT_CARD_ORDER_TYPE_RECHARGE:
                    CsCard.GetGiftCardOrderDetialResponse response = (CsCard.GetGiftCardOrderDetialResponse) msg.obj;
                    CsCard.GiftCardOrderItem item = response.getGiftCardOrderBase();
                    cardOrderNo = item.getGiftCardOrderNo();
                    orderAmount = item.getTotal();
                    orderNumberTv.setText(cardOrderNo);
                    orderDateTv.setText(item.getGiftCardCreateTime());
                    orderGrandTotalTv.setText(UIUtils.getCurrency(CardOrderDetailActivity.this, currencyCode, item.getTotal()));
                    orderStateTv.setText(item.getStatusMsg());
                    orderTypeTv.setText(item.getOrderType() == 1 ? getString(R.string.cart_order_card_type_recharge) : getString(R.string.cart_order_card_type_prepaid));
                    backTv.setText(item.getOrderType() == 1 ? getString(R.string.cart_order_card_type_recharge) : getString(R.string.cart_order_card_type_prepaid));
                    payment = item.getPaymentCode();
                    paymentString = item.getPaymentName();
                    paymentTv.setText(paymentString);

//                    if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_ADYEN)) {
//                        paymentString = getString(R.string.String_adyen_pay);
//                        paymentTv.setText(paymentString);
////                        paymentTv.setText(getString(R.string.String_ali_pay));
//                    }
//                    if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_KRBANK)) {
//                        paymentString = getString(R.string.String_krbank_pay);
//                        paymentTv.setText(paymentString);
////                        paymentTv.setText(getString(R.string.String_wechat_pay));
//                    }
//                    if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_WXPAY)) {
//                        paymentString = getString(R.string.String_wechat_pay);
//                        paymentTv.setText(paymentString);
////                        paymentTv.setText(getString(R.string.String_wechat_pay));
//                    }
//                    if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_ALIPAY)) {
//                        paymentString = getString(R.string.String_ali_pay);
//                                paymentTv.setText(paymentString);
////                        paymentTv.setText(getString(R.string.String_wechat_pay));
//                    }
//                    if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
//                        paymentString = getString(R.string.dao_u_pay);
//                        paymentTv.setText(paymentString);
////                        paymentTv.setText(getString(R.string.String_wechat_pay));
//                    }
//                     = getResources().getString(R.string.String_order_price);
                    paymentAmount = UIUtils.getCurrency(CardOrderDetailActivity.this, currencyCode, item.getTotal());
                    payGrandTotalTv.setText(paymentAmount);
                    if (item.getOrderType() == 4) {
                        cardListView.setVisibility(View.GONE);
                    }else {
                        CardOrderDetailAdapter adapter = new CardOrderDetailAdapter(CardOrderDetailActivity.this, response.getGiftCardItemList(), currencyCode);
                        cardListView.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(cardListView);
                        cardListView.setVisibility(View.VISIBLE);
                    }
                    break;
                case Constants.GIFT_CARD_ORDER_TYPE_PREPAID:
                    CsCard.GetRechargeOrderDetialResponse rechargeResponse = (CsCard.GetRechargeOrderDetialResponse) msg.obj;
                    CsCard.GiftCardOrderItem cardItem = rechargeResponse.getGiftCardOrder();
                    cardOrderNo = cardItem.getGiftCardOrderNo();
                    orderNumberTv.setText(cardOrderNo);
                    orderDateTv.setText(cardItem.getGiftCardCreateTime());
                    orderGrandTotalTv.setText(UIUtils.getCurrency(CardOrderDetailActivity.this, currencyCode, cardItem.getTotal()));
                    orderStateTv.setText(cardItem.getStatusMsg());
                    orderTypeTv.setText(cardItem.getOrderType() == 1 ? getString(R.string.cart_order_card_type_recharge) : getString(R.string.cart_order_card_type_prepaid));
                    backTv.setText(cardItem.getOrderType() == 1 ? getString(R.string.cart_order_card_type_recharge) : getString(R.string.cart_order_card_type_prepaid));
                    payment = cardItem.getPaymentCode();
                    paymentString = cardItem.getPaymentName();
                    paymentTv.setText(paymentString);
//                    if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_ADYEN)) {
//                        paymentString = getString(R.string.String_adyen_pay);
//                        paymentTv.setText(paymentString);
////                        paymentTv.setText(getString(R.string.String_ali_pay));
//                    }
//                    if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_KRBANK)) {
//                        paymentString = getString(R.string.String_krbank_pay);
//                        paymentTv.setText(paymentString);
////                        paymentTv.setText(getString(R.string.String_wechat_pay));
//                    }
//                    if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_WXPAY)) {
//                        paymentString = getString(R.string.String_wechat_pay);
//                        paymentTv.setText(paymentString);
////                        paymentTv.setText(getString(R.string.String_wechat_pay));
//                    }
//                    if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_ALIPAY)) {
//                        paymentString = getString(R.string.String_ali_pay);
//                        paymentTv.setText(paymentString);
////                        paymentTv.setText(getString(R.string.String_wechat_pay));
//                    }
//                    if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
//                        paymentString = getString(R.string.dao_u_pay);
//                        paymentTv.setText(paymentString);
////                        paymentTv.setText(getString(R.string.String_wechat_pay));
//                    }
                    String Amount = getResources().getString(R.string.String_order_price);
//                    paymentAmount = UIUtils.getCurrency(CardOrderDetailActivity.this, currencyCode) + cardItem.getTotal();
//                    paymentAmount = getString(R.string.recharge_order_detail_price, cardItem.getTotal());
                    payGrandTotalTv.setText(UIUtils.getCurrency(CardOrderDetailActivity.this, currencyCode, cardItem.getTotal()));
//                    switch (cardItem.getOrderType()) {
//                        case 2:
//                            typeTv.setText(getString(R.string.cart_order_card_msg_title_type_submit));
//                            freightTitleTv.setText(getString(R.string.cart_order_card_msg_freight_estimate));
//                            parcelNameTv.setText(cardItem.getParcelName());
//                            String estimateFee = getResources().getString(R.string.recharge_order_detail_price);
//                            estimateFee = getString(R.string.recharge_order_detail_price, cardItem.getEstimateShippingFee());
//                            freightTv.setText(estimateFee);
//                            break;
//                        case 3:
//                            typeTv.setText(getString(R.string.cart_order_card_msg_title_type_split));
//                            freightTitleTv.setText(getString(R.string.cart_order_card_msg_freight_split));
//                            parcelNameTv.setText(rechargeResponse.getP..getgetParcelName());
//                            String splitFee = getResources().getString(R.string.recharge_order_detail_price);
//                            splitFee = getString(R.string.recharge_order_detail_price, cardItem.getSplitFee());
//                            freightTv.setText(splitFee);
//                            break;
//                    }
                    if (cardItem.getOrderType() == 4) {
                        cardListView.setVisibility(View.GONE);
                    }else {
                        ParcelAdapter parcelAdapter = new ParcelAdapter(CardOrderDetailActivity.this, rechargeResponse.getParcellistdataList(), currencyCode);
                        cardListView.setAdapter(parcelAdapter);
                        setListViewHeightBasedOnChildren(cardListView);
                        cardListView.setVisibility(View.VISIBLE);
                    }

                    String AccountPay = getResources().getString(R.string.recharge_order_detail_price);
                    AccountPay = getString(R.string.recharge_order_detail_price, cardItem.getAccountPay());
                    balancePayTv.setText(UIUtils.getCurrency(CardOrderDetailActivity.this, currencyCode, cardItem.getAccountPay()));

                    String needPay = getResources().getString(R.string.recharge_order_detail_price);
                    needPay = getString(R.string.recharge_order_detail_price, cardItem.getNeedPay());
                    needPayTv.setText(UIUtils.getCurrency(CardOrderDetailActivity.this, currencyCode, cardItem.getNeedPay()));
                    break;
            }

        }
    };

    /**
     * 设置ListView的行高
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
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
        params.setMargins(0, 0, 0, 0);
        listView.setLayoutParams(params);
    }

    /**
     * 取消充值卡订单
     *
     * @param giftCardOrderId
     */
    public void cancelGiftCardOrder(int giftCardOrderId) {
        showLoading(5000);
        CsCard.CancelGiftCardOrderRequest.Builder builder = CsCard.CancelGiftCardOrderRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setGiftCardOrderId(giftCardOrderId).setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.CancelGiftCardOrderResponse>() {

            @Override
            public void onSuccess(CsCard.CancelGiftCardOrderResponse response) {
                closeLoading();
                Looper.prepare();
                Toast.makeText(CardOrderDetailActivity.this, getString(R.string.toast_msg_canceled), Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        orderCancelBtn.setVisibility(View.GONE);
                        payLayout.setVisibility(View.GONE);
                        orderStateTv.setText(getString(R.string.card_order_detail_title_bar_canceled));
                        titleBar.setTitleText(getString(R.string.card_order_detail_title_bar_canceled));
                    }
                });
                Looper.loop();
                LogUtils.d(response.toString());
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
                Looper.prepare();
                Toast.makeText(CardOrderDetailActivity.this, getString(R.string.toast_msg_cancel_failed), Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }


    public void payGiftCardOrder(final String cardOrderNo, final int giftCardOrderId, final String payment, final float orderAmount) {
        showLoading(5000);
        CsParcel.PayGiftCardOrderRequest.Builder builder = CsParcel.PayGiftCardOrderRequest.newBuilder();
        builder.setGiftCardOrderId(giftCardOrderId);
        if (payment != null) {
            builder.setPaymentCode(payment);
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayGiftCardOrderResponse>() {

            @Override
            public void onSuccess(CsParcel.PayGiftCardOrderResponse response) {
                LogUtils.d(response.toString());

                PaymentManager paymentManager = PaymentManager.getInstance(CardOrderDetailActivity.this);
                SysApplication app = (SysApplication) getApplication();
                switch (giftCardOrderType) {
                    case Constants.GIFT_CARD_ORDER_TYPE_RECHARGE:
                        app.setPaymentRequestCode(Constants.PAYMENT_REQUEST_CODE_CARD_CART);
                        app.setGiftCardOrderNo(cardOrderNo);
                        break;
                    case Constants.GIFT_CARD_ORDER_TYPE_PREPAID:
                        app.setPaymentRequestCode(Constants.PAYMENT_REQUEST_CODE_PACKAGE_DETAIL);
//                        app.setParcelId(parcelId);
                        break;
                }

                if (TextUtils.equals(payment, "alipay")) {
                    paymentManager.aliPay(response.getPayInfo(), new PaymentManager.OnAliPayListener() {
                        @Override
                        public void onSuccess(String resultStatus) {
                            Intent intent = new Intent();
                            intent.setClass(CardOrderDetailActivity.this, CardPaymentSuccessActivity.class);
                            intent.putExtra("orderNumber", cardOrderNo);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(String resultStatus, String errorMsg) {

                        }
                    });
                }
                if (TextUtils.equals(payment, "wxap")) {
                    paymentManager.wechatPay(response.getPayInfo());
                }

                if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
                    DaoUPayActivity.IntentBuilder intentBuilder = DaoUPayActivity.IntentBuilder.build(CardOrderDetailActivity.this)
                            .setAmount((int) orderAmount)
                            .setOrderID(giftCardOrderId)
                            .setOrderType(1)
                            .setListener(new OperaRequestListener() {
                                @Override
                                public void onOperaSuccess() {
                                    Intent intent = new Intent();
                                    intent.setClass(CardOrderDetailActivity.this, CardPaymentSuccessActivity.class);
                                    intent.putExtra("orderNumber", giftCardOrderId);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onOperaFailure() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CardOrderDetailActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });
                    startActivity(intentBuilder);
                }
                if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_ADYEN)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            showLoading();
                        }
                    });
                    paymentManager.adyenPay(paymentString, cardOrderNo, giftCardOrderId, 1, orderAmount, AccountManager.getInstance().getCurrencyCode(), new PaymentManager.OnPayListener() {
                        @Override
                        public void onPay(Response response, PayResultBaen payResult) {
                            try {
//                            LogUtils.d(payResult.authCode);
                                if (!TextUtils.isEmpty(payResult.authCode)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("orderNumber", giftCardOrderId);
                                    intent.setClass(CardOrderDetailActivity.this, CardPaymentSuccessActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CardOrderDetailActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String errMsg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CardOrderDetailActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
//                        MyPayActivity.closeLoading();
                                }
                            });
                        }
                    });
                }
//                closeLoading();
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }

}
