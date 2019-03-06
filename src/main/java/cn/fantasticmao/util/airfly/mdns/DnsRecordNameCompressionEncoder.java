package cn.fantasticmao.util.airfly.mdns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.dns.DefaultDnsRecordEncoder;
import io.netty.handler.codec.dns.DnsPtrRecord;
import io.netty.handler.codec.dns.DnsRecord;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DnsRecordNameCompressionEncoder
 *
 * @author maodh
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-4.1.4">Message compression</a>
 * @since 2019/2/16
 */
class DnsRecordNameCompressionEncoder extends DefaultDnsRecordEncoder {
    private static final String ROOT = ".";

    private Map<String, Short> domainNameOffsetMap = new HashMap<>(1 << 4);

    @Override
    public void encodeRecord(DnsRecord record, ByteBuf out) throws Exception {
        if (record instanceof DnsPtrRecord) {
            this.encodePtrRecord((DnsPtrRecord) record, out);
        } else if (record instanceof DnsTxtRecord) {
            this.encodeTxtRecord((DnsTxtRecord) record, out);
        } else if (record instanceof DnsSrvRecord) {
            this.encodeSrvRecord((DnsSrvRecord) record, out);
        } else {
            super.encodeRecord(record, out);
        }
    }

    private void encodePtrRecord(DnsPtrRecord record, ByteBuf out) throws Exception {
        this.encodeName(record.name(), out);
        out.writeShort(record.type().intValue());
        out.writeShort(record.dnsClass());
        out.writeInt((int) record.timeToLive());

        DnsRecordCompressedDomainName domainName = this.compressDomainName(record.hostname(), out.writerIndex(), Short.BYTES);
        // 输出 data length
        out.writeShort(domainName.length());
        // 输出 domain name
        out.writeBytes(domainName.toByteBuf());
    }

    private void encodeTxtRecord(DnsTxtRecord record, ByteBuf out) throws Exception {
        this.encodeName(record.name(), out);
        out.writeShort(record.type().intValue());
        out.writeShort(record.dnsClass());
        out.writeInt((int) record.timeToLive());

        List<String> text = record.text();
        int recordDataLength = text.size() + text.stream()
                .map(String::length)
                .filter(length -> length != 0)
                .reduce((l1, l2) -> l1 + l2)
                .orElse(0);
        // 输出 data length
        out.writeShort(recordDataLength);
        // 输出 text
        text.stream()
                .filter(str -> str.length() != 0)
                .forEach(str -> {
                    out.writeByte(str.length());
                    ByteBufUtil.writeAscii(out, str);
                });
    }

    private void encodeSrvRecord(DnsSrvRecord record, ByteBuf out) throws Exception {
        this.encodeName(record.name(), out);
        out.writeShort(record.type().intValue());
        out.writeShort(record.dnsClass());
        out.writeInt((int) record.timeToLive());

        DnsRecordCompressedDomainName domainName = this.compressDomainName(record.target(), out.writerIndex(), 4 * Short.BYTES);
        // 输出 data length
        out.writeShort(3 * Short.BYTES + domainName.length());
        // 输出 priority
        out.writeShort(record.priority());
        // 输出 weight
        out.writeShort(record.weight());
        // 输出 port
        out.writeShort(record.port());
        // 输出 target
        out.writeBytes(domainName.toByteBuf());
    }

    @Override
    protected void encodeName(String name, ByteBuf buf) throws Exception {
        DnsRecordCompressedDomainName domainName = this.compressDomainName(name, buf.writerIndex(), 0);
        buf.writeBytes(domainName.toByteBuf());
    }

    /**
     * 使用压缩算法消除 DNS 报文中重复的域名。例如当 <code>application._airplay._tcp.local</code> 域名在报文中第一次出现时，
     * 压缩算法的执行过程如下：
     * <pre>
     *   +---+-------------------------------------+------------------------------------+------------------------------+
     *   |   | check if the domain name was cached | output the first domain name label | remaining domain name labels |
     *   +---+-------------------------------------+------------------------------------+------------------------------+
     *   | 1 | application._airplay._tcp.local     | application                        | _airplay._tcp.local          |
     *   +---+-------------------------------------+------------------------------------+------------------------------+
     *   | 2 | _airplay._tcp.local                 | _airplay                           | _tcp.local                   |
     *   +---+-------------------------------------+------------------------------------+------------------------------+
     *   | 3 | _tcp.local                          | _tcp                               | local                        |
     *   +---+-------------------------------------+------------------------------------+------------------------------+
     *   | 4 | local                               | local                              | N/A                          |
     *   +---+-------------------------------------+------------------------------------+------------------------------+
     * </pre>
     *
     * @param name             域名
     * @param startOffset      起始偏移量
     * @param additionalOffset 附加偏移量
     * @see <a href="https://tools.ietf.org/html/rfc1035#section-4.1.4">Message compression</a>
     */
    private DnsRecordCompressedDomainName compressDomainName(String name, int startOffset, int additionalOffset) {
        if (ROOT.equals(name)) {
            return new DnsRecordCompressedDomainName(Collections.emptyList(), (short) 0);
        }

        List<String> labels = Arrays.asList(name.split("\\."));
        for (int i = 0, size = labels.size(); i <= size; i++) {
            List<String> prefixLabels = labels.subList(0, i); // 未被压缩的域名
            List<String> suffixLabels = labels.subList(i, size); // 需被压缩的域名
            String subName = suffixLabels.stream().collect(Collectors.joining("."));
            if (domainNameOffsetMap.containsKey(subName) || subName.length() == 0) {
                final short pointer = domainNameOffsetMap.getOrDefault(subName, (short) 0);
                return new DnsRecordCompressedDomainName(prefixLabels, pointer);
            } else {
                DnsRecordCompressedDomainName compressedDomainName = new DnsRecordCompressedDomainName(prefixLabels, (short) 0);
                final short offset = (short) ((startOffset + additionalOffset + compressedDomainName.length() - 1) | 0xC000);
                domainNameOffsetMap.put(subName, offset);
            }
        }
        throw new RuntimeException("compress domain name error");
    }
}
