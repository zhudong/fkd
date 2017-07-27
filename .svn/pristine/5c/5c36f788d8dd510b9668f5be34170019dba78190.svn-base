package com.fuexpress.kr.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.merchant_detail.CategoryItemAdapter;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.ClassUtil;
import com.fuexpress.kr.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsItem;


public class PlacesProductActivity extends BaseActivity implements RefreshListView.OnRefreshListener {
    public static final int FROM_PLACE = 1;
    public static final int FROM_LINK = 2;

    public static final int REFRESH = 3;
    public static final int LOADEMORE = 4;
    public static final int LOADE_FAIL = 5;

    public static String ID = "id";
    public static String FROM_WHERE = "from_where";
    public static String NAME = "name";

    private View mRootView;
    private int mPageIndex;
    private List<ItemBean> mItemsList;
    private int mState;
    private int mId;
    private RefreshListView mBody;


    private CategoryItemAdapter mCategoryItemAdapter;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBody.stopRefresh();
            mBody.stopLoadMore(true);
            mBody.setHasLoadMore(mMore);
            mCount.setText("(" + mItemsCount + ")");
            closeLoading();
            switch (msg.what) {
                case REFRESH:
                    if (mCategoryItemAdapter == null) {
                        mCategoryItemAdapter = new CategoryItemAdapter(PlacesProductActivity.this, mItemsList);
                        mBody.setAdapter(mCategoryItemAdapter);
                    }
                    break;
                case LOADEMORE:
                    mCategoryItemAdapter.notifyReflash();
                    break;

                case LOADE_FAIL:
                    closeLoading();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private RelativeLayout mTitle;
    private TextView mTitleName;
    private TextView mForm;
    private TextView mName;
    private TextView mCount;
    private int mItemsCount;
    private boolean mMore;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_places_product, null);
        mBody = (RefreshListView) mRootView.findViewById(R.id.rlv_body);
        mTitleName = (TextView) mRootView.findViewById(R.id.title_tv_center);
        mRootView.findViewById(R.id.title_iv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
       /* mTitle = new TitleBarView(mRootView).getCommonTitle();
        mTitle.findViewById(R.id.img_title_left).setOnClickListener(this);
        mTitle.findViewById(R.id.img_title_right).setOnClickListener(this);
        mTitleName = (TextView) mTitle.findViewById(R.id.tv_title);
*/
        View headView = View.inflate(this, R.layout.item_for_place_item, null);
        mForm = (TextView) headView.findViewById(R.id.tv_from);
        mName = (TextView) headView.findViewById(R.id.tv_from_name);
        mCount = (TextView) headView.findViewById(R.id.tv_item_count);
        //mBody.addHeaderView(headView);
        mBody.addHeaderView(headView);
        mBody.setOnRefreshListener(this);

        initData();
        return mRootView;
    }

    @Override
    public void initView() {
//        super.initView();
        unEnableShare();
    }

    private void initData() {
        mItemsList = new ArrayList<>();
        mId = getIntent().getIntExtra(ID, 0);
        mState = getIntent().getIntExtra(FROM_WHERE, 0);
        mTitleName.setText(getIntent().getStringExtra(NAME));
        mPageIndex = 1;
        if (mState == FROM_PLACE) {
            mForm.setText("at");
            mName.setText(getIntent().getStringExtra(NAME));
            getItemByPlace(mId);
        } else if (mState == FROM_LINK) {
            mForm.setText("from");
            mName.setText(getIntent().getStringExtra(NAME));
            getItemByLink(mId);
        }
        showLoading();
    }

    private void getItemByPlace(int placeID) {
        CsItem.GetItemListByPlaceRequest.Builder builder = CsItem.GetItemListByPlaceRequest.newBuilder();
        builder.setPlaceId(placeID).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setPageno(mPageIndex).setCurrencycode(AccountManager.getInstance().getCurrencyCode()).setCurrencyid(AccountManager.getInstance().getCurrencyId()).setLocalecode(AccountManager.getInstance().getLocaleCode());
        LogUtils.d("builderString", builder.toString());
        NetEngine.postRequest(builder, new INetEngineListener<CsItem.GetItemListByPlaceResponse>() {
            @Override
            public void onSuccess(CsItem.GetItemListByPlaceResponse response) {
                if (mPageIndex == 1) {
                    mItemsCount = response.getTotal();
                    mItemsList.removeAll(mItemsList);
                    mItemsList.addAll(ClassUtil.convertItemList2ItemBean(response.getItemsList()));
                    mHandler.sendEmptyMessage(REFRESH);

                } else {
                    mItemsList.addAll(ClassUtil.convertItemList2ItemBean(response.getItemsList()));
                    mHandler.sendEmptyMessage(LOADEMORE);
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mMore = false;
                if (mPageIndex == 1) {
                    mItemsList = new ArrayList<ItemBean>();
                    mHandler.sendEmptyMessage(REFRESH);

                } else {
                    mHandler.sendEmptyMessage(LOADEMORE);
                }
            }
        });
        /*NetEngine.postRequest(builder, new INetEngineListener<CsItem.GetItemListByPlaceResponse>() {

            @Override
            public void onSuccess(CsItem.GetItemListByPlaceResponse response) {
                mMore = response.getMore();

                if (mPageIndex == 1) {
                    mItemsCount = response.getTotal();
                    mItemsList.removeAll(mItemsList);
                    mItemsList.addAll(ClassUtil.convertItemList2ItemBean(response.getItemsList()));
                    mHandler.sendEmptyMessage(REFRESH);

                } else {
                    mItemsList.addAll(ClassUtil.convertItemList2ItemBean(response.getItemsList()));
                    mHandler.sendEmptyMessage(LOADEMORE);
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mMore = false;
                if (mPageIndex == 1) {
                    mItemsList=new ArrayList<ItemBean>();
                    mHandler.sendEmptyMessage(REFRESH);

                } else {
                    mHandler.sendEmptyMessage(LOADEMORE);
                }
            }
        });*/
    }

    private void getItemByLink(int id) {
        CsItem.GetItemListByWebsiteRequest.Builder builder = CsItem.GetItemListByWebsiteRequest.newBuilder();
        builder.setWebsiteId(id).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setPageno(mPageIndex).setCurrencycode(AccountManager.getInstance().getCurrencyCode()).setCurrencyid(AccountManager.getInstance().getCurrencyId()).setLocalecode(AccountManager.getInstance().getLocaleCode());
        ;
        LogUtils.d("builderString", builder.toString());


        INetEngineListener<CsItem.GetItemListByWebsiteResponse> listener = new INetEngineListener<CsItem.GetItemListByWebsiteResponse>() {

            @Override
            public void onSuccess(CsItem.GetItemListByWebsiteResponse response) {
                mMore = response.getMore();
                if (mPageIndex == 1) {
                    mItemsCount = response.getTotal();
                    mItemsList.removeAll(mItemsList);
                    mItemsList.addAll(ClassUtil.convertItemList2ItemBean(response.getItemsList()));
                    mHandler.sendEmptyMessage(REFRESH);

                } else {
                    mItemsList.addAll(ClassUtil.convertItemList2ItemBean(response.getItemsList()));
                    mHandler.sendEmptyMessage(LOADEMORE);
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mMore = false;
                if (mPageIndex == 1) {
                    mItemsList = new ArrayList<ItemBean>();
                    mHandler.sendEmptyMessage(REFRESH);

                } else {
                    mHandler.sendEmptyMessage(LOADEMORE);
                }
            }
        };
        NetEngine.postRequest(builder, listener);
//        BaseProtocol.getInstance().loadData(builder, listener);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_title_left:
                this.finish();
                break;
            case R.id.img_title_right:
                switchMenu();
                break;
        }
    }

    @Override
    protected View getAnchorView() {
        return mTitle;
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        mPageIndex = 1;
        if (mState == FROM_PLACE) {
            getItemByPlace(mId);
        } else {
            getItemByLink(mId);
        }
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        mPageIndex++;
        if (mState == FROM_PLACE) {
            getItemByPlace(mId);
        } else {
            getItemByLink(mId);
        }
    }
}
