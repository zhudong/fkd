package com.fuexpress.kr.bean;

import java.io.Serializable;

/**
 * Created by k550 on 5/3/2016.
 * 订单bean
 */
public class SalesOrderBean implements Serializable {
    public SalesOrderBean(){}
    public long order_id;
    public String order_number;
    public long create_time;
    public int state ;
    public int pay_method;
    public int shipping_scheme;
    public boolean use_gift_card;
    public float grand_total;
    public boolean is_crowd;
    public String currencyCode;//币种
    public float total_paid;// 需要支付的金额，修改adyen金额支付数值不对。
}
