package io.hetty.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsHandler;

/**
 * Created by yuck on 2015/11/30.
 */
public class HettyChannelIniter extends ChannelInitializer<Channel> {
    private ChannelHandler handler;


    public HettyChannelIniter(ChannelHandler handler) {
        this.handler = handler;
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
//        if(corsConfig!=null){
//            pipeline.addLast("cors", new CorsHandler(corsConfig));
//        }
//        if(handler instanceof CiHttpWebSocketHandler){
//            pipeline.addLast("encoder-websocket", new WebSocketDataEncoder());
//        }
        pipeline.addLast("handler", handler);
    }
}
