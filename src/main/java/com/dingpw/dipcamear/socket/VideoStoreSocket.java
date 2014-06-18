package com.dingpw.dipcamear.socket;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;

public class VideoStoreSocket extends Activity{
	private Preview				mPreview;
	private static final int	ENTER_TEXT_DIALOG	= 0;
	private static final int	PROGRESS_DIALOG		= 1;

	private String				video				= "video";

	// �ļ�
	private File				myRecAudioFile;
	public final static String	PLAY_PATH			= "sdcard/DCIM/Camera";
	private File				myRecVideoDir		= new File(PLAY_PATH);
	private MediaRecorder		mMediaRecorder;
	private String				strTempFile			= "Video";

	private Context				context				= this;

	private boolean				isSave				= true;
	private Socket				socket				= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// ʹӦ�ó���ȫ��Ļ���У���ʹ��title bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		AbsoluteLayout absoluteLayout = new AbsoluteLayout(this);

		if (checkSDCard())
		{
			mPreview = new Preview(this);

			absoluteLayout.addView(mPreview, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			DrawOnTopSocket mDraw = new DrawOnTopSocket(this);
			absoluteLayout.addView(mDraw, new AbsoluteLayout.LayoutParams(60, 60, 380, 10));
		}
		else
		{
			// TextShow.mMakeTextToast(context,
			// getResources().getText(R.string.str_err_nosd).toString(), true);
		}
		setContentView(absoluteLayout);

	}


	private boolean checkSDCard()
	{
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * ׼��¼��
	 */
	private void prepareRecordVideo(SurfaceHolder mSurfaceHolder)
	{
		/* ����¼��Ƶ�ļ� */
		// try
		// {
		//
		// if (myRecAudioFile != null && myRecAudioFile.exists())
		// {
		// Log.i(video, "delete file:" + myRecAudioFile.getPath());
		// myRecAudioFile.delete();
		// }
		// if (myRecVideoDir != null && !myRecVideoDir.exists())
		// {
		// myRecVideoDir.mkdirs();
		// }
		// myRecAudioFile = File.createTempFile(strTempFile, ".3gp",
		// myRecVideoDir);
		// Log.i(video, "create file:" + myRecAudioFile.getPath());
		// }
		// catch (IOException e2)
		// {
		// e2.printStackTrace();
		// }
		//
		// Log.i(video, myRecVideoDir.getAbsolutePath());
		String hostname = "192.168.20.106";
		int port = 1234;
		try
		{
			socket = new Socket(InetAddress.getByName(hostname), port);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
		mMediaRecorder = new MediaRecorder();


		mMediaRecorder.setOutputFile(pfd.getFileDescriptor());
		mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mMediaRecorder.setVideoSize(176, 144);
		mMediaRecorder.setVideoFrameRate(15);
		mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mMediaRecorder.setMaxDuration(10000);
		try
		{
			mMediaRecorder.prepare();
		}
		catch (IllegalStateException e1)
		{
			close();
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e(video, e1.getMessage());
		}
		catch (IOException e1)
		{
			close();
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e(video, e1.getMessage());
		}
	}

	/**
	 * ��ʼ¼����Ƶ
	 * 
	 * @throws java.io.IOException
	 */
	public void startRecordVideo() throws IOException
	{
		// prepareRecordVideo();
		isSave = false;
		mMediaRecorder.start();

	}

	/**
	 * ֹͣ¼����Ƶ
	 */
	public void stopRecordVideo()
	{

		mMediaRecorder.stop();

		mMediaRecorder.reset();
		mMediaRecorder.release();

		mMediaRecorder = null;
		/* ֹͣ¼�� */

		try
		{

			socket.close();

			socket = null;
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}

		finish();
		// showDialog(ENTER_TEXT_DIALOG);

	}

	class Preview extends SurfaceView implements SurfaceHolder.Callback
	{
		SurfaceHolder	mHolder;

		Preview(Context context)
		{
			super(context);

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		}

		public void surfaceCreated(SurfaceHolder holder)
		{
			close();
			Log.d(video, "surface changed");
		}

		public void surfaceDestroyed(SurfaceHolder holder)
		{
			close();
			Log.d(video, "surface created");
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
		{

			prepareRecordVideo(holder);
			Log.d(video, "surface destroyed");
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// Log.i(video, keyCode + "");
		// if (keyCode == KeyEvent.KEYCODE_BACK)
		// {
		// Log.i(video, "quit");
		// if (isSave == false)
		// {
		// showDialog(ENTER_TEXT_DIALOG);
		// return false;
		// }
		// onDestroy();
		// }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish()
	{
		super.finish();
		try
		{
			super.finalize();
		}
		catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void close()
	{

		if (mMediaRecorder != null)
		{

			mMediaRecorder.release();
			mMediaRecorder = null;
		}

		if (myRecAudioFile != null && myRecAudioFile.exists())
		{
			Log.i(video, "delete file:" + myRecAudioFile.getPath());
			myRecAudioFile.delete();
		}

	}

}
