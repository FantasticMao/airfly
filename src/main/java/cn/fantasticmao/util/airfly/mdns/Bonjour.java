package cn.fantasticmao.util.airfly.mdns;

import cn.fantasticmao.util.airfly.util.NetworkInterfaceUtils;
import com.google.common.base.Strings;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.*;
import lombok.extern.slf4j.Slf4j;

import java.net.*;

/**
 * Bonjour
 *
 * @author maodh
 * @since 2019/3/6
 */
@Slf4j
public class Bonjour implements Runnable {
    private final String deviceName;
    private final NetworkInterface networkInterface;
    private final AirPlayService airPlayService;
    private final RaopService raopService;

    public Bonjour(String application, String device, NetworkInterface networkInterface) throws SocketException {
        if (Strings.isNullOrEmpty(application) || Strings.isNullOrEmpty(device)) {
            throw new IllegalArgumentException("application and device cannot be null or empty");
        }
        this.deviceName = device + ".local.";
        this.networkInterface = networkInterface;

        final String deviceId = NetworkInterfaceUtils.getMacAddress(networkInterface);
        this.airPlayService = new AirPlayService(application, device, deviceId, 7000);
        this.raopService = new RaopService(application, device, 7001);
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channelFactory(() -> new NioDatagramChannel(InternetProtocolFamily.IPv4))
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.IP_MULTICAST_IF, networkInterface)
                    .localAddress(new InetSocketAddress(5353))
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new DatagramDnsResponseEncoder(new DnsRecordNameCompressionEncoder()));
                        }
                    });
            Channel channel = bootstrap.bind().sync().channel();

            InetSocketAddress sender = new InetSocketAddress(5353);
            InetSocketAddress recipient = new InetSocketAddress("224.0.0.251", 5353);
            DatagramDnsResponse dnsResponse = new DatagramDnsResponse(sender, recipient, 0);
            dnsResponse.setAuthoritativeAnswer(true);

            // _airplay._tcp.local.
            dnsResponse.addRecord(DnsSection.ANSWER, airPlayService.ptr());
            dnsResponse.addRecord(DnsSection.ANSWER, airPlayService.srv());
            dnsResponse.addRecord(DnsSection.ANSWER, airPlayService.txt());

            // _raop._tcp.local.
            dnsResponse.addRecord(DnsSection.ANSWER, raopService.ptr());
            dnsResponse.addRecord(DnsSection.ANSWER, raopService.srv());
            dnsResponse.addRecord(DnsSection.ANSWER, raopService.txt());

            dnsResponse.addRecord(DnsSection.ADDITIONAL, this.a());
            dnsResponse.addRecord(DnsSection.ADDITIONAL, this.aaaa());

            channel.writeAndFlush(dnsResponse);
            channel.close().sync();
        } catch (Exception e) {
            log.error("bootstrap bonjour service error", e);
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * A 类型记录
     */
    public DnsRecord a() {
        Inet4Address inet4Address = NetworkInterfaceUtils.getAvailableInet4Address(networkInterface);
        ByteBuf ip4Buf = Unpooled.wrappedBuffer(inet4Address.getAddress());
        return new DefaultDnsRawRecord(deviceName, DnsRecordType.A, 120, ip4Buf);
    }

    /**
     * AAAA 类型记录
     */
    public DnsRecord aaaa() {
        Inet6Address inet6Address = NetworkInterfaceUtils.getAvailableInet6Address(networkInterface);
        ByteBuf ip6Buf = Unpooled.wrappedBuffer(inet6Address.getAddress());
        return new DefaultDnsRawRecord(deviceName, DnsRecordType.AAAA, 120, ip6Buf);
    }

}
