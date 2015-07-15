package com.example.androiddownloader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dll.downloadutil.download.DownloadListener;
import com.dll.downloadutil.download.DownloadParallelManager;
import com.dll.downloadutil.download.DownloadTask;

public class MainActivity extends Activity implements DownloadListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DownloadParallelManager mDownloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	mDownloadManager = DownloadParallelManager.getInstance(this);
	mDownloadManager.setMaxDownloadingNumber(3);
	mDownloadManager.setDownloadManagerListener(this);
    }

    public void onClickStart(View view) {
	for (int i = 0; i < 10; i++) {
	    DownloadTask downloadTask1 = new DownloadTask(this,
		    "http://www.52mm.com/upload/cjpic/2012-11-27/f394c33b2d8d93d1cb8dcbe19d022a3a.jpg");
	    downloadTask1.setMemoryCacheSize(10);
	    downloadTask1.setFileName("photo" + (i * 3 + 1) + ".jpg");
	    mDownloadManager.addDownloadTask(downloadTask1, false);

	    DownloadTask downloadTask2 = new DownloadTask(this,
		    "http://www.52mm.com/upload/cjpic/2012-11-27/aa2ee73db6620362b174b5469f75b1a3.jpg");
	    downloadTask2.setMemoryCacheSize(10);
	    downloadTask2.setFileName("photo" + (i * 3 + 2) + ".jpg");
	    mDownloadManager.addDownloadTask(downloadTask2, false);

	    DownloadTask downloadTask3 = new DownloadTask(this,
		    "http://www.52mm.com/upload/cjpic/2012-11-27/a2ae39ce709661f77d4f5a75cbf13e51.jpg");
	    downloadTask3.setMemoryCacheSize(10);
	    downloadTask3.setFileName("photo" + (i * 3 + 3) + ".jpg");
	    mDownloadManager.addDownloadTask(downloadTask3, false);
	}
	mDownloadManager.resumeAll();
    }

    public void onClickStop(View view) {
	mDownloadManager.stopAll();
    }

    public void onClickResume(View view) {
	mDownloadManager.resumeAll();
    }

    @Override
    public void onReceiveDownloadData(DownloadTask downloadTask) {
    }

    @Override
    public void onStartDownload(DownloadTask downloadTask) {
	Log.i(TAG, "download start: " + downloadTask.getFileName());
    }

    @Override
    public void onFinishDownload(DownloadTask downloadTask) {
	Log.i(TAG, "download finish: " + downloadTask.getFileName());
    }

    @Override
    public void onFailedDownload(DownloadTask downloadTask, Exception exception) {
	Log.i(TAG, "download failed: " + downloadTask.getFileName());
    }
}
