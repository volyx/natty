package io.volyx.netty.jax.rs.http;

import okhttp3.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BaseServerTest {
	private static final Logger logger = LoggerFactory.getLogger(BaseServerTest.class);
	static final String LISTEN_ADDRESS = "127.0.0.1";
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

		final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
				.readTimeout(60, TimeUnit.SECONDS)
				.connectTimeout(60, TimeUnit.SECONDS)
				.build();


		retrofit = new Retrofit.Builder()
				.addConverterFactory(GsonConverterFactory.create())
				.client(okHttpClient)
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
			final String body = okService.form("test", "test2").execute().body();
			Assert.assertNotNull(body);
			Assert.assertEquals("test" + "test2", body);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(timeout = 10_000L)
	public void testBody() {
		final OkService okService = retrofit.create(OkService.class);

		try {
			Map<String, String> map = Map.of("1", "2");
			final Map<String, String> body = okService.body(map).execute().body();
			Assert.assertNotNull(body);
			Assert.assertEquals(map, body);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(timeout = 10_000L)
	public void testModel() {
		final OkService okService = retrofit.create(OkService.class);

		try {
			Model model = new Model();
			model.test = "123";
			final Model body = okService.model(model).execute().body();
			Assert.assertNotNull(body);
			Assert.assertEquals(model, body);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
//			(timeout = 10_000L)
	public void testFileUpload() throws URISyntaxException {
		final URL url = this.getClass().getClassLoader().getResource("test.jpg");
		Objects.requireNonNull(url);
		final OkService okService = retrofit.create(OkService.class);
		File file = new File(url.getFile());
		RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

		MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

		try {
			final String responseBody = okService.updateProfile(body).execute().body();
			Assert.assertNotNull(responseBody);
			Assert.assertEquals(file.getName(), responseBody);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
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
		Call<String> form(@Field("form") String form, @Field("form2") String from2);

		@POST("/body")
		Call<Map<String, String>> body(@Body Map<String, String> body);

		@POST("/model")
		Call<Model> model(@Body Model body);

		@Multipart
		@POST("file")
		Call<String> updateProfile(@Part MultipartBody.Part image);

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
