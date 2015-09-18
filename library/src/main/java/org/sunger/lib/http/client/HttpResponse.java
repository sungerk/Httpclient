package org.sunger.lib.http.client;

import android.os.Build;
import android.text.TextUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
	private Map<String, List<String>> headers;
	private InputStream payload;
	private int contentLength;
	private String contentType;
	private int statusCode;
	private String fileName;
	private String result = "";

	public HttpResponse(int contentLength, String contentType, int statusCode,
			InputStream payload, Map<String, List<String>> rawHeaders) {
		this.contentLength = contentLength;
		this.contentType = contentType;
		this.statusCode = statusCode;
		this.payload = payload;
		init(rawHeaders);
	}

	private void init(Map<String, List<String>> rawHeaders) {
		initHeaders(rawHeaders);
		String contentDispositionStr = getFirstHeaderValue("Content-Disposition");
		if (!TextUtils.isEmpty(contentDispositionStr)) {
			fileName = contentDispositionStr.replace("attachment; filename=",
					"").trim();
		}
		if (TextUtils.isEmpty(fileName)) {
			result = readToText();
		}
	}

	private void initHeaders(Map<String, List<String>> rawHeaders) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			final Map<String, List<String>> newHeaders = new HashMap<String, List<String>>(
					rawHeaders.size());
			for (final Map.Entry<String, List<String>> e : rawHeaders
					.entrySet()) {
				final String key = e.getKey();
				final int keyLen = key.length();
				final StringBuilder newKey = new StringBuilder(keyLen);
				for (int i = 0; i < keyLen; ++i) {
					final char c = key.charAt(i);
					final char c2;
					if (i == 0 || key.charAt(i - 1) == '-') {
						c2 = Character.toUpperCase(c);
					} else {
						c2 = c;
					}
					newKey.append(c2);
				}
				newHeaders.put(newKey.toString(), e.getValue());
			}
			this.headers = Collections.unmodifiableMap(newHeaders);
		} else {
			this.headers = Collections.unmodifiableMap(rawHeaders);
		}
	}

	private String readToText() {
		StringBuilder buffer = new StringBuilder();
		try {
			String enc = getContentCharset();
			if (enc == null) {
				enc = "UTF-8";
			}
			InputStream input = getPayload();
			InputStreamReader reader = new InputStreamReader(input, enc);
			final char[] inBuf = new char[64];
			for (int charsRead; (charsRead = reader.read(inBuf)) != -1;) {
				buffer.append(inBuf, 0, charsRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();

	}

	public String getContentCharset() {
		final String contentType = getFirstHeaderValue("Content-Type");
		if (contentType == null) {
			return null;
		}
		final int i = contentType.indexOf('=');
		return i == -1 ? null : contentType.substring(i + 1).trim();
	}

	public InputStream getPayload() {
		return payload;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public int getContentLength() {
		return contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public String getResult() {
		return result;
	}

	public String getFirstHeaderValue(String name) {
		final List<String> values = headers.get(name);
		if (values == null || values.isEmpty()) {
			return null;
		}
		return values.get(0);
	}

}
