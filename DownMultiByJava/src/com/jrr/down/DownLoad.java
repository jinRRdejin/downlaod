package com.jrr.down;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownLoad {

	public static int threaCount = 4;
	static long time =  0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		time =  System.currentTimeMillis();

		String path = "http://central.maven.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar";

		HttpURLConnection conn;
		RandomAccessFile raf;
		try {

			URL url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

				int length = conn.getContentLength();
				System.out.println("文件长度 = " + length);
				String name = "session1388.log";
				File file = new File("D:\\putty", name);
				raf = new RandomAccessFile(file, "rwd");

				// 指定创建的文件的长度
				raf.setLength(length);
				raf.close();
				// 在客户端本地
				// 假设3个线程去下载资源
				// 平均每一个线程下载的文件的大小。

				int blockSize = length / threaCount;
                
				for (int i = 1; i <= threaCount; i++) {

					// 每个线程起始与结束的位置
				
					int startIndex = (i - 1) * blockSize;
					int endIndex = blockSize * i - 1;

					if (i == threaCount) {
						endIndex = length;

					}

					System.out.println("线程:" + i + "下载:--" + startIndex + "-->"
							+ endIndex);
					 new DownLoadThread(i, startIndex, endIndex, path).start();  
					

				}
                
                
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 long s = System.currentTimeMillis() - time;
         System.out.println("线程共用时间为：" + s);

	}

	public static class DownLoadThread extends Thread {

		private int threadId;
		private int startIndex;
		private int endIndex;
		private String path;
		
		
		public DownLoadThread(int threadId, int startIndex, int endIndex,
				String path) {
			super();
			this.threadId = threadId;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.path = path;
		}
		public void run() {
			
			try {
				URL url = new URL(path);
				
				  HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	                conn.setRequestMethod("GET");  
	                //很重要：请求服务器下载部分的文件的指定的位置：  
	                conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);  
	                
	                
	                conn.setConnectTimeout(5000);  
	                int code  = conn.getResponseCode();//从服务器请求全部资源 200ok ,如果请求部分资源 206 ok  
	                System.out.println("code="+code);  
	                  
	                InputStream is = conn.getInputStream();//返回资源  
	                File file = new File("D:\\putty","commons-io-2.6.jar");
	                RandomAccessFile raf = new RandomAccessFile(file, "rwd");  
	                //随机写文件的时候从哪个位置开始写  
	                raf.seek(startIndex);//定位文件  
            
	                int len =0;  
	                byte[] buffer = new byte[1024];  
	                while((len = is.read(buffer)) != -1){  
	                    raf.write(buffer,0,len);  
	                }  
	                is.close();  
	                raf.close();  
	                System.out.println("线程"+threadId+"下载完毕");  
			
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			super.run();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

	}

}
