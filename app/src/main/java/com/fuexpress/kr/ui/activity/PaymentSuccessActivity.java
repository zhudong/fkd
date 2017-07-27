package com.fuexpress.kr.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.view.TitleBarView;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/4/27.
 */
public class PaymentSuccessActivity extends BaseActivity {
    private View rootView;

    private TextView orderNumberTv;
    private TextView shippingTv;
    private TextView msgTv;
    private Button shoppingBtn;
    private SysApplication app;
    private ImageView toBackIv;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_payment_success, null);
        TitleBarView titleBarView = new TitleBarView(this);
        titleBarView.setTitleText(getString(R.string.card_payment_success_title_bar_title));
        toBackIv = titleBarView.getIv_in_title_back();

        orderNumberTv = (TextView) rootView.findViewById(R.id.payment_success_order_number_tv);
        shippingTv = (TextView) rootView.findViewById(R.id.payment_success_shipping_tv);
        msgTv = (TextView) rootView.findViewById(R.id.payment_success_message_tv);
        shoppingBtn = (Button) rootView.findViewById(R.id.payment_success_shopping_btn);

        app = (SysApplication) getApplication();
        orderNumberTv.setText(app.getOrderNumber());
        shippingTv.setText(app.getShippingScheme() == Constants.SHIPPING_SCHEME_DIRECT
                ? getResources().getString(R.string.String_direct_mail_1)
                : getResources().getString(R.string.String_merge_order));
        if(app.getOrderType() == 1){
            msgTv.setText(getResources().getString(R.string.pay_result_payment_success_crowd_title));

        }
        if(app.getOrderType() == 0){
            msgTv.setText(getResources().getString(R.string.pay_result_payment_success_order_title));
        }

        shoppingBtn.setOnClickListener(this);
        toBackIv.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.payment_success_shopping_btn:
                EventBus.getDefault().post(new BusEvent(BusEvent.GO_PRODUSRC_PAGE, null));
                break;
            case R.id.iv_title_back:
                finish();
                break;
        }
    }

}
