package com.sunger.lib.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String SQL_NAME = "downloader.db";
	private static final int DOWNLOAD_VERSION = 1;

	public DBHelper(Context context) {
		super(context, SQL_NAME, null, DOWNLOAD_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table download_info(_id integer PRIMARY KEY AUTOINCREMENT, thread_id integer, "
				+ "start_pos integer, end_pos integer, compelete_size integer,url char)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}