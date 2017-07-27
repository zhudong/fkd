package com.fuexpress.kr.ui.activity.transport_address;

import android.text.TextUtils;

import com.fuexpress.kr.bean.CountryBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.help_signed.data.WareHouseBean;
import com.fuexpress.kr.utils.LogUtils;
import com.google.protobuf.GeneratedMessage;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsUser;

/**
 * Created by Longer on 2017/5/22.
 * 转运地址的数据请求管理类
 */
public class TranSportAdManager {

    private static TranSportAdManager sTranSportAdManager;
    private ArrayList<WareHouseBean> mWareHouseBeanArrayList;
    private List<CountryBean> mCountryBeanList;
    private String mCountryCode = "";


    public static TranSportAdManager getInstance() {
        if (sTranSportAdManager == null)
            sTranSportAdManager = new TranSportAdManager();
        return sTranSportAdManager;
    }


    public void getTransportAddressDataRemote(int pageNum, final String counCode, final TraspoAdRemoteListener listener) {
        //testMethod(listener);

        CsUser.GetCountryWarehouseListRequest.Builder builder = CsUser.GetCountryWarehouseListRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        //builder.setCountrycode("");
        if (!TextUtils.isEmpty(counCode))
            builder.setCountrycode(counCode);
        builder.setType(2);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setPagenum(pageNum);
        builder.setPageSize(5);

        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetCountryWarehouseListResponse>() {

            @Override
            public void onSuccess(CsUser.GetCountryWarehouseListResponse response) {
                //LogUtils.e(response.toString());
                //if (mCountryBeanList == null && TextUtils.isEmpty(counCode))
                mCountryBeanList = new ArrayList<CountryBean>();
                List<CsUser.CountryList> countryListList = response.getCountryListList();
                for (CsUser.CountryList countryList : countryListList) {
                    CountryBean countryBean = new CountryBean();
                    countryBean.setCountryCode(countryList.getCountryCode());
                    countryBean.setCountryName(countryList.getCountryName());
                    mCountryBeanList.add(countryBean);
                }
                List<CsUser.WareHouseList> warehouseListList = response.getWarehouseListList();
                List<WareHouseBean> wareHouseBeans = new ArrayList<WareHouseBean>();
                for (CsUser.WareHouseList wareHouseList : warehouseListList) {
                    WareHouseBean wareHouseBean = new WareHouseBean();
                    wareHouseBean.setName(wareHouseList.getWarehousename());
                    wareHouseBean.setAddress(wareHouseList.getFulladdress());
                    wareHouseBean.setReceiver(wareHouseList.getReceiver());
                    wareHouseBean.setPhone(wareHouseList.getPhone());
                    wareHouseBean.setPostCode(wareHouseList.getPostcode());
                    wareHouseBeans.add(wareHouseBean);
                }
                mCountryCode = response.getCountryCode();
                listener.success(wareHouseBeans, response.getMore().equals("yes"));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("请求失败:" + ret + errMsg);
                listener.fail("请求失败,服务器:" + ret + errMsg);
            }
        });
    }

    void testMethod(TraspoAdRemoteListener listener) {
        mWareHouseBeanArrayList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            //WareHouseBean wareHouseBean = new WareHouseBean("福快递仓库1","韩国首尔市钟路区钟路266号","Fu-Experess","");
            WareHouseBean wareHouseBean = new WareHouseBean();
            wareHouseBean.setName("福快递仓库1");
            wareHouseBean.setAddress("韩国首尔市钟路区钟路266号");
            wareHouseBean.setReceiver("Fu-Experess");
            wareHouseBean.setPhone("123456789");
            wareHouseBean.setPostCode("123456789");
            mWareHouseBeanArrayList.add(wareHouseBean);
        }
        listener.success(mWareHouseBeanArrayList, false);
    }

    public ArrayList<WareHouseBean> getWareHouseBeanArrayList() {
        if (mWareHouseBeanArrayList == null)
            mWareHouseBeanArrayList = new ArrayList<>();
        return mWareHouseBeanArrayList;
    }

    public List<CountryBean> getCountryBeanList() {
        if (mCountryBeanList == null)
            mCountryBeanList = new ArrayList<>();
        return mCountryBeanList;
    }

    public String getCountryCode() {
        return mCountryCode;
    }
}
