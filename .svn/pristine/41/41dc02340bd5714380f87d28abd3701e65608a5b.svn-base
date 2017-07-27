package com.fuexpress.kr.ui.activity.help_send;


import com.fuexpress.kr.base.BaseModel;
import com.fuexpress.kr.base.BasePresenter;
import com.fuexpress.kr.base.BaseView;
import com.fuexpress.kr.bean.HelpSendParcelBean;

import java.util.List;

import fksproto.CsBase;
import fksproto.CsParcel;
import fksproto.CsUser;


public interface HelpSendContract {
    interface Model extends BaseModel {
        List<CsParcel.Parcel> getParcels();

        float getEstimatePrice();
    }


    interface View extends BaseView {
        void showParcleList(List<HelpSendParcelBean> mParcels, boolean hasid, boolean noid);

        void showPayType(float shippingFee);

        void showEstimatePrice(float price);

        void showWarehouseDialog();
    }

    abstract class Presenter extends BasePresenter<Model, View> {
        protected abstract float getShippingFee();

        public abstract void submit();

        abstract void switchPayType();

        abstract void submitSuccess(boolean sucess);

        public abstract void reSet();

        public abstract void setPayCode(String payCode);
    }
}
