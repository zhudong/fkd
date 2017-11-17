package com.fuexpress.kr.ui.activity.bind_module;

import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.RequestNetListenerWithMsg;
import com.fuexpress.kr.utils.CommonUtils;
import com.fuexpress.kr.utils.LogUtils;

import fksproto.CsLogin;


/**
 * Created by longer on 2017/10/30.
 */

public class ThirdBindManager {
    private static ThirdBindManager thirdBindManager = null;

    //第三方登录标记EncryptUtils
    public static final int THIRD_PLATFROM_WX = 1;
    public static final int THIRD_PLATFROM_QQ = 2;
    public static final int THIRD_PLATFROM_WB = 3;
    public static final int THIRD_PLATFROM_FB = 4;


    private ThirdBindManager() {
    }

    public static ThirdBindManager getInstance() {
        if (thirdBindManager == null) thirdBindManager = new ThirdBindManager();
        return thirdBindManager;
    }


    public void getBindInfoRequest(final RequestNetListenerWithMsg listenerWithMsg) {
        CsLogin.GetBindInfoRequest.Builder builder = CsLogin.GetBindInfoRequest.newBuilder();

        builder.setUin(AccountManager.getInstance().mUin);

        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());

        builder.setRandomKey(AccountManager.isLogin ? AccountManager.getInstance().mSessionKey : NetEngine.sRandomKey);

        NetEngine.postRequest(builder, new INetEngineListener<CsLogin.GetBindInfoResponse>() {


            @Override
            public void onSuccess(CsLogin.GetBindInfoResponse response) {
                listenerWithMsg.onSuccess(response);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                listenerWithMsg.onFailure(ret, errMsg);
            }
        });


    }


    public void removeBindInfo(int paltNum, String account, final RequestNetListenerWithMsg requestNetListenerWithMsg) {
        CsLogin.AccountRequest.Builder builder = CsLogin.AccountRequest.newBuilder();

        builder.setOperacode(CsLogin.AccountOperacode.ACCOUNT_OPERACODE_THIRD_UNBIND_VALUE);

        builder.setAccountType(CsLogin.AccountType.ACCOUNT_TYPE_PHONE_VALUE);

        builder.setAccountExtra(AccountManager.getInstance().userNumber);

        builder.setAccount(account);

        CsLogin.ThirdAccount.Builder thirdAccount = CsLogin.ThirdAccount.newBuilder();

        thirdAccount.setPlatform(paltNum);

        builder.setThird(thirdAccount);

        builder.setRandomKey(AccountManager.isLogin ? AccountManager.getInstance().mSessionKey : NetEngine.sRandomKey);

        builder.setUin(AccountManager.getInstance().mUin);

        NetEngine.postRequest(builder, new INetEngineListener<CsLogin.AccountResponse>() {


            @Override
            public void onSuccess(CsLogin.AccountResponse response) {
                LogUtils.e("解除绑定成功");
                requestNetListenerWithMsg.onSuccess(response);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                LogUtils.e("解除绑定失败");
                requestNetListenerWithMsg.onFailure(ret, errMsg);
            }
        });
    }


    //第三方绑定手机号或者邮箱。在注册或者登陆成功后，调用此方法，绑定账号。绑定以后，既可以使用第三方账号登录
    public void bindThirdPlatformInfo(int thirdPlatfrom, String openid, String token, final RequestNetListenerWithMsg listener) {

        CsLogin.AccountRequest.Builder builder = CsLogin.AccountRequest.newBuilder();
        builder.setOperacode(CsLogin.AccountOperacode.ACCOUNT_OPERACODE_THIRD_BIND_NEW_VALUE);


        /*builder.setAccountType(CsLogin.AccountType.ACCOUNT_TYPE_PHONE_VALUE);
        builder.setAccountExtra(AccountManager.getInstance().userNumber);*/

        //因为协议是必须参数，C后台该接口也没用到这些参数，这里的两个参数就随意传些过去就好
        builder.setAccountType(CsLogin.AccountType.ACCOUNT_TYPE_PHONE_VALUE);
        builder.setAccount("123");

        builder.setUin(AccountManager.getInstance().mUin);


        builder.setRandomKey(AccountManager.isLogin ? AccountManager.getInstance().mSessionKey : NetEngine.sRandomKey);
        //builder.setRandomKey(NetEngine.sRandomKey);
        CsLogin.ThirdAccount.Builder thirdAccount = CsLogin.ThirdAccount.newBuilder();
        int platformInt = thirdPlatfrom;
        switch (platformInt) {
            case THIRD_PLATFROM_WX:
                platformInt = CsLogin.AuthPlatform.AUTH_PLATFORM_WX_VALUE;
                break;
            case THIRD_PLATFROM_QQ:
                platformInt = CsLogin.AuthPlatform.AUTH_PLATFORM_QQ_VALUE;
                break;
            case THIRD_PLATFROM_WB:
                platformInt = CsLogin.AuthPlatform.AUTH_PLATFORM_SINA_VALUE;
                break;
            case THIRD_PLATFROM_FB:
                platformInt = CsLogin.AuthPlatform.AUTH_PLATFORM_FACEBOOK_VALUE;
                break;
        }
        thirdAccount.setPlatform(platformInt);
        thirdAccount.setOpenid(openid);
        thirdAccount.setToken(token);
        //LogUtils.e("openid = " + openid + " token = " + token + " int = " + platformInt);

        builder.setThird(thirdAccount);
        NetEngine.postRequest(builder, new INetEngineListener<CsLogin.AccountResponse>() {
            @Override
            public void onSuccess(final CsLogin.AccountResponse response) {
                LogUtils.e(response.toString());
                if (null != listener)
                    listener.onSuccess(response);

            }

            public void onFailure(final int ret, final String errMsg) {
                LogUtils.e("failed ,ret=" + ret + ",errMsg " + CommonUtils.getErrMsg(ret));
                if (null != listener) {
                    listener.onFailure(ret, CommonUtils.getErrMsg(ret));
                }

            }
        });
    }


}
