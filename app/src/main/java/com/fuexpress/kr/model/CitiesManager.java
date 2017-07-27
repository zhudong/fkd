package com.fuexpress.kr.model;

import com.fuexpress.kr.bean.CouponCurrencyInfoData;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.RequestNetListenerWithMsg;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.List;

import fksproto.CsAddress;
import fksproto.CsUser;

/**
 * Created by root on 17-3-28.
 */

public class CitiesManager {
    private List<CsAddress.CountryRegionInfo> mDatas;
    private CsAddress.GetWarehouseRegionResponse mResponse;
    private static CitiesManager ourInstance = new CitiesManager();
    public static CitiesManager getInstance() {
        return ourInstance;
    }
    private CitiesManager() {
    }
    public CsAddress.GetWarehouseRegionResponse getResponse(){
        return mResponse;
    }
    public void getCities(final RequestNetListenerWithMsg listenerWithMsg){
        CsAddress.GetWarehouseRegionRequest.Builder builder=CsAddress.GetWarehouseRegionRequest.newBuilder();
        builder.setBaseuser(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsAddress.GetWarehouseRegionResponse>() {
            @Override
            public void onSuccess (final CsAddress.GetWarehouseRegionResponse response) {
                mDatas=response.getCountryRegionListList();
                mResponse=response;
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        if(listenerWithMsg!=null){
                            listenerWithMsg.onSuccess(response);
                        }
                    }
                });
                KLog.i("getCityList",response.toString());

            }

            @Override
            public void onFailure(final int ret, final String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        if(listenerWithMsg!=null){
                            listenerWithMsg.onFailure(ret,errMsg);
                        }
                    }
                });
            }
        });
    }
}
