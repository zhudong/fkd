package com.fuexpress.kr.bean;

import android.support.v4.util.ArrayMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andy on 2016/12/7.
 */
public class ItemAppendBean implements Serializable {


    int mItemID;
    int mParcelId;
    List<String> path;
    Map<String, String> path_img;
    List<String> imgs;
    String info;
    float price;
    int num;
    boolean appended;

    MaterialsBean materails;
    CategoryBean category;
    String brandName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemAppendBean bean = (ItemAppendBean) o;

        if (mParcelId != bean.mParcelId) return false;
        if (Float.compare(bean.price, price) != 0) return false;
        if (num != bean.num) return false;
        if (imgs != null ? !imgs.equals(bean.imgs) : bean.imgs != null) return false;
        if (info != null ? !info.equals(bean.info) : bean.info != null) return false;
        if (materails != null ? !materails.equals(bean.materails) : bean.materails != null)
            return false;
        if (category != null ? !category.equals(bean.category) : bean.category != null)
            return false;
        return !(brandName != null ? !brandName.equals(bean.brandName) : bean.brandName != null);

    }

    @Override
    public int hashCode() {
        int result = mParcelId;
        result = 31 * result + (imgs != null ? imgs.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + num;
        result = 31 * result + (materails != null ? materails.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (brandName != null ? brandName.hashCode() : 0);
        return result;
    }

    public void formatPathImg(List<String> list) {
        if (path_img == null) return;
        list.clear();
        for (String p : path) {
            String s = path_img.get(p) == null ? "" : path_img.get(p);
            list.add(p + "," + s);
        }
    }

    public ItemAppendBean() {
    }

    public ItemAppendBean(int itemID, int parcelID, List<String> path, Map<String, String> path_img, List<String> imgs, String info, float price, int num, boolean appended) {
        this.mItemID = itemID;
        this.mParcelId = parcelID;
        this.path = path;
        this.path_img = path_img;
        this.imgs = imgs;
        this.info = info;
        this.price = price;
        this.num = num;
        this.appended = appended;
    }

    public static void copy(ItemAppendBean formValue, ItemAppendBean result) {
        ArrayList<String> paths = new ArrayList<>();
        paths.addAll(formValue.getPath());
        ArrayList<String> imgs = new ArrayList<>();
        imgs.addAll(formValue.getImgs());
        HashMap<String, String> path_img = new HashMap<String, String>();
        path_img.putAll(formValue.getPath_img());

        result.setmItemID(formValue.getmItemID());
        result.setmParcelId(formValue.getmParcelId());
        result.setPath(paths);
        result.setPath_img(path_img);
        result.setImgs(imgs);
        result.setInfo(formValue.getInfo());
        result.setPrice(formValue.getPrice());
        result.setNum(formValue.getNum());
        result.setAppended(formValue.isAppended());

        result.setMaterails(formValue.getMaterails());
        result.setCategory(formValue.getCategory());
        result.setBrandName(formValue.getBrandName());
    }


    public ItemAppendBean(List<String> path, Map<String, String> path_img, List<String> imgs) {
        this.path = path;
        this.path_img = path_img;
        this.imgs = imgs;
    }

    public MaterialsBean getMaterails() {
        return materails;
    }

    public void setMaterails(MaterialsBean materails) {
        this.materails = materails;
    }

    public CategoryBean getCategory() {
        return category;
    }

    public void setCategory(CategoryBean category) {
        this.category = category;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getmParcelId() {
        return mParcelId;
    }

    public void setmParcelId(int mParcelId) {
        this.mParcelId = mParcelId;
    }

    public int getmItemID() {
        return mItemID;
    }

    public void setmItemID(int mItemID) {
        this.mItemID = mItemID;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public boolean isAppended() {
        return appended;
    }

    public void setAppended(boolean appended) {
        this.appended = appended;
    }

    public Map<String, String> getPath_img() {
        return path_img;
    }

    public void setPath_img(Map<String, String> path_img) {
        this.path_img = path_img;
    }

    public List<String> getImgs() {
        if (path_img == null) return null;
        if (imgs == null) imgs = new ArrayList<>();
        imgs.clear();
        for (String p : path) {
            String s = path_img.get(p);
            imgs.add(s);
        }
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "ItemAppendBean{" +
                "imgs=" + imgs +
                ", info='" + info + '\'' +
                ", price=" + price +
                ", num=" + num +
                ", appended=" + appended +
                '}';
    }
}
