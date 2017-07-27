package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.SysApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by k550 on 2016/9/27.
 */

public class ShareSelectActiviy extends BaseActivity {
    @BindView(R.id.tv_for_login_sina)
    TextView mTvForLoginSina;
    @BindView(R.id.tv_for_login_email)
    TextView mTvForLoginEmail;
    @BindView(R.id.cancelLayout)
    RelativeLayout mCancelLayout;

    @Override
    public View setInitView() {
        View view = View.inflate(this, R.layout.activity_select_share, null);
        Window window = this.getWindow();//宽度为屏幕宽,位置为底部
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        view.setMinimumWidth(SysApplication.mWidthPixels);
        window.setAttributes(lp);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_for_login_sina, R.id.tv_for_login_email, R.id.cancelLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_for_login_sina:
                Intent intent=new Intent(myActivity(), ShareActivity.class);
                intent.putExtra(ShareActivity.ITEM_ID,0);
                intent.putExtra(ShareActivity.IS_WECHAT,ShareActivity.WECHAT);
                myActivity().startActivity(intent);
                finish();
                break;
            case R.id.tv_for_login_email:
                Intent intent1=new Intent(myActivity(), ShareActivity.class);
                intent1.putExtra(ShareActivity.ITEM_ID,0);
                intent1.putExtra(ShareActivity.IS_WECHAT,ShareActivity.QQ);
                myActivity().startActivity(intent1);
                finish();
                break;
            case R.id.cancelLayout:
                onBackPressed();
                break;
        }
    }
}
