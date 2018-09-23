package io.volyx.netty.jax.rs.http.rest.params;

import io.volyx.netty.jax.rs.http.rest.URIDecoder;
import io.netty.channel.ChannelHandlerContext;

public class PathParam extends Param {

    public PathParam(String name, Class<?> type) {
        super(name, type);
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        return convertTo(uriDecoder.pathData.get(name));
    }

}
