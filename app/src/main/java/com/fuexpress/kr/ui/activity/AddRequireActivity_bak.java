package com.fuexpress.kr.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.HelpItemViewBean;
import com.fuexpress.kr.bean.PickUpItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.AnimationUtils;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomGridView;
import com.fuexpress.kr.ui.view.CustomListView;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.ui.view.imageselector.ImageSelectorActivity;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;
import com.fuexpress.kr.utils.UpLoadImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsParcel;

import static com.fuexpress.kr.base.SysApplication.mContext;

/**
 * Created by Administrator on 2016-12-5.
 */

public class AddRequireActivity_bak extends BaseActivity {

    private static final int CODE_PAYMENT_REQUEST = 0x1001;
    public static final String CODE_PRODUCT_LIST = "productList";

    private View rootView;
    //    private View itemView;
//    private GridView pickItemGv;
//    private LinearLayout commoditysLayout;
//    private LinearLayout paymentLayout;
    private MyAdapter adapter;
    private LinearLayout addLayout;
    private TitleBarView titleBarView;
    private TextView backTv;
    private TextView rightTv;
    private ImageView backIv;
    private CustomListView pickListView;
    private List<PickUpItemBean> itemlist;
    private ItemAdapter itemAdapter;
    private ScrollView pickUpSv;
    private ArrayList<String> imgList;
    private List<String> mPathList;
    private String mImage;
    private int itemPos;

    private TextView balanceTv;
    private TextView grandTotalTv;
    private CsParcel.HelpMyGetInitResponse response;
    //    private List<String> serviceFeeList = new ArrayList<String>();
    private List<PickUpItemBean> pickUpItemlist = new ArrayList<PickUpItemBean>();
    //    private Set<PickUpItemBean> serviceFeeSet = new HashSet<PickUpItemBean>();
//    private LinearLayout serviceFeeLayout;
//    private TextView waringTv;
//    private Button submitBtn;
    private boolean isChanged = false;
    private int payType = Constants.PAYMENT_ALIPAY;
    private TextView paymentTv;
    private String currencyCode;
    private String payCode;
    private String paymentString;
    private int itemCount = 1;
    private List<String> mUpLoadCompletelist;
    private List<List<String>> mUpLoadlist;
    private boolean isClickSub = false;
    private Button confirmBtn;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_add_require, null);
//        itemView = LayoutInflater.from(this).inflate(R.layout.pick_up_item, null);
        titleBarView = (TitleBarView) rootView.findViewById(R.id.pick_up_titlebar);
        backTv = titleBarView.getmTv_in_title_back_tv();
        backTv.setText(getString(R.string.pick_up_title));
        rightTv = titleBarView.getTv_in_title_right();
        rightTv.setText(getString(R.string.pick_up_right_delete));
        rightTv.setVisibility(View.GONE);
        backIv = titleBarView.getIv_in_title_back();
//        paymentLayout = (LinearLayout) rootView.findViewById(R.id.pick_up_payment_layout);
        paymentTv = (TextView) rootView.findViewById(R.id.pick_up_payment_tv);
        addLayout = (LinearLayout) rootView.findViewById(R.id.add_require_add_layout);
        pickUpSv = (ScrollView) rootView.findViewById(R.id.pick_up_sv);
        pickListView = (CustomListView) rootView.findViewById(R.id.pick_up_listview);
        balanceTv = (TextView) rootView.findViewById(R.id.pick_up_balance_tv);
//        serviceFeeLayout = (LinearLayout) rootView.findViewById(R.id.pick_up_service_fee_layout);
//        waringTv = (TextView) rootView.findViewById(R.id.pick_up_service_waring_tv);
        grandTotalTv = (TextView) rootView.findViewById(R.id.pick_up_grand_total_tv);
        confirmBtn = (Button) rootView.findViewById(R.id.add_require_confirm_btn);
//        submitBtn = (Button) rootView.findViewById(R.id.pick_up_submit_btn);

        init();

        backIv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        rightTv.setOnClickListener(this);
        addLayout.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
//        paymentLayout.setOnClickListener(this);
//        submitBtn.setOnClickListener(this);
        return rootView;
    }


    public void init() {
        mUpLoadCompletelist = new ArrayList<>();
        mUpLoadlist = new ArrayList<List<String>>();
        itemlist = new ArrayList<PickUpItemBean>();
        PickUpItemBean bean = new PickUpItemBean();
        imgList = new ArrayList<String>();
        bean.setItemUrlList(imgList);
        itemlist.add(bean);
        itemAdapter = new ItemAdapter(AddRequireActivity_bak.this, itemlist, AccountManager.getInstance().getCurrencyCode());
        pickListView.setAdapter(itemAdapter);
//        helpMyGetInitRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.tv_in_title_right:
                itemAdapter.doDeletAnimation();
                break;
            case R.id.add_require_add_layout:
                itemCount++;
                if (itemCount > 4) {
                    CustomToast.makeText(this, getString(R.string.pick_up_add_item_max_msg), Toast.LENGTH_SHORT).show();
                    itemCount = 1;
                    return;
                }
                if (!checkItemStaus()) {
                    return;
                }
                PickUpItemBean bean = new PickUpItemBean();
                List<String> imgList = new ArrayList<String>();
                bean.setItemUrlList(imgList);
                itemlist.add(bean);
                itemAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(pickListView);
                scrollToBottom();
                break;
            case R.id.pick_up_payment_layout:
                Intent intent = new Intent(this, PaymentActivity.class);
                int i = (int) SPUtils.get(this, AccountManager.getInstance().getCurrencyCode() + "paymentPos", 0);
                intent.putExtra("paymentPos", i);
                intent.putExtra(PaymentActivity.CURRENCY_CODE, currencyCode);
                intent.putExtra("payType", payType);
                intent.putExtra("payCode", payCode);
                intent.putExtra("paymentString", paymentTv.getText().toString());
                startActivityForResult(intent, CODE_PAYMENT_REQUEST);
                break;
            case R.id.add_require_confirm_btn:
                if (!checkItemStaus()) {
                    return;
                }
                isClickSub = true;
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_UP_LOAD_IMAGE_LIST_PROGRESS, null));
                break;
//            case R.id.pick_up_submit_btn:
//                isClickSub = true;
//                EventBus.getDefault().post(new BusEvent(BusEvent.GET_UP_LOAD_IMAGE_LIST_PROGRESS, null));
//                break;
        }
    }

    public boolean checkItemStaus() {
        for (int i = 0; i < pickListView.getChildCount(); i++) {
            GridView view = (GridView) pickListView.getChildAt(i).findViewById(R.id.pick_up_item_gv);
            EditText noteEt = (EditText) pickListView.getChildAt(i).findViewById(R.id.pick_up_item_note_et);
            EditText addressEt = (EditText) pickListView.getChildAt(i).findViewById(R.id.pick_up_item_address_et);
            EditText priceEt = (EditText) pickListView.getChildAt(i).findViewById(R.id.pick_up_item_price_et);
            EditText countEt = (EditText) pickListView.getChildAt(i).findViewById(R.id.pick_up_item_count_et);

            if (view.getAdapter().getCount() <= 1) {
                CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_adapter_msg), Toast.LENGTH_SHORT).show();
                itemCount = 1;
                return false;
            }
            if (TextUtils.isEmpty(noteEt.getText().toString())) {
                CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_note_msg), Toast.LENGTH_SHORT).show();
                requestFocus(noteEt);
                itemCount = 1;
                return false;
            }
            if (TextUtils.isEmpty(addressEt.getText().toString())) {
                CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_address_msg), Toast.LENGTH_SHORT).show();
                requestFocus(addressEt);
                itemCount = 1;
                return false;
            }
            if (TextUtils.isEmpty(priceEt.getText().toString())) {
                CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_price_msg), Toast.LENGTH_SHORT).show();
                requestFocus(priceEt);
                itemCount = 1;
                return false;
            }
            if (TextUtils.isEmpty(countEt.getText().toString())) {
                CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_count_msg), Toast.LENGTH_SHORT).show();
                requestFocus(countEt);
                itemCount = 1;
                return false;
            }
            LogUtils.d("gridCount " + view.getAdapter().getCount());
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.d("----requestCode " + requestCode);
//        if (requestCode == Constants.ALBUM_GET_BITMAP_REQUEST_CODE) {
//            if (data != null) {
//                mImage = data.getStringExtra("image");
//                Uri uri = Uri.parse(mImage);
//                String path = uri.getPath();
//                //                try {
//                //                    Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri));
//                //                    bmpList.add(bitmap);
//                imgList.add(path);
//                adapter.notifyDataSetChanged();
//
//                //                } catch (FileNotFoundException e) {
//                //                    e.printStackTrace();
//                //                }
//            }
//        }
        if (resultCode == Constants.PAYMENT_REQUEST_CODE) {
            String payString = SPUtils.get(AddRequireActivity_bak.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_NAME, "").toString();
            payCode = SPUtils.get(AddRequireActivity_bak.this, AccountManager.getInstance().getCurrencyCode() + PaymentActivity.DEF_PAY_CODE, "").toString();
            paymentTv.setText(payString);
        }

        if (requestCode == PreviewMerchandiseActivity.REQUEST_CODE_BACK_IMG_LIST) {
            if (data != null) {
                List<String> backList = (List<String>) data.getExtras().getSerializable("backImgList");
//                imgList.clear();
//                imgList.addAll(backList);
                itemlist.get(itemPos).getItemUrlList().clear();
                itemlist.get(itemPos).getItemUrlList().addAll(backList);
                adapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 1000 || resultCode == -1 || resultCode == 0) {
            if (data != null) {
                mPathList = new ArrayList<>();
                mPathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                itemlist.get(itemPos).getItemUrlList().clear();
                itemlist.get(itemPos).getItemUrlList().addAll(mPathList);
                for (int i = 0; i < pickListView.getChildCount(); i++) {
                    GridView view = (GridView) pickListView.getChildAt(itemPos).findViewById(R.id.pick_up_item_gv);
                    view.setAdapter(new MyAdapter(this, itemlist.get(itemPos).getItemUrlList(), itemPos));
                    if (pickListView.getChildCount() > 1 && itemlist.get(itemPos).getItemUrlList().size() >= 4) {
                        setListViewHeightBasedOnChildren2(pickListView);
                    }
                }
//                adapter.notifyDataSetChanged();
                uploadImages();
                /*for (String path : mPathList) {
                    LogUtils.e("ImagePathList", path);//返回了一个图片路径的集合
                }*/
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadImages() {

        UpLoadImageManager.getInstance().zoomImageAndUpLoad(itemlist.get(itemPos).getItemUrlList(), this);
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        if (event.getType() == BusEvent.UP_LOAD_IMAGE_COMPLETE2) {
            if (event.getBooleanParam()) {
                mUpLoadCompletelist.clear();
                mUpLoadCompletelist.addAll((List<String>) event.getParam());
                mUpLoadlist.add((List<String>) event.getParam());
                if (isClickSub) {
                    upLoadInfo();
                }
            } else {
                int failIndex = event.getIntValue();
                //closeLoading();
                CustomToast.makeText(mContext, getString(R.string.upload_failed_msg), Toast.LENGTH_SHORT).show();
            }
        }
        if (event.getType() == BusEvent.RETURN_CURRENT_UP_LOAD_IMAGE_LIST_PROGRESS) {
            int intParam = event.getIntParam();
            if (intParam < 100) {
                EventBus.getDefault().post(new BusEvent(BusEvent.SHOW_UP_LOAD_IMAGE_LIST_PROGRESS, null));
            } else {
                upLoadInfo();
            }
        }
    }

    public void upLoadInfo() {
        //List<String> list = (List<String>) event.getParam();
        if (mUpLoadCompletelist == null) {

        } else {
            /*if (mSweetAlertDialog != null) {
                if (!mSweetAlertDialog.isShowing()) {
                    showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, "上传中");
                }
            } else {
                showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, "上传中");
            }*/
            List<Map<String, String>> mapList = new ArrayList<>();
            for (int h = 0; h < mUpLoadlist.size(); h++) {
                Map<String, String> map = new HashMap<String, String>();

                for (int i = 0; i < mUpLoadlist.get(h).size(); i++) {
                    String[] str = mUpLoadlist.get(h).get(i).split(",");
                    map.put(str[1], str[0]);
                    mapList.add(map);
                }
            }

            CsBase.ItemImage.Builder imgBuilder = CsBase.ItemImage.newBuilder();
            List<CsParcel.ProductDataList> productList = new ArrayList<>();
            for (int j = 0; j < pickUpItemlist.size(); j++) {
                CsParcel.ProductDataList.Builder builder = CsParcel.ProductDataList.newBuilder();
                PickUpItemBean item = pickUpItemlist.get(j);
                builder.setProductdescription(item.getItemNote());
                builder.setAddressinfo(item.getItemAddress());
                builder.setPrice(item.getItemPrice());
                builder.setNum(item.getItemCount());
                builder.setImageNum(item.getItemUrlList().size());
                for (int k = 0; k < mUpLoadlist.get(j).size(); k++) {
//                        imgBuilder.setImageUrl(map.get(item.getItemUrlList().get(k)));
                    imgBuilder.setImageUrl(mapList.get(k).get(item.getItemUrlList().get(j)));
                    builder.addExtra(imgBuilder);
                }
//                    imgBuilder.setImageUrl(item.getItemUrlList().get(k));
                productList.add(builder.build());
                pickUpItemlist.get(j).setItemUrlList(mUpLoadlist.get(j));
            }

            LogUtils.d("123");
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(CODE_PRODUCT_LIST, (Serializable) productList);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            saveHelpMyGetRequest(payCode, pickUpItemlist.size() + "", productList);

           /* if (imgList.size() > 1) {
                for (int i = 1; i < imgList.size(); i++) {
                    CsBase.ItemImage.Builder builder = CsBase.ItemImage.newBuilder();
                    builder.setImageUrl(map.get(imgList.get(i)));
                    itemList.add(builder.build());
                }
                addItemSaveRequest(albumId, subMatchItem.getMatchitemcategoryid(),
                        map.get(imgList.get(0)), captionEt.getText().toString(),
                        Float.parseFloat(priceEt.getText().toString()), itemList);
            } else {
                for (int i = 0; i < imgList.size(); i++) {
                    CsBase.ItemImage.Builder builder = CsBase.ItemImage.newBuilder();
                    builder.setImageUrl(map.get(imgList.get(i)));
                    itemList.add(builder.build());
                }
                addItemSaveRequest(albumId, subMatchItem.getMatchitemcategoryid(),
                        map.get(imgList.get(0)), captionEt.getText().toString(),
                        Float.parseFloat(priceEt.getText().toString()), null);
            }*/
        }

    }

    public void saveHelpMyGetRequest(String payCode, String sumcount, List<CsParcel.ProductDataList> productList) {
        CsParcel.SaveHelpMyGetRequest.Builder builder = CsParcel.SaveHelpMyGetRequest.newBuilder();
        builder.setUserhead(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setPaymentcode(payCode);
        builder.setSumcount(sumcount);
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
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        mSweetAlertDialog.dismiss();
    }

    private Handler saveHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_save_success_msg), Toast.LENGTH_SHORT).show();
//            clearData();
            getSweetAlertDialog().dismiss();
//            EventBus.getDefault().post(new BusEvent(BusEvent.CODE_GO_MERCHAND, true));
        }
    };

   /* public void clearData() {
        isClickSub = false;
        imgList.clear();
        itemList.clear();
        bmpList.clear();
        adapter.notifyDataSetChanged();
        captionEt.setText("");
        priceEt.setText("");
        subMatchItem = null;
        classifyMsg.setText(getString(R.string.add_merchandise_classify_msg));
        albumId = -1;
        albumNameTv.setText(getString(R.string.add_merchandise_album_msg));
    }*/

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
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }

    private Handler initHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            response = (CsParcel.HelpMyGetInitResponse) msg.obj;
            currencyCode = response.getCurrencyCode();
            PaymentManager.getInstance(AddRequireActivity_bak.this).getFKDPaymentListRequest(response.getCurrencyCode(), new PaymentManager.OnFKDPaymentListener() {
                @Override
                public void onResult(int paymentType, final String pString, String pCode) {
                    payCode = pCode;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            paymentTv.setText(pString);
                        }
                    });
                }
            });
            balanceTv.setText(getString(R.string.pick_up_payment_balance, response.getGiftCardAccount()));
//            waringTv.setText(getString(R.string.pick_up_services_waring_msg, (int) response.getServiceFee()));
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

    public static void setListViewHeightBasedOnChildren2(ListView listView) {
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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 50;
        listView.setLayoutParams(params);
    }

    public void updateServiceFeeLayout(Set<PickUpItemBean> serviceFeeSet) {
        for (PickUpItemBean item : serviceFeeSet) {
            float price = Float.parseFloat(item.getItemPrice());
            int count = Integer.parseInt(item.getItemCount());
            float fee = (float) (price * count * (response.getServiceFee() / 100));
            String feeMsg = response.getCurrencySign() + price + "*" + count + "*" + (int) response.getServiceFee() + "%=" + response.getCurrencySign() + fee;

            TextView feeTv = new TextView(AddRequireActivity_bak.this);
            feeTv.setTextColor(getResources().getColor(R.color.black_0));
            feeTv.setTextSize(13);
            feeTv.setText(feeMsg);
            feeTv.setGravity(Gravity.RIGHT);
//            serviceFeeLayout.addView(feeTv);
        }

    }

    int itemId = -1;

    public void updateServiceFeeLayout(PickUpItemBean item) {
        List<PickUpItemBean> list = new ArrayList<PickUpItemBean>();
        if (itemId != item.getItemId()) {
            pickUpItemlist.add(item);
        } else {
            for (int i = 0; i < pickUpItemlist.size(); i++) {
                if (pickUpItemlist.get(i).getItemId() == item.getItemId()) {
                    pickUpItemlist.set(i, item);
                }
            }
        }
        float grandTotal = 0;
        for (int i = 0; i < pickUpItemlist.size(); i++) {
            float price = Float.parseFloat(pickUpItemlist.get(i).getItemPrice());
            int count = Integer.parseInt(pickUpItemlist.get(i).getItemCount());
            float fee = (float) (price * count * (response.getServiceFee() / 100));
            String feeMsg = response.getCurrencySign() + price + "*" + count + "*" + (int) response.getServiceFee() + "%=" + response.getCurrencySign() + fee;

            TextView feeTv = new TextView(AddRequireActivity_bak.this);
            feeTv.setTextColor(getResources().getColor(R.color.black_0));
            feeTv.setTextSize(13);
            feeTv.setText(feeMsg);
            feeTv.setGravity(Gravity.RIGHT);
//            serviceFeeLayout.addView(feeTv);
            grandTotal += price * count + fee;
        }
        grandTotalTv.setText(getString(R.string.String_price2, UIUtils.getCurrency(this), grandTotal));
        itemId = item.getItemId();
    }

    public void requestFocus(EditText editText) {
        editText.requestFocus();
    }

    class ItemAdapter extends BaseAdapter {

        private Context mContext;
        private List<PickUpItemBean> list;
        private List<ViewHolder> mViewHolderList;
        private float oldX;
        private float currentX;
        private boolean mIsShowingAni = false;
        private boolean mIsClickAni = false;
        private String currencyCode;
        private int currentPos = -1;

        public ItemAdapter(Context context, List<PickUpItemBean> list, String currencyCode) {
            this.mContext = context;
            this.list = list;
            this.currencyCode = currencyCode;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (mViewHolderList == null) {
                mViewHolderList = new ArrayList<>();
            }
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.pick_up_item, null);
                holder = new ViewHolder();
                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.pick_up_content_layout);
                holder.itemGv = (CustomGridView) convertView.findViewById(R.id.pick_up_item_gv);
                holder.noteEt = (EditText) convertView.findViewById(R.id.pick_up_item_note_et);
                holder.addressEt = (EditText) convertView.findViewById(R.id.pick_up_item_address_et);
                holder.priceEt = (EditText) convertView.findViewById(R.id.pick_up_item_price_et);
                holder.currencyCodeTv = (TextView) convertView.findViewById(R.id.pick_up_item_currency_code_tv);
                holder.countEt = (EditText) convertView.findViewById(R.id.pick_up_item_count_et);
                holder.deleteIv = (ImageView) convertView.findViewById(R.id.pick_up_delete_iv);
                convertView.setTag(holder);
                mViewHolderList.add(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final PickUpItemBean item = list.get(position);
            final MyAdapter myAdapter = new MyAdapter(mContext, item.getItemUrlList(), position);
            holder.itemGv.setAdapter(myAdapter);
            holder.currencyCodeTv.setText(currencyCode);

            holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemlist.size() <= 1) {
                        CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_min_msg), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    itemlist.remove(position);
                    itemAdapter.notifyDataSetChanged();
                }
            });

            holder.countEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    isChanged = true;
                }
            });
            holder.countEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        LogUtils.d("------------Done");
                        Hidekeyboard(holder.countEt);
                        //holder.countEt.clearFocus(); 此行会导致内嵌GridView重复刷新，原因不明
                        if (holder.itemGv.getAdapter().getCount() <= 1) {
                            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_adapter_msg), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (TextUtils.isEmpty(holder.noteEt.getText().toString())) {
                            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_note_msg), Toast.LENGTH_SHORT).show();
                            requestFocus(holder.noteEt);
                            return true;
                        }
                        if (TextUtils.isEmpty(holder.addressEt.getText().toString())) {
                            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_address_msg), Toast.LENGTH_SHORT).show();
                            requestFocus(holder.addressEt);
                            return true;
                        }
                        if (TextUtils.isEmpty(holder.priceEt.getText().toString())) {
                            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_price_msg), Toast.LENGTH_SHORT).show();
                            requestFocus(holder.priceEt);
                            return true;
                        }
                        if (TextUtils.isEmpty(holder.countEt.getText().toString())) {
                            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_count_msg), Toast.LENGTH_SHORT).show();
                            requestFocus(holder.countEt);
                            return true;
                        }

//                        if (!TextUtils.isEmpty(holder.countEt.getText().toString())) {
//                            if (response != null) {
//                                int isFree = response.getFree();
////                                if(isFree == 0){
//                                //免费
////                                }else {
//                                if (response.getRateType() == 0) {
//                                    //定额服务费
//                                } else if (response.getRateType() == 1) {
//                                    if (position != currentPos || isChanged) {
//                                        serviceFeeSet = new HashSet<PickUpItemBean>();
//                                        PickUpItemBean itemBean = new PickUpItemBean();
//                                        itemBean.setItemId(position);
//                                        itemBean.setItemUrlList(item.getItemUrlList());
//                                        itemBean.setItemNote(holder.noteEt.getText().toString());
//                                        itemBean.setItemAddress(holder.addressEt.getText().toString());
//                                        itemBean.setItemPrice(holder.priceEt.getText().toString());
//                                        itemBean.setItemCount(holder.countEt.getText().toString());
//                                        LogUtils.d("---------------" + serviceFeeSet.contains(itemBean.getItemId()));
//                                        serviceFeeSet.add(itemBean);
//                                        serviceFeeLayout.removeAllViews();
//                                        updateServiceFeeLayout(itemBean);
//                                        isChanged = false;
//                                    }
//
//                                    currentPos = position;
//                                }
////                                }
//                            }
//                        }
                    }
                    return true;
                }
            });
            /*convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        oldX = event.getX();
                    }
                    if(event.getAction() == MotionEvent.ACTION_MOVE){
                        currentX = event.getX();
                        LogUtils.d("------------- " + oldX + " --- " + currentX);
                        if(currentX < oldX){
                            doDeletAnimationPlus(holder, 200, mIsClickAni);
                        }
                    }
                    return true;
                }
            });*/
            return convertView;
        }

        class ViewHolder {
            LinearLayout contentLayout;
            CustomGridView itemGv;
            EditText noteEt;
            EditText addressEt;
            EditText priceEt;
            TextView currencyCodeTv;
            EditText countEt;
            ImageView deleteIv;
        }

        public void doDeletAnimation() {
            if (mIsShowingAni) return;
            mIsClickAni = !mIsClickAni;
            for (ViewHolder viewHolder : mViewHolderList) {
                doDeletAnimationPlus(viewHolder, 200, mIsClickAni);
            }
        }

        private void doDeletAnimationPlus(ViewHolder viewHolder, long time, final boolean isOpen) {

            AnimationUtils.doDeletAnimation(HelpItemViewBean.create().setLLView(viewHolder.contentLayout).setImageView(viewHolder.deleteIv).setDuration(time).setAnimatiorAdapter(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsShowingAni = true;
                    if (isOpen) {
                        rightTv.setText(getString(R.string.pick_up_right_confrim));
                    } else {
                        rightTv.setText(getString(R.string.pick_up_right_delete));
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsShowingAni = false;
                }
            }), isOpen);


        }
    }

    class MyAdapter extends BaseAdapter {

        private Context mCtx;
        private List<String> list;
        private int parentPosition;
        private ImageLoader imageLoader;
        private DisplayImageOptions options;

        public MyAdapter(Context context, List<String> list, int position) {
            this.mCtx = context;
            this.list = list;
            this.parentPosition = position;
            this.imageLoader = ImageLoader.getInstance();
            this.options = ImageLoaderHelper.getInstance(mCtx).getDisplayOptions();
        }

        @Override
        public int getCount() {
            if (list == null || list.size() == 0) {
                return 1;
            } else if (list.size() == 4) {
                return 4;
            } else {
                return list.size() + 1;
            }

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mCtx).inflate(R.layout.merchandise_item, null);
                holder = new Holder();
                holder.merchandiseIv = (ImageView) convertView.findViewById(R.id.merchandise_iv);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            int width = UIUtils.getScreenWidthPixels((Activity) mCtx) / 4 - 30;
            holder.merchandiseIv.setLayoutParams(new LinearLayout.LayoutParams(width, width));

            if (position == list.size() || list == null || list.size() == 0) {
                if (position == 4) {
                    convertView.setVisibility(View.GONE);
                } else {
                    holder.merchandiseIv.setScaleType(ImageView.ScaleType.CENTER);
                    holder.merchandiseIv.setVisibility(View.VISIBLE);
                    holder.merchandiseIv.setImageResource(R.mipmap.demand_addimage);
                    holder.merchandiseIv.setBackgroundResource(R.drawable.add_merchandise_shape);
                    holder.merchandiseIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent();
                            intent.setClass(mCtx, Down2UpDialogActivity.class);
                            intent.putExtra(Down2UpDialogActivity.GO_THIS_ACTIVITY_INTENT_KEY, Down2UpDialogActivity.ADD_MERCHANT_ITEM_TYPE);
                            startActivityForResult(intent, Constants.ALBUM_GET_BITMAP_REQUEST_CODE);*/
                            itemPos = parentPosition;
                            MPermissions.requestPermissions(AddRequireActivity_bak.this, 3, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                            UIUtils.startImageSelectorForAddItem(1000, imgList, PickUpActivity.this, 6);
                        }
                    });
                }

            } else {
                if (position == parent.getChildCount()) {
                    holder.merchandiseIv.setVisibility(View.VISIBLE);
                    //                imageLoader.displayImage(imageList.get(position).toString(), holder.merchandiseIv, options);
                    try {
                        //                        FileInputStream fis = new FileInputStream(list.get(position));
                        //                        Bitmap bmp = BitmapFactory.decodeFile(list.get(position));


                        //                    holder.merchandiseIv.setImageBitmap(BitMapUtils.getResizedBitmap(bmp, width, width));
                        holder.merchandiseIv.setCropToPadding(true);
                        holder.merchandiseIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
                        Uri uri = Uri.fromFile(new File(list.get(position)));
                        //ImageLoader.getInstance().displayImage(uri.toString(), holder.merchandiseIv, options);
                        Glide.with(AddRequireActivity_bak.this).load(uri.toString()).into(holder.merchandiseIv);
                        holder.merchandiseIv.setLayoutParams(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    holder.merchandiseIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(mCtx, AddRequireActivity_bak.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("imgList", (Serializable) itemlist.get(itemPos).getItemUrlList());
                            intent.putExtra("resourceType", PreviewMerchandiseActivity.RESOURCE_TYPE_ADD);
                            bundle.putInt("position", position);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, PreviewMerchandiseActivity.REQUEST_CODE_BACK_IMG_LIST);
                        }
                    });
                }
            }
            return convertView;
        }

        class Holder {
            ImageView merchandiseIv;
        }
    }

    @PermissionGrant(3)
    public void requestContactSuccess() {
        UIUtils.startImageSelectorForAddItem(1000, (ArrayList<String>) itemlist.get(itemPos).getItemUrlList(), AddRequireActivity_bak.this, 4);
        UpLoadImageManager.getInstance().setUpLoadProgressEmpty();
    }

    @PermissionDenied(3)
    public void requestContactFailed() {
        Toast.makeText(AddRequireActivity_bak.this, "DENY ACCESS CONTACTS!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView) {
        // 获取GridView对应的Adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int rows;
        int columns = 0;
        int horizontalBorderHeight = 0;
        Class<?> clazz = gridView.getClass();
        try {
            //利用反射，取得每行显示的个数
            Field column = clazz.getDeclaredField("mRequestedNumColumns");
            column.setAccessible(true);
            columns = (Integer) column.get(gridView);
            //利用反射，取得横向分割线高度
            Field horizontalSpacing = clazz.getDeclaredField("mRequestedHorizontalSpacing");
            horizontalSpacing.setAccessible(true);
            horizontalBorderHeight = (Integer) horizontalSpacing.get(gridView);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        //判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
        if (listAdapter.getCount() % columns > 0) {
            rows = listAdapter.getCount() / columns + 1;
        } else {
            rows = listAdapter.getCount() / columns;
        }
        int totalHeight = 0;
        for (int i = 0; i < rows; i++) { //只计算每项高度*行数
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + horizontalBorderHeight * (rows - 1);//最后加上分割线总高度
        gridView.setLayoutParams(params);
    }

}