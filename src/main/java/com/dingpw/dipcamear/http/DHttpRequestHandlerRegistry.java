package com.dingpw.dipcamear.http;

import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.UriPatternMatcher;

import java.util.Map;

/**
 * Created by dingpw on 6/19/14.
 */
public class DHttpRequestHandlerRegistry extends HttpRequestHandlerRegistry {

    private UriPatternMatcher uriPatternMatcher = null;

    public DHttpRequestHandlerRegistry() {
        this.uriPatternMatcher = new UriPatternMatcher();
    }

    @Override
    public synchronized void register(String pattern, HttpRequestHandler httpRequestHandler) {
        this.uriPatternMatcher.register(pattern, httpRequestHandler);
    }

    @Override
    public synchronized void unregister(String pattern) {
        this.uriPatternMatcher.unregister(pattern);
    }

    @Override
    public synchronized void setHandlers(Map map) {
        this.uriPatternMatcher.setHandlers(map);
    }

    @Override
    public synchronized HttpRequestHandler lookup(String requestURI) {
        return (HttpRequestHandler) this.uriPatternMatcher.lookup(requestURI);
    }
}
