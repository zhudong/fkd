
package com.fuexpress.kr.ui.activity.merchant_detail;

import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.model.MerChantBean;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.utils.ClassUtil;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsMerchant;

/**
 * Created by Longer on 2017/6/26.
 */

public class MerchantDetailManager {

    private static MerchantDetailManager mMerchantDetailManager = new MerchantDetailManager();
    public String mMainBusiness;
    public MerChantBean mMerChantBean;
    public List<ItemBean> mLoaclItemList;
    public List<CsBase.MerchantImage> mMerchantImagesList;
    public ArrayList<String> mImageUrlList;
    public List<CsBase.Follower> mLocalFollowersList;
    public List<String> mShopTimeData;
    public String mStatus;
    public List<CsMerchant.FollowerList> mFollowerlistList;
    public String mMerchantname;
    public int mFollowcount;
    public String isFollow;

    private MerchantDetailManager() {
        mLoaclItemList = new ArrayList<>();
        mMerchantImagesList = new ArrayList<>();
        mImageUrlList = new ArrayList<>();
        mLocalFollowersList = new ArrayList();
    }

    public static MerchantDetailManager getInstance() {
        return mMerchantDetailManager;
    }

    public void getMerChantDetailRequest(long mMerchantid) {
        CsMerchant.GetMerchantDetailRequest.Builder builder02 = CsMerchant.GetMerchantDetailRequest.newBuilder();
        builder02.setMerchantId(mMerchantid);
        builder02.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder02.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder02, new INetEngineListener<CsMerchant.GetMerchantDetailResponse>() {

            @Override
            public void onSuccess(CsMerchant.GetMerchantDetailResponse response) {
                CsBase.Merchant merchant = response.getMerchant();
                mMerChantBean = ClassUtil.conventMerchant2MerChantBean(merchant);
                CsBase.MerchantBusiness merchantBusiness = response.getMerchantBusiness();
                mMainBusiness = merchantBusiness.getMainBusiness();
                mMerchantImagesList = response.getMerchantImagesList();
                mStatus = response.getStatus();
                setImagesUrlList();
                /*List<CsBase.MerchantShopTime> shopTimesList = merchantBusiness.getShopTimesList();
                for (int i = 0; i < shopTimesList.size(); i++) {
                    LogUtils.e(shopTimesList.get(i) + "");
                }*/
                List<CsBase.MerchantShopTime> shopTimesList = merchantBusiness.getShopTimesList();
                mShopTimeData = StringUtils.handleShopTimeData(shopTimesList);
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_MERCHANT_DETAIL_SUCCESS, mMerChantBean));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("我是请求商户详情的方法,我是失败了");
                EventBus.getDefault().post(new BusEvent(BusEvent.REQUEST_FAIL, null));
            }
        });
    }

    /**
     * 对商店详情页进行其ItemList的请求
     *
     * @param merchantId
     * @param pageNum
     * @param status     状态,0是刷新,1是更多
     */
    public void getMerchantItemsList(long merchantId, int pageNum, final int status) {
        if (status == 0) {
            mLoaclItemList = new ArrayList<>();
        }
        CsMerchant.GetMerchantItemListRequest.Builder builder = CsMerchant.GetMerchantItemListRequest.newBuilder();
        builder.setPageno(pageNum);
        builder.setMerchantId(merchantId);
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsMerchant.GetMerchantItemListResponse>() {
                    @Override
                    public void onSuccess(CsMerchant.GetMerchantItemListResponse response) {
                        //LogUtils.e("我是请求商店列表的方法的返回值:" + response.toString());
                        boolean mMore = response.getMore();
                        mLoaclItemList.addAll(ClassUtil.convertItemList2ItemBean(response.getItemsList()));
                        if (0 == status) {
                            EventBus.getDefault().post(new BusEvent(BusEvent.GET_MERCHANT_ITEMS_LIST_SUCCESS, mMore));
                        } else {
                            EventBus.getDefault().post(new BusEvent(BusEvent.GET_MERCHANT_ITEMS_LIST_SUCCESS_MORE, mMore));
                        }
                    }

                    @Override
                    public void onFailure(int ret, String errMsg) {
                        //LogUtils.e("我是请求商店列表的方法的失败返回值:" + errMsg);
                        EventBus.getDefault().post(new BusEvent(BusEvent.REQUEST_FAIL, null));
                    }
                }

        );
    }


    private void setImagesUrlList() {
        mImageUrlList = new ArrayList<>();
        for (int x = 0; x < mMerchantImagesList.size(); x++) {
            CsBase.MerchantImage merchantImage = mMerchantImagesList.get(x);
            String imageUrl = merchantImage.getImageUrl();
            mImageUrlList.add(imageUrl);
        }
    }


    public void getMerchantFollowerListRequest(long merchantId, int pageNum, final int status) {
        if (status == 0) {
            mLocalFollowersList = new ArrayList<>();
        }
        CsMerchant.GetMerchantFollowerListRequest.Builder builder = CsMerchant.GetMerchantFollowerListRequest.newBuilder();
        builder.setPageno(pageNum);
        builder.setMerchantId(merchantId);
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsMerchant.GetMerchantFollowerListResponse>() {
            @Override
            public void onSuccess(CsMerchant.GetMerchantFollowerListResponse response) {
                mLocalFollowersList.addAll(response.getFollowersList());
                boolean mMore = response.getMore();
                if (0 == status) {
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_MERCHANT_FOLLOWS_SUCCESS, mMore));
                } else if (1 == status) {
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_MERCHANT_FOLLOWS_SUCCESS_MORE, mMore));
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("我是请求商店的关注者列表的方法,我失败了:" + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.REQUEST_FAIL, null));
            }
        });
    }

    public void getMerchantFollowersRequest(long merID, int pageNum) {
        CsMerchant.FollowersRequest.Builder builder = CsMerchant.FollowersRequest.newBuilder();
        //builder.setSecond(AccountManager.getInstance().mBaseUserRequest);
        builder.setPagenum(pageNum);
        CsBase.BaseUserRequest.Builder builder1 = AccountManager.getInstance().getBaseUserRequest();
        builder1.setMid((int) merID);
        builder.setSecond(builder1);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        if (1 == pageNum) {
            mFollowerlistList = new ArrayList<>();
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsMerchant.FollowersReponse>() {
            @Override
            public void onSuccess(CsMerchant.FollowersReponse response) {
                mMerchantname = response.getMerchantname();
                mFollowcount = response.getFollowcount();
                String status = response.getStatus();
                isFollow = response.getIsFollow();
                boolean isHasMore = false;
                if ("more".equals(status)) {
                    isHasMore = true;
                }
                mFollowerlistList.addAll(response.getFollowerlistList());
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_MERCHANT_FOLLOWS_LIST_COMPLETE, true, isHasMore));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                LogUtils.e("fail: " + ret + " " + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_MERCHANT_FOLLOWS_LIST_COMPLETE, false, ret + errMsg));
            }
        });
    }
}

