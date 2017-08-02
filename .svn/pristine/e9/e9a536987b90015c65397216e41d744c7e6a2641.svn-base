package com.fuexpress.kr.ui.activity.crowd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.utils.ActivityUtils;

public class CrowdListActivity extends BaseActivity {

    @Override
    public View setInitView() {
        return View.inflate(this, R.layout.activity_append_item, null);
    }

    @Override
    public void initView() {
        super.initView();
        findViewById(R.id.title).setVisibility(View.GONE);
        showFragment();
    }

    private void showFragment() {
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), new CrowdListFragment(), R.id.fl_container);
    }
}
