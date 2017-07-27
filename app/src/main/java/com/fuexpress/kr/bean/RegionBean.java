package com.fuexpress.kr.bean;

/**
 * Created by Longer on 2017/3/1.
 * 封装了地址信息的Bean
 */
public class RegionBean {
    private String regionCode = "";
    private String regionName = "";
    private String provinceCode = "";
    private String provinceName = "";
    private String countryCode = "";
    private String regionNumber = "";


    private boolean isCity;

    private boolean isCountry;

    private boolean isProvince;

    public RegionBean() {

    }

    public RegionBean(String regionName) {
        this.regionName = regionName;
    }


    public String getRegionNumber() {
        return regionNumber;
    }

    public void setRegionNumber(String regionNumber) {
        this.regionNumber = regionNumber;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isCity() {
        return isCity;
    }

    public void setIsCity(boolean isCity) {
        this.isCity = isCity;
    }

    public boolean isCountry() {
        return isCountry;
    }

    public void setIsCountry(boolean isCountry) {
        this.isCountry = isCountry;
    }

    public boolean isProvince() {
        return isProvince;
    }

    public void setIsProvince(boolean isProvince) {
        this.isProvince = isProvince;
    }

    @Override
    public String toString() {
        return "isCity:" + isCity + " isCountry:" + isCountry + " isProvince:" + isProvince + " countryCode:" +
                countryCode + " province_code:" + provinceCode + " region_code:" + regionCode +
                " region_name:" + regionName + " province_name:" + provinceName;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof RegionBean) {
            RegionBean regionBean = (RegionBean) o;
            //if (isCountry())
            return regionCode.equals(regionBean.getRegionCode());
           /* else
                return regionNumber.equals(regionBean.getRegionNumber());*/
        }
        return super.equals(o);
    }
}