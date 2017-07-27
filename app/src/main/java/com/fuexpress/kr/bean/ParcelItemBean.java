package com.fuexpress.kr.bean;

/**
 * Created by yuan on 2016-6-15.
 */
public class ParcelItemBean {

  /*  optional int32   parcelid 	  = 1;//包裹id
    optional int32   parcelitemid = 2;//需求单号
    optional string  name     	  = 3;//物品标题
    optional float   price 	      = 4;//加价格
    optional float   imageheight  = 5;//高
    optional string  imageurl     = 6;//url
    optional float   imagewidth   = 7;//宽
    optional int32   qty 	      = 8;//数量
    optional string  status 	  = 9;//状态
    optional string  remark 	  = 10;//备注说明
    optional string  message 	  = 11;//商户留言*/

    public String img;
    public String titel;
    public int count;
    public String price;

    public int needNumber;
    public String status;
    public int type;
    public String remark;
    public String message;

    public int salesOrderId;//= 14;//商品订单id，需求单时为0
    public int matchItemId;//= 15;//单品id，需求单时为0
    public String orderNumber; //= 16;//商品订单编码，需求单时为空字符串

}
