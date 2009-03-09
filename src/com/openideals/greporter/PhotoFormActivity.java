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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openideals.android.db.PreferenceDB;
import com.openideals.android.ui.ImageCaptureActivity;

/*
 * This is the user interface activity for the report form
 */
public class PhotoFormActivity extends Activity implements OnClickListener, Runnable
{

	private final String TAG = "reportForm";
	
	private ProgressDialog progressDialog = null;
	
	private String currentPhotoPath = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        currentPhotoPath = this.getIntent().getStringExtra("photofile");
        //setTheme(android.R.style.Them);

	    
        
        setContentView(R.layout.reportphotoform); 
       
        ((Button)findViewById(R.id.btnTakePicture)).setOnClickListener(this);
		
      ((Button)findViewById(R.id.btnReportFormSubmit)).setOnClickListener(this);
      ((Button)findViewById(R.id.btnReportFormCancel)).setOnClickListener(this);
      
      if (currentPhotoPath != null)
      {
    	  Toast.makeText(getBaseContext(), "Ready to send photo: " + currentPhotoPath, Toast.LENGTH_LONG).show();
    	  
    	  ((ImageView)findViewById(R.id.previewphoto)).setImageURI(Uri.parse(currentPhotoPath));
    	  
    	  
      }
    }
  
    

	@Override
	public void onClick(View v) {
		
		if (v.getId()==R.id.btnReportFormSubmit)
		{
			progressDialog = ProgressDialog.show(PhotoFormActivity.this,
				      "Submitting Report",
				      "Please wait...",
				      true, true);
			
				
			Handler handler = new Handler();
			
			handler.postDelayed(this, 1000);
			
			
		}
		else if (v.getId()==R.id.btnReportFormCancel)
		{
			showMain ();
		}
		else if (v.getId()==R.id.btnTakePicture)
		{
			takePicture();
		}
	}
	
	
	public void run ()
	{
		
		
		submitForm ();
		
	}
	
	private void takePicture ()
	{

		Intent iMain = new Intent(this, ImageCaptureActivity.class);
         
         startActivity(iMain);
	}
	
	private void submitForm ()
    {
		if (currentPhotoPath == null)
		{
			 Toast.makeText(getBaseContext(), "Please take a picture first!", Toast.LENGTH_LONG).show();

			 if (progressDialog != null)
	 	    	  progressDialog.dismiss();
		}
		else
		{
		try
        {
    	   Log.i(TAG, "submitting form...");
    	   
 	       
 	       String caption = ((TextView)findViewById(R.id.entryCaption)).getText().toString();
 	       
 	       String photoPath = currentPhotoPath;
 	       
 	       boolean reportAccepted = Reporter.submitPhotoReport(caption, photoPath);
 	       
 	       if (progressDialog != null)
 	    	  progressDialog.dismiss();
 	       
 	       if (reportAccepted)
 	       {
 	    	   Toast.makeText(getBaseContext(), "Thank you. Your photo report has been accepted!", Toast.LENGTH_LONG).show();

 	    	  showMain();
 	    	   
 	    	  currentPhotoPath = null;
 	    	  
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
    }

	
	private void showMain ()
	{
		Intent iMain = new Intent(this, LocationFinderActivity.class);
         
         startActivity(iMain);
	}
    
}
