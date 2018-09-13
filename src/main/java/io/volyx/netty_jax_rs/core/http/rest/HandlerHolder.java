package io.volyx.netty_jax_rs.core.http.rest;

import java.util.Map;

public final class HandlerHolder {

    public final HandlerWrapper handler;

    public final Map<String, String> extractedParams;

    public HandlerHolder(HandlerWrapper handler, Map<String, String> extractedParams) {
        this.handler = handler;
        this.extractedParams = extractedParams;
    }
}
