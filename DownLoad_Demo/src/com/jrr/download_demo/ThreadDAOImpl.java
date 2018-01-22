package com.jrr.download_demo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.method.MovementMethod;

public class ThreadDAOImpl implements ThreadDAO{
	
	/*
	 * 数据接口实现(non-Javadoc)
	 * @see com.jrr.download_demo.ThreadDAO#insertThread(com.jrr.download_demo.ThreadInfo)
	 */
    private DBHelper mHelper;
    
	public ThreadDAOImpl(Context context) {

           mHelper = new DBHelper(context);
	}

	@Override
	public void insertThread(ThreadInfo threadInfo) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.execSQL("insert into Thread_info(thread_id,url,start,end,finished)" +
				" values (?,?,?,?,?)" ,
				new Object[]{threadInfo.getId(),threadInfo.getUrl(),threadInfo.getStart(),
				threadInfo.getStop(),threadInfo.getFinished()});
		db.close();
	}

	@Override
	public void deleteThread(String url, int thread_id) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.execSQL("delete from Thread_info where url = ? and thread_id = ?",
				new Object[]{url,thread_id});
		db.close();
		
	}

	@Override
	public void updateThread(String url, int thread_id, int finished) {
		SQLiteDatabase db = mHelper.getWritableDatabase();		
		 db.execSQL("update thread_info set finished=? where url=? and thread_id=?",  
	                new Object[]{finished,url,thread_id});
		 
		db.close();
		
	}

	@Override
	public List<ThreadInfo> getThreads(String url) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		List<ThreadInfo> list = new ArrayList<ThreadInfo>();
		
		Cursor cursor = db.rawQuery("select * from Thread_info where url = ?",new String[]{url});
		while(cursor.moveToNext()){	
			ThreadInfo thread = new ThreadInfo();  
            thread.setId(cursor.getInt(cursor.getColumnIndex("thread_id"))); 
            thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            thread.setStop(cursor.getInt(cursor.getColumnIndex("end")));
            thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			
			list.add(thread);
		}
		cursor.close();
		db.close();
		return list;
	}

	@Override
	public boolean isExists(String url, int thread_id) {
		
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from Thread_info where url = ? and thread_id = ?",
				new String[]{url,thread_id+ ""});
		boolean exits = cursor.moveToNext();
		cursor.close();
		db.close();
		return exits;
	}

	

}
