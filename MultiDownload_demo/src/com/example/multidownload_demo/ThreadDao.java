package com.example.multidownload_demo;

import java.util.List;

public interface ThreadDao {
	
	/*
	 * 
	 * 插入线程信息
	 */
	void insertThread(ThreadInfo threadInfo);
	/*
	 * 
	 * 删除线程信息
	 */
	void deleteThread(String url);
	
	/*
	 * 
	 * 更新线程信息
	 */
	void updateThread(String url,int thread_id ,long finished);
	
	/*
	 * 查询文件线程信息
	 * 
	 */
    List<ThreadInfo>getThread(String url);
    
    /*
     * 判断是否存在
     */
    boolean isExists(String url,int thread_id);
}
