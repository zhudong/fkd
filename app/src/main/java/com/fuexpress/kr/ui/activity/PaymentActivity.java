package com.fuexpress.kr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.protobuf.GeneratedMessage;
import com.socks.library.KLog;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;
import fksproto.CsCard;
import fksproto.CsMy;
import fksproto.CsUser;

/**
 * Created by Administrator on 2016-10-28.
 */
public class PaymentActivity extends BaseActivity {

    public static final String SHIPPING_FEE = "shipping_fee";
    public static final String CURRENCY_CODE = "currencyCode";
    public static final String CURRENCY_NAME = "currencyName";
    public static final String IS_SEND_PACKAGE = "is_send_package";
    public static final String NEED_PAY = "need_pay";

    public static final String CODE_COUPON_NAME = "code_coupon_name";
    public static final String CODE_COUPON_ID = "code_coupon_id";
    public static final String DEF_PAY_CODE = "def_pay_code";
    public static final String DEF_PAY_NAME = "def_pay_name";
    public static final String IS_USE_BALANCE = "is_use_balance";
    public static final String IS_SHOW_GIFTLAYOUT = "is_show_gift_layout";

    private View rootView;
    private Button confirmBtn;
    private TextView couponNameTv;
    private LinearLayout couponLayout;
    private LinearLayout giftLayout;
    private TextView freeBalanceTv;
    private ImageView balanceCheckedIv;
    private TitleBarView titleBar;
    private ImageView backIv;
    private RelativeLayout upMemberLayout;
    private EditText giftCartEdit;
    private Button bindBtn;
    private Switch memberSwitch;
    private ListView vipListView;

    private String currencyCode;
    private float fee;
    private CsUser.UseableCouponListResponse resp;

    private float account;
    private int paymentType;
    private String paymentString = "";
    private int paymentPos;
    private CsUser.CouponList coupon;
    private int couponId = -1;
    private ListView payListView;
    private String payCode = "";
    private int giftCardOrderState;
    private LinearLayout balanceLayout;
    private boolean isSendPackage;
    private boolean isUseBalance = true;
    private String levelUpFlag = "0";
    private int membergroupid;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_payment_activity, null);
        confirmBtn = (Button) rootView.findViewById(R.id.btn_confirm);
        couponNameTv = (TextView) rootView.findViewById(R.id.payment_coupon_name_tv);
        couponLayout = (LinearLayout) rootView.findViewById(R.id.payment_coupon_layout);
        giftLayout = (LinearLayout) rootView.findViewById(R.id.payment_gift_layout);
        freeBalanceTv = (TextView) rootView.findViewById(R.id.tv_balance);
        balanceCheckedIv = (ImageView) rootView.findViewById(R.id.rb_use_balance);
        titleBar = (TitleBarView) rootView.findViewById(R.id.payment_titile_bar);
        backIv = titleBar.getIv_in_title_back();
        payListView = (ListView) rootView.findViewById(R.id.payment_listview);
        balanceLayout = (LinearLayout) rootView.findViewById(R.id.ll_use_balance);
        giftCartEdit = (EditText) rootView.findViewById(R.id.payment_gift_edit);
        bindBtn = (Button) rootView.findViewById(R.id.payment_bind_btn);
        upMemberLayout = (RelativeLayout) rootView.findViewById(R.id.rl_up_member);
        vipListView = (ListView) rootView.findViewById(R.id.payment_vip_listview);
        memberSwitch = (Switch) rootView.findViewById(R.id.sw_member);
        memberSwitch.setChecked(false);
        memberSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vipListView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        fee = getIntent().getFloatExtra(SHIPPING_FEE, 0.00f);
        currencyCode = getIntent().getStringExtra(CURRENCY_CODE);
        boolean isShowGiftLayout = getIntent().getBooleanExtra(IS_SHOW_GIFTLAYOUT, false);
        if(isShowGiftLayout){
            giftLayout.setVisibility(View.VISIBLE);
        }else {
            giftLayout.setVisibility(View.GONE);
        }
//        currencyCode = AccountManager.getInstance().getCurrencyCode();
        int couponId = getIntent().getIntExtra(CODE_COUPON_ID, -1);
        if (couponId >= 0) {
            couponNameTv.setText(SPUtils.get(this, CODE_COUPON_NAME, "").toString());
        } else {
            SPUtils.put(PaymentActivity.this, CODE_COUPON_ID, -1);
            useableCouponListRequest(currencyCode, fee);
        }
        isUseBalance = getIntent().getBooleanExtra(IS_USE_BALANCE, isUseBalance);
        if (isUseBalance) {
            balanceCheckedIv.setImageResource(R.mipmap.select_for_uid);
        } else {
            balanceCheckedIv.setImageResource(R.mipmap.unselect_for_uid);
        }
        getAccountBalanceRequest(currencyCode);
        getFKDPaymentListRequest(currencyCode);
        redeemGiftCardRequest(currencyCode);

        paymentType = getIntent().getIntExtra("payType", Constants.PAYMENT_ALIPAY);
        paymentPos = getIntent().getIntExtra("paymentPos", 0);
        payCode = getIntent().getStringExtra("payCode");
        paymentString = getIntent().getStringExtra("paymentString");
        isSendPackage = getIntent().getBooleanExtra(IS_SEND_PACKAGE, false);
        if (isSendPackage) {
            couponLayout.setVisibility(View.VISIBLE);
        } else {
            couponLayout.setVisibility(View.GONE);
        }
        boolean isOrderDetails = getIntent().getBooleanExtra(CardOrderDetailActivity.IS_ORDER_DETAILS, false);
        if (isOrderDetails) {
            balanceLayout.setVisibility(View.GONE);
        } else {
            balanceLayout.setVisibility(View.VISIBLE);
        }
        backIv.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        couponLayout.setOnClickListener(this);
        balanceCheckedIv.setOnClickListener(this);
        bindBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                coupon = (CsUser.CouponList) data.getExtras().getSerializable(CouponActivity.CODE_COUPON);
                String s = AccountManager.getInstance().getCurrencyCode();
                couponNameTv.setText(coupon.getShippingcouponname() + " " + UIUtils.getCurrency(this, currencyCode, coupon.getDiscountamount()));
                couponId = coupon.getShippingcouponcustomerid();
            }
        }
    }

    //    private void saveSp() {
//        SPUtils.put(this, ParclePaymentActivity.PAY_TYPE, paymentType);
//        SPUtils.putString(this, ParclePaymentActivity.PAY_TYPE_NAME, paymentString);
//        SPUtils.put(this, ParclePaymentActivity.PAY_TYPE_POSITION, paymentPos);
//    }
//
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_confirm:
//                saveSp();
                SPUtils.put(this, AccountManager.getInstance().getCurrencyCode(), paymentType);
                SPUtils.put(this, AccountManager.getInstance().getCurrencyCode() + DEF_PAY_NAME, paymentString);
                SPUtils.put(this, AccountManager.getInstance().getCurrencyCode() + "paymentPos", paymentPos);
                SPUtils.put(this, AccountManager.getInstance().getCurrencyCode() + DEF_PAY_CODE, payCode);
                SPUtils.put(this, AccountManager.getInstance().getCurrencyCode() + IS_USE_BALANCE, isUseBalance);
                intent.putExtra("payType", (int) SPUtils.get(this, AccountManager.getInstance().getCurrencyCode(), 0));
                intent.putExtra("paymentString", SPUtils.get(this, AccountManager.getInstance().getCurrencyCode() + DEF_PAY_NAME, "").toString());
                intent.putExtra("payment", payCode);
                intent.putExtra("paymentPos", paymentPos);
                intent.putExtra(IS_USE_BALANCE, isUseBalance);
                intent.putExtra("account", account);
                Bundle bundle = new Bundle();
                bundle.putSerializable(CouponActivity.CODE_COUPON, coupon);
                intent.putExtras(bundle);
                setResult(Constants.PAYMENT_REQUEST_CODE, intent);
                EventBus.getDefault().post(new BusEvent(BusEvent.PAYMENT_TYPE_CHANGE, ""));
                EventBus.getDefault().post(new BusEvent(BusEvent.CODE_COUPON_ID,
                        ((Integer) SPUtils.get(PaymentActivity.this, CODE_COUPON_ID, -1)) != -1
                                ? SPUtils.get(PaymentActivity.this, CODE_COUPON_ID, -1)
                                : couponId));
                if (coupon != null) {
                    SPUtils.put(PaymentActivity.this, CODE_COUPON_NAME, coupon.getShippingcouponname());
                    SPUtils.put(PaymentActivity.this, CODE_COUPON_ID, coupon.getShippingcouponcustomerid());

                }
                finish();
                break;
            case R.id.payment_coupon_layout:
                intent.setClass(PaymentActivity.this, CouponActivity.class);
                intent.putExtra(PaymentActivity.SHIPPING_FEE, fee);
                intent.putExtra(PaymentActivity.CURRENCY_CODE, currencyCode);
                startActivityForResult(intent, CouponActivity.CODE_RESULT_COUPON);
                break;
            case R.id.iv_in_title_back:
                finish();
                break;
            case R.id.rb_use_balance:
                isUseBalance = !isUseBalance;
                if (isUseBalance) {
                    balanceCheckedIv.setImageResource(R.mipmap.select_for_uid);
                } else {
                    balanceCheckedIv.setImageResource(R.mipmap.unselect_for_uid);
                }
                break;
            case R.id.payment_bind_btn:
                getGiftCardStoresiteRequest(giftCartEdit.getText().toString(), AccountManager.getInstance().getLocaleCode(), memberSwitch.isChecked() ? 1: 0, membergroupid);
                break;
        }
    }

    public void getGiftCardStoresiteRequest(final String giftCard, String localeCode, final int levelUp, final int membergroupid){
        CsCard.GetGiftCardStoresiteRequest.Builder builder = CsCard.GetGiftCardStoresiteRequest.newBuilder();
        builder.setGiftCard(giftCard);
        builder.setLocalecode(localeCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GetGiftCardStoresiteResponse>() {

            @Override
            public void onSuccess(CsCard.GetGiftCardStoresiteResponse response) {
                LogUtils.d(response.toString());
                bindGiftCardRequest(giftCard, levelUp, membergroupid);
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, errMsg);
                        dissMissProgressDiaLog(1000);
                    }
                });
            }
        });
    }

    public void bindGiftCardRequest(String giftCard, int levelUp, int membergroupid){
        CsCard.BindGiftCardRequest.Builder builder = CsCard.BindGiftCardRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setGiftCard(giftCard);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        builder.setLevelup(levelUp);
        if(levelUp == 1){
            builder.setMembergroupid(membergroupid);
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.BindGiftCardResponse>() {

            @Override
            public void onSuccess(CsCard.BindGiftCardResponse response) {
                LogUtils.d(response.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDiaLog(SweetAlertDialog.SUCCESS_TYPE, getString(R.string.my_gift_card_order_binding_success));
                        dissMissProgressDiaLog(1000);
                        getAccountBalanceRequest(currencyCode);
                        AccountManager.getInstance().getUserinfo();
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, getString(R.string.my_gift_card_order_binding_faile_text));
                        dissMissProgressDiaLog(1000);
                    }
                });
            }
        });
    }

    /**
     * 初始化充值卡列表
     */
    public void redeemGiftCardRequest(String currencyCode) {
        CsCard.RedeemGiftCardRequest.Builder builder = CsCard.RedeemGiftCardRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(currencyCode);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.RedeemGiftCardReponse>() {

            @Override
            public void onSuccess(CsCard.RedeemGiftCardReponse response) {
                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                giftHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    private Handler giftHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CsCard.RedeemGiftCardReponse response = (CsCard.RedeemGiftCardReponse) msg.obj;
            if (TextUtils.equals(response.getLevelupflag(), "0")) {
                upMemberLayout.setVisibility(View.GONE);
            }
            if (TextUtils.equals(response.getLevelupflag(), "1")) {
                upMemberLayout.setVisibility(View.VISIBLE);
                final VipAdapter adapter = new VipAdapter(PaymentActivity.this, response.getMemberGroupListList());
                adapter.setCheckedAtPosition(0);
                membergroupid = response.getMemberGroupList(0).getMembergroupid();
                vipListView.setAdapter(adapter);
                vipListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        adapter.setCheckedAtPosition(position);
                        adapter.notifyDataSetChanged();
                        membergroupid = ((CsCard.MemberGroupList) adapter.getItem(position)).getMembergroupid();
                    }
                });
            }
        }
    };

    public void getAccountBalanceRequest(final String currencyCode) {
        CsMy.GetAccountBalanceRequest.Builder builder = CsMy.GetAccountBalanceRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(currencyCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsMy.GetAccountBalanceResponse>() {

            @Override
            public void onSuccess(final CsMy.GetAccountBalanceResponse response) {
                LogUtils.d(response.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        freeBalanceTv.setText(UIUtils.getCurrency(PaymentActivity.this, currencyCode, response.getFreeBalance()));
                        account = response.getFreeBalance();
                        if (response.getFreeBalance() > 0 && isUseBalance) {
//                            balanceCheckedIv.setEnabled(true);
                            balanceCheckedIv.setImageResource(R.mipmap.select_for_uid);
                            isUseBalance = true;
                        } else {
//                            balanceCheckedIv.setEnabled(false);
                            balanceCheckedIv.setImageResource(R.mipmap.unselect_for_uid);
                            isUseBalance = false;
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        if (event.getType() == BusEvent.CODE_COUPON_EXCHANGE_SUCCESS) {
            useableCouponListRequest(TextUtils.isEmpty(currencyCode) ? "CNY" : currencyCode, fee);
        }
    }

    public void useableCouponListRequest(String currencyCode, float shippingFee) {
        showLoading();
        CsUser.UseableCouponListRequest.Builder builder = CsUser.UseableCouponListRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(currencyCode);
        builder.setShippingfee(shippingFee);

        NetEngine.postRequest(builder, new INetEngineListener<CsUser.UseableCouponListResponse>() {

            @Override
            public void onSuccess(final CsUser.UseableCouponListResponse response) {
                closeLoading();
                LogUtils.d(response.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.getCount() > 0) {
                            couponNameTv.setText(getString(R.string.coupon_count_msg, response.getCount()));
                            resp = response;
                            if (isSendPackage) {
                                couponLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            couponLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }

    public void getFKDPaymentListRequest(String currencyCode) {
        CsUser.GetFKDPaymentListRequest.Builder builder = CsUser.GetFKDPaymentListRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(currencyCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetFKDPaymentListResponse>() {

            @Override
            public void onSuccess(CsUser.GetFKDPaymentListResponse response) {
                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                paymentHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        confirmBtn.setEnabled(false);
                        confirmBtn.setBackgroundResource(R.drawable.shape_btn_bg_red_more_radius_unenable);
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
            final PaymentAdapter adapter = new PaymentAdapter(PaymentActivity.this, list);
            payListView.setAdapter(adapter);
            adapter.setCheckedAtPosition(paymentPos);
//            paymentString = list.get(0).getPayname();
//            if(list != null && list.size() > 0){
//                payCode = list.get(0).getPaycode();
//            }
//            KLog.i("payCode = " + payCode);
//            if (TextUtils.equals(payCode, "adyen")) {
//                paymentType = Constants.PAYMENT_ADYEN;
//            }
//            if (TextUtils.equals(payCode, "krbank")) {
//                paymentType = Constants.PAYMENT_KRBANK;
//            }
//            if (TextUtils.equals(payCode, "wxap")) {
//                paymentType = Constants.PAYMENT_WECHAT;
//            }
//            if (TextUtils.equals(payCode, "alipay")) {
//                paymentType = Constants.PAYMENT_ALIPAY;
//            }
            payListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adapter.setCheckedAtPosition(position);
                    adapter.notifyDataSetChanged();
                    paymentPos = position;
                    paymentString = ((CsUser.PaymentList) parent.getAdapter().getItem(position)).getPayname();
                    payCode = ((CsUser.PaymentList) parent.getAdapter().getItem(position)).getPaycode();
                    KLog.i("payCode = " + payCode);
                    if (TextUtils.equals(payCode, "adyen")) {
                        paymentType = Constants.PAYMENT_ADYEN;
                    }
                    if (TextUtils.equals(payCode, "krbank")) {
                        paymentType = Constants.PAYMENT_KRBANK;
                    }
                    if (TextUtils.equals(payCode, "wxap")) {
                        paymentType = Constants.PAYMENT_WECHAT;
                    }
                    if (TextUtils.equals(payCode, "alipay")) {
                        paymentType = Constants.PAYMENT_ALIPAY;
                    }
                    if (TextUtils.equals(payCode, "daoupay")) {
                        paymentType = Constants.PAYMENT_DAOUPAY;
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Payment Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

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
//                holder.nameTv.setText(getString(R.string.String_adyen_pay));
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.adyen_pay));
            }
            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_KRBANK)) {
//                holder.nameTv.setText(getString(R.string.String_krbank_pay));
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.krbank_pay));
            }
            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_ALIPAY)) {
//                holder.nameTv.setText(getString(R.string.String_ali_pay));
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.pay_alipay));
            }
            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_WXPAY)) {
//                holder.nameTv.setText(getString(R.string.String_weixin_pay));
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.pay_wechat));
            }
            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_DAOUPAY)) {
//                holder.nameTv.setText(item.getPayname());
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.daoupay));
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

    class VipAdapter extends BaseAdapter {
        private Context context;
        private List<CsCard.MemberGroupList> list;
        private int pos;

        public VipAdapter(Context context, List<CsCard.MemberGroupList> list){
            this.context = context;
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
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.vip_item, null);
                holder = new ViewHolder();
                holder.titleTv = (TextView) convertView.findViewById(R.id.vip_item_title_txt);
                holder.ct = (CheckedTextView) convertView.findViewById(R.id.vip_item_ct);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            CsCard.MemberGroupList item = list.get(position);
            holder.titleTv.setText(item.getMembergroupname() + " " + item.getSignandfee());
            holder.ct.setCheckMarkDrawable(R.drawable.selector_payment_checkbox);
            if (pos == position) {
                holder.ct.setChecked(true);
            } else {
                holder.ct.setChecked(false);
            }

            return convertView;
        }
        public void setCheckedAtPosition(int pos) {
            this.pos = pos;
        }

        class ViewHolder{
            TextView titleTv;
            CheckedTextView ct;
        }
    }
}