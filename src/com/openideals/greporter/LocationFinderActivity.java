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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.openideals.android.db.PreferenceDB;
import com.openideals.android.ui.InternalWebView;

/*
 * This activity handles finding the users location via GPS or the mobile network
 */
public class LocationFinderActivity extends MapActivity implements OnClickListener, Runnable
{    
	/** location members **/
    private static LocationManager lm;
    private static LocationListener locationListener;
    private static Location currentLocation = null;
    private static String currentLocationName = "";
   
    
    /** views and widgets **/
    private TextView txtLocation;
  //  private ProgressDialog progressDialog = null;
    
    /** constants **/
    private final static String TAG = "LocationFinder";
    private final static int  LOCATION_UPDATE_TIME = 60000;//check every minutes
    private final static int LOCATION_UPDATE_DISTANCE = 100;//don't bother if its not more than 100M
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
     
    	super.onCreate(savedInstanceState);
    	setTheme(android.R.style.Theme_Black_NoTitleBar);
    	showLocationForm();
    	
    	if (currentLocation == null && lm == null)
    	{
	        Handler handle = new Handler();
	        handle.postDelayed(this, 500);
    	}

    }
    
    public void run ()
    {
    	
    		startGPS();
    }
    
    public void startGPS ()
    {

        //---use the LocationManager class to obtain GPS locations---
    	if (lm == null)
    	{
    		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);    
        
	        locationListener = new MyLocationListener();
	        
	        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
	        {
	        
	        	Log.i(TAG, "starting up GPS location provider...");
	        	
		        lm.requestLocationUpdates(
		            LocationManager.GPS_PROVIDER, 
		            LOCATION_UPDATE_TIME, 
		            LOCATION_UPDATE_DISTANCE, 
		            locationListener);
		       
		        currentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		        updateLocation (currentLocation);
	        }
	      
	        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
	        {
		    	Log.i(TAG, "starting up Network location provider...");
		    	
		        lm.requestLocationUpdates(
		                LocationManager.NETWORK_PROVIDER, 
		                LOCATION_UPDATE_TIME, 
			            LOCATION_UPDATE_DISTANCE,  
		                locationListener);
		        
		        currentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		        updateLocation (currentLocation);
	        }
    	}
        
        
       
    }
    
    public void stopLocationListening ()
    {
    	if (lm != null)
    		lm.removeUpdates(locationListener);
    	
    }
    
    private void showLocationForm ()
    {
    	setContentView(R.layout.locator);
    
    	txtLocation = (TextView)findViewById(R.id.labelFindLocation);
    	
    	((Button)findViewById(R.id.btnSubmitAudio)).setOnClickListener(this);
    	((Button)findViewById(R.id.btnSubmitPhoto)).setOnClickListener(this);
    	((Button)findViewById(R.id.btnSubmitText)).setOnClickListener(this);
    	
    	if (currentLocation != null)
    		updateLocation(currentLocation);
    }
    

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }        
    
    private void updateLocation (Location loc)
    {
    	  try
          {
    		  
    		  if (loc != null)
    		  {
    			  String latlon =  "Lat:" + (loc.getLatitude()) + 
    			  " Lon:" + (loc.getLongitude());
		    	
		          String locText = "You are at: " + latlon;
		            	
		          Toast.makeText(getBaseContext(), locText, Toast.LENGTH_SHORT).show();
		                
		           
		          Reporter.setLocation(loc);
    		  }
          }
          catch (Exception ioe)
          {
          	
          }
    }
    
    
    private class MyLocationListener implements LocationListener 
    {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {                
                updateLocation (loc);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        	Log.i(TAG,"provider disabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
        	Log.i(TAG,"provider enabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, 
            Bundle extras) {
        	Log.i(TAG,"status changed: " + provider + "=" + status);
        }
        
        public void updateStatus ()
        {
        	
        	Log.i(TAG,"status updated");
        }
    }  
    
    public static Location getCurrentLocation ()
    {
    	return currentLocation;
    }
    
    public static String getCurrentLocationName ()
    {
    	return currentLocationName;
    }
    
    private void showAudioReport ()
    {
    	
    	Intent iReportForm = new Intent(this, AudioFormActivity.class);
        
        startActivity(iReportForm);
    }
    
    private void showPhotoReport ()
    {
    	
    	Intent iReportForm = new Intent(this, PhotoFormActivity.class);
        
        startActivity(iReportForm);
    }
    
    private void showReportForm ()
    {
    	
    	Intent iReportForm = new Intent(this, ReportFormActivity.class);
        
        startActivity(iReportForm);
    }
    
    private void showRegistration ()
    {
    	
    	Intent iReportForm = new Intent(this, PersonFormActivity.class);
        
        startActivity(iReportForm);
    }
    
    private void showWebView ()
    {
    	Intent iReportForm = new Intent(this, InternalWebView.class);
    	
    	String reportDisplayUrl = PreferenceDB.getInstance(this).getPref(GReporterConstants.PREFKEY_REPORT_DISPLAY_URL);
    	
    	iReportForm.putExtra("url", reportDisplayUrl);
    	
        startActivity(iReportForm);
    	
    }
   
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        MenuItem mItem = menu.add(0, 1, Menu.NONE, "About");
        MenuItem mItem2 = menu.add(0, 2, Menu.NONE, "Settings");
       MenuItem mItem3 = menu.add(0, 3, Menu.NONE, "Reports");
       
       mItem.setIcon(R.drawable.ic_menu_about);
       mItem2.setIcon(R.drawable.ic_menu_register);
       mItem3.setIcon(R.drawable.ic_menu_reports);
       
        return true;
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		super.onMenuItemSelected(featureId, item);
		
		if (item.getItemId() == 1)
			showCredits();
		else if (item.getItemId() == 2)
			showRegistration();
		else if (item.getItemId() == 3)
		{
			
			showWebView ();
		}
		
        return true;
	}
	

    
    private void showCredits ()
    {
    	String credits = "Application:\nNathan Freitas (nathan@freitas.net)\nhttp://openideals.com";
    	
    	Toast toast = Toast.makeText(getBaseContext(), credits, Toast.LENGTH_LONG);
    	toast.show();
    	
    }
    
    public String getCurrentLocationString ()
    {
    	 
    	NumberFormat f = NumberFormat.getInstance();
		 if (f instanceof DecimalFormat) {
		     ((DecimalFormat) f).setMinimumFractionDigits(3);
		     ((DecimalFormat) f).setMaximumFractionDigits(3);
		     
		 }
		 

		String latlon = f.format(currentLocation.getLatitude()) + "," 
		+ f.format(currentLocation.getLongitude());
		
		return latlon;
    }

	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
			case R.id.btnSubmitText:
				showReportForm ();
				break;
				
			case R.id.btnSubmitAudio:
				showAudioReport ();
				break;
			
			case R.id.btnSubmitPhoto:
				showPhotoReport ();
				break;
			
			
			default:
		}
		
		
			
		
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		
		stopLocationListening();
		
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onPause()
	 */
	@Override
	protected void onPause() {

		stopLocationListening();
		
		
		super.onPause();
		
			
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onResume()
	 */
	@Override
	protected void onResume() {
	
		super.onResume();
		showLocationForm ();
		
		lm = null;
		startGPS();
		
	}
}

