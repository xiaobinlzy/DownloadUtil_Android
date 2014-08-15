package com.dll.downloadutil.download;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * 以队列的形式管理下载任务，使用{@link #getInstance(Context)}获得单例对象。
 * @author DLL    email: xiaobinlzy@163.com
 *
 */
public class DownloadQueueManager extends DownloadManager {

    private static DownloadQueueManager gDownloadQueueManager;
    private static final String FILE_NAME = "DownloadUtil";
    private static final String FIELD_NAME = "downloads";
    private Queue<DownloadTask> mdownloadTasks;

    protected DownloadQueueManager(Context context) {
	super(context);
    }
    
    
    /**
     * 获取{@link DownloadQueueManager}实例对象。
     * @param context
     * @return
     */
    public static DownloadQueueManager getInstance(Context context) {
	if (gDownloadQueueManager == null) {
	    gDownloadQueueManager = new DownloadQueueManager(context.getApplicationContext());
	}
	return gDownloadQueueManager;
    }

    @Override
    protected void handleStop() {
	if (mdownloadTasks != null && mdownloadTasks.size() > 0) {
	    mdownloadTasks.peek().stop();
	}
    }

    @Override
    protected synchronized void writeFile() {
	JSONArray jsonArray = new JSONArray();
	if (mdownloadTasks != null) {
	    for (Iterator<DownloadTask> iterator = mdownloadTasks.iterator(); iterator.hasNext();) {
		DownloadTask downloadTask = iterator.next();
		jsonArray.put(downloadTask.getJsonObject());
	    }
	}
	mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit()
		.putString(FIELD_NAME, jsonArray.toString()).commit();
    }
    
    @Override
    protected void removeDownloadTask(DownloadTask donwloadTask) {
	mdownloadTasks.remove(donwloadTask);
    }

    @Override
    public Queue<DownloadTask> getTaskCollection() {
	return mdownloadTasks;
    }

    @Override
    protected void handleAddDownloadTask(DownloadTask downloadTask) {
	if (mdownloadTasks == null) {
	    mdownloadTasks = new LinkedList<DownloadTask>();
	}
	downloadTask.setDownloadManager(this);
	mdownloadTasks.add(downloadTask);
    }

    @Override
    protected void handleWriteFile() {
	JSONArray jsonArray = new JSONArray();
	if (mdownloadTasks != null) {
	    for (Iterator<DownloadTask> iterator = mdownloadTasks.iterator(); iterator.hasNext();) {
		DownloadTask downloadTask = iterator.next();
		jsonArray.put(downloadTask.getJsonObject());
	    }
	}
	mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit()
		.putString(FIELD_NAME, jsonArray.toString()).commit();
    }

    @Override
    protected boolean handleStartNextDownload() {
	if (mdownloadTasks == null || mdownloadTasks.size() == 0) {
	    return false;
	}
	DownloadTask header = mdownloadTasks.peek();
	if (header != null && !header.isDownloading()) {
	    header.start();
	    return true;
	}
	return false;
    }

    @Override
    protected boolean handleResume() {
	if (mdownloadTasks != null && mdownloadTasks.size() > 0) {
	    startNextDownload();
	    return true;
	} else {
	    String json = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString(
		    FIELD_NAME, null);
	    if (json != null) {
		try {
		    JSONArray jsonArray = new JSONArray(json);
		    for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			addDownloadTask(new DownloadTask(mContext, jsonObject));
		    }
		    if (jsonArray.length() > 0) {
			return true;
		    }
		} catch (JSONException e) {
		    e.printStackTrace();
		}
	    }
	    return false;
	}
    }

    @Override
    protected void handleRemoveAll() {
	if (mdownloadTasks != null && mdownloadTasks.size() > 0) {
	    mdownloadTasks.peek().stop();
	    mdownloadTasks.clear();
	}
    }
}
