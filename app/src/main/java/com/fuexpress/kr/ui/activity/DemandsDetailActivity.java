package com.fuexpress.kr.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.DemandDetailBean;
import com.fuexpress.kr.bean.PayResultBaen;
import com.fuexpress.kr.bean.PickUpItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.PayMethodManager;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.activity.help_signed.HelpMeSignedActivity;
import com.fuexpress.kr.ui.activity.help_signed.data.HelpSignedRemoteDataSource;
import com.fuexpress.kr.ui.activity.package_detail.PackageDetailActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.utils.ClassUtil;
import com.fuexpress.kr.utils.SPUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.socks.library.KLog;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import fksproto.CsCard;
import fksproto.CsParcel;
import fksproto.CsUser;
import okhttp3.Response;

/**
 * Created by kevin on 2016/12/19.
 */

public class DemandsDetailActivity extends BaseActivity {
    private static final int CODE_PAYMENT_REQUEST = 0x1001;
    public static final String CREATE_ANOTHER_ORDRE_ID = "create_order_id";
    public static final String DEMAND_BEAN = "demand_bean";
    public static final String TITLE_BACK = "title_back";

    public static final int GetItem = 1;//取货
    public static final int FindItem = 2;//找
    public static final int BuyItem = 3;//买
    public static final int Receive = 4;//收货


    @BindView(R.id.title_iv_left)
    ImageView titleIvLeft;
    @BindView(R.id.title_tv_left)
    TextView titleTvLeft;
    @BindView(R.id.title_tv_center)
    TextView titleTvCenter;
    @BindView(R.id.title_tv_right)
    TextView titleTvRight;
    @BindView(R.id.title_iv_right)
    ImageView titleIvRight;
    @BindView(R.id.tv_demand_no)
    TextView tvDemandNo;
    @BindView(R.id.tv_demand_time)
    TextView tvDemandTime;
    @BindView(R.id.tv_demand_type)
    TextView tvDemandType;
    @BindView(R.id.tv_demand_state)
    TextView tvDemandState;
    @BindView(R.id.tv_demand_service_fee)
    TextView tvDemandServiceFee;
    @BindView(R.id.icon_01)
    ImageView icon01;
    @BindView(R.id.icon_02)
    ImageView icon02;
    @BindView(R.id.icon_03)
    ImageView icon03;
    @BindView(R.id.icon_04)
    ImageView icon04;
    @BindView(R.id.icon_05)
    ImageView icon05;
    @BindView(R.id.tv_item_name)
    TextView tvItemName;
    @BindView(R.id.tv_item_price)
    TextView tvItemPrice;
    @BindView(R.id.tv_item_count)
    TextView tvItemCount;
    @BindView(R.id.tv_item_notice)
    TextView tvItemNotice;
    @BindView(R.id.pick_up_balance_tv)
    TextView pickUpBalanceTv;
    @BindView(R.id.pick_up_payment_tv)
    TextView pickUpPaymentTv;
    @BindView(R.id.pick_up_payment_layout)
    LinearLayout pickUpPaymentLayout;
    @BindView(R.id.pick_up_grand_total_tv)
    TextView pickUpGrandTotalTv;
    @BindView(R.id.pick_up_submit_btn)
    Button pickUpSubmitBtn;
    @BindView(R.id.submitLayout)
    RelativeLayout submitLayout;
    @BindView(R.id.address_1)
    TextView mAddress1;
    @BindView(R.id.address_2)
    TextView mAddress2;
    @BindView(R.id.address_layout)
    LinearLayout mAddressLayout;
    @BindView(R.id.payment_iv)
    ImageView mPaymentIv;
    @BindView(R.id.merchant_message_tv)
    TextView mMerchantMessageTv;
    @BindView(R.id.message_1_layout)
    RelativeLayout mMessage1Layout;
    @BindView(R.id.create_order_btn)
    Button mCreateOrderBtn;
    @BindView(R.id.warehouse_tv)
    TextView mWarehouseTv;
    @BindView(R.id.receive_address_tv)
    TextView receiveAddressTv;
    @BindView(R.id.receiver_name_tv)
    TextView receiverNameTv;
    @BindView(R.id.receiver_phone_tv)
    TextView receiverPhoneTv;
    @BindView(R.id.receive_postcode_tv)
    TextView receivePostcodeTv;
    @BindView(R.id.help_me_receive_layout)
    LinearLayout helpMeReceiveLayout;

    @BindView(R.id.parcel_name_tv)
    TextView mParcelNameTv;
    @BindView(R.id.to_parcel_btn)
    Button mToParcelBtn;
    @BindView(R.id.parcel_name_layout)
    RelativeLayout mParcelNameLayout;
    String payString;
    @BindView(R.id.tv_pre_order_time)
    TextView mTvPreOrderTime;
    private boolean clickable = true;
    private int mId;
    private int mType;
    private DemandDetailBean mBean;
    private ImageView[] mImageViews;
    //用来标记当前请求的需求
    CsUser.Require mRequire;
    //用来标记当前的余额
    private double mGiftBalance = 0.0;
    private String defaultPaymentString = "";
    private String defaultPayCode = "";
    private String mBackTitle = "";
    private final DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true).displayer(new FadeInBitmapDisplayer(100)).cacheInMemory(true
    ).cacheOnDisk(true).build();
    final ImageLoader loader = ImageLoader.getInstance();
    boolean isUseBalance = false;

    @Override
    public View setInitView() {
        View view = View.inflate(this, R.layout.activity_demands_detail, null);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        titleIvLeft.setVisibility(View.VISIBLE);
        titleTvLeft.setVisibility(View.VISIBLE);
        titleTvLeft.setText(getString(R.string.help_me));
        mImageViews = new ImageView[4];
        mImageViews[0] = icon01;
        mImageViews[1] = icon02;
        mImageViews[2] = icon03;
        mImageViews[3] = icon04;
        mRequire = (CsUser.Require) getIntent().getSerializableExtra(DEMAND_BEAN);
        mBackTitle = getIntent().getStringExtra(TITLE_BACK);
        if (!TextUtils.isEmpty(mBackTitle)) {
            titleTvLeft.setText(mBackTitle);
        }
        KLog.i(mRequire.toString());
        mId = mRequire.getSalesRequireId();
        if (mRequire != null) {
            mType = mRequire.getType();
        } else {
            return;
        }
        if (mId != 0 && mType != 0 && AccountManager.getInstance().getBaseUserRequest() != null) {
            switch (mType) {
                case BuyItem:
                    getBuyInfo();
                    break;
                case GetItem:
                    getGetInfo();
                    break;
                case Receive:
                    getReceiveInfo();
                    /*//如果是帮我收,则请求一下数据(以防其点击在下一单)
                    HelpSignedRemoteDataSource.getInstance().getHelpSignedDataRemote(mId);*/
                    break;
            }
        }
    }

    @OnClick({R.id.title_iv_left, R.id.title_tv_left, R.id.pick_up_payment_layout, R.id.pick_up_submit_btn,
            R.id.create_order_btn, R.id.title_tv_right, R.id.to_parcel_btn, R.id.parcel_name_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                onBackPressed();
                break;
            case R.id.title_tv_left:
                onBackPressed();
                break;
            case R.id.pick_up_payment_layout:
                //修改支付方式
                if (null != mBean && Constants.DemandStatus.STATUS_PENDING.equals(mBean.status)) {
                    changePayment();
                }
                break;
            case R.id.pick_up_submit_btn:
                gotoPay();
                break;
            case R.id.create_order_btn:
                toCreateOrder();
                break;
            case R.id.title_tv_right:
                showDialog();

                //  cancel_demand();
                break;
            case R.id.to_parcel_btn:
                if (mBean == null) {
                    return;
                }
                Intent intent = new Intent(DemandsDetailActivity.this, PackageDetailActivity.class);
                intent.putExtra(PackageDetailActivity.PARCEL_ID, Long.valueOf(mBean.parcelId));
                intent.putExtra(PackageDetailActivity.FROM_WHERE, Constants.getStatusString(mBean.status));
                this.startActivity(intent);
                break;
            case R.id.parcel_name_layout:
                if (mBean == null) {
                    return;
                }
                Intent intent1 = new Intent(DemandsDetailActivity.this, PackageDetailActivity.class);
                intent1.putExtra(PackageDetailActivity.PARCEL_ID, Long.valueOf(mBean.parcelId));
                intent1.putExtra(PackageDetailActivity.FROM_WHERE, Constants.getStatusString(mBean.status));
                this.startActivity(intent1);
                break;
        }
    }

    //显示确认取消的窗口
    public void showDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.are_you_sure_cancel_demands));
        if (mBean.status.equals(Constants.DemandStatus.STATUS_PENDING)) {
            builder.setMessage(getResources().getString(R.string.are_you_sure_cancel));
        }
        builder.setTitle(getString(R.string.edit_album_dialog_title_msg));
        builder.setPositiveButton(getString(R.string.pick_up_right_confrim), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancel_demand();
            }
        });
        builder.setNegativeButton(getString(R.string.down_to_up_string_cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        CustomDialog customDialog = builder.create();
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
    }

    //取消申请
    private void cancel_demand() {
        CsUser.CancelRequireRequest.Builder builder = CsUser.CancelRequireRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setSalesrequireid(mId);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.CancelRequireResponse>() {
            @Override
            public void onSuccess(CsUser.CancelRequireResponse response) {
                KLog.i(response.toString());
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        if (mBean.status.equals(Constants.DemandStatus.STATUS_PENDING)) {
                            EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_NEED_LIST, null));
                            DemandsDetailActivity.this.finish();
                        } else {
                            mViewUtils.toast(getString(R.string.you_cancel_require_have_commit));
                            mBean.status = Constants.DemandStatus.STATUS_AWAITINGCANCEL;
                            titleTvCenter.setText(getString(R.string.awaitingcancel));
                            tvDemandState.setText(getString(R.string.demand_state) + getString(R.string.awaitingcancel));
                            EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_NEED_LIST, null));
                        }


                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                KLog.i("Demands", errMsg + " ret = " + ret);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast(getString(R.string.string_for_send_requset_fail_02));
                    }
                });
            }
        });
    }

    private void toCreateOrder() {
        Intent intent = null;
        switch (mType) {
            case BuyItem:
                intent = new Intent(this, AddReplenishActivity.class);
                List<PickUpItemBean> itemList = (List<PickUpItemBean>) SPUtils.readObject(this, EditReplenishActivity.CODE_PICK_LIST);
                if (itemList != null && itemList.size() == 4) {
                    //满了4天，提示
                    mViewUtils.toast(getString(R.string.pick_up_add_item_max_msg));
                    return;
                }
                break;
            case GetItem:
                intent = new Intent(this, AddRequireActivity.class);
                break;
            case Receive:
                intent = new Intent(this, HelpMeSignedActivity.class);
                // TODO: 2017/1/4 设置跳转状态 -longer
                intent.putExtra(HelpMeSignedActivity.HMS_STATE_KEY, HelpMeSignedActivity.ADD_DEMAND_AGAIN);
                break;
        }
        if (null != intent) {
            intent.putExtra(CREATE_ANOTHER_ORDRE_ID, mId);
            this.startActivity(intent);
        }

    }

    private void changePayment() {
        if (mBean == null) {
            return;
        }
      /*  Intent intent = new Intent(this, PaymentActivity.class);
        int i = (int) SPUtils.get(this, AccountManager.getInstance().getCurrencyCode() + "paymentPos", 0);
        intent.putExtra("paymentPos", i);
        intent.putExtra(PaymentActivity.CURRENCY_CODE, mBean.currencycode);
        intent.putExtra("payType", Constants.getTyptByCode(mBean.paymentcode));
        intent.putExtra("payCode", mBean.paymentcode);
        intent.putExtra(PaymentActivity.IS_SEND_PACKAGE, false);
        intent.putExtra(PaymentActivity.IS_USE_BALANCE, isUseBalance);
        intent.putExtra("paymentString", payString);
        startActivityForResult(intent, CODE_PAYMENT_REQUEST);*/
        float payCount = 0;
        if (mType == BuyItem) {
            payCount = mBean.totalpay;
        } else if (mType == GetItem) {
            payCount = Float.valueOf(mBean.servicefee.replace(",", ""));
        }
        Intent intent = new Intent(this, ParcelPayMethodActivity.class);
        intent.putExtra(ParcelPayMethodActivity.CURRENCY_CODE, mBean.currencycode);
        intent.putExtra(ParcelPayMethodActivity.SHIPPING_FEE, payCount);
        PayMethodManager.getInstance(this).setUseBalance(isUseBalance);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        KLog.d("----requestCode " + requestCode);
      /*  if (resultCode == Constants.PAYMENT_REQUEST_CODE) {
            payString = SPUtils.get(DemandsDetailActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_NAME, "").toString();
            mBean.paymentname = payString;
            mBean.paymentcode = SPUtils.get(DemandsDetailActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_CODE, "").toString();
            KLog.i(" payString " + payString + " code = " + mBean.paymentcode);
            isUseBalance = data.getBooleanExtra(PaymentActivity.IS_USE_BALANCE, false);
            pickUpPaymentTv.setText(payString.replace("adyen", getString(R.string.String_adyen_pay)));
            setPayInfo();
        }*/
        super.onActivityResult(requestCode, resultCode, data);
        float payCount = 0;
        if (mType == BuyItem) {
            payCount = mBean.totalpay;
        } else if (mType == GetItem) {
            payCount = Float.valueOf(mBean.servicefee.replace(",", ""));
        }
        if (requestCode == 0 && resultCode == Constants.PAYMENT_REQUEST_CODE) {

            final PayMethodManager instance = PayMethodManager.getInstance(this);
            instance.getCurrentPayMethod(new PayMethodManager.PayMethodResultListener() {
                @Override
                public void onResult(String payCode, String result) {
//                    mView.showBalanceAndPayType(result);
//                    usedPayCode = payCode;
                    pickUpPaymentTv.setVisibility(View.VISIBLE);
                    pickUpPaymentTv.setText(result);
                    mBean.paymentname = result;
                    mBean.paymentcode = payCode;
                    isUseBalance = instance.isUseBalance();
                }

                @Override
                public void onMethodList(List<CsUser.PaymentList> paymantlistList, String balance, int couponCount) {

                }
            }, payCount, mBean.currencycode);
        }

    }

    //去付款
    private void gotoPay() {
        if (!clickable) {
            return;
        }
        clickable = false;
        if ((!isUseBalance & TextUtils.isEmpty(mBean.paymentcode)) || TextUtils.isEmpty(mBean.currencycode) || AccountManager.getInstance().mUin == 0 || mBean.salesrequireid == 0) {
            mViewUtils.toast(getString(R.string.data_is_worng));
            return;
        }

        if (Constants.GIFT_CARD_PAYMENT_KRBANK.equals(mBean.paymentcode)) {
            //如果为银行付款，则请求KR
            clickable = true;
            Intent intent = new Intent(this, KrBankInfoActivity.class);
            intent.putExtra(KrBankInfoActivity.NEED_PAY_COUNT, UIUtils.getCurrency(this, mBean.currencycode, Float.valueOf(mBean.totalpay + "")));
            intent.putExtra(KrBankInfoActivity.SHIPPING_FEE_TOTAL, mBean.totalpay);
            intent.putExtra(KrBankInfoActivity.NOTICE_SALES_ID, mId + "");
            intent.putExtra(KrBankInfoActivity.PAYMENT, pickUpPaymentTv.getText());
            intent.putExtra(KrBankInfoActivity.BALANCE, mBean.giftcardaccount);
            intent.putExtra(KrBankInfoActivity.PAY_CURRENCY_CODE, mBean.currencycode);
            intent.putExtra(KrBankInfoActivity.FROM_DEMANDS_LIST, KrBankInfoActivity.FROM_DEMAND_INT);
            this.startActivity(intent);
            clickable = true;
            return;
        }
        showLoading();
        CsCard.GetCheckToPayInApprequireRequest.Builder builder = CsCard.GetCheckToPayInApprequireRequest.newBuilder();
        builder.setPaymentcode(mBean.paymentcode);
        builder.setUin(AccountManager.getInstance().mUin);
        builder.setSalesrequireld(mBean.salesrequireid);
        builder.setCurrencycode(mBean.currencycode);
        builder.setUseaccountpay(isUseBalance ? 1 : 2);
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GetCheckToPayInApprequireResponse>() {
            @Override
            public void onSuccess(final CsCard.GetCheckToPayInApprequireResponse response) {
                KLog.i(response.toString());
                if (TextUtils.isEmpty(response.getRedirecturl())) {
                    //直接支付成功
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            closeLoading();
                            DemandsDetailActivity.this.startActivity(new Intent(DemandsDetailActivity.this, PayDemandSuccessActivity.class));
                            EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_NEED_LIST, null));
                            finish();
                        }
                    });
                } else {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            closeLoading();
                            payDirectCard(response.getRedirecturl());
                        }
                    });
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                KLog.i("ret = " + ret + " errMsg = " + errMsg);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        clickable = true;
                        mViewUtils.toast(getString(R.string.string_for_send_requset_fail));
                    }
                });
            }
        });
    }

    //生成直冲订单
    private void payDirectCard(String url) {
        try {
            Map<String, String> map = Constants.getPayInfoFromDirectUrl(url);
            CsParcel.DoDirectGiftCardRequest.Builder builder = CsParcel.DoDirectGiftCardRequest.newBuilder();
            builder.setType(Integer.valueOf(map.get(Constants.PayGift.TYPE)));
            builder.setAppType(Integer.valueOf(map.get(Constants.PayGift.APPTYPE)));
            builder.setPaymentCode(map.get(Constants.PayGift.PAYMENTCODE));
            builder.setTotal(Float.valueOf(map.get(Constants.PayGift.TOTAL)));
            //    builder.setParcelid(Integer.valueOf(map.get(Constants.PayGift.SALESREQIREID)));
            builder.setSalesrequireid(Integer.valueOf(map.get(Constants.PayGift.SALESREQIREID)));
            builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
            builder.setCurrencycode(mBean.currencycode);
            NetEngine.postRequest(builder, new INetEngineListener<CsParcel.DoDirectGiftCardResponse>() {
                @Override
                public void onSuccess(final CsParcel.DoDirectGiftCardResponse response) {
                    KLog.i(response.toString());
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_DAOUPAY, response.getPaymentCode()) || (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_ADYEN, response.getPaymentCode()))) {
                                payWithAdyenOrDaouPay(response.getNumber(), response.getOrderId());
                            } else {
                                payOrder(response.getPaymentCode(), response.getOrderId());
                            }
                        }
                    });
                }

                @Override
                public void onFailure(int ret, String errMsg) {
                    KLog.i("ret = " + ret + " errMsg = " + errMsg);
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            clickable = true;
                            mViewUtils.toast(getString(R.string.string_for_send_requset_fail));
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void payOrder(String paymentCode, final int id) {
        if (!("alipay".equals(mBean.paymentcode) | "wxap".equals(mBean.paymentcode))) return;
        showLoading();
        CsParcel.PayGiftCardOrderRequest.Builder builder = CsParcel.PayGiftCardOrderRequest.newBuilder();
        builder.setCurrencycode(mBean.currencycode);
        builder.setPaymentCode(paymentCode);
        builder.setGiftCardOrderId(id);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayGiftCardOrderResponse>() {
            @Override
            public void onSuccess(final CsParcel.PayGiftCardOrderResponse response) {
                KLog.i(response.toString());
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();

                        if (Constants.GIFT_CARD_PAYMENT_ALIPAY.equals(mBean.paymentcode)) {
                            //阿里支付
                            PaymentManager.getInstance(DemandsDetailActivity.this).aliPay(response.getPayInfo(), new PaymentManager.OnAliPayListener() {
                                @Override
                                public void onSuccess(String resultStatus) {
                                    UIUtils.postTaskSafely(new Runnable() {
                                        @Override
                                        public void run() {
                                            mViewUtils.toast(UIUtils.getString(R.string.card_payment_success_title_bar_title));
                                            EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_NEED_LIST, null));
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(String resultStatus, String errorMsg) {
                                    KLog.i(errorMsg + "+errMsg");
                                    UIUtils.postTaskSafely(new Runnable() {
                                        @Override
                                        public void run() {
                                            mViewUtils.toast(getString(R.string.cancel_fail));
                                            clickable = true;
                                        }
                                    });
                                }
                            });

                        }
                        if (Constants.GIFT_CARD_PAYMENT_WXPAY.equals(mBean.paymentcode)) {
                            //微信支付
                            SysApplication.mCurrentRequestPayment = DemandsDetailActivity.class.getSimpleName();
                            PaymentManager.getInstance(DemandsDetailActivity.this).wechatPay(response.getPayInfo());
                        }

                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                KLog.i("ret = " + ret + " errMsg = " + errMsg);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        mViewUtils.toast(getString(R.string.string_for_send_requset_fail));
                        clickable = true;
                    }
                });
            }
        });
    }


    public void payWithAdyenOrDaouPay(String orderNumber, int giftCardOrderId) {
        showLoading();
        if (Constants.GIFT_CARD_PAYMENT_ADYEN.equals(mBean.paymentcode)) {
            //adyen支付
            PaymentManager.getInstance(DemandsDetailActivity.this).adyenPay(payString, orderNumber, giftCardOrderId, 1, Float.valueOf(mBean.needPay + "")
                    , mBean.currencycode, new PaymentManager.OnPayListener() {
                        @Override
                        public void onPay(Response response, PayResultBaen payResult) {
                            UIUtils.postTaskSafely(new Runnable() {
                                @Override
                                public void run() {
                                    closeLoading();
                                    mViewUtils.toast(UIUtils.getString(R.string.card_payment_success_title_bar_title));
                                    EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_NEED_LIST, null));
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onError(String errMsg) {
                            KLog.i(errMsg + "+errMsg");
                            UIUtils.postTaskSafely(new Runnable() {
                                @Override
                                public void run() {
                                    closeLoading();
                                    mViewUtils.toast(getString(R.string.cancel_fail));
                                    clickable = true;
                                }
                            });
                        }
                    });
        }
        if (Constants.GIFT_CARD_PAYMENT_DAOUPAY.equals(mBean.paymentcode)) {
            DaoUPayActivity.IntentBuilder intentBuilder = DaoUPayActivity.IntentBuilder.build(DemandsDetailActivity.this)
                    .setAmount((new Double(mBean.needPay)).intValue())
                    .setOrderID(giftCardOrderId)
                    .setOrderType(1)
                    .setListener(new OperaRequestListener() {
                        @Override
                        public void onOperaSuccess() {

                            mViewUtils.toast(UIUtils.getString(R.string.card_payment_success_title_bar_title));
                            EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_NEED_LIST, null));
                            finish();

                        }

                        @Override
                        public void onOperaFailure() {
                            mViewUtils.toast(getString(R.string.cancel_fail));
                            clickable = true;
                        }
                    });
            startActivity(intentBuilder);
        }
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        if (event.getType() == BusEvent.PAY_MENT_RESULT && event.getStrParam().equals(ReplenishActivity.class.getSimpleName())) {
            SysApplication.mCurrentRequestPayment = "";
            if (event.getBooleanParam()) {
                // toSuccessPage();
                EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_NEED_LIST, null));
                finish();
            } else {
                clickable = true;
            }
        }
    }


    public void toSuccessPage() {
        Intent intent = new Intent(DemandsDetailActivity.this, PickUpSuccessActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setData() {
        pickUpBalanceTv.setVisibility(View.GONE);
        payString = mBean.paymentname;
       /* String payCode = SPUtils.get(DemandsDetailActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_CODE, "").toString();
        if (Constants.DemandStatus.STATUS_PENDING.equals(mBean.status)) {
            //  mBean.paymentcode=payCode;
        }
        if (TextUtils.isEmpty(mBean.paymentcode) && Constants.DemandStatus.STATUS_PENDING.equals(mBean.status)) {
            PaymentManager.getInstance(this).getFKDPaymentListRequest(mBean.currencycode, new PaymentManager.OnFKDPaymentListener() {
                @Override
                public void onResult(int paymentType, final String pString, final String pCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            KLog.i("code = " + pCode + " string = " + pString);
                            mBean.paymentcode = pCode;
                            pickUpPaymentTv.setText(pString.replace("adyen", UIUtils.getString(R.string.String_adyen_pay)));
//                            setPayInfo();
                        }
                    });
                }
            });
        }*/
        if (mType == Receive) {
            //
            // helpMeReceiveLayout.setVisibility(View.VISIBLE);
            receiveAddressTv.setText(receiveAddressTv.getText() + "：" + mBean.fulladdress);
            receiverNameTv.setText(receiverNameTv.getText() + mBean.receiver);
            receiverPhoneTv.setText(receiverPhoneTv.getText() + mBean.phone);
            receivePostcodeTv.setText(receivePostcodeTv.getText() + mBean.postcode);
        }
        if (null != mBean) {
            tvDemandNo.setText(getString(R.string.demand_no) + mBean.salesrequireid);
            tvDemandNo.getPaint().setFakeBoldText(false);
            tvDemandTime.setText(getString(R.string.demand_time) + mBean.createtime);
            tvDemandTime.getPaint().setFakeBoldText(false);
            if (0 <= mBean.requiretype && mBean.requiretype <= 4) {
                tvDemandType.setText(getString(R.string.demand_type) + Constants.getType(DemandsDetailActivity.this, mBean.requiretype));
                tvDemandType.getPaint().setFakeBoldText(false);
            }

            tvDemandState.setText(getString(R.string.demand_state) + Constants.getStatusString(mBean.status));
            tvDemandState.getPaint().setFakeBoldText(false);
            if (mType != Receive) {
                tvDemandServiceFee.setText(getString(R.string.service_fee) + UIUtils.getCurrency(this, mBean.currencycode, Float.valueOf(mBean.servicefee.replace(",", ""))));
                tvDemandServiceFee.getPaint().setFakeBoldText(false);
            } else {
                tvDemandServiceFee.setVisibility(View.GONE);
            }

            tvItemName.setText(getString(R.string.item_name) + "：" + mBean.description);
            tvItemPrice.setText(getString(R.string.pick_up_commodity_price) + "：" + UIUtils.getCurrency(this, mBean.pricecurrencycode, Float.valueOf(mBean.price.replace(",", ""))));
            tvItemCount.setText(getString(R.string.pick_up_commodity_count) + "：" + mBean.qty);
            tvItemNotice.setText(getString(R.string.hms_remarks_text) + "：" + mBean.remark);
            if (TextUtils.isEmpty(mBean.paymentcode)) {
                pickUpPaymentTv.setText(getString(R.string.pay_by_gift_card));
            } else {
                pickUpPaymentTv.setText(mBean.paymentname);
            }


            if (mBean.imagesurl != null && mBean.imagesurl.size() > 0) {
                for (int i = 0; i < 4; i++) {
                    if (i >= mBean.imagesurl.size()) {
                        break;
                    }
                    loader.displayImage(mBean.imagesurl.get(i).getImage(), mImageViews[i], options);
                }
            }
            titleTvCenter.setText("" + Constants.getStatusString(mBean.status));
            if (Constants.DemandStatus.STATUS_SHIPPED.equals(mBean.status)) {
                //已发货就显示地址
                mAddressLayout.setVisibility(View.VISIBLE);
                mAddress1.setText(mBean.shippingname + "," + mBean.shippingphone);
                mAddress2.setText(mBean.shippingaddress);
            } else {
                mAddressLayout.setVisibility(View.GONE);
            }

            if (Constants.DemandStatus.STATUS_PENDING.equals(mBean.status)) {
                //如果为待付款状态则支付提交按钮可见
                submitLayout.setVisibility(View.VISIBLE);
                pickUpGrandTotalTv.setText(UIUtils.getCurrency(this, mBean.currencycode, mBean.totalpay));
                mPaymentIv.setVisibility(View.VISIBLE);
                pickUpBalanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(this, mBean.currencycode, Float.parseFloat(mBean.giftcardaccount + "")));

            } else {
                //隐藏显示与余额位置
                pickUpBalanceTv.setVisibility(View.GONE);
                //隐藏选择支付方式按钮
                mPaymentIv.setVisibility(View.GONE);
                submitLayout.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(mBean.merchantmessage)) {
                mMessage1Layout.setVisibility(View.GONE);
            } else {
                mMessage1Layout.setVisibility(View.VISIBLE);
                mMerchantMessageTv.setText(mBean.merchantmessage + "");
            }
            if (mType == Receive) {
                //帮我收货特殊处理
                tvDemandServiceFee.setVisibility(View.GONE);
            }
            if (Constants.DemandStatus.STATUS_PENDING.equals(mBean.status)) {
                //如果待付款，则不显示“再下一单”
                mCreateOrderBtn.setVisibility(View.GONE);
            } else {
                mCreateOrderBtn.setVisibility(View.VISIBLE);
            }
            if (mBean.status.equals(Constants.DemandStatus.STATUS_ALLOCATING)/*||mBean.status.equals(Constants.DemandStatus.STATUS_PENDING)*/) {
                titleTvRight.setVisibility(View.VISIBLE);
                titleTvRight.setText(getString(R.string.cancel_apply));
            }
            if (mBean.status.equals(Constants.DemandStatus.STATUS_PENDING)) {
                titleTvRight.setVisibility(View.VISIBLE);
                titleTvRight.setText(getString(R.string.cancel));
            }
            switch (mType) {
                case GetItem:
                    //帮我取货
                    tvItemName.setText(getString(R.string.String_parcel_item_info) + "：" + mBean.description);
                    tvItemPrice.setText(getString(R.string.item_price) + "：" + UIUtils.getCurrency(this, mBean.pricecurrencycode, Float.valueOf(mBean.price.replace(",", ""))));
                    tvItemCount.setText(getString(R.string.String_item_count) + "：" + mBean.qty);
                    tvItemNotice.setVisibility(View.GONE);
                    mWarehouseTv.setVisibility(View.VISIBLE);
                    mWarehouseTv.setText(getString(R.string.pick_up_commodity_address) + "：" + mBean.address);
                    //   pickUpGrandTotalTv.setText(UIUtils.getCurrency(this,mBean.currencycode)+Float.valueOf(mBean.totalpay)+"……………………………………");
                    if (TextUtils.isEmpty(mBean.paymentcode)) {
                        mBean.paymentcode = "balance";
                    }
                    pickUpPaymentTv.setText(mBean.paymentname);
                    showPayMoney(mBean.currencycode, Float.valueOf(mBean.giftcardaccount + ""), Float.valueOf(mBean.servicefee.replace(",", "")));
                    break;
                case BuyItem:
                    //帮我买货
                    tvItemName.setText(getString(R.string.String_parcel_item_info) + "：" + mBean.description);
                    tvItemPrice.setText(getString(R.string.item_price) + "：" + UIUtils.getCurrency(this, mBean.pricecurrencycode, Float.valueOf(mBean.price.replace(",", ""))));
                    tvItemCount.setText(getString(R.string.String_item_count) + "：" + mBean.qty);
                    tvItemNotice.setText(getString(R.string.hms_remarks_text) + "：" + mBean.remark);
                    pickUpPaymentTv.setText(mBean.paymentname);
                    pickUpPaymentTv.setVisibility(View.VISIBLE);
                    showPayMoney(mBean.currencycode, Float.valueOf(mBean.giftcardaccount + ""), mBean.totalpay);
                    if (Constants.DemandStatus.STATUS_PREORDER.equals(mBean.status)) {
                        //如果当前为帮我买并且处于入库中，这添加入库时间提示
                        mTvPreOrderTime.setVisibility(View.VISIBLE);
                        mTvPreOrderTime.setText(getString(R.string.pre_order_time) + " " + mBean.pickUpTime);
                    }
                    break;
                case Receive:
                    //帮我收货
                    pickUpPaymentLayout.setVisibility(View.GONE);
                    tvItemName.setText(getString(R.string.String_parcel_item_info) + "：" + mBean.description);
                    tvItemPrice.setText(getString(R.string.report_price) + "：" + UIUtils.getCurrency(this, mBean.pricecurrencycode, Float.valueOf(mBean.price.replace(",", ""))));
                    tvItemCount.setText(getString(R.string.String_item_count) + "：" + mBean.qty);
                    tvItemNotice.setText(getString(R.string.hms_remarks_text) + "：" + mBean.remark);
                    mWarehouseTv.setVisibility(View.VISIBLE);
                    mWarehouseTv.setText(getString(R.string.hms_warehouse_text) + "：" + mBean.warehousename);
                    tvDemandServiceFee.setVisibility(View.GONE);
                    break;
            }

            //如果需求单为已发货或者为已入库，这显示包裹名称
            if (Constants.DemandStatus.STATUS_PACKING.equals(mBean.status) || Constants.DemandStatus.STATUS_SHIPPED.equals(mBean.status)) {
                mParcelNameLayout.setVisibility(View.VISIBLE);
                mParcelNameTv.setText(getString(R.string.String_parcle_name) + mBean.parcelName);
                if (Constants.DemandStatus.STATUS_PACKING.equals(mBean.status)) {
                    mToParcelBtn.setVisibility(View.VISIBLE);
                } else {
                    mToParcelBtn.setVisibility(View.GONE);
                }
            }
        }

    }

    private void setPayInfo() {
        switch (mType) {
            case GetItem:
                showPayMoney(mBean.currencycode, Float.valueOf(mBean.giftcardaccount + ""), Float.valueOf(mBean.servicefee.replace(",", "")));
                break;
            case Receive:
                break;
            case BuyItem:
                showPayMoney(mBean.currencycode, Float.valueOf(mBean.giftcardaccount + ""), mBean.totalpay);
                break;
        }
    }

    //根据货币符号，需要支付的总额，余额显示
    private void showPayMoney(String code, float balance, float total) {
        isUseBalance = (balance > 0 & balance < total & "balance".equals(mBean.paymentcode));
        if (!Constants.DemandStatus.STATUS_PENDING.equals(mBean.status)) {
            pickUpBalanceTv.setVisibility(View.GONE);
            //    pickUpPaymentTv.setText(mBean.paymentname);
            return;
        }
        if (!isUseBalance) {
            pickUpPaymentTv.setText(getPayText(code, total - 0));
            pickUpPaymentTv.setVisibility(View.VISIBLE);
            pickUpBalanceTv.setVisibility(View.GONE);
            mBean.needPay = total;
            return;
        }
        if (balance == 0) {
            //隐藏余额
            pickUpBalanceTv.setVisibility(View.GONE);
            pickUpPaymentTv.setText(getPayText(code, total));
            mBean.needPay = Math.ceil(Double.valueOf(total + ""));
        } else {
            //需要充值的金额
            double needPay = Math.ceil(Double.valueOf((total - balance) + ""));
            mBean.needPay = needPay < 0 ? 0 : needPay;
            String text = getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(this, code, balance);
            if (needPay > 0) {
                text += "\n" + getPayText(code, total - balance);
            }
            pickUpPaymentTv.setText(text);
            pickUpPaymentTv.setVisibility(View.VISIBLE);
        }
    }

    //根据需要支付的金额，修改显示字符串
    private String getPayText(String payCode, double money) {
        String str = mBean.paymentname + " " + UIUtils.getCurrency(this, mBean.currencycode, (float) money);
        /*String template = "还需使用支付方式充值：";
        return template.replace("支付方式", Constants.getPaymentString(mBean.paymentcode)) + UIUtils.getCurrency(this, mBean.currencycode, (float) money);*/
        return str;

    }

    private void getBuyInfo() {
        CsUser.helpMyBuyInfoRequest.Builder builder = CsUser.helpMyBuyInfoRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setSalesrequireid(mId);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.helpMyBuyInfoResponse>() {
            @Override
            public void onSuccess(CsUser.helpMyBuyInfoResponse response) {
                KLog.i("Demand", response.toString());
                mBean = ClassUtil.conventhelpMyBuyInfoResponse2DemandDetailBean(response);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        setData();
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                KLog.i("Demands", errMsg + " ret = " + ret);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast(myActivity().getString(R.string.get_message_failed));
                    }
                });
            }
        });
    }

    private void getGetInfo() {
        CsUser.HelpMyGetInfoRequest.Builder builder = CsUser.HelpMyGetInfoRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setSalesrequireid(mId);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.HelpMyGetInfoResponse>() {
            @Override
            public void onSuccess(CsUser.HelpMyGetInfoResponse response) {
                KLog.i("Demand", response.toString());
                mBean = ClassUtil.conventHelpMyGetInfoResponse2DemandDetailBean(response);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        setData();
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                KLog.i("Demands", errMsg + " ret = " + ret);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast(myActivity().getString(R.string.get_message_failed));
                    }
                });
            }
        });
    }

    private void getReceiveInfo() {
        CsUser.helpMyReceiveInfoRequest.Builder builder = CsUser.helpMyReceiveInfoRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setSalesrequireid(mId);
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.helpMyReceiveInfoResponse>() {
            @Override
            public void onSuccess(CsUser.helpMyReceiveInfoResponse response) {
                KLog.i("Demand", response.toString());
                mBean = ClassUtil.conventhelpMyReceiveInfoResponse2DemandDetailBean(response);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        setData();
                    }
                });
                //把请求回来的数据添加到帮我收中,以防其点击再下一单,免得再次请求: add by longer
                HelpSignedRemoteDataSource.getInstance().processResponseData(response);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                KLog.i("Demands", errMsg + " ret = " + ret);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast(myActivity().getString(R.string.get_message_failed));
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeLoading();
    }
}
