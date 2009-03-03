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

import com.openideals.android.ui.HorizontalSlider;
import com.openideals.android.ui.HorizontalSlider.OnProgressChangeListener;

/*
 * This is the user interface activity for the report form
 */
public class ReportFormActivity extends Activity implements OnClickListener, Runnable
{

	private final String TAG = "reportForm";
	
	private ProgressDialog progressDialog = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black_NoTitleBar);
        setContentView(R.layout.reportform); 
       
      ((Button)findViewById(R.id.btnReportFormSubmit)).setOnClickListener(this);
      ((Button)findViewById(R.id.btnReportFormCancel)).setOnClickListener(this);
      
      
    }
    
   
    
    

	@Override
	public void onClick(View v) {
		
		if (v.getId()==R.id.btnReportFormSubmit)
		{
			progressDialog = ProgressDialog.show(ReportFormActivity.this,
				      "Submitting Report",
				      "Please wait...",
				      true);
				
			Handler handler = new Handler();
			
			handler.postDelayed(this, 1000);
			
		}
		else if (v.getId()==R.id.btnReportFormCancel)
		{
			showMain ();
		}
	}
	
	
	public void run ()
	{
		
		
		submitForm ();
		
	}
	
	private void submitForm ()
    {
    	try
        {
    	   Log.i(TAG, "submitting form...");
    	   
 	       
 	       String reportTitle = ((TextView)findViewById(R.id.entryTitle)).getText().toString();
 	       String reportText = ((TextView)findViewById(R.id.entryReport)).getText().toString();
 	       
 	       
 	       
 	       boolean reportAccepted = Reporter.submitTextReport(reportTitle, reportText);
 	       
 	       if (progressDialog != null)
 	    	  progressDialog.dismiss();
 	       
 	       if (reportAccepted)
 	       {
 	    	   Toast.makeText(getBaseContext(), "Thank you. Your report has been accepted!", Toast.LENGTH_LONG).show();

 	    	   showMain();
 	    	   
 	       }
 	       else
 	       {
 	    	   Toast.makeText(getBaseContext(), "There was a problem submitting your report. Wait a second, and then try again!", Toast.LENGTH_LONG).show();
 	       }
 	      
        }
        catch (Exception e)
        {
     	   e.printStackTrace();
        }
    }

	
	
	private void showMain ()
	{
		Intent iMain = new Intent(this, LocationFinderActivity.class);
         
         startActivity(iMain);
	}
    
}
