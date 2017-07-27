package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.PayResultBaen;
import com.fuexpress.kr.bean.PickUpItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ActivityController;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.adapter.PickItemAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomListView;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.ClassUtil;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.RateUtil;
import com.fuexpress.kr.utils.SPUtils;
import com.google.protobuf.GeneratedMessage;
import com.socks.library.KLog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;
import fksproto.CsParcel;
import okhttp3.Response;

/**
 * Created by kevin on 2016/12/13.
 */

public class ReplenishActivity extends BaseActivity {
    private static final int CODE_PAYMENT_REQUEST = 0x1001;
    private static final int CODE_EDIT_REQUEST = 0x1002;
    public static final String KOREA_CUCCENCY_CODE = "KRW";
    @BindView(R.id.fee_list_tv)
    TextView mFeeListTv;
    private View rootView;
    private LinearLayout paymentLayout;
    private TitleBarView titleBarView;
    private TextView backTv;
    private ImageView backIv;
    private CustomListView pickListView;
    private ScrollView pickUpSv;
    private LinearLayout emptyLayout;

    private TextView balanceTv;
    private TextView grandTotalTv;
    private LinearLayout serviceFeeLayout;
    private TextView waringTv;
    private Button submitBtn;
    private TextView paymentTv;
    private LinearLayout rateLayout;
    private TextView rateTv;
    private TextView rateBackTv;

    private CsParcel.HelpMyBuyInitResponse mResponse;
    private List<PickUpItemBean> pickUpItemlist = new ArrayList<PickUpItemBean>();
    private int payType = Constants.PAYMENT_ALIPAY;
    private String currencyCode;
    private String payCode;

    private Button addBtn;
    private List<CsParcel.ProductDataList> productList;
    private PickItemAdapter pickItemAdapter;
    private List<PickUpItemBean> pickList = new ArrayList<>();
    private SweetAlertDialog mSweetDialog;
    private boolean isFull = false;
    private String payString;
    private double mServiceFeeRate;
    private float grandTotal;
    private float balance;
    private boolean isUseBalance = false;
    //需求单号
    private String salesRequireManId;
    static String SHOW_TIPS = "show_replenish_tip";
    private ImageView helpIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean show_pick_tip = (boolean) SPUtils.get(this, SHOW_TIPS, true);
        if (show_pick_tip) {
            showTips();
        }
    }
    private void showTips(){
        final FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        View inflate = View.inflate(this, R.layout.pick_tips, null);
        initTip(inflate);
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.put(ReplenishActivity.this, SHOW_TIPS, false);
                decorView.removeView(v);
            }
        });
        decorView.addView(inflate);
    }
    private void initTip(View inflate) {
        TextView child1 = (TextView) inflate.findViewById(R.id.tv_tip1);
        String string = SPUtils.getString(this, ChooseCityActivity.F_RECENT_CITY_ID, "");
        if (string.contains("2103")) {
            child1.setText(getString(R.string.string_pick_tip_2103));
        } else {

            child1.setText(getString(R.string.string_pick_tip_1130));
        }

    }


    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_replenish, null);
        titleBarView = (TitleBarView) rootView.findViewById(R.id.pick_up_titlebar);
        backTv = titleBarView.getmTv_in_title_back_tv();
        backTv.setText(getString(R.string.main_home_tab_string));
        backIv = titleBarView.getIv_in_title_back();
        emptyLayout = (LinearLayout) rootView.findViewById(R.id.pick_up_empty_layout);
        paymentLayout = (LinearLayout) rootView.findViewById(R.id.pick_up_payment_layout);
        paymentTv = (TextView) rootView.findViewById(R.id.pick_up_payment_tv);
        pickUpSv = (ScrollView) rootView.findViewById(R.id.pick_up_sv);
        pickListView = (CustomListView) rootView.findViewById(R.id.pick_up_listview);
        balanceTv = (TextView) rootView.findViewById(R.id.pick_up_balance_tv);
        serviceFeeLayout = (LinearLayout) rootView.findViewById(R.id.pick_up_service_fee_layout);
        waringTv = (TextView) rootView.findViewById(R.id.pick_up_service_waring_tv);
        grandTotalTv = (TextView) rootView.findViewById(R.id.pick_up_grand_total_tv);
        submitBtn = (Button) rootView.findViewById(R.id.pick_up_submit_btn);
        addBtn = (Button) rootView.findViewById(R.id.pick_up_add_btn);
        rateLayout = (LinearLayout) rootView.findViewById(R.id.pick_up_rate_layout);
        rateTv = (TextView) rootView.findViewById(R.id.pick_up_rate_msg_one);
        rateBackTv = (TextView) rootView.findViewById(R.id.pick_up_rate_msg_two);
        mSweetDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        backIv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        paymentLayout.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        helpIv = (ImageView) rootView.findViewById(R.id.pick_up_help_iv);
        helpIv.setOnClickListener(this);
        showList();
        helpMyBuyInitRequest();
        return rootView;
    }


    public void init() {

        productList = new ArrayList<CsParcel.ProductDataList>();
        pickList = new ArrayList<PickUpItemBean>();
        List<PickUpItemBean> itemList = (List<PickUpItemBean>) SPUtils.readObject(UIUtils.getContext(), EditReplenishActivity.CODE_PICK_LIST);

        List<CsParcel.ProductDataList> pList = (List<CsParcel.ProductDataList>) SPUtils.readObject(UIUtils.getContext(), EditReplenishActivity.CODE_PRODUCT_LIST);
        if (itemList != null) {
            pickList.addAll(itemList);
        }
       /* if (itemList != null) {
            for (PickUpItemBean bean : itemList) {
                KLog.i(bean.toString());
            }
        }*/

        if (pList != null) {
            productList.addAll(pList);
        }
        if (pickList.size() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
            updateServiceFeeLayout(pickList);
        } else {
            emptyLayout.setVisibility(View.GONE);
        }
        pickItemAdapter = new PickItemAdapter(this, pickList, true,mResponse !=null ? mResponse.getCityCurrencyCode() : SPUtils.getString(this, PickUpActivity.CODE_CITY_CURRENCY, ""));
        pickListView.setAdapter(pickItemAdapter);
        pickListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CsParcel.ProductDataList item = (CsParcel.ProductDataList) parent.getAdapter().getItem(position);
                Intent intent = new Intent(ReplenishActivity.this, EditReplenishActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(EditReplenishActivity.CODE_PICK_LIST, (Serializable) pickList.get(position).getPathList());
                bundle.putSerializable(EditReplenishActivity.CODE_PICK_ITEM, (Serializable) pickList.get(position));
//                bundle.putSerializable(EditReplenishActivity.CODE_PRODUCT_ITEM, item);
                intent.putExtra(PickUpActivity.CODE_CITY_CURRENCY, mResponse.getCityCurrencyCode());
                intent.putExtra(EditReplenishActivity.CODE_POSITION, position);
                intent.putExtras(bundle);
                startActivityForResult(intent, CODE_EDIT_REQUEST);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pickList != null && pickList.size() == 4) {
            //满了4天，禁用添加
            isFull = true;
            addBtn.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
        } else {
            isFull = false;
            addBtn.setBackground(getResources().getDrawable(R.drawable.btn_bg));
        }
        if (pickList == null || pickList.size() == 0) {
        //    submitBtn.setEnabled(false);
            emptyLayout.setVisibility(View.VISIBLE);
            if (mResponse != null) {
                grandTotalTv.setText(UIUtils.getCurrency(this, mResponse.getCurrencyCode(), 0));
            }

            submitBtn.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
        } else {
            submitBtn.setEnabled(true);
            emptyLayout.setVisibility(View.GONE);
            submitBtn.setBackgroundColor(getResources().getColor(R.color.title_bar_black));
        }
        KLog.i("code = " + AccountManager.getInstance().getCurrencyCode());
        if (KOREA_CUCCENCY_CODE.equals(AccountManager.getInstance().getCurrencyCode())) {
            //    rateLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.tv_in_title_right:
                break;
            case R.id.pick_up_payment_layout:
                Intent intent = new Intent(this, PaymentActivity.class);
                int i = (int) SPUtils.get(this, AccountManager.getInstance().getCurrencyCode() + "paymentPos", 0);
                intent.putExtra("paymentPos", i);
                intent.putExtra(PaymentActivity.CURRENCY_CODE, currencyCode);
                intent.putExtra("payType", payType);
                intent.putExtra("payCode", payCode);
                intent.putExtra(PaymentActivity.IS_USE_BALANCE, isUseBalance);
                intent.putExtra(PaymentActivity.IS_SEND_PACKAGE, false);
                intent.putExtra("paymentString", payString);
                startActivityForResult(intent, CODE_PAYMENT_REQUEST);
                break;
            case R.id.pick_up_submit_btn:
                if (pickList.size() <= 0) {
                    CustomToast.makeText(this, getString(R.string.pick_submit_error_msg_item), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (payCode == null) {
                    CustomToast.makeText(this, getString(R.string.pick_up_choose_payment_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<CsParcel.ProductDataList> lists = ClassUtil.conventBeanList2DataList(pickList);
                saveHelpMyBuyRequest(payCode, lists.size() + "", lists);
                break;
            case R.id.pick_up_add_btn:
                if (pickList != null && pickList.size() >= 4) {
                    addBtn.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
                    CustomToast.makeText(this, getString(R.string.item_should_be_less_than_4), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    int j = 0;
                    if (pickList != null) {
                        j = pickList.size();
                    }
                    Intent intent1 = new Intent(this, AddReplenishActivity.class);
                    intent1.putExtra(PickUpActivity.CODE_CITY_CURRENCY, mResponse.getCityCurrencyCode());
                    intent1.putExtra(AddReplenishActivity.CODE_ITEM_COUNT, j);
                    startActivityForResult(intent1, 100);
                }
//                toActivity(AddReplenishActivity.class);
                break;
            case R.id.pick_up_help_iv:
                showTips();
                break;
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.d("----requestCode " + requestCode);
        if (resultCode == Constants.PAYMENT_REQUEST_CODE) {
            payString = SPUtils.get(ReplenishActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_NAME, "").toString();
            payCode = SPUtils.get(ReplenishActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_CODE, "").toString();
            paymentTv.setText(payString);
            isUseBalance = data.getBooleanExtra(PaymentActivity.IS_USE_BALANCE, false);
             balance = data.getFloatExtra("account", 0.0f);
            if(isUseBalance){
                if (balance <= 0) {
                    balanceTv.setVisibility(View.GONE);
                } else {
                    balanceTv.setVisibility(View.VISIBLE);
                    balanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(ReplenishActivity.this, balance));
                }
                float price = grandTotal - balance;
                paymentTv.setText(getString(R.string.pick_up_payment_balance2, payString) + UIUtils.getCurrency(ReplenishActivity.this, AccountManager.getInstance().getCurrencyCode(), price));
                if (price <= 0) {
                    paymentTv.setVisibility(View.GONE);
                } else {
                    paymentTv.setVisibility(View.VISIBLE);
                }
            }else {
                float price = grandTotal;
                paymentTv.setText(getString(R.string.pick_up_payment_balance2, payString) + UIUtils.getCurrency(ReplenishActivity.this, AccountManager.getInstance().getCurrencyCode(), price));
                if (price <= 0) {
                    paymentTv.setVisibility(View.GONE);
                } else {
                    paymentTv.setVisibility(View.VISIBLE);
                }
            }
        }
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            List<CsParcel.ProductDataList> list = (List<CsParcel.ProductDataList>) bundle.getSerializable(AddReplenishActivity.CODE_PRODUCT_LIST);
            List<PickUpItemBean> pickItem = (List<PickUpItemBean>) bundle.getSerializable(AddReplenishActivity.CODE_PICK_LIST);
//            pickList.clear();
            pickList.addAll(pickItem);

//            productList.clear();
            productList.addAll(list);
            if (pickList.size() > 0) {
                if (pickList.size() >= 4) {
                    isFull = true;
                    updateServiceFeeLayout(pickList);
                    pickItemAdapter.notifyDataSetChanged();
//                    addBtn.setBackground();
                } else {
                    emptyLayout.setVisibility(View.GONE);
                    updateServiceFeeLayout(pickList);
                    pickItemAdapter.notifyDataSetChanged();
                }
                for (PickUpItemBean bean : pickList) {
                    KLog.i(bean.toString());
                }
            } else {
                emptyLayout.setVisibility(View.VISIBLE);
            }
        }
        if (resultCode == EditReplenishActivity.CODE_RESULT_EDIT) {
            Bundle bundle = data.getExtras();
            List<CsParcel.ProductDataList> list = (List<CsParcel.ProductDataList>) bundle.getSerializable(EditReplenishActivity.CODE_PRODUCT_LIST);
            List<PickUpItemBean> pickItem = (List<PickUpItemBean>) bundle.getSerializable(EditReplenishActivity.CODE_PICK_LIST);
            int position = data.getIntExtra(EditReplenishActivity.CODE_POSITION, -1);
            if (position > -1) {
                pickList.remove(position);
                pickList.addAll(pickItem);
                //    productList.remove(position);
                //  productList.addAll(list);
            }
            if (pickList.size() > 0) {
                if (pickList.size() >= 4) {
                    isFull = true;
//                    addBtn.setBackground();
                    emptyLayout.setVisibility(View.GONE);
                    updateServiceFeeLayout(pickList);
                    pickItemAdapter.notifyDataSetChanged();
                } else {
                    emptyLayout.setVisibility(View.GONE);
                    updateServiceFeeLayout(pickList);
                    pickItemAdapter.notifyDataSetChanged();
                }
            } else {

            }
        }
        if (resultCode == EditReplenishActivity.CODE_RESULT_DELETE) {
            boolean isDeleted = data.getBooleanExtra(EditReplenishActivity.CODE_IS_DELETE_ITEM, false);
            if (isDeleted) {
                int position = data.getIntExtra(EditReplenishActivity.CODE_POSITION, -1);
                pickList.remove(position);
                pickItemAdapter.notifyDataSetChanged();
                SPUtils.saveObject(UIUtils.getContext(), EditReplenishActivity.CODE_PICK_LIST, pickList);
            }
        }

        SPUtils.saveObject(UIUtils.getContext(), EditReplenishActivity.CODE_PICK_LIST, pickList);
        if (requestCode == 4129) {
            //支付成功之后，清除数据
            SPUtils.saveObject(UIUtils.getContext(), EditReplenishActivity.CODE_PICK_LIST,null);
            pickList.clear();
            pickItemAdapter.notifyDataSetChanged();
            setEmptyNotice();
        }
        if (mResponse != null && pickList != null && pickList.size() != 0) {
            updateServiceFeeLayout(pickList);
            setPaymentData(mResponse, pickList);
        }
        if (mResponse != null) {
            if (pickList == null || pickList.size() == 0) {
                setEmptyNotice();
            }
        }
    }

    public void saveHelpMyBuyRequest(String payCode, String sumcount, List<CsParcel.ProductDataList> productList) {

        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.pick_up_submitting));
        CsParcel.SaveHelpMyBuyRequest.Builder builder = CsParcel.SaveHelpMyBuyRequest.newBuilder();
        builder.setUserhead(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setPaymentcode(payCode);
        builder.setSumcount(sumcount);
        //是否使用余额 1代表使用 2代表不使用
        builder.setUseaccountpay(isUseBalance ? 1 : 2);
        builder.addAllProductlist(productList);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.SaveHelpMyBuyResponse>() {

            @Override
            public void onSuccess(CsParcel.SaveHelpMyBuyResponse response) {
                SPUtils.saveObject(ReplenishActivity.this, EditReplenishActivity.CODE_PICK_LIST, null);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        pickList.clear();
                        pickItemAdapter.notifyDataSetChanged();
                        setEmptyNotice();
                    }
                });

                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                saveHanlder.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissMissProgressDiaLog(500);
                    }
                });
            }
        });
    }

    private String total;
    private Handler saveHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CsParcel.SaveHelpMyBuyResponse response = (CsParcel.SaveHelpMyBuyResponse) msg.obj;
            String redirectUrl = response.getRedirecturl();
            if (TextUtils.isEmpty(redirectUrl)) {
                dissMissProgressDiaLog(500);
//                CustomToast.makeText(PickUpActivity.this, getString(R.string.pick_up_save_help_my_get_empty), Toast.LENGTH_SHORT).show();
                CustomToast.makeText(ReplenishActivity.this, getString(R.string.pick_up_submit_success_msg), Toast.LENGTH_SHORT).show();
                SPUtils.saveObject(ReplenishActivity.this, EditReplenishActivity.CODE_PICK_LIST, null);
                SPUtils.saveObject(ReplenishActivity.this, EditReplenishActivity.CODE_PRODUCT_LIST, null);
                Intent intent = new Intent(ReplenishActivity.this, PickUpSuccessActivity.class);
                startActivityForResult(intent, 4129);
                return;
            }
            String param[] = redirectUrl.split("\\?")[1].split("&");
            String type = param[1].split("=")[1];
            String appType = param[2].split("=")[1];
            String paymentCode = param[3].split("=")[1];
            salesRequireManId = param[4].split("=")[1];
            total = param[5].split("=")[1];

            if (TextUtils.equals(paymentCode, Constants.GIFT_CARD_PAYMENT_KRBANK)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissMissProgressDiaLog(500);
                    }
                });
                Intent intent = new Intent(ReplenishActivity.this, PickUpKrbankInfoActivity.class);
                intent.putExtra(PickUpKrbankInfoActivity.CODE_SALES_REQUIRE_MAN_ID, salesRequireManId);
                intent.putExtra(PickUpKrbankInfoActivity.CODE_TOTAL, UIUtils.getCurrency(ReplenishActivity.this, Float.parseFloat(total)));
                intent.putExtra(PickUpKrbankInfoActivity.CODE_PAY_TYPE, Integer.parseInt(type));
                intent.putExtra(PickUpKrbankInfoActivity.FROM_WHERE,false);
                String balanceString;
                String needString;
                if (balance > Float.parseFloat(total)) {
                    balanceString = total;
                    needString = "0.0";
                } else {
                    balanceString = balance + "";
                    needString = Float.parseFloat(total) - balance + "";
                }

                intent.putExtra(PickUpKrbankInfoActivity.CODE_BALANCE_PAY, UIUtils.getCurrency(ReplenishActivity.this, Float.parseFloat(balanceString)));
                intent.putExtra(PickUpKrbankInfoActivity.CODE_NEED_PAY,
                        UIUtils.getCurrency(ReplenishActivity.this, Float.parseFloat(needString)));
                startActivity(intent);
            } else {
                doDirectGiftCardRequest(Integer.parseInt(type),
                        Integer.parseInt(appType),
                        paymentCode,
                        Float.parseFloat(total),
                        Integer.parseInt(salesRequireManId),
                        ReplenishActivity.this.mResponse.getCurrencyCode());
            }
//            CustomToast.makeText(mContext, getString(R.string.pick_up_add_save_success_msg), Toast.LENGTH_SHORT).show();
//            clearData();
//            mSweetAlertDialog.dismiss();
//            EventBus.getDefault().post(new BusEvent(BusEvent.CODE_GO_MERCHAND, true));
            /*if(mResponse!=null&&pickList!=null&&pickList.size()!=0){
                setServiceFeeData(mResponse,pickList);
            }*/
        }
    };

    public void doDirectGiftCardRequest(int type, int appType, String paymentCode, float total, int salesrequiremainid, String currencyCode) {
        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.pick_up_generating_order));
        CsParcel.DoDirectGiftCardRequest.Builder builder = CsParcel.DoDirectGiftCardRequest.newBuilder();
        builder.setType(type);
        builder.setAppType(appType);
        builder.setPaymentCode(paymentCode);
        builder.setTotal(total);
        builder.setSalesrequiremainid(salesrequiremainid);
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(currencyCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.DoDirectGiftCardResponse>() {

            @Override
            public void onSuccess(CsParcel.DoDirectGiftCardResponse response) {

                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                directHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissMissProgressDiaLog(500);
                    }
                });
            }
        });
    }

    private Handler directHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CsParcel.DoDirectGiftCardResponse response = (CsParcel.DoDirectGiftCardResponse) msg.obj;
            int giftCardOrderId = response.getOrderId();
            String paymentCode = response.getPaymentCode();
            String orderNumber=response.getNumber();
            if(TextUtils.equals(Constants.GIFT_CARD_PAYMENT_DAOUPAY,paymentCode) || (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_ADYEN, paymentCode))){
                payWithAdyenOrDaouPay(paymentCode, orderNumber, giftCardOrderId, ReplenishActivity.this.mResponse.getCurrencyCode());
            }else {
                payGiftCardOrderRequest(giftCardOrderId, paymentCode, ReplenishActivity.this.mResponse.getCurrencyCode());
            }

        }
    };

    public void payGiftCardOrderRequest(final int giftCardOrderId, final String paymentCode,final String currencyCode) {
//        showLoading();
        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.pick_up_getting_pay_info));
        CsParcel.PayGiftCardOrderRequest.Builder builder = CsParcel.PayGiftCardOrderRequest.newBuilder();
        builder.setGiftCardOrderId(giftCardOrderId);
        builder.setPaymentCode(paymentCode);
        builder.setCurrencycode(currencyCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayGiftCardOrderResponse>() {

            @Override
            public void onSuccess(CsParcel.PayGiftCardOrderResponse response) {
                LogUtils.d(response.toString());
                SysApplication.mCurrentRequestPayment = ReplenishActivity.class.getSimpleName();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissMissProgressDiaLog(500);
                        closeLoading();
                    }
                });

                if (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_ALIPAY, paymentCode)) {
                    PaymentManager.getInstance(ReplenishActivity.this).aliPay(response.getPayInfo(), new PaymentManager.OnAliPayListener() {
                        @Override
                        public void onSuccess(String resultStatus) {
                            SPUtils.saveObject(UIUtils.getContext(), EditReplenishActivity.CODE_PICK_LIST, null);
                            SPUtils.saveObject(UIUtils.getContext(), EditReplenishActivity.CODE_PRODUCT_LIST, null);
                            Intent intent = new Intent(ReplenishActivity.this, PickUpSuccessActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String resultStatus, final String errorMsg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToast.makeText(ReplenishActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().post(new BusEvent(BusEvent.GO_MY_NEED, false));
                                    ActivityController.finishActivityOutOfMainActivity();
                                }
                            });
                        }
                    });
                }
                if (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_WXPAY, paymentCode)) {
                   /* SysApplication app = (SysApplication) getApplication();
                    app.setPaymentRequestCode(Constants.PAYMENT_REQUEST_CODE_PICK_UP);*/
                    PaymentManager.getInstance(ReplenishActivity.this).wechatPay(response.getPayInfo());
                }

            }

            @Override
            public void onFailure(int ret, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        dissMissProgressDiaLog(500);
                    }
                });
            }
        });

    }


    public void payWithAdyenOrDaouPay(String paymentCode, String orderNumber, int giftCardOrderId, String currencyCode){
        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.pick_up_getting_pay_info));
        if (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_ADYEN, paymentCode)) {
            PaymentManager.getInstance(ReplenishActivity.this).adyenPay(payString, orderNumber, giftCardOrderId, 1, Float.parseFloat(total), currencyCode, new PaymentManager.OnPayListener() {
                @Override
                public void onPay(Response response, PayResultBaen payResult) {
                    String authCode = payResult.authCode;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeLoading();
                            dissMissProgressDiaLog(500);
                        }
                    });
                    //setEmptyNotice();
                    if (!TextUtils.isEmpty(authCode)) {
                        // TODO: 2017/4/30 请注意在该回调中是子线程,请把UI相关的操作放到主线程中运行 -edit by longer
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setEmptyNotice();
                                Intent intent = new Intent(ReplenishActivity.this, PickUpSuccessActivity.class);
                                startActivityForResult(intent, 4129);
                            }
                        });
                                /*Intent intent = new Intent(ReplenishActivity.this, PickUpSuccessActivity.class);
                                startActivityForResult(intent, 4129);*/

                    }
                }

                @Override
                public void onError(final String errMsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToast.makeText(ReplenishActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                            dissMissProgressDiaLog();
                            EventBus.getDefault().post(new BusEvent(BusEvent.GO_MY_NEED, false));
                            ActivityController.finishActivityOutOfMainActivity();
                        }
                    });
                }
            });
        }
        if(TextUtils.equals(Constants.GIFT_CARD_PAYMENT_DAOUPAY, paymentCode)){
            DaoUPayActivity.IntentBuilder intentBuilder = DaoUPayActivity.IntentBuilder.build(ReplenishActivity.this)
                    .setAmount(Integer.parseInt(total))
                    .setOrderID(giftCardOrderId)
                    .setOrderType(1)
                    .setListener(new OperaRequestListener() {
                        @Override
                        public void onOperaSuccess() {

                            SPUtils.saveObject(UIUtils.getContext(), EditReplenishActivity.CODE_PICK_LIST, null);
                            SPUtils.saveObject(UIUtils.getContext(), EditReplenishActivity.CODE_PRODUCT_LIST, null);
                            Intent intent = new Intent(ReplenishActivity.this, PickUpSuccessActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }

                        @Override
                        public void onOperaFailure() {
                            CustomToast.makeText(ReplenishActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
                            dissMissProgressDiaLog();
                            EventBus.getDefault().post(new BusEvent(BusEvent.GO_MY_NEED, false));
                            ActivityController.finishActivityOutOfMainActivity();
                        }
                    });
            startActivity(intentBuilder);
        }
        if (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_KRBANK, paymentCode)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dissMissProgressDiaLog();
                }
            });
        }
    }

    public void showProgressDiaLog(int type, String titleText) {
        if (SweetAlertDialog.PROGRESS_TYPE == type) {
            mSweetDialog.getProgressHelper().setBarColor(Color.parseColor("#FFFFFF"));
        }
        if (!TextUtils.isEmpty(titleText) || !(titleText == null)) {
            mSweetDialog.setTitleText(titleText);
        }

        mSweetDialog.setCancelable(false);
        mSweetDialog.show();
    }

    public void dissMissProgressDiaLog() {
        //mSweetDialog.setTitleTextViewGone();
        mSweetDialog.dismiss();
    }

    public void dissMissProgressDiaLog(long delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //mSweetDialog.setTitleTextViewGone();
                mSweetDialog.dismiss();
            }
        }, delay);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSweetDialog.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mSweetDialog != null) {
                mSweetDialog.dismiss();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private void reSetString(String currencyCode){
        if(EditReplenishActivity.CODE_PRODUCT_LIST.contains(currencyCode)){
            //已经处理过了
            return;
        }
        EditReplenishActivity.CODE_PRODUCT_LIST= EditReplenishActivity.CODE_PRODUCT_LIST_final+currencyCode;
        EditReplenishActivity.CODE_ITEM_COUNT=EditReplenishActivity.CODE_ITEM_COUNT_fianl+currencyCode;
        EditReplenishActivity.CODE_PRODUCT_ITEM=EditReplenishActivity.CODE_PRODUCT_ITEM_final+currencyCode;
        EditReplenishActivity.CODE_PICK_ITEM =EditReplenishActivity.CODE_PICK_ITEM_final+currencyCode;
        EditReplenishActivity.CODE_PICK_LIST = EditReplenishActivity.CODE_PICK_LIST_final+currencyCode  ;
        EditReplenishActivity.CODE_IS_DELETE_ITEM = EditReplenishActivity.CODE_IS_DELETE_ITEM_final +currencyCode;
        EditReplenishActivity.CODE_POSITION = EditReplenishActivity.CODE_POSITION_final+currencyCode;

        AddReplenishActivity.CODE_PRODUCT_LIST= AddReplenishActivity.CODE_PRODUCT_LIST_final+currencyCode ;
        AddReplenishActivity.CODE_PICK_LIST = AddReplenishActivity.CODE_PICK_LIST_final+currencyCode;
        AddReplenishActivity.CODE_ITEM_COUNT= AddReplenishActivity. CODE_ITEM_COUNT_final+currencyCode;
    }
    public void helpMyBuyInitRequest() {
        showLoading();
        CsParcel.HelpMyBuyInitRequest.Builder builder = CsParcel.HelpMyBuyInitRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencyCode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.HelpMyBuyInitResponse>() {

            @Override
            public void onSuccess(CsParcel.HelpMyBuyInitResponse response) {
                reSetString(SPUtils.getString(ReplenishActivity.this,ChooseCityActivity.F_RECENT_CITY_ID,""));
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        // TODO: 2017/5/31 TB Android1.0.6 BUG06 修改-chenyl
                        //init();
                    }
                });
                closeLoading();
                KLog.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                initHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                KLog.i("ret = " + ret + " errMsg " + errMsg);
                closeLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.pick_up_getting_pay_info));
                        dissMissProgressDiaLog(500);
                    }
                });
            }
        });
    }

    private Handler initHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mResponse = (CsParcel.HelpMyBuyInitResponse) msg.obj;
            balance = (float) mResponse.getGiftCardAccount();
            currencyCode = mResponse.getCurrencyCode();
            SPUtils.putString(ReplenishActivity.this, PickUpActivity.CODE_CITY_CURRENCY, mResponse.getCityCurrencyCode());
            PaymentManager.getInstance(ReplenishActivity.this).getFKDPaymentListRequest(mResponse.getCurrencyCode(), new PaymentManager.OnFKDPaymentListener() {
                @Override
                public void onResult(final int paymentType, final String pString, String pCode) {
                    payCode = pCode;
                    payString = pString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mResponse.getGiftCardAccount() <= 0) {
                                balanceTv.setVisibility(View.GONE);
                                NumberFormat nf = NumberFormat.getInstance();
                                nf.setMaximumFractionDigits(0);
                                nf.setRoundingMode(RoundingMode.CEILING);
                                nf.setGroupingUsed(true);
                                paymentTv.setText(getString(R.string.pick_up_payment_balance2, payString) + UIUtils.getCurrency(ReplenishActivity.this, AccountManager.getInstance().getCurrencyCode()) + grandTotal);
                            } else {
                                balanceTv.setVisibility(View.VISIBLE);
                                paymentTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(ReplenishActivity.this, (float) mResponse.getGiftCardAccount()));
                            }
                            if (mResponse != null && pickList != null) {
                                updateServiceFeeLayout(pickList);
                                setPaymentData(mResponse, pickList);
                            }
                          /*  if (pickList == null || pickList.size() == 0) {
                                paymentTv.setText(payString);
                            //    paymentTv.setVisibility(View.GONE);
                            }*/
                        }
                    });
                }
            });
            if (!currencyCode.equals(mResponse.getCityCurrencyCode())) {
//                rateTv.setText(getString(R.string.pick_up_rate_msg, mResponse.getCityCurrencyCode(), mResponse.getExchangeRate(), mResponse.getCurrencyCode()));
//                rateBackTv.setText(getString(R.string.pick_up_rate_back_msg, mResponse.getCurrencyCode(), mResponse.getExchangeRateBack(), mResponse.getCityCurrencyCode()));
                rateTv.setText(getString(R.string.pick_up_rate_msg, mResponse.getCityCurrencyCode(), mResponse.getStrExchangeRate(), mResponse.getCurrencyCode()));
                rateBackTv.setText(getString(R.string.pick_up_rate_back_msg, mResponse.getCurrencyCode(), mResponse.getStrExchangeRateBack(), mResponse.getCityCurrencyCode()));
                rateLayout.setVisibility(View.VISIBLE);
            } else {
                rateLayout.setVisibility(View.GONE);
            }
            balanceTv.setVisibility(View.GONE);
            //   balanceTv.setText(getString(R.string.pick_up_payment_balance)+UIUtils.getCurrency(ReplenishActivity.this,mResponse.getCurrencyCode(),Float.valueOf(mResponse.getGiftCardAccount()+"")));
            waringTv.setText(getString(R.string.replenish_services_waring_msg, (int) mServiceFeeRate));
            if (mResponse != null && pickList != null) {
                updateServiceFeeLayout(pickList);
                setPaymentData(mResponse, pickList);
            }
//            itemAdapter = new ItemAdapter(ReplenishActivity.this, itemlist, mResponse.getCurrencyCode());
//            itemAdapter.notifyDataSetChanged();
            // TODO: 2017/5/31 TB Android1.0.6 BUG06 修改-chenyl
            init();
        }
    };


    //设置支付方式数据
    private void setPaymentData(CsParcel.HelpMyBuyInitResponse response, List<PickUpItemBean> list) {
        if (response == null || list == null || list.size() == 0) {
            return;
        }
    }

    private String getServiceFeeString(PickUpItemBean list) {
        String feeStr = UIUtils.getCurrency(this, mResponse.getCurrencyCode(), Float.valueOf(list.getItemPrice())) +
                "x" + list.getItemCount() + "x" + mServiceFeeRate + "%" + " = " +
                UIUtils.getCurrency(this, mResponse.getCurrencyCode(), Float.valueOf(list.getItemPrice()) * (float) mServiceFeeRate / 100);
        return feeStr;
    }

    public void scrollToBottom() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                pickUpSv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null)
            return;
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
        listView.setLayoutParams(params);
    }

    public CsParcel.RateList getRate(int count) {

        return getTyep1ServiceRate(count);
    }

    public CsParcel.RateList getTyep1ServiceRate(int count) {
        if (mResponse == null || count < 0 || mResponse.getRateListList() == null) {
            return null;
        } else {
            CsParcel.RateList list = null;
            int qtyResult = Integer.MAX_VALUE;
            for (CsParcel.RateList rateList : mResponse.getRateListList()) {
                int temp = count - rateList.getQty();
                if (temp < 0) {
                    continue;
                } else {
                    if (qtyResult > temp) {
                        qtyResult = temp;
                        list = rateList;
                    }
                }
            }
            return list;
        }
    }

    public float getType0ServiceRate(int count) {
        if (mResponse == null || count < 0 || mResponse.getRateListList() == null) {
            if(mResponse!=null){
                return (float) mResponse.getServiceFee();
            }else {
                return 0;
            }
        } else {
            double result = mResponse.getServiceFee();
            int qtyResult = Integer.MAX_VALUE;
            for (CsParcel.RateList rateList : mResponse.getRateListList()) {
                int temp = count - rateList.getQty();
                if (temp < 0) {
                    continue;
                } else {
                    if (qtyResult > temp) {
                        qtyResult = temp;
                        result = rateList.getServiceRate();
                    }
                }
            }
            return Float.valueOf(result + "");
        }
    }

    private void setEmptyNotice() {
        serviceFeeLayout.removeAllViews();
        emptyLayout.setVisibility(View.VISIBLE);
        TextView feeTv = new TextView(ReplenishActivity.this);
        feeTv.setTextColor(getResources().getColor(R.color.ff999));
        feeTv.setTextSize(13);
        feeTv.setGravity(Gravity.RIGHT);
        if(isUseBalance){
            balanceTv.setVisibility(View.VISIBLE);
            balanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(ReplenishActivity.this, (float) mResponse.getGiftCardAccount()));
        }else {
            balanceTv.setVisibility(View.GONE);
            paymentTv.setText(payString);
            paymentTv.setVisibility(View.VISIBLE);
        }
        if (mResponse.getRateType() == 0) {
            feeTv.setText(getString(R.string.pick_up_services_waring_msg2_item, UIUtils.getCurrency(this, mResponse.getCurrencyCode(), Float.valueOf(getType0ServiceRate(1) + ""))));
        } else {
            float i = (float) mResponse.getServiceFee();
            try {
                CsParcel.RateList list=getTyep1ServiceRate(1);
                if(list!=null){
                    i = (float) getTyep1ServiceRate(1).getServiceRate();
                }
            } catch (Exception e) {

            }
            feeTv.setText(getString(R.string.pick_up_services_waring_msg_item, i));
        }
        serviceFeeLayout.addView(feeTv);
       /* balanceTv.setVisibility(View.GONE);
        if(balance>0){
            paymentTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(ReplenishActivity.this, (float) mResponse.getGiftCardAccount()));
        }else {
            paymentTv.setText(payString);
        }
*/
    }

    public void updateServiceFeeLayout(List<PickUpItemBean> been) {
        if (been == null) {
            return;
        }
        if (null == mResponse) {
            return;
        }
        serviceFeeLayout.removeAllViews();
        float feeRate = 0;
        grandTotal = 0;
        if (been.size() == 0) {
            setEmptyNotice();
        }
        List<CsParcel.ProductDataList> lists = ClassUtil.conventBeanList2DataList(been);
        for (CsParcel.ProductDataList item : lists) {
            if (mResponse != null) {
                float fee = 0;
                String feeMsg = "";
                String feeHintMsg = "";
                float price = getFloat2(Float.parseFloat(item.getPrice()));
                int count = Integer.parseInt(item.getNum());
                if (mResponse.getFree() == 1) {
                    fee = 0;
                    serviceFeeLayout.removeAllViews();
                    TextView feeTv = new TextView(ReplenishActivity.this);
                    feeTv.setTextColor(getResources().getColor(R.color.black_0));
                    feeTv.setTextSize(13);
                    feeTv.setText(getString(R.string.pick_up_services_waring_msg3));
                    feeTv.setGravity(Gravity.RIGHT);
                    serviceFeeLayout.addView(feeTv);
                } else {
                    if (mResponse.getRateType() == 0) {
                        //服务费按照件数
                        fee = getType0ServiceRate(Integer.valueOf(item.getNum()));
                        feeMsg = UIUtils.getCurrency(ReplenishActivity.this,  mResponse.getCurrencyCode(),Float.valueOf(""+ price))
                                + "x"
                                + count
                                + "+"
                                + UIUtils.getCurrency(ReplenishActivity.this, mResponse.getCurrencyCode(), getType0ServiceRate(count))
                                + "x"
                                + count
                                + "="
                                + UIUtils.getCurrency(this, mResponse.getCurrencyCode(), Float.valueOf(((price * count) + fee  * count) + ""));
                        feeHintMsg = getString(R.string.pick_up_services_waring_msg2_item, UIUtils.getCurrency(this, mResponse.getCurrencyCode(), fee));
                    } else {
                        List<CsParcel.RateList> rateLists = mResponse.getRateListList();
                        if (rateLists.size() > 0) {
                            CsParcel.RateList rate;
                            if (rateLists.size() > 1) {
                                rate = getRate(count);
                            } else {
                                rate = rateLists.get(0);
                            }
                            if(rate==null){
                                fee = (float) (price * count * (mResponse.getServiceFee() / 100) );
                                feeRate = (float) mResponse.getServiceFee();
                            }else {
                                if (count >= rate.getQty()) {
                                    fee = (float) (price * count * (rate.getServiceRate() / 100) );
                                    feeRate = (float) rate.getServiceRate();
                                } else {
                                    fee = (float) (price * count * (mResponse.getServiceFee() / 100) );
                                    feeRate = (float) mResponse.getServiceFee();
                                }
                            }

                           // LogUtils.d("--------------" + rate.getServiceRate());
                        } else {
                            fee = (float) (price * count * (mResponse.getServiceFee() / 100) );
                            feeRate = (float) mResponse.getServiceFee();
                        }
                        feeMsg = UIUtils.getCurrency(ReplenishActivity.this, mResponse.getCurrencyCode(), price)
                                + "x"
                                + count
                                + "x"
                                + (getRate(feeRate))
                                + "%="
                                + UIUtils.getCurrency(this, mResponse.getCurrencyCode(), (float) (price * count * (feeRate + 100) / 100));
                        feeHintMsg = getString(R.string.pick_up_services_waring_msg_item, feeRate);
                    }
                    TextView feeTv = new TextView(ReplenishActivity.this);
                    feeTv.setTextColor(getResources().getColor(R.color.black_0));
                    feeTv.setTextSize(13);
                    feeTv.setText(feeMsg);
                    feeTv.setGravity(Gravity.RIGHT);
                    serviceFeeLayout.addView(feeTv);
                    TextView feeHintTv = new TextView(ReplenishActivity.this);
                    feeHintTv.setTextColor(getResources().getColor(R.color.gray_999));
                    feeHintTv.setTextSize(13);
                    feeHintTv.setText(feeHintMsg);
                    feeHintTv.setGravity(Gravity.RIGHT);
                    serviceFeeLayout.addView(feeHintTv);
                }


//                float f = price * count;
//                float f2 = f * feeRate / 100;


//                waringTv.setText(getString(R.string.pick_up_services_waring_msg, feeRate));
                if (mResponse.getRateType() == 0) {
                    grandTotal += price * count + Float.valueOf("" + fee  * count);
                } else {
                    grandTotal += price*count+fee;
                }
                //    grandTotal += fee;

            }
        }
        float ta = grandTotal;
        grandTotalTv.setText(UIUtils.getCurrency(this, ta));
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        nf.setRoundingMode(RoundingMode.CEILING);
        nf.setGroupingUsed(true);
        if (!isUseBalance||mResponse.getGiftCardAccount() <= 0) {
            balanceTv.setVisibility(View.GONE);

            paymentTv.setText(getString(R.string.pick_up_payment_balance2, payString) + UIUtils.getCurrency(this, AccountManager.getInstance().getCurrencyCode(), ta));
        } else {
            if (mResponse.getGiftCardAccount() > ta) {
                balanceTv.setVisibility(View.VISIBLE);
                balanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(ReplenishActivity.this, balance));
            } else {
                balanceTv.setVisibility(View.VISIBLE);
                balanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(ReplenishActivity.this, (float) mResponse.getGiftCardAccount()));
                paymentTv.setText(getString(R.string.pick_up_payment_balance2, payString) + UIUtils.getCurrency(this, AccountManager.getInstance().getCurrencyCode(),(float)(ta - mResponse.getGiftCardAccount())));
            }

        }

    }
    public float getFloat2(float price){
        if(mResponse.getCityCurrencyCode().equals(AccountManager.getInstance().getCurrencyCode())){
            return price;
        }
        int scale=2;
        int   roundingMode  =  4;
        BigDecimal bd  =   new  BigDecimal((double)price*mResponse.getExchangeRate());
        bd   =  bd.setScale(scale,roundingMode);
        return bd.floatValue();
    }
    public float getRate2(float rate,float price){
        int scale=2;
        int   roundingMode  =  4;
        BigDecimal bd  =   new  BigDecimal((double)rate*price);
        bd   =  bd.setScale(scale,roundingMode);
        return      bd.floatValue();
    }

    private String getRate(float rate){
        String str=(rate+100)+"";
        str=str.replace(".00","").replace(".0","");
        return str;
    }
    int itemId = -1;


    public void requestFocus(EditText editText) {
        editText.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        showList();
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.ADD_OTHER_REPLENISH_ITEM:
                //添加一个帮我补货
                PickUpItemBean bean = (PickUpItemBean) event.getParam();
                if (bean != null) {
                    if(pickList.size()>=4){
                        addBtn.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
                        CustomToast.makeText(this, getString(R.string.item_should_be_less_than_4), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pickList.add(bean);
                    pickItemAdapter.notifyDataSetChanged();
                    updateServiceFeeLayout(pickList);
                    SPUtils.saveObject(this, EditReplenishActivity.CODE_PICK_LIST, pickList);
                } else {
                    KLog.i(" 参数为空 '");
                }
                break;
            case BusEvent.PAY_MENT_RESULT:
                if(event.getStrParam().equals(ReplenishActivity.class.getSimpleName())){
                    SysApplication app = (SysApplication) getApplication();
                    if(event.getBooleanParam()){
                        //如果支付成功
                        if (app.getPaymentRequestCode() == Constants.PAYMENT_REQUEST_CODE_PICK_UP) {
                            app.setPaymentRequestCode(0);
                            Intent intent = new Intent(ReplenishActivity.this, PickUpSuccessActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, 4129);
                        }
                    }else {
                        //如果取消支付
                        CustomToast.makeText(ReplenishActivity.this, "pay error", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new BusEvent(BusEvent.GO_MY_NEED, false));
                        ActivityController.finishActivityOutOfMainActivity();
                    }

                }
                break;
            case BusEvent.GO_HOME_PAGE:
                finish();
                break;
        }
    }
   /* //根据ratalist和qty设置费率
    private void setServiceFeeRate(){
        List<CsParcel.RateList> lists=mResponse.getRateListList();
        mServiceFeeRate=mResponse.getServiceFee();
        if(lists==null||lists.size()==0){
            return;
        }else {
            int qtyTemp=Integer.MAX_VALUE;
            int qtyResult=getQty();
            if(qtyResult<=0){
                return;
            }
            for(CsParcel.RateList list:lists){
                int temp=qtyResult-list.getQty();
                if(temp<=0){
                    continue;
                }else {
                    if(temp<qtyTemp){
                        qtyTemp=temp;
                        mServiceFeeRate=list.getServiceRate();
                    }
                }
            }
            KLog.i("qty = "+qtyTemp+" rate = "+mServiceFeeRate);
        }

    }

    private int getQty(){
        if(pickList==null||pickList.size()==0){
            return -1;
        }else {
            int result=0;
            for(PickUpItemBean bean:pickList){
                result=result+Integer.valueOf(bean.getItemCount());
            }
            KLog.i("count = "+result);
            return result;
        }
    }*/

    private void showList(){
        List<PickUpItemBean> itemList = (List<PickUpItemBean>) SPUtils.readObject(UIUtils.getContext(), EditReplenishActivity.CODE_PICK_LIST);
        KLog.i("bean = "+EditReplenishActivity.CODE_PICK_LIST);
        if(itemList!=null){
            for(PickUpItemBean bean:itemList){
                KLog.i("bean = "+bean.toString());
            }
        }
    }
}