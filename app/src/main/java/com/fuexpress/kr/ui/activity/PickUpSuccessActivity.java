package com.fuexpress.kr.ui.activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.ActivityController;
import com.fuexpress.kr.ui.view.TitleBarView;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016-12-28.
 */

public class PickUpSuccessActivity extends BaseActivity {

    public static final int CODE_RESULT_SUCCESS = 0x1021;


    private View rootView;
    private Button goHomeBtn;
    private ImageView backIv;
    private TitleBarView titleBarView;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.pick_up_success, null);
        titleBarView = (TitleBarView) rootView.findViewById(R.id.pick_up_success_title_bar);
        backIv = titleBarView.getIv_in_title_back();
        goHomeBtn = (Button) rootView.findViewById(R.id.pick_up_success_go_home_btn);
        ActivityController.addActivity(this);
        backIv.setOnClickListener(this);
        goHomeBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void finish() {
        super.finish();
        setResult(CODE_RESULT_SUCCESS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(CODE_RESULT_SUCCESS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pick_up_success_go_home_btn:
                EventBus.getDefault().post(new BusEvent(BusEvent.GO_HOME_PAGE, null));
                ActivityController.finishActivityOutOfMainActivity();
                break;
            case R.id.iv_in_title_back:
                finish();
                break;
        }
    }
}
