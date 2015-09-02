package org.sunger.lib.http.asyn;


import org.sunger.lib.http.client.HttpClientException;
import org.sunger.lib.http.client.HttpRequestBuilder;
import org.sunger.lib.http.client.HttpResponse;

public class AsyncHttpRequest implements Runnable {
	private HttpRequestBuilder httpRequestBuilder;

	private ResponseHandlerInterface responseHandler;

	AsyncHttpRequest(HttpRequestBuilder httpRequestBuilder,
			ResponseHandlerInterface responseHandlerInterface) {
		this.httpRequestBuilder = httpRequestBuilder;
		this.responseHandler = responseHandlerInterface;
	}

	@Override
	public void run() {
		responseHandler.sendStartMessage();
		try {
 			HttpResponse response = httpRequestBuilder.execute();
			responseHandler.sendSuccessMessage(response);
		} catch (HttpClientException e) {
			responseHandler.sendFailureMessage(e);
		}
	}
	

}
