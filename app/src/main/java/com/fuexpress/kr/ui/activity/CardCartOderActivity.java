package com.fuexpress.kr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ActivityController;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CartListView;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsCard;
import fksproto.CsUser;

/**
 * Created by Administrator on 2016/5/9.
 */
public class CardCartOderActivity extends BaseActivity {

    private View rootView;
    private CartListView cardCartListView;
    private ListView cardCartPaymentListView;
    private ImageView backIv;
    //    private LinearLayout                        cardOrderAliPayLayout;
//    private ImageView                           cardOrderAliPayIv;
//    private LinearLayout                        cardOrderWeChatPayLayout;
//    private ImageView                           cardOrderWeChatPayIv;
    private TextView cardOrderGrandTotalTv;
    private Button settlementBtn;
    private CsCard.GetGiftCardAlongCartResponse response;
    private String paymentType = Constants.GIFT_CARD_PAYMENT_ADYEN;
    private String paymentString;
    private int cardCount;
    private String grandTotal;
    private SharedPreferences shared;
    private int payType;
    private int paymentPos;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_card_cart_order, null);
        TitleBarView titleBarView = (TitleBarView) rootView.findViewById(R.id.card_cart_order_titlebar);
        titleBarView.setTitleText(getString(R.string.cart_order_title_bar_title));
        backIv = titleBarView.getIv_in_title_back();
        cardCartListView = (CartListView) rootView.findViewById(R.id.card_cart_order_list_view);
        cardCartPaymentListView = (ListView) rootView.findViewById(R.id.card_cart_order_payment_list_view);
//        cardOrderAliPayLayout = (LinearLayout) rootView.findViewById(R.id.card_order_ali_pay_layout);
//        cardOrderAliPayIv = (ImageView) rootView.findViewById(R.id.card_order_ali_pay_iv);
//        cardOrderWeChatPayLayout = (LinearLayout) rootView.findViewById(R.id.card_order_wechat_pay_layout);
//        cardOrderWeChatPayIv = (ImageView) rootView.findViewById(R.id.card_order_wechat_pay_iv);
        cardOrderGrandTotalTv = (TextView) rootView.findViewById(R.id.card_order_grandtotal_tv);
        settlementBtn = (Button) rootView.findViewById(R.id.card_settlement_btn);
        shared = getSharedPreferences("payShare", Constants.PAY_TYPE_SHARE_CODE);
        payType = shared.getInt("payType", 1);
//        if (payType == Constants.PAYMENT_ALIPAY) {
//            paymentType = Constants.GIFT_CARD_PAYMENT_ALIPAY;
//            cardOrderAliPayIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.select_icon));
//            cardOrderWeChatPayIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.unselect_icon));
//        }
//        if (payType == Constants.PAYMENT_WECHAT) {
//            paymentType = Constants.GIFT_CARD_PAYMENT_WXCHAT;
//            cardOrderAliPayIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.unselect_icon));
//            cardOrderWeChatPayIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.select_icon));
//        }
        giftCardAlongCartPlay();
        GetIntlPaymentListRequest();
        ActivityController.cardAddActivity(this);

        settlementBtn.setOnClickListener(this);
//        cardOrderAliPayLayout.setOnClickListener(this);
//        cardOrderWeChatPayLayout.setOnClickListener(this);
        backIv.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
//            case R.id.card_order_ali_pay_layout:
//                shared.edit().putInt("payType", 1).commit();
//                cardOrderAliPayIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.select_icon));
//                cardOrderWeChatPayIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.unselect_icon));
//                paymentType = Constants.GIFT_CARD_PAYMENT_ALIPAY;
//                //editGiftCardOrderPayMethod(Integer.parseInt(response.getGiftcardcart().getGiftCardOrderId()), "aliapy");
//                break;
//            case R.id.card_order_wechat_pay_layout:
//                shared.edit().putInt("payType", 2).commit();
//                paymentType = Constants.GIFT_CARD_PAYMENT_WXCHAT;
//                cardOrderAliPayIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.unselect_icon));
//                cardOrderWeChatPayIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.select_icon));
//                break;
            case R.id.card_settlement_btn:
                placeGiftCardOrder(paymentType);
                break;
        }
    }

    public void GetIntlPaymentListRequest() {
        CsUser.GetFKDPaymentListRequest.Builder builder = CsUser.GetFKDPaymentListRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetFKDPaymentListResponse>() {

            @Override
            public void onSuccess(CsUser.GetFKDPaymentListResponse response) {
                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                paymentHandler.sendMessage(msg);
                List<CsUser.PaymentList> list = response.getPaymantlistList();
//                for (int i = 0; i < list.size(); i++) {
//                    if(list.get(i).getSortorder().equals("1")){
//                        paymentType = list.get(i).getPaycode();
//                    }
//                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        settlementBtn.setEnabled(false);
                    }
                });
            }
        });
    }

    private Handler paymentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CsUser.GetFKDPaymentListResponse response = (CsUser.GetFKDPaymentListResponse) msg.obj;
            List<CsUser.PaymentList> list = response.getPaymantlistList();
            List<CsUser.PaymentList> payList = new ArrayList<CsUser.PaymentList>();
            for (int i = 0; i < list.size(); i++) {
//                if (list.get(i).getPaycode().equals(Constants.GIFT_CARD_PAYMENT_ADYEN)
//                        || list.get(i).getPaycode().equals(Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
                    payList.add(list.get(i));
                    paymentType = list.get(0).getPaycode();
                    paymentString = list.get(0).getPayname();
//                }
            }
            if (payList.size() <= 0) {
                settlementBtn.setEnabled(false);
                settlementBtn.setBackground(ContextCompat.getDrawable(CardCartOderActivity.this, R.drawable.shape_btn_bg_red_more_radius_unenable));
            } else {
                settlementBtn.setEnabled(true);
                settlementBtn.setBackground(ContextCompat.getDrawable(CardCartOderActivity.this, R.drawable.selector_red_btn_drawable));
            }
            final PaymentAdapter adapter = new PaymentAdapter(CardCartOderActivity.this, payList);
            cardCartPaymentListView.setAdapter(adapter);
            adapter.setCheckedAtPosition(paymentPos);
            setListViewHeightBasedOnChildren(cardCartPaymentListView);
            cardCartPaymentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adapter.setCheckedAtPosition(position);
                    adapter.notifyDataSetChanged();
                    paymentPos = position;
                    paymentType = ((CsUser.PaymentList) parent.getAdapter().getItem(position)).getPaycode();
                    paymentString = ((CsUser.PaymentList) parent.getAdapter().getItem(position)).getPayname();
                }
            });
        }
    };

    class PaymentAdapter extends BaseAdapter {

        private Context ctx;
        private List<CsUser.PaymentList> list;
        private int pos;

        public PaymentAdapter(Context ctx, List<CsUser.PaymentList> list) {
            this.ctx = ctx;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ctx).inflate(R.layout.payment_item, null);
                holder = new ViewHolder();
                holder.logoIv = (ImageView) convertView.findViewById(R.id.payment_item_logo_iv);
                holder.nameTv = (TextView) convertView.findViewById(R.id.payment_item_name_tv);
                holder.checkedTv = (CheckedTextView) convertView.findViewById(R.id.payment_item_ct);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CsUser.PaymentList item = list.get(position);

            holder.checkedTv.setCheckMarkDrawable(R.drawable.selector_payment_checkbox);
            if (pos == position) {
                holder.checkedTv.setChecked(true);
            } else {
                holder.checkedTv.setChecked(false);
            }

            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_ADYEN)) {
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.adyen_pay));
//                holder.nameTv.setText(getString(R.string.String_adyen_pay));

            }
            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_KRBANK)) {
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.krbank_pay));
//                holder.nameTv.setText(getString(R.string.String_krbank_pay2));

            }
            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.daoupay));
            }
            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_ALIPAY)) {
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.pay_alipay));
//                holder.nameTv.setText(item.getPayname());
            }
            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_WXPAY)) {
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.pay_wechat));
//                holder.nameTv.setText(item.getPayname());
            }
            holder.nameTv.setText(item.getPayname());


//            if (TextUtils.equals(item.getPaycode(), Constants.PAYMENT_DEFAULT)) {
//                holder.checkedTv.setChecked(true);
//            }

            return convertView;
        }

        public void setCheckedAtPosition(int pos) {
            this.pos = pos;
        }

        class ViewHolder {
            ImageView logoIv;
            TextView nameTv;
            CheckedTextView checkedTv;
        }
    }

    public void placeGiftCardOrder(String payment) {
        showLoading(5000);
        CsCard.PlaceGiftCardOrderRequest.Builder builder = CsCard.PlaceGiftCardOrderRequest.newBuilder();
        builder.setPaymentCode(payment);
        builder.setAppType("android");
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode()).setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.PlacetGiftCardOrderResponse>() {

            @Override
            public void onSuccess(CsCard.PlacetGiftCardOrderResponse response) {
                closeLoading();
                LogUtils.d(response.toString());
                Intent intent = new Intent(CardCartOderActivity.this, CardConfirmPaymentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("giftcardorderid", response.getGiftCardOrderId());
                bundle.putString("giftcardorderno", response.getGiftCardOrderNo());
                bundle.putInt("cardcount", cardCount);
                bundle.putString("grandtotal", grandTotal);
                bundle.putString("paymenttype", paymentType);
                bundle.putString("paymentString", paymentString);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }


    public void giftCardAlongCartPlay() {
        showLoading(5000);
//        response = (CsCard.GetGiftCardAlongCartResponse) getIntent().getExtras().getSerializable("cardResponse");
        List<CsCard.GiftItem> list = (List<CsCard.GiftItem>) getIntent().getExtras().getSerializable("cardList");
        if (list != null) {
//            List<CsCard.GiftItem> list = response.getGiftcardcart().getGiftitemList();
            String items = "";
            for (int i = 0; i < list.size(); i++) {
                if (i == list.size() - 1) {
                    items += list.get(i).getGiftCardCartItemId();
                } else {
                    items += list.get(i).getGiftCardCartItemId() + ",";
                }
            }

            CsCard.GiftCardAlongCartPlayRequest.Builder builder = CsCard.GiftCardAlongCartPlayRequest.newBuilder();
            builder.setGiftcardcartitems(items).setLocalecode(AccountManager.getInstance().getLocaleCode());
            builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
            builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
            builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
            NetEngine.postRequest(builder, new INetEngineListener<CsCard.GiftCardAlongCartPlayResponse>() {

                @Override
                public void onSuccess(CsCard.GiftCardAlongCartPlayResponse response) {
                    closeLoading();
                    LogUtils.d(response.toString());
                    Message msg = Message.obtain();
                    msg.obj = response;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onFailure(int ret, String errMsg) {
                    closeLoading();
                }
            });
        }

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CsCard.GiftCardAlongCartPlayResponse response = (CsCard.GiftCardAlongCartPlayResponse) msg.obj;
//            grandTotal = getResources().getString(R.string.String_order_price);
            grandTotal = response.getGiftcardcartplay().getGrandTotal() + "";
            cardOrderGrandTotalTv.setText(UIUtils.getCurrency(CardCartOderActivity.this) + grandTotal);
            List<CsCard.GiftItemplay> list = response.getGiftcardcartplay().getGiftitemplayList();
            for (int i = 0; i < list.size(); i++) {
                cardCount += list.get(i).getQty();

            }
            MyAdapter adapter = new MyAdapter(list);
            cardCartListView.setAdapter(adapter);
            setListViewHeightBasedOnChildren(cardCartListView);
        }
    };

    class MyAdapter extends BaseAdapter {
        private List<CsCard.GiftItemplay> list;
        private ImageLoader imageLoader;
        private DisplayImageOptions options;
//        private final int[] GIFT_CARD_RESOURCES = {R.mipmap.small_card1, R.mipmap.small_card2, R.mipmap.small_card3};
        private final int[] GIFT_CARD_RESOURCES = {R.mipmap.card};

        public MyAdapter(List<CsCard.GiftItemplay> list) {
            this.list = list;
            this.options = ImageLoaderHelper.getInstance(CardCartOderActivity.this).getDisplayOptions();
            this.imageLoader = ImageLoader.getInstance();
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(CardCartOderActivity.this).inflate(R.layout.card_cart_order_item, null);
                holder = new ViewHolder();
                holder.cardImg = (ImageView) convertView.findViewById(R.id.card_cart_order_img);
                holder.cardAmountTv = (TextView) convertView.findViewById(R.id.card_cart_order_amount_tv);
                holder.cardAddresseeNameTv = (TextView) convertView.findViewById(R.id.card_cart_order_addressee_name_tv);
                holder.cardAddresseeMailTv = (TextView) convertView.findViewById(R.id.card_cart_order_addressee_mail_tv);
                holder.cardMessageTv = (TextView) convertView.findViewById(R.id.card_cart_order_message_tv);
                holder.cardPriceTv = (TextView) convertView.findViewById(R.id.card_cart_order_price_tv);
                holder.cardCountTv = (TextView) convertView.findViewById(R.id.card_cart_order_count_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CsCard.GiftItemplay itemplay = list.get(position);
//            imageLoader.displayImage(itemplay.getEmailTemplate(), holder.cardImg, options);
            String imgUrl = itemplay.getEmailTemplate();
            String[] strings = imgUrl.split("!")[0].split("/");
            String img = strings[strings.length - 1].split("\\.")[0];
            switch (img) {
                case "68":
                    holder.cardImg.setImageResource(GIFT_CARD_RESOURCES[0]);
                    break;
                case "18":
                    holder.cardImg.setImageResource(GIFT_CARD_RESOURCES[1]);
                    break;
                case "19":
                    holder.cardImg.setImageResource(GIFT_CARD_RESOURCES[2]);
                    break;
            }
            String amount = getResources().getString(R.string.gift_card_item_price);
            holder.cardAmountTv.setText(UIUtils.getCurrency(CardCartOderActivity.this, itemplay.getAmount()));
            holder.cardAddresseeNameTv.setText(itemplay.getRecipientName());
            holder.cardAddresseeMailTv.setText(itemplay.getRecipientEmail());
            holder.cardMessageTv.setText(itemplay.getMessage());
//            amount = String.format(amount, itemplay.getFaceAmount() * itemplay.getQty());
            holder.cardPriceTv.setText(UIUtils.getCurrency(CardCartOderActivity.this, itemplay.getFaceAmount() * itemplay.getQty()));
            String countStr = getResources().getString(R.string.gift_card_count_msg, itemplay.getQty() + "");
//            countStr = String.format(countStr, itemplay.getQty());
            holder.cardCountTv.setText(countStr);
            return convertView;
        }

        class ViewHolder {
            ImageView cardImg;
            TextView cardAmountTv;
            TextView cardAddresseeNameTv;
            TextView cardAddresseeMailTv;
            TextView cardMessageTv;
            TextView cardPriceTv;
            TextView cardCountTv;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.setMargins(0, 30, 0, 0);
        listView.setLayoutParams(params);
    }
}
