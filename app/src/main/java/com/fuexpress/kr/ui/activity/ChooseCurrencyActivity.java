package com.fuexpress.kr.ui.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.PreferencesBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ParcelHelpDao;
import com.fuexpress.kr.model.UserManager;
import com.fuexpress.kr.ui.adapter.PreferencesAdapter;
import com.fuexpress.kr.ui.view.TitleBarView;

import java.util.Stack;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;

public class ChooseCurrencyActivity extends BaseActivity {

    private View mRootView;
    private ListView mLv_choose_currency;
    private int mLastOnclickPosition;
    private PreferencesAdapter mPreferencesAdapter;



    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_choose_currency, null);
        TitleBarView title_in_choose_currency = (TitleBarView) mRootView.findViewById(R.id.title_in_choose_currency);
        title_in_choose_currency.getIv_in_title_back().setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void initView() {
        mLv_choose_currency = (ListView) mRootView.findViewById(R.id.lv_choose_currency);
        mLv_choose_currency.setOnItemClickListener(this);
        initData();
    }

    private void initData() {
        // TODO: 2016/11/1 接口没完成,暂时注释
        UserManager.getInstance().getCurrencyListRequest();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_CURRENCY_COMPLETE:
                // TODO: 2016/11/1 接口没完成,暂时注释
                if (event.getBooleanParam()) {
                    mPreferencesAdapter = new PreferencesAdapter(this, UserManager.getInstance().mPreferencesBeanList);
                    mLv_choose_currency.setAdapter(mPreferencesAdapter);
                } else {
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.home_page_dialog_request_fail_02));
                    if (!isFinishing())
                        dissMissProgressDiaLog(1000);
                }
                break;
            case BusEvent.CHANGE_CURRENCY_COMPLETE:
                if (event.getBooleanParam()) {
                    PreferencesBean preferencesBean = UserManager.getInstance().mPreferencesBeanList.get(mLastOnclickPosition);
                    preferencesBean.setIsSelected(true);
                    PreferencesBean lastPreferencesBean = UserManager.getInstance().mPreferencesBeanList.get(mPreferencesAdapter.mLastPosition);
                    lastPreferencesBean.setIsSelected(false);
                    mPreferencesAdapter.notifyDataSetChanged();
                    AccountManager.getInstance().setCurrencyCode(preferencesBean.getBeanCode(), preferencesBean.getBeanID());
                    AccountManager.getInstance().mCurrencyCode = preferencesBean.getBeanCode();
                    AccountManager.getInstance().mCurrencyId = preferencesBean.getBeanID();
                    EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_CURRENCY_COMPLETE, null));
//                    清除旧的包裹
                    ParcelHelpDao instance = ParcelHelpDao.getInstance(this);
                    instance.deleteAll();
                } else {
                    changeDiagLogAlertType(SweetAlertDialog.ERROR_TYPE, getString(R.string.change_pwd_fail_text));
                }
                dissMissProgressDiaLogAndFinish(1000);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*mLastOnclickPosition = mPreferencesAdapter.mLastPosition;
        if (mLastOnclickPosition != position) {*/
        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, null);
        mLastOnclickPosition = position;
        // TODO: 2016/11/1 接口没完成,暂时注释
        PreferencesBean preferencesBean = UserManager.getInstance().mPreferencesBeanList.get(mLastOnclickPosition);
        UserManager.getInstance().ChangeCurrencyRequest(preferencesBean.getBeanCode());
        //}

    }
}
