package org.sunger.lib.http.client;

import android.os.Build;
import android.text.TextUtils;

import org.sunger.lib.http.utils.SSLSocketFactoryUtil;

import java.io.InputStream;

import javax.net.ssl.SSLSocketFactory;

public class HttpClient {
    private static final String DEFAULT_USER_AGENT = getDefaultUserAgent();
    private static final String USER_AGENT = "User-Agent";
    private static final String REFERER = "Referer";
    private static final String RANGE = "Range";
    private HttpRequestBuilder builder;

    public HttpClient() {
        builder = new HttpRequestBuilder();
    }

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
        return builder.setUrl(uri).setRequestMethod(method);
    }

    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout < 0) {
            throw new IllegalArgumentException("Invalid connect timeout: "
                    + connectTimeout);
        }
        builder.setConnectTimeout(connectTimeout);
    }

    private static final String getDefaultUserAgent() {
        return "HttpClient (" + Build.MANUFACTURER + " " + Build.MODEL
                + "; Android " + Build.VERSION.RELEASE + "/"
                + Build.VERSION.SDK_INT + ")";
    }

    public void addHeader(String name, String value) {
        builder.addHeader(name, value);
    }

    public void addParam(String name, String value) {
        builder.addParam(name, value);
    }

    public void setCertificates(InputStream[] certificates, InputStream bksFile, String password) {
        try {
            SSLSocketFactory sslSocketFactory = SSLSocketFactoryUtil.create(certificates, bksFile, password);
            builder.setSSLSocketFactory(sslSocketFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @param certificates context.getAssets().open("srca.cer")
     */
    public void setCertificates(InputStream... certificates) {
        setCertificates(certificates, null, null);
    }


    public void setUserAgent(String userAgent) {
        addHeader(USER_AGENT, userAgent);
    }

    public void setReferer(String referer) {
        addHeader(REFERER, referer);
    }

    public void setRange(String range) {
        addHeader(RANGE, range);
    }

}
