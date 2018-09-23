package io.volyx.netty.jax.rs.http.rest.params;

import io.volyx.netty.jax.rs.http.rest.URIDecoder;
import io.netty.channel.ChannelHandlerContext;

public class ContextParam extends Param {

    public ContextParam(Class<?> type) {
        super(null, type);
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        return ctx;
    }

}
