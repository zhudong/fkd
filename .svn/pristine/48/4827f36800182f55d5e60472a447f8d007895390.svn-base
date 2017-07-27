package com.fuexpress.kr.model;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.DataCleanManager;
import com.socks.library.KLog;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsBase;
import fksproto.CsItem;

/**
 * Created by k550 on 2016/6/24.
 * 推广分享模块——分享到微信朋友圈
 * 1,下载之本地
 * 2，分享
 */
public class ShareManager {
    public static final String WEIXIN_PACKAGE = "com.tencent.mm";
    public static final String WEIXIN_METHOD = "com.tencent.mm.ui.tools.ShareToTimeLineUI";
    public static DownloadManager downManager;
    public static List<Long> mIds; //下载队列任务ID
    public static List<String> urls;//图片集合
    private static DownLoadCompleteReceiver receiver;
    public static String desc = "";
    public static boolean shareNow = false;
    public static boolean downloadComplete = false;
    static String tag;

    public static void init(final long id, Activity current) {
        if (current != null) {
            tag = current.hashCode() + "";
        } else {
            tag = "";
        }

        CsItem.GetItemImageListRequest.Builder builder = CsItem.GetItemImageListRequest.newBuilder();
        if (AccountManager.getInstance().mBaseUserRequest != null) {
            builder.setUserinfo(AccountManager.getInstance().mBaseUserRequest);
        }
        builder.setMatchItemId((int) id);
        NetEngine.postRequest(builder, new INetEngineListener<CsItem.GetItemImageListResponse>() {
            @Override
            public void onSuccess(final CsItem.GetItemImageListResponse response) {
                KLog.i(response.toString());

                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        String des = response.getCaption();
//                        KLog.i("name   " + response.getCatecory1Name());
//                        KLog.i("2name  " + response.getCatecory2Name());
                        urls = new ArrayList<>();

                        //    urls.add(response.getImageUrl());
//                        String strUrl = response.getImageUrl();
                        for (int i = 0; i < response.getItemImageUrlListCount(); i++) {
                            urls.add(response.getItemImageUrlListList().get(i).getImageUrl());

                        }

//                        for (CsItem.merchantImageList image : response.getMerchantimagelistList()) {
//                            String str = image.getImagelist();
//
//                            urls.add(str);
//                        }
                        KLog.i("列表 size = " + response.getItemImageUrlListCount());
                        EventBus.getDefault().post(new BusEvent(BusEvent.SEND_DOWNLOAD_IMAGES_PROGRESS, 0, tag));
                        downloadImages(des);
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    public static void initWithRes(List<CsBase.ItemImage> imgs, String des, Activity current) {
        if (current != null) {
            tag = current.hashCode() + "";
        } else {
            tag = "";
        }
        urls = new ArrayList<>();
        for (CsBase.ItemImage itemImage : imgs) {
            urls.add(itemImage.getImageUrl());
        }
        EventBus.getDefault().post(new BusEvent(BusEvent.SEND_DOWNLOAD_IMAGES_PROGRESS, 0, tag));
        downloadImages(des);
    }


    private static void downloadImages(String decc) {
        EventBus.getDefault().post(new BusEvent(BusEvent.SEND_DOWNLOAD_IMAGES_PROGRESS, urls.size() - urls.size(), tag));
        desc = decc;
        downManager = (DownloadManager) UIUtils.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        receiver = new DownLoadCompleteReceiver();
        UIUtils.getContext().registerReceiver(receiver, filter);
        //清除Download文件夹下文件
        DataCleanManager.deleteFilesByDirectory(UIUtils.getContext().getExternalFilesDir("Download"));
        for (String str : urls) {
            downImages(str);
        }
    }

    //下载图片到手机
    private static void downImages(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("下载中");
        //   request.setDescription(getString(R.string.download_now));
        request.setDestinationInExternalFilesDir(UIUtils.getContext(), Environment.DIRECTORY_DOWNLOADS, getFileName(url));
        request.allowScanningByMediaScanner();
        long downloadTaskId = downManager.enqueue(request);
        if (mIds == null) {
            mIds = new ArrayList<>();
        }
        mIds.add(downloadTaskId);
        KLog.i("downloadId = " + downloadTaskId);
    }

    //从图片地址中截取图片名称
    private static String getFileName(String str) {
        return str.substring(str.lastIndexOf("/") + 1);
    }

    /*********************
     * 接受下载完毕的广播
     ************************/
    private static class DownLoadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (mIds.contains(id)) {
                    mIds.remove(id);
                    if (mIds.size() == 0) {
                        KLog.i("下载完毕");
                        downloadComplete = true;
                        EventBus.getDefault().post(new BusEvent(BusEvent.GET_SHARE_IMAGE_LIST_SUCCESS, null));

                    }
                    EventBus.getDefault().post(new BusEvent(BusEvent.SEND_DOWNLOAD_IMAGES_PROGRESS, urls.size() - mIds.size(), tag));
                }
            } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                //    Toast.makeText(UIUtils.getContext(), "别瞎点！！！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //调用微信朋友圈分享图片;
    private static void shareToTimeLine() {
        File root = UIUtils.getContext().getExternalFilesDir("Download");
        KLog.i("root.name = " + root.getAbsolutePath());
        File[] files = root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".png") || pathname.getName().endsWith(".jpg"))
                    return true;
                return false;
            }
        });
        /*for(File file:files){
            KLog.i("file.name=  "+file.getName());
            String fileName=file.getName();
            if(fileName.contains("!dplist")){
                file.renameTo(new File(fileName.substring(0,fileName.length()-7)));
            }
            KLog.i("file.name=  "+file.getName());
        }*/
        shareMultiplePictureToTimeLine(files);
    }

    private static void shareMultiplePictureToTimeLine(File[] files) {

        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        intent.putExtra("Kdescription", desc);
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for (File f : files) {
            KLog.i(f.getName().toString());
            imageUris.add(Uri.fromFile(f));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        KLog.i("shareToTimeLine");
        UIUtils.getContext().startActivity(intent);
    }

}
