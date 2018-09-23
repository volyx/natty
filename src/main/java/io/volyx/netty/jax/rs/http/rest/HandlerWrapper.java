package io.volyx.netty.jax.rs.http.rest;

import io.volyx.netty.jax.rs.http.Response;
import io.volyx.netty.jax.rs.http.UriTemplate;
import io.volyx.netty.jax.rs.http.rest.params.Param;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.lang.reflect.Method;


/**
 * Wrapper around Singleton Services.
 * Holds all info about annotations and service purpose.
 *
 */
public class HandlerWrapper {

    private static final Logger log = LoggerFactory.getLogger(HandlerWrapper.class);

    public final UriTemplate uriTemplate;

    public final HttpMethod httpMethod;

    public final Method classMethod;

    public final Object handler;

    public final Param[] params;

//    public final short metricIndex;

//    public final GlobalStats globalStats;

    public HandlerWrapper(UriTemplate uriTemplate, Method method, Object handler) {
        this.uriTemplate = uriTemplate;
        this.classMethod = method;
        this.handler = handler;

        if (method.isAnnotationPresent(POST.class)) {
            this.httpMethod = HttpMethod.POST;
        } else if (method.isAnnotationPresent(PUT.class)) {
            this.httpMethod = HttpMethod.PUT;
        } else if (method.isAnnotationPresent(DELETE.class)) {
            this.httpMethod = HttpMethod.DELETE;
        } else {
            this.httpMethod = HttpMethod.GET;
        }

//        Metric metricAnnotation = method.getAnnotation(Metric.class);
//        if (metricAnnotation != null) {
//            metricIndex = metricAnnotation.value();
//        } else {
//            metricIndex = -1;
//        }

        this.params = new Param[method.getParameterCount()];
//        this.globalStats = globalStats;
    }

    public Object[] fetchParams(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        Object[] res = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            res[i] = params[i].get(ctx, uriDecoder);
        }

        return res;
    }

    public FullHttpResponse invoke(Object[] params) {
        try {
            mark();
            return (FullHttpResponse) classMethod.invoke(handler, params);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                log.error("Error invoking handler. Reason : {}.", e.getMessage());
                log.debug(e.getMessage(), e);
            } else {
                log.error("Error invoking handler. Reason : {}.", cause.getMessage());
                log.debug(cause.getMessage(), e);
            }

            return Response.serverError(e.getMessage());
        }
    }

    private void mark() {
//        globalStats.mark(HTTP_TOTAL);
//        if (metricIndex > -1) {
//            globalStats.markSpecificCounterOnly(metricIndex);
//        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HandlerWrapper)) {
            return false;
        }

        HandlerWrapper that = (HandlerWrapper) o;

        if (uriTemplate != null ? !uriTemplate.equals(that.uriTemplate) : that.uriTemplate != null) {
            return false;
        }
        return !(httpMethod != null ? !httpMethod.equals(that.httpMethod) : that.httpMethod != null);

    }

    @Override
    public int hashCode() {
        int result = uriTemplate != null ? uriTemplate.hashCode() : 0;
        result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
        return result;
    }
}
