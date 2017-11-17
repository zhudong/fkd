package com.fuexpress.kr.model;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.ui.activity.login_register.LoginByPhoneActivity;

/**
 * Created by Longer on 2016/11/1.
 */
public class ConfigManager {

    public static ConfigManager configManager = new ConfigManager();
    private static Context mContext;

    public static final String KEY_JUMP_SETTING = "JUMP_SETTING";

    private ConfigManager() {

    }

    public static ConfigManager getInstance(Context context) {
        mContext = context;
        return configManager;
    }

    public void updateConfig(Configuration config) {
        Resources res = mContext.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());
        ActivityController.finishAll();
        Intent intent = new Intent();
        intent.setClass(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }

    public void updateConfig(Configuration config, boolean isLogion) {
        Resources res = mContext.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());
        ActivityController.finishAll();
        Intent intent = new Intent();
        intent.setClass(mContext, isLogion ? MainActivity.class : LoginByPhoneActivity.class);
        mContext.startActivity(intent);
    }

    public void updateConfigJumpSetting(Configuration config) {
        Resources res = mContext.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());
        ActivityController.finishAll();
        Intent intent = new Intent();
        intent.putExtra(KEY_JUMP_SETTING, true);
        intent.setClass(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }

    //2016/10/09 add by Longer 安全切换语种资源的方法
    public void updateConfigByNotActivity(Configuration config) {
        Resources res = mContext.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());
        ActivityController.finishAllSafe(new ActivityController.WhenTheArrayListFinish() {
            @Override
            public void readComplete() {
                Intent intent = new Intent();
                intent.setClass(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    //2016/10/09 add by Longer 安全切换语种资源的方法
    public void updateConfigByNotActivityBundBoolean(Configuration config, final String tag, final boolean booleanValue) {
        Resources res = mContext.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());
        ActivityController.finishAllSafe(new ActivityController.WhenTheArrayListFinish() {
            @Override
            public void readComplete() {
                Intent intent = new Intent();
                intent.setClass(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(tag, booleanValue);
                mContext.startActivity(intent);
            }
        });
    }

    public void updateConfigInLogin(Configuration config) {
        Resources res = mContext.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());
        ActivityController.finishAll();
        Intent intent = new Intent();
        //changeAddressStrings();
        intent.setClass(mContext, LoginByPhoneActivity.class);
        mContext.startActivity(intent);
    }
}
