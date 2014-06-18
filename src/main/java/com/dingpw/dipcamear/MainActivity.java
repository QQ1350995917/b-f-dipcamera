package com.dingpw.dipcamear;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends Activity{
    private String hostname = "";
    private int port = 0;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try{
            Socket socket = new Socket(InetAddress.getByName(hostname), port);
            ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
            MediaRecorder mediaRecorder = new MediaRecorder();
            mediaRecorder.setOutputFile(pfd.getFileDescriptor());
            //mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setVideoSize(176, 144);
            mediaRecorder.setVideoFrameRate(15);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setMaxDuration(10000);

        }catch (Exception e){

        }
    }


}
