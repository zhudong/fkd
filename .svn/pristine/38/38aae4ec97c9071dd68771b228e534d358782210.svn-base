package com.fuexpress.kr.ui.activity.choose_category;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.google.protobuf.GeneratedMessage;

import java.util.List;

import fksproto.CsUser;

/**
 * Created by Longer on 2017/1/18.
 * 分类列表里的数据
 */
public class CategoryDataManager {

    @Nullable
    private static CategoryDataManager sCategoryDataManager = null;

    private List<CsUser.MatchItemCategory> mMatchItemCategoryAllListList;

    private ArrayMap<Integer, List<CsUser.MatchItemCategory>> mIntegerListArrayMap;

    private CategoryDataManager() {

    }

    public static interface CategoryDataListener {
        void onSuccess(List<CsUser.MatchItemCategory> list);

        void onFail(int ret, String errMsg);
    }

    public static CategoryDataManager getInstance() {
        if (sCategoryDataManager == null)
            sCategoryDataManager = new CategoryDataManager();
        return sCategoryDataManager;
    }

    public List<CsUser.MatchItemCategory> getDataList(int parentID) {
        List<CsUser.MatchItemCategory> list;
        if (parentID == -1) {
            list = mMatchItemCategoryAllListList;
        } else {
            if (mIntegerListArrayMap == null)
                mIntegerListArrayMap = new ArrayMap<>();
            list = mIntegerListArrayMap.get(parentID);
        }
        return list;
    }


    public void getCategoryDataFromNet(final int parentID, final CategoryDataListener listener) {
        CsUser.GetCategoryListRequest.Builder builder = CsUser.GetCategoryListRequest.newBuilder();
        if (parentID != -1)
            builder.setParentId(parentID);
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetCategoryListResponse>() {
            @Override
            public void onSuccess(CsUser.GetCategoryListResponse response) {
                List<CsUser.MatchItemCategory> list;
                if (parentID == -1) {
                    mMatchItemCategoryAllListList = response.getMatchItemCategoryListList();
                    list = mMatchItemCategoryAllListList;
                } else {
                    mIntegerListArrayMap.put(parentID, response.getMatchItemCategoryListList());
                    list = mIntegerListArrayMap.get(parentID);
                }
                listener.onSuccess(list);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                listener.onFail(ret, errMsg);
            }
        });
    }


}
