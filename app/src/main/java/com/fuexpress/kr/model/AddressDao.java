package com.fuexpress.kr.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.RegionBean;
import com.fuexpress.kr.conf.AddressDBHelper;
import com.fuexpress.kr.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Longer on 2017/2/28.
 * 地址数据库的Dao操作类
 * 增删改查等
 */
public class AddressDao {

    static private AddressDBHelper sAddressDBHelper;
    private static AddressDao sAddressDao;

    private AddressDao() {

    }

    public static AddressDao getInstance(Context context) {
        if (sAddressDao == null) {
            sAddressDao = new AddressDao();
            sAddressDBHelper = new AddressDBHelper(context, null, null, 0);
        }
        return sAddressDao;
    }


    /**
     * 插入国家表的方法
     *
     * @param countryCode 国家码
     * @param countryName 国家名
     */
    public void insertCountry(String countryCode, String countryName) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase writableDatabase = sAddressDBHelper.getWritableDatabase();
        contentValues.put(AddressDBHelper.regionTable.REGION_NAME, countryName);
        contentValues.put(AddressDBHelper.regionTable.REGION_CODE, countryCode);
        contentValues.put(AddressDBHelper.regionTable.IS_COUNTRY, 1);
        writableDatabase.insert(AddressDBHelper.regionTable.TABLE_NAME, null, contentValues);
    }

    /**
     * 插入省份的方法
     *
     * @param countryCode  国家码
     * @param provinceCode 省码
     * @param provinceName 省名
     * @param regionID     地区的ID
     */
    public void insertProvince(String countryCode, String provinceCode, String provinceName, String regionID) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase writableDatabase = sAddressDBHelper.getWritableDatabase();
        contentValues.put(AddressDBHelper.regionTable.REGION_CODE, provinceCode);
        contentValues.put(AddressDBHelper.regionTable.REGION_NAME, provinceName);
        contentValues.put(AddressDBHelper.regionTable.IS_PROVINCE, 1);
        contentValues.put(AddressDBHelper.regionTable.COUNTRY_CODE, countryCode);
        contentValues.put(AddressDBHelper.regionTable.REGION_ID, regionID);
        writableDatabase.insert(AddressDBHelper.regionTable.TABLE_NAME, null, contentValues);
    }

    /**
     * 插入城市的方法
     *
     * @param countryCode  国家码
     * @param provinceCode 省码
     * @param cityCode     城市码
     * @param cityName     城市名
     * @param regionID     地区的ID
     */
    public void insertCity(String countryCode, String provinceCode, String cityCode, String cityName, String regionID) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase writableDatabase = sAddressDBHelper.getWritableDatabase();
        contentValues.put(AddressDBHelper.regionTable.IS_CITY, 1);
        contentValues.put(AddressDBHelper.regionTable.PROVINCE_CODE, provinceCode);
        contentValues.put(AddressDBHelper.regionTable.REGION_CODE, cityCode);
        contentValues.put(AddressDBHelper.regionTable.REGION_NAME, cityName);
        contentValues.put(AddressDBHelper.regionTable.REGION_ID, regionID);
        writableDatabase.insert(AddressDBHelper.regionTable.TABLE_NAME, null, contentValues);
    }

    /**
     * 得出数据中所有的国家信息
     *
     * @return 国家的信息列表
     */
    public List<RegionBean> getAllCountryData() {
        SQLiteDatabase readableDatabase = sAddressDBHelper.getReadableDatabase();

        String[] columns = new String[]{
                AddressDBHelper.regionTable.REGION_CODE,
                AddressDBHelper.regionTable.REGION_NAME
        };

        Cursor query = readableDatabase.query(true, AddressDBHelper.regionTable.TABLE_NAME, columns, AddressDBHelper.regionTable.IS_COUNTRY + "=1", null, null, null, null, null);

        List<RegionBean> countryBeanList = new ArrayList<>();
        countryBeanList.add(new RegionBean(SysApplication.getContext().getResources().getString(R.string.user_info_drtail_please_choose)));
        while (query.moveToNext()) {
            //LogUtils.e("国家code:" + query.getString(0) + " " + "国家Name:" + query.getString(1));
            RegionBean countryBean = new RegionBean();
            countryBean.setRegionCode(query.getString(0));
            countryBean.setRegionName(query.getString(1));
            countryBean.setIsCountry(true);
            countryBeanList.add(countryBean);
        }
        //closeMethod(query, readableDatabase);
        query.close();
        return countryBeanList;
    }

    /**
     * 获得特定国家的所有省份
     *
     * @param countryCode 国家码
     * @return 省份列表
     */
    public List<RegionBean> getProvincesData(String countryCode) {
        SQLiteDatabase readableDatabase = sAddressDBHelper.getReadableDatabase();
        String[] columns = new String[]{
                AddressDBHelper.regionTable.COUNTRY_CODE,
                AddressDBHelper.regionTable.REGION_CODE,
                AddressDBHelper.regionTable.REGION_NAME,
        };

        Cursor query = readableDatabase.query(true, AddressDBHelper.regionTable.TABLE_NAME, columns,
                AddressDBHelper.regionTable.COUNTRY_CODE + "=? and " + AddressDBHelper.regionTable.IS_PROVINCE + "=1", new String[]{countryCode},
                null, null, null, null);
        List<RegionBean> provinceBeanList = new ArrayList<>();
        provinceBeanList.add(new RegionBean(SysApplication.getContext().getResources().getString(R.string.user_info_drtail_please_choose)));
        while (query.moveToNext()) {
            RegionBean provinceBean = new RegionBean();
            provinceBean.setCountryCode(query.getString(0));
            provinceBean.setRegionCode(query.getString(1));
            provinceBean.setRegionName(query.getString(2));
            provinceBean.setIsProvince(true);
            provinceBeanList.add(provinceBean);

            //LogUtils.e("这是省的Code:" + query.getString(0) + "省Name:" + query.getString(1));
        }
        //closeMethod(query, readableDatabase);
        query.close();
        return provinceBeanList;
    }

    /**
     * 获得特定省份的城市列表
     *
     * @param provinceCode 省份Code
     * @return 特定省份下的城市列表
     */
    public List<RegionBean> getCitysData(String provinceCode) {
        SQLiteDatabase readableDatabase = sAddressDBHelper.getReadableDatabase();
        String[] columns = new String[]{
                AddressDBHelper.regionTable.PROVINCE_CODE,
                AddressDBHelper.regionTable.REGION_CODE,
                AddressDBHelper.regionTable.REGION_NAME
        };
        Cursor query = readableDatabase.query(true, AddressDBHelper.regionTable.TABLE_NAME, columns,
                AddressDBHelper.regionTable.PROVINCE_CODE + "=? and " + AddressDBHelper.regionTable.IS_CITY + "=1", new String[]{provinceCode},
                null, null, null, null);
        List<RegionBean> cityBeanList = new ArrayList<>();
        cityBeanList.add(new RegionBean(SysApplication.getContext().getResources().getString(R.string.user_info_drtail_please_choose)));
        while (query.moveToNext()) {
//            LogUtils.e("pCode:" + query.getString(0) + "cityCode:"
//                    + query.getString(1) + "cityName:" + query.getString(2));
            RegionBean cityBean = new RegionBean();
            cityBean.setProvinceCode(query.getString(0));
            cityBean.setRegionCode(query.getString(1));
            cityBean.setRegionName(query.getString(2));
            cityBean.setIsCity(true);
            cityBeanList.add(cityBean);
        }
        //closeMethod(query, readableDatabase);
        query.close();
        return cityBeanList;
    }

    /**
     * 根据传入来的地区名字来寻找地区码
     *
     * @param locationName 地区名
     * @return 地区码
     */
    public String getRegionCode(String locationName) {
        SQLiteDatabase readableDatabase = sAddressDBHelper.getReadableDatabase();

        Cursor query = readableDatabase.query(AddressDBHelper.regionTable.TABLE_NAME, new String[]{AddressDBHelper.regionTable.REGION_CODE},
                AddressDBHelper.regionTable.REGION_NAME + "=?", new String[]{locationName}, null, null, null);
        String regionCode = "";
        while (query.moveToNext())
            regionCode = query.getString(0);
        LogUtils.e("这是查询出来的:" + regionCode);
        closeMethod(query, readableDatabase);
        return regionCode;
    }

    /**
     * 根据ID去查找回来一个Bean
     *
     * @param regionID 需要查找的ID
     * @return 已经格式化好的Bean
     */
   /* public String getRegionBeanByID(String regionID) {
        SQLiteDatabase readableDatabase = sAddressDBHelper.getReadableDatabase();

        String[] columns = new String[]{
                AddressDBHelper.regionTable.COUNTRY_CODE,
                AddressDBHelper.regionTable.PROVINCE_CODE,
                AddressDBHelper.regionTable.REGION_NAME
        };

        Cursor query = readableDatabase.query(AddressDBHelper.regionTable.TABLE_NAME, columns,
                AddressDBHelper.regionTable.REGION_ID + "=?", new String[]{regionID}, null, null, null);
        RegionBean regionBean = new RegionBean();
    }*/

    /**
     * 根据地区Code来获取相应的地址名
     *
     * @param regionCode 地址Code
     * @return 该code的地址名
     */
    public RegionBean getRegionNameByCode(String regionCode, boolean isIDType) {
        if (TextUtils.isEmpty(regionCode)) {
            return null;
        }
        SQLiteDatabase readableDatabase = sAddressDBHelper.getReadableDatabase();

        String[] columns = new String[]{
                AddressDBHelper.regionTable.COUNTRY_CODE,
                AddressDBHelper.regionTable.PROVINCE_CODE,
                AddressDBHelper.regionTable.REGION_NAME
        };
        Cursor query;
        if (!isIDType)
            query = readableDatabase.query(AddressDBHelper.regionTable.TABLE_NAME, columns,
                    AddressDBHelper.regionTable.REGION_CODE + "=?", new String[]{regionCode}, null, null, null);
        else
            query = readableDatabase.query(AddressDBHelper.regionTable.TABLE_NAME, columns,
                    AddressDBHelper.regionTable.REGION_ID + "=?", new String[]{regionCode}, null, null, null);
        RegionBean regionBean = new RegionBean();
        while (query.moveToNext()) {
            if (query.getString(0) != null) {//有国家Code,说明是省份
                regionBean.setIsProvince(true);
                regionBean.setCountryCode(query.getString(0));
            } else if (query.getString(1) != null) {//有省code,说明是城市
                regionBean.setIsCity(true);
                regionBean.setProvinceCode(query.getString(1));
                Cursor query1 = readableDatabase.query(AddressDBHelper.regionTable.TABLE_NAME,
                        new String[]{AddressDBHelper.regionTable.REGION_NAME},
                        AddressDBHelper.regionTable.REGION_CODE + "=?", new String[]{query.getString(1)},
                        null, null, null);
                while (query1.moveToNext())//根据省Code查出其省名
                    regionBean.setProvinceName(query1.getString(0));
                query1.close();
            } else {//说明两个都没有,则是国家
                regionBean.setIsCountry(true);
            }

            regionBean.setRegionCode(regionCode);
            regionBean.setRegionName(query.getString(2));

        }
        LogUtils.e(regionBean.toString());
        //closeMethod(query, readableDatabase);
        return regionBean;
    }


    public void closeMethod(Cursor cursor, SQLiteDatabase sqLiteDatabase) {
        cursor.close();
        sqLiteDatabase.close();
    }

    /**
     * 更新国家信息
     *
     * @param regionBean 地区格式化好的Bean
     */
    public void updateCountry(RegionBean regionBean) {
        SQLiteDatabase writableDatabase = sAddressDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AddressDBHelper.regionTable.REGION_NAME, regionBean.getRegionName());
        contentValues.put(AddressDBHelper.regionTable.REGION_CODE, regionBean.getRegionCode());
        writableDatabase.update(AddressDBHelper.regionTable.TABLE_NAME, contentValues, AddressDBHelper.regionTable.REGION_CODE + "=?", new String[]{regionBean.getRegionCode() + ""});
    }

    /**
     * 更新省份信息
     *
     * @param regionBean 地区格式化好的Bean
     */
    public void updateProvinceBean(RegionBean regionBean) {
        SQLiteDatabase writableDatabase = sAddressDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AddressDBHelper.regionTable.REGION_NAME, regionBean.getRegionName());
        contentValues.put(AddressDBHelper.regionTable.REGION_CODE, regionBean.getRegionCode());
        contentValues.put(AddressDBHelper.regionTable.COUNTRY_CODE, regionBean.getCountryCode());
        writableDatabase.update(AddressDBHelper.regionTable.TABLE_NAME, contentValues, AddressDBHelper.regionTable.REGION_ID + "=?", new String[]{regionBean.getRegionNumber() + ""});
    }

    /**
     * 更新城市信息
     *
     * @param regionBean 地区格式化好的Bean
     */
    public void updateCityBean(RegionBean regionBean) {
        SQLiteDatabase writableDatabase = sAddressDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AddressDBHelper.regionTable.REGION_NAME, regionBean.getRegionName());
        contentValues.put(AddressDBHelper.regionTable.REGION_CODE, regionBean.getRegionCode());
        contentValues.put(AddressDBHelper.regionTable.COUNTRY_CODE, regionBean.getCountryCode());
        contentValues.put(AddressDBHelper.regionTable.PROVINCE_CODE, regionBean.getProvinceCode());
        writableDatabase.update(AddressDBHelper.regionTable.TABLE_NAME, contentValues, AddressDBHelper.regionTable.REGION_ID + "=?", new String[]{regionBean.getRegionNumber() + ""});
    }


}
