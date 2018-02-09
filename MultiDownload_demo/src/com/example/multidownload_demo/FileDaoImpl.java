package com.example.multidownload_demo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FileDaoImpl implements FileDao{
	
   private static final String TAG = "jrr";
   private DbHelper mDbHelper;
   private static FileDaoImpl fileDaoImpl;
   
   
	public FileDaoImpl(Context  context) {

	this.mDbHelper = DbHelper.getInstance(context);
    
	}
	
	public static synchronized FileDaoImpl getInstance(Context  context){
		if(fileDaoImpl == null){
			fileDaoImpl = new FileDaoImpl(context);
		}
		return fileDaoImpl;		
	}

	@Override
	public void insertFile(FileInfo fileInfo) {
		Log.i(TAG, "insertFile");
		SQLiteDatabase db= mDbHelper.getWritableDatabase();
		db.execSQL("insert into file_info(file_id,url,name,length,finish) values(?,?,?,?,?)",
				new Object[]{fileInfo.getId(),fileInfo.getUrl(),fileInfo.getFileName(),
				fileInfo.getLength(),fileInfo.getFinish()});
		db.close();
	}

	@Override
	public List<FileInfo> getFile(String url) {
		Log.i(TAG, "getFile");
//		SQLiteDatabase db= mDbHelper.getWritableDatabase();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		List<FileInfo> list = new ArrayList<FileInfo>();
		Cursor cursor = db.rawQuery("select * from file_info where url = ?", new String[]{url});
		while (cursor.moveToNext()) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setId(cursor.getInt(cursor.getColumnIndex("file_id")));
			fileInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			fileInfo.setFileName(cursor.getString(cursor.getColumnIndex("name")));
			fileInfo.setLength(cursor.getLong(cursor.getColumnIndex("length")));
			fileInfo.setFinish(cursor.getLong(cursor.getColumnIndex("finish")));
			list.add(fileInfo);
		}
		cursor.close();
		db.close();
		return list;
	}

	@Override
	public void deleteFile(String url) {
		Log.i(TAG, "deleteFile");
		SQLiteDatabase db= mDbHelper.getWritableDatabase();
		db.execSQL("delete from file_info where url = ? ",new String []{url});
		db.close();
	}

}
