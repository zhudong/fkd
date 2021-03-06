package com.fuexpress.kr.model;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.MemberBean;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.utils.LogUtils;
import com.google.protobuf.GeneratedMessage;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsCard;
import fksproto.CsMy;

/**
 * Created by Longer on 2016/11/1.
 */
public class BalanceManager {
    private static BalanceManager mGiftCardManager = new BalanceManager();
    public List<CsCard.GiftCardBalanceList> mGiftCardBalanceLists;
    public float mGiftcardaccount;
    public float mFrozenamount;
    private String mUpFlag;
    private List<CsCard.MemberGroupList> mMemberGroupLists;

    private BalanceManager() {
        mGiftCardBalanceLists = new ArrayList<>();
    }

    public static BalanceManager getInstance() {
        return mGiftCardManager;
    }


    /**
     * 获得充值卡账户的数据
     */
    public void giftCardBalanceListRequest(final int uin, final int pageNum) {
        CsCard.GiftCardBalanceListRequest.Builder builder = CsCard.GiftCardBalanceListRequest.newBuilder();
        //  builder.setUin(uin).setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setUserHead(AccountManager.getInstance().mBaseUserRequest).setCurrencycode(AccountManager.getInstance().getCurrencyCode()).setPage(pageNum);
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GiftCardBalanceListReponse>() {
            @Override
            public void onSuccess(CsCard.GiftCardBalanceListReponse response) {
                //LogUtils.e("请求充值卡成功:" + response.toString());
                if (pageNum == 1) {
                    mGiftCardBalanceLists = new ArrayList<>();
                    mGiftcardaccount = response.getGiftcardaccount();
                    mFrozenamount = response.getFrozenamount();
                }
                mGiftCardBalanceLists.addAll(response.getListList());
                String status = response.getStatus();
                //giftCardBalanceListAjaxRequest(uin, 1);
                //int size = mGiftCardBalanceLists.size();
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_BAN_LIST_INIT_SUCCESS, true, status));
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
    public void giftCardBalanceListAjaxRequest(int uin, final int page) {
        /*CsCard.GiftCardBalanceListAjaxRequest.Builder builder = CsCard.GiftCardBalanceListAjaxRequest.newBuilder();
        //  builder.setUin(uin);
        builder.setUserHead(AccountManager.getInstance().mBaseUserRequest);
        builder.setPage(page);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());

        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GiftCardBalanceListAjaxReponse>() {

            @Override
            public void onSuccess(CsCard.GiftCardBalanceListAjaxReponse response) {
                //LogUtils.e("我是充值卡账户的列表请求更多成功:" + response.toString());
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
        });*/
    }

    /**
     * 获取账户充值余额请求:
     */
    public void getGiftCardBalanceRequest() {
        CsMy.GetAccountBalanceRequest.Builder builder = CsMy.GetAccountBalanceRequest.newBuilder();
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsMy.GetAccountBalanceResponse>() {

            @Override
            public void onSuccess(CsMy.GetAccountBalanceResponse response) {
                //LogUtils.e("我是获取充值卡账户余额的方法,成功!" + response.toString());
                mGiftcardaccount = response.getFreeBalance();
                mFrozenamount = response.getFrozenBalance();
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_BALANCE_COMPLETE, true));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("我是获取充值卡账户余额的方法,失败!" + " " + ret + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_BALANCE_COMPLETE, false));
            }
        });

    }

    /**
     * 初始化绑定充值卡的接口
     */
    public void redeemGiftCardRequest() {

        CsCard.RedeemGiftCardRequest.Builder builder = CsCard.RedeemGiftCardRequest.newBuilder();

        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());

        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());

        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());

        NetEngine.postRequest(builder, new INetEngineListener<CsCard.RedeemGiftCardReponse>() {

            @Override
            public void onSuccess(CsCard.RedeemGiftCardReponse response) {
                mGiftcardaccount = response.getGiftcardaccount();
                mMemberGroupLists = new ArrayList<CsCard.MemberGroupList>();
                mMemberGroupLists.addAll(response.getMemberGroupListList());
                mUpFlag = response.getLevelupflag();
                EventBus.getDefault().post(new BusEvent(BusEvent.REDEEM_GIFT_CARD_COMPETE, true));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                EventBus.getDefault().post(new BusEvent(BusEvent.REDEEM_GIFT_CARD_COMPETE, false, ret + errMsg));
            }
        });
    }

    /**
     * 获取充值卡的购买来源
     *
     * @param cardNum 充值卡的卡号
     */
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
                    whereComeFrom = SysApplication.getContext().getString(R.string.gift_card_from_there);
                } else if (6 == storesiteId) {
                    whereComeFrom = SysApplication.getContext().getString(R.string.gift_card_from_fkd);
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

    /**
     * 绑定充值卡的请求:
     *
     * @param isUp     是否需要升级会员组
     * @param memberID 升级的会员组ID
     * @param cardNum  充值卡的卡号
     */
    public void bindGiftCardRequest(boolean isUp, int memberID, String cardNum) {
        CsCard.BindGiftCardRequest.Builder builder = CsCard.BindGiftCardRequest.newBuilder();
        builder.setGiftCard(cardNum);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest()).setLocaleCode(AccountManager.getInstance().getLocaleCode());
        if (isUp) {
            builder.setLevelup(1);
            builder.setMembergroupid(memberID);
        }
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
    }

    public void getGiftCardOrderListRequest(final int orderType, final int statusType, int pageNum, final int status) {
        CsCard.GetGiftCardOrderListRequest.Builder builder = CsCard.GetGiftCardOrderListRequest.newBuilder();
        builder.setPageno(pageNum);
        builder.setOrderType(orderType);
        builder.setStatusType(statusType);
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setUserHead(AccountManager.getInstance().mBaseUserRequest);
        //builder.setUin(AccountManager.getInstance().mUin);
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GetGiftCardOrderListReponse>() {
            @Override
            public void onSuccess(CsCard.GetGiftCardOrderListReponse response) {
                KLog.i(response.toString());
                boolean more = response.getMore();
                if (0 == status) {
                    mGiftcarditemsList01 = new ArrayList<CsCard.GiftCardOrderItem>();
                }

                mGiftcarditemsList01.addAll(response.getGiftcarditemsList());
                mPendingOrderNum_01 = response.getPendingOrderNum();

                //LogUtils.e("我是请求充值卡订单列表的方法,我成功了" + response.toString());
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_LIST_REQUEST_COMPLETE, true, more, status));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("我是请求充值卡订单列表的方法,我失败了:" + ret + " " + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_GIFT_CARD_LIST_REQUEST_COMPLETE, false, status));
            }
        });
    }

    public List<CsCard.GiftCardOrderItem> mGiftcarditemsList01;
    public int mPendingOrderNum_01;


    public String getUpFlag() {
        return mUpFlag;
    }

    public List<MemberBean> getMemberGroupLists() {
        List<MemberBean> memberBeans = new ArrayList<>();
        if (mMemberGroupLists == null) {
            mMemberGroupLists = new ArrayList<>();
        } else {
            for (int i = 0; i < mMemberGroupLists.size(); i++) {
                CsCard.MemberGroupList memberGroupList = mMemberGroupLists.get(i);
                MemberBean memberBean = new MemberBean();
                memberBean.setIsSelected(i == 0);
                memberBean.setMemberId(memberGroupList.getMembergroupid());
                memberBean.setMemberName(memberGroupList.getMembergroupname());
                memberBean.setSingAndPrice(memberGroupList.getSignandfee());
                memberBeans.add(memberBean);
            }
        }
        return memberBeans;
    }

    public float getGiftcardaccount() {
        return mGiftcardaccount;
    }
}
