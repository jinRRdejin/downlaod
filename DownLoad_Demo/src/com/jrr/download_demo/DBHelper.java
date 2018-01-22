package com.jrr.download_demo;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author cv
 *
 */
public class DBHelper extends SQLiteOpenHelper{
	
	/*
	 * Êý¾Ý¿â°ïÖúÀà
	 */
	
	private static final String DB_NAME = "download.db";
	private static final int VERSION = 1;
	private static final String SQL_CREAT ="create table thread_info(_id integer primary key," +  
		    "thread_id integer,url text,start integer,end integer,finished integer)";	  
	private static final String SQL_DROP = "drop table if exists Thread_info";
	
	

	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		
	}
   
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(SQL_CREAT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_DROP);
		db.execSQL(SQL_CREAT);
	}

}
