package com.yy.android.gamenews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.R;
import com.yy.android.gamenews.ui.view.ActionBar;

public class ChannelsMoreActivity extends BaseActivity {
	private static final String FG_TAG = "CHANNELS_MORE_FG";
	private ActionBar mActionBar;
	private ChannelGridFragment mFragment;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_more);
		
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnRightClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChannelsMoreActivity.this,
						ChannelSearchActivity.class);
				startActivity(intent);
			}
		});
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		int columnId = getIntent().getIntExtra(Constants.EXTRA_COLUMN_ID, 0);
		String columnName = getIntent().getStringExtra(
				Constants.EXTRA_COLUMN_NAME);
		
		if (savedInstanceState != null) {
			mFragment = (ChannelGridFragment) getSupportFragmentManager()
					.findFragmentByTag(FG_TAG);
		} else {
			mFragment = ChannelGridFragment.newInstance(columnId, columnName);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, mFragment, FG_TAG).commit();
		}
	}
	
	
}