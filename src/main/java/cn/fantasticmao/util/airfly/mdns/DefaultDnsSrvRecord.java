package cn.fantasticmao.util.airfly.mdns;

import io.netty.handler.codec.dns.AbstractDnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;

/**
 * DefaultDnsSrvRecord
 *
 * @author maodh
 * @since 2019/2/27
 */
class DefaultDnsSrvRecord extends AbstractDnsRecord implements DnsSrvRecord {
    private final int priority;
    private final int weight;
    private final int port;
    private final String target;

    DefaultDnsSrvRecord(String name, int port, String target) {
        this(name, 120, 0, 0, port, target);
    }

    DefaultDnsSrvRecord(String name, long timeToLive, int priority, int weight, int port, String target) {
        super(name, DnsRecordType.SRV, timeToLive);
        this.priority = priority;
        this.weight = weight;
        this.port = port;
        this.target = target;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public int weight() {
        return weight;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public String target() {
        return target;
    }
}
