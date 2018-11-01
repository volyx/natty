package io.natty;


import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class StaticFileHandlerTest {

	@GET
	public Response get() {
		return Response.ok("");
	}

}


