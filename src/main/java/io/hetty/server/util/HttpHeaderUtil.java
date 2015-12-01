package io.hetty.server.util;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;

/**
 * Created by yuck on 2015/11/30.
 */
public class HttpHeaderUtil {
    public static void setKeepAlive(HttpMessage message, boolean keepAlive) {
        HttpHeaders h = message.headers();
        if (message.getProtocolVersion().isKeepAliveDefault()) {
            if (keepAlive) {
                h.remove(HttpHeaders.Names.CONNECTION);
            } else {
                h.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
            }
        } else {
            if (keepAlive) {
                h.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            } else {
                h.remove(HttpHeaders.Names.CONNECTION);
            }
        }
    }
}
