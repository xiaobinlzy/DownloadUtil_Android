package com.dll.downloadutil.download;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private static final String STATE_FILE_NAME = "downloadstate";

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
	ObjectInputStream in = null;
	try {
	    in = new ObjectInputStream(mContext.openFileInput(STATE_FILE_NAME));
	    @SuppressWarnings("unchecked")
	    List<DownloadInfo> infos = (List<DownloadInfo>) in.readObject();
	    List<DownloadTask> tasks = new ArrayList<DownloadTask>();
	    for (DownloadInfo info : infos) {
		DownloadTask task = DownloadTask.createTaskFromConfigFile(mContext,
			info.configFile);
		if (task != null) {
		    tasks.add(task);
		}
	    }
	    restoreState(tasks);
	} catch (FileNotFoundException e) {
	} catch (IOException e) {
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    protected abstract void restoreState(List<DownloadTask> tasks);

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
    public synchronized void addDownloadTask(DownloadTask downloadTask, boolean start) {
	downloadTask.writeConfigFile();
	handleAddDownloadTask(downloadTask);
	if (start) {
	    startNextDownload();
	}
	saveState();
    }

    public synchronized void resumeAll() {
	handleResumeAll();
    }

    protected abstract void handleResumeAll();

    protected abstract void handleAddDownloadTask(DownloadTask downloadTask);

    protected synchronized boolean startNextDownload() {
	return handleStartNextDownload();
    }

    protected abstract boolean handleStartNextDownload();

    /**
     * 移除掉所有的下载任务
     */
    public synchronized void removeAll() {
	handleRemoveAll();
	saveState();
    }

    public synchronized void remove(DownloadTask downloadTask, boolean start) {
	handleRemove(downloadTask);
	saveState();
	if (start) {
	    startNextDownload();
	}
    }

    protected abstract void handleRemove(DownloadTask downloadTask);

    protected abstract void handleStopAll();

    /**
     * 停止当前的下载任务
     */
    public synchronized void stopAll() {
	handleStopAll();
    }

    protected synchronized void onStartDownload(DownloadTask downloadTask) {
	mHandler.obtainMessage(HANDLE_ON_START_DOWNLOAD, downloadTask).sendToTarget();
    }

    protected synchronized void onFailedDownload(DownloadTask downloadTask) {
	mHandler.obtainMessage(HANDLE_ON_FAILED_DOWNLOAD, downloadTask).sendToTarget();
    }

    protected synchronized void onReceiveDownloadData(DownloadTask downloadTask) {
	mHandler.obtainMessage(HANDLE_ON_RECEIVE_DOWNLOAD_DATA, downloadTask)
		.sendToTarget();
    }

    protected abstract void handleRemoveAll();

    protected synchronized void onFinishDownload(DownloadTask downloadTask) {
	handleFinish(downloadTask);
	startNextDownload();
	mHandler.obtainMessage(HANDLE_ON_FINISH_DOWNLOAD, downloadTask).sendToTarget();
	saveState();
    }

    protected abstract void handleFinish(DownloadTask donwloadTask);

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
	return true;
    }

    public synchronized void saveState() {
	List<DownloadTask> tasks = getDownloadTaskList();

	List<DownloadInfo> cfgs = new ArrayList<DownloadInfo>();
	for (DownloadTask task : tasks) {
	    DownloadInfo info = new DownloadInfo(task);
	    cfgs.add(info);
	}
	ObjectOutputStream out = null;
	try {
	    out = new ObjectOutputStream(mContext.openFileOutput(STATE_FILE_NAME,
		    Context.MODE_PRIVATE));
	    out.writeObject(cfgs);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (out != null) {
		try {
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    /**
     * 返回当前在下载队列中的配置文件路径列表
     * 
     * @return
     */
    protected abstract List<DownloadTask> getDownloadTaskList();

    public static class DownloadInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -520939353272967829L;
	public String configFile;
	public String targetURL;

	public DownloadInfo(DownloadTask task) {
	    configFile = task.getConfigFilePath();
	    targetURL = task.getTargetURL();
	}
    }
}
