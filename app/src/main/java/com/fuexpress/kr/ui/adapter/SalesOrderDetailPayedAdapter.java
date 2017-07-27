package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.bean.OrderConstants;
import com.fuexpress.kr.bean.SalesOrderItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.TranslateManager;
import com.fuexpress.kr.ui.activity.package_detail.PackageDetailActivity;
import com.fuexpress.kr.ui.activity.product_detail.ProductDetailActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socks.library.KLog;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import fksproto.CsOrder;

/**
 * Created by k550 on 5/10/2016.
 */
public class SalesOrderDetailPayedAdapter extends SimpleBaseAdapter<SalesOrderItemBean> {
    private RotateAnimation mRotateAnimation;
    private boolean loading = false;

    class TranslateListener implements View.OnClickListener {
        ImageView iv;
        TextView tv, orderTv, messageTv;
        SalesOrderItemBean item;

        public TranslateListener(ImageView iv, TextView tv, TextView orderTv, TextView messageTv, SalesOrderItemBean bean) {
            this.iv = iv;
            this.tv = tv;
            this.item = bean;
            this.orderTv = orderTv;
            this.messageTv = messageTv;
        }

        @Override
        public void onClick(View v) {
            if (item.isTranslated) {
                item.isTranslated = false;
                if (!TextUtils.isEmpty(item.korea_order)) {
                    orderTv.setText(item.korea_order);
                }
                if (!TextUtils.isEmpty(item.merchant_message)) {
                    messageTv.setText(item.merchant_message);
                }
                tv.setText(mContent.getString(R.string.auto_translate));
                //EventBus.getDefault().post(new BusEvent(BusEvent.GET_TEXT_TRANSLATE_SUCCESS,null));
                return;
            } else {
                if (!TextUtils.isEmpty(item.merchant_message_translated) || !TextUtils.isEmpty(item.korea_order_translated)) {
                    item.isTranslated = true;
                    // EventBus.getDefault().post(new BusEvent(BusEvent.GET_TEXT_TRANSLATE_SUCCESS,null));
                    if (!TextUtils.isEmpty(item.korea_order)) {
                        orderTv.setText(item.korea_order_translated);
                    }
                    if (!TextUtils.isEmpty(item.merchant_message)) {
                        messageTv.setText(item.merchant_message_translated);
                    }
                    tv.setText(mContent.getString(R.string.view_original));
                    return;
                }
                List<String> list = new ArrayList<>();
                rotate(iv, true);
                tv.setText(mContent.getString(R.string.auto_translateing));
                list.add(item.korea_order);
                list.add(item.merchant_message);
                TranslateManager.getInstance().translate(list, new TranslateManager.iTranslateListener() {
                    @Override
                    public void onResult(final boolean success, final Map<String, String> result, String msg) {
                        UIUtils.postTaskSafely(new Runnable() {
                            @Override
                            public void run() {
                                loading = false;
                                if (success) {
                                    KLog.i(result.toString());
                                    if (!TextUtils.isEmpty(item.korea_order)) {
                                        item.korea_order_translated = result.get(item.korea_order);
                                        orderTv.setText(item.korea_order_translated);
                                    }
                                    if (!TextUtils.isEmpty(item.merchant_message)) {
                                        item.merchant_message_translated = result.get(item.merchant_message);
                                        messageTv.setText(item.merchant_message_translated);
                                    }
                                    tv.setText(mContent.getString(R.string.view_original));
                                    item.isTranslated = true;
                                    //   EventBus.getDefault().post(new BusEvent(BusEvent.GET_TEXT_TRANSLATE_SUCCESS,null));
                                } else {
                                    ((BaseActivity) mContent).mViewUtils.toast(mContent.getString(R.string.translation_failure));
                                }
                            }
                        });
                    }
                });

            }
            return;
        }
    }

    //添加字段，判断是否直邮发货，直邮发货则隐藏去发货按钮
    public boolean isDirect = false;

    public SalesOrderDetailPayedAdapter(Activity activity, List<SalesOrderItemBean> mDatas) {
        super(activity, mDatas);
    }

    public SalesOrderDetailPayedAdapter(Activity activity, List<SalesOrderItemBean> mDatas, boolean direct) {
        super(activity, mDatas);
        isDirect = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SalesOrderItemBean item = (SalesOrderItemBean) getItem(position);

        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContent, R.layout.item_for_order_detail_payed, null);
            convertView.findViewById(R.id.top_divider).setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            holder.iconIv = (ImageView) convertView.findViewById(R.id.img_parcel_item_icon);
            holder.nameTv = (TextView) convertView.findViewById(R.id.tv_item_name);
            holder.whereTv = (TextView) convertView.findViewById(R.id.tv_where_buy);
            holder.sizeTv = (TextView) convertView.findViewById(R.id.tv_size);
            holder.stateTv = (TextView) convertView.findViewById(R.id.tv_state);
            holder.stateFromTv = (TextView) convertView.findViewById(R.id.tv_state_from);
            holder.priceTv = (TextView) convertView.findViewById(R.id.tv_item_price_end);
            holder.countTv = (TextView) convertView.findViewById(R.id.tv_item_count);
            holder.idTv = (TextView) convertView.findViewById(R.id.parcelIdTv);
            holder.mButton = (Button) convertView.findViewById(R.id.checkBtn);
            holder.mRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.commitLayout);

            holder.orderMessageTv = (TextView) convertView.findViewById(R.id.order_message_tv);
            holder.orderMessageTv.getPaint().setFakeBoldText(false);
            holder.orderMessageLayout = (LinearLayout) convertView.findViewById(R.id.order_message_layout);
            holder.koreaColorTv = (TextView) convertView.findViewById(R.id.korea_color_tv);
            holder.koreaColorTv.getPaint().setFakeBoldText(false);
            holder.merchantMessageTv = (TextView) convertView.findViewById(R.id.merchant_message_tv);
            holder.merchantMessageTv.getPaint().setFakeBoldText(false);
            holder.koreaOrderTv = (TextView) convertView.findViewById(R.id.korea_order_tv);
            holder.koreaOrderTv.getPaint().setFakeBoldText(false);

            holder.lnMerchantLayout = (LinearLayout) convertView.findViewById(R.id.merchant_message_layout);
            holder.message1Layout = (RelativeLayout) convertView.findViewById(R.id.message_1_layout);
            holder.message2Layout = (RelativeLayout) convertView.findViewById(R.id.message_2_layout);

            holder.message3Layout = (RelativeLayout) convertView.findViewById(R.id.message_3_layout);
            holder.autoTranslateTv = (TextView) convertView.findViewById(R.id.auto_translate_tv);

            holder.animIv = (ImageView) convertView.findViewById(R.id.anim_view);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }


        holder.nameTv.setText(item.title);
        holder.whereTv.setText(mContent.getString(R.string.String_cart_buyfrom) + item.buyfrom);
        holder.sizeTv.setText(/*mContent.getString(R.string.String_for_cart_lv_item_size)+*/item.extend_label);
        String pickUpTime = "";
        if (item.state == CsOrder.SalesOrderItemState.SALES_ORDER_ITEM_STATE_PREORDERING_VALUE) {
            pickUpTime = item.prompt.toString();
        }
        if (0 < item.state && item.state <= 10) {
            holder.stateTv.setText(mContent.getString(R.string.order_status) + OrderConstants.getOrderState(item.state) + " " + pickUpTime);
        }

        holder.mButton.setBackgroundResource(R.drawable.bg_raduis_button_e2);
        holder.mButton.setTextColor(Color.BLACK);
        if (position == 0) {
            holder.mRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            SalesOrderItemBean item01 = (SalesOrderItemBean) getItem(position - 1);
            if (!item01.parcel_number.equals(item.parcel_number)) {
                holder.mRelativeLayout.setVisibility(View.VISIBLE);
            } else {
                holder.mRelativeLayout.setVisibility(View.GONE);
            }
        }
        switch (item.state) {
            case 1:
            case 2:

            case 3:
            case CsOrder.SalesOrderItemState.SALES_ORDER_ITEM_STATE_PREORDERING_VALUE:
                //属于其他类型
                holder.mRelativeLayout.setVisibility(View.GONE);
                break;
            case CsOrder.SalesOrderItemState.SALES_ORDER_ITEM_STATE_PACKING_VALUE:
                holder.mButton.setText(mContent.getString(R.string.String_goto_send));
                holder.mButton.setTextColor(Color.WHITE);
                holder.mButton.setBackgroundResource(R.drawable.shape_for_login_btn);
                break;
            case CsOrder.SalesOrderItemState.SALES_ORDER_ITEM_STATE_NOTICED_VALUE:
            case 7:
                holder.mButton.setText(mContent.getString(R.string.check_parcel));
                break;
            default:
                //属于其他类型
                holder.mRelativeLayout.setVisibility(View.GONE);
                break;
        }
        holder.mButton.setVisibility(View.VISIBLE);
        if (isDirect) {
            holder.mButton.setVisibility(View.GONE);
        }

        holder.orderMessageLayout.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(item.order_message)) {
            holder.orderMessageLayout.setVisibility(View.GONE);
        } else {
            holder.orderMessageTv.setText(item.order_message);
        }

        holder.koreaColorTv.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(item.korea_color)) {
            holder.koreaColorTv.setVisibility(View.GONE);
        } else {
            holder.koreaColorTv.setText(item.korea_color);
        }

        holder.message1Layout.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(item.merchant_message)) {
            holder.message1Layout.setVisibility(View.GONE);
        } else {
            holder.merchantMessageTv.setText(item.merchant_message);
        }

        holder.message2Layout.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(item.korea_order)) {
            holder.message2Layout.setVisibility(View.GONE);
        } else {
            holder.koreaOrderTv.setText(item.korea_order);
        }

        holder.lnMerchantLayout.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(item.korea_order) && TextUtils.isEmpty(item.merchant_message)) {
            holder.lnMerchantLayout.setVisibility(View.GONE);
            //    holder.message3Layout.setVisibility(View.GONE);
        }

        if (item.isTranslated) {
            holder.autoTranslateTv.setText(UIUtils.getString(R.string.view_original));
            if (!TextUtils.isEmpty(item.korea_order)) {
                holder.koreaOrderTv.setText(item.korea_order_translated);
            }
            if (!TextUtils.isEmpty(item.merchant_message)) {
                holder.merchantMessageTv.setText(item.merchant_message_translated);
            }

        } else {
            holder.autoTranslateTv.setText(UIUtils.getString(R.string.auto_translate));
            if (!TextUtils.isEmpty(item.korea_order)) {
                holder.koreaOrderTv.setText(item.korea_order);
            }
            if (!TextUtils.isEmpty(item.merchant_message)) {
                holder.merchantMessageTv.setText(item.merchant_message);
            }
        }


        holder.idTv.setText(mContent.getString(R.string.parcel_title) + item.parcel_number);
        final String string = mContent.getResources().getString(R.string.String_price);
        String price = UIUtils.getCurrency(mContent, item.currencyCode, item.unit_price);
        holder.priceTv.setText(price);
        holder.countTv.setText("x" + item.qty);
        ImageLoader.getInstance().displayImage("", holder.iconIv, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
        ImageLoader.getInstance().displayImage(item.url + Constants.ImgUrlSuffix.small_9, holder.iconIv, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KLog.i("onclick " + AccountManager.getInstance().getLocaleCode());
                //标记当前是否被翻译

                switch (v.getId()) {
                    case R.id.checkBtn:
                        final Intent intent = new Intent(mContent, PackageDetailActivity.class);
                        intent.putExtra(PackageDetailActivity.PARCEL_ID, item.parcel_id);
                        mContent.startActivity(intent);
                        return;
                    case R.id.message_3_layout:
                        if (item.isTranslated) {
                            item.isTranslated = false;
                            EventBus.getDefault().post(new BusEvent(BusEvent.GET_TEXT_TRANSLATE_SUCCESS, null));
                        } else {
                            if (!TextUtils.isEmpty(item.merchant_message_translated) || !TextUtils.isEmpty(item.korea_order_translated)) {
                                item.isTranslated = true;
                                EventBus.getDefault().post(new BusEvent(BusEvent.GET_TEXT_TRANSLATE_SUCCESS, null));
                                return;
                            }
                            List<String> list = new ArrayList<>();
                            list.add(item.korea_order);
                            list.add(item.merchant_message);
                            TranslateManager.getInstance().translate(list, new TranslateManager.iTranslateListener() {
                                @Override
                                public void onResult(final boolean success, final Map<String, String> result, String msg) {
                                    UIUtils.postTaskSafely(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (success) {
                                                if (!TextUtils.isEmpty(item.korea_order)) {
                                                    item.korea_order_translated = result.get(item.korea_order);
                                                }
                                                if (!TextUtils.isEmpty(item.merchant_message)) {
                                                    item.merchant_message_translated = result.get(item.merchant_message);
                                                }
                                                item.isTranslated = true;
                                                EventBus.getDefault().post(new BusEvent(BusEvent.GET_TEXT_TRANSLATE_SUCCESS, null));
                                            } else {
                                                ((BaseActivity) mContent).mViewUtils.toast(mContent.getString(R.string.translation_failure));
                                            }
                                        }
                                    });
                                }
                            });

                        }
                        return;
                }
                Intent intent = new Intent(mContent, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.ITEM_ID, item.item_id);
                mContent.startActivity(intent);
            }
        };
        //   holder.message3Layout.setOnClickListener(onClickListener);
        holder.message3Layout.setOnClickListener(new TranslateListener(holder.animIv, holder.autoTranslateTv, holder.koreaOrderTv, holder.merchantMessageTv, item));
        holder.mButton.setOnClickListener(onClickListener);
        convertView.setOnClickListener(onClickListener);
        return convertView;
    }


    static class Holder {
        TextView nameTv, whereTv, sizeTv, stateTv, stateFromTv, priceTv, countTv, idTv;
        TextView orderMessageTv, koreaColorTv, merchantMessageTv, koreaOrderTv, autoTranslateTv;
        RelativeLayout message1Layout, message2Layout, message3Layout;
        LinearLayout lnMerchantLayout, orderMessageLayout;
        ImageView iconIv, animIv;
        Button mButton;
        RelativeLayout mRelativeLayout;
    }

    private void rotate(View v, boolean start) {
        if (start) {
            loading = true;
            if (mRotateAnimation == null)
                mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
//            rotateAnimation.setRepeatMode(Animation.INFINITE);
            mRotateAnimation.setRepeatCount(Animation.INFINITE);
            mRotateAnimation.setDuration(500);
            mRotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            mRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (!loading) {
                        animation.cancel();
                    }
                }
            });
            v.startAnimation(mRotateAnimation);
        }
    }




}
