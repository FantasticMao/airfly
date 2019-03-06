package cn.fantasticmao.util.airfly.jmdns;

import cn.fantasticmao.util.airfly.util.NetworkInterfaceUtils;
import lombok.extern.slf4j.Slf4j;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * MDnsService
 *
 * @author maodh
 * @since 2019/2/1
 */
@Slf4j
public class MDnsService implements Runnable {
    public static final int PORT_AIRPLAY = 7000;
    public static final int PORT_RAOP = 7001;

    private final String name;
    private final NetworkInterface networkInterface;

    public MDnsService(String name, NetworkInterface networkInterface) {
        this.name = name;
        this.networkInterface = networkInterface;
    }

    private void startBonjourService() throws SocketException {
        // 获取网卡 IP 地址
        final InetAddress inetAddress = NetworkInterfaceUtils.getAvailableInet4Address(networkInterface);
        // 获取网卡 MAC 地址
        String macAddress = NetworkInterfaceUtils.getMacAddress(networkInterface);

        try (JmDNS jmDns = JmDNS.create(inetAddress, name)) {
            if (log.isDebugEnabled()) {
                log.debug("create an instance of JmDNS named \"{}\" and bind it to the \"{}\" network interface",
                        jmDns.getName(), networkInterface.getName());
            }

            // 注册 AirPlay 服务
            AirPlayServiceInfo airPlayServiceInfo = new AirPlayServiceInfo(name + " AirPlay", PORT_AIRPLAY, macAddress);
            jmDns.registerService(airPlayServiceInfo.buildServiceInfo());
            if (log.isDebugEnabled()) {
                log.debug("\"{}\" register AirPlay service \"{}\"", jmDns.getName(), airPlayServiceInfo.getName());
            }

            // 注册 Raop 服务
            RaopServiceInfo raopServiceInfo = new RaopServiceInfo(name + " RAOP", PORT_RAOP);
            jmDns.registerService(raopServiceInfo.buildServiceInfo());
            if (log.isDebugEnabled()) {
                log.debug("\"{}\" register RAOP service \"{}\"", jmDns.getName(), raopServiceInfo.getName());
            }
        } catch (IOException e) {
            log.error("start Bonjour service error", e);
        }
    }

    @Override
    public void run() {
        try {
            this.startBonjourService();
        } catch (SocketException e) {
            log.error(e.getMessage(), e);
        }
    }
}
