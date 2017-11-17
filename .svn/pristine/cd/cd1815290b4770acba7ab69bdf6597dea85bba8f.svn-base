package com.fuexpress.kr.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.CartCommodityBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.CardOrderDetailActivity;
import com.fuexpress.kr.ui.activity.order_detail.OrderDetailCanceledActivity;
import com.fuexpress.kr.ui.activity.shopping_cart.ShopCartActivity;
import com.fuexpress.kr.utils.LogUtils;
import com.google.protobuf.GeneratedMessage;

import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsCart;
import fksproto.CsCrowd;


public class ShopCartManager {
    private static final String TAG = "ShopCartManager";
    private static Context mCtx;
    private static int cartCount;
//    private static SweetAlertDialog dialog;

    private static ShopCartManager instance = new ShopCartManager();
    private static SysApplication mApp;
    public int mShopCartCount = 0;

    private ShopCartManager() {

    }

    public static ShopCartManager getInstance(Context context, SysApplication app) {
        mCtx = context;
        mCtx.getMainLooper();
        cartCount = 0;
        mApp = app;
//        dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
//        dialog.getProgressHelper().setBarColor(Color.WHITE);
//        dialog.setCancelable(false);
        return instance;
    }

    public static ShopCartManager getInstance() {
        //mCtx = context;
        //mCtx.getMainLooper();
        cartCount = 0;
        //mApp = app;
//        dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
//        dialog.getProgressHelper().setBarColor(Color.WHITE);
//        dialog.setCancelable(false);
        return instance;
    }

    public void addCrowdToCartRequest(int crowdId, int itemId, final int qty, String note, List<CsBase.PairIntInt> optionIds) {
        CsCrowd.AddCrowdToCartRequest.Builder builder = CsCrowd.AddCrowdToCartRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCrowdId(crowdId);
        builder.setItemId(itemId);
        builder.setQty(qty);
        builder.setNote(note);
        builder.addAllAttrs(optionIds);
        builder.setCurrencyCode(AccountManager.getInstance().getCurrencyCode());
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCrowd.AddCrowdToCartResponse>() {

            @Override
            public void onSuccess(CsCrowd.AddCrowdToCartResponse response) {
                ((Activity) mCtx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mCtx, mCtx.getString(R.string.add_to_cart_success_msg), Toast.LENGTH_SHORT).show();
                        int count = mApp.getQtyCount();
                        Log.d(TAG, "------------------------ShopCartManager count: " + count);
                        sendCartQty(qty + count);
                    }
                });
            }

            @Override
            public void onFailure(final int ret, String errMsg) {
                ((Activity) mCtx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mCtx, mCtx.getString(R.string.add_to_cart_failed_msg) + ret, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    public void addToShopCart(final CartCommodityBean cartCommodityBean) {
//        dialog.show();
        CsCart.AppendSalesCartRequest.Builder builder = CsCart.AppendSalesCartRequest.newBuilder();
        if (cartCommodityBean != null) {
            builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
            builder.setItemId(cartCommodityBean.getItemID());
            builder.setQty(cartCommodityBean.getQty());
            builder.setNote(cartCommodityBean.getNote());
            builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
            builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
            List<CsBase.PairIntInt> extendAttrs = cartCommodityBean.getExtendAttrs();
            for (CsBase.PairIntInt pairIntInt : extendAttrs) {
                builder.addAttrs(pairIntInt);
            }
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.AppendSalesCartResponse>() {
            @Override
            public void onSuccess(CsCart.AppendSalesCartResponse response) {
                Looper.prepare();
                //                mCtx.runOnUiThread(new Runnable() {
                //                    @Override
                //                    public void run() {
                //                        dialog.setTitleText("添加购物车成功");
                //                    }
                //                });
                //    LogUtils.d(response.toString());

                Toast.makeText(mCtx, mCtx.getString(R.string.add_to_cart_success_msg), Toast.LENGTH_SHORT).show();
                int count = mApp.getQtyCount();
                Log.d(TAG, "------------------------ShopCartManager count: " + count);
                sendCartQty(cartCommodityBean.getQty() + count);
                try {
                    Thread.sleep(500);
//                    dialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Looper.loop();

            }

            @Override
            public void onFailure(int ret, String errMsg) {
                Looper.prepare();
//                dialog.setTitle("添加购物车失败");
                try {
                    Thread.sleep(500);
//                    dialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ret == Constants.CODE_ADD_TO_CART_ERROR) {
                    Toast.makeText(mCtx, mCtx.getString(R.string.add_to_cart_error_msg) + ret, Toast.LENGTH_SHORT).show();

                }
                Toast.makeText(mCtx, mCtx.getString(R.string.add_to_cart_failed_msg) + ret, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }


    public void placeOrderAgainRequest(int salesOrderId, final String currencyCode){
        ((OrderDetailCanceledActivity)mCtx).showLoading();
        CsCart.PlaceOrderAgainRequest.Builder builder = CsCart.PlaceOrderAgainRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencyCode(currencyCode);
        builder.setSalesOrderId(salesOrderId);
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.PlaceOrderAgainResponse>() {

            @Override
            public void onSuccess(final CsCart.PlaceOrderAgainResponse response) {
                LogUtils.d(response.toString());
                ((Activity) mCtx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((OrderDetailCanceledActivity)mCtx).closeLoading();
                        if (!TextUtils.isEmpty(response.getHead().getErrmsg())) {
                            Toast.makeText(mCtx, response.getHead().getErrmsg(), Toast.LENGTH_SHORT).show();
                        }
                        int count = mApp.getQtyCount();
                        Log.d(TAG, "------------------------ShopCartManager count: " + count);
                        sendCartQty(count++);
                        Intent intent = new Intent();
                        intent.setClass(mCtx, ShopCartActivity.class);
                        intent.putExtra("currency", currencyCode);
                        mCtx.startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(final int ret, String errMsg) {
                ((Activity) mCtx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((OrderDetailCanceledActivity)mCtx).closeLoading();
                        Toast.makeText(mCtx, mCtx.getString(R.string.add_to_cart_failed_msg) + ret, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void getSaleCartCount() {

        CsCart.GetSalesCartListRequest.Builder builder = CsCart.GetSalesCartListRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCart.GetSalesCartListResponse>() {

            @Override
            public void onSuccess(CsCart.GetSalesCartListResponse response) {
                //    LogUtils.d(TAG, "response: " + response.toString());
                List<CsCart.SalesCartItem> list = response.getItemsList();
                int count = 0;
                for (int i = 0; i < list.size(); i++) {
                    count += list.get(i).getQty();
                }
                sendCartQty(count);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                LogUtils.d(TAG, "ErrorMsg: " + errMsg);
            }
        });
    }

    public void sendCartQty(int count) {
        mShopCartCount = count;
        EventBus.getDefault().post(new BusEvent(BusEvent.SHOP_CART_COMMODITY_COUNT, count));
    }

}
