package com.example.multidownload_demo;

import java.util.List;

public interface FileDao {
	
	/*
	 * �ṩ���ݷ��ʽӿ�
	 */
	
	/*
	 * 
	 * �����ļ���Ϣ
	 */

	void insertFile(FileInfo fileInfo);
	
	/*
	 *��ѯ�ļ���Ϣ
	 */
	
	List<FileInfo>  getFile(String url);
	
	/*
	 * 
	 * ɾ���ļ���Ϣ
	 */
	
	void deleteFile(String url);
}
