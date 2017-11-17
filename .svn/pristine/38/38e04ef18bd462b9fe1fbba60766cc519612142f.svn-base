package com.fuexpress.kr.wxapi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.RequestNetListenerWithMsg;
import com.fuexpress.kr.ui.activity.bind_module.BindPhoneActivity;
import com.fuexpress.kr.ui.activity.bind_module.ThirdLoginManager;
import com.fuexpress.kr.ui.activity.login_register.LoginByPhoneActivity;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.utils.LogUtils;
import com.socks.library.KLog;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import fksproto.CsLogin;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by k550 on 4/28/2016.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    public IWXAPI iwxapi;
    public Button button;
    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.wxBtn:
                    share2WXF();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_share);
        button = (Button) findViewById(R.id.wxBtn);
        button.setOnClickListener(onClickListener);
        iwxapi = WXAPIFactory.createWXAPI(this, Constants.WeiXin.WX_APPID, true);
        iwxapi.registerApp(Constants.WeiXin.WX_APPID);
        iwxapi.handleIntent(getIntent(), this);
    }

    public void share2WXF() {
        WXTextObject textObject = new WXTextObject();
        textObject.text = "testQQLogin";
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = "testQQLogin";
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        iwxapi.sendReq(req);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        KLog.i("req=" + baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        KLog.i("resp=" + baseResp.errCode);

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (LoginByPhoneActivity.WX_LOGIN || ThirdLoginManager.getInstance().isWx_Bind()) {
                    //当前处于微信登录回调
                    KLog.i("opendid = " + baseResp.openId);
                    String code = ((SendAuth.Resp) baseResp).code;
                    KLog.i("code = " + code);
                    String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                            + Constants.WeiXin.WX_APPID_SHARE + "&secret=" +
                            Constants.WeiXin.WX_APPSECRET_SHARE + "&code=" +
                            code + "&grant_type=authorization_code";
                    KLog.i("url = " + url);
                    OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder().url(url).build();
                    try {
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String s = response.body().string();
                                KLog.i("response " + s);
                                getToken(s);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
                    WXEntryActivity.this.onBackPressed();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                // TODO: 2017/6/2 TB Android 1.0.6 Bug10修改  - chenyl
                //Toast.makeText(this, "分享被取消", Toast.LENGTH_LONG).show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(this, "分享失败", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, "分享失败", Toast.LENGTH_LONG).show();
                break;

        }
        WXEntryActivity.this.onBackPressed();
    }

    public void getToken(String s) {
        LoginByPhoneActivity.TITLE = getString(R.string.wechat);
        try {
            JSONObject jsonObject = new JSONObject(s);
            String token = jsonObject.getString("access_token");
            String opendid = jsonObject.getString("openid");
            String refresh_token = jsonObject.getString("refresh_token");
            AccountManager.getInstance().WX_refresh_token = refresh_token;
            AccountManager.getInstance().openid = opendid;
            AccountManager.getInstance().token = refresh_token;
            String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + token + "&openid=" + opendid;
            KLog.i("url = " + url);
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder().url(url).build();
            try {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String s = response.body().string();
                        KLog.i("response " + s);
                        getWXUserInfo(s);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!AccountManager.isLogin)
                AccountManager.getInstance().loginByThirdPlatform(AccountManager.THIRD_PLATFROM_WX, token, opendid, new RequestNetListenerWithMsg<CsLogin.ThirdLoginWithoutBindResponse>() {
                    @Override
                    public void onSuccess(CsLogin.ThirdLoginWithoutBindResponse response) {
                        /*String localecode = response.getLocalecode();
                        boolean isBindPhone = response.getIsBindPhone();
                        Intent intent = new Intent(WXEntryActivity.this, MainActivity.class);
                        intent.putExtra(Constants.KEY_IS_JUMP_MEFRAG, !isBindPhone);
                        startActivity(intent);*/
                        LogUtils.e("微信登录但未绑定手机号");
                        //startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
                        LogUtils.e("微信登录请求成功");
                    }

                    @Override
                    public void onFailure(int ret, String msg) {
                        Toast.makeText(WXEntryActivity.this, msg, Toast.LENGTH_LONG).show();
                        LogUtils.e("微信登录请求失败");
                        //startActivity(new Intent(WXEntryActivity.this, ThirdPlatformLoginActivity.class));
                        //showDialog();
                    /*if (ret == -25) {
                        Intent intent = new Intent(WXEntryActivity.this, MainActivity.class);
                        intent.putExtra(Constants.KEY_IS_JUMP_MEFRAG, true);
                        LogUtils.e("微信登录但未绑定手机号");
                        startActivity(intent);
                    }*/
                    }
                });
            else {

                EventBus.getDefault().post(new BusEvent(BusEvent.BIND_WX_REQUEST, opendid, token));
                finish();
            }

        } catch (Exception e) {

        }
    }

    private CustomDialog.Builder dialog;

    public void showDialog() {
        String dialogConfig = "去绑定";
        String dialogcancle = "不绑定";
        dialog = new CustomDialog.Builder(this);
        dialog.setTitle("绑定手机号");
        dialog.setMessage("可绑定已注册的手机号");
        dialog.setPositiveButton(dialogConfig, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(BindingGiftCardActivity.this, "点击了确定", Toast.LENGTH_SHORT).show();
                //GiftCardManager.getInstance().bindGiftCardRequest(mCardNum);
                startActivity(new Intent(WXEntryActivity.this, BindPhoneActivity.class));
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(dialogcancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    public void getWXUserInfo(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            String nickname = jsonObject.getString("nickname");
            String iconUrl = jsonObject.getString("headimgurl");
            KLog.i(" nickname = " + nickname + " iconUrl = " + iconUrl);
            AccountManager.getInstance().thridNickname = nickname;
            AccountManager.getInstance().thridUserIcon = iconUrl;
            EventBus.getDefault().post(new BusEvent(BusEvent.GET_THIRD_ACCOUNT_INFO, null));
            //  startActivity(new Intent(this, ThirdPlatformLoginActivity.class));
        } catch (Exception e) {

        }
    }

}
