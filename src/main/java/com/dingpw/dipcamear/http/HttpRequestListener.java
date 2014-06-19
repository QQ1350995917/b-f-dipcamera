package com.dingpw.dipcamear.http;

import java.net.ServerSocket;

/**
 * Created by dingpw on 6/19/14.
 */
public class HttpRequestListener extends RequestListener {

    public HttpRequestListener(int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            this.construct(serverSocket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void kill() {
        super.kill();
    }
}
