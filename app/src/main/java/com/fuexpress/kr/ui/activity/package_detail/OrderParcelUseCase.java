package com.fuexpress.kr.ui.activity.package_detail;

import android.content.Intent;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.PayResultBaen;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.PayMethodManager;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.activity.KrBankInfoActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;

import fksproto.CsMy;
import fksproto.CsParcel;
import fksproto.CsUser;
import okhttp3.Response;

/**
 * Created by andy on 2016/12/23.
 */
public class OrderParcelUseCase {


    private final BaseActivity mContext;
    private final PayMethodManager mPayMethodManager;
    private PackageDeatilPresent mPresent;
    private float mTotalFee;
    private long parcelID;
    private int addressID;
    private int shippingID;
    private String currencyCode;
    private String idCard;
    private String cardFrontImg;
    private String cardBackImg;
    private CsUser.CouponList coupon;
    private float mNeedPay;
    private String parcelName;
    private float giftcardaccount;


    public OrderParcelUseCase(BaseActivity context, PackageDeatilPresent present) {
        this.mContext = context;
        this.mPresent = present;
        mPayMethodManager = PayMethodManager.getInstance(context);
    }

    public String getParcelName() {
        return parcelName;
    }

    public void setParcelName(String parcelName) {
        this.parcelName = parcelName;
    }

  /*  //提交包裹发货通知
    message SubmitParcelNoticeRequest
    {
        required BaseRequest        head                = 1;
        optional BaseUserRequest    second              = 2;
        required int64              parcel_id           = 3;//包裹id
        optional int32              shipping_address_id = 4;//包裹邮寄地址
        optional int32              shipping_method_id  = 5;//物流方式
        optional string             idcard              = 6;//身份证
        optional string             localecode          = 7;//语种
        optional string             currencycode        = 8;//币种
        optional int32              currencyid          = 9;//币种id
        optional string             idcardfrontimage    = 10;//身份证正面图片
        optional string             idcardbackimage     = 11;//身份证反面图片
        optional string             shippingcouponcustomerid = 12;//优惠券id

    }*/

    public void submitParcelNotice(long parcelID, int addressID, int shippingID, String currencyCode, String idCard, String cardFrontImg, String cardBackImg, CsUser.CouponList coupon) {

        CsParcel.SubmitParcelNoticeRequest.Builder builder = CsParcel.SubmitParcelNoticeRequest.newBuilder();
        builder.setSecond(AccountManager.getInstance().getBaseUserRequest())
                .setParcelId(parcelID)
                .setShippingAddressId(addressID)
                .setShippingMethodId(shippingID)
                .setCurrencycode(currencyCode)
                .setLocalecode(AccountManager.getInstance().getLocaleCode());
        if (idCard != null) {
            builder.setIdcard(idCard)
                    .setIdcardfrontimage(cardFrontImg)
                    .setIdcardbackimage(cardBackImg);
        }

        CsUser.CouponList currentCoupon = mPayMethodManager.getCurrentCoupon();
        if (currentCoupon != null) {
            builder.setShippingcouponcustomerid(currentCoupon.getShippingcouponcustomerid() + "");
        }

        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.SubmitParcelNoticeResponse>() {

            @Override
            public void onSuccess(CsParcel.SubmitParcelNoticeResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mContext.closeLoading();
                        mPresent.submitSucess(true);
                        CustomToast.makeText(mContext, "sucess", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(final int ret, final String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mPresent.submitSucess(false);
                        mContext.closeLoading();
                        CustomToast.makeText(mContext, /*"fail ：" + CommonUtils.getErrMsg(ret) +*/ errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void submitParcelNotice(float fee, float giftcardaccount, float estimateshippingfee, long parcelID, int addressID, int shippingID, String currencyCode, String idCard, String cardFrontImg, String cardBackImg, CsUser.CouponList coupon, float pureFee) {
        mContext.showLoading();
        mTotalFee = fee;
        this.parcelID = parcelID;
        this.addressID = addressID;
        this.giftcardaccount = giftcardaccount;
//        this.estimateShippingFee = estimateshippingfee;
        this.shippingID = shippingID;
        this.currencyCode = currencyCode;
        this.idCard = idCard;
        this.cardFrontImg = cardFrontImg;
        this.cardBackImg = cardBackImg;
        this.coupon = mPayMethodManager.getCurrentCoupon();


        mNeedPay = mPayMethodManager.getNeedPay();
       /* if (coupon != null) {
            mTotalFee -= coupon.getDiscountamount();
            mNeedPay -= coupon.getDiscountamount();
        }
        if(PackageDeatilPresent.mIsUseBalance){
            mNeedPay -= giftcardaccount;
        }

        mNeedPay = UIUtils.formatNumber(currencyCode, mNeedPay);*/
        if (mNeedPay > 0) {
            doDirectPay(estimateshippingfee, (int) parcelID, mNeedPay, currencyCode, coupon, pureFee);
        } else {
            submitParcelNotice(parcelID, addressID, shippingID, currencyCode, idCard, cardFrontImg, cardBackImg, coupon);
        }
    }

    private void doDirectPay(final float shippingFee, int parcelID, float total, final String currencyCode, CsUser.CouponList coupon, float pureFee) {
/*

        String payCode = "";
        switch (mPayType) {
            case 1:
                payCode = "alipay";
                break;
            case 2:
                payCode = "wxap";
                break;
            case 4:
                payCode = "adyen";
                break;
            case 5:
                payCode = "krbank";
                break;
        }

*/

        CsParcel.PayDoDirectGiftCardAppSingleRequest.Builder builder = CsParcel.PayDoDirectGiftCardAppSingleRequest.newBuilder();
        int type = 2;
        int appType = 3;
        final String paymentCode = mPayMethodManager.getCurrentPayCode();
        builder.setType(type)
                .setAppType(appType)
                .setPaymentCode(paymentCode)
                .setTotal(total)
                .setUserinfo(AccountManager.getInstance().mBaseUserRequest)
                .setParcelid(parcelID)
                .setEstimateshippingfee(shippingFee)
                .setCurrencycode(currencyCode)
                .setShippingfee(pureFee);
        CsUser.CouponList currentCoupon = mPayMethodManager.getCurrentCoupon();
        if (currentCoupon != null) {
            builder.setShippingcouponcustomerid(currentCoupon.getShippingcouponcustomerid());
        }

        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayDoDirectGiftCardAppSingleResponse>() {

            @Override
            public void onSuccess(CsParcel.PayDoDirectGiftCardAppSingleResponse response) {
                getPayInfo(shippingFee, mNeedPay, response.getOrderId(), paymentCode, currencyCode);
//                getBalance();
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    private void getPayInfo(final float shippingFee, final float needPay, final int orderCode, final String payCode, final String currencyCode) {

        final PaymentManager paymentManager = PaymentManager.getInstance(mContext);
        if ("krbank".equals(payCode)) {
            Intent intent = new Intent(mContext, KrBankInfoActivity.class);
            intent.putExtra(KrBankInfoActivity.NEED_PAY_COUNT, needPay);
            intent.putExtra(KrBankInfoActivity.SHIPPING_FEE_TOTAL, shippingFee);
            intent.putExtra(KrBankInfoActivity.BALANCE, giftcardaccount);
            intent.putExtra(KrBankInfoActivity.PAYMENT, mContext.getString(R.string.String_krbank_pay2));
            intent.putExtra(KrBankInfoActivity.PAY_CURRENCY_CODE, mPresent.getParcel().getCurrencycode());
            mContext.startActivity(intent);
        } else if ("adyen".equals(payCode)) {
            paymentManager.adyenPay(mPayMethodManager.getPayMethodName(), parcelName, orderCode, 1, needPay, currencyCode, new PaymentManager.OnPayListener() {
                @Override
                public void onPay(Response response, final PayResultBaen payResult) {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            if ("Authorised".equals(payResult.resultCode) && !"".equals(payResult.authCode)) {
//                                        mPresent.submitSucess(true);
                                getBalance();
                            }
                        }
                    });
                }

                @Override
                public void onError(String errMsg) {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
//                                    mPresent.submitSucess(false);
                            Toast.makeText(mContext, mContext.getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else if (Constants.GIFT_CARD_PAYMENT_DAOUPAY.equals(payCode)) {
            paymentManager.daouPay(mContext, needPay, orderCode, 1, new OperaRequestListener() {
                @Override
                public void onOperaSuccess() {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            paySuccess(true);
                        }

                    });
                }

                @Override
                public void onOperaFailure() {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
//                                    Toast.makeText(mContext, mContext.getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
                            paySuccess(false);
                        }
                    });
                }
            });
        }


        if (!("alipay".equals(payCode) | "wxap".equals(payCode))) return;
        CsParcel.PayGiftCardOrderRequest.Builder builder = CsParcel.PayGiftCardOrderRequest.newBuilder();
        builder.setPaymentCode(payCode).setGiftCardOrderId(orderCode).setCurrencycode(currencyCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayGiftCardOrderResponse>() {

            @Override
            public void onSuccess(CsParcel.PayGiftCardOrderResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
//                        context.closeLoading();
                    }
                });
                SysApplication.mCurrentRequestPayment = parcelName;
                String payInfo = response.getPayInfo();
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

            }
        });
    }

    public void paySuccess(boolean success) {
        if (success) {
            getBalance();
        } else {
            mContext.closeLoading();
        }
    }


    public void getBalance() {
        CsMy.GetAccountBalanceRequest.Builder builder = CsMy.GetAccountBalanceRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsMy.GetAccountBalanceResponse>() {

            @Override
            public void onSuccess(final CsMy.GetAccountBalanceResponse response) {
                UIUtils.postTaskSafelyDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (response.getFreeBalance() >= mTotalFee) {
                            submitParcelNotice(parcelID, addressID, shippingID, currencyCode, idCard, cardFrontImg, cardBackImg, coupon);
                            return;
                        } else {
                            getBalance();
                        }
                    }
                }, 600);
            }

            @Override
            public void onFailure(int ret, String errMsg) {


            }
        });
    }

}