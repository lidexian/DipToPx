package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.ArticleType;
import com.duowan.gamenews.GetFavArticleListRsp;
import com.duowan.gamenews.RefrshType;
import com.yy.android.gamenews.R;
import com.yy.android.gamenews.model.ArticleModel;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.thread.BackgroundTask;

public class MyFavorListFragment extends BaseListFragment<ArticleInfo> {
	// private static final String TAG =
	// ArticleListFragment.class.getSimpleName();

//	private IPageCache mPageCache;
	private ArticleListAdapter mAdapter;
	private List<ArticleInfo> mDataSource;
	private ListView mListView;

//	private List<Long> mViewedList;
	private GetFavArticleListRsp mRsp;
	private boolean mIsLoading;

	public static MyFavorListFragment newInstance() {
		MyFavorListFragment fragment = new MyFavorListFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public MyFavorListFragment() {
		super(0);
//		mPageCache = new IPageCache();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		mViewedList = mPageCache.getObject(
//				Constants.CACHE_KEY_VIEWED_ARTICLE_LIST, ArrayList.class);
//		if (mViewedList == null) {
//			mViewedList = new ArrayList<Long>();
//		}
		if (mDataSource == null) {
			mDataSource = new ArrayList<ArticleInfo>();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mListView = getListView();
		if (mListView != null) {
			mListView.setOnItemClickListener(mOnItemClickListener);
//			new BgTask().execute();
		}
		setNeedShowUpdatedCount(false);
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		setListAdapter(null);
		super.onDestroyView();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if ((scrollState == OnScrollListener.SCROLL_STATE_FLING || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)) {
			mAdapter.pause();
		} else {
			mAdapter.resume();
		}
		super.onScrollStateChanged(view, scrollState);
		
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Adapter adapter = (Adapter) parent.getAdapter();
			ArticleInfo model = (ArticleInfo) adapter.getItem(position);
			if (model != null) {
				switch (model.getArticleType()) {
				case ArticleType._ARTICLE_TYPE_ARTICLE: {
//
//					if (mViewedList.size() >= Constants.CACHE_SIZE_VIEWED_ARTI_LIST) {
//						mViewedList.remove(0);
//					}
//					mViewedList.add(model.getId());
//					mPageCache.setObject(
//							Constants.CACHE_KEY_VIEWED_ARTICLE_LIST,
//							mViewedList, Constants.CACHE_DURATION_FOREVER);
//					mAdapter.notifyDataSetChanged();
					ArticleDetailActivity.startArticleDetailActivity(
							getActivity(), model);
					break;
				}
				case ArticleType._ARTICLE_TYPE_SPECIAL: {
					Intent intent = new Intent(getActivity(),
							SpecialArticleActivity.class);
					intent.putExtra(SpecialArticleActivity.KEY_SPECIAL_INFO,
							model);
					startActivity(intent);
					break;
				}
				}
			}
		};
	};

	protected void requestData(final int refresh) {

		if (mIsLoading) {
			return;
		}
		mIsLoading = true;
		String attachInfo = null;
		if (mRsp != null) {
			attachInfo = mRsp.getAttachInfo();
		}
		ArticleModel.getFavArticleList(
				new ResponseListener<GetFavArticleListRsp>(getActivity()) {
					@Override
					public void onResponse(GetFavArticleListRsp data) {
						setEmptyText(strEmptyNoData);
						requestFinish(refresh, data);
					}

					@Override
					public void onError(Exception e) {
						setEmptyText(strEmptyReload);
						requestFinish(refresh, null);

						if(e != null) {
							ToastUtil.showToast(R.string.http_error);
						}
						super.onError(e);
					}
				}, // Listener
				refresh, attachInfo);
	}
	
	private void requestFinish(int refresh, GetFavArticleListRsp data) {
		mIsLoading = false;
		ArrayList<ArticleInfo> dataList = null;
		boolean hasMore = false;
		if (data != null) {
			mRsp = data;
			dataList = data.getArticleList();
			hasMore = data.hasMore;
		}
		requestFinish(refresh, dataList, hasMore, true);
	}

	@Override
	protected ImageAdapter<ArticleInfo> getAdapter() {
		if(mAdapter == null) {
			mAdapter = new ArticleListAdapter(getActivity());
		}
		return mAdapter;
	}
	
	@Override
	public void onResume() {
		
		refreshData();
		super.onResume();
	}
	
//	private class BgTask extends BackgroundTask<Void, Void, Void> {
//		@Override
//		protected Void doInBackground(Void... params) {
////			mRsp = mPageCache.getObject(Constants.CACHE_KEY_MYFAVOR_LIST, GetFavArticleListRsp.class);
//
////			mViewedList.clear();
////
////			List<Long> idList = mPageCache.getObject(
////					Constants.CACHE_KEY_VIEWED_ARTICLE_LIST, ArrayList.class);
////			if (idList != null) {
////
////				mViewedList.addAll(idList);
////			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			if (mRsp == null) {
//				loadData();
//			} else {
//				requestFinish(RefrshType._REFRESH_TYPE_REFRESH, mRsp);
//			}
//			super.onPostExecute(result);
//		}
//	}
}