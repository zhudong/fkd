package com.fuexpress.kr.ui.activity.help_signed.data;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.utils.SPUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;
import fksproto.CsParcel;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Longer on 2016/12/8.
 * 给帮我收提供本地数据源的类,实现了帮我收数据源的一系列抽象接口
 * 请注意该类只是提供本地数据,并不参与逻辑(绝大部分是数据库)
 * 在本项目中,实际上使用不到(替代成内存)
 */
public class HelpSignedLocalDataSource implements HelpSignedDataSource {

    private static HelpSignedLocalDataSource sHelpSignedLocalDataSource;

    private List<CsParcel.ProductDataList> mProductDataLists;

    private List<HelpMeSingleBean> mHelpMeSingleBeans;

    @Nullable
    private List<Integer> mResIDList = null;

    private HelpSignedLocalDataSource() {

        //Gson gson = new Gson();

    }

    public static HelpSignedLocalDataSource getInstance() {
        if (sHelpSignedLocalDataSource == null)
            sHelpSignedLocalDataSource = new HelpSignedLocalDataSource();
        return sHelpSignedLocalDataSource;
    }

    public void initLoaclData() {
        String string = SPUtils.getString(SysApplication.getContext(), Constants.USER_INFO.HELP_SIGNED_DATA, "");
        if (!TextUtils.isEmpty(string))
            Observable.just(string).map(new Func1<String, List<HelpMeSingleBean>>() {
                @Override
                public List<HelpMeSingleBean> call(String s) {
                    return new Gson().fromJson(s, new TypeToken<List<HelpMeSingleBean>>() {
                    }.getType());
                }
            }).subscribeOn(Schedulers.io()).subscribe(new Action1<List<HelpMeSingleBean>>() {
                @Override
                public void call(List<HelpMeSingleBean> list) {
                    getHelpMeSingleBeans().addAll(list);
                }
            });
    }

    public List<HelpMeSingleBean> getLoaclDataBeanList(String spKey) {
        String beanJson = SPUtils.getString(SysApplication.getContext(), spKey, "");
        Gson gson = new Gson();
        if (mHelpMeSingleBeans == null)
            mHelpMeSingleBeans = new ArrayList<>();
        mHelpMeSingleBeans = gson.fromJson(beanJson, new TypeToken<List<HelpMeSingleBean>>() {
        }.getType());
        return mHelpMeSingleBeans;
    }


    @Override
    public List<CsParcel.ProductDataList> getHelpSignedDataList() {
        if (mProductDataLists == null) {//当这个集合为空的情况下,代表初次进入,至少需要给予一个数据
            mProductDataLists = new ArrayList<>();
            /*CsParcel.ProductDataList.Builder builder = CsParcel.ProductDataList.newBuilder();
            CsParcel.ProductDataList build = builder.build();
            mProductDataLists.add(build);*/
        }
        return mProductDataLists;
    }

    public List<HelpMeSingleBean> getHelpMeSingleBeans() {
        if (mHelpMeSingleBeans == null)
            mHelpMeSingleBeans = new ArrayList<>();
        return mHelpMeSingleBeans;
    }

    @Override
    public CsParcel.ProductDataList getHelpSignedDataOnly(int index) {
        return mProductDataLists.get(index);
    }

    @Override
    public void saveHelpSignedData(@NonNull CsParcel.ProductDataList helpSignedData) {
        mProductDataLists.add(helpSignedData);
    }

    @Override
    public boolean deleteHelpSignedData(int index) {
        boolean isSuccess = false;
        if (mProductDataLists.size() != 1) {

            mProductDataLists.remove(index);
            isSuccess = true;
        }
        return isSuccess;
    }

    @Override
    public void deletAllHelpSignedData() {
        mProductDataLists = new ArrayList<>();
    }

    @Override
    public void refreshHelpSignedData() {//目前暂时没用

    }

    @Override
    public void editHelpSignedData(int index, @NonNull CsParcel.ProductDataList helpSignedData) {
        mProductDataLists.set(index, helpSignedData);
    }

    /*@Override
    public List<CsBase.Warehouse> getWareHouseDataList() {
        return null;
    }*/

    /**
     * 获得需要显示的左边标题资源集合
     *
     * @return 返回左边标题的文字资源集合
     */
    public List<Integer> getResIDList() {
        if (mResIDList != null) {
            return mResIDList;
        } else {
            mResIDList = new ArrayList<>();
            mResIDList.add(R.string.hm_item_desc);
            mResIDList.add(R.string.hm_item_remark);
            mResIDList.add(R.string.hm_item_ware_house);
            mResIDList.add(R.string.hm_item_request_price);
            mResIDList.add(R.string.hm_item_quantity);
            return mResIDList;
        }
    }

    /**
     * 把没有上传到服务器的数据保存到本地的方法
     *
     * @param list 数据的List
     */
    public void saveDataToLoacl(List<HelpMeSingleBean> list) {
        /*Gson gson = new Gson();
        String beanListJson = gson.toJson(list);
        SPUtils.putString(SysApplication.getContext(), Constants.USER_INFO.HELP_SIGNED_DATA, beanListJson);*/
        //LogUtils.e("转换后的json串:"+beanListJson);
        Observable.just(list).map(new Func1<List<HelpMeSingleBean>, String>() {
            @Override
            public String call(List<HelpMeSingleBean> list) {
                return new Gson().toJson(list);
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                SPUtils.putString(SysApplication.getContext(), Constants.USER_INFO.HELP_SIGNED_DATA, s);
            }
        });
    }
}
