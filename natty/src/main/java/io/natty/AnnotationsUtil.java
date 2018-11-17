package io.natty;

import io.natty.rest.HandlerWrapper;
import io.natty.rest.params.*;
import io.netty.channel.ChannelHandlerContext;

import javax.ws.rs.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

final class AnnotationsUtil {

	private AnnotationsUtil() {
	}

	static HandlerWrapper[] register(Object o) {
		Class<?> handlerClass = o.getClass();
		Path pathAnnotation = handlerClass.getAnnotation(Path.class);
		String handlerMainPath = pathAnnotation.value();

		List<HandlerWrapper> processors = new ArrayList<>();

		for (Method method : handlerClass.getMethods()) {
			Consumes consumes = method.getAnnotation(Consumes.class);
			String contentType = MediaType.APPLICATION_JSON;
			if (consumes != null) {
				contentType = consumes.value()[0];
			}

			if (!isHttpMethodSupported(method)) {
				continue;
			}

			final Path path = method.getAnnotation(Path.class);

			final String fullPath = (path == null) ? handlerMainPath: handlerMainPath + path.value();

			final UriTemplate uriTemplate = new UriTemplate(fullPath);

			final HandlerWrapper handlerHolder = new HandlerWrapper(uriTemplate, method, o);

			for (int i = 0; i < method.getParameterCount(); i++) {
				Parameter parameter = method.getParameters()[i];

				final QueryParam queryParamAnnotation = parameter.getAnnotation(QueryParam.class);
				if (queryParamAnnotation != null) {
					handlerHolder.params[i] = new io.natty.rest.params.QueryParam(queryParamAnnotation.value(), parameter.getType());
				}

				final PathParam pathParamAnnotation = parameter.getAnnotation(PathParam.class);
				if (pathParamAnnotation != null) {
					handlerHolder.params[i] = new io.natty.rest.params.PathParam(pathParamAnnotation.value(), parameter.getType());
				}

				final FormParam formParamAnnotation = parameter.getAnnotation(FormParam.class);
				if (formParamAnnotation != null) {
					handlerHolder.params[i] = new io.natty.rest.params.FormParam(formParamAnnotation.value(), parameter.getType());
				}

				final Annotation contextAnnotation = parameter.getAnnotation(Context.class);
				if (contextAnnotation != null) {
					handlerHolder.params[i] = new ContextParam(ChannelHandlerContext.class);
				}

				final Annotation decoderAnnotation = parameter.getAnnotation(MultipleFiles.class);
				if (decoderAnnotation != null) {
					handlerHolder.params[i] = new MultipleFilesParam("decoder", MultipleFilesParam.class);
				}

				if (pathParamAnnotation == null && queryParamAnnotation == null && formParamAnnotation == null && contextAnnotation == null && decoderAnnotation == null) {
					handlerHolder.params[i] = new BodyParam(parameter.getName(), parameter.getType(), contentType);
				}
			}

			processors.add(handlerHolder);

		}

		return processors.toArray(new HandlerWrapper[0]);
	}

	private static boolean isHttpMethodSupported(Method method) {
		return method.getAnnotation(GET.class) != null ||
				method.getAnnotation(POST.class) != null ||
				method.getAnnotation(DELETE.class) != null ||
				method.getAnnotation(OPTIONS.class) != null ||
				method.getAnnotation(PUT.class) != null ||
				method.getAnnotation(HEAD.class) != null;
	}

}
