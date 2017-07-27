package com.fuexpress.kr.ui.activity.shopping_cart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.PayResultBaen;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.activity.AddRequireActivity;
import com.fuexpress.kr.ui.activity.DaoUPayActivity;
import com.fuexpress.kr.ui.activity.PickUpActivity;
import com.fuexpress.kr.ui.activity.PickUpSuccessActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;
import com.socks.library.KLog;

import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsOrder;
import fksproto.CsShipping;
import fksproto.CsUser;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/22.
 */
public class ConfirmPaymentActivity extends BaseActivity {
    public static String IS_CROWD_ORDER = "is_crowd_order";

    private View rootView;
    private TextView toCartTv;

    private TextView orderIdTv;
    private TextView deliveryTv;
    private TextView purchaseTv;
    private TextView payTypeTv;
    private TextView commodityCountTv;
    private TextView subTotalTv;
    private TextView grandTotalTv;
    private TextView balancePayTv;
    private TextView needPayTv;

    private Button confirmPaymentBtn;
    private LinearLayout feeLayout;
    private int payType;
    private long orderid;
    private boolean isUseBalance;
    private CustomDialog.Builder dialog;
    private float balance;
    private float grandTotalf;
    private float payTotalf;
    private ImageView toBackIv;
    private String orderNumber;


    private TextView bankNameTv, recevierNameTv, bankAccountTv, bankCountTv, redNoticeTv;
    private LinearLayout bankLayout, infoLayout;
    private boolean isKrBank = false;
    private String payString;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_confirm_payment, null);
        TitleBarView titleBarView = (TitleBarView) rootView.findViewById(R.id.confirm_titlebar);
        titleBarView.setTitleText(getString(R.string.cart_confirm_title_bar_title));
//        toCartTv = titleBarView.getTextViewTitleLeftShopCart();
        toBackIv = titleBarView.getIv_in_title_back();

        Intent intent = getIntent();
        boolean isCrowdOrder = intent.getBooleanExtra(IS_CROWD_ORDER, false);
        if (isCrowdOrder) {
            rootView.findViewById(R.id.ll_buy_type).setVisibility(View.GONE);
        }


        Bundle bundle = intent.getExtras();
        orderNumber = bundle.getString("orderNumber");
        orderid = bundle.getLong("orderId");
        payType = bundle.getInt("payType");
        payString = bundle.getString("payMethod");
        if (TextUtils.equals(payString, Constants.GIFT_CARD_PAYMENT_KRBANK)) {
            payString = getString(R.string.String_krbank_pay2);
        }
        if (TextUtils.equals(payString, Constants.GIFT_CARD_PAYMENT_ADYEN)) {
            payString = getString(R.string.String_adyen_pay);
        }
        if (TextUtils.equals(payString, Constants.GIFT_CARD_PAYMENT_ALIPAY)) {
            payString = getString(R.string.String_ali_pay);
        }
        if (TextUtils.equals(payString, Constants.GIFT_CARD_PAYMENT_WXCHAT)) {
            payString = getString(R.string.String_wechat_pay);
        }
        KLog.i("payType = " + payType);
        confirmPaymentBtn = (Button) rootView.findViewById(R.id.payment_confirm_btn);
        if (payType == Constants.PAYMENT_KRBANK) {
            //使用有利银行（krbank）线下汇款
            isKrBank = true;
            bankNameTv = (TextView) rootView.findViewById(R.id.bank_name_tv);
            recevierNameTv = (TextView) rootView.findViewById(R.id.receive_name_tv);
            bankAccountTv = (TextView) rootView.findViewById(R.id.bank_account_tv);
            bankCountTv = (TextView) rootView.findViewById(R.id.count);
            redNoticeTv = (TextView) rootView.findViewById(R.id.red_notice);
            bankLayout = (LinearLayout) rootView.findViewById(R.id.bank_receiver_info_layout);
            infoLayout = (LinearLayout) rootView.findViewById(R.id.krbank_notice_layout);
            bankLayout.setVisibility(View.VISIBLE);
            infoLayout.setVisibility(View.VISIBLE);
            confirmPaymentBtn.setText(getString(R.string.keep_searching));
            getKrBankReceiveInfo();
        }
        int purchaseScheme = bundle.getInt("purchaseScheme");
        int shippingScheme = bundle.getInt("shippingScheme");
        balance = bundle.getFloat("balance");
        String fee = bundle.getString("fee");
        int commodityCount = bundle.getInt("commodityCount");
        float subTotalf = bundle.getFloat("subTotal");
        grandTotalf = bundle.getFloat("grandTotal");
        isUseBalance = bundle.getBoolean("isUseBalance");
        List<CsShipping.Shipping> shippingList = (List<CsShipping.Shipping>) bundle.getSerializable("shippingList");

        orderIdTv = (TextView) rootView.findViewById(R.id.payment_orderid_tv);
        deliveryTv = (TextView) rootView.findViewById(R.id.payment_delivery_tv);
        purchaseTv = (TextView) rootView.findViewById(R.id.payment_purchase_tv);
        payTypeTv = (TextView) rootView.findViewById(R.id.payment_paytype_tv);
        commodityCountTv = (TextView) rootView.findViewById(R.id.payment_commodity_count_tv);
        subTotalTv = (TextView) rootView.findViewById(R.id.payment_subtotal_tv);
        grandTotalTv = (TextView) rootView.findViewById(R.id.payment_grandtotal_tv);
        balancePayTv = (TextView) rootView.findViewById(R.id.payment_balance_pay_tv);
        needPayTv = (TextView) rootView.findViewById(R.id.payment_need_pay_tv);

        feeLayout = (LinearLayout) rootView.findViewById(R.id.payment_fee_layout);
        //   confirmPaymentBtn = (Button) rootView.findViewById(R.id.payment_confirm_btn);

        orderIdTv.setText(orderNumber);
        if (shippingScheme == Constants.SHIPPING_SCHEME_MERGE) {
            deliveryTv.setText(getResources().getString(R.string.String_merge_order));
            feeLayout.setVisibility(View.GONE);
        }
        if (shippingScheme == Constants.SHIPPING_SCHEME_DIRECT) {
            feeLayout.setVisibility(View.VISIBLE);
            if (shippingList != null) {
                feeLayout.removeAllViews();
                for (int i = 0; i < shippingList.size(); i++) {
                    CsShipping.Shipping shipping = shippingList.get(i);
                    LinearLayout feeChildLayot = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.confirm_payment_fee_layout, null);
                    TextView feeTitleTv = (TextView) feeChildLayot.findViewById(R.id.payment_fee_title_tv);
                    TextView feeTv = (TextView) feeChildLayot.findViewById(R.id.payment_fee_tv);
                    feeTitleTv.setText(shipping.getTitle());
                    String price = getResources().getString(R.string.String_order_price);
                    feeTv.setText(UIUtils.getCurrency(ConfirmPaymentActivity.this, (float) shipping.getFee()));
                    feeLayout.addView(feeChildLayot);
                }
            }
            deliveryTv.setText(getResources().getString(R.string.String_direct_mail_1) + fee);
        }
        purchaseTv.setText(purchaseScheme == Constants.PURCHASE_SCHEME_STOCK
                ? getResources().getString(R.string.String_cancel_msg)
                : getResources().getString(R.string.String_reseve));
        String str = "";
        String payMethod = getString(R.string.String_adyen_pay);
        if (isUseBalance) {
//            str = getString(R.string.confirm_payment_pay_type_msg) + balance;
//            str += "\n" + payString;
            str += payString;
//            if (payType == Constants.PAYMENT_ALIPAY) {
//                str += "\n" + getResources().getString(R.string.String_ali_pay);
//            } else {
//                str += "\n" + getResources().getString(R.string.String_wechat_pay);
//            }
            payTypeTv.setText(str);

        } else {
//            payTypeTv.setText((payType == Constants.PAYMENT_ALIPAY
//                    ? getResources().getString(R.string.String_ali_pay)
//                    : getResources().getString(R.string.String_wechat_pay)));
            payTypeTv.setText(payString);
        }

        commodityCountTv.setText(commodityCount + "");

        String price = getResources().getString(R.string.String_order_price);
//        String subTotal = String.format(price, subTotalf);
        String currencyCode = UIUtils.getCurrency(this);
        String subTotal;
//        if(TextUtils.equals(currencyCode, "₩")){
        subTotal = UIUtils.getCurrency(this, subTotalf);
//        }else {
//            subTotal = getString(R.string.String_order_price, UIUtils.getCurrency(this), subTotalf);
//        }
        String grandTotal;
        if (shippingList != null) {
            for (int i = 0; i < shippingList.size(); i++) {
                CsShipping.Shipping shipping = shippingList.get(i);
                grandTotalf += shipping.getFee();
            }
            /*if(TextUtils.equals(currencyCode, "₩")){
                grandTotal = getString(R.string.String_order_price2,UIUtils.getCurrency(this) , grandTotalf);
            }else {
                grandTotal = getString(R.string.String_order_price,UIUtils.getCurrency(this) , grandTotalf);
            }*/
            grandTotal = UIUtils.getCurrency(this, grandTotalf);

        } else {
            /*if(TextUtils.equals(currencyCode, "₩")){
                grandTotal = getString(R.string.String_order_price2,UIUtils.getCurrency(this) , grandTotalf);
            }else {
                grandTotal = getString(R.string.String_order_price,UIUtils.getCurrency(this) , grandTotalf);
            }*/
            grandTotal = UIUtils.getCurrency(this, grandTotalf);

        }

        String needStr;
        String balanceStr;
        float defaultFee = 0.00f;
        if (isUseBalance) {
            if (balance >= grandTotalf) {
                /*if(TextUtils.equals(currencyCode, "₩")) {
                    needStr = getString(R.string.String_order_price2, UIUtils.getCurrency(this), defaultFee);
                    balanceStr = getString(R.string.String_order_price2, UIUtils.getCurrency(this), grandTotalf);
                }else {
                    needStr = getString(R.string.String_order_price, UIUtils.getCurrency(this), defaultFee);
                    balanceStr = getString(R.string.String_order_price, UIUtils.getCurrency(this), grandTotalf);
                }*/

                needStr = UIUtils.getCurrency(this, defaultFee);
                balanceStr = UIUtils.getCurrency(this, grandTotalf);
                payTotalf = 0;
            } else {
               /* if(TextUtils.equals(currencyCode, "₩")) {
                    needStr = getString(R.string.String_order_price2,UIUtils.getCurrency(this) , (grandTotalf - balance));
                    balanceStr = getString(R.string.String_order_price2, UIUtils.getCurrency(this) , balance);
                }else {
                    needStr = getString(R.string.String_order_price,UIUtils.getCurrency(this) , (grandTotalf - balance));
                    balanceStr = getString(R.string.String_order_price, UIUtils.getCurrency(this) , balance);
                }*/
                needStr = UIUtils.getCurrency(this, (grandTotalf - balance));
                balanceStr = UIUtils.getCurrency(this, balance);
                payTotalf = grandTotalf - balance;
            }
        } else {
            /*if(TextUtils.equals(currencyCode, "₩")) {
                balanceStr = getString(R.string.String_order_price2,UIUtils.getCurrency(this) , 0.00);
                needStr = getString(R.string.String_order_price2,UIUtils.getCurrency(this) , grandTotalf);
            }else {
                balanceStr = getString(R.string.String_order_price,UIUtils.getCurrency(this) , 0.00);
                needStr = getString(R.string.String_order_price,UIUtils.getCurrency(this) , grandTotalf);
            }*/
            balanceStr = UIUtils.getCurrency(this, 0.00f);
            needStr = UIUtils.getCurrency(this, grandTotalf);
            payTotalf = grandTotalf;
        }

        subTotalTv.setText(subTotal);
        grandTotalTv.setText(grandTotal);
        balancePayTv.setText(balanceStr);
        needPayTv.setText(needStr);


        confirmPaymentBtn.setOnClickListener(this);
//        toCartTv.setOnClickListener(this);
        toBackIv.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_confirm_btn:
//                if(isUseBalance){
//                    if(balance >= grandTotalf){
//                        SysApplication app            = (SysApplication) getApplication();
//                        app.setPaymentRequestCode(Constants.PAYMENT_REQUEST_CODE_SHOP_CART);
//                        Intent intent = new Intent();
//                        intent.setClass(this, PaymentSuccessActivity.class);
//                        startActivity(intent);
//                    }else{
//                        applyForSalesOrderPay();
//                    }
//                }else{
                //}
                if (isKrBank) {
                    EventBus.getDefault().post(new BusEvent(BusEvent.GO_STORE_PAGE
                            , null));
                    return;
                }
                if (payTotalf == 0) {
                    Intent intent = new Intent();
                    intent.setClass(ConfirmPaymentActivity.this, PaymentSuccessActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    pay();
                }
//                applyForSalesOrderPay();
                break;
            case R.id.iv_in_title_back:
            case R.id.textview_title_left_shorcart:
                if (payType == Constants.PAYMENT_KRBANK) {
                    finish();
                } else {
                    showDialog();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            showDialog();
        }
        return true;
    }

    public void pay(){
        if (payType == Constants.PAYMENT_DAOUPAY) {
            DaoUPayActivity.IntentBuilder intentBuilder = DaoUPayActivity.IntentBuilder.build(ConfirmPaymentActivity.this)
                    .setAmount((int) payTotalf)
                    .setOrderID(orderid)
                    .setOrderType(0)
                    .setListener(new OperaRequestListener() {
                        @Override
                        public void onOperaSuccess() {
                            Intent intent = new Intent();
                            intent.setClass(ConfirmPaymentActivity.this, PaymentSuccessActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onOperaFailure() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ConfirmPaymentActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
            startActivity(intentBuilder);
        }
        if (payType == Constants.PAYMENT_ADYEN) {
            showLoading();
            PaymentManager.getInstance(ConfirmPaymentActivity.this).adyenPay(payString, orderNumber, orderid, 0, payTotalf, AccountManager.getInstance().getCurrencyCode(), new PaymentManager.OnPayListener() {
                @Override
                public void onPay(Response response, PayResultBaen payResult) {
                    try {
//                            LogUtils.d(payResult.authCode);
                        if (!TextUtils.isEmpty(payResult.authCode)) {
                            Intent intent = new Intent();
                            intent.setClass(ConfirmPaymentActivity.this, PaymentSuccessActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    closeLoading();
                                    Toast.makeText(ConfirmPaymentActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
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
                            closeLoading();
                            Toast.makeText(ConfirmPaymentActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });

        }
        if(payType == Constants.PAYMENT_WECHAT || payType == Constants.PAYMENT_ALIPAY){
            applyForSalesOrderPay();
        }
    }

    public void showDialog() {
        dialog = new CustomDialog.Builder(this);
        dialog.setMessage(R.string.String_out_pay_title);
        dialog.setPositiveButton(R.string.String_go_on_pay_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                applyForSalesOrderPay();
                if (payTotalf == 0) {
                    Intent intent = new Intent();
                    intent.setClass(ConfirmPaymentActivity.this, PaymentSuccessActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    pay();
                }

            }
        });
        dialog.setNegativeButton(R.string.String_out_pay_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("isCancelPay", true);
                setResult(Constants.CONFIRM_REQUEST_CODE, intent);
                finish();
            }
        });
        dialog.create().show();
    }

    public void applyForSalesOrderPay() {
        showLoading(5000);
        CsOrder.ApplyForSalesOrderPayRequest.Builder builder = CsOrder.ApplyForSalesOrderPayRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setOrderId(orderid);
//        if(isUseBalance){
//            builder.setPayMethod(3);
//        }else {
        builder.setPayMethod(payType);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
//        }
        NetEngine.postRequest(builder, new INetEngineListener<CsOrder.ApplyForSalesOrderPayResponse>() {

            @Override
            public void onSuccess(CsOrder.ApplyForSalesOrderPayResponse response) {

                LogUtils.d(response.toString());
                String payInfo = response.getPayInfo();
                PaymentManager paymentManager = PaymentManager.getInstance(ConfirmPaymentActivity.this);
                SysApplication app = (SysApplication) getApplication();
                app.setPaymentRequestCode(Constants.PAYMENT_REQUEST_CODE_SHOP_CART);
                if (payType == Constants.PAYMENT_WECHAT) {
                    paymentManager.wechatPay(payInfo);
                }
                if (payType == Constants.PAYMENT_ALIPAY) {
                    closeLoading();

                    PaymentManager.getInstance(ConfirmPaymentActivity.this).aliPay(response.getPayInfo(), new PaymentManager.OnAliPayListener() {
                        @Override
                        public void onSuccess(String resultStatus) {
                            Intent intent = new Intent(ConfirmPaymentActivity.this, PickUpSuccessActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            intent.setClass(ConfirmPaymentActivity.this, PaymentSuccessActivity.class);
                            ConfirmPaymentActivity.this.startActivity(intent);
                        }

                        @Override
                        public void onFailure(String resultStatus, final String errorMsg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToast.makeText(ConfirmPaymentActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            });
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

    private void getKrBankReceiveInfo() {
        CsUser.GetKrBankInfoRequest.Builder builder = CsUser.GetKrBankInfoRequest.newBuilder();
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetKrBankInfoResponse>() {

            @Override
            public void onSuccess(final CsUser.GetKrBankInfoResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bankNameTv.setText(response.getBankName());
                        recevierNameTv.setText(response.getAcountName());
                        bankAccountTv.setText(response.getAcountNumber());
                        bankCountTv.setText(needPayTv.getText());
                        redNoticeTv.setText("" + orderid);
                    }
                });
                KLog.d(response.toString());
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }
}
