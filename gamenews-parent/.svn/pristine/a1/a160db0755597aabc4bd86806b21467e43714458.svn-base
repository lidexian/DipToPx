package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.duowan.gamenews.Channel;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.R;
import com.yy.android.gamenews.event.CheckExpireEvent;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.event.RefreshEvent;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;

import de.greenrobot.event.EventBus;

public class NewsFragment extends Fragment {
	private static final String TAG = NewsFragment.class.getSimpleName();

	// SectionsPagerAdapter mSectionsPagerAdapter;
	Runnable mTitleSelector;
	HorizontalScrollView mTitleContainer;
	RadioGroup mTitles;
	ViewPager mViewPager;
	private LayoutInflater mInflater;

	private Preference mPref;

	private View mAddTitle;
	private Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");

		mPref = Preference.getInstance();

		EventBus.getDefault().register(this);
	}

	@Override
	public void onResume() {
		if (mEvent != null) {
			boolean hasChanged = mEvent.isSubscribeChanged;
			boolean isMulti = mEvent.isSubscribeMultiple;
			if (hasChanged) {

				SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
						getChildFragmentManager());

				mSectionsPagerAdapter.updateDataSource(getTitles());
				mViewPager.setAdapter(mSectionsPagerAdapter);

				hasChanged = false;

				refreshTitleIndicators();

				if (isMulti) {
					mTitles.check(0);
				} else {
					mTitles.check(mTitles.getChildCount() - 1);
				}
			}
			mEvent = null;
		}

		super.onResume();
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private List<Channel> getTitles() {
		List<Channel> channelList = new ArrayList<Channel>();

		List<Channel> savedList = mPref.getMyFavorChannelList();
		if (savedList != null) {
			channelList.addAll(savedList);
		}

		// Channel recmd = new Channel();
		// recmd.setName(Constants.TITLE_RECMD);
		// channelList.add(0, recmd);

		// if(channelList.size() > 1) {
		Channel myFavor = new Channel();
		myFavor.setId(Constants.MY_FAVOR_CHANNEL_ID);
		myFavor.setName(Constants.TITLE_MY_FAVOR);
		channelList.add(0, myFavor);
		// }
		return channelList;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View view = inflater.inflate(R.layout.my_favor_news, container, false);

		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());

		mSectionsPagerAdapter.updateDataSource(getTitles());
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						Log.v(TAG, "onPageSelected " + position);
						mTitles.check(position);

						FragmentCallbackEvent event = new FragmentCallbackEvent();
						event.mEventType = FragmentCallbackEvent.FRGMT_TAB_CHANGED;
						event.mFragment = NewsFragment.this;
						EventBus.getDefault().post(event);
					}
				});

		mTitleContainer = (HorizontalScrollView) view
				.findViewById(R.id.title_container);

		mTitles = (RadioGroup) view.findViewById(R.id.titles);
		mTitles.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.v(TAG, "onCheckedChanged " + checkedId);
				if (checkedId != -1) {
					mViewPager.setCurrentItem(checkedId);
					animateToTitle(checkedId);
				}
			}
		});
		refreshTitleIndicators();

		mAddTitle = view.findViewById(R.id.add_title);
		mAddTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ChannelDepotActivity.class);
				startActivity(intent);
			}
		});

		if (savedInstanceState == null) {
			mTitles.check(0);
		}

		return view;
	}

	public void onHiddenChanged(boolean hidden) {
		if (!hidden && mAddTitle != null) {
			showTips();
		}
	}

	private void showTips() {
		int step = Preference.getInstance().getCurrentGuideStep();
		if (step == Preference.STEP_FAV) {
			if (mAddTitle != null && mAddTitle.getWidth() > 0) {
				Util.showHelpTips(getActivity(), mAddTitle, null);
				if (mHandler != null) {
					mHandler.removeCallbacksAndMessages(null);
					mHandler = null;
				}
			} else {
				if (mHandler == null) {
					mHandler = new Handler();
				}
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						showTips();
					}
				}, 200);
			}
		}
	}

	private void refreshTitleIndicators() {
		mTitles.removeAllViews();
		SectionsPagerAdapter mSectionsPagerAdapter = (SectionsPagerAdapter) mViewPager
				.getAdapter();

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			RadioButton btn = (RadioButton) mInflater.inflate(
					R.layout.my_favor_news_title, mTitles, false);
			btn.setId(i);
			btn.setText(mSectionsPagerAdapter.getPageTitle(i));
			mTitles.addView(btn);
		}
		mTitles.check(-1);
	}

	private void animateToTitle(final int id) {
		Log.v(TAG, "animateToTitle " + id);
		final View titleView = mTitles.findViewById(id);
		if (mTitleSelector != null) {
			mTitleContainer.removeCallbacks(mTitleSelector);
		}
		mTitleSelector = new Runnable() {
			@Override
			public void run() {
				int x = titleView.getLeft()
						- (mTitleContainer.getWidth() - titleView.getWidth())
						/ 2;
				Log.v(TAG, "animateToTitle " + id + " " + titleView.getLeft()
						+ " " + titleView.getWidth() + " " + x);
				mTitleContainer.smoothScrollTo(x, 0);
				mTitleSelector = null;
			}
		};
		mTitleContainer.post(mTitleSelector);
	}

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
		private List<Channel> mTitles;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public void updateDataSource(List<Channel> newTitles) {
			mTitles = newTitles;
		}
		
		public List<Channel> getDatasource() {
			return mTitles;
		}

		@Override
		public ArticleListFragment getItem(int position) {
			Channel channel = mTitles.get(position);
			ArticleListFragment fragment = ArticleListFragment
					.newInstance(channel);
			return fragment;
		}

		public Channel getData(int position) {
			return mTitles.get(position);
		}

		@Override
		public int getCount() {
			return mTitles.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mTitles.get(position).getName();
		}
	}

	public void refreshCurrent() {
		SectionsPagerAdapter mSectionsPagerAdapter = (SectionsPagerAdapter) mViewPager
				.getAdapter();
		Channel channel = mSectionsPagerAdapter.getData(mViewPager
				.getCurrentItem());
		RefreshEvent event = new RefreshEvent();
		event.mChannel = channel;
		EventBus.getDefault().post(event);
	}
	
	public void checkExpireCurrent() {
		SectionsPagerAdapter mSectionsPagerAdapter = (SectionsPagerAdapter) mViewPager
				.getAdapter();
		Channel channel = mSectionsPagerAdapter.getData(mViewPager
				.getCurrentItem());
		CheckExpireEvent event = new CheckExpireEvent();
		event.mChannel = channel;
		EventBus.getDefault().post(event);
	}

	private SubscribeEvent mEvent;

	public void onEvent(SubscribeEvent event) {
		mEvent = event;
	}
	
	public Channel getCurrentChannel() {
		SectionsPagerAdapter adapter = (SectionsPagerAdapter) mViewPager.getAdapter();
		List<Channel> dataList = adapter.getDatasource();
		int currentPosition = mViewPager.getCurrentItem();
		return dataList.get(currentPosition);
	}
}