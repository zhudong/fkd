package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.CommonUtils;
import com.fuexpress.kr.utils.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ParcelPaymentAtivity extends MbaseActivity {

    public static final String USE_BALANCE_FIRST = "use_balance_first";
    public static final String THIRD_PAY_TYPE = "third_pay_type";
    public static final int PAY_MENT_RESULT_CODE = 111;
    public static final String COUNT_BALANCE = "count_balance";

    @BindView(R.id.rb_use_balance)
    RadioButton mRbUseBalance;
    @BindView(R.id.rb_alipay)
    RadioButton mRbAlipay;
    @BindView(R.id.rb_weixin_pay)
    RadioButton mRbWeixinPay;
    @BindView(R.id.tv_balance)
    TextView mTvBalance;
    @BindView(R.id.ll_weixin_pay)
    LinearLayout mLlWeixinPay;
    @BindView(R.id.parcel_payment_coupon_layout)
    LinearLayout couponLayout;
    @BindView(R.id.parcel_payment_coupon_name_tv)
    TextView couponNameTv;
    private boolean mIsUserBalance;
    private int mThirdPayType;
    private float mBalance;

    @Override
    protected int getViewResId() {
        return R.layout.activity_parcel_payment_ativity;
    }

    @Override
    public void initView() {
        initTitle("", getString(R.string.String_parcel_pay_method), "");
        hintIVRight();
        mThirdPayType = (int) SPUtils.get(this, THIRD_PAY_TYPE, 1);
      /*  mIsUseBalance = (boolean) SPUtils.get(this, USE_BALANCE_FIRST, true);
        mRbUseBalance.setChecked(mIsUseBalance);*/
        mBalance = getIntent().getFloatExtra(COUNT_BALANCE, 0);
        mTvBalance.setText(UIUtils.getCurrency(this, mBalance));

        if (!CommonUtils.isWeixinAvilible(this)) {
            mLlWeixinPay.setVisibility(View.GONE);
        }

        switch (mThirdPayType) {
            case 1:
                mRbAlipay.setChecked(true);
                break;
            case 2:
                mRbWeixinPay.setChecked(true);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                String couponName = data.getStringExtra("couponName");
                couponNameTv.setText(couponName);
            }
        }
    }

    @OnClick({R.id.ll_use_balance, R.id.ll_alipay, R.id.ll_weixin_pay, R.id.btn_confirm, R.id.parcel_payment_coupon_layout})
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.ll_use_balance:
//                mIsUseBalance = !mIsUseBalance;
//                mRbUseBalance.setChecked(mIsUseBalance);
//                SPUtils.put(this, USE_BALANCE_FIRST, mIsUseBalance);
                break;
            case R.id.ll_alipay:
                mRbAlipay.setChecked(true);
                mRbWeixinPay.setChecked(false);
                mThirdPayType = 1;
                break;
            case R.id.ll_weixin_pay:
                mRbWeixinPay.setChecked(true);
                mRbAlipay.setChecked(false);
                mThirdPayType = 2;
                break;

            case R.id.btn_confirm:
                SPUtils.put(this, THIRD_PAY_TYPE, mThirdPayType);
                Intent intent = new Intent();
               /* intent.putExtra("payType", paymentType);
                intent.putExtra("isUseBalance", isUseBalance);
                intent.putExtra("balance", balance);*/
//                EventBus.getDefault().post(new BusEvent(BusEvent.PAYMENT_TYPE_CHANGE, ""));
//                setResult(PAY_MENT_RESULT_CODE, intent);
                finish();
                break;
            case R.id.parcel_payment_coupon_layout:
                Intent intent1 = new Intent();
                intent1.setClass(ParcelPaymentAtivity.this, CouponActivity.class);
                startActivityForResult(intent1, CouponActivity.CODE_RESULT_COUPON);
                break;
        }
    }


}