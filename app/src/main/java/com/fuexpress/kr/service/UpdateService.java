package com.fuexpress.kr.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.RemoteViews;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.model.AppDownloadTask;

import java.io.File;

/**
 * Created by Longer on 2016/10/26.
 */
public class UpdateService extends IntentService {
    public static String UPDATE_URL = "update_url";
    public static int NOTIFI_ID = 12345;
    private static File DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private NotificationManager notificationManager;
    private Notification notification;
    private Intent updateIntent;
    private PendingIntent pendingIntent;


    public UpdateService() {
        super("update service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        createNotification();
        String url = intent.getStringExtra(UPDATE_URL);
        AppDownloadTask downloadTask = new AppDownloadTask(url, notificationManager, notification);
        downloadTask.downloadApk(this, DOWNLOAD_DIR);
    }

    RemoteViews contentView;

    @SuppressWarnings("deprecation")
    public void createNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.drawable.ic_launcher;
        notification.tickerText = "开始下载";
        contentView = new RemoteViews(getPackageName(), R.layout.notification_item);
        contentView.setProgressBar(R.id.pb_update, 100, 0, false);
        contentView.setTextViewText(R.id.tv_progress, "0");
        notification.contentView = contentView;
        updateIntent = new Intent(this, MainActivity.class);
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
        notification.contentIntent = pendingIntent;
        notificationManager.notify(NOTIFI_ID, notification);
    }

}
