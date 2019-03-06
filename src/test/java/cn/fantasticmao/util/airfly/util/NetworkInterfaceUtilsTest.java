package cn.fantasticmao.util.airfly.util;

import org.junit.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * NetworkInterfaceUtilsTest
 *
 * @author maodh
 * @since 2019/1/30
 */
public class NetworkInterfaceUtilsTest {

    @Test
    public void getMacAddress() throws SocketException {
        NetworkInterface wifiNetworkInterface = NetworkInterface.getByName("en0");
        String macAddress = NetworkInterfaceUtils.getMacAddress(wifiNetworkInterface);
        System.out.println(macAddress);
    }

    @Test
    public void getAvailableInet4Address() throws SocketException {
        NetworkInterface wifiNetworkInterface = NetworkInterface.getByName("en0");
        InetAddress inetAddress = NetworkInterfaceUtils.getAvailableInet4Address(wifiNetworkInterface);
        System.out.println(inetAddress);
    }

    @Test
    public void getAvailableInet6Address() throws SocketException {
        NetworkInterface wifiNetworkInterface = NetworkInterface.getByName("en0");
        InetAddress inetAddress = NetworkInterfaceUtils.getAvailableInet6Address(wifiNetworkInterface);
        System.out.println(inetAddress);
    }

}