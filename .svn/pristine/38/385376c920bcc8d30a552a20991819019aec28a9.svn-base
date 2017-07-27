package com.fuexpress.kr.ui.activity.produ_source;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.MerChantBean;
import com.fuexpress.kr.model.OperaRequestManager;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.activity.merchant_detail.MerChantDetailActivity02;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.LikeOrFollowBtn;
import com.fuexpress.kr.ui.view.RatioLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longer on 2017/6/26.
 */
public class ProduSrcMyFollowAdapter extends BaseAdapter {

    private List<MerChantBean> mDates;
    private Activity mContext;
    private DisplayImageOptions options;
    private int mCurrentItem;
    private boolean isRequesting = false;
    private int mCurrPosition;

    public ProduSrcMyFollowAdapter(Activity context, List<MerChantBean> list) {
        options = ImageLoaderHelper.getInstance(mContext).getDisplayOptions();
        mDates = list;
        this.mContext = context;
        //    EventBus.getDefault().register(this);
    }

    public ProduSrcMyFollowAdapter(Activity context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mDates != null) {
            return (mDates.size() + 1) / 2;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        ArrayList<Object> list;
        if (mDates != null) {
            list = new ArrayList<>();
            list.add(mDates.get(position * 2));

            if ((position * 2 + 1) <= mDates.size() - 1) {
                list.add(mDates.get(position * 2 + 1));
            }
            return list;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item2_for_choose_in_self_album, null);
            holder = new Holder();
            for (int i = 0; i < 2; i++) {
                View child = ((ViewGroup) convertView).getChildAt(i);
                Holder.Holderitem holderitem = holder.mHolderitems[i];
                holderitem.mRoot = child;
                holderitem.mImagCover = (ImageView) child.findViewById(R.id.iv_in_item_in_choose_self_album);
                holderitem.mTvName = (TextView) child.findViewById(R.id.tv_in_item_in_choose_self_album_title);
                holderitem.mRlFocuseRoot = (RelativeLayout) child.findViewById(R.id.rl_album_focuse);
                holderitem.mTvFocuse = (TextView) child.findViewById(R.id.tv_album_is_focuse);
                holderitem.mTvFocuseCount = (TextView) child.findViewById(R.id.tv_album_focuse_count);
                holderitem.ratiorl_in_followed = (RatioLayout) child.findViewById(R.id.ratiorl_in_followed);
                holderitem.ratiorl_in_followed.setPicRatio(1.5f);
            }
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        List<MerChantBean> list = (List<MerChantBean>) getItem(position);

        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                index = position * 2;
            } else if (i == 1) {
                index = position * 2 + 1;
            }
            final int fIndex = index;
            if (list.size() > 1) {
                holder.mHolderitems[1].mRoot.setVisibility(View.VISIBLE);
            } else {
                holder.mHolderitems[1].mRoot.setVisibility(View.INVISIBLE);
            }
            final MerChantBean bean = list.get(i);
            final Holder.Holderitem holderitem = holder.mHolderitems[i];
            holderitem.mTvName.setText(bean.getTitle());
            final ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(bean.getCover() + "!bizlist", holderitem.mImagCover, options);
            if (!bean.is_follow()) {
//                未关注、、
                holderitem.mTvFocuse.setText(mContext.getResources().getString(R.string.String_for_home_item_add_attention));
            } else {
                holderitem.mTvFocuse.setText(mContext.getResources().getString(R.string.merchant_detail_followed));
            }
            holderitem.mTvFocuseCount.setText(bean.getFollow_num() + "");

            holderitem.mRlFocuseRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrPosition = fIndex;
                    //关注或者取消关注店铺
                    KLog.i("like " + bean.is_follow() + "beanSum " + bean.getFollow_num());
                    if (!AccountManager.getInstance().isUserLogin(mContext)) {
                        return;
                    }
                    OperaRequestManager.getInstance().operateLikeMerchant(bean.getMerchantid(), bean, new OperaRequestListener() {
                        @Override
                        public void onOperaSuccess() {
                            //请求成功
                            /*KLog.i("like "+bean.is_follow()+" beanSum "+bean.getFollow_num());
                            if(PersonInfoActivity.isMyself){
                                if(mContext instanceof PersonInfoActivity){
                                    PersonInfoActivity activity=(PersonInfoActivity)mContext;
                                    activity.unLikeMerchantSuccess(bean.getMerchantid());
                                }
                            }else{
                                if(bean.is_follow()){
                                    holderitem.mTvFocuse.setText(mContext.getResources().getString(R.string.String_for_home_item_not_add_attention));
                                }else{
                                    holderitem.mTvFocuse.setText(mContext.getResources().getString(R.string.String_for_home_item_add_attention));
                                }
                                holderitem.mTvFocuseCount.setText(bean.getFollow_num() + "");
                            }*/
                            if (bean.is_follow()) {
                                holderitem.mTvFocuse.setText(mContext.getResources().getString(R.string.merchant_detail_followed));
                            } else {
                                holderitem.mTvFocuse.setText(mContext.getResources().getString(R.string.String_for_home_item_add_attention));
                            }
                            holderitem.mTvFocuseCount.setText(bean.getFollow_num() + "");

                        }

                        @Override
                        public void onOperaFailure() {
                            CustomToast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            holderitem.mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentItem = fIndex;
                    long merchantid = bean.getMerchantid();
                    Intent intent = new Intent(mContext, MerChantDetailActivity02.class);
                    intent.putExtra("merchantId", merchantid);
                    //MerchantDetailManager.getInstance().getMerChantDetailRequest(merchantid);
                    mContext.startActivity(intent);
                }
            });


        }

        return convertView;
    }


    class Holder {
        Holderitem[] mHolderitems = new Holderitem[]{new Holderitem(), new Holderitem()};

        class Holderitem {
            View mRoot;
            TextView mTvName;
            ImageView mImagCover;

            RelativeLayout mRlFocuseRoot;
            TextView mTvFocuse;
            TextView mTvFocuseCount;
            RatioLayout ratiorl_in_followed;

        }
    }


    public void setDate(List<MerChantBean> items) {
        mDates = items;
    }

    public void notifyReflash() {
        this.notifyDataSetChanged();
    }


    private boolean isTop() {
        String topActivity = UIUtils.getTopActivity(mContext);
        String name = mContext.getClass().getName();
        return name.equals(topActivity);
    }

    public void setData(List<MerChantBean> list) {
        if (mDates != null)
            if (list != null)
                mDates.addAll(list);
    }

    public int getmCurrPosition() {
        return mCurrPosition;
    }
}
