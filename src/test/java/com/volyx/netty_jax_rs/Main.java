package com.volyx.netty_jax_rs;

import io.volyx.netty_jax_rs.core.http.BaseServer;
import io.volyx.netty_jax_rs.core.http.TransportTypeHolder;

import java.util.Collections;

import static com.volyx.netty_jax_rs.BaseServerTest.LISTEN_ADDRESS;

public class Main {
	public static void main(String[] args) {
		try {
			BaseServer server = new BaseServer(LISTEN_ADDRESS, 8080, new TransportTypeHolder(1), Collections.emptyList())
					.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
