package com.fuexpress.kr.ui.view.imageselector;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.view.imageselector.imsutils.ImUtils;
import com.fuexpress.kr.utils.CropPhotoListActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * @创建者 ${user}
 * @创时间 2016/7/8
 * @描述
 * @版本 $Rev:
 * @更新者 $Author:
 * @更新时间 $Date:
 * @更新描述 TODO
 */

public class ImageSelectorActivity extends FragmentActivity implements ImageSelectorFragment.Callback {


    public static final String EXTRA_RESULT = "select_result";

    private ArrayList<String> pathList = new ArrayList<>();

    private ImageConfig imageConfig;

    private TextView title_text;
    private TextView submitButton;
    private RelativeLayout imageselector_title_bar_layout;
    private TextView mRedPointTv;
    private ListView mLv_in_image_selector_activity;
    private ArrayList<String> mOldPathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageselector_activity);

        imageConfig = ImageSelector.getImageConfig();

        ImUtils.hideTitleBar(this, R.id.imageselector_activity_layout, imageConfig.getSteepToolBarColor());
        //final ImageSelectorFragment imageSelectorFragment = new ImageSelectorFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, ImageSelectorFragment.class.getName(), null), "1")
                .commit();

        submitButton = (TextView) super.findViewById(R.id.title_right);
        title_text = (TextView) super.findViewById(R.id.title_text);
        mRedPointTv = (TextView) super.findViewById(R.id.tv_red_point_in_title_bar);
        mLv_in_image_selector_activity = (ListView) super.findViewById(R.id.lv_in_image_selector_activity);
        title_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //imageSelectorFragment.showThePopUpWindow();
                ImageSelectorFragment fragmentByTag = (ImageSelectorFragment) getSupportFragmentManager().findFragmentByTag("1");
                fragmentByTag.showThePopUpWindow();

            }
        });
        imageselector_title_bar_layout = (RelativeLayout) super.findViewById(R.id.imageselector_title_bar_layout);

        init();

    }

    private void init() {

        submitButton.setTextColor(imageConfig.getTitleSubmitTextColor());
        title_text.setTextColor(imageConfig.getTitleTextColor());
        imageselector_title_bar_layout.setBackgroundColor(imageConfig.getTitleBgColor());

        pathList = imageConfig.getPathList();
        mOldPathList = new ArrayList<>();
        mOldPathList.addAll(imageConfig.getPathList());


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setResult(RESULT_CANCELED);
                Intent data = new Intent();
                data.putStringArrayListExtra(EXTRA_RESULT, ImageSelectorActivity.this.mOldPathList);
                setResult(RESULT_CANCELED, data);
                exit();
            }
        });


        if (this.pathList == null || this.pathList.size() <= 0) {
            submitButton.setText(R.string.finish);
            mRedPointTv.setVisibility(View.GONE);
            submitButton.setEnabled(false);
        } else {
            //submitButton.setText((getResources().getText(R.string.finish)) + "(" + pathList.size() + "/" + imageConfig.getMaxSize() + ")");
            mRedPointTv.setVisibility(View.VISIBLE);
            mRedPointTv.setText(this.pathList.size() + "");
            submitButton.setEnabled(true);
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageConfig.isJumpToCrop()) {
                    ArrayList<String> cropPathList = new ArrayList<String>();
                    if (mOldPathList.size() > 0) {
                        for (int i = mOldPathList.size(); i < pathList.size(); i++) {
                            cropPathList.add(pathList.get(i));
                        }
                    } else
                        cropPathList = pathList;

                    if (cropPathList != null && cropPathList.size() > 0) {
                        Intent data = new Intent(ImageSelectorActivity.this, CropPhotoListActivity.class);
                        data.putStringArrayListExtra(CropPhotoListActivity.PAGE_IMAGES, cropPathList);
                        startActivityForResult(data, CropPhotoListActivity.REQUEST_CODE);
                    }
                } else {
                    if (ImageSelectorActivity.this.pathList != null && ImageSelectorActivity.this.pathList.size() > 0) {
                        Intent data = new Intent();
                        data.putStringArrayListExtra(EXTRA_RESULT, ImageSelectorActivity.this.pathList);
                        setResult(RESULT_OK, data);
                        exit();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == ImageSelector.IMAGE_CROP_CODE && resultCode == RESULT_OK) {
            /*Intent intent = new Intent();
            if (pathList.size() < imageConfig.getMaxSize()) {
                pathList.add(cropImagePath);
            }
            intent.putStringArrayListExtra(EXTRA_RESULT, pathList);
            setResult(RESULT_OK, intent);
            exit();*/
                insertImageToAlbumMethod(new File(cropImagePath));
                if (imageConfig.getIsAddDesc()) {

                } else {
                    Intent intent = new Intent();
                    if (pathList.size() < imageConfig.getMaxSize()) {
                        pathList.add(cropImagePath);
                    }
                    intent.putStringArrayListExtra(EXTRA_RESULT, pathList);
                    setResult(RESULT_OK, intent);
                    exit();
                }
            }
            if (requestCode == CropPhotoListActivity.REQUEST_CODE) {//多图裁剪回来完毕
                ArrayList<String> cropPathList = new ArrayList<>();
                if (mOldPathList.size() > 0) {
                    cropPathList.addAll(mOldPathList);
                }
                cropPathList.addAll(data.getStringArrayListExtra(EXTRA_RESULT));
                Intent returnData = new Intent();
                returnData.putStringArrayListExtra(EXTRA_RESULT, cropPathList);
                setResult(RESULT_OK, returnData);
                exit();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void exit() {
        finish();
    }

    private String cropImagePath;

    private void crop(String imagePath, int aspectX, int aspectY, int outputX, int outputY) {
        File file;
        if (ImUtils.existSDCard()) {
            file = new File(Environment.getExternalStorageDirectory() + imageConfig.getFilePath(), ImUtils.getImageName());
        } else {
            file = new File(getCacheDir(), ImUtils.getImageName());
        }


        cropImagePath = file.getAbsolutePath();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(imagePath)), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, ImageSelector.IMAGE_CROP_CODE);
    }

    @Override
    public void onSingleImageSelected(String path) {
        if (imageConfig.isCrop()) {
            crop(path, imageConfig.getAspectX(), imageConfig.getAspectY(), imageConfig.getOutputX(), imageConfig.getOutputY());
        } else {
            Intent data = new Intent();
            pathList.add(path);
            data.putStringArrayListExtra(EXTRA_RESULT, pathList);
            setResult(RESULT_OK, data);
            exit();
        }
    }

    @Override
    public void onImageSelected(String path) {
        if (!pathList.contains(path)) {
            pathList.add(path);
        }
        if (pathList.size() > 0) {
            //submitButton.setText((getResources().getText(R.string.finish)) + "(" + pathList.size() + "/" + imageConfig.getMaxSize() + ")");
            mRedPointTv.setVisibility(View.VISIBLE);
            mRedPointTv.setText(pathList.size() + "");
            //submitButton.setText(getResources().getText(R.string.finish));
            if (!submitButton.isEnabled()) {
                submitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if (pathList.contains(path)) {
            pathList.remove(path);
            //submitButton.setText((getResources().getText(R.string.finish)) + "(" + pathList.size() + "/" + imageConfig.getMaxSize() + ")");
            mRedPointTv.setVisibility(View.VISIBLE);
            mRedPointTv.setText(pathList.size() + "");
            //submitButton.setText(getResources().getText(R.string.finish));
        } else {
            //submitButton.setText((getResources().getText(R.string.finish)) + "(" + pathList.size() + "/" + imageConfig.getMaxSize() + ")");
            mRedPointTv.setVisibility(View.VISIBLE);
            mRedPointTv.setText(pathList.size() + "");
            //submitButton.setText(getResources().getText(R.string.finish));
        }
        if (pathList.size() == 0) {
            mRedPointTv.setVisibility(View.GONE);
            submitButton.setText(R.string.finish);
            submitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        /*if (imageFile != null) {
            Intent data = new Intent();
            if (pathList.size() < imageConfig.getMaxSize()) {
                pathList.add(imageFile.getAbsolutePath());
            }
            data.putStringArrayListExtra(EXTRA_RESULT, pathList);
            setResult(RESULT_OK, data);
            exit();
        }*/
        if (imageFile != null) {
            if (imageConfig.isCrop()) {
                crop(imageFile.getAbsolutePath(), imageConfig.getAspectX(), imageConfig.getAspectY(), imageConfig.getOutputX(), imageConfig.getOutputY());
            } else {
                /*Intent data = new Intent();
                if (pathList.size() < imageConfig.getMaxSize()) {
                    pathList.add(imageFile.getAbsolutePath());
                }
                data.putStringArrayListExtra(EXTRA_RESULT, pathList);
                setResult(RESULT_OK, data);
                exit();*/
                insertImageToAlbumMethod(imageFile);

            }
        }

    }

    public void insertImageToAlbumMethod(File imageFile) {
        /*String name = imageFile.getName();
        String[] split = name.split("\\.");
        String newName = split[0] + AccountManager.getInstance().mUin + ".jpg";*/
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getPath(), imageFile.getName(), "image from camera");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //LogUtils.e(imageFile.getName());
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(imageFile.getPath()));
        intent.setData(uri);
        this.sendBroadcast(intent);
        setTitleText(getString(R.string.all_folder));
    }

    public void setTitleText(String titleText) {
        title_text.setText(titleText);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent data = new Intent();
            data.putStringArrayListExtra(EXTRA_RESULT, ImageSelectorActivity.this.mOldPathList);
            setResult(RESULT_CANCELED, data);
            exit();

        }

        return false;

    }
}
