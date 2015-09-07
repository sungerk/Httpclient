package com.sunger.lib.download.task;

import android.content.Context;

import com.sunger.lib.download.bean.DownloadEntity;
import com.sunger.lib.download.db.Dao;
import com.sunger.lib.download.handler.DownloadHandler;
import com.sunger.lib.download.utils.FileUtils;

import org.sunger.lib.http.client.HttpClient;
import org.sunger.lib.http.client.HttpResponse;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sunger on 15/8/25.
 */
public class DownLoadRequest implements Runnable {
    private List<DownLoadTask> subRunnables = new ArrayList<>();
    private DownloadHandler handler;
    private String filePath;
    private String fileName;
    //默认线程开启数量
    private int threadCount = 5;
    private Dao dao;
    //统一使用线程池执行任务
    private ExecutorService threadPool;
    private String url;


    public DownLoadRequest(Context context, String url, String fileName, String filePath, final DownloadHandler handler) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.url = url;
        this.handler = handler;
        this.dao = new Dao(context);
        threadPool = Executors.newCachedThreadPool();
    }

    private boolean isFileExists() {
        return FileUtils.isFileExists(getFilePath());
    }

    /**
     * 初始化下载
     * @param response
     */
    private void initDownload(HttpResponse response) {
        FileUtils.createFile(getFileTempName());
        if (fileName == null)
            fileName = response.getFileName();
    }

    /**
     * 分配下载任务
     *
     * @param response
     * @return
     * @throws Exception
     */
    private List<DownloadEntity> allocateDownLoadTask(HttpResponse response) throws Exception {
        File file = FileUtils.createFile(getFileTempName());
        RandomAccessFile accessFile = new RandomAccessFile(getFileTempName(), "rwd");
        accessFile.setLength(response.getContentLength());
        accessFile.close();
        List<DownloadEntity> downloadInfos = new ArrayList<>();
        int fileSize = response.getContentLength();
        int range = fileSize / threadCount;
        for (int i = 0; i < threadCount - 1; i++) {
            DownloadEntity info = new DownloadEntity(i, i * range, (i + 1)
                    * range - 1, 0, url);
            downloadInfos.add(info);
        }
        //计算最后一个线程
        DownloadEntity info = new DownloadEntity(threadCount - 1,
                (threadCount - 1) * range, fileSize, 0, url);
        downloadInfos.add(info);
        dao.insertInfos(downloadInfos);
        return downloadInfos;
    }

    /**
     * 从数据库读取下载记录
     *
     * @param response
     * @return
     */
    private List<DownloadEntity> getDownLoadInTaskInfoFromDB(HttpResponse response) {

        List<DownloadEntity> downloadInfos = dao.getInfos(url);
        if (!FileUtils.isFileExists(getFileTempName())) {
            //如果下载临时文件不存在，删除数据库下载记录
            dao.delete(url);
            downloadInfos.clear();
        }
        return downloadInfos;
    }

    /**
     * 发送开始现在信息
     *
     * @param downloadInfos
     */
    private void sendStartMsg(List<DownloadEntity> downloadInfos) {
        int globalCompelete = 0;
        for (DownloadEntity entity : downloadInfos) {
            globalCompelete += entity.compeleteSize;
        }
        handler.sendStartMessage(getFilePath(), getFileTempName(), globalCompelete, downloadInfos.get(downloadInfos.size() - 1).endPos);
    }

    private void excuteSubThreads(List<DownloadEntity> downloadInfos) {
        for (DownloadEntity entity : downloadInfos) {
            DownLoadTask task = new DownLoadTask(dao, getFileTempName(), entity, handler);
            subRunnables.add(task);
            threadPool.submit(task);
        }
    }

    @Override
    public void run() {
        if (isFileExists()) {
            handler.sendFinishMessage("文件已经存在");
            return;
        }
        try {
            HttpResponse response = new HttpClient().get(url).execute();
            initDownload(response);
            List<DownloadEntity> downloadInfos = getDownLoadInTaskInfoFromDB(response);
            if (downloadInfos.isEmpty())
                downloadInfos = allocateDownLoadTask(response);
            sendStartMsg(downloadInfos);
            excuteSubThreads(downloadInfos);
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendErrorMessage(e);
        }
    }

    /**
     * 开始下载
     */
    public void start() {
        threadPool.submit(this);
    }

    /**
     * 暂停下载
     */
    public void pause() {
        for (DownLoadTask task : subRunnables) {
            task.pause();
        }
    }


    private String getFilePath() {
        return filePath + "/" + fileName;
    }

    private String getFileTempName() {
        return getFilePath() + ".temp";
    }

    public DownloadHandler getHandler() {
        return handler;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }


}
