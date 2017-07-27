package com.fuexpress.kr.model;

import android.app.Activity;

import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.socks.library.KLog;

import de.greenrobot.event.EventBus;
import fksproto.CsItem;
import fksproto.CsMerchant;

/**
 * Created by Longer on 2017/6/22.
 */
public class OperaRequestManager {

    private static OperaRequestManager ourInstance;

    public static OperaRequestManager getInstance() {
        if (ourInstance == null)
            ourInstance = new OperaRequestManager();
        return ourInstance;
    }

    private OperaRequestManager() {

    }

    /**
     * @param itemId   单品id,必须的参数
     * @param itemBean 单品的bean,用来判断其当前状态和修改状态
     * @param listener 接口回调,喜欢用的可以用,或者evntBus接受处理
     */
    public static void operateLikeItem(final long itemId, final ItemBean itemBean, Activity activity, final OperaRequestListener listener) {

        if (!AccountManager.getInstance().isUserLogin(activity)) {
            return;
        }
        operateLikeItem(itemId, itemBean, listener);
    }

    public static void operateLikeItem(final long itemId, final ItemBean itemBean, final OperaRequestListener listener) {


        final boolean is_like = itemBean.is_like;
        int opera = 1;
        if (is_like) {
            opera = 2;
        }
        CsItem.LikeItemRequest.Builder builder = CsItem.LikeItemRequest.newBuilder();
        //然后设置好值,发送请求:
        builder.setItemId(itemId);
        builder.setOpera(opera);
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());

        NetEngine.postRequest(builder, new INetEngineListener<CsItem.LikeItemResponse>() {
            @Override
            public void onSuccess(CsItem.LikeItemResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        AccountManager.getInstance().getUserCount();
                        itemBean.setIs_like(!is_like);
                        int likeCount = itemBean.getLikeCount();
                        if (is_like) {
                            itemBean.setLike_count(likeCount - 1);
                        } else {
                            itemBean.setLike_count(likeCount + 1);
                        }
                        if (listener != null)
                            listener.onOperaSuccess();
                        EventBus.getDefault().post(new BusEvent(BusEvent.OPERA_ITEM_SUCCESS, itemBean));

                    }
                });

            }

            @Override
            public void onFailure(int ret, String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null)
                            listener.onOperaFailure();
                    }
                });
                EventBus.getDefault().post(new BusEvent(BusEvent.OPERA_ITEM_FAIL, null));
                //LogUtils.e("Opera Item fail" + errMsg);

            }
        });
    }


    /**
     * @param merChantId   商店id
     * @param merChantBean bean用来判断状态和修改状态
     * @param listener     接口回调
     */
    public void operateLikeMerchant(final long merChantId, final MerChantBean merChantBean, Activity activity, final OperaRequestListener listener) {
        if (!AccountManager.getInstance().isUserLogin(activity)) {
            return;
        }
        operateLikeMerchant(merChantId, merChantBean, listener);
    }


    /**
     * @param merChantId   商店id
     * @param merChantBean bean用来判断状态和修改状态
     * @param listener     接口回调
     */
    public void operateLikeMerchant(final long merChantId, final MerChantBean merChantBean, final OperaRequestListener listener) {
        CsMerchant.FollowMerchantRequest.Builder builder = CsMerchant.FollowMerchantRequest.newBuilder();
        final boolean follow = merChantBean.is_follow();
        int opera = 1;
        if (follow) {
            opera = 2;
        }
        builder.setOpera(opera);
        builder.setMerchantId(merChantId);
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());

        //发送请求:
        NetEngine.postRequest(builder, new INetEngineListener<CsMerchant.FollowMerchantResponse>() {

            @Override
            public void onSuccess(CsMerchant.FollowMerchantResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {

                        int follow_num = merChantBean.getFollow_num();
                        merChantBean.setIs_follow(!follow);
                        if (follow) {
                            merChantBean.setFollow_num(follow_num - 1);
                        } else {
                            merChantBean.setFollow_num(follow_num + 1);
                        }
                        if (listener != null) {

                            listener.onOperaSuccess();
                        }
                        AccountManager.getInstance().getUserCount();
                        KLog.i("like ? " + follow + " id = " + merChantId);

                        EventBus.getDefault().post(new BusEvent(BusEvent.OPERA_MERCHANT_SUCCESS, true, merChantBean));
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("关注失败!" + errMsg);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        listener.onOperaFailure();
                    }
                });

                EventBus.getDefault().post(new BusEvent(BusEvent.REQUEST_FAIL, ret + errMsg));
            }
        });
    }


    /**
     * @param merChantId   商店id
     * @param merChantBean bean用来判断状态和修改状态
     * @param listener     接口回调
     */
    public void operateLikeMerchant(final long merChantId, final MerChantBean merChantBean, final OperaRequestListener listener, final String adapterName) {
        CsMerchant.FollowMerchantRequest.Builder builder = CsMerchant.FollowMerchantRequest.newBuilder();
        final boolean follow = merChantBean.is_follow();
        int opera = 1;
        if (follow) {
            opera = 2;
        }
        builder.setOpera(opera);
        builder.setMerchantId(merChantId);
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());

        //发送请求:
        NetEngine.postRequest(builder, new INetEngineListener<CsMerchant.FollowMerchantResponse>() {

            @Override
            public void onSuccess(CsMerchant.FollowMerchantResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {

                        int follow_num = merChantBean.getFollow_num();
                        merChantBean.setIs_follow(!follow);
                        if (follow) {
                            merChantBean.setFollow_num(follow_num - 1);
                        } else {
                            merChantBean.setFollow_num(follow_num + 1);
                        }
                        if (listener != null) {

                            listener.onOperaSuccess();
                        }
                        AccountManager.getInstance().getUserCount();
                        KLog.i("like ? " + follow + " id = " + merChantId);

                        EventBus.getDefault().post(new BusEvent(BusEvent.OPERA_MERCHANT_SUCCESS, true, merChantBean, adapterName));
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("关注失败!" + errMsg);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        listener.onOperaFailure();
                    }
                });

                EventBus.getDefault().post(new BusEvent(BusEvent.REQUEST_FAIL, ret + errMsg));
            }
        });
    }
}
