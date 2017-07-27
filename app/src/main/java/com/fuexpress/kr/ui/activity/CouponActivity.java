package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.adapter.CouponAdapter;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import fksproto.CsBase;
import fksproto.CsCard;
import fksproto.CsUser;


/**
 * Created by Administrator on 2016-10-20.
 */
public class CouponActivity extends BaseActivity {

    public static final String CODE_COUPON_RESPONSE = "code_coupon_response";
    public static final String CODE_COUPON = "code_coupon";
    public static final String SHIPPING_FEE = "shipping_fee";

    private View rootView;
    private TextView topTv;
    private ListView touponListView;
    private ImageView backIv;
    //    private TextView exchangeTv;
    public static final int CODE_RESULT_COUPON = 1001;
    private String currencyCode;
    private float fee;
    private LinearLayout emptyLayout;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_coupon_layout, null);
        TitleBarView titleBar = (TitleBarView) rootView.findViewById(R.id.coupon_titlebarview);
        titleBar.setTitleText(getString(R.string.coupon_title_msg));
//        exchangeTv = titleBar.getTv_in_title_right();
//        exchangeTv.setVisibility(View.VISIBLE);
//        exchangeTv.setText(getString(R.string.coupon_exchange_msg));

        backIv = titleBar.getIv_in_title_back();
        topTv = (TextView) rootView.findViewById(R.id.coupon_top_tv);
        touponListView = (ListView) rootView.findViewById(R.id.coupon_listview);
        emptyLayout = (LinearLayout) rootView.findViewById(R.id.coupon_empty_layout);
//        currencyCode = getIntent().getStringExtra(ParcelPaymentAtivity.CURRENCY_CODE);
        fee = getIntent().getFloatExtra(SHIPPING_FEE, 0.00f);
        currencyCode = getIntent().getStringExtra(PaymentActivity.CURRENCY_CODE);
        init(fee);
        backIv.setOnClickListener(this);
//        exchangeTv.setOnClickListener(this);
        return rootView;
    }

    public void init(float fee) {
        useableCouponListRequest(TextUtils.isEmpty(currencyCode) ? "CNY" : currencyCode, fee);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
                finish();
                break;
            case R.id.tv_in_title_right:
//                Intent intent = new Intent(this, CouponExchangeActivity.class);
//                startActivity(intent);
                break;
        }
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        if (event.getType() == BusEvent.CODE_COUPON_EXCHANGE_SUCCESS) {
            useableCouponListRequest(TextUtils.isEmpty(currencyCode) ? "CNY" : currencyCode, fee);
        }
    }

    public void useableCouponListRequest(String currencyCode, float shippingFee) {
        showLoading();
        CsUser.UseableCouponListRequest.Builder builder = CsUser.UseableCouponListRequest.newBuilder();
        CsBase.BaseUserRequest.Builder bur = AccountManager.getInstance().mBaseUserRequest;
//        bur.setUin(102457);
        builder.setUserHead(bur);
        builder.setCurrencycode(currencyCode);
        builder.setShippingfee(shippingFee);

        NetEngine.postRequest(builder, new INetEngineListener<CsUser.UseableCouponListResponse>() {

            @Override
            public void onSuccess(final CsUser.UseableCouponListResponse response) {
                closeLoading();
                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CsUser.UseableCouponListResponse response = (CsUser.UseableCouponListResponse) msg.obj;
//            if (TextUtils.equals(currencyCode, "CNY")) {
//                topTv.setText(getString(R.string.coupon_currency_name_cny) + getString(R.string.coupon_currency_msg2, response.getCount()));
//            }
//            if (TextUtils.equals(currencyCode, "USD")) {
//                topTv.setText(getString(R.string.coupon_currency_name_usd) + getString(R.string.coupon_currency_msg2, response.getCount()));
//            }
//            if (TextUtils.equals(currencyCode, "KRW")) {
//                topTv.setText(getString(R.string.coupon_currency_name_krw) + getString(R.string.coupon_currency_msg2, response.getCount()));
//            }
            topTv.setText(response.getCurrencyname() + getString(R.string.coupon_currency_msg2, response.getCount()));
            if(response.getCouponlistCount() > 0){
                emptyLayout.setVisibility(View.GONE);
            }else {
                emptyLayout.setVisibility(View.VISIBLE);
            }
            CouponAdapter adapter = new CouponAdapter(CouponActivity.this, response.getCouponlistList(), currencyCode);
            touponListView.setAdapter(adapter);
            touponListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CsUser.CouponList item = (CsUser.CouponList) parent.getAdapter().getItem(position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(CODE_COUPON, item);
                    setResult(RESULT_OK, new Intent().putExtras(bundle));
                    finish();
                }
            });
        }
    };
}
