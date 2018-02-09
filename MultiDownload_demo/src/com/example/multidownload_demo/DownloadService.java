package com.example.multidownload_demo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service{

	private static final String TAG = "jrr";
	public static final String ACTION_START = "ACTION_START";
	public static final String ACTION_STOP = "ACTION_STOP";	
	//结束下载
	public static final String ACTION_FINISHED = "ACTION_FINISHED";
	//更新UI
	public static final String ACTION_UPDATE = "ACTION_UPDATE";
	//下载路径
	public static final String DOWNLOAD_PATH =  Environment.getDataDirectory().getPath() + "/downloads/";
	//初始化
	public static final int  MSG_INIT  = 0;
	//下载任务集合
	private InitThread mInitThread;
	private Map<Integer, DownloadTask> mTask = new LinkedHashMap<Integer, DownloadTask>();
	public static final int runThreadCount = 3;
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	//获取Acvity传参
		if(ACTION_START.equals(intent.getAction())){
			FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			Log.i(TAG,  "onStartCommand : SART: "+fileInfo.toString());
			
			mInitThread = new InitThread(fileInfo);
			DownloadTask.sExecutorService.execute(mInitThread);
			
		}else if(ACTION_STOP.equals(intent.getAction())){
			
			FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			Log.i(TAG,  "onStartCommand : STOP: "+fileInfo.toString());
			
			//从集合中取出下载任务
			DownloadTask mDownloadTask = mTask.get(fileInfo.getId());
			if (mDownloadTask != null) {
				//停止下载任务
				mDownloadTask.isPause = true;
			}
		}
		
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_INIT:
				  FileInfo fileinfo = (FileInfo) msg.obj;
                  Log.i(TAG, "init:" + fileinfo.toString());
                  //初始化后，启动下载任务
                  DownloadTask mdDownloadTask = new DownloadTask(DownloadService.this,
                		  fileinfo, runThreadCount);
                  mdDownloadTask.download();
                  
                  //将下载任务添加到集合中
                  Log.i(TAG, fileinfo.getId()+ " = fileinfo.getId() ");
                  mTask.put(fileinfo.getId(), mdDownloadTask);
				break;
			}
		};
	};
	
	/*
	 * 初始化子线程
	 */
	
	class InitThread extends Thread{
		
		private FileInfo tFileInfo = null;

        RandomAccessFile raf;
        HttpURLConnection conn;
        
		public InitThread(FileInfo tFileInfo) {
			super();
			this.tFileInfo = tFileInfo;
		}
		@Override
		public void run() {
			
			//连接网络文件
			try {
				URL url = new URL(tFileInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
			
				
				//获取网络文件长度
				int length = -1;	
				if(conn.getResponseCode() ==  HttpURLConnection.HTTP_OK){
					
					length = conn.getContentLength();
					Log.i(TAG, "length = "+ length + " ");
				}
				
				if(length < 0){
					return;
					
				}
				
				//创建本地文件
				File file = new File(DownloadService.DOWNLOAD_PATH);
				if(! file.exists()){
					file.mkdir();
				}
				
				File files = new File(file,tFileInfo.getFileName());
				raf = new RandomAccessFile(files, "rwd");
				
				//设置本地长度
				raf.setLength(length);
				
				tFileInfo.setLength(length);
				 Log.i(TAG, "tFileInfo.getLength==" + tFileInfo.getLength());
				 mHandler.obtainMessage(MSG_INIT, tFileInfo).sendToTarget();
				 
				 
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(raf != null){
					try {
						raf.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						if(conn != null){
							conn.disconnect();
						}	
					}
				}
				
			}
			
			
			
			
			
			//
			
		}
		
	}
	

}
