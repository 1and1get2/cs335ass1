package com.derek.cshome;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.derek.cshome.MainActivity.ELEMENT_ID;
import com.derek.cshome.R.id;
//import com.derek.cshome.util.MyContactHelper;
import com.derek.cshome.util.MyContentHandler;
import com.derek.cshome.util.MyCoursesContentHandler;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class StaffActivity extends ListActivity implements OnItemClickListener, OnItemLongClickListener{

	public static String STAFF_XML_URL = "http://redsox.tcs.auckland.ac.nz/CSS/CSService.svc/people";
	public static String PHOTO_SERVER = "http://www.cs.auckland.ac.nz/our_staff/vcard.php?upi=";
	protected UpiDownloader upiD;
	protected BackgroundWorker backgroundWorker;
	protected List<String> UPIs = new ArrayList<String>();
	// private List<Staff> staffList = new ArrayList<StaffActivity.Staff>();
	protected HashMap<String, Staff> staffHM = new HashMap<String, StaffActivity.Staff>();
	protected List<Staff> staffList = new ArrayList<StaffActivity.Staff>();
	protected ProgressDialog progress;// = new ProgressDialog(ac);
	protected Bundle messager;
	protected MySimpleArrayAdapter contactAdapter;
	protected ListView listView;

	// protected Activity stAc;
	// progress.setMessage("Loading...")

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_layout);
		listView = this.getListView();
//		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //list.setItemChecked(0, true);
		listView.setOnItemClickListener(this);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// upiDownloader
		messager = new Bundle();
		progress = new ProgressDialog(this);
		progress.setMessage("retrieving UPI list....");
		upiD = new UpiDownloader(this);
		upiD.execute(STAFF_XML_URL);
		progress.setMessage("UPI List Retrived");
		progress.dismiss();

		backgroundWorker = new BackgroundWorker(staffHM, this);

		Log.w("StaffActivity", "staffHM.size() = " + staffHM.size());


		//
	}
	

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

		return false;
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.d("StaffActivity", "received url request on arg2=" + arg2 + "\n" + staffList.get(arg2).getName());
		String url ;
		if ((url = staffList.get(arg2).getUrl()) != null && !url.trim().equals("")){
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
					.parse(url));
			startActivity(browserIntent);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.staff, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class UpiDownloader extends AsyncTask<String, Void, List> {
		private List<String> list = new ArrayList<String>();
		private SAXParserFactory saxPF;
		private XMLReader xmlReader;
		private Activity ac;

		public UpiDownloader(Activity ac) {
			this.ac = ac;
		}

		@Override
		protected List doInBackground(String... arg0) {
			saxPF = SAXParserFactory.newInstance();
			try {
				xmlReader = saxPF.newSAXParser().getXMLReader();

				xmlReader.setContentHandler(new UpiParser());
				xmlReader.parse(new InputSource(new URL(arg0[0]).openStream()));
			} catch (Exception e) {
				Log.e("UpiDownloader", e.toString());
			}
			// this.onProgressUpdate();
			Log.d("StaffActivity", "doInBackground: list.size()" + list.size());
			// UPIs = list;
			return list;
		}

		@Override
		protected void onPostExecute(List result) {
			Log.d("StaffActivity",
					"onPostExecute: result.size()" + result.size());
			UPIs = list;
			for (String str : UPIs) {
				// backgroundWorker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
				// str);
				// backgroundWorker.execute( str);

				try {
					new BackgroundWorker(staffHM, ac).executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, str);
				} catch (Exception e) {
					Log.e("StaffActivity", "exception: " + e.toString());
				}
			}
			Log.d("StaffActivity", "onPostExecute: all thread added");
			new Toast(ac).makeText(ac, "received " + list.size() + " upis", 1);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// new Toast(ac).makeText(ac, "received " + list.size() + " upis",
			// 1);

		}

		class UpiParser extends DefaultHandler {
			// UPIs
			private String str = "";

			@Override
			public void characters(char[] ch, int start, int length)
					throws SAXException {
				// TODO Auto-generated method stub
				super.characters(ch, start, length);
				str = new String(ch).substring(start, start + length);
			}

			@Override
			public void endElement(String uri, String localName, String qName)
					throws SAXException {
				// TODO Auto-generated method stub
				super.endElement(uri, localName, qName);
				if (qName == "uPIField")
					list.add(str);
			}

			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {
				// TODO Auto-generated method stub
				super.startElement(uri, localName, qName, attributes);
				str = "";
			}

		}
	}

	class BackgroundWorker extends
			AsyncTask<String, Staff, HashMap<String, Staff>> {
		private Activity ac;
		private String url = new String();
		private HashMap hm;
		Staff staff;

		public BackgroundWorker(HashMap<String, Staff> hm, Activity ac) {
			this.hm = hm;
			this.ac = ac;
		}

		@Override
		protected HashMap<String, Staff> doInBackground(String... params) {
			url = PHOTO_SERVER + params[0];
			staff = new Staff(params[0]);
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new URL(url).openStream()));
				String result, line = reader.readLine();
				String str = "";
				String base64 = "";
				while ((str = reader.readLine()) != null) {
					// if (staff.getName() != null && staff.getEmail() != null
					// && staff.getUrl() != null && staff.getPhone() != null){
					//
					// }
					if (str.startsWith("FN:")) {
						staff.setName(str.substring(3));
					} else if (str.startsWith("TEL")) {
						staff.setPhone(str.substring(str.indexOf(":") + 1));
					} else if (str.startsWith("EMAIL")) {
						staff.setEmail(str.substring(str.indexOf(":") + 1));
					} else if (str.startsWith("URL")) {
						staff.setUrl(str.substring(str.indexOf(":") + 1));
					} else if ((str.startsWith("TITLT"))) {
						staff.setTitle(str.substring(str.indexOf(":") + 1));
					} else if (str.startsWith("PHOTO")) {
						publishProgress(staff);
						base64 += str.substring(str.indexOf(":") + 1);
						while (!(str = reader.readLine()).startsWith("REV:")) {
							base64 += "\n" + str;
						}
						byte[] decodedString = Base64.decode(base64,
								Base64.DEFAULT);
						Bitmap decodedByte = BitmapFactory.decodeByteArray(
								decodedString, 0, decodedString.length);
						staff.setPhoto(decodedByte);
					}
				}
				Log.d("output", params[0] + ": " + staff.toString());
				Log.d("output",
						params[0] + "photo == null? "
								+ (staff.getPhoto() == null));
				if (staff.getEmail() == null || staff.getName() == null
						|| staff.getPhone() == null || staff.getUrl() == null) {
					Log.e("StaffActivity",
							" spoted null field: " + staff.toString());
				}
				hm.put(params[0], staff);
				Log.d("StaffActivity", "backgroundworker publishProgress: "
						+ staff.toString());
				publishProgress(staff);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return hm;
		}

		@Override
		protected void onCancelled(HashMap<String, Staff> result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
		}

		@Override
		protected void onPostExecute(HashMap<String, Staff> result) {
		}

		@Override
		protected void onPreExecute() {
//			staffList.add(new Staff("api1234", "the name", "the phone","title", "url", null));
			contactAdapter = new MySimpleArrayAdapter(ac, staffList);
			listView.setAdapter(contactAdapter);
		}

		@Override
		protected void onProgressUpdate(Staff... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			contactAdapter.insert(values[0]);
		}

	}

	public class Staff {
		private String name, phone, title, url, upi, email;
		private Bitmap photo;

		public Staff(String upi) {
			this.upi = upi;
		}

		public Staff(String upi, String name, String phone, String title,
				String url, Bitmap photo) {
			this(upi);
			this.name = name;
			this.phone = phone;
			this.title = title;
			this.url = url;
			this.photo = photo;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getUpi() {
			return upi;
		}

		public void setUpi(String upi) {
			this.upi = upi;
		}

		public Bitmap getPhoto() {
			return photo;
		}

		public void setPhoto(Bitmap photo) {
			this.photo = photo;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void addPhone(String phone) {

		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public String toString() {
			return "Staff [name=" + name + ", phone=" + phone + ", title="
					+ title + ", url=" + url + ", upi=" + upi + ", email="
					+ email + "]";
		}

		public String toMessage() {
			String text = ((getName() == null ? "" : "Name: " + getName()) + "\n" + 
					(getEmail() == null ? "" : "Email: " + getEmail()) + "\n" + 
					(getPhone() == null ? "" : "Phone: " + getPhone()) + "\n" + 
					(getUrl() == null ? "" : "Link: " + getUrl()));
			return text;
		}

	}

	public class MySimpleArrayAdapter extends ArrayAdapter<Staff> {
		private final Context context;
		// private List<Staff> contactsList;
		private HashMap<String, Staff> hm;
		private List<Staff> values = new ArrayList<Staff>();

		public MySimpleArrayAdapter(Context context, List<Staff> staffList) {
			super(context, R.layout.contacts_detail_row_layout, staffList);
			this.context = context;
			values = staffList;
			// this.hm = hm;
			// this.values = values;
		}

		public void insert(Staff staff) {
			// TODO:
			Log.d("StaffActivity","MySimpleArrayAdapter insert: " + staff.toString());
			if (!values.contains(staff)){
				insert(staff, getCount());
			}
			
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.contacts_detail_row_layout,
						parent, false);
			}
			Log.d("StaffActivity", "getView called, with position" + position);
			TextView textView = (TextView) rowView
					.findViewById(R.id.contact_detail_label);
			ImageView imageView = (ImageView) rowView
					.findViewById(R.id.contact_detail_photo);
//			textView.setText("this is a fucking text! display it!"
//					.toUpperCase());

			if (values != null || true) {
				Log.d("StaffActivity",
						"getView: values: " + values.get(position).toString());
				textView.setText(values.get(position).toMessage());
				// Change the icon for Windows and iPhone
				if (values.get(position).getPhoto() != null) {imageView.setImageBitmap(values.get(position).getPhoto());}
				else {
					imageView.setImageResource(R.drawable.address_book_icon);
				}
			}
			


			return rowView;
		}
	}
}
