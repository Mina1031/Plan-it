package com.plan_it.mobile.plan_it;

/**
 * Created by Kristian on 09/11/2015.
 */
import com.loopj.android.http.*;

public class RestClient {
    private static final String BASE_URL = "http://planit.lukefarnell.ca:3000/";
   // private static final String BASE_URL = "http://192.168.0.52:3000/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params,String header, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("x-access-token", header);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params,String header, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("x-access-token", header);
        client.post(getAbsoluteUrl(url), params, responseHandler);

    }
    public static void put(String url, RequestParams params,String header, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("x-access-token", header);
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, RequestParams params,String header, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("x-access-token", header);
        client.delete(getAbsoluteUrl(url), params, responseHandler);

    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}