package cn.fantasticmao.util.airfly.mdns;

import cn.fantasticmao.util.airfly.util.NetworkInterfaceUtils;
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
import org.junit.Test;

import java.net.*;
import java.util.Arrays;

/**
 * MdnsTest
 *
 * @author maodh
 * @since 2019/2/12
 */
public class MdnsTest {

    @Test
    public void test() throws InterruptedException, SocketException {
        NetworkInterface wifiNetworkInterface = NetworkInterface.getByName("en0");
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channelFactory(() -> new NioDatagramChannel(InternetProtocolFamily.IPv4))
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.IP_MULTICAST_IF, wifiNetworkInterface)
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

            // PTR 类型记录
            DnsRecord airPlayTypePtrRecord = new DefaultDnsPtrRecord("_airplay._tcp.local.", DnsRecord.CLASS_IN, 4500, "MaoMao._airplay._tcp.local.");
            dnsResponse.addRecord(DnsSection.ANSWER, airPlayTypePtrRecord);

            // SRV 类型记录
            DnsRecord airPlayTypeSrvRecord = new DefaultDnsSrvRecord("MaoMao._airplay._tcp.local.", 7000, "maomaodeMacBook-Pro.local.");
            dnsResponse.addRecord(DnsSection.ANSWER, airPlayTypeSrvRecord);

            // TXT 类型记录
            DnsRecord airPlayTypeTxtRecord = new DefaultDnsTxtRecord("MaoMao._airplay._tcp.local.", Arrays.asList("text1", "text2"));
            dnsResponse.addRecord(DnsSection.ANSWER, airPlayTypeTxtRecord);

            // A 类型记录
            Inet4Address inet4Address = NetworkInterfaceUtils.getAvailableInet4Address(wifiNetworkInterface);
            ByteBuf ip4Buf = Unpooled.wrappedBuffer(inet4Address.getAddress());
            DnsRecord typeARecord = new DefaultDnsRawRecord("maomaodeMacBook-Pro.local.", DnsRecordType.A, 120, ip4Buf);
            dnsResponse.addRecord(DnsSection.ADDITIONAL, typeARecord);

            // AAAA 类型记录
            Inet6Address inet6Address = NetworkInterfaceUtils.getAvailableInet6Address(wifiNetworkInterface);
            ByteBuf ip6Buf = Unpooled.wrappedBuffer(inet6Address.getAddress());
            DnsRecord typeA4Record = new DefaultDnsRawRecord("maomaodeMacBook-Pro.local.", DnsRecordType.AAAA, 120, ip6Buf);
            dnsResponse.addRecord(DnsSection.ADDITIONAL, typeA4Record);

            channel.writeAndFlush(dnsResponse);
            channel.close().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}