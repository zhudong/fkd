package com.fuexpress.kr.model;

import java.io.Serializable;

/**
 * Created by Longer on 2017/6/23.
 */
public class ItemBean implements Serializable {
    public ItemBean(){}
    public long itemid; //单品id
    public String name; //单品名称
    public String title; //单品标题
    public String image_url; //单品图片url
    public int default_currency; //单品默认货币
    public float default_price; //单品默认价格
    public String desc; //单品描述
    public int like_count; //单品喜欢数
    public boolean is_like; //单品是否喜欢
    public float exchange_rate=1;//对RMB汇率
    public long album_id;//原创或者转采的图集ID
    public float image_ratio;//单品small格式图片宽高比
    public boolean cannot_buyit;//是否能购买
    public long crowd_id;//拼单Id
    public String place;//在哪儿买
    public long crowd_product_id;
    public long product_id;
    public int alert_type;
    public int is_crowd;//1不显示按钮，2参加拼单，3、发起拼单
    public long crowd_order_id;// 参与拼单id add by jack.zuoquan

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String currency_code;// 参与拼单id add by jack.zuoquan

    public long getCreateId() {
        return create_id;
    }

    public void setCreateId(long create_id) {
        this.create_id = create_id;
    }

    public long getCrowdOrderId() {
        return crowd_order_id;
    }

    public void setCrowdOrderId(long crowd_order_id) {
        this.crowd_order_id = crowd_order_id;
    }

    public int getIsCrowd() {
        return is_crowd;
    }

    public void setIsCrowd(int is_crowd) {
        this.is_crowd = is_crowd;
    }

    public int getAlertType() {
        return alert_type;
    }

    public void setAlertType(int alert_type) {
        this.alert_type = alert_type;
    }

    public long create_id;//创建者id add by jack.zuoquan

    public void setItemid(long itemid) {
        this.itemid = itemid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public void setDefault_currency(int default_currency) {
        this.default_currency = default_currency;
    }

    public void setDefault_price(float default_price) {
        this.default_price = default_price;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public void setIs_like(boolean is_like) {
        this.is_like = is_like;
    }

    public void setExchange_rate(float exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }

    public void setImage_ratio(float image_ratio) {
        this.image_ratio = image_ratio;
    }

    public void setCannot_buyit(boolean cannot_buyit) {
        this.cannot_buyit = cannot_buyit;
    }

    public void setCrowd_id(long crowd_id) {
        this.crowd_id = crowd_id;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setCrowd_product_id(long crowd_product_id) {
        this.crowd_product_id = crowd_product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public long getItemid() {
        return itemid;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return image_url;
    }

    public int getDefaultCurrency() {
        return default_currency;
    }

    public float getDefaultPrice() {
        return default_price;
    }

    public String getDesc() {
        return desc;
    }

    public int getLikeCount() {
        return like_count;
    }

    public boolean hasIsLike() {
        return is_like;
    }

    public float getExchangeRate() {
        return exchange_rate;
    }

    public long getAlbumId() {
        return album_id;
    }

    public float getImageRatio() {
        return image_ratio;
    }

    public boolean getCannotBuyit() {
        return cannot_buyit;
    }

    public long getCrowdId() {
        return crowd_id;
    }

    public String getPlace() {
        return place;
    }

    public long getCrowdProductId() {
        return crowd_product_id;
    }

    public long getProductId() {
        return product_id;
    }
}
