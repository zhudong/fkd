package com.fuexpress.kr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.UserManager;
import com.fuexpress.kr.ui.activity.choose_address.AddressBundleBean;
import com.fuexpress.kr.ui.activity.choose_address.AddressBundleBeanFactory;
import com.fuexpress.kr.ui.activity.choose_address.ChooseRegionActivity;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.socks.library.KLog;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsUser;

import static com.google.common.base.Preconditions.checkNotNull;

public class UserDetailInfoActivity extends BaseActivity {

    private View mRootView;
    private TitleBarView mTitleBarView;
    private TextView mTextViewRight;
    private ImageView mImageViewLeft;
    private EditText mEd_in_user_info_nick_name;
    private EditText mEd_in_user_info_self_introduction;
    private TextView mTv_in_user_info_member_group, mTv_in_user_info_phone_number,
            mTv_in_user_info_email, mTv_in_user_info_gender, mTv_in_user_info_birthday,
            mTv_in_user_info_country, mTv_in_user_info_location, mTv_in_user_info_personal_homepage;
    private RelativeLayout mRl_in_user_info_email, mRl_in_user_info_birthday, mRl_in_user_info_gender,
            mRl_in_user_info_country, mRl_in_user_info_location;
    private String mCountry;
    private String mProvince;
    private String mCity;
    private RelativeLayout mRl_in_user_info_phone;
    private EditText mEd_in_user_info_personal_homepage;
    private String mLastNikename = "";
    private String mLastBrithday = "";
    private String mLastHomePage = "";
    private String mLastIntro = "";
    private String mGenderString = "";
    private String mLastRegionValue = "";
    private String mPleaseChoose;
    private String mThisRegionCode;
    private AddressBundleBean mCurrentAddressBean;
    private String mCurrentCountryStr = "";
    private String mCurrentRegionStr = "";
    private String mCountryCode = "";
    private String mRegionID = "";
    private String mRegionCode = "";
    private RelativeLayout mRl_member;


    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_user_detail_info, null);
        TitleBarView titleBarView = (TitleBarView) mRootView.findViewById(R.id.title_in_user_info);
        titleBarView.getIv_in_title_back().setOnClickListener(this);
        mLastRegionValue = getString(R.string.user_info_drtail_please_choose);
        mPleaseChoose = getString(R.string.user_info_drtail_please_choose);
        return mRootView;
    }


    @Override
    public void initView() {
        initData();
        registerWidget();
    }

    private void initData() {
        UserManager.getInstance().getUserDetailInfo();
        UserManager.getInstance().getUserSimpleInfo(AccountManager.getInstance().mUin, null);
    }

    private void registerWidget() {
        mEd_in_user_info_nick_name = (EditText) mRootView.findViewById(R.id.ed_in_user_info_nick_name);
        mEd_in_user_info_self_introduction = (EditText) mRootView.findViewById(R.id.ed_in_user_info_self_introduction);
        mTv_in_user_info_member_group = (TextView) mRootView.findViewById(R.id.tv_in_user_info_member_group);
        mTv_in_user_info_phone_number = (TextView) mRootView.findViewById(R.id.tv_in_user_info_phone_number);
        mTv_in_user_info_email = (TextView) mRootView.findViewById(R.id.tv_in_user_info_email);
        mTv_in_user_info_gender = (TextView) mRootView.findViewById(R.id.tv_in_user_info_gender);
        mTv_in_user_info_birthday = (TextView) mRootView.findViewById(R.id.tv_in_user_info_birthday);
        mTv_in_user_info_country = (TextView) mRootView.findViewById(R.id.tv_in_user_info_country);
        mTv_in_user_info_location = (TextView) mRootView.findViewById(R.id.tv_in_user_info_location);
        mEd_in_user_info_personal_homepage = (EditText) mRootView.findViewById(R.id.ed_in_user_info_personal_homepage);

        mRl_in_user_info_email = (RelativeLayout) mRootView.findViewById(R.id.rl_in_user_info_email);
        mRl_in_user_info_gender = (RelativeLayout) mRootView.findViewById(R.id.rl_in_user_info_gender);
        mRl_in_user_info_birthday = (RelativeLayout) mRootView.findViewById(R.id.rl_in_user_info_birthday);
        mRl_in_user_info_country = (RelativeLayout) mRootView.findViewById(R.id.rl_in_user_info_country);
        mRl_in_user_info_location = (RelativeLayout) mRootView.findViewById(R.id.rl_in_user_info_location);
        mRl_in_user_info_phone = (RelativeLayout) mRootView.findViewById(R.id.rl_in_user_info_phone);
        Button btn_user_info_save = (Button) mRootView.findViewById(R.id.btn_user_info_save);
        mRl_member = (RelativeLayout) mRootView.findViewById(R.id.rl_member);
        btn_user_info_save.setOnClickListener(this);
        mRl_in_user_info_country.setOnClickListener(this);
        mRl_in_user_info_location.setOnClickListener(this);
        mRl_in_user_info_birthday.setOnClickListener(this);
        mRl_in_user_info_gender.setOnClickListener(this);
        mRl_in_user_info_email.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_user_info_save:
                //LogUtils.e("我被点击了");
                checkInfo();
                break;
            case R.id.rl_in_user_info_country:
                /*Intent intent = new Intent(this, AddressDialogActivity.class);
                intent.putExtra("type", AddressDialogActivity.COUNTRY_TYPE);
                String trim = mTv_in_user_info_country.getText().toString().trim();
                if (trim != null && !TextUtils.isEmpty(trim)) {
                    intent.putExtra("country", trim);
                }
                startActivityForResult(intent, 1);*/
                Intent intent = new Intent(this, ChooseRegionActivity.class);
                intent = AddressBundleBeanFactory.getInstance().getCountryTypeIntentBeanNew(intent, mCurrentCountryStr, mCountryCode,
                        getString(R.string.user_info_drtail_country_title), getString(R.string.user_info_drtail_title));
                startActivityForResult(intent, 1);
                break;
            case R.id.rl_in_user_info_location:
               /* Intent intent02 = new Intent(this, AddressDialogActivity.class);
                intent02.putExtra("type", AddressDialogActivity.PROVINCE_AND_CITY_TYPE);
                if (mCountry != null && !TextUtils.isEmpty(mCountry)) {
                    intent02.putExtra("country", mCountry);
                } else {
                    String trim02 = mTv_in_user_info_country.getText().toString().trim();
                    if (!TextUtils.isEmpty(trim02)) {
                        intent02.putExtra("country", trim02);
                    }
                }
                String trim1 = mTv_in_user_info_location.getText().toString().trim();
                if (!TextUtils.isEmpty(trim1)) {
                    intent02.putExtra("haveLocation", trim1);
                }
                String regionCode = "";
                if (mThisRegionCode != null && !TextUtils.isEmpty(mThisRegionCode))
                    regionCode = mThisRegionCode;
                else {
                    String[] split = UserManager.getInstance().mRegionCode.split("\\_");
                    regionCode = split[0];
                }
                intent02.putExtra(AddressDialogActivity.KEY_REGION_CODE, regionCode);
                startActivityForResult(intent02, 2);*/
                Intent intent02 = new Intent(this, ChooseRegionActivity.class);
                mCurrentAddressBean.setRegionCode(mRegionCode);
                String selectString = "";
                if (mTv_in_user_info_location.getText().toString().toString().equals(mPleaseChoose)) {
                    mCurrentAddressBean = new AddressBundleBean();
                    mCurrentAddressBean.setCouSntryCode(mCountryCode);
                } else {
                    if (mCurrentAddressBean != null)
                        selectString = mCurrentAddressBean.getProvinceString();
                }
                intent02 = AddressBundleBeanFactory.getInstance().getAddressBeanByBeanDefaultNew(intent02, mCurrentAddressBean,
                        mCountryCode, mRegionID, mCurrentCountryStr != null ? mCurrentCountryStr : getString(R.string.user_info_drtail_location_title),
                        getString(R.string.user_info_drtail_title), selectString);
                startActivityForResult(intent02, 2);
                break;
            case R.id.rl_in_user_info_birthday:
                //startDDMActivity(TImePickerActivity.class, false);
                String time = mTv_in_user_info_birthday.getText().toString().trim();
                if (!time.isEmpty() && !(mPleaseChoose.equals(time))) {
                    Intent intent03 = new Intent(this, TImePickerActivity.class);
                    String[] split = time.split("\\-");
                    int year = Integer.parseInt(split[0]);
                    int month = Integer.parseInt(split[1]);
                    int day = Integer.parseInt(split[2]);
                    intent03.putExtra("year", year);
                    intent03.putExtra("month", month - 1);
                    intent03.putExtra("day", day);
                    startActivityForResult(intent03, 3);
                } else {
                    //startDDMActivity(TImePickerActivity.class, true);
                    Intent intent03 = new Intent(this, TImePickerActivity.class);
                    startActivityForResult(intent03, 3);
                }
                break;
            case R.id.rl_in_user_info_gender:
                Intent intent04 = new Intent(this, GenderDialogActivity.class);
                startActivityForResult(intent04, 4);
                break;
            case R.id.rl_in_user_info_email:
                Intent intent05 = new Intent(this, ChangeEmailActivity.class);
                startActivityForResult(intent05, 5);
                break;
        }
    }

    private void checkInfo() {
        ArrayMap<Integer, String> paramMap = new ArrayMap<>();
        String newNickName = mEd_in_user_info_nick_name.getText().toString().trim();
        if (TextUtils.isEmpty(newNickName) || newNickName == null) {
            showProgressDiaLog(SweetAlertDialog.WARNING_TYPE, getString(R.string.user_info_drtail_dialog_for_nick_name));
            dissMissProgressDiaLog(1000);
            return;
        } else if (!mLastNikename.equals(newNickName)) {
            paramMap.put(CsUser.UserInfoFieldType.USER_INFO_FIELD_NICKNAME_VALUE, newNickName);
        }
        String newGender = mTv_in_user_info_gender.getText().toString().trim();
        String male = getString(R.string.user_info_drtail_gender_male);
        String female = getString(R.string.user_info_drtail_gender_female);
        if (!mGenderString.equals(newGender)) {
            if (female.equals(newGender)) {
                newGender = "1";
            } else if (male.equals(newGender)) {
                newGender = "2";
            }
            paramMap.put(CsUser.UserInfoFieldType.USER_INFO_FIELD_GENDER_VALUE, newGender);
        }
        String newBrithday = mTv_in_user_info_birthday.getText().toString().trim();
        if (mLastBrithday == null || !mLastBrithday.equals(newBrithday) && !mLastBrithday.equals(mPleaseChoose)) {
            paramMap.put(CsUser.UserInfoFieldType.USER_INFO_FIELD_BIRTHDAY_VALUE, newBrithday);
        }
        String newCountry = mTv_in_user_info_country.getText().toString().trim();
        String newLocation = mTv_in_user_info_location.getText().toString().trim();
        if (newCountry == null || mPleaseChoose.equals(newCountry)) {
            showProgressDiaLog(SweetAlertDialog.WARNING_TYPE, getString(R.string.user_info_drtail_dialog_address));
            dissMissProgressDiaLog(1000);
            return;
        } else if (newLocation == null || mPleaseChoose.equals(newLocation)) {
            showProgressDiaLog(SweetAlertDialog.WARNING_TYPE, getString(R.string.user_info_drtail_dialog_address_02));
            dissMissProgressDiaLog(1000);
            return;
        } else {
            if (!mLastRegionValue.equals(newLocation) && mRegionCode != null && !TextUtils.isEmpty(mRegionCode)) {
                /*String[] split = newLocation.split("\\ ");
                String s = split[1];
                String stringText = AssetFileManager.getInstance().getStringTextPro(newLocation, newCountry);*/
                //LogUtils.e("这是StringText的返回值:" + stringText);
                paramMap.put(CsUser.UserInfoFieldType.USER_INFO_FIELD_REGION_VALUE, mRegionCode);
                paramMap.put(CsUser.UserInfoFieldType.USER_INFO_FIELD_REGION_ID_VALUE, mRegionCode);
            }
        }

        String newUserHomePage = mEd_in_user_info_personal_homepage.getText().toString().trim();
        if (!mLastHomePage.equals(newUserHomePage)) {
            paramMap.put(CsUser.UserInfoFieldType.USER_INFO_FIELD_HOME_PAGE_VALUE, newUserHomePage);
        }

        String newSelfIntro = mEd_in_user_info_self_introduction.getText().toString().trim();
        if (mLastIntro != null && !mLastIntro.equals(newSelfIntro)) {
            paramMap.put(CsUser.UserInfoFieldType.USER_INFO_FIELD_INTRO_VALUE, newSelfIntro);
        }

        if (paramMap.size() >= 1) {
            UserManager.getInstance().setUserInfo(paramMap);
        } else {
            showProgressDiaLog(SweetAlertDialog.WARNING_TYPE, getString(R.string.user_info_drtail_if_info_not_change_dialog));
            dissMissProgressDiaLog(1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (1 == requestCode && data != null) {
            Bundle bundle = data.getBundleExtra("address");
            AddressBundleBean addressBundleBean = (AddressBundleBean) bundle.getSerializable("address");
            checkNotNull(addressBundleBean, "return country Data null");
            mCountryCode = addressBundleBean.getCouSntryCode();
            mCurrentCountryStr = addressBundleBean.getSelectedString();
            String trim = mTv_in_user_info_country.getText().toString().trim();
            if (!mCurrentCountryStr.equals(trim)) {
                mTv_in_user_info_country.setText(mCurrentCountryStr);
                mTv_in_user_info_location.setText(mPleaseChoose);
            }
        } else if (2 == requestCode && data != null) {
//            String location = data.getStringExtra("location");
//            mTv_in_user_info_location.setText(location);
//            mRegion = data.getStringExtra("rCode");
            Bundle bundle = data.getBundleExtra("address");
            AddressBundleBean addressBundleBean = (AddressBundleBean) bundle.getSerializable("address");
            checkNotNull(addressBundleBean, "return region Data null");
            mRegionID = addressBundleBean.getRegionId();
            mCurrentRegionStr = addressBundleBean.getSelectedString();
            mCountryCode = addressBundleBean.getCouSntryCode();
            mTv_in_user_info_location.setText(mCurrentRegionStr);
            mRegionCode = addressBundleBean.getRegionCode();
            mCurrentAddressBean = addressBundleBean;
        } else if (3 == requestCode && data != null) {
            String time = data.getStringExtra("time");
            mTv_in_user_info_birthday.setText(time);
            //mLastBrithday = time;
        } else if (4 == requestCode && data != null) {
            String gender = data.getStringExtra("gender");
            LogUtils.e(gender);
            mTv_in_user_info_gender.setText(data.getStringExtra("gender"));
        }
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_USER_DETAIL_INFO_FILED_COMPLETE:
                boolean isSuccess = event.getBooleanParam();
                if (isSuccess) {
                    setwidgetAndData();
                    UserManager.getInstance().getMemberGroupNameRequest(UserManager.getInstance().getMemberGroup());
                } else {
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.user_info_drtail_dialog_get_user_info_fail));
                    dissMissProgressDiaLog(1000);
                }
                break;
            case BusEvent.SET_USER_INFO_FILED_COMPLETE:
                boolean isSetSuccess = event.getBooleanParam();
                if (isSetSuccess) {
                    showProgressDiaLog(SweetAlertDialog.SUCCESS_TYPE, getString(R.string.user_info_drtail_dialog_change_user_info_success));
                    KLog.i(" currency_code " + AccountManager.getInstance().getCurrencyCode());
                    AccountManager.getInstance().getUserinfo();
                } else {
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.user_info_drtail_dialog_change_user_info_fail));
                }
                dissMissProgressDiaLog(1000);
                break;
            case BusEvent.GET_SIMPLE_USER_INFO_COMPETE:
                boolean isGetSuccess = event.getBooleanParam();
                if (isGetSuccess) {
                    mCurrentAddressBean = (AddressBundleBean) event.getParam();
                    mCurrentCountryStr = UserManager.getInstance().mCurrentCountryStr;
                    mCurrentRegionStr = mCurrentAddressBean.getSelectedString();
                    mTv_in_user_info_country.setText(TextUtils.isEmpty(mCurrentCountryStr) ? getString(R.string.user_info_drtail_please_choose) : mCurrentCountryStr);
                    mTv_in_user_info_location.setText(TextUtils.isEmpty(mCurrentRegionStr) ? getString(R.string.user_info_drtail_please_choose) : mCurrentRegionStr);
                } else {
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.user_info_drtail_dialog_get_user_info_fail));
                    dissMissProgressDiaLog(600);
                }
                break;
            case BusEvent.GET_MEMBER_NAME_CONPLETE:
                if (event.getBooleanParam()) {
                    mRl_member.setVisibility(View.VISIBLE);
                    mTv_in_user_info_member_group.setText(getString(R.string.user_info_detail_end, UserManager.getInstance().getGroupName(), UserManager.getInstance().mMemberEnd));
                } else {
                    CustomToast.makeText(this, "会员组失败,服务器:" + event.getStrParam(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setwidgetAndData() {
        mCountryCode = UserManager.getInstance().mCountryCode;
        mRegionID = UserManager.getInstance().mRegionID;
        mRegionCode = UserManager.getInstance().mRegionCode;
        mLastNikename = AccountManager.getInstance().nikename;
        mEd_in_user_info_nick_name.setText(mLastNikename);
        int memberGroup = UserManager.getInstance().getMemberGroup();
        if (memberGroup == 0) {
            mRl_member.setVisibility(View.GONE);
        } else {
            mRl_member.setVisibility(View.VISIBLE);
            String fullText = "";
            int stringID = -1;
            switch (memberGroup) {
                case 1:
                    //mTv_in_user_info_member_group.setText(getString(R.string.user_info_drtail_member_super));
                    stringID = R.string.user_info_drtail_member_normal;
                    break;
                case 2:
                    //mTv_in_user_info_member_group.setText(getString(R.string.user_info_drtail_member_vip) + getString(R.string.user_info_drtail_member_end_01) + UserManager.getInstance().mMemberEnd + getString(R.string.user_info_drtail_member_end_02));
                    stringID = R.string.user_info_drtail_member_vip;
                    break;
                case 3:
                   /* String allString = getString(R.string.user_info_drtail_member_normal) + getString(R.string.user_info_detail_end, UserManager.getInstance().mMemberEnd);
                    mTv_in_user_info_member_group.setText(allString);*/
                    stringID = R.string.user_info_drtail_member_super;
                    break;
                case 4:
                    stringID = R.string.user_info_drtail_member_buy;
                    break;
                case 5:
                    stringID = R.string.user_info_drtail_member_net;
                    break;
            }
            String memberEnd = UserManager.getInstance().mMemberEnd;
            if (!TextUtils.isEmpty(memberEnd))
                mTv_in_user_info_member_group.setText(getString(R.string.user_info_detail_end, getString(stringID), memberEnd));
            else
                mRl_member.setVisibility(View.GONE);
        }

        if (UserManager.getInstance().mMobile != null && !TextUtils.isEmpty(UserManager.getInstance().mMobile)) {
            mTv_in_user_info_phone_number.setText(UserManager.getInstance().mMobile);
        } else {
            mRl_in_user_info_phone.setVisibility(View.GONE);
        }
        if (UserManager.getInstance().mEmail != null && !TextUtils.isEmpty(UserManager.getInstance().mEmail)) {
            mTv_in_user_info_email.setText(UserManager.getInstance().mEmail);
        } else {
            mRl_in_user_info_email.setVisibility(View.GONE);
        }
        int gender = AccountManager.getInstance().mGender;

        mGenderString = "";
        if (gender != 0) {
            if (1 == gender) {//1为女2为男
                mTv_in_user_info_gender.setText(getString(R.string.user_info_drtail_gender_female));
                mGenderString = getString(R.string.user_info_drtail_gender_female);
            } else {
                mTv_in_user_info_gender.setText(getString(R.string.user_info_drtail_gender_male));
                mGenderString = getString(R.string.user_info_drtail_gender_male);
            }
        } else {
            mTv_in_user_info_gender.setText(mPleaseChoose);
        }

        mLastBrithday = UserManager.getInstance().mBrithday;
        if (mLastBrithday.isEmpty()) {
            mTv_in_user_info_birthday.setText(mPleaseChoose);
        } else {
            mTv_in_user_info_birthday.setText(mLastBrithday);
        }
        if (UserManager.getInstance().mHomePage != null) {
            mLastHomePage = UserManager.getInstance().mHomePage;
            mEd_in_user_info_personal_homepage.setText(UserManager.getInstance().mHomePage);
        }
        if (AccountManager.getInstance().mIntro != null) {
            mLastIntro = AccountManager.getInstance().mIntro;
            mEd_in_user_info_self_introduction.setText(AccountManager.getInstance().mIntro);
        }

        /*mThisRegionCode = UserManager.getInstance().mRegionCode;
        String locationString = AssetFileManager.getInstance().readFilePlus(mThisRegionCode, AssetFileManager.EDIT_ADDRESS_TYPE);
        if (TextUtils.isEmpty(locationString)) {
            mLastRegionValue = mPleaseChoose;
        } else {
            mLastRegionValue = locationString;
        }
        //mLastRegionValue = AssetFileManager.getInstance().readFilePlus(region, AssetFileManager.EDIT_ADDRESS_TYPE);
        String country = AssetFileManager.getInstance().readFilePlus(mThisRegionCode, AssetFileManager.COUNTRY_TYPE);
        if (TextUtils.isEmpty(country)) {
            country = mPleaseChoose;
        }
        mTv_in_user_info_country.setText(country);
        mTv_in_user_info_location.setText(mLastRegionValue);*/
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}
