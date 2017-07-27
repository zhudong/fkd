package com.fuexpress.kr.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.CategoryBean;
import com.fuexpress.kr.bean.MaterialsBean;
import com.fuexpress.kr.bean.PickUpItemBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.choose_category.ChooseCategoryActivity;
import com.fuexpress.kr.ui.activity.package_detail.AutoEditText;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomGridView;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.ui.view.imageselector.ImageSelectorActivity;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;
import com.fuexpress.kr.utils.UpLoadImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsParcel;
import fksproto.CsUser;
import okhttp3.Call;
import okhttp3.OkHttpClient;

import static com.fuexpress.kr.base.SysApplication.mContext;


/**
 * Created by kevin on 2016/12/20.
 * 这里用来处理编辑商品好新增商品两个功
 * 2016.12.28，修改成员，去掉冗余的列表
 */

public class AddReplenishActivity extends BaseActivity {
    private static final int ITEM_SIZE = 4;
    private static final int CODE_PAYMENT_REQUEST = 0x1001;
    public static  String CODE_PRODUCT_LIST = "EditReplenishActivity_productList";
    public static  String CODE_PICK_LIST = "EditReplenishActivity_pickItemList";
    public static  String CODE_ITEM_COUNT = "EditReplenishActivity_code_item_count";
    public static final String CODE_PRODUCT_LIST_final = "EditReplenishActivity_productList";
    public static final String CODE_PICK_LIST_final = "EditReplenishActivity_pickItemList";
    public static final String CODE_ITEM_COUNT_final = "EditReplenishActivity_code_item_count";
    @BindView(R.id.add_require_material_et)
    TextView mAddRequireMaterialEt;
    @BindView(R.id.choose_material_layout)
    LinearLayout mChooseMaterialLayout;
    @BindView(R.id.add_require_class_et)
    TextView mAddRequireClassEt;
    @BindView(R.id.rl_choose_category_layout)
    LinearLayout mRlChooseCategoryLayout;
    @BindView(R.id.add_require_brand_et)
    AutoEditText mAddRequireBrandEt;

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
    private EditText priceEt;
    private EditText countEt;


    private List<PickUpItemBean> pickUpItemlist = new ArrayList<PickUpItemBean>();
    private String payCode;
    private int itemCount = 0;
    private List<String> mUpLoadCompletelist;
    private List<List<String>> mUpLoadlist;
    private boolean isClickSub = false;
    private Button confirmBtn;
    private ScrollView contentSv;
    private List<CsParcel.ProductDataList> productList;
    private List<PickUpItemBean> pickList;
    //当前是否处于继续添加
    private boolean isAddAgeinSub = false;
    private int mId;
    private List<String> localUrl = new ArrayList();
    List<PickUpItemBean> itemList;
    //材质
    private int materId = -1;
    //分类
    private int categoryId = -1,classParentId=-1,classSubId=-1;
    //品牌名称
    private String brand = "";

    private TextView currencyCodeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mChooseMaterialLayout.setOnClickListener(this);
        mRlChooseCategoryLayout.setOnClickListener(this);
        checkSize(3);
    }
    private void checkSize(int count ){
        List<PickUpItemBean> been=(List<PickUpItemBean>) SPUtils.readObject(UIUtils.getContext(), EditReplenishActivity.CODE_PICK_LIST);
        if(been!=null&&been.size()==count){
            addLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_add_replenish, null);
        titleBarView = (TitleBarView) rootView.findViewById(R.id.pick_up_titlebar);
        backTv = titleBarView.getmTv_in_title_back_tv();
        backTv.setText(getString(R.string.home_fg_help_03));
        rightTv = titleBarView.getTv_in_title_right();
        rightTv.setText(getString(R.string.pick_up_right_delete));
        rightTv.setVisibility(View.GONE);
        backIv = titleBarView.getIv_in_title_back();
        addLayout = (LinearLayout) rootView.findViewById(R.id.add_require_add_layout);
        confirmBtn = (Button) rootView.findViewById(R.id.add_require_confirm_btn);
        itemGv = (CustomGridView) rootView.findViewById(R.id.add_require_gv);
        noteEt = (EditText) rootView.findViewById(R.id.add_require_note_et);
        addressEt = (EditText) rootView.findViewById(R.id.add_require_address_et);
        priceEt = (EditText) rootView.findViewById(R.id.add_require_price_et);
        countEt = (EditText) rootView.findViewById(R.id.add_require_count_et);
        contentSv = (ScrollView) rootView.findViewById(R.id.add_require_sv);
        currencyCodeTv = (TextView) rootView.findViewById(R.id.pick_up_item_currency_code_tv);

        String currencyCode = getIntent().getStringExtra(PickUpActivity.CODE_CITY_CURRENCY);
        if(currencyCode == null || TextUtils.isEmpty(currencyCode)){
            currencyCode = SPUtils.getString(this, PickUpActivity.CODE_CITY_CURRENCY, currencyCode);
        }
        currencyCodeTv.setText(currencyCode);

        mId = getIntent().getIntExtra(DemandsDetailActivity.CREATE_ANOTHER_ORDRE_ID, -1);
        if (mId == -1) {
            // 空白初始化
            init();
        } else {
            getBuyBeanInfo(mId);
        }
        itemCount = getIntent().getIntExtra(CODE_ITEM_COUNT, 1);

        backIv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        rightTv.setOnClickListener(this);
        addLayout.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

//        paymentLayout.setOnClickListener(this);
//        submitBtn.setOnClickListener(this);
        return rootView;
    }

    private void getBuyBeanInfo(int id) {
        CsUser.helpMyBuyInfoRequest.Builder builder = CsUser.helpMyBuyInfoRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest());
        builder.setSalesrequireid(id);
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.helpMyBuyInfoResponse>() {
            @Override
            public void onSuccess(final CsUser.helpMyBuyInfoResponse response) {
                KLog.i("Demand", response.toString());
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        init(response);
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

    public void init(CsUser.helpMyBuyInfoResponse response) {
        productList = new ArrayList<CsParcel.ProductDataList>();
        pickList = new ArrayList<PickUpItemBean>();
        mUpLoadCompletelist = new ArrayList<>();
        mUpLoadlist = new ArrayList<List<String>>();
        itemlist = new ArrayList<PickUpItemBean>();
        PickUpItemBean bean = new PickUpItemBean();
        imgList = new ArrayList<String>();
        for (CsUser.ImagesUrl url : response.getImagesurlList()) {
            imgList.add(url.getImage().replace("!small9", ""));
        }
        bean.setItemUrlList(imgList);
        itemlist.add(bean);
        noteEt.setText(response.getDescription());
        addressEt.setText(response.getRemark());
        priceEt.setText(Float.valueOf(response.getPrice().replace(",", "")) + "");
        countEt.setText(response.getQty() + "");
        adapter = new MyAdapter(this, itemlist.get(itemPos).getItemUrlList(), itemPos);
        itemGv.setAdapter(adapter);
        addLayout.setVisibility(View.GONE);
        materId=Integer.valueOf(response.getMatchTagId());
        categoryId=Integer.valueOf(response.getMatchCategoryId());
        if(!TextUtils.isEmpty(response.getBrandName())){
            mAddRequireBrandEt.setText(response.getBrandName());
        }
        if(!TextUtils.isEmpty(response.getMatchTagName())){
            mAddRequireMaterialEt.setText(response.getMatchTagName());
        }
        if(!TextUtils.isEmpty(response.getMatchCategoryName())){
            mAddRequireClassEt.setText(response.getMatchCategoryName());
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
        for (int i = 0; i < imgList.size(); i++) {
            if (TextUtils.isEmpty(imgList.get(i))) {
                imgList.remove(i);
            } else {
                downloadImage(imgList.get(i));
            }
        }
    }

    private void downloadImage(String url) {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(AddReplenishActivity.this.getExternalCacheDir() + "", getFileNameFromUrl(url)) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        KLog.i(e.toString());
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        KLog.e("onResponse :" + response.getAbsolutePath());
                        localUrl.add(response.getAbsolutePath());
                        if (localUrl.size() == imgList.size()) {
                            KLog.i("图片下载完毕");
                            imgList.clear();
                            for (String str : localUrl) {
                                imgList.add(str);
                            }
                        }
                        UIUtils.postTaskSafely(new Runnable() {
                            @Override
                            public void run() {
                                itemlist.get(itemPos).setItemUrlList(localUrl);
                                adapter = new MyAdapter(AddReplenishActivity.this, itemlist.get(itemPos).getItemUrlList(), itemPos);
                                itemGv.setAdapter(adapter);
                                uploadImages();
                            }
                        });
                    }
                });
    }

    public static String getFileNameFromUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        } else {
            return url.substring(url.lastIndexOf("/") + 1).replace("!small9", "");
        }
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

        adapter = new MyAdapter(this, itemlist.get(itemPos).getItemUrlList(), itemPos);
        itemGv.setAdapter(adapter);
        requestFocus(noteEt);
//        scrollToBottom(contentSv);
//        HelpMyBuyInitRequest();
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
                finish();
                break;
            case R.id.tv_in_title_right:
                break;
            case R.id.add_require_add_layout:
                //修改流程，当用户点击继续添加是，保存当前数据到本地
                //  itemCount++;
                if (itemCount > ITEM_SIZE) {
                    CustomToast.makeText(this, getString(R.string.pick_up_add_item_max_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkItemStatus()) {
                    return;
                }

                isAddAgeinSub = true;
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_UP_LOAD_IMAGE_LIST_PROGRESS, null));
                clearData();
                break;
            case R.id.add_require_confirm_btn:
                if (mId != -1) {
                    if (!checkItemStatus()) {
                        return;
                    }
                    //置为已经被点击
                    isClickSub = true;
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_UP_LOAD_IMAGE_LIST_PROGRESS, null));
                    return;
                }
                if (itemCount > ITEM_SIZE) {
                    CustomToast.makeText(this, getString(R.string.pick_up_add_item_max_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkItemStatus()) {
                    return;
                }
                isClickSub = true;
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_UP_LOAD_IMAGE_LIST_PROGRESS, null));
                break;
//            case R.id.pick_up_submit_btn:
//                isClickSub = true;
//                EventBus.getDefault().post(new BusEvent(BusEvent.GET_UP_LOAD_IMAGE_LIST_PROGRESS, null));
//                break;
            case R.id.choose_material_layout:
                chooseMaterials();
                break;
            case R.id.rl_choose_category_layout:
                chooseCategorys();
                break;
        }
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
            builder.setPrice(priceEt.getText().toString());
            builder.setNum(countEt.getText().toString());
            builder.setImageNum(mUpLoadCompletelist.size());
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
            bean.setItemPrice(priceEt.getText().toString());
            bean.setItemCount(countEt.getText().toString());
            bean.setImageCount(mUpLoadCompletelist.size());


            String brand=mAddRequireBrandEt.getText().toString();
            if(!TextUtils.isEmpty(brand)){
                bean.setItemBrand(brand);
            }


            if(!TextUtils.isEmpty(mAddRequireMaterialEt.getText().toString())){
                bean.setItemMaterials(mAddRequireMaterialEt.getText().toString());
                bean.setMatchtagid(materId);
            }


            bean.setMatchcategoryid(categoryId);
            bean.setItemClass(mAddRequireClassEt.getText().toString());
            bean.setItemClassParentId(classParentId);
            bean.setItemClassSubId(classSubId);

            bean.getMap().putAll(map);
            for (int k = 0; k < imgList.size(); k++) {
//                        imgBuilder.setImageUrl(map.get(item.getItemUrlList().get(k)));
                imgBuilder.setImageUrl(map.get(imgList.get(k)));
                bean.getPathList().add(imgList.get(k));
            }
            pickList=new ArrayList<>();
            pickList.add(bean);

            if (isClickSub) {
                LogUtils.d("123");
                if (mId != -1) {
                    List<PickUpItemBean> been = (List<PickUpItemBean>) SPUtils.readObject(this, CODE_PICK_LIST);
                    if (been == null) {
                        been = new ArrayList<>();
                    }
                    KLog.i("been.size " + been.size());
                    been.add(bean);
                    SPUtils.saveObject(this, CODE_PICK_LIST, been);
                    getSweetAlertDialog().dismiss();
                    CustomToast.makeText(AddReplenishActivity.this, getString(R.string.add_require_success_msg), Toast.LENGTH_SHORT).show();
                    finish();

                    return;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
              /*  SPUtils.saveObject(this, CODE_PICK_LIST, pickList);
                SPUtils.saveObject(this, CODE_PRODUCT_LIST, productList);*/
                bundle.putSerializable(CODE_PICK_LIST, (Serializable) pickList);
                bundle.putSerializable(CODE_PRODUCT_LIST, (Serializable) productList);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
            if (isAddAgeinSub) {
                isAddAgeinSub=false;
                checkSize(2);
                EventBus.getDefault().post(new BusEvent(BusEvent.ADD_OTHER_REPLENISH_ITEM, bean));
            }
            getSweetAlertDialog().dismiss();
            CustomToast.makeText(AddReplenishActivity.this, getString(R.string.add_require_success_msg), Toast.LENGTH_SHORT).show();

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
        LogUtils.d("123");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CODE_PRODUCT_LIST, (Serializable) productList);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        saveHelpMyGetRequest(payCode, pickUpItemlist.size() + "", productList);
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
        mAddRequireBrandEt.setText("");
        materId=-1;
        mAddRequireMaterialEt.setText("");
        categoryId=-1;
        mAddRequireClassEt.setText("");
        Hidekeyboard(countEt);
//        subMatchItem = null;
//        classifyMsg.setText(getString(R.string.add_merchandise_classify_msg));
//        albumId = -1;
//        albumNameTv.setText(getString(R.string.add_merchandise_album_msg));
    }

    public boolean checkItemStatus() {

        if (adapter.getCount() <= 1) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_adapter_msg), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(noteEt.getText().toString())) {
            CustomToast.makeText(mContext, getString(R.string.please_enter_item_name), Toast.LENGTH_SHORT).show();
            requestFocus(noteEt);
            Showkeyboard(noteEt);
            itemCount = 0;
            return false;
        }
        if (TextUtils.isEmpty(addressEt.getText().toString())) {
            CustomToast.makeText(mContext, getString(R.string.please_enter_item_note), Toast.LENGTH_SHORT).show();
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
        if (TextUtils.isEmpty(countEt.getText().toString())) {
            CustomToast.makeText(mContext, getString(R.string.pick_up_add_item_count_msg), Toast.LENGTH_SHORT).show();
            requestFocus(countEt);
            Showkeyboard(countEt);
            itemCount = 0;
            return false;
        }
       /* if(TextUtils.isEmpty(mAddRequireBrandEt.getText().toString())){
            CustomToast.makeText(mContext, getString(R.string.hs_name_hint), Toast.LENGTH_SHORT).show();
            requestFocus(mAddRequireBrandEt);
            Showkeyboard(mAddRequireBrandEt);
            itemCount = 0;
            return false;
        }*/
       /* if(materId==-1){
            CustomToast.makeText(mContext, getString(R.string.hs_note_material), Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if(categoryId==-1){
            CustomToast.makeText(mContext, getString(R.string.hs_note_category), Toast.LENGTH_SHORT).show();
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
            for (String str : list) {
                KLog.i("Str " + str);
            }
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
                    holder.merchandiseIv.setBackgroundResource(R.drawable.shape_bg_rectangle_);
                    holder.merchandiseIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent();
                            intent.setClass(mCtx, Down2UpDialogActivity.class);
                            intent.putExtra(Down2UpDialogActivity.GO_THIS_ACTIVITY_INTENT_KEY, Down2UpDialogActivity.ADD_MERCHANT_ITEM_TYPE);
                            startActivityForResult(intent, Constants.ALBUM_GET_BITMAP_REQUEST_CODE);*/
                            itemPos = parentPosition;
                            MPermissions.requestPermissions(AddReplenishActivity.this, 3, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

                        //显示本地图片
                        Uri uri = Uri.fromFile(new File(list.get(position)));
                        //ImageLoader.getInstance().displayImage(uri.toString(), holder.merchandiseIv, options);
                        Glide.with(AddReplenishActivity.this).load(uri.toString()).into(holder.merchandiseIv);

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
        UIUtils.startImageSelectorForAddItem(1000, (ArrayList<String>) itemlist.get(itemPos).getItemUrlList(), AddReplenishActivity.this, 4);
        UpLoadImageManager.getInstance().setUpLoadProgressEmpty();
    }

    @PermissionDenied(3)
    public void requestContactFailed() {
        Toast.makeText(AddReplenishActivity.this, "DENY ACCESS CONTACTS!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void chooseMaterials() {
//        Intent intent = new Intent(mContext, MaterialsSearchActivity.class);
        Intent intent = new MaterialsSearchActivity.IntentBuilder(0).build(AddReplenishActivity.this, new MaterialsSearchActivity.iMaterialsSelectListener() {
            @Override
            public void select(boolean select, MaterialsBean bean) {
                if (select) {
                    mAddRequireMaterialEt.setText(bean.getName());
                    materId = bean.getId();
                }
            }
        });
        startActivity(intent);
    }

    private void chooseCategorys() {
        ChooseCategoryActivity.IntentBuilder intentBuilder = ChooseCategoryActivity.IntentBuilder.build(mContext)
                .setParentID(-1)//设置父级分类ID
                .setWhereFrom(getString(R.string.home_fg_help_03))
                .setSubID(-1)//设置子级分类ID
                .setListener(new ChooseCategoryActivity.ChooseCategoryListener() {//设置监听
                    @Override
                    public void select(boolean select, CategoryBean bean) {
                        LogUtils.e(String.valueOf(select));
                        if (select) {
                            mAddRequireClassEt.setText(bean.getSubName());
                            categoryId = bean.getSubID();
                            classParentId=bean.getParentID();
                            classSubId=bean.getSubID();

                        }
                    }
                });
        startActivity(intentBuilder);

    }


}
