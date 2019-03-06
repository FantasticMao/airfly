package cn.fantasticmao.util.airfly.jmdns;

import lombok.Getter;

import javax.jmdns.ServiceInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * RaopServiceInfo
 *
 * @author maodh
 * @since 2019/1/30
 */
@Getter
class RaopServiceInfo {
    public static final String RAOP_SERVICE_TYPE = "_raop._tcp.local.";

    private final String name;
    private final int port;
    private final String tp = "UDP"; // supported transport: TCP or UDP
    private final String ft = "0x5A7FFFF7,0x1E";
    private final String rhd = "3.0.0.0";
    private final String cn = "1,2,3"; // audio codecs: 0 PCM, 1 Apple Lossless (ALAC), 2 AAC, 3 AAC ELD (Enhanced Low Delay)
    private final String ch = "2"; // audio channels: stereo
    private final String pw = "false"; // does the speaker require a password?
    private final String sv = "false";
    private final String vv = "2";
    private final String txtvers = "UDP";
    private final String ss = "16"; // audio sample size: 16-bit
    private final String am = "MaoMao Raop"; // device model
    private final String vs = "200.68"; // server version 130.14
    private final String sr = "44100"; // audio sample rate: 44100HZ
    private final String vn = "65537";
    private final String pk = "8039806fa4625ceb0b968c0c351e6b8d3194c401ac7a04900f03bc945d83bb72";
    private final String et = "0,3,5"; // supported encryption types: 0 no encryption, 1 RSA (AirPort Express), 3 FairPlay, 4 MFiSAP (3rd-party devices), 5 FairPlay SAPv2.5
    private final String da = "true";
    private final String md = "0,1,2"; // supported metadata types: 0 text, 1 artwork, 2 progress
    private final String sf = "0x4";

    RaopServiceInfo(String name, int port) {
        this.name = name;
        this.port = port;
    }

    ServiceInfo buildServiceInfo() {
        Map<String, String> text = new HashMap<>();
        text.put("tp", tp);
        text.put("ft", ft);
        text.put("rhd", rhd);
        text.put("cn", cn);
        text.put("ch", ch);
        text.put("pw", pw);
        text.put("sv", sv);
        text.put("vv", vv);
        text.put("txtvers", txtvers);
        text.put("ss", ss);
        text.put("am", am);
        text.put("vs", vs);
        text.put("sr", sr);
        text.put("vn", vn);
        text.put("pk", pk);
        text.put("et", et);
        text.put("da", da);
        text.put("md", md);
        text.put("sf", sf);
        return ServiceInfo.create(RAOP_SERVICE_TYPE, name, port, 0, 0, text);
    }
}
