package cn.fantasticmao.util.airfly.mdns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.util.List;
import java.util.Objects;

/**
 * DnsRecordCompressedDomainName
 *
 * @author maodh
 * @since 2019/2/17
 */
class DnsRecordCompressedDomainName {
    private List<String> labels;
    private short pointer;

    DnsRecordCompressedDomainName(List<String> labels, short pointer) {
        Objects.requireNonNull(labels);
        this.labels = labels;
        this.pointer = pointer;
    }

    public boolean isCompressed() {
        // 根据偏移量指针是否为零，判断域名是否被压缩过
        return pointer != 0;
    }

    public int length() {
        int length = 0;
        for (String label : labels) {
            length++; // label size: 1
            length += label.length(); // label length
        }
        if (this.isCompressed()) {
            length += Short.BYTES; // pointer size: 2
        } else {
            length += Byte.BYTES; // name without compress end field size: 1
        }
        return length;
    }

    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = Unpooled.buffer(this.length());
        for (String label : labels) {
            // 1. 输出域名字节长度
            byteBuf.writeByte(label.length());
            // 2. 输出域名字节内容
            ByteBufUtil.writeAscii(byteBuf, label);
        }
        if (this.isCompressed()) { // 如果域名已被压缩，则输出偏移量指针
            byteBuf.writeShort(pointer);
        } else { // 如果域名未被压缩，则输出零字节，表述域名输出结束
            byteBuf.writeByte(0);
        }
        return byteBuf;
    }
}
