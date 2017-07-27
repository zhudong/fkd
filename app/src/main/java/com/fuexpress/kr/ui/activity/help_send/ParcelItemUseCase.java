package com.fuexpress.kr.ui.activity.help_send;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.ItemAppendBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import java.util.List;

import fksproto.CsBase;
import fksproto.CsParcel;

/**
 * Created by Administrator on 2016-12-18.
 */

public class ParcelItemUseCase {

  /*  required BaseRequest      head           = 1;
    optional BaseUserRequest  userinfo       = 2;   //用户信息
    optional int32            parcelid       = 3;   //
    optional string           name           = 4;
    optional float            price          = 5;
    optional int32            qty            = 6;
    optional string           images         = 7;*/

    public interface IOperareResult {
        void onResult(boolean success, int itemID, String msg);
    }

    public ParcelItemUseCase() {
    }

    /*public ParcelItemUseCase(ItemAppendBean itemBean) {
        this.mItemBean = itemBean;
    }

    public ItemAppendBean getmItemBean() {
        return mItemBean;
    }*/

    public void appendItem(ItemAppendBean itemBean, final IOperareResult listener) {
        List<String> lis = itemBean.getImgs();
        String imgs = "";
        for (int i = 0; i < lis.size(); i++) {
            String img = lis.get(i);
            if (i == lis.size() - 1) {
                imgs += img;
                break;
            }
            imgs += img + ",";
        }
        CsParcel.AddItemRequest.Builder builder = CsParcel.AddItemRequest.newBuilder()
                .setUserinfo(AccountManager.getInstance().getBaseUserRequest())
                .setParcelid(itemBean.getmParcelId())
                .setPrice(itemBean.getPrice())
                .setImages(imgs)
                .setName(itemBean.getInfo())
                .setQty(itemBean.getNum())
                .setBrandname(itemBean.getBrandName() == null ? "" : itemBean.getBrandName())
                .setMatchcategoryid(itemBean.getCategory().getSubID());
        if (itemBean.getMaterails() != null) {
            builder.setMatchtagid(itemBean.getMaterails().getId());
        }


        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.AddItemResponse>() {

            @Override
            public void onSuccess(CsParcel.AddItemResponse response) {
                listener.onResult(true, response.getParcelitemid(), SysApplication.getContext().getString(R.string.item_append_success));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                listener.onResult(false, 0, errMsg);
            }
        });
    }



   /* required BaseRequest      head           = 1;
    optional BaseUserRequest  userhead       = 2;
    optional int32            parcelid       = 3;//包裹id
    optional int32            parcelitemid   = 4;//物品id
    optional int32            image_num      = 5;//图片数量
    repeated ItemImage        extra          = 6;//多图路径串
    optional string           name           = 7;//物品信息
    optional string           price          = 8;//申报单价
    optional int32            qty            = 9;//物品数量

    optional string           brandname     = 10;//品牌名称
    optional int32            matchtagid    = 11;//材质ID
    optional int32            matchcategoryid   = 12;//分类ID*/

    public void editItem(ItemAppendBean itemBean, final IOperareResult listener) {
        List<String> lis = itemBean.getImgs();
        CsParcel.EditItemRequest.Builder builder = CsParcel.EditItemRequest.newBuilder().setUserhead(AccountManager.getInstance().getBaseUserRequest())
                .setParcelid(itemBean.getmParcelId())
                .setParcelitemid(itemBean.getmItemID())
                .setPrice(itemBean.getPrice() + "")
                .setImageNum(lis.size())
                .setName(itemBean.getInfo())
                .setQty(itemBean.getNum())
                .setBrandname(itemBean.getBrandName() == null ? "" : itemBean.getBrandName())
                .setMatchcategoryid(itemBean.getCategory().getSubID());
        for (String img : lis) {
            builder.addExtra(CsBase.ItemImage.newBuilder().setImageUrl(img));
        }
        if (itemBean.getMaterails() != null) {
            builder.setMatchtagid(itemBean.getMaterails().getId());
        }


        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.EditItemResponse>() {
            @Override
            public void onSuccess(CsParcel.EditItemResponse response) {
                listener.onResult(true, response.getParcelitemid(), SysApplication.getContext().getString(R.string.item_edit_sucess));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                listener.onResult(false, -1, errMsg + ret);
            }
        });
    }

    public void deleteItem(int itemID, final IOperareResult listener) {
        CsParcel.DelItemRequest.Builder builder = CsParcel.DelItemRequest.newBuilder().setUserinfo(AccountManager.getInstance().getBaseUserRequest()).setParcelitemid(itemID);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.DelItemResponse>() {

            @Override
            public void onSuccess(CsParcel.DelItemResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResult(true, -1, "success");
                    }
                });
            }

            @Override
            public void onFailure(final int ret, final String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResult(false, -1, errMsg + ret);
                    }
                });
            }
        });
    }
}