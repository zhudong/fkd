package com.fuexpress.kr.model;

import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.socks.library.KLog;

import de.greenrobot.event.EventBus;
import fksproto.CsUser;

/**
 * Created by k550 on 4/25/2016.
 * 处理获取红点请求的管理类，包含包裹，付款，通知
 */
public class RedPointCountManager {
    public static  long redPointOrderCount=0;
    public static  long redPointNotifyCount=0;
    public static  long redPointParcelCount=0;
    public static void getOrderCount(){
        KLog.a("order");
        CsUser.GetMyRedPointRequest.Builder builder =CsUser.GetMyRedPointRequest.newBuilder();
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.addTypes(CsUser.RedPointType.RED_POINT_TYPE_ORDER_VALUE);
        builder.addTypes(CsUser.RedPointType.RED_POINT_TYPE_NOTIFY_VALUE);
        builder.addTypes(CsUser.RedPointType.RED_POINT_TYPE_PARCEL_VALUE);
        builder.setUin(AccountManager.getInstance().mUin);
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetMyRedPointResponse>() {
            @Override
            public void onSuccess(CsUser.GetMyRedPointResponse response) {
                KLog.a(response.toString());
                redPointOrderCount=response.getPairs(0).getV();
                redPointNotifyCount=response.getPairs(1).getV();
                redPointParcelCount=response.getPairs(2).getV();
                KLog.a("order "+redPointOrderCount+" notify "+redPointNotifyCount+" parcel "+redPointParcelCount);
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_MY_RED_POINT_COUNT_SUCCESS,null));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                KLog.e(errMsg+"ret = "+ret);
            }
        });

    }
    public static void Logout(){
        redPointOrderCount=0;
        redPointParcelCount=0;
        redPointNotifyCount=0;
        EventBus.getDefault().post(new BusEvent(BusEvent.GET_MY_RED_POINT_COUNT_SUCCESS,null));
    }
}
