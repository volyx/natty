package io.volyx.netty_jax_rs.core.http.rest.params;

import io.volyx.netty_jax_rs.core.http.rest.URIDecoder;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 *
 * Created on 09.12.15.
 */
public class ContextParam extends Param {

    public ContextParam(Class<?> type) {
        super(null, type);
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        return ctx;
    }

}
