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
 * Atom handler with support for Geo tags
 */
public class AtomHandler extends DefaultHandler {

	private final static String TAG = "AtomHandler";
	
	// Used to define what elements we are currently in
	private boolean inItem = false;
	private boolean inTitle = false;
	private boolean inLink = false;
	private boolean inSummary = false;
	private boolean inGeo = false;

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
		else if (name.trim().equals("entry"))
			inItem = true;
		else if (name.trim().equals("link"))
		{
			inLink = true;
			try
			{
			currentArticle.link = new URL(atts.getValue("href"));
			}
			catch (Exception e)
			{}
		}
		
		
		
	}

	public void endElement(String uri, String name, String qName)
			throws SAXException {
		if (name.trim().equals("title"))
			inTitle = false;
		else if (name.trim().equals("entry"))
			inItem = false;
		else if (name.trim().equals("link"))
			inLink = false;

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
				
				if (inTitle)
					currentArticle.title = chars;
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
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
			Log.e(TAG, e.toString());
		} catch (SAXException e) {
			Log.e(TAG, e.toString());
		} catch (ParserConfigurationException e) {
			Log.e(TAG, e.toString());
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
			Log.e(TAG, e.toString());
		} catch (SAXException e) {
			Log.e(TAG, e.toString());
		} catch (ParserConfigurationException e) {
			Log.e(TAG, e.toString());
		} 
	}

}
 

 /*
  * <feed xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/" xmlns:georss="http://www.georss.org/georss" xmlns="http://www.w3.org/2005/Atom">
   <title>#votereport</title>
   <id>http://votereport.us/reports</id>
   <link rel="self" href="http://votereport.us/reports.atom" type="application/atom+xml"/>
   <link rel="alternate" href="http://votereport.us/reports.kml" type="application/vnd.google-earth.kml+xml"/>
   <link rel="alternate" href="http://votereport.us/reports" type="text/html"/>
   <opensearch:totalResults>64</opensearch:totalResults>
   <opensearch:startIndex>10</opensearch:startIndex>
   <opensearch:itemsPerPage>10</opensearch:itemsPerPage>
   <updated>2008-10-30T06:59:11+00:00</updated>
   <link rel="first" href="http://votereport.us/reports.atom?page=1" type="application/atom+xml"/>
   <link rel="next" href="http://votereport.us/reports.atom?page=2" type="application/atom+xml"/>
   <link rel="last" href="http://votereport.us/reports.atom?page=7" type="application/atom+xml"/>
   <entry>
     <title>OH_observers</title>
     <link rel="alternate" href="http://votereport.us/reports/78" type="text/html"/>
     <id>http://votereport.us/reports/78</id>
     <updated>2008-10-30T06:46:02Z</updated>
     <author>
       <name>OH_observers</name>
     </author>
     <summary>NYTimes reporting DOJ will not require Ohio to publish list of 200,000 names as requested by Bush #votereport http://tinyurl.com/6of22c</summary>
     <category term="{attribute} = "/>
     <category term="{attribute} = "/>
     <category term="{attribute} = TWT"/>

 <georss:point>39.962208 -83.000676</georss:point>
     <content type="html">
 OH_observers: NYTimes reporting DOJ will not require Ohio to publish list of 200,000 names as requested by Bush #votereport http://tinyurl.com/6of22c    </content>
   </entry>

*/
