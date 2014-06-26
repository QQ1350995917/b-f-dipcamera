package keturi;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.dingpw.dipcamear.R;

import java.io.IOException;

public class Vilkas04Activity extends Activity implements SurfaceHolder.Callback {

    static final public String LOG_TAG = "WifiMedia";
    RtspServer rtspServer;
    LocalServer localServer;
    MediaRecorder recorder;
    Camera mCamera;
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    int session_id = 6238;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintest2);
        recorder = new MediaRecorder();
        localServer = new LocalServer(session_id);
        rtspServer = new RtspServer(localServer);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (mCamera == null) {
            mCamera = Camera.open();
            mCamera.unlock();
        }

        recorder.setCamera(mCamera);
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        CamcorderProfile cp = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        recorder.setProfile(cp);
        recorder.setOutputFile(localServer.getSenderFileDescriptor());

        rtspServer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = Camera.open();
            mCamera.unlock();
        }

        if (recorder == null) {
            recorder.setCamera(mCamera);
            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            CamcorderProfile cp = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
            recorder.setProfile(cp);
            recorder.setOutputFile(localServer.getSenderFileDescriptor());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void release() {
        if (recorder != null) {
            recorder.reset();
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        recorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		release();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        release();
    }

}