package com.example.scalephoto.http.download;

import java.util.Map;

/**
 * <p>
 * 功能 文件相关请求回调
 */
public interface FileDownLoadListener<T> {
    /**
     * 文件任务成功回调
     *
     * @param url      文件任务描述
     * @param response 请求返回(下载时为文件路径)
     */
    public void onSuccess(String url, Map<String, String> headers, String destFilePath, String response);

    /**
     * 文件任务失败回调
     *
     * @param url       文件任务描述
     * @param intercept 中断位置
     * @param e         错误
     */
    public void onFailure(String url, Map<String, String> headers, String destFilePath, long intercept, Exception e);


    /**
     * 文件任务上传or 下载过程回调
     *
     * @param url   原始请求参数
     * @param total 文件总字节大小
     * @param seek  过程中进行位置
     * @param isUp  是否上传
     */
    public void onLoad(String url, Map<String, String> headers, String destFilePath, long total, long seek, boolean isUp);
}
