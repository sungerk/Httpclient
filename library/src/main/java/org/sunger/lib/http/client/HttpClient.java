package org.sunger.lib.http.client;

import android.os.Build;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.util.Map;

public class HttpClient {
    private static final String DEFAULT_USER_AGENT = getDefaultUserAgent();
    private static final String USER_AGENT = "User-Agent";
    private static final String REFERER = "Referer";
    private static final String Range = "Range";
    /**
     * 默认超时时间
     */
    private int connectTimeout = 5 * 1000;

    private Map<String, String> clientHeaderMap = new ArrayMap<>();

    public HttpRequestBuilder get(String uri) {
        return newHttpRequestBuilder(uri, RequestMethod.GET);
    }

    public HttpRequestBuilder delete(String uri) {
        return newHttpRequestBuilder(uri, RequestMethod.DELETE);
    }

    public HttpRequestBuilder post(String uri) {
        return newHttpRequestBuilder(uri, RequestMethod.POST);
    }

    public HttpRequestBuilder put(String uri) {
        return newHttpRequestBuilder(uri, RequestMethod.PUT);
    }

    public HttpRequestBuilder head(String uri) {
        return newHttpRequestBuilder(uri, RequestMethod.HEAD);
    }

    private HttpRequestBuilder newHttpRequestBuilder(String uri,
                                                     RequestMethod method) {

        if (TextUtils.isEmpty(uri)) {
            throw new IllegalArgumentException("URI cannot be null");
        }

        return new HttpRequestBuilder(uri, method, this);

    }

    public String getUserAgent() {
        String userAgent = clientHeaderMap.get("User-Agent");
        if (userAgent == null) {
            return DEFAULT_USER_AGENT;
        }
        return userAgent;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout < 0) {
            throw new IllegalArgumentException("Invalid connect timeout: "
                    + connectTimeout);
        }
        this.connectTimeout = connectTimeout;
    }

    private static final String getDefaultUserAgent() {
        return "HttpClient (" + Build.MANUFACTURER + " " + Build.MODEL
                + "; Android " + Build.VERSION.RELEASE + "/"
                + Build.VERSION.SDK_INT + ")";
    }

    public void addHeader(String name, String value) {
         if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        clientHeaderMap.put(name, value);
    }



    public void setUserAgent(String userAgent) {
        addHeader(USER_AGENT, userAgent);
    }

    public void setReferer(String referer) {
        addHeader(REFERER, referer);
    }

    public void setRange(String range) {
        addHeader(Range, range);
    }

    public Map<String, String> getHeaderMap() {
        return clientHeaderMap;
    }
}
