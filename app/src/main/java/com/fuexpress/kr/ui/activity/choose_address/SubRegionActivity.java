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

public class SubRegionActivity extends BaseActivity implements RefreshListView.OnRefreshListener {

    private View mRootView;
    private RefreshListView mRlv_address_list;
    private ChooseAddressAdapter mChooseAddressAdapter;
    private AddressBundleBean mAddressBundleBean;
    private View mHeadView;
    private boolean mHasMore;
    private TitleBarView mTitleBarView;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_sub_region, null);
        return mRootView;
    }

    @Override
    public void initView() {
        mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.title_sub_region);
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
        mTitleBarView.getIv_in_title_back().setOnClickListener(this);
        TextView textViewLeft = mTitleBarView.getmTv_in_title_back_tv();
        textViewLeft.setText(mAddressBundleBean.getBackTitle());
        textViewLeft.setOnClickListener(this);
        RegionDataManager.getInstance().getRegionListByRegionData(mAddressBundleBean, this.getClass().getName());
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
            RegionDataManager.getInstance().getRegionListByRegionData(mAddressBundleBean, this.getClass().getName());
        }
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
            bundle.putSerializable("address", addressBundleBean);
            intent.putExtras(bundle);
            setResult(1024, intent);
            finish();
        } else
            finish();
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_SUB_REGION_COMPLETE:
                if (event.getStrParam().equals(this.getClass().getName()))
                    if (event.getBooleanParam()) {
                        mRlv_address_list.stopRefresh();
                        mRlv_address_list.stopLoadMore(true);
                        if (mChooseAddressAdapter == null) {
                            mChooseAddressAdapter = new ChooseAddressAdapter(this, ChooseAddressAdapter.KEY_REGION_DATA,
                                    null, RegionDataManager.getInstance().getSubRegionInfoListList());
                            mRlv_address_list.setAdapter(mChooseAddressAdapter);
                            mRlv_address_list.addHeaderView(mHeadView);
                        } else {
                            mChooseAddressAdapter.reLoadRegionDataList(RegionDataManager.getInstance().getSubRegionInfoListList());
                            mChooseAddressAdapter.notifyDataSetChanged();
                        }
                        AddressResponBean responBean = (AddressResponBean) event.getParam();
                        mHasMore = responBean.isHasMore();
                        mRlv_address_list.setHasLoadMore(mHasMore);
                        setSelectedString(responBean.getCurrentRegionName());
                    } else
                        CustomToast.makeText(this, getString(R.string.string_for_send_requset_fail_02), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setSelectedString(String selectedAddress) {
        if (TextUtils.isEmpty(selectedAddress)) {
            if (mAddressBundleBean.getDefaultCityId() != null) {
                if (mAddressBundleBean.getParentId() == mAddressBundleBean.getDefaultProvinceId())
                    selectedAddress = mAddressBundleBean.getDefaultCityString();
            }
        }
        TextView tv_selected_string = (TextView) mHeadView.findViewById(R.id.tv_selected_string);
        tv_selected_string.setText(selectedAddress);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1) {
            if (mAddressBundleBean.getRegionId() != null) {
                finishAndSetResult(mAddressBundleBean);
            }
        } else {
            int index;
            index = position - 2;
            List<CsAddress.DirectoryRegionInfo> subRegionInfoListList = RegionDataManager.getInstance().getSubRegionInfoListList();
            CsAddress.DirectoryRegionInfo directoryRegionInfo = subRegionInfoListList.get(index);
            AddressBundleBean addressBundleBean = new AddressBundleBean();
            addressBundleBean.setSelectedString(directoryRegionInfo.getDirectoryCountryRegionName() + " " + mAddressBundleBean.getProvinceString());
            addressBundleBean.setRegionCode(directoryRegionInfo.getDirectoryCountryRegionCode());
            addressBundleBean.setParentId(mAddressBundleBean.getParentId());
            addressBundleBean.setRegionId(directoryRegionInfo.getDirectoryCountryRegionId());
            addressBundleBean.setCityString(directoryRegionInfo.getDirectoryCountryRegionName());
            addressBundleBean.setProvinceString(mAddressBundleBean.getProvinceString());
            addressBundleBean.setCouSntryCode(mAddressBundleBean.getCouSntryCode());
            addressBundleBean.setLocaleCode(mAddressBundleBean.getLocaleCode());
            addressBundleBean.setDefaultProvinceId(mAddressBundleBean.getParentId());
            finishAndSetResult(addressBundleBean);
        }
    }
}
