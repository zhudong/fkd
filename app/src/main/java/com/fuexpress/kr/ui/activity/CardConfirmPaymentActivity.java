package com.fuexpress.kr.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import fksproto.CsCard;
import fksproto.CsParcel;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/5/10.
 */
public class CardConfirmPaymentActivity extends BaseActivity {

    private View rootView;
    private Bundle bundle;

    private TextView cardOrderIdTv;
    private TextView cardPaymentTv;
    private TextView cardCountTv;
    private TextView cardGrandTotalTv;
    private Button confirmBtn;
    private String payment;
    private String paymentString;
    private int giftCardOrderId;
    private String giftCardOrderNo;
    private String grandTotal;
    private ImageView toBackIv;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_card_confirm_payment, null);
        TitleBarView titleBarView = (TitleBarView) rootView.findViewById(R.id.card_confirm_titlebar);
        titleBarView.setTitleText(getString(R.string.cart_confirm_title_bar_title));
        toBackIv = titleBarView.getIv_in_title_back();
        bundle = getIntent().getExtras();
        giftCardOrderId = bundle.getInt("giftcardorderid");
        payment = bundle.getString("paymenttype");
        paymentString = bundle.getString("paymentString");
        giftCardOrderNo = bundle.getString("giftcardorderno");
        grandTotal = bundle.getString("grandtotal");

        cardOrderIdTv = (TextView) rootView.findViewById(R.id.confirm_payment_order_id_tv);
        cardPaymentTv = (TextView) rootView.findViewById(R.id.confirm_payment_tv);
        cardCountTv = (TextView) rootView.findViewById(R.id.confirm_payment_count_tv);
        cardGrandTotalTv = (TextView) rootView.findViewById(R.id.confirm_payment_grandtotal_tv);
        confirmBtn = (Button) rootView.findViewById(R.id.confirm_payment_btn);

        cardOrderIdTv.setText(giftCardOrderNo);
//        if (TextUtils.equals("alipay", payment)) {
//            cardPaymentTv.setText(getString(R.string.String_ali_pay));
//        }
//        if (TextUtils.equals("wxap", payment)) {
//            cardPaymentTv.setText(getString(R.string.String_wechat_pay));
//        }
//        if (TextUtils.equals("adyen", payment)) {
//            cardPaymentTv.setText(getString(R.string.String_adyen_pay));
//        }
//        if (TextUtils.equals("krbank", payment)) {
//            cardPaymentTv.setText(getString(R.string.String_krbank_pay));
//        }
//        if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
//            cardPaymentTv.setText(payment);
//        }
        cardPaymentTv.setText(paymentString);
        cardCountTv.setText(bundle.getInt("cardcount") + "");
//        cardGrandTotalTv.setText(UIUtils.getCurrency(this) + bundle.getString("grandtotal"));
        cardGrandTotalTv.setText(UIUtils.getCurrency(this, Float.valueOf(grandTotal)));

        ActivityController.cardAddActivity(this);
        toBackIv.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_payment_btn:
                payGiftCardOrder(giftCardOrderId, payment, giftCardOrderNo, Float.parseFloat(grandTotal));
//                payGiftCardOrder(giftCardOrderNo, giftCardOrderId, Float.parseFloat(grandTotal));
                break;
            case R.id.iv_in_title_back:
                showDialog();
                break;
        }
    }

    public void showDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(getString(R.string.gift_card_dialog_msg));
        builder.setPositiveButton(R.string.String_go_on_pay_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                payGiftCardOrder(giftCardOrderId, payment);
                payGiftCardOrder(giftCardOrderNo, giftCardOrderId, Float.parseFloat(grandTotal));

            }
        });
        builder.setNegativeButton(R.string.String_out_pay_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                ActivityController.finishAll();
                ActivityController.cardFinish();
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            showDialog();
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void payGiftCardOrder(final int giftCardOrderId, final String payment, final String orderNumber, final float amount) {
        showLoading(5000);
        CsParcel.PayGiftCardOrderRequest.Builder builder = CsParcel.PayGiftCardOrderRequest.newBuilder();
        builder.setGiftCardOrderId(giftCardOrderId);
        builder.setPaymentCode(payment);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayGiftCardOrderResponse>() {

            @Override
            public void onSuccess(CsParcel.PayGiftCardOrderResponse response) {
                LogUtils.d(response.toString());
                PaymentManager paymentManager = PaymentManager.getInstance(CardConfirmPaymentActivity.this);
                SysApplication app = (SysApplication) getApplication();
                app.setPaymentRequestCode(Constants.PAYMENT_REQUEST_CODE_CARD_CART);
                app.setGiftCardOrderNo(giftCardOrderNo);
                if (TextUtils.equals(payment, "alipay")) {
                    paymentManager.aliPay(response.getPayInfo(), new PaymentManager.OnAliPayListener() {
                        @Override
                        public void onSuccess(String resultStatus) {
                            Intent intent = new Intent();
                            intent.setClass(CardConfirmPaymentActivity.this, CardPaymentSuccessActivity.class);
                            intent.putExtra("orderNumber", giftCardOrderNo);
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
                    DaoUPayActivity.IntentBuilder intentBuilder = DaoUPayActivity.IntentBuilder.build(CardConfirmPaymentActivity.this)
                            .setAmount((int) amount)
                            .setOrderID(giftCardOrderId)
                            .setOrderType(1)
                            .setListener(new OperaRequestListener() {
                                @Override
                                public void onOperaSuccess() {
                                    Intent intent = new Intent();
                                    intent.setClass(CardConfirmPaymentActivity.this, CardPaymentSuccessActivity.class);
                                    intent.putExtra("orderNumber", orderNumber);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onOperaFailure() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CardConfirmPaymentActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();

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
                    paymentManager.adyenPay(paymentString, orderNumber, giftCardOrderId, 1, amount, AccountManager.getInstance().getCurrencyCode(), new PaymentManager.OnPayListener() {
                        @Override
                        public void onPay(Response response, PayResultBaen payResult) {
                            try {
//                            LogUtils.d(payResult.authCode);
                                if (!TextUtils.isEmpty(payResult.authCode)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("orderNumber", orderNumber);
                                    intent.setClass(CardConfirmPaymentActivity.this, CardPaymentSuccessActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CardConfirmPaymentActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(CardConfirmPaymentActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
//                        MyPayActivity.closeLoading();
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

    public void payGiftCardOrder(final String orderNumber, long orderId, float amount) {
        if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
            DaoUPayActivity.IntentBuilder intentBuilder = DaoUPayActivity.IntentBuilder.build(CardConfirmPaymentActivity.this)
                    .setAmount((int) amount)
                    .setOrderID(orderId)
                    .setOrderType(1)
                    .setListener(new OperaRequestListener() {
                        @Override
                        public void onOperaSuccess() {
                            Intent intent = new Intent();
                            intent.setClass(CardConfirmPaymentActivity.this, CardPaymentSuccessActivity.class);
                            intent.putExtra("orderNumber", orderNumber);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onOperaFailure() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CardConfirmPaymentActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
            startActivity(intentBuilder);
        }
        if (TextUtils.equals(payment, Constants.GIFT_CARD_PAYMENT_ADYEN)) {
            PaymentManager.getInstance(this).adyenPay(paymentString, orderNumber, orderId, 1, amount, AccountManager.getInstance().getCurrencyCode(), new PaymentManager.OnPayListener() {
                @Override
                public void onPay(Response response, PayResultBaen payResult) {
                    try {
//                            LogUtils.d(payResult.authCode);
                        if (!TextUtils.isEmpty(payResult.authCode)) {
                            Intent intent = new Intent();
                            intent.putExtra("orderNumber", orderNumber);
                            intent.setClass(CardConfirmPaymentActivity.this, CardPaymentSuccessActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CardConfirmPaymentActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CardConfirmPaymentActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
//                        MyPayActivity.closeLoading();
                        }
                    });
                }
            });
        }
    }


}
