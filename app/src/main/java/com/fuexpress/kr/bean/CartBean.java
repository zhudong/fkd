package com.fuexpress.kr.bean;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;
import fksproto.CsCart;

/**
 * Created by Administrator on 2017-7-4.
 */

public class CartBean {
    private float subtotal;
    private float grandtotal;
    public List<WareHouse> warehouseList = new ArrayList<>();

    public static class WareHouse{
        public int warehouse_id;
        public String name;
        public String desc;
        public String fulladdress;
        public String receiver;
        public String phone;
        public String postcode;
        public List<CsCart.SalesCartItem> salesCartItems = new ArrayList<>();
    }


    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public float getGrandtotal() {
        return grandtotal;
    }

    public void setGrandtotal(float grandtotal) {
        this.grandtotal = grandtotal;
    }
}
