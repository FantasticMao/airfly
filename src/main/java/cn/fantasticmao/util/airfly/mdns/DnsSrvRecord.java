package cn.fantasticmao.util.airfly.mdns;

import io.netty.handler.codec.dns.DnsRecord;

/**
 * DnsSrvRecord
 *
 * @author maodh
 * @since 2019/2/27
 */
interface DnsSrvRecord extends DnsRecord {

    int priority();

    int weight();

    int port();

    String target();
}
