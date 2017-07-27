package com.fuexpress.kr.model;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.BindingGiftCardActivity;
import com.fuexpress.kr.ui.activity.HomeWeblActivity;
import com.fuexpress.kr.ui.activity.IntegralManagementActivity;
import com.fuexpress.kr.ui.activity.InviteFriendsActivity;
import com.fuexpress.kr.ui.activity.coupon.CouponsActivity;
import com.fuexpress.kr.ui.activity.notice.NoticeListActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.NetworkImageHolderView;
import com.fuexpress.kr.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fksproto.CsCard;
import fksproto.CsFeed;

/**
 * Created by Administrator on 2016-10-28.
 */
public class HeaderModel {
    public static HeaderModel mInstance = new HeaderModel();
    private List<CsFeed.Banner> mBannersList;
    private ArrayList mNetworkImages;
    android.os.Handler mHandler = new android.os.Handler();
    private Activity mContext;

    private HeaderModel() {
    }

    public void cleanData() {
        mBannersList = null;
    }


    public static HeaderModel getInstance() {
        return mInstance;
    }

    public void initDta(Activity mContext, ConvenientBanner mConvenientBanner) {
        mConvenientBanner.refreshDrawableState();
        int screenWidthPixels = UIUtils.getScreenWidthPixels(mContext);
        int height = (int) (screenWidthPixels / 2.35);
        mConvenientBanner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        initPages(mContext, mConvenientBanner);
    }

    private void initPages(Activity mContext, ConvenientBanner mConvenientBanner) {
        this.mContext = mContext;
        mConvenientBanner.refreshDrawableState();

        if (mBannersList == null) {
            getBannerList(mContext, mConvenientBanner);
            return;
        }
        mNetworkImages = new ArrayList();
        for (CsFeed.Banner banner : mBannersList) {
            mNetworkImages.add(banner.getUrl());
        }

        if (mNetworkImages.size() <= 1) {
            mConvenientBanner.stopTurning();
            mConvenientBanner.setCanLoop(false);
        } else {
            mConvenientBanner.startTurning(5000);
            mConvenientBanner.setCanLoop(true);
            mConvenientBanner.setPageIndicator(new int[]{R.drawable.graypoint, R.drawable.redpoint});
        }

        mConvenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, mNetworkImages)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);

        mConvenientBanner.startTurning(5000);
        mConvenientBanner.setOnItemClickListener(new ItemListener());
    }

    public void getBannerList(final Activity mContext, final ConvenientBanner mConvenientBanner) {

//        Locale locale = mContext.getResources().getConfiguration().locale;
        String lang = AccountManager.getInstance().getLocaleCode();
        CsFeed.IndexAdImageRequest.Builder builder = CsFeed.IndexAdImageRequest.newBuilder();
        int tiem = (int) (new Date().getTime() / 1000);
        builder.setLocalecode(lang).setVersionNo(CommonUtils.getVersionName(mContext)).setApptype(2);
        INetEngineListener<CsFeed.IndexAdImageResponse> listener = new INetEngineListener<CsFeed.IndexAdImageResponse>() {
            @Override
            public void onSuccess(CsFeed.IndexAdImageResponse response) {
                mBannersList = response.getBannersList();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initPages(mContext, mConvenientBanner);
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                Log.d("fail", "fail");
            }
        };
        NetEngine.postRequest(builder, listener);
    }

    class ItemListener implements OnItemClickListener {

        @Override
        public void onItemClick(int position) {
            CsFeed.Banner banner = mBannersList.get(position);
            String jump = banner.getJump();
            Intent intent;

            //需要告诉跳转页面返回标题是什么,详见TB福快递Android 1.0.4版的 BUG 37
            String backTitle = SysApplication.getContext().getString(R.string.main_home_tab_string);
//            绑定充值卡(jump:redeem-gift-card)”、优惠券(jump:coupon)、邀请好友(jump:invite-friend)、通知(jump:notification)；
            if (jump.contains("jump") && jump.contains("crowd-ordering")) {
                return;
            } else if (jump.contains("jump") && jump.contains("redeem-gift-card")) {
                intent = new Intent(mContext, BindingGiftCardActivity.class);
                intent.putExtra(Constants.RETURN_TITLE, backTitle);
                mContext.startActivity(intent);
            } else if (jump.contains("jump") && jump.contains("coupon")) {
                intent = new Intent(mContext, CouponsActivity.class);
                intent.putExtra(Constants.RETURN_TITLE, backTitle);
                mContext.startActivity(intent);
            } else if (jump.contains("jump") && jump.contains("invite-friend")) {
                intent = new Intent(mContext, InviteFriendsActivity.class);
                intent.putExtra(Constants.RETURN_TITLE, backTitle);
                mContext.startActivity(intent);
            } else if (jump.contains("jump") && jump.contains("jump:notification")) {
                intent = new Intent(mContext, NoticeListActivity.class);
                intent.putExtra(Constants.RETURN_TITLE, backTitle);
                mContext.startActivity(intent);
            } else if (jump.contains("http")) {
                intent = new Intent(mContext, HomeWeblActivity.class);
                intent.putExtra(HomeWeblActivity.URL, banner.getJump());
                intent.putExtra(HomeWeblActivity.TITLE, banner.getTitle());
                intent.putExtra(HomeWeblActivity.NAVIGATION, mContext.getString(R.string.main_home_tab_string));
                mContext.startActivity(intent);
            }
        }
    }

}
