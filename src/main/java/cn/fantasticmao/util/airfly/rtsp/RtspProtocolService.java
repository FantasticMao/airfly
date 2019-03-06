package cn.fantasticmao.util.airfly.rtsp;

import cn.fantasticmao.util.airfly.util.NetworkInterfaceUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.codec.rtsp.RtspEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * RtspProtocolService
 *
 * @author maodh
 * @since 2019/1/31
 */
@Slf4j
public class RtspProtocolService implements Runnable {
    private final InetAddress inetAddress;
    private final int port;

    public RtspProtocolService(NetworkInterface networkInterface, int port) {
        this.inetAddress = NetworkInterfaceUtils.getAvailableInet4Address(networkInterface);
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new RtspDecoder(), new RtspEncoder())
                                    .addLast(new HttpObjectAggregator(1 << 20))
                                    .addLast(new RtspServerHandler());
                        }
                    });

            ChannelFuture f = b.bind(inetAddress, port).sync();
            if (log.isDebugEnabled()) {
                log.debug("start RTSP service at {}:{}", inetAddress.getHostAddress(), port);
            }

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
