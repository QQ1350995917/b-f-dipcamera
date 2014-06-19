package com.dingpw.dipcamear.http;

import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by dingpw on 6/19/14.
 */
public class WorkerThread extends Thread {
    private HttpService httpservice = null;
    private HttpServerConnection httpServerConnection = null;
    private Socket socket = null;

    public WorkerThread(HttpService httpservice,HttpServerConnection httpServerConnection,Socket socket){
        this.httpservice = httpservice;
        this.httpServerConnection = httpServerConnection;
        this.socket = socket;
    }

    @Override
    public void run() {
        BasicHttpContext basicHttpContext = new BasicHttpContext();
        try {
            while (!Thread.interrupted() && this.httpServerConnection.isOpen()) {
                try {
                    this.httpservice.handleRequest(this.httpServerConnection, basicHttpContext);
                } catch (UnsupportedOperationException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){

        }finally {
            try {
                OutputStream sockOutOStream = this.socket.getOutputStream();
                sockOutOStream.write(new byte[0]);
                sockOutOStream.flush();
                this.socket.close();
            } catch (IOException e) {

            }
            try {
                this.httpServerConnection.shutdown();
            } catch (Exception ignore) {

            }
        }
    }
}
