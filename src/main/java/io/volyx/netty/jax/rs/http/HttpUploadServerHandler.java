package io.volyx.netty.jax.rs.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {

	private static final Logger logger = LoggerFactory.getLogger(HttpUploadServerHandler.class.getName());

	private HttpRequest request;

	private HttpData partialContent;

	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

	private HttpPostRequestDecoder decoder;

	static {
		DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
		// on exit (in normal
		// exit)
		DiskFileUpload.baseDirectory = null; // system temp directory
		DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
		// exit (in normal exit)
		DiskAttribute.baseDirectory = null; // system temp directory
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (decoder != null) {
			decoder.cleanFiles();
		}
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
//		boolean readingChunks;
		if (msg instanceof HttpRequest) {
			HttpRequest request = this.request = (HttpRequest) msg;
			try {
				decoder = new HttpPostRequestDecoder(factory, request);
			} catch (ErrorDataDecoderException e1) {
				e1.printStackTrace();
//				responseContent.append(e1.getMessage());
				writeResponse(ctx.channel(), e1.getMessage());
				ctx.channel().close();
				return;
			}
		}

		// check if the decoder was constructed before
		// if not it handles the form get
		if (decoder != null) {
			if (msg instanceof HttpContent) {
				// New chunk is received
				HttpContent chunk = (HttpContent) msg;
				try {
					decoder.offer(chunk);
				} catch (ErrorDataDecoderException e1) {
					e1.printStackTrace();
//					responseContent.append(e1.getMessage());
					writeResponse(ctx.channel(), e1.getMessage());
					ctx.channel().close();
					return;
				}
//				responseContent.append('o');
				// example of reading chunk by chunk (minimize memory usage due to
				// Factory)
				readHttpDataChunkByChunk();
				// example of reading only if at the end
				if (chunk instanceof LastHttpContent) {
					writeResponse(ctx.channel(), "OK!");
//					readingChunks = false;

					reset();
				}
			}
		} else {
			writeResponse(ctx.channel(), "decoder == null");
		}
	}

	private void reset() {
		request = null;

		// destroy the decoder to release all resources
		decoder.destroy();
		decoder = null;
	}

	/**
	 * Example of reading request by chunk and getting values from chunk to chunk
	 */
	private void readHttpDataChunkByChunk() {
		try {
			while (decoder.hasNext()) {
				InterfaceHttpData data = decoder.next();
				if (data != null) {
					// check if current HttpData is a FileUpload and previously set as partial
					if (partialContent == data) {
						logger.info(" 100% (FinalSize: " + partialContent.length() + ")");
						partialContent = null;
					}
					try {
						// new value
//						writeHttpData(data);
					} finally {
						data.release();
					}
				}
			}
			// Check partial decoding for a FileUpload
			InterfaceHttpData data = decoder.currentPartialHttpData();

		} catch (EndOfDataDecoderException e1) {
			// end
//			responseContent.append("\r\n\r\nEND OF CONTENT CHUNK BY CHUNK\r\n\r\n");
		}
	}

//	private void writeHttpData(InterfaceHttpData data) {
//		if (data.getHttpDataType() == HttpDataType.FileUpload) {
//			FileUpload fileUpload = (FileUpload) data;
//			if (fileUpload.isCompleted()) {
//				Path file = Paths.get("/Users/volyx/Projects/netty-jax-rs/src/test/resources/byByteChannel.png");
//
//				try {
//					fileUpload.renameTo(file.toFile());
//				} catch (IOException e) {
//					throw new UncheckedIOException(e);
//				}
//			} else {
////					responseContent.append("\tFile to be continued but should not!\r\n");
//			}
//		}
//
//	}

	private void writeResponse(Channel channel, String message) {
		// Convert the response content to a ChannelBuffer.
		ByteBuf buf = copiedBuffer(message, CharsetUtil.UTF_8);
//		responseContent.setLength(0);

		// Decide whether to close the connection or not.
		boolean close = request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE, true)
				|| request.protocolVersion().equals(HttpVersion.HTTP_1_0)
				&& !request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE, true);

		// Build the response object.
		FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

		if (!close) {
			// There's no need to add 'Content-Length' header
			// if this is the last response.
			response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
		}

		Set<Cookie> cookies;
		String value = request.headers().get(HttpHeaderNames.COOKIE);
		if (value == null) {
			cookies = Collections.emptySet();
		} else {
			cookies = ServerCookieDecoder.STRICT.decode(value);
		}
		if (!cookies.isEmpty()) {
			// Reset the cookies if necessary.
			for (Cookie cookie : cookies) {
				response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
			}
		}
		// Write the response.
		ChannelFuture future = channel.writeAndFlush(response);
		// Close the connection after the write operation is done if necessary.
		if (close) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.warn(cause.getMessage(), cause);
		ctx.channel().close();
	}
}