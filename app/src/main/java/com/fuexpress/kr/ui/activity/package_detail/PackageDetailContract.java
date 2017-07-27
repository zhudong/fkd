package com.fuexpress.kr.ui.activity.package_detail;


import com.fuexpress.kr.base.BaseModel;
import com.fuexpress.kr.base.BasePresenter;
import com.fuexpress.kr.base.BaseView;
import com.fuexpress.kr.bean.ParcelItemBean;

import java.util.List;

import fksproto.CsAddress;
import fksproto.CsParcel;
import fksproto.CsUser;


public interface PackageDetailContract {
    interface Model extends BaseModel {
        //        float getEstimatePrice();

    }


    interface View extends BaseView {
        //        void showParcleList(List<CsParcel.Parcel> mParcels);
//        void showPayType(float price, String payType);
//        void showEstimatePrice(float price);
        void showTitle(CsParcel.Parcel parcel);

        void hintHeader();

        void hitFoot();

        void showHeader(CsParcel.Parcel parcel, String wareHouse, boolean outStore);

        void showComment(List<CsParcel.ParcelMessage> comments);

        void showProductInfo(int count, float declarePrice);

        void showTransportInfo(String name, String detail);

        void setParcelsItme(List<ParcelItemBean> itemBeans, boolean orderParcel);

        void showCustomerAddress(String topText, String addressText, int id);

        void showCustomerAddress(CsAddress.CustomerAddress address);

        void showParcelAddress(CsAddress.CustomerAddress address);

        void showShippingMethods(List<CsParcel.MerchantParcelShippingMethodList> methods);

        void showShippingMethodInsurance(int isShow, float maxdeclaredvalue, float declareValue, float premiumrate, float premium, float shippingid, CsParcel.SelectShippingMethodReponse response);

        void showBalanceAndPayType(String result);

        void showEstimatePrice(float price);

        void showEstimateWeight(float weight);

        void showIdNumber(boolean show, String number);

        float getPureFee();

        void setTraceVisibility(boolean visibility, List<CsParcel.ParcelItemList> size);

        void showInsurance(CsParcel.MerchantParcelShippingMethodList shippingMethod);
        void showParcleDialog(String message, final int type, final String cancelText);

    }

    abstract class Presenter extends BasePresenter<Model, View> {
        public static String mParcelName;

        abstract float getShippingfee();

        //        public abstract void getParcelList();
//        public abstract void addParcel();
        public abstract void toPay();

        public abstract void switchPayType();

        public abstract void choiceAddress();

        public abstract void selectShippingMethod(CsParcel.MerchantParcelShippingMethodList method);


        abstract void setAddress(String topText, String addressText, int id);

        protected abstract void submitSucess(boolean sucess);

        public abstract  void setCouponsAndCalc(boolean freshCoupon);

        public abstract String getCurrencyCode();

        public abstract CsParcel.Parcel getParcel();

        public abstract void inputIdNumber();

        public abstract void setIdInfo(String idCardNumber, String idCardFront, String idCardBack);

        public abstract boolean checkIdCard();

        public abstract OrderParcelUseCase getOrderParcelUseCase();

        public abstract void traceShiping();
    }
}
