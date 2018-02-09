package com.example.multidownload_demo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener{
    
	
	private Button btStart;
	private Button btStop;
	public Context context;
	private ListView lvFile;
	private List<FileInfo> fileInfoList;
	private FileInfo fileInfo;
	private FileListAdapter mAdapter;
	private FileDaoImpl mFileDAO;
	
	 public static final String[] videoPaths = {"http://pic29.photophoto.cn/20131204/0034034499213463_b.jpg",
		 "http://central.maven.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar",
		 "http://pic32.photophoto.cn/20140711/0011024086081224_b.jpg"};
	private static final String TAG = "jrr";

	//         "http://pic29.photophoto.cn/20131204/0034034499213463_b.jpg",
    //    "http://pic32.photophoto.cn/20140711/0011024086081224_b.jpg"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        context = this;
        init();
        initRegister();
        mockData();
    
    }
    
    

    private void mockData() {
        for (int i = 0; i < videoPaths.length; i++) {
            String videopath = videoPaths[i];
            String[] fn = videoPaths[i].split("/");
            String FileName = fn[fn.length -1];

            List<FileInfo> fileInfos = mFileDAO.getFile(videopath);
            if (fileInfos.size() == 0) {
                //创建文件信息对象
            	
                fileInfo = new FileInfo(i, videopath,  FileName, 0, 0);
                fileInfoList.add(fileInfo);
            } else {
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/" + fileInfos.get(0).getFileName());
                if (!file.exists()) {
                    mFileDAO.deleteFile(videopath);
                    fileInfo = new FileInfo(i, videopath,  FileName, 0, 0);
                    fileInfoList.add(fileInfo);
                }
            }
        }

        //创建适配器
        mAdapter = new FileListAdapter(MainActivity.this, fileInfoList);
        //给listView设置适配器
        lvFile.setAdapter(mAdapter);

//        if (fileInfoList.size() > 0) {
//            startDown();
//        }
    }
    
    
    
    
    public void init(){
    	btStart = (Button) findViewById(R.id.bt_start);
    	btStart.setOnClickListener(this);
    	btStop = (Button) findViewById(R.id.bt_stop);
    	btStart.setOnClickListener(this);
    	
    	lvFile =  (ListView) findViewById(R.id.lv_file);
    	
    	//创建文件信息集合
    	fileInfoList = new ArrayList<FileInfo>();
    	mFileDAO = FileDaoImpl.getInstance(context);
    }
     
 
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_start:
			startDown();
			break;
        case R.id.bt_stop:
        	stopDown();
			break;

		default:
			break;
		}
		
		
	}
	/*
	 * 注册广播接收器
	 */
	private void initRegister(){
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadService.ACTION_UPDATE);
		filter.addAction(DownloadService.ACTION_FINISHED);
		
		registerReceiver(mReceiver, filter);
	}
	/*
	 * 
	 * 更新UI的广播接收器
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(DownloadService.ACTION_UPDATE.equals(intent.getAction())){
				//更新进度条
				long finish = intent.getIntExtra("finished", 0);
				
				
				Log.i(TAG, "MainActivity finish = " + finish);
				int id = intent.getIntExtra("id", 0);
				mAdapter.updateProgress(id, finish);
			}else if(DownloadService.ACTION_FINISHED.equals(intent.getAction())){
				
				FileInfo fileinfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                //更新进度为100
				long  p = 100;
                mAdapter.updateProgress(fileinfo.getId(), p);
			}
			
		}
	};
	
	  @Override
	    protected void onResume() {
	        super.onResume();
	        Log.i("lsd", "onResume");
	    }

	    @Override
	    protected void onStop() {
	        super.onStop();
	        Log.i("lsd", "onStop");
	    }

	 
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
	};
	
	
	
	 private void startDown() {

	        for (FileInfo fileInfo : fileInfoList) {
	        	Log.i(TAG, "startDown");
	            Intent intent = new Intent(context, DownloadService.class);
	            intent.setAction(DownloadService.ACTION_START);
	            intent.putExtra("fileInfo", fileInfo);
	            context.startService(intent);
	        }
	    }

	    private void stopDown() {
	        for (FileInfo fileInfo : fileInfoList) {
	        	Log.i(TAG, "startDown");
	            Intent intent = new Intent(context, DownloadService.class);
	            intent.setAction(DownloadService.ACTION_STOP);
	            intent.putExtra("fileInfo", fileInfo);
	            context.startService(intent);
	        }
	    }

}
