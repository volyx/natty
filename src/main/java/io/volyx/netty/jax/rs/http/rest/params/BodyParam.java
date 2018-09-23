package io.volyx.netty.jax.rs.http.rest.params;

import com.google.gson.JsonSyntaxException;
import io.volyx.netty.jax.rs.http.JsonParser;
import io.volyx.netty.jax.rs.http.rest.URIDecoder;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

public class BodyParam extends Param {

    private static final Logger log = LoggerFactory.getLogger(BodyParam.class);

    private final String expectedContentType;

    public BodyParam(String name, Class<?> type, String expectedContentType) {
        super(name, type);
        this.expectedContentType = expectedContentType;
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        if (uriDecoder.contentType == null || !uriDecoder.contentType.contains(expectedContentType)) {
            throw new RuntimeException("Unexpected content type. Expecting " + expectedContentType + ".");
        }

        switch (expectedContentType) {
            case MediaType.APPLICATION_JSON :
                String data = "";
                try {
                    data = uriDecoder.getContentAsString();
                    return JsonParser.MAPPER.fromJson(data, type);
                } catch (JsonSyntaxException jsonParseError) {
                    log.debug("Error parsing body param : '{}'.", data);
                    throw new RuntimeException("Error parsing body param. " + data);
                } catch (Exception e) {
                    log.error("Unexpected error during parsing body param.", e);
                    throw new RuntimeException("Unexpected error during parsing body param.", e);
                }
            default :
                return uriDecoder.getContentAsString();
        }
    }

}
