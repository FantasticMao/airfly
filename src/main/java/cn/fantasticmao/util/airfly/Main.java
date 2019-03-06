package cn.fantasticmao.util.airfly;

import cn.fantasticmao.util.airfly.mdns.Bonjour;
import cn.fantasticmao.util.airfly.rtsp.RtspProtocolService;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.concurrent.TimeUnit;

/**
 * Main
 *
 * @author maodh
 * @since 2019/1/29
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 选择无线网卡 en0
        final NetworkInterface wifi = NetworkInterface.getByName("en0");

        // 开启 Bonjour 服务
        new Bonjour("MaoMao", "maomaodeMacBook-Pro", wifi).run();

        // 开启 Rtsp 服务
        new RtspProtocolService(wifi, 7000).run();

        // Wait a bit
        TimeUnit.HOURS.sleep(1);
    }
}
