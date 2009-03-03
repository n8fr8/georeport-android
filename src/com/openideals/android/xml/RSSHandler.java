/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openideals.android.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


import android.content.Context;
import android.util.Log;

/*
 * Parser for RSS 
 */
public class RSSHandler extends DefaultHandler {

	// Used to define what elements we are currently in
	private boolean inItem = false;
	private boolean inTitle = false;
	private boolean inLink = false;
	private boolean inEnc = false;

	// Feed and Article objects to use for temporary storage
	private Article currentArticle = new Article();
	private Feed currentFeed = new Feed();

	// Number of articles added so far
	private int articlesAdded = 0;

	// Number of articles to download
	private static final int ARTICLES_LIMIT = 15;

	// The possible values for targetFlag
	private static final int TARGET_FEED = 0;
	private static final int TARGET_ARTICLES = 1;

	// A flag to know if looking for Articles or Feed name
	private int targetFlag;

	
	public void startElement(String uri, String name, String qName,
			Attributes atts) {
		
		if (name.trim().equals("title"))
			inTitle = true;
		else if (name.trim().equals("item"))
			inItem = true;
		else if (name.trim().equals("link"))
			inLink = true;
		else if (name.trim().equals("enclosure") 
				|| name.trim().equals("content"))
		{
			String encUrl = atts.getValue("url");
			try {
				currentArticle.link = new URL(encUrl);
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			inEnc = false;
		}
		else if (name.trim().equals("thumbnail"))
		{
			String encUrl = atts.getValue("url");
			try {
				
				currentArticle.imageUrl = new URL(encUrl);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			inEnc = false;
		}
		
		
	}

	public void endElement(String uri, String name, String qName)
			throws SAXException {
		if (name.trim().equals("title"))
			inTitle = false;
		else if (name.trim().equals("item"))
			inItem = false;
		else if (name.trim().equals("link"))
			inLink = false;
		else if (name.trim().equals("enclosure"))
			inEnc = false;

		// Check if looking for feed, and if feed is complete
		if (targetFlag == TARGET_FEED && currentFeed.url != null
				&& currentFeed.title != null) {

			// We know everything we need to know, so insert feed and exit
			//feedDB.insertFeed(currentFeed.title, currentFeed.url, currentFeed.imageUrl);
			throw new SAXException();
		}

		// Check if looking for article, and if article is complete
		if (targetFlag == TARGET_ARTICLES && name.trim().equals("item")) {
			
		//	feedDB.insertArticle(currentFeed.feedId, currentArticle.title,
			//		currentArticle.link, currentArticle.imageUrl);
			
			currentArticle.title = null;
			currentArticle.link = null;

			// Lets check if we've hit our limit on number of articles
			articlesAdded++;
			//if (articlesAdded >= ARTICLES_LIMIT)
				//throw new SAXException();
		}

	}

	public void characters(char ch[], int start, int length) {

		String chars = (new String(ch).substring(start, start + length));

		try {
			// If not in item, then title/link refers to feed
			if (!inItem) {
				if (inTitle)
					currentFeed.title = chars;
			} else {
				if (inLink && currentArticle.link == null)
					currentArticle.link = new URL(chars);
				if (inTitle)
					currentArticle.title = chars;
			}
		} catch (MalformedURLException e) {
			Log.e("NewsDroid", e.toString());
		}

	}

	public void createFeed(Context ctx, URL url) {
		try {
			targetFlag = TARGET_FEED;
		//	feedDB = FeedsDB.getInstance(ctx);
			currentFeed.url = url;

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(this);
			
			URLConnection uc = url.openConnection();
			InputStream is = uc.getInputStream();
			
			xr.parse(new InputSource(is));
			
		} catch (IOException e) {
			Log.e("NewsDroid", e.toString());
		} catch (SAXException e) {
			Log.e("NewsDroid", e.toString());
		} catch (ParserConfigurationException e) {
			Log.e("NewsDroid", e.toString());
		}
	}

	public void updateArticles(Context ctx, Feed feed) {
		try {
			targetFlag = TARGET_ARTICLES;
			//feedDB = FeedsDB.getInstance(ctx);
			currentFeed = feed;

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(this);
			xr.parse(new InputSource(currentFeed.url.openStream()));
			
		} catch (IOException e) {
			Log.e("NewsDroid", e.toString());
		} catch (SAXException e) {
			Log.e("NewsDroid", e.toString());
		} catch (ParserConfigurationException e) {
			Log.e("NewsDroid", e.toString());
		} 
	}

}
