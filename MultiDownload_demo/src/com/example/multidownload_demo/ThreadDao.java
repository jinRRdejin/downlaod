package com.example.multidownload_demo;

import java.util.List;

public interface ThreadDao {
	
	/*
	 * 
	 * �����߳���Ϣ
	 */
	void insertThread(ThreadInfo threadInfo);
	/*
	 * 
	 * ɾ���߳���Ϣ
	 */
	void deleteThread(String url);
	
	/*
	 * 
	 * �����߳���Ϣ
	 */
	void updateThread(String url,int thread_id ,long finished);
	
	/*
	 * ��ѯ�ļ��߳���Ϣ
	 * 
	 */
    List<ThreadInfo>getThread(String url);
    
    /*
     * �ж��Ƿ����
     */
    boolean isExists(String url,int thread_id);
}
