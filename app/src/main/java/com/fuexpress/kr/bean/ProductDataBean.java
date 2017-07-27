package com.fuexpress.kr.bean;

import java.util.List;

/**
 * Created by root on 16-12-22.
 */

public class ProductDataBean {
    private String productname;
    private String productdescription;
    private String productremark;
    private String warehouseid;
    private String addressinfo;
    private String price;
    private String num;
    private int image_num;
    List<ItemImageBean> extra;

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public void setProductdescription(String productdescription) {
        this.productdescription = productdescription;
    }

    public String getProductremark() {
        return productremark;
    }

    public void setProductremark(String productremark) {
        this.productremark = productremark;
    }

    public String getWarehouseid() {
        return warehouseid;
    }

    public void setWarehouseid(String warehouseid) {
        this.warehouseid = warehouseid;
    }

    public String getAddressinfo() {
        return addressinfo;
    }

    public void setAddressinfo(String addressinfo) {
        this.addressinfo = addressinfo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getImage_num() {
        return image_num;
    }

    public void setImage_num(int image_num) {
        this.image_num = image_num;
    }

    public List<ItemImageBean> getExtra() {
        return extra;
    }

    public void setExtra(List<ItemImageBean> extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "ProductDataBean{" +
                "productname='" + productname + '\'' +
                ", productdescription='" + productdescription + '\'' +
                ", productremark='" + productremark + '\'' +
                ", warehouseid='" + warehouseid + '\'' +
                ", addressinfo='" + addressinfo + '\'' +
                ", price='" + price + '\'' +
                ", num='" + num + '\'' +
                ", image_num=" + image_num +
                ", extra=" + extra +
                '}';
    }
}
