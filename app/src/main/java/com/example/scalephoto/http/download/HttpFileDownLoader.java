package com.example.scalephoto.http.download;

import android.text.TextUtils;

import com.example.scalephoto.http.base.BaseHttp;
import com.example.scalephoto.http.config.HttpConfig;
import com.example.scalephoto.http.config.HttpMethod;
import com.example.scalephoto.http.utils.NetLogUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * create by xiaxl on 2017.02.15
 * <p>
 * 文件下载
 */
public class HttpFileDownLoader extends BaseHttp {
    private static final String TAG = "HttpFileDownLoader";

    /**
     * 文件流校验段大小
     */
    private static final int CHECK_SIZE = 512;


    /**
     * 发送POST请求
     */
    public void _get_file(String url, Map<String, String> headers, Map<String, String> params, String destFilePath, FileDownLoadListener mListener) {
        NetLogUtils.d(TAG, "---_get_file---");
        NetLogUtils.d(TAG, "url: " + url);
        NetLogUtils.d(TAG, "headers: " + headers);
        NetLogUtils.d(TAG, "params: " + params);
        NetLogUtils.d(TAG, "destFilePath: " + destFilePath);
        if (TextUtils.isEmpty(url)) {
            if (mListener != null) {
                mListener.onFailure(url, headers, destFilePath, 0, new Exception("url is null"));
            }
            return;
        }


        // 组合get请求url
        if (params != null && params.size() > 0) {
            url = url + "?" + map2QueryString(params);
        }
        NetLogUtils.d(TAG, "url: " + url);


        //------------------------------------------
        File file = null;
        long seek = 0;
        //------------------------------------------
        HttpURLConnection connection = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        FileOutputStream fileOut = null;
        InputStream netIn = null;

        try {

            //------------------
            file = initFile(destFilePath);
            //----断点续传
            seek = file.length();
            seek -= CHECK_SIZE;
            //seek小于校验区大小则删除重新下载
            if (seek <= 0) {
                file.delete();
                file.createNewFile();
                seek = 0;
            }
            //------------------
            // 获取 HttpURLConnection
            connection = initConnection(url);
            // ----添加header----
            connection = addHeaders(connection, headers);
            connection.addRequestProperty("Range", "bytes=" + seek + "-");
            // 状态码
            int responseCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode || HttpURLConnection.HTTP_PARTIAL == responseCode) {
                //下载文件
                netIn = connection.getInputStream();
                //
                fileOut = initFileOutputStream(file, seek, netIn);
                bis = new BufferedInputStream(netIn);
                bos = new BufferedOutputStream(fileOut);
                long total = connection.getContentLength() + file.length() - seek;
                //
                byte[] tmp = new byte[BUFFER_SIZE_DEFAULT];
                int len;
                int i = 0;
                while ((len = bis.read(tmp)) != -1) {
                    bos.write(tmp, 0, len);
                    seek += len;
                    i++;
                    //回调刷新进度
                    if (mListener != null && i % 25 == 0) {//防止调用过快
                        mListener.onLoad(url, headers, destFilePath, total, seek, false);
                    }
                }
                if (mListener != null) {
                    mListener.onLoad(url, headers, destFilePath, total, seek, false);
                    mListener.onSuccess(url, headers, destFilePath, file.getPath());
                }
                bos.flush();
            } else {
                if (mListener != null) {
                    mListener.onFailure(url, headers, destFilePath, seek, new Exception("HTTP ERROR ResponseCode=" + responseCode));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onFailure(url, headers, destFilePath, seek, e);
            }
        } finally {
            try {
                //
                closeStream(bis, bos, fileOut, netIn);
                //
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * 初始化URLConnection
     *
     * @param urlPath 请求地址
     * @return HttpURLConnection连接
     * @throws IOException
     */
    @Override
    protected HttpURLConnection initConnection(String urlPath) throws IOException {
        //
        URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置连接超时时间和读取超时时间
        connection.setConnectTimeout(HttpConfig.TIME_OUT);
        connection.setReadTimeout(HttpConfig.TIME_OUT);
        //
        connection.setRequestMethod(HttpMethod.GET);
        return connection;
    }

    //############################################################################################

    /**
     * 初始化文件
     *
     * @param filePath 文件存储路径
     * @return 文件对象
     * @throws IOException
     */
    private static File initFile(String filePath) throws IOException {
        File file = new File(filePath);
        File fileParent = new File(file.getParent());
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


    /**
     * 初始化文件输出流 校验断点续传连接处字节
     *
     * @param file  需要输出的文件
     * @param seek  校验起始位置
     * @param netIn 网络数据流
     * @return 构造好的初始文件输出流
     * @throws IOException
     */
    private FileOutputStream initFileOutputStream(File file, long seek, InputStream netIn) throws IOException {
        FileOutputStream fileOut = null;
        if (seek > 0) {
            //断点续传
            FileInputStream fileIn = new FileInputStream(file);
            byte[] fileCheckBuffer = readBytes(fileIn, seek, CHECK_SIZE);
            byte[] netCheckBuffer = readBytes(netIn, 0, CHECK_SIZE);
            boolean isCheckSuccess = Arrays.equals(netCheckBuffer, fileCheckBuffer);
            closeStream(fileIn);
            if (!isCheckSuccess) {
                //文件流校验错误
                file.delete();
                throw new IOException("Cache invalidation failure");
            }
            //文件流校验成功
            fileOut = new FileOutputStream(file, true);
        } else {
            fileOut = new FileOutputStream(file);
        }
        return fileOut;
    }

    /**
     * 读取字节
     *
     * @param in   输入流
     * @param skip 跳过字节
     * @param size 读取长度
     * @return size长度的数据
     * @throws IOException
     */
    private byte[] readBytes(InputStream in, long skip, int size) throws IOException {
        byte[] result = null;
        if (skip > 0) {
            long skipped = 0;
            while (skip > 0 && (skipped = in.skip(skip)) > 0) {
                skip -= skipped;
            }
        }
        result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) in.read();
        }
        return result;
    }

}
