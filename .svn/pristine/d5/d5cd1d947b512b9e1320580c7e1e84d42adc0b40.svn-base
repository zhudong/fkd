package com.fuexpress.kr.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import fksproto.CsUser;

import static com.fuexpress.kr.base.SysApplication.mContext;

/**
 * Created by kevin.xie on 2016/11/17.
 */

public class KrBankInfoActivity extends BaseActivity {
    public static final String NEED_PAY_COUNT = "needPayCount";
    public static final String BALANCE = "balance";
    public static final String PAYMENT = "payment";
    public static final String SHIPPING_FEE_TOTAL = "total";
    public static final String PAY_CURRENCY_CODE = "code";
    public static final String NOTICE_SALES_ID = "notice";
    public static final String FROM_DEMANDS_LIST="from_demands_list";
    public static int FROM_DEMAND_INT=1;
    @BindView(R.id.title_iv_left)
    ImageView mTitleIvLeft;
    @BindView(R.id.title_tv_center)
    TextView mTitleTvCenter;
    @BindView(R.id.bank_name_tv)
    TextView mBankNameTv;
    @BindView(R.id.receive_name_tv)
    TextView mReceiveNameTv;
    @BindView(R.id.bank_account_tv)
    TextView mBankAccountTv;
    @BindView(R.id.count)
    TextView mCount;
    @BindView(R.id.payment_confirm_btn)
    Button mPaymentConfirmBtn;
    @BindView(R.id.total_tv)
    TextView mTotalTv;
    @BindView(R.id.payment_tv)
    TextView mPaymentTv;
    @BindView(R.id.balance_tv)
    TextView mBalanceTv;
    @BindView(R.id.need_pay_tv)
    TextView mNeedPayTv;
    @BindView(R.id.payment_fee_layout)
    LinearLayout mPaymentFeeLayout;
    @BindView(R.id.red_notice)
    TextView mRedNotice;
    @BindView(R.id.kr_layout)
    LinearLayout mKrLayout;
    @BindView(R.id.bank_receiver_info_layout)
    LinearLayout mBankReceiverInfoLayout;
    @BindView(R.id.krbank_notice_layout)
    LinearLayout mKrbankNoticeLayout;
    int where;

    private float needPayCount;
    private String mCurrencyCode;

    @Override
    public View setInitView() {
        View view = View.inflate(this, R.layout.activity_krbank_info, null);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mTitleTvCenter.setText(getString(R.string.cart_confirm_title_bar_title));
        mTitleIvLeft.setVisibility(View.VISIBLE);
        mPaymentConfirmBtn.setText(getString(R.string.notice_customer_service));
        mPaymentConfirmBtn.setTextColor(Color.WHITE);
        needPayCount = getIntent().getFloatExtra(NEED_PAY_COUNT, 0);
        float total = getIntent().getFloatExtra(SHIPPING_FEE_TOTAL, 0);
        float balance = getIntent().getFloatExtra(BALANCE, 0);
        String str=getIntent().getStringExtra(NOTICE_SALES_ID);
        if(!TextUtils.isEmpty(str)){
            mRedNotice.setText(str);
        }
        where=getIntent().getIntExtra(FROM_DEMANDS_LIST,-1);
        if(where==FROM_DEMAND_INT){
            mPaymentConfirmBtn.setText(getString(R.string.back_to_demands_list));
        }
        mPaymentConfirmBtn.setText(getString(R.string.notice_customer_service));
        needPayCount = total - balance;
        mCurrencyCode = getIntent().getStringExtra(PAY_CURRENCY_CODE);
        String payment = getIntent().getStringExtra(PAYMENT);
        if (!TextUtils.isEmpty(payment)) {
            mPaymentTv.setText(payment);
        }
        mBalanceTv.setText(UIUtils.getCurrency(this, mCurrencyCode, balance));
        mTotalTv.setText(UIUtils.getCurrency(this, mCurrencyCode, total));
        mNeedPayTv.setText(UIUtils.getCurrency(this, mCurrencyCode, needPayCount));
//        needPayCount = UIUtils.getCurrency(this, sign, total);
        EventBus.getDefault().post(new BusEvent(BusEvent.Append_PARCEN_SUCESS, ""));
        getKrBankInfo();
    }

    public void getKrBankInfo() {
        CsUser.GetKrBankInfoRequest.Builder builder = CsUser.GetKrBankInfoRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetKrBankInfoResponse>() {
            @Override
            public void onSuccess(final CsUser.GetKrBankInfoResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mBankNameTv.setText(response.getBankName());
                        mBankAccountTv.setText(response.getAcountNumber());
                        mReceiveNameTv.setText(response.getAcountName());
                        mCount.setText(UIUtils.getCurrency(KrBankInfoActivity.this, mCurrencyCode, needPayCount));
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast(getString(R.string.get_krbank_info_failure));
                    }
                });
            }
        });
    }

    @OnClick({R.id.title_iv_left, R.id.payment_confirm_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                onBackPressed();
                break;
            case R.id.payment_confirm_btn:
                String title = getString(R.string.app_name);
                // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
                // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
                ConsultSource source = new ConsultSource("123", "fkd", "custom information string");
                // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
                Unicorn.openServiceActivity(mContext, // 上下文
                        title, // 聊天窗口的标题
                        source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
                );
                finish();
                break;
        }
    }
}
