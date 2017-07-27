package com.fuexpress.kr.utils;


import android.app.Activity;
import android.text.TextUtils;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.SortCountrtyModel;
import com.fuexpress.kr.model.AccountManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by xiekaihua on 2016/5/23.
 * 校对手机号,使用手机注册，绑定手机，使用手机号登陆三个地方需要校对手机号
 */
public class CountryNumberUtils {
    public static boolean isNumberCurrect(int countryNumber,String number){
        if(TextUtils.isEmpty(number)) {
            return  false;
        }
        return isNumeric(number);

    }
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String delete0(String phone) {
        if (phone.startsWith("0")) {
            phone = phone.substring(1);
            return delete0(phone);
        } else {
            return phone;
        }
    }
    public static void setResetCountry(Activity activity){
        List<SortCountrtyModel>   courtrys = filledData(activity.getResources().getStringArray(R.array.date));
        if(courtrys==null||courtrys.size()==0){
            return;
        }else {
            for(SortCountrtyModel model:courtrys){
                if(AccountManager.getInstance().mCountryCode.equals(model.getCourntyCode())){
                    AccountManager.getInstance().userCountry=model.getCountry();
                    return;
                }
            }
        }
    }
    private static List<SortCountrtyModel> filledData(String[] date) {
        List<SortCountrtyModel> mSortList = new ArrayList<SortCountrtyModel>();

        for (int i = 0; i < date.length; i++) {
            SortCountrtyModel sortModel = new SortCountrtyModel();

            String dataStr = date[i].toString();
            dataStr.replace(" ", "");
            String[] str1 = date[i].split(",");
            sortModel.setCountry(str1[0]);
            sortModel.setPhone(str1[1]);
            sortModel.setCourntyCode(str1[2]);
            //汉字转换成拼音
            String pinyin = CharacterParser.getInstance().getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

}