package com.fuexpress.kr.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.UserManager;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.SPUtils;
import com.fuexpress.kr.utils.StringUtils;

public class PreferencesActivity extends BaseActivity {

    public static final String KEY_JUMP_CL = "JUMP_CL";
    private View mRootView;
    private TitleBarView mTitleBarView;
    private RelativeLayout mLanguage_in_preferences, mCurrency_in_preferences;
    private TextView mTv_in_preferences_lanague, mTv_in_preferences_currency;


    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_preferences, null);
        TitleBarView title_in_preferences = (TitleBarView) mRootView.findViewById(R.id.title_in_preferences);
        title_in_preferences.getIv_in_title_back().setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void initView() {
        mLanguage_in_preferences = (RelativeLayout) mRootView.findViewById(R.id.language_in_preferences);
        mLanguage_in_preferences.setOnClickListener(this);
        mCurrency_in_preferences = (RelativeLayout) mRootView.findViewById(R.id.currency_in_preferences);
        mCurrency_in_preferences.setOnClickListener(this);
        mTv_in_preferences_lanague = (TextView) mRootView.findViewById(R.id.tv_in_preferences_lanague);
        mTv_in_preferences_currency = (TextView) mRootView.findViewById(R.id.tv_in_preferences_currency);
        /*findViewAndSetonCLickListenerplus(mRootView, R.id.tv_in_preferences_currency, true, mTv_in_preferences_currency);
        mTv_in_preferences_currency.setText("sdasdasdsad");*/
        initData();
    }

    private void initData() {
        String localeCode = AccountManager.getInstance().getLocaleCode();
        String currencyCode = AccountManager.getInstance().getCurrencyCode();
        UserManager.getInstance().getCurrencyListRequest();
        showTextView(localeCode, currencyCode);
        boolean booleanExtra = getIntent().getBooleanExtra(KEY_JUMP_CL, false);
    }

    private void showTextView(String localeCode, String currencyCode) {
        int loacaStringId = StringUtils.matchingCountryString(localeCode);
        mTv_in_preferences_lanague.setText(getString(loacaStringId));

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.language_in_preferences:
                startDDMActivity(ChooseLanguageActivity.class, true);
                break;
            case R.id.currency_in_preferences:
                startDDMActivity(ChooseCurrencyActivity.class, true);
                break;
        }
    }

    @Override
    protected void onResume() {
        UserManager.getInstance().getCurrencyListRequest();
        super.onResume();
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_CURRENCY_COMPLETE:
                String currency_string;
                if (event.getBooleanParam()) {
                    currency_string = event.getStrParam();
                } else {
                    currency_string = (String) SPUtils.get(this, Constants.USER_INFO.USER_CURRENCY_STRING, "");
                }
                mTv_in_preferences_currency.setText(currency_string);
                break;
        }
    }

}
