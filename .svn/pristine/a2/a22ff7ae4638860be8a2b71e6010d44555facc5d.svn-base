package com.fuexpress.kr.ui.activity.append_parcel;

import android.content.Context;

import com.fuexpress.kr.model.AccountManager;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Properties;

/**
 * Created by andy on 2016/12/21.
 */
public class NumberFormate {

    public static String getCurrency(Context context, String currencyCode, float price) {
        String formatprice;
        if (currencyCode.contains("KRW")) {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(0);
            nf.setRoundingMode(RoundingMode.HALF_UP);
            nf.setGroupingUsed(true);
            formatprice = nf.format(price);
        } else if (currencyCode.contains("TWD")) {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(0);
            nf.setRoundingMode(RoundingMode.HALF_UP);
            nf.setGroupingUsed(true);
            formatprice = nf.format(price);
        } else {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);
            nf.setRoundingMode(RoundingMode.HALF_UP);
            nf.setGroupingUsed(true);
            formatprice = nf.format(price).replace(",","");
        }
        return formatprice;
    }

    public static float getNumber(float price) {
        String currencyCode = AccountManager.getInstance().getCurrencyCode();
        return getNumber(currencyCode, price);
    }

    public static float getNumber(String currencyCode, float price) {
        BigDecimal d = new BigDecimal(price);
        if (currencyCode.contains("KRW")) {
            return d.setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
        } else if (currencyCode.contains("TWD")) {
            return d.setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
        } else {
            return d.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        }
    }
}
