package com.fuexpress.kr.ui.activity.shopping_cart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.bean.CommoditysBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.AddNewAddressActivity;
import com.fuexpress.kr.ui.activity.AddressManagerActivity;
import com.fuexpress.kr.ui.activity.ContractServiceActivity;
import com.fuexpress.kr.ui.activity.help_signed.data.WareHouseBean;
import com.fuexpress.kr.ui.adapter.OrderCommodityAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.CustomListView;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fksproto.CsAddress;
import fksproto.CsBase;
import fksproto.CsCart;
import fksproto.CsShipping;


/**
 * Created by Administrator on 2016/4/18.
 */
public class DeliveryActivity extends BaseActivity {

    public final static String ORDER_TYPE = "order_type";
    private final static String TAG = "DelivertActivity";
    private View rootView;
    private TextView confirmOrderTv;
    private RelativeLayout mergeOrderLayout;
    private RelativeLayout directMailLayout;
    private RelativeLayout shippingLayout;
    private LinearLayout commodityLayout;
    private RelativeLayout addressLayout;

    private ImageView mergeOrderIv;
    private ImageView directMailIv;
    private TextView nameAndPhoneTv;
    private TextView phoneTv;
    private Button confirmBtn;
    private TextView shippingTitle;
    private TextView shippingInfo;
    private TextView shippingFee;
    private ImageView mergeOrerHelpIv;
    private ImageView directMailHelpIv;
    private TextView defaultAddressIv;

    private List<CommoditysBean> list;
    private WareHouseBean whBean;
    private CommoditysBean cb;
    private boolean isOpen = false;
    private List<CsBase.PairIntInt> pairList;
    private List<CsBase.PairIntInt> crowdPairList = new ArrayList<>();
    private CsAddress.CustomerAddress address;
    private CsAddress.CustomerAddress callBackaddress;

    private int shippingScheme;
    private CsShipping.Shipping shipping;
    private CsShipping.Shipping mShipping;
    private CommoditysBean commoditysBean;
    private boolean isCrowdOrder;
    private CommoditysBean commoditysBean2;
    private List<CsCart.SalesCartItem> itemList;
    private List<CsBase.Warehouse> warehouseList;
    private int warehouseId;
    private List<CsShipping.Shipping> shippingList = new ArrayList<CsShipping.Shipping>();
    private int count = 0;
    private ImageView toBackIv;
    private Map<Long, Double> feeMap = new HashMap<Long, Double>();
    private Double fee;
    private Map<Integer, CsShipping.Shipping> shipMap = new HashMap<Integer, CsShipping.Shipping>();
    private Map<Integer, Boolean> checkMap = new HashMap<Integer, Boolean>();
    private List<CsShipping.Shipping> shipList = new ArrayList<CsShipping.Shipping>();
    private float dutyRate;
    private float shippingDuty;
    private Map<Integer, Float> dutyMap = new HashMap<Integer, Float>();

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_cart_delivery, null);
        TitleBarView titleBarView = (TitleBarView) rootView.findViewById(R.id.deliverty_titlebar);
        titleBarView.setTitleText(getString(R.string.delivery_title_bar_title));
        toBackIv = titleBarView.getIv_in_title_back();
//        confirmOrderTv = titleBarView.getConfirmOrderTv();

        Intent intent = getIntent();
        itemList = (List<CsCart.SalesCartItem>) intent.getExtras().getSerializable("itemList");
        warehouseList = (List<CsBase.Warehouse>) intent.getExtras().getSerializable("warehouseList");
        address = (CsAddress.CustomerAddress) intent.getExtras().get("address");
        shippingScheme = intent.getExtras().getInt("shippingScheme", 2);
//        shipList = (List<CsShipping.Shipping>) intent.getExtras().getSerializable("shipList");
        isCrowdOrder = intent.getBooleanExtra(ORDER_TYPE, false);

        mergeOrderLayout = (RelativeLayout) rootView.findViewById(R.id.delivery_merge_order_layout);
        directMailLayout = (RelativeLayout) rootView.findViewById(R.id.delivery_direct_mail_layout);

        addressLayout = (RelativeLayout) rootView.findViewById(R.id.delivery_address_layout);
        commodityLayout = (LinearLayout) rootView.findViewById(R.id.delivery_commodity_layout);
        mergeOrderIv = (ImageView) rootView.findViewById(R.id.delivery_merge_order_iv);
        directMailIv = (ImageView) rootView.findViewById(R.id.delivery_direct_mail_iv);
        nameAndPhoneTv = (TextView) rootView.findViewById(R.id.delivery_name_and_phone_tv);
        phoneTv = (TextView) rootView.findViewById(R.id.delivery_phone_tv);
        confirmBtn = (Button) rootView.findViewById(R.id.delivery_confirm_btn);

        mergeOrerHelpIv = (ImageView) rootView.findViewById(R.id.delivery_merge_order_help_iv);
        directMailHelpIv = (ImageView) rootView.findViewById(R.id.delivery_direct_mail_help_iv);
        defaultAddressIv = (TextView) rootView.findViewById(R.id.delivery_default_address_iv);

        if (shippingScheme == Constants.SHIPPING_SCHEME_MERGE) {
            mergeOrderIv.setImageResource(R.mipmap.cart_select);
            directMailIv.setImageResource(R.mipmap.cart_unselect);
            commodityLayout.setVisibility(View.GONE);
//            addressLayout.setVisibility(View.GONE);
//            shippingLayout.setVisibility(View.GONE);
        } else if (shippingScheme == Constants.SHIPPING_SCHEME_DIRECT) {
            mergeOrderIv.setImageResource(R.mipmap.cart_unselect);
            directMailIv.setImageResource(R.mipmap.cart_select);
            commodityLayout.setVisibility(View.VISIBLE);
//            addressLayout.setVisibility(View.VISIBLE);

//            shippingLayout.setVisibility(View.VISIBLE);
        }
        if (isCrowdOrder) {
            getShipping();
        } else {
            getSaleCartItem();
        }

        if (address == null) {
            getCustomerAddressList();
        } else {
            setAddress(address);
            if (isCrowdOrder) {
                getCrowdShipping(address);
            } else {
            }
        }

        toBackIv.setOnClickListener(this);
        addressLayout.setOnClickListener(this);
        mergeOrderLayout.setOnClickListener(this);
        directMailLayout.setOnClickListener(this);
//        confirmOrderTv.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        mergeOrerHelpIv.setOnClickListener(this);
        directMailHelpIv.setOnClickListener(this);

        return rootView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOpen = false;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.confirm_order_tv:
                finish();
                break;
            case R.id.delivery_address_layout:
                /*intent = new Intent(this, PackageAddressActivity.class);
                if (address != null) {
                    intent.putExtra("addressID", address.getAddressId());
                }
                startActivityForResult(intent, Constants.ADDRESS_REQUEST_CODE);*/
                intent = new Intent(this, AddressManagerActivity.class);
                intent.putExtra(AddressManagerActivity.KEY_IS_CHOOSE_TYPE, true);
                startActivityForResult(intent, Constants.ADDRESS_REQUEST_CODE);
                break;
            case R.id.delivery_merge_order_layout:
                mergeOrderIv.setImageResource(R.mipmap.cart_select);
                directMailIv.setImageResource(R.mipmap.cart_unselect);
                commodityLayout.setVisibility(View.GONE);
//                shippingLayout.setVisibility(View.GONE);
//                addressLayout.setVisibility(View.GONE);
                shippingScheme = Constants.SHIPPING_SCHEME_MERGE;
                break;
            case R.id.delivery_direct_mail_layout:
                mergeOrderIv.setImageResource(R.mipmap.cart_unselect);
                directMailIv.setImageResource(R.mipmap.cart_select);
                commodityLayout.setVisibility(View.VISIBLE);
//                addressLayout.setVisibility(View.VISIBLE);
//                shippingLayout.setVisibility(View.VISIBLE);
                shippingScheme = Constants.SHIPPING_SCHEME_DIRECT;
                break;
            case R.id.delivery_confirm_btn:
                Iterator iter = shipMap.entrySet().iterator();
                shippingList.clear();
                if (shipMap != null && shipMap.size() > 0) {
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        int key = (int) entry.getKey();
                        CsShipping.Shipping value = (CsShipping.Shipping) entry.getValue();
                        shippingList.add(value);
                    }
                } else {
                    if (shipList != null) {
                        for (int i = 0; i < shipList.size(); i++) {
                            shippingList.add(shipList.get(i));
                        }
                    }
                }
                if (shippingList != null && shippingList.size() > 0 || shippingScheme == Constants.SHIPPING_SCHEME_MERGE) {
                    intent = new Intent();
                    intent.putExtra("shippingScheme", shippingScheme);
                    Bundle bundle = new Bundle();
                    List<CsBase.PairIntInt> mPairList = new ArrayList<CsBase.PairIntInt>();
                    if (isCrowdOrder) {
                        for (int i = 0; i < crowdPairList.size(); i++) {
                            for (int j = 0; j < shippingList.size(); j++) {
                                if (shippingList.get(j).getShippingId() == crowdPairList.get(i).getV()) {
                                    mPairList.add(crowdPairList.get(i));
                                }
                            }
                        }
                    } else {
                        if (pairList != null) {

                            for (int i = 0; i < pairList.size(); i++) {
                                for (int j = 0; j < shippingList.size(); j++) {
                                    if (shippingList.get(j).getShippingId() == pairList.get(i).getV()) {
                                        mPairList.add(pairList.get(i));
                                    }
                                }
                            }
                        }

                    }
                    if (shippingScheme == Constants.SHIPPING_SCHEME_DIRECT) {
                        bundle.putSerializable("pairList", (Serializable) mPairList);
                    }
                    bundle.putSerializable("crowdPairList", (Serializable) crowdPairList);
                    bundle.putSerializable("shippingList", (Serializable) shippingList);
                    bundle.putSerializable("callBackAddress", address);
                    bundle.putSerializable("feeMap", (Serializable) feeMap);
                    bundle.putSerializable("fee", fee);
                    bundle.putSerializable("dutyRate", dutyRate);
                    bundle.putSerializable("dutyMap", (Serializable) dutyMap);
                    bundle.putSerializable("shippingDuty", shippingDuty);
                    bundle.putSerializable("cb", initListData(cb.getWarehouses(), itemList));
                    intent.putExtras(bundle);
                    setResult(Constants.DELIVERY_REQUEST_CODE, intent);
                    finish();
                } else {
//                    final AlertDialog alertDialog = new AlertDialog.Builder(DeliveryActivity.this).create();
//                    alertDialog.show();
//                    alertDialog.show();
//                    Window window = alertDialog.getWindow();
//                    window.setContentView(R.layout.dialog_notice);
//                    TextView tv_title = (TextView) window.findViewById(R.id.tv_dialog_message);
//                    TextView tv_message = (TextView) window.findViewById(R.id.tv_dialog_message01);
//                    tv_title.setText("图集内有单品");
//                    tv_message.setText(list.get(0).getWarehouses().get(0).getName());
//                    phoneTv.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            alertDialog.hide();
////                        Intent intent = new Intent();
////                        //设置返回值
////                        intent.putExtra("isCreat", false);
////                        setResult(MeActivity.EDIT_CANCEL, intent);
////                        finish();
//                        }
//                    }, 500);
                    Toast.makeText(this, list.get(0).getWarehouses().get(0).getName() + getString(R.string.delivery_toast_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.delivery_merge_order_help_iv:
                showDialog(getString(R.string.String_merge_order), getString(R.string.dialog_merge_order_help_msg));
                break;
            case R.id.delivery_direct_mail_help_iv:
                showDialog(getString(R.string.String_direct_mail_1), getString(R.string.dialog_direct_mail_help_msg));
                break;
            default:
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ADDRESS_REQUEST_CODE) {
            shipList.clear();
            if (data != null) {
                Message msg = Message.obtain();
                address = (CsAddress.CustomerAddress) data.getExtras().getSerializable("address");
                msg.obj = data.getExtras().getSerializable("address");
                mHandler.sendMessage(msg);
                if (isCrowdOrder) {
                    getCrowdShipping(address);
                } else {
                    getShippingList(address);
                }
            }
        }
    }

    public void showDialog(String title, String msg) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.cart_order_dialog_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startDDMActivity(ContractServiceActivity.class, true);
//                MeiqiaManager.getInstance(DeliveryActivity.this).contactCustomerService();
            }
        });
        builder.setNegativeButton(getString(R.string.cart_dialog_btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void getShippingList(CsAddress.CustomerAddress address) {
        CsShipping.GetShippingListRequest.Builder builder = CsShipping.GetShippingListRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        Set<CsBase.Warehouse> set = new HashSet<CsBase.Warehouse>();
        for (int i = 0; i < warehouseList.size(); i++) {
            for (int j = 0; j < itemList.size(); j++) {
                if (warehouseList.get(i).getWarehouseId() == itemList.get(j).getWarehouseId()) {
                    if (itemList.get(j).getIsSelected()) {
                        set.add(warehouseList.get(i));
                    }
                }
            }
        }

        Iterator<CsBase.Warehouse> iter = set.iterator();
        while (iter.hasNext()) {
            CsBase.Warehouse wh = iter.next();
            builder.addWarehouseIds(wh.getWarehouseId());

        }

        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getIsSelected()) {
                warehouseId = itemList.get(i).getWarehouseId();
            }
        }
        if (address != null) {
            builder.setShippingAddressId(address.getAddressId());
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsShipping.GetShippingListResponse>() {

            @Override
            public void onSuccess(CsShipping.GetShippingListResponse response) {
                LogUtils.d(response.toString());
                pairList = response.getPairsList();

                Message msg = Message.obtain();
                msg.obj = response;
                shippingHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    private Handler shippingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CsShipping.GetShippingListResponse response = (CsShipping.GetShippingListResponse) msg.obj;
            List<CsShipping.Shipping> shippingList = response.getShippingsList();
            List<CsBase.PairIntInt> pairList = response.getPairsList();

            if (shippingList.size() > 0) {
            setListAdapter(shippingList, pairList);


            } else {
                if (shippingScheme == Constants.SHIPPING_SCHEME_DIRECT) {
                }

            }
        }
    };

    /*  required BaseRequest head = 1;
      required int64       crowd_id            = 2;//拼单id
      required int64       item_id             = 3;//单品id
      required int32       qty                 = 4;//交易数量
      optional int32       shipping_address_id = 5;//收货地址
      optional int32       warehouse_id        = 6;//仓库列表*/
    private void getCrowdShipping(CsAddress.CustomerAddress address) {
        CsShipping.GetCrowdShippingMethodListRequest.Builder builder = CsShipping.GetCrowdShippingMethodListRequest.newBuilder();
        builder.setCrowdId(commoditysBean2.crowdId).setItemId(commoditysBean.getItemId()).setQty(commoditysBean.getQty());

        if (address != null) {
            builder.setShippingAddressId(address.getAddressId());
        } else {
            return;
        }
        for (int i = 0; i < cb.getSalesCartItems().size(); i++) {
            CsCart.SalesCartItem item = cb.getSalesCartItems().get(i);
            if (item.getIsSelected()) {
                warehouseId = item.getWarehouseId();
                builder.setWarehouseId(warehouseId);
            }
        }

        NetEngine.postRequest(builder, new INetEngineListener<CsShipping.GetCrowdShippingMethodListResponse>() {

            @Override
            public void onSuccess(CsShipping.GetCrowdShippingMethodListResponse response) {

                final List<CsShipping.Shipping> shippingsList = response.getShippingsList();
                if (shippingsList != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeLoading();
                            crowdPairList.clear();
                            for (int i = 0; i < shippingsList.size(); i++) {
                                CsBase.PairIntInt buildInt = CsBase.PairIntInt.newBuilder().setK(warehouseId).setV(shippingsList.get(i).getShippingId()).build();
                                crowdPairList.add(buildInt);
                            }
                            setListAdapter(shippingsList, crowdPairList);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
            }
        });
    }

    public CommoditysBean initListData(List<CsBase.Warehouse> warehouseList, List<CsCart.SalesCartItem> cartItemList) {
        CommoditysBean cb = new CommoditysBean();
        List<CsCart.SalesCartItem> itemList = new ArrayList<CsCart.SalesCartItem>();
        List<CsBase.Warehouse> whList = new ArrayList<CsBase.Warehouse>();
        Set<CsBase.Warehouse> set = new HashSet<CsBase.Warehouse>();
        int whId;
        for (int i = 0; i < warehouseList.size(); i++) {
            whId = warehouseList.get(i).getWarehouseId();
            for (int j = 0; j < cartItemList.size(); j++) {
                CsCart.SalesCartItem item = cartItemList.get(j);
                CsBase.Warehouse warehouse = warehouseList.get(i);
                if (item.getWarehouseId() == warehouse.getWarehouseId()) {
                    if (item.getIsSelected()) {
                        LogUtils.d("-----whId " + whId + " list id " + warehouseList.get(i).getWarehouseId());
                        set.add(warehouse);
                        whList.add(warehouse);
                        itemList.add(item);

                    }
                }
            }
        }
        cb.setWarehouses(whList);
        cb.setWarehouseSet(set);
        cb.setSalesCartItems(itemList);
        return cb;
    }


    class ShipAdapter extends BaseAdapter {

        private Context mCtx;
        private List<CsShipping.Shipping> shipList;
        private List<CsShipping.Shipping> checkedShip;

        private int checkedPos = -1;

        public ShipAdapter(Context context, List<CsShipping.Shipping> shipList, List<CsShipping.Shipping> checkedShip) {
            this.mCtx = context;
            this.shipList = shipList;
            this.checkedShip = checkedShip;
            init();
        }

        public void init() {
            for (int i = 0; i < shipList.size(); i++) {
                if (checkedShip != null) {
                    for (int j = 0; j < checkedShip.size(); j++) {
                        if (checkedShip.get(j).getShippingId() == shipList.get(i).getShippingId()) {
                            checkMap.put(i, true);
                        } else {
                            checkMap.put(i, false);
                        }
                    }

                } else {
                    if (shipList.size() == 1) {
                        checkMap.put(i, true);
                        shipMap.put(i, shipList.get(0));
                    } else {
                        checkMap.put(i, false);
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return shipList != null ? shipList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return shipList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mCtx).inflate(R.layout.delivery_freight_layout, null);
                holder = new Holder();
                holder.shippigLayout = (RelativeLayout) convertView.findViewById(R.id.shipping_scheme_layout);
                holder.checkedTv = (CheckedTextView) convertView.findViewById(R.id.checked_tv);
                holder.titleTv = (TextView) convertView.findViewById(R.id.shipping_title_tv);
                holder.infoTv = (TextView) convertView.findViewById(R.id.shipping_info_tv);
                holder.feeTv = (TextView) convertView.findViewById(R.id.shipping_fee_tv);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            final CsShipping.Shipping ship = shipList.get(position);
            holder.checkedTv.setCheckMarkDrawable(R.drawable.selector_for_cart_selected);
            holder.titleTv.setText(ship.getTitle());
            holder.infoTv.setText(ship.getInfo());
//            holder.feeTv.setText(getString(R.string.String_order_price, UIUtils.getCurrency(DeliveryActivity.this), ship.getFee()));
            holder.feeTv.setText(UIUtils.getCurrency(DeliveryActivity.this, (float) ship.getFee()));
            if (checkMap != null) {
                if (checkMap.get(position)) {
                    holder.checkedTv.setChecked(true);
                } else {
                    holder.checkedTv.setChecked(false);
                }
            }

            return convertView;
        }

        public void setCheckedAtPosition(int pos) {
            for (int i = 0; i < shipList.size(); i++) {
                if (pos == i) {
                    checkMap.put(i, true);
                } else {
                    checkMap.put(i, false);
                }

            }
        }

        class Holder {
            RelativeLayout shippigLayout;
            CheckedTextView checkedTv;
            TextView titleTv;
            TextView infoTv;
            TextView feeTv;
        }
    }

    int position = 0;

    public void setListAdapter(final List<CsShipping.Shipping> shippingList, List<CsBase.PairIntInt> pairList) {
//        this.shippingList = shippingList;

        List<CsBase.Warehouse> warehouseList = cb.getWarehouses();
        List<CsCart.SalesCartItem> cartItemList = cb.getSalesCartItems();
        CommoditysBean cb = initListData(warehouseList, itemList);
        Set<CsBase.Warehouse> whSet = cb.getWarehouseSet();
        List<CsCart.SalesCartItem> mCIList = cb.getSalesCartItems();
        Iterator<CsBase.Warehouse> iter = whSet.iterator();
        commodityLayout.removeAllViews();


        while (iter.hasNext()) {
            final CsBase.Warehouse warehouse = iter.next();
            LogUtils.d("---------setListAdapter");
            TextView wareHouseTextView = new TextView(this);
            wareHouseTextView.setText(warehouse.getName());
            wareHouseTextView.setPadding(30, 30, 30, 30);
            wareHouseTextView.setTextColor(Color.BLACK);
            wareHouseTextView.setTextSize(14);
            wareHouseTextView.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            wareHouseTextView.setLayoutParams(lp);
            final ImageView arrow = new ImageView(this);
            arrow.setImageResource(R.mipmap.arrow_down);
            LinearLayout.LayoutParams arrowLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            arrowLp.setMargins(0, 0, 30, 0);
            arrow.setLayoutParams(arrowLp);
            final LinearLayout titleLayout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutLp.setMargins(0, 30, 0, 0);
            titleLayout.setLayoutParams(layoutLp);
            titleLayout.setGravity(Gravity.CENTER_VERTICAL);
            titleLayout.addView(wareHouseTextView);
            titleLayout.addView(arrow);
            titleLayout.setTag(warehouse.getWarehouseId());
            titleLayout.setBackgroundColor(Color.WHITE);
            final CustomListView commodityListView = new CustomListView(this);
            titleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commodityListView.getVisibility() == View.GONE) {
                        arrow.setImageResource(R.mipmap.arrow_up);
                        commodityListView.setVisibility(View.VISIBLE);
                        commodityListView.setVisibility(View.VISIBLE);
                    } else {
                        arrow.setImageResource(R.mipmap.arrow_down);
                        commodityListView.setVisibility(View.GONE);
                        commodityListView.setVisibility(View.GONE);
                    }
                }
            });

            List<CsCart.SalesCartItem> list = new ArrayList<>();
            for (int j = 0; j < itemList.size(); j++) {
                if (warehouse.getWarehouseId() == itemList.get(j).getWarehouseId()) {
                    if (itemList.get(j).getIsSelected()) {
                        LogUtils.d("------id " + itemList.get(j).getWarehouseId() + " tid " + titleLayout.getTag());
                        list.add(itemList.get(j));

                    } else {
                    }
                }
            }
            commodityLayout.addView(titleLayout);


            commodityListView.setFocusable(false);
            commodityListView.setBackgroundColor(Color.WHITE);
            OrderCommodityAdapter adapter = new OrderCommodityAdapter(this, list);
            commodityListView.setAdapter(adapter);
            setListViewHeightBased(commodityListView);
            commodityListView.setVisibility(View.GONE);


            ImageView line = new ImageView(this);

            line.setImageResource(R.color.line_gray);
            LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            line.setLayoutParams(lineLp);

            ImageView line2 = new ImageView(this);
            line2.setImageResource(R.color.line_gray);
            LinearLayout.LayoutParams lineLp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
            line2.setLayoutParams(lineLp2);

            commodityLayout.addView(line);
            commodityLayout.addView(commodityListView);
            commodityLayout.addView(line2);


//            LinearLayout freightLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.delivery_freight_layout, null);
//            shippingLayout = (RelativeLayout) rootView.findViewById(R.id.shipping_scheme_layout);
//            shippingTitle = (TextView) freightLayout.findViewById(R.id.shipping_title_tv);
//            shippingInfo = (TextView) freightLayout.findViewById(R.id.shipping_info_tv);
//            shippingFee = (TextView) freightLayout.findViewById(R.id.shipping_fee_tv);

            final List<CsShipping.Shipping> shippings = new ArrayList<CsShipping.Shipping>();
            double totalFee = 0;
            for (int h = 0; h < pairList.size(); h++) {
                if (pairList.get(h).getK() == warehouse.getWarehouseId()) {
                    if (shippingList.get(h).getShippingId() == pairList.get(h).getV()) {
                        LogUtils.d("--------V: " + pairList.get(h).getV() + " id " + shippingList.get(h).getShippingId());
                        shippings.add(shippingList.get(h));
                        shipList.add(shippingList.get(h));
//                        mShipping = shippingList.get(h);
//                        totalFee += mShipping.getFee();
//                        feeMap.put(pairList.get(h).getV(), totalFee);
//                        shippingTitle.setText(mShipping.getTitle());
//                        shippingInfo.setText(mShipping.getInfo());
//                        String fee = getResources().getString(R.string.String_order_price);
//                        fee = String.format(fee, totalFee);
//                        shippingFee.setText(fee);
                    }
                }
            }

            ListView shipListView = new ListView(this);
            shipListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            final ShipAdapter shipAdapter = new ShipAdapter(this, shippings, shipList);
            dutyRate = shippings.get(position).getDutyRate();
            shipListView.setAdapter(shipAdapter);
            setListViewHeightBasedOnChildren(shipListView);
            shipMap.put(warehouse.getWarehouseId(), shippings.get(position));
            if(shippings.get(position).getIsNeedDuty()){
                dutyMap.put(warehouse.getWarehouseId(), shippings.get(position).getDutyRate());
            }
            shipListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    checkMap.put(position, true);
                    fee = shippings.get(position).getFee();
                    mShipping = shippings.get(position);
                    shipMap.put(warehouse.getWarehouseId(), mShipping);
                    dutyMap.put(warehouse.getWarehouseId(), shippings.get(position).getDutyRate());
                    dutyRate = shippings.get(position).getDutyRate();
                    shippingDuty = shippings.get(position).getShippingDuty();
//                    DeliveryActivity.this.shippingList.add(mShipping);
                    shipAdapter.setCheckedAtPosition(position);
                    shipAdapter.notifyDataSetChanged();
                }
            });
            commodityLayout.addView(shipListView);

//            commodityLayout.addView(freightLayout);
//
//            if (shippingList.size() > 0) {
//                freightLayout.setVisibility(View.VISIBLE);
//            } else {
//                freightLayout.setVisibility(View.GONE);
//            }

        }
    }

    public void setListAdapter(List<CsShipping.Shipping> shippingList) {
        this.shippingList = shippingList;
        List<CsBase.Warehouse> warehouseList = cb.getWarehouses();
        List<CsCart.SalesCartItem> cartItemList = cb.getSalesCartItems();
        commodityLayout.removeAllViews();
        for (int i = 0; i < warehouseList.size(); i++) {
            LogUtils.d("---------setListAdapter");
            TextView wareHouseTextView = new TextView(this);
            wareHouseTextView.setText(warehouseList.get(i).getName());
            wareHouseTextView.setPadding(30, 30, 30, 30);
            wareHouseTextView.setTextColor(Color.BLACK);
            wareHouseTextView.setTextSize(14);
            wareHouseTextView.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            wareHouseTextView.setLayoutParams(lp);
            final ImageView arrow = new ImageView(this);
            arrow.setImageResource(R.mipmap.arrow_down);
            LinearLayout.LayoutParams arrowLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            arrowLp.setMargins(0, 0, 30, 0);
            arrow.setLayoutParams(arrowLp);
            final LinearLayout titleLayout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutLp.setMargins(0, 30, 0, 0);
            titleLayout.setLayoutParams(layoutLp);
            titleLayout.setGravity(Gravity.CENTER_VERTICAL);
            titleLayout.addView(wareHouseTextView);
            titleLayout.addView(arrow);
            titleLayout.setTag(i);
            titleLayout.setBackgroundColor(Color.WHITE);
            final ListView commodityListView = new ListView(this);
            titleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isOpen = !isOpen;
                    if (isOpen) {
                        count++;
                        arrow.setImageResource(R.mipmap.arrow_up);
                        commodityListView.setVisibility(View.VISIBLE);
                    } else {
                        count = 0;
                        arrow.setImageResource(R.mipmap.arrow_down);
                        commodityListView.setVisibility(View.GONE);
                    }
                    LogUtils.d("--------count " + count + "  v " + v.getId());
                }
            });

            List<CsCart.SalesCartItem> list = new ArrayList<>();
            for (int j = 0; j < cartItemList.size(); j++) {
                if (warehouseList.get(i).getWarehouseId() == cartItemList.get(j).getWarehouseId()) {
                    if (cartItemList.get(j).getIsSelected()) {
                        list.add(cartItemList.get(j));

                    }
                }
            }


            commodityListView.setFocusable(false);
            commodityListView.setBackgroundColor(Color.WHITE);
            OrderCommodityAdapter adapter = new OrderCommodityAdapter(this, list);
            commodityListView.setAdapter(adapter);
            commodityListView.setVisibility(View.GONE);


            ImageView line = new ImageView(this);
            line.setImageResource(R.color.line_gray);
            LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            line.setLayoutParams(lineLp);
            LinearLayout.LayoutParams params = setListViewHeightBasedOnChildren(commodityListView);
            commodityLayout.addView(titleLayout);
            commodityLayout.addView(line);
            if (params != null) {
                commodityLayout.addView(commodityListView, params);
            }
            for (int k = 0; k < shippingList.size(); k++) {
                LinearLayout freightLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.delivery_freight_layout, null);
                shippingLayout = (RelativeLayout) rootView.findViewById(R.id.shipping_scheme_layout);
                shippingTitle = (TextView) freightLayout.findViewById(R.id.shipping_title_tv);
                shippingInfo = (TextView) freightLayout.findViewById(R.id.shipping_info_tv);
                shippingFee = (TextView) freightLayout.findViewById(R.id.shipping_fee_tv);
                CsShipping.Shipping shipping = shippingList.get(k);

                shippingTitle.setText(shipping.getTitle());
                shippingInfo.setText(shipping.getInfo());
                String fee = getResources().getString(R.string.String_order_price);
                fee = UIUtils.getCurrency(DeliveryActivity.this, (float) shipping.getFee()) ;
                shippingFee.setText(fee);
                commodityLayout.addView(freightLayout);
                CsBase.PairIntInt build = CsBase.PairIntInt.newBuilder().setK(warehouseId).setV(shipping.getShippingId()).build();
                crowdPairList.add(build);
            }
        }
    }

    public static void setListViewHeightBased(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            LogUtils.d("----------height: " + listItem.getMeasuredHeight());
            totalHeight += listItem.getMeasuredHeight();
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        LogUtils.d("----------p height: " + params.height);
        params.setMargins(0, 3, 0, 0);
        listView.setLayoutParams(params);
    }

    public static LinearLayout.LayoutParams setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null)
            return null;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return null;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        int HEIGHT = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, HEIGHT);
        params.height = HEIGHT;
        listView.setLayoutParams(params);
        return params;
    }

    public List<CsCart.SalesCartItem> getListData(List<CommoditysBean> list) {
        getShippingList(address);

        commoditysBean = list.get(0);
        List<CsBase.Warehouse> wareHouseList = commoditysBean.getWarehouses();
        commoditysBean2 = list.get(1);
        List<CsCart.SalesCartItem> salesCartList = commoditysBean2.getSalesCartItems();
        List<CsCart.SalesCartItem> salesTempList = new ArrayList<CsCart.SalesCartItem>();
        List<CsBase.Warehouse> wareHouseTempList = new ArrayList<CsBase.Warehouse>();

        for (int i = 0; i < wareHouseList.size(); i++) {
            cb = new CommoditysBean();
            wareHouseTempList.add(wareHouseList.get(i));

            for (int j = 0; j < salesCartList.size(); j++) {
                if (wareHouseList.get(i).getWarehouseId() == salesCartList.get(j).getWarehouseId()) {
                    Log.d(TAG, "Title: " + salesCartList.get(j).getTitle());
                    if (salesCartList.get(j).getIsSelected()) {

                        salesTempList.add(salesCartList.get(j));
                    }

                }
            }
        }
        cb.setWarehouses(wareHouseTempList);
        cb.setSalesCartItems(salesTempList);
        return salesTempList;

    }

    public void getSaleCartItem() {
//        qtyCount = 0;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoading(5000);
            }
        });
        CsCart.GetSalesCartListRequest.Builder builder = CsCart.GetSalesCartListRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.GetSalesCartListResponse>() {

            @Override
            public void onSuccess(CsCart.GetSalesCartListResponse response) {
                closeLoading();
                LogUtils.d(TAG, "response: " + response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                cartHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
                LogUtils.d(TAG, "ErrorMsg: " + errMsg);
            }
        });
    }

    private Handler cartHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CsCart.GetSalesCartListResponse response = (CsCart.GetSalesCartListResponse) msg.obj;
            final List<CsCart.SalesCartItem> commodityList = response.getItemsList();
            final List<CsBase.Warehouse> warehouseList = response.getWarehousesList();

            list = getCommodityData(warehouseList, commodityList);
            getListData(list);
        }
    };

    private void getShipping() {
        if (isCrowdOrder) {
//            list = AttendCrowdActivity.cartItems;
            getListData(list);
        }

        if (address == null) {
            getCustomerAddressList();
        } else {
            setAddress(address);
            if (isCrowdOrder) {
                getCrowdShipping(address);
            } else {
                getShippingList(address);
            }
        }
    }

    public List<CommoditysBean> getCommodityData(List<CsBase.Warehouse> whList, List<CsCart.SalesCartItem> commodityList) {
        List<CommoditysBean> list = new ArrayList<CommoditysBean>();
        for (int i = 0; i < whList.size(); i++) {
            CommoditysBean warehousecb = new CommoditysBean();
            warehousecb.setWarehouses(whList);
            warehousecb.setType(0);
            list.add(warehousecb);
            for (int j = 0; j < commodityList.size(); j++) {
                if (whList.get(i).getWarehouseId() == commodityList.get(j).getWarehouseId()) {
                    CommoditysBean commoditycb = new CommoditysBean();
                    commoditycb.setSalesCartItems(commodityList);
                    commoditycb.setType(1);
                    list.add(commoditycb);
                }
            }
        }
        return list;
    }

    public void getCustomerAddressList() {
        showLoading(5000);
        CsAddress.GetCustomerAddressListExRequest.Builder builder = CsAddress.GetCustomerAddressListExRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setPageNum(1);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsAddress.GetCustomerAddressListExResponse>() {

            @Override
            public void onSuccess(CsAddress.GetCustomerAddressListExResponse response) {
                closeLoading();
                LogUtils.d(response.toString());
                List<CsAddress.CustomerAddress> addressesList = response.getAddressesList();
                if (addressesList != null && addressesList.size() > 0) {
                    for (int i = 0; i < addressesList.size(); i++) {
                        address = addressesList.get(i);
                        if (address.getIsDefault()) {
                            Message msg = Message.obtain();
                            msg.arg1 = 1;
                            msg.obj = address;
                            mHandler.sendMessage(msg);
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addressLayout.setVisibility(View.GONE);
                            final CustomDialog.Builder builder = new CustomDialog.Builder(DeliveryActivity.this);
                            builder.setMessage(getString(R.string.delivert_dialog_msg));
                            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(DeliveryActivity.this, AddNewAddressActivity.class);
                                    intent.putExtra("deliveryAddAddress", true);
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            CustomDialog dialog = builder.create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }
                    });

                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }

    public void setAddress(CsAddress.CustomerAddress address) {
        if (address != null) {
//            addressLayout.setVisibility(View.VISIBLE);
            String str = getResources().getString(R.string.String_cart_consignee_msg);
            str = String.format(str, address.getName(), address.getPhone());
            nameAndPhoneTv.setText(str);
            /*AssetFileManager assetFileManager = new AssetFileManager(this);
            String region = assetFileManager.readFile(address.getRegion());*/
            //String region = AssetFileManager.getInstance().readFilePlus(address.getRegion(), AssetFileManager.ADDRESS_TYPE);
            String region = address.getFullRegionName();
            phoneTv.setText(address.getStreet() + "," + region);
            if (address.getIsDefault()) {
                defaultAddressIv.setVisibility(View.VISIBLE);
            } else {
                defaultAddressIv.setVisibility(View.GONE);
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CsAddress.CustomerAddress address = (CsAddress.CustomerAddress) msg.obj;
            setAddress(address);
        }
    };
}
