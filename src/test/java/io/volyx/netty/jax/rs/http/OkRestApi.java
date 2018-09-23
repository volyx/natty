package io.volyx.netty.jax.rs.http;

import io.netty.handler.codec.http.multipart.FileUpload;

import javax.ws.rs.*;
import java.util.Map;

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

	@POST
	@Path("model")
	public Response bodyModel(Model body) {
		return Response.ok(JsonParser.toJson(body));
	}

	@POST
	@Path("file")
	public Response uploadFile(@FormParam("file") FileUpload file) {

		return Response.ok(JsonParser.toJson(file.getFilename()));
	}



}
