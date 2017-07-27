package com.fuexpress.kr.model;

import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.CouponCurrencyInfoData;
import com.fuexpress.kr.bean.CouponDataBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsAddress;
import fksproto.CsCard;
import fksproto.CsUser;

/**
 * Created by kevin.xie on 2016/10/25.
 */

public class CouponDataManager {
    private HashMap<String,List<CsUser.CouponList>> mCouponList;//优惠券总列表
    private List<CouponCurrencyInfoData> mDatas;
    private static CouponDataManager ourInstance = new CouponDataManager();
    public static CouponDataManager getInstance() {
        return ourInstance;
    }
    private CouponDataManager() {
    }

    public  void  getCouponDataList(final int pageNo){
        KLog.i("coupon "+pageNo);
        if(pageNo==1){
            mDatas=null;
        }
        CsUser.MyShippingCouponListRequest.Builder builder=CsUser.MyShippingCouponListRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setPagenum(pageNo);
        builder.setCurrencycode(Constants.Coupon.DEFAULT_CODE);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.MyShippingCouponListResponse>() {

            @Override
            public void onSuccess(CsUser.MyShippingCouponListResponse response) {
                KLog.i("coupon",response.toString());
                for(CsUser.CurrencyCouponList currencyList:response.getCurrencylistList()){
                    addToDatas(currencyList);
                }
                if(Constants.Coupon.NO_MORE.equals(response.getStatus())){
                    //没有更多
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_COUPON_CURRENCY_LIST_SUCCESS,null));

                }else {
                    getCouponDataList(pageNo+1);
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                KLog.i("coupon" ,errMsg);
            }
        });
    }
    public void addToDatas(CsUser.CurrencyCouponList currencyList){
        if(null==mDatas){
            mDatas=new ArrayList<>();
        }
        mDatas.add(new CouponCurrencyInfoData(currencyList.getCurrencycode(),currencyList.getCurrencyname()
                ,currencyList.getCurrencysign(),currencyList.getCount()));

    }
    public List<CouponCurrencyInfoData> getDatas(){
        return mDatas;

//        if(mDatas==null){
//            return null;
//        }else {
//            List<CouponCurrencyInfoData> list=new ArrayList<>();
//            for(CouponCurrencyInfoData data:mDatas){
//                if(data.currencyCode.equals("CNY")){
//                    list.add(data);
//                    return list;
//                }
//            }
//        }
//        return null;
    }
}