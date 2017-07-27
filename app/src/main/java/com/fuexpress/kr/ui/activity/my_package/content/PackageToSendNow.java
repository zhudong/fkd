package com.fuexpress.kr.ui.activity.my_package.content;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.adapter.PackageItemsAdapter;
import com.fuexpress.kr.ui.fragment.MyPackageFragment;
import com.fuexpress.kr.ui.view.RefreshListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsParcel;


/**
 * Created by yuan on 2016-5-4.
 */
public class PackageToSendNow extends Fragment implements RefreshListView.OnRefreshListener, View.OnClickListener {

    private RefreshListView mRefreshListView;
    private static Handler mHandler = new Handler();
    private int mPageIndex = 1;
    private List<CsParcel.Parcel> mParcelsList;
    private boolean mHasMore = true;
    private PackageItemsAdapter mAdapter;
    private View mNoOne;
    private long mdelay = 500;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = View.inflate(getContext(), R.layout.fragment_package_items, null);
        mRefreshListView = (RefreshListView) rootView.findViewById(R.id.refresh_lv_package);
        mNoOne = rootView.findViewById(R.id.noLayout);
        rootView.findViewById(R.id.selectBtn).setOnClickListener(this);
        mRefreshListView.setOnRefreshListener(this);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initdata();
    }

    private void initdata() {
        mParcelsList = new ArrayList<>();
        mAdapter = new PackageItemsAdapter(getActivity(), mParcelsList);
        mRefreshListView.setAdapter(mAdapter);
        getParcelList();
    }
/*


    去发货：appmerchant/send/list?uin=102682&page=2&type=toPayOrNotice
    待收货：appmerchant/send/list?uin=102682&page=2&status=Payed&type=all
    已发货：appmerchant/send/list?uin=102682&page=2&status=Shipped&type=all
    全部：appmerchant/send/list?uin=102682&page=2&type=all*/

    protected String getTab() {
        return "";
    }

    protected String getType() {
        return "toPayOrNotice";
    }

    private void getParcelList() {
        CsParcel.SendParcelListRequest.Builder builder = CsParcel.SendParcelListRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().mBaseUserRequest);
        builder.setPageNo(mPageIndex);
        if (mPageIndex == 1 && mParcelsList == null) {
            mParcelsList = new ArrayList<>();
            BaseActivity activity = (BaseActivity) getActivity();
//            activity.showLoading();
        }
        builder.setStatus(getTab()).setType(getType());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.SendParcleListResponse>() {

            @Override
            public void onSuccess(final CsParcel.SendParcleListResponse response) {
                List<CsParcel.Parcel> itemsList = response.getItemsList();
                mHasMore = response.hasMore() && itemsList.size() > 4;
                if (mPageIndex == 1) {
                    mParcelsList.clear();
                    mParcelsList.addAll(itemsList);
                } else {
                    mParcelsList.addAll(itemsList);
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshListView.stopRefresh();
                        mRefreshListView.stopLoadMore(true);
                        mRefreshListView.setHasLoadMore(mHasMore);
                        if (mParcelsList.size() == 0) {
                            mNoOne.setVisibility(View.VISIBLE);
                        } else {
                            mNoOne.setVisibility(View.INVISIBLE);
                        }

                        if (mPageIndex == 1)
                            ((MyPackageFragment) getParentFragment()).setNeedPayCount(response.getToshipnum());
                        mAdapter.notifyDataSetChanged();
                        if (mHasMore) {
                            mPageIndex++;
                        }
                    }
                }, mdelay);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshListView.stopRefresh();
                        mRefreshListView.stopLoadMore(true);
                        mRefreshListView.setHasLoadMore(false);
                        mAdapter.notifyDataSetChanged();
                        if (mParcelsList.size() == 0) {
                            mNoOne.setVisibility(View.VISIBLE);
                        } else {
                            mNoOne.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onRefresh(RefreshListView refreshListView) {
        mPageIndex = 1;
        getParcelList();
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (mHasMore) {
            getParcelList();
        }
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new BusEvent(BusEvent.PARCEL_APPEND, null));
    }


    public android.support.v4.util.ArrayMap getItem(long id) {
        ArrayMap map = new ArrayMap();
        for (int i = 0; i < mParcelsList.size(); i++) {
            CsParcel.Parcel parcel = mParcelsList.get(i);
            long parcelId = parcel.getParcelId();
            if (id == parcelId) {
                mParcelsList.remove(i);
                mAdapter.notifyDataSetChanged();
                return map;
            }
        }
        return null;
    }

    public void addData(CsParcel.Parcel pacel) {
        mParcelsList.add(pacel);


    }

    public void putData(CsParcel.Parcel pacel) {
        ArrayList<CsParcel.Parcel> list = new ArrayList<>();
        list.addAll(mParcelsList);
        mParcelsList.removeAll(mParcelsList);
        mParcelsList.add(pacel);
        mParcelsList.addAll(list);
        list = null;
        mAdapter.notifyDataSetChanged();
    }

    public static boolean mNeedRefresh;

    @Override
    public void onResume() {
        super.onResume();
    }

    public void reloade() {
        mRefreshListView.setSelection(0);
        mRefreshListView.autoRefresh();
    }

    public void onEventMainThread(BusEvent event) {
        if (event.getType() == BusEvent.REFRESH_TOSEND_PARCEL) {
//            onRefresh(mRefreshListView);
            reloade();
        }
    }
}