module io.natty {
	requires slf4j.api;
	requires javax.ws.rs.api;
	requires gson;
	requires io.netty.codec.http;
	requires io.netty.codec;

    requires io.netty.buffer;
    requires io.netty.transport;
	requires io.netty.transport.epoll;
	requires io.netty.common;
	requires io.netty.handler;

	exports io.natty;
}