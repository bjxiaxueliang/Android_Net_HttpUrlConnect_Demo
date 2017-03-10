package com.example.scalephoto;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.example.scalephoto.http.HttpDelete;
import com.example.scalephoto.http.HttpLongPoll;
import com.example.scalephoto.http.HttpMulti;
import com.example.scalephoto.http.download.FileDownLoadListener;
import com.example.scalephoto.http.download.HttpFileDownLoader;
import com.example.scalephoto.http.utils.NetJsonUtils;
import com.example.scalephoto.http.utils.NetLogUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by xueliangxia on 2017/3/8.
 */
public class HttpAgent {

    private static final String TAG = "HttpAgent";


    /**
     * 私信长轮询
     *
     * @param url
     * @param params
     * @param headers
     */
    public static <T> T get_Sync(String url, Map<String, String> headers, Map<String, String> params, final Class<T> cls) throws InterruptedException, ExecutionException {
        NetLogUtils.d(TAG, "---get_Async---");
        NetLogUtils.d(TAG, "url: " + url);
        NetLogUtils.d(TAG, "headers: " + headers);
        NetLogUtils.d(TAG, "params: " + params);
        if (TextUtils.isEmpty(url)) {
            throw new InterruptedException("url is null");
        }

        String response = "";
        try {
            //--------------同步耗时请求-------------
            response = new HttpLongPoll()._getLongPoll(url, headers, params);
            NetLogUtils.d(TAG, "response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterruptedException(e.getMessage());
        }
        //
        return NetJsonUtils.json2Obj(response, cls);
    }


    /**
     * 私信的文件下载（校验最后512字节）
     */
    public static void downLoadFile_Sync(String url, Map<String, String> headers, Map<String, String> params, String destFilePath, FileDownLoadListener mListener) {
        NetLogUtils.d(TAG, "---downLoadFile_Sync---");
        NetLogUtils.d(TAG, "url: " + url);
        NetLogUtils.d(TAG, "headers: " + headers);
        NetLogUtils.d(TAG, "params: " + params);
        NetLogUtils.d(TAG, "destFilePath: " + destFilePath);
        //-----------------
        HttpFileDownLoader downLoader = new HttpFileDownLoader();
        //
        downLoader._get_file(url, headers, params, destFilePath, mListener);
    }


    /**
     * 文件上传
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param cls
     * @param <T>
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static <T> T uploadFile_Sync(String url, Map<String, String> headers, Map<String, String> params, Map<String, File> files, final Class<T> cls) throws InterruptedException, ExecutionException {
        NetLogUtils.d(TAG, "---uploadFile_Sync---");
        NetLogUtils.d(TAG, "url: " + url);
        NetLogUtils.d(TAG, "headers: " + headers);
        NetLogUtils.d(TAG, "params: " + params);
        NetLogUtils.d(TAG, "files: " + files);
        if (TextUtils.isEmpty(url)) {
            throw new InterruptedException("url is null");
        }

        String response = "";
        try {
            //--------------同步耗时请求-------------
            response = new HttpMulti()._post(url, headers, params, files);// this will block
            NetLogUtils.d(TAG, "response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterruptedException(e.getMessage());
        }
        //
        return NetJsonUtils.json2Obj(response, cls);
    }


    /**
     * 分块上传
     *
     * @param url
     * @param headers
     * @param params
     * @param file
     * @param seekStart
     * @param seekEnd
     * @param fileKey
     * @return
     * @throws Exception
     */
    public static <T> T uploadFile_Sync(String url, Map<String, String> headers, Map<String, String> params, String fileKey, File file, long seekStart, long seekEnd, final Class<T> cls) throws InterruptedException, ExecutionException {
        NetLogUtils.d(TAG, "---uploadFile_Sync---");
        NetLogUtils.d(TAG, "url: " + url);
        NetLogUtils.d(TAG, "headers: " + headers);
        NetLogUtils.d(TAG, "params: " + params);
        NetLogUtils.d(TAG, "file: " + file);
        NetLogUtils.d(TAG, "seekStart: " + seekStart);
        NetLogUtils.d(TAG, "seekEnd: " + seekEnd);
        NetLogUtils.d(TAG, "fileKey: " + fileKey);
        if (TextUtils.isEmpty(url)) {
            throw new InterruptedException("url is null");
        }

        String response = "";
        try {
            //--------------同步耗时请求-------------
            response = new HttpMulti()._post(url, headers, params, fileKey, file, seekStart, seekEnd);
            NetLogUtils.d(TAG, "response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterruptedException(e.getMessage());
        }
        //
        return NetJsonUtils.json2Obj(response, cls);
    }

    /**
     * 分块上传
     *
     * @param url
     * @param headers
     * @param params
     * @param fileKey
     * @return
     * @throws Exception
     */
    public static <T> T uploadBitmap_Sync(String url, Map<String, String> headers, Map<String, String> params, String fileKey, Bitmap bitmap, final Class<T> cls) throws InterruptedException, ExecutionException {
        NetLogUtils.d(TAG, "---uploadFile_Sync---");
        NetLogUtils.d(TAG, "url: " + url);
        NetLogUtils.d(TAG, "headers: " + headers);
        NetLogUtils.d(TAG, "params: " + params);
        NetLogUtils.d(TAG, "fileKey: " + fileKey);

        if (TextUtils.isEmpty(url)) {
            throw new InterruptedException("url is null");
        }

        String response = "";
        try {
            //--------------同步耗时请求-------------
            response = new HttpMulti()._post(url, headers, params, fileKey, bitmap);
            NetLogUtils.d(TAG, "response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterruptedException(e.getMessage());
        }
        //
        return NetJsonUtils.json2Obj(response, cls);
    }


    /**
     * Http Delete 将参数放到消息体中
     *
     * @param url
     * @param headers
     * @m params
     */
    public static <T> T deleteBody_Sync(String url, Map<String, String> headers, Map<String, String> params, final Class<T> cls) throws InterruptedException, ExecutionException {
        NetLogUtils.d(TAG, "---deleteBody_Sync---");
        NetLogUtils.d(TAG, "url: " + url);
        NetLogUtils.d(TAG, "headers: " + headers);
        NetLogUtils.d(TAG, "params: " + params);

        if (TextUtils.isEmpty(url)) {
            throw new InterruptedException("url is null");
        }

        String response = "";
        try {
            //--------------同步耗时请求-------------
            response = new HttpDelete()._deleteBody(url, headers, params);
            NetLogUtils.d(TAG, "response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterruptedException(e.getMessage());
        }
        //
        return NetJsonUtils.json2Obj(response, cls);
    }


}
