package com.fuexpress.kr.model;

import android.content.Context;
import android.text.TextUtils;

import com.fuexpress.kr.R;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.LogUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fksproto.CsMy;
import fksproto.CsUser;

/**
 * Created by andy on 2017/4/5.
 */
public class PayMethodManager {

    private List<CsUser.PaymentList> mPaymantlistList = new ArrayList<>();
    private static Context mContext;
    private float mFreeBalance;
    private boolean mFreshCoupon;
    private int mCouponCount;
    private float mNeedPay;
    private String payMethodName = "";

    public interface PayMethodResultListener {
        void onResult(String payCode, String result);

        void onMethodList(List<CsUser.PaymentList> paymantlistList, String balance, int couponCount);
    }

    public static PayMethodManager instance = new PayMethodManager();
    public static boolean use_balance = true;
    public static String currentCurrencyCode;
    public static CsUser.CouponList currentCoupon;
    public static CsUser.PaymentList currentPayMethod;

    //    public static String currentPayMethod;
    boolean freshBalance;
    boolean freshMethodList;

    public static PayMethodManager getInstance(Context context) {
        mContext = context;
        return instance;
    }

    public String getPayMethodName() {
        return payMethodName;
    }

    public void setPayMethodName(String payMethodName) {
        this.payMethodName = payMethodName;
    }

    public void reSet() {
        freshBalance = false;
        freshMethodList = false;
        currentCoupon = null;
    }

    public CsUser.CouponList getCurrentCoupon() {
        return currentCoupon;
    }

    public void setCurrentCoupon(CsUser.CouponList currentCoupon) {
        PayMethodManager.currentCoupon = currentCoupon;
    }

    public float getFreeBalance() {
        return mFreeBalance;
    }

    public int getMethodIndex() {
        return getNeedPay() > 0 && methodIndex < 0 ? 0 : methodIndex;
//        return methodIndex;
    }

    public void setMethodIndex(int index) {
        methodIndex = index;
        //        return methodIndex;
    }


    public boolean isUseBalance() {
        return use_balance;
    }

    public void setUseBalance(boolean use) {
        use_balance = use;
    }

    public void setCurrentMethod(int index) {
        if (index < mPaymantlistList.size() && index > -1) {
            methodIndex = index;
            currentPayMethod = mPaymantlistList.get(index);
        } else {
            currentPayMethod = null;
            methodIndex = index;
        }
    }

    public float getNeedPay() {
        return mNeedPay > 0 ? mNeedPay : 0;
    }

    public void setFreshBalance(boolean isFresh) {
        this.freshBalance = isFresh;
    }

    public String getCurrentPayCode() {
        if (currentPayMethod != null)
            return currentPayMethod.getPaycode();
        return "";
    }

    public void getCurrentPayMethod(PayMethodResultListener listener, float payCount, String currencyCode) {
        showMethodList = false;
        judgeCurrency(currencyCode);
        processResult(listener, payCount, currencyCode);
        if (!freshMethodList) {
            getFKDPaymentListRequest(listener, payCount, currencyCode);
        }
        if (!freshBalance) {
            getAccountBalanceRequest(listener, payCount, currencyCode);
        }
    }

    private boolean showMethodList;

    public void getPayMethodList(PayMethodResultListener listener, float payCount, String currencyCode) {
        getPayMethodList(listener, payCount, currencyCode, true);
    }

    public void getPayMethodList(PayMethodResultListener listener, float payCount, String currencyCode, boolean useCoupon) {
        showMethodList = true;
        mFreshCoupon = !useCoupon;
        judgeCurrency(currencyCode);
        processResult(listener, 0, currencyCode);
        if (!freshMethodList) {
            getFKDPaymentListRequest(listener, 0, currencyCode);
        }
        if (!freshBalance) {
            getAccountBalanceRequest(listener, 0, currencyCode);
        }
        if (useCoupon) {
            useableCouponListRequest(listener, currencyCode, payCount);
        }
    }

    private void processResult(PayMethodResultListener listener, float payCount, String currencyCode) {
        mNeedPay = payCount;
        boolean getCroup = !showMethodList || mFreshCoupon;
        if (freshBalance && freshMethodList && getCroup) {
            String balance_text = mContext.getString(R.string.String_balance_first) + UIUtils.getCurrency(mContext, currencyCode, mFreeBalance);
            String payinfo = "";
            String payCode = "";
            if (currentPayMethod != null) {
                payMethodName = currentPayMethod.getPayname();
                payCode = currentPayMethod.getPaycode();
            }
            if (use_balance && mFreeBalance >= 0.5) {
                payinfo += mContext.getString(R.string.package_use_balance_first) + UIUtils.getCurrency(mContext, currencyCode, mFreeBalance) + "!";
                mNeedPay -= mFreeBalance;
            }
            if (currentCoupon != null) {
                payinfo += mContext.getString(R.string.pr_use) + currentCoupon.getShippingcouponname() + "：" + UIUtils.getCurrency(mContext, currencyCode, currentCoupon.getDiscountamount()) + "!";
                mNeedPay -= currentCoupon.getDiscountamount();
            }
            mNeedPay = getNumber(currencyCode, mNeedPay);

          /*  if (TextUtils.equals(payCode, "adyen")) {
                payMethodName = mContext.getString(R.string.String_adyen_pay);
            }
            if (TextUtils.equals(payCode, "krbank")) {
                payMethodName = mContext.getString(R.string.String_krbank_pay2);
            }
            if (TextUtils.equals(payCode, "wxap")) {
                payMethodName = mContext.getString(R.string.String_weixin_pay);
            }
            if (TextUtils.equals(payCode, "alipay")) {
                payMethodName = mContext.getString(R.string.String_ali_pay);
            }
            if (TextUtils.equals(payCode, Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
                payMethodName = currentPayMethod.getPayname();
            }*/
            if (mNeedPay > 0) {
                payinfo += mContext.getString(R.string.String_pure_pay, payMethodName,
                        UIUtils.getCurrency(mContext, currencyCode, UIUtils.formatNumber(currencyCode, mNeedPay)));
            }
            String[] split = payinfo.split("!");
            String result = "";
            for (int i = 0; i < split.length; i++) {
                result += split[i];
                if (i < split.length - 1) result += "\n";
            }
            listener.onResult(payCode, result);
            listener.onMethodList(mPaymantlistList, balance_text, mCouponCount);
        }
    }

    private void judgeCurrency(String currencyCode) {
        if (!currencyCode.equals(currentCurrencyCode)) {
            freshBalance = false;
            freshMethodList = false;
        }
    }


    private void getAccountBalanceRequest(final PayMethodResultListener listener, final float payCount, final String currencyCode) {
        CsMy.GetAccountBalanceRequest.Builder builder = CsMy.GetAccountBalanceRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(currencyCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsMy.GetAccountBalanceResponse>() {

            @Override
            public void onSuccess(final CsMy.GetAccountBalanceResponse response) {
                freshBalance = true;
//                LogUtils.d(response.toString());
                mFreeBalance = response.getFreeBalance();
                currentCurrencyCode = currencyCode;
                processResult(listener, payCount, currencyCode);
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    int methodIndex;

    private void getFKDPaymentListRequest(final PayMethodResultListener listener, final float payCount, final String currencyCode) {
        CsUser.GetFKDPaymentListRequest.Builder builder = CsUser.GetFKDPaymentListRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(currencyCode);
//        builder.setCurrencycode("cccc");
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetFKDPaymentListResponse>() {

            @Override
            public void onSuccess(CsUser.GetFKDPaymentListResponse response) {
                freshMethodList = true;
                LogUtils.d(response.toString());
                mPaymantlistList.clear();
                mPaymantlistList.addAll(response.getPaymantlistList());
                Collections.sort(mPaymantlistList, new Comparator<CsUser.PaymentList>() {
                    @Override
                    public int compare(CsUser.PaymentList lhs, CsUser.PaymentList rhs) {
                        int first = 0;
                        int second = 0;
                        String lhsSortorder = lhs.getSortorder();
                        if (!TextUtils.isEmpty(lhsSortorder)) first = Integer.valueOf(lhsSortorder);
                        String rhsSortorder = rhs.getSortorder();
                        if (!TextUtils.isEmpty(rhsSortorder))
                            second = Integer.valueOf(rhsSortorder);
                        return first - second;
                    }
                });
                methodIndex = methodIndex < mPaymantlistList.size() ? methodIndex : 0;
                if (mPaymantlistList.size() > 0) {
                    currentPayMethod = mPaymantlistList.get(methodIndex);
                } else {
                    currentPayMethod = null;
                }
                currentCurrencyCode = currencyCode;
                processResult(listener, payCount, currencyCode);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                freshMethodList = true;
                mPaymantlistList.clear();
                currentPayMethod = null;
                currentCurrencyCode = currencyCode;
                processResult(listener, payCount, currencyCode);
            }
        });
    }

    public void useableCouponListRequest(final PayMethodResultListener listener, final String currencyCode, final float shippingFee) {
        CsUser.UseableCouponListRequest.Builder builder = CsUser.UseableCouponListRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(currencyCode);
        builder.setShippingfee(shippingFee);

        NetEngine.postRequest(builder, new INetEngineListener<CsUser.UseableCouponListResponse>() {

            @Override
            public void onSuccess(final CsUser.UseableCouponListResponse response) {
//                LogUtils.d(response.toString());
                mFreshCoupon = true;
                mCouponCount = response.getCount();
                processResult(listener, shippingFee, currencyCode);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mFreshCoupon = true;
                mCouponCount = 0;
                processResult(listener, shippingFee, currencyCode);
            }
        });
    }


    public static float getNumber(String currencyCode, float price) {
        BigDecimal d = new BigDecimal(price);
        if (currencyCode.contains("KRW")) {
            return d.setScale(0, BigDecimal.ROUND_UP).floatValue();
        } else if (currencyCode.contains("TWD")) {
            return d.setScale(0, BigDecimal.ROUND_UP).floatValue();
        } else {
            return d.setScale(2, BigDecimal.ROUND_UP).floatValue();
        }
    }


}
