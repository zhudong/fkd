package com.fuexpress.kr.model.downloade;

/**
 * time: 15/7/15
 * description:
 *
 * @author sunjianfei
 */

public interface IDownloadListener {
    void onDownloadStarted();

    void onDownloadFinished(DownloadResult result);

    void onProgressUpdate(Float... value);
}

