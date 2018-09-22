package io.volyx.netty_jax_rs.core.http.rest;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class URIDecoder extends QueryStringDecoder implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(QueryStringDecoder.class);
	// Disk if size exceed
	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

	static {
		DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
		// on exit (in normal
		// exit)
		DiskFileUpload.baseDirectory = null; // system temp directory
		DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
		// exit (in normal exit)
		DiskAttribute.baseDirectory = null; // system temp directory
	}


	public final String[] paths;
	public final Map<String, String> pathData;
	public String contentType;
	public Map<String, String> headers;

	private HttpData partialContent;

	private InterfaceHttpPostRequestDecoder decoder;
	private ByteBuf bodyData;

	public URIDecoder(HttpRequest httpRequest, Map<String, String> extractedParams) {
		super(httpRequest.uri());
		this.paths = path().split("/");
		if (httpRequest.method() == HttpMethod.PUT || httpRequest.method() == HttpMethod.POST) {
			if (httpRequest instanceof HttpContent) {
				this.contentType = httpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE);


				if (contentType != null) {
					try {
						decoder = new HttpPostRequestDecoder(factory, httpRequest);
					} catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
						throw new RuntimeException(e);
					}

					if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
//					this.decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), httpRequest);
						try {
							decoder = new HttpPostRequestDecoder(factory, httpRequest);
						} catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
							throw new RuntimeException(e);
						}
					}
					if (decoder.isMultipart()) {

						HttpContent chunk = (HttpContent) httpRequest;
						try {
							decoder.offer(chunk);
						} catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
							throw new RuntimeException(e);
						}
						readHttpDataChunkByChunk();
						if (chunk instanceof LastHttpContent) {
//						reset();
						}
					} else {

						this.bodyData = ((HttpContent) httpRequest).content();
					}
					if (contentType != null && contentType.equals(MediaType.APPLICATION_JSON)) {
						try {
							decoder = new HttpPostRequestDecoder(factory, httpRequest);
						} catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
							throw new RuntimeException(e);
						}

						this.bodyData = ((HttpContent) httpRequest).content();

					}
				}
			}
		}
		this.pathData = extractedParams;
	}

	public List<InterfaceHttpData> getBodyHttpDatas() {
		Objects.requireNonNull(decoder, "decoder is null");
		return decoder.getBodyHttpDatas();
	}

	public String getContentAsString() {
		return bodyData.toString(StandardCharsets.UTF_8);
	}

	@Override
	public void close() {
		if (decoder != null) {
			decoder.destroy();
			decoder = null;
		}
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
						writeHttpData(data);
					} finally {
						data.release();
					}
				}
			}
			// Check partial decoding for a FileUpload
			InterfaceHttpData data = decoder.currentPartialHttpData();
			if (data != null) {
				StringBuilder builder = new StringBuilder();
				if (partialContent == null) {
					partialContent = (HttpData) data;
					if (partialContent instanceof FileUpload) {
						builder.append("Start FileUpload: ")
								.append(((FileUpload) partialContent).getFilename()).append(" ");
					} else {
						builder.append("Start Attribute: ")
								.append(partialContent.getName()).append(" ");
					}
					builder.append("(DefinedSize: ").append(partialContent.definedLength()).append(")");
				}
				if (partialContent.definedLength() > 0) {
					builder.append(" ").append(partialContent.length() * 100 / partialContent.definedLength())
							.append("% ");
				} else {
					builder.append(" ").append(partialContent.length()).append(" ");
				}
				logger.info(builder.toString());
			}
		} catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
			// end
//			responseContent.append("\r\n\r\nEND OF CONTENT CHUNK BY CHUNK\r\n\r\n");
		}
	}


	private void writeHttpData(InterfaceHttpData data) {
		if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
			FileUpload fileUpload = (FileUpload) data;
			if (fileUpload.isCompleted()) {

				Path file = Paths.get("/Users/volyx/Projects/netty-jax-rs/src/test/resources/byByteChannel.png");

				try {
					fileUpload.renameTo(file.toFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
				decoder.removeHttpDataFromClean(fileUpload); //remove
				// the File of to delete file
			} else {
//					responseContent.append("\tFile to be continued but should not!\r\n");
			}
		}
	}

	public void removeHttpDataFromClean(FileUpload fileUpload) {
		this.decoder.removeHttpDataFromClean(fileUpload);
	}
}
