package com.fuexpress.kr.ui.activity;


import com.fuexpress.kr.bean.AdyenUserInfoBean;
import com.fuexpress.kr.bean.CheckoutResponse;

/**
 * Created by Administrator on 2016/8/19.
 */
public interface AdyenCheckoutListener {
    void checkoutAuthorizedPayment(CheckoutResponse var1, boolean isChecked);

    void checkoutFailedWithError(String var1, int errorType);

    void checkoutFastPay(AdyenUserInfoBean adyenInfo, CheckoutResponse var1);
}