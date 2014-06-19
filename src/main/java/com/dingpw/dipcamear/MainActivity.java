package com.dingpw.dipcamear;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.dingpw.dipcamear.http.LightHttpServer;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by dpw on 6/19/14.
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback {
    private boolean isLocal = true;
    private final String localPath = "/mnt/sdcard/demo.3gp";
    private MediaRecorder mediarecorder;// 录制视频的类
    private SurfaceView surfaceview;// 显示视频的控件
    private LocalServerSocket localServerSocket = null;
    private LocalSocket localSocket = null;
    private LocalSocket remoteSocket = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        this.surfaceview = new SurfaceView(this);


        try{
            localServerSocket = new LocalServerSocket("H264");
            localSocket = new LocalSocket();
            localSocket.connect(new LocalSocketAddress("H264"));
            localSocket.setReceiveBufferSize(500000);
            localSocket.setSendBufferSize(500000);

            remoteSocket = localServerSocket.accept();
            remoteSocket.setReceiveBufferSize(500000);
            remoteSocket.setSendBufferSize(500000);
        }catch (Exception e){

        }

        this.startService(new Intent(this,LightHttpServer.class));

        SurfaceHolder holder = surfaceview.getHolder();// 取得holder
        holder.addCallback(this); // holder加入回调接口
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setContentView(this.surfaceview);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象
        // 设置录制视频源为Camera(相机)
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置录制的视频编码h263 h264
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoSize(176, 144);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoFrameRate(20);
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
        // 设置视频文件输出的路径
        mediarecorder.setMaxDuration(0);//called after setOutputFile before prepare,if zero or negation,disables the limit
        mediarecorder.setMaxFileSize(0);//called after setOutputFile before prepare,if zero or negation,disables the limit
        if(isLocal){
            mediarecorder.setOutputFile(this.localPath);
        }else{
            FileDescriptor fileDescriptor = remoteSocket.getFileDescriptor();
            System.out.println(fileDescriptor);
            mediarecorder.setOutputFile(fileDescriptor);
        }
        try {
            // 准备录制
            mediarecorder.prepare();
            // 开始录制
            mediarecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mediarecorder != null) {
            // 停止录制
            mediarecorder.stop();
            // 释放资源
            mediarecorder.release();
            mediarecorder = null;
        }
        this.surfaceview = null;
        this.mediarecorder = null;
    }
}

