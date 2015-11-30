package io.hetty.server;

/**
 * Created by yuck on 2015/11/30.
 */
public class DemoApplication {
    public static void main(String[] args) {
        HettyServer hettyServer = HettyServer.Builder.createDefault();
        hettyServer.startAsync();
        hettyServer.awaitRunning();
    }
}
