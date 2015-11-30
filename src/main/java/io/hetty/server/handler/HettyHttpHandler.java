package io.hetty.server.handler;

import io.hetty.server.util.HttpHeaderUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Created by yuck on 2015/11/30.
 */
@ChannelHandler.Sharable
public class HettyHttpHandler extends SimpleChannelInboundHandler<Object> {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(HettyHttpHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.debug("requestMsg:{}",msg);
        if ((msg instanceof FullHttpRequest)) {
            FullHttpRequest req = (FullHttpRequest) msg;
            if (!req.getDecoderResult().isSuccess()) {
                DefaultFullHttpResponse ret = new DefaultFullHttpResponse(req.getProtocolVersion(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
                HttpHeaderUtil.setKeepAlive(ret, false);
                ctx.writeAndFlush(ret).addListener(ChannelFutureListener.CLOSE);
                return;
            }else{
                LOGGER.info("TODO HANDLE REQUEST");
                DefaultFullHttpResponse ret = new DefaultFullHttpResponse(req.getProtocolVersion(), HttpResponseStatus.OK);
                HttpHeaderUtil.setKeepAlive(ret, false);
                ctx.writeAndFlush(ret).addListener(ChannelFutureListener.CLOSE);
            }
//
//            HettyContext context = new HettyContext(ctx, req);
//            if (doProcess(context) == false) {
//                context.out().setStatus(HttpResponseStatus.NOT_FOUND);
//            }
//            if (context.isFinish() == false) {
//                if (context.isAsync() == false) {
//                    context.finish();
//                }
//            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        LOGGER.error("有错误！！！！！！TODO 待完善");
    }
}
