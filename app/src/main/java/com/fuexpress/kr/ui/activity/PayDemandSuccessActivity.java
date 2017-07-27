package com.fuexpress.kr.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 17-4-6.
 */

public class PayDemandSuccessActivity extends BaseActivity {

    @BindView(R.id.card_payment_order_no_tv)
    TextView mCardPaymentOrderNoTv;
    @BindView(R.id.card_payment_go_home_btn)
    Button mCardPaymentGoHomeBtn;
    @BindView(R.id.title_iv_left)
    ImageView mTitleIvLeft;
    @BindView(R.id.title_tv_left)
    TextView mTitleTvLeft;
    @BindView(R.id.title_tv_center)
    TextView mTitleTvCenter;

    @Override
    public View setInitView() {
        View view = View.inflate(this, R.layout.activity_pay_demand_success, null);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mTitleIvLeft.setVisibility(View.VISIBLE);
        mTitleTvLeft.setVisibility(View.VISIBLE);
        mTitleTvCenter.setText(getString(R.string.pay_demand_success));
        mTitleTvLeft.setText(getString(R.string.to_pay));
    }

    @OnClick({R.id.title_iv_left, R.id.title_tv_left,R.id.card_payment_go_home_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.title_tv_left:
                finish();
                break;
            case R.id.card_payment_go_home_btn:
                finish();
                break;
        }
    }
}
