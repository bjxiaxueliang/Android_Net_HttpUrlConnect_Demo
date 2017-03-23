package com.example.scalephoto.http;

import com.example.scalephoto.http.base.BaseHttp;
import com.example.scalephoto.http.config.HttpConfig;
import com.example.scalephoto.http.config.HttpMethod;
import com.example.scalephoto.http.utils.NetLogUtils;
import com.example.scalephoto.http.utils.NetRequestUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * create by xiaxl on 2017.02.15
 * <p>
 */
public class HttpGet extends BaseHttp {
    private static final String TAG = "HttpGet";


    /**
     * 同步get请求
     *
     * @return
     */
    public String _get(String url, final Map<String, String> headers, Map<String, String> params) throws Exception {
        NetLogUtils.d(TAG, "---_getLongPoll---");
        NetLogUtils.d(TAG, "url: " + url);
        NetLogUtils.d(TAG, "headers: " + headers);
        NetLogUtils.d(TAG, "params: " + params);
        // 组合get请求url
        if (params != null && params.size() > 0) {
            url = url + "?" + NetRequestUtils.map2QueryString(params);
        }
        NetLogUtils.d(TAG, "get url: " + url);
        //
        HttpURLConnection connection = null;
        try {
            //
            connection = initConnection(url);
            // header
            connection = addHeaders(connection, headers);
            // 返回参数
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                // 返回数据
                return readStr(connection.getInputStream());
            } else {
                throw new Exception("HTTP ERROR ResponseCode=" + connection.getResponseCode() + " Response=" + readStr(connection.getInputStream()));
            }
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
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
        // HttpURLConnection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置连接超时时间和读取超时时间
        connection.setConnectTimeout(HttpConfig.TIME_OUT);
        connection.setReadTimeout(HttpConfig.TIME_OUT);
        //
        connection.setRequestMethod(HttpMethod.GET);
        connection.setUseCaches(false);

        return connection;

    }

}
