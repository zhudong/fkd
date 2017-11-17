package com.fuexpress.kr.ui.activity.bind_module;

/**
 * Created by longer on 2017/10/30.
 */

public interface ThirdLoginListener {

    void onRequestSuccess(String openID, String token);

    void onRequestFail(String errMsg);
}
