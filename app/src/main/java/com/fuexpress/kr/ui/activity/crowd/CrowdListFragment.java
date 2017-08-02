package com.fuexpress.kr.ui.activity.crowd;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.adapter.CrowdAdapter;
import com.fuexpress.kr.ui.adapter.CrowdGrideAdapter;
import com.fuexpress.kr.ui.adapter.CrowdListAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.FlowListLayout;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.ui.view.SwitcherHotAndNew;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.SPUtils;
import com.umeng.analytics.MobclickAgent;


import java.util.ArrayList;
import java.util.List;

import fksproto.CsCrowd;


/**
 * Created by yuan on 2016-6-27.
 */
public class CrowdListFragment extends BaseFragment<MainActivity> implements SwitcherHotAndNew.OnSwitchListener, RefreshListView.OnRefreshListener, SwitcherHotAndNew.OnArrowStateListener {

    public static String SHOWLIST = "showCrowdList";
    private RelativeLayout mRootView;
    private RefreshListView mBody;
    private SwitcherHotAndNew mSwitcherHotAndNew;

    private List<CsCrowd.Crowd> mNewHot;
    private List<CsCrowd.Crowd> mfinishing;
    private int mHotIndex = 1;
    private int mFinishingIndex = 1;

    private boolean mHotMore = true;
    private boolean mFinishMore = true;

    private int mState;
    private static final int SHOW_HOT_NEW = 1;
    private static final int SHOW_FINISHING = 2;

    private int mListState;
    private static final int REFRESH = 3;
    private static final int LOADE_MORE = 4;

    private CrowdAdapter mAdapter;

    static Handler mhandler = new Handler();
    private boolean mShowList;
    private ImageView mRight;
    private FlowListLayout mToast;
    private SimpleBaseAdapter<CsCrowd.CrowdTag> mCategory;
    private View mRootToast;
    private List<CsCrowd.CrowdTag> crowdtagList;


    public void switchAdapter() {
        if (!(mAdapter instanceof CrowdListAdapter)) {
            mAdapter = new CrowdListAdapter(mContext, mAdapter.getData());
            mBody.setAdapter(mAdapter);
            return;
        }

        if (!(mAdapter instanceof CrowdGrideAdapter)) {
            mAdapter = new CrowdGrideAdapter(mContext, mAdapter.getData());
            mBody.setAdapter(mAdapter);
            return;
        }
    }


    @Override
    protected void initTitleBar() {

    }

    @Override
    public View initView() {
        mRootView = (RelativeLayout) View.inflate(mContext, R.layout.activity_crowd_list, null);
        mBody = (RefreshListView) mRootView.findViewById(R.id.lv_crowd_body);
        mToast = (FlowListLayout) mRootView.findViewById(R.id.gv_category_crowd);
        mRootToast = mRootView.findViewById(R.id.ll_other);
        mRootToast.setOnClickListener(this);

        mSwitcherHotAndNew = (SwitcherHotAndNew) mRootView.findViewById(R.id.sw_hot_new);
//        mSwitcherHotAndNew = new SwitcherHotAndNew(this);
        mSwitcherHotAndNew.setLeftTextAndCount(getResources().getString(R.string.String_hot_crowd), null);
        mSwitcherHotAndNew.setRightTextCount(getResources().getString(R.string.String_finish_crowd), null);
        mSwitcherHotAndNew.setOnSwitchListener(this);
        mSwitcherHotAndNew.setArrowEnable(true);
        mSwitcherHotAndNew.setmOnArrowStateListener(this);
//        mBody.addHeaderView(mSwitcherHotAndNew);
        mBody.setOnRefreshListener(this);
//        TitleBarView TitleBarView = new TitleBarView();
//        RelativeLayout commonTitle = TitleBarView.getCommonTitle();
        mRight = (ImageView) mRootView.findViewById(R.id.title_iv_right);
        mRight.setOnClickListener(this);
        mRight.setVisibility(View.VISIBLE);
        ((TextView) mRootView.findViewById(R.id.title_tv_center)).setText(getResources().getString(R.string.String_crowd_title));
        mShowList = (boolean) SPUtils.get(mContext, SHOWLIST, true);
        showStateButton();
        return mRootView;
    }

    int hotCategoryPositon;
    int endCategoryPositon;

    private void initToastView() {
        final int space = UIUtils.dip2px(8);
        mToast.setHorizontalSpacing(space);
        mToast.setVerticalSpacing(space);

        if (crowdtagList == null) return;
        final int paddingtop = UIUtils.dip2px(3);
        final int paddingHorizontal = UIUtils.dip2px(12);
        mCategory = new SimpleBaseAdapter<CsCrowd.CrowdTag>(getActivity(), crowdtagList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = new TextView(mContext);
                textView.setText(crowdtagList.get(position).getCrowdtagname());
                textView.setBackground(getResources().getDrawable(R.drawable.seletor_crowd_category));
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(paddingHorizontal, paddingtop, paddingHorizontal, paddingtop);
                int index = 0;
                if (mState == SHOW_HOT_NEW) {
                    index = hotCategoryPositon;
                } else {
                    index = endCategoryPositon;
                }
                if (position == index) {
                    textView.setEnabled(false);
                    textView.setTextColor(getResources().getColor(R.color.text_switch_indicator));
                } else {
                    textView.setEnabled(true);
                    textView.setTextColor(getResources().getColor(R.color.text_color_666));
                }
                return textView;
            }
        };
        mToast.setAdapter(mCategory);

        mToast.setOnItemClickListener(new FlowListLayout.OnItemClickListener() {
            @Override
            public void onItemClick(FlowListLayout parent, View view, int position, long id) {
//                Toast.makeText(mContext, position + "", Toast.LENGTH_SHORT).show();
                mSwitcherHotAndNew.closeToast();
                showLoading();
                if (mState == SHOW_HOT_NEW) {
                    hotCategoryPositon = position;
                    mHotIndex = 1;
                    getHotNewList();
                } else {
                    endCategoryPositon = position;
                    mFinishingIndex = 1;
                    getFinishingList();
                }
                mToast.notifyDataSetChanged();
            }
        });
    }

    private void showStateButton() {
        SPUtils.put(mContext, SHOWLIST, mShowList);
        if (mShowList) {
            mRight.setImageResource(R.mipmap.crowd_colletcions_gride);
            MobclickAgent.onEvent(mContext, "CrowdList");
        } else {
            mRight.setImageResource(R.mipmap.crowd_colletcions_list);
            MobclickAgent.onEvent(mContext, "CrowdGride");
        }
    }

    @Override
    public void initData() {
        mNewHot = new ArrayList<>();
        mfinishing = new ArrayList<>();
        mState = SHOW_HOT_NEW;
        getCategory();
        getHotNewList();
    }

    private void getCategory() {
        CsCrowd.GetCrowdTagRequest.Builder build = CsCrowd.GetCrowdTagRequest.newBuilder().setLocalecode(AccountManager.getInstance().getLocaleCode()).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(build, new INetEngineListener<CsCrowd.GetCrowdTagResponse>() {
            @Override
            public void onSuccess(CsCrowd.GetCrowdTagResponse response) {
                crowdtagList = response.getCrowdtagList();
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initToastView();
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                Log.d("te", errMsg);
            }
        });
    }


    private void getHotNewList() {
        if (mNewHot.size() == 0) showLoading();
        if (mHotIndex == 1) {
            mListState = REFRESH;
        }
        CsCrowd.GetCrowdListRequest.Builder builder = CsCrowd.GetCrowdListRequest.newBuilder();
        AccountManager instance = AccountManager.getInstance();
        builder.setType(1).setUserinfo(instance.getBaseUserRequest());
        builder.setPageno(mHotIndex).setCurrencycode(instance.getCurrencyCode()).setLocalecode(instance.getLocaleCode());
        if (crowdtagList != null && crowdtagList.size() > 0) {
            int crowdtagid = crowdtagList.get(hotCategoryPositon).getCrowdtagid();
            builder.setCrowdtagid(crowdtagid);
        } else {
            builder.setCrowdtagid(0);
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsCrowd.GetCrowdListResponse>() {
            @Override
            public void onSuccess(CsCrowd.GetCrowdListResponse response) {
                List<CsCrowd.Crowd> crowdsList = response.getCrowdsList();
                if (mHotIndex == 1) {
                    mNewHot.removeAll(mNewHot);
                    mNewHot.addAll(crowdsList);
                } else {
                    mListState = LOADE_MORE;
                    mNewHot.addAll(crowdsList);
                }
                mHotMore = response.getMore();
                if (response.getMore()) {
                    mHotIndex++;
                }
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();

                        if (mAdapter == null) {
//                            mAdapter = new CrowdGrideAdapter(mContext, mNewHot);
                            newAdapter(mNewHot);
                            mBody.setAdapter(mAdapter);
                        } else {
                            mAdapter.setData(mNewHot);
                            mBody.stopLoadMore(true);
                            mAdapter.notiRefresh();
                        }
                        if (mListState == REFRESH) {
                            mBody.stopRefresh();
                        } else {
                            mBody.stopLoadMore(true);
                        }
                        mBody.setHasLoadMore(mHotMore);

                    }
                }, 800);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                    }
                });

            }
        });
    }

    private void newAdapter(List<CsCrowd.Crowd> datas) {
        if (mShowList) {
            mAdapter = new CrowdListAdapter(mContext, datas);
        } else {
            mAdapter = new CrowdGrideAdapter(mContext, datas);
        }
    }

    private void getFinishingList() {
        if (mfinishing.size() == 0) showLoading();
        if (mFinishingIndex == 1) {
            mListState = REFRESH;
        }
        CsCrowd.GetCrowdListRequest.Builder builder = CsCrowd.GetCrowdListRequest.newBuilder();
        AccountManager instance = AccountManager.getInstance();
        builder.setType(2).setCurrencycode(instance.getCurrencyCode()).setLocalecode(instance.getLocaleCode());
        builder.setPageno(mFinishingIndex);
        if (crowdtagList != null && crowdtagList.size() > 0) {
            int crowdtagid = crowdtagList.get(endCategoryPositon).getCrowdtagid();
            builder.setCrowdtagid(crowdtagid);
        } else {
            builder.setCrowdtagid(0);
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsCrowd.GetCrowdListResponse>() {
            @Override
            public void onSuccess(CsCrowd.GetCrowdListResponse response) {
                List<CsCrowd.Crowd> crowdsList = response.getCrowdsList();
                if (mFinishingIndex == 1) {
                    mfinishing.removeAll(mfinishing);
                    mfinishing.addAll(crowdsList);
                } else {
                    mListState = LOADE_MORE;
                    mfinishing.addAll(crowdsList);
                }

                mFinishMore = response.getMore();
                if (response.getMore()) {
                    mFinishingIndex++;
                }

                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        if (mAdapter == null) {
//                            mAdapter = new CrowdGrideAdapter(mContext, mfinishing);
                            newAdapter(mfinishing);
                            mBody.setAdapter(mAdapter);
                        } else {
                            mAdapter.setData(mfinishing);
                            mAdapter.notiRefresh();
                        }
                        if (mListState == REFRESH) {
                            mBody.stopRefresh();
                        } else {
                            mBody.stopLoadMore(true);
                        }
                        mBody.setHasLoadMore(mFinishMore);

                    }
                }, 800);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                    }
                });
            }
        });
    }

    @Override
    public void switchLeft(SwitcherHotAndNew view) {
        mState = SHOW_HOT_NEW;
        if (mCategory != null)
            mToast.notifyDataSetChanged();
        if (mNewHot.size() == 0) {
            getHotNewList();
        } else {
            mAdapter.setData(mNewHot);
            mAdapter.notiRefresh();
        }
    }

    @Override
    public void switchRight(SwitcherHotAndNew view) {
        mState = SHOW_FINISHING;
        if (mCategory != null)
            mToast.notifyDataSetChanged();
        if (mfinishing.size() == 0) {
            getFinishingList();
        } else {
            mAdapter.setData(mfinishing);
            mAdapter.notiRefresh();
        }

    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        switch (mState) {
            case SHOW_FINISHING:
                mFinishingIndex = 1;
                getFinishingList();
                break;
            case SHOW_HOT_NEW:
                mHotIndex = 1;
                getHotNewList();
                break;
        }
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        switch (mState) {
            case SHOW_FINISHING:
                if (mFinishMore) {
                    getFinishingList();
                }
                break;
            case SHOW_HOT_NEW:
                if (mHotMore) {
                    getHotNewList();
                }
                break;
        }
    }

    public void refresh() {
        hotCategoryPositon = 0;
        endCategoryPositon = 0;
        crowdtagList = null;
        mBody.autoRefresh();
        getCategory();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_iv_right:
                switchAdapter();
                mShowList = !mShowList;
                showStateButton();
                break;
            case R.id.ll_other:
                mSwitcherHotAndNew.closeToast();
                break;
        }
    }

    @Override
    public void onSwitch(boolean open, boolean isLeft) {
        if (open) {
            mRootToast.setVisibility(View.VISIBLE);
        } else {
            mRootToast.setVisibility(View.GONE);
        }
    }
}