package com.example.scalephoto.http.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

/**
 * Json解析
 */
public class NetJsonUtils {
    private static final String TAG = "NetJsonUtils";

    public static <T> T json2Obj(String json, final Class<T> cls) throws ExecutionException {
        Log.d(TAG, "---json2Obj---");
        //--------------解析数据-------------
        if (TextUtils.isEmpty(json) == false) {
            if (cls.isAssignableFrom(String.class)) {
                Log.d(TAG, "---String---");
                return (T) json;
            } else {
                Log.d(TAG, "---Gson---");
                Gson gson = new Gson();
                Object bean = gson.fromJson(json, cls);
                if (bean != null) {
                    return (T) bean;
                } else {
                    throw new ExecutionException("ParseError", new Throwable("ParseError"));
                }
            }
        } else {
            throw new ExecutionException("json is null", new Throwable("json is null"));
        }
    }

}
