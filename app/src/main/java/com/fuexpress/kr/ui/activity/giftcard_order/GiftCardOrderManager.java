package com.fuexpress.kr.ui.activity.giftcard_order;

import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsCard;

/**
 * Created by Longer on 2017/5/24.
 */
public class GiftCardOrderManager {

    private static GiftCardOrderManager sGiftCardOrderManager = new GiftCardOrderManager();
    public List<CsCard.GiftCardOrderItem> mGiftcarditemsList01;
    public List<CsCard.GiftCardOrderItem> mGiftcarditemsList01_Tab_01;
    public List<CsCard.GiftCardOrderItem> mGiftcarditemsList02;
    public List<CsCard.GiftCardOrderItem> mGiftcarditemsList02_Tab_01;

    public int mPendingOrderNum_01;
    public int mPendingOrderNum_02;

    private GiftCardOrderManager() {

    }

    public static GiftCardOrderManager getInstance() {
        return sGiftCardOrderManager;
    }

    // 1:一般的充值卡类型;    2:直充值卡类型
    // 0:充值卡订单-全部;  1: 充值卡订单-待付款; 2: 充值卡订单-已支付;  3: 充值卡订单-已取消

    public void getGiftCardOrderListRequest(final int orderType, final int statusType, int pageNum, final int status) {
        CsCard.GetGiftCardOrderListRequest.Builder builder = CsCard.GetGiftCardOrderListRequest.newBuilder();
        builder.setPageno(pageNum);
        builder.setOrderType(orderType);
        builder.setStatusType(statusType);
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setUserHead(AccountManager.getInstance().mBaseUserRequest);
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GetGiftCardOrderListReponse>() {
            @Override
            public void onSuccess(CsCard.GetGiftCardOrderListReponse response) {
                boolean more = response.getMore();
                if (0 == status && 1 == orderType) {
                    mGiftcarditemsList01 = new ArrayList<CsCard.GiftCardOrderItem>();
                    if (1 == statusType) {
                        mGiftcarditemsList01_Tab_01 = new ArrayList<CsCard.GiftCardOrderItem>();
                    }
                } else if (0 == status && 2 == orderType) {
                    mGiftcarditemsList02 = new ArrayList<CsCard.GiftCardOrderItem>();
                    if (1 == statusType) {
                        mGiftcarditemsList02_Tab_01 = new ArrayList<CsCard.GiftCardOrderItem>();
                    }
                }
                if (1 == orderType) {
                    if (1 == statusType) {
                        mGiftcarditemsList01_Tab_01.addAll(response.getGiftcarditemsList());
                    }
                    mGiftcarditemsList01.addAll(response.getGiftcarditemsList());
                } else if (2 == orderType) {
                    if (1 == statusType) {
                        mGiftcarditemsList02_Tab_01.addAll(response.getGiftcarditemsList());
                    }
                    mGiftcarditemsList02.addAll(response.getGiftcarditemsList());
                }
                if (1 == orderType) {
                    mPendingOrderNum_01 = response.getPendingOrderNum();
                } else {
                    mPendingOrderNum_02 = response.getPendingOrderNum();
                }

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
}
