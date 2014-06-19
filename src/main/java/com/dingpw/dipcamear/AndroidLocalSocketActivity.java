package com.dingpw.dipcamear;

import android.app.Activity;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dpw on 6/19/14.
 */
public class AndroidLocalSocketActivity extends Activity {

    private static final String TAG = "MY_LOCAL_SOCKET";

    /** 开始演示按钮 */
    private Button button_start;
    /** 结束演示按钮 */
    private Button button_end;

    private LocalSocket receiver;
    private LocalSocket sender;
    private LocalServerSocket lss;

    /** 数据缓冲大小 */
    private static final int BUFFER_SIZE = 500000;

    /** 判断是否正在运行 */
    private boolean running;

    /** 用于计数 */
    private int i = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button_start = (Button) findViewById(R.id.start);
        button_end = (Button) findViewById(R.id.stop);

        // 设置监听事件
        button_start.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                receiver = new LocalSocket();
                try {
                    lss = new LocalServerSocket("Local_Socket");

                    receiver.connect(new LocalSocketAddress("Local_Socket"));
                    receiver.setReceiveBufferSize(BUFFER_SIZE);
                    receiver.setSendBufferSize(BUFFER_SIZE);

                    sender = lss.accept();
                    sender.setReceiveBufferSize(BUFFER_SIZE);
                    sender.setSendBufferSize(BUFFER_SIZE);

                    // 将控制器running设置为true
                    running = true;

                    // 启动发送接受线程
                    new Thread (local_send).start();
                    new Thread (local_receive).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 取消数据发送
        button_end.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                running = false;
            }
        });
    }

    // 发送线程
    Thread local_send = new Thread (){
        // 线程运行函数
        public void run() {
            OutputStream m_Send = null;
            try {
                m_Send = sender.getOutputStream();
                while(running) {
                    byte[] data = ("LOCAL-SOCKET" + i).getBytes();
                    sender.setSendBufferSize(data.length);
                    sender.setReceiveBufferSize(data.length);
                    m_Send.write(data);
                    m_Send.flush();

                    Thread.sleep(1000);
                    i ++;
                }

                m_Send.close();
                sender.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    // 接收线程
    Thread local_receive = new Thread(){
        public void run(){
            InputStream m_Rece = null;

            try {
                m_Rece = receiver.getInputStream();

                byte[] data;
                int receiveLen = 0;

                while(running) {
                    receiveLen = receiver.getReceiveBufferSize();
                    data = new byte[receiveLen];
                    m_Rece.read(data);
                    Log.i(TAG, "receiver.getReceiveBufferSize()" + receiveLen + " --- " + new String(data) + " ---");
                    Thread.sleep(1000);

                    // 将i设为0
                    i = 0;
                }

                m_Rece.close();
                receiver.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}