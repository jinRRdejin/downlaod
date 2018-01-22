package com.jrr.download_demo;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	protected static final String TAG = "jrr";
	private TextView filename;
	private ProgressBar progressbar;	
	private Button start;
	private Button stop;
	private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        filename = (TextView) findViewById(R.id.filename);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        progressbar.setMax(100);
        
        
        //创建文件信息对象
        final FileInfo fileInfo = new FileInfo(0, "http://central.maven.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar", 
        		"commons-io-2.6.jar", 0, 0);
        filename.setText(fileInfo.getFileName());
        //添加监听事件
        start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 通过Intent传递参数给Service
				Intent intent = new Intent(MainActivity.this , DownloadService.class);
				intent.setAction(DownloadService.ACTION_START);
				Log.i("jrr","ACTION_START" );		
				intent.putExtra("fileInfo", fileInfo);
				startService(intent);
			}
		});
        
        stop.setOnClickListener(new OnClickListener() {
			
     			@Override
     			public void onClick(View v) {
     				// 通过Intent传递参数给Service
     				Intent intent = new Intent(MainActivity.this , DownloadService.class);
     				intent.setAction(DownloadService.ACTION_STOP);
     				Log.i("jrr","ACTION_STOP" );
     				intent.putExtra("fileInfo", fileInfo);
     				startService(intent);
     			}
     		});
        
        //注册接收管广播
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDAT);
        this.registerReceiver(receiver, filter);
        
        
    }
    
    //更新UI的广播接收器
    BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if(DownloadService.ACTION_UPDAT.equals(intent.getAction())){
				int finished = intent.getIntExtra("finished", 0);
				
				Log.i(TAG, finished + "");
				progressbar.setProgress(finished);
			}
			
		}
    	
    };
    
    @Override  
    protected void onDestroy(){  
        super.onDestroy();  
        this.unregisterReceiver(receiver);  
    }
    
    
    
    
    
}
