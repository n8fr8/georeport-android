package com.openideals.greporter;

import com.openideals.android.db.PreferenceDB;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class GReporterMainActivity extends Activity {
	 private static final int STOPSPLASH = 0;
	    //time in milliseconds
	    private static final long SPLASHTIME = 2000; 
	    
	    private ImageView splash; 
	    
	  //handler for splash screen
	    private Handler splashHandler = new Handler() {
	         /* (non-Javadoc)
	          * @see android.os.Handler#handleMessage(android.os.Message)
	          */
	         @Override
	         public void handleMessage(Message msg) {
	              switch (msg.what) {
	              case STOPSPLASH:
	                   //remove SplashScreen from view
	                  // splash.setVisibility(View.GONE);
	                   showFirstForm ();
	                   
	            	
	                
	                   break;
	              }
	              super.handleMessage(msg);
	         }
	    };
	    
	   private void showFirstForm ()
	   {
		   if (PreferenceDB.getInstance(this).getPref("lastname")==null)
       		showPersonForm ();
       	  else
       	  {
       		  loadPerson();
       		  showLocationFinder();
       	  }
	   }
	    
	   private void loadPerson ()
	   {
		   PreferenceDB prefDB = PreferenceDB.getInstance(this);
		   
		   Reporter.setPerson(prefDB.getPref("firstname"), prefDB.getPref("lastname"), prefDB.getPref("email"), prefDB.getPref("zip"));
	   }
	   
	   /** Called when the activity is first created. */
	   @Override
	   public void onCreate(Bundle icicle) {
	       super.onCreate(icicle);
	       setTheme(android.R.style.Theme_Black_NoTitleBar);

	       setContentView(R.layout.splash);
	       
	    // splash = (ImageView) findViewById(R.id.splashscreen);
	     
	      Message msg = new Message();
	      msg.what = STOPSPLASH;
	      splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	         
	      
	      getDeviceGUID();
	   } 
	   
		private void getDeviceGUID()
		{
			TelephonyManager mgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
			
			String deviceId = mgr.getDeviceId();
			
			Log.i("main","got device id: " + deviceId);
			
			if (deviceId == null)
					deviceId = "0000000000000000";
			
 			Reporter.setDeviceGuid(deviceId);
		}
	   
	   private void showReportForm ()
	   {
	   	
	   		Intent iReportForm = new Intent(this, ReportFormActivity.class);
	       
	       startActivity(iReportForm);
	   }
	   
	   private void showPersonForm ()
	   {
		   
	   	
		   Intent i = new Intent(this, PersonFormActivity.class);
	       
	       startActivity(i);
	   }
	   
	   private void showLocationFinder ()
	   {
		   
	   	
	   	
	   	Intent i = new Intent(this, LocationFinderActivity.class);
	       
	       startActivity(i);
	   }
}