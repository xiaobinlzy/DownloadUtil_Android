package com.dll.downloadutil.download;

import java.util.Collection;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

/**
 * 管理下载任务的抽象类
 * 
 * @author DLL email: xiaobinlzy@163.com
 * 
 */
public abstract class DownloadManager implements Callback {

    @SuppressWarnings("unused")
    private static final String TAG = DownloadManager.class.getSimpleName();

    private DownloadListener mListener;
    protected Context mContext;
    protected Handler mHandler = new Handler(Looper.getMainLooper(), this);

    protected static final int HANDLE_ON_START_DOWNLOAD = 1;
    protected static final int HANDLE_ON_FAILED_DOWNLOAD = 2;
    protected static final int HANDLE_ON_FINISH_DOWNLOAD = 3;
    protected static final int HANDLE_ON_RECEIVE_DOWNLOAD_DATA = 4;

    /**
     * 设置下载事件监听接口
     * 
     * @param listener
     */
    public void setDownloadManagerListener(DownloadListener listener) {
	this.mListener = listener;
    }

    protected DownloadManager(Context context) {
	mContext = context.getApplicationContext();
    }

    /**
     * 获取当前的下载任务集合
     * 
     * @return
     */
    public abstract Collection<DownloadTask> getTaskCollection();

    /**
     * 添加新的任务
     * 
     * @param downloadTask
     */
    public synchronized void addDownloadTask(DownloadTask downloadTask) {
	handleAddDownloadTask(downloadTask);
	startNextDownload();
    }

    protected abstract void handleAddDownloadTask(DownloadTask downloadTask);

    protected synchronized void writeFile() {
	handleWriteFile();
    }

    protected abstract void handleWriteFile();

    protected void finalize() throws Throwable {
	writeFile();
	super.finalize();
    };

    protected synchronized boolean startNextDownload() {
	return handleStartNextDownload();
    }

    protected abstract boolean handleStartNextDownload();

    /**
     * 继续开始执行下载任务
     * 
     * @return
     */
    public synchronized boolean resume() {
	return handleResume();
    }

    protected abstract boolean handleResume();

    /**
     * 移除掉所有的下载任务
     */
    public synchronized void removeAll() {
	handleRemoveAll();
    }

    protected abstract void handleStop();

    /**
     * 停止当前的下载任务
     */
    public synchronized void stop() {
	handleStop();
    }

    protected void onStartDownload(DownloadTask downloadTask) {
	mHandler.obtainMessage(HANDLE_ON_START_DOWNLOAD, downloadTask).sendToTarget();
    }

    protected void onFailedDownload(DownloadTask downloadTask) {
	mHandler.obtainMessage(HANDLE_ON_FAILED_DOWNLOAD, downloadTask).sendToTarget();
	writeFile();
    }

    protected void onReceiveDownloadData(DownloadTask downloadTask) {
	writeFile();
	mHandler.obtainMessage(HANDLE_ON_RECEIVE_DOWNLOAD_DATA, downloadTask).sendToTarget();
    }

    protected abstract void handleRemoveAll();

    protected void onFinishDownload(DownloadTask downloadTask) {
	writeFile();
	removeDownloadTask(downloadTask);
	startNextDownload();
	mHandler.obtainMessage(HANDLE_ON_FINISH_DOWNLOAD, downloadTask).sendToTarget();
    }

    protected abstract void removeDownloadTask(DownloadTask donwloadTask);

    @Override
    public boolean handleMessage(Message msg) {
	if (mListener != null) {
	    DownloadTask downloadTask = (DownloadTask) msg.obj;
	    switch (msg.what) {
	    case HANDLE_ON_FAILED_DOWNLOAD:
		mListener.onFailedDownload(downloadTask, downloadTask.getException());
		break;
	    case HANDLE_ON_FINISH_DOWNLOAD:
		mListener.onFinishDownload(downloadTask);
		break;
	    case HANDLE_ON_RECEIVE_DOWNLOAD_DATA:
		mListener.onReceiveDownloadData(downloadTask);
		break;
	    case HANDLE_ON_START_DOWNLOAD:
		mListener.onStartDownload(downloadTask);
		break;
	    default:
		break;
	    }
	}
	if (msg.what == HANDLE_ON_FINISH_DOWNLOAD) {
	    startNextDownload();
	}
	return true;
    }
}
