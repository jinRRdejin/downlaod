package com.example.multidownload_demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DownloadTask {
	
	private static final String TAG = "jrr";
	private Context context = null;
    private FileInfo mFileInfo = null; 
    private int mThreadCount = 1;//线程数量
    private List<DownloadThread>mThreadList = null; //线程集合
    public static ExecutorService sExecutorService = Executors.newCachedThreadPool(); //线程池
    
    private ThreadDao mThreadDAO = null;
    private FileDao mFileDAO = null;
    public boolean isPause = false;
    long finish = 0;
    
    
    
    
    
	public DownloadTask(Context context, FileInfo mFileInfo, int mThreadCount) {
		super();
		this.context = context;
		this.mFileInfo = mFileInfo;
		this.mThreadCount = mThreadCount;
		
		 mThreadDAO = ThreadDaoImpl.getInstance(context);
	     mFileDAO = FileDaoImpl.getInstance(context);
	}
    
    public void download(){
    	
    	//读取数据库线程信息
    	List<ThreadInfo> threadInfos = mThreadDAO.getThread(mFileInfo.getUrl());
    	 Log.i(TAG, "threadInfos = " + threadInfos .size());
    	if(threadInfos.size() == 0){
    		//获取每个线程下载的长度
    		 long length = mFileInfo.getLength() / mThreadCount;
             for (int i = 0; i < mThreadCount; i++) {
            	 
            	 Log.i(TAG, "mThreadCount = " +mThreadCount);
                 //初始化线程信息对象
            	 long  startIndex = length * i;
            	 Log.i(TAG, "i =" + i + "startIndex = " + startIndex);
            	 long endIndex = (i + 1) * length - 1;
            	 Log.i(TAG, "i =" + i + "endIndex = " + endIndex);
            	 if( i == mThreadCount - 1){
            		 endIndex = mFileInfo.getLength();
                 }
                 ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(),startIndex ,endIndex, 0);
                
               //添加线程信息到集合中
                 threadInfos.add(threadInfo);
               //向数据库插入线程信息
                 mThreadDAO.insertThread(threadInfo);
                Log.i(TAG, i+ " = i  ") ;
             }
             Log.i(TAG, "download()  finish!!!"); 
    	}
    	
    	//启动多个线程进行下载
    	mThreadList = new ArrayList<DownloadTask.DownloadThread>();
    	Log.i(TAG,  " mThreadList") ;
    	for (ThreadInfo threadInfo : threadInfos) {
    		
    		Log.i(TAG,"strat == " + threadInfo.getStart() +" end == " + threadInfo.getEnd());
    		DownloadThread downloadThread = new DownloadThread(threadInfo);
//    		downloadThread.start();
    		DownloadTask.sExecutorService.execute(downloadThread);
    		//添加到线程集合中来
    		mThreadList.add(downloadThread);
		}
    	
    	
    }
    
    
    /*
     * 
     * 数据下载线程
     */
    class DownloadThread extends Thread{
    	
    	private ThreadInfo threadInfo = null;
    	public boolean isFinshed = false; //标示线程是否执行完
    	
    	
		public DownloadThread(ThreadInfo threadInfo) {
			super();
			this.threadInfo = threadInfo;
		}

		@Override
		public void run() {
			 HttpURLConnection conn = null;
	         RandomAccessFile raf = null;
	         InputStream in = null;	 
	         
	         try {
	        	 
	        	URL url = new URL(threadInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(5000);
				conn.setRequestMethod("GET");
				
				//设置下载位置
				long start = threadInfo.getStart();
				long end = threadInfo.getEnd();
				Log.i(TAG,  "DownloadTask ---start =  " + start) ;
				
				
				conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
				 //设置文件写入的位置
				 File file = new File(DownloadService.DOWNLOAD_PATH, mFileInfo.getFileName());
	             raf = new RandomAccessFile(file, "rwd");
	             raf.seek(start);
				 
	             Intent  intent = new Intent(DownloadService.ACTION_UPDATE);
	             
	             
	             if(conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL){
	            	 
	            	 //读取数据源
	            	 in = conn.getInputStream(); 
	            	 byte[] bytes = new byte[50];
	            	 int len = -1;  
	            	 long time = System.currentTimeMillis();
	            	 while ( (len =in.read(bytes)) != -1) {
						//写入文件
	            		 raf.write(bytes, 0, len);
	            		 //累加单个线程完成的进度
	            		synchronized (DownloadTask.class) {
	            			finish += len;
						}
	            		 
	            		 
	            	    //累加每个线程完成的计划
//	            		 threadInfo.setFinished(DownloadTask.finish);
	            		//每隔一秒刷新UI
	            		 
	            		 if (System.currentTimeMillis() - time > 2000) {
//							time = System.currentTimeMillis();
	            			 Log.i(TAG,"the whole file finished = " + threadInfo.getFinished() );
							 //发送进度到Activity
	            			 int fin = (int) (finish * 100 / mFileInfo.getLength());
	            			 
	            			Log.i(TAG, "fin = " + fin);
	            			 
                            intent.putExtra("finished",fin);
                            intent.putExtra("id", mFileInfo.getId());
                            context.sendBroadcast(intent);
                            Log.i(TAG, "mFinished id==" + mFileInfo.getId() + ",percent==" + fin);
                      
						}
	            		 //下载暂停，保存进度
	            		 
	            		 if(isPause){
	            			 mThreadDAO.updateThread(mFileInfo.getUrl(),
	            			  mFileInfo.getId(), mFileInfo.getFinish()); 
	            			 return;
	            		 }	 
	            		 
					}
	            	 
	            	 //标识线程执行完毕
	            	 isFinshed = true;
	            	 Log.i(TAG," isFinshed = " + isFinshed);
	            	 //检查下载任务是否完成
	            	 checkAllThreadFinished( threadInfo);
	            	 
	             }      
	             
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				
				if(raf != null){
					try {
						raf.close();
						if( in != null){
							in.close();
						}
						if(conn != null){
							conn.disconnect();
						}		
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
			super.run();
		}
    	
		private synchronized void checkAllThreadFinished(ThreadInfo mThreadinfo){
			boolean allFinished = true;
			//便令线程，判断线程是否都已执行完毕
			Log.i(TAG, "checkAllThreadFinished 1");
			for (DownloadThread thread : mThreadList) {
			if( !thread.isFinshed){
				allFinished = false;
				break;
			  }
			}
			Log.i(TAG, "checkAllThreadFinished 2");
			if (allFinished) {
				
				//删除信息
				mThreadDAO.deleteThread(mFileInfo.getUrl());
				Intent intent = new Intent(DownloadService.ACTION_FINISHED);
				intent.putExtra("fileInfo", mFileInfo);
				
				context.sendBroadcast(intent);
				mFileDAO.insertFile(mFileInfo);
				
			}
			Log.i(TAG, "checkAllThreadFinished 3");
		}
    	
    	
    }
    
    
    
    
    
    
}
