package com.derek.cshome;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.derek.cshome.util.MyContentHandler;
import com.derek.cshome.util.RssRetriverBase;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	public static final String HOME_IMAGE_URL = "http://redsox.tcs.auckland.ac.nz/CSS/CSService.svc/home_image";
	private static final String TAG = "MainActivity";

	// private static final int HOME_IMAGE_ID = 1, NEWS_RSS_ID = 2,
	// COURSES_RSS_ID = 3, EVENTS_RSS_ID = 4, SEMINORS_RSS_ID = 5;
	public static enum ELEMENT_ID {
		ALL_ELEMENTS(0), HOME_IMAGE_ID(1), NEWS_RSS_ID(2), COURSES_RSS_ID(3), EVENTS_RSS_ID(4), SEMINORS_RSS_ID(5);
		private final int value;
		private static int index = 0;
		private ELEMENT_ID (int value){
			this.value = value;
		}
		private static final Map<Integer, ELEMENT_ID> typesByValue = new HashMap<Integer, ELEMENT_ID>();

	    static {
	        for (ELEMENT_ID type : ELEMENT_ID.values()) {
	            typesByValue.put(type.value, type);
	        }
	    }
	    public static ELEMENT_ID formValue(int value) {
	        return typesByValue.get(value);
	    }
		public int getValue(){
			return this.value;
		}
	}

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	ImageView homeImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Set up the home image
		homeImageView = (ImageView) findViewById(R.id.logoBackground);
		homeImageView.setClickable(true);
		homeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				refresher(ELEMENT_ID.HOME_IMAGE_ID);
			}
		});
		refresher(ELEMENT_ID.ALL_ELEMENTS);


		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		// ActionBar.Tab#select() to do this if we have
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			Log.d(TAG, "adding tab: " + i + "/" + mSectionsPagerAdapter.getCount() + " with text: " + mSectionsPagerAdapter.getPageTitle(i));
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
		refresher(ELEMENT_ID.ALL_ELEMENTS);
		Log.d(TAG, "actionBar getTabCount: " + actionBar.getTabCount());
	}

	private void refresher(ELEMENT_ID element_id) {
		// Aga warlock with refresher is simplely just imba!
		switch (element_id) {
		case ALL_ELEMENTS:

		case HOME_IMAGE_ID:
			new HomeImageLoader().execute(HOME_IMAGE_URL);
			if (element_id != ELEMENT_ID.ALL_ELEMENTS)
				break;
		case NEWS_RSS_ID:
			;

			if (element_id != ELEMENT_ID.ALL_ELEMENTS)
				break;
		case COURSES_RSS_ID:
			;

			if (element_id != ELEMENT_ID.ALL_ELEMENTS)
				break;
		case SEMINORS_RSS_ID:
			;

			if (element_id != ELEMENT_ID.ALL_ELEMENTS)
				break;
		case EVENTS_RSS_ID:
			;

			if (element_id != ELEMENT_ID.ALL_ELEMENTS)
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_refresh:
	            refresher(ELEMENT_ID.ALL_ELEMENTS);
	            return true;
	        case R.id.staff_activity:
	            startActivity(new Intent(MainActivity.this, StaffActivity.class));
	            return true;
	        case android.R.id.home:
//	            NavUtils.navigateUpFromSameTask(this);
	        	finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}


	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private List<RssRetriverBase> fmList = new ArrayList<RssRetriverBase>() ;
//		private RetriveCoursesActivity retriveCoursesActivity;
		private RssRetriverBase retriveCoursesActivity;
		private RssRetriverBase retriveNewsActivity, retriveEventsActivity, retriveSeminarsActivity;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			retriveNewsActivity = new RssRetriverBase().newInstance(ELEMENT_ID.NEWS_RSS_ID);
//			retriveCoursesActivity = new RetriveCoursesActivity();
			retriveCoursesActivity = new RssRetriverBase().newInstance(ELEMENT_ID.COURSES_RSS_ID);
			retriveEventsActivity = new RssRetriverBase().newInstance(ELEMENT_ID.EVENTS_RSS_ID);
			retriveSeminarsActivity = new RssRetriverBase().newInstance(ELEMENT_ID.SEMINORS_RSS_ID);
			
			fmList.add(retriveNewsActivity);
			fmList.add(retriveCoursesActivity);
			fmList.add(retriveEventsActivity);
			fmList.add(retriveSeminarsActivity);
		}

		@Override
		public Fragment getItem(int position) {
			 return fmList.get(position);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return fmList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			// return fmList.get(position).get;
			CharSequence chars = "";
			if (position == 0) {
				chars = getString(R.string.title_news_rss).toUpperCase(l);
			} else if (position == 1) {
				chars = getString(R.string.title_courses_rss).toUpperCase(l);
			} else if (position == 2) {
				chars = getString(R.string.title_events_rss).toUpperCase(l);
			} else if (position == 3) {
				chars = getString(R.string.title_seminars_rss).toUpperCase(l);
			} else if (position == 4) {
				chars = "null";
			} else {
				chars = "null";
			}
			return chars;
		}
	}


	// image download etc
	class HomeImageLoader extends AsyncTask<String, Void, Drawable> {

		@Override
		protected Drawable doInBackground(String... params) {

			URL url;
			try {
				url = new URL(params[0]);

				HttpGet httpRequest = null;

				httpRequest = new HttpGet(url.toURI());
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = (HttpResponse) httpclient
						.execute(httpRequest);

				HttpEntity entity = response.getEntity();
				BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
				InputStream inputS = (InputStream) b_entity.getContent();
				return Drawable.createFromStream(inputS, "src");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Drawable result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			homeImageView.setImageDrawable(result);
			findViewById(R.id.loadingPannel).setVisibility(View.GONE);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			findViewById(R.id.loadingPannel).setVisibility(View.VISIBLE);
			// Log.i("loadingPannel",
			// "FullscreenActivity.LoadImageFromUrl.started");
		}

	}

}
