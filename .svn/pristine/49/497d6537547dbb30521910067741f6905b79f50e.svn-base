package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.BitMapUtils;
import com.fuexpress.kr.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 *
 */
public class Down2UpDialogActivity extends BaseActivity {


    public static String ADD_MERCHANT_ITEM_TYPE = "add_item_type";
    public static String CHANGE_MERCHANT_PHOTO_TYPE = "change_merchant_photo_type";
    public static String CHOOSE_ID_CARD_TYPE = "choose id card";
    public static String CHANGE_HEAD_ICON_TYPE = "change_head_icon_type";
    public static String GO_THIS_ACTIVITY_INTENT_KEY = "key";
    int aspectX = 0;
    int aspectY = 0;
    int outputX = 0;
    int outputY = 0;
    private String mType;
    private Uri mUritempFile;
    private ArrayList<String> mPathList;

    @Override
    public View setInitView() {
        Intent intent = getIntent();
        mType = intent.getStringExtra(GO_THIS_ACTIVITY_INTENT_KEY);

        View view = View.inflate(this, R.layout.activity_down2_up_dialog, null);
        setLocalInBottom(view);
        TextView tv_in_down_to_up_dialog_take_photo = (TextView) view.findViewById(R.id.tv_in_down_to_up_dialog_take_photo);
        tv_in_down_to_up_dialog_take_photo.setOnClickListener(this);
        TextView tv_in_down_to_up_dialog_take_choose_in_album = (TextView) view.findViewById(R.id.tv_in_down_to_up_dialog_take_choose_in_album);
        TextView tv_in_down_to_up_dialog_cancle = (TextView) view.findViewById(R.id.tv_in_down_to_up_dialog_cancle);
        tv_in_down_to_up_dialog_cancle.setOnClickListener(this);
        TextView tv_in_down_to_up_dialog_take_choose_in_location_album = (TextView) view.findViewById(R.id.tv_in_down_to_up_dialog_take_choose_in_location_album);
        tv_in_down_to_up_dialog_take_choose_in_location_album.setOnClickListener(this);
        if (!CHANGE_MERCHANT_PHOTO_TYPE.equals(mType)) {
            if (ADD_MERCHANT_ITEM_TYPE.equals(mType) || CHOOSE_ID_CARD_TYPE.equals(mType)) {
                aspectX = SysApplication.mWidthPixels;
                aspectY = SysApplication.mWidthPixels;
                outputX = SysApplication.mWidthPixels;
                outputY = outputX;
            } else {
                aspectX = 1;
                aspectY = 1;
                outputX = 140;
                outputY = 140;
            }
            tv_in_down_to_up_dialog_take_choose_in_location_album.setVisibility(View.VISIBLE);
            tv_in_down_to_up_dialog_take_choose_in_album.setVisibility(View.GONE);
        } else {
            aspectX = SysApplication.mWidthPixels;
            aspectY = (int) (SysApplication.mWidthPixels / 1.5);
            outputX = SysApplication.mWidthPixels;
            outputY = (int) (SysApplication.mWidthPixels / 1.5);
            tv_in_down_to_up_dialog_take_choose_in_album.setOnClickListener(this);
            tv_in_down_to_up_dialog_take_choose_in_album.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public void setLocalInBottom(View view) {
        Window window = this.getWindow();//宽度为屏幕宽,位置为底部
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        view.setMinimumWidth(mWidthPixels);
        window.setAttributes(lp);
        LinearLayout ll_01_in_down_to_up_dialog = (LinearLayout) view.findViewById(R.id.ll_01_in_down_to_up_dialog);
        ll_01_in_down_to_up_dialog.setAlpha(0.9f);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_in_down_to_up_dialog_take_photo:
                mPathList = new ArrayList<>();
                if (CHANGE_HEAD_ICON_TYPE.equals(mType)) {
                    UIUtils.startImageSelectorForHeadIcon(1000, mPathList, this);
                } else {
                    UIUtils.startImageSelectorForChangeMerChantCover(1000, mPathList, this);
                    //finish();
                }
                //finish();
                break;
            case R.id.tv_in_down_to_up_dialog_take_choose_in_album:
                //Toast.makeText(this, "去图片列表中选择", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.tv_in_down_to_up_dialog_cancle:
                finish();
                break;
            case R.id.tv_in_down_to_up_dialog_take_choose_in_location_album:
                intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (ADD_MERCHANT_ITEM_TYPE.equals(mType) || CHOOSE_ID_CARD_TYPE.equals(mType)) {
                        zoomThisImagMethod(data.getData());
                    } else {
                        cropPhoto(data.getData());//裁剪图片
                    }
                    //cropPhoto(data.getData());//裁剪图片
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory()
                            + "/head.jpg");
                    if (ADD_MERCHANT_ITEM_TYPE.equals(mType)) {
                        zoomThisImagMethod(Uri.fromFile(temp));
                    } else {
                        cropPhoto(Uri.fromFile(temp));//裁剪图片
                    }
                    //cropPhoto(Uri.fromFile(temp));//裁剪图片
                }
                break;
            case 3:
                if (data != null) {
                    Intent intent = new Intent();
                    if (CHANGE_HEAD_ICON_TYPE.equals(mType)) {
                        Bundle extras = data.getExtras();
                        Bitmap head = extras.getParcelable("data");
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("data", head);
                        intent.putExtra("photo", bundle);
                        setResult(100, intent);
                    } else {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUritempFile);
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        intent.putExtra("image", mUritempFile.toString());
                        setResult(100, intent);
                    }
                    finish();
                }
                break;
            case 1000:
                //LogUtils.e("返回成功:");
                /*for (String path : mPathList) {
                    LogUtils.e(path);
                }*/
                if (mPathList != null && mPathList.size() > 0) {
                    Intent intent = new Intent();
                    if (CHANGE_HEAD_ICON_TYPE.equals(mType)) {

                        Bitmap head = BitmapFactory.decodeFile(mPathList.get(0));
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("data", head);
                        intent.putExtra("photo", bundle);
                        setResult(100, intent);
                    } else {
                        intent.putExtra("image", mPathList.get(0));
                        setResult(100, intent);
                    }
                }
                finish();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void zoomThisImagMethod(Uri temp) {
        //String path = temp.getPath();
        Bitmap bitmap = BitMapUtils.zoomImg(temp, this);
        BitMapUtils.setPicToView(bitmap, "addItemPhoto" + AccountManager.getInstance().mUin + System.currentTimeMillis());
    }

    /**
     * 调用系统的裁剪
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("noFaceDetection", true);
        if (CHANGE_HEAD_ICON_TYPE.equals(mType)) {
            intent.putExtra("return-data", true);
        } else {
            mUritempFile = Uri.parse("file://" + "/" + FileUtils.getCacheDir() + "/" + System.currentTimeMillis() + "image.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUritempFile);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        }
        startActivityForResult(intent, 3);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.IMAGE_FILE_READY:
                File file = (File) event.getParam();
                Uri uri = Uri.fromFile(file);
                Intent intent = new Intent();
                intent.putExtra("image", uri.toString());
                setResult(100, intent);
                finish();
                break;
        }
    }
}
