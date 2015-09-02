package org.sunger.lib.http.client;

import android.text.TextUtils;

import org.sunger.lib.http.utils.UrlUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequestBuilder {
    public static final String UTF8_CHAR_SET = "UTF-8";
    private String urlString;
    private RequestMethod method;
    private Map<String, String> parameters = new HashMap<String, String>();
    private HttpURLConnection httpURLConnection;
    private HttpClient cliect;

    private static class Method {
        public static final String METHOD_GET = "GET";
        public static final String METHOD_PUT = "PUT";
        public static final String METHOD_POST = "POST";
        public static final String METHOD_DELETE = "DELETE";
        public static final String METHOD_HEAD = "HEAD";
    }

    public HttpRequestBuilder(String url, RequestMethod method,
                              HttpClient cliect) {
        this.urlString = url;
        this.cliect = cliect;
        this.method = method;
    }

    public HttpRequestBuilder setUrl(String url) {
        this.urlString = url;
        return this;
    }

    private void addRequestProperty(URLConnection urlConnection) {
        Iterator<Entry<String, String>> iter = cliect.getHeaderMap().entrySet()
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
        if (stringBuffer.length() != 0)
            stringBuffer.delete(stringBuffer.length() - 1,
                    stringBuffer.length());
        return stringBuffer.toString();
    }

    private String parseUrl() throws IOException {
        if (!UrlUtil.isUrl(urlString)) {
            throw new IllegalArgumentException("url不合法");
        }
        urlString = UrlUtil.parseUrl(urlString);
        if (method == RequestMethod.GET) {
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

    private void makeRequest() throws IOException {
        URL url = new URL(parseUrl());
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(cliect.getConnectTimeout());
        if (hasOutPut())
            httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setAllowUserInteraction(false);
        httpURLConnection.setInstanceFollowRedirects(false);
        httpURLConnection.setUseCaches(false);
        addRequestProperty(httpURLConnection);
        setRequestMethod(httpURLConnection);
    }


    private void sendRequest() throws IOException {
        httpURLConnection.connect();
        if (hasOutPut()) {
            String params = parseParam(parameters);
            byte[] bypes = params.toString().getBytes();
            httpURLConnection.getOutputStream().write(bypes);
        }
    }

    public HttpResponse execute() throws HttpClientException {
        HttpResponse httpResponse = null;
        try {
            makeRequest();
            sendRequest();
            InputStream payload = httpURLConnection.getInputStream();
            int responseCode = httpURLConnection.getResponseCode();
            Map<String, List<String>> headerFields = httpURLConnection
                    .getHeaderFields();
            int contentLength = httpURLConnection.getContentLength();
            String file = httpURLConnection.getURL().getFile().toString();
            String fileName = file.substring(file.lastIndexOf('/') + 1);
            httpResponse = new HttpResponse(responseCode, contentLength,
                    fileName, payload, headerFields);
        } catch (IOException e) {
            throw new HttpClientException("请求异常" + e.getMessage());
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return httpResponse;
    }
}
