package com.fuexpress.kr.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.AddressManager;
import com.fuexpress.kr.model.AssetFileManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.choose_address.AddressBundleBean;
import com.fuexpress.kr.ui.activity.choose_address.AddressBundleBeanFactory;
import com.fuexpress.kr.ui.activity.choose_address.ChooseRegionActivity;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsAddress;

import static com.google.common.base.Preconditions.checkNotNull;


public class AddNewAddressActivity extends BaseActivity {

    private View rootView;
    private TextView packageAddressTv;
    private EditText consigneeEt;
    private EditText phoneEt;
    private EditText detailAddressEt;
    private EditText postcodeEt;
    private EditText companyEt;
    private EditText idCardEt;
    private TextView countryTv;
    private TextView cityTv;
    private CheckBox isDefaultCb;
    private Button saveBtn;
    private RelativeLayout countryLayout;
    private RelativeLayout locationLayout;
    private LinearLayout isDefaultLayout;
    private String region;
    private boolean isDefault;
    private int addressId = -1;
    private CsAddress.CustomerAddress address;
    private int mAddressListSize;
    private CustomDialog.Builder dialog;
    private String countryCode = "";
    private String mTitleText;
    private AddressBundleBean mCurrentAddressBean;
    private String mCurrentCountryStr;
    private String mRegionCode;
    private String mCountryCode;
    private String mRegionID;
    private String mCurrentRegionStr;

    /*public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_add_new_address, null);
        return rootView;
    }*/

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_add_new_address, null);
        Intent intent = getIntent();
        mAddressListSize = intent.getIntExtra("addressListSize", 0);
        Bundle bundle = intent.getExtras();
        boolean deliveryAddAddress = intent.getBooleanExtra("deliveryAddAddress", false);
        if (bundle != null) {
            address = (CsAddress.CustomerAddress) bundle.getSerializable("address");
            if (address != null) {
                addressId = address.getAddressId();
                mRegionCode = address.getRegion();
                mRegionID = String.valueOf(address.getRegionId());
                mCurrentCountryStr = address.getCountryname();
                mCountryCode = address.getCountryCode();
                mCurrentRegionStr = address.getRegionname();
            }
            if (address != null) {
                region = address.getRegion();
            }
        }
        TitleBarView title_in_address = (TitleBarView) rootView.findViewById(R.id.title_in_address);
        title_in_address.getIv_in_title_back().setOnClickListener(this);
        title_in_address.getmTv_in_title_back_tv().setText(getString(R.string.address_manager));
        //TitleBarView titleBarView = new TitleBarView(rootView);
        //packageAddressTv = titleBarView.getPackageAddressTv();
        consigneeEt = (EditText) rootView.findViewById(R.id.ed_in_add_new_address_consignee);
        phoneEt = (EditText) rootView.findViewById(R.id.ed_in_add_new_address_phone);
        detailAddressEt = (EditText) rootView.findViewById(R.id.ed_in_add_new_address_detail_address);
        postcodeEt = (EditText) rootView.findViewById(R.id.ed_in_add_new_address_postcode);
        companyEt = (EditText) rootView.findViewById(R.id.ed_in_add_new_address_company_name);
        idCardEt = (EditText) rootView.findViewById(R.id.ed_in_add_new_address_id_card);
        countryLayout = (RelativeLayout) rootView.findViewById(R.id.ll_in_add_new_address_country);
        locationLayout = (RelativeLayout) rootView.findViewById(R.id.ll_in_add_new_address_location);
        countryTv = (TextView) rootView.findViewById(R.id.add_new_address_country_tv);
        cityTv = (TextView) rootView.findViewById(R.id.add_new_address_city_tv);
        saveBtn = (Button) rootView.findViewById(R.id.add_new_address_save_btn);
        isDefaultCb = (CheckBox) rootView.findViewById(R.id.cb_in_add_new_address_is_defalt);
        isDefaultLayout = (LinearLayout) rootView.findViewById(R.id.add_new_address_is_default_layout);

        if (address != null) {
            mTitleText = getString(R.string.delivery_title_bar_edit_address);
            title_in_address.setTitleText(mTitleText);
            //titleBarView.setTitle(getResources().getString(R.string.delivery_title_bar_edit_address));
            consigneeEt.setText(address.getName());
            phoneEt.setText(address.getPhone());
            AssetFileManager afManager = AssetFileManager.getInstance();
            //String country = afManager.readFilePlus(address.getRegion(), AssetFileManager.COUNTRY_TYPE);
            String country = address.getCountryname();
            processAddressData(address);
            //String city = afManager.readFilePlus(address.getRegion(), AssetFileManager.EDIT_ADDRESS_TYPE);
            String city = mCurrentAddressBean.getSelectedString();
            countryTv.setText(country);
            cityTv.setText(city);
            detailAddressEt.setText(address.getStreet());
            postcodeEt.setText(address.getPostcode());
            companyEt.setText(address.getCompany());
            idCardEt.setText(address.getIdCard());
            isDefaultCb.setChecked(address.getIsDefault());
            title_in_address.getTv_in_title_right().setText(getString(R.string.preview_delete_msg));
            title_in_address.getTv_in_title_right().setOnClickListener(this);
            //titleBarView.getTextViewRight().setText(getString(R.string.delete));
            //titleBarView.getTextViewRight().setOnClickListener(this);
        } else {
            mTitleText = getString(R.string.delivery_title_bar_add_address);
            title_in_address.setTitleText(mTitleText);
            //titleBarView.setTitle(getResources().getString(R.string.delivery_title_bar_add_address));
        }

        if (deliveryAddAddress) {
            isDefaultCb.setChecked(true);
            isDefaultCb.setEnabled(false);
        }
        isDefaultLayout.setOnClickListener(this);
        countryLayout.setOnClickListener(this);
        locationLayout.setOnClickListener(this);
        //packageAddressTv.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        return rootView;
    }


    public void processAddressData(CsAddress.CustomerAddress customerAddress) {
        String regionName = customerAddress.getRegionname();
        String[] regionSplit = regionName.split("\\|");
        AddressBundleBean addressBundleBean = new AddressBundleBean();
        if (regionSplit.length >= 2) {
            String parentId = regionSplit[1];
            addressBundleBean.setDefaultProvinceId(parentId);
            String region = regionSplit[0];
            String[] split = region.split("~");
            if (split.length >= 2) {
                String city = split[0];
                String province = split[1];
                addressBundleBean.setCityString(city);
                addressBundleBean.setProvinceString(province);
                addressBundleBean.setSelectedString(city + " " + province);
            }
        } else {//说明只有省份
            addressBundleBean.setProvinceString(regionName);
            addressBundleBean.setSelectedString(regionName);
        }
        addressBundleBean.setRegionCode(customerAddress.getRegion());
        addressBundleBean.setCouSntryCode(customerAddress.getCountryCode());
        addressBundleBean.setRegionId(String.valueOf(customerAddress.getRegionId()));
        mCurrentAddressBean = addressBundleBean;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            /*case R.id.package_address_tv:
                finish();
                break;*/
            case R.id.ll_in_add_new_address_country:
                /*Intent intent = new Intent(this, AddressDialogActivity.class);
                if (TextUtils.isEmpty(countryTv.getText()) || TextUtils.equals(getResources().getString(R.string.delivery_address_default), countryTv.getText().toString())) {
                } else {
                    intent.putExtra("country", countryTv.getText().toString());
                }
                intent.putExtra("type", AddressDialogActivity.COUNTRY_TYPE);
                startActivityForResult(intent, 1);*/

                Intent intent = new Intent(this, ChooseRegionActivity.class);
                intent = AddressBundleBeanFactory.getInstance().getCountryTypeIntentBeanNew(intent, mCurrentCountryStr, mCountryCode,
                        getString(R.string.user_info_drtail_country_title), mTitleText);
                //startActivity(intent);
                startActivityForResult(intent, 1);
                break;
            case R.id.ll_in_add_new_address_location:
                toCityActivity();
                break;
            case R.id.add_new_address_is_default_layout:
                isDefaultCb.setChecked(!isDefaultCb.isChecked());
                break;
            case R.id.add_new_address_save_btn:

                String name = consigneeEt.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    toast(getResources().getString(R.string.delivery_toast_msg_consignee));
                    return;
                }
                String phone = phoneEt.getText().toString();
                /*if (!isMobileNO(phone)) {
                    toast(getResources().getString(R.string.delivery_toast_msg_phone));
                    return;
                }*/
                if (TextUtils.isEmpty(countryTv.getText())) {
                    toast(getResources().getString(R.string.delivery_toast_msg_country));
                    return;
                }
                if (TextUtils.isEmpty(mRegionCode)) {
                    //LogUtils.e("我是添加地址的返回值:" + region);
                    toast(getResources().getString(R.string.delivery_toast_msg_region));
                    return;
                }
                String street = detailAddressEt.getText().toString();
                if (TextUtils.isEmpty(street)) {
                    toast(getResources().getString(R.string.delivery_toast_msg_detail_address));
                    return;
                }
                String postCode = postcodeEt.getText().toString();
                /*if (TextUtils.isEmpty(postCode)) {
                    toast(getResources().getString(R.string.delivery_toast_msg_post_code));
                    return;
                }*/


                String company = companyEt.getText().toString();
                String idCard = idCardEt.getText().toString();
                CsAddress.CustomerAddress.Builder customerAddress = CsAddress.CustomerAddress.newBuilder();
                if (address != null) {
                    customerAddress.setAddressId(address.getAddressId());
                } else {
                    customerAddress.setAddressId(0);
                }
                customerAddress.setName(name);
                customerAddress.setRegion(mRegionCode);
                customerAddress.setStreet(street);
                customerAddress.setPostcode(postCode);
                customerAddress.setPhone(phone);
                customerAddress.setCompany(company);
                customerAddress.setIdCard(idCard);
                if (AddressManager.getInstance().mAddressesList.size() == 0 || AddressManager.getInstance().mAddressesList == null) {
                    customerAddress.setIsDefault(true);
                } else {
                    customerAddress.setIsDefault(isDefaultCb.isChecked());
                }
                if (address != null) {
                    editCustomerAddress(customerAddress.build());
                } else {
                    addCustomerAddress(customerAddress);
                }
                break;
            case R.id.tv_in_title_right:
                //Toast.makeText(AddNewAddressActivity.this, "我是删除", Toast.LENGTH_SHORT).show();
                //UserAddressManager.getInstance().deletAddressRequest(addressId);
                showDialog();
                break;
        }
    }

    public void toCityActivity() {
       /* Intent intent = new Intent(this, AddressDialogActivity.class);
        String trim = countryTv.getText().toString().trim();
        if (!TextUtils.isEmpty(cityTv.getText().toString()) && !TextUtils.equals(getResources().getString(R.string.delivery_address_default), cityTv.getText().toString())) {
            intent.putExtra("haveLocation", cityTv.getText().toString());
        }
        intent.putExtra("type", AddressDialogActivity.PROVINCE_AND_CITY_TYPE);
        //String regionCode = "";
        *//*if (address != null && TextUtils.isEmpty(region))
            region = address.getRegion().split("\\_")[0];*//*
        *//*String[] codeSplit = region.split("\\_");
        if (codeSplit.length == 2)
            regionCode = codeSplit[0];
        else regionCode = region;*//*
        if (TextUtils.isEmpty(region)) {
            if (address != null)
                region = address.getRegion().split("\\_")[0];
            else
                region = countryCode;
        }
        intent.putExtra(AddressDialogActivity.KEY_REGION_CODE, region);
        intent.putExtra("country", countryTv.getText().toString());
        startActivityForResult(intent, 2);*/

        Intent intent02 = new Intent(this, ChooseRegionActivity.class);
        String selectString = "";
        if (mCurrentAddressBean != null)
            mCurrentAddressBean.setRegionCode(mRegionCode);
        if (countryTv.getText().toString().trim().equals(getString(R.string.user_info_drtail_please_choose))) {
            mCurrentAddressBean = new AddressBundleBean();
            mCurrentAddressBean.setCouSntryCode(mCountryCode);
        }
        if (!(cityTv.getText().toString().trim().equals(getString(R.string.user_info_drtail_please_choose)))) {
            if (mCurrentAddressBean != null)
                selectString = mCurrentAddressBean.getProvinceString();
        }
        intent02 = AddressBundleBeanFactory.getInstance().getAddressBeanByBeanDefaultNew(intent02, mCurrentAddressBean,
                mCountryCode, mRegionID, mCurrentCountryStr != null ? mCurrentCountryStr : getString(R.string.user_info_drtail_location_title),
                mTitleText, selectString);
        startActivityForResult(intent02, 2);
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^(13[0,1,2,3,4,5,6,7,8,9]|15[0,1,2,3,5,6,7,8,9]|18[0,1,2,3,4,5,6,7,8,9]|17[0,1,2,3,4,5,6,7,8,9])\\d{8}$");
        Matcher m = p.matcher(mobiles);
        LogUtils.d(m.matches() + "---");
        return m.matches();
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            Bundle bundle = data.getBundleExtra("address");
            AddressBundleBean addressBundleBean = (AddressBundleBean) bundle.getSerializable("address");
            checkNotNull(addressBundleBean, "return country Data null");
            mCountryCode = addressBundleBean.getCouSntryCode();
            mCurrentCountryStr = addressBundleBean.getSelectedString();
            String c = countryTv.getText().toString();
            if (!c.equals(mCurrentCountryStr)) {
                cityTv.setText(getResources().getString(R.string.user_info_drtail_please_choose));
            }
            countryTv.setText(mCurrentCountryStr);
            toCityActivity();
        }
        if (requestCode == 2 && data != null) {

            Bundle bundle = data.getBundleExtra("address");
            AddressBundleBean addressBundleBean = (AddressBundleBean) bundle.getSerializable("address");
            checkNotNull(addressBundleBean, "return country Data null");
            mRegionID = addressBundleBean.getRegionId();
            mCurrentRegionStr = addressBundleBean.getSelectedString();
            mCountryCode = addressBundleBean.getCouSntryCode();
            cityTv.setText(mCurrentRegionStr);
            mRegionCode = addressBundleBean.getRegionCode();
            mCurrentAddressBean = addressBundleBean;

            /*String location = data.getStringExtra("location");
            if (location != null && !TextUtils.isEmpty(location) && !getResources().getString(R.string.user_info_drtail_please_choose).equals(location)) {
                cityTv.setText(location);
                *//*String[] split = location.split("\\ ");
                String s = split[1];*//*
                region = data.getStringExtra("rCode");
            } else {
                region = "";
                cityTv.setText(getResources().getString(R.string.user_info_drtail_please_choose));
            }*/
        }
    }


    public void editCustomerAddress(CsAddress.CustomerAddress address) {
        //showLoading(5000);
        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.uploading_string));
        CsAddress.EditCustomerAddressRequest.Builder builder = CsAddress.EditCustomerAddressRequest.newBuilder();
        builder.setAddress(address);
        builder.setBaseuser(AccountManager.getInstance().mBaseUserRequest);
        NetEngine.postRequest(builder, new INetEngineListener<CsAddress.EditCustomerAddressResponse>() {

            @Override
            public void onSuccess(CsAddress.EditCustomerAddressResponse response) {
                //closeLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissMissProgressDiaLog();
                    }
                });
                LogUtils.d(response.toString());
                finish();
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //closeLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeDiagLogAlertType(SweetAlertDialog.ERROR_TYPE, getString(R.string.string_for_send_requset_fail_02));
                        dissMissProgressDiaLog(1000);
                    }
                });

                LogUtils.d("ret=" + ret);
            }
        });
    }

    /**
     * 添加收货地址
     *
     * @param address
     */
    public void addCustomerAddress(CsAddress.CustomerAddress.Builder address) {
        //showLoading(5000);
        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.uploading_string));
        CsAddress.AddCustomerAddressRequest.Builder builder = CsAddress.AddCustomerAddressRequest.newBuilder();
        builder.setAddress(address.build());
        builder.setBaseuser(AccountManager.getInstance().mBaseUserRequest);
        NetEngine.postRequest(builder, new INetEngineListener<CsAddress.AddCustomerAddressResponse>() {

            @Override
            public void onSuccess(CsAddress.AddCustomerAddressResponse response) {
                //closeLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissMissProgressDiaLog();
                    }
                });

                LogUtils.d(response.toString());
                finish();
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                LogUtils.d("errMsg ret: " + ret);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeDiagLogAlertType(SweetAlertDialog.ERROR_TYPE, getString(R.string.string_for_send_requset_fail_02));
                        dissMissProgressDiaLog(1000);
                    }
                });

            }
        });
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        if (event.getType() == BusEvent.DELET_ADDRESS_REQUEST && !event.getBooleanParam())
            showProgressDiaLog(SweetAlertDialog.WARNING_TYPE, getString(R.string.string_for_send_requset_fail_02));
        /*SysApplication.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);*/
    }

    public void showDialog() {
        if (address.getIsDefault()) {
            /*showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.default_not_delete));
            dissMissProgressDiaLog(600);*/
            CustomToast.makeText(this, getString(R.string.default_not_delete), Toast.LENGTH_SHORT).show();
        } else {
            dialog = new CustomDialog.Builder(this);
            String dialogConfig = getString(R.string.my_gift_card_order_binding_dialog_config);
            String dialogcancle = getString(R.string.my_gift_card_order_binding_dialog_cancle);
            String message = getString(R.string.delet_address_dialog_message);
            dialog.setMessage(message);
            dialog.setPositiveButton(dialogConfig, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AddressManager.getInstance().deletAddressRequest(addressId);
                    dialog.dismiss();
                }
            });
            dialog.setNegativeButton(dialogcancle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.create().show();
        }

    }
}
