package com.fuexpress.kr.ui.activity.product_detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;


import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.choose_address.ChooseAddressAdapter;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.utils.WeakHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fksproto.CsAddress;

public class CountryListActivity extends BaseActivity implements RefreshListView.OnRefreshListener {

    public static final String select_country = "SELECT_COUNTRY";
    public static final String Curreny_country = "Curreny_country";
    public static final int requrst_code = 2312;

    @BindView(R.id.title_iv_left)
    ImageView mIvLeft;
    @BindView(R.id.title_tv_left)
    TextView mTvLeft;
    @BindView(R.id.title_tv_center)
    TextView mTvCenter;
    @BindView(R.id.title_tv_right)
    TextView mTvRight;
    @BindView(R.id.tv_banner_name)
    TextView mTvBannerName;
    @BindView(R.id.title_iv_right)
    ImageView mIvRight;
    @BindView(R.id.lv_body)
    RefreshListView mLvBody;

    private WeakHandler mHandler = new WeakHandler();
    private ChooseAddressAdapter mAdapter;
    private List<CsAddress.DirectoryCountryInfo> mCountries;
    private boolean mHasMore;

    @Override
    public View setInitView() {
        return View.inflate(this, R.layout.activity_country_list, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initTitle();
        String name = getIntent().getStringExtra(Curreny_country);
        if (name != null) mTvBannerName.setText(name);

        mLvBody.setOnRefreshListener(this);
        mLvBody.setVisibility(View.INVISIBLE);
        mCountries = new ArrayList<>();
        mAdapter = new ChooseAddressAdapter(this, ChooseAddressAdapter.KEY_COUNTRY_TYPE, mCountries, null);
        mLvBody.setAdapter(mAdapter);
        mLvBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                intent.putExtra(select_country, mCountries.get(position - 1));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        getCountries(1);
    }

    /* required BaseRequest   head        = 1;
     optional string        localeCode  = 2;//语种
     optional int32         page        = 3;//第几页
     optional int32         num         = 4;//一页显示几条
     optional string        countryCode = 5;//国家代码(新增不需要,编辑才需要)
     optional int32         sortBy      = 6;//0或不传按照sortOrder排序。1代表按照字母排序
 */
    private int index = 1;

    private void getCountries(final int i) {
        CsAddress.GetCountryListRequest.Builder builder = CsAddress.GetCountryListRequest.newBuilder()
                .setLocaleCode(AccountManager.getInstance().getLocaleCode())
                .setPage(i)
                .setNum(20);
        if (AccountManager.isLogin) {
            builder.setCountryCode(AccountManager.getInstance().mCountryCode);
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsAddress.GetCountryListResponse>() {
            @Override
            public void onSuccess(CsAddress.GetCountryListResponse response) {
                mHasMore = "more".equals(response.getDrop());
                ++index;
                final List<CsAddress.DirectoryCountryInfo> list = response.getCountryInfoListList();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLvBody.setVisibility(View.VISIBLE);
                        if (i == 1) {
                            mCountries.clear();
                            mCountries.addAll(list);
                        } else {
                            mCountries.addAll(list);
                        }
                        mAdapter.notifyDataSetChanged();
                        mLvBody.stopLoadMore(true);
                        mLvBody.setHasLoadMore(mHasMore);
                        mLvBody.stopRefresh();
                    }
                }, 500);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mLvBody.stopLoadMore(true);
                        mLvBody.setHasLoadMore(false);
                        mLvBody.stopRefresh();
                    }
                }, 500);
            }
        });
    }

    private void initTitle() {
        mIvRight.setVisibility(View.GONE);
        mTvRight.setVisibility(View.GONE);
        mTvCenter.setText(getString(R.string.login_tv_01));
        mTvLeft.setText(R.string.product_shipping_info);
    }


    @OnClick({R.id.title_iv_left, R.id.title_tv_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        index = 1;
        getCountries(index);
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (mHasMore)
            getCountries(index);
    }
}
