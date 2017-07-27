package com.fuexpress.kr.ui.activity;

import android.widget.TextView;


import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ParcelSubmitSucessActivity extends MbaseActivity {
    public static final String PARCEL_NAMES = "pircel_names";
    @BindView(R.id.tv_parcel_numbers)
    TextView mTvParcelNumbers;

    @Override
    protected int getViewResId() {
        return R.layout.activity_parcel_submit_sucess;
    }

    @Override
    public void initView() {
        super.initView();
        initTitle("", getString(R.string.String_parcel_submit_sucess), "");
        hintIVRight();
        String names = getIntent().getStringExtra(PARCEL_NAMES);
        mTvParcelNumbers.setText(names.trim());
    }

    @OnClick(R.id.tv_back_home)
    public void onClick() {
        EventBus.getDefault().post(new BusEvent(BusEvent.GO_HOME_CODE, ""));
        finish();
    }
}