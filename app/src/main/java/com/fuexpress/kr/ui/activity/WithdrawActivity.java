package com.fuexpress.kr.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.bean.CouponCurrencyInfoData;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;
import com.google.protobuf.GeneratedMessage;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsMy;
import fksproto.CsUser;

/**
 * Created by Administrator on 2017-4-18.
 */

public class WithdrawActivity extends BaseActivity {
    private View rootView;
    private TitleBarView titleBarView;
    private ImageView backIv;
    private TextView backTv;

    private TextView balanceTv;
    private TextView pointsTv;
    private EditText amountEt;
    private EditText emailEt;
    private TextView currencyTv;
    private TextView minAmountTv;
    private Button withdrawBtn;
    private boolean amountVail, emailVail;
    private Bundle extras;
    private CsUser.CurrencyList data;
    private CsUser.GetCreditsDetailResponse response;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_withdraw, null);
        titleBarView = (TitleBarView) rootView.findViewById(R.id.withdraw_titlebar);
        backIv = titleBarView.getIv_in_title_back();
        backTv = titleBarView.getmTv_in_title_back_tv();
        backTv.setText(getString(R.string.integral_management_resources_1));

        balanceTv = (TextView) rootView.findViewById(R.id.withdraw_balance_tv);
        pointsTv = (TextView) rootView.findViewById(R.id.withdraw_points_tv);
        amountEt = (EditText) rootView.findViewById(R.id.withdraw_amount_edit);
        emailEt = (EditText) rootView.findViewById(R.id.withdraw_email_edit);
        currencyTv = (TextView) rootView.findViewById(R.id.withdraw_currency_tv);
        minAmountTv = (TextView) rootView.findViewById(R.id.withdraw_min_amount_tv);
        withdrawBtn = (Button) rootView.findViewById(R.id.withdraw_btn);

        init();
        backTv.setOnClickListener(this);
        backIv.setOnClickListener(this);
        withdrawBtn.setOnClickListener(this);
        amountEt.addTextChangedListener(amountWatcher);
        emailEt.addTextChangedListener(emailWatcher);
        return rootView;
    }

    public void init() {
        String email = SPUtils.getString(this, "emailStr", "");
        if(!TextUtils.isEmpty(email)){
            emailVail = true;
        }
        emailEt.setText(email);
        extras = getIntent().getExtras();
        if (extras != null) {
            data = (CsUser.CurrencyList) extras.getSerializable("couponCurrencyInfoData");
            response = (CsUser.GetCreditsDetailResponse) extras.getSerializable("currentGetCreditsDetailResponse");
            currencyTv.setText(data.getCurrencycode());
            if (response != null) {
                balanceTv.setText(response.getAmount());
                pointsTv.setText(response.getCredits() + "");
                minAmountTv.setText(getString(R.string.withdraw_waring_msg_2,
                        UIUtils.getCurrency(this, data.getCurrencycode(), Float.parseFloat(response.getMinchangecashnum()), 0, false)));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.withdraw_btn:
                if (Float.parseFloat(amountEt.getText().toString()) < Float.parseFloat(response.getMinchangecashnum())) {
                    showProgressDiaLog(SweetAlertDialog.WARNING_TYPE, getString(R.string.withdraw_waring_msg_2,
                            UIUtils.getCurrency(this, data.getCurrencycode(), Float.parseFloat(response.getMinchangecashnum()), 0, false)));
                    dissMissProgressDiaLog(1000);
                    return;
                }
                long amount = Long.parseLong(amountEt.getText().toString());
                creditsWithdrawRequest(amount, data.getCurrencycode(), emailEt.getText().toString());
                break;
        }
    }

    public void creditsWithdrawRequest(long changeAmount, String currencyCode, String sendEmail) {
        showLoading();
        CsMy.CreditsWithdrawRequest.Builder builder = CsMy.CreditsWithdrawRequest.newBuilder();
        builder.setUin(AccountManager.getInstance().mUin);
        builder.setChangeamount((int)changeAmount);
        builder.setCurrencycode(currencyCode);
        builder.setSendemail(sendEmail).setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsMy.CreditsWithdrawResponse>() {

            @Override
            public void onSuccess(CsMy.CreditsWithdrawResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        SPUtils.putString(WithdrawActivity.this, "emailStr", emailEt.getText().toString());
                        CustomToast.makeText(WithdrawActivity.this, getString(R.string.withdraw_success_msg), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        CustomToast.makeText(WithdrawActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private TextWatcher amountWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.d("----------------textChanged");
        }

        @Override
        public void afterTextChanged(Editable s) {
            LogUtils.d("----------------afterChanged");
            if (s.length() > 0) {
                amountVail = true;
            } else {
                amountVail = false;
            }
            changeBtnEnabled();
        }
    };

    private TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                emailVail = true;
            } else {
                emailVail = false;
            }
            changeBtnEnabled();
        }
    };

    public void changeBtnEnabled() {
        if (amountVail && emailVail)
            withdrawBtn.setEnabled(true);
        else
            withdrawBtn.setEnabled(false);

    }
}
