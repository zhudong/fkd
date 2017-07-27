package com.fuexpress.kr.model.download;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.AppInfo;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.downloade.DownloadResult;
import com.fuexpress.kr.model.downloade.DownloadTask;
import com.fuexpress.kr.model.downloade.IDownloadListener;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.utils.CommonUtils;
import com.fuexpress.kr.utils.LogUtils;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import fksproto.CsInfo;


/**
 * Created by Administrator on 2016-9-11.
 */
public class UpdateManager {
    public static String TAG = "UpdateService";
    public static int NOTIFI_ID = 12345;
    private static File DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    private NotificationManager notificationManager;
    private Notification notification;

    public void getNewVersion(final Activity context) {
        String url = " http://api.fir.im/apps/latest/57fb6dae959d69487d000008?api_token=9bb7210eb739780524f859683bcc40bc";
//        String url = " http://api.fir.im/latest/57ccb739780524f859683bcc40bc";
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("update", "update fail");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Gson gson = new Gson();
                final AppInfo appInfo = gson.fromJson(response.body().string(), AppInfo.class);
                int versionCode = CommonUtils.getVersionCode(context);
                if (appInfo.getVersion() == null) {
                    return;
                }

                if (Integer.valueOf(appInfo.getVersion()) <= versionCode) {
                    return;
                }
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.update_tip_title))
                                .setMessage(appInfo.getName() + "\n" + context.getString(R.string.update_new_version) + appInfo.getVersionShort() + "\n" + appInfo.getChangelog())
                                .setCancelable(false)
                                .setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       /* Intent intent = new Intent(context, UpdateService.class);
                                        intent.putExtra(UpdateService.UPDATE_URL, appInfo.getInstall_url());*/
//                                        context.startService(intent);
                                    }
                                }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        work(0);
                                    }
                                });
                        builder.show();
                    }
                });

            }
        });
    }

    public void getNewVersion(final Activity context, int type) {
        CsInfo.GetVersionInfoRequest.Builder builder = CsInfo.GetVersionInfoRequest.newBuilder();
        builder.setApptype(3).setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsInfo.GetVersionInfoResponse>() {

            @Override
            public void onSuccess(CsInfo.GetVersionInfoResponse response) {
                String version = response.getVersion();
                String versionName = CommonUtils.getVersionName(context);
                if (!TextUtils.isEmpty(version) && !TextUtils.isEmpty(versionName) && !TextUtils.isEmpty(response.getDownloadurl())) {
                    if (version.compareTo(versionName) > 0) {
                        showDialog(context, response);
                    }
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                LogUtils.e(errMsg);
            }
        });
    }

    private void showDialog(final Activity context, final CsInfo.GetVersionInfoResponse response) {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                final CustomDialog.Builder builder = new CustomDialog.Builder(context);
                if (!AccountManager.getInstance().getLocaleCode().contains("zh")) {
                    builder.setVertical(true);
                }
                builder.setTitle(context.getString(R.string.app_update_found_version) + response.getVersion());
                CustomDialog dialog = builder.setMessage(response.getDescription())
                        .setPositiveButton(context.getString(R.string.app_update_now), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startTask(context, response.getDownloadurl());
//                                startTask2(context, response.getDownloadurl());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(context.getString(R.string.app_update_ignore), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }

    private void startTask(final Context context, String url) {
        createNotification(context);
        AppDownloadTask task = new AppDownloadTask(url, notificationManager, notification);
        task.downloadApk(DOWNLOAD_DIR);
    }

    private void startTask2(final Context context, String url) {
        String path = getFileName(url);
        new DownloadTask(context, url, path, new IDownloadListener() {
            @Override
            public void onDownloadStarted() {
                createNotification(context);
            }

            @Override
            public void onDownloadFinished(DownloadResult result) {
                Log.d(TAG, "Start download finshed ");
                notificationManager.cancel(UpdateManager.NOTIFI_ID);
                CommonUtils.install(UIUtils.getContext(), Uri.fromFile(new File(result.path)));
            }

            @Override
            public void onProgressUpdate(Float... value) {
                int progress = Math.round(value[0] / value[1] * 100);
                notification.contentView.setProgressBar(R.id.pb_update, 100, progress, false);
                notification.contentView.setTextViewText(R.id.tv_progress, progress + "%");
                notificationManager.notify(UpdateManager.NOTIFI_ID, notification);
            }
        }).execute();
    }

    private String getFileName(String url) {
        String filename = "";
        if (!TextUtils.isEmpty(url)) {
            String[] split = url.split("/");
            if (split.length > 0) {
                filename = split[split.length - 1];
            }
        }
        if (!filename.contains(".apk")) filename += ".apk";

        return DOWNLOAD_DIR + File.separator + filename;
    }


    public void createNotification(Context content) {
        notificationManager = (NotificationManager) content.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.drawable.mq_ic_download;
        notification.tickerText = "开始下载";
        RemoteViews contentView = new RemoteViews(content.getPackageName(), R.layout.notification_item);
        contentView.setProgressBar(R.id.pb_update, 100, 0, false);
        contentView.setTextViewText(R.id.tv_progress, "0");
        notification.contentView = contentView;
        Intent updateIntent = new Intent(content, MainActivity.class);
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notification.contentIntent = PendingIntent.getActivity(content, 0, updateIntent, 0);
        notificationManager.notify(NOTIFI_ID, notification);
    }
}
