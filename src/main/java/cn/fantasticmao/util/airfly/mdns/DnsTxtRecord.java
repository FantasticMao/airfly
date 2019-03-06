package cn.fantasticmao.util.airfly.mdns;

import io.netty.handler.codec.dns.DnsRecord;
import io.netty.util.internal.UnstableApi;

import java.util.List;

/**
 * DnsTxtRecord
 *
 * @author maodh
 * @since 2019/2/17
 */
@UnstableApi
interface DnsTxtRecord extends DnsRecord {

    List<String> text();
}
