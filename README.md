Netty Jax-RS implementation

Example

```java
@Path("/")
@ChannelHandler.Sharable
public class HttpRestAPI extends BaseHttpHandler {

    public HttpRestAPI() {
        super("");
    }

    @GET
    @Path("{name}")
    public Response getDashboard(@PathParam("hello") String name) {
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
```

```bash
curl http://localhost:9191/world
```