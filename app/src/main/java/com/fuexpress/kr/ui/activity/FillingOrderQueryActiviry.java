package com.fuexpress.kr.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.ui.fragment.FillingOrderQueryFragment;
import com.fuexpress.kr.ui.view.TitleBarView;

public class FillingOrderQueryActiviry extends BaseActivity {

    private View mRootView;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_filling_order_query_activiry, null);
        TitleBarView title_in_filling_order = (TitleBarView) mRootView.findViewById(R.id.title_in_filling_order);
        title_in_filling_order.getIv_in_title_back().setOnClickListener(this);
        TextView textView = title_in_filling_order.getmTv_in_title_back_tv();
        textView.setText(getString(R.string.string_for_my_balance_title));
        textView.setOnClickListener(this);
        return mRootView;
    }


    @Override
    public void initView() {
        setDefaultFragment();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_in_title_back_tv:
                finish();
                break;
        }
    }

    private void setDefaultFragment() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        FillingOrderQueryFragment fillingOrderQueryFragment = new FillingOrderQueryFragment(2);
        transaction.add(R.id.fl_in_filling_order_query, fillingOrderQueryFragment);
        transaction.commit();
    }
}
