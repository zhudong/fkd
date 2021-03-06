package com.fuexpress.kr.ui.activity.help_signed.data;

import android.support.annotation.NonNull;

import com.fuexpress.kr.bean.HelpSignedBean;

import java.util.List;

import fksproto.CsBase;
import fksproto.CsParcel;
import rx.Observable;

/**
 * Created by Longer on 2016/12/5.
 * 帮我收货的数据源接口(用来定义需要实现这个接口的数据源类要必须实现的方法)
 * 定义了无论是远程获取还是本地获取都需要实现的一系列接口
 */
public interface HelpSignedDataSource {

    List<CsParcel.ProductDataList> getHelpSignedDataList();//提供给外部用来获取全部Bean列表的接口(local类实现,remote类不需要实现)

    CsParcel.ProductDataList getHelpSignedDataOnly(int index);//提供给外部用来获取特定Bean的接口

    void saveHelpSignedData(@NonNull CsParcel.ProductDataList helpSignedData);//提供给外部用来保存Bean的接口

    boolean deleteHelpSignedData(int index);//提供给外部删除Bean的接口

    void deletAllHelpSignedData();//删除全部数据

    void refreshHelpSignedData();//刷新数据

    void editHelpSignedData(int index, @NonNull CsParcel.ProductDataList helpSignedData);//提供给外界改变数据的接口

    //List<CsBase.Warehouse> getWareHouseDataList();//获取仓库数据列表的方法(在这里因为pb类型的不统一定义多一个接口),local层不需要实现,网络层实现即可,至于编辑删除等等的都是通用的

}
