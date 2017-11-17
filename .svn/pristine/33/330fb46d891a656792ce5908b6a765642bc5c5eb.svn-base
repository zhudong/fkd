package com.fuexpress.kr.ui.activity.crowd;

import com.fuexpress.kr.model.ItemBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;
import fksproto.CsCart;

/**
 * Created by andy on 2017/8/2.
 */

public class CrowdOrderInfo implements Serializable {
    private CsBase.Warehouse mWarehouse;
    private CsCart.SalesCartItem mSalesCartItem;
    private ItemBean mItemBean;
    private List<CsBase.PairIntInt> selectedAttrs = new ArrayList<>();
    private float currencyPrice;
    private int count;

    public CsBase.Warehouse getWarehouse() {
        return mWarehouse;
    }

    public void setWarehouse(CsBase.Warehouse warehouse) {
        mWarehouse = warehouse;
    }

    public CsCart.SalesCartItem getSalesCartItem() {
        return mSalesCartItem;
    }

    public void setSalesCartItem(CsCart.SalesCartItem salesCartItem) {
        mSalesCartItem = salesCartItem;
    }

    public ItemBean getItemBean() {
        return mItemBean;
    }

    public void setItemBean(ItemBean itemBean) {
        mItemBean = itemBean;
    }

    public List<CsBase.PairIntInt> getSelectedAttrs() {
        return selectedAttrs;
    }

    public void setSelectedAttrs(List<CsBase.PairIntInt> selectedAttrs) {
        this.selectedAttrs = selectedAttrs;
    }

    public float getCurrencyPrice() {
        return currencyPrice;
    }

    public void setCurrencyPrice(float currencyPrice) {
        this.currencyPrice = currencyPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public float getTotal() {
        return currencyPrice * count;
    }
}
