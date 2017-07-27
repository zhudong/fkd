package com.fuexpress.kr.model;



import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsCard;

/**
 * @创建者 chenyl
 * @创时间 2016/5/11
 * @描述 充值卡相关的manager
 * @版本 $Rev:
 * @更新者 $Author:
 * @更新时间 $Date:
 * @更新描述 TODO
 */

public class GiftCardManager {
    private static GiftCardManager mGiftCardManager = new GiftCardManager();
    public List<CsCard.GiftCardBalanceList> mGiftCardBalanceLists;
    public float mGiftcardaccount;
    public float mFrozenamount;

    private GiftCardManager() {
        mGiftCardBalanceLists = new ArrayList<>();
    }

    public static GiftCardManager getInstance() {
        return mGiftCardManager;
    }


    /**
     * 获得充值卡账户的数据
     */
    public void giftCardBalanceListRequest(final int uin, final int pageNum) {
        CsCard.GiftCardBalanceListRequest.Builder builder = CsCard.GiftCardBalanceListRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest()).setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GiftCardBalanceListReponse>() {


            @Override
            public void onSuccess(CsCard.GiftCardBalanceListReponse response) {
                mGiftcardaccount = response.getGiftcardaccount();
                mFrozenamount = response.getFrozenamount();
//                giftCardBalanceListAjaxRequest(uin, pageNum);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("请求充值卡失败:" + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_BAN_LIST_INIT_SUCCESS, false));
            }
        });
    }

    /**
     * 请求充值卡账户的列表方法
     */
    /*public void giftCardBalanceListAjaxRequest(int uin, final int page) {
        CsCard.GiftCardBalanceListAjaxRequest.Builder builder = CsCard.GiftCardBalanceListAjaxRequest.newBuilder();
        builder.setUin(uin);
        builder.setPage(page);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());

        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GiftCardBalanceListAjaxReponse>() {

            @Override
            public void onSuccess(CsCard.GiftCardBalanceListAjaxReponse response) {
                //LogUtils.e("我是充值卡账户的列表请求更多成功:" + response.toString());
                if (page == 1)
                    mGiftCardBalanceLists = new ArrayList<CsCard.GiftCardBalanceList>();
                mGiftCardBalanceLists.addAll(response.getListList());
                int size = response.getListList().size();
                //int listCount = response.getListCount();
                //LogUtils.e("这是listCount:" + listCount);
                if (page != 1) {
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_LIST_MORE_COMPLETE, true, size));
                } else {
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_BAN_LIST_INIT_SUCCESS, true, size));
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("我是充值卡账户的列表请求更多失败" + " " + ret + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_LIST_MORE_COMPLETE, false));
            }
        });
    }
    */


    public void getGiftCardBalanceRequest() {
//        CsCard.GetGiftCardBalanceRequest.Builder builder = CsCard.GetGiftCardBalanceRequest.newBuilder();
//        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
//        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GetGiftCardBalanceResponse>() {
//
//            @Override
//            public void onSuccess(CsCard.GetGiftCardBalanceResponse response) {
//                mGiftcardaccount = response.getFreeBalance();
//                mFrozenamount = response.getFrozenBalance();
//                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_BALANCE_COMPLETE, true));
//            }
//
//            @Override
//            public void onFailure(int ret, String errMsg) {
//                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_BALANCE_COMPLETE, false));
//            }
//        });

    }
/*/
    *//**
     * 获取充值卡的购买来源
     *
     * @param cardNum 充值卡的卡号
     *//*
    public void getGiftCardStoresiteRequest(String cardNum) {
        CsCard.GetGiftCardStoresiteRequest.Builder builder = CsCard.GetGiftCardStoresiteRequest.newBuilder();
        builder.setGiftCard(cardNum);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GetGiftCardStoresiteResponse>() {
            @Override
            public void onSuccess(CsCard.GetGiftCardStoresiteResponse response) {
                //LogUtils.e("我是请求充值卡账号来源的方法,我成功了!" + response.toString());
                int storesiteId = response.getStoresiteId();
                String currencycode = response.getCurrencycode();
                String whereComeFrom = "";
                if (4 == storesiteId) {
                    whereComeFrom = SysApplication.getContext().getString(R.string.gift_card_from_yiss);
                } else if (2 == storesiteId) {
                    whereComeFrom = SysApplication.getContext().getString(R.string.gift_card_from_ddm);
                } else if (3 == storesiteId) {
                    whereComeFrom = SysApplication.getContext().getString(R.string.gift_card_from_oneyiss);
                } else if (1 == storesiteId) {
                    whereComeFrom = SysApplication.getContext().getString(R.string.gift_card_from_ddm);
                }
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_STORESITE_COMPLETE, true, whereComeFrom, currencycode));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("我是请求充值卡账号来源的方法,失败!" + " " + ret + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_STORESITE_COMPLETE, false, errMsg));
            }
        });
    }

    *//**
     * 绑定充值卡的请求:
     *
     * @param cardNum 充值卡的卡号
     *//*
    public void bindGiftCardRequest(String cardNum) {
        CsCard.BindGiftCardRequest.Builder builder = CsCard.BindGiftCardRequest.newBuilder();
        builder.setGiftCard(cardNum).setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.BindGiftCardResponse>() {


            @Override
            public void onSuccess(CsCard.BindGiftCardResponse response) {
                //LogUtils.e("我是请求绑定的方法,我成功了!" + response.toString());
                EventBus.getDefault().post(new BusEvent(BusEvent.BINDING_GIFT_CARD_REQUEST_COMPLETE, true));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("我是请求绑定的方法,失败!" + " " + ret + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.BINDING_GIFT_CARD_REQUEST_COMPLETE, false));
            }
        });
    }*/

}
