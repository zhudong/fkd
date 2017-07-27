package com.fuexpress.kr.ui.fragment.myneed;


import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.adapter.NeedItemsAdapter;
import com.fuexpress.kr.ui.fragment.MyNeedFragment;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.RefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import fksproto.CsBase;
import fksproto.CsUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToPayFragment extends BaseFragment<MainActivity> implements RefreshListView.OnRefreshListener, AdapterView.OnItemClickListener {


    @BindView(R.id.lv_body)
    RefreshListView lvBody;
    @BindView(R.id.rl_hint)
    RelativeLayout rlHint;
    int mIndex;
    private List<CsUser.Require> mData;
    private NeedItemsAdapter mNeedItemsAdapter;
    private boolean hasMore = true;

    @Override
    protected void initTitleBar() {

    }

    @Override
    public View initView() {
        return View.inflate(getContext(), R.layout.fragment_to_need_pay, null);
    }

    @Override
    public void initData() {
        mData = new ArrayList<>();
        mNeedItemsAdapter = new NeedItemsAdapter(mContext, mData);
        lvBody.setOnRefreshListener(this);
        lvBody.setAdapter(mNeedItemsAdapter);
        mIndex = 1;
        getData(mIndex);
    }


    private void getData(int index) {
        CsBase.BaseUserRequest.Builder baseUserRequest = AccountManager.getInstance().getBaseUserRequest();
//        CsBase.BaseUserRequest.Builder builder1 = baseUserRequest.clone().setUin(102682);
        CsUser.GetMyRequireListRequest.Builder builder = CsUser.GetMyRequireListRequest.newBuilder().setUserinfo(baseUserRequest).setPageNo(index);
        String state = getState();
        if (state != null) {
            builder.setStatus(state);
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetMyRequireListResponse>() {
            @Override
            public void onSuccess(final CsUser.GetMyRequireListResponse response) {
                UIUtils.postTaskSafelyDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<CsUser.Require> requireListList = response.getRequireListList();
                        if (mIndex == 1) {
                            mData.clear();
                            ((MyNeedFragment) getParentFragment()).setNeedPayCount(response.getPendingCounts());
                            ((MyNeedFragment) getParentFragment()).setInstorageCount(response.getPackingCounts());
                            if (requireListList.size() == 0) {
                                rlHint.setVisibility(View.VISIBLE);
                            } else {
                                rlHint.setVisibility(View.GONE);
                            }
                        }
                        lvBody.stopLoadMore(true);
                        lvBody.stopRefresh();
                        if (requireListList.size() > 0) {
                            mData.addAll(requireListList);
                            mNeedItemsAdapter.notifyDataSetChanged();
                        }
                        String more = response.getMore();
                        if (more.contains("yes")) {
                            hasMore = true;
                        } else {
                            hasMore = false;
                        }
                        lvBody.setHasLoadMore(hasMore);
                    }
                },500);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        lvBody.stopLoadMore(true);
                        lvBody.stopRefresh();
                        lvBody.setHasLoadMore(false);
                    }
                });
            }
        });
    }


    protected String getState() {
        return "Pending";
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        mIndex = 1;
        getData(mIndex);
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (hasMore) {
            mIndex++;
            getData(mIndex);
        }
    }


    public void reloade() {
        lvBody.setSelection(0);
        lvBody.autoRefresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CsUser.Require item = ((NeedItemsAdapter) parent.getAdapter()).getItem(position - 1);
    }
}
