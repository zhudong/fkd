package com.fuexpress.kr.ui.activity.login_register;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.RequestNetListenerWithMsg;
import com.fuexpress.kr.ui.activity.ChooseCountryActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.google.protobuf.GeneratedMessage;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kevin.xie on 2016/10/31.
 */

public class BindThirdPlatformActivity extends BaseActivity {
    @BindView(R.id.title_tv_left)
    TextView mTitleTvLeft;
    @BindView(R.id.title_tv_center)
    TextView mTitleTvCenter;
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
    @BindView(R.id.ed_for_login_pwd)
    EditText mEdForLoginPwd;
    @BindView(R.id.logoutButton)
    Button mLogoutButton;
    @BindView(R.id.tv_for_login_forgetpwd)
    TextView mTvForLoginForgetpwd;
    @BindView(R.id.showAnotherLoginMethod)
    LinearLayout mShowAnotherLoginMethod;

    private String phone_number,phone_password;

    @Override
    public View setInitView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_login_phone, null);

        return view;
    }
    @Override
    protected void onResume(){
        super.onResume();
        mTvForLoginLocation.setText(AccountManager.getInstance().userCountry);
        mTvForLoginInRl02.setText("+"+AccountManager.getInstance().userNumber);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mShowAnotherLoginMethod.setVisibility(View.GONE);
        mTitleTvCenter.setText(getString(R.string.bind_account));
        mTitleTvLeft.setText(getString(R.string.cancel));
        mTitleTvLeft.setVisibility(View.VISIBLE);
        mLogoutButton.setText(getString(R.string.check_login));
        mEdForLoginPwd.setTypeface(Typeface.DEFAULT);
        mEdForLoginPwd.setTransformationMethod(new PasswordTransformationMethod());
    }

    @OnClick({R.id.title_tv_left, R.id.rl_for_login_choose_location, R.id.logoutButton,R.id.tv_for_login_forgetpwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_tv_left:
                onBackPressed();
                break;
            case R.id.rl_for_login_choose_location:
                toActivity(ChooseCountryActivity.class);
                break;
            case R.id.logoutButton:
                login();
                break;
            case R.id.tv_for_login_forgetpwd:
                toActivity(FindPasswordActivity.class);
                break;
        }
    }
    private void login() {
        KLog.i( "登录");
        phone_number = mEdForLoginPhoneNumber.getText().toString();
        phone_password = mEdForLoginPwd.getText().toString();
        if (!TextUtils.isEmpty(phone_number) && !TextUtils.isEmpty(phone_password)) {
            KLog.i( "phone = " + phone_number + " pwd = " + phone_password);
            AccountManager.getInstance().bindThirdPlatform(AccountManager.TYPE_PHONE, phone_number, phone_password, new RequestNetListenerWithMsg() {
                @Override
                public void onSuccess(GeneratedMessage response) {
                    KLog.i("绑定成功",response.toString());
                    toActivity(MainActivity.class);
                }

                @Override
                public void onFailure(int ret,final String msg) {
                    KLog.i("绑定失败",msg);
                    mViewUtils.toast(msg);
                }
            });
        }else{
            mViewUtils.toast(getString(R.string.String_error_notice));
        }
    }
}
