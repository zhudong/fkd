package com.fuexpress.kr.ui.activity.package_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.IDinfoBean;
import com.fuexpress.kr.bean.ParcelItemBean;
import com.fuexpress.kr.bean.PayResultBaen;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.PayMethodManager;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.activity.AddressManagerActivity;
import com.fuexpress.kr.ui.activity.KrBankInfoActivity;
import com.fuexpress.kr.ui.activity.ParcelPayMethodActivity;
import com.fuexpress.kr.ui.activity.QueryShippingActivity;
import com.fuexpress.kr.ui.activity.append_item.JsonSerializer;
import com.fuexpress.kr.ui.activity.append_parcel.IdCardActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.utils.FloatUtils;
import com.fuexpress.kr.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsAddress;
import fksproto.CsBase;
import fksproto.CsMy;
import fksproto.CsParcel;
import fksproto.CsUser;
import okhttp3.Response;

/**
 * Created by yuan on 2016-6-16.
 */
public class PackageDeatilPresent extends PackageDetailContract.Presenter {

    private long mParcelID;
    private Handler mHandler = new Handler();
    private CsBase.Warehouse mWarehouse;
    private float mGiftcardaccount;
    public static boolean mIsUseBalance;
    private CsParcel.Parcel mParcel;
    private boolean mHasSelectShippingMethod;
    private int mParcelshippingmethodid;
    float fee;
    String currencyCode;
    private CsUser.CouponList mCoupon;
    private float shippingfee;
    private float total;
    private String mIdCardFrontImg;
    private String mIdCardBackImg;
    private String mIdCard;
    private boolean mNeedIdcard;
    private int addressID;
    private int methodID;
    private float mPureShippingFee;
    private float mPureFeeDuty;
    private CsParcel.ParcelShipping mShipping;
    private PayMethodManager mPayMethodManager;

    public OrderParcelUseCase getOrderParcelUseCase() {
        return mOrderParcelUseCase;
    }

    private OrderParcelUseCase mOrderParcelUseCase;


    public PackageDeatilPresent(long parcelID) {
        this.mParcelID = parcelID;
    }

    @Override
    public void onStart() {
        mOrderParcelUseCase = new OrderParcelUseCase(context, this);
        mPayMethodManager = PayMethodManager.getInstance(context);
        mPayMethodManager.setFreshBalance(false);
        getParcelDetail(mParcelID);
    }

    public void setCouponsAndCalc(boolean freshCoupon) {
        if (!freshCoupon) mPayMethodManager.setCurrentCoupon(null);
        calc(selectShippingRes);
    }


    @Override
    public boolean checkIdCard() {
        return !TextUtils.isEmpty(mIdCard);
    }

    @Override
    public float getShippingfee() {
        return mPureFeeDuty;
    }


    @Override
    public void toPay() {

        if (!mHasSelectShippingMethod) {
            CustomToast.makeText(context, R.string.package_please_select_method, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(usedPayCode)) {
            CustomToast.makeText(context, context.getString(R.string.please_select_pay_method), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mParcel.getType() == CsParcel.ParcelType.PARCEL_TYPE_ORDER_VALUE) {
            String tIdCard = null;
            if (mNeedIdcard) {
                tIdCard = mIdCard;
            }
            mOrderParcelUseCase.submitParcelNotice(mPureFeeDuty, mGiftcardaccount, shippingfee, mParcel.getParcelId(), addressID, methodID, mParcel.getCurrencycode(), tIdCard, mIdCardFrontImg, mIdCardBackImg, mCoupon, mPureShippingFee);
        } else {
            checkBalance();
        }
    }

    @Override
    public void switchPayType() {
        Intent intent = new Intent(context, ParcelPayMethodActivity.class);
        intent.putExtra(ParcelPayMethodActivity.CURRENCY_CODE, currencyCode);
        intent.putExtra(ParcelPayMethodActivity.SHIPPING_FEE, shippingfee);
        context.startActivityForResult(intent, 0);
    }

    @Override
    public void choiceAddress() {
        Intent intent = new Intent(context, AddressManagerActivity.class);
        intent.putExtra(AddressManagerActivity.BACK_TITLE, context.getString(R.string.string_package_detail_back));
        intent.putExtra(AddressManagerActivity.KEY_IS_CHOOSE_TYPE, true);//根据状态判断是否点击结束页面
        ((Activity) mView).startActivityForResult(intent, Constants.ADDRESS_REQUEST_CODE);
    }

    public CsParcel.SelectShippingMethodReponse selectShippingRes;

    @Override
    public void selectShippingMethod(final CsParcel.MerchantParcelShippingMethodList method) {
        this.methodID = method.getParcelshippingmethodid();
        mHasSelectShippingMethod = false;
        mNeedIdcard = method.getIsneedidcard() == 1;// TODO: 2016/12/20 正式时候要改成1
        CsParcel.SelectShippingMethodRequest.Builder builder = CsParcel.SelectShippingMethodRequest.newBuilder().
                setParcelid((int) mParcelID).setSecond(AccountManager.getInstance().mBaseUserRequest).
                setParcelshippingmethodid(method.getParcelshippingmethodid()).setIsNewVersion(1).setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.SelectShippingMethodReponse>() {

            @Override
            public void onSuccess(final CsParcel.SelectShippingMethodReponse response) {
                mHasSelectShippingMethod = true;
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        selectShippingRes = response;
                        mParcelshippingmethodid = method.getParcelshippingmethodid();
                        mView.showShippingMethodInsurance(response.getValueAlert(), response.getMaxdeclaredvalue(), response.getDeclaredtotal(), response.getPremiumrate(), response.getPremium(), mParcelshippingmethodid, response);
                        mGiftcardaccount = response.getGiftcardaccount();
                        shippingfee = response.getShippingfee();
                        mPureShippingFee = shippingfee - response.getShippingduty() - response.getProductduty() - response.getPremium();
                        setCouponsAndCalc(false);
                        mView.showInsurance(method);
                        mView.showEstimatePrice(shippingfee);
                        fee = response.getNeedpay();
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        CustomToast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                        String toastMsg = errMsg;
                        String[] split = errMsg.replace("|", "@").split("@");
                        int type = 0;
                        if (split.length >= 2) {
                            toastMsg = split[0];
                            if (!TextUtils.isEmpty(split[1])) {
                                type = Integer.valueOf(split[1]);
                            }
                        }
                        if (mParcel.getType() == CsParcel.ParcelType.PARCEL_TYPE_TRANSPORT_VALUE)
                            type = -1;
                        mView.showParcleDialog(toastMsg, type, context.getString(R.string.cancel));
                    }
                });
            }
        });
    }

    String usedPayCode;

    private void calc(CsParcel.SelectShippingMethodReponse response) {
        if (response == null) return;
        mPureFeeDuty = UIUtils.formatNumber(mParcel.getCurrencycode(), response.getShippingfee());
        mPayMethodManager.getCurrentPayMethod(new PayMethodManager.PayMethodResultListener() {
            @Override
            public void onResult(String payCode, String result) {
                mView.showBalanceAndPayType(result);
                usedPayCode = payCode;
            }

            @Override
            public void onMethodList(List<CsUser.PaymentList> paymantlistList, String balance, int couponCount) {

            }
        }, mPureFeeDuty, currencyCode);

    }


    public CsParcel.Parcel getParcel() {
        return mParcel;
    }


    void getParcelDetail(long parcelId) {
//        parcelId = 80706;
        CsParcel.GetParcelDetailRequest.Builder builder = CsParcel.GetParcelDetailRequest.newBuilder();
        builder.setBaseuser(AccountManager.getInstance().mBaseUserRequest).setParcelId(parcelId).setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.GetParcelDetailResponse>() {

            @Override
            public void onSuccess(final CsParcel.GetParcelDetailResponse response) {
                mParcel = response.getParcel();
                mParcelName = mParcel.getParcelNumber();
                mOrderParcelUseCase.setParcelName(mParcelName);
                currencyCode = mParcel.getCurrencycode();
                final CsAddress.CustomerAddress address = response.getAddress();
                addressID = address.getAddressId();
                mShipping = response.getShipping();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.showTitle(mParcel);
                        mWarehouse = response.getWarehouse();
                        mView.showComment(response.getMessagesList());

                        mView.showProductInfo(mParcel.getQty(), mShipping.getDeclaration());
                        showItem(response.getParcelitemlistList());

                        switch (mParcel.getState()) {
                           /* PARCEL_STATE_SUBMITTED     = 0;//已提交
                            PARCEL_STATE_PAYED         = 1;//待入库
                            PARCEL_STATE_INSTORED      = 2;//已入库
                            PARCEL_STATE_NOTICED       = 3;//待出库
                            PARCEL_STATE_CHECKED       = 4;//已审核
                            PARCEL_STATE_SHIPPED       = 5;//已发货*/
                            case CsParcel.ParcelState.PARCEL_STATE_PAYED_VALUE:
                            case CsParcel.ParcelState.PARCEL_STATE_CHECKED_VALUE:
                            case CsParcel.ParcelState.PARCEL_STATE_NOTICED_VALUE:
                                mView.showHeader(mParcel, mWarehouse.getName(), false);
                                mView.setTraceVisibility(false, response.getParcelitemlistList());

                                mView.hintHeader();
                                mView.showParcelAddress(response.getAddress());
                                mView.showComment(response.getMessagesList());
                                showShipping(mShipping, 1);
                                break;

                            case CsParcel.ParcelState.PARCEL_STATE_SHIPPED_VALUE:
                                mView.showHeader(mParcel, mWarehouse.getName(), true);
                                showShipping(mShipping, 0);
                                boolean shippingNumber = mShipping.getShippingNumbersList().size() > 0;
                                mView.setTraceVisibility(shippingNumber, response.getParcelitemlistList());
                                mView.hintHeader();
                                mView.showParcelAddress(response.getAddress());
                                mView.showComment(response.getMessagesList());
                                break;

                            case CsParcel.ParcelState.PARCEL_STATE_SUBMITTED_VALUE:
                            case CsParcel.ParcelState.PARCEL_STATE_INSTORED_VALUE:
                                mView.showHeader(mParcel, mWarehouse.getName(), false);
                                mView.setTraceVisibility(false, response.getParcelitemlistList());
                                mView.hitFoot();
                                mView.showEstimateWeight(response.getShipping().getWeight());
                                mView.showShippingMethods(response.getMerchantparcelshippingmethodlistList());
                                showAddress(address);
                                mView.showIdNumber(!"".endsWith(address.getIdCard()), address.getIdCard());
                                mIdCardBackImg = address.getIdcardbackimage();
                                mIdCardFrontImg = address.getIdcardfrontimage();
                                mIdCard = address.getIdCard();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showItem(List<CsParcel.ParcelItemList> parcelitemlistList) {
        List<ParcelItemBean> list = new ArrayList<>();
        for (CsParcel.ParcelItemList itemList : parcelitemlistList) {
            ParcelItemBean itemBean = new ParcelItemBean();
            itemBean.titel = itemList.getName();
            itemBean.img = itemList.getImageurl();
            itemBean.count = itemList.getQty();
            itemBean.price = UIUtils.getCurrency(context, itemList.getPriceCurrencyCode(), itemList.getPrice());
            itemBean.needNumber = itemList.getParcelitemid();
            itemBean.remark = itemList.getRemark();
            itemBean.status = itemList.getStatus();
            itemBean.salesOrderId = itemList.getSalesOrderId();
            itemBean.orderNumber = itemList.getOrderNumber();
            itemBean.matchItemId = itemList.getMatchItemId();
            if (!TextUtils.isEmpty(itemList.getType())) {
                itemBean.type = Integer.valueOf(itemList.getType());
            }
            itemBean.message = itemList.getMessage();
            list.add(itemBean);
        }
        mView.setParcelsItme(list, mParcel.getType() == CsParcel.ParcelType.PARCEL_TYPE_ORDER_VALUE | mParcel.getType() == CsParcel.ParcelType.PARCEL_TYPE_DIRECT_VALUE);
    }


    @Override
    public void inputIdNumber() {
        Intent intent = new Intent(context, IdCardActivity.class);
        IDinfoBean bean = new IDinfoBean();
        bean.setServerIDNumber(mIdCard);
        bean.setServerUrlBack(mIdCardBackImg);
        bean.setServerUrlFront(mIdCardFrontImg);
        String s = new JsonSerializer().serializeIDinfo(bean);
        intent.putExtra(IdCardActivity.ID_CARD_BEAN, s);
        context.startActivityForResult(intent, 11);
        context.overridePendingTransition(R.anim.activity_translate_x_in, R.anim.activity_translate_x_out);
    }


    @Override
    public void setIdInfo(String idCardNumber, String idCardFront, String idCardBack) {
        mIdCard = idCardNumber;
        mIdCardFrontImg = idCardFront;
        mIdCardBackImg = idCardBack;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public void setAddress(String topText, String addressText, int id) {
        showShippingMethod(id + "", mWarehouse);
//        mView.showCustomerAddress(topText, addressText, id);
        showAddress(topText, addressText, id);
    }


    private void showAddress(CsAddress.CustomerAddress address) {
        this.addressID = address.getAddressId();
        mView.showCustomerAddress(address);

    }

    private void showAddress(String topText, String addressText, int id) {
        this.addressID = id;
        mView.showCustomerAddress(topText, addressText, id);
    }


    private void showShippingMethod(String addressID, CsBase.Warehouse warehouse) {
        this.addressID = Integer.valueOf(addressID);
        CsParcel.GetSelectAddressAjaxRequest.Builder builder = CsParcel.GetSelectAddressAjaxRequest.newBuilder()
                .setWarehouseid(warehouse.getWarehouseId() + "")
                .setCustomeraddressid(addressID)
                .setCurrencyCode(mParcel.getCurrencycode())
                .setParcelid((int) mParcel.getParcelId()).setEstimateweight(mParcel.getWeight() + "")
                .setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.GetSelectAddressAjaxReponse>() {

            @Override
            public void onSuccess(final CsParcel.GetSelectAddressAjaxReponse response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<CsParcel.MerchantParcelShippingMethodList> methodLists = response.getMerchantparcelshippintmethodlistList();
                        mView.showShippingMethods(methodLists);
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                LogUtils.d(errMsg);
            }
        });

    }

    private void checkBalance() {
        context.showLoading();
        CsMy.CheckToPayRequest.Builder builder = CsMy.CheckToPayRequest.newBuilder();
        builder.setBaseuser(AccountManager.getInstance()
                .getBaseUserRequest())
//                .setPaymentcode(usedPayCode)
                .setParcelid(mParcelID + "")
                .setShippingmethodid(mParcelshippingmethodid)
                .setCustomeraddressid(addressID).setIsNewVersion(1)
                .setUseaccountpay(mPayMethodManager.isUseBalance() ? 1 : 2);//2：不使用余额 0,1：使用余额
        if (mPayMethodManager.getMethodIndex() != -1) builder.setPaymentcode(usedPayCode);
        CsUser.CouponList currentCoupon = mPayMethodManager.getCurrentCoupon();
        if (currentCoupon != null) {
            builder.setShippingcouponcustomerid(currentCoupon.getShippingcouponcustomerid());
        } else {
            builder.setShippingcouponcustomerid(0);
        }

        if (mNeedIdcard) {
            builder.setIdcard(mIdCard)
                    .setIdcardbackimage(mIdCardBackImg)
                    .setIdcardfrontimage(mIdCardFrontImg);
        }

        NetEngine.postRequest(builder, new INetEngineListener<CsMy.CheckToPayResponse>() {

            @Override
            public void onSuccess(final CsMy.CheckToPayResponse response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String redirecturl = response.getRedirecturl();
                        if ("".equals(redirecturl)) {
//                            直接扣除余额
//                            mView.finishView();
                            context.closeLoading();
                            submitSucess(true);
                        } else {
//                            第三方支付
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
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                LogUtils.d(errMsg);
                mHandler.post(new Runnable() {
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
//        type=4&appType=3&paymentCode=alipay&salesRequireMainId=452&total=56.84
        CsParcel.PayDoDirectGiftCardAppSingleRequest.Builder builder = CsParcel.PayDoDirectGiftCardAppSingleRequest.newBuilder();
        int type = "".equals(map.get("type")) ? 0 : Integer.valueOf(map.get("type"));
        int appType = "".equals(map.get("appType")) ? 0 : Integer.valueOf(map.get("appType"));
        String paymentCode = map.get("paymentCode");
        total = "".equals(map.get("total")) ? 0 : FloatUtils.vlaueOf(map.get("total"));
//        total = 0.1f;
        float estimate = "".equals(map.get("estimateShippingFee")) ? 0 : FloatUtils.vlaueOf(map.get("estimateShippingFee"));
        builder.setType(type)
                .setAppType(appType)
                .setPaymentCode(paymentCode)
                .setTotal(total)
                .setUserinfo(AccountManager.getInstance().mBaseUserRequest)
                .setParcelid((int) mParcelID)
                .setEstimateshippingfee(estimate)
                .setCurrencycode(mParcel.getCurrencycode())
                .setShippingfee(mPureShippingFee);
        CsUser.CouponList currentCoupon = mPayMethodManager.getCurrentCoupon();
        if (currentCoupon != null) {
            builder.setShippingcouponcustomerid(currentCoupon.getShippingcouponcustomerid());
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayDoDirectGiftCardAppSingleResponse>() {

            @Override
            public void onSuccess(CsParcel.PayDoDirectGiftCardAppSingleResponse response) {
                getPayInfo(response.getNumber(), response.getOrderId(), response.getPaymentCode());
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    private void getPayInfo(final String number, final int orderCode, final String payCode) {
        final PaymentManager paymentManager = PaymentManager.getInstance(context);
        if ("krbank".equals(payCode)) {
            Intent intent = new Intent(context, KrBankInfoActivity.class);
            intent.putExtra(KrBankInfoActivity.NEED_PAY_COUNT, mView.getPureFee());
            intent.putExtra(KrBankInfoActivity.SHIPPING_FEE_TOTAL, shippingfee);
            intent.putExtra(KrBankInfoActivity.BALANCE, mGiftcardaccount);
            intent.putExtra(KrBankInfoActivity.PAYMENT, context.getString(R.string.String_krbank_pay2));
            intent.putExtra(KrBankInfoActivity.PAY_CURRENCY_CODE, mParcel.getCurrencycode());
            context.startActivity(intent);
        } else if ("adyen".equals(payCode)) {
            paymentManager.adyenPay(mPayMethodManager.getPayMethodName(), number, orderCode, 1, total, mParcel.getCurrencycode(), new PaymentManager.OnPayListener() {
                @Override
                public void onPay(Response response, final PayResultBaen payResult) {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            if ("Authorised".equals(payResult.resultCode) && !"".equals(payResult.authCode)) {
                                submitSucess(true);
                            }
                        }
                    });
                }

                @Override
                public void onError(final String errMsg) {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            submitSucess(false);
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
            paymentManager.daouPay(context, total, orderCode, 1, new OperaRequestListener() {
                @Override
                public void onOperaSuccess() {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            submitSucess(true);
                        }

                    });
                }

                @Override
                public void onOperaFailure() {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            submitSucess(false);
//                                    CustomToast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        if (!("alipay".equals(payCode) | "wxap".equals(payCode))) return;

        CsParcel.PayGiftCardOrderRequest.Builder builder = CsParcel.PayGiftCardOrderRequest.newBuilder();
        builder.setPaymentCode(payCode).setGiftCardOrderId(orderCode).setCurrencycode(mParcel.getCurrencycode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayGiftCardOrderResponse>() {

            @Override
            public void onSuccess(CsParcel.PayGiftCardOrderResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        context.closeLoading();
                    }
                });

                LogUtils.d(response.toString());
                SysApplication.mCurrentRequestPayment = mParcelName;
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

    public void traceShiping() {
        Intent intent = new Intent(context, QueryShippingActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra(QueryShippingActivity.SHIPPINGS, bundle);
        bundle.putSerializable(QueryShippingActivity.SHIPPINGS, mShipping);
        context.startActivity(intent);
    }


    private void showShipping(CsParcel.ParcelShipping shipping, int i) {
        String[] split = shipping.getShippingMethodInfo().split("\\s+");
        String detail = "";
        String name = "";
        if (split.length == 1) {
            name = split[0];
        } else if (split.length == 2) {
            name = split[0];
            detail = split[1];
        } else if (split.length == 3) {
            name = split[0] + " " + split[1];
            detail = split[2];
        } else if (split.length == 4) {
            name = split[1] + " " + split[2];
            detail = split[3];
        } else if (split.length > 4) {
            name = split[1] + " " + split[2];
            detail = split[3];
        }
        Activity content = (Activity) mView;
        if (1 == i) {
            detail += "\n" + content.getString(R.string.package_forcecast_shipping_fee) + UIUtils.getCurrency(content, mParcel.getCurrencycode(), shipping.getEstimateShippingFee());
            if (mParcel.getState() != CsParcel.ParcelState.PARCEL_STATE_PAYED_VALUE) {
                detail += "\n" + content.getString(R.string.package_forcecast_weight) + content.getString(R.string.package_weight, shipping.getWeight());
            }
        } else {
            detail += "\n" + content.getString(R.string.package_realy_shipping_fee) + UIUtils.getCurrency(content, mParcel.getCurrencycode(), shipping.getRealShippingFee())
                    + "\n" + content.getString(R.string.package_realy_weight) + content.getString(R.string.package_weight, shipping.getWeight());
        }
        mView.showTransportInfo(name, detail);
    }

    @Override
    protected void submitSucess(boolean sucess) {
        if (sucess) {
            Intent intent = new Intent(context, ParcelToSendActivity.class);
            intent.putExtra(ParcelToSendActivity.TITLE, mParcel.getParcelNumber());
            context.startActivity(intent);
            context.finish();
        }
    }
}
