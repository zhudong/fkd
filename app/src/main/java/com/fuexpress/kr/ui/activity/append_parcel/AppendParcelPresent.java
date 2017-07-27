package com.fuexpress.kr.ui.activity.append_parcel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.HelpSendParcelBean;
import com.fuexpress.kr.bean.IDinfoBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.AddressManager;
import com.fuexpress.kr.model.ParcelHelpDao;
import com.fuexpress.kr.model.ParcleUploadManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.AddressManagerActivity;
import com.fuexpress.kr.ui.activity.PreviewParceldiseActivity;
import com.fuexpress.kr.ui.activity.append_item.JsonSerializer;
import com.fuexpress.kr.utils.FloatUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsParcel;


/**
 * Created by yuan on 2016-6-14.
 */
public class AppendParcelPresent extends ParcelAppendContract.Presenter {
    static Handler mHandler = new Handler();
    private final ParcleUploadManager uploadManager;
    private int mWarehouseid;
    private int mCustomeraddressid;
    private int mParcelid;
    private Float mShippingFee;
    public List<String> mPhotosPath;
    private String mDeclarePrice;
    private String mProductInfo;
    private HelpSendParcelBean mParcelBean;
    private LinkedHashMap<String, String> mPhotoFileUrlMap;
    private List<String> mInfos;
    private boolean mNeedSave;
    private boolean mClose;
    public boolean mCompleteUpdate;
    private List<String> failImages;
    private double defaultWeight;
    private JsonSerializer mSerializer;
    private IDinfoBean mIDinfoBean;

    public AppendParcelPresent() {
        uploadManager = ParcleUploadManager.getInstance();
        uploadManager.reset();
    }

    @Override
    public void setmSave(boolean mSave) {
        this.mSave = mSave;
    }

    public boolean mSave;

    @Override
    public void setCompleteUpdate(boolean completeUpdate) {
        this.mCompleteUpdate = completeUpdate;
    }

    @Override
    public void getShippingList() {

    }

    @Override
    public void getShippingFee() {

    }

    @Override
    public List<String> getPhotosPath() {
        return mPhotosPath;
    }

    @Override
    public void setPhotosPath(List<String> photosPath) {
        mPhotosPath = photosPath;
    }

    @Override
    public void onStart() {
        mPhotoFileUrlMap = new LinkedHashMap<>();
        mPhotosPath = new ArrayList<>();
        mInfos = new ArrayList<>();
        mSerializer = new JsonSerializer();

        if (mParcelBean == null) {
           /* List<HelpSendParcelBean> allParcel = ParcelHelpDao.getInstance(context).getAllParcel(false);
            if (allParcel != null && allParcel.size() > 0) {
                mParcelBean = allParcel.get(0);
            }*/
            mParcelBean = ParcelHelpDao.getInstance(context).getEditParcel();
        }
        if (mParcelBean != null) {
            reStore();
        } else {
            mParcelBean = new HelpSendParcelBean();
            mIDinfoBean = new IDinfoBean();
            initParcel();
        }
        mParcelBean.setDefaultWeight((float) defaultWeight);
    }

    @Override
    public void setParcelBean(HelpSendParcelBean bean) {
        this.mParcelBean = bean;
    }

    private void reStore() {
        mParcelid = mParcelBean.getParcelid();
//        mWarehouseid = mParcelBean.getWareHouseID();

        mView.shoParcelName(mParcelBean.getParcelName());
        mView.showProductInfo(mParcelBean.getProductdescription());
        mView.showDeclarePrice(mParcelBean.getProductprice());
        mView.showPircelItemCount(mParcelBean.getQty());
        mView.showPircelWeight(mParcelBean.getWeight());
        mView.showAddress(mParcelBean.getCustomeraddress());
        mView.showShippingFee(mParcelBean.getShippingFee());
        showShippingMethod(mParcelBean.getCustomeraddressid() + "", mWarehouseid + "", mParcelBean.getShippingmethodid());
        mIDinfoBean = mSerializer.deserializeIDinfo(mParcelBean.getIDCardInfo());
        if (mIDinfoBean == null) mIDinfoBean = new IDinfoBean();
        mNeedSave = true;
    }

    @Override
    public void selectAddress() {
        Intent intent = new Intent(context, AddressManagerActivity.class);
        intent.putExtra(AddressManagerActivity.BACK_TITLE, context.getString(R.string.String_parcle_add));//加上一个返回标题文件
        intent.putExtra(AddressManagerActivity.KEY_IS_CHOOSE_TYPE, true);//根据状态判断是否点击结束页面
        ((Activity) mView).startActivityForResult(intent, Constants.ADDRESS_REQUEST_CODE);
    }

    public void setmWarehouseid(int id) {
        mWarehouseid = id;
    }

    @Override
    public void setDefaultWeight(double defaultWeight) {
        this.defaultWeight = defaultWeight;
//        mParcelBean.setDefaultWeight((float) defaultWeight);
    }


    private void initParcel() {
        CsParcel.SendInitRequest.Builder builder = CsParcel.SendInitRequest.newBuilder();
        builder.setSecond(AccountManager.getInstance().mBaseUserRequest)
                .setCurrencycode(AccountManager.getInstance().getCurrencyCode())
                .setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.SendInitReponse>() {

            @Override
            public void onSuccess(final CsParcel.SendInitReponse response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mParcelid = response.getParcelid();
                        mView.shoParcelName(response.getParcelname());
//                        mWarehouseid = response.getWarehouseid();
                        mParcelBean.setCurrencyCode(response.getCurrencycode());
                        mParcelBean.setWareHouseID(mWarehouseid);
                        CsParcel.AddressList addresslist = null;
                        if (response.getAddresslistCount() > 0) {
                            addresslist = response.getAddresslist(0);
                            for (CsParcel.AddressList address : response.getAddresslistList()) {
                                if ("yes".equals(address.getIsdefaultship())) {
                                    addresslist = address;
                                }
                            }
                            mCustomeraddressid = addresslist.getCustomeraddressid();
                            showShippingMethod(mCustomeraddressid + "", mWarehouseid + "");
                            mParcelBean.setCustomeraddressid(mCustomeraddressid);
                        }
                        mView.showAddress(addresslist);
                        mParcelBean.setParcelid(mParcelid);
                        mParcelBean.setParcelName(response.getParcelname());
                        String defaultparcelweight = response.getDefaultparcelweight();
//                        mParcelBean.setDefaultWeight("".equals(defaultparcelweight) ? 0f :  FloatUtils.vlaueOf(defaultparcelweight));
                        /*自动保存*/
                        mNeedSave = true;
                        save2Db(null, false);
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void setWeight(float weight) {
        mView.showPircelWeight(weight);
        mParcelBean.setWeight(weight);
    }

    @Override
    public void setItemCount(int count) {
        mView.showPircelItemCount(count);
        mParcelBean.setParcelCount(count);
        mParcelBean.setQty(count);
    }

    @Override
    public void setDeclarePrice(float declarePrice) {
        mView.showDeclarePrice(declarePrice);
        mParcelBean.setProductprice(declarePrice);
    }

    @Override
    public HelpSendParcelBean getParcelBean() {
        return mParcelBean;
    }


    public String formatAddress(CsParcel.AddressList address) {
        String s = address.getName() + "," + address.getTelephone();
//        String region = AssetFileManager.getInstance().readFile(context, address.getRegioncode(), AssetFileManager.ADDRESS_TYPE);
//        String detail = (s + "\n" + region + "," + address.getStreet());
//        return detail;
        return "";
    }

    @Override
    public void setAddress(String topText, String addressText, int id) {
        mParcelBean.setCustomeraddressid(id);
        mParcelBean.setCustomeraddress(topText + "\n" + addressText);
        mView.showAddress(mParcelBean.getCustomeraddress());
        showShippingMethod(mParcelBean.getCustomeraddressid() + "", mWarehouseid + "");
//        save2Db(null, false);
    }

    public void updateShippingMethod() {
        showShippingMethod(mParcelBean.getCustomeraddressid() + "", mWarehouseid + "");
    }

    public void showShippingMethod(String addressID, String warehouseID) {
        showShippingMethod(addressID, warehouseID, -1);
    }

    public void showShippingMethod(String addressID, String warehouseID, final int selectid) {
        CsParcel.GetSelectAddressAjaxRequest.Builder builder = CsParcel.GetSelectAddressAjaxRequest.newBuilder()
                .setWarehouseid(warehouseID)
                .setCustomeraddressid(addressID)
                .setCurrencyCode(AccountManager.getInstance().getCurrencyCode())
                .setParcelid(mParcelid).setEstimateweight(mParcelBean.getWeight() + "")
                .setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.GetSelectAddressAjaxReponse>() {

            @Override
            public void onSuccess(final CsParcel.GetSelectAddressAjaxReponse response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<CsParcel.MerchantParcelShippingMethodList> methodLists = response.getMerchantparcelshippintmethodlistList();
                        if (mView == null) return;
                        mView.showShippingMethods(methodLists);
//                        response.getIdcard();
                        setIdInfo(response);
                        if (selectid > 0) {
                            for (CsParcel.MerchantParcelShippingMethodList methodList : methodLists) {
                                if (selectid == methodList.getParcelshippingmethodid()) {
                                    selectShippingMethod(methodList);
                                    // TODO: 2016/12/20  
                                    mIDinfoBean.setNeedId(methodList.getIsneedidcard() == 0);
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
            }
        });
    }

    void setIdInfo(CsParcel.GetSelectAddressAjaxReponse response) {
        mIDinfoBean.setServerUrlBack(response.getIdcardbackimage());
        mIDinfoBean.setServerUrlFront(response.getIdcardfrontimage());
        mIDinfoBean.setServerIDNumber(response.getIdcard());
    }


    @Override
    public int getShippingMethodId() {
        if (mParcelBean != null) {
            return mParcelBean.getShippingmethodid();
        } else {
            return 0;
        }
    }

    @Override
    public void setShippingMethodId(int shippingMethodId) {
        if (mParcelBean != null) {
            mParcelBean.setShippingmethodid(shippingMethodId);
        }
    }

    private boolean getInputInfo(boolean check) {
        boolean hasData = mView.getInfo(mInfos, check, false);
        if (!hasData && check)
            return false;
        mDeclarePrice = "".equals(mInfos.get(3)) ? "0" : mInfos.get(3);
        String count = "".equals(mInfos.get(2)) ? "0" : mInfos.get(2);
        String weight = "".equals(mInfos.get(1)) ? "0" : mInfos.get(1);
        String addressinfo = mInfos.get(0);

        mParcelBean.setProductprice( FloatUtils.vlaueOf(mDeclarePrice.replace(",", "")));
        mParcelBean.setQty(Integer.valueOf(count));
        mParcelBean.setWeight( FloatUtils.vlaueOf(weight));
        mParcelBean.setCustomeraddress(addressinfo);
        mParcelBean.setWareHouseID(mWarehouseid);
        return true;
    }


    @Override
    public void selectShippingMethod(final CsParcel.MerchantParcelShippingMethodList method) {
        mShippingFee = 0f;
        if (!getInputInfo(true))
            return;
        String[] split = method.getShippingmethodstring().split("\n");
        String title = "";
        if (split.length == 3) {
            title = split[0] /*+ "\n" + split[1]*/;
        } else if (split.length == 2) {
            title = split[0] /*+ "\n" + split[1]*/;
        } else {
            title = method.getShippingmethodstring();
        }
        mParcelBean.setShippingTitle(title);
        CsParcel.GetShippingInfoRequest.Builder builder = CsParcel.GetShippingInfoRequest.newBuilder().
                setParcelshippingmethodid(method.getParcelshippingmethodid()).
                setSecond(AccountManager.getInstance().mBaseUserRequest)
//                .setQty(mParcelBean.getQty())原来传件数现在传款数
                .setQty(mView.getItemCount())
                .setWeight(mParcelBean.getWeight())
                .setPrice(mParcelBean.getProductprice())
                .setLocalecode(AccountManager.getInstance().getLocaleCode())
                .setIsNewVersion(1);
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.GetShippingInfoReponse>() {

            @Override
            public void onSuccess(final CsParcel.GetShippingInfoReponse response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String total = "".equals(response.getTotal()) ? "0" : response.getTotal();
                        mShippingFee = NumberFormate.getNumber( FloatUtils.vlaueOf(total.replaceAll(",", "")));
                        mView.showShippingFee(mShippingFee);
                        mParcelBean.setShippingmethodid(method.getParcelshippingmethodid());
                        mParcelBean.setShippingFee(mShippingFee);
                        String shippingduty = response.getShippingduty();
                        float shippingDutyFee = 0;
                        if (!"".equals(shippingduty)) {
                            shippingDutyFee =  FloatUtils.vlaueOf(shippingduty);
                        }
                        mView.selsetShippingMethod(method.getParcelshippingmethodid(), shippingDutyFee, mShippingFee);
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.selsetShippingMethod(0, 0, mShippingFee);
                        mParcelBean.setShippingmethodid(0);
                        mShippingFee = 0f;
                        mParcelBean.setShippingFee(mShippingFee);
                        mView.showShippingFee(mShippingFee);
                        mView.showParcleDialog(errMsg, context.getString(R.string.package_confirm), "");
                    }
                });
            }
        });
    }


    @Override
    public void delectParcel() {
        if (mParcelBean != null && mParcelBean.getParcelid() > 0) {
            ParcelHelpDao.getInstance(context).delete(mParcelBean.getParcelid());
            mView.finishView();
            EventBus.getDefault().post(new BusEvent(BusEvent.Append_PARCEN_SUCESS, ""));
        }
    }

    @Override
    public void delectPhoto(List<String> photos) {
        List<String> indexs = new ArrayList<>();
        for (int i = 0; i < mPhotosPath.size(); i++) {
            String s = mPhotosPath.get(i);
            if (!photos.contains(s)) {
                indexs.add(s);
            }
        }
        if (indexs.size() > 0) {
            mPhotosPath.removeAll(indexs);
            for (String str : indexs) {
                mPhotoFileUrlMap.remove(str);
            }
        }
        mView.appendPhotos(getPreStrings());
    }

    @Override
    public void preViewPhoto(int index) {
        Intent intent = new Intent();
        intent.setClass(context, PreviewParceldiseActivity.class);
        Bundle bundle = new Bundle();
        ArrayList<String> imgs = getPreStrings();
        bundle.putSerializable("imgList", (Serializable) imgs);
        intent.putExtra("resourceType", PreviewParceldiseActivity.RESOURCE_TYPE_ADD);
        bundle.putInt("position", index);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, PreviewParceldiseActivity.REQUEST_CODE_BACK_IMG_LIST);
    }

    @NonNull
    private ArrayList<String> getPreStrings() {
        ArrayList<String> imgs = new ArrayList<>();
        for (int i = 0; i < mPhotosPath.size(); i++) {
            String key = mPhotosPath.get(i);
            String url = mPhotoFileUrlMap.get(key);
            if (url != null) {
                imgs.add(key + "," + url);
            } else {
                imgs.add(key + "," + "");
            }
        }
        return imgs;
    }

    @Override
    public void sava() {
        mParcelBean.setSaved(true);
        mClose = true;
        save2Db(null, true);
       /*

        if (failImages != null && failImages.size() > 0) {
            uploadManager.addTask(failImages);
        }
        ((BaseActivity) mView).showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, "正在上传 ... ");
        mParcelBean.setSaved(true);
        uploadManager.getAllComplete(new ParcleUploadManager.UpLoadeCompleteListener() {
            @Override
            public void complete(final float prgress, final ArrayMap map) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        if (failImages != null) failImages = null;
                        List<String> list = new ArrayList<String>();
                        ((BaseActivity) mView).showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, "正在上传:" + Math.round(prgress) + "%");
                        if (prgress >= 100) {
                            for (String key : mPhotosPath) {
                                String value = (String) map.get(key);
                                if (value == null) value = mPhotoFileUrlMap.get(key);
                                String s = value + "," + key;
                                list.add(s);
                            }
                            ((BaseActivity) mView).showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, "正在上传:" + Math.round(100) + "%");
                            mClose = true;
                            save2Db(list, true);
                            ((BaseActivity) mView).closeLoading();
                        }
                    }
                });
            }

            @Override
            public void error(final List<String> urls) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity) mView).showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, "上传失败");
                        ((BaseActivity) mView).closeLoading();
                        failImages = urls;
                    }
                });

            }
        });*/
    }

    @Override
    public void upPhoto() {
        if (mPhotosPath != null && mPhotosPath.size() > 0) {
            ArrayList<String> photsPath = new ArrayList<>();
            for (int i = 0; i < mPhotosPath.size(); i++) {
                String key = mPhotosPath.get(i);
                if (mPhotoFileUrlMap.get(key) == null) {
                    photsPath.add(mPhotosPath.get(i));
                }
            }
            if (photsPath.size() > 0) {
                uploadManager.addTask(photsPath);
            } else {
                save2Db(null, false);
            }
        }
    }


    @Override
    public void addPhotoFile(String fileName) {
        ArrayList<String> imgs = getPreStrings();
        mView.appendPhotos(imgs);
        save2Db(null, false);
    }

    @Override
    public void save2Db(List<String> imgs, boolean finsh) {
        ParcelHelpDao dao = ParcelHelpDao.getInstance(context);
        mParcelBean.setIDCardInfo(mSerializer.serializeIDinfo(mIDinfoBean));
        if (mParcelBean.getShippingmethodid() == 0) {
            mParcelBean.setShippingmethodid(((AppendParcelActivity) mView).mStartShippingID);
        }

        HelpSendParcelBean helpSendParcelBean = dao.getItem(mParcelBean.getParcelid());
        if (helpSendParcelBean == null) {
            dao.insert(mParcelBean);
            EventBus.getDefault().post(new BusEvent(BusEvent.Append_PARCEN_SUCESS, ""));
        } else {
            dao.update(mParcelBean);
            EventBus.getDefault().post(new BusEvent(BusEvent.Append_PARCEN_SUCESS, ""));
        }
        if (mClose) {
            Log.d("save", "finish");
            mView.finishView();
        }
    }

    public void saveItems(String items, String imgs) {
        mParcelBean.setProductdescription(items);
        mParcelBean.setImages(imgs);
        save2Db(null, false);
    }

    @Override
    public IDinfoBean getIdInfo() {
        return mIDinfoBean;
    }
}
