package com.fuexpress.kr.utils;

/**
 * Created by andy on 2017/6/28.
 */
public class FloatUtils {
    public static float vlaueOf(String sValue) {
        return Float.valueOf(sValue.replaceAll(",", ""));
    }
}
