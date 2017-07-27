
package com.fuexpress.kr.ui.activity.merchant_detail;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.MerChantBean;
import com.fuexpress.kr.model.OperaRequestManager;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.activity.PhotoViewActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.TimeUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;


/**
 * Created by Longer on 2017/6/26.
 */

public class MerChantImageActivity02 extends BaseActivity {

    private ListView mLv_in_merchant_dteail_image_list;
    private long mMerchantId;
    private List<CsBase.MerchantImage> mMerchantImagesList;
    private MerChantBean mMerChantBean;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayOptions;
    private MerchantImageListAdapter mMerchantImageListAdapter;
    //private TitleBarView mTitleBarView;
    private ImageView mMoreImageView;
    //private ImageView mImageViewLeft;
    private View mHeadView;
    private TextView mTv_in_head_for_merchant_follows_lv_attention;
    private ArrayList<String> mImageUrlList;
    private TextView mTv_in_head_for_merchant_follows_lv_followsnum;
    private TitleBarView mTitle_in_merimage;

    @Override
    public View setInitView() {
        View rootView = View.inflate(this, R.layout.activity_mer_chant_image, null);
        mLv_in_merchant_dteail_image_list = (ListView) rootView.findViewById(R.id.lv_in_merchant_dteail_image_list);
        mTitle_in_merimage = (TitleBarView) rootView.findViewById(R.id.title_in_merimage);
        mTitle_in_merimage.getIv_in_title_back().setOnClickListener(this);
        mTitle_in_merimage.setTitleText(getString(R.string.merchant_detail_image_title));
        //mTitleBarView = new TitleBarView(mRootView);
        return rootView;
    }

    @Override
    public void initView() {
        //super.initView();
        mMerchantId = getIntent().getLongExtra("merchantId", 0);
        mImageLoader = SysApplication.getImageLoader();
        mDisplayOptions = ImageLoaderHelper.getInstance(this).getDisplayOptions();
        //mTitleBarView.setTitle(getString(R.string.merchant_detail_image_title));
        //mMoreImageView = mTitleBarView.getMoreImageView();
        //mMoreImageView.setOnClickListener(this);
        //mImageViewLeft = mTitleBarView.getImageViewLeft();
        initHeadView();
        //mImageViewLeft.setOnClickListener(this);

        initData();
    }

    private void initData() {
        mImageUrlList = MerchantDetailManager.getInstance().mImageUrlList;
        mMerchantImagesList = MerchantDetailManager.getInstance().mMerchantImagesList;
        mMerchantImageListAdapter = new MerchantImageListAdapter();
        mLv_in_merchant_dteail_image_list.setAdapter(mMerchantImageListAdapter);
        mLv_in_merchant_dteail_image_list.addHeaderView(mHeadView);
    }

    /**
     * 自定义的一个Adapter类;
     */
    class MerchantImageListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mMerchantImagesList.size() != 0) {
                return mMerchantImagesList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mMerchantImagesList.size() != 0) {
                return mMerchantImagesList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(MerChantImageActivity02.this, R.layout.item_for_merchant_detail_image, null);

                viewHolder.iv_for_merchant_detail_image = (ImageView) convertView.findViewById(R.id.iv_for_merchant_detail_image);
                viewHolder.iv_for_merchant_detail_image.setOnClickListener(MerChantImageActivity02.this);
                viewHolder.tv_for_merchant_detail_image_name = (TextView) convertView.findViewById(R.id.tv_for_merchant_detail_image_name);
                viewHolder.tv_for_merchant_detail_image_time = (TextView) convertView.findViewById(R.id.tv_for_merchant_detail_image_time);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            CsBase.MerchantImage merchantImage = mMerchantImagesList.get(position);

            viewHolder.tv_for_merchant_detail_image_name.setText(merchantImage.getImageName());

            viewHolder.tv_for_merchant_detail_image_time.setText(TimeUtils.getDateStyleString(merchantImage.getCreateTime()));

            String imageUrl = merchantImage.getImageUrl();

            ViewGroup.LayoutParams layoutParams = viewHolder.iv_for_merchant_detail_image.getLayoutParams();

            mWidthPixels = UIUtils.getScreenWidthPixels(MerChantImageActivity02.this);

            layoutParams.width = mWidthPixels;

            layoutParams.height = (int) (mWidthPixels / 1.5);

            mImageLoader.displayImage(imageUrl + Constants.ImgUrlSuffix.biz_list, viewHolder.iv_for_merchant_detail_image, mDisplayOptions);
            viewHolder.iv_for_merchant_detail_image.setTag(position);

            return convertView;
        }


        class ViewHolder {
            ImageView iv_for_merchant_detail_image;
            TextView tv_for_merchant_detail_image_name;
            TextView tv_for_merchant_detail_image_time;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
                finish();
                break;
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.tv_in_head_for_merchant_follows_lv_attention:
                if (AccountManager.isLogin) {
                    /*if (!UIUtils.isFastClick()) {
                        operaFollowMethond();
                    }*/
                    operaFollowMethond();
                } else {
                    AccountManager.getInstance().isUserLogin(SysApplication.getContext());
                }
                break;
            case R.id.iv_for_merchant_detail_image:
                int po = (int) v.getTag();
                Intent intent = new Intent(this, PhotoViewActivity.class);
                intent.putStringArrayListExtra(PhotoViewActivity.PAGE_IMAGES, mImageUrlList);
                intent.putExtra(PhotoViewActivity.PAGE_INDEX, po);
                this.startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_out_alpha, R.anim.activity_translate_in_alpha);
                break;
            /*case R.id.iv_title_more:
                switchMenu();
                break;*/
        }
    }

    /**
     * 初始化头布局:
     */
    private void initHeadView() {
        mHeadView = View.inflate(this, R.layout.head_for_merchant_follows_lv, null);
        mMerChantBean = MerchantDetailManager.getInstance().mMerChantBean;
        TextView back_tv = mTitle_in_merimage.getmTv_in_title_back_tv();
        back_tv.setText(mMerChantBean.getTitle());
        back_tv.setOnClickListener(this);
        TextView tv_in_head_for_merchant_follows_lv_name = (TextView) mHeadView.findViewById(R.id.tv_in_head_for_merchant_follows_lv_name);
        tv_in_head_for_merchant_follows_lv_name.setText(mMerChantBean.getTitle());
        mTv_in_head_for_merchant_follows_lv_followsnum = (TextView) mHeadView.findViewById(R.id.tv_in_head_for_merchant_follows_lv_followsnum);
        mTv_in_head_for_merchant_follows_lv_followsnum.setText(getString(R.string.merchant_detail_follows_space) + mMerChantBean.getFollow_num());
        mTv_in_head_for_merchant_follows_lv_attention = (TextView) mHeadView.findViewById(R.id.tv_in_head_for_merchant_follows_lv_attention);
        mTv_in_head_for_merchant_follows_lv_attention.setOnClickListener(this);
        if (mMerChantBean.is_follow()) {
            mTv_in_head_for_merchant_follows_lv_attention.setText(getString(R.string.merchant_detail_followed));
        } else {
            mTv_in_head_for_merchant_follows_lv_attention.setText(getString(R.string.merchant_detail_followed_not));
        }
    }


    private void operaFollowMethond() {
        OperaRequestManager operaRequestManager = OperaRequestManager.getInstance();
        long merchantid = mMerChantBean.getMerchantid();
        operaRequestManager.operateLikeMerchant(merchantid, mMerChantBean, this, new OperaRequestListener() {
            @Override
            public void onOperaSuccess() {
            }

            @Override
            public void onOperaFailure() {

            }
        });
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        switch (event.getType()) {
            case BusEvent.OPERA_MERCHANT_SUCCESS:
                MerChantBean merChantBean = (MerChantBean) event.getParam();
                boolean follow = merChantBean.is_follow();
                int follow_num = merChantBean.getFollow_num();
                if (follow) {
                    mTv_in_head_for_merchant_follows_lv_attention.setText(getString(R.string.merchant_detail_followed));
                } else {
                    mTv_in_head_for_merchant_follows_lv_attention.setText(getString(R.string.merchant_detail_followed_not));
                }
                mTv_in_head_for_merchant_follows_lv_followsnum.setText(getString(R.string.merchant_detail_follows_space) + follow_num);
                break;
            case BusEvent.REQUEST_FAIL:
                CustomToast.makeText(this, event.getStrParam(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /*@Override
    protected View getAnchorView() {
        return mImageViewLeft;
    }*/
}
