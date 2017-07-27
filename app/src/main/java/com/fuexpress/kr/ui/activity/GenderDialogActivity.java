package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.wheel.OnWheelChangedListener;
import com.fuexpress.kr.ui.view.wheel.WheelView;
import com.fuexpress.kr.ui.view.wheel.wheeladapter.ArrayWheelAdapter;
import com.fuexpress.kr.ui.view.wheel.wheeladapter.WheelViewForTimePick;

public class GenderDialogActivity extends BaseActivity implements OnWheelChangedListener {
    private View mView;
    private WheelView mWheelView01;
    private String[] mStrings;
    private String mGender;
    private TextView mTv_in_adress_dialog_config_btn;

    @Override
    public View setInitView() {
        mView = View.inflate(this, R.layout.activity_address_dialog, null);
        Window window = this.getWindow();//宽度为屏幕宽,位置为底部
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        mView.setMinimumWidth(UIUtils.getScreenWidthPixels(this));
        window.setAttributes(lp);
        return mView;
    }

    @Override
    public void initView() {
        mWheelView01 = (WheelView) mView.findViewById(R.id.wv_in_adress_dialog);
        mTv_in_adress_dialog_config_btn = (TextView) mView.findViewById(R.id.tv_in_adress_dialog_config_btn);
        mTv_in_adress_dialog_config_btn.setOnClickListener(this);
        mWheelView01.addChangingListener(this);
        initData();
    }

    private void initData() {
        mStrings = new String[2];
        mStrings[0] = getString(R.string.user_info_drtail_gender_male);
        mStrings[1] = getString(R.string.user_info_drtail_gender_female);
        setWheelViewAdapter();
    }

    private void setWheelViewAdapter() {
        mWheelView01.setVisibleItems(2);
        mWheelView01.setViewAdapter(new ArrayWheelAdapter<String>(this, mStrings));
        mGender = mStrings[0];
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mWheelView01) {
            int currentItem = mWheelView01.getCurrentItem();
            mGender = mStrings[currentItem];
        }
    }

    @Override
    public void onChanged(WheelViewForTimePick wheel, int oldValue, int newValue) {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_in_adress_dialog_config_btn:
                Intent intent = new Intent();
                intent.putExtra("gender", mGender);
                setResult(4, intent);
                finish();
                break;
        }

    }
}
