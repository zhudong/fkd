package com.fuexpress.kr.bean;

import com.fuexpress.kr.conf.Constants;

/**
 * Created by andy on 2017/2/22.
 */
public class ReParcelItemBean {

    private String parcelItemId;//    = 1; //单品id(共)
    private float price;//    = 2; //单品价格(共)
    private int qtyPack;//  = 3; //打包数量(共)
    private String imagePath;//    = 4; //图片路径(共)，需求单json中对应字段smallImageUrl，订单json中对应子字段imagePath
    private String captionCutPrice;//    = 6; //单品描述(共),订单json中对应字段captionCutPrice，需求单json中对应字段description
    private float declaredValue;//    = 10;//item申报总价(共)
    private boolean select;


    public ReParcelItemBean(String parcelItemId, float price, int qtyPack, String imagePath, String captionCutPrice, float declaredValue) {
        this.parcelItemId = parcelItemId;
        this.price = price;
        this.qtyPack = qtyPack;
        if (!imagePath.contains("small9")) {
            this.imagePath = imagePath + Constants.ImgUrlSuffix.small_9;
        } else {
            this.imagePath = imagePath;
        }
        this.captionCutPrice = captionCutPrice;
        this.declaredValue = declaredValue;
    }

    public String getParcelItemId() {
        return parcelItemId;
    }

    public void setParcelItemId(String parcelItemId) {
        this.parcelItemId = parcelItemId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQtyPack() {
        return qtyPack;
    }

    public void setQtyPack(int qtyPack) {
        this.qtyPack = qtyPack;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCaptionCutPrice() {
        return captionCutPrice;
    }

    public void setCaptionCutPrice(String captionCutPrice) {
        this.captionCutPrice = captionCutPrice;
    }

    public float getDeclaredValue() {
        return declaredValue;
    }

    public void setDeclaredValue(float declaredValue) {
        this.declaredValue = declaredValue;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
