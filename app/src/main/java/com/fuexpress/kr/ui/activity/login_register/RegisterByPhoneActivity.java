package com.fuexpress.kr.ui.activity.login_register;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.ChooseCountryActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.CommonUtils;
import com.fuexpress.kr.utils.CountryNumberUtils;
import com.fuexpress.kr.utils.MD5Util;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fksproto.CsLogin;

/**
 * Created by kevin.xie on 2016/10/31.
 */

public class RegisterByPhoneActivity extends BaseActivity {
    @BindView(R.id.title_tv_left)
    TextView mTitleTvLeft;
    @BindView(R.id.title_tv_center)
    TextView mTitleTvCenter;
    @BindView(R.id.title_tv_right)
    TextView mTitleTvRight;
    @BindView(R.id.tv_for_login_in_rl_01)
    TextView mTvForLoginInRl01;
    @BindView(R.id.tv_for_login_location)
    TextView mTvForLoginLocation;
    @BindView(R.id.rl_for_login_choose_location)
    RelativeLayout mRlForLoginChooseLocation;
    @BindView(R.id.tv_for_login_in_rl_02)
    TextView mTvForLoginInRl02;
    @BindView(R.id.ed_for_login_phone_number)
    EditText mEdForLoginPhoneNumber;
    @BindView(R.id.tv_for_login_in_rl_03)
    TextView mTvForLoginInRl03;
    @BindView(R.id.getCodeBtn)
    Button mGetCodeBtn;
    @BindView(R.id.ed_for_code)
    EditText mEdForCode;
    @BindView(R.id.ed_for_login_pwd)
    EditText mEdForLoginPwd;
    @BindView(R.id.logoutButton)
    Button mLogoutButton;

    private boolean isGetCode = false;
    public static boolean isNeedBind = false;
    private String mcode, phoneStr, passwordStr;
    private CountDownTimer timer;

    @Override
    public View setInitView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_register_phone, null);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mTitleTvCenter.setText(getString(R.string.app_name));
        mTitleTvLeft.setText(getString(R.string.cancel));
        mTitleTvLeft.setVisibility(View.VISIBLE);
        mTitleTvRight.setText(getString(R.string.login));
        mLogoutButton.setText(getString(R.string.register));
        mTitleTvRight.setVisibility(View.VISIBLE);
        mEdForLoginPwd.setTypeface(Typeface.DEFAULT);
        mEdForLoginPwd.setTransformationMethod(new PasswordTransformationMethod());
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mGetCodeBtn.setText(millisUntilFinished / 1000 + "S");
                mGetCodeBtn.setClickable(false);
               /* try {
                    Thread.sleep(999);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onFinish() {
                mGetCodeBtn.setText(getString(R.string.String_get_code));
                mGetCodeBtn.setClickable(true);
            }
        };
        mEdForLoginPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = mEdForLoginPhoneNumber.getText().toString();
                if (!TextUtils.isEmpty(temp) && temp.startsWith("0")) {
                    temp = temp.substring(1);
                    mEdForLoginPhoneNumber.setText(temp);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CountryNumberUtils.setResetCountry(this);
        mTvForLoginLocation.setText(AccountManager.getInstance().userCountry);
        mTvForLoginInRl02.setText("+" + AccountManager.getInstance().userNumber);
    }

    @OnClick({R.id.title_tv_left, R.id.title_tv_right, R.id.rl_for_login_choose_location, R.id.getCodeBtn, R.id.logoutButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_tv_left:
                onBackPressed();
                break;
            case R.id.title_tv_right:
                toActivity(LoginByPhoneActivity.class);
                break;
            case R.id.rl_for_login_choose_location:
                toActivity(ChooseCountryActivity.class);
                break;
            case R.id.getCodeBtn:
                phoneStr = mEdForLoginPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneStr) || !CountryNumberUtils.isNumberCurrect(Integer.valueOf(AccountManager.getInstance().userNumber), phoneStr)) {
                    mViewUtils.toast(getString(R.string.String_phone_error));
                    return;
                } else {
                    getCode();
                }
                break;
            case R.id.logoutButton:
                register();
                break;
        }
    }

    private void getCode() {
        isGetCode = true;
        //倒计时60秒内
        timer.start();
        CsLogin.AccountRequest.Builder builder = CsLogin.AccountRequest.newBuilder();
        //请求手机验证码
        builder.setOperacode(CsLogin.AccountOperacode.ACCOUNT_OPERACODE_APPLY_VERIFY_CODE_VALUE);
        builder.setAccountType(CsLogin.AccountType.ACCOUNT_TYPE_PHONE_VALUE);
        builder.setAccount(phoneStr);
        builder.setRandomKey(NetEngine.sRandomKey);
        builder.setReserve(CsLogin.VerifyCodeScene.VERIFY_CODE_SCENE_REGISTER_VALUE + "");
        builder.setAccountExtra(AccountManager.getInstance().userNumber);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        KLog.i("phone =" + phoneStr + " code = " + AccountManager.getInstance().userNumber);
        NetEngine.postRequest(builder, new INetEngineListener<CsLogin.AccountResponse>() {
            @Override
            public void onSuccess(CsLogin.AccountResponse response) {
                KLog.i(response.toString());
            }

            public void onFailure(final int ret, final String errMsg) {
                KLog.e("failed ,ret=" + ret + ",errMsg " + errMsg);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast(errMsg);
                    }
                });

            }
        });
    }

    private void register() {

        if (checkCode() && checkEditText()) {
            //发送注册请求
            CsLogin.AccountRequest.Builder builder = CsLogin.AccountRequest.newBuilder();
            //请求手机验证码
            builder.setOperacode(CsLogin.AccountOperacode.ACCOUNT_OPERACODE_REGISTER_VALUE);
            builder.setAccountType(CsLogin.AccountType.ACCOUNT_TYPE_PHONE_VALUE);
            builder.setAccount(phoneStr);
            builder.setRandomKey(NetEngine.sRandomKey);
            builder.setVerifyCode(mcode);
            builder.setPassword(MD5Util.getMD5(passwordStr).toLowerCase());
            builder.setAccountExtra(/*"+" +*/ AccountManager.getInstance().userNumber);
            //添加国家码（不是区号
            builder.setCountrycode(AccountManager.getInstance().mCountryCode);
            builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
            KLog.i("phone =" + phoneStr + " code = " + AccountManager.getInstance().mCountryCode);
            NetEngine.postRequest(builder, new INetEngineListener<CsLogin.AccountResponse>() {
                @Override
                public void onSuccess(CsLogin.AccountResponse response) {
                    KLog.i(response.toString());
                    //注册成功，开始登录
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            mViewUtils.toast(getString(R.string.register_success));
                            //如果需要绑定则走绑定逻辑，否则直接登录
                            toActivity(MainActivity.class);
                            if (isNeedBind) {
                                AccountManager.getInstance().bindThirdPlatform(AccountManager.TYPE_PHONE, phoneStr, passwordStr);
                            } else {
                                AccountManager.getInstance().login(CsLogin.AccountType.ACCOUNT_TYPE_PHONE, phoneStr, passwordStr, null, RegisterByPhoneActivity.this);
                            }

                        }
                    });
                }

                public void onFailure(final int ret, final String errMsg) {
                    KLog.e("failed ,ret=" + ret + ",errMsg " + errMsg);
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            //mViewUtils.toast(CommonUtils.getErrMsg(ret));
                            mViewUtils.toast(errMsg);
                        }
                    });

                }
            });
        }
    }

    public boolean checkCode() {
        phoneStr = mEdForLoginPhoneNumber.getText().toString();
        mcode = mEdForCode.getText().toString();
        passwordStr = mEdForLoginPwd.getText().toString();
        if (!isGetCode) {
            mViewUtils.toast(getString(R.string.String_please_get_code));
            return false;
        } else if (TextUtils.isEmpty(mcode)) {
            mViewUtils.toast(getString(R.string.enter_code));
            return false;
        } else if (TextUtils.isEmpty(passwordStr)) {
            mViewUtils.toast(getString(R.string.password_empty));
            return false;
        } else if (passwordStr.length() > 12 || passwordStr.length() < 6) {
            mViewUtils.toast(getString(R.string.password_is_illegal));
            return false;
        } else {
            return true;
        }
    }

    public boolean checkEditText() {
        if (TextUtils.isEmpty(mEdForLoginPhoneNumber.getText().toString())) {
            mViewUtils.toast(getString(R.string.please_enter_phone_number));
            return false;
        }
        if (TextUtils.isEmpty(mEdForLoginPwd.getText().toString())) {
            mViewUtils.toast(getString(R.string.enter_password));
            return false;
        }
        return true;
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.LOGIN_SUCCESS:
                closeLoading();
                mViewUtils.toast(getString(R.string.login_success));
                finish();
                break;
        }
    }
}
