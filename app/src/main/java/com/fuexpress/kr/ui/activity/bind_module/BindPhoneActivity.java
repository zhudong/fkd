package com.fuexpress.kr.ui.activity.bind_module;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.RequestNetListenerWithMsg;
import com.fuexpress.kr.ui.activity.ChooseCountryActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.CountryNumberUtils;
import com.google.protobuf.GeneratedMessage;
import com.socks.library.KLog;

import fksproto.CsLogin;

/**
 * Created by longer on 2017/10/10.
 */

public class BindPhoneActivity extends BaseActivity {
    private TextView leftTv, centerTv, rigthTv;
    private ImageView leftIv, rightIv;
    public boolean isGetCode = false;
    public String phoneStr, codeStr;
    private TextView locationText, numberText;
    private Button bindBtn, getCodeBtn;
    private LinearLayout chooseLocationLayout;
    private EditText phoneEd, codeEd;
    private CountDownTimer timer;       //倒计时60秒
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_login:
                    bindPhone();
                    break;
                case R.id.getCodeBtn:
                    getCode();
                    break;
                case R.id.rl_for_login_choose_location:
                    Intent intent = new Intent(BindPhoneActivity.this, ChooseCountryActivity.class);
                    startActivity(intent);
                    break;
                /*case R.id.tv_left:
                    onBackPressed();
                    break;
                case R.id.iv_left:
                    onBackPressed();
                    break;*/
            }
        }
    };
    private EditText ed_bind_pwd;

    @Override
    public View setInitView() {
        View view = View.inflate(this, R.layout.activity_bind_phone, null);
        TitleBarView titleBarView = (TitleBarView) view.findViewById(R.id.title_in_bind_phone);
        titleBarView.getIv_in_title_back().setOnClickListener(this);
        ed_bind_pwd = (EditText) view.findViewById(R.id.ed_bind_pwd);
        /*leftIv = (ImageView) view.findViewById(R.id.iv_left);
        rightIv = (ImageView) view.findViewById(R.id.iv_right);
        leftTv = (TextView) view.findViewById(R.id.tv_left);
        centerTv = (TextView) view.findViewById(R.id.tv_center);
        rigthTv = (TextView) view.findViewById(R.id.tv_right);*/

        /*rigthTv.setVisibility(View.GONE);
        rightIv.setVisibility(View.GONE);

        leftIv.setOnClickListener(onClickListener);
        leftTv.setOnClickListener(onClickListener);*/

        /*leftTv.setText(getString(R.string.String_for_me_title));
        centerTv.setText(getString(R.string.String_binld_phone));*/

        chooseLocationLayout = (LinearLayout) view.findViewById(R.id.rl_for_login_choose_location);
        chooseLocationLayout.setOnClickListener(onClickListener);
        codeEd = (EditText) view.findViewById(R.id.ed_for_login_pwd);
        phoneEd = (EditText) view.findViewById(R.id.ed_for_login_phone_number);
        bindBtn = (Button) view.findViewById(R.id.tv_login);
        bindBtn.setOnClickListener(onClickListener);
        getCodeBtn = (Button) view.findViewById(R.id.getCodeBtn);
        getCodeBtn.setOnClickListener(onClickListener);
        locationText = (TextView) view.findViewById(R.id.tv_for_login_location);
        numberText = (TextView) view.findViewById(R.id.tv_for_login_in_rl_02);
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getCodeBtn.setText(millisUntilFinished / 1000 + "S");
                getCodeBtn.setClickable(false);
               /* try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                Log.e("CountDown", millisUntilFinished + "");
            }

            @Override
            public void onFinish() {
                getCodeBtn.setText(getString(R.string.String_get_code));
                getCodeBtn.setClickable(true);
            }
        };
        return view;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }

    private void getCode() {
        isGetCode = true;
        phoneStr = phoneEd.getText().toString();
        if (TextUtils.isEmpty(phoneStr)) {
            mViewUtils.toast(getString(R.string.String_enter_phone));
            return;
        }
        int countryNumber = Integer.valueOf(AccountManager.getInstance().userNumber);
        if (TextUtils.isEmpty(phoneStr) || !CountryNumberUtils.isNumberCurrect(countryNumber, phoneStr)) {
            mViewUtils.toast(getString(R.string.String_phone_error));
            return;
        }
        //倒计时60秒内
        timer.start();

        CsLogin.AccountRequest.Builder builder = CsLogin.AccountRequest.newBuilder();
        //请求绑定手机
        builder.setOperacode(CsLogin.AccountOperacode.ACCOUNT_OPERACODE_APPLY_VERIFY_CODE_VALUE);
        builder.setAccountType(CsLogin.AccountType.ACCOUNT_TYPE_PHONE_VALUE);
        builder.setAccount(phoneStr);
        builder.setRandomKey(AccountManager.isLogin ? AccountManager.getInstance().mSessionKey : NetEngine.sRandomKey);

        builder.setReserve(CsLogin.VerifyCodeScene.VERIFY_CODE_SCENE_PHONE_BIND_VALUE + "");

        builder.setAccountExtra(AccountManager.getInstance().userNumber);

        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        KLog.i("phone =" + phoneStr + " code = " + AccountManager.getInstance().userNumber);
        NetEngine.postRequest(builder, new INetEngineListener<CsLogin.AccountResponse>() {
            @Override
            public void onSuccess(CsLogin.AccountResponse response) {
                KLog.i(response.toString());
                KLog.i("获取验证码成功");
            }

            public void onFailure(final int ret, String errMsg) {
                KLog.i("failed ,ret=" + ret + ",errMsg " + errMsg);
                final String errStr = errMsg;
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast(errStr);
                    }
                });

            }
        });
    }

    private void bindPhone() {
        //发送绑定手机请求
        if (checkCode() && checkEditText() && checkPWD()) {
            AccountManager.getInstance().bindThirdPlatform(AccountManager.TYPE_PHONE, phoneStr, ed_bind_pwd.getText().toString().trim(), codeStr, new RequestNetListenerWithMsg() {
                @Override
                public void onSuccess(GeneratedMessage response) {
                    KLog.i(response.toString());
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            mViewUtils.toast(getString(R.string.String_bind_success));
                        }
                    });
                    onBackPressed();
                }

                @Override
                public void onFailure(int ret, String msg) {
                    KLog.i("failed ,ret=" + ret + ",errMsg " + msg);
                    final String str = msg;
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            //mViewUtils.toast(CommonUtils.getErrMsg(ret));
                            mViewUtils.toast(str);
                        }
                    });
                }
            });
            // TODO: 2017/10/10 在国际版中，这里是通过获得其邮箱账号来进行手机的绑定，目前福快递没有邮箱登录，需要商议
            /*AccountManager.getInstance().emailStr = (String) SPUtils.get(this, Constants.USER_INFO.USER_ACCOUNT, "");
            KLog.i("email = " + AccountManager.getInstance().emailStr);
            if (TextUtils.isEmpty(AccountManager.getInstance().emailStr)) {
                mViewUtils.toast(getString(R.string.String_email_empty));
                return;
            }
            CsLogin.AccountRequest.Builder builder = CsLogin.AccountRequest.newBuilder();

            //操作码，请求绑定手机
            builder.setOperacode(CsLogin.AccountOperacode.ACCOUNT_OPERACODE_PHONE_BIND_VALUE);
            builder.setAccountType(CsLogin.AccountType.ACCOUNT_TYPE_PHONE_VALUE);
            builder.setAccount(phoneStr);
            builder.setReserve(AccountManager.getInstance().emailStr);
            builder.setRandomKey(NetEngine.sRandomKey);
            builder.setAccountExtra(AccountManager.getInstance().userNumber);
            builder.setVerifyCode(codeStr);
            builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
            KLog.i("phone =" + phoneStr + " code = " + codeStr + "userNumber = " + AccountManager.getInstance().userNumber);
            NetEngine.postRequest(builder, new INetEngineListener<CsLogin.AccountResponse>() {
                @Override
                public void onSuccess(CsLogin.AccountResponse response) {
                    KLog.i(response.toString());
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            mViewUtils.toast(getString(R.string.String_bind_success));
                        }
                    });
                    onBackPressed();
                }

                public void onFailure(final int ret, String errMsg) {
                    KLog.i("failed ,ret=" + ret + ",errMsg " + errMsg);
                    final String str = errMsg;
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            //mViewUtils.toast(CommonUtils.getErrMsg(ret));
                            mViewUtils.toast(str);
                        }
                    });

                }
            });*/
        }
    }

    private void SetLocation() {
        if (!TextUtils.isEmpty(AccountManager.getInstance().userCountry)) {
            locationText.setText(AccountManager.getInstance().userCountry);
        }
        if (!TextUtils.isEmpty(AccountManager.getInstance().userNumber)) {
            numberText.setText("+" + AccountManager.getInstance().userNumber);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SetLocation();
    }

    public boolean checkEditText() {
        if (TextUtils.isEmpty(phoneEd.getText().toString())) {
            mViewUtils.toast(getString(R.string.String_enter_phone));
            return false;
        }

        return true;
    }

    public boolean checkPWD() {
        if (TextUtils.isEmpty(ed_bind_pwd.getText().toString().trim())) {
            mViewUtils.toast("请输入密码");
            return false;
        }
        return true;
    }

    public boolean checkCode() {
        phoneStr = phoneEd.getText().toString();
        codeStr = codeEd.getText().toString();
        if (!isGetCode) {
            mViewUtils.toast(getString(R.string.String_please_get_code));
            return false;
        } else if (TextUtils.isEmpty(codeStr)) {
            mViewUtils.toast(getString(R.string.tv_for_register_in_phone_ed_03_hinnt));
            return false;
        } else {
            return true;
        }
    }
}
