package com.example.multidownload_demo;

import java.util.List;

public interface FileDao {
	
	/*
	 * 提供数据访问接口
	 */
	
	/*
	 * 
	 * 插入文件信息
	 */

	void insertFile(FileInfo fileInfo);
	
	/*
	 *查询文件信息
	 */
	
	List<FileInfo>  getFile(String url);
	
	/*
	 * 
	 * 删除文件信息
	 */
	
	void deleteFile(String url);
}
