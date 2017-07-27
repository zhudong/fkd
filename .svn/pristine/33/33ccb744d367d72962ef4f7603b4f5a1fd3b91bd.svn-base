package com.fuexpress.kr.ui.activity.help_signed;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.HelpAdapterValueBean;
import com.fuexpress.kr.bean.UpLoadImageValueBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.help_signed.data.HelpSingedDataRepository;
import com.fuexpress.kr.ui.adapter.HelpAdapterInterface;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.InputBoxView;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.StringUtils;
import com.fuexpress.kr.utils.UpLoadImageUtils;

import com.google.protobuf.GeneratedMessage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsBase;
import fksproto.CsParcel;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Longer on 2016/12/5.
 * 这个是具体实现合同类中的方法的实体类,在这里我们将对各种方法进行实现
 */
public class HelpSignedPresenter implements HelpSignedContract.Presenter {

    @NonNull
    private final HelpSingedDataRepository mHelpSingedDataRepository;//数据仓库对象,不可改且私有

    @NonNull
    private final HelpSignedContract.View mHelpSingedView;//视图层对象,也是不可改且私有

    private boolean mIsCilckSub = false;//判断是否点击了提交

    private final int mMaxDeMand = 5;//当前最大需求单数量为5个

    private ArrayMap<Integer, Boolean> mWantUpLoadIndexMap;//想上传图片的Index集合

    //上传图片成功的进度回调
    private final UpLoadImageUtils.UpLaodUtilsListener mUpLaodUtilsListener = new UpLoadImageUtils.UpLaodUtilsListener() {
        @Override
        public void upLoadStateListener(boolean isSuccess, int progress, int index, String url) {
            if (mIsCilckSub) {//点击了确认按钮,当还没完成的时候需要显示进度
                if (isSuccess) {
                    ArrayMap<String, String> pathUrlMap = addImageUrlIntoRepository(url, index);
                    editItemImage(pathUrlMap, index);
                    if (progress < 100) {
                        /*String showString = mHelpSingedView.getViewContext().getString(R.string.uploading_string_progress, index + 1, progress);
                        mHelpSingedView.showLoadingView(SweetAlertDialog.PROGRESS_TYPE, showString);*/
                        mHelpSingedView.showLoadingView(SweetAlertDialog.PROGRESS_TYPE, mHelpSingedView.getViewContext().getString(R.string.uploading_string_image));
                    } else {
                        mWantUpLoadIndexMap.put(index, true);//说明该index下的这个图片列表上传完了
                        // TODO: 2016/12/11 在点击了提交按钮之后图片上传完了还需要提交到服务器
                        if (!mWantUpLoadIndexMap.values().contains(false)) {//判断一下需要上传的集合中是否有没完成的
                            mHelpSingedView.dissMissLoadingView(0);
                            boolean isReady = checkItemParameter(mHelpSingedDataRepository.getHelpSignedDataList());
                            if (isReady)
                                submitItemToServer(mHelpSingedDataRepository.getHelpSignedDataList());
                        }
                    }
                } else {
                    mHelpSingedView.dissMissLoadingView(0);
                    mHelpSingedView.showCustomToast("第:" + index + "个需求图片上传失败,这是返回结果:" + url);//错误的话显示错误信息就可以了
                }
            } else {//还没点击确认按钮,成功了的话就丢进map集合就行了
                if (isSuccess) {
                    ArrayMap<String, String> parhUrlMap = addImageUrlIntoRepository(url, index);
                    if (100 == progress)
                        editItemImage(parhUrlMap, index);
                } else {
                    mHelpSingedView.showCustomToast("第:" + index + "个需求图片上传失败,这是返回结果:" + url);//错误的话显示错误信息就可以了
                }
            }

        }
    };
    private HelpAdapterValueBean mHelpAdapterValueBean;
    private int mStartISItemPosition = -1;
    private int mOnItemClickPosition = -1;


    public HelpSignedPresenter(@NonNull HelpSingedDataRepository helpSingedDataRepository, @NonNull HelpSignedContract.View helpSingedView) {
        mHelpSingedDataRepository = checkNotNull(helpSingedDataRepository, "repository is not Null");
        mHelpSingedView = helpSingedView;
    }

    @Override
    public void initPresenterData() {//初始化一些数据
        //mIntegerInputBoxViewArrayMap = mHelpSingedDataRepository.getInputBoxViewArryMap();
    }

    @Override
    public void addNewDeMand() {//添加新的需求单
        CsParcel.ProductDataList.Builder builder = CsParcel.ProductDataList.newBuilder();
        mHelpSingedDataRepository.saveHelpSignedData(builder.build());
        mHelpSingedView.referListViewShow(true);
    }

    @Override
    public int getItemOnclickPosition() {
        return mOnItemClickPosition;
    }

    @Override
    public void setItemOnclickPosition(int position) {
        mOnItemClickPosition = position;
    }

    @Override
    public String transformPrice(String currencyCode, String price) {//转换价格(当是韩元的时候就去除小数)
        String changePrice = price;
        if ("KRW".equals(currencyCode))
            if (TextUtils.isEmpty(price))
                changePrice = StringUtils.changeFolatToString(Float.parseFloat(price));

        return changePrice;
    }

    @Override
    public List<String> handleImageSelectorReturn(int index, List<String> list) {//处理返回的图片集合

        ArrayMap imageIndexArrayMap = mHelpSingedDataRepository.getImageIndexArrayMap();
        //这里我们可以剔除掉已经上传过的
        ArrayMap<Integer, ArrayMap<String, String>> thisIndexUpLoadUrlArrayMap = mHelpSingedDataRepository.getImageUpLoadUrlArrayMap();
        ArrayMap<String, String> pathKeyUrlMap = thisIndexUpLoadUrlArrayMap.get(index);
        if (pathKeyUrlMap == null)
            pathKeyUrlMap = new ArrayMap<>();
        List<String> shouldUpLoadImagePathList = new ArrayList<>();
        for (String returnImagePath : list) {
            String url = pathKeyUrlMap.get(returnImagePath);
            if (TextUtils.isEmpty(url))
                shouldUpLoadImagePathList.add(returnImagePath);
        }
        imageIndexArrayMap.put(index, list);//装进本地的路径集合
        mHelpSingedView.referListViewShow(false);//刷新界面
        if (shouldUpLoadImagePathList.size() > 0)
            return shouldUpLoadImagePathList;
        else {
            //if (pathKeyUrlMap != null)
            editItemImage(pathKeyUrlMap, index);
            return null;
        }
    }

    @Override
    public void upLoadImageToUpYun(List<String> imagePathList, int type, UpLoadImageUtils.UpLaodUtilsListener upLaodUtilsListener, int index) {
        //UpLoadImageUtils.getInstance().zoomImageAndUpLoad(imagePathList, type, index, upLaodUtilsListener);
        if (mWantUpLoadIndexMap == null)
            mWantUpLoadIndexMap = new ArrayMap<>();
        mWantUpLoadIndexMap.put(index, false);//只要能进到这个方法的,都是要执行上传图片操作的,那么我们就将其上传状态置为false
        UpLoadImageValueBean build = new UpLoadImageValueBean.Builder()
                .setImagePathList(imagePathList)
                .setIndex(index)
                .setType(type)
                .build();
        UpLoadImageUtils.getInstance().zoomImageAndUpLoadToYun(build, mUpLaodUtilsListener);
    }

    @Override
    public void enterItemDesc(int index, String itemDesc) {
        CsParcel.ProductDataList helpSignedDataOnly = mHelpSingedDataRepository.getHelpSignedDataOnly(index);
        if (!helpSignedDataOnly.getProductdescription().equals(itemDesc)) {
            CsParcel.ProductDataList.Builder tempBuilder = helpSignedDataOnly.toBuilder();
            tempBuilder.setProductdescription(itemDesc);
            mHelpSingedDataRepository.editHelpSignedData(index, tempBuilder.build());
            LogUtils.e("我是第:" + index + "个需求单,我的描述被修改了:" + tempBuilder.getProductdescription());
        }
    }

    @Override
    public void enterItemRemarks(int index, String itemReMarks) {
        CsParcel.ProductDataList helpSignedDataOnly = mHelpSingedDataRepository.getHelpSignedDataOnly(index);
        if (!helpSignedDataOnly.getProductremark().equals(itemReMarks)) {
            CsParcel.ProductDataList.Builder tempBuilder = helpSignedDataOnly.toBuilder();
            tempBuilder.setProductremark(itemReMarks);
            mHelpSingedDataRepository.editHelpSignedData(index, tempBuilder.build());
            LogUtils.e("我是第:" + index + "个需求单,我的描备注被修改了:" + tempBuilder.getProductremark());
        }

    }

    @Override
    public void enterItemWareHouse(int index, String wareHouseID, CsBase.Warehouse warehouse) {
        CsParcel.ProductDataList helpSignedDataOnly = mHelpSingedDataRepository.getHelpSignedDataOnly(index);
        if (!helpSignedDataOnly.getWarehouseid().equals(wareHouseID)) {
            CsParcel.ProductDataList.Builder tempBuilder = helpSignedDataOnly.toBuilder();
            tempBuilder.setWarehouseid(wareHouseID);
            mHelpSingedDataRepository.editHelpSignedData(index, tempBuilder.build());
            LogUtils.e("我是第:" + index + "个需求单,我的仓库id被修改了:" + tempBuilder.getWarehouseid());
            mHelpSingedView.setAdapterWareHouseData(wareHouseID, warehouse);
            mHelpSingedView.referListViewShow(false);
        }
    }

    @Override
    public void enterItemPrice(int index, String price) {
        CsParcel.ProductDataList helpSignedDataOnly = mHelpSingedDataRepository.getHelpSignedDataOnly(index);
        if (!helpSignedDataOnly.getPrice().equals(price)) {
            CsParcel.ProductDataList.Builder tempBuilder = helpSignedDataOnly.toBuilder();
            tempBuilder.setPrice(price);
            mHelpSingedDataRepository.editHelpSignedData(index, tempBuilder.build());
            LogUtils.e("我是第:" + index + "个需求单,我的价格被修改了:" + tempBuilder.getPrice());
        }
    }

    @Override
    public void enterItemQuantity(int index, String itemQuantity) {
        CsParcel.ProductDataList helpSignedDataOnly = mHelpSingedDataRepository.getHelpSignedDataOnly(index);
        if (!helpSignedDataOnly.getNum().equals(itemQuantity)) {
            CsParcel.ProductDataList.Builder tempBuilder = helpSignedDataOnly.toBuilder();
            tempBuilder.setNum(itemQuantity);
            mHelpSingedDataRepository.editHelpSignedData(index, tempBuilder.build());
            LogUtils.e("我是第:" + index + "个需求单,我的数量被修改了:" + tempBuilder.getNum());
        }
    }

    @Override
    public void submitItemToServer(List<CsParcel.ProductDataList> productDataLists) {
        mHelpSingedView.showLoadingView(SweetAlertDialog.PROGRESS_TYPE, "上传中");
        mIsCilckSub = true;
        // TODO: 2016/12/11 提交到服务器
        LogUtils.e("提交到服务器");
        CsParcel.SaveHelpReceiveRequest.Builder builder = CsParcel.SaveHelpReceiveRequest.newBuilder();
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setSumcount(String.valueOf(productDataLists.size()));
        builder.addAllProductlist(productDataLists);
        builder.setUserhead(AccountManager.getInstance().getBaseUserRequest());
        LogUtils.e("上传的参数:" + builder.toString());
        NetEngine.postRequest(builder, new INetEngineListener<CsParcel.SaveHelpReceiveResponse>() {
            @Override
            public void onSuccess(CsParcel.SaveHelpReceiveResponse response) {
                LogUtils.e("成功:" + response.toString());
                mHelpSingedView.showLoadingView(SweetAlertDialog.SUCCESS_TYPE, "提交成功");
                mHelpSingedView.dissMissLoadingView(1000);
                clearAllDatas();
                mHelpSingedView.referListViewShow(false);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                LogUtils.e("失败:" + "  " + ret + "  " + errMsg);
                mHelpSingedView.showLoadingView(SweetAlertDialog.ERROR_TYPE, "提交失败:" + ret);
                mHelpSingedView.dissMissLoadingView(1000);
            }
        });
    }

    @Override
    public void editData(EditText editText, int index, int resKey) {//按照输入框是什么来分配相应的方法
        String enterText = editText.getText().toString().trim();
        //LogUtils.e("修改前:" + enterText);
        switch (resKey) {
            case R.string.hm_item_desc:
                enterItemDesc(index, enterText);
                break;
            case R.string.hm_item_remark:
                enterItemRemarks(index, enterText);
                break;
            /*case R.string.hm_item_ware_house:
                enterItemWareHouse(index, enterText);
                break;*/
            case R.string.hm_item_request_price:
                enterItemPrice(index, enterText);
                break;
            case R.string.hm_item_quantity:
                enterItemQuantity(index, enterText);
                break;
        }
    }

    @Override
    public void editItemImage(ArrayMap<String, String> pathUrlMap, int index) {//把已经上传好的图片url添加到需求单中的Item里
        CsParcel.ProductDataList helpSignedDataOnly = mHelpSingedDataRepository.getHelpSignedDataOnly(index);
        CsParcel.ProductDataList.Builder tempBuilder = helpSignedDataOnly.toBuilder();
        CsBase.ItemImage.Builder imgBuilder = CsBase.ItemImage.newBuilder();

        //因为多线程的无序性质,我们这里需要对整个图片集合进行排序:
        List<String> tempPathList = new ArrayList<>();
        ArrayList<String> thisIndexPathList = mHelpSingedDataRepository.getImageIndexArrayMap().get(index);
        tempPathList.addAll(thisIndexPathList);
        tempBuilder.clearExtra();
        for (int i = 0; i < tempPathList.size(); i++) {
            String path = tempPathList.get(i);
            String url = pathUrlMap.get(path);
            imgBuilder.setImageUrl(url);
            tempBuilder.addExtra(imgBuilder);
        }
        tempBuilder.setImageNum(tempPathList.size());
        /*for (String url : imageUrlList) {
            imgBuilder.setImageUrl(url);
            tempBuilder.addExtra(imgBuilder);
        }*/
        mHelpSingedDataRepository.editHelpSignedData(index, tempBuilder.build());
        LogUtils.e("图片装载完毕");
    }

    @Override
    public ArrayMap<String, String> addImageUrlIntoRepository(String url, int index) {
        ArrayMap<Integer, ArrayMap<String, String>> imageUpLoadUrlArrayMap = mHelpSingedDataRepository.getImageUpLoadUrlArrayMap();
        ArrayMap<String, String> pathAndUrlArrayMap = imageUpLoadUrlArrayMap.get(index);
        if (null == pathAndUrlArrayMap)
            pathAndUrlArrayMap = new ArrayMap<>();
        String[] urlSplit = url.split(",");
        pathAndUrlArrayMap.put(urlSplit[1], urlSplit[0]);
        imageUpLoadUrlArrayMap.put(index, pathAndUrlArrayMap);
        return pathAndUrlArrayMap;
    }

    @Override
    public boolean checkItemParameter(List<CsParcel.ProductDataList> lists) {//检查每个准备提交的需求单是否正确填写了
        boolean isReadySub = true;
        for (int i = 0; i < lists.size(); i++) {
            CsParcel.ProductDataList productDataList = lists.get(i);
            if (!(isReadySub = checkItemImageUrl(productDataList.getExtraList())))
                mHelpSingedView.showCustomToast(mHelpSingedView.getViewContext().getString(R.string.hs_note_image, i + 1));
            else if (!(isReadySub = checkItemDescIsRight(productDataList.getProductdescription())))
                mHelpSingedView.showCustomToast(mHelpSingedView.getViewContext().getString(R.string.hs_note_desc, i + 1));
            else if (!(isReadySub = checkRemarkIsRight(productDataList.getProductremark())))
                mHelpSingedView.showCustomToast(mHelpSingedView.getViewContext().getString(R.string.hs_note_remark, i + 1));
            else if (!(isReadySub = isChooseWareHouse(productDataList.getWarehouseid())))
                mHelpSingedView.showCustomToast(mHelpSingedView.getViewContext().getString(R.string.hs_note_warehouse, i + 1));
            else if (!(isReadySub = checkPriceIsRight(productDataList.getPrice())))
                mHelpSingedView.showCustomToast(mHelpSingedView.getViewContext().getString(R.string.hs_note_price, i + 1));
            else if (!(isReadySub = checkItemQuantity(productDataList.getNum())))
                mHelpSingedView.showCustomToast(mHelpSingedView.getViewContext().getString(R.string.hs_note_quantity, i + 1));
            if (!isReadySub)
                break;
        }
        return isReadySub;
    }


    @Override
    public boolean checkItemDescIsRight(String desc) {
        return !TextUtils.isEmpty(desc);
    }

    @Override
    public boolean checkRemarkIsRight(String remark) {
        return !TextUtils.isEmpty(remark);
    }

    @Override
    public boolean isChooseWareHouse(String wareHouseID) {//判断是否为空
        return !TextUtils.isEmpty(wareHouseID);
    }

    @Override
    public boolean checkPriceIsRight(String price) {//申报价格这里的需求按照实际情况来,目前只是判断是否为空
        return !TextUtils.isEmpty(price);
    }

    @Override
    public boolean checkItemQuantity(String quantity) {//单品数量这里按照实际情况来,目前只是判断是否为空
        return !TextUtils.isEmpty(quantity);
    }

    @Override
    public boolean checkItemImageUrl(List<CsBase.ItemImage> list) {
        return list.size() > 0;
    }

    @Override
    public void deleteDemand(int index) {//删除其中一个需求单
        boolean isSuccess = mHelpSingedDataRepository.deleteHelpSignedData(index);
        if (isSuccess)
            mHelpSingedView.referListViewShow(false);
        else {
            mHelpSingedView.showLoadingView(SweetAlertDialog.WARNING_TYPE, "至少需要一个需求单");
            mHelpSingedView.dissMissLoadingView(1000);
        }
    }

    @Override
    public void clearAllDatas() {//清除掉所有的数据
        mHelpSingedDataRepository.deletAllHelpSignedData();
    }

    @Override
    public HelpAdapterValueBean getHelpDataValueBean(Context context, HelpAdapterInterface
            helpAdapterInterface) {
        if (mHelpAdapterValueBean == null) {
            mHelpAdapterValueBean = new HelpAdapterValueBean();
            mHelpAdapterValueBean.setContext(context);
            mHelpAdapterValueBean.setDataLists(mHelpSingedDataRepository.getHelpSignedDataList());
            mHelpAdapterValueBean.setHelpAdapterInterface(helpAdapterInterface);
            //mHelpAdapterValueBean.setInputBoxArrayMap(mIntegerInputBoxViewArrayMap);
            mHelpAdapterValueBean.setMaxSize(mMaxDeMand);
        }
        //helpAdapterValueBean.setType("");暂时不需要
        return mHelpAdapterValueBean;
    }

    @Override
    public void setChooseImageItemPosition(int position) {//适配6.0权限,我们需要保存一个启动图片选择器的position
        mStartISItemPosition = position;
    }

    @Override
    public int getChooseImageItemPosition() {
        return mStartISItemPosition;
    }

    @Override
    public ArrayList<String> getItemImageList(int position) {
        ArrayList<String> imagePathList = mHelpSingedDataRepository.getImageIndexArrayMap().get(position);
        if (imagePathList == null) {
            imagePathList = new ArrayList<>();
        }
        return imagePathList;
    }

    @Override
    public ArrayMap<Integer, ArrayList<String>> getItemLocalPathMap() {
        return mHelpSingedDataRepository.getImageIndexArrayMap();
    }

    @Override
    public UpLoadImageUtils.UpLaodUtilsListener getPresenterUpLoadListener() {
        return mUpLaodUtilsListener;
    }

    @Override
    public List<CsParcel.ProductDataList> getProductDataList() {
        return mHelpSingedDataRepository.getHelpSignedDataList();
    }

    @Override
    public void subscribe() {//实际上只是执行一些初始化的任务
        initPresenterData();
    }

    @Override
    public void unsubscribe() {//解除一些订阅之类的

    }
}
