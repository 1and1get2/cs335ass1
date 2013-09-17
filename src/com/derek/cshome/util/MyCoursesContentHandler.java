package com.derek.cshome.util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.derek.cshome.MainActivity.ELEMENT_ID;
import com.derek.cshome.util.MyContentHandler.Item;

import android.renderscript.Element;
import android.util.Log;

public class MyCoursesContentHandler extends MyContentHandler {
	protected Item item;
	protected List<Item> itemList = new ArrayList<Item>();

	public MyCoursesContentHandler(String url, ELEMENT_ID retriver_id) {
		super(url, retriver_id);
		if (isCoursesRss) {
			new MyContentHandler(url, retriver_id);
			return;
		}
	}
	public List<Item> getItemList() {
		return itemList;
	}
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		Log.v(TAG, "startDocument, initialized itemList");
		itemList = new ArrayList<Item>();
	}

	public List<HashMap<String, String>> getItemListMap() {
		List<HashMap<String, String>> itemListMap = new ArrayList<HashMap<String, String>>();
		for (Item item : itemList) {
				Log.d(TAG,"MyCoursesContentHandler-getItemListMap: creating element, title: "
							+ item.getCodeField());
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("codeField", item.getCodeField());
			map.put("semesterField", item.getSemesterField());
			map.put("titleField", item.getTitleField());
//			map.put("title", item.getTitle());
//			map.put("link", item.getLink());
//			map.put("description", item.getDescription());
//			map.put("guid", item.getGuid());
//			map.put("pubDate", item.getPubDate());
//			map.put("endDate", item.getEndDate());
			itemListMap.add(map);
		}
		return itemListMap;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
//		super.startElement(uri, localName, qName, attributes);
		String attrString = "";
		for (int i = 0; i < attributes.getLength(); i++) {
			attrString += attributes.getQName(i) + ":" + attributes.getValue(i)
					+ " ";
		}
		Log.v(TAG, String.format(
				"startElement uri:%s localName:%s qName:%s attributes: %s",
				uri, localName, qName, attrString));
		str = "";
		if (localName.equals("Course")) {
			// start of new item
			item = new Item();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
//		super.endElement(uri, localName, qName);
		Log.v(TAG, String.format("endElement uri:%s localName:%s qName:%s",
				uri, localName, qName));

		if (item == null)
			return;
		if (localName.equals("Course")) {
			Log.d(TAG, "adding new course to itemList: " + item.getCodeField());
			itemList.add(item);
			item = null;
		} else if (localName.equals("codeField")) {
			item.setCodeField(str);
		} else if (localName.equals("semesterField")) {
			item.setSemesterField(str);
		} else if (localName.equals("titleField")) {
			item.setTitleField(str);
		}  else if ( ! Arrays.asList(blaceList).contains(localName)){
			Log.w(TAG, "found un-recongnized tag: " + localName);
		}
		Log.d(TAG, "itemList.size(): " + itemList.size());
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		Log.v(TAG,
				"characters " + new String(ch).substring(start, start + length));
		str = new String(ch).substring(start, start + length);
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
		Log.v(TAG, "endDocument");
	}
	

	

	public class Item {

		private String codeField, semesterField, titleField;
		

		public Item() {
			super();
			// TODO Auto-generated constructor stub
		}

		public String getCodeField() {
			return codeField;
		}

		public void setCodeField(String codeField) {
			this.codeField = codeField;
		}

		public String getSemesterField() {
			return semesterField;
		}

		public void setSemesterField(String semesterField) {
			this.semesterField = semesterField;
		}

		public String getTitleField() {
			return titleField;
		}

		public void setTitleField(String titleField) {
			this.titleField = titleField;
		}
	}
}