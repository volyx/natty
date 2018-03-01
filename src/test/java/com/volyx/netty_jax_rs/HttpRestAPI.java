package com.volyx.netty_jax_rs;

import io.netty.channel.ChannelHandler;
import io.volyx.netty_jax_rs.core.http.BaseHttpHandler;
import io.volyx.netty_jax_rs.core.http.BaseServer;
import io.volyx.netty_jax_rs.core.http.Response;
import io.volyx.netty_jax_rs.core.http.TransportTypeHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.ArrayList;
import java.util.List;

@Path("/")
@ChannelHandler.Sharable
public class HttpRestAPI extends BaseHttpHandler {

    public HttpRestAPI() {
        super("");
    }

    @GET
    @Path("{name}")
    public Response getDashboard(@PathParam("name") String name) {
        return Response.ok("Hello " + name + "!");
    }


    public static void main(String[] args) {
        List<BaseHttpHandler> handlerList = new ArrayList<>();
        handlerList.add(new HttpRestAPI());
        try {
            BaseServer server = new BaseServer("127.0.0.1", 9191, new TransportTypeHolder(1), handlerList);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}