package org.sunger.lib.http.client;

public class HttpClientException extends Exception {
	private static final long serialVersionUID = 1L;

	HttpClientException(final String message, final Throwable cause) {
		super(message);
		initCause(cause);
	}

	public HttpClientException(final String message) {
		this(message, null);
	}
}
