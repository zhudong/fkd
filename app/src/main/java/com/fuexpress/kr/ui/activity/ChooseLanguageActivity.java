package com.fuexpress.kr.ui.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.PreferencesBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ConfigManager;
import com.fuexpress.kr.model.PayMethodManager;
import com.fuexpress.kr.model.UserManager;
import com.fuexpress.kr.ui.adapter.PreferencesAdapter;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChooseLanguageActivity extends BaseActivity {

    private View mRootView;
    private RelativeLayout mRl_in_change_language_default;
    private ListView mLv_in_choose_language;
    private List<PreferencesBean> mPreferencesBeanList;
    private PreferencesAdapter mPreferencesAdapter;
    private int mOnClickPosition;


    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_choose_language, null);
        TitleBarView title_in_choose_language = (TitleBarView) mRootView.findViewById(R.id.title_in_choose_language);
        title_in_choose_language.getIv_in_title_back().setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void initView() {
        mRl_in_change_language_default = (RelativeLayout) mRootView.findViewById(R.id.rl_in_change_language_default);
        mRl_in_change_language_default.setOnClickListener(this);
        mLv_in_choose_language = (ListView) mRootView.findViewById(R.id.lv_in_choose_language);
        mLv_in_choose_language.setOnItemClickListener(this);
        initData();
    }

    private void initData() {
       /* String localeCode = AccountManager.getInstance().mLocaleCode;
        LogUtils.e(AccountManager.getInstance().mLocaleCode);*/
        mPreferencesBeanList = new ArrayList<>();
        String[] strings = new String[]{getString(R.string.string_for_language_english), getString(R.string.string_for_language_korea), getString(R.string.string_for_language_chn), getString(R.string.string_for_language_chnn)};
        String[] code = new String[]{getString(R.string.string_language_us), getString(R.string.string_language_ko), getString(R.string.string_language_zh), getString(R.string.string_language_zh_02)};
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            PreferencesBean preferencesBean = new PreferencesBean();
            preferencesBean.setBeanString(s);
            if (!AccountManager.getInstance().getIsDefaultLanguage()) {
                //if (AccountManager.getInstance().mLocaleCode.equals(code[i]) && i != 0)
                if (AccountManager.getInstance().getLocaleCode().equals(code[i]))
                    preferencesBean.setIsSelected(true);
            } else {
                if (i == 0)
                    preferencesBean.setIsSelected(true);
            }
            preferencesBean.setBeanCode(code[i]);
            mPreferencesBeanList.add(preferencesBean);
        }
        mPreferencesAdapter = new PreferencesAdapter(this, mPreferencesBeanList);
        mLv_in_choose_language.setAdapter(mPreferencesAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
                finish();
                break;
            case R.id.rl_in_change_language_default:
                // TODO: 2016/11/1 接口没完成,暂时注释
                //UserManager.getInstance().changeLocaleRequest(getString(R.string.string_language_us));
                changeLanguage("en_US");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //if (position == 0 || position == 1) {
        mOnClickPosition = mPreferencesAdapter.mLastPosition;
        if (position != mOnClickPosition || mOnClickPosition == 0) {
            mOnClickPosition = position;
            PreferencesBean preferencesBean = mPreferencesBeanList.get(mOnClickPosition);
            changeLanguage(preferencesBean.getBeanCode());
        }
        /*} else {
            CustomToast.makeText(this, "暂不支持该语种", Toast.LENGTH_SHORT).show();
        }*/

    }

    private void changeLanguage(final String lCode) {
        if (!AccountManager.isLogin) {
            AccountManager.getInstance().setLocaleCode(lCode);
            String[] language = lCode.split("_");
            Resources res = getResources();
            Configuration config = res.getConfiguration();
            config.locale = new Locale(language[0], language[1]);
            ConfigManager.getInstance(this).updateConfigInLogin(config);
        } else {
            showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, null);
            // TODO: 2016/11/1 接口没完成,暂时注释
            SysApplication.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserManager.getInstance().changeLocaleRequest(lCode);
                }
            }, 500);
        }
    }


    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.CHANGE_LUANGAGUE_COMPLETE:
                if (event.getBooleanParam()) {
                    PreferencesBean preferencesBean = mPreferencesBeanList.get(mOnClickPosition);
                    preferencesBean.setIsSelected(true);
                    // TODO: 2016/11/1 接口没完成,暂时注释
                    AccountManager.getInstance().mLocaleCode = preferencesBean.getBeanCode();
                    AccountManager.getInstance().setLocaleCode(preferencesBean.getBeanCode());
                    mPreferencesBeanList.get(mPreferencesAdapter.mLastPosition).setIsSelected(false);
                    mPreferencesAdapter.notifyDataSetChanged();
                    LogUtils.e(preferencesBean.getBeanCode());
                    dissMissProgressDiaLog();
                    // TODO: 2016/7/22 实际上这里还是需要切换本应用的语种
                    String[] language = preferencesBean.getBeanCode().split("_");
                    Resources res = getResources();
                    Configuration config = res.getConfiguration();
                    config.locale = new Locale(language[0], language[1]);
                    ConfigManager.getInstance(this).updateConfigJumpSetting(config);
                    //重置支付方式
                    PayMethodManager.getInstance(this).reSet();

                } else {
                    //showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.String_for_store_dialog_request_fail));
                    changeDiagLogAlertType(SweetAlertDialog.ERROR_TYPE, getString(R.string.change_pwd_fail_text));
                    dissMissProgressDiaLog(1000);
                }
                break;
        }
    }
}
