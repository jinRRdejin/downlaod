package com.example.multidownload_demo;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter{
	
	private Context context;
	private List<FileInfo>fileList;
	private LayoutInflater inflater;
	
	
	

	public FileListAdapter(Context context, List<FileInfo> fileList) {
		super();
		this.context = context;
		this.fileList = fileList;
		this.inflater =  LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fileList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return fileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	/*
	 * 不仅利用了ListView的缓存，更通过ViewHolder类来实现
	 * 显示数据视图的缓存
	 * 避免多次通过findViewById寻找控件
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 final FileInfo fileInfo = fileList.get(position);  //通过list列表获取相关文件
		 final ViewHolder holder;
		 
		 if(convertView == null){
			 holder = new ViewHolder();//避免重复查找ID
			 
			 convertView = inflater.inflate(R.layout.listitem_down, null);
			 holder.tvfile = (TextView) convertView.findViewById(R.id.tv_fileName);
			 holder.protext = (TextView) convertView.findViewById(R.id.tv_progress);
			 holder.btStart = (Button) convertView.findViewById(R.id.bt_start);
			 holder.btStop = (Button) convertView.findViewById(R.id.bt_stop);
			 holder.progressBar = (ProgressBar) convertView.findViewById(R.id.pb_progress);
			 
			 
			 holder.tvfile.setText(fileInfo.getFileName());
			 holder.progressBar.setMax(100);
			 
			 holder.btStart.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder.protext.setVisibility(View.VISIBLE);
					Intent intent = new Intent(context, DownloadService.class);
					intent.setAction(DownloadService.ACTION_START);
					intent.putExtra("fileInfo", fileInfo);
					 context.startService(intent);
				}
			});
			 
			 holder.btStop.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						holder.protext.setVisibility(View.VISIBLE);
						Intent intent = new Intent(context, DownloadService.class);
						intent.setAction(DownloadService.ACTION_STOP);
						intent.putExtra("fileInfo", fileInfo);
						 context.startService(intent);
					}
				});
			 
			 convertView.setTag(holder);//通过setTag方法可以快速找到相应的id
			 
		 }else{
			 holder = (ViewHolder) convertView.getTag(); 
		 }
		 
		 int pro = (int) fileInfo.getFinish();
		 
		 holder.progressBar.setProgress(pro);
		 StringBuffer progress = new StringBuffer().append(pro).append("%");
		 
		
         holder.protext.setText(progress);
		return convertView;
	}
	
	/*
	 * 
	 * 更新列表中的进度条
	 */
	public void updateProgress(int id,Long progress){
		
		 FileInfo fileInfo = fileList.get(id);
	        fileInfo.setFinish(progress);
	        notifyDataSetChanged();
	}
	
	class ViewHolder{
		
		TextView tvfile;
		TextView protext;
		Button btStart;
		Button btStop;
		ProgressBar progressBar;
		
		
	}
	
	

}
