package com.sunger.lib.download.bean;

public class DownloadEntity {
    public int threadId;
    public int startPos;
    public int endPos;
    public int compeleteSize;
    public String url;

    public DownloadEntity(int threadId, int startPos, int endPos,
                          int compeleteSize, String url) {
        this.threadId = threadId;
        this.startPos = startPos;
        this.endPos = endPos;
        this.compeleteSize = compeleteSize;
        this.url = url;
    }

}
