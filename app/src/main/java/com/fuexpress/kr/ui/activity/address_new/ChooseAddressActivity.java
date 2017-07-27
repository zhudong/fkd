package com.fuexpress.kr.ui.activity.address_new;

import android.view.View;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;

public class ChooseAddressActivity extends BaseActivity {

    private View mRootView;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_choose_address, null);
        return mRootView;
    }

    @Override
    public void initView() {

    }
}
