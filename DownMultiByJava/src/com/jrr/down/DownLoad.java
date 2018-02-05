package com.jrr.down;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownLoad {

	public static int threaCount = 3;
	static long time =  0;
	public static int finish = 0;
	public static int length = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		time =  System.currentTimeMillis();

		String path = "http://pic29.photophoto.cn/20131204/0034034499213463_b.jpg";

		HttpURLConnection conn;
		RandomAccessFile raf;
		try {

			URL url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

			    length = conn.getContentLength();
				System.out.println("�ļ����� = " + length);
				String name = "jrr";
				File file = new File("D:\\putty", name);
				raf = new RandomAccessFile(file, "rwd");

				// ָ���������ļ��ĳ���
				raf.setLength(length);
				raf.close();
				// �ڿͻ��˱���
				// ����3���߳�ȥ������Դ
				// ƽ��ÿһ���߳����ص��ļ��Ĵ�С��

				int blockSize = length / threaCount;
                
				for (int i = 1; i <= threaCount; i++) {

					// ÿ���߳���ʼ�������λ��
				
					int startIndex = (i - 1) * blockSize;
					int endIndex = blockSize * i - 1;

					if (i == threaCount) {
						endIndex = length;

					}

					System.out.println("�߳�:" + i + "����:--" + startIndex + "-->"
							+ endIndex);
					 new DownLoadThread(i, startIndex, endIndex, path).start();  
					

				}
                
                
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 long s = System.currentTimeMillis() - time;
         System.out.println("�̹߳���ʱ��Ϊ��" + s);

	}

	public static class DownLoadThread extends Thread {

		private int threadId;
		private int startIndex;
		private int endIndex;
		private String path;
		int finished = 0;
		
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
	                //����Ҫ��������������ز��ֵ��ļ���ָ����λ�ã�  
	                conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);  
	                
	                
	                conn.setConnectTimeout(5000);  
	                int code  = conn.getResponseCode();//�ӷ���������ȫ����Դ 200ok ,������󲿷���Դ 206 ok  
	                System.out.println("code="+code);  
	                  
	                
	                InputStream is = conn.getInputStream();//������Դ  
	                
	                String[] str = path.split("/");
	                String name = str[str.length - 1];
	                File file = new File("D:\\putty",name);
	                RandomAccessFile raf = new RandomAccessFile(file, "rwd");  
	                //���д�ļ���ʱ����ĸ�λ�ÿ�ʼд  
	                raf.seek(startIndex);//��λ�ļ�  
	                int len =0;  
	                byte[] buffer = new byte[2048];  
	                while((len = is.read(buffer)) != -1){  
	                    raf.write(buffer,0,len); 
	                   	    
	                    synchronized ( DownLoad.class) { 
	                    	//ͨ��ȫ�ֱ���ͳ�����س���
	                    	 DownLoad.finish = DownLoad.finish + len;
						}
	                      //ͨ������setRequestProperty�����������ط�Χ���ٴλ�ȡ�����ļ����ȣ���Ϊ���߳����ط�Χ����
	                    System.out.println("�߳�"+threadId + "����ļ����� " + "finish = " + DownLoad.finish * 100/DownLoad.length);
	                }  
	                is.close();  
	                raf.close();  
	                System.out.println("�߳�" + threadId + "�������");  
			
				
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
