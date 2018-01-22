package com.jrr.download_demo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
 * 下载任务类
 */
public class DownloadTask {
	
	private static final String TAG = "jrr";
	private Context mContext = null;
	private FileInfo mFileInfo = null;
	private ThreadDAO mDao = null;
	private int mFinished = 0;  
	private boolean isPause = false;
	
	public DownloadTask(Context mContext, FileInfo mFileInfo) {
		this.mContext = mContext;
		this.mFileInfo = mFileInfo;
		mDao = new ThreadDAOImpl(mContext);
	}
	
	public void setPause(boolean isPause){
		this.isPause = isPause;
	}
	
	public void download(){
		
		ThreadInfo threadInfo = null;
		//读取数据库线程信息
		List<ThreadInfo> list = mDao.getThreads(mFileInfo.getUrl());
		Log.i(TAG, "threadInfo");
		//初始化线程对象
		if(list.size() == 0){
			threadInfo = new ThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(), 0);
			Log.i(TAG, "threadInfo1");
		}else{
			threadInfo = list.get(0);
			Log.i(TAG, "threadInfo2");
		}
		
		//创建子线程下载
		new downLoadTread(threadInfo).start();
	}
	
	/*
	 * 下载线程
	 */
	class downLoadTread extends Thread{
		
		private ThreadInfo mThreadInfo = null; 
		
		
		public downLoadTread(ThreadInfo mThreadInfo) {
			super();
			this.mThreadInfo = mThreadInfo;
		}


		@SuppressWarnings("resource")
		@Override
		public void run() {
			
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			InputStream in = null;
			//向数据库插入线程信息
			if(! mDao.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())){
				Log.i(TAG, "insertThread = " );
				mDao.insertThread(mThreadInfo);
			}
			//设置下载位置
			try {
				URL url = new URL(mThreadInfo.getUrl());
				Log.i(TAG, "url = " + url.toString());
				
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				Log.i(TAG, "GET = " + conn.getResponseCode());
				int length = conn.getContentLength();
				//设置文件下载位置
				int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
				int end = mThreadInfo.getStop();
				Log.i(TAG, "start = " + start );
				Log.i(TAG, "end  = " +  end );
//				conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
//				System.setProperty("sun.net.http.allowRestrictedHeaders", "true");  
//				conn.setRequestProperty("Range", "bytes="+start+"-"+end);
				
				//设置文件写入位置
				File file = new File(DownloadService.DOWN_LOAD_PATH ,mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start + end);//在读的时候调过设置好的字节数，从下一个字节数开始
				Log.i(TAG, "start + end" + start + end);				
				
				Intent intent = new Intent(DownloadService.ACTION_UPDAT); 
				mFinished = mFinished + mThreadInfo.getFinished();
				Log.i(TAG, "mFinished = " + mFinished);
				//开始下载
				Log.i(TAG, HttpURLConnection.HTTP_PARTIAL + "");
				if(conn.getResponseCode() ==HttpURLConnection.HTTP_OK){
					//读取数据
					in = conn.getInputStream();
					byte[] bytes = new byte[54];
					int len = -1;
					long time = System.currentTimeMillis();//减慢进度条刷新时间，减少UI负载
					Log.i(TAG, time + "");
					while ((len = in.read(bytes)) != -1) {
						
						//写入文件
						raf.write(bytes,0,len);
						
						//把下载进度以广播发给Activity
						
						
						mFinished += len;
//						Log.d(TAG, ""+mFinished);
						if(System.currentTimeMillis() - time >500){	
							
							intent.putExtra("finished", (mFinished*100)/mFileInfo.getLength());
							Log.d(TAG, ""+mFinished * 100 / mFileInfo.getLength());
							mContext.sendBroadcast(intent);  
						}
						
						 //下载暂停时，保存下载进度  
	                    if(isPause){  
	                    	Log.d(TAG, "isPause:"+ mFinished );
	                        mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinished);  
	                        return;  
	                    }  
						
					}
					//删除数据库中的线程信息
					 mDao.deleteThread(mThreadInfo.getUrl(), mThreadInfo.getId());                         
					   
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			super.run();
		}
	}

}
