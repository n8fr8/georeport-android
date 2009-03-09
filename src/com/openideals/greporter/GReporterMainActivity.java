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
		   
		   Reporter.setPerson(
				   prefDB.getPref(GReporterConstants.PREFKEY_PERSON_FIRSTNAME), 
				   prefDB.getPref(GReporterConstants.PREFKEY_PERSON_LASTNAME), 
				   prefDB.getPref(GReporterConstants.PREFKEY_PERSON_EMAIL));
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