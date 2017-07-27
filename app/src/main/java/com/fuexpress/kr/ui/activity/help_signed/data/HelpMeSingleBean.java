package com.fuexpress.kr.ui.activity.help_signed.data;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.fuexpress.kr.bean.CategoryBean;
import com.fuexpress.kr.bean.MaterialsBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longer on 2016/12/24.
 * 帮我寄的参数Bean
 */
public class HelpMeSingleBean {

    private String mDesc;//描述

    private String mRemark;//备忘

    private String mWareHouseID = "";//仓库id

    private String mPrice;//价格

    private String mItemNum;//商品数量

    private String mCoverImagePath;//封面图

    private List<String> mImagePathLit;//本地图片的路径

    private ArrayMap<String, String> mPathUrlMap;//本地路径和其网上url的Map

    private boolean mIsReady = false;//参数是否准备完全了

    private boolean mIsClickConfirm = false;//是否点击了确认按钮

    private WareHouseBean mWareHouseBean;//仓库的对象

    private int index;//代表其在集合中的下标

    private String currency_sign = "";//货币符号

    private String currency_Code = "";//货币Code

    private boolean mIsUrlReady = false;//判断url是否准备充分了

    private List<String> mShowImgList;//这个集合用来存放显示的图片集合,既包括经过uri转换的地址,也包含上传到网上之后的url地址

    private CategoryBean mCategoryBean;

    private MaterialsBean mMaterialsBean;

    private String mBrandName;//品牌名称

    private int salesrequireid;//需求单id

    public int getSalesrequireid() {
        return salesrequireid;
    }

    public void setSalesrequireid(int salesrequireid) {
        this.salesrequireid = salesrequireid;
    }

    public String getBrandName() {
        return mBrandName;
    }

    public void setBrandName(String brandName) {
        mBrandName = brandName;
    }

    public MaterialsBean getMaterialsBean() {
        if (mMaterialsBean == null)
            mMaterialsBean = new MaterialsBean();
        return mMaterialsBean;
    }

    public void setMaterialsBean(MaterialsBean materialsBean) {
        mMaterialsBean = materialsBean;
    }

    public CategoryBean getCategoryBean() {
        if (mCategoryBean == null)
            mCategoryBean = new CategoryBean();
        return mCategoryBean;
    }

    public void setCategoryBean(CategoryBean categoryBean) {
        mCategoryBean = categoryBean;
    }

    public List<String> getShowImgList() {
        if (mShowImgList == null)
            mShowImgList = new ArrayList<>();
        return mShowImgList;
    }

    public String getCurrency_sign() {
        return currency_sign;
    }

    public void setCurrency_sign(String currency_sign) {
        this.currency_sign = currency_sign;
    }

    public String getCurrency_Code() {
        return currency_Code;
    }

    public void setCurrency_Code(String currency_Code) {
        this.currency_Code = currency_Code;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public WareHouseBean getWareHouseBean() {//获得仓库对象
        if (mWareHouseBean == null)
            mWareHouseBean = new WareHouseBean();
        return mWareHouseBean;
    }

    public boolean isUrlReady() {
        if (mPathUrlMap != null)
            mIsUrlReady = !mPathUrlMap.containsValue("");
        return mIsUrlReady;
    }

    public void setIsUrlReady(boolean isUrlReady) {
        mIsUrlReady = isUrlReady;
    }

    public void setWareHouseBean(WareHouseBean wareHouseBean) {//设置仓库对象
        mWareHouseBean = wareHouseBean;
    }

    public boolean isReady() {
        mIsReady = !TextUtils.isEmpty(mDesc) && !TextUtils.isEmpty(mRemark) && !TextUtils.isEmpty(mPrice)
                && !TextUtils.isEmpty(mWareHouseID) && !TextUtils.isEmpty(mItemNum) && getPathUrlMap().keySet().size() > 0
                && !getPathUrlMap().containsValue("");
        return mIsReady;
    }

    public boolean isClickConfirm() {
        return mIsClickConfirm;
    }

    public void setIsClickConfirm(boolean isClickConfirm) {
        mIsClickConfirm = isClickConfirm;
    }

    public ArrayMap<String, String> getPathUrlMap() {
        if (mPathUrlMap == null)
            mPathUrlMap = new ArrayMap<>();
        return mPathUrlMap;
    }

    public List<String> getImagePathLit() {
        if (mImagePathLit == null)
            mImagePathLit = new ArrayList<>();
        return mImagePathLit;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public String getRemark() {
        return mRemark;
    }

    public void setRemark(String remark) {
        mRemark = remark;
    }

    public String getWareHouseID() {
        return mWareHouseID;
    }

    public void setWareHouseID(String wareHouseID) {
        mWareHouseID = wareHouseID;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getItemNum() {
        return mItemNum;
    }

    public void setItemNum(String itemNum) {
        mItemNum = itemNum;
    }

    public String getCoverImagePath() {
        return mCoverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        mCoverImagePath = coverImagePath;
    }

    public void setImagePathLit(List<String> pathList) {
        for (String path : pathList) {
            File file = new File(path);
            if (file.exists())//这个文件图片存在,我们才加入进去
                mImagePathLit.add(path);
        }
    }
}