
package com.fuexpress.kr.ui.activity.merchant_detail;

import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;

import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.MerChantBean;
import com.fuexpress.kr.model.OperaRequestManager;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.adapter.NewAlbumFollowersAdapter;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.ui.view.TitleBarView;


import java.util.ArrayList;


import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Longer on 2017/6/26.
 */


import fksproto.CsMerchant;

public class MerChantFollowsActivity02 extends BaseActivity implements RefreshListView.OnRefreshListener {

    private View mRootView;
    private RefreshListView mLv_for_merchant_follows;
    //private TitleBarView mTitleBarView;
    //private ImageView mImageViewLeft;
    private View mHeadView;
    private TextView mTv_in_head_for_merchant_follows_lv_followsnum;
    private int mPageNum = 1;
    private NewAlbumFollowersAdapter mAlbumFollowersAdapter;
    public static String MERCHANT_FOLLOWERS_INITENT_KEY = "merchant_name";
    private String mMerchantName;
    private boolean mIsHasMore = false;
    private int mFollowcount;
    private TextView mTvIsFollowed;
    private MerChantBean mMerChantBean;
    private TextView tv_in_head_for_merchant_follows_lv_name;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_mer_chant_follows, null);

        return mRootView;
    }

    @Override
    public void initView() {
        mLv_for_merchant_follows = (RefreshListView) mRootView.findViewById(R.id.lv_for_merchant_follows);
        mLv_for_merchant_follows.setHeaderViewHide();
        mLv_for_merchant_follows.setOnRefreshListener(this);
        mMerchantName = getIntent().getStringExtra("mName");
        initHeadView();
        mAlbumFollowersAdapter = new NewAlbumFollowersAdapter(this, new ArrayList<CsMerchant.FollowerList>());
        mLv_for_merchant_follows.setAdapter(mAlbumFollowersAdapter);
        mLv_for_merchant_follows.addHeaderView(mHeadView);
        initData(mPageNum);
    }


    private void initData(int pageNum) {
        long mID = getIntent().getLongExtra("merchantId", AccountManager.getInstance().mUin);
        MerchantDetailManager.getInstance().getMerchantFollowersRequest(mID, pageNum);
    }


    /**
     * 初始化头布局:
     */
    private void initHeadView() {
        mHeadView = View.inflate(this, R.layout.head_for_merchant_detail_list, null);
        mMerChantBean = MerchantDetailManager.getInstance().mMerChantBean;
        mTvIsFollowed = (TextView) mHeadView.findViewById(R.id.tv_in_head_for_merchant_follows_lv_attention);
        tv_in_head_for_merchant_follows_lv_name = (TextView) mHeadView.findViewById(R.id.tv_in_head_for_merchant_follows_lv_name);
        tv_in_head_for_merchant_follows_lv_name.setText(mMerchantName);
        setFollowersCount(getString(R.string.merchant_detail_follower_befor_string));
        TitleBarView title_in_merfollows_new = (TitleBarView) mRootView.findViewById(R.id.title_in_merfollows_new);
        title_in_merfollows_new.getIv_in_title_back().setOnClickListener(this);
        TextView bTv = title_in_merfollows_new.getmTv_in_title_back_tv();
        title_in_merfollows_new.setTitleText(getString(R.string.merchant_detail_follows_title));
        bTv.setOnClickListener(this);
        bTv.setText(mMerchantName);
        mTvIsFollowed.setOnClickListener(this);

    }

    private void setFollowersCount(String followersCount) {
        mTv_in_head_for_merchant_follows_lv_followsnum = (TextView) mHeadView.findViewById(R.id.tv_in_head_for_merchant_follows_lv_followsnum);
        mTv_in_head_for_merchant_follows_lv_followsnum.setText(followersCount);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
                /*finish();
                break;*/
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.tv_in_head_for_merchant_follows_lv_attention:
                if (AccountManager.isLogin) {
                    /*if (!UIUtils.isFastClick()) {
                        operaFollowMethond();
                    }*/
                    operaFollowMethond();
                } else {
                    AccountManager.getInstance().isUserLogin(SysApplication.getContext());
                }
                break;
        }
    }

    private void operaFollowMethond() {
        OperaRequestManager operaRequestManager = OperaRequestManager.getInstance();
        long merchantid = mMerChantBean.getMerchantid();
        final boolean follow = mMerChantBean.is_follow();
        operaRequestManager.operateLikeMerchant(merchantid, mMerChantBean, this, new OperaRequestListener() {
            @Override
            public void onOperaSuccess() {
            }

            @Override
            public void onOperaFailure() {

            }
        });
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        mLv_for_merchant_follows.stopRefresh();
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (mIsHasMore) {
            mPageNum += 1;
            initData(mPageNum);
        }
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_MERCHANT_FOLLOWS_LIST_COMPLETE:
                //mLv_for_merchant_follows.stopRefresh();
                boolean isSuccess = event.getBooleanParam();
                if (isSuccess) {
                    mFollowcount = MerchantDetailManager.getInstance().mFollowcount;
                    //setFollowersCount(getString(R.string.merchant_detail_follower_befor_string + mFollowcount));
                    tv_in_head_for_merchant_follows_lv_name.setText(MerchantDetailManager.getInstance().mMerchantname);
                    mTv_in_head_for_merchant_follows_lv_followsnum.setText(getString(R.string.merchant_detail_follower_befor_string) + mFollowcount);
                    if (1 == mPageNum) {
                        mAlbumFollowersAdapter = new NewAlbumFollowersAdapter(this, MerchantDetailManager.getInstance().mFollowerlistList);
                        mLv_for_merchant_follows.setAdapter(mAlbumFollowersAdapter);
                    } else {
                        mAlbumFollowersAdapter.notifyDataSetChanged();
                    }
                    mIsHasMore = event.getBooleanParam02();
                    mLv_for_merchant_follows.stopLoadMore(true);
                    mLv_for_merchant_follows.setHasLoadMore(mIsHasMore);
                    if (MerchantDetailManager.getInstance().isFollow.equals("1")) {
                        mTvIsFollowed.setText(getString(R.string.merchant_detail_followed));
                    } else {
                        mTvIsFollowed.setText(getString(R.string.merchant_detail_followed_not));
                    }
                } else {
                    /*showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.string_for_send_requset_fail));
                    dissMissProgressDiaLog(1000);*/
                    CustomToast.makeText(this, event.getStrParam(), Toast.LENGTH_SHORT).show();

                }
                break;
            case BusEvent.OPERA_MERCHANT_SUCCESS:
                initData(mPageNum = 1);
                /*MerChantBean merChantBean = (MerChantBean) event.getParam();
                boolean follow = merChantBean.is_follow();
                int follow_num = merChantBean.getFollow_num();
                if (follow) {
                    mTvIsFollowed.setText(getString(R.string.merchant_detail_followed));
                } else {
                    mTvIsFollowed.setText(getString(R.string.merchant_detail_followed_not));
                }
                mTv_in_head_for_merchant_follows_lv_followsnum.setText(getString(R.string.merchant_detail_follows_space) + follow_num);*/
                break;
            case BusEvent.REQUEST_FAIL:
                CustomToast.makeText(this, event.getStrParam(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

