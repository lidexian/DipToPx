package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yy.android.gamenews.R;

public class AutoAdjustImageView extends ImageView {

	public AutoAdjustImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AutoAdjustImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public AutoAdjustImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray types = context.obtainStyledAttributes(attrs,
				R.styleable.gamenews);
		mAutoAdjustTypeStr = types.getString(R.styleable.gamenews_adjustType);
		if(STR_AUTO_ADJUST_WIDTH.equals(mAutoAdjustTypeStr)) {
			mAutoAdjustType = AUTO_ADJUST_WIDTH;
		} else if(STR_AUTO_ADJUST_HEIGHT.equals(mAutoAdjustTypeStr)) {
			mAutoAdjustType = AUTO_ADJUST_HEIGHT;
		} else if(STR_AUTO_ADJUST_SCALE_WIDTH.equals(mAutoAdjustTypeStr)) {
			mAutoAdjustType = AUTO_ADJUST_SCALE_WIDTH;
		} else if(STR_AUTO_ADJUST_SCALE_HEIGHT.equals(mAutoAdjustTypeStr)) {
			mAutoAdjustType = AUTO_ADJUST_SCALE_HEIGHT;
		} else {
			mAutoAdjustType = AUTO_ADJUST_NONE;
		}
		mScale = types.getFloat(R.styleable.gamenews_scaleRate, 1.0f);

		types.recycle();
	}

	private String mAutoAdjustTypeStr;
	private int mAutoAdjustType;
	private float mScale = 1.0f;
	public static final int AUTO_ADJUST_NONE = 0;
	public static final int AUTO_ADJUST_WIDTH = 1; // 按图片大小自适应宽度
	public static final int AUTO_ADJUST_HEIGHT = 2; // 按图片大小自适应高度
	public static final int AUTO_ADJUST_SCALE_WIDTH = 3; // 按比例自适应宽度
	public static final int AUTO_ADJUST_SCALE_HEIGHT = 4; // 按比例自适应高度

//	private static final String STR_AUTO_ADJUST_NONE = "none";
	private static final String STR_AUTO_ADJUST_WIDTH = "auto_adjust_width";
	private static final String STR_AUTO_ADJUST_HEIGHT = "auto_adjust_height";
	private static final String STR_AUTO_ADJUST_SCALE_WIDTH = "auto_adjust_scale_width";
	private static final String STR_AUTO_ADJUST_SCALE_HEIGHT = "auto_adjust_scale_height";
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//		Drawable drawable = getDrawable();

		int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		int viewHeight = MeasureSpec.getSize(heightMeasureSpec);

		// int drawableWidth = 0;
		// int drawableHeight = 0;
		// if (drawable != null) {
		// drawableWidth = drawable.getIntrinsicWidth();
		// drawableHeight = drawable.getIntrinsicHeight();
		// }

		switch (mAutoAdjustType) {
		case AUTO_ADJUST_NONE: {

			// 不用做处理
			break;
		}
		case AUTO_ADJUST_WIDTH: {
			// TODO:根据图片的宽高比，算出view的宽度
			break;
		}
		case AUTO_ADJUST_HEIGHT: {
			// TODO:根据图片的宽高比，算出view的高度
			break;
		}
		case AUTO_ADJUST_SCALE_WIDTH: {
			viewWidth = (int) (viewHeight * mScale);
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(viewWidth,
					android.view.View.MeasureSpec.EXACTLY);
			break;
		}
		case AUTO_ADJUST_SCALE_HEIGHT: {
			viewHeight = (int) (viewWidth / mScale);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight,
					android.view.View.MeasureSpec.EXACTLY);
			break;
		}
		}

		// setMeasuredDimension(viewWidth, viewHeight);
		// ViewGroup.LayoutParams pParams = getLayoutParams();
		// if (null != pParams) {
		// pParams.width = viewWidth;
		// pParams.height = viewHeight;
		// setLayoutParams(pParams);
		// }
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}