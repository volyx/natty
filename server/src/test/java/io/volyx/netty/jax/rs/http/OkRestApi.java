package io.volyx.netty.jax.rs.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.volyx.netty.jax.rs.http.rest.params.Context;

import javax.ws.rs.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Path("/")
public class OkRestApi {
	@GET
	@Path("ok")
	public Response ok() {
		return Response.ok(JsonParser.toJson("ok"));
	}

	@POST
	@Path("post")
	public Response post(@QueryParam("query") String query) {
		return Response.ok(JsonParser.toJson(query));
	}

	@POST
	@Path("form")
	public Response form(@FormParam("form") String from, @FormParam("form2") String from2) {
		return Response.ok(JsonParser.toJson(from + from2));
	}

	@POST
	@Path("body")
	public Response body(Map<String, String> body) {
		return Response.ok(JsonParser.toJson(body));
	}

	@POST
	@Path("model")
	public Response body(Model body) {
		return Response.ok(JsonParser.toJson(body));
	}

	@GET
	@Path("context")
	public Response context(@Context ChannelHandlerContext context) {
		return Response.ok(JsonParser.toJson(context != null));
	}

	@POST
	@Path("file")
	public Response uploadFile(@FormParam("file") FileUpload fileUpload) {
		java.nio.file.Path dir = null;
		try {
			dir = Files.createTempDirectory(System.currentTimeMillis() + "");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		java.nio.file.Path filePath = dir.resolve(fileUpload.getFilename());
		try {
			if (Files.exists(filePath)) {
				Files.delete(filePath);
			} else {
				filePath = Files.createFile(filePath);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			Files.write(filePath, fileUpload.get());
//			fileUpload.renameTo(path.toFile());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return Response.ok(JsonParser.toJson(filePath.getFileName().toString()));
	}


	@GET
	@Path("getfile")
	public Response getFile() {
		final URL url = this.getClass().getClassLoader().getResource("test.jpg");
		Objects.requireNonNull(url);
		File file = new File(url.getFile());
		java.nio.file.Path path = file.toPath();
//		java.nio.file.Path path = Paths.get("/Users/volyx/Projects/netty-jax-rs/server/src/test/resources/test.jpg");

		try {
			return Response.ok(Files.readAllBytes(path), Files.probeContentType(path));
		} catch (IOException e) {
			System.err.println(e);
			return Response.serverError(e.getMessage());
		}
	}



}
