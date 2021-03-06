package com.fuexpress.kr.ui.activity.order_show;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.UpLoadImageValueBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.PreviewMerchandiseActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.ui.view.imageselector.ImageSelectorActivity;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.UpLoadImageUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsItem;

public class OrderShowActivity extends BaseActivity {


    private View mRootView;
    private GridView mGv_show_head;
    private EditText mEd_show_context;
    private Button mBtn_submit_order_show;
    private OrderShowAdapter mOrderShowAdapter;
    private ArrayList<String> mImagePathList;
    private final static int mISRequestCode = 1000;
    private ArrayMap<String, String> mPathAndUrlMap;
    private boolean mIsClickSubmit = false;
    private boolean mIsUpLoadImageComplete = false;
    private int mItemID;
    private String mCcommand;
    public static final String KEY_ITEM_ID = "ITEM_ID";
    private boolean mIsShowingUpLoad = false;


    private UpLoadImageUtils.UpLaodUtilsListener mUpLaodUtilsListener = new UpLoadImageUtils.UpLaodUtilsListener() {
        @Override
        public void upLoadStateListener(boolean isSuccess, int progress, final int index, String url) {
            if (isSuccess) {
                LogUtils.e(url);
                if (mIsClickSubmit) {
                    putTheUrlToMap(url);
                    mIsUpLoadImageComplete = progress == 100;
                    if (mIsUpLoadImageComplete) {
                        if (checkData()) upLoadShowToServer();
                    } else runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, getString(R.string.uploading_string_image));
                            mIsShowingUpLoad = true;
                        }
                    });
                } else {
                    putTheUrlToMap(url);
                    mIsUpLoadImageComplete = progress == 100;
                }

            } else {
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.makeText(OrderShowActivity.this, "Fail to upload image in " + index + ",Please Check", Toast.LENGTH_SHORT).show();
                    }
                });*/

                showCustomerToastSafly("Fail to upload image in " + index + ",Please Check");
            }
        }
    };


    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_order_show, null);
        TitleBarView title_in_order_show = (TitleBarView) mRootView.findViewById(R.id.title_in_order_show);
        title_in_order_show.getIv_in_title_back().setOnClickListener(this);
        title_in_order_show.setTitleText(getString(R.string.order_show_title));
        TextView backTextView = title_in_order_show.getmTv_in_title_back_tv();
        backTextView.setOnClickListener(this);
        backTextView.setText(getString(R.string.String_item_title));
        mGv_show_head = (GridView) mRootView.findViewById(R.id.gv_show_head);
        mGv_show_head.setOnItemClickListener(this);
        mEd_show_context = (EditText) mRootView.findViewById(R.id.ed_show_context);
        mBtn_submit_order_show = (Button) mRootView.findViewById(R.id.btn_submit_order_show);
        mBtn_submit_order_show.setOnClickListener(this);
        return mRootView;
    }


    @Override
    public void initView() {
        mItemID = getIntent().getIntExtra(KEY_ITEM_ID, 0);
        if (mItemID == 0) CustomToast.makeText(this, "itemID is 0", Toast.LENGTH_SHORT).show();
        mImagePathList = new ArrayList<>();
        mOrderShowAdapter = new OrderShowAdapter(this, mImagePathList, 6);
        mGv_show_head.setAdapter(mOrderShowAdapter);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == mImagePathList.size()) {
            // TODO: 2016/12/22 启动图片选择器
            //CustomToast.makeText(mContext, "启动图片选择器", Toast.LENGTH_SHORT).show();
            MPermissions.requestPermissions(this, 3, Manifest.permission.WRITE_EXTERNAL_STORAGE);//请求权限
        } else {
            // TODO: 2016/12/22 启动图片预览
            Intent intent = new Intent();
            intent.setClass(this, PreviewMerchandiseActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("imgList", mImagePathList);
            intent.putExtra("resourceType", PreviewMerchandiseActivity.RESOURCE_TYPE_ADD);
            bundle.putInt("position", position);
            intent.putExtras(bundle);
            //intent.putExtra(PreviewMerchandiseActivity.JUST_LOAD_BM, true);
            startActivityForResult(intent, PreviewMerchandiseActivity.REQUEST_CODE_BACK_IMG_LIST);
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.btn_submit_order_show:
                mIsClickSubmit = true;
                if (checkData()) upLoadShowToServer();
                break;
        }
    }

    @PermissionGrant(3)
    public void requestContactSuccess() {//请求了读写的权限之后
        //ArrayList<String> itemImageList = mHelpSignedPresenter.getItemImageList(chooseImageItemPosition);
        UIUtils.startImageSelectorForOrderShow(mISRequestCode, mImagePathList, this, 6);
        //UIUtils.startImageSelectorForAddItem(mISRequestCode, mImagePathList, this, 6 - mImagePathList.size());
    }

    @PermissionDenied(3)
    public void requestContactFailed() {
        CustomToast.makeText(this, "DENY ACCESS CONTACTS!", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case mISRequestCode:
                    // TODO: 2016/12/23 处理图片选择器返回的图片
                    mImagePathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                    ArrayList<String> shouldUpLoadImageList = checkImageIsUploaded(mImagePathList);
                    if (shouldUpLoadImageList.size() > 0)
                        upLoadImageToYun(shouldUpLoadImageList);
                    break;
                case PreviewMerchandiseActivity.REQUEST_CODE_BACK_IMG_LIST:
                    // TODO: 2016/12/23 处理预览图片返回的图片
                    mImagePathList = (ArrayList<String>) data.getExtras().getSerializable("backImgList");
                    mOrderShowAdapter.setImagePathList(mImagePathList);
                    mOrderShowAdapter.notifyDataSetChanged();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private ArrayList<String> checkImageIsUploaded(ArrayList<String> imagePathList) {
        if (mPathAndUrlMap == null) mPathAndUrlMap = new ArrayMap<String, String>();
        mOrderShowAdapter.setImagePathList(mImagePathList);
        mOrderShowAdapter.notifyDataSetChanged();
        ArrayList<String> shouldUpLoadList = new ArrayList<>();
        for (String path : imagePathList) {
            String url = mPathAndUrlMap.get(path);
            if (TextUtils.isEmpty(url)) shouldUpLoadList.add(path);
        }
        return shouldUpLoadList;
    }

    private void upLoadImageToYun(ArrayList<String> pathList) {
        UpLoadImageValueBean build = new UpLoadImageValueBean.Builder()
                .setImagePathList(pathList)
                .setIndex(0)
                .setType(2)
                .build();
        UpLoadImageUtils.getInstance().zoomImageAndUpLoadToYun(build, mUpLaodUtilsListener);
    }

    private void upLoadShowToServer() {
        // TODO: 2017/7/4 把晒单内容上传到服务器中去
        CsItem.AddImageReviewRequest.Builder builder = CsItem.AddImageReviewRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setItemid(mItemID);
        builder.setReviewContent(mCcommand);
        CsBase.ItemImage.Builder imageBuilder = CsBase.ItemImage.newBuilder();
        for (String path : mImagePathList) {
            String url = mPathAndUrlMap.get(path);
            imageBuilder.setImageUrl(url);
            builder.addImages(imageBuilder);
        }

        NetEngine.postRequest(builder, new INetEngineListener<CsItem.AddImageReviewResponse>() {

            @Override
            public void onSuccess(CsItem.AddImageReviewResponse response) {
                LogUtils.e("我是上传晒单成功了！");
                EventBus.getDefault().post(new BusEvent(BusEvent.REVIEW_IMG_SUCESS, mItemID));
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDiaLog(SweetAlertDialog.SUCCESS_TYPE, "您的晒单已提交成功");
                        dissMissProgressDiaLogAndFinish(600);
                        //finish();
                    }
                });*/
                if (mIsShowingUpLoad) getSweetAlertDialog().dismiss();
                finish();
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                showCustomerToastSafly(ret + errMsg);
                LogUtils.e("我是上传晒单失败了！");
            }
        });

    }

    private boolean checkData() {
        boolean isReady = false;
        for (String path : mImagePathList) {
            String url = mPathAndUrlMap.get(path);
            isReady = !TextUtils.isEmpty(url);
            if (!isReady) {
                //showCustomerToastSafly("图片上传不完整");
                return isReady;
            }
        }

        mCcommand = mEd_show_context.getText().toString().trim();
        isReady = !TextUtils.isEmpty(mCcommand);
        if (!isReady) {
            showCustomerToastSafly(getString(R.string.show_context_tips_hint));
            return isReady;
        }

        return isReady;
    }

    private void putTheUrlToMap(String url) {
        String[] urlSplit = url.split(",");
        if (urlSplit.length >= 2) {
            String netUrl = urlSplit[0];
            String path = urlSplit[1];
            mPathAndUrlMap.put(path, netUrl);
        }
    }

    @Override
    public void finish() {
        super.finish();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mPathAndUrlMap != null && mPathAndUrlMap.size() > 0) {
                    Set<String> strings = mPathAndUrlMap.keySet();
                    for (String path : strings) {
                        if (path.lastIndexOf("|") != -1) {
                            File file = new File(path);
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    }
                }
            }
        }).start();

    }
}
