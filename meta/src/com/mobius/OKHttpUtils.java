package com.mobius;

import com.squareup.okhttp.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class OKHttpUtils {
    private static final OkHttpClient okHttpClient = new OkHttpClient();
    public static final MediaType JSON;

    public OKHttpUtils() {
    }

    public static String formatParams(List<BasicNameValuePair> params) {
        return URLEncodedUtils.format(params, "UTF-8");
    }

    public static String get(String url, Map<String, String> paramsMap) throws IOException {
        String responseStr = null;
        ResponseBody body = null;
        try {
            List<BasicNameValuePair> nameValuePairs = null;
            if (paramsMap != null && !paramsMap.isEmpty()) {
                nameValuePairs = new ArrayList();
                Set<String> keys = paramsMap.keySet();
                Iterator i$ = keys.iterator();

                while (i$.hasNext()) {
                    String key = (String) i$.next();
                    BasicNameValuePair basicNameValuePair = new BasicNameValuePair(key, (String) paramsMap.get(key));
                    nameValuePairs.add(basicNameValuePair);
                }

                url = url + "?" + formatParams(nameValuePairs);
            }

            Request request = (new Request.Builder()).url(url).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                body = response.body();
                if (body != null) {
                    responseStr = body.string();
                }
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (body != null) {
                body.close();
            }
        }


        return responseStr;
    }

    public static String post(String url, Map<String, String> paramsMap) throws IOException {
        String responseStr = null;
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        if (paramsMap != null && !paramsMap.isEmpty()) {
            Set<String> keys = paramsMap.keySet();
            Iterator i$ = keys.iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                formEncodingBuilder.add(key, (String) paramsMap.get(key));
            }
        }

        RequestBody formBody = formEncodingBuilder.build();
        Request request = (new Request.Builder()).url(url).post(formBody).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            responseStr = response.body().string();
            return responseStr;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public static String post(String url, String jsonData) throws IOException {
        String responseStr = null;
        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = (new Request.Builder()).url(url).post(body).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            responseStr = response.body().string();
            return responseStr;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    static {
        okHttpClient.setConnectTimeout(30L, TimeUnit.SECONDS);
        JSON = MediaType.parse("application/json; charset=utf-8");
    }
}
