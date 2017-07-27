package com.fuexpress.kr.utils;

import android.content.Context;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Created by Administrator on 2017/4/29.
 */

public class RateUtil {

    public static String getCurrency(String fromCurrency,  String toCurrencyCode, double price) {
        int len = 2;
        if (price < 0.01) {
            len = 4;
        }
        if ("KRW".equals(toCurrencyCode)) {
            len = 1;
        }
        String formatprice;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(len);
        nf.setMaximumFractionDigits(len);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setGroupingUsed(true);
        formatprice = nf.format(price);
        return 1 + fromCurrency + " = " + formatprice + toCurrencyCode;
    }
}
