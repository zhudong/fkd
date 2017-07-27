package com.fuexpress.kr.ui.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.model.ActivityController;
import com.fuexpress.kr.ui.view.TitleBarView;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/5/10.
 */
public class CardPaymentSuccessActivity extends BaseActivity {

    private View rootView;
    private TextView cardPaymentOrderNoTv;
    private Button cardGoHomeBtn;
    private ImageView toBackIv;
    private String orderNumber;
    private TitleBarView titleBarView;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_card_payment_success, null);
        titleBarView = (TitleBarView) rootView.findViewById(R.id.pay_success_title_bar);
        titleBarView.setTitleText(getString(R.string.card_payment_success_title_bar_title));
        toBackIv = titleBarView.getIv_in_title_back();
        cardPaymentOrderNoTv = (TextView) rootView.findViewById(R.id.card_payment_order_no_tv);
        cardGoHomeBtn = (Button) rootView.findViewById(R.id.card_payment_go_home_btn);

        orderNumber = getIntent().getStringExtra("orderNumber");
        SysApplication app = (SysApplication) getApplication();
        String orderNo = app.getGiftCardOrderNo();
        if(!TextUtils.isEmpty(orderNo)){
            cardPaymentOrderNoTv.setText(orderNo);
        }
        if(!TextUtils.isEmpty(orderNumber)){
            cardPaymentOrderNoTv.setText(orderNumber);
        }
        ActivityController.cardAddActivity(this);
        cardGoHomeBtn.setOnClickListener(this);
        toBackIv.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_payment_go_home_btn:
                EventBus.getDefault().post(new BusEvent(BusEvent.GO_HOME_PAGE, null));
                ActivityController.finishActivityOutOfMainActivity();
                break;
            case R.id.iv_in_title_back:
                toShopCardForCardActivity();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            toShopCardForCardActivity();
        }
        return true;
    }

    public void toShopCardForCardActivity(){
        ActivityController.cardFinish();
//        Intent intent = new Intent();
//        intent.setClass(this, ShopCartForCardActivity.class);
//        startActivity(intent);
    }
}
