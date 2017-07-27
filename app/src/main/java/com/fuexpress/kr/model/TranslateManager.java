package com.fuexpress.kr.model;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.fuexpress.kr.bean.TraResultBean;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by andy on 2017/1/19.
 */
public class TranslateManager {

    private static TranslateManager mInstance = new TranslateManager();

    //    http://d.yiss.com/batchTranslate?count=2&from=&text0=&text1=%ED%95%91%ED%81%AC3%EC%9E%A5%20%EC%B6%9C%EA%B3%A0&to=zh_CN
    String BASE_URL = "http://d.yiss.com";
    String Action = "batchTranslate";
    OkHttpClient mClient;

    public interface iTranslateListener {
        void onResult(boolean success, Map<String, String> result, String msg);
    }


    public static TranslateManager getInstance() {
        return mInstance;
    }

    private TranslateManager() {
    }

    public void translate(List<String> texts, iTranslateListener listener) {
        mClient = new OkHttpClient();
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("to", AccountManager.getInstance().getLocaleCode());
        if (texts.size() > 0 && !TextUtils.isEmpty(texts.get(0))) {
            params.put("text0", texts.get(0));
            params.put("count", "1");
        }
        if (texts.size() > 1 && !TextUtils.isEmpty(texts.get(1))) {
            params.put("text1", texts.get(1));
            params.put("count", "2");
        }
        requestGet(Action, params, listener);
    }


    private void requestGet(String actionUrl, final ArrayMap<String, String> paramsMap, final iTranslateListener listener) {
        StringBuilder tempParams = new StringBuilder();
        try {
            //处理参数
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                //对参数进行URLEncoder
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址
            String requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl, tempParams.toString());
            //创建一个请求
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(requestUrl).build();
            //创建一个Call
            final Call call = client.newCall(request);
            //执行请求

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    listener.onResult(false, null, "format fail");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String string = response.body().string();
                    if (!TextUtils.isEmpty(string)) {
                        Gson gson = new Gson();
                        TraResultBean traResultBean = gson.fromJson(string, TraResultBean.class);
                        if ("exception".contains(traResultBean.getFlag())) {
                            listener.onResult(false, null, "exception");
                            return;
                        }
                        ArrayMap<String, String> resultMap = new ArrayMap<>();
                        TraResultBean.DataBean data = traResultBean.getData();
                        if (data != null && !TextUtils.isEmpty(data.getText0())) {
                            resultMap.put(paramsMap.get("text0"), data.getText0());
                        }

                        if (data != null && !TextUtils.isEmpty(data.getText1())) {
                            resultMap.put(paramsMap.get("text1"), data.getText1());
                        }
                        listener.onResult(true, resultMap, "sucess");
                    } else {
                        listener.onResult(false, null, "format fail");
                    }
                }
            });

        } catch (Exception e) {
            listener.onResult(false, null, "fail");
        }
    }

}
