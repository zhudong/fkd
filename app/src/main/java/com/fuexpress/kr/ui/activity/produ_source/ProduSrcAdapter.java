package com.fuexpress.kr.ui.activity.produ_source;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.model.MerChantBean;
import com.fuexpress.kr.ui.activity.merchant_detail.MerChantDetailActivity02;
import com.fuexpress.kr.ui.activity.product_detail.ProductDetailActivity;
import com.fuexpress.kr.ui.uiutils.GlideRoundTransform;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.LikeOrFollowBtn;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longer on 2017/6/23.
 */
public class ProduSrcAdapter extends BaseAdapter implements View.OnClickListener {


    private int mScreenWidthPixels;
    private LayoutInflater mInflater;
    private LongSparseArray<List<Long>> mLoaclMapForPairInAdapter;
    private LongSparseArray<ItemBean> mLoaclMapForItemsInAdapter;
    private List<MerChantBean> mAllMerchantsLocalList;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayOptions;
    public int mPosition;
    public boolean isGetStoreOrEmpty = false;
    public static final int FAIL_TYPE = 4;
    private Context mContext;
    private GlideRoundTransform mGlideRoundTransform;
    private RequestManager mGlideWith;
    DisplayImageOptions roundImageLoaderOption = SysApplication.getRoundImageLoaderOption();
    private String mType = "";

    public ProduSrcAdapter(Context context, List<MerChantBean> merchantsLocalList, LongSparseArray<ItemBean> localMapForItems, LongSparseArray<List<Long>> pairsList) {
        //总的我们需要显示的MerchantsLocalList
        mAllMerchantsLocalList = merchantsLocalList;
        //item的map集合:,用于给我们取出值
        mLoaclMapForItemsInAdapter = localMapForItems;
        //对应的商铺和其对应的单品的键值对集合:
        mLoaclMapForPairInAdapter = pairsList;
        mImageLoader = ImageLoader.getInstance();
        mInflater = LayoutInflater.from(SysApplication.getContext());
        mDisplayOptions = ImageLoaderHelper.getInstance(SysApplication.getContext()).getDisplayOptions();
        mContext = context;
        mGlideRoundTransform = new GlideRoundTransform(mContext, 5);
        mGlideWith = Glide.with(mContext);
        //获取屏幕宽:
        mScreenWidthPixels = UIUtils.getScreenWidthPixels((Activity) mContext);
    }

    public ProduSrcAdapter(int type) {
        if (FAIL_TYPE == type) {
            isGetStoreOrEmpty = true;
        }
    }


    @Override
    public int getCount() {
        if (isGetStoreOrEmpty) {
            return 0;
        }
        if (mAllMerchantsLocalList != null) {
            return mAllMerchantsLocalList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mAllMerchantsLocalList != null) {
            mAllMerchantsLocalList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            //convertView = View.inflate(SysApplication.getContext(), R.layout.item_for_store_lv_02, null);
            convertView = mInflater.inflate(R.layout.item_produ_src_ban, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_for_item_store_fragment_01 = (ImageView) convertView.findViewById(R.id.iv_for_item_store_fragment_01);
            viewHolder.iv_for_item_store_fragment_02 = (ImageView) convertView.findViewById(R.id.iv_for_item_store_fragment_02);
            viewHolder.iv_for_item_store_fragment_03 = (ImageView) convertView.findViewById(R.id.iv_for_item_store_fragment_03);
            viewHolder.iv_for_item_store_fragment_04 = (ImageView) convertView.findViewById(R.id.iv_for_item_store_fragment_04);
            viewHolder.iv_for_item_store_fragment_05 = (ImageView) convertView.findViewById(R.id.iv_for_item_store_fragment_05);
            viewHolder.iv_for_item_store_fragment_06 = (ImageView) convertView.findViewById(R.id.iv_for_item_store_fragment_06);
            viewHolder.iv_for_item_store_fragment_07 = (ImageView) convertView.findViewById(R.id.iv_for_item_store_fragment_07);
            viewHolder.iv_for_item_store_fragment_08 = (ImageView) convertView.findViewById(R.id.iv_for_item_store_fragment_08);
            viewHolder.iv_for_item_store_fragment_09 = (ImageView) convertView.findViewById(R.id.iv_for_item_store_fragment_09);
            viewHolder.iv_merchant_head_in_item_for_store_fragment = (ImageView) convertView.findViewById(R.id.iv_merchant_head_in_item_for_store_fragment);
            viewHolder.tv_merchant_name_in_item_for_store_fragment = (TextView) convertView.findViewById(R.id.tv_merchant_name_in_item_for_store_fragment);
            viewHolder.tv_merchant_location_in_item_for_store_fragment = (TextView) convertView.findViewById(R.id.tv_merchant_location_in_item_for_store_fragment);
            viewHolder.tv_follow_num_in_item_for_store_fragment_02 = (TextView) convertView.findViewById(R.id.tv_follow_num_in_item_for_store_fragment_02);
            viewHolder.rl_merchant_head_in_item_for_store_fragment_go_detail = (RelativeLayout) convertView.findViewById(R.id.rl_merchant_head_in_item_for_store_fragment_go_detail);
            viewHolder.rl_merchant_head_in_item_for_store_fragment_go_detail.setOnClickListener(this);
            viewHolder.hsl_for_item_store_fragment = (HorizontalScrollView) convertView.findViewById(R.id.hsl_for_item_store_fragment);
            viewHolder.rl_for_item_store_fragment_last = (RelativeLayout) convertView.findViewById(R.id.rl_for_item_store_fragment_last);
            viewHolder.rl_for_item_store_fragment_last.setOnClickListener(this);
            viewHolder.lofb_in_store_fragment = (LikeOrFollowBtn) convertView.findViewById(R.id.lofb_in_store_fragment);
            viewHolder.lofb_in_store_fragment.setOnClickListener(this);
            viewHolder.iv_src_mer_image = (ImageView) convertView.findViewById(R.id.iv_src_mer_image);
            viewHolder.iv_src_mer_image.setOnClickListener(this);
            viewHolder.ll_parent = (LinearLayout) convertView.findViewById(R.id.ll_parent);
            //把这几个需要用到的作为成员变量保存起来:
            //mTv_follow_num_in_item_for_store_fragment_02 = viewHolder.tv_follow_num_in_item_for_store_fragment_02;
            //viewHolder.rl_follow_in_item_for_store_fragment = (RelativeLayout) convertView.findViewById(R.id.rl_follow_in_item_for_store_fragment);
            //viewHolder.rl_follow_in_item_for_store_fragment.setOnClickListener(this);
            viewHolder.iv_follow_in_item_for_store_fragment = (ImageView) convertView.findViewById(R.id.iv_follow_in_item_for_store_fragment);

            //mIv_follow_in_item_for_store_fragment = viewHolder.iv_follow_in_item_for_store_fragment;
            viewHolder.tv_follow_in_item_for_store_fragment = (TextView) convertView.findViewById(R.id.tv_follow_in_item_for_store_fragment);
            //mTv_follow_in_item_for_store_fragment = viewHolder.tv_follow_in_item_for_store_fragment;


            //然后添加到集合中去;
            viewHolder.addToList(viewHolder.iv_for_item_store_fragment_01);
            viewHolder.addToList(viewHolder.iv_for_item_store_fragment_02);
            viewHolder.addToList(viewHolder.iv_for_item_store_fragment_03);
            viewHolder.addToList(viewHolder.iv_for_item_store_fragment_04);
            viewHolder.addToList(viewHolder.iv_for_item_store_fragment_05);
            viewHolder.addToList(viewHolder.iv_for_item_store_fragment_06);
            viewHolder.addToList(viewHolder.iv_for_item_store_fragment_07);
            viewHolder.addToList(viewHolder.iv_for_item_store_fragment_08);
            viewHolder.addToList(viewHolder.iv_for_item_store_fragment_09);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //DisplayImageOptions roundImageLoaderOption = SysApplication.getRoundImageLoaderOption();

        //然后就拿出数据;
        MerChantBean merChantBean = mAllMerchantsLocalList.get(position);
        long merchantid = merChantBean.getMerchantid();
        // TODO: 2017/6/23 显示封面的缩略图
        //viewHolder.iv_src_mer_image
        if (!TextUtils.isEmpty(merChantBean.getCover())) {
            mImageLoader.displayImage(merChantBean.getCover() + Constants.ImgUrlSuffix.small_9, viewHolder.iv_merchant_head_in_item_for_store_fragment, roundImageLoaderOption);
            /*mGlideWith.load(merChantBean.getCover() + Constants.ImgUrlSuffix.small_9)
                    .transform(mGlideRoundTransform).placeholder(R.mipmap.image_store)
                    .error(R.mipmap.image_store).into(viewHolder.iv_merchant_head_in_item_for_store_fragment);*/
        } else {
            viewHolder.iv_merchant_head_in_item_for_store_fragment.setImageResource(R.mipmap.brand);
        }
        viewHolder.tv_merchant_name_in_item_for_store_fragment.setText(merChantBean.getTitle());
        viewHolder.tv_merchant_location_in_item_for_store_fragment.setText(merChantBean.getPlace());
        viewHolder.tv_follow_num_in_item_for_store_fragment_02.setText(String.valueOf(merChantBean.getFollow_num()));
        //根据商店id,拿到其对应的单品:
        List<Long> ItemsIdList = mLoaclMapForPairInAdapter.get(merchantid);
        List<ImageView> imageView = viewHolder.getImageView(ItemsIdList == null ? 0 : ItemsIdList.size());
        if (ItemsIdList != null) {//有时候会有这东西不出现在当前页的情况
            //然后就进行遍历:
            for (int x = 0; x < ItemsIdList.size(); x++) {
                if (x >= imageView.size()) break;
                //根据其长度,拿到我们已经封装好的集合出来;
                ImageView imageView1 = imageView.get(x);
                //设置其显示:
                imageView1.setVisibility(View.VISIBLE);
                imageView1.setOnClickListener(this);
                //然后再拿到对应的Item:
                long key = ItemsIdList.get(x);
                ItemBean itemBean = mLoaclMapForItemsInAdapter.get(key);
                String imageUrl = itemBean.getImageUrl();
                long itemid = itemBean.getItemid();
                imageView1.setTag(itemid);
                String url = imageUrl + Constants.ImgUrlSuffix.dp_list;
                //然后让ImageLoader显示:
                mImageLoader.displayImage(url, imageView1, mDisplayOptions);
                //mGlideWith.load(url).into(imageView1);
                //Glide.with(SysApplication.getContext()).load(url).into(imageView1);
                //LogUtils.i("我已成功获取!" + s);
            }
        }
        //MerChantBean merChantBean = mAdapterMerchantBeanMap.get(String.valueOf(merchantid));
        boolean follow = merChantBean.is_follow();
        viewHolder.lofb_in_store_fragment.setLikeOrFollow(LikeOrFollowBtn.TYPE_FOLLOW, follow, merChantBean.getFollow_num());
        //LogUtils.e("我是po:" + position + "我返回了");
        //viewHolder.rl_follow_in_item_for_store_fragment.setTag(position);
        viewHolder.rl_merchant_head_in_item_for_store_fragment_go_detail.setTag(position);
        viewHolder.rl_for_item_store_fragment_last.setTag(position);
        viewHolder.hsl_for_item_store_fragment.fullScroll(HorizontalScrollView.FOCUS_UP);
        viewHolder.lofb_in_store_fragment.setTag(position);

        //福快递货源中,新增了一个预览图:
        //首先按照宽高比动态设置好imagView:
        String cover = merChantBean.getCover();
        if (!TextUtils.isEmpty(cover)) {
            viewHolder.iv_src_mer_image.setVisibility(View.VISIBLE);
            viewHolder.iv_src_mer_image.setMinimumHeight(UIUtils.dip2px(374));
            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) viewHolder.iv_src_mer_image.getLayoutParams();
            //按照1比1.5设置好长宽:
            int store_iv_height = (int) (mScreenWidthPixels / 1.5);
            layoutParams1.width = mScreenWidthPixels;
            layoutParams1.height = store_iv_height;
            viewHolder.iv_src_mer_image.setLayoutParams(layoutParams1);
            mImageLoader.displayImage(cover + Constants.ImgUrlSuffix.biz_list, viewHolder.iv_src_mer_image, mDisplayOptions);
        } else {
            viewHolder.iv_src_mer_image.setVisibility(View.GONE);
            viewHolder.ll_parent.setMinimumHeight(UIUtils.dip2px(180));
        }

        viewHolder.iv_src_mer_image.setTag(position);
        //mGlideWith.load(merChantBean.getCover() + Constants.ImgUrlSuffix.biz_list).into(viewHolder.iv_src_mer_image);
        return convertView;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lofb_in_store_fragment:
                if (AccountManager.isLogin) {
                    if (!UIUtils.isFastClick()) {
                        int opMerPosition = (int) v.getTag();
                        mPosition = opMerPosition;
                        LikeOrFollowBtn likeOrFollowBtn = (LikeOrFollowBtn) v;
                        likeOrFollowBtn.likeOrFollowOperation(LikeOrFollowBtn.TYPE_FOLLOW_OP_MERCHANT, mAllMerchantsLocalList.get(opMerPosition), mType);
                    }
                    //changMerchantIsFollow(opMerPosition);
                } else {
                    AccountManager.getInstance().isUserLogin(SysApplication.getContext());
                }

                break;
            case R.id.iv_for_item_store_fragment_01:
            case R.id.iv_for_item_store_fragment_02:
            case R.id.iv_for_item_store_fragment_03:
            case R.id.iv_for_item_store_fragment_04:
            case R.id.iv_for_item_store_fragment_05:
            case R.id.iv_for_item_store_fragment_06:
            case R.id.iv_for_item_store_fragment_07:
            case R.id.iv_for_item_store_fragment_08:
            case R.id.iv_for_item_store_fragment_09:
                long itemId = (long) v.getTag();
                // TODO: 2017/6/23 先屏蔽掉单品点击的事件,等未来放开
                //然后获得Item:
                ItemBean itemBean = mLoaclMapForItemsInAdapter.get(itemId);
                Intent intent = new Intent(SysApplication.getContext(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.ITEM_ID, itemId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ProductDetailActivity.ITEM_BEAN, itemBean);
                intent.putExtra(ProductDetailActivity.ITEM_BEAN, bundle);*/
                SysApplication.getContext().startActivity(intent);
                break;
            case R.id.rl_merchant_head_in_item_for_store_fragment_go_detail:
            case R.id.rl_for_item_store_fragment_last:
            case R.id.iv_src_mer_image:
                mPosition = (int) v.getTag();
                // TODO: 2017/6/23 等待移植商户详情页面
                MerChantBean merChantBean02 = mAllMerchantsLocalList.get(mPosition);
                long merchantId03 = merChantBean02.getMerchantid();
                //MerchantDetailManager.getInstance().getMerChantDetailRequest(merchantId03);
                Intent intent03 = new Intent(SysApplication.getContext(), MerChantDetailActivity02.class);
                //intent03.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent03.putExtra("merchantId", merchantId03);
                mContext.startActivity(intent03);
                break;
        }
    }


    static class ViewHolder {

        List<ImageView> ivList = new ArrayList<ImageView>();

        ImageView iv_merchant_head_in_item_for_store_fragment;
        TextView tv_merchant_name_in_item_for_store_fragment;
        TextView tv_merchant_location_in_item_for_store_fragment;
        TextView tv_follow_num_in_item_for_store_fragment_02;
        ImageView iv_for_item_store_fragment_01;
        ImageView iv_for_item_store_fragment_02;
        ImageView iv_for_item_store_fragment_03;
        ImageView iv_for_item_store_fragment_04;
        ImageView iv_for_item_store_fragment_05;
        ImageView iv_for_item_store_fragment_06;
        ImageView iv_for_item_store_fragment_07;
        ImageView iv_for_item_store_fragment_08;
        ImageView iv_for_item_store_fragment_09;
        //RelativeLayout rl_follow_in_item_for_store_fragment;
        ImageView iv_follow_in_item_for_store_fragment;
        TextView tv_follow_in_item_for_store_fragment;
        RelativeLayout rl_merchant_head_in_item_for_store_fragment_go_detail;
        HorizontalScrollView hsl_for_item_store_fragment;
        RelativeLayout rl_for_item_store_fragment_last;
        LikeOrFollowBtn lofb_in_store_fragment;
        ImageView iv_src_mer_image;
        LinearLayout ll_parent;


        public void addToList(ImageView imageView) {
            ivList.add(imageView);
        }

        public List<ImageView> getImageView(int sizeIndex) {
            if (sizeIndex != ivList.size()) {
                for (int x = sizeIndex; x < ivList.size(); x++) {
                    ivList.get(x).setVisibility(View.GONE);
                }
            }
            return ivList;
        }
    }

    public void setData(List<MerChantBean> merchantsLocalList, LongSparseArray<ItemBean> localMapForItems, LongSparseArray<List<Long>> pairsList) {
        //总的我们需要显示的MerchantsLocalList
        mAllMerchantsLocalList = merchantsLocalList;
        //item的map集合:,用于给我们取出值
        mLoaclMapForItemsInAdapter = localMapForItems;
        //对应的商铺和其对应的单品的键值对集合:
        mLoaclMapForPairInAdapter = pairsList;
    }

    public void setData(LongSparseArray<ItemBean> localMapForItems, LongSparseArray<List<Long>> pairsList) {
        //总的我们需要显示的MerchantsLocalList
        //mAllMerchantsLocalList = merchantsLocalList;
        //item的map集合:,用于给我们取出值
        mLoaclMapForItemsInAdapter = localMapForItems;
        //对应的商铺和其对应的单品的键值对集合:
        int size = pairsList.size();
        for (int x = 0; x < size; x++) {
            long key = pairsList.keyAt(x);
            mLoaclMapForPairInAdapter.put(key, pairsList.get(key));
        }

        //mLoaclMapForPairInAdapter = pairsList;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }
}
