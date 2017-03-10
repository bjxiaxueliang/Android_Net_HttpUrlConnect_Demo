package com.example.scalephoto.http;


import com.example.scalephoto.http.base.BaseHttp;
import com.example.scalephoto.http.config.HttpConfig;
import com.example.scalephoto.http.config.HttpMethod;
import com.example.scalephoto.http.utils.NetLogUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * create by xiaxl on 2017.02.15
 * elete
 * <p>
 * 为什么不用volley?
 * 答：volley 的Delete请求，将请求参数添加到了Url地址上，所以没有用Volley
 */
public class HttpDelete extends BaseHttp {
    private static final String TAG = "HttpDelete";


    /**
     * 同步delete请求(将请求参数放到body中)
     *
     * @param url
     * @return
     */
    public String _deleteBody(final String url, final Map<String, String> headers, final Map<String, String> params) throws Exception {
        NetLogUtils.d(TAG, "---_deleteBody---");
        NetLogUtils.d(TAG, "url: " + url);
        NetLogUtils.d(TAG, "headers: " + headers);
        NetLogUtils.d(TAG, "params: " + params);

        HttpURLConnection connection = null;
        DataOutputStream out = null;
        try {
            // HttpURLConnection
            connection = initConnection(url);
            // 添加header
            connection = addHeaders(connection, headers);

            out = new DataOutputStream(connection.getOutputStream());
            // 添加body
            out = addParams(out, params);
            out = writeEnd(out);
            out.flush();

            // 返回参数
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                // 返回数据
                return readStr(connection.getInputStream());
            } else {
                throw new Exception("HTTP ResponseCode=" + connection.getResponseCode() + " Response=" + readStr(connection.getInputStream()));
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
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
        URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        //设置连接超时时间和读取超时时间
        connection.setConnectTimeout(HttpConfig.TIME_OUT);
        connection.setReadTimeout(HttpConfig.TIME_OUT);
        //
        connection.setRequestMethod(HttpMethod.DELETE);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY_PREFIX_BYTES);
        return connection;

    }

}
