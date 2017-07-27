package com.fuexpress.kr.ui.activity.help_signed.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.CategoryBean;
import com.fuexpress.kr.bean.MaterialsBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.LogUtils;

import java.util.List;

import fksproto.CsBase;
import fksproto.CsParcel;
import fksproto.CsUser;

/**
 * Created by Longer on 2016/12/9.
 * 帮我收的远程数据源(从网络上获取)
 * 在这里的需求中,主要还是获取仓库列表的
 * 因为pb的数据源定义位置不一样,所以这里我们就只能实现网络接口来获取对应的数据
 * 本地源接口无视即可
 * 因为不涉及到增删改仓库数据,这里我们就不需要实现其它增删改数据的接口
 */
public class HelpSignedRemoteDataSource implements HelpSignedDataSource {

    @Nullable
    private List<CsBase.Warehouse> mWarehouseList;

    @Nullable
    private HelpMeSingleBean mHelpMeSingleBean;


    private static HelpSignedRemoteDataSource INSTANCE;


    private HelpSignedRemoteDataSource() {

    }


    public static HelpSignedRemoteDataSource getInstance() {
        if (INSTANCE == null)
            INSTANCE = new HelpSignedRemoteDataSource();
        return INSTANCE;
    }


    @Override
    public List<CsParcel.ProductDataList> getHelpSignedDataList() {
        return null;
    }

    @Override
    public CsParcel.ProductDataList getHelpSignedDataOnly(int index) {
        return null;
    }

    @Override
    public void saveHelpSignedData(@NonNull CsParcel.ProductDataList helpSignedData) {
    }

    @Override
    public boolean deleteHelpSignedData(int index) {
        return false;
    }

    @Override
    public void deletAllHelpSignedData() {

    }

    @Override
    public void refreshHelpSignedData() {

    }

    @Override
    public void editHelpSignedData(int index, @NonNull CsParcel.ProductDataList helpSignedData) {

    }

    public HelpMeSingleBean getHelpMeSingleBean() {
        return mHelpMeSingleBean;
    }


    public void getHelpSignedDataRemote(int ID) {
        CsUser.helpMyReceiveInfoRequest.Builder builder = CsUser.helpMyReceiveInfoRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setSalesrequireid(ID);

        NetEngine.postRequest(builder, new INetEngineListener<CsUser.helpMyReceiveInfoResponse>() {
            @Override
            public void onSuccess(CsUser.helpMyReceiveInfoResponse response) {
                LogUtils.e("需求单详情请求回来了:" + response.toString());

            }

            @Override
            public void onFailure(int ret, String errMsg) {
                LogUtils.e("需求单详情请求失败");
            }
        });
    }

    /**
     * 处理一下返回的数据
     *
     * @param response 处理返回的数据
     */
    public void processResponseData(CsUser.helpMyReceiveInfoResponse response) {
        mHelpMeSingleBean = new HelpMeSingleBean();
        CategoryBean categoryBean = new CategoryBean();
        categoryBean.setSubID(Integer.valueOf(response.getMatchCategoryId()));
        categoryBean.setSubName(response.getMatchCategoryName());
        categoryBean.setParentID(Integer.valueOf(response.getParentMatchCategoryId()));
        mHelpMeSingleBean.setCategoryBean(categoryBean);
        MaterialsBean materialsBean = new MaterialsBean();
        materialsBean.setId(Integer.valueOf(response.getMatchTagId()));
        materialsBean.setName(response.getMatchTagName());
        mHelpMeSingleBean.setMaterialsBean(materialsBean);
        mHelpMeSingleBean.setDesc(response.getDescription());
        mHelpMeSingleBean.setRemark(response.getRemark());
        mHelpMeSingleBean.setPrice(response.getPrice());
        mHelpMeSingleBean.setItemNum(String.valueOf(response.getQty()));
        mHelpMeSingleBean.setCurrency_sign(UIUtils.getCurrency(SysApplication.getContext()
                , response.getPricecurrencycode()));
        WareHouseBean wareHouseBean = new WareHouseBean();
        wareHouseBean.setReceiver(response.getReceiver());
        wareHouseBean.setPhone(response.getPhone());
        wareHouseBean.setID(String.valueOf(response.getWarehouseid()));
        wareHouseBean.setName(response.getWarehousename());
        wareHouseBean.setAddress(response.getFulladdress());
        wareHouseBean.setPostCode(response.getPostcode());
        mHelpMeSingleBean.setWareHouseBean(wareHouseBean);
        mHelpMeSingleBean.setWareHouseID(String.valueOf(response.getWarehouseid()));
        ArrayMap<String, String> allImageUpLoadUrlArrayMap =
                HelpSingedDataRepository.getInstance(HelpSignedLocalDataSource.getInstance())
                        .getAllImageUpLoadUrlArrayMap();
        ArrayMap<String, String> pathUrlMap = mHelpMeSingleBean.getPathUrlMap();
        List<String> showImgList = mHelpMeSingleBean.getShowImgList();
        mHelpMeSingleBean.setCurrency_Code(response.getPricecurrencycode());
        List<CsUser.ImagesUrl> imagesurlList = response.getImagesurlList();
        for (CsUser.ImagesUrl imagesUrl : imagesurlList) {
            String image = imagesUrl.getImage();
            allImageUpLoadUrlArrayMap.put(image, image);
            pathUrlMap.put(image, image);
            showImgList.add(image);
        }
        mHelpMeSingleBean.setSalesrequireid(response.getSalesrequireid());
    }
}
