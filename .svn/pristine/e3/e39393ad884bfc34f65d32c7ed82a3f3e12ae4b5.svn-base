package com.fuexpress.kr.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.ui.adapter.CropViewAdapter;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class CropPhotoListActivity extends BaseActivity {


    private View mRootView;
    private ViewPager vp_item_imgs;
    public static final String PAGE_IMAGES = "PAGE_IMAGES";
    private TitleBarView title_in_cropper;
    private int mIndex = 0;
    private int mMaxSize = 0;
    private CropViewAdapter cropViewAdapter;
    private ArrayMap<Integer, String> mImageIndexPathMap;
    private ArrayMap<Integer, Bitmap> mBitmapArrayMap;
    private TextView tv_in_title_right;
    public static int REQUEST_CODE = 31231;
    public static final String EXTRA_RESULT = "select_result";


    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_crop_photo_list, null);
        return mRootView;
    }

    CropImageView.OnCropImageCompleteListener mOnCropImageCompleteListener = new CropImageView.OnCropImageCompleteListener() {
        @Override
        public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
            Bitmap bitmap = result.getBitmap();
        }
    };

    CropImageResultListener mCropImageResultListener = new CropImageResultListener() {
        @Override
        public void onSuccessByPath(int index, String path) {
            if (mImageIndexPathMap == null) mImageIndexPathMap = new ArrayMap<>();
            mImageIndexPathMap.put(index, path);
            if (mImageIndexPathMap.keySet().size() == mMaxSize) {
                finishCrop();
            }
        }

        @Override
        public void onSuccessByBitMap(int index, Bitmap bitmap) {
            if (mBitmapArrayMap == null) mBitmapArrayMap = new ArrayMap<>();
            mBitmapArrayMap.put(index, bitmap);
        }

        @Override
        public void onFail(String errMsg) {
            showCustomerToastSafly(errMsg);
        }
    };

    private void finishCrop() {
        ArrayList<String> pathList = new ArrayList<>();
        for (int i = 0; i < mMaxSize; i++) {
            String path = mImageIndexPathMap.get(i);
            pathList.add(path);
        }
        Intent data = new Intent();
        data.putStringArrayListExtra(EXTRA_RESULT, pathList);
        setResult(REQUEST_CODE, data);
        exit(false);
    }

    private void exit(boolean isClearFile) {
        if (isClearFile && mImageIndexPathMap != null) {
            final Set<Integer> integers = mImageIndexPathMap.keySet();
            if (mImageIndexPathMap.size() > 0)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int key : integers) {
                            String path = mImageIndexPathMap.get(key);
                            if (path.lastIndexOf("|") != -1) {
                                if (null != path && TextUtils.isEmpty(path)) {
                                    File file = new File(path);
                                    if (file.exists()) file.delete();
                                }
                            }
                        }
                    }
                });

        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit(true);

    }

    @Override
    public void initView() {
        vp_item_imgs = (ViewPager) mRootView.findViewById(R.id.vp_item_imgs);
        title_in_cropper = (TitleBarView) mRootView.findViewById(R.id.title_in_cropper);
        title_in_cropper.getIv_in_title_back().setOnClickListener(this);
        TextView textView = title_in_cropper.getmTv_in_title_back_tv();
        textView.setOnClickListener(this);
        textView.setText(getString(R.string.phone_album));
        tv_in_title_right = title_in_cropper.getTv_in_title_right();
        tv_in_title_right.setOnClickListener(this);
        tv_in_title_right.setText(getString(R.string.cropper_next));
        RelativeLayout rl_cropper_rotation = (RelativeLayout) mRootView.findViewById(R.id.rl_cropper_rotation);
        rl_cropper_rotation.setOnClickListener(this);
        initData();
    }

    private void initData() {
        List<String> imgs = getIntent().getStringArrayListExtra(PAGE_IMAGES);
        mMaxSize = imgs.size();
        title_in_cropper.setTitleText(getString(R.string.cropper_title_text, String.valueOf(mIndex + 1), String.valueOf(mMaxSize)));
        //cropViewAdapter = new CropViewAdapter(this, imgs, mOnCropImageCompleteListener);
        cropViewAdapter = new CropViewAdapter(this, imgs, mCropImageResultListener);
        vp_item_imgs.setAdapter(cropViewAdapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_in_title_right:
                ++mIndex;
                if (mIndex < mMaxSize) {
                    if (mIndex == mMaxSize - 1)
                        tv_in_title_right.setText(getString(R.string.pick_up_confirm_msg));
                    vp_item_imgs.setCurrentItem(mIndex);
                    title_in_cropper.setTitleText(getString(R.string.cropper_title_text, String.valueOf(mIndex + 1), String.valueOf(mMaxSize)));
                    //cropViewAdapter.getCropPhoto(new ArrayList<Bitmap>(), mIndex - 1);
                    //cropViewAdapter.getCropPhotoFliePath(mIndex - 1);
                }
                cropViewAdapter.getCropPhotoFliePath(mIndex - 1);
                break;
            case R.id.rl_cropper_rotation:
                cropViewAdapter.rotationCropperView(mIndex);
                break;
        }
    }


}
