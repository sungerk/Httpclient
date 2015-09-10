package org.sunger.lib.http.asyn;

import org.sunger.lib.http.client.HttpClient;
import org.sunger.lib.http.client.HttpRequestBuilder;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHttpCliect {
    private ExecutorService threadPool;

    private HttpClient httpClient;

    public AsyncHttpCliect() {
        threadPool = getDefaultThreadPool();
        httpClient = new HttpClient();
    }

    public void post(String url, ResponseHandlerInterface responseHandler) {
        get(url, null, responseHandler);
    }

    public void post(String url, Map<String, String> params,
                     ResponseHandlerInterface responseHandler) {
        HttpRequestBuilder builder = httpClient.post(url);
        sendRequest(builder, params, responseHandler);
    }

    public void get(String url, ResponseHandlerInterface responseHandler) {
        get(url, null, responseHandler);
    }

    public void get(String url, Map<String, String> params,
                    ResponseHandlerInterface responseHandler) {
        HttpRequestBuilder builder = httpClient.get(url);
        sendRequest(builder, params, responseHandler);
    }

    private void sendRequest(HttpRequestBuilder httpRequestBuilder,
                             Map<String, String> params, ResponseHandlerInterface responseHandler) {
        if (params != null) {
            httpRequestBuilder.setParam(params);
        }
        AsyncHttpRequest asyncHttpRequest = new AsyncHttpRequest(
                httpRequestBuilder, responseHandler);
        threadPool.submit(asyncHttpRequest);
    }

    protected ExecutorService getDefaultThreadPool() {
        return Executors.newCachedThreadPool();
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void setCertificates(InputStream[] certificates, InputStream bksFile, String password) {
        httpClient.setCertificates(certificates, bksFile, password);
    }


    /**
     * @param certificates context.getAssets().open("srca.cer")
     */
    public void setCertificates(InputStream... certificates) {
        httpClient.setCertificates(certificates, null, null);
    }

    public void addHeader(String name, String value) {
        httpClient.addHeader(name, value);
    }

    public void setUserAgent(String userAgent) {
        httpClient.setUserAgent(userAgent);

    }

    public void setReferer(String referer) {
        httpClient.setReferer(referer);
    }

    public void setRange(String range) {
        httpClient.setRange(range);

    }

}
