package io.volyx.netty_jax_rs.core.http.rest.params;

import io.volyx.netty_jax_rs.core.http.rest.ReflectionUtil;
import io.volyx.netty_jax_rs.core.http.rest.URIDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 *
 *
 * Created on 09.12.15.
 */
public class FormParam extends Param {

    private static final Logger log = LogManager.getLogger(FormParam.class);

    public FormParam(String name, Class<?> type) {
        super(name, type);
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        List<InterfaceHttpData> bodyHttpDatas = uriDecoder.getBodyHttpDatas();
        if (bodyHttpDatas == null || bodyHttpDatas.size() == 0) {
            return null;
        }

        for (InterfaceHttpData data : bodyHttpDatas) {
            if (name.equals(data.getName())) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    Attribute attribute = (Attribute) data;
                    try {
                        return ReflectionUtil.castTo(type, attribute.getValue());
                    } catch (IOException e) {
                        log.error("Error getting form params. Reason : {}", e.getMessage(), e);
                    }
                }
            }
        }

        return null;
    }

}
