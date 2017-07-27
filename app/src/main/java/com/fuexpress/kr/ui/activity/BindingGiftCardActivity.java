package com.fuexpress.kr.ui.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.MemberBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.BalanceManager;
import com.fuexpress.kr.ui.adapter.MemberAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.ClearEditText;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BindingGiftCardActivity extends BaseActivity {

    private View mRootView;
    private TextView mTv_in_binding_gift_card_can_use_money;
    private ClearEditText mEd_in_binding_gift_card_number;
    private String mCardNum;
    private CustomDialog.Builder dialog;
    private Switch mSw_member;
    private ListView mLv_member;
    private MemberAdapter mMemberAdapter;
    private RelativeLayout mRl_up_member;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_binding_gift_card, null);
        TitleBarView titleBarView = (TitleBarView) mRootView.findViewById(R.id.title_in_bind_gc);
        titleBarView.getIv_in_title_back().setOnClickListener(this);
        TextView title_back_tv = titleBarView.getmTv_in_title_back_tv();
        String titleExtra = getIntent().getStringExtra(Constants.RETURN_TITLE);
        title_back_tv.setText(TextUtils.isEmpty(titleExtra) ? getString(R.string.string_for_my_balance_title) : titleExtra);
        title_back_tv.setOnClickListener(this);
        mSw_member = (Switch) mRootView.findViewById(R.id.sw_member);
        //mSw_member.setOnClickListener(this);
        mSw_member.setChecked(false);
        mSw_member.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //testAnimatior();
                mLv_member.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mLv_member = (ListView) mRootView.findViewById(R.id.lv_member);
        mLv_member.setOnItemClickListener(this);
        mRl_up_member = (RelativeLayout) mRootView.findViewById(R.id.rl_up_member);
        /*TitleBarView titleBarView = new TitleBarView(mRootView);
        titleBarView.setTitle(getString(R.string.my_gift_card_tab_03));
        titleBarView.getImageViewLeft().setOnClickListener(this);
        titleBarView.setTextViewOnclickAndReturn(titleBarView.getTextViewTitleLeftShopCart(), this).setText(getString(R.string.String_my_value_card));*/

        return mRootView;
    }

    private void testAnimatior() {
        mLv_member.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mLv_member, View.TRANSLATION_Y, -100f, 0f);
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }

    @Override
    public void initView() {
        mTv_in_binding_gift_card_can_use_money = (TextView) mRootView.findViewById(R.id.tv_in_binding_gift_card_can_use_money);
        mEd_in_binding_gift_card_number = (ClearEditText) mRootView.findViewById(R.id.ed_in_binding_gift_card_number);
        Button btn_in_binding_gift_card_go_binding = (Button) mRootView.findViewById(R.id.btn_in_binding_gift_card_go_binding);
        btn_in_binding_gift_card_go_binding.setOnClickListener(this);
        initData();

    }

    private void initData() {
        BalanceManager.getInstance().getGiftCardBalanceRequest();
        BalanceManager.getInstance().redeemGiftCardRequest();
        //testMethod();
    }

    public void testMethod() {
        List<MemberBean> memberBeans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MemberBean memberBean = new MemberBean("VIP会员 ￥3000", i == 0);
            memberBeans.add(memberBean);
        }
        mMemberAdapter = new MemberAdapter(this, memberBeans);
        mLv_member.setAdapter(mMemberAdapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            /*case R.id.iv_title_back:
            case R.id.textview_title_left_shorcart:
                finish();
                break;*/
            case R.id.btn_in_binding_gift_card_go_binding:
                checkCardNum();
                break;
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.sw_member:
                boolean splitTrack = mSw_member.isChecked();
                LogUtils.e("这是选择状态:" + splitTrack);
                break;
        }
    }

    private void checkCardNum() {
        mCardNum = mEd_in_binding_gift_card_number.getText().toString().trim();
        if (TextUtils.isEmpty(mCardNum)) {
            return;
        }
        /*String matcher = StringUtils.getMatcher("[0-9a-zA-Z]+", mCardNum);
        LogUtils.e("这是用正则截取后:" + matcher);*/
        String[] split = mCardNum.split("\\-");
        String newCardNum = "";
        for (String aSplit : split) {
            newCardNum += aSplit;
        }
        //LogUtils.e("这是截取好多的:" + newCardNum);
        BalanceManager.getInstance().getGiftCardStoresiteRequest(newCardNum);
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);

        switch (event.getType()) {
            case BusEvent.GET_GIFT_CARD_BALANCE_COMPLETE:
                boolean isSuccess = event.getBooleanParam();
                if (isSuccess) {
                    mTv_in_binding_gift_card_can_use_money.setText(UIUtils.getCurrency(this, AccountManager.getInstance().getCurrencyCode(), BalanceManager.getInstance().mGiftcardaccount));
                }
                break;
            case BusEvent.GET_GIFT_CARD_STORESITE_COMPLETE:
                boolean isGetGiftCardStoresiteSuccess = event.getBooleanParam();
                if (isGetGiftCardStoresiteSuccess) {
                    String whereFrom = event.getStrParam();
                    String cyCode = event.getStrParam02();
                    //Toast.makeText(this, "来自" + whereFrom, Toast.LENGTH_SHORT).show();
                    showCyCodeDialog(cyCode, whereFrom);
                    //showDialog(whereFrom);
                } else {
                    setIsShowContentView(true);
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, event.getStrParam());
                    dissMissProgressDiaLog(1000);
                }
                break;
            case BusEvent.BINDING_GIFT_CARD_REQUEST_COMPLETE:
                boolean isBindSuccess = event.getBooleanParam();
                if (isBindSuccess) {
                    initData();
                    mEd_in_binding_gift_card_number.setText("");
                    AccountManager.getInstance().getUserinfo();
                    showProgressDiaLog(SweetAlertDialog.SUCCESS_TYPE, getString(R.string.my_gift_card_order_binding_success));
                    dissMissProgressDiaLog(1000);
                } else {
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.my_gift_card_order_binding_faile_text));
                    dissMissProgressDiaLog(1000);
                }
                break;
            case BusEvent.REDEEM_GIFT_CARD_COMPETE:
                boolean isRedeemSuccess = event.getBooleanParam();
                if (isRedeemSuccess) {
                    if (BalanceManager.getInstance().getUpFlag().equals("1")) {
                        mRl_up_member.setVisibility(View.VISIBLE);
                        mSw_member.setChecked(false);
                        List<MemberBean> memberGroupLists = BalanceManager.getInstance().getMemberGroupLists();
                        mMemberAdapter = new MemberAdapter(this, memberGroupLists);
                        mLv_member.setAdapter(mMemberAdapter);
                    }
                    mTv_in_binding_gift_card_can_use_money.setText(UIUtils.getCurrency(this
                            , AccountManager.getInstance().getCurrencyCode()
                            , BalanceManager.getInstance().getGiftcardaccount()));
                } else {
                    CustomToast.makeText(this, event.getStrParam(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showCyCodeDialog(String cyCode, final String whereFrom) {
        /*if (!AccountManager.getInstance().getCurrencyCode().equals(cyCode)) {
            dialog = new CustomDialog.Builder(this);
            dialog.setMessage(getString(R.string.my_gift_card_bingding_cycode_text, cyCode));
            dialog.setPositiveButton(getString(R.string.my_gift_card_order_binding_dialog_config), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    showDialog(whereFrom);

                }
            });
            dialog.setNegativeButton(getString(R.string.my_gift_card_order_binding_dialog_cancle), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    showDialog(whereFrom);
                }
            });
            dialog.create().show();
        } else {
            showDialog(whereFrom);
        }*/

        if (getString(R.string.gift_card_from_fkd).equals(whereFrom)) {
            if (!AccountManager.getInstance().getCurrencyCode().equals(cyCode)) {
                dialog = new CustomDialog.Builder(this);
                dialog.setMessage(getString(R.string.my_gift_card_bingding_cycode_text, cyCode));
                dialog.setPositiveButton(getString(R.string.my_gift_card_order_binding_dialog_config), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        bindingGiftCardInNet();

                    }
                });
                dialog.setNegativeButton(getString(R.string.my_gift_card_order_binding_dialog_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //showDialog(whereFrom);
                    }
                });
                dialog.create().show();
            } else
                bindingGiftCardInNet();
        } else {
            if (!TextUtils.isEmpty(whereFrom))
                showDialog(whereFrom);
            else
                bindingGiftCardInNet();
        }

    }

    public void showDialog(String s) {
        String fromThis = getString(R.string.gift_card_from_there);
        String dialogConfig = getString(R.string.my_gift_card_order_binding_dialog_config);
        String dialogcancle = getString(R.string.my_gift_card_order_binding_dialog_cancle);
        if (!fromThis.equals(s)) {
            dialog = new CustomDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.binding_gift_card_dialog_title_01) + s + getResources().getString(R.string.binding_gift_card_dialog_title_02));
            dialog.setPositiveButton(dialogConfig, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(BindingGiftCardActivity.this, "点击了确定", Toast.LENGTH_SHORT).show();
                    //GiftCardManager.getInstance().bindGiftCardRequest(mCardNum);
                    bindingGiftCardInNet();
                    dialog.dismiss();
                }
            });
            dialog.setNegativeButton(dialogcancle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.create().show();
        } else
            bindingGiftCardInNet();
    }

    private void bindingGiftCardInNet() {
        boolean checked = mSw_member.isChecked();
        int mID = checked ? mMemberAdapter.getChoosedMemberID() : -1;
        BalanceManager.getInstance().bindGiftCardRequest(checked, mID, mCardNum);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //CustomToast.makeText(this, "接口？不存在的" + position, Toast.LENGTH_SHORT).show();
        List<MemberBean> dataList = mMemberAdapter.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            MemberBean memberBean = dataList.get(i);
            memberBean.setIsSelected(i == position);
        }
        mMemberAdapter.setChoosedMemberID(position);
        mMemberAdapter.notifyDataSetChanged();
    }
}