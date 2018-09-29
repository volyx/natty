package io.volyx.netty.jax.rs.http;
import io.netty.buffer.Unpooled;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslHandler;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public final class Response extends DefaultFullHttpResponse {

    private static final String JSON = MediaType.APPLICATION_JSON + ";charset=utf-8";
    private static final String PLAIN_TEXT = MediaType.TEXT_PLAIN + ";charset=utf-8";

    final static Response NO_RESPONSE = null;

    private Response(HttpVersion version, HttpResponseStatus status, String content, String contentType) {
        super(version, status, (
                content == null
                        ? Unpooled.EMPTY_BUFFER
                        : Unpooled.copiedBuffer(content, StandardCharsets.UTF_8))
        );
        fillHeaders(contentType);
    }

    private Response(HttpVersion version, HttpResponseStatus status, byte[] content, String contentType) {
        super(version, status, (content == null ? Unpooled.EMPTY_BUFFER : Unpooled.copiedBuffer(content)));
        fillHeaders(contentType);
    }

    private Response(HttpVersion version, HttpResponseStatus status) {
        super(version, status);
        headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                 .set(ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                 .set(CONTENT_LENGTH, 0);
    }

	public Response(DefaultFileRegion defaultFileRegion, String contentType) {
        super(HTTP_1_1, OK);
        fillHeaders(contentType, defaultFileRegion.count());
	}

	private void fillHeaders(String contentType) {
        headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                 .set(CONTENT_TYPE, contentType)
                 .set(ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                 .set(CONTENT_LENGTH, content().readableBytes());
    }

    private void fillHeaders(String contentType, long contentLength) {
        headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                 .set(CONTENT_TYPE, contentType)
                 .set(ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                 .set(CONTENT_LENGTH, contentLength);
    }

    public static Response noResponse() {
        return NO_RESPONSE;
    }

    public static Response ok() {
        return new Response(HTTP_1_1, OK);
    }

    public static Response notFound() {
        return new Response(HTTP_1_1, NOT_FOUND);
    }

    public static Response forbidden() {
        return new Response(HTTP_1_1, FORBIDDEN);
    }

    public static Response forbidden(String error) {
        return new Response(HTTP_1_1, FORBIDDEN, error, PLAIN_TEXT);
    }

    public static Response badRequest() {
        return new Response(HTTP_1_1, BAD_REQUEST);
    }

    public static Response redirect(String url) {
        Response response = new Response(HTTP_1_1, MOVED_PERMANENTLY);
        response.headers()
                .set(LOCATION, url)
                .set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        return response;
    }

    public static Response badRequest(String message) {
        return new Response(HTTP_1_1, BAD_REQUEST, message, PLAIN_TEXT);
    }

    public static Response serverError() {
        return new Response(HTTP_1_1, INTERNAL_SERVER_ERROR);
    }

    public static Response serverError(String message) {
        return new Response(HTTP_1_1, INTERNAL_SERVER_ERROR, message, PLAIN_TEXT);
    }

    public static Response ok(String data) {
        return new Response(HTTP_1_1, OK, data, JSON);
    }

    public static Response ok(String data, String contentType) {
        return new Response(HTTP_1_1, OK, data, contentType);
    }

    public static Response ok(byte[] data, String contentType) {
        return new Response(HTTP_1_1, OK, data, contentType);
    }


    public static Response ok(boolean bool) {
        return new Response(HTTP_1_1, OK, String.valueOf(bool), JSON);
    }

    public static Response ok(List<?> list, int page, int size) {
        String data = JsonParser.toJson(list.subList( page, size));
        return ok(data == null ? "[]" : data);
    }

    public static Response ok(Map<?, ?> map) {
        String data = JsonParser.toJson(map);
        return ok(data == null ? "{}" : data);
    }

    public static Response ok(Collection<?> list) {
        String data = JsonParser.toJson(list);
        return ok(data == null ? "[]" : data);
    }

    public static Response appendTotalCountHeader(Response response, int count) {
        response.headers().set("X-Total-Count", count);
        return response;
    }
}
