package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.bean.NeedBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.activity.DemandsDetailActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import fksproto.CsUser;


/**
 * Created by yuan on 2016-5-3.
 */
public class NeedItemsAdapter extends SimpleBaseAdapter<CsUser.Require> implements View.OnClickListener {
    private final int mSize;
    private final String sToSend;
    private final String sSended;
    private final String sWaitSend;
    List<NeedBean> mUrls;
    private final int mWidth;
    private final int mMarginLeft;

    public NeedItemsAdapter(Activity context, List<CsUser.Require> parcels) {
        super(context, parcels);
        mWidth = UIUtils.dip2px((float) 57.3);
        mMarginLeft = UIUtils.dip2px((float) 10);
        int padding = UIUtils.dip2px((float) 1);
        int screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
        sWaitSend = context.getString(R.string.string_my_need_to_pay);
        sSended = context.getString(R.string.string_need_in_storage);
        sToSend = context.getString(R.string.string_neeed_cancel);

        mSize = (screenWidth - 2 * mMarginLeft) / (mWidth + mMarginLeft) + 1;

    }

    @Override
    public int getCount() {
//        return mUrls.size() > mData.size() ? mData.size() : mUrls.size();
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holer holer = null;
        if (convertView == null) {
            holer = new Holer();
            convertView = View.inflate(mContent, R.layout.item_for_needs, null);
            holer.mLlCovers = (LinearLayout) convertView.findViewById(R.id.ll_pckage_covers);
            holer.mTvDate = (TextView) convertView.findViewById(R.id.tv_need_time_);
            holer.mTvType = (TextView) convertView.findViewById(R.id.tv_need_type_);
            holer.mTvState = (TextView) convertView.findViewById(R.id.tv_needs_item_state);
            convertView.setTag(holer);
        } else {
            holer = (Holer) convertView.getTag();
        }

      /*  optional int32          salesRequireId         = 1;//需求单id
        optional string 		status                 = 2;//不传查出所有
        optional int32			type                   = 3;//1取2找3买4收
        optional string         createTime             = 4;//生成日期
        optional string			imageUrl               = 5;//图片链接
        optional string			description            = 6;//描述
        optional float			price                  = 7;//单价
        optional float 			qty                    = 8;//数量
        1取2找3买4收
        */

        CsUser.Require needBean = mData.get(position);
        holer.mTvDate.setText(needBean.getCreateTime());

        holer.mTvState.setText(Constants.getStatusString(needBean.getStatus()));
        addCovers(holer.mLlCovers, needBean.getImageUrlList());

        String type = "";
        switch (needBean.getType()) {
            case 1:
                type = mContent.getString(R.string.home_fg_help_01);
                break;
            case 2:
                type = mContent.getString(R.string.help_me_find);
                break;
            case 3:
                type = mContent.getString(R.string.home_fg_help_03);
                break;
            case 4:
                type = mContent.getString(R.string.home_fg_help_02);
                break;
        }
        holer.mTvType.setText(type);

        convertView.setTag(R.id.ll_pckage_covers, position);
        convertView.setOnClickListener(this);
        return convertView;
    }
/*
    private String getType(){
        public static String[] REQUIRETYPE = {"",
                UIUtils.getString(R.string.home_fg_help_01),
                UIUtils.getString(R.string.help_me_find),
                UIUtils.getString(R.string.home_fg_help_03),
                UIUtils.getString(R.string.home_fg_help_02)
        };
    }*/



    private void addCovers(LinearLayout llCovers, List<fksproto.CsUser.ImagesUrl> split) {
        int len = mSize < split.size() ? mSize : split.size();
        llCovers.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mWidth, mWidth);
        params.setMargins(0, 0, mMarginLeft, 0);
        for (int i = 0; i < len; i++) {
            ImageView imageView = new ImageView(mContent);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            ImageLoader.getInstance().displayImage(split[i]+ Constants.ImgUrlSuffix.small_9,imageView, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
            ImageLoader.getInstance().displayImage(split.get(i).getImage() + Constants.ImgUrlSuffix.dp_list, imageView, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
            llCovers.addView(imageView, params);
        }
    }

    /* private void toDetail(int position){
         Intent intent = new Intent(mContent, PackageDetail.class);
         CsParcel.Parcel parcel = mData.get(position);
         Bundle bundle = new Bundle();
         bundle.putSerializable(PackageDetail.PARCLE_BEAN,parcel);
         intent.putExtra(PackageDetail.PARCLE_BEAN,bundle);
         mContent.startActivity(intent);
     }
 */
    @Override
    public void onClick(View v) {
        int postion = (int) v.getTag(R.id.ll_pckage_covers);
        CsUser.Require item = mData.get(postion);
//        toDetail(postion);
        Intent intent = new Intent(mContent, DemandsDetailActivity.class);
        intent.putExtra(DemandsDetailActivity.DEMAND_BEAN, item);
        mContent.startActivity(intent);
        mContent.overridePendingTransition(R.anim.activity_translate_x_in, R.anim.activity_translate_x_out);
    }


    static class Holer {
        TextView mTvDate;
        TextView mTvType;
        TextView mTvState;
        LinearLayout mLlCovers;
    }
}