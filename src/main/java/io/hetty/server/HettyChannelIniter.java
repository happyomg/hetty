package io.hetty.server;

import io.hetty.server.handler.HettyHttpHandler;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringBootMockServletContext;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;

/**
 * Created by yuck on 2015/11/30.
 */
public class HettyChannelIniter extends ChannelInitializer<Channel> {
    private final HettyHttpHandler handler;

    @Autowired
    private DispatcherServlet dispatcherServlet;

    public HettyChannelIniter() throws ServletException {
        MockServletContext servletContext = new MockServletContext();
        MockServletConfig servletConfig = new MockServletConfig(servletContext);
        XmlWebApplicationContext wac = new XmlWebApplicationContext();
        wac.setServletContext(servletContext);
        wac.setServletConfig(servletConfig);
        wac.setConfigLocation("classpath:/application-context.xml");
        wac.refresh();
        this.dispatcherServlet = new DispatcherServlet(wac);
        this.dispatcherServlet.init(servletConfig);
        this.handler = new HettyHttpHandler(this.dispatcherServlet);
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

//        if(ssl!=null){
//            pipeline.addLast(ssl.newHandler(channel.alloc()));
//        }
        pipeline.addLast("decoder", new HttpRequestDecoder());
//        if(hostHandler!=null){
//            pipeline.addLast("hosts", hostHandler);
//        }
        pipeline.addLast("aggregator", new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
//        if(corsConfig!=null){
//            pipeline.addLast("cors", new CorsHandler(corsConfig));
//        }
//        if(handler instanceof CiHttpWebSocketHandler){
//            pipeline.addLast("encoder-websocket", new WebSocketDataEncoder());
//        }
        pipeline.addLast("handler", new HettyHttpHandler(this.dispatcherServlet));
    }
}
