package com.example.multidownload_demo;

import java.io.Serializable;

public class FileInfo implements Serializable{

	private int id;
	private String url;
	private String fileName;
	private long length;
	private long finish;
	
	
	
	public FileInfo() {
		super();
	}
	
	
	public FileInfo(int id, String url, String fileName, int length, int finish) {
		this.id = id;
		this.url = url;
		this.fileName = fileName;
		this.length = length;
		this.finish = finish;
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public long getFinish() {
		return finish;
	}
	public void setFinish(long l) {
		this.finish = l;
	}


	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", url=" + url + ", fileName=" + fileName
				+ ", length=" + length + ", finish=" + finish + "]";
	}
	
    
}
