package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class AndResponseHandler implements ResponseHandler {
    private ResponseHandler[] handlers;

    public AndResponseHandler(ResponseHandler[] handlers) {
        this.handlers = handlers;
    }


    @Override
    public void writeToResponse(HttpResponse response) {
        for (ResponseHandler handler : handlers) {
            handler.writeToResponse(response);
        }
    }
}
