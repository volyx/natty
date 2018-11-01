package io.volyx.netty.jax.rs.http;

import io.netty.handler.codec.http.HttpMethod;
import io.volyx.netty.jax.rs.http.rest.HandlerWrapper;
import io.volyx.netty.jax.rs.http.rest.params.Param;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/api")
public class TestAnnotationsUtil {

	@GET
	@Path("/test/{name}/{surname}")
	public Response test(@PathParam("name") String name, @PathParam("surname") String surname) {

		return Response.ok("ok");
	}

	@Test
	public void test() {
		final HandlerWrapper[] register = AnnotationsUtil.register(new TestAnnotationsUtil());
		Assert.assertNotNull(register);
		Assert.assertEquals(1, register.length);
		final HandlerWrapper wrapper = register[0];
		Assert.assertNotNull(wrapper);
		Assert.assertEquals(HttpMethod.GET, wrapper.httpMethod);
		Assert.assertEquals("/api/test/{name}/{surname}", wrapper.uriTemplate.getUrlPattern());
		Assert.assertNotNull(wrapper.params);
		Assert.assertEquals(2, wrapper.params.length);
		final Param nameParam = wrapper.params[0];
		Assert.assertNotNull(nameParam);
		Assert.assertEquals("name", nameParam.getName());
		Assert.assertEquals(String.class, nameParam.getType());
		final Param surnameParam = wrapper.params[1];
		Assert.assertNotNull(surnameParam);
		Assert.assertEquals("surname", surnameParam.getName());
		Assert.assertEquals(surnameParam.getType(), surnameParam.getType());
	}

	@Test
	public void testStatic() {
		final HandlerWrapper[] register = AnnotationsUtil.register(new StaticFileHandlerTest());
		Assert.assertNotNull(register);
		Assert.assertEquals(1, register.length);
	}
}
