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

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/*
 * This is the user interface activity for the report form
 */
public class AudioFormActivity extends Activity implements OnClickListener, Runnable
{

	private final String TAG = "reportForm";
	
	private ProgressDialog progressDialog = null;
	

	MediaRecorder mRecorder;
	MediaPlayer mMediaPlayer;
	private final static String file_ext = ".3gp";
	private String currentAudioFile = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.reportaudioform); 
     
        
        ((Button)findViewById(R.id.btnRecordAudio)).setOnClickListener(this);
        ((Button)findViewById(R.id.btnPlayAudio)).setOnClickListener(this);
        
      ((Button)findViewById(R.id.btnReportFormSubmit)).setOnClickListener(this);
      ((Button)findViewById(R.id.btnReportFormCancel)).setOnClickListener(this);
      
      
    }
    
   
    

	@Override
	public void onClick(View v) {
		
		if (v.getId()==R.id.btnReportFormSubmit)
		{
			progressDialog = ProgressDialog.show(AudioFormActivity.this,
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
		else if (v.getId()==R.id.btnRecordAudio)
		{
			if (mRecorder != null)
				stopRecording();
			else
			{
				String newFile = "audio" + new java.util.Date().getTime();
				currentAudioFile = startRecording(newFile);
			}
		}
		else if (v.getId()==R.id.btnPlayAudio)
		{
			if (currentAudioFile != null)
			{
				playRecording(currentAudioFile);
				
			}
				
		}
	}
	
	
	public void run ()
	{
		
		
		submitForm ();
		
	}
	
	
	private void submitForm ()
    {
		if (currentAudioFile == null)
		{
			 Toast.makeText(getBaseContext(), "Please make a recording first!", Toast.LENGTH_LONG).show();
			 if (progressDialog != null)
	 	    	  progressDialog.dismiss();
		}
		else
		{
    	try
        {
    	   Log.i(TAG, "submitting form...");
    	   
 	       
 	       boolean reportAccepted = Reporter.submitAudioReport(this.currentAudioFile);
 	       
 	       if (progressDialog != null)
 	    	  progressDialog.dismiss();
 	       
 	       if (reportAccepted)
 	       {
 	    	   Toast.makeText(getBaseContext(), "Thank you. Your report has been accepted!", Toast.LENGTH_LONG).show();

 	    	  showMain();
 	    	  currentAudioFile = null;
 	    	   
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
	
	
	private void setStatus (String status)
	{
		((TextView)findViewById(R.id.labelAudioStatus)).setText(status);

	}
	
	private String startRecording (String filename)
	{
		
	//labelAudioStatus
		setStatus ("Recording...");
		
		((TextView)findViewById(R.id.btnRecordAudio)).setText("Stop");
		
		
		String path = null;
		
		if (currentAudioFile != null)
		{
			path = currentAudioFile;
		}
		else
		{
			 path = "/sdcard/" + filename + file_ext;
		}
		
		mRecorder = new MediaRecorder();

		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		mRecorder.setOutputFile(path);

		mRecorder.prepare();

		mRecorder.start(); 
		
		return path;

	}
	
	private void playRecording (String path)
	{
		setStatus ("Playing...");
		
		try
		{
			mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void stopRecording ()
	{
		setStatus ("Finished recording.");
		
		((TextView)findViewById(R.id.btnRecordAudio)).setText("Record");
		
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}
    
}
