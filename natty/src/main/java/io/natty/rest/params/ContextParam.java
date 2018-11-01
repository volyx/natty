package io.natty.rest.params;

import io.natty.rest.URIDecoder;
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
