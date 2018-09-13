package io.volyx.netty_jax_rs.core.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Base server abstraction. Class responsible for Netty EventLoops starting amd port listening.
 *
 */
public class BaseServer {

    private static final Logger log = LogManager.getLogger(BaseServer.class);

    private final String listenAddress;
    private final int port;
    private final TransportTypeHolder transportTypeHolder;
    private final ChannelInitializer<SocketChannel> channelInitializer;

    private ChannelFuture cf;

    public BaseServer(String listenAddress, int port, TransportTypeHolder transportTypeHolder, List<Object> restApi) {
        this.listenAddress = listenAddress;
        this.port = port;
        this.transportTypeHolder = transportTypeHolder;
        List<BaseHttpHandler> handlerList = new ArrayList<>();
        for (Object o : restApi) {
            handlerList.add(new BaseHttpHandler("", o));
        }

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(HttpServerCodec.class.getSimpleName(), new HttpServerCodec())
                        .addLast(HttpServerKeepAliveHandler.class.getSimpleName(), new HttpServerKeepAliveHandler())
                        .addLast(HttpObjectAggregator.class.getSimpleName(), new HttpObjectAggregator(Integer.MAX_VALUE, true))
                        .addLast(ChunkedWriteHandler.class.getSimpleName(), new ChunkedWriteHandler())
                        .addLast(handlerList.toArray(new BaseHttpHandler[0]));

            }
        };
    }

    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    public BaseServer start() throws Exception {
        buildServerAndRun(
                transportTypeHolder.bossGroup,
                transportTypeHolder.workerGroup,
                transportTypeHolder.channelClass
        );

        return this;
    }

    protected String getServerName() {
        return "HTTPS API, WebSockets and Admin page";
    }

    public ChannelFuture close() {
        System.out.println("Shutting down HTTPS API, WebSockets and Admin server...");
        return cf.channel().close();
    }

    private void buildServerAndRun(EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                                   Class<? extends ServerChannel> channelClass) throws Exception {

        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(channelClass)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(getChannelInitializer());

            InetSocketAddress listenTo = (listenAddress == null || listenAddress.isEmpty())
                    ? new InetSocketAddress(port)
                    : new InetSocketAddress(listenAddress, port);
            this.cf = b.bind(listenTo).sync();
        } catch (Exception e) {
            log.error("Error initializing {}, port {}", getServerName(), port, e);
            throw e;
        }

        log.info("{} server listening at {} port.", getServerName(), port);
    }

}
