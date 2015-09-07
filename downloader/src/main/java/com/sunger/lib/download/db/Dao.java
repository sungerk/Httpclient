package com.sunger.lib.download.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sunger.lib.download.bean.DownloadEntity;

import java.util.ArrayList;
import java.util.List;

public class Dao {
    private DBHelper dbHelper;

    public Dao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void insertInfos(List<DownloadEntity> infos) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        for (DownloadEntity info : infos) {
            String sql = "insert into download_info(thread_id,start_pos, end_pos,compelete_size,url) values (?,?,?,?,?)";
            Object[] bindArgs = {info.threadId, info.startPos,
                    info.endPos, info.compeleteSize, info.url};
            database.execSQL(sql, bindArgs);
        }
    }

    public List<DownloadEntity> getInfos(String urlstr) {
        List<DownloadEntity> list = new ArrayList<DownloadEntity>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String sql = "select thread_id, start_pos, end_pos,compelete_size,url from download_info where url=?";
        Cursor cursor = database.rawQuery(sql, new String[]{urlstr});
        while (cursor.moveToNext()) {
            DownloadEntity info = new DownloadEntity(cursor.getInt(0),
                    cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
                    cursor.getString(4));
            list.add(info);
        }
        return list;
    }

    public void updataInfos(int threadId, int compeleteSize, String urlstr) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String sql = "update download_info set compelete_size=? where thread_id=? and url=?";
        Object[] bindArgs = {compeleteSize, threadId, urlstr};
        database.execSQL(sql, bindArgs);
    }

    public void closeDb() {
        dbHelper.close();
    }

    public void delete(String url) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete("download_info", "url=? ", new String[]{url});
    }

    public void delete(String url, int threadId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete("download_info", "url=?  and thread_id=?", new String[]{url, String.valueOf(threadId)});
    }
}