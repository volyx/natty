package io.volyx.netty.jax.rs.http;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.ArrayList;
import java.util.List;

@Path("/test")
public class HttpRestAPI {

    @GET
    @Path("/hello/{name}")
    public Response getDashboard(@PathParam("name") String name) {
        return Response.ok("Hello " + name + "!");
    }

    @GET
    @Path("/test/{name}")
    public Response test(@PathParam("name") String name) {
        return Response.ok(JsonParser.toJson(new Test()),  "application/json");
    }

    @POST
    @Path("create")
    public Response create(Test test) {
        return Response.ok(JsonParser.toJson(test),  "application/json");
    }

    public class Test {
        String name = "Dima";
    }


    public static void main(String[] args) {
        List<Object> handlerList = new ArrayList<>();
        handlerList.add(new HttpRestAPI());
        handlerList.add(new OkRestApi());
        try {
            BaseServer server = new BaseServer("127.0.0.1", 9191, new TransportTypeHolder(1), handlerList)
                .start();
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }
}