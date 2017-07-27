package com.fuexpress.kr.ui.activity.product_detail;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.adapter.ProductShippingAdapter;
import com.fuexpress.kr.ui.view.RatioLayout;
import com.fuexpress.kr.utils.WeakHandler;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fksproto.CsAddress;
import fksproto.CsItem;


public class ProductShippingActivity extends BaseActivity {
    public static final String DATA = "DATA";
    public static final String ITEM_ID = "ITEM_ID";
    public static final int requestCode = 4566;

    @BindView(R.id.title_iv_left)
    ImageView mIvLeft;
    @BindView(R.id.title_tv_left)
    TextView mTvLeft;
    @BindView(R.id.title_tv_center)
    TextView mTvCenter;
    @BindView(R.id.title_tv_right)
    TextView mTvRight;
    @BindView(R.id.title_iv_right)
    ImageView mIvRight;
    @BindView(R.id.img_banner)
    ImageView mImgBanner;
    @BindView(R.id.img_country)
    ImageView mImgCountry;
    @BindView(R.id.tv_country_name)
    TextView mTvCountryName;
    @BindView(R.id.lv_shipping_items)
    ListView mLvShippingItems;
    @BindView(R.id.img_root)
    RatioLayout mImgRoot;

    WeakHandler mHandler = new WeakHandler();
    private int mItemID;
    private List<CsItem.MatchShippingInfo> mData;
    private ProductShippingAdapter mAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private CsItem.GetMatchShippingInfoResponse matchShippingInfo;
    private String mCountryname;
    //    private ProductShippingManage mInstance;

    @Override
    public View setInitView() {
        return View.inflate(this, R.layout.activity_product_shipping, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initTitle();
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void init() {
        mItemID = (int) getIntent().getLongExtra(ITEM_ID, 0);
        matchShippingInfo = (CsItem.GetMatchShippingInfoResponse) getIntent().getBundleExtra(DATA).getSerializable(DATA);
        mData = new ArrayList<>();
        mAdapter = new ProductShippingAdapter(this, mData);
        mLvShippingItems.setAdapter(mAdapter);
        getShippings(null);
        mCountryname = matchShippingInfo.getCountryname();
        setCountryName(mCountryname);
        String shippingimageurl = matchShippingInfo.getShippingimageurl();
        if (!TextUtils.isEmpty(shippingimageurl)) {
            mImgRoot.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(shippingimageurl, mImgBanner);
        }
        String countryimageurl = matchShippingInfo.getCountryimageurl();
        if (!TextUtils.isEmpty(countryimageurl)) {
            mImgCountry.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(countryimageurl, mImgCountry);
        }
    }

    private void setCountryName(String name) {
        if (!TextUtils.isEmpty(name)) {
            mTvCountryName.setText(name);
            mTvCountryName.setTextColor(Color.BLACK);
        } else {
            mTvCountryName.setTextColor(Color.RED);
            mTvCountryName.setText(getString(R.string.string_please_select_country));
        }
    }

    private void initTitle() {
        mIvRight.setVisibility(View.GONE);
        mTvRight.setVisibility(View.GONE);
        mTvCenter.setText(R.string.product_shipping_info);
        mTvLeft.setText(R.string.String_item_title);
    }

    @OnClick({R.id.title_iv_left, R.id.title_tv_left, R.id.tv_select_country})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                finish();
                break;
            case R.id.tv_select_country:
                intent = new Intent(this, CountryListActivity.class);
                intent.putExtra(CountryListActivity.Curreny_country,mCountryname );
                startActivityForResult(intent, CountryListActivity.requrst_code);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CountryListActivity.requrst_code & data != null) {
            CsAddress.DirectoryCountryInfo countryInfo = (CsAddress.DirectoryCountryInfo) data.getExtras().getSerializable(CountryListActivity.select_country);
            if (countryInfo != null) {
//                mInstance.setCountryCode(countryInfo.getDirectoryCountryCode());
//                mInstance.setCountryname(countryInfo.getDirectoryCountryName());
//                mInstance.setCountryimageurl(countryInfo.getCountryImageurl());
                mCountryname = countryInfo.getDirectoryCountryName();
                mTvCountryName.setText(countryInfo.getDirectoryCountryName());
                mTvCountryName.setTextColor(Color.BLACK);
                mImgCountry.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(countryInfo.getCountryImageurl(), mImgCountry);
                getShippings(countryInfo.getDirectoryCountryCode());
            }
        }
    }

    /* required BaseRequest 	head         = 1;
     optional string         countryCode  = 2;
     optional int32       	regionId     = 3;
     optional int32       	matchItemId=4;
     optional string         currencyCode= 5;
     optional string         localeCode   = 6;*/
    private void getShippings(String countryCode) {
        AccountManager instance = AccountManager.getInstance();
        CsItem.GetMatchShippingListRequest.Builder builder = CsItem.GetMatchShippingListRequest.newBuilder()
                .setCurrencyCode(instance.getCurrencyCode()).setUserinfo(AccountManager.getInstance().getBaseUserRequest())
                .setMatchItemId(mItemID)
                .setLocaleCode(instance.getLocaleCode());
        if (countryCode == null) {
            if (AccountManager.isLogin)
                builder.setRegionId(instance.regionID).setCountryCode(instance.countryCode);
        } else {
            builder.setCountryCode(countryCode);
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsItem.GetMatchShippingListResponse>() {

            @Override
            public void onSuccess(CsItem.GetMatchShippingListResponse response) {
                final List<CsItem.MatchShippingInfo> matchShippingListList = response.getMatchShippingListList();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mData.clear();
                        mData.addAll(matchShippingListList);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mData.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void finish() {
//        Intent intent = getIntent();
        setResult(RESULT_OK);
        super.finish();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ProductShipping Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
