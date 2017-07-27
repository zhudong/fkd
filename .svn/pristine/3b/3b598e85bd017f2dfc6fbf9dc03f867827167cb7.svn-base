package com.fuexpress.kr.ui.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.socks.library.KLog;

/**
 * Created by Administrator on 2016/5/12.
 */
public class CardOrderPaymentActivity extends BaseActivity {

    private View         rootView;
//    private LinearLayout alipayLayout;
//    private LinearLayout wechatLayout;
//    private ImageView    alipayIv;
//    private ImageView    wechatIv;
    private Button       confirmBtn;
    private String       payment;
    private int          giftCardOrderId;
    private ListView     payListView;
    private int paymentPos;
    private String mCurrencyCode;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_card_order_payment, null);
        TitleBarView titleBarView = (TitleBarView) rootView.findViewById(R.id.card_payment_title_bar);
        titleBarView.setTitleText(getString(R.string.card_order_payment_title_bar_title));
        ImageView backIv = titleBarView.getIv_in_title_back();
        Intent    intent = getIntent();
        payment = intent.getStringExtra("payment");
        giftCardOrderId = intent.getIntExtra("giftCardOrderId", -1);
        paymentPos = getIntent().getIntExtra("paymentPos", 0);
//        alipayIv = (ImageView) rootView.findViewById(R.id.card_order_payment_alipay_iv);
//        alipayLayout = (LinearLayout) rootView.findViewById(R.id.card_order_payment_alipay_layout);
//        wechatIv = (ImageView) rootView.findViewById(R.id.card_order_payment_wechat_iv);
//        wechatLayout = (LinearLayout) rootView.findViewById(R.id.card_order_payment_wechat_layout);
        confirmBtn = (Button) rootView.findViewById(R.id.card_order_payment_confirm_btn);
        payListView = (ListView) rootView.findViewById(R.id.card_order_payment_listview);
        mCurrencyCode = getIntent().getStringExtra(CardOrderDetailActivity.CURRENCY_CODE);
        if (mCurrencyCode==null||"".equals(mCurrencyCode))
            mCurrencyCode = AccountManager.getInstance().getCurrencyCode();
        if (TextUtils.equals(payment, "wxap")) {
//            alipayIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.unselect_icon));
//            wechatIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.select_icon));
        }
        if(TextUtils.equals(payment, "alipay")){
//            alipayIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.select_icon));
//            wechatIv.setBackground(ContextCompat.getDrawable(this, R.mipmap.unselect_icon));
        }

//        GetIntlPaymentListRequest();
        backIv.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
//        alipayLayout.setOnClickListener(this);
//        wechatLayout.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_in_title_back:
                finish();
                break;
            case R.id.card_order_payment_confirm_btn:
//                editGiftCardOrderPayMethod(giftCardOrderId, payment);
                break;
        }
    }
/*
    public void editGiftCardOrderPayMethod(int giftCardOrderId, final String payment){
        showLoading(5000);
        CsCard.EditGiftCardOrderPayMethodRequest.Builder builder = CsCard.EditGiftCardOrderPayMethodRequest.newBuilder();
        builder.setGiftCardOrderId(giftCardOrderId);
        builder.setPaymentCode(payment);
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.EditGiftCardOrderPayMethodResponse>() {

            @Override
            public void onSuccess(CsCard.EditGiftCardOrderPayMethodResponse response) {
                closeLoading();
                LogUtils.d(response.toString());
                Intent intent = new Intent();
                intent.putExtra("payment", payment);
                intent.putExtra("paymentPos", paymentPos);
                setResult(Constants.CARD_ORDER_PAYMENT_REQUEST_CODE, intent);
                finish();
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }

    public void GetIntlPaymentListRequest() {
        CsUser.GetIntlPaymentListRequest.Builder builder = CsUser.GetIntlPaymentListRequest.newBuilder();
        builder.setCurrencycode(mCurrencyCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetIntlPaymentListResponse>() {

            @Override
            public void onSuccess(CsUser.GetIntlPaymentListResponse response) {
                KLog.i(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                paymentHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    private Handler paymentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CsUser.GetIntlPaymentListResponse response = (CsUser.GetIntlPaymentListResponse) msg.obj;
            List<CsUser.PaymentList>          list     = response.getPaymantlistList();
            List<CsUser.PaymentList>          payList  = new ArrayList<CsUser.PaymentList>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPaycode().equals(Constants.GIFT_CARD_PAYMENT_ADYEN)) {
                    payList.add(list.get(i));
                }
            }
            final PaymentAdapter adapter = new PaymentAdapter(CardOrderPaymentActivity.this, payList);
            payListView.setAdapter(adapter);
            adapter.setCheckedAtPosition(paymentPos);
            payListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adapter.setCheckedAtPosition(position);
                    adapter.notifyDataSetChanged();
                    paymentPos = position;
                    String payCode = ((CsUser.PaymentList) parent.getAdapter().getItem(position)).getPaycode();
                    KLog.i("payCode = " + payCode);
                    if (TextUtils.equals(payCode, "adyen")) {
                        payment = Constants.GIFT_CARD_PAYMENT_ADYEN;
                    }
                    if (TextUtils.equals(payCode, "krbank")) {
                        payment = Constants.GIFT_CARD_PAYMENT_KRBANK;
                    }
                }
            });
        }
    };

    class PaymentAdapter extends BaseAdapter {

        private Context                  ctx;
        private List<CsUser.PaymentList> list;
        private int                      pos;

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
            holder.nameTv.setText(item.getPayname());
            holder.checkedTv.setCheckMarkDrawable(R.drawable.selector_payment_checkbox);
            if (pos == position) {
                holder.checkedTv.setChecked(true);
            } else {
                holder.checkedTv.setChecked(false);
            }
            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_ADYEN)) {
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.adyen_pay));
            }
            if (TextUtils.equals(item.getPaycode(), Constants.GIFT_CARD_PAYMENT_KRBANK)) {
                holder.logoIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.krbank_pay));
            }
//            if (TextUtils.equals(item.getPaycode(), Constants.PAYMENT_DEFAULT)) {
//                holder.checkedTv.setChecked(true);
//            }

            return convertView;
        }

        public void setCheckedAtPosition(int pos) {
            this.pos = pos;
        }

        class ViewHolder {
            ImageView       logoIv;
            TextView        nameTv;
            CheckedTextView checkedTv;
        }
    }*/

}
