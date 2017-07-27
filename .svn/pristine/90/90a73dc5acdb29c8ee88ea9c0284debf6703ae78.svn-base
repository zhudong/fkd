package com.fuexpress.kr.utils;

import com.fuexpress.kr.bean.RegionBean;

/**
 * Created by Longer on 2017/3/1.
 */
public class RegionBeanFactory {

    private static RegionBeanFactory sRegionBeanFactory;

    private RegionBeanFactory() {

    }

    public static RegionBeanFactory getInstance() {
        if (sRegionBeanFactory == null)
            sRegionBeanFactory = new RegionBeanFactory();
        return sRegionBeanFactory;
    }


    /**
     * 生产国家RegionBean的方法
     *
     * @param rCode 地区码
     * @param rName 地区名
     * @return 国家类型的RBean
     */
    public RegionBean createCountryBean(String rCode, String rName) {
        RegionBean regionBean = new RegionBean();
        regionBean.setIsCountry(true);
        regionBean.setRegionCode(rCode);
        regionBean.setRegionName(rName);
        return regionBean;
    }

    /**
     * 创建省份RegionBean的方法
     *
     * @param cCode        国家码
     * @param rCode        地区码
     * @param rName        地区名
     * @param regionNumber 地区的ID
     * @return 省份类型的Bean
     */
    public RegionBean createProvinceBean(String cCode, String rCode, String rName, String regionNumber) {
        RegionBean regionBean = new RegionBean();
        regionBean.setIsProvince(true);
        regionBean.setCountryCode(cCode);
        regionBean.setRegionCode(rCode);
        regionBean.setRegionName(rName);
        regionBean.setRegionNumber(regionNumber);

        return regionBean;
    }

    /**
     * 创建城市RegionBean的方法
     *
     * @param pCode        省份code
     * @param rCode        地区码
     * @param rName        地区名
     * @param regionNumber 地区的ID
     * @return 省份Bean
     */
    public RegionBean createCityBean(String pCode, String rCode, String rName, String regionNumber) {
        RegionBean regionBean = new RegionBean();
        regionBean.setIsCity(true);
        regionBean.setProvinceCode(pCode);
        regionBean.setRegionCode(rCode);
        regionBean.setRegionName(rName);
        regionBean.setRegionNumber(regionNumber);
        return regionBean;
    }
}
