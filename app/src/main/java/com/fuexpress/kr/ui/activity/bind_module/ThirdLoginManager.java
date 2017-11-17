package com.fuexpress.kr.ui.activity.bind_module;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;
import com.socks.library.KLog;
import com.tencent.connect.UserInfo;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Set;

import de.greenrobot.event.EventBus;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.fuexpress.kr.ui.uiutils.UIUtils.getString;

/**
 * Created by longer on 2017/10/30.
 */

public class ThirdLoginManager {


    private boolean wx_Bind = false;
    private static ThirdLoginManager thirdLoginManager = null;

    private ThirdLoginManager() {
    }

    public static ThirdLoginManager getInstance() {
        if (thirdLoginManager == null) thirdLoginManager = new ThirdLoginManager();
        return thirdLoginManager;
    }

    private Tencent mTencent;
    private IUiListener iUiListener;
    public static String TITLE = "";
    private String QQ_openid = "";
    private String QQ_access_token = "";
    private long QQ_expires_in = 0;
    private UserInfo userInfo;

    public void loginByQQ(final Activity activity, final ThirdLoginListener thirdLoginListener) {
        KLog.i("account", "qq)");
        TITLE = getString(R.string.qq);
        iUiListener = new IUiListener() {
            @Override
            public void onComplete(Object arg0) {
                KLog.i("arg= " + arg0.toString());
                JSONObject jsonObject = (JSONObject) arg0;
                KLog.i("success");
                try {
                    //保存QQ相关信息
                    QQ_access_token = jsonObject.getString("access_token");
                    SPUtils.put(activity, Constants.QQ.QQ_ACCESS_TOKEN, QQ_access_token);
                    QQ_openid = jsonObject.getString("openid");
                    SPUtils.put(activity, Constants.QQ.QQ_OPENDID, QQ_openid);
                    QQ_expires_in = jsonObject.getLong("expires_in");
                    SPUtils.put(activity, Constants.QQ.QQ_EXPIRES_IN, QQ_expires_in);
                    SPUtils.put(activity, Constants.QQ.QQ_END_TIME, System.currentTimeMillis() + QQ_expires_in * 1000);
                    //保存第三方登录信息_QQ
                    // saveThirdLoginInfo(AccountManager.THIRD_PLATFROM_QQ,QQ_access_token,QQ_openid);
                    thirdLoginListener.onRequestSuccess(QQ_openid, QQ_access_token);
                    getQQUserInfo(activity);

                    //   showInfo(AccountManager.THIRD_PLATFROM_QQ);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError arg0) {
                // TODO Auto-generated method stub
                KLog.i("uiError" + arg0.toString());
                thirdLoginListener.onRequestFail(arg0.toString());
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                KLog.i("uiCancel");
                thirdLoginListener.onRequestFail("consumer cancel");
            }
        };
        mTencent = Tencent.createInstance(Constants.QQ.QQ_APPID, activity.getApplicationContext());
        mTencent.login(activity, "all", iUiListener);
    }


    private void getQQUserInfo(Context context) {
        userInfo = new UserInfo(context, mTencent.getQQToken());
        IUiListener listener = new IUiListener() {
            @Override
            public void onError(UiError e) {
                // TODO Auto-generated method stub
                LogUtils.e("获取QQ信息失败");
            }

            @Override
            public void onComplete(Object response) {
                KLog.i("object " + response.toString());
                JSONObject json = (JSONObject) response;
                // 昵称
                String nickname = null;
                String userIcon = "";
                try {
                    nickname = json.getString("nickname");
                    AccountManager.getInstance().thridNickname = nickname;
                    userIcon = json.getString("figureurl_qq_2");
                    AccountManager.getInstance().thridUserIcon = userIcon;
                    KLog.i("nickname = " + nickname);
                    KLog.i("userIcon = " + userIcon);

                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_THIRD_ACCOUNT_INFO, null));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        };
        userInfo.getUserInfo(listener);
    }

    /*********************************************
     * facebook
     ***********************/
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;


    public void loginByFaceBook(final Activity activity, final ThirdLoginListener thirdLoginListener) {
        TITLE = UIUtils.getString(R.string.facebook);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                KLog.i("facebook", "Success");
                KLog.i("userId =" + loginResult.getAccessToken().getUserId());
                KLog.i("token =" + loginResult.getAccessToken().getToken());
                KLog.i("permission = " + loginResult.getAccessToken().getPermissions().toString());
                Set<String> set = loginResult.getAccessToken().getPermissions();
                for (String str : set) {
                    KLog.i("permission = " + str);
                }
                // TODO: 2017/10/30 利用回调返回第三方的登录信息
                thirdLoginListener.onRequestSuccess(loginResult.getAccessToken().getUserId(), loginResult.getAccessToken().getToken());
                getFaceBookLoginInfo(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                KLog.i("facebook", "Cancel");
                thirdLoginListener.onRequestFail("cancel");
            }

            @Override
            public void onError(FacebookException error) {
                KLog.i("facebook", "Error " + error.toString());
                thirdLoginListener.onRequestFail(error.toString());
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"));
    }


    /**
     * Facebook登录重新的事件处理
     */
    private void getFaceBookLoginInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (object != null) {
                    String id = object.optString("id");   //比如:1565455221565
                    String name = object.optString("name");  //比如：Zhang San
                    AccountManager.getInstance().thridNickname = name;
                    String gender = object.optString("gender");  //性别：比如 male （男）  female （女）
                    String emali = object.optString("email");  //邮箱：比如：56236545@qq.com
                    //获取用户头像
                    JSONObject object_pic = object.optJSONObject("picture");
                    JSONObject object_data = object_pic.optJSONObject("data");
                    String photo = object_data.optString("url");
                    AccountManager.getInstance().thridUserIcon = photo;
                    KLog.i("facebook " + name);
                    KLog.i("facebook " + photo);
                    //    LoginByPhoneActivity.this.toActivity(ThirdPlatformLoginActivity.class);
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_THIRD_ACCOUNT_INFO, null));
                    //获取地域信息
                   /* String locale = object.optString("locale");   //zh_CN 代表中文简体
                    Toast.makeText( LoginByPhoneActivity.this , "" + object.toString() , Toast.LENGTH_SHORT).show();*/
                    KLog.i("userId = " + object.toString());
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,birthday,email,picture,locale,updated_time,timezone,age_range,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private IWXAPI api;

    public void loginByWX(Activity activity) {
        KLog.i("微信登录");
        api = WXAPIFactory.createWXAPI(activity, Constants.WeiXin.WX_APPID_SHARE, true);
        api.registerApp(Constants.WeiXin.WX_APPID_SHARE);
        wx_Bind = true;
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }

    public boolean isWx_Bind() {
        return wx_Bind;
    }
}
