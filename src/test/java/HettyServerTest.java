import io.hetty.server.HettyServer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yuck on 2015/11/29.
 */
public class HettyServerTest {
    private HettyServer hettyServer;

    @Before
    public void setup() {
        hettyServer = HettyServer.Builder.createDefault();
        hettyServer.startAsync();
        hettyServer.awaitRunning();
    }

    @Test
    public void testPing() throws IOException {
        URL url = new URL("http://127.0.0.1:8080");
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        int responseCode = urlConn.getResponseCode();
        System.out.println("responseCode:" + responseCode);
        urlConn.disconnect();
    }

}
