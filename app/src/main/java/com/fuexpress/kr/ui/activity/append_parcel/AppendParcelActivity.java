package com.fuexpress.kr.ui.activity.append_parcel;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.MbaseActivity;
import com.fuexpress.kr.bean.HelpSendParcelBean;
import com.fuexpress.kr.bean.IDinfoBean;
import com.fuexpress.kr.bean.ItemAppendBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.ui.activity.AddressManagerActivity;
import com.fuexpress.kr.ui.activity.PreviewParceldiseActivity;
import com.fuexpress.kr.ui.activity.append_item.AppendItemActivity;
import com.fuexpress.kr.ui.activity.append_item.JsonSerializer;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.ParcelItemView;
import com.fuexpress.kr.ui.view.imageselector.ImageSelectorActivity;
import com.fuexpress.kr.utils.FloatUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import fksproto.CsAddress;
import fksproto.CsBase;
import fksproto.CsParcel;

public class AppendParcelActivity extends MbaseActivity<ParcelAppendContract.Presenter, ParcelAppendContract.Model> implements ParcelAppendContract.View, View.OnFocusChangeListener {
    public static final String BEAN = "bean";
    public static final String WAREHOUSE_ID = "warehouse_id";
    public static final String DEFAULT_WEIGHT = "default_weight";

    @BindView(R.id.btn_save)
    Button mBtnSave;
    @BindView(R.id.tv_parcel_name)
    TextView mTvParcelName;
    @BindView(R.id.edt_declare_price)
    EditText mEdtDeclarePrice;
    @BindView(R.id.tv_price_type)
    TextView mTvPriceType;
    @BindView(R.id.edt_item_counts)
    EditText mEdtItemCounts;
    @BindView(R.id.edt_parcel_weight)
    EditText mEdtParcelWeight;
    @BindView(R.id.tv_weight_type)
    TextView mTvWeightType;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.ll_shippings_container)
    LinearLayout mLlShippingsContainer;
    @BindView(R.id.tv_parcel_shipping_fee)
    TextView mTvParcelShippingFee;
    @BindView(R.id.sc_root)
    ScrollView mScRoot;
    @BindView(R.id.ll_item_container)
    LinearLayout llItemContainer;
    @BindView(R.id.tv_id_card_name)
    TextView mTvIdCardName;
    @BindView(R.id.tv_apply_price)
    TextView tvApplyPrice;
    @BindView(R.id.tv_apply_price_notice)
    TextView tvApplyPriceNotice;
    @BindView(R.id.tv_transport_insurance)
    TextView tvTransportInsurance;
    @BindView(R.id.tv_transport_insurance_notice)
    TextView tvTransportInsuranceNotice;
    @BindView(R.id.rl_insurance)
    RelativeLayout rlInsurance;
    @BindView(R.id.tv_realy_price_notice)
    TextView tvRealyPriceNotice;
    @BindView(R.id.tv_product_price)
    TextView tvProductPrice;
    @BindView(R.id.tv_product_tarrif_notice)
    TextView tvProductTarrifNotice;
    @BindView(R.id.tv_product_tarrif_price)
    TextView tvProductTarrifPrice;
    @BindView(R.id.tv_shipping_tarrif_notice)
    TextView tvShippingTarrifNotice;
    @BindView(R.id.tv_shipping_tarrif_price)
    TextView tvShippingTarrifPrice;
    @BindView(R.id.rl_tariff)
    RelativeLayout rlTariff;
    @BindView(R.id.tl_to_input_id)
    RelativeLayout rlInputId;
    @BindView(R.id.btn_append_item)
    Button btnAddItem;
    private String mPath;
    private int mItmeCount;
    private Float mDeclarePrice;
    private List<String> mPathList;
    private List<String> mNewPathList;
    private boolean isSave;
    private float mPriceTotal;
    public int mStartShippingID;
    private JsonSerializer mJsonSerializer;
    private double mDefaultWeight;
    private boolean mNeedIdCard;
    private int mItemCount;
    private boolean mShowInputethod;

    @Override
    protected int getViewResId() {
        return R.layout.activity_append_parcel;
    }

    @Override
    public void initView() {
        initTitle(getString(R.string.home_fg_help_04), getString(R.string.String_parcle_add), getString(R.string.String_parcel_delete));
        hintIVRight();
        mPresenter = new AppendParcelPresent();
        mModel = new AppendParcelModel();
        Intent intent = getIntent();
        int warehouseID = intent.getIntExtra(WAREHOUSE_ID, 0);
        mDefaultWeight = intent.getDoubleExtra(DEFAULT_WEIGHT, 0);
        Bundle extra = intent.getBundleExtra(BEAN);
        if (extra != null) {
            HelpSendParcelBean item = (HelpSendParcelBean) extra.getSerializable(BEAN);
            warehouseID = item.getWareHouseID();
            mDefaultWeight = item.getDefaultWeight();
            mStartShippingID = item.getShippingmethodid();
            mPresenter.setParcelBean(item);
        } else {
            hintTVRight();
        }
        mPresenter.setDefaultWeight(mDefaultWeight);
        mPresenter.setmWarehouseid(warehouseID);
        mPresenter.setVM(this, mModel);
        initEvent();
        mTvPriceType.setText(AccountManager.getInstance().getCurrencyCode());
        mJsonSerializer = new JsonSerializer();
    }

    boolean isClicking;

    private static final int DECIMAL_DIGITS = 2;

    private void initEvent() {
        InputFilter lengthfilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                // 删除等特殊字符，直接返回
                if ("".equals(source.toString())) {
                    return null;
                }
                String dValue = dest.toString();
                String[] splitArray = dValue.split("\\.");
                if (splitArray.length > 1) {
                    String dotValue = splitArray[1];
                    int diff = dotValue.length() + 1 - DECIMAL_DIGITS;
                    if (diff > 0) {
                        return source.subSequence(start, end - diff);
                    }
                }
                return null;
            }
        };
//        mEdtItemInfo.addTextChangedListener(this);
//        mTvAddress.addTextChangedListener(this);
//        mEdtDeclarePrice.addTextChangedListener(this);
        mEdtDeclarePrice.setFilters(new InputFilter[]{lengthfilter});
//        mEdtParcelWeight.addTextChangedListener(this);
//        mEdtItemCounts.addTextChangedListener(this);
        mEdtParcelWeight.setOnFocusChangeListener(this);

        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = mRootView.getRootView().getHeight() - mRootView.getHeight();
                        if (heightDiff > 300) { // 说明键盘是弹出状态
//                            Log.v("TAG", "键盘弹出状态");
//                            reSetShippingMethod();
                            mShowInputethod = true;
                        } else {
                            if (mShowInputethod) {
                                checkParcelWeight();
                                getInfo(null, false);
                            }
                            mShowInputethod = false;
                        }
                    }
                });


        mScRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                checkParcelWeight();
                if (mShowInputethod) Hidekeyboard(mEdtParcelWeight);
                return false;
            }
        });

    }

    private void checkParcelWeight() {
        final String s = mEdtParcelWeight.getText().toString();
        float weight = "".equals(s) ? 0 : FloatUtils.vlaueOf(s);
        mPresenter.setWeight(weight);
        if (mEdtParcelWeight.hasFocus()) {
            mEdtParcelWeight.clearFocus();
            reSetShippingMethod();
            if (weight < mDefaultWeight) {
                final String msg = getString(R.string.package_deflaut_weight_tips, mDefaultWeight);
                showParcleDialog(msg, getString(R.string.package_confirm), "");
            }
        }
        mPresenter.updateShippingMethod();
    }


    @Override
    public void showShippingFee(float price) {
        if (price > 0) {
            mTvParcelShippingFee.setText(UIUtils.getCurrency(this, price));
//                    getString(R.string.String_price, price));
        } else {
            mTvParcelShippingFee.setText("");
        }

    }

    @Override
    public void switchButtonState(boolean clickable) {
        mBtnSave.setClickable(clickable);
    }

    @Override
    public void shoParcelName(String name) {
        mTvParcelName.setText(
                getString(R.string.String_parcle_name) + name);
    }


    private void hintShippingMethod() {
        for (int i = 0; i < mLlShippingsContainer.getChildCount(); i++) {
            View child = mLlShippingsContainer.getChildAt(i);
            RadioButton radioButton = (RadioButton) child.findViewById(R.id.rb_check_state);
            radioButton.setChecked(false);
            mTvParcelShippingFee.setText("");
        }
        mPresenter.setShippingMethodId(0);
        rlInputId.setVisibility(View.GONE);
        rlInsurance.setVisibility(View.GONE);
        rlTariff.setVisibility(View.GONE);
    }

    private void reSetShippingMethod() {
        for (int i = 0; i < mLlShippingsContainer.getChildCount(); i++) {
            View child = mLlShippingsContainer.getChildAt(i);
            RadioButton radioButton = (RadioButton) child.findViewById(R.id.rb_check_state);
            radioButton.setChecked(false);
            mTvParcelShippingFee.setText("");
        }
        rlInputId.setVisibility(View.GONE);
        rlInsurance.setVisibility(View.GONE);
        rlTariff.setVisibility(View.GONE);
        isSave = false;
        mStartShippingID = 0;
        mPresenter.setShippingMethodId(0);
        mPresenter.save2Db(null, false);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
       /* if (!hasFocus) {
            final String s = ((EditText) v).getText().toString();
            float weight = "".equals(s) ? 0 :  FloatUtils.vlaueOf(s);
            final float defaultWeight = mPresenter.getParcelBean().getDefaultWeight();
            if (weight < defaultWeight) {
                final String msg = getString(R.string.package_deflaut_weight_tips, defaultWeight);
                showParcleDialog(msg, getString(R.string.package_confirm), "");
            }
        }*/
    }


    class PhotoClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int postioin = (int) v.getTag();
            mPresenter.preViewPhoto(postioin);
        }
    }

    @Override
    public void appendPhotos(List<String> urls) {
        /*mLlPhoteContainer.removeAllViews();
        mLlPhoteContainer.addView(mImgAppendPhoto);

        for (int i = 0; i < urls.size(); i++) {
            String[] split = urls.get(i).split(",");
            if ("".equals(split[0]))
                continue;
            if (split.length > 1 && !"".equals(split[1])) {
                appendPhoto(split[1], 1);
            } else {
                String s = "file://" + split[0];
                appendPhoto(s, 0);
            }
        }
        if (urls.size() >= 4) {
            mImgAppendPhoto.setVisibility(View.GONE);
        } else {
            mImgAppendPhoto.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void showProductInfo(String info) {
        appendBeanList = jsonSerializer.deserializeList(info);
        if (appendBeanList != null)
            processItem();
    }


    @Override
    public void showDeclarePrice(float price) {
        if (price == 0) {
            mEdtDeclarePrice.setText(0 + "");
        } else {
            String currency = NumberFormate.getCurrency(this, AccountManager.getInstance().getCurrencyCode(), price);
            mEdtDeclarePrice.setText(currency);
        }
    }

    public float getDeclarePrice() {
        String s = mEdtDeclarePrice.getText().toString();
        return FloatUtils.vlaueOf(s);
    }

    @Override
    public void showPircelItemCount(int count) {
        if (count == 0) {
            mEdtItemCounts.setText(0 + "");
        } else {
            mEdtItemCounts.setText(count + "");
        }
    }

    public int getPircelItemCount() {
        String s = mEdtItemCounts.getText().toString();
        return Integer.valueOf(s);
    }

    @Override
    public void showPircelWeight(float weight) {
        if (weight == 0) {
            mEdtParcelWeight.setText("");
        } else {
            mEdtParcelWeight.setText(
                    getString(R.string.String_float_double, weight));
        }
    }

    public float getParcelWeight() {
        String s = mEdtParcelWeight.getText().toString();
        return FloatUtils.vlaueOf(s);
    }

    @Override
    public void showAddress(CsParcel.AddressList address) {
        String s2 = null;
        if (address != null) {
            String s = address.getName() + "  " + address.getTelephone() + "  " + address.getPostcode() + "\n";
            s2 = s + address.getStreet() + "," + address.getFullregionname();
        }
        showAddress(s2);
        setIdInfo(address.getIdcardFrontImage(), address.getIdcardBackImage(), address.getIdcard());
    }

    @Override
    public void showAddress(String address) {
        if (address == null) address = "";
        if (TextUtils.isEmpty(address.replaceAll(",", "").replace("\n", ""))) {
            mTvAddress.setText(getString(R.string.String_parcel_item_address_tips));
        } else {
            mTvAddress.setText(address);
            mPresenter.getParcelBean().setCustomeraddress(address);
        }
    }

    @Override
    public void showShippingList(List<CsBase.ShippingMethod> shippingMethods) {
        TransportClickListener listener = new TransportClickListener();
        mLlShippingsContainer.removeAllViews();
        if (shippingMethods != null) {
            for (int i = 0; i < shippingMethods.size(); i++) {
                ViewGroup inflate = (ViewGroup) View.inflate(this, R.layout.view_item_transport_type, null);
                inflate.setOnClickListener(listener);
                TextView child2 = (TextView) inflate.findViewById(R.id.tv_transport_des);
                TextView child3 = (TextView) inflate.findViewById(R.id.tv_transport_detail);
                CsBase.ShippingMethod shippingMethod = shippingMethods.get(i);
                child2.setText(shippingMethod.getTitle());
                String sDetail = shippingMethod.getInfo();
                if (!shippingMethod.getAttention().equals("")) {
                    sDetail += "\n" + getString(R.string.package_attention) + shippingMethod.getAttention();
                }
                child3.setText(sDetail);
                inflate.setTag(shippingMethod);
                mLlShippingsContainer.addView(inflate);
                if (i == 0)
                    inflate.callOnClick();
            }
        }
    }

    @Override
    public void showShippingMethods(List<CsParcel.MerchantParcelShippingMethodList> methods) {
        TransportClickListener listener = new TransportClickListener();
        mLlShippingsContainer.removeAllViews();
        if (methods != null) {
            for (int i = 0; i < methods.size(); i++) {
                ViewGroup inflate = (ViewGroup) View.inflate(this, R.layout.view_item_transport_type, null);
                inflate.setOnClickListener(listener);
                TextView child2 = (TextView) inflate.findViewById(R.id.tv_transport_des);
                TextView child3 = (TextView) inflate.findViewById(R.id.tv_transport_detail);
                CsParcel.MerchantParcelShippingMethodList shippingMethod = methods.get(i);
                String shippingmethodstring = shippingMethod.getShippingmethodstring();
                String[] split = shippingmethodstring.split("\n");
                String title = "";
                String content = "";
                if (split.length >= 1) {
                    title = split[0];
                    if (split.length >= 2) {
                        for (int j = 1; j < split.length; j++) {
                            if (j == split.length - 1) {
                                content += split[j];
                            } else {
                                content += split[j] + "\n";
                            }
                        }
                    }
                }
                child2.setText(title);
                child3.setText(content);
                inflate.setTag(shippingMethod);
                mLlShippingsContainer.addView(inflate);
            }
        }
    }


    private void add(List<String> infos, String str) {
        if (infos != null)
            infos.add(str);
    }

    @Override
    public boolean getInfo(List<String> infos, boolean toast) {
        return getInfo(infos, toast, true);
    }

    @Override
    public boolean getInfo(List<String> infos, boolean toast, boolean selectShipping) {
        if (infos != null)
            infos.removeAll(infos);
        String price = mEdtDeclarePrice.getText().toString();
        String counts = mEdtItemCounts.getText().toString();
        String weight = mEdtParcelWeight.getText().toString();
        String address = mTvAddress.getText().toString();
        int state = 0;
        if (TextUtils.isEmpty(address)) {
            state = 5;
        }
        add(infos, address);

        if (TextUtils.isEmpty(weight)) {
            state = 4;
        }
        add(infos, weight);

        if (TextUtils.isEmpty(counts)) {
            state = 3;
        }
        add(infos, counts);

        if (TextUtils.isEmpty(price)) {
            state = 2;
        }
        add(infos, price);

        if (appendBeanList == null || appendBeanList.size() <= 0) {
            state = 1;
        }

        if (toast) {
            switch (state) {
                case 1:
                    CustomToast.makeText(this, getString(R.string.String_parcel_item_info_tips), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    CustomToast.makeText(this, getString(R.string.String_parcel_declare_price_tips), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    CustomToast.makeText(this, getString(R.string.String_parcel_item_count_tips), Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    CustomToast.makeText(this, getString(R.string.String_parcel_weight_tips), Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    CustomToast.makeText(this, getString(R.string.String_parcel_item_address_tips), Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        /*物流方式检查*/
        if (selectShipping) {
            if (mPresenter.getShippingMethodId() == 0) {
                state = 6;
                if (toast)
                    CustomToast.makeText(this, getString(R.string.package_plase_select_shipping_method), Toast.LENGTH_SHORT).show();
            }
        }


        /*身份信息检查*/
        if (selectShipping) {
            IDinfoBean idInfo = mPresenter.getIdInfo();
            if (idInfo != null) {
                boolean b = TextUtils.isEmpty(idInfo.getIdNumber()) && TextUtils.isEmpty(idInfo.getServerIDNumber());
                if (mNeedIdCard && b) {
                    state = 7;
                    if (toast)
                        CustomToast.makeText(this, getString(R.string.id_card_notice), Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (state == 0) {
            mBtnSave.setEnabled(true);
//            mTvParcelShippingFee.setText("");
            return true;
        } else {
            mBtnSave.setEnabled(false);
            return false;
        }
    }

    private void save() {
        if (isSave) return;
        isSave = true;
        mPresenter.setmSave(true);
        ArrayList<String> infos = new ArrayList<>();
        if (!getInfo(infos, true)) {
            isSave = false;
            return;
        }
        /*if (mPresenter.getShippingMethodId() == 0) {
            CustomToast.makeText(this, getString(R.string.package_plase_select_shipping_method), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mNeedIdCard) {
            IDinfoBean idInfo = mPresenter.getIdInfo();
            boolean b = TextUtils.isEmpty(idInfo.getIdNumber()) && TextUtils.isEmpty(idInfo.getServerIDNumber());
            if (b) {
                CustomToast.makeText(this, getString(R.string.id_card_notice), Toast.LENGTH_SHORT).show();
                return;
            }
        }*/
        processItem(true);
        mPresenter.sava();
    }


    @Override
    protected void close() {
        onBackPressed();
    }


    @Override
    public void selsetShippingMethod(int id, float shippingDutFee, Float shippingFee) {
        for (int i = 0; i < mLlShippingsContainer.getChildCount(); i++) {
            View child = mLlShippingsContainer.getChildAt(i);
            RadioButton radioButton = (RadioButton) child.findViewById(R.id.rb_check_state);
            CsParcel.MerchantParcelShippingMethodList shippingMethod = (CsParcel.MerchantParcelShippingMethodList) child.getTag();
            if (shippingMethod.getParcelshippingmethodid() == id) {
                radioButton.setChecked(true);
                showInsurance(shippingMethod, shippingDutFee, shippingFee);
                showIdCard(shippingMethod.getIsneedidcard() == 1);
            } else {
                radioButton.setChecked(false);
            }
        }
        getInfo(null, false);
        mRootView.postInvalidate();
    }

    private void showIdCard(boolean b) {
//        b = true;// TODO: 2016/12/15 正式注释掉
        mNeedIdCard = b;
        rlInputId.setVisibility(b ? View.VISIBLE : View.GONE);
        IDinfoBean idInfo = mPresenter.getIdInfo();
        idInfo.setNeedId(b);
        String idNumber = idInfo.getIdNumber();
        if (!TextUtils.isEmpty(idNumber)) {
            mTvIdCardName.setText(idNumber);
        } else {
            mTvIdCardName.setText(idInfo.getServerIDNumber() != null ? idInfo.getServerIDNumber() : "");
        }
    }


    class TransportClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            isSave = false;
            if (!getInfo(null, true, false))
                return;
            float cPrice = 0;
            for (int i = 0; i < mLlShippingsContainer.getChildCount(); i++) {
                View child = mLlShippingsContainer.getChildAt(i);
                RadioButton radioButton = (RadioButton) child.findViewById(R.id.rb_check_state);
                CsParcel.MerchantParcelShippingMethodList shippingMethod = (CsParcel.MerchantParcelShippingMethodList) v.getTag();

                String price = mEdtDeclarePrice.getText().toString().replaceAll(",", "");
                if (!TextUtils.isEmpty(price)) {
                    cPrice = FloatUtils.vlaueOf(price);
                }
                if (cPrice > 99999999) {
                    CustomToast.makeText(AppendParcelActivity.this, getString(R.string.package_shipping_warn),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cPrice > shippingMethod.getDeclaredvalue() && shippingMethod.getBondedalert() == 1) {
                    CustomToast.makeText(AppendParcelActivity.this,
                            getString(R.string.package_shipping_warn, UIUtils.getCurrency(AppendParcelActivity.this, shippingMethod.getDeclaredvalue())),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (v == child) {
                    radioButton.setChecked(true);
                    mPresenter.selectShippingMethod(shippingMethod);
                } else {
                    radioButton.setChecked(false);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClicking = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ALBUM_GET_BITMAP_REQUEST_CODE && resultCode == 100) {
            String image = data.getStringExtra("image");
            Uri data1 = Uri.parse(image);
            mPath = data1.getPath();
            mPresenter.addPhotoFile(mPath);
        }
        if (requestCode == PreviewParceldiseActivity.REQUEST_CODE_BACK_IMG_LIST) {
            if (data != null) {
                List<String> backList = (List<String>) data.getExtras().getSerializable("backImgList");
                mPresenter.delectPhoto(backList);
            }
        }
        if (requestCode == Constants.ADDRESS_REQUEST_CODE && resultCode == Constants.ADDRESS_REQUEST_CODE) {
            if (data != null) {
              /*  CsParcel.MerchantAddressList address = (CsParcel.MerchantAddressList) data.getExtras().get("address");
                mPresenter.setAddress(address);
                isClicking = false;
                mTvParcelShippingFee.setText("");*/
            }
        }
        if (requestCode == Constants.ADDRESS_REQUEST_CODE && resultCode == AddressManagerActivity.AddressresultCode) {
            if (data != null) {
                String topString = data.getStringExtra(AddressManagerActivity.topText1);
                String addressString = data.getStringExtra(AddressManagerActivity.addressText);
                int addressID = data.getIntExtra(AddressManagerActivity.addressId, 0);
                CsAddress.CustomerAddress customerAddress = (CsAddress.CustomerAddress) data.getExtras().getSerializable("address");
                setIdInfo(customerAddress.getIdcardfrontimage(), customerAddress.getIdcardbackimage(), customerAddress.getIdCard());
                mPresenter.setAddress(topString, addressString, addressID);
                isClicking = false;
//                mTvParcelShippingFee.setText("");
                hintShippingMethod();
            }
        }


        if (requestCode == 1001 || resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
            if (data != null) {
                mNewPathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                if (mNewPathList != null) {
                    mPathList.clear();
                    mPathList.addAll(mNewPathList);
                    appendPhotos(mPathList);
                    mPresenter.setPhotosPath(mPathList);
                    mPresenter.upPhoto();
                }
            }
        }

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                List<ItemAppendBean> temp = (List<ItemAppendBean>) data.getSerializableExtra(AppendItemActivity.RESULT_ITEMS);
                if (appendBeanList == null || !appendBeanList.equals(temp)) {
                    reSetShippingMethod();
                    appendBeanList = temp;
                    processItem(true);
//                    CustomToast.makeText(this, "请注意选择物流方式后点击保存！", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            String idCardFront = data.getStringExtra(IdCardActivity.ID_CARD_FRONT);
            String idCardBack = data.getStringExtra(IdCardActivity.ID_CARD_BACK);
            String idCardNumber = data.getStringExtra(IdCardActivity.ID_CARD_NUMBER);
            setIdInfo(idCardFront, idCardBack, idCardNumber);
            getInfo(null, false);
        }
    }

    private void setIdInfo(String idCardFront, String idCardBack, String idCardNumber) {
        mTvIdCardName.setText(idCardNumber);
        IDinfoBean idInfo = mPresenter.getIdInfo();
        idInfo.setIdNumber(idCardNumber);
        idInfo.setUrlBack(idCardBack);
        idInfo.setUrlFront(idCardFront);
    }

    JsonSerializer jsonSerializer = new JsonSerializer();

    private void processItem() {
        processItem(false);
    }

    public int getItemCount() {
        return mItemCount;
    }

    private void processItem(boolean save) {
        mItemCount = appendBeanList.size();
        String serialize = jsonSerializer.serialize(appendBeanList);
        llItemContainer.removeAllViews();
        String imgs = "";
        mPriceTotal = 0;
        int numTotal = 0;
        if (appendBeanList.size() >= 4) {
            btnAddItem.setEnabled(false);
        } else {
            btnAddItem.setEnabled(true);
        }

        for (int i = 0; i < appendBeanList.size(); i++) {
            ItemAppendBean bean = appendBeanList.get(i);
            mPriceTotal += bean.getPrice() * bean.getNum();
            numTotal += bean.getNum();
            ParcelItemView parcelItemView = new ParcelItemView(this, 0);
            if (bean.getImgs() != null && bean.getImgs().size() > 0) {
                String url = bean.getImgs().get(0);
                imgs += url + " ";
                parcelItemView.setIvIcon(url);
            }
            parcelItemView.setCount("×" + bean.getNum());
            parcelItemView.setTitle(bean.getInfo());
            parcelItemView.setPrice(UIUtils.getCurrency(this, bean.getPrice()));
            llItemContainer.addView(parcelItemView);
            parcelItemView.setTag(i);
            parcelItemView.setOnClickListener(itemClickListener);
        }
//        showDeclarePrice(mPriceTotal);
//        showPircelItemCount(numTotal);
        mPresenter.setDeclarePrice(mPriceTotal);
        mPresenter.setItemCount(numTotal);
        if (save)
            mPresenter.saveItems(serialize, imgs);
        getInfo(null, false);
    }

    ItemClickListener itemClickListener = new ItemClickListener();

    class ItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Intent intent = new Intent(AppendParcelActivity.this, AppendItemActivity.class);
            intent.putExtra(AppendItemActivity.ITEM_LIST, (Serializable) appendBeanList);
            intent.putExtra(AppendItemActivity.PARCEL_ID, mPresenter.getParcelBean().getParcelid());
            intent.putExtra(AppendItemActivity.TYPE, false);
            intent.putExtra(AppendItemActivity.POSITION, position);
            overridePendingTransition(R.anim.activity_translate_x_in, R.anim.activity_translate_x_out);
            startActivityForResult(intent, 0);
        }
    }


    List<ItemAppendBean> appendBeanList;

    @Override
    public void showParcleDialog(String messsage, final String positiveString, final String cancleText) {
        isSave = false;
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.create().setCanceledOnTouchOutside(false);
        /*final String[] split1 = messsage.split("\\；");
        String msg = "";
        final boolean weightTip = messsage.contains("重量");
        final boolean countTip = messsage.contains("件");
        final boolean declareTip = messsage.contains("申报");
        if (weightTip) {
            String[] split = messsage.split("\\：");
            if (split.length > 1) {
                msg = split[1];
            } else {
                msg = messsage;
            }
        } else {
            msg = messsage;
        }
        if (countTip) {
            final int start = messsage.indexOf("数量为") + 2;
            final int end = messsage.indexOf("件");
            mItmeCount = Integer.valueOf(messsage.substring(start + 1, end));
        }
        if (declareTip) {
            final int start = messsage.indexOf("价值为") + 2;
            final int end = messsage.indexOf("元");
            mDeclarePrice =  FloatUtils.vlaueOf(messsage.substring(start + 1, end));
        }*/

        builder.setMessage(messsage);
        builder.setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mPresenter.setWeight(mPresenter.getParcelBean().getDefaultWeight());
            }
        });
        if (!"".equals(cancleText)) {
            builder.setNegativeButton(cancleText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        CustomDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void showInsurance(CsParcel.MerchantParcelShippingMethodList shippingMethod, float shippingDutyFee, float shippingFee) {
        float pureShippingFee = shippingFee;
        boolean needIns = shippingMethod.getValuealert() == 1;
        int declaredvalueMax = shippingMethod.getDeclaredvalue();
        float premiumrate = shippingMethod.getPremiumrate();
        float premumFee = premiumrate * mPriceTotal / 100;
        showInsurance(needIns, declaredvalueMax, mPriceTotal, premiumrate, premumFee);
        if (needIns) pureShippingFee -= premumFee;

        boolean needDuty = shippingMethod.getIsneedduty() == 1;
        float parcelsubtotalquota = shippingMethod.getParcelsubtotalquota();
        float dutyrate = shippingMethod.getDutyrate();
        float dutyFee = dutyrate * mPriceTotal / 100;
        showDuty(needDuty, parcelsubtotalquota, mPriceTotal, dutyrate, dutyFee, shippingDutyFee);
        if (needDuty) pureShippingFee -= dutyFee;
        pureShippingFee -= shippingDutyFee;
        mPresenter.getParcelBean().setPureShippingFee(pureShippingFee);
//        mPresenter.save2Db(null, false);
//        在这里算纯运费吧。

    }


    public void showInsurance(boolean show, float maxIns, float realIns, float rate, float fee) {
        rlInsurance.setVisibility(show ? View.VISIBLE : View.GONE);
        tvApplyPriceNotice.setText(getString(R.string.package_apply_price_detail, UIUtils.getCurrency(this, maxIns)));
        tvApplyPrice.setText(UIUtils.getCurrency(this, realIns));
        tvTransportInsuranceNotice.setText(getString(R.string.package_shipping_insurance_detail, rate));
        tvTransportInsurance.setText(UIUtils.getCurrency(this, fee));
    }

    public void showDuty(boolean show, float maxPrice, float realPrice, float rate, float productfee, float shippingFee) {
        rlTariff.setVisibility(show ? View.VISIBLE : View.GONE);
        tvRealyPriceNotice.setText(getString(R.string.package_product_realy_price_note, UIUtils.getCurrency(this, maxPrice)));
        tvProductPrice.setText(UIUtils.getCurrency(this, realPrice));
        tvProductTarrifNotice.setText(getString(R.string.package_product_tarrif_note, rate));
        tvProductTarrifPrice.setText(UIUtils.getCurrency(this, productfee));
        tvShippingTarrifNotice.setText(getString(R.string.package_shipping_tarrif_note, rate));
        tvShippingTarrifPrice.setText(UIUtils.getCurrency(this, shippingFee));
    }

    @OnClick({R.id.tv_address, R.id.btn_save, R.id.tv_parcel_name, R.id.btn_append_item, R.id.tl_to_input_id})
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tv_address:
                mPresenter.selectAddress();
                break;
            case R.id.btn_save:
                if (mEdtParcelWeight.hasFocus()) {
                    checkParcelWeight();
                    return;
                }
                save();
                break;
            case R.id.img_append_photo:
                MPermissions.requestPermissions(this, 6, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.tv_parcel_name:
                break;
            case R.id.btn_append_item:
                toAppendItem();
                break;
            case R.id.tl_to_input_id:
                intent = new Intent(this, IdCardActivity.class);
                String s = jsonSerializer.serializeIDinfo(mPresenter.getIdInfo());
                intent.putExtra(IdCardActivity.ID_CARD_BEAN, s);
                startActivityForResult(intent, 11);
                this.overridePendingTransition(R.anim.activity_translate_x_in, R.anim.activity_translate_x_out);
                break;
        }
    }

    private void toAppendItem() {
        if (appendBeanList != null && appendBeanList.size() >= 4) {
            CustomToast.makeText(this, getString(R.string.parcel_apped_item_max), Toast.LENGTH_SHORT).show();
            return;
        }


        Intent intent;
        intent = new Intent(this, AppendItemActivity.class);
        intent.putExtra(AppendItemActivity.ITEM_LIST, (Serializable) appendBeanList);
        intent.putExtra(AppendItemActivity.PARCEL_ID, mPresenter.getParcelBean().getParcelid());
        intent.putExtra(AppendItemActivity.TYPE, true);
        startActivityForResult(intent, 0);
        this.overridePendingTransition(R.anim.activity_translate_x_in, R.anim.activity_translate_x_out);
    }

    /*适配6.0 权限检查*/
    @PermissionGrant(6)
    public void requestContactSuccess() {
        mPathList = mPresenter.getPhotosPath();
        UIUtils.startImageSelectorForAddItem(1001, (ArrayList) mPathList, this, 4);
    }

    @PermissionDenied(6)
    public void requestContactFailed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void showDeleteDiglog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(getString(R.string.string_confirm_del_parcel));
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.delectParcel();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onRightTVClick() {
//        mPresenter.delectParcel();
        showDeleteDiglog();
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        List<String> imgs = null;
        if (event.getType() == BusEvent.UP_LOAD_IMAGE_COMPLETE2) {
            imgs = (List<String>) event.getParam();
        }
        if (event.getType() == BusEvent.RETURN_CURRENT_UP_LOAD_IMAGE_LIST_PROGRESS) {
            int intParam = event.getIntParam();
            if (intParam < 100) {
                EventBus.getDefault().post(new BusEvent(BusEvent.SHOW_UP_LOAD_IMAGE_LIST_PROGRESS, null));
            } else {
                save();
            }
        }
    }


  /*  int pressedCount;

    Handler mHandler = new Handler();*/


  /*  @Override
    public void onBackPressed() {
        if (mPresenter.getParcelBean().getShippingmethodid() == 0 && pressedCount == 0) {
            pressedCount++;
            CustomToast.makeText(this, "没有选择物流方式哦,强制退出会隐藏此包裹。", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pressedCount = 0;
                }
            }, 3000);
        } else {
            super.onBackPressed();
        }
    }*/

}