package com.dingpw.dipcamear.http;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dingpw on 6/19/14.
 */
public class RequestListener extends Thread {

    private ServerSocket serverSocket = null;
    private HttpService httpService = null;
    private BasicHttpProcessor basicHttpProcessor = null;
    private HttpParams httpParams = null;
    private DHttpRequestHandlerRegistry dHttpRequestHandlerRegistry = null;

    protected RequestListener(){
        this.httpParams = new BasicHttpParams();
        this.httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HTTP-SERVER");

        this.basicHttpProcessor = new BasicHttpProcessor();
        this.basicHttpProcessor.addInterceptor(new ResponseDate());
        this.basicHttpProcessor.addInterceptor(new ResponseServer());
        this.basicHttpProcessor.addInterceptor(new ResponseContent());
        this.basicHttpProcessor.addInterceptor(new ResponseConnControl());
        this.httpService = new HttpService(this.basicHttpProcessor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());

        this.dHttpRequestHandlerRegistry = new DHttpRequestHandlerRegistry();
        this.httpService.setHandlerResolver(this.dHttpRequestHandlerRegistry);
    }

    protected void construct(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        start();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try{
                Socket socket = this.serverSocket.accept();
                DefaultHttpServerConnection defaultHttpServerConnection = new DefaultHttpServerConnection();
                defaultHttpServerConnection.bind(socket, this.httpParams);
                WorkerThread t = new WorkerThread(this.httpService, defaultHttpServerConnection, socket);
                t.setDaemon(true);
                t.start();
            }catch (Exception e){

            }
        }
    }

    protected void kill() {
        try {
            this.serverSocket.close();
        } catch (IOException ignore) {}
        try {
            this.join();
        } catch (InterruptedException ignore) {

        }
    }
}
