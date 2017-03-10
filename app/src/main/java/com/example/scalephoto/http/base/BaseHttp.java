package com.example.scalephoto.http.base;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by xueliangxia on 2017/3/8.
 */

public abstract class BaseHttp {

    private static final String TAG = "BaseHttp";


    //############################################################################################################################
    /**
     * http块分隔符
     */
    protected static String BOUNDARY_PREFIX_BYTES = ("*****" + Long.toString(System.currentTimeMillis()) + "*****");
    /**
     * http换行符
     */
    protected static String END_BYTES = "\r\n";
    /**
     * http分隔
     */
    protected static String TWO_DASHES_BYTES = "--";
    /**
     * 编码方式
     */
    protected static String charset = "UTF-8";

    //默认缓冲区大小
    protected static final int BUFFER_SIZE_DEFAULT = 1024 * 4;


    //############################################################################################################################

    /**
     * @param urlPath
     * @return
     * @throws IOException
     */
    protected abstract HttpURLConnection initConnection(String urlPath) throws IOException;


    /**
     * 为http连接添加header
     *
     * @param connection http连接
     * @param headers    需要添加的头部信息
     * @return http连接
     */
    protected HttpURLConnection addHeaders(HttpURLConnection connection, Map<String, String> headers) {
        if (headers == null || headers.size() == 0) {
            return connection;
        }
        Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (TextUtils.isEmpty(entry.getKey()) || TextUtils.isEmpty(entry.getValue())) {
                continue;
            }
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        return connection;
    }

    /**
     * 在连接流中写入参数
     *
     * @param out    连接流
     * @param params 参数组
     * @return 连接流
     * @throws IOException
     */
    protected DataOutputStream addParams(DataOutputStream out, Map<String, String> params) throws IOException {
        if (params == null || params.size() == 0) {
            return out;
        }
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (TextUtils.isEmpty(entry.getKey()) || TextUtils.isEmpty(entry.getValue())) {
                continue;
            }
            writeString(out, entry.getKey(), entry.getValue());
        }
        return out;
    }

    /**
     * 添加要上传的文件
     *
     * @param dataOutStream
     * @param files
     * @return
     */
    protected DataOutputStream addFiles(DataOutputStream dataOutStream, Map<String, File> files) throws IOException {

        // ------向HTTP请求添加上传文件部分------
        if (files != null && files.isEmpty() == false) {
            for (Map.Entry<String, File> fileEntry : files.entrySet()) {
                //
                String fileKey = fileEntry.getKey();
                File file = fileEntry.getValue();
                String fileName = file.getName();
                //
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
                //
                InputStream inputStream = null;

                inputStream = new FileInputStream(fileEntry.getValue());
                byte[] buffer = new byte[BUFFER_SIZE_DEFAULT];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    dataOutStream.write(buffer, 0, bytesRead);
                }
                //
                inputStream.close();
                //
                dataOutStream.writeBytes(END_BYTES);
                //
                dataOutStream.flush();
                //
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        //
        dataOutStream.writeBytes(END_BYTES);
        //
        return dataOutStream;
    }


    //############################################################################################################################


    /**
     * 向连接流中写入单个参数
     *
     * @param out   连接流
     * @param key   HTTP请求参数键
     * @param value HTTP请求参数值
     * @return 连接流
     * @throws IOException
     */
    protected DataOutputStream writeString(DataOutputStream out, String key, String value) throws IOException {
        writeLine(out, TWO_DASHES_BYTES, BOUNDARY_PREFIX_BYTES);
        writeLine(out, ("Content-Disposition: form-data; name=\"" + key + "\""));
        writeLine(out, "Content-Type: text/plain");
        writeLine(out);
        writeLine(out, value);
        return out;
    }

    /**
     * 写入http结尾行
     *
     * @param out 连接流
     * @return 连接流
     * @throws IOException
     */
    protected DataOutputStream writeEnd(DataOutputStream out) throws IOException {
        writeLine(out, TWO_DASHES_BYTES, BOUNDARY_PREFIX_BYTES, TWO_DASHES_BYTES);
        return out;
    }

    /**
     * 写入单行
     *
     * @param out 连接流
     * @param str 单行内容
     * @return 连接流
     * @throws IOException
     */
    protected DataOutputStream writeLine(DataOutputStream out, String... str) throws IOException {
        if (str != null) {
            for (String s : str) {
                out.writeBytes(s);
            }
        }
        out.writeBytes(END_BYTES);
        return out;
    }


    //############################################################################################################################

    /**
     * 从流中读取数据并自动关闭
     *
     * @param in 输入流
     * @return 读取的字符串 失败时为空
     */
    protected String readStr(InputStream in) {
        try {
            return readStr(in, BaseHttp.charset);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
        }
        return null;
    }


    /**
     * 从流中读取数据并自动关闭
     *
     * @param in      输入流
     * @param charset 编码格式
     * @return 读取的字符串
     * @throws IOException
     */
    protected String readStr(InputStream in, String charset) throws IOException {
        if (TextUtils.isEmpty(charset))
            charset = BaseHttp.charset;

        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        Reader reader = new InputStreamReader(in, charset);
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        while ((len = reader.read(buf)) >= 0) {
            sb.append(buf, 0, len);
        }
        closeStream(reader);
        return sb.toString();
    }

    /**
     * 统一关闭流
     *
     * @param streams 输入输出流组
     */
    protected void closeStream(Closeable... streams) {
        for (Closeable stream : streams) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //############################################################################################################################


    /**
     * 生成QueryString,以 a=1&b=2形式返回
     */
    protected String map2QueryString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        String value;
        try {
            if (map != null && map.size() > 0) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    value = "";
                    value = entry.getValue();
                    if (TextUtils.isEmpty(value)) {
                        value = "";
                    } else {
                        value = URLEncoder.encode(value, "utf-8");
                    }
                    sb.append(entry.getKey()).append("=").append(value)
                            .append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}
