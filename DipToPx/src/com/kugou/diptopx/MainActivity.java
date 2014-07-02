package com.kugou.diptopx;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView tv1;

	private TextView tv2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		tv1.setText(dipToPx(this, 19) + "");
		tv2.setText(pxToDip(this, 28) + "");
	}

	/**
	 * dip×ªpx
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	// public int dipToPx(Context context, int dipValue) {
	// return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
	// dipValue, context.getResources().getDisplayMetrics());
	// }

	public int dipToPx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * ×ª»»pxÎªdip
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public int pxToDip(Context context, int px) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
	}

}
