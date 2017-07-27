package com.fuexpress.kr.model;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.MaterialsBean;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsUser;

/**
 * Created by andy on 2017/1/17.
 */
public class MaterialsManager {

    public static MaterialsManager mInstance = new MaterialsManager();
    private final MaterialsDao mDao;

    private MaterialsManager() {
        mDao = MaterialsDao.getInstance(SysApplication.getContext());
    }


    public static MaterialsManager getInstance() {
        return mInstance;
    }


    public void deleteAll() {
        mDao.deleteAll();
    }


    public void getMaterials() {
        getMaterialsNet();
    }


    private void getMaterialsNet() {
        CsUser.GetMaterialsListRequest.Builder builder = CsUser.GetMaterialsListRequest.newBuilder();
        AccountManager instance = AccountManager.getInstance();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest())
                .setLocaleCode(instance.getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetMaterialsListResponse>() {

            @Override
            public void onSuccess(CsUser.GetMaterialsListResponse response) {
                List<MaterialsBean> all = mDao.getAll();
                ArrayMap<Integer, Integer> map = new ArrayMap<Integer, Integer>();
                for (MaterialsBean bean : all) {
                    map.put(bean.getId(), bean.getTime());
                }
                List<CsUser.MatchTag> matchTagListList = response.getMatchTagListList();
                List<MaterialsBean> list = new ArrayList<MaterialsBean>();
                for (CsUser.MatchTag tag : matchTagListList) {
                    int time = map.get(tag.getMatchTagId()) == null ? ((int) System.currentTimeMillis() / 1000) : map.get(tag.getMatchTagId());
                    list.add(new MaterialsBean(tag.getMatchTagId(), tag.getMatchTagName(), time));
                }
                deleteAll();
                mDao.insert(list);
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_MATERIALS_RESULT, true));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_MATERIALS_RESULT, false));
            }
        });
    }

    public void getLocalMaterials() {
        List<MaterialsBean> all = mDao.getAll();

        for (MaterialsBean bean : all) {
            Log.d(MaterialsManager.class.getSimpleName(), bean.getName());
        }
    }

    public List<MaterialsBean> search(String key, boolean toNet) {
        List<MaterialsBean> all = mDao.search(key);
        for (MaterialsBean bean : all) {
            Log.d(MaterialsManager.class.getSimpleName(), bean.getName());
        }
        if ("".equals(key) && all.size() == 0 && toNet) {
            getMaterialsNet();
        }
        return all;
    }

    public void update(MaterialsBean bean) {
        bean.setTime(((int) System.currentTimeMillis() / 1000));
        mDao.update(bean);
    }
}
