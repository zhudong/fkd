package com.fuexpress.kr.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ConfigManager;
import com.fuexpress.kr.model.ShopCartManager;
import com.fuexpress.kr.ui.activity.help_signed.data.HelpSignedLocalDataSource;
import com.fuexpress.kr.ui.activity.help_signed.data.HelpSingedDataRepository;
import com.fuexpress.kr.ui.activity.login_register.LoginByPhoneActivity;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.Locale;

/**
 * Created by kevin.xie on 2016/11/3.
 */

public class SplashActivity extends FragmentActivity {
    public static String DEVICE_TOKEN = "device_token";

    static Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        startDeviceService();
        MPermissions.requestPermissions(this, 6, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS});
    }

    private void startDeviceService() {
        /*Intent intent = new Intent(this, DeviceService.class);
        startService(intent);*/
    }


    /*适配6.0 权限检查*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(6)
    public void requestContactSuccess() {
        work(1500);
    }

    @PermissionDenied(6)
    public void requestContactFailed() {
        work(1500);
    }

    private void work(long time) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getLanguage();
                getAddressDataThread();
                //加载一下帮我收的本地数据
                HelpSingedDataRepository.getInstance(HelpSignedLocalDataSource.getInstance()).initBeanDataList();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                LogUtils.e("登录状态是?" + AccountManager.getInstance().isLogin);
                if (!AccountManager.getInstance().isLogin) {
                    intent = new Intent(SplashActivity.this, LoginByPhoneActivity.class);
                }
                LogUtils.d("-------------------------" + AccountManager.getInstance().mLocaleCode);
                if (!TextUtils.isEmpty(AccountManager.getInstance().mLocaleCode)) {
                    String[] language = AccountManager.getInstance().mLocaleCode.split("_");
                    Resources res = getResources();
                    Configuration config = res.getConfiguration();
                    config.locale = new Locale(language[0], language[1]);
                    ConfigManager.getInstance(SplashActivity.this).updateConfig(config,AccountManager.getInstance().isLogin);
                } else {
                    /*Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);*/
                    startActivity(intent);
                }
                finish();
            }
        }, time);
    }

    public void getLanguage() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.equals("en")) {
            language = language + "_US";
        } else if (language.equals("zh")) {
            String country = locale.getCountry();
            if (country.equals("HK") || country.equals("TW") || country.equals("MO")) {
                language = language + "_TW";
            } else {
                language = language + "_CN";
            }
        } /*else if (language.equals("ko")) {
            language = language + "_KR";
        } else if (language.equals("ru")) {
            language = language + "_RU";
        }*/ else {
            //SysApplication.isDeafultLangague = true;
            AccountManager.getInstance().setIsDefaultLanguage(true);
            language = language + "_" + locale.getCountry();
            SPUtils.put(this, Constants.USER_INFO.USER_IS_DEFAULT_LANGUAGE, true);
        }
        LogUtils.e(language);
        SysApplication.DEFAULT_LANGUAGE = language;
        //SPUtils.put(this, Constants.USER_INFO.USER_PHONE_LANGUAGE, language);
    }


    public void getAddressDataThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //AssetFileManager.getInstance().getAddressData(SysApplication.getContext());
                //AddressManager.getInstance().prepareAddressData();
                //AddressManager.getInstance().getUpdateCountryAndRegionRequest();
            }
        }) {
        }.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //StoreManager.getInstance().getHotMerchantCondRequest();
                ShopCartManager shopCartManager = ShopCartManager.getInstance(SysApplication.getContext(), new SysApplication().getInstances());
                shopCartManager.getSaleCartCount();
            }
        }) {
        }.start();
    }
}
