/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fuexpress.kr.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.OrderConstants;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.PayMethodManager;
import com.fuexpress.kr.ui.activity.CouponActivity;
import com.fuexpress.kr.ui.activity.ParcelPayMethodActivity;
import com.fuexpress.kr.ui.activity.order_detail.OrderDetailPandingActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.TitleBarView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fksproto.CsUser;


public class ButtomFragment extends MyBottomSheetDialogFragment {

    public static final String REMIXER_TAG = "Remixer";
    private String mPayCode;
    private OrderDetailPandingActivity mActivity;
    private float mBalance;


    public static ButtomFragment newInstance() {
        return new ButtomFragment();
    }

    private boolean isAddingFragment = false;
    private final Object syncLock = new Object();

    public static final String SHIPPING_FEE = "shipping_fee";
    public static final String CURRENCY_CODE = "currencyCode";
    public static final String USE_COUPON = "use_coupon";
    String currencyCode;
    @BindView(R.id.payment_titile_bar)
    TitleBarView titleBar;
    @BindView(R.id.rb_use_balance)
    CheckedTextView mChBalance;
    @BindView(R.id.tv_balance)
    TextView mTvBalance;
    @BindView(R.id.ll_content)
    LinearLayout mLlContent;
    @BindView(R.id.payment_coupon_name_tv)
    TextView mPaymentCouponNameTv;
    @BindView(R.id.payment_coupon_layout)
    LinearLayout mPaymentCouponLayout;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;
    private PayMethodManager mPayMethodManager;
    private int mIndex = -1;
    private boolean isUseBalance = false;
    private float mShippingFee;


    public void init() {
//        mChBalance.setdrawselector_payment_checkbox
        mPayMethodManager = PayMethodManager.getInstance(getContext());
//        currencyCode = getIntent().getStringExtra(CURRENCY_CODE);
//        mShippingFee = getIntent().getFloatExtra(SHIPPING_FEE, 0f);
//        mUseCoupon = getIntent().getBooleanExtra(USE_COUPON, true);
        mActivity = (OrderDetailPandingActivity) getActivity();
        mPayCode = mActivity.getPayCode();

        isUseBalance = mPayMethodManager.isUseBalance();
        mChBalance.setChecked(true);
        mChBalance.setClickable(false);

        mTvBalance.setText(getString(R.string.pay_by_gift_card) + ":" + UIUtils.getCurrency(getContext(), currencyCode, mBalance));
        if (mBalance <= 0) ((View) mTvBalance.getParent()).setVisibility(View.GONE);
        ((View) titleBar.getTv_in_title_left().getParent()).setBackgroundColor(getResources().getColor(R.color.default_bg));
        titleBar.getTv_in_title_left().setVisibility(View.INVISIBLE);
        titleBar.getIv_in_title_back().setImageResource(R.mipmap.cancelx);
        titleBar.getTv_in_title().setTextColor(getResources().getColor(R.color.black));
        titleBar.getIv_in_title_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mPayMethodManager.getPayMethodList(new PayMethodManager.PayMethodResultListener() {
            @Override
            public void onResult(String payCode, String result) {

            }

            @Override
            public void onMethodList(final List<CsUser.PaymentList> paymantlistList, final String balance, final int couponCount) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
//                        mTvBalance.setText(balance);
                        mLlContent.removeAllViews();
                        mPaymentCouponNameTv.setText(getString(R.string.coupon_count_msg, couponCount));
                        mPaymentCouponLayout.setVisibility(couponCount > 0 ? View.VISIBLE : View.GONE);
                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                mIndex = (int) v.getTag();
                                int tag = (int) v.getTag();
                                mIndex = -1;
                                for (int i = 0; i < mLlContent.getChildCount(); i++) {
                                    CheckedTextView child = (CheckedTextView) mLlContent.getChildAt(i).findViewById(R.id.payment_item_ct);
                                    if (tag == i) {
                                        if (paymantlistList.size() <= 1) {
                                            child.setChecked(true);
                                        } else {
                                            child.setChecked(!child.isChecked());
                                        }
                                    } else {
                                        child.setChecked(false);
                                    }
                                    if (child.isChecked()) {
                                        mIndex = tag;
                                    }
                                }
                            }
                        };

                        for (int i = 0; i < paymantlistList.size(); i++) {
//                            mIndex
                            CsUser.PaymentList item = paymantlistList.get(i);
                            if (item.getPaycode().equals(mPayCode) & mIndex == -1) {
                                mPayMethodManager.setMethodIndex(i);
                            }
                        }
                        for (int i = 0; i < paymantlistList.size(); i++) {
                            CsUser.PaymentList item = paymantlistList.get(i);
                            View convertView = LayoutInflater.from(getContext()).inflate(R.layout.payment_item, null);
                            mLlContent.addView(convertView);
                            convertView.setOnClickListener(listener);
                            convertView.setTag(i);
                            ImageView logoIv = (ImageView) convertView.findViewById(R.id.payment_item_logo_iv);
                            TextView nameTv = (TextView) convertView.findViewById(R.id.payment_item_name_tv);
                            CheckedTextView checkedTv = (CheckedTextView) convertView.findViewById(R.id.payment_item_ct);

                            checkedTv.setCheckMarkDrawable(R.drawable.selector_payment_checkbox);
                            if (i == mPayMethodManager.getMethodIndex()) {
                                checkedTv.setChecked(true);
                                mIndex = i;
                            } else {
                                checkedTv.setChecked(false);
                            }

                            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_ADYEN)) {
//                                nameTv.setText(getString(R.string.String_adyen_pay));
                                logoIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.adyen_pay));
                            }
                            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_KRBANK)) {
//                                nameTv.setText(getString(R.string.String_krbank_pay));
                                logoIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.krbank_pay));
                            }
                            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_ALIPAY)) {
//                                nameTv.setText(getString(R.string.String_ali_pay));
                                logoIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.pay_alipay));
                            }
                            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_WXPAY)) {
//                                nameTv.setText(getString(R.string.String_weixin_pay));
                                logoIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.pay_wechat));
                            }
                            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
//                                nameTv.setText(item.getPayname());
                                logoIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.daoupay));
                            }
                            nameTv.setText(item.getPayname());

                        }
                    }
                });
            }
        }, mShippingFee, currencyCode, false);
    }


    @OnClick({R.id.rb_use_balance, R.id.btn_confirm, R.id.payment_coupon_layout})
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_confirm:
                mPayMethodManager.setCurrentMethod(mIndex);
                mPayMethodManager.setUseBalance(isUseBalance);
//                setResult(Constants.PAYMENT_REQUEST_CODE, intent);
                mActivity.setPayCode(mPayMethodManager.getCurrentPayCode());
                CsUser.PaymentList currentPayMethod = mPayMethodManager.currentPayMethod;
                mActivity.showPayMethod(currentPayMethod != null ? currentPayMethod.getPayname() : "");
                dismiss();
                break;
            case R.id.rb_use_balance:
                isUseBalance = !isUseBalance;
                mPayMethodManager.setUseBalance(isUseBalance);
                mChBalance.setChecked(isUseBalance);
                if (mIndex == -1) mChBalance.setChecked(true);
                break;
        }
    }

    public void setBalance(float balance) {
//        mBalance = balance;
        mBalance = 0;
    }

    public void attachToButton(final FragmentActivity activity, View button) {
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showRemixer(activity.getSupportFragmentManager(), REMIXER_TAG);
            }
        });
    }

    public void showRemixer(FragmentManager manager, String tag) {
        synchronized (syncLock) {
            if (!isAddingFragment && !isAdded()) {
                isAddingFragment = true;
                show(manager, tag);
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_paymethod_activity, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        isAddingFragment = false;
        super.onResume();
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setShippingFee(float shippingFee) {
        mShippingFee = shippingFee;
    }
}
