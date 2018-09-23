package io.volyx.netty.jax.rs.http;

import java.util.Collections;

public class Main {
	public static void main(String[] args) {
		try {
			BaseServer server = new BaseServer(BaseServerTest.LISTEN_ADDRESS, 8080, new TransportTypeHolder(1), Collections.emptyList())
					.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
