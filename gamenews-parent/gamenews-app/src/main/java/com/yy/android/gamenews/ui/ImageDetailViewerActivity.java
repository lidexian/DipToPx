package com.yy.android.gamenews.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.gamenews.Image;
import com.duowan.gamenews.ImageType;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.yy.android.gamenews.R;
import com.yy.android.gamenews.util.FileUtil;

public class ImageDetailViewerActivity extends BaseActivity {

	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static final String EXTRA_CURRENT_IMAGE = "currentimage";
	private ViewPager mPager;
	private ImageAdapter mAdapter;
	private TextView mPageNum;
	// private List<String> mUrls;
	private ArrayList<Image> mImages;

	private ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			// TODO Auto-generated method stub
			View v = (View) view.getTag();
			v.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
			v.findViewById(R.id.download_failed).setVisibility(View.INVISIBLE);

		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			View v = (View) view.getTag();
			v.findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
			v.findViewById(R.id.download_failed).setVisibility(View.VISIBLE);

		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			View v = (View) view.getTag();
			v.findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
			v.findViewById(R.id.download_failed).setVisibility(View.INVISIBLE);

		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_detail_viewer);
		// mUrls = (List<String>) getIntent().getSerializableExtra(
		// EXTRA_IMAGE_LIST);
		mImages = (ArrayList<Image>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);
		int pos = getIntent().getIntExtra(EXTRA_CURRENT_IMAGE, 0);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPageNum = (TextView) findViewById(R.id.page_number);
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);

		ArrayList<View> views = new ArrayList<View>(mImages.size());
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (Image url : mImages) {
			View view = inflater.inflate(R.layout.article_detail_image_detail,
					null);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}

			});
			views.add(view);
		}
		mAdapter = new ImageAdapter(views);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(pos);
		mPageNum.setText(String.format("%d/%d", pos+1, mAdapter.getCount()));
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			public void onPageSelected(int position) {
				mPageNum.setText(String.format("%d/%d", position + 1,
						mAdapter.getCount()));
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		findViewById(R.id.download).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = mImages.get(mPager.getCurrentItem()).getUrls()
						.get(ImageType._IMAGE_TYPE_BIG).getUrl();
				if (ImageLoader.getInstance().getDiscCache().get(url).exists()) {
					String saveFileName = FileUtil.saveImage(url);
					if (saveFileName != null) {
						Toast.makeText(
								ImageDetailViewerActivity.this,
								getResources()
										.getString(R.string.download_success,
												saveFileName),
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(ImageDetailViewerActivity.this,
							R.string.download_not_ready, Toast.LENGTH_SHORT)
							.show();
				}

			}

		});

	}

	public class ImageAdapter extends PagerAdapter {
		private ArrayList<View> Views;// 存放View的ArrayList

		/*
		 * ViewAdapter构造函数
		 * 
		 * @author：Robin
		 */
		public ImageAdapter(ArrayList<View> Views) {
			this.Views = Views;
		}

		/*
		 * 返回View的个数
		 */
		@Override
		public int getCount() {
			if (Views != null) {
				return Views.size();
			}
			return 0;
		}

		/*
		 * 销毁View
		 */
		@Override
		public void destroyItem(View container, int position, Object object) {
			View v = Views.get(position);
			if (v == null) {
				return;
			}
			ImageView image = (ImageView) v.findViewById(R.id.image);
			if (image == null) {
				return;
			}
			image.setImageDrawable(null);
			ImageLoader.getInstance().cancelDisplayTask(image);
			((ViewPager) container).removeView(Views.get(position));
		}

		/*
		 * 初始化
		 */
		@Override
		public Object instantiateItem(View container, int position) {
			View v = Views.get(position);
			ImageView image = (ImageView) v.findViewById(R.id.image);
			image.setTag(v);
			String url = mImages.get(position).getUrls()
					.get(ImageType._IMAGE_TYPE_BIG).getUrl();
			ImageLoader.getInstance().displayImage(url, image,
					mImageLoadingListener);
			((ViewPager) container).addView(v, 0);
			return v;

		}

		/*
		 * 判断View是否来自Object
		 */
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}
	}

}