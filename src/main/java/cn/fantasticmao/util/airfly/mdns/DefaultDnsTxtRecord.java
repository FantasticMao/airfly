package cn.fantasticmao.util.airfly.mdns;

import io.netty.handler.codec.dns.AbstractDnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.internal.UnstableApi;

import java.util.List;

/**
 * DefaultDnsTxtRecord
 *
 * @author maodh
 * @since 2019/2/17
 */
@UnstableApi
class DefaultDnsTxtRecord extends AbstractDnsRecord implements DnsTxtRecord {
    private final List<String> text;

    DefaultDnsTxtRecord(String name, List<String> text) {
        this(name, 120, text);
    }

    DefaultDnsTxtRecord(String name, long timeToLive, List<String> text) {
        super(name, DnsRecordType.TXT, timeToLive);
        this.text = text;
    }

    @Override
    public List<String> text() {
        return text;
    }
}
