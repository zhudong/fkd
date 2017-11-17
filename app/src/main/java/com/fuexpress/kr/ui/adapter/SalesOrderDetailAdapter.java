package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
import com.fuexpress.kr.model.TranslateManager;
import com.fuexpress.kr.ui.activity.order_detail.OrderDetailCanceledActivity;
import com.fuexpress.kr.ui.activity.product_detail.ProductDetailActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CrowdProgressDetail;
import com.fuexpress.kr.ui.view.RadiusBackgroundSpan;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import fksproto.CsCrowd;

/**
 * Created by k550 on 5/10/2016.
 */
public class SalesOrderDetailAdapter extends SimpleBaseAdapter<SalesOrderItemBean> {
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

    public SalesOrderDetailAdapter(Activity activity, List<SalesOrderItemBean> mDatas) {
        super(activity, mDatas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SalesOrderItemBean item = (SalesOrderItemBean) getItem(position);
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContent, R.layout.item_for_order_detail, null);
            holder.iconIv = (ImageView) convertView.findViewById(R.id.img_parcel_item_icon);
            holder.nameTv = (TextView) convertView.findViewById(R.id.tv_item_name);
            holder.whereTv = (TextView) convertView.findViewById(R.id.tv_where_buy);
            holder.sizeTv = (TextView) convertView.findViewById(R.id.tv_size);
            holder.stateTv = (TextView) convertView.findViewById(R.id.tv_state);
            holder.stateFromTv = (TextView) convertView.findViewById(R.id.tv_state_from);
            holder.priceTv = (TextView) convertView.findViewById(R.id.tv_item_price_end);
            holder.countTv = (TextView) convertView.findViewById(R.id.tv_item_count);

            holder.priceTv.getPaint().setFakeBoldText(false);
            holder.countTv.getPaint().setFakeBoldText(false);

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
            holder.corwdProgress = (CrowdProgressDetail) convertView.findViewById(R.id.corwd_progress);
            holder.crowdState = (TextView) convertView.findViewById(R.id.tv_crowd_state);
            holder.viewStroke = convertView.findViewById(R.id.view_stroke);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (item.crowd != null && item.crowd.getCrowdId() > 0) {
            holder.corwdProgress.setData(item.crowd);
            ((View) holder.corwdProgress.getParent()).setVisibility(View.VISIBLE);
            holder.crowdState.setText(getCrowdState(item.crowd));
        } else {
            ((View) holder.corwdProgress.getParent()).setVisibility(View.GONE);
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
        holder.viewStroke.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(item.korea_order) && TextUtils.isEmpty(item.merchant_message)) {
            holder.lnMerchantLayout.setVisibility(View.GONE);
            holder.viewStroke.setVisibility(View.GONE);
        }

        setTitle(item.title, holder.nameTv, item.crowd);
        holder.whereTv.setText(mContent.getString(R.string.String_cart_buyfrom) + item.buyfrom);
        holder.sizeTv.setText(/*mContent.getString(R.string.String_for_cart_lv_item_size)+*/item.extend_label);
        if (0 <= item.state && item.state <= 10) {
            holder.stateTv.setText(mContent.getString(R.string.order_status) + OrderConstants.getOrderState(item.state, item));
        }

        if (item.state == 8 || item.state == 9) {
            //   holder.stateFromTv.setVisibility(View.VISIBLE);
            holder.stateFromTv.setText(item.cancel_reason);
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


        holder.stateFromTv.setText(item.cancel_reason);
        final String string = mContent.getResources().getString(R.string.String_price);
        String price = UIUtils.getCurrency(mContent, item.currencyCode, item.unit_price);
        holder.priceTv.setText(price);
        holder.countTv.setText("x" + item.qty);
        ImageLoader.getInstance().displayImage("", holder.iconIv, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
        ImageLoader.getInstance().displayImage(item.url + Constants.ImgUrlSuffix.small_9, holder.iconIv, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
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
//                mContent.startActivity(intent);
            }
        };
        holder.message3Layout.setOnClickListener(new TranslateListener(holder.animIv, holder.autoTranslateTv, holder.koreaOrderTv, holder.merchantMessageTv, item));
        convertView.setOnClickListener(onClickListener);
        //  holder.message3Layout.setOnClickListener(onClickListener);
        hintEmpty(holder);
        setMargin(convertView, position);
        return convertView;
    }

    private void setMargin(View convertView, int position) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (position == 0) {
            params.setMargins(0, UIUtils.dip2px(0), 0, 0);
        } else {
            params.setMargins(0, UIUtils.dip2px(8), 0, 0);

        }
        ((LinearLayout) convertView).updateViewLayout(convertView.findViewById(R.id.rl_top), params);
    }

    private void hintEmpty(Holder holder) {
        ViewGroup group = (ViewGroup) holder.nameTv.getParent();
        for (int i = 0; i < group.getChildCount() - 1; i++) {
            View child = group.getChildAt(i);
            if (child instanceof TextView) {
                String s = ((TextView) child).getText().toString();
                if (TextUtils.isEmpty(s))
                    child.setVisibility(View.GONE);
                else
                    child.setVisibility(View.VISIBLE);
            }
        }
    }

    static class Holder {
        TextView nameTv, whereTv, sizeTv, stateTv, stateFromTv, priceTv, countTv;
        TextView orderMessageTv, koreaColorTv, merchantMessageTv, koreaOrderTv, autoTranslateTv;
        RelativeLayout message1Layout, message2Layout, message3Layout;
        LinearLayout lnMerchantLayout, orderMessageLayout;
        ImageView iconIv, animIv;
        CrowdProgressDetail corwdProgress;
        TextView crowdState;
        View viewStroke;
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

    private String getCrowdState(CsCrowd.Crowd crowd) {
        String state = "";
        switch (crowd.getState()) {
            case CsCrowd.CrowdState.CROWD_STATE_PENDING_VALUE:
            case CsCrowd.CrowdState.CROWD_STATE_CHECKING_VALUE:
            case CsCrowd.CrowdState.CROWD_STATE_NONE_VALUE:
                state = UIUtils.getString(R.string.String_start_crowd);
                break;
            case CsCrowd.CrowdState.CROWD_STATE_CROWDING_VALUE:
                if (mContent instanceof OrderDetailCanceledActivity) {
                    state = UIUtils.getString(R.string.crowding);
                } else {
                    state = UIUtils.getString(R.string.String_crowd_panding);
                }
                break;
            case CsCrowd.CrowdState.CROWD_STATE_SUCCESS_VALUE:
                state = UIUtils.getString(R.string.String_crowd_sucess);
                break;
            case CsCrowd.CrowdState.CROWD_STATE_FAILED_VALUE:
                state = UIUtils.getString(R.string.String_crowd_fail);
                break;
        }
        return state;
    }

    private void setTitle(String title, TextView nameTv, CsCrowd.Crowd crowd) {
        if (crowd.getCrowdId() > 0) {
            nameTv.setTextColor(Color.WHITE);
            title += " ";
            String source = title + UIUtils.getString(R.string.crowd_);
            SpannableString spanString = new SpannableString(source);
            spanString.setSpan(new RadiusBackgroundSpan(UIUtils.getColor(R.color.the_red), UIUtils.dip2px(6f)),
                    title.length(), source.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spanString.setSpan(new AbsoluteSizeSpan(10, true),
                    title.length(), source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString.setSpan(new ForegroundColorSpan(Color.BLACK),
                    0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            nameTv.setText(spanString);
        } else {
            nameTv.setText(title);
            nameTv.setTextColor(Color.BLACK);
        }
    }
}
