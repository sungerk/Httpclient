package org.sunger.lib.http.client;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import org.sunger.lib.http.utils.UrlUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

public class HttpRequestBuilder {
	private static final String TAG = HttpRequestBuilder.class.getSimpleName();
	private static final String UTF8_CHAR_SET = "UTF-8";
	private static final int DEFAULT_CONNECT_TIME_OUT = 10 * 1000;
	private static final int DEFAULT_READ_TIME_OUT = 10 * 1000;
	private int readTimeout = DEFAULT_READ_TIME_OUT;
	private int connectTimeout = DEFAULT_CONNECT_TIME_OUT;
	private Map<String, String> parameters = new ArrayMap<>();
	private Map<String, String> clientHeaderMap = new ArrayMap<>();
	private HttpURLConnection httpURLConnection;
	private SSLSocketFactory sslSocketFactory;
	private String urlString;
	private RequestMethod method;

	private static class Method {
		public static final String METHOD_GET = "GET";
		public static final String METHOD_PUT = "PUT";
		public static final String METHOD_POST = "POST";
		public static final String METHOD_DELETE = "DELETE";
		public static final String METHOD_HEAD = "HEAD";
	}

	public HttpRequestBuilder setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public HttpRequestBuilder setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public HttpRequestBuilder setRequestMethod(RequestMethod method) {
		this.method = method;
		return this;
	}

	public HttpRequestBuilder setUrl(String url) {
		this.urlString = url;
		return this;
	}

	public HttpRequestBuilder setSSLSocketFactory(
			SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
		return this;
	}

	private void addRequestProperty(URLConnection urlConnection) {
		Iterator<Entry<String, String>> iter = clientHeaderMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			String value = entry.getValue();
			if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value))
				continue;
			urlConnection.addRequestProperty(key, value);
		}
	}

	public HttpRequestBuilder addHeader(String name, String value) {
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
			Log.d(TAG, "key和value不能为空!");
		} else {
			clientHeaderMap.put(name, value);
		}

		return this;
	}

	public HttpRequestBuilder addParam(String name, String value) {
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
			throw new IllegalArgumentException("参数不能为空");
		}
		parameters.put(name, value);
		return this;
	}

	public HttpRequestBuilder setParam(Map<String, String> params) {
		this.parameters = params;
		return this;
	}

	private String parseParam(Map<String, String> parameters)
			throws UnsupportedEncodingException {
		StringBuffer stringBuffer = new StringBuffer();
		Iterator<Entry<String, String>> iter = parameters.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			String value = entry.getValue();
			if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value))
				continue;
			stringBuffer.append(URLEncoder.encode(key, UTF8_CHAR_SET));
			stringBuffer.append("=");
			stringBuffer.append(URLEncoder.encode(value, UTF8_CHAR_SET));
			stringBuffer.append("&");
		}
		if (stringBuffer.length() > 0)
			stringBuffer.delete(stringBuffer.length() - 1,
					stringBuffer.length());
		return stringBuffer.toString();
	}

	private String parseUrl() throws IOException {
		if (!UrlUtil.isUrl(urlString)) {
			throw new IllegalArgumentException("url不合法");
		}
		urlString = UrlUtil.parseUrl(urlString);
		if (!hasOutPut()) {
			String param = parseParam(parameters);
			if (param.length() != 0) {
				urlString = "?" + param;
			}
		}
		return urlString;
	}

	private void setRequestMethod(HttpURLConnection connection)
			throws IOException {
		if (method == RequestMethod.GET) {
			connection.setRequestMethod(Method.METHOD_GET);
		} else if (method == RequestMethod.POST) {
			connection.setRequestMethod(Method.METHOD_POST);
		} else if (method == RequestMethod.PUT) {
			connection.setRequestMethod(Method.METHOD_PUT);
		} else if (method == RequestMethod.DELETE) {
			connection.setRequestMethod(Method.METHOD_DELETE);
		} else if (method == RequestMethod.PUT) {
			connection.setRequestMethod(Method.METHOD_PUT);
		} else if (method == RequestMethod.HEAD) {
			connection.setRequestMethod(Method.METHOD_HEAD);
		}
	}

	private boolean hasOutPut() {
		return method == RequestMethod.POST || method == RequestMethod.DELETE
				|| method == RequestMethod.PUT;
	}

	private void setCertificates(HttpsURLConnection httpsURLConnection) {
		if (sslSocketFactory != null) {
			httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
		}
	}

	private void setAllowAllHostnameVerifier(
			HttpsURLConnection httpsURLConnection) {
		httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
	}

	private void makeRequest() throws IOException {
		URL url = new URL(parseUrl());
		httpURLConnection = (HttpURLConnection) url.openConnection();
		if (httpURLConnection instanceof HttpsURLConnection) {
			setCertificates((HttpsURLConnection) httpURLConnection);
			setAllowAllHostnameVerifier((HttpsURLConnection) httpURLConnection);
		}
		httpURLConnection.setConnectTimeout(connectTimeout);
		httpURLConnection.setReadTimeout(readTimeout);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setAllowUserInteraction(false);
		httpURLConnection.setInstanceFollowRedirects(false);
		httpURLConnection.setUseCaches(false);
		if (hasOutPut())
			httpURLConnection.setDoOutput(true);
		addRequestProperty(httpURLConnection);
		setRequestMethod(httpURLConnection);
	}

	private void sendRequest() throws IOException {
		httpURLConnection.connect();
		if (hasOutPut()) {
			// 发送输出参数
			String params = parseParam(parameters);
			byte[] bypes = params.toString().getBytes();
			httpURLConnection.getOutputStream().write(bypes);
		}
	}

	private HttpResponse getHttpResponse(HttpURLConnection httpURLConnection)
			throws Exception {
		InputStream payload = httpURLConnection.getInputStream();
		Map<String, List<String>> headerFields = httpURLConnection
				.getHeaderFields();
		int contentLength = httpURLConnection.getContentLength();
		String contentType = httpURLConnection.getContentType();
		int stateCode = httpURLConnection.getResponseCode();
        String file = httpURLConnection.getURL().getFile().toString();
        String fileName = file.substring(file.lastIndexOf('/') + 1);
		return new HttpResponse(fileName,contentLength, contentType, stateCode, payload,
				headerFields);
	}

	public HttpResponse execute() throws HttpClientException {
		try {
			makeRequest();
			sendRequest();
			return getHttpResponse(httpURLConnection);
		} catch (Exception e) {
			throw new HttpClientException("请求异常" + e.getMessage());
		}
	}
}
