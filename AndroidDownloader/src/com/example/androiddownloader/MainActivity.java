package com.example.androiddownloader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dll.downloadutil.download.DownloadListener;
import com.dll.downloadutil.download.DownloadManager;
import com.dll.downloadutil.download.DownloadQueueManager;
import com.dll.downloadutil.download.DownloadTask;

public class MainActivity extends Activity implements DownloadListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private long mStartTime;
    private DownloadManager mDownloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	mDownloadManager = DownloadQueueManager.getInstance(this);
	mDownloadManager.setDownloadManagerListener(this);
    }

    public void onClickStart(View view) {

	DownloadTask downloadTask1 = new DownloadTask(this,
		"http://www.52mm.com/upload/cjpic/2012-11-27/f394c33b2d8d93d1cb8dcbe19d022a3a.jpg");
	downloadTask1.setDownloadRateLimit(5);
	downloadTask1.setMemoryCacheSize(100);
	mDownloadManager.addDownloadTask(downloadTask1);

	DownloadTask downloadTask2 = new DownloadTask(this,
		"http://www.52mm.com/upload/cjpic/2012-11-27/aa2ee73db6620362b174b5469f75b1a3.jpg");
	downloadTask2.setDownloadRateLimit(5);
	downloadTask2.setMemoryCacheSize(100);
	mDownloadManager.addDownloadTask(downloadTask2);

	DownloadTask downloadTask3 = new DownloadTask(this,
		"http://www.52mm.com/upload/cjpic/2012-11-27/a2ae39ce709661f77d4f5a75cbf13e51.jpg");
	downloadTask3.setDownloadRateLimit(5);
	downloadTask3.setMemoryCacheSize(100);
	mDownloadManager.addDownloadTask(downloadTask3);
    }

    public void onClickStop(View view) {
	mDownloadManager.stop();
    }

    public void onClickResume(View view) {
	mDownloadManager.resume();
    }

    public void onClickCancel(View view) {
	mDownloadManager.removeAll();
    }

    @Override
    public void onReceiveDownloadData(DownloadTask downloadTask) {
	Log.i(TAG, "download receive data: " + downloadTask.getProgress() * 100 + "% still "
		+ (downloadTask.getTotalByteLength() - downloadTask.getLoadedByteLength()));
    }

    @Override
    public void onStartDownload(DownloadTask downloadTask) {
	Log.i(TAG, "download start: " + downloadTask.getTargetURL());
	mStartTime = System.currentTimeMillis();
    }

    @Override
    public void onFinishDownload(DownloadTask downloadTask) {
	Log.i(TAG,
		"download finish! average download rate is "
			+ (downloadTask.getTotalByteLength() * 1000 / (System.currentTimeMillis() - mStartTime))
			+ " bps");
    }

    @Override
    public void onFailedDownload(DownloadTask downloadTask, Exception exception) {
	Log.i(TAG, "download exception :");
	exception.printStackTrace();
    }
}
