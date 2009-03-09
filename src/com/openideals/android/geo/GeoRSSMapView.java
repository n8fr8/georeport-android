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
package com.openideals.android.geo;

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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.openideals.android.ui.InternalWebView;
import com.openideals.greporter.R;

/*
 * Map view that renders out location points
 */
public class GeoRSSMapView extends MapActivity implements OnClickListener, Runnable
{    
	/** location members **/
    private static LocationManager lm;
    private static LocationListener locationListener;
    private static Location currentLocation = null;
    private static String currentLocationName = "";
    private static MyLocationOverlay mlo = null;
    
    /** views and widgets **/
    private MapView mapView;
    private MapController mc;
    
    private ProgressDialog progressDialog = null;
    
    /** constants **/
    private final static String TAG = "LocationFinder";
    private final static int  LOCATION_UPDATE_TIME = 60000;//check every minutes
    private final static int LOCATION_UPDATE_DISTANCE = 100;//don't bother if its not more than 100M
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
     
    	super.onCreate(savedInstanceState);
       
    	showMap();
    	
    	
    	if (currentLocation == null)
    	{
	    	progressDialog = ProgressDialog.show(GeoRSSMapView.this,
				      "Location Finder",
				      "looking for you...",
				      true);
					
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
	      
	    	Log.i(TAG, "starting up Network location provider...");
	    	
	        lm.requestLocationUpdates(
	                LocationManager.NETWORK_PROVIDER, 
	                LOCATION_UPDATE_TIME, 
		            LOCATION_UPDATE_DISTANCE,  
	                locationListener);
    	}
        
        
       
    }
    
    public void stopLocationListening ()
    {
    	if (lm != null)
    		lm.removeUpdates(locationListener);
    	
    }
    
    
    
    private void showMap ()
    {
    	setContentView(R.layout.map); 
        mapView = (MapView) findViewById(R.id.mapview1);
        mc = mapView.getController();
        
        mlo = new MyLocationOverlay(this, mapView);
        mlo.enableCompass();
        mlo.enableMyLocation();
        
        mapView.getOverlays().add(mlo);
        
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
		    	String latlon =  "Location changed : Lat: " + loc.getLatitude() + 
		        " Lng: " + loc.getLongitude();
		    	
		            currentLocation = loc;
		            
		          
		          
		
		            	String locText = "You are in " + currentLocationName;
		            	
		        //        Toast.makeText(getBaseContext(), locText, Toast.LENGTH_SHORT).show();
		                
		           
		            
		            updateMapDisplay(loc);
		       
		            
		       
		            if (progressDialog != null)
		            {
		            	progressDialog.dismiss();
		            	progressDialog = null;
		            
		            }
		          
    		  }
          }
          catch (Exception ioe)
          {
          	
          }
    }
    
    private void updateMapDisplay (Location loc)
    {
    	if (mc != null)
    	{
    	  GeoPoint p = new GeoPoint(
                  (int) (loc.getLatitude() * 1E6), 
                  (int) (loc.getLongitude() * 1E6));
    	  
          mc.animateTo(p);
          mc.setZoom(16);        
          
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
    
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        MenuItem mItem = menu.add(0, 1, Menu.NONE, "close");
  
        return true;
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		super.onMenuItemSelected(featureId, item);
		
		//showLocationForm();
		
        return true;
	}
	
	
    private void showWebsite (String url)
    {
    	Intent i = new Intent(this, InternalWebView.class);
       
        i.putExtra(InternalWebView.KEY_URL, url);        
      
        startActivity(i);
        
    	
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
		
		if (mlo != null)
		{
		mlo.disableCompass();
        mlo.disableMyLocation();
		}
		
		super.onPause();
		
			
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onResume()
	 */
	@Override
	protected void onResume() {
	
		super.onResume();
		
		lm = null;
		startGPS();
		
	}
}

