package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.activity.giftcard_order.MyGiftCardOrderActivity;
import com.fuexpress.kr.ui.view.TitleBarView;

public class MyBalanceActivity extends BaseActivity {

    private View mRootView;
    private RelativeLayout mRl_in_gift_card_account, mRl_in_gift_card_oder_check, mRl_in_gift_card_bander;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_my_balance, null);
        TitleBarView title_in_my_balance = (TitleBarView) mRootView.findViewById(R.id.title_in_my_balance);
        title_in_my_balance.getIv_in_title_back().setOnClickListener(this);
        TextView title_back_tv = title_in_my_balance.getmTv_in_title_back_tv();
        title_back_tv.setText(getString(R.string.main_me_tab_string));
        title_back_tv.setOnClickListener(this);
        //BalanceManager.getInstance().giftCardBalanceListRequest(AccountManager.getInstance().mUin);
        //BalanceManager.getInstance().getGiftCardOrderListRequest(0, 0, 1, 0);

        return mRootView;
    }


    @Override
    public void initView() {
        mRl_in_gift_card_account = (RelativeLayout) mRootView.findViewById(R.id.rl_in_gift_card_Account);
        mRl_in_gift_card_oder_check = (RelativeLayout) mRootView.findViewById(R.id.rl_in_gift_card_oder_check);
        mRl_in_gift_card_bander = (RelativeLayout) mRootView.findViewById(R.id.rl_in_gift_card_bander);
        mRl_in_gift_card_account.setOnClickListener(this);
        mRl_in_gift_card_oder_check.setOnClickListener(this);
        mRl_in_gift_card_bander.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.rl_in_gift_card_Account:
                startDDMActivity(BalanceAccountActivity.class, true);
                break;
            case R.id.rl_in_gift_card_oder_check:
                //startDDMActivity(FillingOrderQueryActiviry.class, true);
                startDDMActivity(MyGiftCardOrderActivity.class, true);
                break;
            case R.id.rl_in_gift_card_bander:
                startDDMActivity(BindingGiftCardActivity.class, true);
                break;
        }
    }
}
