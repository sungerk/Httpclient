package org.sunger.lib.http.asyn;


import org.sunger.lib.http.client.HttpResponse;

public interface ResponseHandlerInterface {

	void sendFailureMessage(Throwable msg);

	void sendStartMessage();

	void sendSuccessMessage(HttpResponse response);

}
