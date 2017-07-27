package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import fksproto.CsMerchant;

/**
 * Created by longer on 2017/6/28.
 */

public class NewAlbumFollowersAdapter extends BaseAdapter implements View.OnClickListener {

    private final ImageLoader mImageLoader;
    private final DisplayImageOptions mImageOptions;
    private final int mPadding;
    private List<CsMerchant.FollowerList> mFollowers;
    private Activity mContext;
    private int mSize;
    private final ArrayMap mUserLikes;
    private final ChildClickListener childClickListener;

    public NewAlbumFollowersAdapter(Activity context, List<CsMerchant.FollowerList> followers) {
        this.mContext = context;
        mFollowers = followers;
        mImageLoader = ImageLoader.getInstance();
        mImageOptions = ImageLoaderHelper.getInstance(mContext).getDisplayOptions();
        mPadding = UIUtils.dip2px(1);
        mUserLikes = new ArrayMap();
        childClickListener = new ChildClickListener();
    }


    public void setDate(List<CsMerchant.FollowerList> followers) {
        mFollowers = followers;
    }

    public void addItems(List<CsMerchant.FollowerList> followers) {
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
        //return 4;
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

            int width = UIUtils.dip2px((float) 50);
            int marginLeft = UIUtils.dip2px((float) 8);
            int padding = UIUtils.dip2px((float) 1);
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

        //final CsBase.UserInfo user = mFollowers.get(position).getUser();
        //Log.i("kevin ", "user.nickname = " + user.getNickname());
        CsMerchant.FollowerList followerList = mFollowers.get(position);
        String icon = followerList.getIconsmall9();
        String nickname = followerList.getNickname();
        //String region = followerList.getRegionname();
        //String region = AssetFileManager.getInstance().readFilePlus(followerList.getRegionname(), AssetFileManager.FOLLOW_TYPE);
        String region = followerList.getRegionname();
        holder.imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        holder.tvLocation.setText(region);

        if ("".equals(region)) {
            holder.tvLocation.setCompoundDrawables(null, null, null, null);
        } else {
            holder.tvLocation.setVisibility(View.VISIBLE);
        }
        //showItems(holder.llContainer);
        if (mUserLikes.get(position) != null) {
            List<CsMerchant.MatchRelateList> matchrelatelistList = (List<CsMerchant.MatchRelateList>) mUserLikes.get(position);
            showItems(matchrelatelistList, holder.llContainer);
            //Log.d("show_item", "memory");
        } else {
            getUserLikeItem(followerList.getNickname(), holder.llContainer, position);
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        /*CsBase.UserInfo user = (CsBase.UserInfo) v.getTag(R.id.img_follow_icon);
        UserInfoBean userInfoBean = ClassUtil.conventUser2UserInfoBean(user);

        //startActivityWithBundle(MeActivity.class,MeActivity.USER_INFO_BEAN,userInfoBean);
        Intent intent = new Intent(mContext, PersonInfoActivity.class);
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


    public void getUserLikeItem(String nickName, final ViewGroup viewGroup, final int postion) {
        for (int i = 0; i < mSize; i++) {
            ImageView child = (ImageView) viewGroup.getChildAt(i);
            child.setImageResource(R.color.default_image_background);
            child.setOnClickListener(null);
        }
        CsMerchant.FollowerList followerList = mFollowers.get(postion);
        final List<CsMerchant.MatchRelateList> matchrelatelistList = followerList.getMatchrelatelistList();
        for (int i = 0; i < matchrelatelistList.size(); i++) {
            mUserLikes.put(nickName, matchrelatelistList);
        }
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showItems(matchrelatelistList, viewGroup);
            }
        });
    }

    private void showItems(List<CsMerchant.MatchRelateList> matchRelateLists, ViewGroup viewGroup) {
        for (int i = 0; i < mSize; i++) {
            ImageView child = (ImageView) viewGroup.getChildAt(i);
            child.setImageResource(R.color.default_image_background);
            child.setOnClickListener(null);
        }
        int leng = mSize < matchRelateLists.size() ? mSize : matchRelateLists.size();
        for (int i = 0; i < leng; i++) {
            //final ItemBean item = ClassUtil.convertItem2ItemBean(itemsList.get(i));
            CsMerchant.MatchRelateList matchRelateList = matchRelateLists.get(i);
            final ImageView child = (ImageView) viewGroup.getChildAt(i);
            ImageLoader.getInstance().displayImage(matchRelateList.getSmall9Imageurl(), child, mImageOptions);
            View childAt = viewGroup.getChildAt(i);
            //childAt.setTag(item);
            //childAt.setOnClickListener(childClickListener);
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
            /*ItemBean item = (ItemBean) v.getTag();
            Intent intent = new Intent((Activity) mContext, ProductDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ProductDetailActivity.ITEM_BEAN, item);
            intent.putExtra(ProductDetailActivity.ITEM_BEAN, bundle);
            mContext.startActivity(intent);*/
        }
    }
}
