package io.volyx.netty_jax_rs.core.http;

import io.volyx.netty_jax_rs.core.http.rest.HandlerWrapper;
import io.volyx.netty_jax_rs.core.http.rest.params.BodyParam;
import io.volyx.netty_jax_rs.core.http.rest.params.Context;
import io.volyx.netty_jax_rs.core.http.rest.params.ContextParam;
import io.volyx.netty_jax_rs.core.http.rest.params.FormParam;
import io.volyx.netty_jax_rs.core.http.rest.params.PathParam;
import io.volyx.netty_jax_rs.core.http.rest.params.QueryParam;
import io.netty.channel.ChannelHandlerContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * Created on 09.12.15.
 */
public final class AnnotationsUtil {

    private AnnotationsUtil() {
    }

    public static HandlerWrapper[] register(String rootPath, Object o) {
        return registerHandler(rootPath, o);
    }

    private static HandlerWrapper[] registerHandler(String rootPath, Object handler) {
        Class<?> handlerClass = handler.getClass();
        Annotation pathAnnotation = handlerClass.getAnnotation(Path.class);
        String handlerMainPath = ((Path) pathAnnotation).value();

        List<HandlerWrapper> processors = new ArrayList<>();

        for (Method method : handlerClass.getMethods()) {
            Annotation consumes = method.getAnnotation(Consumes.class);
            String contentType = MediaType.APPLICATION_JSON;
            if (consumes != null) {
                contentType = ((Consumes) consumes).value()[0];
            }

            Annotation path = method.getAnnotation(Path.class);
            if (path != null) {
                String fullPath = rootPath + handlerMainPath + ((Path) path).value();
                UriTemplate uriTemplate = new UriTemplate(fullPath);

                HandlerWrapper handlerHolder = new HandlerWrapper(uriTemplate, method, handler);

                for (int i = 0; i < method.getParameterCount(); i++) {
                    Parameter parameter = method.getParameters()[i];

                    javax.ws.rs.QueryParam queryParamAnnotation =
                            parameter.getAnnotation(javax.ws.rs.QueryParam.class);
                    if (queryParamAnnotation != null) {
                        handlerHolder.params[i] = new QueryParam(queryParamAnnotation.value(), parameter.getType());
                    }

                    javax.ws.rs.PathParam pathParamAnnotation =
                            parameter.getAnnotation(javax.ws.rs.PathParam.class);
                    if (pathParamAnnotation != null) {
                        handlerHolder.params[i] = new PathParam(pathParamAnnotation.value(), parameter.getType());
                    }

                    javax.ws.rs.FormParam formParamAnnotation =
                            parameter.getAnnotation(javax.ws.rs.FormParam.class);
                    if (formParamAnnotation != null) {
                        handlerHolder.params[i] = new FormParam(formParamAnnotation.value(), parameter.getType());
                    }

                    Annotation contextAnnotation = parameter.getAnnotation(Context.class);
                    if (contextAnnotation != null) {
                        handlerHolder.params[i] = new ContextParam(ChannelHandlerContext.class);
                    }

                    if (pathParamAnnotation == null && queryParamAnnotation == null && formParamAnnotation == null
                            && contextAnnotation == null) {
                        handlerHolder.params[i] =
                                new BodyParam(parameter.getName(), parameter.getType(), contentType);
                    }
                }

                processors.add(handlerHolder);
            }
        }

        return processors.toArray(new HandlerWrapper[processors.size()]);
    }

}
