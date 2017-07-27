package com.fuexpress.kr.bean;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.fuexpress.kr.ui.adapter.HelpAdapterInterface;
import com.fuexpress.kr.ui.view.InputBoxView;

import java.util.List;

import fksproto.CsParcel;

/**
 * Created by Longer on 2016/12/7.
 * 这是帮我系列的Adapter的一个参数封装Bean
 * 在创建帮我系列的Adapter时,需要这个Bean
 */
public class HelpAdapterValueBean {
    private Context mContext;//上下文对象
    private HelpAdapterInterface mHelpAdapterInterface;//Adapter和调用的Activity与Fragment的接口回调
    private String mType;//类型,是哪个帮我系列
    /**
     * 这个是数据列表,在第一次进来时,按照目前的需求是没有,但是这里至少需要一个
     * 可以创建这样的一个集合,然后build一个空的CsParcel.ProductDataList，add进去即可
     */
    private List<CsParcel.ProductDataList> dataLists;

    /**
     * 左边的作为输入框的标记,右边是InputBoxView输入框
     * InputBoxView是封装好的一个输入框自定义控件,详情请看其api
     * 这样对应起来,才能让调用的Activity知道,被点击的是哪个输入框
     * 关于回调,可以看HelpAdapterInterface
     */
    private ArrayMap<Integer, InputBoxView> mTitleAndHintTextArrayMap;


    private int mMaxSize = 0;

    public int getMaxSize() {
        return mMaxSize;
    }

    public void setMaxSize(int maxSize) {
        mMaxSize = maxSize;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public HelpAdapterInterface getHelpAdapterInterface() {
        return mHelpAdapterInterface;
    }

    public void setHelpAdapterInterface(HelpAdapterInterface helpAdapterInterface) {
        mHelpAdapterInterface = helpAdapterInterface;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public List<CsParcel.ProductDataList> getDataLists() {
        return dataLists;
    }

    public void setDataLists(List<CsParcel.ProductDataList> dataLists) {
        this.dataLists = dataLists;
    }

    public ArrayMap<Integer, InputBoxView> getInputBoxArrayMap() {
        return mTitleAndHintTextArrayMap;
    }

    public void setInputBoxArrayMap(ArrayMap<Integer, InputBoxView> titleAndHintTextArrayMap) {
        mTitleAndHintTextArrayMap = titleAndHintTextArrayMap;
    }
}
