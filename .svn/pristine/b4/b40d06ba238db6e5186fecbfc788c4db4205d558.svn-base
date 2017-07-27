package com.fuexpress.kr.ui.activity.help_signed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.ui.view.TitleBarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Longer on 2016/12/26.
 * 添加需求成功之后的页面
 */
public class HSSuccessFragment extends BaseFragment<HelpMeSignedActivity> {


    @BindView(R.id.title_sub_success)
    TitleBarView mTitleSubSuccess;
    @BindView(R.id.btn_back_home)
    Button mBtnBackHome;

    @Override
    protected void initTitleBar() {

    }

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.sub_demand_success_layout, null);
    }

    @Override
    public void initData() {
        mTitleSubSuccess.getIv_in_title_back().setOnClickListener(this);
        //TextView tv_in_title_back_tv = mTitleSubSuccess.getmTv_in_title_back_tv();
        //tv_in_title_back_tv.setText();
    }

    @OnClick({R.id.iv_in_title_back, R.id.btn_back_home})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_in_title_back:
                //case R.id.tv_in_title_back_tv:
                //break;
            case R.id.btn_back_home:
                mContext.finish();
                break;
        }
    }
}
