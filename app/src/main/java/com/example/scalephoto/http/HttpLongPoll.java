package com.example.scalephoto.http;


import com.example.scalephoto.http.base.BaseHttp;
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
 * 为什么不用volley?
 * 答：长轮询http请求不设置超时时间，如果用volley，挂起一个请求线程（总共四个网络请求线程）。可能会影响其他网络请求的请求效率
 */
public class HttpLongPoll extends BaseHttp {
    private static final String TAG = "HttpLongPoll";


    /**
     * 同步get请求 长轮询
     *
     * @return
     */
    public String _getLongPoll(String url, final Map<String, String> headers, Map<String, String> params) throws Exception {
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
            // TODO 故意不设置超时时间
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
        connection.setRequestMethod(HttpMethod.GET);
        connection.setUseCaches(false);
        // TODO 故意不设置超时时间

        return connection;

    }

}
