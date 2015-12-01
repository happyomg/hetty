package io.hetty.server;

import com.google.common.util.concurrent.AbstractIdleService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;

import javax.servlet.ServletException;

/**
 * Hetty Server 主类
 * Created by yuck on 2015/11/29.
 */
public class HettyServer extends AbstractIdleService implements EmbeddedServletContainer {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(HettyServer.class);

    protected NioEventLoopGroup bossGroup;
    protected NioEventLoopGroup workerGroup;
    protected ServerBootstrap serverBootstrap;
    protected ChannelFuture bossChannel;
    protected ChannelInboundHandlerAdapter handler;

    protected HettyConfig hettyConfig;

    public HettyServer(HettyConfig hettyConfig) {
        this.hettyConfig = hettyConfig;
    }

    @Override
    protected void startUp() throws Exception {
        LOGGER.info("Starting hetty server on {}:{} ...", hettyConfig.getBindAddress(), hettyConfig.getBindPort());
        if (serverBootstrap != null) {
            LOGGER.info("there is a hetty server runing now , try stoping ...");
            stopAsync();
        }

        bossGroup = new NioEventLoopGroup(hettyConfig.getBossThreadNum());
        workerGroup = new NioEventLoopGroup(hettyConfig.getWorkerThreadNum());
        serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.SO_REUSEADDR, CiSystemConfig.SocketReuseAddr)
//                .childOption(ChannelOption.SO_KEEPALIVE, CiSystemConfig.SocketKeepAlive)
//                .option(ChannelOption.TCP_NODELAY, CiSystemConfig.TCP_NODELAY)
//                .option(ChannelOption.SO_BACKLOG, CiSystemConfig.SocketBackLog)
//                .option(ChannelOption.SO_RCVBUF, CiSystemConfig.SocketRcvbufSize)
//                .option(ChannelOption.SO_SNDBUF, CiSystemConfig.SocketSndbufSize)
                .childHandler(initChildHandler());

        bossChannel = serverBootstrap.bind(hettyConfig.getBindPort());
        try {
            bossChannel.sync();
            LOGGER.info("Web server started at port:" + hettyConfig.getBindPort() + '.');
            LOGGER.info("Open your browser and navigate to http://" + hettyConfig.getBindAddress() + ":" + hettyConfig.getBindPort() + '/');
        } catch (InterruptedException e) {
        }
//        reload();
        LOGGER.info("Started hetty server on {}:{} .", this.hettyConfig.getBindAddress(), this.hettyConfig.getBindPort());

    }

    @Override
    protected void shutDown() throws Exception {
        LOGGER.info("Stopping hetty server on {}:{} ...", this.hettyConfig.getBindAddress(), this.hettyConfig.getBindPort());
        LOGGER.info("Stopped hetty server on {}:{} ...", this.hettyConfig.getBindAddress(), this.hettyConfig.getBindPort());
    }

    private ChannelInitializer<?> initChildHandler() throws ServletException {
        HettyChannelIniter initer = new HettyChannelIniter();
        return initer;
    }

    @Override
    public void start() throws EmbeddedServletContainerException {
        try {
            startUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws EmbeddedServletContainerException {
        try {
            shutDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPort() {
        return this.hettyConfig.getBindPort();
    }


    /**
     * Hetty Server Builder ，用于创建 Hetty Server 实例
     */
    public static class Builder {

        private HettyConfig hettyConfig;

        public Builder() {
            this.hettyConfig = new HettyConfig();
        }

        public static Builder custom() {
            return new Builder();
        }

        public static HettyServer createDefault() {
            return Builder.custom().build();
        }

        public HettyServer build() {
            return new HettyServer(this.hettyConfig);
        }

        public Builder bindAddress(String bindAddress) {
            this.hettyConfig.setBindAddress(bindAddress);
            return this;
        }

        public Builder bindPort(int bindPort) {
            this.hettyConfig.setBindPort(bindPort);
            return this;
        }

        public Builder bossThreadNum(int bossThreadNum) {
            this.hettyConfig.setBossThreadNum(bossThreadNum);
            return this;
        }

        public Builder workerThreadNum(int workerThreadNum) {
            this.hettyConfig.setWorkerThreadNum(workerThreadNum);
            return this;
        }

    }
}
