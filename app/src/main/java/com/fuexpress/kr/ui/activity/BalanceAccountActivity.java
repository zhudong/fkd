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
import com.fuexpress.kr.model.BalanceManager;
import com.fuexpress.kr.model.UserManager;
import com.fuexpress.kr.ui.adapter.BalanceAccountAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.SPUtils;
import com.fuexpress.kr.utils.StringUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BalanceAccountActivity extends BaseActivity implements RefreshListView.OnRefreshListener {


    private View mRootView;
    private RefreshListView mRfl_in_gift_card_account;
    private View mHeadView;
    private BalanceAccountAdapter mGiftCardAccountAdapter;
    private boolean mIsHasMore = false;
    private int mPageNum = 1;
    private TextView mTv_in_preferences_currency;
    private RelativeLayout mCurrency_in_preferences;


    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_balance_account, null);
        return mRootView;
    }

    @Override
    public void initView() {
        mRfl_in_gift_card_account = (RefreshListView) mRootView.findViewById(R.id.rfl_in_gift_card_account);
        mGiftCardAccountAdapter = new BalanceAccountAdapter(BalanceManager.getInstance().mGiftCardBalanceLists);
        mRfl_in_gift_card_account.setAdapter(mGiftCardAccountAdapter);
        mRfl_in_gift_card_account.setHasLoadMore(false);
        mHeadView = View.inflate(this, R.layout.head_view_for_gift_card_account, null);
        mRfl_in_gift_card_account.addHeaderView(mHeadView);
        mRfl_in_gift_card_account.setHeaderViewHide();
        mRfl_in_gift_card_account.setOnRefreshListener(this);
        TitleBarView titleBarView = (TitleBarView) mRootView.findViewById(R.id.title_in_my_balance_account);
        titleBarView.getIv_in_title_back().setOnClickListener(this);
        TextView textView = titleBarView.getmTv_in_title_back_tv();
        textView.setText(getString(R.string.string_for_my_balance_title));
        textView.setOnClickListener(this);
        mTv_in_preferences_currency = (TextView) mHeadView.findViewById(R.id.tv_in_preferences_currency);
        mCurrency_in_preferences = (RelativeLayout) mHeadView.findViewById(R.id.currency_in_preferences);
        mCurrency_in_preferences.setOnClickListener(this);
        //initData(mPageNum);
    }

    private void initData(int pageNum) {
        if (AccountManager.isLogin) {
            // TODO: 2016/11/7 暂时注释
            //BalanceManager.getInstance().giftCardBalanceListAjaxRequest(AccountManager.getInstance().mUin, 1);
            BalanceManager.getInstance().giftCardBalanceListRequest(AccountManager.getInstance().mUin, pageNum);
        } else {
            AccountManager.getInstance().isUserLogin(this);
        }
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        mRfl_in_gift_card_account.stopRefresh();
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (mIsHasMore) {
            mPageNum = mPageNum + 1;
            BalanceManager.getInstance().giftCardBalanceListRequest(AccountManager.getInstance().mUin, mPageNum);
            //BalanceManager.getInstance().giftCardBalanceListAjaxRequest(AccountManager.getInstance().mUin, mPageNum);
        } else {
            return;
        }
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_GIFT_CARD_BAN_LIST_INIT_SUCCESS:
                boolean isSuccess = event.getBooleanParam();
                if (isSuccess) {
                    if (mPageNum == 1) {
                        mGiftCardAccountAdapter = new BalanceAccountAdapter(BalanceManager.getInstance().mGiftCardBalanceLists);
                        mRfl_in_gift_card_account.setAdapter(mGiftCardAccountAdapter);
                        //mGiftCardAccountAdapter.notifyDataSetChanged();
                        TextView textView = (TextView) mHeadView.findViewById(R.id.tv_head_view_gift_card_account_canuse_money);
                        // DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                        //String p = decimalFormat.format(GiftCardManager.getInstance().mGiftcardaccount);
                        //String p = StringUtils.changeFolatToString(GiftCardManager.getInstance().mGiftcardaccount);
                        //textView.setText("¥ " + p);
                        textView.setText(UIUtils.getCurrency(this, BalanceManager.getInstance().mGiftcardaccount));
                        TextView textView1 = (TextView) mHeadView.findViewById(R.id.tv_head_view_gift_card_account_cannt_use_money);
                        //String p2 = decimalFormat.format(GiftCardManager.getInstance().mFrozenamount);
                        //String p2 = StringUtils.changeFolatToString(GiftCardManager.getInstance().mFrozenamount);
                        //textView1.setText("¥ " + p2);
                        textView1.setText(UIUtils.getCurrency(this, BalanceManager.getInstance().mFrozenamount));
                    } else {
                        mGiftCardAccountAdapter.notifyDataSetChanged();
                    }
                    String intParam = event.getStrParam();
                    if (!"more".equals(intParam)) {
                        mIsHasMore = false;
                        mRfl_in_gift_card_account.setHasLoadMore(false);
                    } else {
                        mIsHasMore = true;
                        mRfl_in_gift_card_account.setHasLoadMore(true);
                    }
                    mRfl_in_gift_card_account.stopLoadMore(true);
                } else {
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.home_page_dialog_request_fail_02));
                    dissMissProgressDiaLog(1000);
                    mRfl_in_gift_card_account.setHasLoadMore(false);
                }
                break;
            case BusEvent.GET_GIFT_CARD_LIST_MORE_COMPLETE:
                mRfl_in_gift_card_account.stopLoadMore(true);
                boolean isLoadMoreSuccess = event.getBooleanParam();
                int listSize = event.getIntValue();
                if (isLoadMoreSuccess) {
                    mGiftCardAccountAdapter.notifyDataSetChanged();
                    if (listSize < 10) {
                        mRfl_in_gift_card_account.setHasLoadMore(false);
                    } else {
                        mRfl_in_gift_card_account.setHasLoadMore(true);
                    }
                } else {
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.home_page_dialog_request_fail_02));
                    dissMissProgressDiaLog(1000);
                    mRfl_in_gift_card_account.setHasLoadMore(false);
                }
                break;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.currency_in_preferences:
                startDDMActivity(ChooseCurrencyActivity.class, true);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mPageNum!=1)
            mPageNum = 1;
        initData(1);
        UserManager.getInstance().getCurrencyListRequest();
    }
}
