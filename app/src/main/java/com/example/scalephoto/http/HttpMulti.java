package com.example.scalephoto.http;

import android.graphics.Bitmap;

import com.example.scalephoto.http.base.BaseHttp;
import com.example.scalephoto.http.config.HttpConfig;
import com.example.scalephoto.http.config.HttpMethod;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * create by xiaxl on 2017.02.15
 * <p>
 * 文件上传
 */
public class HttpMulti extends BaseHttp {
    private static final String TAG = "HttpMulti";


    /**
     * 上传文件
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     * @throws Exception
     */
    public String _post(String url, Map<String, String> headers, Map<String, String> params, Map<String, File> files) throws Exception {
        //
        HttpURLConnection connection = null;
        DataOutputStream dataOutStream = null;
        try {
            // 获取 HttpURLConnection
            connection = initConnection(url);
            // ----添加header----
            connection = addHeaders(connection, headers);
            // DataOutputStream
            dataOutStream = new DataOutputStream(connection.getOutputStream());
            // ------添加Post请求参数-----
            dataOutStream = addParams(dataOutStream, params);
            // ----添加要上传的文件----
            dataOutStream = addFiles(dataOutStream, files);
            // ---结尾----
            dataOutStream.writeBytes(TWO_DASHES_BYTES + BOUNDARY_PREFIX_BYTES + TWO_DASHES_BYTES);
            dataOutStream.writeBytes(END_BYTES);
            dataOutStream.close();
            // ---------获取HTTP响应----------
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP ResponseCode=" + status);
            }
            // ------从流中获取数据------
            return readStr(connection.getInputStream());
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
                //
                if (dataOutStream != null) {
                    dataOutStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
    public String _post(String url, Map<String, String> headers, Map<String, String> params, String fileKey, File file, long seekStart, long seekEnd) throws Exception {
        //
        HttpURLConnection connection = null;
        DataOutputStream dataOutStream = null;
        try {
            // 获取 HttpURLConnection
            connection = initConnection(url);
            // ----添加header----
            connection = addHeaders(connection, headers);
            // DataOutputStream
            dataOutStream = new DataOutputStream(connection.getOutputStream());
            // ------添加Post请求参数-----
            dataOutStream = addParams(dataOutStream, params);
            // ----添加要上传的文件----
            dataOutStream = writeFile(dataOutStream, fileKey, file, seekStart, seekEnd);

            // ---结尾----
            dataOutStream.writeBytes(TWO_DASHES_BYTES + BOUNDARY_PREFIX_BYTES + TWO_DASHES_BYTES);
            dataOutStream.writeBytes(END_BYTES);
            dataOutStream.close();
            // ---------获取HTTP响应----------
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP ResponseCode=" + status);
            }
            // ------从流中获取数据------
            return readStr(connection.getInputStream());
        } finally {
            try {
                //
                if (connection != null) {
                    connection.disconnect();
                }
                //
                if (dataOutStream != null) {
                    dataOutStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
    public String _post(String url, Map<String, String> headers, Map<String, String> params, String fileKey, Bitmap bitmap) throws Exception {
        //
        HttpURLConnection connection = null;
        DataOutputStream dataOutStream = null;
        try {
            // 获取 HttpURLConnection
            connection = initConnection(url);
            // ----添加header----
            connection = addHeaders(connection, headers);
            // DataOutputStream
            dataOutStream = new DataOutputStream(connection.getOutputStream());
            // ------添加Post请求参数-----
            dataOutStream = addParams(dataOutStream, params);
            // ----添加要上传的文件----
            dataOutStream = writeBitmap(dataOutStream, fileKey, bitmap);

            // ---结尾----
            dataOutStream.writeBytes(TWO_DASHES_BYTES + BOUNDARY_PREFIX_BYTES + TWO_DASHES_BYTES);
            dataOutStream.writeBytes(END_BYTES);
            dataOutStream.close();
            // ---------获取HTTP响应----------
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP ResponseCode=" + status);
            }
            // ------从流中获取数据------
            return readStr(connection.getInputStream());
        } finally {
            try {
                //
                if (connection != null) {
                    connection.disconnect();
                }
                //
                if (dataOutStream != null) {
                    dataOutStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        //设置连接超时时间和读取超时时间
        connection.setConnectTimeout(HttpConfig.TIME_OUT);
        connection.setReadTimeout(HttpConfig.TIME_OUT);
        //
        connection.setRequestMethod(HttpMethod.POST);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY_PREFIX_BYTES);

        return connection;
    }

    //####################################################################################################

    /**
     * 向连接流中写入文件
     *
     * @param dataOutStream 连接流
     * @param fileKey       文件上传健
     * @param file          需要上传的文件
     * @param seekStart     上传起始位置
     * @param seekEnd       上传结束为止
     * @return 连接流
     * @throws IOException
     */
    private DataOutputStream writeFile(DataOutputStream dataOutStream, String fileKey, File file, long seekStart, long seekEnd) throws IOException {
        if (file == null) {
            return dataOutStream;
        }
        // seekEnd
        if (seekEnd == 0) {
            seekEnd = file.length();
        }
        // fileName
        String fileName = file.getName();
        //----------------------------------------------------------------------------
        dataOutStream.writeBytes(TWO_DASHES_BYTES + BOUNDARY_PREFIX_BYTES);
        dataOutStream.writeBytes(END_BYTES);
        dataOutStream.writeBytes("Content-Disposition: form-data; name=\"" + fileKey + "\"; filename=\"" + fileName + "\"");
        dataOutStream.writeBytes(END_BYTES);
        dataOutStream.writeBytes("Content-Type: multipart/form-data");
        dataOutStream.writeBytes(END_BYTES);
        dataOutStream.writeBytes("Content-Transfer-Encoding: binary");
        dataOutStream.writeBytes(END_BYTES);
        //
        dataOutStream.writeBytes(END_BYTES);


        long nowSeek = seekStart;
        FileInputStream in = new FileInputStream(file);
        if (seekStart > 0) {
            long amt = in.skip(seekStart);
            if (amt == -1) {
                nowSeek = 0;
            }
        }
        int len;
        byte[] buf = new byte[BUFFER_SIZE_DEFAULT];
        while ((len = in.read(buf)) >= 0 && nowSeek < seekEnd) {
            dataOutStream.write(buf, 0, len);
            nowSeek += len;
            if (nowSeek + BUFFER_SIZE_DEFAULT > seekEnd) {
                buf = new byte[Integer.valueOf((seekEnd - nowSeek) + "")];
            }
//            if (mListener != null) {
//                mListener.onLoad(mFileParams, seekEnd - seekStart, nowSeek, true);
//            }
        }
        //
        dataOutStream.writeBytes(END_BYTES);
        //
        if (in != null) {
            in.close();
        }
        return dataOutStream;
    }


    /**
     * 向连接流中写入bitmap
     *
     * @param out    连接流
     * @param key    文件上传健
     * @param bitmap bitmap数据
     * @return 连接流
     * @throws IOException
     */
    protected DataOutputStream writeBitmap(DataOutputStream out, String key, Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return out;
        }
        writeLine(out, TWO_DASHES_BYTES, BOUNDARY_PREFIX_BYTES);
        writeLine(out, ("Content-Disposition: form-data; name=\"" + key + "\"" + "; filename=\"" + key + ".png" + "\""));
        writeLine(out, "Content-Type: multipart/form-data");
        writeLine(out);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        writeLine(out);
        return out;
    }

}
