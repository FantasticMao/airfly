package cn.fantasticmao.util.airfly.mdns;

import org.junit.Test;

import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * BonjourTest
 *
 * @author maodh
 * @since 2019/3/6
 */
public class BonjourTest {

    @Test
    public void test() throws SocketException {
        NetworkInterface wifi = NetworkInterface.getByName("en0");
        Bonjour bonjour = new Bonjour("MaoMao", "maomaodeMacBook-Pro", wifi);
        bonjour.run();
    }
}
