package com.fuexpress.kr.utils;

import android.content.Context;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Created by andy on 2016/12/12.
 */
public class MyNumberFormat {

    public static String format(float price, int leng) {
        String formatprice;
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setMinimumFractionDigits(leng);
        nf.setMaximumFractionDigits(leng);
        nf.setRoundingMode(RoundingMode.DOWN);
        nf.setGroupingUsed(true);
        formatprice = nf.format(price);

        return formatprice;
    }
}
