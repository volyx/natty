package io.volyx.netty.jax.rs.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Base server abstraction. Class responsible for Netty EventLoops starting amd port listening.
 *
 */
public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final String listenAddress;
    private final int port;
    private final TransportTypeHolder transportTypeHolder;
    private final ChannelInitializer<SocketChannel> channelInitializer;
    private final CompletableFuture<Server> startFuture = new CompletableFuture<>();

    private ChannelFuture cf;

    public Server(String listenAddress, int port, TransportTypeHolder transportTypeHolder, List<Object> restApi) {
        this.listenAddress = listenAddress;
        this.port = port;
        this.transportTypeHolder = transportTypeHolder;
        List<BaseHttpHandler> handlerList = new ArrayList<>();
        for (Object o : restApi) {
            handlerList.add(new BaseHttpHandler(o));
        }

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.
                        pipeline()
                        .addLast(HttpServerCodec.class.getSimpleName(), new HttpServerCodec())
                        .addLast(HttpServerKeepAliveHandler.class.getSimpleName(), new HttpServerKeepAliveHandler())
                        .addLast(HttpObjectAggregator.class.getSimpleName(), new HttpObjectAggregator(Integer.MAX_VALUE, true))
                        .addLast(ChunkedWriteHandler.class.getSimpleName(), new ChunkedWriteHandler())
                        .addLast(handlerList.toArray(new BaseHttpHandler[0]))
//                        .addLast(new HttpUploadServerHandler())
                ;

            }
        };
    }

    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    public Server start() throws Exception {
        buildServerAndRun(
                transportTypeHolder.bossGroup,
                transportTypeHolder.workerGroup,
                transportTypeHolder.channelClass
        );

        return this;
    }

    protected String getServerName() {
        return "Http Server";
    }

    public ChannelFuture close() {
        System.out.println("Shutting Http Server ...");
        return cf.channel().close();
    }

    private void buildServerAndRun(EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                                   Class<? extends ServerChannel> channelClass) throws Exception {

        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(channelClass)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
//                    .childOption(ChannelOption.SO_TIMEOUT, (int) TimeUnit.MINUTES.toMillis(2))
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
