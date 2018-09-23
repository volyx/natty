package io.volyx.netty.jax.rs.http;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

/**
 * Used in order to re-use EventLoopGroups, this is done for performance reasons.
 * To create less threads and minimize memory footprint (recommended way by netty devs)
 */
public class TransportTypeHolder implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(TransportTypeHolder.class);

    public final EventLoopGroup bossGroup;
    public final EventLoopGroup workerGroup;
    public final Class<? extends ServerChannel> channelClass;


    public TransportTypeHolder(int workerThreads) {
        if (Epoll.isAvailable()) {
            log.info("Using native epoll transport.");
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(workerThreads);
            channelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(workerThreads);
            channelClass = NioServerSocketChannel.class;
        }
    }

    @Override
    public void close() {
        System.out.println("Stopping Transport Holder...");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

}
