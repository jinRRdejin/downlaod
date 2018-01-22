package com.jrr.download_demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service{
	
	public static final String ACTION_START = "ACTION_START";
	public static final String ACTION_STOP = "ACTION_STOP";
	private static final String  TAG = "jrr";
//	public static final String  DOWN_LOAD_PATH =
//			Environment.getExternalStorageDirectory().getAbsolutePath()
//			+ "downloads";
	public static final String  DOWN_LOAD_PATH = Environment.getDataDirectory().getPath()
			+ "/downloads/";
	public static final int MSG_INIT = 0; 
	public static final String ACTION_UPDAT = "ACTION_UPDAT";
	private DownloadTask task = null; 
	private Context mContext;
		
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		// ���Activity�����Ĳ���
		if(ACTION_START.equals(intent.getAction())){
		FileInfo fileinfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			Log.i(TAG, "Start:" + fileinfo.toString());	
			
			//������ʼ���߳�
			new InitThread(fileinfo).start();
			
		}else if(ACTION_STOP.equals(intent.getAction())){
		FileInfo fileinfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			Log.i(TAG, "Stop:" +fileinfo.toString());
			
			if(task != null){  
				Log.i(TAG, "setPause");
                task.setPause(true);  
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
				FileInfo fileInfo = (FileInfo) msg.obj;
				Log.i(TAG, "Init" + fileInfo.toString());
				
				//������������
				task = new DownloadTask(DownloadService.this, fileInfo);
				task.download();
				break;

			default:
				break;
			}
			
		};
	};
	
	/*
	 * ��ʼ�����߳�
	 */
	class InitThread extends Thread{
		
		private FileInfo mFileInfo = null;
		private RandomAccessFile raf;

		public InitThread(FileInfo mFileInfo) {
			super();
			this.mFileInfo = mFileInfo;
		}
	
		@Override
		public void run() {
			HttpURLConnection conn = null;
			Log.i(TAG, "InitThread");
			try {
				
				//���������ļ�
				URL url = new URL(mFileInfo.getUrl());
				 Log.i(TAG, "url =" + mFileInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setRequestMethod("GET");//��Ϊ�Ǵӷ����������ļ�����������ѡ��get��ʽ����ȡ�������������ݣ�һ��Ҫ�ǵ���postŶ

				Log.i(TAG, "connect network");
				//��ȡ�ļ�����  //HttpURLConnection.HTTP_OK = 200 
				int length = -1;      
	            //������Ӧ��ȡ�ļ���С  	           
				//��׼httpЭ��  ���ʳɹ�����200
				Log.i(TAG, "connect network" + conn.getResponseCode());
				if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){	
				length = conn.getContentLength();
				 Log.i(TAG, "length =" + length);
				}
				if(length < 0){
					return;
					
				}
				
				String paths = Environment.getDataDirectory().getPath();
			        Log.i(TAG, "paths =" + paths);  
				File dir = new File(DownloadService.DOWN_LOAD_PATH);
				Log.i(TAG, "paths =" + paths);  
				if(!dir.exists()){
					dir.mkdir();
				}
				Log.i(TAG, "dir =" + dir);  
				//�ڱ��ش����ļ�
				File file = new File(dir, mFileInfo.getFileName());
				 raf = new  RandomAccessFile(file,"rwd");//��дɾ��Ȩ��
				
				//�����ļ�����
				raf.setLength(length);
				mFileInfo.setLength(length);
				mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();
				Log.i(TAG, "mHandler");  
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				conn.disconnect();
				try {
					raf.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			super.run();
		}
	} 

}
