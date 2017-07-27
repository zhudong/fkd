package com.fuexpress.kr.utils;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fksproto.CsBase;

/**
 * Created by Longer on 2016/10/26.
 */
public class StringUtils {

    public final static String UTF_8 = "utf-8";

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断多个字符串是否相等，如果其中有一个为空字符串或者null，则返回false，只有全相等才返回true
     */
    public static boolean isEquals(String... agrs) {
        String last = null;
        for (int i = 0; i < agrs.length; i++) {
            String str = agrs[i];
            if (isEmpty(str)) {
                return false;
            }
            if (last != null && !str.equalsIgnoreCase(last)) {
                return false;
            }
            last = str;
        }
        return true;
    }

    /**
     * 返回一个高亮spannable
     *
     * @param content 文本内容
     * @param color   高亮颜色
     * @param start   起始位置
     * @param end     结束位置
     * @return 高亮spannable
     */
    public static CharSequence getHighLightText(String content, int color,
                                                int start, int end) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        start = start >= 0 ? start : 0;
        end = end <= content.length() ? end : content.length();
        SpannableString spannable = new SpannableString(content);
        CharacterStyle span = new ForegroundColorSpan(color);
        spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 获取链接样式的字符串，即字符串下面有下划线
     *
     * @param resId 文字资源
     * @return 返回链接样式的字符串
     */
    public static Spanned getHtmlStyleString(int resId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"\"><u><b>").append(UIUtils.getString(resId))
                .append(" </b></u></a>");
        return Html.fromHtml(sb.toString());
    }

    /**
     * 格式化文件大小，不保留末尾的0
     */
    public static String formatFileSize(long len) {
        return formatFileSize(len, false);
    }

    /**
     * 格式化文件大小，保留末尾的0，达到长度一致
     */
    public static String formatFileSize(long len, boolean keepZero) {
        String size;
        DecimalFormat formatKeepTwoZero = new DecimalFormat("#.00");
        DecimalFormat formatKeepOneZero = new DecimalFormat("#.0");
        if (len < 1024) {
            size = String.valueOf(len + "B");
        } else if (len < 10 * 1024) {
            // [0, 10KB)，保留两位小数
            size = String.valueOf(len * 100 / 1024 / (float) 100) + "KB";
        } else if (len < 100 * 1024) {
            // [10KB, 100KB)，保留一位小数
            size = String.valueOf(len * 10 / 1024 / (float) 10) + "KB";
        } else if (len < 1024 * 1024) {
            // [100KB, 1MB)，个位四舍五入
            size = String.valueOf(len / 1024) + "KB";
        } else if (len < 10 * 1024 * 1024) {
            // [1MB, 10MB)，保留两位小数
            if (keepZero) {
                size = String.valueOf(formatKeepTwoZero.format(len * 100 / 1024
                        / 1024 / (float) 100))
                        + "MB";
            } else {
                size = String.valueOf(len * 100 / 1024 / 1024 / (float) 100)
                        + "MB";
            }
        } else if (len < 100 * 1024 * 1024) {
            // [10MB, 100MB)，保留一位小数
            if (keepZero) {
                size = String.valueOf(formatKeepOneZero.format(len * 10 / 1024
                        / 1024 / (float) 10))
                        + "MB";
            } else {
                size = String.valueOf(len * 10 / 1024 / 1024 / (float) 10)
                        + "MB";
            }
        } else if (len < 1024 * 1024 * 1024) {
            // [100MB, 1GB)，个位四舍五入
            size = String.valueOf(len / 1024 / 1024) + "MB";
        } else {
            // [1GB, ...)，保留两位小数
            size = String.valueOf(len * 100 / 1024 / 1024 / 1024 / (float) 100)
                    + "GB";
        }
        return size;
    }

    /**
     * 把浮点型变成字符串类型的工具类:
     *
     * @param f 浮点数
     * @return 转换后的字符串
     */
    public static String changeFolatToString(float f) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(f);
        return p;
    }


    /**
     * 对字符串进行正则表达式获取的方法
     *
     * @param regex  规则
     * @param source 源字符串
     * @return
     */
    public static String getMatcher(String regex, String source) {
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            result = matcher.group(1);//只取第一组
        }
        return result;
    }

    public static int matchingCountryString(String localCode) {
        String[] split = localCode.split("\\_");
        String langagueCode = split[0];
        if ("en".equals(langagueCode)) {
            return R.string.string_for_language_english;
        } else if ("ko".equals(langagueCode)) {
            return R.string.string_for_language_korea;
        } else if ("ru".equals(langagueCode)) {
            return R.string.string_for_language_russia;
        } else if ("zh".equals(langagueCode)) {
            if ("TW".equals(split[1]) || "HK".equals(split[1]) || "MO".equals(split[1])) {
                return R.string.string_for_language_chnn;
            } else {
                return R.string.string_for_language_chn;
            }
        } else {
            return R.string.string_for_language_default;
        }
    }


    public static List<String> handleShopTimeData(List<CsBase.MerchantShopTime> shopTimesList) {
        boolean isHasDiffentTime = false;
        List<String> timeStringList = new ArrayList<>();
        int week = 1;
        int openHour = 0;
        int openMin = 0;
        int closeHour = 0;
        int closeMin = 0;
        for (int i = 0; i < shopTimesList.size(); i++) {
            CsBase.MerchantShopTime merchantShopTime = shopTimesList.get(i);
            if (i == 0) {
                week = merchantShopTime.getWeek();
                openHour = merchantShopTime.getOpenHour();
                openMin = merchantShopTime.getOpenMin();
                closeHour = merchantShopTime.getCloseHour();
                closeMin = merchantShopTime.getCloseMin();
            }
            if (TimeUtils.getWeekOfDate().equals(getWeekDayString(merchantShopTime.getWeek()))) {
                String dateStyleStringByLong = TimeUtils.getDateStyleStringByLong(System.currentTimeMillis());
                //LogUtils.e("这是当前的时间" + dateStyleStringByLong);
                String[] split = dateStyleStringByLong.split("\\ ");
                String s = split[1];
                //LogUtils.e("这是当前时间切割：" + s);
                String[] split1 = s.split("\\:");
                String s1 = split1[0];
                int currencyHourTime = Integer.parseInt(s1);
                int currencyMinTime = Integer.parseInt(split1[1]);
                String isWorking = SysApplication.getContext().getString(R.string.merchant_detail_business_working_none);
                if (currencyHourTime != merchantShopTime.getOpenHour() && currencyHourTime != merchantShopTime.getCloseHour()) {
                    if (currencyHourTime > merchantShopTime.getOpenHour() || currencyHourTime < merchantShopTime.getCloseHour()) {
                        isWorking = SysApplication.getContext().getString(R.string.merchant_detail_business_working);
                    }
                } else {
                    if (currencyHourTime == merchantShopTime.getOpenHour()) {
                        if (currencyMinTime > merchantShopTime.getOpenMin()) {
                            isWorking = SysApplication.getContext().getString(R.string.merchant_detail_business_working);
                        }
                    } else {
                        if (currencyMinTime < merchantShopTime.getCloseMin()) {
                            isWorking = SysApplication.getContext().getString(R.string.merchant_detail_business_working);
                        }
                    }
                }
                timeStringList.add(isWorking);
            }
            if (merchantShopTime.getOpenHour() != openHour || merchantShopTime.getOpenMin() != openMin || merchantShopTime.getCloseHour() != closeHour || merchantShopTime.getCloseMin() != closeMin) {
                isHasDiffentTime = true;
                String shopTimeString = "";
                int changeWeek = merchantShopTime.getWeek();
                if (changeWeek - 1 == week) {
                    if (week == 1) {
                        shopTimeString = getWeekDayString(week) + ":" + openHour + ":" + openMin + "0" + "-" + closeHour + ":" + closeMin + "0";
                    } else {
                        shopTimeString = getWeekDayString(changeWeek) + ":" + openHour + ":" + openMin + "0" + "-" + closeHour + ":" + closeMin + "0";
                    }
                    timeStringList.add(shopTimeString);
                } else {
                    shopTimeString = getWeekDayString(week) + "-" + getWeekDayString(changeWeek - 1) + ":" + openHour + ":" + openMin + "0" + "-" + closeHour + ":" + closeMin + "0";
                    timeStringList.add(shopTimeString);
                }
                week = changeWeek;
                openHour = merchantShopTime.getOpenHour();
                openMin = merchantShopTime.getOpenMin();
                closeHour = merchantShopTime.getCloseHour();
                closeMin = merchantShopTime.getCloseMin();
            } else {
                if (i == shopTimesList.size() - 1 && !isHasDiffentTime) {
                    String timeString = "Mon-Fri:" + openHour + ":" + openMin + "0" + "-" + closeHour + ":" + closeMin + "0";
                    String timeString01 = "Sat-Sun:" + openHour + ":" + openMin + "0" + "-" + closeHour + ":" + closeMin + "0";
                    timeStringList.add(timeString);
                    timeStringList.add(timeString01);
                }
            }
        }
        return timeStringList;
    }


    public static String getWeekDayString(int week) {
        switch (week) {
            case 1:
                return "Mon";
            case 2:
                return "Tue";
            case 3:
                return "Wed";
            case 4:
                return "Thu";
            case 5:
                return "Fri";
            case 6:
                return "Sat";
            case 7:
                return "Sun";
            default:
                return "";
        }
    }
}
