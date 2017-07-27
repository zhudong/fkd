package com.fuexpress.kr.ui.activity.choose_address;

import android.content.Intent;
import android.os.Bundle;

import com.fuexpress.kr.model.AccountManager;

/**
 * Created by Longer on 2017/4/27.
 */
public class AddressBundleBeanFactory {
    private static AddressBundleBeanFactory sAddressBundleBeanFactory;

    private AddressBundleBeanFactory() {

    }


    public static AddressBundleBeanFactory getInstance() {
        if (sAddressBundleBeanFactory == null)
            sAddressBundleBeanFactory = new AddressBundleBeanFactory();
        return sAddressBundleBeanFactory;
    }

    /**
     * 生成国家数据类型封装Bean的方法
     *
     * @param currentCountryCode 当前国家的code
     * @param title              标题
     * @param backTitle          返回标题
     * @return 返回设置好的Intent
     */
    public Intent getCountryTypeIntentBean(Intent intent, String countryString, String currentCountryCode, String title, String backTitle) {
        AddressBundleBean addressBundleBean = new AddressBundleBean();
        addressBundleBean.setIsCountryType(true);
        addressBundleBean.setTitle(title);
        addressBundleBean.setBackTitle(backTitle);
        addressBundleBean.setSelectedString(countryString);
        //addressBundleBean.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        // TODO: 2017/4/21 暂时写死中文语种
        addressBundleBean.setLocaleCode("zh_CN");
        addressBundleBean.setCouSntryCode(currentCountryCode);
        Bundle bundle = new Bundle();
        bundle.putSerializable("address", addressBundleBean);
        intent.putExtra("address", bundle);
        return intent;
    }

    public Intent getCountryTypeIntentBeanNew(Intent intent, String countryString, String currentCountryCode, String title, String backTitle) {
        AddressBundleBean addressBundleBean = new AddressBundleBean();
        addressBundleBean.setIsCountryType(true);
        addressBundleBean.setTitle(title);
        addressBundleBean.setBackTitle(backTitle);
        addressBundleBean.setSelectedString(countryString);
        //addressBundleBean.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        // TODO: 2017/4/21 暂时写死中文语种
        addressBundleBean.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        addressBundleBean.setCouSntryCode(currentCountryCode);
        Bundle bundle = new Bundle();
        bundle.putSerializable("address", addressBundleBean);
        intent.putExtras(bundle);
        return intent;
    }


    public Intent getRegionTypeIntent(Intent intent, String regionString, String currentCountryCode,
                                      String regionID, String title, String backTitle) {
        AddressBundleBean addressBundleBean = new AddressBundleBean();
        addressBundleBean.setIsCountryType(false);
        addressBundleBean.setTitle(title);
        addressBundleBean.setBackTitle(backTitle);
        addressBundleBean.setSelectedString(regionString);
        addressBundleBean.setRegionId(regionID);
        //addressBundleBean.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        // TODO: 2017/4/21 暂时写死中文语种
        addressBundleBean.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        addressBundleBean.setCouSntryCode(currentCountryCode);
        Bundle bundle = new Bundle();
        bundle.putSerializable("address", addressBundleBean);
        intent.putExtra("address", bundle);
        return intent;
    }

    /**
     * 根据上一级Bean来进行子级Bean的请求需要返回的数据封装Bean
     *
     * @param addressBundleBean 封装好的父级Bean
     * @return 返回一个封装好的Bean
     */
//    public AddressBundleBean getRegionAddressBeanByBean(AddressBundleBean addressBundleBean) {
//        AddressBundleBean addressBundleBean1 = new AddressBundleBean();
//        addressBundleBean1.setIsCountryType(false);
//        addressBundleBean1.setParentId(addressBundleBean.getRegionId());
//        addressBundleBean1.setPage(1);
//        addressBundleBean1.setNum(10);
//        addressBundleBean1.setRegionId(UserManager.getInstance().mRegionID);
//    }
    public Intent getAddressBeanByBeanDefault(Intent intent, AddressBundleBean addressBundleBean, String currentCountryCode,
                                              String regionID, String title, String backTitle) {
        addressBundleBean.setIsCountryType(false);
        addressBundleBean.setTitle(title);
        addressBundleBean.setBackTitle(backTitle);
        addressBundleBean.setRegionId(regionID);
        //addressBundleBean.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        addressBundleBean.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        addressBundleBean.setSelectedString(addressBundleBean.getDefaultprovinceString());
        addressBundleBean.setCouSntryCode(currentCountryCode);
        Bundle bundle = new Bundle();
        bundle.putSerializable("address", addressBundleBean);
        intent.putExtra("address", bundle);
        return intent;
    }

    public Intent getAddressBeanByBeanDefaultNew(Intent intent, AddressBundleBean addressBundleBean, String currentCountryCode,
                                                 String regionID, String title, String backTitle, String selectString) {
        if (addressBundleBean == null)
            addressBundleBean = new AddressBundleBean();
        addressBundleBean.setIsCountryType(false);
        addressBundleBean.setTitle(title);
        addressBundleBean.setBackTitle(backTitle);
        addressBundleBean.setRegionId(regionID);
        //addressBundleBean.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        addressBundleBean.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        //addressBundleBean.setSelectedString(addressBundleBean.getDefaultprovinceString());
        addressBundleBean.setSelectedString(selectString);
        addressBundleBean.setCouSntryCode(currentCountryCode);
        Bundle bundle = new Bundle();
        bundle.putSerializable("address", addressBundleBean);
        intent.putExtras(bundle);
        return intent;
    }

    public AddressBundleBean getAddressBundleSortBean(int pageNum) {
        AddressBundleBean addressBundleBean = new AddressBundleBean();
        addressBundleBean.setSortBy(1);
        addressBundleBean.setPage(pageNum);
        addressBundleBean.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        addressBundleBean.setNum(10);
        return addressBundleBean;
    }
}
