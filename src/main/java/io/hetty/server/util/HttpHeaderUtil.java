package io.hetty.server.util;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpVersion;

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

    /**
     * Returns {@code true} if and only if the connection can remain open and
     * thus 'kept alive'.  This methods respects the value of the
     * {@code "Connection"} header first and then the return value of
     * {@link HttpVersion#isKeepAliveDefault()}.
     */
    public static boolean isKeepAlive(HttpMessage message) {
        String connection = message.headers().get(HttpHeaders.Names.CONNECTION);
        if (connection != null && HttpHeaders.Values.CLOSE.equalsIgnoreCase(connection)) {
            return false;
        }

        if (message.getProtocolVersion().isKeepAliveDefault()) {
            return !HttpHeaders.Values.CLOSE.equalsIgnoreCase(connection);
        } else {
            return HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(connection);
        }
    }
}
