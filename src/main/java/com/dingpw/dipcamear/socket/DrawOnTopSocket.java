package com.dingpw.dipcamear.socket;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.dingpw.dipcamear.R;

public class DrawOnTopSocket extends View
{
	Bitmap			startMap;
	Bitmap			stopMap;
	boolean			isStart	= false;
	VideoStoreSocket	cameraPreview;

	public DrawOnTopSocket(VideoStoreSocket ameraPreview)
	{
		super(ameraPreview);
		cameraPreview = ameraPreview;
		startMap = BitmapFactory.decodeResource(ameraPreview.getResources(), R.drawable.start);
		stopMap = BitmapFactory.decodeResource(ameraPreview.getResources(), R.drawable.stop);
	}

	protected void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.RED);
		// canvas.drawText("Test Text", 10, 10, paint);
		if (!isStart)
		{
			canvas.drawBitmap(startMap, 10, 10, null);

		}
		else
		{
			canvas.drawBitmap(stopMap, 10, 10, null);

		}

		super.onDraw(canvas);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// float x = event.getX();
		// float y = event.getY();

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:

				invalidate();
				if (!isStart)
				{
					try
					{
						cameraPreview.startRecordVideo();
						isStart = false;
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Log.i("dd", "true");
				}
				else
				{
					cameraPreview.stopRecordVideo();

					Log.i("dd", "false");
				}
				isStart = !isStart;
				break;
			case MotionEvent.ACTION_MOVE:
				// invalidate();
				break;
			case MotionEvent.ACTION_UP:
				// invalidate();
				break;
		}
		return true;
	}
}
