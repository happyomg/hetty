package io.hetty.server.handler;

import io.netty.handler.codec.http.multipart.InterfaceHttpPostRequestDecoder;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Created by yuck on 2015/12/12.
 */
public class HettyRequestContext {
    private MockHttpServletRequest httpServletRequest;
    private InterfaceHttpPostRequestDecoder decoder;

    public HettyRequestContext(MockHttpServletRequest httpServletRequest, InterfaceHttpPostRequestDecoder decoder) {
        this.httpServletRequest = httpServletRequest;
        this.decoder = decoder;
    }

    public MockHttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }


    public InterfaceHttpPostRequestDecoder getDecoder() {
        return decoder;
    }

}
