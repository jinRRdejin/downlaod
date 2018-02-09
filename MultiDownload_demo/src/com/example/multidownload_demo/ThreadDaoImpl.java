package com.example.multidownload_demo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ThreadDaoImpl implements ThreadDao{
    
	private static final String TAG = "jrr";
	private static  ThreadDaoImpl instance;
	private DbHelper mDbHelper;
	
	
	
	public ThreadDaoImpl(Context context) {

		this.mDbHelper = mDbHelper.getInstance(context);
	}
	
	public static synchronized ThreadDaoImpl getInstance(Context context) {
        if (instance == null) {
            instance = new ThreadDaoImpl(context);
        }
        return instance;
    }

	@Override
	public void insertThread(ThreadInfo threadInfo) {
		Log.i(TAG, "insertThread");
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.execSQL("insert into thread_info(thread_id,url,start,end,finished) values(?,?,?,?,?)",
				new Object[]{threadInfo.getId(),threadInfo.getUrl(),threadInfo.getStart(),
				threadInfo.getEnd(),threadInfo.getFinished()});
		db.close();
	}

	@Override
	public void deleteThread(String url) {
		Log.i(TAG, "deleteThread");
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.execSQL("delete from thread_info where url = ?" , new String[]{url});
		db.close();
	}

	@Override
	public void updateThread(String url, int thread_id, long finished) {
		Log.i(TAG, "updatwThread");
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.execSQL("update Thread_info set finished = ? where url = ? and thread_id = ?",
				new Object[]{url ,thread_id,finished});
		
	}

	 public List<ThreadInfo> getThread(String url) {
	        Log.i(TAG, "getThread");
	        List<ThreadInfo> list = new ArrayList<ThreadInfo>();
	        SQLiteDatabase db = mDbHelper.getReadableDatabase();
	        Cursor cursor = db.rawQuery("select * from thread_info where url = ?", new String[]{url});
	        while (cursor.moveToNext()) {
	            ThreadInfo thread = new ThreadInfo();
	            thread.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
	            thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
	            thread.setStart(cursor.getLong(cursor.getColumnIndex("start")));
	            thread.setEnd(cursor.getLong(cursor.getColumnIndex("end")));
	            thread.setFinished(cursor.getLong(cursor.getColumnIndex("finished")));
	            list.add(thread);
	        }
	        cursor.close();
	        db.close();
	        return list;
	    }

	@Override
	public boolean isExists(String url, int thread_id) {
		Log.i(TAG, "isExists");
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from Thread_id where url = ? and thread_id = ?",
				new String[]{url,String.valueOf(thread_id)});
		boolean isExist = cursor.moveToNext();
		cursor.close();
		db.close();
		Log.i(TAG, "isExists = " + isExist);
		return isExist;
	}

}
