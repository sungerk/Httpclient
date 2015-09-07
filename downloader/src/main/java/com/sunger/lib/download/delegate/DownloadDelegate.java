package com.sunger.lib.download.delegate;

import android.content.Context;

import com.sunger.lib.download.handler.DownloadHandler;
import com.sunger.lib.download.task.DownLoadRequest;

/**
 * Created by sunger on 15/9/3.
 */
public class DownloadDelegate {

    private DownLoadRequest request;

    public DownloadDelegate(Context context, String url, String loaclPath, DownloadHandler handler) {
        this(context, url, null, loaclPath, handler);
    }

    public DownloadDelegate(Context context, String url, String fileName, String loaclPath, DownloadHandler handler) {
        request = new DownLoadRequest(context, url, fileName, loaclPath, handler);
    }


    public void pause() {
        request.pause();
    }

    public void start() {
        request.start();
    }

    public void setThreadCount(int threadCount) {
        request.setThreadCount(threadCount);
    }

}
