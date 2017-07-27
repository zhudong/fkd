package com.fuexpress.kr.model;

import android.text.LoginFilter;

/**
 * Created by Administrator on 2017-7-19.
 */

public class MyInputFilter extends LoginFilter.UsernameFilterGMail {
    @Override
    public boolean isAllowed(char c) {
        // Allow [a-zA-Z0-9@.]
        if ('0' <= c && c <= '9')
            return true;
        if ('a' <= c && c <= 'z')
            return false;
        if ('A' <= c && c <= 'Z')
            return false;
        return false;
    }

}
