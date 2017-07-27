package com.fuexpress.kr.bean;

import java.io.Serializable;

/**
 * Created by kevin.xie on 2016/10/25.
 */

public class CouponCurrencyInfoData implements Serializable{
    public String currencyCode="$";
    public String currencyName="";
    public String currencySign="";
    public int count=0;

    public CouponCurrencyInfoData(String currencyCode, String currencyName, String currencySign, int count) {
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.currencySign = currencySign;
        this.count = count;
    }

    @Override
    public String toString() {
        return "CouponCurrencyInfoData{" +
                "currencyCode='" + currencyCode + '\'' +
                ", currencyName='" + currencyName + '\'' +
                ", currencySign='" + currencySign + '\'' +
                ", count=" + count +
                '}';
    }
    public String getCurrencyInfo(){
        return currencyCode;
    }
}
