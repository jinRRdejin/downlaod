package com.example.multidownload_demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
	
	private static final String DB_NAME = "download.db";
	private static final int VERSION = 1;
	private static DbHelper helper;
	
//	private static final String SQL_CREAT = "creat table thread_info(_id integer primary key autoincrement," +
//			"thread_id integer,url text,start long,end long, finished long )";//数据库创建失败，把create写错了
	
	  private static final String SQL_CREATE = "create table thread_info(_id integer primary key autoincrement," +
	            "thread_id integer,url text,start long,end long,finished long)";	

	  
	  private static final String SQL_CREATE_FILE = "create table file_info(_id integer primary key autoincrement," +
	            "file_id integer,url text,name text,length long,finish long)";
	  
	private static final  String SQL_DROP = "drop table if exists thread_info";
	private static final String SQL_DROP_FILE = "drop table if exists file_info";
    
    
    
	public DbHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}
	/*
	 * 
	 * 获得单利对象
	 */
	public static DbHelper getInstance(Context context){
		if(helper == null  ){
			helper = new DbHelper(context);
		}
		
		return helper;
		
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
		db.execSQL(SQL_CREATE_FILE);
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DROP);
		db.execSQL(SQL_CREATE);
		
		db.execSQL(SQL_DROP_FILE);
		db.execSQL(SQL_CREATE_FILE);
	}

}
