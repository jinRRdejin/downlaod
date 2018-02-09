package com.example.multidownload_demo;

public class ThreadInfo {
	
	/*
	 *线程信息
	 */
	
	private int id;
	private String url;
	private long start;
	private long end;
	private long finished;
	
	
	public ThreadInfo() {
		super();
	}
	public ThreadInfo(int id, String url, long start, long stop, long finished) {
		super();
		this.id = id;
		this.url = url;
		this.start = start;
		this.end = stop;
		this.finished = finished;
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
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long l) {
		this.end = l;
	}
	public long getFinished() {
		return finished;
	}
	public void setFinished(long finished) {
		this.finished = finished;
	}
	
	@Override
	public String toString() {
		return "ThreadInfo [id=" + id + ", url=" + url + ", start=" + start
				+ ", stop=" + end + ", finished=" + finished + "]";
	}
	
	

}
