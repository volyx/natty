package com.volyx.netty_jax_rs;

import io.volyx.netty_jax_rs.core.http.JsonParser;
import io.volyx.netty_jax_rs.core.http.Response;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class OkRestApi {
	@GET
	@Path("ok")
	public Response ok() {
		return Response.ok(JsonParser.toJson("ok"));
	}
}
