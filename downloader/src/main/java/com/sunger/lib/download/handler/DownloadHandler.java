package com.sunger.lib.download.handler;

import android.os.Handler;
import android.os.Message;

import com.sunger.lib.download.utils.FileUtils;

import java.text.DecimalFormat;

/**
 * Created by sunger on 15/9/3.
 */
public abstract class DownloadHandler extends Handler {
    private static final int MESSAGE_START = 0;
    private static final int MESSAGE_ERROR = 1;
    private static final int MESSAGE_FINISH = 2;
    private static final int MESSAGE_SPEED = 3;
    private static final int MESSAGE_PROGRESS = 4;
    private int compeleteSize = 0;
    private int fileSize = 0;
    private String fileName;
    private String tempName;
    private DecimalFormat df = new DecimalFormat("######0.00");

    protected abstract void onFinish(String msg);

    protected abstract void onError(String msg);

    protected void onStart() {
    }

    protected void onProgress(int percent, int completeSize, int total) {
    }

    protected void onSpeed(String speed) {

    }


    @Override
    public void handleMessage(Message msg) {
        Object[] params = (Object[]) msg.obj;
        switch (msg.what) {
            case MESSAGE_START:
                onStart();
                break;
            case MESSAGE_ERROR:
                Throwable throwable = (Throwable) params[0];
                onError(throwable.getMessage());
                break;
            case MESSAGE_FINISH:
                onFinish(params[0].toString());
                break;
            case MESSAGE_SPEED:
                onSpeed(params[0].toString());
                break;
            case MESSAGE_PROGRESS:
                onProgress((Integer) params[0], compeleteSize, fileSize);
                break;
        }
    }

    public synchronized Message obtainMessages(int type, Object[] object) {
        Message message = new Message();
        message.what = type;
        message.obj = object;
        return message;
    }

    public void sendFinishMessage(String msg) {
        sendMessage(obtainMessages(MESSAGE_FINISH, new Object[]{msg}));
    }


    private void sendSpeedMessage(long used_time, int length) {
        if (used_time <= 0)
            return;
        String speedStr = "";
        double speed = length / used_time * 1000;
        if (speed < 1024) {
            speedStr = df.format(speed) + "B/s";
        } else if (speed <= 1024 * 1024) {
            speedStr = df.format(speed / 1024) + "KB/s";
        } else {
            speedStr = df.format(speed / 1024 / 1024) + "MB/s";
        }
        sendMessage(obtainMessages(MESSAGE_SPEED, new Object[]{speed}));
    }

    private void sendProgressMessage() {
        int percent = (int) ((float) compeleteSize / (float) fileSize * 100);
        sendMessage(obtainMessages(MESSAGE_PROGRESS, new Object[]{percent}));
    }


    public void sendStartMessage(String fileName, String tempName, int globalCompelete, int fileSise) {
        this.fileName = fileName;
        this.tempName = tempName;
        this.compeleteSize = globalCompelete;
        this.fileSize = fileSise;
        sendMessage(obtainMessages(MESSAGE_START, null));
    }

    public void sendErrorMessage(Throwable throwable) {
        sendMessage(obtainMessages(MESSAGE_ERROR, new Object[]{throwable}));
    }

    public synchronized void sendSyncMessage(long used_time, int length) {
        compeleteSize += length;
        sendSpeedMessage(used_time, length);
        sendProgressMessage();
        if (compeleteSize >= fileSize) {
            //文件下载完成重命名成目标名称
            FileUtils.renameTo(tempName, fileName);
            sendFinishMessage("文件下载成功");
        }
    }


}
