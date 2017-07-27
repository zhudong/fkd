package com.fuexpress.kr.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;


/**
 * Created by zhudong on 2016/4/12.
 */
public class CartCommodityBean implements Serializable {

    //单品ID
    private long itemID;
    //数量
    private int qty;
    //留言
    private String note;

    private CsBase.PairIntInt sizePairIntInt;

    public CsBase.PairIntInt getSizePairIntInt() {
        return sizePairIntInt;
    }

    public void setSizePairIntInt(CsBase.PairIntInt sizePairIntInt) {
        this.sizePairIntInt = sizePairIntInt;
    }

    //属性ID列表
    private List<CsBase.PairIntInt> mExtendAttrs = new ArrayList<>();

    public void addAttrs(CsBase.PairIntInt pairIntInt) {
        mExtendAttrs.add(pairIntInt);
    }


    public List<CsBase.PairIntInt> getExtendAttrs() {
        return mExtendAttrs;
    }

    public void setExtendAttrs(List<CsBase.PairIntInt> extendAttrs) {
        mExtendAttrs = extendAttrs;
    }


    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getItemID() {
        return itemID;
    }

    public void setItemID(long itemID) {
        this.itemID = itemID;
    }
}
