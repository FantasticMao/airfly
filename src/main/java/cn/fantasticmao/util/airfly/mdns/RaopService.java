package cn.fantasticmao.util.airfly.mdns;

import io.netty.handler.codec.dns.DefaultDnsPtrRecord;
import io.netty.handler.codec.dns.DnsRecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * RaopService
 *
 * @author maodh
 * @since 2019/3/6
 */
public class RaopService {
    private final String appName;
    private final String deviceName;
    private final int port;

    private static final String RAOP_PROTOCOL = "_raop._tcp.local.";

    public RaopService(String application, String device, int port) {
        this.appName = application + "._raop._tcp.local.";
        this.deviceName = device + ".local.";
        this.port = port;
    }

    /**
     * PTR 类型记录
     */
    public DnsRecord ptr() {
        return new DefaultDnsPtrRecord(RAOP_PROTOCOL, DnsRecord.CLASS_IN, 120, appName);
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
        text.put("tp", "UDP"); // supported transport: TCP or UDP
        text.put("ft", "0x5A7FFFF7,0x1E");
        text.put("rhd", "3.0.0.0");
        text.put("cn", "1,2,3"); // audio codecs: 0 PCM, 1 Apple Lossless (ALAC), 2 AAC, 3 AAC ELD (Enhanced Low Delay)
        text.put("ch", "2"); // audio channels: stereo
        text.put("pw", "false"); // does the speaker require a password?
        text.put("sv", "false");
        text.put("vv", "2");
        text.put("txtvers", "UDP");
        text.put("ss", "16"); // audio sample size: 16-bit
        text.put("am", "MaoMao Raop"); // device model
        text.put("vs", "200.68"); // server version 130.14
        text.put("sr", "44100");  // audio sample rate: 44100HZ
        text.put("vn", "65537");
        text.put("pk", "8039806fa4625ceb0b968c0c351e6b8d3194c401ac7a04900f03bc945d83bb72");
        text.put("et", "0,3,5");  // supported encryption types: 0 no encryption, 1 RSA (AirPort Express), 3 FairPlay, 4 MFiSAP (3rd-party devices), 5 FairPlay SAPv2.5
        text.put("da", "true");
        text.put("md", "0,1,2");  // supported metadata types: 0 text, 1 artwork, 2 progress
        text.put("sf", "0x4");

        List<String> list = new ArrayList<>(text.size());
        for (Map.Entry entry : text.entrySet()) {
            list.add(entry.getKey() + "=" + entry.getValue());
        }
        return new DefaultDnsTxtRecord(appName, list);
    }
}
