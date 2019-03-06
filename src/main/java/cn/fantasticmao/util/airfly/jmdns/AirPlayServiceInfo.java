package cn.fantasticmao.util.airfly.jmdns;

import lombok.Getter;

import javax.jmdns.ServiceInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * AirPlayServiceInfo
 *
 * @author maodh
 * @since 2019/1/30
 */
@Getter
class AirPlayServiceInfo {
    public static final String AIR_SERVICE_TYPE = "_airplay._tcp.local.";

    public static final int FEATURE_VIDEO = 1 << 0;
    public static final int FEATURE_PHOTO = 1 << 1;
    public static final int FEATURE_VIDEO_FAIR_PLAY = 1 << 2;
    public static final int FEATURE_VIDEO_VOLUME_CONTROL = 1 << 3;
    public static final int FEATURE_VIDEO_HTTP_LIVE_STREAMS = 1 << 4;
    public static final int FEATURE_SLIDESHOW = 1 << 5;
    public static final int FEATURE_SCREEN = 1 << 7;
    public static final int FEATURE_SCREEN_ROTATE = 1 << 8;
    public static final int FEATURE_AUDIO = 1 << 9;
    public static final int FEATURE_AUDIO_REDUNDANT = 1 << 11;

    private final String name;
    private final int port;
    private final String deviceId; // MAC address of the device
    private final String features = "0x4A7FFFF7,0xE"; // bit field of supported features
    private final String flags = "0x4";
    private final String model = "AppleTV5,3"; // device model
    private final String pk = "8aaf745d27e9861ee4693c4081d2449ce2c9088dd0195d94b5b2bd52962a17f8";
    private final String pi = "f0b8b8be-c1ed-4a72-b05f-8357f1f79e07";
    private final String srcvers = "220.68";
    private final String vv = "2";

    AirPlayServiceInfo(String name, int port, String deviceId) {
        this.name = name;
        this.port = port;
        this.deviceId = deviceId;
    }

    ServiceInfo buildServiceInfo() {
        Map<String, String> text = new HashMap<>();
        text.put("deviceid", deviceId);
        text.put("features", features);
        text.put("flags", flags);
        text.put("model", model);
        text.put("pk", pk);
        text.put("pi", pi);
        text.put("srcvers", srcvers);
        text.put("vv", vv);
        return ServiceInfo.create(AIR_SERVICE_TYPE, name, port, 0, 0, text);
    }
}
