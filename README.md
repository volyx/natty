## Natty - netty JAX-RS implementation. 

Implementation supports subset of [JAX_RS](https://en.wikipedia.org/wiki/Java_API_for_RESTful_Web_Services) specification annotations.

[![Build Status](https://travis-ci.com/volyx/natty.svg?branch=master)](https://travis-ci.com/volyx/natty)

[![codecov](https://codecov.io/gh/volyx/netty-jax-rs/branch/master/graph/badge.svg)](https://codecov.io/gh/volyx/netty-jax-rs)

Example

```java

@Path("/")
public class HttpRestAPI {

    @GET
    @Path("{name}")
    public Response getDashboard(@PathParam("name") String name) {
        return Response.ok("Hello " + name + "!");
    }

    public static void main(String[] args) {
        List<Object> handlerList = new ArrayList<>();
        handlerList.add(new HttpRestAPI());
        try {
            BaseServer server = new BaseServer("127.0.0.1", 9191, new TransportTypeHolder(1), handlerList)
                .start();
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }
}
```

```bash
curl http://localhost:9191/world
```