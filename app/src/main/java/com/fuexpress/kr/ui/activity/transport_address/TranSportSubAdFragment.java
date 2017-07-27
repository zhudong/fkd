package com.fuexpress.kr.ui.activity.transport_address;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.TraspoAddressBean;
import com.fuexpress.kr.ui.activity.help_signed.data.WareHouseBean;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longer on 2017/5/22.
 */
public class TranSportSubAdFragment extends BaseFragment<TranSportAddressActivity> implements RefreshListView.OnRefreshListener {


    private ArrayList<WareHouseBean> mWareHouseBeans;

    private Context mContext = SysApplication.getContext();
    private RefreshListView mRfl_sub_ts_ad;
    private int mPageNum = 1;//默认是1,从第一页开始
    private TranSportAddrAdapter mTranSportAddrAdapter;
    private String mTitle;
    private String mCountryCode;
    private List<WareHouseBean> mWareHouseBeanList;

    private TraspoAdRemoteListener mRemoteListener = new TraspoAdRemoteListener() {
        @Override
        public void success(List<WareHouseBean> wareHouseBeans, boolean hasMore) {
            setListViewAdapter(wareHouseBeans, hasMore);
        }

        @Override
        public void fail(String errStr) {
            setRefreListViewState(mRfl_sub_ts_ad, false);
            CustomToast.makeText(mContext, getString(R.string.string_for_send_requset_fail_02) + errStr, Toast.LENGTH_SHORT).show();
        }
    };
    private TraspoAddressBean mTraspoAddressBean;
    private boolean mHasMore;

    private void setListViewAdapter(final List<WareHouseBean> wareHouseBeans, final boolean hasMore) {
        FragmentActivity activity = getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPageNum == 1 || mTranSportAddrAdapter == null) {
                    mTranSportAddrAdapter = new TranSportAddrAdapter(mContext, wareHouseBeans);
                    mRfl_sub_ts_ad.setAdapter(mTranSportAddrAdapter);
                } else {
                    mTranSportAddrAdapter.addData(wareHouseBeans);
                    mWareHouseBeanList.addAll(wareHouseBeans);
                    mTranSportAddrAdapter.notifyDataSetChanged();
                }
                setRefreListViewState(mRfl_sub_ts_ad, hasMore);
            }
        });

    }


    public static TranSportSubAdFragment getInstance(Bundle bundle) {
        TranSportSubAdFragment tranSportSubAdFragment = new TranSportSubAdFragment();
        tranSportSubAdFragment.setArguments(bundle);
        return tranSportSubAdFragment;
    }


    @Override
    protected void initTitleBar() {

    }

    @Override
    public View initView() {
        LogUtils.e("initView");
        View rootView = View.inflate(mContext, R.layout.layout_for__tssa, null);
        mRfl_sub_ts_ad = (RefreshListView) rootView.findViewById(R.id.rfl_sub_ts_ad);
        mRfl_sub_ts_ad.setHeaderViewHide();
        mRfl_sub_ts_ad.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void initData() {
        checkDataAndShow();
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        mRfl_sub_ts_ad.stopRefresh();
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (mHasMore)
            TranSportAdManager.getInstance().getTransportAddressDataRemote(++mPageNum, mCountryCode, mRemoteListener);
    }

    private void setRefreListViewState(RefreshListView refreListView, boolean hasMore) {
        refreListView.stopRefresh();
        refreListView.stopLoadMore(true);
        mHasMore = hasMore;
        refreListView.setHasLoadMore(hasMore);
    }

    public String getTitle() {
        if (mTitle == null)
            mTitle = "";
        return mTitle;
    }

    public String getCountryCode() {
        return mCountryCode;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            LogUtils.e("visible");
        /*if (isVisibleToUser)
            checkDataAndShow();*/
    }

    private void checkDataAndShow() {
        //LogUtils.e("resume");
        Bundle arguments = getArguments();
        mTraspoAddressBean = (TraspoAddressBean) arguments.getSerializable("bean");
        mTitle = mTraspoAddressBean.getCountryName();
        mCountryCode = mTraspoAddressBean.getCountryCode();
        mWareHouseBeanList = mTraspoAddressBean.getWareHouseBeanList();
        if (mWareHouseBeanList.size() != 0) {
            setListViewAdapter(mWareHouseBeanList, mTraspoAddressBean.isHasMore());
        } else
            TranSportAdManager.getInstance().getTransportAddressDataRemote(mPageNum, mCountryCode, mRemoteListener);
    }
}
