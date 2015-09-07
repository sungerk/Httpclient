package com.sunger.lib.download.task;

import com.sunger.lib.download.bean.DownloadEntity;
import com.sunger.lib.download.db.Dao;
import com.sunger.lib.download.handler.DownloadHandler;

import org.sunger.lib.http.client.HttpClient;
import org.sunger.lib.http.client.HttpResponse;

import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by sunger on 15/8/25.
 */
public class DownLoadTask implements Runnable {
    public static final int STATE_PAUSE = 0;
    public static final int STATE_START = 1;
    public static final int DEFAULT_SOCKET_BUFFER_SIZE = 1024 * 8;
    public int state = STATE_START;
    private DownloadHandler handler;
    private DownloadEntity entity;
    private Dao dao;
    private String tempName;

    public DownLoadTask(Dao dao, String tempName, DownloadEntity entity, DownloadHandler handler) {
        this.tempName = tempName;
        this.entity = entity;
        this.dao = dao;
        this.handler = handler;
    }

    private void writeInputStream(HttpResponse response) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile(tempName, "rwd");
        randomAccessFile.seek(entity.startPos + entity.compeleteSize);
        int length = -1;
        byte[] buffer = new byte[DEFAULT_SOCKET_BUFFER_SIZE];
        InputStream inputStream = response.getPayload();
        int compeleteSize = entity.compeleteSize;
        long time = System.currentTimeMillis();
        while ((length = inputStream.read(buffer)) != -1) {
            randomAccessFile.write(buffer, 0, length);
            long current_time = System.currentTimeMillis();
            handler.sendSyncMessage(current_time - time, length);
            time = current_time;
            compeleteSize += length;
            dao.updataInfos(entity.threadId, compeleteSize, entity.url);
            if (compeleteSize>=entity.endPos-entity.startPos)
                dao.delete(entity.url, entity.threadId);
            if (state == STATE_PAUSE)
                break;
        }
        randomAccessFile.close();
    }

    @Override
    public void run() {
        HttpClient client = new HttpClient();
        client.setRange("bytes="
                + (entity.startPos + entity.compeleteSize) + "-" + entity.endPos);
        try {
            HttpResponse response = client.get(entity.url).execute();
            writeInputStream(response);

        } catch (Exception e) {
            handler.sendErrorMessage(e);
        }
    }

    public void pause() {
        state = STATE_PAUSE;
    }

}
