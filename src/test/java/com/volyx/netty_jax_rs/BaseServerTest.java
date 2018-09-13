package com.volyx.netty_jax_rs;

import io.volyx.netty_jax_rs.core.http.BaseServer;
import io.volyx.netty_jax_rs.core.http.TransportTypeHolder;
import okhttp3.OkHttpClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseServerTest {

	private static final String LISTEN_ADDRESS = "127.0.0.1";
	private static final int PORT = 9191;
	private BaseServer server;
	private Thread serverThread;
	private Retrofit retrofit;

	@Before
	public void before() {
		List<Object> handlerList = new ArrayList<>();
		handlerList.add(new HttpRestAPI());
		handlerList.add(new OkRestApi());

		serverThread = new Thread(() -> {
			try {
				server = new BaseServer(LISTEN_ADDRESS, PORT, new TransportTypeHolder(1), handlerList)
						.start();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		serverThread.start();

		while (server == null) {
			try {
				Thread.sleep(200L);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		retrofit = new Retrofit.Builder()
				.addConverterFactory(GsonConverterFactory.create())
				.client(
						new OkHttpClient.Builder().build()
				)
				.baseUrl("http://" + LISTEN_ADDRESS + ":" + PORT)
				.build();
	}

	@Test(timeout = 10_000L)
	public void testOk() {
		final OkService okService = retrofit.create(OkService.class);

		try {
			final String body = okService.ok().execute().body();
			Assert.assertNotNull(body);
			Assert.assertEquals("ok", body);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(timeout = 10_000L)
	public void testQuery() {
		final OkService okService = retrofit.create(OkService.class);

		try {
			final String body = okService.post("test").execute().body();
			Assert.assertNotNull(body);
			Assert.assertEquals("test", body);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(timeout = 10_000L)
	public void testForm() {
		final OkService okService = retrofit.create(OkService.class);

		try {
			final String body = okService.form("test").execute().body();
			Assert.assertNotNull(body);
			Assert.assertEquals("test", body);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(timeout = 10_000L)
	public void testBody() {
		final OkService okService = retrofit.create(OkService.class);

		try {
			Map<String, String> map = new HashMap<>();
			map.put("1", "2");
			final Map<String, String> body = okService.body(map).execute().body();
			Assert.assertNotNull(body);
			Assert.assertEquals(map, body);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	public static interface OkService {

		@GET("/ok")
		Call<String> ok();

		@POST("/post")
		Call<String> post(@Query("query") String query);

		@FormUrlEncoded
		@POST("/form")
		Call<String> form(@Field("form") String form);

		@POST("/body")
		Call<Map<String, String>> body(@Body Map<String, String> body);
	}

	@After
	public void after() {
		try {
			server.close().await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		serverThread.interrupt();
	}
}
