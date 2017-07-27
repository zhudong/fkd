package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.ParcelItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.activity.DemandsDetailActivity;
import com.fuexpress.kr.ui.activity.my_order.OrderAll;
import com.fuexpress.kr.ui.activity.order_detail.OrderDetailPayedActivity;
import com.fuexpress.kr.ui.activity.package_detail.PackageDetailActivity;
import com.fuexpress.kr.ui.activity.product_detail.ProductDetailActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiyukf.unicorn.api.ProductDetail;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fksproto.CsUser;

/**
 * Created by andy on 2016/12/9.
 */
public class ParceOrderItemView extends RelativeLayout {

    @BindView(R.id.tv_order_number)
    TextView mTvOrderNumber;
    @BindView(R.id.img_icon)
    ImageView mImgIcon;
    @BindView(R.id.tv_count)
    TextView mTvCount;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_price)
    TextView mTvPrice;
    @BindView(R.id.tv_item_state)
    TextView mTvItemState;
    @BindView(R.id.tv_note)
    TextView mTvNote;
    @BindView(R.id.ll_buttom)
    LinearLayout mLlButtom;
    @BindView(R.id.tv_merchant_message)
    TextView mTvMerchantMessage;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private ParcelItemBean item;

    public ParceOrderItemView(Context context) {
        this(context, null);
    }

    public ParceOrderItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParceOrderItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_parcel_detail_item, this);
        ButterKnife.bind(this);
//        IvIcon = (ImageView) findViewById(R.id.img_icon);
//        mTitle = (TextView) findViewById(R.id.tv_title);
//        mPrice = (TextView) findViewById(R.id.tv_price);
//        mCount = (TextView) findViewById(R.id.tv_count);
        options = ImageLoaderHelper.getInstance(getContext()).getDisplayOptions();
        imageLoader = ImageLoader.getInstance();
    }

    //
//    0:商品订单
//    其他：1取2找3买4收
    public void initData(ParcelItemBean item) {
        this.item = item;
        setIvIcon(item.img);
        mTvTitle.setText(item.titel);
        mTvPrice.setText(item.price);
        mTvCount.setText("×" + item.count);
        if (item.type == 0) {
            mTvOrderNumber.setText(getResources().getString(R.string.gift_card_order_id_title) + ":" + item.orderNumber);
        } else {
            mTvOrderNumber.setText(getResources().getString(R.string.package_item_need_number, item.needNumber));
        }
        mTvItemState.setText(getResources().getString(R.string.package_item_state, Constants.getStatusString(item.status)));
//        mTvItemState.setVisibility(TextUtils.isEmpty(item.status) ? GONE : VISIBLE);
        if (item.type == 1) {
            mTvNote.setText(getResources().getString(R.string.package_item_pick_address, item.remark));
        } else {
            mTvNote.setText(getResources().getString(R.string.package_item_note, item.remark));
        }
//        mTvNote.setVisibility(TextUtils.isEmpty(item.remark) ? GONE : VISIBLE);
        mTvMerchantMessage.setText(item.message);
        mLlButtom.setVisibility(TextUtils.isEmpty(item.message) ? GONE : VISIBLE);
    }

    public void setIvIcon(String url) {
        mImgIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageLoader.displayImage(url + Constants.ImgUrlSuffix.dp_list, mImgIcon, options);
    }
 /*
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setPrice(String price) {
        mPrice.setText(price);
    }

    public void setCount(String count) {
        mCount.setText(count);
    }*/

    @OnClick({R.id.rl_top, R.id.rl_bottom})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_top:
                topDetail();
                break;
            case R.id.rl_bottom:
                if (item.type == 0) {
                    Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                    intent.putExtra(ProductDetailActivity.ITEM_ID, (long) item.matchItemId);
                    getContext().startActivity(intent);
                }
                break;
        }
    }

    private void topDetail() {
        if (item.type > 0) {
            CsUser.Require require = CsUser.Require.newBuilder().setType(item.type).setSalesRequireId(item.needNumber).build();
            PackageDetailActivity context = (PackageDetailActivity) getContext();
            Intent intent = new Intent(getContext(), DemandsDetailActivity.class);
            intent.putExtra(DemandsDetailActivity.TITLE_BACK, context.getTitel());
            intent.putExtra(DemandsDetailActivity.DEMAND_BEAN, require);
            context.startActivity(intent);
        }
        if(item.type==0){
            Intent intent = new Intent(getContext(), OrderDetailPayedActivity.class);
            intent.putExtra(OrderAll.BEAN, (long)item.salesOrderId);
            getContext().startActivity(intent);
        }
    }
}
