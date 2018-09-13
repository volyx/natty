package com.volyx.netty_jax_rs;

import io.volyx.netty_jax_rs.core.http.JsonParser;
import io.volyx.netty_jax_rs.core.http.Response;

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
	public Response form(@FormParam("form") String query) {
		return Response.ok(JsonParser.toJson(query));
	}

	@POST
	@Path("body")
	public Response body(Map<String, String> body) {
		return Response.ok(JsonParser.toJson(body));
	}


}
