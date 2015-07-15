package com.dll.downloadutil.download;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class DownloadParallelManager extends DownloadManager {

    private LinkedList<DownloadTask> mQueue;

    private int mMaxDownloadingNumber = 3;

    private static DownloadParallelManager gManager;

    private DownloadParallelManager(Context context) {
	super(context);
	if (mQueue == null) {
	    mQueue = new LinkedList<DownloadTask>();
	}
    }

    public static DownloadParallelManager getInstance(Context context) {
	if (gManager == null) {
	    gManager = new DownloadParallelManager(context);
	}
	return gManager;
    }

    public void setMaxDownloadingNumber(int number) {
	mMaxDownloadingNumber = number;
    }

    public int getMaxDownloadingNumber() {
	return mMaxDownloadingNumber;
    }

    @Override
    public Collection<DownloadTask> getTaskCollection() {
	return mQueue;
    }

    @Override
    protected void handleAddDownloadTask(DownloadTask downloadTask) {
	downloadTask.setDownloadManager(this);
	mQueue.add(downloadTask);
    }

    @Override
    protected boolean handleStartNextDownload() {
	if (mQueue.size() > 0 && numberOfDownloading() < mMaxDownloadingNumber) {
	    DownloadTask task = nextTaskToDownload();
	    if (task != null) {
		if (task.getLoadedByteLength() > 0) {
		    Log.i("", "续传：" + task.getFileName());
		}
		task.start();
		return true;
	    }
	}
	return false;
    }

    private DownloadTask nextTaskToDownload() {
	DownloadTask result = null;
	Iterator<DownloadTask> it = mQueue.iterator();
	while (it.hasNext()) {
	    DownloadTask task = it.next();
	    if (!task.isDownloading()) {
		result = task;
		break;
	    }
	}
	return result;
    }

    private int numberOfDownloading() {
	int result = 0;
	Iterator<DownloadTask> it = mQueue.iterator();
	while (it.hasNext()) {
	    if (it.next().isDownloading()) {
		result++;
	    }
	}
	return result;
    }

    @Override
    protected void handleStopAll() {
	Iterator<DownloadTask> iterator = mQueue.iterator();
	while (iterator.hasNext()) {
	    DownloadTask task = iterator.next();
	    if (task.isDownloading()) {
		task.stop();
	    }
	}
    }

    @Override
    protected void handleRemoveAll() {
	Iterator<DownloadTask> it = mQueue.iterator();
	while (it.hasNext()) {
	    DownloadTask downloadTask = it.next();
	    if (downloadTask.isDownloading()) {
		downloadTask.stop();
	    }
	}
	mQueue.clear();
    }

    @Override
    protected void handleFinish(DownloadTask downloadTask) {
	mQueue.remove(downloadTask);
    }

    @Override
    protected void restoreState(List<DownloadTask> tasks) {
	mQueue = new LinkedList<DownloadTask>();
	for (DownloadTask task : tasks) {
	    task.setDownloadManager(this);
	    addDownloadTask(task, false);
	}
    }

    @Override
    protected List<DownloadTask> getDownloadTaskList() {
	return mQueue;
    }

    @Override
    protected void handleResumeAll() {
	for (int i = numberOfDownloading(); i < mMaxDownloadingNumber; i++) {
	    startNextDownload();
	}
    }

    @Override
    protected void handleRemove(DownloadTask downloadTask) {
	if (downloadTask.isDownloading()) {
	    downloadTask.stop();
	}
	mQueue.remove(downloadTask);
    }
}
