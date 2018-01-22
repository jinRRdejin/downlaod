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
 * ����������
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
		//��ȡ���ݿ��߳���Ϣ
		List<ThreadInfo> list = mDao.getThreads(mFileInfo.getUrl());
		Log.i(TAG, "threadInfo");
		//��ʼ���̶߳���
		if(list.size() == 0){
			threadInfo = new ThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(), 0);
			Log.i(TAG, "threadInfo1");
		}else{
			threadInfo = list.get(0);
			Log.i(TAG, "threadInfo2");
		}
		
		//�������߳�����
		new downLoadTread(threadInfo).start();
	}
	
	/*
	 * �����߳�
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
			//�����ݿ�����߳���Ϣ
			if(! mDao.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())){
				Log.i(TAG, "insertThread = " );
				mDao.insertThread(mThreadInfo);
			}
			//��������λ��
			try {
				URL url = new URL(mThreadInfo.getUrl());
				Log.i(TAG, "url = " + url.toString());
				
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				Log.i(TAG, "GET = " + conn.getResponseCode());
				int length = conn.getContentLength();
				//�����ļ�����λ��
				int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
				int end = mThreadInfo.getStop();
				Log.i(TAG, "start = " + start );
				Log.i(TAG, "end  = " +  end );
//				conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
//				System.setProperty("sun.net.http.allowRestrictedHeaders", "true");  
//				conn.setRequestProperty("Range", "bytes="+start+"-"+end);
				
				//�����ļ�д��λ��
				File file = new File(DownloadService.DOWN_LOAD_PATH ,mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start + end);//�ڶ���ʱ��������úõ��ֽ���������һ���ֽ�����ʼ
				Log.i(TAG, "start + end" + start + end);				
				
				Intent intent = new Intent(DownloadService.ACTION_UPDAT); 
				mFinished = mFinished + mThreadInfo.getFinished();
				Log.i(TAG, "mFinished = " + mFinished);
				//��ʼ����
				Log.i(TAG, HttpURLConnection.HTTP_PARTIAL + "");
				if(conn.getResponseCode() ==HttpURLConnection.HTTP_OK){
					//��ȡ����
					in = conn.getInputStream();
					byte[] bytes = new byte[54];
					int len = -1;
					long time = System.currentTimeMillis();//����������ˢ��ʱ�䣬����UI����
					Log.i(TAG, time + "");
					while ((len = in.read(bytes)) != -1) {
						
						//д���ļ�
						raf.write(bytes,0,len);
						
						//�����ؽ����Թ㲥����Activity
						
						
						mFinished += len;
//						Log.d(TAG, ""+mFinished);
						if(System.currentTimeMillis() - time >500){	
							
							intent.putExtra("finished", (mFinished*100)/mFileInfo.getLength());
							Log.d(TAG, ""+mFinished * 100 / mFileInfo.getLength());
							mContext.sendBroadcast(intent);  
						}
						
						 //������ͣʱ���������ؽ���  
	                    if(isPause){  
	                    	Log.d(TAG, "isPause:"+ mFinished );
	                        mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinished);  
	                        return;  
	                    }  
						
					}
					//ɾ�����ݿ��е��߳���Ϣ
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
