package com.fuexpress.kr.ui.activity.help_send;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.activity.CouponActivity;
import com.fuexpress.kr.ui.activity.PaymentActivity;

import fksproto.CsUser;

public class ToHelpSendActivity extends BaseActivity {

    private HelpSendFragment fragment;

    @Override
    public View setInitView() {
        return View.inflate(this, R.layout.activity_to_help_send, null);
    }

    @Override
    public void initView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = new HelpSendFragment();
        transaction.add(R.id.fl_container, fragment);
        transaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Constants.PAYMENT_REQUEST_CODE && data != null) {
            {
                fragment.setPayInfor(true);
            }
        }
    }
}
