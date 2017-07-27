package com.fuexpress.kr.model;


import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.utils.BitMapUtils;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.TimeUtils;
import com.fuexpress.kr.utils.UpLoadIamgeUtils;
import com.upyun.library.common.UploadManager;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fksproto.CsBase;


/**
 * Created by Administrator on 2016-9-5.
 */
public class ItemUploadManager {
    //    上傳狀態
    static boolean state = true;
    private final String tag;

    public interface UpLoadeCompleteListener {
        public void complete(float prgress, ArrayMap map);

        public void error(List<String> urls);
    }

    private static List<String> mUploding = new ArrayList<>();
    private static ArrayMap<String, String> mComplete = new ArrayMap<>();
    private static ArrayMap<String, String> mUncomplete = new ArrayMap<>();
    private static ArrayMap<String, Float> mFloats = new ArrayMap<>();
    //    private static ItemUploadManager mInstance = new ItemUploadManager();
    private float mProgress;

    public String getTag() {
        return tag;
    }

    public ItemUploadManager(String tag) {
        this.tag = tag;
    }

    public void reset() {
        state = true;
        mUploding.removeAll(mUploding);
        mComplete = null;
        mComplete = new ArrayMap<>();
        mUncomplete = null;
        mUncomplete = new ArrayMap<>();
        mFloats = null;
        mFloats = new ArrayMap<>();
        mListener = null;
    }

    public void addTask(List<String> urls) {
        for (String path : urls) {
            addTask(path);
        }
    }

    public void addTask(String url) {
        if (mUploding.contains(url))
            return;
        mUploding.add(url);
        int i = mUploding.indexOf(url);
        mFloats.put(url, 0f);
        upLoaderToUpYun(url, i);
    }

    public void upLoaderToUpYun(final String fileName, final int index) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //                final File file = new File(fileName);
                final File file = new File(BitMapUtils.zipPhoto(fileName));
                long length1 = file.length();
                int blockNum = UpYunUtils.getBlockNum(file, 1024);//获得块大小
                long length = file.length();//获得文件长度(大小)
                String file_hex = UpYunUtils.md5Hex(file);//获得文件哈希值
                long expiration = TimeUtils.getTodayTimeLongValue() + 518400;//获得超时时间(一个月)
                Map<String, Object> paramsMap = new HashMap<>();//用来储存参数的map集合
                paramsMap.put(UpLoadIamgeUtils.Params.FILE_SIZE, length);//设置文件信息
                paramsMap.put(UpLoadIamgeUtils.Params.BLOCK_NUM, blockNum);
                paramsMap.put(UpLoadIamgeUtils.Params.FILE_MD5, file_hex);
                paramsMap.put(UpLoadIamgeUtils.Params.EXPIRATION, expiration);
                final String upLoadKey = Constants.UPLOAD_IMG_FOR_ADD_PACKAGE + SysApplication.getRandoomUUID() + ".jpg";
                paramsMap.put(UpLoadIamgeUtils.Params.PATH, upLoadKey);

                Map<String, Object> upParamsMap = new HashMap<>();
                String signature = UpYunUtils.getSignature(paramsMap, Constants.secret);
                String policy = UpYunUtils.getPolicy(paramsMap);
                String saveKey = file.getAbsolutePath();
                String absolutePath = file.getAbsolutePath();
                String[] split = absolutePath.split("\\.");
                String s = split[0];
                //final String s1 = s + System.currentTimeMillis() + AccountManager.getInstance().mUin + ".jpg";
                upParamsMap.put(UpLoadIamgeUtils.Params.SIGNATURE, signature);
                upParamsMap.put(UpLoadIamgeUtils.Params.POLICY, policy);
                upParamsMap.put(UpLoadIamgeUtils.Params.BUCKET, Constants.bucket);
                upParamsMap.put(UpLoadIamgeUtils.Params.SAVE_KEY, upLoadKey);

                UploadManager.getInstance().blockUpload(file, upParamsMap, Constants.secret, new UpCompleteListener() {
                    @Override
                    public void onComplete(boolean isSuccess, String result) {
                        if (isSuccess) {
                            String uploadImageUrl = Constants.IMAGE_URL_OFFICIAL + upLoadKey;
                            LogUtils.e("我是上传后的结果:" + uploadImageUrl);
                            if (mUploding.contains(fileName)) {
                                mComplete.put(fileName, uploadImageUrl);
                            }
                            calcProgress(fileName, 100);
                        } else {
                            state = false;
                            mUncomplete.put(fileName, "");
                            calcProgress(fileName, 100);
                        }
                    }
                }, new UpProgressListener() {
                    @Override
                    public void onRequestProgress(long bytesWrite, long contentLength) {
                        float complete = bytesWrite * 100 / contentLength;
                        Log.d(CsBase.Item.class.getSimpleName(), "contentLength :" + contentLength + ">>>bytesWrite ;" + bytesWrite + ":" + bytesWrite / contentLength);
                        Log.d(CsBase.Item.class.getSimpleName(), Thread.currentThread().getName() + index + ">>>" + "progress-> :" + complete);
                        if (complete < 100) {
                            calcProgress(fileName, complete);
                        }
                    }
                });
            }
        }).start();
    }

    private float calcProgress(String index, float complete) {
        Log.d("calcu", "calcProgress");
        if (index != null) {
            mFloats.put(index, complete);
        }
        float max = 0;

        for (String key : mUploding) {
            float f = mFloats.get(key);
            max += f;
        }
        mProgress = max / mUploding.size();
        if (mListener != null) {
            mListener.complete(mProgress, mComplete);
        }

        return mProgress;
    }

    private synchronized boolean isComplete() {
        if (mUploding.size() == mComplete.size() + mUncomplete.size())
            return true;
        else
            return false;
    }


    UpLoadeCompleteListener mListener;

    public void getAllComplete(UpLoadeCompleteListener listener) {
        if (isComplete()) {
            listener.complete(100, mComplete);
            if (!state) {
                listener.error(mUploding);
                reset();
            }
        } else {
            mListener = listener;
        }
    }
}
