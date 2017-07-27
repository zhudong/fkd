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
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.fuexpress.kr.bean.CategoryBean;
import com.fuexpress.kr.bean.HelpItemViewBean;
import com.fuexpress.kr.bean.MaterialsBean;
import com.fuexpress.kr.bean.PickUpItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.PaymentManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.choose_category.ChooseCategoryActivity;
import com.fuexpress.kr.ui.activity.package_detail.AutoEditText;
import com.fuexpress.kr.ui.uiutils.AnimationUtils;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CurrencyEditText;
import com.fuexpress.kr.ui.view.CustomGridView;
import com.fuexpress.kr.ui.view.CustomListView;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.ui.view.imageselector.ImageSelectorActivity;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;
import com.fuexpress.kr.utils.UpLoadImageManager;
import com.google.protobuf.GeneratedMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
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
import fksproto.CsUser;

import static com.fuexpress.kr.base.SysApplication.mContext;

/**
 * Created by Administrator on 2016-12-5.
 */

public class AddRequireActivity extends BaseActivity {

    //固定为韩元币种
    public static final String CURRENCY_KRW = "KRW";

    private static final int ITEM_SIZE = 4;
    private static final int CODE_PAYMENT_REQUEST = 0x1001;
    public static  String CODE_PRODUCT_LIST = "productList";
    public static  String CODE_PICK_LIST = "pickItemList";
    public static final String CODE_ITEM_COUNT = "code_item_count";

    private CustomGridView itemGv;

    private View rootView;
    private MyAdapter adapter;
    private LinearLayout addLayout;
    private TitleBarView titleBarView;
    private TextView backTv;
    private TextView rightTv;
    private ImageView backIv;
    private List<PickUpItemBean> itemlist;
    private ArrayList<String> imgList;
    private List<String> mPathList;
    private int itemPos;

    private EditText noteEt;
    private EditText addressEt;
    private CurrencyEditText priceEt;
    private EditText countEt;
    private AutoEditText brandEt;
    private TextView materialTv;
    private TextView classTv;
    private TextView currencyCodeTv;
    private LinearLayout materialLayout;
    private LinearLayout classLayout;

    private List<PickUpItemBean> pickUpItemlist = new ArrayList<PickUpItemBean>();
    private String payCode;
    private int itemCount = 0;
    private List<String> mUpLoadCompletelist;
    private List<List<String>> mUpLoadlist;
    private boolean isClickSub = false;
    private boolean isAddAgeinSub = false;
    private Button confirmBtn;
    private ScrollView contentSv;
    private List<CsParcel.ProductDataList> productList;
    private List<PickUpItemBean> pickList;
    private int salesrequireid;
    private boolean isNewResources = false;
    private CsUser.HelpMyGetInfoResponse infoResponse;
    List<PickUpItemBean> itemList;
    List<CsParcel.ProductDataList> pList;
    private int materialsId = 0;
    private int classParentId = -1;
    private int classSubId = -1;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_add_require, null);
        titleBarView = (TitleBarView) rootView.findViewById(R.id.pick_up_titlebar);
        backTv = titleBarView.getmTv_in_title_back_tv();
        backTv.setText(getString(R.string.pick_up_title));
        rightTv = titleBarView.getTv_in_title_right();
        rightTv.setText(getString(R.string.pick_up_right_delete));
        rightTv.setVisibility(View.GONE);
        backIv = titleBarView.getIv_in_title_back();
        addLayout = (LinearLayout) rootView.findViewById(R.id.add_require_add_layout);
        confirmBtn = (Button) rootView.findViewById(R.id.add_require_confirm_btn);
        itemGv = (CustomGridView) rootView.findViewById(R.id.add_require_gv);
        noteEt = (EditText) rootView.findViewById(R.id.add_require_note_et);
        addressEt = (EditText) rootView.findViewById(R.id.add_require_address_et);
        priceEt = (CurrencyEditText) rootView.findViewById(R.id.add_require_price_et);
        countEt = (EditText) rootView.findViewById(R.id.add_require_count_et);
        contentSv = (ScrollView) rootView.findViewById(R.id.add_require_sv);
        brandEt = (AutoEditText) rootView.findViewById(R.id.add_require_brand_et);
        materialTv = (TextView) rootView.findViewById(R.id.add_require_material_tv);
        materialLayout = (LinearLayout) rootView.findViewById(R.id.material_layout);
        classLayout = (LinearLayout) rootView.findViewById(R.id.add_class_layout);
        classTv = (TextView) rootView.findViewById(R.id.add_require_class_tv);
        currencyCodeTv = (TextView) rootView.findViewById(R.id.pick_up_item_currency_code_tv);

        String currencyCode = getIntent().getStringExtra(PickUpActivity.CODE_CITY_CURRENCY);
        if(currencyCode == null || TextUtils.isEmpty(currencyCode)){
            currencyCode = SPUtils.getString(this, PickUpActivity.CODE_CITY_CURRENCY, currencyCode);
        }else {
            currencyCodeTv.setText(currencyCode);
            priceEt.setCurrency(currencyCode);
        }
        itemList = (List<PickUpItemBean>) SPUtils.readObject(this, AddRequireActivity.CODE_PICK_LIST);
        pList = (List<CsParcel.ProductDataList>) SPUtils.readObject(this, AddRequireActivity.CODE_PRODUCT_LIST);
        itemCount = itemList == null ? 1 : itemList.size();
        init();
//        itemCount = getIntent().getIntExtra(CODE_ITEM_COUNT, 1);
        salesrequireid = getIntent().getIntExtra(DemandsDetailActivity.CREATE_ANOTHER_ORDRE_ID, -1);
        if (salesrequireid != -1) {
            addLayout.setVisibility(View.GONE);
            helpMyGetInfoRequest(salesrequireid);
        }else {
            addLayout.setVisibility(View.VISIBLE);
        }
        backIv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        rightTv.setOnClickListener(this);
        addLayout.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        materialLayout.setOnClickListener(this);
        classLayout.setOnClickListener(this);
//        paymentLayout.setOnClickListener(this);
//        submitBtn.setOnClickListener(this);
        return rootView;
    }


    public void init() {
        productList = new ArrayList<CsParcel.ProductDataList>();
        pickList = new ArrayList<PickUpItemBean>();
        mUpLoadCompletelist = new ArrayList<>();
        mUpLoadlist = new ArrayList<List<String>>();
        itemlist = new ArrayList<PickUpItemBean>();
        PickUpItemBean bean = new PickUpItemBean();
        imgList = new ArrayList<String>();
        bean.setItemUrlList(imgList);
        itemlist.add(bean);
        pList = new ArrayList<CsParcel.ProductDataList>();

        adapter = new MyAdapter(this, itemlist.get(itemPos).getItemUrlList(), itemPos);
        itemGv.setAdapter(adapter);
        requestFocus(noteEt);
//        scrollToBottom(contentSv);
//        helpMyGetInitRequest();
    }

    @Override
    public void finish() {
        Hidekeyboard(countEt);
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                if (salesrequireid != -1) {
                    Intent intent = new Intent(this, PickUpActivity.class);
                    intent.putExtra("salesrequireid", salesrequireid);
                    startActivity(intent);
                } else {
                    finish();
                }
                break;
            case R.id.tv_in_title_right:
                break;
            case R.id.add_require_add_layout:
                itemCount++;
                if (itemCount > ITEM_SIZE) {
                    itemMaxAndReturn();
                    return;
                }
                if (!checkItemStaus()) {
                    return;
                }
                isAddAgeinSub = true;
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_UP_LOAD_IMAGE_LIST_PROGRESS, null));
                clearData();
                break;
            case R.id.add_require_confirm_btn:
                if (itemCount > ITEM_SIZE) {
                    itemMaxAndReturn();
                    return;
                }
                if (!checkItemStaus()) {
                    return;
                }
                isClickSub = true;
                if (isNewResources) {
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_UP_LOAD_IMAGE_LIST_PROGRESS, null));
                } else {
                    setResult();
                }
                break;
            case R.id.material_layout:
                startMaterials();
                break;
            case R.id.add_class_layout:
                startCategory();
                break;
//            case R.id.pick_up_submit_btn:
//                isClickSub = true;
//                EventBus.getDefault().post(new BusEvent(BusEvent.GET_UP_LOAD_IMAGE_LIST_PROGRESS, null));
//                break;
        }
    }

    public void itemMaxAndReturn() {
        CustomToast.makeText(this, getString(R.string.pick_up_add_item_max_msg), Toast.LENGTH_SHORT).show();
        finish();
    }

    public void scrollToBottom(final ScrollView scrollView) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                contentSv.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
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
                adapter.notifyDataSetChanged();
//                for (int i = 0; i < pickListView.getChildCount(); i++) {
//                    GridView view = (GridView) pickListView.getChildAt(itemPos).findViewById(R.id.pick_up_item_gv);
//                    view.setAdapter(new MyAdapter(this, itemlist.get(itemPos).getItemUrlList(), itemPos));
//                    if (pickListView.getChildCount() > 1 && itemlist.get(itemPos).getItemUrlList().size() >= 4) {
//                        setListViewHeightBasedOnChildren2(pickListView);
//                    }
//                }
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
            if (getSweetAlertDialog() != null) {
                if (!getSweetAlertDialog().isShowing()) {
                    showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, "上传中");
                }
            } else {
                showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, "上传中");
            }
            List<Map<String, String>> mapList = new ArrayList<>();
            Map<String, String> map = new HashMap<String, String>();
            for (int h = 0; h < mUpLoadCompletelist.size(); h++) {
//                for (int i = 0; i < mUpLoadCompletelist.get(h).size(); i++) {
                String[] str = mUpLoadCompletelist.get(h).split(",");
                map.put(str[1], str[0]);
                mapList.add(map);
//                }
            }
            CsBase.ItemImage.Builder imgBuilder = CsBase.ItemImage.newBuilder();
            CsParcel.ProductDataList.Builder builder = CsParcel.ProductDataList.newBuilder();
            builder.setProductdescription(noteEt.getText().toString());
            builder.setAddressinfo(addressEt.getText().toString());
            builder.setPrice(priceEt.getmText().toString());
            builder.setNum(countEt.getText().toString());
            builder.setImageNum(mUpLoadCompletelist.size());
            builder.setBrandname(brandEt.getText().toString());
            builder.setMatchtagid(materialsId);
            builder.setMatchcategoryid(classSubId);
            for (int k = 0; k < imgList.size(); k++) {
//                        imgBuilder.setImageUrl(map.get(item.getItemUrlList().get(k)));
                imgBuilder.setImageUrl(map.get(imgList.get(k)));
                builder.addExtra(imgBuilder);
            }
//                    imgBuilder.setImageUrl(item.getItemUrlList().get(k));
            productList.add(builder.build());

            PickUpItemBean bean = new PickUpItemBean();
            bean.setItemNote(noteEt.getText().toString());
            bean.setItemAddress(addressEt.getText().toString());
            bean.setItemPrice(priceEt.getmText().toString());
            bean.setItemCount(countEt.getText().toString());
            bean.setImageCount(mUpLoadCompletelist.size());
            bean.setItemBrand(brandEt.getText().toString());
            bean.setMatchtagid(materialsId);
            bean.setItemMaterials(materialTv.getText().toString());
            bean.setItemClassParentId(classParentId);
            bean.setItemClassSubId(classSubId);
            bean.setMatchcategoryid(classSubId);
            bean.setItemClass(classTv.getText().toString());
            bean.getMap().putAll(map);
            for (int k = 0; k < imgList.size(); k++) {
//                        imgBuilder.setImageUrl(map.get(item.getItemUrlList().get(k)));
                imgBuilder.setImageUrl(map.get(imgList.get(k)));
            }
            bean.getPathList().clear();
            bean.getPathList().addAll(imgList);
            pickList.add(bean);

            if (isClickSub) {
                if (salesrequireid != -1) {
                    if(itemList == null){
                        itemList = new ArrayList<PickUpItemBean>();
                    }
                    if(pList == null){
                        pList = new ArrayList<CsParcel.ProductDataList>();
                    }
                    itemList.addAll(pickList);
                    pList.addAll(productList);
                    SPUtils.saveObject(this, AddRequireActivity.CODE_PICK_LIST, itemList);
                    SPUtils.saveObject(this, AddRequireActivity.CODE_PRODUCT_LIST, pList);
                    Intent intent1 = new Intent(this, PickUpActivity.class);
                    startActivity(intent1);
                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(CODE_PICK_LIST, (Serializable) pickList);
                    bundle.putSerializable(CODE_PRODUCT_LIST, (Serializable) productList);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
            if (isAddAgeinSub) {
                if(itemList == null){
                    itemList = new ArrayList<PickUpItemBean>();
                }
                if(pList == null){
                    pList = new ArrayList<CsParcel.ProductDataList>();
                }
                itemList.clear();
                pList.clear();
                itemList.addAll(pickList);
                pList.addAll(productList);
                SPUtils.saveObject(this, AddRequireActivity.CODE_PICK_LIST, itemList);
                SPUtils.saveObject(this, AddRequireActivity.CODE_PRODUCT_LIST, pList);
            }
            if(getSweetAlertDialog() != null){
                getSweetAlertDialog().dismiss();
            }
            CustomToast.makeText(AddRequireActivity.this, getString(R.string.add_require_success_msg), Toast.LENGTH_SHORT).show();

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

    public void setResult() {
        CsBase.ItemImage.Builder imgBuilder = CsBase.ItemImage.newBuilder();
        CsParcel.ProductDataList.Builder builder = CsParcel.ProductDataList.newBuilder();
        builder.setProductdescription(noteEt.getText().toString());
        builder.setAddressinfo(addressEt.getText().toString());
        builder.setPrice(priceEt.getmText().toString());
        builder.setNum(countEt.getText().toString());
        builder.setImageNum(infoResponse.getImagesurlCount());
        builder.setBrandname(brandEt.getText().toString());
        builder.setMatchtagid(materialsId);
        builder.setMatchcategoryid(classSubId);
        for (int k = 0; k < infoResponse.getImagesurlCount(); k++) {
//                        imgBuilder.setImageUrl(map.get(item.getItemUrlList().get(k)));
            imgBuilder.setImageUrl(infoResponse.getImagesurlList().get(k).getImage());
            builder.addExtra(imgBuilder);
        }
//                    imgBuilder.setImageUrl(item.getItemUrlList().get(k));
        productList.clear();
        productList.add(builder.build());

        PickUpItemBean bean = new PickUpItemBean();
        bean.setItemNote(noteEt.getText().toString());
        bean.setItemAddress(addressEt.getText().toString());
        bean.setItemPrice(priceEt.getmText().toString());
        bean.setItemCount(countEt.getText().toString());
        bean.setImageCount(infoResponse.getImagesurlCount());
        bean.setItemBrand(brandEt.getText().toString());
        bean.setMatchtagid(materialsId);
        bean.setItemMaterials(materialTv.getText().toString());
        bean.setMatchcategoryid(classSubId);
        bean.setItemClass(classTv.getText().toString());
//        bean.getMap().putAll(map);
        for (int k = 0; k < imgList.size(); k++) {
//                        imgBuilder.setImageUrl(map.get(item.getItemUrlList().get(k)));
            imgBuilder.setImageUrl(infoResponse.getImagesurlList().get(k).getImage());
        }
        bean.getPathList().clear();
        bean.getPathList().addAll(imgList);
        pickList.clear();
        pickList.add(bean);

        if(itemList == null){
            itemList = new ArrayList<PickUpItemBean>();
        }
        if (itemList.size() >= ITEM_SIZE) {
            itemMaxAndReturn();
            return;
        } else {
            itemList.addAll(pickList);
            pList.addAll(productList);
            SPUtils.saveObject(this, AddRequireActivity.CODE_PICK_LIST, itemList);
            SPUtils.saveObject(this, AddRequireActivity.CODE_PRODUCT_LIST, pList);
            Intent intent = new Intent(this, PickUpActivity.class);
            startActivity(intent);

        }

//        saveHelpMyBuyRequest(payCode, pickUpItemlist.size() + "", productList);
    }

    public void saveHelpMyGetRequest(String payCode, String sumcount, List<CsParcel.ProductDataList> productList) {
        CsParcel.SaveHelpMyGetRequest.Builder builder = CsParcel.SaveHelpMyGetRequest.newBuilder();
        builder.setUserhead(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(CURRENCY_KRW);
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
        if (getSweetAlertDialog() != null) {
            getSweetAlertDialog().dismiss();
        }
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

    public void clearData() {
        isClickSub = false;
        imgList.clear();
//        itemList.clear();
//        bmpList.clear();
        adapter.notifyDataSetChanged();
        noteEt.setText("");
        addressEt.setText("");
        priceEt.setText("");
        countEt.setText("");
        brandEt.setText("");
        materialTv.setHint(getString(R.string.pick_up_commodity_material_hint));
        classTv.setHint(getString(R.string.pick_up_commodity_class_hint));
//        materialsId = 0;
//        classSubId = 0;
//        classParentId = 0;
        Hidekeyboard(countEt);
//        subMatchItem = null;
//        classifyMsg.setText(getString(R.string.add_merchandise_classify_msg));
//        albumId = -1;
//        albumNameTv.setText(getString(R.string.add_merchandise_album_msg));
    }

    public boolean checkItemStaus() {

        if (adapter.getCount() <= 1) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_adapter_msg), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(noteEt.getText().toString())) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_note_msg), Toast.LENGTH_SHORT).show();
            requestFocus(noteEt);
            Showkeyboard(noteEt);
            itemCount = 0;
            return false;
        }
       /* if (materialsId == 0) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_materials_msg), Toast.LENGTH_SHORT).show();
            itemCount = 0;
            return false;
        }*/
        if (classSubId == -1) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_class_msg), Toast.LENGTH_SHORT).show();
            itemCount = 0;
            return false;
        }
        if (TextUtils.isEmpty(addressEt.getText().toString())) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_address_msg), Toast.LENGTH_SHORT).show();
            requestFocus(addressEt);
            Showkeyboard(addressEt);
            itemCount = 0;
            return false;
        }
        if (TextUtils.isEmpty(priceEt.getText().toString())) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_price_msg), Toast.LENGTH_SHORT).show();
            requestFocus(priceEt);
            Showkeyboard(priceEt);
            itemCount = 0;
            return false;
        }
        if (Float.parseFloat(priceEt.getmText().toString()) <= 0) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_price_msg2), Toast.LENGTH_SHORT).show();
            requestFocus(priceEt);
            Showkeyboard(priceEt);
            itemCount = 0;
            return false;
        }
        if (TextUtils.isEmpty(countEt.getText().toString())) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_count_msg), Toast.LENGTH_SHORT).show();
            requestFocus(countEt);
            Showkeyboard(countEt);
            itemCount = 0;
            return false;
        }

        if (Integer.parseInt(countEt.getText().toString()) <= 0) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_count_msg2), Toast.LENGTH_SHORT).show();
            requestFocus(countEt);
            Showkeyboard(countEt);
            itemCount = 0;
            return false;
        }
        scrollToBottom(contentSv);
        return true;
    }

    public void requestFocus(EditText editText) {
        editText.requestFocus();
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
                    holder.merchandiseIv.setImageResource(R.mipmap.add_add_photo);
//                    holder.merchandiseIv.setPadding(60,60,60,60);
                    holder.merchandiseIv.setBackgroundResource(R.drawable.shape_bg_rectangle_);
                    holder.merchandiseIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent();
                            intent.setClass(mCtx, Down2UpDialogActivity.class);
                            intent.putExtra(Down2UpDialogActivity.GO_THIS_ACTIVITY_INTENT_KEY, Down2UpDialogActivity.ADD_MERCHANT_ITEM_TYPE);
                            startActivityForResult(intent, Constants.ALBUM_GET_BITMAP_REQUEST_CODE);*/
                            itemPos = parentPosition;
                            isNewResources = true;
                            MPermissions.requestPermissions(AddRequireActivity.this, 3, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

                        String[] https = list.get(position).split("http");
                        if (https.length > 1) {
                            Glide.with(mCtx).load(list.get(position).split("!")[0] + Constants.ImgUrlSuffix.small_9).into(holder.merchandiseIv);
                        } else {
                            File file = new File(list.get(position));
                            Glide.with(mCtx).load(file).into(holder.merchandiseIv);
                        }

//                        Uri uri = Uri.fromFile(new File(list.get(position)));
                        //ImageLoader.getInstance().displayImage(uri.toString(), holder.merchandiseIv, options);
//                        Glide.with(AddRequireActivity.this).load(uri.toString()).into(holder.merchandiseIv);
                        holder.merchandiseIv.setLayoutParams(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    holder.merchandiseIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(mCtx, PreviewMerchandiseActivity.class);
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
        UIUtils.startImageSelectorForAddItem(1000, (ArrayList<String>) itemlist.get(itemPos).getItemUrlList(), AddRequireActivity.this, 4);
        UpLoadImageManager.getInstance().setUpLoadProgressEmpty();
    }

    @PermissionDenied(3)
    public void requestContactFailed() {
        Toast.makeText(AddRequireActivity.this, "DENY ACCESS CONTACTS!", Toast.LENGTH_SHORT).show();
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

    public void helpMyGetInfoRequest(int salesrequireid) {
        CsUser.HelpMyGetInfoRequest.Builder builder = CsUser.HelpMyGetInfoRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setSalesrequireid(salesrequireid);
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.HelpMyGetInfoResponse>() {

            @Override
            public void onSuccess(CsUser.HelpMyGetInfoResponse response) {
                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                infoHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    private Handler infoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            infoResponse = (CsUser.HelpMyGetInfoResponse) msg.obj;
            noteEt.setText(infoResponse.getDescription());
            addressEt.setText(infoResponse.getAddress());
            priceEt.setText(infoResponse.getPrice());
            countEt.setText(infoResponse.getQty() + "");
            priceEt.setCurrency(infoResponse.getCurrencycode());
            currencyCodeTv.setText(infoResponse.getCurrencycode());
            noteEt.setSelection(infoResponse.getDescription().length());
            brandEt.setText(infoResponse.getBrandName());
            materialTv.setText(infoResponse.getMatchTagName());
            classTv.setText(infoResponse.getMatchCategoryName());
            itemlist.get(itemPos).getItemUrlList().clear();
            materialsId = Integer.parseInt(infoResponse.getMatchTagId());
            classParentId = Integer.parseInt(infoResponse.getParentMatchCategoryId());
            classSubId = Integer.parseInt(infoResponse.getMatchCategoryId());

            for (int i = 0; i < infoResponse.getImagesurlCount(); i++) {
                itemlist.get(itemPos).getItemUrlList().add(infoResponse.getImagesurlList().get(i).getImage());
            }
            adapter.notifyDataSetChanged();
        }
    };

    private void startMaterials() {
//        Intent intent = new Intent(mContext, MaterialsSearchActivity.class);
        Intent intent = new MaterialsSearchActivity.IntentBuilder(materialsId).build(mContext, new MaterialsSearchActivity.iMaterialsSelectListener() {
            @Override
            public void select(boolean select, MaterialsBean bean) {
                if (select) {
                    materialsId = bean.getId();
                    materialTv.setText(bean.getName());
                }
            }
        });
        startActivity(intent);
    }

    private void startCategory() {
        ChooseCategoryActivity.IntentBuilder intentBuilder = ChooseCategoryActivity.IntentBuilder.build(mContext)
                .setParentID(classParentId)//设置父级分类ID
                .setSubID(classSubId)//设置子级分类ID
                .setListener(new ChooseCategoryActivity.ChooseCategoryListener() {//设置监听
                    @Override
                    public void select(boolean select, CategoryBean bean) {
                        LogUtils.e(String.valueOf(select));
                        if (select) {
                            classParentId = bean.getParentID();
                            classSubId = bean.getSubID();
                            classTv.setText(bean.getSubName());
                        }
                    }
                });
        startActivity(intentBuilder);
    }
}