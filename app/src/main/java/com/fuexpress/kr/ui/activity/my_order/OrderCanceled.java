package com.fuexpress.kr.ui.activity.my_order;

import fksproto.CsOrder;

/**
 * Created by longer on 2017/7/6.
 */

public class OrderCanceled extends OrderAll {
    @Override
    protected int getTab() {
        return CsOrder.SalesOrderTab.SALES_ORDER_TAB_CANCELED_VALUE;
    }
}
