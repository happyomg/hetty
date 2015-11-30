package io.hetty.server;

/**
 * Created by yuck on 2015/11/29.
 */
public class HettyConfig {
    public static final String DEFAULT_BIND_ADDRESS = "127.0.0.1";
    public static final int DEFAULT_BIND_PORT = 8080;
    public static final int DEFAULT_BOSS_THREAD_NUM = 0;
    public static final int DEFAULT_WORKER_THREAD_NUM = 0;

    private String bindAddress = DEFAULT_BIND_ADDRESS;
    private int bindPort = DEFAULT_BIND_PORT;
    private int bossThreadNum = DEFAULT_BOSS_THREAD_NUM;
    private int workerThreadNum = DEFAULT_WORKER_THREAD_NUM;

    public int getBindPort() {
        return bindPort;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }

    public int getBossThreadNum() {
        return bossThreadNum;
    }

    public void setBossThreadNum(int bossThreadNum) {
        this.bossThreadNum = bossThreadNum;
    }

    public int getWorkerThreadNum() {
        return workerThreadNum;
    }

    public void setWorkerThreadNum(int workerThreadNum) {
        this.workerThreadNum = workerThreadNum;
    }
}
