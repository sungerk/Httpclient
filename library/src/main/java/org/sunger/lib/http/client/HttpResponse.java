package org.sunger.lib.http.client;

import android.os.Build;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HttpResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private InputStream payload;
    private int contentLength;
    private String fileName;
    private String result;

    HttpResponse(final int statusCode, int contentLength, String fileName,
                 final InputStream payload,
                 final Map<String, List<String>> rawHeaders) {
        this.statusCode = statusCode;
        this.payload = payload;
        this.fileName = fileName;
        this.contentLength = contentLength;
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
        this.result = readToText();
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
            for (int charsRead; (charsRead = reader.read(inBuf)) != -1; ) {
                buffer.append(inBuf, 0, charsRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();

    }

    public String getContentType() {
        final String contentType = getFirstHeaderValue("Content-Type");
        if (contentType == null) {
            return null;
        }

        final int i = contentType.indexOf(';');
        return i == -1 ? contentType : contentType.substring(0, i).trim();
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

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getFirstHeaderValue(String name) {
        final List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getFileName() {
        return fileName;
    }
    public String getResult() {
        return result;
    }
}
