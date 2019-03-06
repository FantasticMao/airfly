package cn.fantasticmao.util.airfly.jmdns;

import org.junit.Test;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * JmdnsTest
 *
 * @author maodh
 * @since 2019/1/30
 */
public class JmdnsTest {

    @Test
    public void test() throws InterruptedException, IOException {
        // Create a JmDNS instance
        JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

        // Add a service listener
        jmdns.addServiceListener(AirPlayServiceInfo.AIR_SERVICE_TYPE, new SampleListener());

        // Wait a bit
        TimeUnit.MINUTES.sleep(1);
    }

    private static class SampleListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
        }
    }
}
