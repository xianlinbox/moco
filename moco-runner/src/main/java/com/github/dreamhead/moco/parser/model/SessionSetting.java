package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;

public class SessionSetting {
    private RequestSetting request;
    private ResponseSetting response;

    public ResponseSetting getResponse() {
        return response;
    }

    public RequestSetting getRequest() {
        return request;
    }

    public boolean isAnyResponse() {
        return request == null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("request", request).add("response", response).toString();
    }
}
