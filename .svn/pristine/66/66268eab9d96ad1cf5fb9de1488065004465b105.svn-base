package com.fuexpress.kr.ui.activity.package_detail;

import android.view.View;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.MbaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ParcelToSendActivity extends MbaseActivity {
    public static final String TITLE = "title";

    @BindView(R.id.tv_tile)
    TextView mTvTile;

    @Override
    protected int getViewResId() {
        return R.layout.activity_parcel_to_send;
    }

    @Override
    public void initView() {
        super.initView();
        initTitle("", getString(R.string.package_send_submit_sucess), "");
        hintIVRight();
        String stringExtra = getIntent().getStringExtra(TITLE);
        mTvTile.setText(getString(R.string.package_to_send_submit, stringExtra));
    }

    @OnClick(R.id.tv_to_send_continue)
    public void onClick() {
        EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_TOSEND_PARCEL, ""));
        finish();
    }

    public void goHome(View v){
        EventBus.getDefault().post(new BusEvent(BusEvent.GO_HOME_CODE, ""));
        finish();
    }

}
