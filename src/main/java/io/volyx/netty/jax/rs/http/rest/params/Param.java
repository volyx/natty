package io.volyx.netty.jax.rs.http.rest.params;

import io.volyx.netty.jax.rs.http.rest.URIDecoder;
import io.netty.channel.ChannelHandlerContext;

public abstract class Param {

    protected final String name;

    protected final Class<?> type;

    public Param(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public abstract Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder);

    Object convertTo(String value) {
        if (type == long.class) {
            return Long.valueOf(value);
        }
        if (type == int.class || type == Integer.class) {
            return Integer.valueOf(value);
        }
        if (type == short.class || type == Short.class) {
            return Short.valueOf(value);
        }
        if (type == boolean.class) {
            return Boolean.valueOf(value);
        }
        return value;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }
}
