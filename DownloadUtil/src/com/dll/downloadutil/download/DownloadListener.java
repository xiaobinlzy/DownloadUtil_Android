package com.dll.downloadutil.download;

/**
 * 监听下载任务的回调接口
 * 
 * @author DLL email: xiaobinlzy@163.com
 * 
 */
public interface DownloadListener {
    /**
     * 下载获得数据
     * 
     * @param downloadTask
     */
    public void onReceiveDownloadData(DownloadTask downloadTask);

    /**
     * 下载任务开始
     * 
     * @param downloadTask
     */
    public void onStartDownload(DownloadTask downloadTask);

    /**
     * 下载任务完成
     * 
     * @param downloadTask
     */
    public void onFinishDownload(DownloadTask downloadTask);

    /**
     * 下载任务失败
     * 
     * @param downloadTask
     * @param exception
     */
    public void onFailedDownload(DownloadTask downloadTask, Exception exception);
}
