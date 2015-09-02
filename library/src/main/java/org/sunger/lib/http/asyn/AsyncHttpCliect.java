package org.sunger.lib.http.asyn;

 import org.sunger.lib.http.client.HttpClient;
 import org.sunger.lib.http.client.HttpRequestBuilder;

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

	void sendRequest(HttpRequestBuilder httpRequestBuilder,
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

	public void removeHeader(String header) {
		httpClient.getHeaderMap().remove(header);
	}

	public void removeAllHeaders() {
		httpClient.getHeaderMap().clear();
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
