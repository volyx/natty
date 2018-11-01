package io.volyx.netty.jax.rs.http;

import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		try {
			HttpRestAPI httpRestAPI = new HttpRestAPI();
			List<Object> handlers = new ArrayList<>();
			handlers.add(httpRestAPI);
			Server server = new Server(ServerTest.LISTEN_ADDRESS, 8080, new TransportTypeHolder(1), handlers)
					.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}