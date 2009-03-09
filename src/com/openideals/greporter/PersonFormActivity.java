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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.openideals.android.db.PreferenceDB;
import com.openideals.android.ui.HorizontalSlider;
import com.openideals.android.ui.HorizontalSlider.OnProgressChangeListener;

/*
 * This is the user interface activity for the report form
 */
public class PersonFormActivity extends Activity implements OnClickListener
{

	private final String TAG = "personForm";
	
	private ProgressDialog progressDialog = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black_NoTitleBar);
        setContentView(R.layout.personform); 
       
      ((Button)findViewById(R.id.btnReportFormSubmit)).setOnClickListener(this);
      
      PreferenceDB prefDB = PreferenceDB.getInstance (this);
      
      if (prefDB.getPref("firstname")!=null)
      {
    	  
    	  ((TextView)findViewById(R.id.entryFirstName)).setText(prefDB.getPref(GReporterConstants.PREFKEY_PERSON_FIRSTNAME));
    	  ((TextView)findViewById(R.id.entryLastName)).setText(prefDB.getPref(GReporterConstants.PREFKEY_PERSON_LASTNAME));
    	  ((TextView)findViewById(R.id.entryEmail)).setText(prefDB.getPref(GReporterConstants.PREFKEY_PERSON_EMAIL));
    	
    	  
    	  ((TextView)findViewById(R.id.entrySubmitUrl)).setText(prefDB.getPref(GReporterConstants.PREFKEY_REPORT_SUBMIT_URL));
    	  ((TextView)findViewById(R.id.entryViewUrl)).setText(prefDB.getPref(GReporterConstants.PREFKEY_REPORT_DISPLAY_URL));
    	  
    	   
      }
    }
    
   
    

	@Override
	public void onClick(View v) {
		
		if (v.getId()==R.id.btnReportFormSubmit)
		{
			 
	 	      String firstname = ((TextView)findViewById(R.id.entryFirstName)).getText().toString();
	 	     String lastname = ((TextView)findViewById(R.id.entryLastName)).getText().toString();
	 	    String email = ((TextView)findViewById(R.id.entryEmail)).getText().toString();
	 	
	 	   String submitUrl = ((TextView)findViewById(R.id.entrySubmitUrl)).getText().toString();
	 	  String displayUrl = ((TextView)findViewById(R.id.entryViewUrl)).getText().toString();
		 	
		
			PreferenceDB prefDB = PreferenceDB.getInstance (this);
			
			prefDB.insertPref(GReporterConstants.PREFKEY_PERSON_FIRSTNAME, firstname);
			prefDB.insertPref(GReporterConstants.PREFKEY_PERSON_LASTNAME, lastname);
			prefDB.insertPref(GReporterConstants.PREFKEY_PERSON_EMAIL, email);
			prefDB.insertPref(GReporterConstants.PREFKEY_REPORT_SUBMIT_URL, submitUrl);
			prefDB.insertPref(GReporterConstants.PREFKEY_REPORT_DISPLAY_URL, displayUrl);
			
			
			Reporter.setReportSubmitUrl(submitUrl);
			
	 	     
		}
		else if (v.getId()==R.id.btnReportFormCancel)
		{
			
		}
		
		showMain ();
	}
	
	
	
	private void showMain ()
	{
		Intent iMain = new Intent(this, LocationFinderActivity.class);
         
         startActivity(iMain);
	}
    
}
