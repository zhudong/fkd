package com.fuexpress.kr.model;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longer on 2016/10/31.
 */
public class AssetFileManager {

    public static final String OBSERVER_TYPE = "observer_type";
    public static final String FOLLOW_TYPE = "follow_type";
    public static final String ADDRESS_TYPE = "address_type";
    public static final String EDIT_ADDRESS_TYPE = "edit_address_type";
    public static final String COUNTRY_TYPE = "country_type";
    private static final String TAG = "AssetFileManager";
    public static String THE_LAST_COUNTRY = "";
    public static String THE_LAST_PROVINCE = "";
    public static String THE_LAST_CITY = "";
    public List<String> mCountryList;
    public List<String> mProvince;
    public List<String> mCity;
    public ArrayMap<String, List<String>> mProvinceMap;
    public ArrayMap<String, String[]> mProvinceMapFinal;
    public ArrayMap<String, List<String>> mCityMap;
    public ArrayMap<String, String[]> mCityMapFianl;
    public ArrayMap<String, String> mCountryRegionMap;
    public ArrayMap<String, String> mProvinceRegionMap;
    private List<String> mAddressListFormFile;
    public ArrayMap<String, String> mCityRegionMap_String;
    public ArrayMap<String, String> mProvinceRegionMap_String;
    public ArrayMap<String, String> mCountryRegionMap_String;
    public ArrayMap<String, String> mCountryRegionMap_String_trun;
    public String mPleaseChooseString;
    String regionString;


    private Context context;

    private static AssetFileManager mAssetFileManager = new AssetFileManager();
    public String[] mCountryStrings;
    private ArrayMap<String, String> mProvinceRegionMap_String_trun;

    public static AssetFileManager getInstance() {
        return mAssetFileManager;
    }

    private AssetFileManager() {
        mCountryList = new ArrayList<>();
        mProvince = new ArrayList<>();
        mCity = new ArrayList<>();
        mProvinceMap = new ArrayMap<>();
        mCityMap = new ArrayMap<>();
        mProvinceMapFinal = new ArrayMap<>();
        mCityMapFianl = new ArrayMap<>();
        mCountryRegionMap = new ArrayMap<>();
        mProvinceRegionMap = new ArrayMap<>();
        mCityRegionMap_String = new ArrayMap<>();
        mProvinceRegionMap_String = new ArrayMap<>();
        mCountryRegionMap_String = new ArrayMap<>();
        mProvinceRegionMap_String_trun = new ArrayMap<>();
        mCountryRegionMap_String_trun = new ArrayMap<>();
        mPleaseChooseString = SysApplication.getContext().getString(R.string.user_info_drtail_please_choose);
        //readDataFromFilePlus();
    }

    private void readDataFromFile() {

        try {
            InputStreamReader in = new InputStreamReader(SysApplication.getContext().getAssets().open("ybregioncode.txt"));
            BufferedReader bufReader = new BufferedReader(in);
            String line = "";
            mAddressListFormFile = new ArrayList<String>();

            while ((line = bufReader.readLine()) != null) {
                if (!TextUtils.isEmpty(line)) {
                    mAddressListFormFile.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void readDataFromFilePlus() {
        try {
            InputStreamReader in = new InputStreamReader(SysApplication.getContext().getAssets().open("ybregioncode.txt"));
            BufferedReader bufReader = new BufferedReader(in);
            String line = "";
            mAddressListFormFile = new ArrayList<String>();

            while ((line = bufReader.readLine()) != null) {
                if (!TextUtils.isEmpty(line)) {
                    mAddressListFormFile.add(line);
                    handleAddressRegionData(line);
                }
            }
            LogUtils.e("数据库插入完毕");
            //AddressDao.getInstance(SysApplication.getContext()).getAllCountryData();
            //AddressDao.getInstance(SysApplication.getContext()).getProvincesData("CN");
            //AddressDao.getInstance(SysApplication.getContext()).getCitysData("CN_Anhui");
            //AddressDao.getInstance(SysApplication.getContext()).getRegionCode("安徽");
            //AddressDao.getInstance(SysApplication.getContext()).getRegionNameByCode("CN_Anhui_Anqing");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String readFile(Context mCtx, String region) {
        for (int i = 0; i < mAddressListFormFile.size(); i++) {
            String[] address = mAddressListFormFile.get(i).split("\\|");
            for (int j = 0; j < address.length; j++) {
                if (address[j].equals(region)) {
                    return address[address.length - 1];
                }

            }
        }
        return null;
    }

    public String readFile(Context mCtx, String region, String type) {
        for (int i = 0; i < mAddressListFormFile.size(); i++) {
            String[] address = mAddressListFormFile.get(i).split("\\|");
            String addres = address[1];
            String[] split = addres.split("\\_");
            if (split.length == 1) {
                String country = address[address.length - 1];
                mCountryRegionMap.put(addres, country);
            } else if (split.length == 2) {
                String province = address[address.length - 1];
                mProvinceRegionMap.put(split[1], province);
            }
            for (int j = 0; j < address.length; j++) {
                if (address[j].equals(region)) {
                    String addressString;
                    if (OBSERVER_TYPE.equals(type)) {
                        addressString = mProvinceRegionMap.get(split[1]) + " " + address[address.length - 1] + "," + mCountryRegionMap.get(split[0]);
                    } else if (FOLLOW_TYPE.equals(type)) {
                        String province = mProvinceRegionMap.get(split[1]);
                        String city = address[address.length - 1];
                        if (province.equals(city)) {
                            addressString = province;
                        } else {
                            addressString = mProvinceRegionMap.get(split[1]) + " " + address[address.length - 1];
                        }
                    } else if (COUNTRY_TYPE.equals(type)) {
                        addressString = mCountryRegionMap.get(split[0]);
                    } else {
                        if ("CN".equals(split[0])) {
                            if (!EDIT_ADDRESS_TYPE.equals(type)) {
                                addressString = mCountryRegionMap.get(split[0]) + mProvinceRegionMap.get(split[1]) + address[address.length - 1] + "(市/县)";

                            } else {
                                addressString = mProvinceRegionMap.get(split[1]) + " " + address[address.length - 1];
                            }
                        } else {
                            if (split.length == 2) {
                                addressString = mCountryRegionMap.get(split[0]) + " " + mProvinceRegionMap.get(split[1]);
                            } else {
                                addressString = mCountryRegionMap.get(split[0]) + " " + address[address.length - 1];
                            }
                        }
                        THE_LAST_COUNTRY = mCountryRegionMap.get(split[0]);
                        THE_LAST_PROVINCE = mProvinceRegionMap.get(split[1]);
                        THE_LAST_CITY = address[address.length - 1];
                    }
                    return addressString;
                }

            }
        }
        return "";
    }

    /**
     * 获得地址的所有数据
     */
    public void getAddressData(Context mCtx) {
        String lastCountry = "";
        String lastProvince = "";
        if (mAddressListFormFile == null) {
            readDataFromFile();
        }
        for (int i = 0; i < mAddressListFormFile.size(); i++) {
            String[] address = mAddressListFormFile.get(i).split("\\|");
            String addres = address[1];
            String[] split = addres.split("\\_");

            if (split.length == 1) {
                String country = address[address.length - 1];
                if (i == 0) {
                    lastCountry = country;
                }
                if (!lastCountry.equals(country)) {//国家发生变化了
                    mProvinceMap.put(lastCountry, mProvince);
                    mProvince = new ArrayList<>();
                    lastCountry = country;
                }
                if (!mCountryList.contains(country)) {
                    mCountryList.add(country);
                    //LogUtils.e("国家:" + address[address.length - 1]);
                }
            } else if (split.length == 2) {
                String province = address[address.length - 1];
                if (i == 1) {
                    lastProvince = province;
                }
                if (!lastProvince.equals(province)) {//省份发生变化了
                    mCityMap.put(lastProvince, mCity);
                    lastProvince = province;
                    mCity = new ArrayList<>();
                }
                if (!mProvince.contains(province)) {
                    mProvince.add(province);
                    //LogUtils.e("省:" + address[address.length - 1]);
                }
            } else if (split.length == 3) {
                String city = address[address.length - 1];
                if (!mCity.contains(city)) {
                    mCity.add(city);
                    //LogUtils.e("市:" + address[address.length - 1]);
                }
            }
            if (i == mAddressListFormFile.size() - 1) {//当读到尾部时,把最后的这个也加进去
                mProvinceMap.put(lastCountry, mProvince);
                mCityMap.put(lastProvince, mCity);
            }

        }

        /*} catch (IOException e) {
            e.printStackTrace();
        }*/

        travelAndAddData();
    }

    private void travelAndAddData() {
        mCountryStrings = new String[mCountryList.size() + 1];
        for (int i = 0; i < mCountryList.size() + 1; i++) {
            if (i == 0) {
                mCountryStrings[0] = mPleaseChooseString;
            } else {
                mCountryStrings[i] = mCountryList.get(i - 1);
                List<String> provinceList = mProvinceMap.get(mCountryList.get(i - 1));
                if (provinceList == null) {
                    LogUtils.e("这是出错的数据:" + mCountryList.get(i - 1) + "  " + i);
                    continue;
                }
                String[] povinceStrings = new String[provinceList.size() + 1];
                for (int x = 0; x < provinceList.size() + 1; x++) {
                    if (x == 0) {
                        povinceStrings[x] = mPleaseChooseString;
                    } else {
                        String provinces = provinceList.get(x - 1);
                        povinceStrings[x] = provinces;
                        List<String> ciyList = mCityMap.get(provinces);
                        String[] cityStrings = new String[ciyList.size() + 1];
                        for (int y = 0; y < ciyList.size() + 1; y++) {

                            if (y == 0) {
                                cityStrings[y] = mPleaseChooseString;
                            } else {
                                String city = ciyList.get(y - 1);
                                cityStrings[y] = city;
                            }
                        }
                        mCityMapFianl.put(provinces, cityStrings);
                    }
                }
                mProvinceMapFinal.put(mCountryList.get(i - 1), povinceStrings);
            }
        }
        LogUtils.e("地址数据已准备完成");
    }

    /**
     * 获得其地区码
     */
    public String getStringText(String region) {
        /*if(region.equals(" ")){

        }*/
        //String[] split = region.split("\\ ");
        //String needRegion = split[split.length - 1];
        for (int i = 0; i < mAddressListFormFile.size(); i++) {
            String[] address = mAddressListFormFile.get(i).split("\\|");
            for (int j = 0; j < address.length; j++) {
                if (address[j].equals(region)) {
                    return address[1];
                }
            }
        }
        return null;
    }


    public String readFilePlus(String region, String type) {
        boolean isDBReady = (boolean) SPUtils.get(SysApplication.getContext(), Constants.KEY_IS_ADDRESS_DB_READY, false);
        return isDBReady ? AddressManager.getInstance().getRegionStringByCode(region, type) : AddressManager
                .getInstance().getRegionTextByRAM(region, type);
       /* if (region == null || TextUtils.isEmpty(region)) {
            if (COUNTRY_TYPE.equals(type)) {
                return mPleaseChooseString;
            } else if (EDIT_ADDRESS_TYPE.equals(type)) {
                return mPleaseChooseString;
            }
            return "";
        }
        //LogUtils.e(region);
        boolean isNoCityString = false;
        boolean isHaveCN = false;
        String countryString = "";
        String provinceString = "";
        String cityString = mCityRegionMap_String.get(region);
        String[] split1 = region.split("\\_");

        if (!TextUtils.isEmpty(cityString) && cityString != null) {
            provinceString = mProvinceRegionMap_String.get(split1[0] + "_" + split1[1]);
            countryString = mCountryRegionMap_String.get(split1[0]);
            if ("CN".equals(split1[0])) {
                isHaveCN = true;
            }
        } else {
            //为空有两种情况，第一是没有城市的外国国家，第二就是根本没这个东西
            provinceString = mProvinceRegionMap_String.get(region);
            String provinceKey = split1[0];
            if ("CN".equals(provinceKey)) {
                isHaveCN = true;
            }
            countryString = mCountryRegionMap_String.get(provinceKey);
            if (!TextUtils.isEmpty(countryString) && countryString != null) {
                isNoCityString = true;
            } else {
                return "";
            }
        }

        if (COUNTRY_TYPE.equals(type)) {
            return countryString;

        } else if (OBSERVER_TYPE.equals(type)) {
            if (provinceString == null) {
                return "";
            }
            if (isNoCityString) {
                return provinceString + "," + countryString;
            } else {
                return provinceString + " " + cityString + "," + countryString;
            }

        } else if (ADDRESS_TYPE.equals(type)) {
            if (isNoCityString) {//说明是没有城市
                return provinceString + "," + countryString;
            } else {//说明有城市:
                if (isHaveCN) {
                    return cityString + "," + provinceString + "," + countryString;
                } else {
                    return cityString + "," + provinceString + "," + countryString;
                }
            }
        } else if (FOLLOW_TYPE.equals(type)) {
            if (isNoCityString) {
                if (!isHaveCN) {
                    return countryString + " " + provinceString;
                } else {
                    return provinceString;
                }
            } else {
                return provinceString + " " + cityString;
            }
        } else if (EDIT_ADDRESS_TYPE.equals(type)) {//
            if (isHaveCN) {
                return provinceString + " " + cityString;
            } else {
                if (isNoCityString) {
                    return countryString + " " + provinceString;
                } else {
                    return countryString + " " + cityString;
                }
            }
        }
        return "";*/
    }


    private void handleAddressRegionData(String line) {
        AddressDao addressDao = AddressDao.getInstance(SysApplication.getContext());
        String[] split = line.split("\\|");
        String regionCode = split[1];
        String[] regionCodeStrings = regionCode.split("\\_");
        if (regionCodeStrings.length == 1) {//说明是国家
            mCountryRegionMap_String.put(regionCodeStrings[0], split[split.length - 1]);// CN---中国
            // TODO: 2017/2/28 插入到数据库
            addressDao.insertCountry(regionCodeStrings[0], split[split.length - 1]);//插入到国表
            mCountryRegionMap_String_trun.put(split[split.length - 1], regionCodeStrings[0]);//中国--CN
        } else if (regionCodeStrings.length == 2) {//说明是省
            if (!mProvinceRegionMap_String.containsKey(split[1])) {
                mProvinceRegionMap_String.put(split[1], split[split.length - 1]);//CN_ANHUI-----安徽
                // TODO: 2017/2/28 插入到数据库
                //addressDao.insertProvince(regionCodeStrings[0], split[1], split[split.length - 1]);//插入到省表
                mProvinceRegionMap_String_trun.put(split[split.length - 1], split[1]);//安徽----CN_ANHUI
            }
        } else if (regionCodeStrings.length == 3) {
            mCityRegionMap_String.put(regionCode, split[split.length - 1]);//CN_ANHUI_ANQING-----安庆
            // TODO: 2017/2/28 插入到数据库
            //addressDao.insertCity(regionCodeStrings[0], regionCodeStrings[0] + "_" + regionCodeStrings[1], regionCode, split[split.length - 1]);//插入到市表
        }
    }

    /**
     * 获得其地区码
     */
    public String getStringText(String region, String country) {
        /*if(region.equals(" ")){

        }*/
        //String[] split = region.split("\\ ");
        //String needRegion = split[split.length - 1];
        regionString = "";
        for (int i = 0; i < mAddressListFormFile.size(); i++) {
            String[] address = mAddressListFormFile.get(i).split("\\|");
            for (int j = 0; j < address.length; j++) {
                if (address[j].equals(region)) {
                    String countryRegion = mCountryRegionMap_String_trun.get(country);
                    String[] split = address[1].split("\\_");
                    String s = split[0];
                    if (countryRegion.equals(s)) {
                        //LogUtils.e("返回了该字符串:" + address[1]);
                        regionString = address[1];
                        //return address[1];
                        break;
                    }
                }
            }
            if (regionString != null && !regionString.isEmpty()) {
                break;
            }
        }
        return regionString;
    }


    /**
     * 获得其地区码
     */
    public String getStringTextPro(String region, String country) {
        String[] split1 = region.split(country + " ");
        String needCheckReString;
        if (split1.length > 1) {
            needCheckReString = split1[1];
        } else {
            String[] split = region.split("\\ ");
            needCheckReString = split[1];
        }
        regionString = "";
        for (int i = 0; i < mAddressListFormFile.size(); i++) {
            String[] address = mAddressListFormFile.get(i).split("\\|");
            for (int j = 0; j < address.length; j++) {
                if (address[j].equals(needCheckReString)) {
                    String countryRegion = mCountryRegionMap_String_trun.get(country);
                    String[] split = address[1].split("\\_");
                    String s = split[0];
                    if (countryRegion.equals(s)) {
                        //LogUtils.e("返回了该字符串:" + address[1]);
                        regionString = address[1];
                        //return address[1];
                        break;
                    }
                }
            }
            if (regionString != null && !regionString.isEmpty()) {
                break;
            }
        }
        return regionString;
    }
}
