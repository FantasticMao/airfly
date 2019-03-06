package cn.fantasticmao.util.airfly.rtsp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * RtspServerHandler
 *
 * @author maodh
 * @since 2019/1/31
 */
@Slf4j
public class RtspServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        log.info("{} {} {}", msg.method(), msg.uri(), msg.protocolVersion().toString());
        for (Map.Entry<String, String> entry : msg.headers()) {
            log.info("{}: {}", entry.getKey(), entry.getValue());
        }
        ByteBuf content = msg.content();
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        log.info(Arrays.toString(bytes));
        log.info(new String(bytes, StandardCharsets.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
