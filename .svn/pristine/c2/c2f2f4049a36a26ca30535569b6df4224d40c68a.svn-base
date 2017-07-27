package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.bean.NeedBean;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


/**
 * Created by yuan on 2016-5-3.
 */
public class ParcelItemsAdapter extends SimpleBaseAdapter<NeedBean> implements View.OnClickListener {
    private final int mSize;
    private final String sToSend;
    private final String sSended;
    private final String sWaitSend;
    List<NeedBean> mUrls;
    private final int mWidth;
    private final int mMarginLeft;

    public ParcelItemsAdapter(Activity context, List<NeedBean> parcels) {
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
            convertView = View.inflate(mContent, R.layout.item_for_packages, null);
            holer.mLlCovers = (LinearLayout) convertView.findViewById(R.id.ll_pckage_covers);
            holer.mTvNumber = (TextView) convertView.findViewById(R.id.tv_package_number);
            holer.mTvState = (TextView) convertView.findViewById(R.id.tv_parcle_state);
            convertView.setTag(holer);
        } else {
            holer = (Holer) convertView.getTag();
        }
        NeedBean needBean = mData.get(position);
        holer.mTvNumber.setText(needBean.getDate());
//        holer.mTvType.setText(needBean.getType());
        holer.mTvState.setText(needBean.getState());
        addCovers(holer.mLlCovers,needBean.getImgs());
        http://car2.autoimg.cn/cardfs/product/g23/M0D/90/01/u_autohomecar__wKgFV1bSTEqAYEMEAAoYNQ1knhY263.jpg

//        CsParcel.Parcel parcel = (CsParcel.Parcel) getItem(position);
//        parcel.getState();

      /*  PARCEL_STATE_NONE      = 0;//全部
        PARCEL_STATE_INSTORED  = 1;//已入库
        PARCEL_STATE_NOTICED   = 2;//待发货
        PARCEL_STATE_SHIPPED   = 3;//已发货*/

       /* switch (parcel.getState()){
            case CsParcel.ParcelTab.PARCEL_TAB_INSTORED_VALUE:
//                去发货
                holer.mTvState.setText(sToSend);
                break;
            case CsParcel.ParcelTab.PARCEL_TAB_NOTICED_VALUE:
                holer.mTvState.setText(sWaitSend);
                break;
            case CsParcel.ParcelTab.PARCEL_TAB_SHIPPED_VALUE:
                holer.mTvState.setText(sSended);
                break;
        }*/


//        holer.mTvNumber.setText(parcel.getParcelNumber() + "");
//        CsBase.PairIntStr pairIntStr = mUrls.get(position);
//        String[] split = pairIntStr.getV().split(",");
//        addCovers(holer.mLlCovers,split);
        convertView.setTag(R.id.ll_pckage_covers, position);
        convertView.setOnClickListener(this);
        return convertView;
    }

    private void addCovers(LinearLayout llCovers, String[] split) {
        int len = mSize < split.length ? mSize : split.length;
        llCovers.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mWidth, mWidth);
        params.setMargins(0, 0, mMarginLeft, 0);
        for (int i = 0; i < len; i++) {
            ImageView imageView = new ImageView(mContent);
//            ImageLoader.getInstance().displayImage(split[i]+ Constants.ImgUrlSuffix.small_9,imageView, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
            ImageLoader.getInstance().displayImage(split[i],imageView, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
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
//        toDetail(postion);
    }


    static class Holer {
        TextView mTvNumber;
        TextView mTvState;
        LinearLayout mLlCovers;
    }
}
