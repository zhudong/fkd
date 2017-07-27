package com.fuexpress.kr.ui.activity.help_send;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.HelpSendParcelBean;
import com.fuexpress.kr.bean.IDinfoBean;
import com.fuexpress.kr.bean.PayResultBaen;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ParcelHelpDao;
import com.fuexpress.kr.model.PayMethodManager;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.activity.KrBankInfoActivity;
import com.fuexpress.kr.ui.activity.ParcelPayMethodActivity;
import com.fuexpress.kr.ui.activity.ParcelSubmitSucessActivity;
import com.fuexpress.kr.ui.activity.append_item.JsonSerializer;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.utils.FloatUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsParcel;
import okhttp3.Response;

/**
 * Created by yuan on 2016-6-13.
 */
public class HelpSendPresent extends HelpSendContract.Presenter {

    private List<HelpSendParcelBean> mResults = new ArrayList<>();
    private float mShippingFee;
    private static Handler mHandler = new Handler();
    private float mGiftcardaccount;
    private String parceName;
    private float total;
    private String mPayCode;
    private float mPureShippingFee;


    @Override
    public void reSet() {
        mShippingFee = 0;
        mPureShippingFee = 0;
    }

    @Override
    public void onStart() {
        mResults = ParcelHelpDao.getInstance(context).getAllParcel(true);
        mView.showParcleList(mResults, true, true);
        int hasShippingCount = 0;
        int noShippingCount = 0;
        for (int i = 0; i < mResults.size(); i++) {
            if (mResults.get(i).getShippingmethodid() == 0) {
                ++noShippingCount;
            } else {
                ++hasShippingCount;
            }
        }

        mView.showParcleList(mResults, hasShippingCount < 3, noShippingCount < 1);
        calculateShippingFee();
    }

    @Override
    protected float getShippingFee() {
        return mShippingFee;
    }


    float oldShippingFee = 0;

    private void calculateShippingFee() {
        parceName = "";
        mShippingFee = 0;
        mPureShippingFee = 0;
        for (int i = 0; i < mResults.size(); i++) {
            HelpSendParcelBean parcelBean = mResults.get(i);
            if (parcelBean.getShippingmethodid() == 0) continue;
            mShippingFee += parcelBean.getShippingFee();
            mPureShippingFee += parcelBean.getPureShippingFee();
            if (i == mResults.size() - 1) {
                parceName += parcelBean.getParcelName() + "\n";
            } else {
                parceName += parcelBean.getParcelName() + "，\n";
            }
        }
//        if (oldShippingFee > 0 && oldShippingFee != mShippingFee) {
        mView.showPayType(getShippingFee());
//        }

        oldShippingFee = mShippingFee;
        mView.showEstimatePrice(mShippingFee);
    }

    public void setPayCode(String payCode) {
        this.mPayCode = payCode;
    }


    @Override
    public void submit() {
        sendParcelItem();
    }


  /*  required BaseRequest           head                       = 1;
    optional BaseUserRequest       userinfo                   = 2;   //用户信息
    optional string                paymentcode                = 3;//支付方式(支付宝:alipay 微信:wxap)
    optional int32                 sumcount                   = 4;//包裹数量
    optional string                localecode                 = 5;//语种(中文:zh_CN 韩文:ko_KR)
    optional int32                 shippingcouponcustomerid   = 6;//优惠劵Id
    repeated SendForParcelList     sendforparcellist          = 7;*/

    private void sendParcelItem() {
        context.showLoading();
        if (mPayCode == null) {
            CustomToast.makeText(context, context.getString(R.string.please_select_pay_method), Toast.LENGTH_SHORT).show();
            return;
        }
        CsParcel.SendForItemRequest.Builder builder = CsParcel.SendForItemRequest.newBuilder();
        int toSendCount = 0;
        JsonSerializer jsonSerializer = new JsonSerializer();
        for (int i = 0; i < mResults.size(); i++) {
            HelpSendParcelBean bean = mResults.get(i);
            if (bean.getShippingmethodid() == 0) {
                continue;
            }

            ++toSendCount;
            CsParcel.SendForParcelList.Builder parcel = CsParcel.SendForParcelList.newBuilder();
            parcel.setWarehouseid(bean.getWareHouseID())
                    .setParcelid(bean.getParcelid())
                    .setWeight(bean.getWeight())
                    .setCustomeraddressid(bean.getCustomeraddressid())
                    .setShippingmethodid(bean.getShippingmethodid())
                    .setDeclaredtotal(bean.getProductprice())
                    .setQty(bean.getQty());

            IDinfoBean iDinfoBean = jsonSerializer.deserializeIDinfo(bean.getIDCardInfo());
            if (iDinfoBean != null && iDinfoBean.isNeedId()) {
                if (!TextUtils.isEmpty(iDinfoBean.getIdNumber())) {
                    parcel.setIdcard(iDinfoBean.getIdNumber() != null ? iDinfoBean.getIdNumber() : "");
                    parcel.setIdcardbackimage(iDinfoBean.getUrlBack() != null ? iDinfoBean.getUrlBack() : "");
                    parcel.setIdcardfrontimage(iDinfoBean.getUrlFront() != null ? iDinfoBean.getUrlFront() : "");
                } else {
                    parcel.setIdcard(iDinfoBean.getServerIDNumber() != null ? iDinfoBean.getServerIDNumber() : "");
                    parcel.setIdcardbackimage(iDinfoBean.getServerUrlBack() != null ? iDinfoBean.getServerUrlBack() : "");
                    parcel.setIdcardfrontimage(iDinfoBean.getServerUrlFront() != null ? iDinfoBean.getServerUrlFront() : "");
                }
            }
            builder.addSendforparcellist(parcel);
        }


        if (toSendCount == 0) {
            CustomToast.makeText(context, "没有合适的包裹，请重新编辑包裹！", Toast.LENGTH_SHORT).show();
            closeLoading(null);
            return;
        }
        PayMethodManager manager = PayMethodManager.getInstance(context);

        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest())
//                .setPaymentcode(mPayCode)
                .setSumcount(toSendCount)
                .setLocalecode(AccountManager.getInstance().getLocaleCode())
                .setCurrencycode(AccountManager.getInstance().getCurrencyCode())
                .setUseaccountpay(manager.isUseBalance() ? 1 : 2).setIsNewVersion(1);//2：不使用余额 0,1：使用余额
        if (manager.getCurrentCoupon() != null)
            builder.setShippingcouponcustomerid(manager.getCurrentCoupon().getShippingcouponcustomerid());

        if (manager.getMethodIndex() != -1) {
            builder.setPaymentcode(mPayCode);
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.SendForItemResponse>() {

            @Override
            public void onSuccess(final CsParcel.SendForItemResponse response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        余额不足到第三方支付
                        if ("Pending".equals(response.getStatus())) {
                            String redirecturl = response.getRedirecturl();
                            String s1 = redirecturl.split("\\?")[1];
                            String[] params = s1.split("\\&");
                            ArrayMap map = new ArrayMap();
                            for (int i = 0; i < params.length; i++) {
                                String[] kv = params[i].split("=");
                                if (kv.length > 1 && kv[0] != null && kv[1] != null) {
                                    map.put(kv[0], kv[1]);
                                }
                            }
                            doDirectPay(map);
                            clearItems();
                        } else {
//                            直接扣除余额支付
                            context.closeLoading();
                            submitSuccess(true);
                        }
                    }
                });
            }

            @Override
            public void onFailure(final int ret, final String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        context.closeLoading();
                        CustomToast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    private void doDirectPay(ArrayMap<String, String> map) {
        CsParcel.DoDirectGiftCardRequest.Builder builder = CsParcel.DoDirectGiftCardRequest.newBuilder();
        int type = "".equals(map.get("type")) ? 0 : Integer.valueOf(map.get("type"));
        int appType = "".equals(map.get("appType")) ? 0 : Integer.valueOf(map.get("appType"));
        String paymentCode = map.get("paymentCode");
        int salesRequireMainId = "".equals(map.get("salesRequireMainId")) ? 0 : Integer.valueOf(map.get("salesRequireMainId"));
        total = "".equals(map.get("total")) ? 0 : FloatUtils.vlaueOf(map.get("total"));
        float estimate = "".equals(map.get("estimateShippingFee")) ? 0 : FloatUtils.vlaueOf(map.get("estimateShippingFee"));
        builder.setType(type)
                .setAppType(appType)
                .setPaymentCode(paymentCode)
                .setSalesrequiremainid(salesRequireMainId)
                .setTotal(total)
                .setUserinfo(AccountManager.getInstance().mBaseUserRequest)
                .setCurrencycode(AccountManager.getInstance().getCurrencyCode()).setShippingfee(mPureShippingFee);
        PayMethodManager manager = PayMethodManager.getInstance(context);
        if (manager.getCurrentCoupon() != null)
            builder.setShippingcouponcustomerid(manager.getCurrentCoupon().getShippingcouponcustomerid());


        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.DoDirectGiftCardResponse>() {

            @Override
            public void onSuccess(CsParcel.DoDirectGiftCardResponse response) {
                getPayInfo(response.getNumber(), response.getOrderId(), response.getPaymentCode());
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading(errMsg);
            }
        });
    }

    private void closeLoading(final String errMsg) {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                context.closeLoading();
                if (!TextUtils.isEmpty(errMsg)) {
                    CustomToast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void getPayInfo(final String number, final int orderCode, final String payCode) {
        if ("krbank".equals(payCode)) {
            mGiftcardaccount = PayMethodManager.getInstance(context).getFreeBalance();
            Intent intent = new Intent(context, KrBankInfoActivity.class);
            intent.putExtra(KrBankInfoActivity.NEED_PAY_COUNT, PayMethodManager.getInstance(context).getNeedPay());
            intent.putExtra(KrBankInfoActivity.SHIPPING_FEE_TOTAL, mShippingFee);
            intent.putExtra(KrBankInfoActivity.BALANCE, mGiftcardaccount);
            intent.putExtra(KrBankInfoActivity.PAYMENT, context.getString(R.string.String_krbank_pay2));
            intent.putExtra(KrBankInfoActivity.PAY_CURRENCY_CODE, AccountManager.getInstance().getCurrencyCode());
            context.startActivity(intent);
        } else if ("adyen".equals(payCode)) {
            PaymentManager.getInstance(context).adyenPay(PayMethodManager.getInstance(context).getPayMethodName(), number, orderCode, 1, total, AccountManager.getInstance().getCurrencyCode(), new PaymentManager.OnPayListener() {
                @Override
                public void onPay(Response response, final PayResultBaen payResult) {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            if ("Authorised".equals(payResult.resultCode) && !"".equals(payResult.authCode)) {
                                submitSuccess(true);
                            }
                        }
                    });
                }

                @Override
                public void onError(final String errMsg) {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            submitSuccess(false);
                            String err = errMsg;
                            if (!TextUtils.isEmpty(err) && TextUtils.isDigitsOnly(err)) {
                                err = context.getString(R.string.pay_error_msg);
                                CustomToast.makeText(context, err, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else if (Constants.GIFT_CARD_PAYMENT_DAOUPAY.equals(payCode)) {
            PaymentManager.getInstance(context).daouPay(context, total, orderCode, 1, new OperaRequestListener() {
                @Override
                public void onOperaSuccess() {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            submitSuccess(true);
                        }
                    });
                }

                @Override
                public void onOperaFailure() {
                    submitSuccess(false);
                }
            });
        }

        if (!("alipay".equals(payCode) | "wxap".equals(payCode))) return;

        CsParcel.PayGiftCardOrderRequest.Builder builder = CsParcel.PayGiftCardOrderRequest.newBuilder();
        builder.setPaymentCode(payCode)
                .setGiftCardOrderId(orderCode)
                .setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayGiftCardOrderResponse>() {

            @Override
            public void onSuccess(CsParcel.PayGiftCardOrderResponse response) {
//                closeLoading(null);
                SysApplication.mCurrentRequestPayment = HelpSendPresent.class.getSimpleName();
                String payInfo = response.getPayInfo();
                PaymentManager paymentManager = PaymentManager.getInstance(context);
                if ("alipay".equals(payCode)) {
                    paymentManager.aliPay(payInfo, new PaymentManager.OnAliPayListener() {
                        @Override
                        public void onSuccess(String resultStatus) {

                        }

                        @Override
                        public void onFailure(String resultStatus, String errorMsg) {

                        }
                    });
                } else if ("wxap".equals(payCode)) {
                    paymentManager.wechatPay(payInfo);
                }

            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading(errMsg);
            }
        });
    }


    @Override
    public void switchPayType() {
        Intent intent = new Intent(context, ParcelPayMethodActivity.class);
        intent.putExtra(ParcelPayMethodActivity.CURRENCY_CODE, AccountManager.getInstance().getCurrencyCode());
        intent.putExtra(ParcelPayMethodActivity.SHIPPING_FEE, mPureShippingFee);
        context.startActivityForResult(intent, 0);
    }


    @Override
    public void submitSuccess(boolean sucess) {
        closeLoading("");
        if (sucess) {
            Intent intent = new Intent(context, ParcelSubmitSucessActivity.class);
            intent.putExtra(ParcelSubmitSucessActivity.PARCEL_NAMES, parceName);
            context.startActivity(intent);
        } else {
            EventBus.getDefault().post(new BusEvent(BusEvent.GO_SUBMIT_PARCEL, ""));
        }
        clearItems();
        EventBus.getDefault().post(new BusEvent(BusEvent.Append_PARCEN_SUCESS, ""));
    }

    private void clearItems() {
        ParcelHelpDao.getInstance(context).delete(true);
    }
}
