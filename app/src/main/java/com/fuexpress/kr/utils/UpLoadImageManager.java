package com.fuexpress.kr.utils;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.UpLoadImgBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.google.gson.Gson;
import com.upyun.library.common.UploadManager;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;

/**
 * @创建者 chenyl
 * @创时间 2016/6/20
 * @描述
 * @版本 $Rev:
 * @更新者 $Author:
 * @更新时间 $Date:
 * @更新描述 TODO
 */

public class UpLoadImageManager {

    private static UpLoadImageManager sUpLoadImageManager = new UpLoadImageManager();
    private List<String> mImageUrlList;
    private List<String> mImageFileNameUrlList;
    private List<String> mLastImageList;
    private int mSingleMaxSize;
    private SweetAlertDialog mSweetAlertDialog;
    private int mRealityProgress;
    private List<String> mManagerImageNameList;
    public int mImageIndex;
    public int mListSize;
    public boolean mIsShowProgress = false;
    private List<String> mTempList;
    private ArrayMap<String, String> mUpLoadedImageMap;
    private List<String> mRepetitionImageList;
    public boolean mIsFirstAdd = false;
    public static int HEAD_ICON_TYPE = 123;
    public static int MERCHANT_COVER_TYPE = 234;


    private UpLoadImageManager() {
        mUpLoadedImageMap = new ArrayMap<>();
    }


    public static UpLoadImageManager getInstance() {
        return sUpLoadImageManager;
    }

    /**
     * upLoad image to UpYun
     *
     * @param file     需要上传的图片文件
     * @param listener 需要进度回调的，传进度回调进来
     */
    public void upLoaderToUpYun(final File file, final UpProgressListener listener, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int blockNum = UpYunUtils.getBlockNum(file, 1024);//获得块大小
                long length = file.length();//获得文件长度(大小)
                String file_hex = UpYunUtils.md5Hex(file);//获得文件哈希值
                long expiration = TimeUtils.getTodayTimeLongValue() + 518400;//获得超时时间(一个月)
                Map<String, Object> paramsMap = new HashMap<>();//用来储存参数的map集合
                paramsMap.put(UpLoadIamgeUtils.Params.FILE_SIZE, length);//设置文件信息
                paramsMap.put(UpLoadIamgeUtils.Params.BLOCK_NUM, blockNum);
                paramsMap.put(UpLoadIamgeUtils.Params.FILE_MD5, file_hex);
                paramsMap.put(UpLoadIamgeUtils.Params.EXPIRATION, expiration);
                final String merchant_cover_String = Constants.UPLOAD_IMG_FOR_MERCHANT_COVER + AccountManager.getInstance().mUin + "/" + SysApplication.getRandoomUUID() + ".jpg";
                final String head_up_load_string = Constants.UPLOAD_IMG_FOR_MERCHANT_USERINFO + AccountManager.getInstance().mUin + "_" + SysApplication.getRandoomUUID() + ".jpg";
                if (HEAD_ICON_TYPE == type) {
                    paramsMap.put(UpLoadIamgeUtils.Params.PATH, head_up_load_string);
                } else if (MERCHANT_COVER_TYPE == type) {
                    paramsMap.put(UpLoadIamgeUtils.Params.PATH, merchant_cover_String);
                }

                Map<String, Object> upParamsMap = new HashMap<>();
                String signature = UpYunUtils.getSignature(paramsMap, Constants.secret);
                String policy = UpYunUtils.getPolicy(paramsMap);
                String saveKey = file.getAbsolutePath();
                String absolutePath = file.getAbsolutePath();
                String[] split = absolutePath.split("\\.");
                String s = split[0];

                if (HEAD_ICON_TYPE == type) {
                    upParamsMap.put(UpLoadIamgeUtils.Params.SAVE_KEY, head_up_load_string);
                } else if (MERCHANT_COVER_TYPE == type) {
                    upParamsMap.put(UpLoadIamgeUtils.Params.SAVE_KEY, merchant_cover_String);
                }
                //s1 = s + System.currentTimeMillis() + AccountManager.getInstance().mUin + ".jpg";
                upParamsMap.put(UpLoadIamgeUtils.Params.SIGNATURE, signature);
                upParamsMap.put(UpLoadIamgeUtils.Params.POLICY, policy);
                upParamsMap.put(UpLoadIamgeUtils.Params.BUCKET, Constants.bucket);
                //upParamsMap.put(UpLoadIamgeUtils.Params.SAVE_KEY, s1);

                UploadManager.getInstance().blockUpload(file, upParamsMap, Constants.secret, new UpCompleteListener() {
                    @Override
                    public void onComplete(boolean isSuccess, String result) {
                        //LogUtils.e("我是上传后的结果:" + isSuccess + "  " + result);
                        if (isSuccess) {
                            //String uploadImageUrl = Constants.IMAGE_URL_OFFICIAL + s1;
                            LogUtils.e("这是返回的结果：" + result.toString());
                            String uploadImageUrl = "";
                            if (HEAD_ICON_TYPE == type) {
                                uploadImageUrl = Constants.IMAGE_URL_OFFICIAL + head_up_load_string;
                            } else if (MERCHANT_COVER_TYPE == type) {
                                uploadImageUrl = Constants.IMAGE_URL_OFFICIAL + merchant_cover_String;
                            }
                            EventBus.getDefault().post(new BusEvent(BusEvent.UP_LOAD_IMAGE_COMPLETE, true, uploadImageUrl));
                            LogUtils.e("我是上传后的结果:" + uploadImageUrl);
                            file.delete();
                        } else {
                            EventBus.getDefault().post(new BusEvent(BusEvent.UP_LOAD_IMAGE_COMPLETE, false));
                        }
                    }
                }, listener == null ? null : listener);
            }
        }).start();
    }

    public synchronized void upLoaderToUpYunByList(final List<File> files, final Context context) {
        upLoaderToUpYunByList(files, null, context);

    }


    public synchronized void upLoaderToUpYunByList(final List<File> files, final List<String> fileNames, final Context context) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                mListSize = files.size();
                mImageUrlList = new ArrayList<String>();
                mImageFileNameUrlList = new ArrayList<String>();
                final ArrayList<String> tempFileNameList = new ArrayList<String>();
                for (String theFileName : fileNames) {
                    tempFileNameList.add(theFileName);
                }
                //mLastImageList = new ArrayList<String>();
                for (int x = 0; x < files.size(); x++) {
                    final File file = files.get(x);
                    int blockNum = UpYunUtils.getBlockNum(file, 1024);//获得块大小
                    long length = file.length();//获得文件长度(大小)
                    String file_hex = UpYunUtils.md5Hex(file);//获得文件哈希值
                    long expiration = TimeUtils.getTodayTimeLongValue() + 518400;//获得超时时间(一个月)
                    Map<String, Object> paramsMap = new HashMap<>();//用来储存参数的map集合
                    paramsMap.put(UpLoadIamgeUtils.Params.FILE_SIZE, length);//设置文件信息
                    paramsMap.put(UpLoadIamgeUtils.Params.BLOCK_NUM, blockNum);
                    paramsMap.put(UpLoadIamgeUtils.Params.FILE_MD5, file_hex);
                    paramsMap.put(UpLoadIamgeUtils.Params.EXPIRATION, expiration);
                    paramsMap.put(UpLoadIamgeUtils.Params.PATH, Constants.UPLOAD_IMG_FOR_ADD_ITEM + SysApplication.getRandoomUUID() + ".jpg");

                    Map<String, Object> upParamsMap = new HashMap<>();
                    String signature = UpYunUtils.getSignature(paramsMap, Constants.secret);
                    String policy = UpYunUtils.getPolicy(paramsMap);
                    String saveKey = file.getAbsolutePath();
                    final String absolutePath = file.getAbsolutePath();
                    String[] split = absolutePath.split("\\.");
                    String s = split[0];
                    final String name = file.getName();
                    //final String s1 = "/" + UpYunUtils.md5Hex(file) + System.currentTimeMillis() + AccountManager.getInstance().mUin + ".jpg";
                    final String s1 = Constants.UPLOAD_IMG_FOR_ADD_ITEM + SysApplication.getRandoomUUID() + ".jpg";
                    upParamsMap.put(UpLoadIamgeUtils.Params.SIGNATURE, signature);
                    upParamsMap.put(UpLoadIamgeUtils.Params.POLICY, policy);
                    upParamsMap.put(UpLoadIamgeUtils.Params.BUCKET, Constants.bucket);
                    upParamsMap.put(UpLoadIamgeUtils.Params.SAVE_KEY, s1);

                    final int finalX = x;
                    mImageIndex = finalX;

                    //mListSize = files.size();
                    UploadManager.getInstance().blockUpload(file, upParamsMap, Constants.secret, new UpCompleteListener() {
                        @Override
                        public void onComplete(boolean isSuccess, String result) {
                            LogUtils.e("我是上传后的结果:" + isSuccess + "  " + result);

                            if (isSuccess) {
                                Gson gson = new Gson();
                                UpLoadImgBean upLoadImgBean = gson.fromJson(result, UpLoadImgBean.class);
                                //LogUtils.e(upLoadImgBean.getPath());
                                //String uploadImageUrl = Constants.IMAGE_URL_OFFICIAL + s1;
                                String uploadImageUrl = upLoadImgBean.getPath();
                                mImageUrlList.add(uploadImageUrl);
                                if (tempFileNameList != null) {
                                    /*for (String fileName : fileNames) {
                                        int i = fileName.lastIndexOf("/");
                                        int i1 = fileName.lastIndexOf(".");
                                        String substring = fileName.substring(i + 1, i1);
                                        //String[] split1 = name.split("\\.");
                                        int i2 = name.lastIndexOf(".");
                                        String substring1 = name.substring(0, i2);
                                        if (substring.equals(substring1)) {
                                            mImageFileNameUrlList.add(uploadImageUrl + "," + fileName);
                                            mUpLoadedImageMap.put(fileName, uploadImageUrl + "," + fileName);
                                            break;
                                        }
                                        //LogUtils.e(substring);
                                    }*/
                                    mImageFileNameUrlList.add(uploadImageUrl + "," + tempFileNameList.get(finalX));
                                    /*String absolutePath1 = file.getPath();
                                    LogUtils.e("这是路径哦：" + absolutePath1);
                                    mImageFileNameUrlList.add(uploadImageUrl + "," + file.getPath());*/
                                    //mLastImageList.add(fileNames.get(finalX));
                                }
                                if (mImageUrlList.size() == files.size()) {
                                    EventBus.getDefault().post(new BusEvent(BusEvent.UP_LOAD_IMAGE_COMPLETE, true, mImageUrlList));
                                    if (tempFileNameList != null) {
                                        /*if (mRepetitionImageList != null && mRepetitionImageList.size() > 0) {
                                            mImageFileNameUrlList.addAll(mRepetitionImageList);
                                        }*/
                                        EventBus.getDefault().post(new BusEvent(BusEvent.UP_LOAD_IMAGE_COMPLETE2, true, mImageFileNameUrlList));
                                    }
                                }
                                LogUtils.e("我是上传后的结果:" + uploadImageUrl);
                                //                                file.delete();
                            } else {
                                EventBus.getDefault().post(new BusEvent(BusEvent.UP_LOAD_IMAGE_COMPLETE2, false, finalX));
                                LogUtils.e("上传图片失败了,这是第" + finalX + "张");
                            }
                        }
                    }, context == null ? null : new UpProgressListener() {
                        @Override
                        public void onRequestProgress(long bytesWrite, long contentLength) {
                            if (!mIsShowProgress) {
                                setRealityProgress(bytesWrite, contentLength, mListSize, finalX);
                            } else {
                                setProgress(bytesWrite, contentLength, mListSize, mContext, finalX);
                            }
                        }
                    });
                }
            }
        }).start();
    }


    private synchronized void setProgress(long bytesWrite, long contentLength, int listSize, Context context, int index) {
        /*if (mSweetAlertDialog == null) {
            mSweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            mSweetAlertDialog.getProgressHelper().setBarColor(Color.WHITE);
            mSweetAlertDialog.setCancelable(false);
        }*/

        mSingleMaxSize = 100 / listSize;
        /*if (0 == index) {
            mRealityProgress = 0;
        }*/
        if (bytesWrite == 0) {
            int pros = (int) ((mSingleMaxSize * 2) / 2);
            mRealityProgress += pros;
        }
        //mSweetAlertDialog.getProgressHelper().setProgress(pros);
        if (listSize - 1 == index) {
            if (mRealityProgress < 100) {
                mRealityProgress = 100;
            } else if (mRealityProgress > 100) {
                mRealityProgress = 100;
            }
        }
        /*mSweetAlertDialog.setTitleText("正在上传:" + mRealityProgress + "%");
        mSweetAlertDialog.show();*/
        mContext.showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, "正在上传:" + mRealityProgress + "%");
        if (listSize - 1 == index) {
            //mRealityProgress = 0;
            /*SysApplication.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSweetAlertDialog.dismiss();
                }
            }, 1000);*/
        }
    }

    ArrayList<String> theWantUpLoadImagList;

    public void zoomImageAndUpLoad(List<String> imagePathList, Context context) {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        mTempList = new ArrayList<>();
        mRepetitionImageList = new ArrayList<>();
        theWantUpLoadImagList = new ArrayList<>();
        theWantUpLoadImagList.addAll(imagePathList);
        mTempList.addAll(imagePathList);
        if (mLastImageList != null && mLastImageList.size() != 0) {
            for (String lastImage : mLastImageList) {
                for (String imageNew : imagePathList) {
                    if (imageNew.equals(lastImage)) {
                        mTempList.remove(imageNew);
                        mRepetitionImageList.add(mUpLoadedImageMap.get(imageNew));
                        break;
                    }
                }
            }
        } else {
            mLastImageList = new ArrayList<>();
            mLastImageList.addAll(imagePathList);
            mIsFirstAdd = true;
        }

        if (mTempList.size() == 0) {//说明进来的都是已经上传过了的:
            /*EventBus.getDefault().post(new BusEvent(BusEvent.UP_LOAD_IMAGE_COMPLETE2, true, mRepetitionImageList));
            mRealityProgress = 100;*/
            mManagerImageNameList = imagePathList;
            mFileList = new ArrayList<>();
            mContext = (BaseActivity) context;
            BitMapUtils.zoomImg(imagePathList);
        } else {
            mManagerImageNameList = imagePathList;
            if (mIsFirstAdd) {
                mIsFirstAdd = false;
            } else {
                mLastImageList.addAll(mTempList);
            }
            mFileList = new ArrayList<>();
            mContext = (BaseActivity) context;
            BitMapUtils.zoomImg(imagePathList);
        }
    }

    private List<File> mFileList;
    BaseActivity mContext;

    ArrayMap<String, File> stringFileArrayMap = new ArrayMap<>();

    public void onEventMainThread(BusEvent event) {
        switch (event.getType()) {
            case BusEvent.IMAGE_FILE_READY:
                if (mFileList.size() < mManagerImageNameList.size()) {
                    stringFileArrayMap.put(event.getStrParam(), (File) event.getParam());
                    mFileList.add((File) event.getParam());
                    if (mFileList.size() == mManagerImageNameList.size()) {
                        mFileList.clear();
                        for (String pathKey : mManagerImageNameList) {
                            mFileList.add(stringFileArrayMap.get(pathKey));
                        }
                        if (mContext != null) {
                            upLoaderToUpYunByList(mFileList, mManagerImageNameList, mContext);
                        } else {
                            upLoaderToUpYunByList(mFileList, mManagerImageNameList, null);
                        }
                    }
                }
                break;
            case BusEvent.GET_UP_LOAD_IMAGE_LIST_PROGRESS:
                EventBus.getDefault().post(new BusEvent(BusEvent.RETURN_CURRENT_UP_LOAD_IMAGE_LIST_PROGRESS, mRealityProgress));
                break;
            case BusEvent.SHOW_UP_LOAD_IMAGE_LIST_PROGRESS:
                mIsShowProgress = true;
                break;
            case BusEvent.UP_LOAD_IMAGE_COMPLETE2:
                if (!event.getBooleanParam()) {
                    if (mIsShowProgress && mSweetAlertDialog != null) {
                        mSweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        mSweetAlertDialog.setTitleText("第" + event.getIntParam() + "张上传失败！");
                        SysApplication.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSweetAlertDialog.dismiss();
                            }
                        }, 1000);
                    }
                } else {
                    //mRealityProgress = 0;
                }
                break;
        }
    }


    public void setRealityProgress(long bytesWrite, long contentLength, int listSize, int index) {
        mSingleMaxSize = 100 / listSize;
        if (bytesWrite == 0) {
            int pros = (int) ((mSingleMaxSize * 2) / 2);
            mRealityProgress += pros;
        }
        if (listSize - 1 == index) {
            if (mRealityProgress < 100) {
                mRealityProgress = 100;
            } else if (mRealityProgress > 100) {
                mRealityProgress = 100;
            }
        }
    }


    public void setUpLoadProgressEmpty() {
        mIsShowProgress = false;
        mRealityProgress = 0;
    }


}
