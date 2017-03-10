package com.example.scalephoto.http.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 *
 */
public class NetRequestUtils {
    private static final String TAG = "NetRequestUtils";

    /**
     * 生成QueryString,以 a=1&b=2形式返回
     */
    public static String map2QueryString(Map<String, String> map) {
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
