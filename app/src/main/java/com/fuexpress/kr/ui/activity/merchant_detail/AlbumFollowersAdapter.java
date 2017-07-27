package com.fuexpress.kr.ui.activity.merchant_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.model.UserInfoBean;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.ClassUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import fksproto.CsBase;
import fksproto.CsItem;

/**
 * Created by Longer on 2017/6/26.
 */
public class AlbumFollowersAdapter extends BaseAdapter implements View.OnClickListener {

    private final ImageLoader mImageLoader;
    private final DisplayImageOptions mImageOptions;
    private final int mPadding;
    private List<CsBase.Follower> mFollowers;
    private Activity mContext;
    private int mSize;
    private final ArrayMap mUserLikes;
    private final ChildClickListener childClickListener;

    public AlbumFollowersAdapter(Activity context, List<CsBase.Follower> followers) {
        this.mContext = context;
        mFollowers = followers;
        mImageLoader = ImageLoader.getInstance();
        mImageOptions = ImageLoaderHelper.getInstance(mContext).getDisplayOptions();
        mPadding = UIUtils.dip2px(1);
        mUserLikes = new ArrayMap();
        childClickListener = new ChildClickListener();
    }


    public void setDate(List<CsBase.Follower> followers) {
        mFollowers = followers;
    }

    public void addItems(List<CsBase.Follower> followers) {
        mFollowers.addAll(followers);
    }

    public void notifyRefresh() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mFollowers != null) {
            return mFollowers.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mFollowers != null) {
            return mFollowers.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate((Activity) mContext, R.layout.item_for_item_follow_lv, null);
            ImageView imgIcon = (ImageView) convertView.findViewById(R.id.img_follow_icon);
            imgIcon.setCropToPadding(true);
            imgIcon.setPadding(mPadding, mPadding, mPadding, mPadding);
            TextView tvNickName = (TextView) convertView.findViewById(R.id.tv_follow_name);
            TextView tvLocation = (TextView) convertView.findViewById(R.id.tv_follow_location);
            LinearLayout llContainer = (LinearLayout) convertView.findViewById(R.id.ll_follows_pictures);

            int width = UIUtils.dip2px(50);
            int marginLeft = UIUtils.dip2px(8);
            int padding = UIUtils.dip2px(1);
            int screenWidth = mContext.getWindowManager().getDefaultDisplay().getWidth();

            mSize = (screenWidth - 2 * marginLeft) / (width + marginLeft);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);

            for (int i = 0; i < mSize; i++) {
                ImageView img = new ImageView(mContext);
                img.setPadding(padding, padding, padding, padding);
                img.setCropToPadding(true);
                img.setBackground(mContext.getResources().getDrawable(R.drawable.shape_album_circle_bg));
                img.setImageDrawable(mContext.getResources().getDrawable(R.color.home_add_and_like_bg));
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                params.setMargins(marginLeft, 0, 0, 0);
                llContainer.addView(img, params);
            }

            holder.imgIcon = imgIcon;
            holder.tvNickName = tvNickName;
            holder.tvLocation = tvLocation;
            holder.llContainer = llContainer;
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final CsBase.UserInfo user = mFollowers.get(position).getUser();
        Log.i("kevin ", "user.nickname = " + user.getNickname());
        String icon = user.getAvatar() + Constants.ImgUrlSuffix.small_9;
        String nickname = user.getNickname();
        //String region = AssetFileManager.getInstance().readFilePlus(user.getRegion(), AssetFileManager.FOLLOW_TYPE);
        // TODO: 2017/6/26 获取全地址名,缺少字段暂时注释
        String region = user.getFullRegionName();
        holder.tvLocation.setText(region);

        if ("".equals(region)) {
            //holder.tvLocation.setVisibility(View.GONE);
            holder.tvLocation.setCompoundDrawables(null, null, null, null);
        } else {
            holder.tvLocation.setVisibility(View.VISIBLE);
        }
        holder.imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/6/26 暂时不需要跳转到个人详情页
                /*UserInfoBean userInfoBean = ClassUtil.conventUser2UserInfoBean(user);

                //startActivityWithBundle(MeActivity.class,MeActivity.USER_INFO_BEAN,userInfoBean);
                Intent intent = new Intent(mContext, PersonInfoActivity.class);
                intent.putExtra(MeActivity.USER_INFO_BEAN, userInfoBean.uin);
                intent.putExtra(MeActivity.STATE, 0);
                mContext.startActivity(intent);*/
            }
        });

        mImageLoader.displayImage(icon, holder.imgIcon, ImageLoaderHelper.getInstance(mContext).getDisplayOptionsIcon());
        holder.tvNickName.setText(nickname);


        if (mUserLikes.get(position) != null) {
            List<CsBase.Item> items = (List<CsBase.Item>) mUserLikes.get(position);
            showItems(items, holder.llContainer);
            Log.d("show_item", "memory");
        } else {
            getUserLikeItem(user.getUin(), holder.llContainer, position);
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        CsBase.UserInfo user = (CsBase.UserInfo) v.getTag(R.id.img_follow_icon);
        UserInfoBean userInfoBean = ClassUtil.conventUser2UserInfoBean(user);

        //startActivityWithBundle(MeActivity.class,MeActivity.USER_INFO_BEAN,userInfoBean);
        /*Intent intent = new Intent(mContext, PersonInfoActivity.class);
        intent.putExtra(MeActivity.USER_INFO_BEAN, userInfoBean.uin);
        intent.putExtra(MeActivity.STATE, 0);
        mContext.startActivity(intent);*/
    }

    class Holder {
        public ImageView imgIcon;
        public TextView tvNickName;
        public TextView tvLocation;
        public LinearLayout llContainer;
    }


    public void getUserLikeItem(int uid, final ViewGroup viewGroup, final int postion) {
        for (int i = 0; i < mSize; i++) {
            ImageView child = (ImageView) viewGroup.getChildAt(i);
            child.setImageResource(R.color.default_image_background);
            child.setOnClickListener(null);
        }
        // TODO: 2017/6/26 接口暂时没完成,先注释
        CsItem.GetMyItemListRequest.Builder builder = CsItem.GetMyItemListRequest.newBuilder();
        builder.setAuthor(uid);
        builder.setPageno(1);
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsItem.GetMyItemListResponse>() {
            @Override
            public void onSuccess(CsItem.GetMyItemListResponse response) {
                final List<CsBase.Item> itemsList = response.getItemsList();
                mUserLikes.put(postion, itemsList);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showItems(itemsList, viewGroup);
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    private void showItems(List<CsBase.Item> itemsList, ViewGroup viewGroup) {
        for (int i = 0; i < mSize; i++) {
            ImageView child = (ImageView) viewGroup.getChildAt(i);
            child.setImageResource(R.color.default_image_background);
            child.setOnClickListener(null);
        }
        int leng = mSize < itemsList.size() ? mSize : itemsList.size();
        for (int i = 0; i < leng; i++) {
            final ItemBean item = ClassUtil.convertItem2ItemBean(itemsList.get(i));
            final ImageView child = (ImageView) viewGroup.getChildAt(i);
            ImageLoader.getInstance().displayImage(item.getImageUrl() + Constants.ImgUrlSuffix.small_9, child, mImageOptions);
            View childAt = viewGroup.getChildAt(i);
            childAt.setTag(item);
            childAt.setOnClickListener(childClickListener);
           /* childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent((Activity) mContext, ProductDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ProductDetailActivity.ITEM_BEAN, item);
                    intent.putExtra(ProductDetailActivity.ITEM_BEAN, bundle);
                    mContext.startActivity(intent);
                }
            });*/
        }
    }


    class ChildClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO: 2017/6/26  暂时不需要跳转到单品详情
            /*ItemBean item = (ItemBean) v.getTag();
            Intent intent = new Intent((Activity) mContext, ProductDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ProductDetailActivity.ITEM_BEAN, item);
            intent.putExtra(ProductDetailActivity.ITEM_BEAN, bundle);
            mContext.startActivity(intent);*/
        }
    }
}
