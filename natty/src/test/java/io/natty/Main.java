package io.natty;

import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		try {
			HttpRestAPI httpRestAPI = new HttpRestAPI();
			List<Object> handlers = new ArrayList<>();
			handlers.add(httpRestAPI);
			HttpServer server = new HttpServer(ServerTest.LISTEN_ADDRESS, 8080, new TransportTypeHolder(1), handlers)
					.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
