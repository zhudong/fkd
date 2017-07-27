package com.fuexpress.kr.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.AdyenUserInfoBean;
import com.fuexpress.kr.bean.HelpItemViewBean;
import com.fuexpress.kr.bean.PayResultBaen;
import com.fuexpress.kr.bean.PickUpItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ActivityController;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.ui.activity.help_send.HelpSendPresent;
import com.fuexpress.kr.ui.adapter.AdapterForHelp;
import com.fuexpress.kr.ui.adapter.PickItemAdapter;
import com.fuexpress.kr.ui.uiutils.AnimationUtils;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomGridView;
import com.fuexpress.kr.ui.view.CustomListView;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.ui.view.imageselector.ImageSelectorActivity;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.RateUtil;
import com.fuexpress.kr.utils.SPUtils;
import com.fuexpress.kr.utils.UpLoadImageManager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.GeneratedMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socks.library.KLog;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsParcel;
import okhttp3.Response;

import static com.fuexpress.kr.base.SysApplication.mContext;
import static com.fuexpress.kr.conf.SqlParcelsHelper.Parcel.currencyCode;

/**
 * Created by Administrator on 2016-12-5.
 */

public class PickUpActivity extends BaseActivity {

    private static final int CODE_PAYMENT_REQUEST = 0x1001;
    private static final int CODE_EDIT_REQUEST = 0x1002;
    public static final String CODE_CITY_CURRENCY = "city_currency_code";

    public static final String CODE_PICK_LIST_FINAL = "code_pick_list_final";
    public static final String CODE_PRODUCT_LIST_FINAL = "code_product_list_final";

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
    //    private TextView waringTv;
    private Button submitBtn;
    private TextView paymentTv;
    private LinearLayout rateLayout;
    private TextView rateTv;
    private TextView rateBackTv;

    private CsParcel.HelpMyGetInitResponse response;
    private List<PickUpItemBean> pickUpItemlist = new ArrayList<PickUpItemBean>();
    private int payType = Constants.PAYMENT_ALIPAY;
    private String currencyCode;
    private String payCode;
    private float balance;

    private Button addBtn;
    private ImageView helpIv;
    private List<CsParcel.ProductDataList> productList;
    private PickItemAdapter pickItemAdapter;
    private List<PickUpItemBean> pickList;
    private SweetAlertDialog mSweetDialog;
    private boolean isUseBalance = false;

    //需求单号
    private String salesRequireManId;
    private int salesrequireid = -1;

    static String SHOW_TIPS = "show_pick_tip";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean show_pick_tip = (boolean) SPUtils.get(this, SHOW_TIPS, true);
        if (show_pick_tip) {
            setShowTips();
        }
        mSweetDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initTip(View tipsView) {
        TextView child1 = (TextView) tipsView.findViewById(R.id.child_1);
        String string = SPUtils.getString(this, ChooseCityActivity.F_RECENT_CITY_ID, "");
        if (string.contains("2103")) {
            child1.setText(getString(R.string.string_replenish_tip_2103));
        } else {
            child1.setText(getString(R.string.string_replenish_tip_1130));
        }
    }

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_pick_up, null);
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
//        waringTv = (TextView) rootView.findViewById(R.id.pick_up_service_waring_tv);
        grandTotalTv = (TextView) rootView.findViewById(R.id.pick_up_grand_total_tv);
        submitBtn = (Button) rootView.findViewById(R.id.pick_up_submit_btn);
        addBtn = (Button) rootView.findViewById(R.id.pick_up_add_btn);
        rateLayout = (LinearLayout) rootView.findViewById(R.id.pick_up_rate_layout);
        rateTv = (TextView) rootView.findViewById(R.id.pick_up_rate_msg_one);
        rateBackTv = (TextView) rootView.findViewById(R.id.pick_up_rate_msg_two);
        helpIv = (ImageView) rootView.findViewById(R.id.pick_up_help_iv);
        salesrequireid = getIntent().getIntExtra("salesrequireid", -1);
        isUseBalance = (boolean) SPUtils.get(this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.IS_USE_BALANCE, false);

//        init();
        ActivityController.addActivity(this);

        backIv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        paymentLayout.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        helpIv.setOnClickListener(this);
        return rootView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
//        if (pickList.size() > 0) {
//            if (pickList.size() >= 4) {
//                addBtn.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
//            } else {
//            emptyLayout.setVisibility(View.GONE);
//            if (response != null) {
//                updateServiceFeeLayout(productList);
//            }
        pickItemAdapter.notifyDataSetChanged();
//            }
//        } else {
//            emptyLayout.setVisibility(View.VISIBLE);
//        }
    }

    public void init() {
        productList = new ArrayList<CsParcel.ProductDataList>();
        pickList = new ArrayList<PickUpItemBean>();
        reSetCityKey(SPUtils.getString(PickUpActivity.this, ChooseCityActivity.F_RECENT_CITY, ""));
        List<PickUpItemBean> itemList = (List<PickUpItemBean>) SPUtils.readObject(this, AddRequireActivity.CODE_PICK_LIST);
        List<CsParcel.ProductDataList> pList = (List<CsParcel.ProductDataList>) SPUtils.readObject(this, AddRequireActivity.CODE_PRODUCT_LIST);

        if (itemList != null) {
            pickList.addAll(itemList);
        }

        if (pList != null) {
            productList.addAll(pList);
        }
        if (pickList.size() == 0) {
            serviceFeeLayout.removeAllViews();
            grandTotalTv.setText("");
            emptyLayout.setVisibility(View.VISIBLE);
            submitBtn.setEnabled(false);
            submitBtn.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
        } else {
            emptyLayout.setVisibility(View.GONE);
            submitBtn.setEnabled(true);
            submitBtn.setBackgroundColor(getResources().getColor(R.color.title_bar_black));
        }
        pickItemAdapter = new PickItemAdapter(this, pickList, true, response != null ? response.getCityCurrencyCode() : SPUtils.getString(this, CODE_CITY_CURRENCY, ""));
        pickListView.setAdapter(pickItemAdapter);
        pickListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CsParcel.ProductDataList item = (CsParcel.ProductDataList) parent.getAdapter().getItem(position);
                Intent intent = new Intent(PickUpActivity.this, EditRequireActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(AddRequireActivity.CODE_PICK_LIST, (Serializable) pickList.get(position).getPathList());
                bundle.putSerializable(EditRequireActivity.CODE_PICK_ITEM, (Serializable) pickList.get(position));
//                bundle.putSerializable(EditRequireActivity.CODE_PRODUCT_ITEM, item);
                intent.putExtra(PaymentActivity.CURRENCY_CODE, currencyCode);
                intent.putExtra(EditRequireActivity.CODE_POSITION, position);
                intent.putExtra(CODE_CITY_CURRENCY, response.getCityCurrencyCode());
                intent.putExtras(bundle);
                startActivityForResult(intent, CODE_EDIT_REQUEST);
            }
        });
        helpMyGetInitRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                if (salesrequireid != -1) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                } else {
                    finish();
                }
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
                    CustomToast.makeText(this, getString(R.string.pick_submit_error_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (payCode == null) {
                    CustomToast.makeText(this, getString(R.string.pick_up_choose_payment_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                saveHelpMyGetRequest(payCode, productList.size() + "", productList);
                break;
            case R.id.pick_up_add_btn:
                if (pickList.size() >= 4) {
                    addBtn.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
                    CustomToast.makeText(this, getString(R.string.pick_up_add_item_max_msg), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intent1 = new Intent(this, AddRequireActivity.class);
                    intent1.putExtra(AddRequireActivity.CODE_ITEM_COUNT, pickList.size());
                    intent1.putExtra(PaymentActivity.CURRENCY_CODE, currencyCode);
                    intent1.putExtra(CODE_CITY_CURRENCY, response.getCityCurrencyCode());
                    startActivityForResult(intent1, 100);
                }
//                toActivity(AddRequireActivity.class);
                break;
            case R.id.pick_up_help_iv:
                setShowTips();
                break;
        }
    }

    public void setShowTips(){
        final FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        View inflate = View.inflate(this, R.layout.replenish_tips, null);
        initTip(inflate);
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.put(PickUpActivity.this, SHOW_TIPS, false);
                decorView.removeView(v);
            }
        });
        decorView.addView(inflate);
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        if (event.getType() == BusEvent.GO_PICK_UP) {

        }
        if (event.getType() == BusEvent.PAY_MENT_RESULT && event.getStrParam().equals(PickUpActivity.class.getSimpleName())) {
            SysApplication.mCurrentRequestPayment = "";
            if (event.getBooleanParam()) {
                Intent intent = new Intent(PickUpActivity.this, PickUpSuccessActivity.class);
                startActivityForResult(intent, 4129);
            } else {
                CustomToast.makeText(PickUpActivity.this, "pay error", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new BusEvent(BusEvent.GO_MY_NEED, false));
                ActivityController.finishActivityOutOfMainActivity();
            }

        }

    }

    public void checkDate() {
        if (pickList.size() > 0) {
            submitBtn.setEnabled(true);
            submitBtn.setBackgroundColor(getResources().getColor(R.color.title_bar_black));
            if (pickList.size() >= 4) {
                addBtn.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
            } else {
                addBtn.setBackground(getResources().getDrawable(R.drawable.btn_bg));
                emptyLayout.setVisibility(View.GONE);
                if (response != null) {
                }
                pickItemAdapter.notifyDataSetChanged();
            }
            updateServiceFeeLayout(productList);
        } else {
            pickItemAdapter.notifyDataSetChanged();
            submitBtn.setEnabled(false);
            submitBtn.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.d("----requestCode " + requestCode);
        if (resultCode == Constants.PAYMENT_REQUEST_CODE) {
            String payString = SPUtils.get(PickUpActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_NAME, "").toString();
            payCode = SPUtils.get(PickUpActivity.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_CODE, "").toString();
            isUseBalance = data.getBooleanExtra(PaymentActivity.IS_USE_BALANCE, false);
            balance = data.getFloatExtra("account", 0.0f);
            if (isUseBalance) {
                if (balance <= 0) {
                    balanceTv.setVisibility(View.GONE);
                } else {
                    balanceTv.setVisibility(View.VISIBLE);
                    balanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(PickUpActivity.this, balance));
                }
                float price = grandTotal - balance;
                paymentTv.setText(getString(R.string.pick_up_payment_balance2, payString) + UIUtils.getCurrency(PickUpActivity.this, AccountManager.getInstance().getCurrencyCode(), price));
                if (price <= 0) {
                    paymentTv.setVisibility(View.GONE);
                } else {
                    paymentTv.setVisibility(View.VISIBLE);
                }
            } else {
                float price = grandTotal;
                paymentTv.setText(getString(R.string.pick_up_payment_balance2, payString) + UIUtils.getCurrency(PickUpActivity.this, AccountManager.getInstance().getCurrencyCode(), price));
                if (price <= 0) {
                    paymentTv.setVisibility(View.GONE);
                } else {
                    paymentTv.setVisibility(View.VISIBLE);
                }
            }

          /*  NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(0);
            nf.setRoundingMode(RoundingMode.CEILING);
            nf.setGroupingUsed(true);*/

        }
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            List<CsParcel.ProductDataList> list = (List<CsParcel.ProductDataList>) bundle.getSerializable(AddRequireActivity.CODE_PRODUCT_LIST);
            List<PickUpItemBean> pickItem = (List<PickUpItemBean>) bundle.getSerializable(AddRequireActivity.CODE_PICK_LIST);
//            pickList.clear();
            pickList.addAll(pickItem);
//            productList.clear();
            productList.addAll(list);
            SPUtils.saveObject(this, AddRequireActivity.CODE_PICK_LIST, pickList);
            SPUtils.saveObject(this, AddRequireActivity.CODE_PRODUCT_LIST, productList);
            pickItemAdapter.notifyDataSetChanged();
            checkDate();
        }
        if (resultCode == EditRequireActivity.CODE_RESULT_EDIT) {
            Bundle bundle = data.getExtras();
            List<CsParcel.ProductDataList> list = (List<CsParcel.ProductDataList>) bundle.getSerializable(AddRequireActivity.CODE_PRODUCT_LIST);
            List<PickUpItemBean> pickItem = (List<PickUpItemBean>) bundle.getSerializable(AddRequireActivity.CODE_PICK_LIST);
            int position = data.getIntExtra(EditRequireActivity.CODE_POSITION, -1);
            if (position > -1) {
                pickList.remove(position);
                pickList.addAll(pickItem);
                productList.remove(position);
                productList.addAll(list);
                SPUtils.saveObject(this, AddRequireActivity.CODE_PICK_LIST, pickList);
                SPUtils.saveObject(this, AddRequireActivity.CODE_PRODUCT_LIST, productList);
                pickItemAdapter.notifyDataSetChanged();
                checkDate();
            }
        }
        if (resultCode == EditRequireActivity.CODE_RESULT_DELETE) {
            boolean isDeleted = data.getBooleanExtra(EditRequireActivity.CODE_IS_DELETE_ITEM, false);
            if (isDeleted) {
                int position = data.getIntExtra(EditRequireActivity.CODE_POSITION, -1);
                pickList.remove(position);
                if (productList.size() > 0) {
                    productList.remove(position);
                }
                pickItemAdapter.notifyDataSetChanged();
                checkDate();
            }
            SPUtils.saveObject(this, AddRequireActivity.CODE_PICK_LIST, pickList);
            SPUtils.saveObject(this, AddRequireActivity.CODE_PRODUCT_LIST, productList);
        }
        if (requestCode == 4129) {
            init();
            checkDate();
        }
    }

    private void setEmptyData() {
        serviceFeeLayout.removeAllViews();
        emptyLayout.setVisibility(View.VISIBLE);
        TextView feeTv = new TextView(PickUpActivity.this);
        feeTv.setTextColor(getResources().getColor(R.color.gray_999));
        feeTv.setTextSize(13);
        feeTv.setGravity(Gravity.RIGHT);

        if (response.getRateType() == 0) {
            feeTv.setText(getString(R.string.pick_up_services_waring_msg2, UIUtils.getCurrency(this, response.getCurrencyCode(), Float.valueOf(response.getServiceFee() + ""))));
        } else {
            float i = 0;
            try {
                if (response.getRateListList() != null && response.getRateListList().size() > 0) {
//                    feeTv.setVisibility(View.VISIBLE);
                    i = (float) response.getRateList(0).getServiceRate();
                } else {
//                    feeTv.setVisibility(View.GONE);
                    i = (float) response.getServiceFee();
                }
                feeTv.setText(getString(R.string.pick_up_services_waring_msg2, i + ""));
            } catch (Exception e) {

            }
//            feeTv.setText(getString(R.string.pick_up_services_waring_msg2, i + ""));
        }
        serviceFeeLayout.addView(feeTv);
        if (isUseBalance) {
            balanceTv.setVisibility(View.VISIBLE);
            balanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(PickUpActivity.this, (float) response.getGiftCardAccount()));
        } else {
            balanceTv.setVisibility(View.GONE);
        }
        grandTotalTv.setText(UIUtils.getCurrency(PickUpActivity.this, 0.00f));
    }

    private boolean checkecPorductListImgs(List<CsParcel.ProductDataList> productList) {
        for (int i = 0; i < productList.size(); i++) {
            for (int j = 0; j < productList.get(i).getExtraList().size(); j++) {
                if (TextUtils.isEmpty(productList.get(i).getExtraList().get(j).getImageUrl())) {
                    CustomToast.makeText(PickUpActivity.this, getString(R.string.pick_up_up_image_error, i, j), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    public void saveHelpMyGetRequest(String payCode, String sumcount, List<CsParcel.ProductDataList> productList) {

        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.pick_up_submitting));
        CsParcel.SaveHelpMyGetRequest.Builder builder = CsParcel.SaveHelpMyGetRequest.newBuilder();
        builder.setUserhead(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        //是否使用余额 1代表使用 2代表不使用
        builder.setUseaccountpay(isUseBalance ? 1 : 2);
        builder.setPaymentcode(payCode);
        builder.setSumcount(sumcount);
        if (checkecPorductListImgs(productList)) {

            builder.addAllProductlist(productList);
            NetEngine.postRequest(builder, new INetEngineListener<CsParcel.SaveHelpMyGetResponse>() {

                @Override
                public void onSuccess(CsParcel.SaveHelpMyGetResponse response) {

                    LogUtils.d(response.toString());
                    Message msg = Message.obtain();
                    msg.obj = response;
                    saveHanlder.sendMessage(msg);
                }

                @Override
                public void onFailure(int ret, final String errMsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dissMissProgressDiaLog(500);
                            CustomToast.makeText(PickUpActivity.this, errMsg, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }

    }

    private void clean() {
//        dissMissProgressDiaLog(500);
        SPUtils.saveObject(PickUpActivity.this, AddRequireActivity.CODE_PICK_LIST, null);
        SPUtils.saveObject(PickUpActivity.this, AddRequireActivity.CODE_PRODUCT_LIST, null);
        addBtn.setEnabled(true);
    }

    private String total;
    private Handler saveHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//           清除数据
//            cnvf,nlean();

            CsParcel.SaveHelpMyGetResponse response = (CsParcel.SaveHelpMyGetResponse) msg.obj;
            String redirectUrl = response.getRedirecturl();
            if (TextUtils.isEmpty(redirectUrl)) {
                dissMissProgressDiaLog(500);

//                dissMissProgressDiaLog(500);
//                CustomToast.makeText(PickUpActivity.this, getString(R.string.pick_up_save_help_my_get_empty), Toast.LENGTH_SHORT).show();
                CustomToast.makeText(PickUpActivity.this, getString(R.string.pick_up_submit_success_msg), Toast.LENGTH_SHORT).show();
                SPUtils.saveObject(PickUpActivity.this, AddRequireActivity.CODE_PICK_LIST, null);
                SPUtils.saveObject(PickUpActivity.this, AddRequireActivity.CODE_PRODUCT_LIST, null);
                Intent intent = new Intent(PickUpActivity.this, PickUpSuccessActivity.class);
                startActivityForResult(intent, 4129);
                return;
            }
            String param[] = redirectUrl.split("\\?")[1].split("&");
            String type = param[1].split("=")[1];
            String appType = param[2].split("=")[1];
            String paymentCode = param[3].split("=")[1];
            salesRequireManId = param[4].split("=")[1];
            total = param[5].split("=")[1];

            doDirectGiftCardRequest(Integer.parseInt(type),
                    Integer.parseInt(appType),
                    paymentCode,
                    Float.parseFloat(total),
                    Integer.parseInt(salesRequireManId),
                    PickUpActivity.this.response.getCurrencyCode());

            if (TextUtils.equals(paymentCode, Constants.GIFT_CARD_PAYMENT_KRBANK)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissMissProgressDiaLog(500);
                    }
                });
                Intent intent = new Intent(PickUpActivity.this, PickUpKrbankInfoActivity.class);
                intent.putExtra(PickUpKrbankInfoActivity.CODE_SALES_REQUIRE_MAN_ID, salesRequireManId);
                intent.putExtra(PickUpKrbankInfoActivity.CODE_TOTAL, UIUtils.getCurrency(PickUpActivity.this, Float.parseFloat(total)));
                intent.putExtra(PickUpKrbankInfoActivity.CODE_PAY_TYPE, Integer.parseInt(type));
                String balanceString;
                String needString;
                if (balance > Float.parseFloat(total)) {
                    balanceString = total;
                    needString = "0.0";
                } else {
                    balanceString = balance + "";
                    needString = Float.parseFloat(total) - balance + "";
                }

                intent.putExtra(PickUpKrbankInfoActivity.CODE_BALANCE_PAY, UIUtils.getCurrency(PickUpActivity.this, Float.parseFloat(balanceString)));
                intent.putExtra(PickUpKrbankInfoActivity.CODE_NEED_PAY,
                        UIUtils.getCurrency(PickUpActivity.this, Float.parseFloat(needString)));
                startActivity(intent);
            }


//            CustomToast.makeText(mContext, getString(R.string.pick_up_add_save_success_msg), Toast.LENGTH_SHORT).show();
//            clearData();
//            mSweetAlertDialog.dismiss();
//            EventBus.getDefault().post(new BusEvent(BusEvent.CODE_GO_MERCHAND, true));
        }
    };

    public void doDirectGiftCardRequest(int type, int appType, String paymentCode, float total, int salesrequiremainid, String currencyCode) {
        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.pick_up_generating_order));
        CsParcel.DoDirectGiftCardRequest.Builder builder = CsParcel.DoDirectGiftCardRequest.newBuilder();
        builder.setType(type);
        builder.setAppType(appType);
        builder.setPaymentCode(paymentCode);
//        total = 0.01f;
        builder.setTotal(total);
        builder.setSalesrequiremainid(salesrequiremainid);
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(currencyCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.DoDirectGiftCardResponse>() {

            @Override
            public void onSuccess(CsParcel.DoDirectGiftCardResponse response) {
                clean();
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
            if(TextUtils.equals(Constants.GIFT_CARD_PAYMENT_DAOUPAY,paymentCode) || (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_ADYEN, paymentCode))){
                payWithAdyenOrDaouPay(paymentCode, response.getNumber(), giftCardOrderId, PickUpActivity.this.response.getCurrencyCode());
            }else {
                payGiftCardOrderRequest(response.getNumber(), giftCardOrderId, paymentCode, PickUpActivity.this.response.getCurrencyCode());
            }

        }
    };

    public void payGiftCardOrderRequest(final String orderNumber, final int giftCardOrderId, final String paymentCode, final String currencyCode) {
        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.pick_up_getting_pay_info));
        CsParcel.PayGiftCardOrderRequest.Builder builder = CsParcel.PayGiftCardOrderRequest.newBuilder();
        builder.setGiftCardOrderId(giftCardOrderId);
        builder.setPaymentCode(paymentCode);
        builder.setCurrencycode(currencyCode);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.PayGiftCardOrderResponse>() {

            @Override
            public void onSuccess(CsParcel.PayGiftCardOrderResponse response) {
                LogUtils.d(response.toString());
                SysApplication.mCurrentRequestPayment = PickUpActivity.class.getSimpleName();

                if (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_ALIPAY, paymentCode)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dissMissProgressDiaLog();
                        }
                    });
                    PaymentManager.getInstance(PickUpActivity.this).aliPay(response.getPayInfo(), new PaymentManager.OnAliPayListener() {
                        @Override
                        public void onSuccess(String resultStatus) {
                            SPUtils.saveObject(PickUpActivity.this, AddRequireActivity.CODE_PICK_LIST, null);
                            SPUtils.saveObject(PickUpActivity.this, AddRequireActivity.CODE_PRODUCT_LIST, null);
                            Intent intent = new Intent(PickUpActivity.this, PickUpSuccessActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, 4129);
                        }

                        @Override
                        public void onFailure(String resultStatus, final String errorMsg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToast.makeText(PickUpActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                if (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_WXPAY, paymentCode)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dissMissProgressDiaLog();
                        }
                    });
//                    SysApplication app = (SysApplication) getApplication();
//                    app.setPaymentRequestCode(Constants.PAYMENT_REQUEST_CODE_PICK_UP);
                    PaymentManager.getInstance(PickUpActivity.this).wechatPay(response.getPayInfo());
                }

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

    public void payWithAdyenOrDaouPay(String paymentCode, String orderNumber, int giftCardOrderId, String currencyCode) {
        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.pick_up_getting_pay_info));
        if (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_ADYEN, paymentCode)) {
            PaymentManager.getInstance(PickUpActivity.this).adyenPay(payString, orderNumber, giftCardOrderId, 1, Float.parseFloat(total), currencyCode, new PaymentManager.OnPayListener() {
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
                    if (!TextUtils.isEmpty(authCode)) {
                        Intent intent = new Intent(PickUpActivity.this, PickUpSuccessActivity.class);
                        startActivityForResult(intent, 4129);

                    }else {
                        EventBus.getDefault().post(new BusEvent(BusEvent.PAY_MENT_RESULT, false, SysApplication.mCurrentRequestPayment));
                    }
                }

                @Override
                public void onError(final String errMsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clean();
                            CustomToast.makeText(PickUpActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                            dissMissProgressDiaLog();
                            EventBus.getDefault().post(new BusEvent(BusEvent.PAY_MENT_RESULT, false, SysApplication.mCurrentRequestPayment));
                        }
                    });
                }
            });
        }
        if (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_KRBANK, paymentCode)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dissMissProgressDiaLog();
                }
            });
        }
        if (TextUtils.equals(Constants.GIFT_CARD_PAYMENT_DAOUPAY, paymentCode)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dissMissProgressDiaLog();
                }
            });
            DaoUPayActivity.IntentBuilder intentBuilder = DaoUPayActivity.IntentBuilder.build(PickUpActivity.this)
                    .setAmount(Integer.parseInt(total))
                    .setOrderID(giftCardOrderId)
                    .setOrderType(1)
                    .setListener(new OperaRequestListener() {
                        @Override
                        public void onOperaSuccess() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    closeLoading();
                                    dissMissProgressDiaLog(500);
                                }
                            });
                            Intent intent = new Intent(PickUpActivity.this, PickUpSuccessActivity.class);
                            startActivityForResult(intent, 4129);
                        }

                        @Override
                        public void onOperaFailure() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PickUpActivity.this, getString(R.string.pay_error_msg), Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().post(new BusEvent(BusEvent.PAY_MENT_RESULT, false, SysApplication.mCurrentRequestPayment));
                                }
                            });
                        }
                    });
            startActivity(intentBuilder);
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
        if (!mSweetDialog.isShowing()) {
            mSweetDialog.show();
        }
    }

    public void dissMissProgressDiaLog() {
        //mSweetDialog.setTitleTextViewGone();
        mSweetDialog.dismiss();
    }

    public void dissMissProgressDiaLog(long delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSweetDialog != null) {
                    //mSweetDialog.setTitleTextViewGone();
                    mSweetDialog.dismiss();
                }
            }
        }, delay);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeLoading();
        dissMissProgressDiaLog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            closeLoading();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void reSetCityKey(String cityCode) {
        AddRequireActivity.CODE_PICK_LIST = CODE_PICK_LIST_FINAL + cityCode;
        AddRequireActivity.CODE_PRODUCT_LIST = CODE_PRODUCT_LIST_FINAL + cityCode;
    }

    public void helpMyGetInitRequest() {
        showLoading();
        CsParcel.HelpMyGetInitRequest.Builder builder = CsParcel.HelpMyGetInitRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencyCode(AccountManager.getInstance().getCurrencyCode());
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.HelpMyGetInitResponse>() {

            @Override
            public void onSuccess(CsParcel.HelpMyGetInitResponse response) {
                closeLoading();
                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                initHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                KLog.i("ret = " + ret + " errmsg = " + errMsg);
                closeLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.makeText(PickUpActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                        showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.pick_up_init_error_msg));
                        dissMissProgressDiaLog(1000);
                        addBtn.setEnabled(false);
                        paymentLayout.setEnabled(false);
                        submitBtn.setEnabled(false);
                    }
                });
            }
        });
    }

    private String payString;
    private float grandTotal;
    private Handler initHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            response = (CsParcel.HelpMyGetInitResponse) msg.obj;
            checkDate();
            balance = (float) response.getGiftCardAccount();
//            updateServiceFeeLayout(productList);
            currencyCode = response.getCurrencyCode();
            SPUtils.putString(PickUpActivity.this, PickUpActivity.CODE_CITY_CURRENCY, response.getCityCurrencyCode());
            PaymentManager.getInstance(PickUpActivity.this).getFKDPaymentListRequest(response.getCurrencyCode(), new PaymentManager.OnFKDPaymentListener() {
                @Override
                public void onResult(final int paymentType, final String pString, String pCode) {
                    payCode = pCode;
                    payString = pString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            paymentTv.setText(pString);
                            if (pickList.size() <= 0) {
                                paymentTv.setText(payString);
                                paymentTv.setVisibility(View.VISIBLE);
                                setEmptyData();
                            } else {
                                paymentTv.setVisibility(View.VISIBLE);
                                float price;
                                if (isUseBalance) {
                                    price = grandTotal - balance;
                                } else {
                                    price = grandTotal;
                                }
                                paymentTv.setText(getString(R.string.pick_up_payment_balance2, payString) + UIUtils.getCurrency(PickUpActivity.this, AccountManager.getInstance().getCurrencyCode(), price));
                                if (price <= 0) {
                                    paymentTv.setVisibility(View.GONE);
                                } else {
                                    paymentTv.setVisibility(View.VISIBLE);
                                }
                            }
                            if (isUseBalance) {
                                if (response.getGiftCardAccount() > 0) {
//                                balanceTv.setVisibility(View.GONE);
                              /*  NumberFormat nf = NumberFormat.getInstance();
                                nf.setMaximumFractionDigits(0);
                                nf.setRoundingMode(RoundingMode.CEILING);
                                nf.setGroupingUsed(true);*/
                                    balanceTv.setVisibility(View.VISIBLE);
//                                paymentTv.setVisibility(View.GONE);
                                    balanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(PickUpActivity.this, (float) response.getGiftCardAccount()));
                                } else {
                                    balanceTv.setVisibility(View.GONE);
//                                paymentTv.setVisibility(View.VISIBLE);
                                    balanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(PickUpActivity.this, (float) response.getGiftCardAccount()));
                                }
                            }
                            /*if (response != null) {
                                if (productList == null || productList.size() == 0) {
                                    setEmptyData();
                                } else {
                                    updateServiceFeeLayout(productList);
                                }
                            }*/
                        }
                    });
                }
            });
            if (!currencyCode.equals(response.getCityCurrencyCode())) {
                rateTv.setText(getString(R.string.pick_up_rate_msg, response.getCityCurrencyCode(), response.getStrExchangeRate(), response.getCurrencyCode()));
                rateBackTv.setText(getString(R.string.pick_up_rate_back_msg, response.getCurrencyCode(), response.getStrExchangeRateBack(), response.getCityCurrencyCode()));
//                rateTv.setText(RateUtil.getCurrency(response.getCityCurrencyCode(),response.getCurrencyCode(), response.getExchangeRate()));
//                rateBackTv.setText(RateUtil.getCurrency(response.getCurrencyCode(),response.getCityCurrencyCode(), response.getExchangeRateBack()));
                rateLayout.setVisibility(View.VISIBLE);
            } else {
                rateLayout.setVisibility(View.GONE);
            }
//            if (response.getGiftCardAccount() <= 0) {
////                balanceTv.setVisibility(View.GONE);
//                balanceTv.setText(getString(R.string.pick_up_payment_balance2, paymentTv.getText().toString()) + UIUtils.getCurrency(PickUpActivity.this, (float) response.getGiftCardAccount()));
//            } else {
////                balanceTv.setVisibility(View.VISIBLE);
//                balanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(PickUpActivity.this, (float) response.getGiftCardAccount()));
//            }
//            itemAdapter = new ItemAdapter(PickUpActivity.this, itemlist, response.getCurrencyCode());
//            itemAdapter.notifyDataSetChanged();
        }
    };

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
        /*List<CsParcel.RateList> rateLists = response.getRateListList();

        for (int i = 0; i < rateLists.size() - 1; i++) {    //最多做n-1趟排序
            for (int j = 0; j < rateLists.size() - i - 1; j++) {    //对当前无序区间score[0......length-i-1]进行排序(j的范围很关键，这个范围是在逐步缩小的)
                if (rateLists.get(j).getQty() < rateLists.get(j + 1).getQty()) {    //把小的值交换到后面
                    CsParcel.RateList rate = rateLists.get(j);
                    rateLists.set(j, rateLists.get(j + 1));
                    rateLists.set(j + 1, rate);
//                                        int temp = res[j];
//                                        res[j] = res[j + 1];
//                                        res[j + 1] = temp;
                }
            }
        }
        for (int a = 0; a < rateLists.size(); a++) {
            if (count >= rateLists.get(a).getQty()) {
                return rateLists.get(a);
            }
        }
        return null;*/
    }

    public void updateServiceFeeLayout(List<CsParcel.ProductDataList> productList) {
        serviceFeeLayout.removeAllViews();
        double feeRate = 0.0f;
        grandTotal = 0;
        for (CsParcel.ProductDataList item : productList) {
            if (response != null) {
                float fee = 0;
                String feeMsg = "";
                String feeHintMsg = "";
                float price = Float.parseFloat(item.getPrice());
                int count = Integer.parseInt(item.getNum());
                if (response.getFree() == 1) {
                    fee = 0;
                    serviceFeeLayout.removeAllViews();
                    TextView feeTv = new TextView(PickUpActivity.this);
                    feeTv.setTextColor(getResources().getColor(R.color.black_0));
                    feeTv.setTextSize(13);
                    feeTv.setText(getString(R.string.pick_up_services_waring_msg3));
                    feeTv.setGravity(Gravity.RIGHT);
                    serviceFeeLayout.addView(feeTv);
                } else {
                    if (response.getRateType() == 0) {
                        List<CsParcel.RateList> rateList = response.getRateListList();
                        if (rateList != null && rateList.size() > 0) {
                            CsParcel.RateList rate;
                            if (rateList.size() > 1) {
                                rate = getRate(count);
                            } else {
                                rate = rateList.get(0);
                            }
                            if (count >= rate.getQty()) {
                                fee = (float) rate.getServiceRate() * count;
                                feeMsg = UIUtils.getCurrency(PickUpActivity.this, response.getCurrencyCode(), (float) rate.getServiceRate())
                                        + "x"
                                        + count
                                        + "="
                                        + UIUtils.getCurrency(this, response.getCurrencyCode(), (float) (rate.getServiceRate() * count));
                            } else {
                                fee = (float) response.getServiceFee() * count;
                                feeMsg = UIUtils.getCurrency(PickUpActivity.this, response.getCurrencyCode(), (float) response.getServiceFee())
                                        + "x"
                                        + count
                                        + "="
                                        + UIUtils.getCurrency(this, response.getCurrencyCode(), (float) (response.getServiceFee() * count));
                            }
                            feeHintMsg = getString(R.string.pick_up_services_waring_msg2_item, UIUtils.getCurrency(this, response.getCurrencyCode(), fee));
                        }
                    } else {
                        List<CsParcel.RateList> rateLists = response.getRateListList();
                        if (rateLists != null && rateLists.size() > 0) {
                            CsParcel.RateList rate;
                            if (rateLists.size() > 1) {
                                rate = getRate(count);
                            } else {
                                rate = rateLists.get(0);
                            }
                            if (count >= rate.getQty()) {
                                fee = (float) (price * count * (rate.getServiceRate() / 100) / response.getExchangeRateBack());
                                feeRate = rate.getServiceRate();
                            } else {
                                fee = (float) (price * count * (response.getServiceFee() / 100) / response.getExchangeRateBack());
                                feeRate = response.getServiceFee();
                            }
                            LogUtils.d("--------------" + rate.getServiceRate());
                        } else {
                            fee = (float) (price * count * (response.getServiceFee() / 100) / response.getExchangeRateBack());
                            feeRate = (float) response.getServiceFee();
                        }

                        double exchangeRate = response.getExchangeRate();
                        BigDecimal bigDecimal = new BigDecimal(4);
                        bigDecimal.floatValue();
                        feeMsg = UIUtils.getCurrency(PickUpActivity.this, response.getCurrencyCode(), Float.valueOf(price * response.getExchangeRate() + ""))
                                + "x"
                                + count
                                + "x"
                                + feeRate
                                + "%="
                                + UIUtils.getCurrency(this, response.getCurrencyCode(), Float.valueOf(price * count * feeRate / 100 * response.getExchangeRate() + ""));
                        feeHintMsg = getString(R.string.pick_up_services_waring_msg2, feeRate + "");
                    }
                    TextView feeTv = new TextView(PickUpActivity.this);
                    feeTv.setTextColor(getResources().getColor(R.color.black_0));
                    feeTv.setTextSize(13);
                    feeTv.setText(feeMsg);
                    feeTv.setGravity(Gravity.RIGHT);
                    serviceFeeLayout.addView(feeTv);
                    TextView feeHintTv = new TextView(PickUpActivity.this);
                    feeHintTv.setTextColor(getResources().getColor(R.color.gray_999));
                    feeHintTv.setTextSize(13);
                    feeHintTv.setText(feeHintMsg);
                    feeHintTv.setGravity(Gravity.RIGHT);
                    serviceFeeLayout.addView(feeHintTv);
                }


//                float f = price * count;
//                float f2 = f * feeRate / 100;


//                waringTv.setText(getString(R.string.pick_up_services_waring_msg, feeRate));
//                grandTotal += price * count + fee;
                grandTotal += fee;


            }
            float ta = (float) (grandTotal * response.getExchangeRate());
            grandTotalTv.setText(UIUtils.getCurrency(this, grandTotal));
            if (!isUseBalance || response.getGiftCardAccount() <= 0) {
                balanceTv.setVisibility(View.GONE);
              /*  NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(0);
                nf.setRoundingMode(RoundingMode.CEILING);
                nf.setGroupingUsed(true);*/
//                float v = (float) (grandTotal - response.getGiftCardAccount());
                paymentTv.setVisibility(View.VISIBLE);
                paymentTv.setText(getString(R.string.pick_up_payment_balance2, payString) + UIUtils.getCurrency(this, AccountManager.getInstance().getCurrencyCode(), grandTotal));
//                if (v <= 0) {
//                    paymentTv.setVisibility(View.GONE);
//                } else {
//                }
            } else {
                balanceTv.setVisibility(View.VISIBLE);
                balanceTv.setText(getString(R.string.pick_up_payment_balance) + UIUtils.getCurrency(PickUpActivity.this, (float) response.getGiftCardAccount()));
            }

        }
    }


    int itemId = -1;

    public CsParcel.RateList getTyep1ServiceRate(int count) {
        if (response == null || count < 0 || response.getRateListList() == null) {
            return null;
        } else {
            CsParcel.RateList list = null;
            int qtyResult = Integer.MAX_VALUE;
            for (CsParcel.RateList rateList : response.getRateListList()) {
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

    public void requestFocus(EditText editText) {
        editText.requestFocus();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("PickUp Page") // TODO: Define a title for the content shown.
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
}