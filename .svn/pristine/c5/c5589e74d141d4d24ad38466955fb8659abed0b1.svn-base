package com.fuexpress.kr.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.fuexpress.kr.R;
import com.fuexpress.kr.service.UpdateService;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.CommonUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Longer on 2016/10/26.
 */
public class AppDownloadTask {
    public static int UPCODE = 123456;
    private static final String TAG = "AppDownloadTask";
    private static final int BUFFER_SIZE = 16 * 1024;
    private final NotificationManager notificationManager;
    private final Notification notification;

    private String mUrl;
    private DownloadInfo mDownloadInfo;
    private Handler handler = new Handler() {

        private int progress;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPCODE) {
                if (progress == (int) msg.obj) return;
                progress = (int) msg.obj;
                Log.d("progress", progress + "");
                notification.contentView.setProgressBar(R.id.pb_update, 100, progress, false);
                notification.contentView.setTextViewText(R.id.tv_progress, progress + "%");
                notificationManager.notify(UpdateService.NOTIFI_ID, notification);
                if (mDownloadInfo.progress == 1) {
                    CommonUtils.install(UIUtils.getContext(), Uri.fromFile(mDownloadInfo.apkFile));
                    notificationManager.cancel(UpdateService.NOTIFI_ID);
                }
            }
        }
    };


    /* package */
    public AppDownloadTask(String url, NotificationManager manager, Notification notification) {
        mUrl = url;
        mDownloadInfo = new DownloadInfo();
        notificationManager = manager;
        this.notification = notification;
    }

    public void downloadApk(Context context, final File fileDir) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream outputStream = null;
        try {
            URL url = new URL(mUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.connect();
            int contentLength = urlConnection.getContentLength();

            Log.d(TAG, "Start downloading " + urlConnection.getURL());
            Log.d(TAG, String.format("File size %.2f kb", (float) contentLength / 1024));

            String fileName = getFileName(urlConnection);
            mDownloadInfo.apkFile = new File(fileDir, fileName);
            outputStream = new BufferedOutputStream(new FileOutputStream(mDownloadInfo.apkFile));
            Log.d(TAG, "Downloading apk into " + mDownloadInfo.apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            int totalLength = 0;
            InputStream in = urlConnection.getInputStream();
            while ((length = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                totalLength += length;
                mDownloadInfo.progress = (totalLength == 0f) ? 0f : (float) totalLength / (float) contentLength;
                Message message = handler.obtainMessage();
                message.what = UPCODE;
                message.obj = (int) (mDownloadInfo.progress * 100);
                handler.handleMessage(message);
            }
        } catch (IOException e) {
            Log.e(TAG, String.format("Download: %s, %s", mDownloadInfo.apkFile, mUrl), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private String getFileName(HttpURLConnection urlConnection) {
        Uri uri = Uri.parse(urlConnection.getURL().toString());
        // fir.im url: http://pkg.fir.im/27c81a3398038551ab67aa9335a4418f009c0655.apk
        // ?attname=bailu-2.6.6-16032001-160320142728-release.apk_2.6.6.apk
        // &e=1458475559&token=LOvmia8oXF4xnLh0IdH05XMYpH6ENHNpARlmPc-T:6N-VyCkN2bcr68ykQzhnDj3OkUE=
        String fileName = uri.getQueryParameter("attname");
        if (TextUtils.isEmpty(fileName)) {
            // attachment; filename="bailu-2.6.6-16032001-160320142728-release.apk_2.6.6.apk"
            String attachment = urlConnection.getHeaderField("Content-Disposition");
            if (attachment != null) {
                String delimiter = "filename=\"";
                int index = attachment.indexOf(delimiter);
                if (index != -1) {
                    fileName = attachment.substring(index + delimiter.length(), attachment.length() - 1);
                }
                if (TextUtils.isEmpty(fileName)) {
                    fileName = uri.getLastPathSegment();
                }
            }
        }
        return fileName;
    }

    /* package */ DownloadInfo getDownloadInfo() {
        return mDownloadInfo;
    }

    static class DownloadInfo {
        float progress;
        File apkFile;
    }
}
