package com.fuexpress.kr.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fksproto.CsBase;
import fksproto.CsCart;


/**
 * Created by Lenovo on 2016/4/7.
 */
public class CommoditysBean implements Serializable {
    private List<CsBase.Warehouse> warehouses;
    private List<CsCart.SalesCartItem> salesCartItems;
    private Set<CsBase.Warehouse> warehouseSet;
    private int type;
    private float subtotal;
    private float grandtotal;
    private boolean groupChecked;
    private boolean childChecked;


    public List<CsBase.PairIntInt> extendAttrs = new ArrayList<>();
    public CsBase.PairIntInt attr;
    public CsBase.PairIntInt ColorAttr;
    public long crowdId;
    public long itemId;
    public int qty;

    public List<CsBase.PairIntInt> getExtendAttrs() {
        return extendAttrs;
    }

    public void setExtendAttrs(List<CsBase.PairIntInt> extendAttrs) {
        this.extendAttrs = extendAttrs;
    }

    public long getCrowdId() {
        return crowdId;
    }

    public void setCrowdId(long crowdId) {
        this.crowdId = crowdId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
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

    public List<CsBase.Warehouse> getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(List<CsBase.Warehouse> warehouses) {
        this.warehouses = warehouses;
    }

    public List<CsCart.SalesCartItem> getSalesCartItems() {
        return salesCartItems;
    }

    public void setSalesCartItems(List<CsCart.SalesCartItem> salesCartItems) {
        this.salesCartItems = salesCartItems;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isGroupChecked() {
        return groupChecked;
    }

    public void setGroupChecked(boolean groupChecked) {
        this.groupChecked = groupChecked;
    }

    public boolean isChildChecked() {
        return childChecked;
    }

    public void setChildChecked(boolean childChecked) {
        this.childChecked = childChecked;
    }

    public Set<CsBase.Warehouse> getWarehouseSet() {
        return warehouseSet;
    }

    public void setWarehouseSet(Set<CsBase.Warehouse> warehouseSet) {
        this.warehouseSet = warehouseSet;
    }
}
