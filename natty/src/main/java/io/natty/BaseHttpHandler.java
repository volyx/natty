package io.natty;

import io.natty.rest.HandlerWrapper;
import io.netty.channel.ChannelHandler;
import io.natty.rest.HandlerHolder;
import io.natty.rest.URIDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;

import static io.natty.Response.serverError;

@ChannelHandler.Sharable
public class BaseHttpHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(BaseHttpHandler.class);
    private final HandlerWrapper[] handlers;


    public BaseHttpHandler(Object restApi) {
        this.handlers = AnnotationsUtil.register(restApi);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            if (!process(ctx, req)) {
                ctx.fireChannelRead(req);
            }
        }
    }

    public boolean process(ChannelHandlerContext ctx, HttpRequest req) {
        HandlerHolder handlerHolder = lookupHandler(req);

        if (handlerHolder != null) {
            try {
                invokeHandler(ctx, req, handlerHolder.handler, handlerHolder.extractedParams);
            } catch (Exception e) {
                log.error("Error processing http request.", e);
                ctx.writeAndFlush(Response.serverError(e.getMessage()), ctx.voidPromise());
            } finally {
                ReferenceCountUtil.release(req);
            }
            return true;
        }

        return false;
    }

    private void invokeHandler(ChannelHandlerContext ctx, HttpRequest req,
                               HandlerWrapper handler, Map<String, String> extractedParams) {
        log.debug("{} : {}", req.method().name(), req.uri());
        try (URIDecoder uriDecoder = new URIDecoder(req, extractedParams)) {
            Object[] params = handler.fetchParams(ctx, uriDecoder);
            finishHttp(ctx, handler, params);
        }
    }

    private void finishHttp(ChannelHandlerContext ctx, HandlerWrapper handler, Object[] params) {
        Response response = handler.invoke(params);
        if (response != Response.NO_RESPONSE) {
            ctx.writeAndFlush(response);
        }
    }

    private HandlerHolder lookupHandler(HttpRequest req) {
        for (HandlerWrapper handler : handlers) {
            if (handler.httpMethod == req.method()) {
                Matcher matcher = handler.uriTemplate.matcher(req.uri());
                if (matcher.matches()) {
                    Map<String, String> extractedParams = handler.uriTemplate.extractParameters(matcher);
                    return new HandlerHolder(handler, extractedParams);
                }
            }
        }
        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
    }

}
