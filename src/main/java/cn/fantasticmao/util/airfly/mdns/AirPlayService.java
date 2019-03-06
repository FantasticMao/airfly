package cn.fantasticmao.util.airfly.mdns;

import io.netty.handler.codec.dns.DefaultDnsPtrRecord;
import io.netty.handler.codec.dns.DnsRecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AirPlayService
 *
 * @author maodh
 * @since 2019/3/6
 */
public class AirPlayService {
    private final String appName;
    private final String deviceName;
    private final String deviceId;
    private final int port;

    private static final String AIR_PLAY_PROTOCOL = "_airplay._tcp.local.";

    public AirPlayService(String application, String device, String deviceId, int port) {
        this.appName = application + "._airplay._tcp.local.";
        this.deviceName = device + ".local.";
        this.deviceId = deviceId;
        this.port = port;
    }

    /**
     * PTR 类型记录
     */
    public DnsRecord ptr() {
        return new DefaultDnsPtrRecord(AIR_PLAY_PROTOCOL, DnsRecord.CLASS_IN, 120, appName);
    }

    /**
     * SRV 类型记录
     */
    public DnsRecord srv() {
        return new DefaultDnsSrvRecord(appName, port, deviceName);
    }

    /**
     * TXT 类型记录
     */
    public DnsRecord txt() {
        Map<String, String> text = new LinkedHashMap<>();
        text.put("deviceid", deviceId); // MAC address of the device
        text.put("features", "0x4A7FFFF7,0xE"); // bit field of supported features
        text.put("flags", "0x4");
        text.put("model", "AppleTV5,3"); // device model
        text.put("pk", "8aaf745d27e9861ee4693c4081d2449ce2c9088dd0195d94b5b2bd52962a17f8");
        text.put("pi", "f0b8b8be-c1ed-4a72-b05f-8357f1f79e07");
        text.put("srcvers", "220.68");
        text.put("vv", "2");

        List<String> list = new ArrayList<>(text.size());
        for (Map.Entry entry : text.entrySet()) {
            list.add(entry.getKey() + "=" + entry.getValue());
        }
        return new DefaultDnsTxtRecord(appName, list);
    }
}
