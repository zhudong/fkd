package com.fuexpress.kr.ui.activity.login_register;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
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

public class FindPasswordActivity extends BaseActivity {
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
    private CountDownTimer timer;
    private String phoneStr, codeStr, pwdStr;

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
        mTitleTvLeft.setText(getString(R.string.cancel));
        mTitleTvLeft.setVisibility(View.VISIBLE);
        mTitleTvCenter.setText(getString(R.string.find_password));
        mLogoutButton.setText(getString(R.string.check_login));
        mEdForLoginPwd.setTypeface(Typeface.DEFAULT);
        mEdForLoginPwd.setTransformationMethod(new PasswordTransformationMethod());
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mGetCodeBtn.setText(millisUntilFinished / 1000 + "S");
                mGetCodeBtn.setClickable(false);
            }

            @Override
            public void onFinish() {
                mGetCodeBtn.setText(getString(R.string.String_get_code));
                mGetCodeBtn.setClickable(true);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                break;
            case R.id.rl_for_login_choose_location:
                toActivity(ChooseCountryActivity.class);
                break;
            case R.id.getCodeBtn:
                phoneStr = mEdForLoginPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneStr)) {
                    mViewUtils.toast(getString(R.string.phone_is_empty));
                    return;
                }
                int countryNumber = Integer.valueOf(AccountManager.getInstance().userNumber);
                if (TextUtils.isEmpty(phoneStr) || !CountryNumberUtils.isNumberCurrect(countryNumber, phoneStr)) {
                    mViewUtils.toast(getString(R.string.String_phone_error));
                    return;
                }
                getCode();
                break;
            case R.id.logoutButton:
                resetPwd();
                break;
        }
    }

    private void getCode() {
        isGetCode = true;
        timer.start();
        CsLogin.AccountRequest.Builder builder = CsLogin.AccountRequest.newBuilder();
        //请求手机验证码
        builder.setOperacode(CsLogin.AccountOperacode.ACCOUNT_OPERACODE_APPLY_VERIFY_CODE_VALUE);
        builder.setAccountType(CsLogin.AccountType.ACCOUNT_TYPE_PHONE_VALUE);
        builder.setAccount(phoneStr);
        builder.setRandomKey(NetEngine.sRandomKey);
        builder.setReserve(CsLogin.VerifyCodeScene.VERIFY_CODE_SCENE_RESET_PASSWORD_VALUE + "");
        builder.setAccountExtra(AccountManager.getInstance().userNumber);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsLogin.AccountResponse>() {
            @Override
            public void onSuccess(CsLogin.AccountResponse response) {
                KLog.i(response.toString());
                //成功获取验证码

            }

            public void onFailure(final int ret, final String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast(errMsg);
                    }
                });

            }
        });
    }

    public boolean checkEditText() {
        phoneStr = mEdForLoginPhoneNumber.getText().toString();
        codeStr = mEdForCode.getText().toString();
        pwdStr = mEdForLoginPwd.getText().toString();
        KLog.i("phoneStr = " + phoneStr + " codeStr = " + codeStr + " pwdStr = " + pwdStr);
        if (TextUtils.isEmpty(phoneStr)) {
            mViewUtils.toast(getString(R.string.String_enter_phone));
            return false;
        }
        if (TextUtils.isEmpty(codeStr)) {
            mViewUtils.toast(getString(R.string.enter_code));
            return false;
        }
        if (TextUtils.isEmpty(pwdStr)) {
            mViewUtils.toast(getString(R.string.enter_password));
            return false;
        }
        if (pwdStr.length() > 12 || pwdStr.length() < 6) {
            mViewUtils.toast(getString(R.string.password_is_illegal));
            return false;
        }

        return true;
    }

    public void resetPwd() {
        if (!isGetCode) {
            mViewUtils.toast(getString(R.string.String_please_get_code));
            return;
        }
        if (checkEditText()) {
            CsLogin.AccountRequest.Builder builder = CsLogin.AccountRequest.newBuilder();
            //请求手机验证码
            builder.setOperacode(CsLogin.AccountOperacode.ACCOUNT_OPERACODE_PHONE_RESET_PASSWORD_VALUE);
            builder.setAccountType(CsLogin.AccountType.ACCOUNT_TYPE_PHONE_VALUE);
            builder.setAccount(phoneStr);
            builder.setRandomKey(NetEngine.sRandomKey);
            builder.setVerifyCode(codeStr);
            builder.setPassword(MD5Util.getMD5(pwdStr).toLowerCase());
            builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
            if (phoneStr.startsWith("18")) {
                builder.setAccountExtra(/*"+" +*/ AccountManager.getInstance().userNumber);
            } else {
                builder.setAccountExtra(AccountManager.getInstance().userNumber);
            }
            KLog.i("phone =" + phoneStr + " code = " + codeStr);
            NetEngine.postRequest(builder, new INetEngineListener<CsLogin.AccountResponse>() {
                @Override
                public void onSuccess(CsLogin.AccountResponse response) {
                    KLog.i(response.toString());
                    //注册成功，开始登录
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            mViewUtils.toast(getString(R.string.change_password_success));
                            AccountManager.getInstance().login(CsLogin.AccountType.ACCOUNT_TYPE_PHONE, phoneStr, pwdStr, null, FindPasswordActivity.this);
                            onBackPressed();
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
}
