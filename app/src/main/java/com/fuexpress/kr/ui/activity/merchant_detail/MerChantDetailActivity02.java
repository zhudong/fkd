
package com.fuexpress.kr.ui.activity.merchant_detail;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.model.MerChantBean;
import com.fuexpress.kr.model.OperaRequestManager;
import com.fuexpress.kr.model.ShareManager;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Longer on 2017/6/26.
 */

public class MerChantDetailActivity02 extends BaseActivity implements RefreshListView.OnRefreshListener {

    RefreshListView mLvForStoreFragmentMerchantDetail;
    private View mRootView;
    private MerChantBean mMerChantBean;
    private int mPageNo = 1;
    //private TitleBarView mTitleBarView;
    //private ImageView mIv_title_more;
    private boolean mMore = false;
    private View mHeadView;
    private ImageLoader mImageLoader;
    private CategoryItemAdapter mCategoryItemAdapter;
    private boolean mIsAddHeadView = false;
    private TextView mTv_for_store_fragment_detail_attention;
    private TextView mTv_for_store_fragment_detail_follows;
    private long mMerchantid1;
    private long mMerchantId;
    private TitleBarView mTitle_in_merdetail;
    private ImageView mIv_title_more;
    private List<ItemBean> mDates;


    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_for_merchantdetail, null);
        //mTitleBarView = new TitleBarView(mRootView);
        mTitle_in_merdetail = (TitleBarView) mRootView.findViewById(R.id.title_in_merdetail);
        mTitle_in_merdetail.getIv_in_title_back().setOnClickListener(this);
        TextView back_tv = mTitle_in_merdetail.getmTv_in_title_back_tv();
        back_tv.setText(getString(R.string.main_source));
        back_tv.setOnClickListener(this);
        mIv_title_more = mTitle_in_merdetail.getIv_title_more();
        mIv_title_more.setOnClickListener(this);
        mLvForStoreFragmentMerchantDetail = (RefreshListView) mRootView.findViewById(R.id.lv_for_store_fragment_merchant_detail);
        mLvForStoreFragmentMerchantDetail.setOnRefreshListener(this);
        mImageLoader = ImageLoader.getInstance();
        mDates = new ArrayList<>();
        mCategoryItemAdapter = new CategoryItemAdapter(this, mDates);
        mLvForStoreFragmentMerchantDetail.setAdapter(mCategoryItemAdapter);
        return mRootView;
    }


    @Override
    public void initView() {
        //获得传过来的商店对象:
        mMerchantId = getIntent().getLongExtra("merchantId", 0);
        /*ImageView imageViewLeft = mTitleBarView.getImageViewLeft();
        mIv_title_more = mTitleBarView.getMoreImageView();
        imageViewLeft.setOnClickListener(this);
        mIv_title_more.setOnClickListener(this);*/

        /*List<ItemBean> mDates = new ArrayList<>();
        mLvForStoreFragmentMerchantDetail.setAdapter(new CategoryItemAdapter(this, mDates));*/
        mLvForStoreFragmentMerchantDetail.autoRefresh();
        mLvForStoreFragmentMerchantDetail.setHasLoadMore(true);
        //initData(0);
    }


    /**
     * 这是设置头布局的方法:
     */
    private View setHeadViewMethod() {
        mHeadView = View.inflate(SysApplication.getContext(), R.layout.head_view_for_store, null);
        //找到相应的控件:
        ImageView iv_for_store_fragment_detail_cover = (ImageView) mHeadView.findViewById(R.id.iv_for_store_fragment_detail_cover);
        iv_for_store_fragment_detail_cover.setOnClickListener(this);
        TextView tv_for_store_fragment_detail_merchantname = (TextView) mHeadView.findViewById(R.id.tv_for_store_fragment_detail_merchantname);
        TextView tv_for_store_fragment_detail_address = (TextView) mHeadView.findViewById(R.id.tv_for_store_fragment_detail_address);
        TextView tv_for_store_fragment_detail_info = (TextView) mHeadView.findViewById(R.id.tv_for_store_fragment_detail_info);
        tv_for_store_fragment_detail_info.setOnClickListener(this);
        TextView tv_for_store_fragment_detail_pic = (TextView) mHeadView.findViewById(R.id.tv_for_store_fragment_detail_pic);
        tv_for_store_fragment_detail_pic.setOnClickListener(this);
        mTv_for_store_fragment_detail_follows = (TextView) mHeadView.findViewById(R.id.tv_for_store_fragment_detail_follows);
        mTv_for_store_fragment_detail_follows.setOnClickListener(this);
        mTv_for_store_fragment_detail_attention = (TextView) mHeadView.findViewById(R.id.tv_for_store_fragment_detail_attention);
        mTv_for_store_fragment_detail_attention.setOnClickListener(this);
        mTv_for_store_fragment_detail_follows.setTag(mMerChantBean.getMerchantid());

        //然后进行设置:
        String cover = mMerChantBean.getCover();
        ViewGroup.LayoutParams layoutParams = iv_for_store_fragment_detail_cover.getLayoutParams();
        mWidthPixels = UIUtils.getScreenWidthPixels(this);
        layoutParams.width = mWidthPixels;
        int height = (int) (mWidthPixels / 1.5);
        layoutParams.height = height;
        iv_for_store_fragment_detail_cover.setLayoutParams(layoutParams);

        DisplayImageOptions displayOptions = ImageLoaderHelper.getInstance(SysApplication.getContext()).getDisplayOptions();
        mImageLoader.displayImage(cover + Constants.ImgUrlSuffix.biz_list, iv_for_store_fragment_detail_cover, displayOptions);
        tv_for_store_fragment_detail_merchantname.setText(mMerChantBean.getTitle());
        tv_for_store_fragment_detail_address.setText(mMerChantBean.getAddress());
        mTv_for_store_fragment_detail_follows.setText(getString(R.string.merchant_detail_follower_befor_string) + mMerChantBean.getFollow_num());
        boolean follow = mMerChantBean.is_follow();
        if (follow) {
            //说明是已经关注了的;
            mTv_for_store_fragment_detail_attention.setText(getString(R.string.merchant_detail_followed));
        } else {
            mTv_for_store_fragment_detail_attention.setText(getString(R.string.merchant_detail_followed_not));
        }
        return mHeadView;
    }

    /**
     * 加载数据的方法:
     */
    private void initData(final int status) {
        MerchantDetailManager.getInstance().getMerChantDetailRequest(mMerchantId);
        //unEnableShare();
        //unEnableShare();
        //getItetmFormNet(status);
    }

    private void getItetmFormNet(int status) {
        if (status == 1 && mMore) {
            mPageNo = mPageNo + 1;
        } else if (0 == status) {
            mPageNo = 1;
        }
        MerchantDetailManager.getInstance().getMerchantItemsList(mMerchantId, mPageNo, status);
    }


    @Override
    public void onRefresh(RefreshListView refreshListView) {
        //mPageNo = 1;
        initData(0);
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (mMore) {
            //initData(1);
            getItetmFormNet(1);
        } else {
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_for_store_fragment_detail_follows:
                Intent intent02 = new Intent(this, MerChantFollowsActivity02.class);
                intent02.putExtra("merchantId", mMerchantId);
                intent02.putExtra("mName", mMerChantBean.getTitle());
                intent02.putExtra("isFollowed", mMerChantBean.is_follow());
                this.startActivity(intent02);
                break;
            case R.id.tv_for_store_fragment_detail_info:
                /*Intent intent = new Intent(this, MerchantBusinessActivity02.class);
                this.startActivity(intent);*/
                break;
            case R.id.tv_for_store_fragment_detail_pic:
                Intent intent03 = new Intent(this, MerChantImageActivity02.class);
                intent03.putExtra("merchantId", mMerchantId);
                this.startActivity(intent03);
                break;

            case R.id.iv_in_title_back:
                /*finish();
                break;*/
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.tv_for_store_fragment_detail_attention:
                if (AccountManager.isLogin) {
                    /*if (!UIUtils.isFastClick()) {
                        operaFollowMethond();
                    }*/
                    operaFollowMethond();
                } else {
                    AccountManager.getInstance().isUserLogin(SysApplication.getContext());
                }
                break;
            case R.id.iv_title_more:
                switchMenu();
                break;

        }
    }

    private void operaFollowMethond() {
        OperaRequestManager operaRequestManager = OperaRequestManager.getInstance();
        mMerchantid1 = mMerChantBean.getMerchantid();
        operaRequestManager.operateLikeMerchant(mMerchantid1, mMerChantBean, this, null);
    }


    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.OPERA_MERCHANT_SUCCESS:
                boolean follow = mMerChantBean.is_follow();
                int follow_num = mMerChantBean.getFollow_num();
                if (!follow) {
                    mTv_for_store_fragment_detail_attention.setText(getString(R.string.merchant_detail_followed_not));
                    mTv_for_store_fragment_detail_follows.setText(getString(R.string.merchant_detail_follower_befor_string) + follow_num);
                } else if (follow) {
                    mTv_for_store_fragment_detail_attention.setText(getString(R.string.merchant_detail_followed));
                    mTv_for_store_fragment_detail_follows.setText(getString(R.string.merchant_detail_follower_befor_string) + follow_num);
                }
                break;
            case BusEvent.GET_MERCHANT_DETAIL_SUCCESS:
                //mMerChantBean = (MerChantBean) event.getParam();
                mMerChantBean = MerchantDetailManager.getInstance().mMerChantBean;
                setHeadViewMethod();
                mTitle_in_merdetail.setTitleText(mMerChantBean.getTitle());
                //initData(0);
                if (!mIsAddHeadView) {
                    mLvForStoreFragmentMerchantDetail.addHeaderView(mHeadView);
                    mIsAddHeadView = true;
                }
                getItetmFormNet(0);
                break;
            case BusEvent.GET_MERCHANT_ITEMS_LIST_SUCCESS:
                /*SysApplication.mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLvForStoreFragmentMerchantDetail.stopRefresh();
                    }
                }, 400);*/
                mDates.clear();
                mDates.addAll(MerchantDetailManager.getInstance().mLoaclItemList);
                mCategoryItemAdapter.setDate(mDates);
                mCategoryItemAdapter.notifyDataSetChanged();
                List<ItemBean> loaclItemList = MerchantDetailManager.getInstance().mLoaclItemList;
                //mLvForStoreFragmentMerchantDetail.setAdapter(mCategoryItemAdapter);
                if (!mIsAddHeadView) {
                    mLvForStoreFragmentMerchantDetail.addHeaderView(mHeadView);
                    mIsAddHeadView = true;
                }
                mLvForStoreFragmentMerchantDetail.stopRefresh();
                mMore = event.getBooleanParam();
                if (mMore) {
                    mLvForStoreFragmentMerchantDetail.setHasLoadMore(true);
                } else {
                    mLvForStoreFragmentMerchantDetail.setHasLoadMore(false);
                }
                mLvForStoreFragmentMerchantDetail.invalidateViews();
                break;
            case BusEvent.GET_MERCHANT_ITEMS_LIST_SUCCESS_MORE:
                mLvForStoreFragmentMerchantDetail.stopLoadMore(true);
                mMore = event.getBooleanParam();
                if (mMore) {
                    mLvForStoreFragmentMerchantDetail.setHasLoadMore(true);
                } else {
                    mLvForStoreFragmentMerchantDetail.setHasLoadMore(false);
                }
                mDates = MerchantDetailManager.getInstance().mLoaclItemList;
                mCategoryItemAdapter.setDate(mDates);
                mCategoryItemAdapter.notifyDataSetChanged();
                break;
            case BusEvent.REQUEST_FAIL:
                /*showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.String_for_store_dialog_request_fail));
                dissMissProgressDiaLog(1000);*/
                CustomToast.makeText(this, event.getStrParam(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected View getAnchorView() {
        return mIv_title_more;
    }

    @Override
    protected void share() {
        //CustomToast.makeText(this, "dklfjksafjaj", Toast.LENGTH_SHORT).show();
        ShareManager.initWithRes(MerchantDetailManager.getInstance().getShareImageList(), MerchantDetailManager.getInstance().getIntroshare(), this);
    }

}
