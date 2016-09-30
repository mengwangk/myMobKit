package com.mymobkit.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.ui.base.BaseActivity;

import static com.mymobkit.common.LogUtils.makeLogTag;

public final class ExploreActivity extends BaseActivity {

	private static final String TAG = makeLogTag(ExploreActivity.class);

	private GridView gridView;

	public static Item[] ITEMS = new Item[] { new Item(R.string.label_howto_surveillance, R.drawable.explore_item_theme1), new Item(R.string.label_howto_api, R.drawable.explore_item_theme1),
			new Item(R.string.label_title_about, R.drawable.explore_item_theme2) };

	public static String[] PAGES = { "surveillance.html", "apis.html", "about.html" };

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explore);
		setTitle(getDefaultTitle());

		gridView = (GridView) findViewById(R.id.grid);
		gridView.setAdapter(new ExploreAdapter());
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Intent intent = new Intent(ExploreActivity.this, AboutActivity.class);
				intent.putExtra(AppConfig.PAGE_PARAM, PAGES[position]);
				startActivity(intent);
				finish();
			}
		});

		overridePendingTransition(0, 0);
	}

	@Override
	protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
		super.onNavDrawerStateChanged(isOpen, isAnimating);
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_EXPLORE;
	}

	@Override
	protected String getDefaultTitle() {
		return getString(R.string.label_explore);
	}

	private class ExploreAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return ITEMS.length;
		}

		@Override
		public Item getItem(int position) {
			return ITEMS[position];
		}

		@Override
		public long getItemId(int position) {
			return ITEMS[position].getId();
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.explore_item, viewGroup, false);
			}

			final Item item = ITEMS[position];

			CardView card = (CardView) view.findViewById(R.id.explore_card);
			ImageView image = (ImageView) view.findViewById(R.id.explore_image);
			TextView name = (TextView) view.findViewById(R.id.explore_text);
			image.setImageResource(item.image);
			name.setText(item.getTitle());
			return view;
		}
	}

	static class Item {

		private final int title;
		private final int image;

		public Item(int title, int image) {
			this.title = title;
			this.image = image;
		}

		public int getId() {
			return title + image;
		}

		public int getTitle() {
			return title;
		}

		public int getImage() {
			return image;
		}

	}
}