package com.fuexpress.kr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AddressManager;
import com.fuexpress.kr.ui.adapter.AddressManagerAdapter;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.ui.view.wheel.ClearEditTextOrg;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsAddress;

public class AddressManagerActivity extends BaseActivity implements RefreshListView.OnRefreshListener, AdapterView.OnItemClickListener, TextView.OnEditorActionListener {

    public static final int AddressresultCode = 100;
    public static final String BACK_TITLE = "balck_title";
    public static final String KEY_IS_CHOOSE_TYPE = "IS_CHOOSE_TYPE";

    private int mPageNum = 1;

    @BindView(R.id.rfl_in_address_manager)
    RefreshListView mRflInAddressManager;

    private View mRootView;
    private AddressManagerAdapter mAddressManagerAdapter;
    private boolean mIsChooseType;
    private boolean mIsHaseMore;
    private ClearEditTextOrg mEd_address_search;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_address_manager, null);
        mEd_address_search = (ClearEditTextOrg) mRootView.findViewById(R.id.ed_address_search);
        mEd_address_search.setType(ClearEditTextOrg.KEY_SEARCH_TYPE);
        mEd_address_search.setOnEditorActionListener(this);
        TitleBarView title_in_address_manager = (TitleBarView) mRootView.findViewById(R.id.title_in_address_manager);
        title_in_address_manager.getIv_in_title_back().setOnClickListener(this);
        title_in_address_manager.getTv_in_title_right().setText(getString(R.string.string_for_address_manager_add));
        title_in_address_manager.getTv_in_title_right().setOnClickListener(this);
        title_in_address_manager.getmTv_in_title_back_tv().setText(TextUtils.isEmpty(getIntent().getStringExtra(BACK_TITLE)) ? "" : getIntent().getStringExtra(BACK_TITLE));
        //initData();
        return mRootView;
    }

    private void initData() {
        // TODO: 2016/11/2 加载地址列表数据
        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, null);
        //AddressManager.getInstance().getCustomerAddressListRequest(mPageNum, mSearchKey);
        mRflInAddressManager.autoRefresh();
        mIsChooseType = getIntent().getBooleanExtra(KEY_IS_CHOOSE_TYPE, false);
    }


    @Override
    public void initView() {
        //mRflInAddressManager.setAdapter(new AddressManagerAdapter(this));
        mRflInAddressManager.setOnRefreshListener(this);
        //mRflInAddressManager.setHasLoadMore(false);
        mRflInAddressManager.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_in_title_right:
                // TODO: 2016/11/1 跳转到新增地址页面
                //startDDMActivity(AddNewAddressActivity.class, true);
                Intent intent = new Intent(this, AddNewAddressActivity.class);
                intent.putExtra("addressListSize", AddressManager.getInstance().mAddressesList.size());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        //AddressManager.getInstance().getCustomerAddressListRequest();
        mPageNum = 1;
        AddressManager.getInstance().getCustomerAddressListRequest(mPageNum, "");
        //initData();
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (mIsHaseMore)
            AddressManager.getInstance().getCustomerAddressListRequest(++mPageNum, "");
        //return;
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_ADDRESS_LIST_COMPLETE:
                mRflInAddressManager.stopRefresh();
                mRflInAddressManager.stopLoadMore(true);
                mIsHaseMore = event.getBooleanParam02();
                mRflInAddressManager.setHasLoadMore(mIsHaseMore);
                if (event.getBooleanParam()) {
                    if (mAddressManagerAdapter == null) {
                        mAddressManagerAdapter = new AddressManagerAdapter(this, AddressManager.getInstance().mAddressesList);
                        mRflInAddressManager.setAdapter(mAddressManagerAdapter);
                    } else {
                        mAddressManagerAdapter.reSetDataList(AddressManager.getInstance().mAddressesList);
                        mAddressManagerAdapter.notifyDataSetChanged();
                    }
                } else {
                    changeDiagLogAlertType(SweetAlertDialog.ERROR_TYPE, getString(R.string.string_for_send_requset_fail_02));
                }
                dissMissProgressDiaLog(600);
                break;
            case BusEvent.DELET_ADDRESS_REQUEST:
                if (event.getBooleanParam()) {
                    //AddressManager.getInstance().getCustomerAddressListRequest();
                    initData();
                } else {
                    showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.string_for_send_requset_fail_02));
                    dissMissProgressDiaLog(500);
                }
                break;
            case BusEvent.SET_ADDRESS_IS_DEFAULT_COMPETE:
                //AddressManager.getInstance().getCustomerAddressListRequest();
                if (this.getClass().getName().equals(event.getStrParam()))
                    if (event.getBooleanParam()) {
                        initData();
                    } else {
                        changeDiagLogAlertType(SweetAlertDialog.ERROR_TYPE, getString(R.string.home_page_dialog_request_fail_02));
                        dissMissProgressDiaLog(500);
                    }
                break;
        }
    }

    @Override
    public void finish() {
        //if (mAddressManagerAdapter != null && mAddressManagerAdapter.isCilckDefault)
        //AddressManager.getInstance().setAddressDefault(mAddressManagerAdapter.mData.get(mAddressManagerAdapter.oldDeafaultIndex), 0);
        super.finish();
    }

    @Override
    protected void onResume() {
        //AddressManager.getInstance().getCustomerAddressListRequest();
        initData();
        super.onResume();
    }

    public static final String topText1 = "topText";
    public static final String addressText = "addressText";
    public static final String addressId = "addressId";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CsAddress.CustomerAddress customerAddress = AddressManager.getInstance().mAddressesList.get(position - 1);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("address", customerAddress);
        intent.putExtras(bundle);
        if (mIsChooseType) {
            String topText = customerAddress.getName() + "  " + customerAddress.getPhone() + "  " + customerAddress.getPostcode();
            //String s = AssetFileManager.getInstance().readFilePlus(customerAddress.getRegion(), AssetFileManager.ADDRESS_TYPE);
            String s = customerAddress.getFullRegionName();
            intent.putExtra(topText1, topText);
            intent.putExtra(addressText, customerAddress.getStreet() + "," + s);
            intent.putExtra(addressId, customerAddress.getAddressId());
            /*Bundle bundle = new Bundle();
            bundle.putSerializable("address", customerAddress);
            intent.putExtras(bundle);*/
            setResult(AddressresultCode, intent);
            finish();
        } else {
            intent = new Intent(this, AddNewAddressActivity.class);
            /*Bundle bundle = new Bundle();
            bundle.putSerializable("address", customerAddress);
            intent.putExtras(bundle);*/
            intent.putExtra("addressListSize", AddressManager.getInstance().mAddressesList.size());
            startActivity(intent);
        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
            //关闭软键盘
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive())
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            String searchKey = mEd_address_search.getText().toString().trim();
            Intent intent = new Intent(this, AddressSearchActivity.class);
            intent.putExtra(AddressSearchActivity.KEY_SEARCH_KEY, searchKey);
            intent.putExtra(AddressSearchActivity.KEY_IS_CHOOSE, mIsChooseType);
            if (mIsChooseType)
                startActivityForResult(intent, AddressresultCode);
            else
                startActivity(intent);
            /*mPageNum = 1;
            AddressManager.getInstance().getCustomerAddressListRequest(mPageNum, mSearchKey);*/
            //LogUtils.e(searchKey);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null)
            if (requestCode == AddressresultCode) {
                CsAddress.CustomerAddress customerAddress = (CsAddress.CustomerAddress) data.getSerializableExtra("address");
                String topText = customerAddress.getName() + "  " + customerAddress.getPhone() + "  " + customerAddress.getPostcode();
                //String s = AssetFileManager.getInstance().readFilePlus(customerAddress.getRegion(), AssetFileManager.ADDRESS_TYPE);
                String s = customerAddress.getFullRegionName();
                Intent intent = new Intent();
                intent.putExtra(topText1, topText);
                intent.putExtra(addressText, customerAddress.getStreet() + "," + s);
                intent.putExtra(addressId, customerAddress.getAddressId());
                setResult(AddressresultCode, intent);
                finish();
            }

    }


}
