package com.fuexpress.kr.ui.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;

import de.greenrobot.event.EventBus;
import fksproto.CsUser;

/**
 * Created by Administrator on 2016-10-31.
 */
public class CouponExchangeActivity extends BaseActivity {

    private View rootView;
    private EditText exchangeCodeEt;
    private Button exchangeBtn;
    private ImageView backIv;
    private TextView backTv;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.coupon_exchange_activity, null);
        TitleBarView titleBarView = (TitleBarView) rootView.findViewById(R.id.coupon_exchange_titlebar);
        titleBarView.setTitleText(getString(R.string.exchange));
        backIv = titleBarView.getIv_in_title_back();
        backTv = titleBarView.getmTv_in_title_back_tv();
        backTv.setText(getString(R.string.coupon));

        exchangeCodeEt = (EditText) rootView.findViewById(R.id.coupon_exchange_edit_text);
        exchangeBtn = (Button) rootView.findViewById(R.id.coupon_exchange_btn);

        exchangeBtn.setOnClickListener(this);
        backIv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.coupon_exchange_btn:
                if(TextUtils.isEmpty(exchangeCodeEt.getText())){
                    return;
                }
                exchangeShippingCouponRequest(exchangeCodeEt.getText().toString());
                break;
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
        }
    }

    public void exchangeShippingCouponRequest(String couponCode){
        showLoading();
        CsUser.ExchangeShippingCouponRequest.Builder builder = CsUser.ExchangeShippingCouponRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().mBaseUserRequest);
        builder.setCouponcode(couponCode);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.ExchangeShippingCouponResponse>() {

            @Override
            public void onSuccess(final CsUser.ExchangeShippingCouponResponse response) {
                Log.d("CouponExhangeActivity", response.toString());
                closeLoading();
                EventBus.getDefault().post(new BusEvent(BusEvent.CODE_COUPON_EXCHANGE_SUCCESS, response.getMessage()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.makeText(CouponExchangeActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(final int ret, final String errMsg) {
                Log.d("CouponExhangeActivity", errMsg);
                closeLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.makeText(CouponExchangeActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
