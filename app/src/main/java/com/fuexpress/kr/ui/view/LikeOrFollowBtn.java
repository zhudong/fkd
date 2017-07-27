package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.model.MerChantBean;
import com.fuexpress.kr.model.OperaRequestManager;
import com.fuexpress.kr.ui.uiutils.UIUtils;

/**
 * Created by Longer on 2017/6/22.
 */
public class LikeOrFollowBtn extends LinearLayout {
    public static final String TYPE_LIKE = "liketype";
    public static final String TYPE_FOLLOW = "followtype";
    public static final String TYPE_LIKE_OR_UNLIKE_ING = "like_or_unlike_ing_type";
    public static final String TYPE_FOLLOW_OR_UNFOLLW_ING = "follow_or_follow_ing_type";
    public static final String TYPE_FOLLOW_OP_MERCHANT = "followmerchanttype";
    public static final String TYPE_FOLLOW_OP_ALBUM = "followalbumtype";
    private ImageView mIv_icon_for_likeorfollow_btn;
    private TextView mTv_for_likeorfollow_btn;
    private TextView mTv_count_for_likeorfollow_btn;

    public LikeOrFollowBtn(Context context) {
        this(context, null);
    }

    public LikeOrFollowBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_for_like_or_follow_btn, this);
        initView();
    }

    private void initView() {
        //mRootView = View.inflate(getContext(), R.layout.layout_for_like_or_follow_btn, null);
        mIv_icon_for_likeorfollow_btn = (ImageView) findViewById(R.id.iv_icon_for_likeorfollow_btn);
        mTv_for_likeorfollow_btn = (TextView) findViewById(R.id.tv_for_likeorfollow_btn);
        mTv_count_for_likeorfollow_btn = (TextView) findViewById(R.id.tv_count_for_likeorfollow_btn);
        //mLl_for_likeorfollow = (LinearLayout) mRootView.findViewById(R.id.ll_for_likeorfollow);
    }

    /**
     * @param type            类型,看上面的静态常量
     * @param isLikeOrFollow  是否关注or喜欢
     * @param likeOrFollowNum 喜欢or关注的数量
     * @return
     */
    public void setLikeOrFollow(String type, boolean isLikeOrFollow, int likeOrFollowNum) {
        if (TYPE_LIKE.equals(type)) {
            if (isLikeOrFollow) {
                mIv_icon_for_likeorfollow_btn.setImageResource(R.mipmap.unlike);
                mTv_for_likeorfollow_btn.setText(getResources().getString(R.string.String_btn_like));
            } else {
                mIv_icon_for_likeorfollow_btn.setImageResource(R.mipmap.like);
                mTv_for_likeorfollow_btn.setText(getResources().getString(R.string.String_btn_unlike));
            }
        } else if (TYPE_FOLLOW.equals(type)) {
            if (isLikeOrFollow) {
                mIv_icon_for_likeorfollow_btn.setImageResource(R.mipmap.unfocus);
                mTv_for_likeorfollow_btn.setText(getResources().getString(R.string.String_for_home_item_unfocus));
            } else {
                mIv_icon_for_likeorfollow_btn.setImageResource(R.mipmap.focus);
                mTv_for_likeorfollow_btn.setText(getResources().getString(R.string.String_for_home_item_focus));
            }
        } else if (TYPE_LIKE_OR_UNLIKE_ING.equals(type)) {
            if (isLikeOrFollow) {
                mTv_for_likeorfollow_btn.setText(getResources().getString(R.string.String_for_home_item_unliking));
            } else {
                mTv_for_likeorfollow_btn.setText(getResources().getString(R.string.String_for_home_item_liking));
            }
        } else if (TYPE_FOLLOW_OR_UNFOLLW_ING.equals(type)) {
            if (isLikeOrFollow) {
                mTv_for_likeorfollow_btn.setText(getResources().getString(R.string.String_for_home_item_not_add_attention_ing));
            } else {
                mTv_for_likeorfollow_btn.setText(getResources().getString(R.string.String_for_home_item_add_attention_ing));
            }
        }
        mTv_count_for_likeorfollow_btn.setText(String.valueOf(likeOrFollowNum));
        //return mLl_for_likeorfollow;
    }

    /**
     * @param type 类型,参考上面的静态常量
     * @param bean 需要修改的bean
     */
    public void likeOrFollowOperation(String type, final Object bean, String adapterType) {
        if (TYPE_LIKE.equals(type)) {
            final ItemBean itemBean = (ItemBean) bean;
            long id = itemBean.getItemid();
            final boolean isLike = itemBean.is_like;
            final int likeNum = itemBean.getLikeCount();
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    setLikeOrFollow(TYPE_LIKE_OR_UNLIKE_ING, isLike, likeNum);
                }
            });

            OperaRequestManager.getInstance().operateLikeItem(id, itemBean, null);
        } else if (TYPE_FOLLOW_OP_MERCHANT.equals(type)) {
            final MerChantBean merChantBean = (MerChantBean) bean;
            long merchantid = merChantBean.getMerchantid();
            final int follow_num = merChantBean.getFollow_num();
            final boolean follow = merChantBean.is_follow();
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    setLikeOrFollow(TYPE_FOLLOW_OR_UNFOLLW_ING, follow, follow_num);
                }
            });

            OperaRequestManager.getInstance().operateLikeMerchant(merchantid, merChantBean, null, adapterType);
        }/* else if (TYPE_FOLLOW_OP_ALBUM.equals(type))

        {
            final AlbumBean albumBean = (AlbumBean) bean;
            long albumId = albumBean.getAlbumId();
            final int isFollow = albumBean.getIsFollow();
            final int followCount = albumBean.getFollowCount();
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    boolean isFollowAlbum = false;
                    if (1 == isFollow) {
                        isFollowAlbum = true;
                    }
                    setLikeOrFollow(TYPE_FOLLOW_OR_UNFOLLW_ING, isFollowAlbum, followCount);
                }
            });
            OperaRequestManager.getInstance().operateLikeAlbum(albumId, albumBean, null);
        }*/

    }


    /**
     * @param type 类型,参考上面的静态常量
     * @param bean 需要修改的bean
     */
    public void likeOrFollowOperation(String type, final Object bean) {
        if (TYPE_LIKE.equals(type)) {
            final ItemBean itemBean = (ItemBean) bean;
            long id = itemBean.getItemid();
            final boolean isLike = itemBean.is_like;
            final int likeNum = itemBean.getLikeCount();
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    setLikeOrFollow(TYPE_LIKE_OR_UNLIKE_ING, isLike, likeNum);
                }
            });

            OperaRequestManager.getInstance().operateLikeItem(id, itemBean, null);
        } else if (TYPE_FOLLOW_OP_MERCHANT.equals(type)) {
            final MerChantBean merChantBean = (MerChantBean) bean;
            long merchantid = merChantBean.getMerchantid();
            final int follow_num = merChantBean.getFollow_num();
            final boolean follow = merChantBean.is_follow();
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    setLikeOrFollow(TYPE_FOLLOW_OR_UNFOLLW_ING, follow, follow_num);
                }
            });

            OperaRequestManager.getInstance().operateLikeMerchant(merchantid, merChantBean, null);
        }/* else if (TYPE_FOLLOW_OP_ALBUM.equals(type))

        {
            final AlbumBean albumBean = (AlbumBean) bean;
            long albumId = albumBean.getAlbumId();
            final int isFollow = albumBean.getIsFollow();
            final int followCount = albumBean.getFollowCount();
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    boolean isFollowAlbum = false;
                    if (1 == isFollow) {
                        isFollowAlbum = true;
                    }
                    setLikeOrFollow(TYPE_FOLLOW_OR_UNFOLLW_ING, isFollowAlbum, followCount);
                }
            });
            OperaRequestManager.getInstance().operateLikeAlbum(albumId, albumBean, null);
        }*/

    }


    /**
     * 设置后面的数字是否显示
     *
     * @param isShow
     */
    public void setLikeOrFollowCountIsShow(boolean isShow) {
        if (isShow) {
            mTv_count_for_likeorfollow_btn.setVisibility(View.VISIBLE);
        } else {
            mTv_count_for_likeorfollow_btn.setVisibility(View.GONE);
        }
    }
}
