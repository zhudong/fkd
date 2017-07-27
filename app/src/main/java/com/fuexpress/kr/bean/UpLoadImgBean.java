package com.fuexpress.kr.bean;

import com.fuexpress.kr.conf.Constants;

/**
 * @创建者 longer
 * @创时间 2016/9/28
 * @描述 上传图片到upyun完之后的json数据返回处理类
 * @版本 $Rev:
 * @更新者 $Author:
 * @更新时间 $Date:
 * @更新描述 TODO
 */

public class UpLoadImgBean {
    /**
     * bucket_name : dongimage
     * last_modified : 1475047804
     * image_frames : 1
     * path : /merchantapp/item/2016/0928/15/9401664d-f101-4c40-9b7d-39f732ca14da.jpg
     * signature : 247aa6592f8376705608f86311ebff44
     * file_size : 172978
     * image_width : 432
     * image_type : JPEG
     * image_height : 648
     * mimetype : image/jpeg
     */

    private String bucket_name;
    private String last_modified;
    private int image_frames;
    private String path;
    private String signature;
    private int file_size;
    private int image_width;
    private String image_type;
    private int image_height;
    private String mimetype;

    public String getBucket_name() {
        return bucket_name;
    }

    public void setBucket_name(String bucket_name) {
        this.bucket_name = bucket_name;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public int getImage_frames() {
        return image_frames;
    }

    public void setImage_frames(int image_frames) {
        this.image_frames = image_frames;
    }

    public String getPath() {
        return Constants.IMAGE_URL_OFFICIAL + path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }

    public int getImage_width() {
        return image_width;
    }

    public void setImage_width(int image_width) {
        this.image_width = image_width;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public int getImage_height() {
        return image_height;
    }

    public void setImage_height(int image_height) {
        this.image_height = image_height;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }
    /*"bucket_name":"dongimage",
            "last_modified":1475047804,
            "image_frames":1,
            "path":"\/merchantapp\/item\/2016\/0928\/15\/9401664d-f101-4c40-9b7d-39f732ca14da.jpg",
            "signature":"247aa6592f8376705608f86311ebff44","file_size":172978,
            "image_width":432,"image_type":"JPEG",
            "image_height":648,
            "mimetype":"image\/jpeg"*/
    /*String bucket_name;
    String last_modified;
    "image_frames":1,
            "path":"\/merchantapp\/item\/2016\/0928\/15\/9401664d-f101-4c40-9b7d-39f732ca14da.jpg",
            "signature":"247aa6592f8376705608f86311ebff44","file_size":172978,
            "image_width":432,"image_type":"JPEG",
            "image_height":648,
            "mimetype":"image\/jpeg"*/

}
