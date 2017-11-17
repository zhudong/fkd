package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.adapter.PaymentMenberAdapter;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.WeakHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsCard;

/**
 * Created by andy on 2017/8/9.
 */

public class PaymentGiftChargeView extends LinearLayout {
    @BindView(R.id.payment_gift_edit)
    EditText mPaymentGiftEdit;
    @BindView(R.id.payment_bind_btn)
    Button mPaymentBindBtn;
    @BindView(R.id.sw_member)
    Switch mSwMember;
    @BindView(R.id.rl_up_member)
    RelativeLayout mRlUpMember;
    @BindView(R.id.payment_vip_listview)
    ListView mPaymentVipListview;
    private int membergroupid;
    WeakHandler mHandler = new WeakHandler();

    public interface OnBindClick {
        void onResult(boolean success);
    }

    private OnBindClick mBindClick;

    public PaymentGiftChargeView(Context context) {
        this(context, null);
    }

    public PaymentGiftChargeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentGiftChargeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initView();
    }

    public void initView(String currencyCode) {
        View.inflate(getContext(), R.layout.payment_gift_charge, this);
        ButterKnife.bind(this);
        mSwMember.setChecked(false);
        mSwMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPaymentVipListview.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        redeemGiftCardRequest(currencyCode);
    }

    @OnClick(R.id.payment_bind_btn)
    public void onClick() {
        getGiftCardStoresiteRequest(mPaymentGiftEdit.getText().toString(), AccountManager.getInstance().getLocaleCode(), mSwMember.isChecked() ? 1 : 0, membergroupid);
    }

    public void redeemGiftCardRequest(String currencyCode) {
        CsCard.RedeemGiftCardRequest.Builder builder = CsCard.RedeemGiftCardRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(currencyCode);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.RedeemGiftCardReponse>() {

            @Override
            public void onSuccess(final CsCard.RedeemGiftCardReponse response) {
                runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.equals(response.getLevelupflag(), "0")) {
                            mRlUpMember.setVisibility(View.GONE);
                        }
                        if (TextUtils.equals(response.getLevelupflag(), "1")) {
                            mRlUpMember.setVisibility(View.VISIBLE);
                            final PaymentMenberAdapter adapter = new PaymentMenberAdapter(getContext(), response.getMemberGroupListList());
                            adapter.setCheckedAtPosition(0);
                            membergroupid = response.getMemberGroupList(0).getMembergroupid();
                            mPaymentVipListview.setAdapter(adapter);
                            mPaymentVipListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    adapter.setCheckedAtPosition(position);
                                    adapter.notifyDataSetChanged();
                                    membergroupid = ((CsCard.MemberGroupList) adapter.getItem(position)).getMembergroupid();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    public void getGiftCardStoresiteRequest(final String giftCard, String localeCode, final int levelUp, final int membergroupid) {
        CsCard.GetGiftCardStoresiteRequest.Builder builder = CsCard.GetGiftCardStoresiteRequest.newBuilder();
        builder.setGiftCard(giftCard);
        builder.setLocalecode(localeCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GetGiftCardStoresiteResponse>() {

            @Override
            public void onSuccess(CsCard.GetGiftCardStoresiteResponse response) {
                LogUtils.d(response.toString());
                bindGiftCardRequest(giftCard, levelUp, membergroupid);
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, errMsg);
                        dissMissProgressDiaLog(1000);
                        if (mBindClick != null) {
                            mBindClick.onResult(false);
                        }
                    }
                });
            }
        });
    }

    private void dissMissProgressDiaLog(int i) {
        ((BaseActivity) getContext()).dissMissProgressDiaLog(i);
    }

    private void showProgressDiaLog(int errorType, String errMsg) {
        ((BaseActivity) getContext()).showProgressDiaLog(errorType, errMsg);
    }

    public void bindGiftCardRequest(String giftCard, int levelUp, int membergroupid) {
        CsCard.BindGiftCardRequest.Builder builder = CsCard.BindGiftCardRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setGiftCard(giftCard);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        builder.setLevelup(levelUp);
        if (levelUp == 1) {
            builder.setMembergroupid(membergroupid);
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.BindGiftCardResponse>() {

            @Override
            public void onSuccess(CsCard.BindGiftCardResponse response) {
                LogUtils.d(response.toString());
                runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDiaLog(SweetAlertDialog.SUCCESS_TYPE, getResources().getString(R.string.my_gift_card_order_binding_success));
                        dissMissProgressDiaLog(1000);
//                        getAccountBalanceRequest(currencyCode);
                        if (mBindClick != null) {
                            mBindClick.onResult(true);
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getResources().getString(R.string.my_gift_card_order_binding_faile_text));
                        dissMissProgressDiaLog(1000);
                        if (mBindClick != null) {
                            mBindClick.onResult(false);
                        }
                    }
                });
            }
        });
    }

    private void runOnUi(Runnable runnable) {
        mHandler.post(runnable);
    }


    public void setOnBindClick(OnBindClick bindClick) {
        mBindClick = bindClick;
    }
}
