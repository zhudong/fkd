package com.fuexpress.kr.ui.activity.produ_source;

import android.support.v4.util.LongSparseArray;
import android.util.Log;

import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.model.MerChantBean;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.utils.ClassUtil;
import com.fuexpress.kr.utils.LogUtils;


import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsMerchant;

/**
 * Created by Longer on 2017/6/26.
 * 货源信息的DataManager
 */
public class ProduSrcDataManager {

    private static ProduSrcDataManager sProduSrcDataManager;

    //public List<CsBase.Item> mItemsList;
    public List<CsBase.PairIntInt> mHotPairsList;
    public List<CsBase.PairIntInt> mAllPairsList;
    public LongSparseArray<ItemBean> mLocalMapForAllItems;
    public LongSparseArray<ItemBean> mLocalMapForHotItems;
    public List<MerChantBean> mAllMerChantBeanList;
    public List<MerChantBean> mHotMerChantBeanList;
    public LongSparseArray<List<Long>> mHotMapForPairsProcessed;
    public LongSparseArray<List<Long>> mAllMapForPairsProcessed;
    private long mLastKey;
    public ArrayList<Long> mItemIdList;
    private boolean mIsHasMore;
    public List<MerChantBean> mFollowedMerchantBeans;

    private ProduSrcDataManager() {
    }

    public static ProduSrcDataManager getInstance() {
        if (sProduSrcDataManager == null)
            sProduSrcDataManager = new ProduSrcDataManager();
        return sProduSrcDataManager;
    }

    public void getProductSourceAll(String className, int pageNum) {
        getHotMerchantList2Request(className, 0, pageNum);
    }

    public void getProductSourceHot(String className, int pageNum) {
        getHotMerchantList2Request(className, 1, pageNum);
    }

    public void getProductSourceFollowed(int pageNuun, String typeName) {
        getFollowMerchantListRequest(pageNuun, typeName);
    }


    private void getFollowMerchantListRequest(int pageNum, final String typeName) {
        CsMerchant.GetFollowMerchantListRequest.Builder builder = CsMerchant.GetFollowMerchantListRequest.newBuilder();

        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());

        builder.setPageno(pageNum);

        builder.setAuthor(AccountManager.getInstance().mUin);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        //builder.setAuthor(21953);

        if (pageNum == 1)
            mFollowedMerchantBeans = new ArrayList<>();

        NetEngine.postRequest(builder, new INetEngineListener<CsMerchant.GetFollowMerchantListResponse>() {
            @Override
            public void onSuccess(CsMerchant.GetFollowMerchantListResponse response) {
                LogUtils.e(response.toString());
                List<CsBase.Merchant> merchantsList = response.getMerchantsList();
                mFollowedMerchantBeans.addAll(ClassUtil.conventMerChantList2MerChantBeanList(merchantsList));
                boolean more = response.getMore();
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_PRODUSRC_FOLLOWED_COMPETE, true, more, typeName));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                LogUtils.e(ret + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_PRODUSRC_FOLLOWED_COMPETE, false, ret + errMsg));
            }
        });
    }


    /**
     * 这是按照tab的选择情况进行网络数据加载的方法:
     *
     * @param status  是初始状态还是加载更多的状态,0位初始,1为加载更多
     * @param pageNum 页数
     */
    public void getHotMerchantList2Request(final String typeName, final int status, final int pageNum) {
        if (1 == pageNum) {
            ArrayList<Object> mMerchantsList = new ArrayList<>();
            //mItemsList = new ArrayList<>();
            if (0 == status) {
                mAllPairsList = new ArrayList<>();
                mAllMerChantBeanList = new ArrayList<>();
                mLocalMapForAllItems = new LongSparseArray<>();
            } else {
                mHotPairsList = new ArrayList<>();
                mHotMerChantBeanList = new ArrayList<>();
                mLocalMapForHotItems = new LongSparseArray<>();
            }

        }

        CsMerchant.GetHotMerchantList2Request.Builder builder = CsMerchant.GetHotMerchantList2Request.newBuilder();

        builder.setPageno(pageNum);

        CsBase.PairIntInt.Builder builder2 = CsBase.PairIntInt.newBuilder();
        CsBase.PairIntInt.Builder builder3 = CsBase.PairIntInt.newBuilder();
        CsBase.PairIntInt.Builder builder1 = CsBase.PairIntInt.newBuilder();

        builder1.setK(1);
        builder1.setV(0);
        builder.addConds(0, builder1);
        builder2.setK(2);
        builder2.setV(0);
        builder3.setK(3);
        builder3.setV(status);
        builder.addConds(1, builder2);
        builder.addConds(2, builder3);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencyCode(AccountManager.getInstance().getCurrencyCode());
        builder.setCurrencyID(AccountManager.getInstance().getCurrencyId());
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setBizType(2);


        NetEngine.postRequest(builder, new INetEngineListener<CsMerchant.GetHotMerchantList2Response>() {
            @Override
            public void onSuccess(CsMerchant.GetHotMerchantList2Response response) {
                //LogUtils.e("这是请求最热商圈的方法:" + response.toString());
                //mMerchantsList.addAll(response.getMerchantsList());
                if (status == 1) {
                    mHotPairsList = response.getPairsList();
                    ClassUtil.convertItemList2ItemBeanMap(response.getItemsList(), mLocalMapForHotItems);
                    mHotMerChantBeanList.addAll(ClassUtil.conventMerChantList2MerChantBeanList(response.getMerchantsList()));
                } else {
                    mAllPairsList = response.getPairsList();
                    ClassUtil.convertItemList2ItemBeanMap(response.getItemsList(), mLocalMapForAllItems);
                    mAllMerChantBeanList.addAll(ClassUtil.conventMerChantList2MerChantBeanList(response.getMerchantsList()));
                }
                mIsHasMore = response.getMore();
                processPairsList(typeName, status, pageNum);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("这是请求最热商圈的方法失败了:" + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.REQUEST_FAIL, null, ret + errMsg));
            }
        });
    }


    public void processPairsList(String className, int status, int page) {
        if (1 == page) {
            mHotMapForPairsProcessed = new LongSparseArray<>();
            mAllMapForPairsProcessed = new LongSparseArray<>();
        }

        List<CsBase.PairIntInt> pairsList = 0 == status ? mAllPairsList : mHotPairsList;
        LongSparseArray<List<Long>> mapForPairsProcessed = 0 == status ? mAllMapForPairsProcessed : mHotMapForPairsProcessed;

        for (int x = 0; x < pairsList.size(); x++) {
            CsBase.PairIntInt pairIntInt = pairsList.get(x);
            long k = pairIntInt.getK();
            long v = pairIntInt.getV();
            if (x == 0) {
                mLastKey = k;//当等于0的时候,需要一个新集合:
                mItemIdList = new ArrayList<Long>();//创建新集合
                //mMapForPairsProcessed = new LongSparseArray<>();
            } else if (k != mLastKey) {
                mapForPairsProcessed.put(mLastKey, mItemIdList);//当产生不同的时候,我们先添加到map集合中去:
                mLastKey = k;//然后把上一个的商店id给覆盖掉:
                mItemIdList = new ArrayList<Long>();//创建一个新的集合,让其转移指向:
            } else if (x == pairsList.size() - 1) {
                if (k != mLastKey) {//最后一个时，假如与前面的商户key不一致的话，我们就新建一个itemList加进map中
                    mapForPairsProcessed.put(mLastKey, mItemIdList);
                    mItemIdList = new ArrayList<>();
                    mItemIdList.add(v);
                    mapForPairsProcessed.put(k, mItemIdList);
                } else {//一致时，我们就加完进itemList再加到map中
                    mItemIdList.add(v);
                    mapForPairsProcessed.put(k, mItemIdList);
                }
                mItemIdList = new ArrayList<>();
                mLastKey = k;
            }
            //首先先加入到一个集合中去先:
            if (!mItemIdList.contains(v)) {
                mItemIdList.add(v);
            }
            //mLocalPairsList.add(pairIntInt);
        }
        if (0 == status) {
            EventBus.getDefault().post(new BusEvent(BusEvent.GET_PRODUSRC_ALL_COMPETE, true, mIsHasMore, className));
        } else {
            EventBus.getDefault().post(new BusEvent(BusEvent.GET_PRODUSRC_HOT_COMPETE, true, mIsHasMore, className));
        }
    }
}
