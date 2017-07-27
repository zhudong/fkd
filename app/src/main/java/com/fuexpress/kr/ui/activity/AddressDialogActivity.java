package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.bean.RegionBean;
import com.fuexpress.kr.model.AddressDao;
import com.fuexpress.kr.model.AddressManager;
import com.fuexpress.kr.model.AssetFileManager;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.wheel.OnWheelChangedListener;
import com.fuexpress.kr.ui.view.wheel.WheelView;
import com.fuexpress.kr.ui.view.wheel.wheeladapter.ArrayWheelAdapter;
import com.fuexpress.kr.ui.view.wheel.wheeladapter.RegionWheelAdapter;
import com.fuexpress.kr.ui.view.wheel.wheeladapter.WheelViewForTimePick;
import com.fuexpress.kr.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class AddressDialogActivity extends BaseActivity implements OnWheelChangedListener {


    public static String COUNTRY_TYPE = "country_type";
    public static String PROVINCE_AND_CITY_TYPE = "province_and_city_type";
    public static final String KEY_REGION_CODE = "REGION_CODE";
    private View mView;
    private WheelView mWheelView01;
    private WheelView mWheelView02;
    private List<RegionBean> mCountryList;
    private String[] mCountryStrings;
    private ArrayMap<String, String[]> mProvinceMapFinal;
    private ArrayMap<String, String[]> mCityMapFianl;
    private String mType;
    private String mCurrentCountry;
    private TextView mTv_in_adress_dialog_config_btn;
    private String mShouldShowCountry;
    private String[] mProvinceStrings;
    private String[] mCityStrings;
    private String mCurrentCity;
    private String mCurrentProvince;
    private String mPleaseChooseString;

    private String mHaveLocation;
    private ArrayMap<String, List<RegionBean>> mCountry_provinceMap;
    private ArrayMap<String, List<RegionBean>> mProvince_cityMap;
    private String mOutRegionCode;
    private List<RegionBean> mProvincesRegionBeans;
    private List<RegionBean> mCurrentCityBeanList;

    @Override
    public View setInitView() {
        mView = View.inflate(this, R.layout.activity_address_dialog, null);
        Window window = this.getWindow();//宽度为屏幕宽,位置为底部
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        mWidthPixels = UIUtils.getScreenWidthPixels(this);
        mView.setMinimumWidth(mWidthPixels);
        window.setAttributes(lp);
        return mView;
    }

    @Override
    public void initView() {
        mWheelView01 = (WheelView) mView.findViewById(R.id.wv_in_adress_dialog);
        mWheelView02 = (WheelView) mView.findViewById(R.id.wv_in_adress_dialog_02);
        mTv_in_adress_dialog_config_btn = (TextView) mView.findViewById(R.id.tv_in_adress_dialog_config_btn);
        mTv_in_adress_dialog_config_btn.setOnClickListener(this);
        mWheelView01.addChangingListener(this);
        mWheelView02.addChangingListener(this);
        initData();
    }

    private void initData() {

        mPleaseChooseString = getString(R.string.user_info_drtail_please_choose);
        Intent intent = getIntent();
        mType = intent.getStringExtra("type");

        mShouldShowCountry = intent.getStringExtra("country");
        mHaveLocation = intent.getStringExtra("haveLocation");
        mOutRegionCode = intent.getStringExtra(KEY_REGION_CODE);

        mCountryStrings = AssetFileManager.getInstance().mCountryStrings;

        mProvinceMapFinal = AssetFileManager.getInstance().mProvinceMapFinal;
        mCityMapFianl = AssetFileManager.getInstance().mCityMapFianl;
        mCountry_provinceMap = AddressManager.getInstance().getCountryProvinceMap();
        mProvince_cityMap = AddressManager.getInstance().getProvinceCityMap();
        mCountryList = AddressManager.getInstance().getCountryList();
        //mCurrentCountry = mCountryStrings[0];
        setWheelViewAdapter();
    }

    public int findIndexByKey(List<RegionBean> list, String key) {//寻找集合中key的位置
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            RegionBean regionBean = list.get(i);
            if (regionBean.getRegionName().equals(key))
                index = i;
        }
        return index;
    }

    private void setWheelViewAdapter() {
        mWheelView01.setVisibleItems(5);
        if (COUNTRY_TYPE.equals(mType)) {
            RegionWheelAdapter regionWheelAdapter = new RegionWheelAdapter(this, mCountryList);
            mWheelView01.setViewAdapter(regionWheelAdapter);
            //mWheelView01.setViewAdapter(new ArrayWheelAdapter<String>(this, mCountryStrings));
            if (mShouldShowCountry != null && !TextUtils.isEmpty(mShouldShowCountry)) {
                int i = findIndexByKey(mCountryList, mShouldShowCountry);
                mWheelView01.setCurrentItem(i);
                /*int i = mCountryList.indexOf(mShouldShowCountry);d
                mWheelView01.setCurrentItem(i + 1);*/
            }

        } else {
            // TODO: 2017/3/2 设置第二个WheelView
            ArrayMap<String, RegionBean> codeCountryMap = AddressManager.getInstance().getCodeCountryMap();
            ArrayMap<String, List<RegionBean>> countryProvinceMap = AddressManager.getInstance().getCountryProvinceMap();
            ArrayMap<String, List<RegionBean>> provinceCityMap = AddressManager.getInstance().getProvinceCityMap();
            RegionBean regionNameByCode = AddressManager.getInstance().getRegionByCode(mOutRegionCode);
            if (regionNameByCode.isProvince() || regionNameByCode.isCountry()) {//只有省的情况
                mProvincesRegionBeans = countryProvinceMap.get(regionNameByCode.isProvince() ?
                        regionNameByCode.getCountryCode() : regionNameByCode.getRegionCode());
                int pIndex = valueIndexRegion(mProvincesRegionBeans, regionNameByCode.getRegionCode());
                mWheelView01.setViewAdapter(new RegionWheelAdapter(this, mProvincesRegionBeans));
                mWheelView01.setCurrentItem(pIndex);
            } else {
                String provinceCode = regionNameByCode.getProvinceCode();
                List<RegionBean> cityRegionBeans = provinceCityMap.get(provinceCode);
                String[] codeSplit = provinceCode.split("\\_");
                mProvincesRegionBeans = countryProvinceMap.get(codeSplit[0]);
                int cIndex = valueIndexRegion(cityRegionBeans, regionNameByCode.getRegionCode());
                int pIndex = valueIndexRegion(mProvincesRegionBeans, regionNameByCode.getProvinceCode());
                mWheelView01.setViewAdapter(new RegionWheelAdapter(this, mProvincesRegionBeans));
                mWheelView01.setCurrentItem(pIndex);
                mWheelView02.setVisibility(View.VISIBLE);
                mWheelView02.setViewAdapter(new RegionWheelAdapter(this, cityRegionBeans));
                mWheelView02.setCurrentItem(cIndex);
            }
            /*if (checkTextIsNotEmptyOrNull(mShouldShowCountry)) {
                mProvinceStrings = mProvinceMapFinal.get(mShouldShowCountry);
                mWheelView01.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceStrings));
                if (checkTextIsNotEmptyOrNull(mHaveLocation)) {
                    String[] split = mHaveLocation.split(mShouldShowCountry + " ");
                    if (split.length > 1) {//国外的情况
                        int i;
                        i = valueIndex(mProvinceMapFinal.get(mShouldShowCountry), split[1]);
                        if (i == -1) {//说明是外国且含有省市两级的情况
                            String regionStringText = AssetFileManager.getInstance().getStringText(split[1], mShouldShowCountry);
                            String cityString = AssetFileManager.getInstance().mCityRegionMap_String.get(regionStringText);
                            String[] split1 = regionStringText.split("\\_");
                            setWheelViewMethod(mShouldShowCountry, split1[1], cityString);
                        } else {
                            mWheelView01.setCurrentItem(i);
                        }
                    } else {//国内的情况
                        String[] split3 = mHaveLocation.split("\\ ");
                        setWheelViewMethod(mShouldShowCountry, split3[0], split3[1]);
                    }
                }

            }*/
        }
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mWheelView01 && PROVINCE_AND_CITY_TYPE.equals(mType)) {
            int currentItem = mWheelView01.getCurrentItem();
            if (currentItem != 0) {
                RegionBean regionBean = mProvincesRegionBeans.get(currentItem);//取出当前的省Bean
                String provinceCode = regionBean.getRegionCode();
                List<RegionBean> cityRegionBeans = mProvince_cityMap.get(provinceCode);//查找出其是否含有旗下城市
                if (cityRegionBeans != null && cityRegionBeans.size() != 1) {//假如有,就显示出来
                    mWheelView02.setVisibility(View.VISIBLE);
                    mWheelView02.setViewAdapter(new RegionWheelAdapter(this, cityRegionBeans));
                    mCurrentCityBeanList = cityRegionBeans;
                    mWheelView02.setCurrentItem(0);
                } else {//没有就隐藏起来咯,挑
                    mWheelView02.setVisibility(View.GONE);
                    mCurrentCityBeanList = new ArrayList<>();
                }
            } else {
                mWheelView02.setVisibility(View.GONE);
                mCurrentCityBeanList = new ArrayList<>();
            }

        }
        /*if (wheel == mWheelView01 && COUNTRY_TYPE.equals(mType)) {
            int currentItem = mWheelView01.getCurrentItem();
            mCurrentCountry = mCountryStrings[currentItem];
        } else if (wheel == mWheelView01 && PROVINCE_AND_CITY_TYPE.equals(mType)) {
            int currentItem = mWheelView01.getCurrentItem();
            mCurrentProvince = mProvinceStrings[currentItem];
            if (checkTextIsNotEmptyOrNull(mCurrentProvince)) {
                mCityStrings = mCityMapFianl.get(mCurrentProvince);
                if (mCityStrings != null && !(mCityStrings.length == 1)) {
                    mWheelView02.setVisibility(View.VISIBLE);
                    mWheelView02.setViewAdapter(new ArrayWheelAdapter<String>(this, mCityStrings));
                    mWheelView02.setCurrentItem(0);
                } else if (mCityStrings.length == 1) {
                    mWheelView02.setVisibility(View.GONE);
                    //RETURN_TAG = "no_city";
                } else {
                    mWheelView02.setVisibility(View.GONE);
                }
            } else {
                mWheelView02.setVisibility(View.GONE);
            }

        } else if (wheel == mWheelView02 && PROVINCE_AND_CITY_TYPE.equals(mType)) {
            int currentItem = mWheelView02.getCurrentItem();
            mCurrentCity = mCityStrings[currentItem];

        }*/
    }

    @Override
    public void onChanged(WheelViewForTimePick wheel, int oldValue, int newValue) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_in_adress_dialog_config_btn:
                Intent intent = new Intent();
                if (COUNTRY_TYPE.equals(mType)) {
                    /*if (mCurrentCountry == null) {
                        mCurrentCountry = mPleaseChooseString;
                    }*/
                    int currentItem = mWheelView01.getCurrentItem();
                    RegionBean regionBean = mCountryList.get(currentItem);
                    mCurrentCountry = regionBean.getRegionName();
                    intent.putExtra("country", mCurrentCountry);
                    intent.putExtra(KEY_REGION_CODE, regionBean.getRegionCode());
                    setResult(1, intent);
                } else {
                    String readFile = "";

                    /*if (checkTextIsNotEmptyOrNull(mCurrentProvince)) {
                       *//* int i;
                        if (checkTextIsNotEmptyOrNull(mCurrentCountry)) {
                            i = mCountryList.indexOf(mCurrentCountry);
                        } else {
                            i = mCountryList.indexOf(mShouldShowCountry);
                        }

                        if (i == 1) {
                            intent.putExtra("isCNtype", true);
                        } else {
                            intent.putExtra("isCNtype", false);
                            if (checkTextIsNotEmptyOrNull(mCurrentCountry)) {
                                intent.putExtra("country", mCurrentCountry);
                            } else {
                                intent.putExtra("country", mShouldShowCountry);
                            }
                        }
                        intent.putExtra("province", mCurrentProvince);
                        if (mCurrentCity != null && !TextUtils.isEmpty(mCurrentCity)) {
                            intent.putExtra("city", mCurrentCity);
                        }*//*

                        if (checkTextIsNotEmptyOrNull(mCurrentCity)) {
                            //readFile = AssetFileManager.getInstance().readFile(this, mCurrentCity, AssetFileManager.EDIT_ADDRESS_TYPE);
                            //String stringText = AssetFileManager.getInstance().getStringText(mCurrentCity);
                            String stringText = AssetFileManager.getInstance().getStringText(mCurrentCity, mShouldShowCountry);
                            readFile = AssetFileManager.getInstance().readFilePlus(stringText, AssetFileManager.EDIT_ADDRESS_TYPE);

                        } else {
                            if (mCityStrings == null || mCityStrings.length == 1) {

                                //readFile = AssetFileManager.getInstance().readFile(this, mCurrentProvince, AssetFileManager.EDIT_ADDRESS_TYPE);
                                String stringText = AssetFileManager.getInstance().getStringText(mCurrentProvince, mShouldShowCountry);
                                readFile = AssetFileManager.getInstance().readFilePlus(stringText, AssetFileManager.EDIT_ADDRESS_TYPE);
                            } else {
                                readFile = mPleaseChooseString;
                            }
                        }

                    } else {
                        readFile = mPleaseChooseString;
                    }*/
                    RegionBean regionBean = null;
                    if (View.VISIBLE == mWheelView02.getVisibility()) {
                        int currentItem = mWheelView02.getCurrentItem();
                        if (mCurrentCityBeanList.size() != 0 && currentItem != 0) {
                            regionBean = mCurrentCityBeanList.get(currentItem);
                            readFile = AddressManager.getInstance().getRegionStringByCode(regionBean.getRegionCode(), AddressManager.EDIT_ADDRESS_TYPE);
                        }
                    } else {
                        int currentItem = mWheelView01.getCurrentItem();
                        if (currentItem != 0) {
                            regionBean = mProvincesRegionBeans.get(currentItem);
                            readFile = AddressManager.getInstance().getRegionStringByCode(regionBean.getRegionCode(), AddressManager.EDIT_ADDRESS_TYPE);
                        }
                    }
                    intent.putExtra("location", TextUtils.isEmpty(readFile) ? mPleaseChooseString : readFile);
                    intent.putExtra(KEY_REGION_CODE, regionBean == null ? "" : regionBean.getRegionCode());
                    setResult(2, intent);
                }
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
    }

    public boolean checkTextIsNotEmptyOrNull(String s) {
        if (s == null || TextUtils.isEmpty(s) || mPleaseChooseString.equals(s)) {
            return false;
        }
        return true;
    }

    public int valueIndex(String[] strings, String targetValue) {
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (targetValue.equals(string)) {
                return i;
            }
        }
        return -1;
    }

    public int valueIndexRegion(List<RegionBean> regionBeanList, String targetValue) {
        int index = -1;
        if (TextUtils.isEmpty(targetValue)) {
            index = 0;
        } else {
            for (int i = 0; i < regionBeanList.size(); i++) {
                RegionBean regionBean = regionBeanList.get(i);
                if (targetValue.equals(regionBean.getRegionCode())) {
                    index = i;
                }
            }
        }
        return index;
    }

    public void setWheelViewMethod(String country, String province, String theCity) {
        int i = valueIndex(mProvinceMapFinal.get(country), province);
        String keyByProvince = mProvinceMapFinal.get(country)[i];
        String[] cityStrings = mCityMapFianl.get(keyByProvince);
        int i1 = valueIndex(cityStrings, theCity);
        mWheelView01.setCurrentItem(i);
        mWheelView02.setVisibility(View.VISIBLE);
        mWheelView02.setViewAdapter(new ArrayWheelAdapter<String>(this, cityStrings));
        mWheelView02.setCurrentItem(i1);
    }
}