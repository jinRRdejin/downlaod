package com.jrr.download_demo;

import java.util.List;


/*
 * ���ݷ��ʽӿ�
 */
public interface ThreadDAO {
	
	//�����߳���Ϣ
	public void insertThread(ThreadInfo threadInfo);
	
    //ɾ���߳���Ϣ
	public void deleteThread(String url,int thread_id);
	
	//�����߳����ؽ���
    public void updateThread(String url,int thread_id,int finished);
    
    //��ѯ�ļ����߳���Ϣ
    public List<ThreadInfo> getThreads(String url);
    
    //�߳���Ϣ�Ƿ����
    public boolean isExists(String url,int thread_id);
}
