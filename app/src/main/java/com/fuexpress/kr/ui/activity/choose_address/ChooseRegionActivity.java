package com.fuexpress.kr.ui.activity.choose_address;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.ui.view.TitleBarView;

import java.util.List;

import fksproto.CsAddress;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChooseRegionActivity extends BaseActivity implements RefreshListView.OnRefreshListener {

    private static final int SUB_CODE = 1002;

    private View mRootView;
    private RefreshListView mRlv_address_list;
    private ChooseAddressAdapter mChooseAddressAdapter;
    private AddressBundleBean mAddressBundleBean;
    private View mHeadView;
    private boolean mHasMore;
    boolean isCountryType = false;
    public static final String KEY_COUNTRY = "COUNTRY";
    private AddressBundleBean mTempAddressBundleBean;
    private TitleBarView mTitleBarView;


    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_choose_region, null);
        return mRootView;
    }

    @Override
    public void initView() {
        mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.title_choose_subregion);
        mHeadView = View.inflate(this, R.layout.head_for_new_address, null);
        mRlv_address_list = (RefreshListView) mRootView.findViewById(R.id.rlv_address_list);
        mRlv_address_list.setOnRefreshListener(this);
        mRlv_address_list.setOnItemClickListener(this);
        mRlv_address_list.setHeaderViewHide();
        initData();
    }

    private void initData() {
        mAddressBundleBean = (AddressBundleBean) getIntent().getExtras().getSerializable("address");
        checkNotNull(mAddressBundleBean, "address data is not null!");
        mTitleBarView.setTitleText(mAddressBundleBean.getTitle());
        TextView textViewLeft = mTitleBarView.getmTv_in_title_back_tv();
        mTitleBarView.getIv_in_title_back().setOnClickListener(this);
        textViewLeft.setText(mAddressBundleBean.getBackTitle());
        textViewLeft.setOnClickListener(this);
        isCountryType = mAddressBundleBean.isCountryType();
        if (isCountryType)
            RegionDataManager.getInstance().getCountryListData(mAddressBundleBean);
        else
            RegionDataManager.getInstance().getRegionListData(mAddressBundleBean);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                // TODO: 2017/4/20 根据情况来判断是关闭页面还是切换
                finishAndSetResult(null);
                break;
        }
    }

    private void finishAndSetResult(AddressBundleBean addressBundleBean) {
        if (addressBundleBean != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            if (!addressBundleBean.isCountryType()) {
                String selectString;
                String cityString = addressBundleBean.getCityString();
                if (cityString != null)
                    selectString = cityString + " " + addressBundleBean.getProvinceString();
                else
                    selectString = addressBundleBean.getProvinceString();
                addressBundleBean.setSelectedString(selectString);
            }
            bundle.putSerializable("address", addressBundleBean);
            intent.putExtra("address", bundle);
            setResult(1024, intent);
            finish();
        } else
            finish();

    }

    private void jumpToSubRegionActivity(AddressBundleBean addressBundleBean) {
        Intent intent = new Intent(this, SubRegionActivity.class);
        Bundle bundle = new Bundle();
        addressBundleBean.setTitle(addressBundleBean.getProvinceString());
        bundle.putSerializable("address", addressBundleBean);
        intent.putExtras(bundle);
        startActivityForResult(intent, SUB_CODE);
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        mRlv_address_list.stopRefresh();
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (mHasMore) {
            int page = mAddressBundleBean.getPage();
            mAddressBundleBean.setPage(++page);
            if (isCountryType)
                RegionDataManager.getInstance().getCountryListData(mAddressBundleBean);
            else {
                if (mAddressBundleBean.getCouSntryCode() != null && !TextUtils.isEmpty(mAddressBundleBean.getCouSntryCode()))
                    RegionDataManager.getInstance().getRegionListData(mAddressBundleBean);
            }
        }

    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_REGION_COMPLETE:
                if (event.getBooleanParam()) {
                    mRlv_address_list.stopRefresh();
                    mRlv_address_list.stopLoadMore(true);
                    if (isCountryType) {
                        if (mChooseAddressAdapter == null) {
                            mChooseAddressAdapter = new ChooseAddressAdapter(this, ChooseAddressAdapter.KEY_COUNTRY_TYPE,
                                    RegionDataManager.getInstance().getCountryInfoListList(), null);
                            mRlv_address_list.setAdapter(mChooseAddressAdapter);
                            mRlv_address_list.addHeaderView(mHeadView);
                        } else {
                            mChooseAddressAdapter.reLoadCountryDataList(RegionDataManager.getInstance().getCountryInfoListList());
                            mChooseAddressAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (mChooseAddressAdapter == null) {
                            mChooseAddressAdapter = new ChooseAddressAdapter(this, ChooseAddressAdapter.KEY_REGION_DATA,
                                    null, RegionDataManager.getInstance().getRegionInfoListList());
                            mRlv_address_list.setAdapter(mChooseAddressAdapter);
                            mRlv_address_list.addHeaderView(mHeadView);
                        } else {
                            mChooseAddressAdapter.reLoadRegionDataList(RegionDataManager.getInstance().getRegionInfoListList());
                            mChooseAddressAdapter.notifyDataSetChanged();
                        }
                    }
                    AddressResponBean responBean = (AddressResponBean) event.getParam();
                    mHasMore = responBean.isHasMore();
                    mRlv_address_list.setHasLoadMore(mHasMore);
                    setSelectedString(responBean.getCurrentRegionName());
                } else
                    CustomToast.makeText(this, getString(R.string.string_for_send_requset_fail_02), Toast.LENGTH_SHORT).show();
                break;
            case BusEvent.GET_SUB_REGION_COMPLETE:
                if (event.getBooleanParam()) {
                    if (event.getStrParam().equals(this.getClass().getName()))
                        if (event.getBooleanParam02())
                            jumpToSubRegionActivity(mTempAddressBundleBean);
                        else {
                            finishAndSetResult(mTempAddressBundleBean);
                        }
                } else
                    CustomToast.makeText(this, getString(R.string.string_for_send_requset_fail_02), Toast.LENGTH_SHORT).show();
                break;
        }
    }


    public void setSelectedString(String selectedAddress) {
        if (TextUtils.isEmpty(selectedAddress)) {
            if (isCountryType) {
                if (mAddressBundleBean.getCouSntryCode() != null)
                    selectedAddress = mAddressBundleBean.getSelectedString();
            } else {
                if (mAddressBundleBean.getRegionId() != null)
                    selectedAddress = mAddressBundleBean.getProvinceString();
            }

        }
        TextView tv_selected_string = (TextView) mHeadView.findViewById(R.id.tv_selected_string);
        tv_selected_string.setText(selectedAddress);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AddressBundleBean addressBundleBean = new AddressBundleBean();
        int index;
        if (position == 1) {
            index = position;
            if (!isCountryType) {
                addressBundleBean.setRegionId(mAddressBundleBean.getRegionId());
                addressBundleBean.setParentId(mAddressBundleBean.getDefaultProvinceId() == null ? mAddressBundleBean.getRegionId() : mAddressBundleBean.getDefaultProvinceId());
                addressBundleBean.setLocaleCode(mAddressBundleBean.getLocaleCode());
                addressBundleBean.setSelectedString(mAddressBundleBean.getSelectedString());
                addressBundleBean.setProvinceString(mAddressBundleBean.getProvinceString());
                addressBundleBean.setCityString(mAddressBundleBean.getCityString());
                addressBundleBean.setRegionCode(mAddressBundleBean.getRegionCode());
                addressBundleBean.setCouSntryCode(mAddressBundleBean.getCouSntryCode());
                addressBundleBean.setBackTitle("所在地");
                RegionDataManager.getInstance().getRegionListByRegionData(addressBundleBean, this.getClass().getName());
            } else
                finishAndSetResult(mAddressBundleBean);
        } else {
            index = position - 2;
            if (isCountryType) {
                List<CsAddress.DirectoryCountryInfo> countryInfoListList = RegionDataManager.getInstance().getCountryInfoListList();
                CsAddress.DirectoryCountryInfo directoryCountryInfo = countryInfoListList.get(index);
                addressBundleBean.setIsCountryType(true);
                addressBundleBean.setCouSntryCode(directoryCountryInfo.getDirectoryCountryCode());
                addressBundleBean.setSelectedString(directoryCountryInfo.getDirectoryCountryName());
                finishAndSetResult(addressBundleBean);
            } else {
                List<CsAddress.DirectoryRegionInfo> regionInfoListList = RegionDataManager.getInstance().getRegionInfoListList();
                CsAddress.DirectoryRegionInfo directoryRegionInfo = regionInfoListList.get(index);
                addressBundleBean.setRegionId(mAddressBundleBean.getRegionId());
                addressBundleBean.setRegionCode(directoryRegionInfo.getDirectoryCountryRegionCode());
                addressBundleBean.setParentId(directoryRegionInfo.getDirectoryCountryRegionId());
                addressBundleBean.setLocaleCode(mAddressBundleBean.getLocaleCode());
                addressBundleBean.setProvinceString(directoryRegionInfo.getDirectoryCountryRegionName());
                addressBundleBean.setBackTitle("所在地");
                addressBundleBean.setCouSntryCode(mAddressBundleBean.getCouSntryCode());
                RegionDataManager.getInstance().getRegionListByRegionData(addressBundleBean, this.getClass().getName());
            }
        }
        mTempAddressBundleBean = addressBundleBean;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == SUB_CODE) {
                AddressBundleBean addressBundleBean = (AddressBundleBean) data.getExtras().getSerializable("address");
                finishAndSetResult(addressBundleBean);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
