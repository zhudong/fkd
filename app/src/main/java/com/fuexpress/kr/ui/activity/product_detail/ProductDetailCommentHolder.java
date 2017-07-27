package com.fuexpress.kr.ui.activity.product_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseHolder;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.model.ShareManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.PhotoViewActivity;
import com.fuexpress.kr.ui.activity.order_show.OrderShowActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.utils.TimeUtils;
import com.google.protobuf.GeneratedMessage;
import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;
import fksproto.CsItem;

public class ProductDetailCommentHolder extends BaseHolder<ItemBean> implements View.OnClickListener {
    public static String TAG = "productdetailcommentholder";


    private View mRootView;
    private List<CsItem.Comment> mCommentsList;
    private List<CsItem.ReviewUserInfo> mCommentUserList;
    private int mIconPadding;

    private static Handler mHandler = new Handler();
    private DisplayImageOptions mImageOptions;

    private LinearLayout mLlCommentContainer;
    private EditText mEtCommentContent;
    private TextView mBtnCommentRelease;
    private ItemBean mItemBean;
    private TextView mTvLikerCount;
    private int commentCount;
    private TextView mTvShippingName;
    private TextView mTvShippingInfo;
    private String mCountryname;
    private CsItem.GetMatchShippingInfoResponse matchShippingInfo;
    private TextView mTvCommentCount;
    private int mCommentsCount;

    public ProductDetailCommentHolder(Activity context) {
        super(context);
    }

    @Override
    public View initView() {
        mRootView = View.inflate(mContext, R.layout.view_for_product_comment, null);
        mLlCommentContainer = (LinearLayout) mRootView.findViewById(R.id.ll_comment_container);
        mEtCommentContent = (EditText) mRootView.findViewById(R.id.et_comment_content);
        mBtnCommentRelease = (TextView) mRootView.findViewById(R.id.btn_comment_release);
        mTvLikerCount = (TextView) mRootView.findViewById(R.id.tv_liker_count);
        mTvCommentCount = (TextView) mRootView.findViewById(R.id.tv_comment_count);
        mRootView.findViewById(R.id.btn_comment_release_img).setOnClickListener(this);

        mRootView.findViewById(R.id.rl_product_detail).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_shipping_current).setOnClickListener(this);
        mTvShippingName = (TextView) mRootView.findViewById(R.id.tv_shipping_name);
        mTvShippingInfo = (TextView) mRootView.findViewById(R.id.tv_shipping_info);
        return mRootView;
    }

    @Override
    public void initData(ItemBean itemBean) {
        mItemBean = itemBean;
        mIconPadding = UIUtils.dip2px(1);
        mImageOptions = ImageLoaderHelper.getInstance(mContext).getDisplayOptions();
        getComment(itemBean.getItemid());
        initEvent();
//        getProductShipping();
        AccountManager.getInstance().getUserinfo();
    }

    @Override
    public void initEvent() {
        mBtnCommentRelease.setOnClickListener(this);
        mEtCommentContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 70) {
                    mEtCommentContent.setText(s.subSequence(0, 70));
                    mEtCommentContent.setSelection(70);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void getComment(long itemID) {
        CsItem.GetReviewListRequest.Builder builder = CsItem.GetReviewListRequest.newBuilder();
        builder.setMatchItemid((int) itemID).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsItem.GetReviewListResponse>() {

            @Override
            public void onSuccess(final CsItem.GetReviewListResponse response) {
                mCommentsList = response.getCommentsList();
                mCommentUserList = response.getReviewerinfoList();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showComments();
                        mCommentsCount = response.getTotal();
                        showCommentCounts(mCommentsCount);
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }


    public void showCommentCounts(int count) {
        mTvCommentCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        mTvCommentCount.setText(mContext.getString(R.string.String_comment_count, count));
    }


    public void posComment(long itemID) {
        boolean userLogin = AccountManager.getInstance().isUserLogin(mContext);
        if (!userLogin)
            return;

        if (TextUtils.isEmpty(mEtCommentContent.getText()))
            return;
        mBtnCommentRelease.setEnabled(false);
        CsItem.PostCommentRequest.Builder builder = CsItem.PostCommentRequest.newBuilder();
        builder.setItemId(itemID).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setContent(mEtCommentContent.getText().toString());
        NetEngine.postRequest(builder, new INetEngineListener<CsItem.PostCommentResponse>() {

            @Override
            public void onSuccess(CsItem.PostCommentResponse response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        commentSucess();
                        mBtnCommentRelease.setEnabled(true);
                        mCommentsCount++;
                        showCommentCounts(mCommentsCount);
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }


    private void showComments() {
        String commmentCount = mContext.getString(R.string.String_comment_count);
        commentCount = mCommentsList.size();
        commmentCount = String.format(commmentCount, commentCount);
       /* mTvCommentCount.setText(commmentCount);
        if (mCommentsList.size() == 0) {
            mTvCommentCount.setVisibility(View.GONE);
        }*/

        for (int i = 0; i < mCommentsList.size(); i++) {
            View view = View.inflate(mContext, R.layout.item_for_product_comment, null);
            TextView com = (TextView) view.findViewById(R.id.tv_person_comment);
            TextView time = (TextView) view.findViewById(R.id.tv_comment_date);
            ImageView icon = (ImageView) view.findViewById(R.id.img_person_icon);
            View share_comment = view.findViewById(R.id.img_share_comment);
            LinearLayout commentImgs = (LinearLayout) view.findViewById(R.id.ll_imgs);
            icon.setCropToPadding(true);
            icon.setPadding(mIconPadding, mIconPadding, mIconPadding, mIconPadding);

            TextView name = (TextView) view.findViewById(R.id.tv_person_nicke_name);
            final CsItem.Comment comment = mCommentsList.get(i);
            share_comment.setVisibility(comment.getImagesCount() > 0 ? View.VISIBLE : View.GONE);
            share_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    // TODO: 2017/7/5  评论晒单
                    reCommend(comment);
                }
            });

            com.setText(comment.getContent());
            CsItem.ReviewUserInfo commentUser = null;
            int uin = comment.getUin();
            for (CsItem.ReviewUserInfo user : mCommentUserList) {
                if (uin == user.getUin()) {
                    commentUser = user;
                    name.setText(user.getNickname());
                    ImageLoader.getInstance().displayImage(user.getAvatar() + Constants.ImgUrlSuffix.small_9, icon, ImageLoaderHelper.getInstance(mContext).getDisplayOptionsIcon());
                }
            }
            time.setText(comment.getReviewTime());
            mLlCommentContainer.addView(view);
            showCommentImgs(comment, commentImgs);
            final CsItem.ReviewUserInfo finalUser = commentUser;
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    toMeActivity(finalUser);
                }
            });
        }
    }

    private void showCommentImgs(final CsItem.Comment comment, LinearLayout llCommentContainer) {
        if (comment.getImagesCount() > 0) {
            llCommentContainer.setVisibility(View.VISIBLE);
        } else {
            llCommentContainer.setVisibility(View.GONE);
        }

        int width = UIUtils.dip2px((float) 50);
        int marginLeft = UIUtils.dip2px((float) 8);
        int padding = UIUtils.dip2px((float) 1);
        int screenWidth = mContext.getWindowManager().getDefaultDisplay().getWidth();
        int mLikersItemCount = (screenWidth) / (width + marginLeft);
        mLikersItemCount = mLikersItemCount < comment.getImagesCount() ? mLikersItemCount : comment.getImagesCount();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);

        for (int i = 0; i < mLikersItemCount; i++) {
            ImageView img = new ImageView(mContext);
            img.setPadding(padding, padding, padding, padding);
            img.setCropToPadding(true);
            img.setBackground(mContext.getResources().getDrawable(R.drawable.shape_album_circle_bg));
            img.setImageDrawable(mContext.getResources().getDrawable(R.color.home_add_and_like_bg));
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            params.setMargins(marginLeft, 0, 0, 0);
            llCommentContainer.addView(img, params);
            ImageLoader.getInstance().displayImage(comment.getImages(i).getImageUrl(), img, ImageLoaderHelper.getInstance(mContext).getDisplayOptionsIcon());
            final int finalI = i;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goActivity(finalI, comment.getImagesList());
                }
            });
        }
    }

    protected void goActivity(int index, List<CsBase.ItemImage> itemImages) {
        Intent intent = new Intent(mContext, PhotoViewActivity.class);
        intent.putExtra(PhotoViewActivity.PAGE_INDEX, index);
        ArrayList<String> imgs = new ArrayList<String>();
        for (CsBase.ItemImage itemImage : itemImages) {
            imgs.add(itemImage.getImageUrl());
        }
        intent.putStringArrayListExtra(PhotoViewActivity.PAGE_IMAGES, imgs);
//        intent.putExtra(PhotoViewActivity.PAGE_INDEX, mVpItemImgs.getCurrentItem());
        mContext.startActivity(intent);
    }


    private void commentSucess() {
      /*  commentCount++;
        String count = mContext.getString(R.string.String_comment_count, commentCount);
        if (commentCount > 0) mTvCommentCount.setVisibility(View.VISIBLE);
        mTvCommentCount.setText(count);*/

        View view = View.inflate(mContext, R.layout.item_for_product_comment, null);
        TextView com = (TextView) view.findViewById(R.id.tv_person_comment);
        TextView name = (TextView) view.findViewById(R.id.tv_person_nicke_name);
        TextView time = (TextView) view.findViewById(R.id.tv_comment_date);
        ImageView icon = (ImageView) view.findViewById(R.id.img_person_icon);
        icon.setCropToPadding(true);
        icon.setPadding(mIconPadding, mIconPadding, mIconPadding, mIconPadding);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                toMeActivity(AccountManager.getInstance().userInfo);
            }
        });

        ImageLoader.getInstance().displayImage(AccountManager.getInstance().avater, icon, ImageLoaderHelper.getInstance(mContext).getDisplayOptionsIcon());
        name.setText(AccountManager.getInstance().nikename);
        com.setText(mEtCommentContent.getText().toString());
        time.setText(TimeUtils.getDateStyleStringByLong(System.currentTimeMillis()));
        mEtCommentContent.setText("");
        //mBtnCommentRelease.setEnabled(true);
        mLlCommentContainer.addView(view);
        if (mLlCommentContainer.getChildCount() > 5) {
            mLlCommentContainer.removeViewAt(0);
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_comment_release:
                posComment(mItemBean.getItemid());
                break;
            case R.id.rl_product_detail:
//                ((ProductDetailActivity) mContext).start2ProductDetail();
                break;
            case R.id.btn_comment_release_img:
                intent = new Intent(mContext, OrderShowActivity.class);
                intent.putExtra(OrderShowActivity.KEY_ITEM_ID, (int) mItemBean.getItemid());
                mContext.startActivity(intent);
                break;
            case R.id.rl_shipping_current:
//                if (!AccountManager.isLogin) return;
                intent = new Intent(mContext, ProductShippingActivity.class);
                intent.putExtra(ProductShippingActivity.ITEM_ID, mItemBean.getItemid());
                Bundle bundle = new Bundle();
                bundle.putSerializable(ProductShippingActivity.DATA, matchShippingInfo);
                intent.putExtra(ProductShippingActivity.DATA, bundle);
                mContext.startActivityForResult(intent, ProductShippingActivity.requestCode);
                break;
        }
    }


    public void getProductShipping() {
        AccountManager instance = AccountManager.getInstance();
        CsItem.GetMatchShippingInfoRequest.Builder builder = CsItem.GetMatchShippingInfoRequest.newBuilder()
                .setMatchitemid((int) mItemBean.getItemid())
                .setCurrencycode(instance.getCurrencyCode())
                .setLocalecode(instance.getLocaleCode()).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        if (AccountManager.isLogin) {
            builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest()).setCountrycode(instance.countryCode).setRegionid(instance.regionID);
        }

        NetEngine.postRequest(builder, new INetEngineListener<CsItem.GetMatchShippingInfoResponse>() {
            @Override
            public void onSuccess(final CsItem.GetMatchShippingInfoResponse response) {
                matchShippingInfo = response;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTvShippingName.setText(response.getTitle());
                        mTvShippingInfo.setText(response.getShippinginfo());
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
//                LogUtils.d(errMsg);
            }
        });

    }

    private boolean clicking = false;

    private void reCommend(final CsItem.Comment comment) {
        if (clicking) return;
        clicking = true;

        String id = comment.getMatchReviewid();
        if (TextUtils.isEmpty(id)) return;
        CsItem.DoReCommendReviewRequest.Builder builder = CsItem.DoReCommendReviewRequest.newBuilder().setMatchreviewid(Integer.valueOf(id)).setUserinfo(AccountManager.getInstance().getBaseUserRequest()).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsItem.DoReCommendReviewResponse>() {

            @Override
            public void onSuccess(CsItem.DoReCommendReviewResponse response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        clicking = false;
//                        CustomToast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                        ShareManager.initWithRes(comment.getImagesList(), comment.getContent(), mContext);
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                clicking = false;
            }
        });
    }


    public void refreshComments() {
        mLlCommentContainer.removeAllViews();
        getComment(mItemBean.getItemid());
        showCommentCounts(mCommentsCount);
    }

    public void addCount() {
        mCommentsCount++;
    }
}