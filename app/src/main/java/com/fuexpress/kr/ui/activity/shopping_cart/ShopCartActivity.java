package com.fuexpress.kr.ui.activity.shopping_cart;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.CartBean;
import com.fuexpress.kr.bean.CartCommodityBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ListViewManager;
import com.fuexpress.kr.model.MyInputFilter;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CartListView;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.CustomListView;
import com.fuexpress.kr.ui.view.CustomPtrHeader;
import com.fuexpress.kr.ui.view.NumberCounter2;
import com.fuexpress.kr.ui.view.OrderMessageView;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.protobuf.GeneratedMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.app.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsCart;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2017-7-3.
 */

public class ShopCartActivity extends BaseActivity {
    private View rootView;
    private TitleBarView titleBarView;
    private ImageView backIv;
    private TextView backTv;
    private TextView grandtotalTv;
    private Button cartGoHomeBtn;
    //    private EditText cartCouponEt;
//    private Button cartUsedCouponBtn;
    private Button cartSettleBtn;
    //单品详情页面
    private ImageView detailsIV;
    private TextView detailsTitleTv;
    private TextView detailsSubTitleTv;
    private TextView detailsPriceTv;
    private TextView detailsPriceTitleTv;
    private TextView detailsSizeTitleTv;
    private TextView detailsSizeTv;
    private TextView detailsColorTitlteTv;
    private TextView detailsColorTv;
    private OrderMessageView destailsOrderMsg;
    private NumberCounter2 detailsNumberCount;
    private ImageView detailsDeleteIv;
    private Button detailsSaveBtn;
    private RelativeLayout bottomLayout;
    private ProgressBar itemDetailLoadingView;
//    private ScrollView mScrollView;

    private FrameLayout mFrameView;
    private LinearLayout mLlComfirmInfo;
    private RelativeLayout detailsTopLayout;
    //    private LinearLayout contentLayout;
    private ListView cartListView;
    private CartAdapter cartAdapter;
    private Map<Integer, Boolean> wareHouseMap = new HashMap<Integer, Boolean>();
    private CustomDialog.Builder dialog;
    private PtrClassicFrameLayout mPtrFrameLayout;
    private LinearLayout emptyLayout;
    private boolean isPutMapDate = true;
    private BottomSheetDialog itemDetailsDialog;
    private LinearLayout itemDetailsLayout;
    private LinearLayout allCheckecdLayout;
    private CheckBox allSelectedCB;

    private boolean childIsChecked = true;
    private boolean mIsRefresh;
    private List<Long> buyList = new ArrayList<Long>();
    private boolean isAllSelected = true;

    //所有商品的封装对象，很重要
    private CartBean bean;
    //所有商品的ID集合，很重要
    private List<Long> itemIdList = new ArrayList<Long>();
    //当前listview中选中的item对象
    private CsCart.SalesCartItem cartItem;
    private SysApplication app;
    private boolean mIsOperaing = false;
    public boolean mIsOpenButtom;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_shop_cart, null);
        itemDetailsLayout = (LinearLayout) rootView.findViewById(R.id.cart_item_details);
        titleBarView = (TitleBarView) rootView.findViewById(R.id.shop_cart_titlebar);
        backIv = titleBarView.getIv_in_title_back();
        backTv = titleBarView.getmTv_in_title_back_tv();
        backTv.setText(getString(R.string.main_source));
        bottomLayout = (RelativeLayout) rootView.findViewById(R.id.grandtotal_ll);
        cartListView = (ListView) rootView.findViewById(R.id.shop_cart_listview);
//        contentLayout = (LinearLayout) rootView.findViewById(R.id.shop_cart_content_layout);
        mPtrFrameLayout = (PtrClassicFrameLayout) rootView.findViewById(R.id.mptr_framelayout);
        emptyLayout = (LinearLayout) rootView.findViewById(R.id.cart_empty_layout);
        grandtotalTv = (TextView) rootView.findViewById(R.id.grandtotal_tv);
        cartGoHomeBtn = (Button) rootView.findViewById(R.id.cart_gohome_btn);
//        cartCouponEt = (EditText) rootView.findViewById(R.id.cart_coupon_et);
//        cartUsedCouponBtn = (Button) rootView.findViewById(R.id.cart_used_coupon_btn);
        cartSettleBtn = (Button) rootView.findViewById(R.id.cart_settle_btn);
        allCheckecdLayout = (LinearLayout) rootView.findViewById(R.id.allChecked_layout);
        allSelectedCB = (CheckBox) rootView.findViewById(R.id.cart_commodity_allselect_cb);
        mLlComfirmInfo = (LinearLayout) rootView.findViewById(R.id.ll_comfirm_info);
        mFrameView = (FrameLayout) rootView.findViewById(R.id.cart_rootview_fl);
        detailsTopLayout = (RelativeLayout) rootView.findViewById(R.id.rl_top);
        itemDetailLoadingView = (ProgressBar) rootView.findViewById(R.id.cart_item_details_loading);
//        mScrollView = (ScrollView) rootView.findViewById(R.id.cart_scrollview);

        //单品详情页
        detailsIV = (ImageView) itemDetailsLayout.findViewById(R.id.details_iv);
        detailsTitleTv = (TextView) itemDetailsLayout.findViewById(R.id.details_title_tv);
        detailsSubTitleTv = (TextView) itemDetailsLayout.findViewById(R.id.details_subTitle_tv);
        detailsPriceTv = (TextView) itemDetailsLayout.findViewById(R.id.details_price_tv);
        detailsPriceTitleTv = (TextView) itemDetailsLayout.findViewById(R.id.details_price_title_tv);
        detailsSizeTitleTv = (TextView) itemDetailsLayout.findViewById(R.id.details_size_title_tv);
        detailsSizeTv = (TextView) itemDetailsLayout.findViewById(R.id.details_size_tv);
        detailsColorTitlteTv = (TextView) itemDetailsLayout.findViewById(R.id.details_color_title_tv);
        detailsColorTv = (TextView) itemDetailsLayout.findViewById(R.id.details_color_tv);
        destailsOrderMsg = (OrderMessageView) itemDetailsLayout.findViewById(R.id.details_order_msg);
        detailsDeleteIv = (ImageView) itemDetailsLayout.findViewById(R.id.details_delete_iv);
        detailsSaveBtn = (Button) itemDetailsLayout.findViewById(R.id.details_save_btn);
        detailsNumberCount = (NumberCounter2) itemDetailsLayout.findViewById(R.id.details_nc);
        detailsNumberCount.mNumber.setInputType(InputType.TYPE_CLASS_NUMBER);

        app = (SysApplication) getApplication();

//        killEditFocused();

        CustomPtrHeader header = new CustomPtrHeader(this);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(true);
            }
        }, 150);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                ViewGroup parentGroup = (ViewGroup) content;
//                ViewGroup childGroup  = (ViewGroup) parentGroup.getChildAt(1);
//                ViewGroup child1Group  = (ViewGroup) childGroup.getChildAt(0);
//                ViewGroup child2Group  = (ViewGroup) child1Group.getChildAt(0);
//
//                ViewGroup viewGroup = (ViewGroup) childGroup.getChildAt(0);

//                if (parentGroup instanceof ScrollView || parentGroup instanceof AbsListView) {
//                    return parentGroup.getScrollY() == 0;
//                }
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                rootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrameLayout.refreshComplete();
                        getSalesCartListRequest();
                    }
                }, 100);
            }
        });

        backTv.setOnClickListener(this);
        backIv.setOnClickListener(this);
        detailsSaveBtn.setOnClickListener(this);
//        cartUsedCouponBtn.setOnClickListener(this);
        cartGoHomeBtn.setOnClickListener(this);
        cartSettleBtn.setOnClickListener(this);
        allCheckecdLayout.setOnClickListener(this);
        detailsTopLayout.setOnClickListener(this);
        allSelectedCB.setOnClickListener(this);
        return rootView;
    }

//    private void setData() {
//        CartBean bean = new CartBean();
//        bean.wareHouse = "测试数据1";
//        bean.commodity.add("商品1-1");
//        bean.commodity.add("商品1-2");
//        bean.commodity.add("商品1-3");
//        data.add(bean);
//        CartBean bean2 = new CartBean();
//        bean2.wareHouse = "测试数据2";
//        bean2.commodity.add("商品2-1");
//        bean2.commodity.add("商品2-2");
//        data.add(bean2);
//    }


    @Override
    protected void onResume() {
        super.onResume();
        app.setQtyCount(0);
        getSalesCartListRequest();
    }

    public void getSalesCartListRequest() {
        isPutMapDate = true;
        //清空商品ID集合
        itemIdList.clear();
        app.setQtyCount(0);
        CsCart.GetSalesCartListRequest.Builder builder = CsCart.GetSalesCartListRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.GetSalesCartListResponse>() {

            @Override
            public void onSuccess(CsCart.GetSalesCartListResponse response) {
                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                cartHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });

    }


    private Handler cartHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CsCart.GetSalesCartListResponse response = (CsCart.GetSalesCartListResponse) msg.obj;
            if (response.getItemsList() == null || response.getItemsCount() == 0) {
                emptyLayout.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.GONE);
                cartListView.setVisibility(View.GONE);
                return;
            } else {
                emptyLayout.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.VISIBLE);
                cartListView.setVisibility(View.VISIBLE);

                grandtotalTv.setText(UIUtils.getCurrency(ShopCartActivity.this, response.getGrandtotal()));
                bean = new CartBean();
                bean.setSubtotal(response.getSubtotal());
                bean.setGrandtotal(bean.getGrandtotal());
                int qtyCount = 0;
                for (int i = 0; i < response.getWarehousesCount(); i++) {
                    CartBean.WareHouse wh = new CartBean.WareHouse();
                    wh.warehouse_id = response.getWarehouses(i).getWarehouseId();
                    wh.name = response.getWarehouses(i).getName();
                    wh.desc = response.getWarehouses(i).getDesc();
                    wh.fulladdress = response.getWarehouses(i).getFulladdress();
                    wh.receiver = response.getWarehouses(i).getReceiver();
                    wh.phone = response.getWarehouses(i).getPhone();
                    wh.postcode = response.getWarehouses(i).getPostcode();
                    bean.warehouseList.add(wh);

                    for (int j = 0; j < response.getItemsCount(); j++) {
                        itemIdList.add(response.getItems(j).getCartItemId());
                        qtyCount += response.getItems(j).getQty();
                        if (response.getWarehouses(i).getWarehouseId() == response.getItems(j).getWarehouseId()) {
                            wh.salesCartItems.add(response.getItems(j));
                        }
                    }
                }
                app.setQtyCount(qtyCount);
                for (int i = 0; i < bean.warehouseList.size(); i++) {
                    int count = 0;
                    for (int j = 0; j < bean.warehouseList.get(i).salesCartItems.size(); j++) {
                        if(bean.warehouseList.get(i).salesCartItems.get(j).getIsSelected()){
                            count++;
                        }
                        if(count == bean.warehouseList.get(i).salesCartItems.size()){
                            wareHouseMap.put(i, true);
                        }else {
                            wareHouseMap.put(i, false);
                        }
                    }

                }
                int wareHouseCount = 0;
                for (int i = 0; i < wareHouseMap.size(); i++) {
                    if(wareHouseMap.get(i)){
                        wareHouseCount++;
                    }
                }
                if(wareHouseCount == bean.warehouseList.size()){
                    isAllSelected = true;
                    allSelectedCB.setChecked(true);
                }else {
                    isAllSelected = false;
                    allSelectedCB.setChecked(false);
                }
                cartAdapter = new CartAdapter(ShopCartActivity.this, bean.warehouseList);
                cartListView.setAdapter(cartAdapter);
//                ListViewManager.setListViewHeightBasedOnChildren(cartListView);
            }

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.details_save_btn:
                modityCart(cartItem.getCartItemId(), destailsOrderMsg.getMessage(), detailsNumberCount.getCurrentNumber());
                break;
            case R.id.cart_settle_btn:
                if (buyList.size() > 0) {
                    showDialog();
                    return;
                }
                if (countWareHouses() > 1) {
                    showWareHouseDialog();
                } else {
                    toOrderActivity();
                }
                break;
            case R.id.cart_commodity_allselect_cb:
                isAllSelected = !isAllSelected;
                allSelectedCB.setChecked(isAllSelected);
//                setWareHouseChecked(isAllSelected);
                selectedSalesCartRequest(itemIdList, isAllSelected ? 1 : 2, true, true);
                break;
//            case R.id.cart_used_coupon_btn:
//                String coupon = cartCouponEt.getText().toString();
//                usedCoupon(coupon);
//                break;
            case R.id.rl_top:
                closeDetailsLayout();
                Hidekeyboard(destailsOrderMsg.mMessage);
                break;
            case R.id.cart_gohome_btn:
                EventBus.getDefault().post(new BusEvent(BusEvent.GO_PRODUSRC_PAGE, null));
                break;
        }
    }

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
                .setName("ShopCart Page") // TODO: Define a title for the content shown.
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

    class CartAdapter extends BaseAdapter {

        private Context context;
        private List<CartBean.WareHouse> list;

        public CartAdapter(Context context, List<CartBean.WareHouse> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
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
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.shop_cart_item, null);
                holder = new ViewHolder();
                holder.wareHouseLayout = (LinearLayout) convertView.findViewById(R.id.shop_cart_item_warehouse_layout);
                holder.wareHousetTv = (TextView) convertView.findViewById(R.id.shop_cart_item_warehouses_tv);
                holder.wareHouseBox = (CheckBox) convertView.findViewById(R.id.cart_commodity_title_cb);
                holder.commodityListView = (CustomListView) convertView.findViewById(R.id.shop_cart_item_listview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final CartBean.WareHouse bean = list.get(position);
            holder.wareHousetTv.setText(bean.name);
            final ViewHolder finalHolder = holder;
            finalHolder.wareHouseBox.setChecked(wareHouseMap.get(position));

            if (isPutMapDate) {
                final CommodityAdapter commodityAdapter = new CommodityAdapter(context, bean.salesCartItems, position);
                holder.commodityListView.setAdapter(commodityAdapter);
//                ListViewManager.setListViewHeightBasedOnChildren(holder.commodityListView);

                holder.wareHouseLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wareHouseMap.put(position, !wareHouseMap.get(position));
                        finalHolder.wareHouseBox.setChecked(wareHouseMap.get(position));
                        commodityAdapter.setAllChecked(wareHouseMap.get(position));
                        commodityAdapter.notifyDataSetChanged();

                        List<Long> ids = new ArrayList<Long>();
                        for (int i = 0; i < bean.salesCartItems.size(); i++) {
                            ids.add(bean.salesCartItems.get(i).getCartItemId());
                        }
                        allSelectedCB.setChecked(finalHolder.wareHouseBox.isChecked());
                        selectedSalesCartRequest(ids, finalHolder.wareHouseBox.isChecked() ? 1 : 2, false, false);

                    }
                });
            }

            return convertView;
        }

        public class ViewHolder {
            LinearLayout wareHouseLayout;
            CheckBox wareHouseBox;
            TextView wareHousetTv;
            CustomListView commodityListView;
        }

        public void setPositionChecked(int position, boolean isChecked) {
            wareHouseMap.put(position, isChecked);
        }
    }

    class CommodityAdapter extends BaseAdapter {

        private Context context;
        private List<CsCart.SalesCartItem> list;
        private int parentPosition;
        private Map<Integer, Boolean> commodityMap;
        private DisplayImageOptions imgOptions;
        private ImageLoader imgLoader;

        public CommodityAdapter(Context context, List<CsCart.SalesCartItem> list, int parentPosition) {
            this.context = context;
            this.list = list;
            this.parentPosition = parentPosition;
            commodityMap = new HashMap<Integer, Boolean>();
            for (int j = 0; j < list.size(); j++) {
                commodityMap.put(j, list.get(j).getIsSelected());
            }
            this.imgOptions = ImageLoaderHelper.getInstance(context).getDisplayOptions();
            this.imgLoader = ImageLoader.getInstance();
        }

        @Override
        public int getCount() {
            return list.size();
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
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.cart_commodity_item, null);
                holder = new ViewHolder();
                holder.titleTv = (TextView) convertView.findViewById(R.id.commodity_titile_tv);
                holder.commodityBox = (CheckBox) convertView.findViewById(R.id.commodity_cb);

                holder.commodityIV = (ImageView) convertView.findViewById(R.id.commodity_iv);
                holder.sellerTV = (TextView) convertView.findViewById(R.id.commodity_seller_tv);
                holder.buyFromTV = (TextView) convertView.findViewById(R.id.commodity_buyfrom_tv);
                holder.sizeTitleTV = (TextView) convertView.findViewById(R.id.commodity_size_title_tv);
                holder.sizeTV = (TextView) convertView.findViewById(R.id.commodity_size_tv);
                holder.colorTitleTv = (TextView) convertView.findViewById(R.id.commodity_color_title_tv);
                holder.colorTv = (TextView) convertView.findViewById(R.id.commodity_color_tv);
                holder.priceTV = (TextView) convertView.findViewById(R.id.commodity_price_tv);
                holder.noteLL = (LinearLayout) convertView.findViewById(R.id.note_ll);
                holder.noteTV = (TextView) convertView.findViewById(R.id.commodity_note_tv);
                holder.numberCounter = (NumberCounter2) convertView.findViewById(R.id.commodity_nc);
                holder.removeSalesIV = (ImageView) convertView.findViewById(R.id.commodity_delete_iv);
                holder.soldOutTv = (TextView) convertView.findViewById(R.id.sold_out_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final CsCart.SalesCartItem item = list.get(position);
            if (item.getCannotBuyit()) {
                buyList.add(item.getCartItemId());
            }

            if (item.getExtendsCount() > 0) {
                holder.sizeTV.setVisibility(View.VISIBLE);
                holder.sizeTV.setText(item.getExtends(0).getAttr().getAttrName() + ": " + item.getExtends(0).getOptions(0).getOptionName());
            } else {
                holder.sizeTV.setVisibility(View.GONE);
            }
            holder.titleTv.setText(item.getSubtitle());
            imgLoader.displayImage(item.getImageUrl() + Constants.ImgUrlSuffix.dp_list, holder.commodityIV, imgOptions);
            holder.sellerTV.setText(item.getSeller());
            holder.buyFromTV.setText(item.getBuyfrom());
            String currencyCode = UIUtils.getCurrency(context, item.getUnitPrice());
            holder.priceTV.setText(currencyCode);
            if (!item.getNote().toString().equals("") && item.getNote().length() > 0) {
                holder.noteTV.setText(item.getNote());
                holder.noteLL.setVisibility(View.VISIBLE);
            } else {
                holder.noteLL.setVisibility(View.GONE);
            }
            holder.numberCounter.init(item.getMinQtyAllow(), item.getMaxQtyAllow() > item.getStock() ? item.getStock() : item.getMaxQtyAllow());
            holder.numberCounter.setNumberText(item.getQty());
            holder.numberCounter.getCurrentNumber();
            if (item.getCannotBuyit()) {
                holder.soldOutTv.setVisibility(View.VISIBLE);
                holder.numberCounter.setVisibility(View.GONE);
            } else {
                holder.soldOutTv.setVisibility(View.GONE);
                holder.numberCounter.setVisibility(View.VISIBLE);
            }
            MyInputFilter filter = new MyInputFilter();
//            holder.numberCounter.mNumber.setFilters(new InputFilter[]{filter});
            holder.numberCounter.mNumber.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ((ViewGroup) v.getParent())
                            .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                    EditText edit = (EditText) v;
                    edit.requestFocus();
                    Showkeyboard((EditText) edit);
                    return false;
                }
            });

            holder.numberCounter.setOnCounterClickListener(new NumberCounter2.OnCounterClickListener() {
                @Override
                public void onPlusClick(NumberCounter2 view) {
//                    view.plus();
                    int count = app.getQtyCount();
                    sendCartQty(++count);
//                    app.setQtyCount(count);
                    modityCart(item, view.getCurrentNumber());
                }

                @Override
                public void onMinusClick(NumberCounter2 view) {
                    int count = app.getQtyCount();
                    sendCartQty(--count);
//                    app.setQtyCount(count);
                    modityCart(item, view.getCurrentNumber());
//                    }
                }
            });
            holder.removeSalesIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(false, item.getCartItemId());
                }
            });

            holder.commodityBox.setChecked(commodityMap.get(position));
            final ViewHolder finalHolder = holder;
            holder.commodityBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commodityMap.put(position, !commodityMap.get(position));
                    finalHolder.commodityBox.setChecked(commodityMap.get(position));
                    int checkedCount = 0;
                    for (int i = 0; i < commodityMap.size(); i++) {
                        if (commodityMap.get(i)) {
                            checkedCount++;
                        }
                    }
//                    cartListView.isMeasure = false;
                    isPutMapDate = false;
                    if (checkedCount == commodityMap.size()) {
                        cartAdapter.setPositionChecked(parentPosition, true);
                    } else {
                        cartAdapter.setPositionChecked(parentPosition, false);
                    }
                    cartAdapter.notifyDataSetChanged();
                    int wareHouseCount = 0;

                    for (int i = 0; i < wareHouseMap.size(); i++) {
                        if(wareHouseMap.get(i)){
                            wareHouseCount++;
                        }
                    }
                    if(wareHouseCount == bean.warehouseList.size()){
                        isAllSelected = true;
                        allSelectedCB.setChecked(true);
                    }else {
                        isAllSelected = false;
                        allSelectedCB.setChecked(false);
                    }
                    childIsChecked = finalHolder.commodityBox.isChecked();

                    mIsRefresh = true;
                    int count = app.getQtyCount();
                    if (!childIsChecked) {
                        count = count - item.getQty();
                        app.setQtyCount(count);
                        if (mIsRefresh) {
                            selectedSalesCartRequest(item.getCartItemId(), 2);
                        }
                    } else {
                        count = count + item.getQty();
                        app.setQtyCount(count);
//                        sendCartQty(count);
                        CartCommodityBean bean = new CartCommodityBean();
                        bean.setQty(count);
                        bean.setItemID(item.getItemId());
                        bean.setNote(item.getNote());
                        if (item.getExtendsCount() > 0) {
                            if (item.getExtends(0).getOptionsCount() > 0) {
                                CsBase.ItemExtendOption option = item.getExtends(0).getOptions(0);
                                CsBase.PairIntInt pairIntInt = CsBase.PairIntInt.newBuilder().setK(item.getExtends(0).getAttr().getAttrId()).setV(option.getOptionId()).build();
                                bean.setSizePairIntInt(pairIntInt);
                            }
                        }
                        if (mIsRefresh) {
                            selectedSalesCartRequest(item.getCartItemId(), 1);
                        }
                    }
                }
            });

            final ViewHolder finalHolder1 = holder;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartItem = item;
                    imgLoader.displayImage(item.getImageUrl() + Constants.ImgUrlSuffix.dp_list, detailsIV, imgOptions);
                    detailsTitleTv.setText(item.getTitle());
                    detailsSubTitleTv.setText(item.getSubtitle());
//                    detailsPriceTitleTv.setText(UIUtils.getCurrency(mContext));
                    detailsPriceTv.setText(UIUtils.getCurrency(ShopCartActivity.this, item.getUnitPrice()));
                    if (item.getNote() != null && item.getNote().length() > 0) {
//                detailsNoteEt.setText(cartItem.getNote());
                        destailsOrderMsg.setMessage(item.getNote());
                    } else {
//                detailsNoteEt.setHint(getResources().getString(R.string.String_details_order_note));
                        destailsOrderMsg.setMessage(" ");
                        destailsOrderMsg.setHint(getResources().getString(R.string.String_details_order_note));
                    }
                    if (item.getExtendsCount() > 0) {
                        if (item.getExtends(0).getOptionsCount() > 0) {
                            detailsSizeTitleTv.setVisibility(View.VISIBLE);
                            detailsSizeTv.setText(item.getExtends(0).getOptions(0).getOptionName());
                        }
                        if (item.getExtendsList().size() > 1) {
                            detailsColorTitlteTv.setVisibility(View.VISIBLE);
                            detailsColorTv.setText(item.getExtends(1).getOptions(0).getOptionName());
                        } else {
                            detailsColorTitlteTv.setVisibility(View.GONE);
                        }
                    } else {
                        detailsSizeTv.setText("");
                        detailsColorTv.setText("");
                        detailsSizeTitleTv.setVisibility(View.GONE);
                        detailsColorTitlteTv.setVisibility(View.GONE);
                    }

                    detailsNumberCount.init(item.getMinQtyAllow(), item.getStock());
                    detailsNumberCount.setNumberText(finalHolder1.numberCounter.getCurrentNumber());
                    detailsDeleteIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog(true, item.getCartItemId());
                        }
                    });
                    detailsNumberCount.setOnCounterClickListener(new NumberCounter2.OnCounterClickListener() {
                        @Override
                        public void onPlusClick(NumberCounter2 view) {
//                    view.plus();
                            //selectedSaleCart(position, item.getCartItemId());
                        }

                        @Override
                        public void onMinusClick(NumberCounter2 view) {
//                    view.minus();
                            //removeSaleCart(item.getCartItemId());
                        }
                    });
                    switchBottomState();

//                    if (itemDetailsLayout.getParent() != null) {
//                        ((ViewGroup) itemDetailsLayout.getParent()).removeView(itemDetailsLayout);
//
//                    }
//                    itemDetailsDialog = new BottomSheetDialog(ShopCartActivity.this, com.rey.material.R.style.Material_App_BottomSheetDialog);
//                    itemDetailsDialog.heightParam(ViewGroup.LayoutParams.WRAP_CONTENT);
//                    itemDetailsDialog.contentView(itemDetailsLayout)
//                            .show();
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView titleTv;
            CheckBox commodityBox;
            ImageView commodityIV;
            TextView sellerTV;
            TextView buyFromTV;
            TextView sizeTitleTV;
            TextView sizeTV;
            TextView colorTitleTv;
            TextView colorTv;
            TextView currencyTv;
            TextView priceTV;
            LinearLayout noteLL;
            TextView noteTV;
            NumberCounter2 numberCounter;
            ImageView removeSalesIV;
            TextView soldOutTv;
        }

        public void setAllChecked(boolean isChecked) {
            for (int i = 0; i < commodityMap.size(); i++) {
                commodityMap.put(i, isChecked);
            }
        }
    }

    public void selectedSalesCartRequest(List<Long> list, int action, final boolean mIsRefresh, final boolean isAllSelected) {
        CsCart.SelectedSalesCartRequest.Builder builder = CsCart.SelectedSalesCartRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.addAllCartItemIds(list);
        builder.setAction(action);
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.SelectedSalesCartResponse>() {
            @Override
            public void onSuccess(final CsCart.SelectedSalesCartResponse response) {
                ShopCartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        grandtotalTv.setText(UIUtils.getCurrency(ShopCartActivity.this, response.getGrandtotal()));
                        changeSettleBtnState(response.getGrandtotal());
                        if (mIsRefresh) {
                            getSalesCartListRequest();
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    public void selectedSalesCartRequest(long itemId, int action) {
        CsCart.SelectedSalesCartRequest.Builder builder = CsCart.SelectedSalesCartRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.addCartItemIds(itemId);
        builder.setAction(action);
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.SelectedSalesCartResponse>() {
            @Override
            public void onSuccess(final CsCart.SelectedSalesCartResponse response) {
                ShopCartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        grandtotalTv.setText(UIUtils.getCurrency(ShopCartActivity.this, response.getGrandtotal()));
                        changeSettleBtnState(response.getGrandtotal());
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    /**
     * 移除购物车
     *
     * @param cartItemID
     * @param isDialog
     */
    public void removeSaleCart(long cartItemID, final boolean isDialog) {
        CsCart.RemoveSalesCartRequest.Builder builder = CsCart.RemoveSalesCartRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCartItemId(cartItemID);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.RemoveSalesCartResponse>() {
            @Override
            public void onSuccess(final CsCart.RemoveSalesCartResponse response) {
                LogUtils.d(response.toString());
                if (isDialog) {
                    int count = app.getQtyCount();
                    app.setQtyCount(count--);
                    sendCartQty(count);
                    getSalesCartListRequest();
                }
                ShopCartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        grandtotalTv.setText(UIUtils.getCurrency(ShopCartActivity.this, response.getGrandtotal()));

                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }


    /**
     * 批量移除购物车
     *
     * @param cartItemIds
     */
    public void removeBatchSalce(final List<Long> cartItemIds) {
        CsCart.RemoveBatchSalesCartRequest.Builder builder = CsCart.RemoveBatchSalesCartRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.addAllCartItemIds(cartItemIds);
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.RemoveBatchSalesCartResponse>() {
            @Override
            public void onSuccess(final CsCart.RemoveBatchSalesCartResponse response) {
                LogUtils.d(response.toString());
                buyList.remove(cartItemIds);
                int count = app.getQtyCount();
                if (count - cartItemIds.size() > 0) {
                    app.setQtyCount(count - cartItemIds.size());
                } else {
                    app.setQtyCount(0);
                }
                sendCartQty(count);
                getSalesCartListRequest();
                ShopCartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        grandtotalTv.setText(getString(R.string.String_price, response.getGrandtotal()));
                        grandtotalTv.setText(UIUtils.getCurrency(ShopCartActivity.this, response.getGrandtotal()));

                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }


    /**
     * 修改购物车
     *
     * @param cardItemId
     * @param note
     * @param qty
     */
    public void modityCart(long cardItemId, String note, int qty) {
        closeDetailsLayout();
        itemDetailLoadingView.setVisibility(View.VISIBLE);
        CsCart.ModifySalesCartRequest.Builder builder = CsCart.ModifySalesCartRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCartItemId(cardItemId);
        builder.setQty(qty);
        builder.setNote(note);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.ModifySalesCartResponse>() {

            @Override
            public void onSuccess(final CsCart.ModifySalesCartResponse response) {
                ShopCartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemDetailLoadingView.setVisibility(View.GONE);
                        Hidekeyboard(destailsOrderMsg.mMessage);
                        grandtotalTv.setText(UIUtils.getCurrency(ShopCartActivity.this, response.getGrandtotal()));
                        getSalesCartListRequest();
                    }
                });

            }

            @Override
            public void onFailure(int ret, String errMsg) {
                itemDetailLoadingView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 修改购物车
     *
     * @param item
     * @param qty
     */
    public void modityCart(CsCart.SalesCartItem item, int qty) {
        CsCart.ModifySalesCartRequest.Builder builder = CsCart.ModifySalesCartRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCartItemId(item.getCartItemId());
        builder.setQty(qty);
        builder.setNote(item.getNote());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.ModifySalesCartResponse>() {

            @Override
            public void onSuccess(final CsCart.ModifySalesCartResponse response) {
                ShopCartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        grandtotalTv.setText(UIUtils.getCurrency(ShopCartActivity.this, response.getGrandtotal()));
                    }
                });

            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    /**
     * 使用优惠券
     *
     * @param coupon
     */
    public void usedCoupon(String coupon) {
        CsCart.ApplyCouponRequest.Builder builder = CsCart.ApplyCouponRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCoupon(coupon);
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.ApplyCouponResponse>() {

            @Override
            public void onSuccess(CsCart.ApplyCouponResponse response) {
                LogUtils.d(response.toString());

            }

            @Override
            public void onFailure(int ret, String errMsg) {
                LogUtils.d("-----------------" + errMsg);
                ShopCartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShopCartActivity.this, getString(R.string.use_coupon_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void changeSettleBtnState(float grandTotal) {
        if (grandTotal <= 0) {
            cartSettleBtn.setEnabled(false);
            cartSettleBtn.setBackground(ContextCompat.getDrawable(ShopCartActivity.this, R.drawable.shape_btn_bg_red_more_radius_unenable));
        } else {
            cartSettleBtn.setEnabled(true);
            cartSettleBtn.setBackground(ContextCompat.getDrawable(ShopCartActivity.this, R.drawable.selector_red_btn_drawable));

        }
    }

    public int countWareHouses() {
        int count = 0;
        for (int i = 0; i < wareHouseMap.size(); i++) {
            if (wareHouseMap.get(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 是否删除单品Dialog
     *
     * @param isDetails
     * @param cartItemID
     */
    public void showDialog(final boolean isDetails, final long cartItemID) {

        dialog = new CustomDialog.Builder(this);
        dialog.setMessage(R.string.exit_app);
        dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isDetails) {
                    closeDetailsLayout();
                }
                removeSaleCart(cartItemID, true);
                dialog.dismiss();
//                            notifyDataSetChanged();
            }
        });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isDetails) {
                    switchBottomState();
                }
            }
        });
        dialog.create().show();
    }

    public void showWareHouseDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.cart_dialog_title));
        builder.setMessage(String.format(getResources().getString(R.string.cart_dialog_msg), countWareHouses()));
        builder.setPositiveButton(getResources().getString(R.string.cart_dialog_btn_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toOrderActivity();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cart_dialog_btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        CustomDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 无货商品提示Dialog
     */
    public void showDialog() {
        dialog = new CustomDialog.Builder(this);
        dialog.setMessage(R.string.sold_out_msg);
        dialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                removeBatchSalce(buyList);
            }
        });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    public void toOrderActivity() {

        Intent intent = new Intent(this, CartOrderActivity.class);
        Bundle bundle = new Bundle();
//        bundle.putSerializable("commoditysBean", (Serializable) list);
//        bundle.putSerializable("salesCartResponse", response);
//        intent.putExtra("grandTotal", rsp.getGrandtotal());
//        intent.putExtra("qtyCount", app.getQtyCount());
//        intent.putExtras(bundle);
        startActivity(intent);

        /*
        //跳转Html5订单界面
        Intent intent = new Intent(mContext, OrderActivity.class);
        startActivity(intent);
        */
    }

    public void sendCartQty(int count) {
        EventBus.getDefault().post(new BusEvent(BusEvent.SHOP_CART_COMMODITY_COUNT, null, count));
    }

//    private void killEditFocused() {
//            /*解决editedText抢焦点*/
//        contentLayout.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
//        contentLayout.setFocusable(true);
//        contentLayout.setFocusableInTouchMode(true);
//        contentLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                v.requestFocusFromTouch();
//                return false;
//            }
//        });
//    }

    public void startAnima(int fromWhere, int toPlace) {
        ValueAnimator animator = ValueAnimator.ofInt(fromWhere, toPlace);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLlComfirmInfo.getLayoutParams();
                params.height = height;
                mFrameView.updateViewLayout(mLlComfirmInfo, params);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsOperaing = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsOperaing = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mIsOperaing = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public void switchBottomState() {
        int height = getWindowManager().getDefaultDisplay().getHeight();
        if (!mIsOpenButtom && !mIsOperaing) {
//            mScItemContent.smoothScrollTo(0, 0);
            startAnima(0, height);
            mIsOpenButtom = true;
        } else if (mIsOpenButtom && !mIsOperaing) {
            startAnima(height, 0);
            mIsOpenButtom = false;
        }
    }

    public void closeDetailsLayout() {
        int height = getWindowManager().getDefaultDisplay().getHeight();
        startAnima(height, 0);
        mIsOpenButtom = false;
    }
}