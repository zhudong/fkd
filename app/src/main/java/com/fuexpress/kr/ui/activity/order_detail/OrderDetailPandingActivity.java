package com.fuexpress.kr.ui.activity.order_detail;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.OrderConstants;
import com.fuexpress.kr.bean.PayResultBaen;
import com.fuexpress.kr.bean.SalesOrderBean;
import com.fuexpress.kr.bean.SalesOrderItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.GiftCardManager;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.model.SalesOrderManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.net.RequestNetListener;
import com.fuexpress.kr.ui.activity.DaoUPayActivity;
import com.fuexpress.kr.ui.activity.KrBankInfoActivity;
import com.fuexpress.kr.ui.activity.PaymentSuccessActivity;
import com.fuexpress.kr.ui.activity.my_order.OrderAll;
import com.fuexpress.kr.ui.adapter.SalesOrderDetailAdapter;
import com.fuexpress.kr.ui.adapter.ShippingAdapter;
import com.fuexpress.kr.ui.fragment.ButtomFragment;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CrowdProgressDetail;
import com.fuexpress.kr.ui.view.CrowdTimer;
import com.fuexpress.kr.utils.ClassUtil;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.TimeUtils;
import com.socks.library.KLog;


import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;
import fksproto.CsOrder;
import okhttp3.Response;


/**
 * Created by k550 on 5/9/2016.
 */
public class OrderDetailPandingActivity extends BaseActivity {
    //    final String string = UIUtils.getString(R.string.String_price);
    private TextView leftTv, centerTv, rigthTv;
    private ImageView leftIv, rightIv;
    private TextView idTv, priceTv, timeTv, countTv;
    public SalesOrderBean mSalesOrderBean;
    private ListView mListView, mShippingListView;
    private ImageView shrinkIv;
    private SalesOrderDetailAdapter mAdapter, mAdapterShort;
    private List<SalesOrderItemBean> mList, mListShort;
    private PopupWindow mPopWindow;
    public TextView editBtn;
    TextView payBtn;
    public RelativeLayout shrinkLayout;
    private View changePayMethod;
    public boolean isShrink = true;       //默认收缩
    public TextView itemCountTv, sumTv, payMethodTv, timeLeftTv;
    public View bottomLayout;
    private CrowdProgressDetail mProgressDetail;
    private CrowdTimer mCrowdTimer;
    public float freeBalance = 0;
    public int itemOrderCount;
    //海外方式直邮需要委外的信息
    public LinearLayout otherLayout;
    public TextView addressTv, nameTv, deliverTv, feeDetailTv;
    private int payType;//支付方式
    private boolean isWaitingCancel = false;//是否为等待取消
    private String mCurrencyCode = "";
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.title_tv_left:
                    onBackPressed();
                    break;
                case R.id.title_iv_left:
                    onBackPressed();
                    break;
                case R.id.changePayMethodLayout:
                    break;
                case R.id.editBtn:
                    changeOrderState();
                    break;
                case R.id.shrinkLayout:
                    isShrink = !isShrink;
                    shrinkList();
                    break;
                case R.id.payBtn:
                    payOrder();
                    break;
            }
        }
    };
    private ButtomFragment mButtomFragment;
    private String mPayCode;
    private CsOrder.SalesOrder mOrder;

    @Override
    public View setInitView() {
        View view = View.inflate(this, R.layout.activity_order_detail_panding, null);
        mSalesOrderBean = (SalesOrderBean) getIntent().getBundleExtra(OrderAll.BEAN).getSerializable(OrderAll.BEAN);
        KLog.i("total = " + mSalesOrderBean.total_paid);
        feeDetailTv = (TextView) view.findViewById(R.id.feeTv);
        leftIv = (ImageView) view.findViewById(R.id.title_iv_left);
        rightIv = (ImageView) view.findViewById(R.id.title_iv_right);
        leftTv = (TextView) view.findViewById(R.id.title_tv_left);
        centerTv = (TextView) view.findViewById(R.id.title_tv_center);
        rigthTv = (TextView) view.findViewById(R.id.title_tv_right);
        rightIv.setVisibility(View.GONE);
        rigthTv.setVisibility(View.GONE);
        leftIv.setOnClickListener(onClickListener);
        leftTv.setOnClickListener(onClickListener);
        leftTv.setText(getString(R.string.order_panding));
        centerTv.setText(getString(R.string.panding));
        mListView = (ListView) view.findViewById(R.id.mListView);
        sumTv = (TextView) view.findViewById(R.id.sumTv);
        payBtn = (TextView) view.findViewById(R.id.payBtn);
        payBtn.setOnClickListener(onClickListener);
        timeLeftTv = (TextView) view.findViewById(R.id.timeLeftTv);
        bottomLayout = view.findViewById(R.id.rl_bottom);

        View footView = View.inflate(this, R.layout.layout_order_panding, null);
        if (mSalesOrderBean.state == CsOrder.SalesOrderState.SALES_ORDER_STATE_AWAITING_CANCEL_VALUE) {
            footView = View.inflate(this, R.layout.layout_order_payed, null);
        }
        mShippingListView = (ListView) footView.findViewById(R.id.list_shipping);
//        arrowIv = (ImageView) footView.findViewById(R.id.arrow_iv);
//        arrowIv.setVisibility(View.VISIBLE);
        shrinkLayout = (RelativeLayout) footView.findViewById(R.id.shrinkLayout);
        shrinkLayout.setOnClickListener(onClickListener);
        shrinkIv = (ImageView) footView.findViewById(R.id.upIv);
        itemCountTv = (TextView) footView.findViewById(R.id.itemCountTv);

        payMethodTv = (TextView) footView.findViewById(R.id.payment_type_tv);

        otherLayout = (LinearLayout) footView.findViewById(R.id.otherLayout);
        addressTv = (TextView) footView.findViewById(R.id.ad_address_tv);
        nameTv = (TextView) footView.findViewById(R.id.ad_name_tv);
        deliverTv = (TextView) footView.findViewById(R.id.deliverTv);

        mButtomFragment = ButtomFragment.newInstance();
        changePayMethod = footView.findViewById(R.id.change_pay_method);
        if (changePayMethod != null) {
            changePayMethod.setOnClickListener(onClickListener);
            mButtomFragment.attachToButton(this, changePayMethod);
        }

        mListView.addFooterView(footView);

        addCrowdView();
        getSalesDetail();
        switch (mSalesOrderBean.state) {
            case CsOrder.SalesOrderState.SALES_ORDER_STATE_AWAITING_CANCEL_VALUE:
                centerTv.setText(getString(R.string.wait_cancel));
                leftTv.setText(getString(R.string.order_wait_cancel));
                editBtn.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                break;
        }


        return view;
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_GIFT_CARD_BALANCE_COMPLETE:
                boolean isSuccess = event.getBooleanParam();
                if (isSuccess) {
                    freeBalance = GiftCardManager.getInstance().mGiftcardaccount;
                }
                break;
            case BusEvent.GET_TEXT_TRANSLATE_SUCCESS:
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }

        if (event.getType() == BusEvent.PAY_MENT_RESULT && event.getStrParam().equals(mSalesOrderBean.order_number)) {
            if (event.getBooleanParam()) {
                SysApplication.mCurrentRequestPayment = "";
                Intent intent = new Intent(this, PaymentSuccessActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(OrderDetailPandingActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void shrinkList() {
        if (isShrink) {
            if (mAdapterShort == null) {
                mAdapterShort = new SalesOrderDetailAdapter(OrderDetailPandingActivity.this, mListShort);
            }
            shrinkIv.setImageResource(R.mipmap.arrow_down);
            mListView.setAdapter(mAdapterShort);
            int length = mList.size() - mListShort.size();
            itemCountTv.setText(getString(R.string.left_no) + length + getString(R.string.xiang));
        } else {
            itemCountTv.setText(getString(R.string.total_no) + mList.size() + getString(R.string.xiang));
            shrinkIv.setImageResource(R.mipmap.arrow_up);
            mListView.setAdapter(mAdapter);
        }

    }

    public void getSalesDetail() {
        //获取账户余额
        GiftCardManager.getInstance().getGiftCardBalanceRequest();
        SalesOrderManager.getSalesOrderDetail(mSalesOrderBean.order_id, new RequestNetListener<CsOrder.GetSalesOrderDetailResponse>() {
            @Override
            public void onSuccess(CsOrder.GetSalesOrderDetailResponse response) {
                mOrder = response.getOrder();
                KLog.i("getSalesDetail", response.toString());
                if (response.getOrder().getState() == CsOrder.SalesOrderState.SALES_ORDER_STATE_AWAITING_CANCEL_VALUE) {
                    isWaitingCancel = true;
//                    arrowIv.setVisibility(View.GONE);
                }
//                response.getOrder().getUseGiftCard()
                mCurrencyCode = response.getOrder().getCurrencycode();
                String price = UIUtils.getCurrency(myActivity(), response.getOrder().getCurrencycode(), response.getOrder().getGrandTotal());
                priceTv.setText(getString(R.string.grand_total) + price);
                sumTv.setText(price);
                idTv.setText(getString(R.string.order_no) + response.getOrder().getOrderNumber());
                timeTv.setText(getString(R.string.order_date) + TimeUtils.getDateStyle(response.getOrder().getCreateTime()));
                //国际版只有“Adyen"支付
                payMethodTv.setText(mOrder.getPayMethodStr());
                payType = response.getOrder().getPayMethod();
                mPayCode = getPayCode();
                timeLeftTv.setText(getLeftTime(System.currentTimeMillis(), response.getOrder().getCreateTime()));

                mList = ClassUtil.conventSalesOrderItemList2BeanList(response.getOrderItemsList());
                itemOrderCount = 0;
                for (SalesOrderItemBean bean : mList) {
                    itemOrderCount = itemOrderCount + bean.qty;
                }
                countTv.setText(getString(R.string.item_number) + itemOrderCount);
                mAdapter = new SalesOrderDetailAdapter(OrderDetailPandingActivity.this, mList);
                mListView.setAdapter(mAdapter);
                if (mList.size() > 3) {
                    mListShort = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        mListShort.add(mList.get(i));
                    }
                    mAdapterShort = new SalesOrderDetailAdapter(OrderDetailPandingActivity.this, mListShort);
                    mListView.setAdapter(mAdapterShort);
                    shrinkLayout.setVisibility(View.VISIBLE);
                    itemCountTv.setText(getString(R.string.left_no) + (mList.size() - 3) + getString(R.string.xiang));
                } else {
                    shrinkLayout.setVisibility(View.GONE);
                }
                if (response.getOrder().getIsCrowd() && mProgressDetail != null) {
                    mProgressDetail.setData(response.getCrowd());
                    mCrowdTimer.initTime(response.getCrowd(), true);
                }
                showOtherInfo(response);

                mButtomFragment.setCurrencyCode(response.getOrder().getCurrencycode());
                mButtomFragment.setShippingFee(response.getOrder().getGrandTotal());
                mButtomFragment.setBalance(response.getOrder().getGiftCardAmount());
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private String getPayMethod(String name) {
        String result = "";
        if (mOrder.getGiftCardAmount() > 0) {
            result += getString(R.string.pay_by_gift_card) + ":" + UIUtils.getCurrency(this, mOrder.getCurrencycode(), mOrder.getGiftCardAmount()) + "\n";
        }
        result += name;
        return result;
    }

    public void showDialog() {
        final CheckBox yueCb, weCb, aliCb;
        Button mButton;
        ImageView iv;
        //显示窗口
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_pay_method, null);
        yueCb = (CheckBox) contentView.findViewById(R.id.yueCb);
        weCb = (CheckBox) contentView.findViewById(R.id.weCb);
        aliCb = (CheckBox) contentView.findViewById(R.id.aliCb);
        aliCb.setChecked(false);
        weCb.setChecked(false);
        if (mSalesOrderBean.pay_method == CsBase.PayMethod.PAY_METHOD_ALIPAY_VALUE) {
            aliCb.setChecked(true);
        } else {
            weCb.setChecked(true);
        }
        mButton = (Button) contentView.findViewById(R.id.tv_login);
        iv = (ImageView) contentView.findViewById(R.id.cancelIv);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_login:
                        KLog.i("yueCb " + yueCb.isChecked() + " weCb " + weCb.isChecked() + " aliCb " + aliCb.isChecked());
                        if (mPopWindow != null) {
                            mPopWindow.dismiss();
                        }
                        if (weCb.isChecked()) {
                            mSalesOrderBean.pay_method = CsBase.PayMethod.PAY_METHOD_WXPAY_VALUE;

                        } else {
                            mSalesOrderBean.pay_method = CsBase.PayMethod.PAY_METHOD_ALIPAY_VALUE;
                        }
                        payMethodTv.setText(OrderConstants.PAY_METHOD[mSalesOrderBean.pay_method]);
                        break;
                    case R.id.cancelIv:
                        if (mPopWindow != null) {
                            mPopWindow.dismiss();
                        }
                        break;
                    case R.id.aliCb:
                        KLog.i("check alicB");
                        weCb.setChecked(!aliCb.isChecked());
                        break;
                    case R.id.weCb:
                        KLog.i("check yueCb");
                        aliCb.setChecked(!weCb.isChecked());
                        break;
                }
            }
        };
        aliCb.setOnClickListener(onClickListener);
        weCb.setOnClickListener(onClickListener);
        iv.setOnClickListener(onClickListener);
        mButton.setOnClickListener(onClickListener);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_order_detail_panding, null);
        mPopWindow.setFocusable(true);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.5f);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    public void cancelOrder() {
        if (mList != null) {
            for (SalesOrderItemBean salesOrderItemBean : mList) {
                salesOrderItemBean.state = 9;
            }
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (mListShort != null) {
            for (SalesOrderItemBean salesOrderItemBean : mListShort) {
                salesOrderItemBean.state = 9;
            }
        }
        if (mAdapterShort != null) {
            mAdapterShort.notifyDataSetChanged();
        }
    }

    //取消订单
    public void changeOrderState() {
        CsOrder.CancelSalesOrderRequest.Builder builder = CsOrder.CancelSalesOrderRequest.newBuilder();
        builder.setOrderId(mSalesOrderBean.order_id).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsOrder.CancelSalesOrderResponse>() {
            @Override
            public void onSuccess(CsOrder.CancelSalesOrderResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        KLog.i("取消成功");
                        centerTv.setText(getString(R.string.String_canceled));
                        editBtn.setVisibility(View.GONE);
                        bottomLayout.setVisibility(View.GONE);
                        changePayMethod.setClickable(false);
                        cancelOrder();
                    }
                });

            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast(errMsg);
                    }
                });
            }
        });
    }

    public void addCrowdView() {
        View mHeadView = View.inflate(this, R.layout.layout_order_crowd_detail, null);


        idTv = (TextView) mHeadView.findViewById(R.id.idTv);
        priceTv = (TextView) mHeadView.findViewById(R.id.priceTv);
        timeTv = (TextView) mHeadView.findViewById(R.id.timeTv);
        countTv = (TextView) mHeadView.findViewById(R.id.countTv);

        editBtn = (TextView) mHeadView.findViewById(R.id.editBtn);
        editBtn.setVisibility(View.VISIBLE);
        editBtn.setOnClickListener(onClickListener);
        mCrowdTimer = (CrowdTimer) mHeadView.findViewById(R.id.crowd_timer);
        mProgressDetail = (CrowdProgressDetail) mHeadView.findViewById(R.id.crowd_progress_detail);
        if (!mSalesOrderBean.is_crowd) {
            mCrowdTimer.setVisibility(View.GONE);
            mProgressDetail.setVisibility(View.GONE);
        }
        mListView.addHeaderView(mHeadView);
    }

    public void payOrder() {
        if (Constants.GIFT_CARD_PAYMENT_KRBANK.equals(mPayCode)) {
            //使用友利银行线下支付
//            Intent intent = new Intent();
//            intent.putExtra(KrBankInfoActivity.ORDER_ID, mSalesOrderBean.order_id + "");
//            intent.putExtra(KrBankInfoActivity.ORDER_COUNT, UIUtils.getCurrency(myActivity(), mSalesOrderBean.currencyCode, mSalesOrderBean.total_paid));
            Intent intent = new Intent(this, KrBankInfoActivity.class);
            intent.putExtra(KrBankInfoActivity.NEED_PAY_COUNT, mSalesOrderBean.total_paid);
            intent.putExtra(KrBankInfoActivity.SHIPPING_FEE_TOTAL, mSalesOrderBean.total_paid);
//            intent.putExtra(KrBankInfoActivity.BALANCE, mGiftcardaccount);
            intent.putExtra(KrBankInfoActivity.PAYMENT, getString(R.string.String_krbank_pay2));
            intent.putExtra(KrBankInfoActivity.PAY_CURRENCY_CODE, mSalesOrderBean.currencyCode);
            intent.setClass(OrderDetailPandingActivity.this, KrBankInfoActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        showLoading();
        if (Constants.GIFT_CARD_PAYMENT_ADYEN.equals(mPayCode)) {
            PaymentManager.getInstance(OrderDetailPandingActivity.this).adyenPay(OrderConstants.getPayMethod(OrderConstants.getPayType(mPayCode, -1)), mSalesOrderBean.order_number, mSalesOrderBean.order_id,
                    0, mSalesOrderBean.total_paid, mSalesOrderBean.currencyCode, new PaymentManager.OnPayListener() {
                        @Override
                        public void onPay(Response response, PayResultBaen payResult) {
                            //支付成功
                            try {
                                LogUtils.d(payResult.authCode);
                                if (!TextUtils.isEmpty(payResult.authCode)) {
                                    Intent intent = new Intent();
                                    intent.setClass(OrderDetailPandingActivity.this, PaymentSuccessActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            OrderDetailPandingActivity.this.finish();
                        }

                        @Override
                        public void onError(String errMsg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(OrderDetailPandingActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
//                                MyPayActivity.closeLoading();
                                }
                            });
                        }
                    });
            return;
        }
        if (Constants.GIFT_CARD_PAYMENT_DAOUPAY.equals(mPayCode)) {
            DaoUPayActivity.IntentBuilder intentBuilder = DaoUPayActivity.IntentBuilder.build(OrderDetailPandingActivity.this)
                    .setAmount((int) mSalesOrderBean.total_paid)
                    .setOrderID(mSalesOrderBean.order_id)
                    .setOrderType(0)
                    .setListener(new OperaRequestListener() {
                        @Override
                        public void onOperaSuccess() {
                            Intent intent = new Intent();
                            intent.setClass(OrderDetailPandingActivity.this, PaymentSuccessActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onOperaFailure() {
                            Toast.makeText(OrderDetailPandingActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
                        }
                    });
            startActivity(intentBuilder);
        }
        if (Constants.GIFT_CARD_PAYMENT_ALIPAY.equals(mPayCode)) {
            payOrderOther();
        }
        if (Constants.GIFT_CARD_PAYMENT_WXPAY.equals(mPayCode)) {
            payOrderOther();
        }
    }

    public void payOrderOther() {
        CsOrder.ApplyForSalesOrderPayRequest.Builder builder = CsOrder.ApplyForSalesOrderPayRequest.newBuilder();
        builder.setOrderId(mSalesOrderBean.order_id).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setPayMethod(OrderConstants.getPayType(mPayCode, payType)).setCurrencycode(mSalesOrderBean.currencyCode).setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsOrder.ApplyForSalesOrderPayResponse>() {
            @Override
            public void onSuccess(final CsOrder.ApplyForSalesOrderPayResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        SysApplication.mCurrentRequestPayment = mSalesOrderBean.order_number;
                        if (Constants.GIFT_CARD_PAYMENT_ALIPAY.equals(mPayCode)) {
                            PaymentManager.getInstance(OrderDetailPandingActivity.this).aliPay(response.getPayInfo(), new PaymentManager.OnAliPayListener() {
                                @Override
                                public void onSuccess(String resultStatus) {

                                }

                                @Override
                                public void onFailure(String resultStatus, String errorMsg) {

                                }
                            });
                        }
                        if (Constants.GIFT_CARD_PAYMENT_WXPAY.equals(mPayCode)) {
                            PaymentManager.getInstance(OrderDetailPandingActivity.this).wechatPay(response.getPayInfo());
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }


    public String getLeftTime(long now, long create) {
        KLog.i("now = " + (now / 1000) + " createTime= " + create);
        long left = create + 48 * 60 * 60 - now / 1000;

        if (left / 60 / 60 > 1) {
            return getString(R.string.left_pay_time) + (left / 60 / 60) + getString(R.string.left_pay_time_hour);
        } else {
            return getString(R.string.left_pay_time) + (left / 60 / 60 / 60) + getString(R.string.left_pay_time_minute);
        }
    }

    public void showOtherInfo(CsOrder.GetSalesOrderDetailResponse response) {
        float fee = getFee(response.getShippingsList());
//        String price = String.format(string, fee);
        String price = UIUtils.getCurrency(this, response.getOrder().getCurrencycode(), fee);
        if (response.getOrder().getShippingScheme() == 1) {
            otherLayout.setVisibility(View.VISIBLE);
            deliverTv.setText(getString(R.string.String_direct_mail_1) + UIUtils.getCurrency(myActivity(), response.getOrder().getCurrencycode(), fee));
        } else if (response.getOrder().getShippingScheme() == 2) {
            deliverTv.setText(getString(R.string.String_merge_order));
        }


        ShippingAdapter shippingAdapter = new ShippingAdapter(OrderDetailPandingActivity.this, response.getShippingsList(), response.getOrder().getCurrencycode());
        mShippingListView.setAdapter(shippingAdapter);
        setListViewHeightBasedOnChildren(mShippingListView);
        nameTv.setText(response.getAddress().getName() + "," + response.getAddress().getPhone());
        //String region = AssetFileManager.getInstance().readFilePlus(response.getAddress().getRegion(), AssetFileManager.ADDRESS_TYPE);
        String region = response.getAddress().getFullRegionName();
        String addressDetail = region + response.getAddress().getStreet();
        addressTv.setText(addressDetail);
        feeDetailTv.setText(getString(R.string.fee_detail_before) + price + getString(R.string.fee_detail_after));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == Constants.PAYMENT_REQUEST_CODE) {
                String paymentString = data.getStringExtra("paymentString");
                payMethodTv.setText(paymentString);
                payType = data.getIntExtra("payType", Constants.PAYMENT_ADYEN);
            }
        }
    }

    private float getFee(List<CsOrder.SalesOrderShipping> list) {
        float fee = 0;
        if (list == null) {
            return 0;
        }
        for (CsOrder.SalesOrderShipping shipping : list) {
            if (shipping != null) {
                fee = fee + shipping.getShippingFee();
            }
        }
        return fee;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeght()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
/*

    PAY_METHOD_NONE     = 0;
    PAY_METHOD_ALIPAY   = 1;//支付宝支付
    PAY_METHOD_WXPAY    = 2;//微信支付
    PAY_METHOD_GIFTCARD = 3;//使用礼品卡支付
    PAY_METHOD_ADYEN    = 4;//使用国际支付
    PAY_METHOD_KRBANK   = 5;//友利银行
    PAY_METHOD_DAOUPAY  = 6;//daoupay
*/

    @Override
    protected void onPause() {
        super.onPause();
        closeLoading();
    }

    public void setPayCode(String payCode) {
        mPayCode = payCode;
    }

    public String getPayCode() {
        return OrderConstants.getPayCode(payType);
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public void showPayMethod(String name) {
        payMethodTv.setText(getPayMethod(name));
    }

}
