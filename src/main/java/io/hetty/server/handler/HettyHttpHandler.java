package io.hetty.server.handler;

import io.hetty.server.handler.fileupload.HettyMultipartFile;
import io.hetty.server.util.HttpHeaderUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by yuck on 2015/11/30.
 */
@ChannelHandler.Sharable
public class HettyHttpHandler extends SimpleChannelInboundHandler<Object> {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(HettyHttpHandler.class);

    private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk

    private final Servlet servlet;
    private final ServletContext servletContext;

    public HettyHttpHandler(Servlet servlet, ServletContext servletContext) {
        this.servlet = servlet;
        this.servletContext = servletContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.debug("requestMsg:{}", msg);
        if ((msg instanceof FullHttpRequest)) {
            FullHttpRequest req = (FullHttpRequest) msg;
            if (!req.getDecoderResult().isSuccess()) {
                DefaultFullHttpResponse ret = new DefaultFullHttpResponse(req.getProtocolVersion(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
                HttpHeaderUtil.setKeepAlive(ret, false);
                ctx.writeAndFlush(ret).addListener(ChannelFutureListener.CLOSE);
                return;
            } else {
                HettyRequestContext requestContext = null;
                try {
                    requestContext = createServletRequest(req);
                    MockHttpServletRequest servletRequest = requestContext.getHttpServletRequest();
                    MockHttpServletResponse servletResponse = new MockHttpServletResponse();

                    this.servlet.service(servletRequest, servletResponse);

                    HttpResponseStatus status = HttpResponseStatus.valueOf(servletResponse.getStatus());
                    HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);

                    for (String name : servletResponse.getHeaderNames()) {
                        for (Object value : servletResponse.getHeaderValues(name)) {
                            response.headers().add(name, value);
                        }
                    }

                    // Write the initial line and the header.
                    if (status.code() < 200 || status.code() >= 400) {
                        HttpHeaderUtil.setKeepAlive(response, false);
                    }
                    ctx.write(response);

                    ChannelFuture channelFuture;
                    if (!StringUtil.isNullOrEmpty(servletResponse.getErrorMessage())) {
                        channelFuture = ctx.write(servletResponse.getErrorMessage());
                    } else {
                        // Write the content.
                        final InputStream contentStream = new ByteArrayInputStream(servletResponse.getContentAsByteArray());
                        channelFuture = ctx.write(new ChunkedStream(contentStream));
                    }
                    ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
                } finally {
                    if (requestContext != null && requestContext.getDecoder() != null) {
                        requestContext.getDecoder().destroy();
                    }
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        ByteBuf content = Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        HttpHeaderUtil.setKeepAlive(response, false);
        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private HettyRequestContext createServletRequest(FullHttpRequest httpRequest) throws IOException {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(httpRequest.getUri()).build();
        HttpMethod httpMethod = httpRequest.getMethod();
        MockHttpServletRequest servletRequest;
        InterfaceHttpPostRequestDecoder decoder = null;
        if (HttpMethod.GET.equals(httpMethod) || HttpMethod.DELETE.equals(httpMethod)) {
            servletRequest = new MockHttpServletRequest(this.servletContext);
        } else {
            servletRequest = new MockMultipartHttpServletRequest(this.servletContext);
        }

        servletRequest.setRequestURI(uriComponents.getPath());
        servletRequest.setPathInfo(uriComponents.getPath());
        servletRequest.setMethod(httpMethod.name());

        if (uriComponents.getScheme() != null) {
            servletRequest.setScheme(uriComponents.getScheme());
        }
        if (uriComponents.getHost() != null) {
            servletRequest.setServerName(uriComponents.getHost());
        }
        if (uriComponents.getPort() != -1) {
            servletRequest.setServerPort(uriComponents.getPort());
        }
        for (Map.Entry<String, String> headerEntry : httpRequest.headers()) {
            servletRequest.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        if (HttpMethod.GET.equals(httpMethod) || HttpMethod.DELETE.equals(httpMethod)) {
            servletRequest.setContent(httpRequest.content().array());
        } else if (HttpMethod.POST.equals(httpMethod) || HttpMethod.PUT.equals(httpMethod)) {
            MockMultipartHttpServletRequest multipartHttpServletRequest = (MockMultipartHttpServletRequest) servletRequest;
            decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, httpRequest);

            List<InterfaceHttpData> httpDatas = decoder.getBodyHttpDatas();
            for (InterfaceHttpData data : httpDatas) {
                System.out.println(data.getName() + ", " + data.getHttpDataType());
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    String key = data.getName();
                    if (key.endsWith("[]")) {//spring mvc不支持直接以[]结尾的参数
                        key = key.replace("[]", "");
                    }
                    multipartHttpServletRequest.addParameter(key, ((Attribute) data).getValue());
                } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    MixedFileUpload fileUpload = (MixedFileUpload) data;
                    if (fileUpload.isCompleted()) {
                        if (fileUpload.isInMemory()) {
                            MockMultipartFile multipartFile = new MockMultipartFile(fileUpload.getName(), fileUpload.getFilename(), fileUpload.getContentType(), fileUpload.get());
                            multipartHttpServletRequest.addFile(multipartFile);
                            System.out.println(multipartFile);
                        } else {
                            System.out.println(fileUpload);
                            multipartHttpServletRequest.addFile(new HettyMultipartFile(fileUpload));
                        }
                    } else {
                        LOGGER.warn("{} not complete!!!!!!", fileUpload);
                    }
//                    throw new UnsupportedOperationException("FileUpload is unsupported !");
                }
            }
        }

        try {
            if (uriComponents.getQuery() != null) {
                String query = UriUtils.decode(uriComponents.getQuery(), "UTF-8");
                servletRequest.setQueryString(query);
            }

            for (Map.Entry<String, List<String>> entry : uriComponents.getQueryParams().entrySet()) {
                for (String value : entry.getValue()) {
                    servletRequest.addParameter(
                            UriUtils.decode(entry.getKey(), "UTF-8"),
                            UriUtils.decode(value, "UTF-8"));
                }
            }
        } catch (UnsupportedEncodingException ex) {
            // shouldn't happen
            LOGGER.error(ex.getMessage(), ex);
        }

        return new HettyRequestContext(servletRequest, decoder);
    }


}
