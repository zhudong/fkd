package com.fuexpress.kr.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.RadiusBackgroundSpan;

import fksproto.CsCart;
import fksproto.CsCrowd;

/**
 * Created by Administrator on 2017-9-18.
 */

public class TextSpannable {
    public static void setTitle(String title, TextView titleTv, CsCart.SalesCartItem item) {
        if (item.getCrowdOrderId() > 0) {
            titleTv.setTextColor(Color.WHITE);
            title += " ";
            String source = title + UIUtils.getString(R.string.crowd_);
            SpannableString spanString = new SpannableString(source);
            spanString.setSpan(new RadiusBackgroundSpan(UIUtils.getColor(R.color.the_red), UIUtils.dip2px(6f)),
                    title.length(), source.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spanString.setSpan(new AbsoluteSizeSpan(10, true),
                    title.length(), source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString.setSpan(new ForegroundColorSpan(Color.BLACK),
                    0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleTv.setText(spanString);
        }else {
            titleTv.setText(title);
            titleTv.setTextColor(Color.BLACK);
        }

    }
}
