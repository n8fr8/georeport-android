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
package com.openideals.greporter;


import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.openideals.android.db.PreferenceDB;
import com.openideals.android.net.HttpManager;


/*
 * This class handles all of the REST calls into the back-end reporting system
 */
public class Reporter implements GReporterConstants, Runnable
{
	/**
	 * @return the reportSubmitUrl
	 */
	public static String getReportSubmitUrl() {
		return reportSubmitUrl;
	}

	/**
	 * @param reportSubmitUrl the reportSubmitUrl to set
	 */
	public static void setReportSubmitUrl(String reportSubmitUrl) {
		Reporter.reportSubmitUrl = reportSubmitUrl;
	}

	private final static String TAG = "Reporter";
	
	private static String deviceGuid = null;
	
	private static Location location = null;

	private static String reportSubmitUrl = null;
	
	/**
	 * @return the location
	 */
	public static Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public static void setLocation(Location location) {
		Reporter.location = location;
	}

	/**
	 * @return the deviceGuid
	 */
	public static String getDeviceGuid() {
		return deviceGuid;
	}

	/**
	 * @param deviceGuid the deviceGuid to set
	 */
	public static void setDeviceGuid(String deviceGuid) {
		Reporter.deviceGuid = deviceGuid;
	}

	public static String getLocationName (Location location) 
	{
		// Setter for the user's location; initiate a HTTP request to get the name of this place
		// when the user's location is set
		String locationName = "";
		
		try
		{
			String latlon = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
			
			Properties params = new Properties();
			params.put("latlon", latlon);
			//locationName = HttpManager.doGet (TWITTERVISION_LOCATION_LOOKUP_URL, params);
			
			//Log.i(TAG, "getLocationName resp: " + locationName);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return locationName;
		
	}
	
	private static Person person = null;
	
	public static void setPerson (String firstname, String lastname, String email)
	{
		   person = new Person ();
	       
	       person.firstname = firstname;
	       person.lastname = lastname;
	       person.email = email;
	    
	}
	
	public static Person getPerson ()
	{
		
		return person;
	}
	
	public static boolean submitTextReport (String title, String body)
	{
		
		Properties props = new Properties();
		props.put("report[type]", "TextReport");
		props.put("report[title]", title);
		props.put("report[body]", body);

		return submitReport (props, null, null);
	}
	
	public static boolean submitPhotoReport (String caption, String photoPath)
	{
		
		Properties props = new Properties();
		props.put("report[type]", "PhotoReport");
		props.put("report[title]", caption);
		props.put("imagefile", new File(photoPath).getName());
		
		return submitReport (props, "uploaded", photoPath);
	}
	
	public static boolean submitAudioReport (String audioPath)
	{
		String soundb64 = "";
		
		Properties props = new Properties();
		props.put("report[type]", "AudioReport");
		props.put("soundfile", new File(audioPath).getName());

		return submitReport (props, "uploaded", audioPath);
	}
	
	private static boolean submitReport (Properties props, String fileParam, String file) 
	{
		Log.i(TAG,"submitReport: submitting...");
		

		Reporter reporter = new Reporter();
		reporter.props = props;
		reporter.filename = fileParam;
		reporter.file = file;
		  
		Handler handle = new Handler();
        handle.postDelayed(reporter, 500);
        
        return true;
	}
	
	public Properties props;
	public String filename;
	public String file;
	
	public void run ()
	{

		new Thread() {
		    @Override public void run() {

		    	backgroundReport ();
		    	
		    }
		  }.start();
	
	}
	
	private void backgroundReport ()
	{
	      
	     Person person = getPerson();
	       
		
		
		NumberFormat f = NumberFormat.getInstance();
		 if (f instanceof DecimalFormat) {
		     ((DecimalFormat) f).setMinimumFractionDigits(3);
		     ((DecimalFormat) f).setMaximumFractionDigits(3);
		     
		 }

		String latlon = f.format(location.getLatitude()) + "," 
		+ f.format(location.getLongitude()) + ":"
		+ (int)(location.getAccuracy());
		
		Log.i(TAG,"latlon="+latlon);
		
		if (deviceGuid == null)
			deviceGuid = "0000000000000000";
		
		props.put("reporter[uniqueid]", deviceGuid);
		props.put("reporter[firstname]", person.firstname);
		props.put("reporter[lastname]", person.lastname);
		props.put("reporter[email]", person.email);
		props.put("reporter[zipcode]", person.zip);
		
		props.put("report[latlon]", latlon);
		
		
		String response = null;
		
		if (file == null)
		{
			try
			{
				response = HttpManager.doPost (reportSubmitUrl, props);
				Log.i(TAG,"submitReport response: " + response);
			
			}
			catch (Exception e)
			{
				Log.i(TAG,"error doing post: " + e.toString());
			}
		}
		else
		{
			try
			{
				
				response = HttpManager.uploadFile(reportSubmitUrl, props, filename, file);
				Log.i(TAG,"submitReport response: " + response);
			
				 File file = new File(this.file);
	 	    	   boolean deleted = file.delete();
	 	    	   Log.i(TAG, "file was deleted: " + deleted);
			}
			catch (Exception e)
			{
				Log.i(TAG,"error doing post: " + e.toString());
			}
				
		
		}
		
	}
	
	public static void postAudioFile ()
	{
		/*
		 * HTTPManager *httpRequest = [[HTTPManager alloc] init];
	httpRequest.target = self;
	httpRequest.targetSelector = @selector(reportComplete:);
	NSString *soundfile = [params valueForKey:@"soundfile"];
	if (soundfile)
		[httpRequest uploadFile:soundfile toUrl:VOTEREPORT_REPORTS_URL withParameters:params];
	else
		[httpRequest performRequestWithMethod:@"POST" toUrl:VOTEREPORT_REPORTS_URL withParameters:params];

		 */
	}
	
	
	
	
}
