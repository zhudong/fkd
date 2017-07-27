package com.fuexpress.kr.bean;

import java.io.Serializable;

/**
 * Created by yuan on 2016-6-21.
 */
public class HelpSendParcelBean implements Serializable {
    //    public static final int INT = 9;
    private int parcelid;
    private String parcelName;
    private int parcelCount;//包裹数量
    private String productdescription;//        = 2;//物品信息
    private float productprice;//  = 3;//申报价
    private float weight;// = 4;//总重量
    private int customeraddressid;// = 5;//地址id
    private String customeraddress;// = 5;//地址id
    private int qty;//= 6;//数量
    private String shippingTitle;
    private int shippingmethodid;// = 7;//物流id
    private int image_num;// = 8;//图片数量
    private int wareHouseID;//
    private float shippingFee;//= INT;//图片路径结构体数组
    private String images;//= INT;//图片路径结构体数组
    private String imagePaths;//= INT;//图片路径结构体数组
    private float defaultWeight;// 默认重量
    private String currencyCode;// 币种
    private String IDCardInfo;// 身份信息

    private float pureShippingFee;// 纯运费

    private String ext1;
    private String ext2;
    private String ext3;

    public float getPureShippingFee() {
        return pureShippingFee;
    }

    public void setPureShippingFee(float prueShippingFee) {
        this.pureShippingFee = prueShippingFee;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getIDCardInfo() {
        return IDCardInfo;
    }

    public void setIDCardInfo(String IDCardInfo) {
        this.IDCardInfo = IDCardInfo;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    private boolean saved;

    public String getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(String imagePaths) {
        this.imagePaths = imagePaths;
    }

    public HelpSendParcelBean(int parcelid,
                              String parcelName,
                              int parcelCount,
                              String productdescription,
                              float productprice,
                              float weight,
                              int customeraddressid,
                              String customeraddress,
                              int qty,
                              int shippingmethodid,
                              int image_num,
                              int wareHouseID,
                              float shippingFee,
                              String images,
                              String imagePaths,
                              String shippingTitle,
                              boolean saved,
                              float defaultWeight) {
        this.parcelid = parcelid;
        this.parcelName = parcelName;
        this.parcelCount = parcelCount;
        this.productdescription = productdescription;
        this.productprice = productprice;
        this.weight = weight;
        this.customeraddressid = customeraddressid;
        this.customeraddress = customeraddress;
        this.qty = qty;
        this.shippingmethodid = shippingmethodid;
        this.image_num = image_num;
        this.wareHouseID = wareHouseID;
        this.shippingFee = shippingFee;
        this.images = images;
        this.imagePaths = imagePaths;
        this.shippingTitle = shippingTitle;
        this.saved = saved;
        this.defaultWeight = defaultWeight;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getShippingTitle() {
        return shippingTitle;
    }

    public void setShippingTitle(String shippingTitle) {
        this.shippingTitle = shippingTitle;
    }

    public String getCustomeraddress() {
        return customeraddress;
    }

    public HelpSendParcelBean() {
    }

    public int getWareHouseID() {
        return wareHouseID;
    }

    public void setWareHouseID(int wareHouseID) {
        this.wareHouseID = wareHouseID;
    }

    public void setCustomeraddress(String customeraddress) {
        this.customeraddress = customeraddress;
    }

    public float getDefaultWeight() {
        return defaultWeight;
    }

    public void setDefaultWeight(float defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    public float getShippingFee() {
        return shippingFee;
    }

    public String getParcelName() {
        return parcelName;
    }

    public void setParcelName(String parcelName) {
        this.parcelName = parcelName;
    }

    public void setShippingFee(float shippingFee) {
        this.shippingFee = shippingFee;
    }

    public int getParcelid() {
        return parcelid;
    }

    public void setParcelid(int parcelid) {
        this.parcelid = parcelid;
    }

    public int getParcelCount() {
        return parcelCount;
    }

    public void setParcelCount(int parcelCount) {
        this.parcelCount = parcelCount;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public void setProductdescription(String productdescription) {
        this.productdescription = productdescription;
    }

    public float getProductprice() {
        return productprice;
    }

    public void setProductprice(float productprice) {
        this.productprice = productprice;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getCustomeraddressid() {
        return customeraddressid;
    }

    public void setCustomeraddressid(int customeraddressid) {
        this.customeraddressid = customeraddressid;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getShippingmethodid() {
        return shippingmethodid;
    }

    public void setShippingmethodid(int shippingmethodid) {
        this.shippingmethodid = shippingmethodid;
    }

    public int getImage_num() {
        return image_num;
    }

    public void setImage_num(int image_num) {
        this.image_num = image_num;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
