package com.fuexpress.kr.ui.activity.package_detail;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.ReParcelItemBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsParcel;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler2;


public class InsuranceDeclarationActivity extends BaseActivity {
    public static final String CURRENCY_CODE = "currency_id";
    public static String PARCLE_ID = "parcle_id";

    private View mRootView;
    private TextView mTvCount;
    private ListView mListView;
    private TextView mTvPriceRealy;
    private TextView mTvDeclarePrice;
    private int mPageIndex = 1;
    private int mParcleID;
    private boolean mIsScrollTop;
    private boolean mIsScrollButtom;
    private int mListViewHeight;
    private PtrClassicFrameLayout ptrFrame;
    private boolean mHasMore;

    List<CsParcel.ReparcelItemInfo> mItemLists;
    private static Handler mHandler = new Handler();
    private ParcelInsuranceItemsApadter mAdapter;
    private int count;
    private float mPrice;
    private String currency_code;
    private float mDeclaredTotal;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_insurance_declaration, null);
        mListView = (ListView) mRootView.findViewById(R.id.refresh_lv_body);
        ptrFrame = (PtrClassicFrameLayout) mRootView.findViewById(R.id.mptr_framelayout);
//        initEvent(mRootView);
        initTitel(getString(R.string.package_apply_insurance));
        initheader();
        initfoot();
        initData();
        initRefreshMore();
        return mRootView;
    }

    private void initEvent(final View mRootView) {
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = mRootView.getRootView().getHeight() - mRootView.getHeight();
                        if (heightDiff > 300) { // 说明键盘是弹出状态
                            Log.v("TAG", "键盘弹出状态");
                        } else {
                            if (mAdapter == null) return;
                            mAdapter.hintInputMethod();
                        }
                    }
                });
    }


    private void initData() {
        Intent intent = getIntent();
        currency_code = intent.getStringExtra(CURRENCY_CODE);
        mParcleID = (int) intent.getLongExtra(PARCLE_ID, 0);
        if (mParcleID == 0) return;
        mItemLists = new ArrayList<>();
        getDeatail();
    }

    private void initfoot() {
        View foot = View.inflate(this, R.layout.view_foot_insurance_declaration, null);
        mListView.addFooterView(foot);
        foot.findViewById(R.id.tv_save_declaration).setOnClickListener(this);
    }

    private void initheader() {
        View headerView = View.inflate(this, R.layout.view_split_parcle_header, null);
        headerView.findViewById(R.id.ll_pay_type).setVisibility(View.GONE);
        mListView.addHeaderView(headerView);
        mTvCount = (TextView) headerView.findViewById(R.id.tv_pavkage_item_count);
        mTvPriceRealy = (TextView) headerView.findViewById(R.id.tv_pavkage_real_price);
        mTvDeclarePrice = (TextView) headerView.findViewById(R.id.tv_pavkage_apply_price);
    }


    private void initTitel(String string) {
        mRootView.findViewById(R.id.title_iv_left).setOnClickListener(this);
        mRootView.findViewById(R.id.title_tv_right).setVisibility(View.GONE);
        mRootView.findViewById(R.id.title_iv_right).setVisibility(View.GONE);

        TextView title = (TextView) mRootView.findViewById(R.id.title_tv_center);
        View lefttv = mRootView.findViewById(R.id.title_tv_left);
        lefttv.setVisibility(View.VISIBLE);
        lefttv.setOnClickListener(this);
        ((TextView) lefttv).setText(getString(R.string.string_package_detail_back));

        title.setText(string);
    }

    private void initRefreshMore() {
        ptrFrame.setDurationToCloseFooter(400);
        ptrFrame.setPtrHandler(new PtrHandler2() {
            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return PtrDefaultHandler2.checkContentCanBePulledUp(frame, content, footer) && mHasMore;

//                return mIsScrollButtom && mHasMore;
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                getDeatail();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPageIndex = 1;
                getDeatail();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // 默认实现，根据实际情况做改动
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
//                return mIsScrollTop;
            }
        });
    }

    private void getDeatail() {
        CsParcel.InitInsuranceDeclarationRequest.Builder builder = CsParcel.InitInsuranceDeclarationRequest.newBuilder().setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setPageNum(mPageIndex);
        builder.setParcelID(mParcleID)
                .setLocaleCode(AccountManager.getInstance().getLocaleCode()).setCurrencyCode(currency_code);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.InitInsuranceDeclarationResponse>() {
            @Override
            public void onSuccess(final CsParcel.InitInsuranceDeclarationResponse response) {
                String status = response.getStatus();
                if (mPageIndex == 1) {
                    mItemLists.removeAll(mItemLists);
                    mItemLists.addAll(response.getReparcelItemInfoList());
                } else {
                    mItemLists.addAll(response.getReparcelItemInfoList());
                }

                if (!"nomore".equals(status)) {
                    mHasMore = true;
                } else {
                    mHasMore = false;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter == null) {
                            mAdapter = new ParcelInsuranceItemsApadter(InsuranceDeclarationActivity.this, mItemLists, currency_code);
                            mListView.setAdapter(mAdapter);
                        }
                        ptrFrame.refreshComplete();
                        if (mPageIndex == 1) {
                            mAdapter.clearDeclarePries();
                            mDeclaredTotal = response.getDeclaredTotal();
                            showHeader(response.getProductNum(), response.getSubTotal(), mDeclaredTotal);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mHasMore) mPageIndex++;
                    }
                });

            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        Toast.makeText(InsuranceDeclarationActivity.this, "fail:" + errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showHeader(int productnum, float subtotal, float declaredtotal) {
        mTvCount.setText(getString(R.string.package_product_count, productnum));
        mTvPriceRealy.setText(getString(R.string.package_price_realy, UIUtils.getCurrency(this, currency_code, subtotal)));
//        mTvDeclarePrice.setText(getString(R.string.package_declare_price, UIUtils.getCurrency(this, currency_code, declaredtotal)));
        showSelectItems();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                finish();
                break;

            case R.id.tv_save_declaration:
                saveDecl();
                break;
        }
    }

/*

    required BaseRequest     head                = 1;
    required BaseUserRequest userinfo            = 2;
    required int64           parcelId            = 3; //包裹id
    repeated PairStrFloat    declaredValue       = 4; //单品id列表与单品申报价
    optional string          localeCode          = 5;//语种
    optional string          currencyCode        = 6;//币种
    optional int32           currencyId          = 7;//币种id
*/

    private void saveDecl() {
        showLoading();
        CsParcel.SaveInsuranceDeclarationRequest.Builder builder = CsParcel.SaveInsuranceDeclarationRequest.newBuilder().setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setParcelId(mParcleID).setCurrencyCode(currency_code).setLocaleCode(AccountManager.getInstance().getLocaleCode());
        ArrayMap declarePrices = mAdapter.getDeclarePrices();
        for (int i = 0; i < mItemLists.size(); i++) {
            Object o = declarePrices.get(i);
            CsParcel.ReparcelItemInfo itemListID = mItemLists.get(i);
            if (o != null) {
//                builder.setPairsNum(itemListID.getQtypack());
                float price = (float) o;
                builder.addDeclaredValue(CsBase.PairStrFloat.newBuilder().setK(itemListID.getParcelItemId()).setV(price));
            } else {
//                builder.setPairsNum(itemListID.getQtypack());
                builder.addDeclaredValue(CsBase.PairStrFloat.newBuilder().setK(itemListID.getParcelItemId()).setV(itemListID.getDeclaredValue()));

            }
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.SaveInsuranceDeclarationResponse>() {

            @Override
            public void onSuccess(final CsParcel.SaveInsuranceDeclarationResponse response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        Intent intent = new Intent(InsuranceDeclarationActivity.this, PackageDetailActivity.class);
                        long parcelid = (long) response.getParcelId();
                        intent.putExtra(PackageDetailActivity.PARCEL_ID, parcelid);
                        startActivity(intent);
                        sendBus((long) mParcleID);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        Toast.makeText(InsuranceDeclarationActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    float mDeclaredTotalFinal;

    public void showSelectItems() {
        ArrayMap declarePrices = null;
        if (mAdapter != null) {
            declarePrices = mAdapter.getDeclarePrices();
        }
        if (mItemLists != null) {
            mDeclaredTotalFinal = mDeclaredTotal;
            for (int i = 0; i < mItemLists.size(); i++) {
                CsParcel.ReparcelItemInfo itemInfo = mItemLists.get(i);
                mDeclaredTotalFinal -= itemInfo.getDeclaredValue();
                Object obj = declarePrices.get(i);
                float temp = 0;
                if (obj != null) {
                    temp = (float) obj;
                } else {
                    temp = itemInfo.getDeclaredValue();
                }
                mDeclaredTotalFinal += temp;
            }
        }
        mTvDeclarePrice.setText(getString(R.string.package_declare_price, UIUtils.getCurrency(this, currency_code, mDeclaredTotalFinal)));
    }


    private void sendBus(long id) {
        EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_TOSEND_PARCEL, id));
    }
}
