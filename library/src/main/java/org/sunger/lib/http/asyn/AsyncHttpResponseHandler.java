package org.sunger.lib.http.asyn;


import android.os.Handler;
import android.os.Looper;

import org.sunger.lib.http.client.HttpResponse;

public abstract class AsyncHttpResponseHandler implements
        ResponseHandlerInterface {
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;

    private Handler mDelivery;

    public abstract void onSuccess(int stateCode, String data);

    public void onFailure(Throwable error) {
    }

    protected void onStart() {

    }

    public AsyncHttpResponseHandler() {
        mDelivery = new Handler(Looper.getMainLooper());
    }

    @Override
    public void sendStartMessage() {
        sendMessage(START_MESSAGE, null);
    }

    @Override
    public void sendFailureMessage(Throwable error) {
        sendMessage(FAILURE_MESSAGE, error);
    }

    @Override
    public void sendSuccessMessage(HttpResponse response) {
        sendMessage(SUCCESS_MESSAGE, response);
    }

    private void handlerMessage(int type, Object msg) {
        switch (type) {
            case SUCCESS_MESSAGE:
                HttpResponse response=(HttpResponse) msg;
                onSuccess(response.getStatusCode(),response.getResult());
                break;
            case FAILURE_MESSAGE:
                onFailure((Throwable) msg);
                break;
            case START_MESSAGE:
                onStart();
                break;
        }
    }

    protected void sendMessage(final int type, final Object msg) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                handlerMessage(type, msg);
            }
        });
    }

}
