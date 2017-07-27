package com.fuexpress.kr.ui.activity.giftcard_order;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.ui.view.SwitcherText;
import com.fuexpress.kr.ui.view.TitleBarView;

import java.util.List;

public class MyGiftCardOrderActivity extends BaseActivity implements SwitcherText.OnSwitchListener {


    public static final int GIFT_CARD = 0;//充值卡状态
    public static final int DIRECT_RECHARGE = 1;//直充状态
    private SwitcherText mSwitchTextView;
    private FragmentManager mFragmentManager;
    public boolean isFragmentChange = false;
    public int currentTab = 0;

    @Override
    public View setInitView() {
        View rootView = View.inflate(this, R.layout.activity_my_gift_card_order, null);
        TitleBarView title_in_filling_order = (TitleBarView) rootView.findViewById(R.id.title_in_filling_order);
        title_in_filling_order.getIv_in_title_back().setOnClickListener(this);
        TextView textView = title_in_filling_order.getmTv_in_title_back_tv();
        //textView.setText(getString(R.string.string_for_my_balance_title));
        textView.setOnClickListener(this);
        mSwitchTextView = title_in_filling_order.getSwitcherText();
        mSwitchTextView.setText(getString(R.string.my_gift_card_order_title_text_01), getString(R.string.my_gift_card_order_title_text_02));
        mSwitchTextView.setOnSwitchListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        mFragmentManager = getSupportFragmentManager();
        //mTransaction = mFragmentManager.beginTransaction();
        commintFragment(0);
        //mTransaction.commitAllowingStateLoss();
        mSwitchTextView.setLeftSelected();
        mSwitchTextView.setRightTextcolorStyle(R.color.white);
        mSwitchTextView.setLeftTextcolorStyle(R.color.white);

    }

    private void commintFragment(int tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment frg : fragments) {
                fragmentTransaction.hide(frg);
            }
        }
        fragmentTransaction.show(getFragment(tag, fragmentTransaction));
        fragmentTransaction.commitAllowingStateLoss();
    }

    public Fragment getFragment(int tag, FragmentTransaction fragmentTransaction) {
        Fragment fragment = null;
        switch (tag) {
            case MyGiftCardOrderActivity.GIFT_CARD:
                String GIFT_CARD_TAG = "gift_card";
                fragment = mFragmentManager.findFragmentByTag(GIFT_CARD_TAG);
                if (fragment == null) {
                    fragment = new FragmentForGiftCard01(1);
                    fragmentTransaction.add(R.id.fl_in_my_gift_card_order_list, fragment, GIFT_CARD_TAG);
                }
                break;
            case MyGiftCardOrderActivity.DIRECT_RECHARGE:
                String DIRECT_RECHARGE_TAG = "direct_recharge";
                fragment = mFragmentManager.findFragmentByTag(DIRECT_RECHARGE_TAG);
                if (fragment == null) {
                    fragment = new FragmentForGiftCard01(2);
                    fragmentTransaction.add(R.id.fl_in_my_gift_card_order_list, fragment, DIRECT_RECHARGE_TAG);
                }
                break;
        }
        return fragment;
    }

    @Override
    public void switchLeft(SwitcherText view) {
        commintFragment(0);
        if (currentTab != 0) {
            currentTab = 0;
            isFragmentChange = true;
        } else {
            isFragmentChange = false;
        }
        //mTransaction.commitAllowingStateLoss();
    }

    @Override
    public void switchRight(SwitcherText view) {
        //LogUtils.e("我是第二个tab,我被点击了!");
        commintFragment(1);
        if (currentTab != 1) {
            currentTab = 1;
            isFragmentChange = true;
        } else {
            isFragmentChange = false;
        }
        //mTransaction.commitAllowingStateLoss();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_in_title_back_tv:
                finish();
                break;
        }
    }
}
