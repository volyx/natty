module io.natty {
//	requires log4j.api;
//	requires org.apache.logging.log4j.core;
	requires slf4j.api;
	requires log4j.slf4j.impl;
	requires javax.ws.rs.api;
	requires gson;
	requires io.netty.all;

	exports io.natty;
}