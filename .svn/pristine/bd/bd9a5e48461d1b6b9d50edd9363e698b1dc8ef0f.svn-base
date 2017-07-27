package com.fuexpress.kr.utils;

import com.fuexpress.kr.bean.ReParcelItemBean;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsParcel;

/**
 * Created by andy on 2017/2/22.
 */
public class ReParcelItemTransverter {

    public static List<ReParcelItemBean> transform(List<CsParcel.ReparcelItemInfo> itemInfos) {
        ArrayList<ReParcelItemBean> Beans = new ArrayList<>();
        for (CsParcel.ReparcelItemInfo itemInfo : itemInfos) {
            Beans.add(transform(itemInfo));
        }
        return Beans;
    }

    public static ReParcelItemBean transform(CsParcel.ReparcelItemInfo itemInfo) {
        return new ReParcelItemBean(
                itemInfo.getParcelItemId(),
                itemInfo.getPrice(),
                itemInfo.getQtyPack(),
                itemInfo.getImagePath(),
                itemInfo.getCaptionCutPrice(),
                itemInfo.getDeclaredValue()
        );
    }
}
