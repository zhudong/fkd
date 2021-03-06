package com.fuexpress.kr.ui.activity;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.MD5Util;
import com.fuexpress.kr.utils.SPUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChangePassWordActivity extends BaseActivity {

    private View mRootView;
    private TextView mTv_in_change_password_save_btn;
    private EditText mEd_in_change_password_01;
    private EditText mEd_in_change_password_02;
    private EditText mEd_in_change_password_03;
    private String mOldPassWord;
    private String mNewPassWord;
    private String mNewPassWordAgain;
    private TitleBarView mTitle_in_cpw;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_change_pass_word, null);
        mTitle_in_cpw = (TitleBarView) mRootView.findViewById(R.id.title_in_cpw);
        mTitle_in_cpw.getIv_in_title_back().setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void initView() {
        mTv_in_change_password_save_btn = (TextView) mRootView.findViewById(R.id.tv_in_change_password_save_btn);
        mTv_in_change_password_save_btn.setOnClickListener(this);
        mEd_in_change_password_01 = (EditText) mRootView.findViewById(R.id.ed_in_change_password_01);
        mEd_in_change_password_01.setTypeface(Typeface.DEFAULT);
        mEd_in_change_password_01.setTransformationMethod(new PasswordTransformationMethod());
        mEd_in_change_password_02 = (EditText) mRootView.findViewById(R.id.ed_in_change_password_02);
        mEd_in_change_password_02.setTypeface(Typeface.DEFAULT);
        mEd_in_change_password_02.setTransformationMethod(new PasswordTransformationMethod());
        mEd_in_change_password_03 = (EditText) mRootView.findViewById(R.id.ed_in_change_password_03);
        mEd_in_change_password_03.setTypeface(Typeface.DEFAULT);
        mEd_in_change_password_03.setTransformationMethod(new PasswordTransformationMethod());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_in_change_password_save_btn:
                if (checkEdTextViewIsRight()) {
                    showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, null);
                    AccountManager.getInstance().changePassWord(mOldPassWord, mNewPassWord);
                } else {
                    return;
                }
                break;
        }
    }

    private boolean checkEdTextViewIsRight() {
        mOldPassWord = mEd_in_change_password_01.getText().toString().trim();
        mNewPassWord = mEd_in_change_password_02.getText().toString().trim();
        mNewPassWordAgain = mEd_in_change_password_03.getText().toString().trim();
        if (TextUtils.isEmpty(mOldPassWord) || TextUtils.isEmpty(mNewPassWord) || TextUtils.isEmpty(mNewPassWordAgain)) {
            showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.change_pwd_warn_text_01));
            dissMissProgressDiaLog(1500);
            return false;
        } else {
            if (mNewPassWord.equals(mNewPassWordAgain)) {
                String pw = (String) SPUtils.get(this, Constants.USER_INFO.USER_PWD, "");
                String md5 = MD5Util.getMD5(mOldPassWord);
                String md51 = MD5Util.getMD5(md5);
                if (pw.equals(md51)) {
                    return true;
                } else {
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.change_pwd_warn_text_02));
                    dissMissProgressDiaLog(1500);
                    return false;
                }
                //return true;
            } else {
                showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.change_pwd_warn_text_03));
                dissMissProgressDiaLog(1500);
                return false;
            }
        }

    }


    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.CHANGE_PASS_WORD_COMPLETE:
                final boolean isSuccess = event.getBooleanParam();
                if (isSuccess) {
                    changeDiagLogAlertType(SweetAlertDialog.SUCCESS_TYPE, getString(R.string.user_info_drtail_dialog_change_user_info_success));
                } else {
                    changeDiagLogAlertType(SweetAlertDialog.ERROR_TYPE, getString(R.string.change_pwd_fail_text));
                }
                dissMissProgressDiaLog(500);
                SysApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
                break;
        }
    }
}
