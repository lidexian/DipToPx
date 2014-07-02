package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yy.android.gamenews.R;
import com.yy.android.gamenews.util.Util;

public class CustomGifView extends View { 

	private Movie mMovie;
	private long mMovieStart;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
	private Matrix mMatrix;
	
	private static final String LOG_TAG = "CustomGifView";
	
	public CustomGifView(Context context) {
		super(context);
	}

	public CustomGifView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomGifView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private void init() {
//		mMovie = getResources().getMovie(R.drawable.welcome);
		if(mMovie == null) {
			return;
		}
		
		int screenHeight = Util.getAppHeight();
		int screenWidth = Util.getAppWidth();
		int gifWidth = mMovie.width();
		int gifHeight = mMovie.height();
		float scaleX = (float)screenWidth / (float)gifWidth;
		float scaleY = (float)screenHeight / (float)gifHeight;
		String format = "screenHeight = %d, screenWidth = %d, gifWidth = %d, gifHeight = %d, scaleX = %f, scaleY = %f";
		String msg = String.format(format, screenHeight, screenWidth, gifWidth, gifHeight, scaleX, scaleY);
		Log.d(LOG_TAG, msg);
		
		int bitmapWidth = scaleX > 1 ? screenWidth : gifWidth;
		int bitmapHeight = scaleY > 1 ? screenHeight : gifHeight;
		
		mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		mMatrix = new Matrix();
		mMatrix.postScale(scaleX, scaleY);
	}
	
	public void start() {
		init();
		invalidate();
	}
	
	public void onDraw(Canvas canvas) {
		
		if(mMovie == null) {
			return;
		}

		long now = android.os.SystemClock.uptimeMillis();
		if (mMovieStart == 0) { // first time
			mMovieStart = now;
		}
		int timePassed = (int) (now - mMovieStart);
		int dur = mMovie.duration();
		if(timePassed > dur) {
			if(mOnCompletionListener != null) {
				mOnCompletionListener.onCompleted();
			}
			return;
		}

		if (dur == 0) {
			dur = 1000;
		}
		int relTime = (int) ((now - mMovieStart) % dur);
		mMovie.setTime(relTime);
		mMovie.draw(mCanvas, 0, 0);
		
		canvas.drawBitmap(mBitmap, mMatrix, mPaint);
		invalidate();
	}
	
	public interface OnCompletionListener {
		public void onCompleted();
	}
	
	private OnCompletionListener mOnCompletionListener;
	public void setOnCompletionListener(OnCompletionListener listener) {
		mOnCompletionListener = listener;
	}
}