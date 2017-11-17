package com.fuexpress.kr.model;

import com.fuexpress.kr.bean.SalesOrderBean;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.OperaRequestListener;
import com.fuexpress.kr.net.RequestNetListener;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.ClassUtil;
import com.socks.library.KLog;

import java.util.List;

import fksproto.CsCart;
import fksproto.CsOrder;

/**
 * Created by longer on 2017/7/6.
 */

public class SalesOrderManager {
    public static final int STATUS_ALL = 0;
    public static final int STATUS_TO_PAY = 1;
    public static final int STATUS_PAYED = 2;
    public static final int STATUS_CANCELED = 3;
    public static boolean isAllHasMore = true;
    public static boolean isPandingHasMore = true;
    public static boolean isPayedHasMore = true;
    public static boolean isCanceledHasMore = true;
    public static int currentIndexAll = 1;
    public static int currentIndexPanding = 1;
    public static int currentIndexPayed = 1;
    public static int currentIndexCanceled = 1;
    public static List<SalesOrderBean> orderBeanAlls, orderBeanPendings, orderBeanPayeds, orderBeanCanceleds;

    public static void getAllOrderList(final boolean refresh, final OperaRequestListener listener) {
        CsOrder.GetSalesOrderListRequest.Builder builder = CsOrder.GetSalesOrderListRequest.newBuilder();
        builder.setTab(CsOrder.SalesOrderTab.SALES_ORDER_TAB_NONE_VALUE).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        if (refresh) {
            builder.setPageno(1);
        } else {
            builder.setPageno(currentIndexAll);
        }
        NetEngine.postRequest(builder, new INetEngineListener<CsOrder.GetSalesOrderListResponse>() {
            @Override
            public void onSuccess(CsOrder.GetSalesOrderListResponse response) {
                KLog.i("onSuccess ", response.toString());
                currentIndexAll++;
                isAllHasMore = response.getMore();
                if (orderBeanAlls == null) {
                    orderBeanAlls = ClassUtil.conventSalesOrderList2OrderBeanList(response.getOrdersList());
                } else {
                    for (CsOrder.SalesOrder salesOrder : response.getOrdersList()) {
                        orderBeanAlls.add(ClassUtil.conventSalesOrder2OrderBean(salesOrder));
                    }
                }
                if (refresh) {
                    currentIndexAll = 1;
                    orderBeanAlls = ClassUtil.conventSalesOrderList2OrderBeanList(response.getOrdersList());
                }

                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        listener.onOperaSuccess();
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        listener.onOperaFailure();
                    }
                });

            }
        });
    }

    public static void getSalesOrderDetail(long id, final RequestNetListener listener) {
        CsOrder.NewSalesOrderDetailRequest.Builder builder = CsOrder.NewSalesOrderDetailRequest.newBuilder();
//        builder.setCustomerId(AccountManager.getInstance().mUin);
        builder.setOrderId(id).setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsOrder.NewSalesOrderDetailResponse>() {
            @Override
            public void onSuccess(final CsOrder.NewSalesOrderDetailResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        CsOrder.NewSalesOrderDetailResponse.Builder newBuild = CsOrder.NewSalesOrderDetailResponse.newBuilder()
                                .setHead(response.getHead())
                                .setOrder(response.getOrder())
                                .setAddress(response.getAddress());
                        List<CsOrder.SalesOrderItem> orderItemsList = response.getOrderItemsList();
                        /*过滤*/
                        for (CsOrder.SalesOrderItem item : orderItemsList) {
                            if (item.getParcelIdReturn() == 0) {
                                newBuild.addOrderItems(item);
                            }
                        }
                        for (CsOrder.SalesOrderShipping shipping : response.getShippingsList()) {
                            newBuild.addShippings(shipping);
                        }
                        listener.onSuccess(newBuild.build());
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure();
                    }
                });
            }
        });
    }
}
