package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.PayResultBaen;
import com.fuexpress.kr.bean.ReParcelItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.PayMethodManager;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.activity.package_detail.PackageDetailActivity;
import com.fuexpress.kr.ui.adapter.ParcelSplitItemsApadter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.utils.FloatUtils;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.ReParcelItemTransverter;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsParcel;
import fksproto.CsUser;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import okhttp3.Response;


public class ParcelSplitActivity extends BaseActivity {

    public static String PARCEL_IREM = "parcel_irem";

    private int pageIndex = 1;
    private CsParcel.Parcel mParcle;
    private List<ReParcelItemBean> mItemLists;

    private static Handler mHandler = new Handler();
    private float mPrice;
    private float declarePrice;
    private TextView mTvCount;
    private TextView mTvPriceRealy;
    private TextView mTvDeclarePrice;
    private TextView mTvBalance;
    private TextView mTvSplit;
    private TextView mTvSplitFee;
    private boolean mHasMore = true;
    private PtrClassicFrameLayout ptrFrame;
    private View mRootView;
    private ListView mListView;
    private int mListViewHeight;
    private boolean mIsScrollButtom;
    private boolean mIsScrollTop;
    private float mRawY;
    private float mRawX;
    private ParcelSplitItemsApadter mAdapter;
    private int payType = -1;
    private TextView mTvBalanceSecond;
    private boolean mIsUseBalance = true;
    private float mGiftcardaBanlance;
    private String mSplitfee;
    private boolean mIsSplit;
    private float fSplitFee;
    private int mPaymentPos;
    private String mPayMethodName;
    private int paymentPos;
    private String mPayCode;
    private float mNeedMoney;
    private float mFee;
    private boolean mKeyBoardOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int heigh = getWindowManager().getDefaultDisplay().getHeight() / 3;
        mRootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //弹起事件
                if (bottom != 0 && oldBottom != 0 && oldBottom - bottom > heigh) {
                    mKeyBoardOpen = true;
//                    Toast.makeText(ParcelSplitActivity.this, "监听到软件盘开...", Toast.LENGTH_SHORT).show();
                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > heigh)) {
//                    Toast.makeText(ParcelSplitActivity.this, "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();
                    mKeyBoardOpen = false;
                }
            }
        });
    }

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_parcle_split, null);
        View headerView = View.inflate(this, R.layout.view_split_parcle_header, null);
        headerView.findViewById(R.id.ll_pay_type).setOnClickListener(this);

        ptrFrame = (PtrClassicFrameLayout) mRootView.findViewById(R.id.mptr_framelayout);
//        ptrFrame.setMode(PtrFrameLayout.Mode.BOTH);
        mTvBalanceSecond = (TextView) headerView.findViewById(R.id.tv_balance_second);

        mTvCount = (TextView) headerView.findViewById(R.id.tv_pavkage_item_count);
        mTvPriceRealy = (TextView) headerView.findViewById(R.id.tv_pavkage_real_price);
        mTvDeclarePrice = (TextView) headerView.findViewById(R.id.tv_pavkage_apply_price);
        mTvBalance = (TextView) headerView.findViewById(R.id.tv_balance_first);
        mTvSplitFee = (TextView) mRootView.findViewById(R.id.tv_split_fee);
        mTvSplit = (TextView) mRootView.findViewById(R.id.tv_split);
        mTvSplit.setOnClickListener(this);

        mListView = (ListView) mRootView.findViewById(R.id.refresh_lv_body);
        mListView.addHeaderView(headerView);
        initTitel();
        initRefreshMore();
        initData();
        return mRootView;
    }

    private void initTitel() {
        View back = mRootView.findViewById(R.id.title_iv_left);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        TextView backName = (TextView) mRootView.findViewById(R.id.title_tv_left);
        backName.setOnClickListener(this);
        backName.setVisibility(View.VISIBLE);
        backName.setText(R.string.string_package_detail_back);
        TextView title = (TextView) mRootView.findViewById(R.id.title_tv_center);
        title.setText(getString(R.string.package_split_parcel));
    }


    private void initRefreshMore() {
        ptrFrame.setDurationToCloseFooter(400);
        ptrFrame.setPtrHandler(new PtrDefaultHandler2() {

            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                boolean view_more = super.checkCanDoLoadMore(frame, content, footer);
                return view_more && mHasMore;
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                initRePacel();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageIndex = 1;
                initRePacel();
            }
        });
    }

    private void initData() {
        mParcle = (CsParcel.Parcel) getIntent().getBundleExtra(PARCEL_IREM).getSerializable(PARCEL_IREM);
        mItemLists = new ArrayList<>();
        mTvBalanceSecond.setText(getString(R.string.card_order_payment_title_bar_title));
        PayMethodManager.getInstance(this).reSet();
        initRePacel();
    }

    public CsParcel.InitReparcelResponse mResponse;

    private void initRePacel() {
        CsParcel.InitReparcelRequest.Builder builder = CsParcel.InitReparcelRequest.newBuilder();
        builder.setParcelId((int) mParcle.getParcelId())
                .setPageNum(pageIndex)
                .setUserinfo(AccountManager.getInstance().getBaseUserRequest())
                .setCurrencyCode(mParcle.getCurrencycode())
                .setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.InitReparcelResponse>() {
            @Override
            public void onSuccess(final CsParcel.InitReparcelResponse response) {
                mResponse = response;
                initPayType2();
                String status = response.getStatus();
                if (pageIndex == 1) {
                    mItemLists.clear();
                    mItemLists.addAll(ReParcelItemTransverter.transform(response.getReparcelItemInfoList()));
                } else {
                    mItemLists.addAll(ReParcelItemTransverter.transform(response.getReparcelItemInfoList()));
                }

                if (!"nomore".equals(status)) {
                    mHasMore = true;
                } else {
                    mHasMore = false;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (mAdapter == null) {
                            mAdapter = new ParcelSplitItemsApadter(ParcelSplitActivity.this, mItemLists, mParcle);
                            mListView.setAdapter(mAdapter);
                        }
                        showSelectItems();
                        ptrFrame.refreshComplete();
                        if (pageIndex == 1) {
                            mAdapter.notifyDataSetChanged();
                            showOther(response);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mHasMore) pageIndex++;
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.makeText(ParcelSplitActivity.this, "fail:" + errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initPayType2() {
        mFee = 0;
        String splitFee = mResponse.getSplitFee();
        if (!TextUtils.isEmpty(splitFee)) {
            mFee = Float.parseFloat(splitFee);
        }

        PayMethodManager.getInstance(this).getCurrentPayMethod(new PayMethodManager.PayMethodResultListener() {
            @Override
            public void onResult(String payCode, final String result) {
                mPayCode = payCode;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvBalance.setVisibility(View.VISIBLE);
                        mTvBalance.setText(result);
                    }
                });
            }

            @Override
            public void onMethodList(List<CsUser.PaymentList> paymantlistList, String balance, int couponCount) {

            }
        }, mFee, mParcle.getCurrencycode());
    }

    private void showOther(CsParcel.InitReparcelResponse response) {
        mGiftcardaBanlance = response.getGiftCardAccount();
        mSplitfee = response.getSplitFee();
        if (!TextUtils.isEmpty(mSplitfee)) {
            fSplitFee = Float.parseFloat(mSplitfee);
            String sPrice = UIUtils.getCurrency(this, mParcle.getCurrencycode(),  FloatUtils.vlaueOf(mSplitfee));
            mTvSplitFee.setText(sPrice);
        }
    }


    /*required string  salesorderitemid                = 1; //单品id
    required float   price                           = 2; //单品价格
    optional int32   qtypack                         = 3; //打包数量
    optional string  imagepath                       = 4; //图片路径
    optional string  productentityname               = 5; //产品名称
    optional string  captioncutprice                 = 6; //单品描述
    optional string  buyfrom                         = 7; //在哪买
    optional string  productextendattributenames     = 8; //产品变种属性
    optional string  message                         = 9; //订单单品留言
    optional float   declaredvalue                   = 10;//item申报总价
    optional int64   salesorderid                    = 11;//订单id
    optional string  number                          = 12;//订单序号
    optional int32   ischecked                       = 13;//是否点击
}*/
    int count;

    public void showSelectItems() {
        showSelectItems(false);
    }

    public void showSelectItems(boolean refreshList) {
        count = 0;
        mPrice = 0;
        declarePrice = 0;
        if (mItemLists != null) {
            for (int i = 0; i < mItemLists.size(); i++) {
                ReParcelItemBean list = mItemLists.get(i);
                if (list.isSelect()) {
                    Integer numValue = mAdapter.getDeclareNum().get(i);
                    int num = numValue != null ? numValue : list.getQtyPack();
                    count += num;
                    mPrice += list.getPrice() * num;
                    Object obj = mAdapter.getDeclarePrices().get(i);
                    if (obj != null) {
                        declarePrice += (float) obj;
                    } else {
                        declarePrice += list.getDeclaredValue();
                    }

                }
            }
        }
        if (count > 0) {
            mTvSplit.setEnabled(true);
        } else {
            mTvSplit.setEnabled(false);
        }
        mTvCount.setText(getString(R.string.package_item_count, count));
        String string = getString(R.string.package_price_realy, UIUtils.getCurrency(this, mParcle.getCurrencycode(), mPrice));
        mTvPriceRealy.setText(string);
        String string1 = getString(R.string.package_declare_price, UIUtils.getCurrency(this, mParcle.getCurrencycode(), declarePrice));
        mTvDeclarePrice.setText(string1);
        if (refreshList) mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.title_tv_left:
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.ll_pay_type:
                intent = new Intent(this, ParcelPayMethodActivity.class);
                intent.putExtra(ParcelPayMethodActivity.CURRENCY_CODE, mParcle.getCurrencycode());
                intent.putExtra(ParcelPayMethodActivity.SHIPPING_FEE, mFee);
                intent.putExtra(ParcelPayMethodActivity.USE_COUPON, false);
                startActivityForResult(intent, 0);
                break;

            case R.id.tv_split:
                if (mAdapter.checkDeclareNum(null, null) && checkHasFocuse()) splitParcle();
                break;
        }
    }

    private void splitParcle() {
        showLoading();
        mNeedMoney = getPayAmount();
        if (!PayMethodManager.getInstance(this).isUseBalance()) {
            splitUseMoney(mNeedMoney);
            return;
        }
        splitUseBanlance();
    }


    private float getPayAmount() {
      /*  if (mIsUseBalance) {
            float v = Float.valueOf(mSplitfee) - mGiftcardaBanlance;
            return v > 0 ? v : 0;
        } else {
            return Float.valueOf(mSplitfee);
        }*/
        return PayMethodManager.getInstance(this).getNeedPay();
    }

    /*  payment_code        = 4;  //支付方式(支付宝:alipay 微信:wxap)
      optional float           total               = 5;  //充值金额
      optional int32           parcel_id           = 6;  //包裹id
      repeated declarationDic  declaration_list    = 7;  //{declared_id,declared_value}列表*/
    private void splitUseMoney(final float needMoney) {
        if (TextUtils.isEmpty(mPayCode)) {
            Toast.makeText(this, getString(R.string.please_select_payment_method), Toast.LENGTH_SHORT).show();
            closeLoading();
            return;
        }

        CsParcel.ReparcelDoDirectGiftCardRequest.Builder builder = CsParcel.ReparcelDoDirectGiftCardRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest())
                .setAppType(3).setType(3)
                .setPaymentCode(mPayCode)
                .setTotal(needMoney)
                .setParcelId((int) mParcle.getParcelId())
                .setSplitFee(fSplitFee)
                .setCurrencycode(mParcle.getCurrencycode())
                .setLocalecode(AccountManager.getInstance().getLocaleCode())
        ;
        ArrayMap declarePrices = mAdapter.getDeclarePrices();
        for (int i = 0; i < mItemLists.size(); i++) {
            ReParcelItemBean itemBean = mItemLists.get(i);
            if (itemBean.isSelect()) {
                float declare = 0;
                int qty_value = mAdapter.getDeclareNum().get(i) == null ? itemBean.getQtyPack() : mAdapter.getDeclareNum().get(i);
                qty_value = qty_value == 0 ? itemBean.getQtyPack() : qty_value;
                if (declarePrices.get(i) == null) {
                    declare = itemBean.getDeclaredValue();
                } else {
                    declare = (float) declarePrices.get(i);
                }
                builder.addDeclarationList(CsBase.PairStrFloat.newBuilder().setK(itemBean.getParcelItemId()).setV(declare));
                builder.addQty(CsBase.PairStrStr.newBuilder().setK(itemBean.getParcelItemId()).setV(qty_value + ""));
            }
        }

        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.ReparcelDoDirectGiftCardResponse>() {

            @Override
            public void onSuccess(CsParcel.ReparcelDoDirectGiftCardResponse response) {
                // TODO: 2016-8-4  返回货币符号 传给下一步
                SysApplication.mCurrentRequestPayment = mParcle.getParcelName();
                getPayInfo(response.getNumber(), response.getOrderId(), response.getPaymentCode());
              /*  UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                    }
                });*/
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        Toast.makeText(ParcelSplitActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getPayInfo(final String number, final int orderCode, final String payCode) {
        final PaymentManager paymentManager = PaymentManager.getInstance(ParcelSplitActivity.this);

        if ("krbank".equals(payCode)) {
            Intent intent = new Intent(ParcelSplitActivity.this, KrBankInfoActivity.class);
            intent.putExtra(KrBankInfoActivity.NEED_PAY_COUNT, mNeedMoney);
            intent.putExtra(KrBankInfoActivity.SHIPPING_FEE_TOTAL, mNeedMoney);
            intent.putExtra(KrBankInfoActivity.BALANCE, mGiftcardaBanlance);
            intent.putExtra(KrBankInfoActivity.PAYMENT, getString(R.string.String_krbank_pay2));
            intent.putExtra(KrBankInfoActivity.PAY_CURRENCY_CODE, mParcle.getCurrencycode());
            startActivity(intent);
        } else if ("adyen".equals(payCode)) {
            paymentManager.adyenPay(PayMethodManager.getInstance(ParcelSplitActivity.this).getPayMethodName(), number, orderCode, 1, mNeedMoney, mParcle.getCurrencycode(), new PaymentManager.OnPayListener() {
                @Override
                public void onPay(Response response, final PayResultBaen payResult) {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            if ("Authorised".equals(payResult.resultCode) && !"".equals(payResult.authCode)) {
                                paySucess(true);
                            }
                        }
                    });
                }

                @Override
                public void onError(final String errMsg) {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            paySucess(false);
                            String err = errMsg;
                            if (TextUtils.isDigitsOnly(err)) {
                                err = getString(R.string.pay_error_msg);
                            }
//                                    CustomToast.makeText(ParcelSplitActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else if (Constants.GIFT_CARD_PAYMENT_DAOUPAY.equals(payCode)) {
            paymentManager.daouPay(ParcelSplitActivity.this, mNeedMoney, orderCode, 1, new OperaRequestListener() {
                @Override
                public void onOperaSuccess() {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            paySucess(true);
                        }

                    });
                }

                @Override
                public void onOperaFailure() {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            paySucess(false);
//                                    CustomToast.makeText(ParcelSplitActivity.this, "err", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


        if (!("alipay".equals(payCode) | "wxap".equals(payCode))) return;
        CsParcel.PayGiftCardOrderRequest.Builder builder = CsParcel.PayGiftCardOrderRequest.newBuilder();
        builder.setPaymentCode(payCode).setGiftCardOrderId(orderCode).setCurrencycode(mParcle.getCurrencycode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayGiftCardOrderResponse>() {

            @Override
            public void onSuccess(CsParcel.PayGiftCardOrderResponse response) {
               /* UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                    }
                });*/
                LogUtils.d(response.toString());
                SysApplication.mCurrentRequestPayment = mParcle.getParcelName();
                String payInfo = response.getPayInfo();
                if ("alipay".equals(payCode)) {
                    paymentManager.aliPay(payInfo, new PaymentManager.OnAliPayListener() {
                        @Override
                        public void onSuccess(String resultStatus) {

                        }

                        @Override
                        public void onFailure(String resultStatus, String errorMsg) {

                        }
                    });
                } else if ("wxap".equals(payCode)) {
                    paymentManager.wechatPay(payInfo);
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    private void splitUseBanlance() {
        CsParcel.SaveReparcelRequest.Builder builder = CsParcel.SaveReparcelRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest()).setParcelId(mParcle.getParcelId());
        ArrayMap declarePrices = mAdapter.getDeclarePrices();
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode()).setCurrencyCode(mParcle.getCurrencycode());
        for (int i = 0; i < mItemLists.size(); i++) {
            ReParcelItemBean list = mItemLists.get(i);
            if (list.isSelect()) {
                float declare = 0;
                int qty_value = mAdapter.getDeclareNum().get(i) == null ? list.getQtyPack() : mAdapter.getDeclareNum().get(i);
                qty_value = qty_value == 0 ? list.getQtyPack() : qty_value;
                if (declarePrices.get(i) == null) {
                    declare = list.getDeclaredValue();
                } else {
                    declare = (float) declarePrices.get(i);
                }
                builder.addItemIds(CsParcel.CheckedItemList.newBuilder().setItemid(list.getParcelItemId()));
                builder.addDeclaredValue(CsBase.PairStrFloat.newBuilder().setK(list.getParcelItemId()).setV(declare));
                builder.addQty(CsBase.PairStrStr.newBuilder().setK(list.getParcelItemId()).setV(qty_value + ""));
            }
        }

        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.SaveReparcelResponse>() {

            @Override
            public void onSuccess(final CsParcel.SaveReparcelResponse response) {
//                response.getParcelid();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        Toast.makeText(ParcelSplitActivity.this, getString(R.string.package_toast_split_sucess), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ParcelSplitActivity.this, PackageDetailActivity.class);
                        intent.putExtra(PackageDetailActivity.PARCEL_ID, response.getParcelId());
                        startActivity(intent);
                        EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_TOSEND_PARCEL, mParcle.getParcelId()));
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        Toast.makeText(ParcelSplitActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Constants.PAYMENT_REQUEST_CODE) {
            initPayType2();
        }
    }


    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        if (event.getType() == BusEvent.PAY_MENT_RESULT && event.getStrParam().equals(mParcle.getParcelName())) {
            SysApplication.mCurrentRequestPayment = "";
            boolean succcess = event.getBooleanParam();
            paySucess(succcess);
        }
    }

    private void paySucess(boolean succcess) {
        if (succcess) {
            Toast.makeText(ParcelSplitActivity.this, getString(R.string.package_toast_split_sucess), Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new BusEvent(BusEvent.REFRESH_TOSEND_PARCEL, mParcle.getParcelId()));
            finish();
        } else {
            closeLoading();
        }
    }

    private boolean checkHasFocuse() {
        if (mKeyBoardOpen) hideSoftInput();
        return !mKeyBoardOpen;
    }

}
