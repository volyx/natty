package io.volyx.netty.jax.rs.http;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @GET
    @Path("/file")
    public Response file() {
        final URL url = this.getClass().getClassLoader().getResource("test.jpg");
        Objects.requireNonNull(url);
        File file = new File(url.getFile());
        try {
            final String probeContentType = Files.probeContentType(file.toPath());
            System.out.println(probeContentType);
            return Response.ok(Files.readAllBytes(file.toPath()), probeContentType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.notFound();
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
            Server server = new Server("127.0.0.1", 9191, new TransportTypeHolder(1), handlerList)
                .start();
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }
}