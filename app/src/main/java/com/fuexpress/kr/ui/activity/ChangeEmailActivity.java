package com.fuexpress.kr.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.ui.view.ClearEditTextPlus;
import com.fuexpress.kr.ui.view.TitleBarView;

public class ChangeEmailActivity extends BaseActivity {

    private View mRootView;

    private Button mBtn_in_change_email_config;
    private ClearEditTextPlus mEd_in_change_email_new_email;
    private ClearEditTextPlus mEd_in_change_email_new_email_again;


    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_change_email, null);
        TitleBarView title_in_change_email = (TitleBarView) mRootView.findViewById(R.id.title_in_change_email);
        title_in_change_email.getIv_in_title_back().setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void initView() {
        mEd_in_change_email_new_email = (ClearEditTextPlus) mRootView.findViewById(R.id.ed_in_change_email_new_email);
        mEd_in_change_email_new_email_again = (ClearEditTextPlus) mRootView.findViewById(R.id.ed_in_change_email_new_email_again);
        mBtn_in_change_email_config = (Button) mRootView.findViewById(R.id.btn_in_change_email_config);
        mBtn_in_change_email_config.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_in_change_email_config:
                checkEmail();
                break;
        }
    }

    private void checkEmail() {
        String newEmail = mEd_in_change_email_new_email.getText().toString().trim();
        String newEmailAgain = mEd_in_change_email_new_email_again.getText().toString().trim();
        if (newEmail == null || newEmailAgain == null || TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(newEmailAgain)) {
            mBtn_in_change_email_config.setClickable(false);
            return;
        } else if (!newEmail.equals(newEmailAgain)) {
            mBtn_in_change_email_config.setClickable(false);
            return;
        } else {
            // TODO: 2016/10/31 因为接口没有好,暂时注释
            //UserManager.getInstance().setUserInfo(CsUser.UserInfoFieldType.USER_INFO_FIELD_EMAIL_VALUE, newEmail);
        }

    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            // TODO: 2016/10/31 因为接口没有好,暂时注释
            /*case BusEvent.CHANGE_EMAIL_COMPLETE:
                boolean isSuccess = event.getBooleanParam();
                if (isSuccess) {
                    showProgressDiaLog(SweetAlertDialog.SUCCESS_TYPE, "修改成功!");
                } else {
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, "失败,该邮箱已存在!");
                }
                dissMissProgressDiaLog(1500);
                break;*/
        }
    }
}