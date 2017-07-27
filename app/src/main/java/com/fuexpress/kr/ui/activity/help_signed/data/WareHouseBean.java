package com.fuexpress.kr.ui.activity.help_signed.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Longer on 2016/12/27.
 * 仓库的Bean对象
 */
public class WareHouseBean implements Parcelable {

    private String mName;
    private String mAddress;

    private String mReceiver;

    private String mPhone;

    private String mPostCode;

    private String mID;

    public WareHouseBean() {

    }

    public WareHouseBean(String name, String address, String receiver, String phone, String postCode, String ID) {
        mName = name;
        mAddress = address;
        mReceiver = receiver;
        mPhone = phone;
        mPostCode = postCode;
        mID = ID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getReceiver() {
        return mReceiver;
    }

    public void setReceiver(String receiver) {
        mReceiver = receiver;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getPostCode() {
        return mPostCode;
    }

    public void setPostCode(String postCode) {
        mPostCode = postCode;
    }

    public String getID() {
        return mID;
    }

    public void setID(String ID) {
        mID = ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mAddress);
        dest.writeString(mReceiver);
        dest.writeString(mPhone);
        dest.writeString(mPostCode);
        dest.writeString(mID);
    }

    public static final Creator CREATOR = new Creator() {

        @Override
        public WareHouseBean createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
            WareHouseBean wareHouseBean = new WareHouseBean();
            wareHouseBean.setName(source.readString());
            wareHouseBean.setAddress(source.readString());
            wareHouseBean.setReceiver(source.readString());
            wareHouseBean.setPhone(source.readString());
            wareHouseBean.setPostCode(source.readString());
            wareHouseBean.setID(source.readString());
            return wareHouseBean;
        }

        @Override
        public WareHouseBean[] newArray(int size) {
            // TODO Auto-generated method stub
            return new WareHouseBean[size];
        }
    };
}
