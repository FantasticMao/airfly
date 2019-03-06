package cn.fantasticmao.util.airfly.util;

import org.junit.Test;

import java.net.*;
import java.util.Enumeration;

/**
 * NetworkInterfaceUtilsTest
 *
 * @author maodh
 * @since 2019/1/30
 */
public class NetworkInterfaceUtilsTest {

    @Test
    public void getMacAddress() throws SocketException {
        NetworkInterface networkInterface = null;
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface ni = enumeration.nextElement();
            if (ni.getHardwareAddress() != null) {
                networkInterface = ni;
                break;
            }
        }
        if (networkInterface == null) {
            return;
        }

        String macAddress = NetworkInterfaceUtils.getMacAddress(networkInterface);
        System.out.println(macAddress);
    }

    @Test
    public void getAvailableInet4Address() throws SocketException {
        NetworkInterface networkInterface = null;
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface ni = networkInterfaceEnumeration.nextElement();
            Enumeration<InetAddress> inetAddressEnumeration = ni.getInetAddresses();
            while (inetAddressEnumeration.hasMoreElements()) {
                if (inetAddressEnumeration.nextElement() instanceof Inet4Address) {
                    networkInterface = ni;
                    break;
                }
            }
        }
        if (networkInterface == null) {
            return;
        }

        InetAddress inetAddress = NetworkInterfaceUtils.getAvailableInet4Address(networkInterface);
        System.out.println(inetAddress);
    }

    @Test
    public void getAvailableInet6Address() throws SocketException {
        NetworkInterface networkInterface = null;
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface ni = networkInterfaceEnumeration.nextElement();
            Enumeration<InetAddress> inetAddressEnumeration = ni.getInetAddresses();
            while (inetAddressEnumeration.hasMoreElements()) {
                if (inetAddressEnumeration.nextElement() instanceof Inet6Address) {
                    networkInterface = ni;
                    break;
                }
            }
        }
        if (networkInterface == null) {
            return;
        }

        InetAddress inetAddress = NetworkInterfaceUtils.getAvailableInet6Address(networkInterface);
        System.out.println(inetAddress);
    }

}