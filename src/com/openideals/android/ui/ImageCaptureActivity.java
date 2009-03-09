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
package com.openideals.android.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.openideals.greporter.PhotoFormActivity;
import com.openideals.greporter.R;


public class ImageCaptureActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback
{
private Camera camera;
private boolean isPreviewRunning = false;
private SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");

private SurfaceView surfaceView;
private SurfaceHolder surfaceHolder;
//private Uri target =null;// Media.EXTERNAL_CONTENT_URI;

private String currentPhotoFile = null;

private final static int PHOTO_WIDTH = 960;//213;//480;
private final static int PHOTO_HEIGHT = 640;//350;//320;


public void onCreate(Bundle icicle)
{
super.onCreate(icicle);
Log.e(getClass().getSimpleName(), "onCreate");
getWindow().setFormat(PixelFormat.TRANSLUCENT);
setContentView(R.layout.camera);
surfaceView = (SurfaceView)findViewById(R.id.surface);

surfaceHolder = surfaceView.getHolder();

surfaceHolder.addCallback(this);
surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
}

public boolean onCreateOptionsMenu(android.view.Menu menu) {
	
MenuItem item = menu.add(0, 0, 0, "Take Picture");

item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
  public boolean onMenuItemClick(MenuItem item) {
	  takePicture();
	  
      return true;
  }
});


return true;
}

private void sendPicture ()
{
	if (currentPhotoFile != null)
	{
		Intent intent = new Intent(this, PhotoFormActivity.class); 
		intent.putExtra("photofile", currentPhotoFile);
		startActivity(intent);
	}
}

private void takePicture ()
{
  camera.takePicture(mShutterCallback, null, mPictureCallbackJpeg);

  
}

@Override
protected void onRestoreInstanceState(Bundle savedInstanceState)
{
super.onRestoreInstanceState(savedInstanceState);
}

private boolean sizeSet = false;

public void onPreviewFrame(byte[] data, Camera c) {
	
	if (!sizeSet)
	{
		Log.i(getClass().getSimpleName(), "preview frame RAW: " + data);
		
		Camera.Parameters params = c.getParameters();
		params.setPictureFormat(PixelFormat.JPEG);
		params.setPictureSize(PHOTO_WIDTH,PHOTO_HEIGHT);
		c.setParameters(params);
		
		sizeSet = true;
	}
}

Camera.PictureCallback mPictureCallbackRaw = new Camera.PictureCallback() {
public void onPictureTaken(byte[] data, Camera c) {
  
}
};

Camera.PictureCallback mPictureCallbackJpeg= new Camera.PictureCallback() {
public void onPictureTaken(byte[] data, Camera c) {
  Log.e(getClass().getSimpleName(), "PICTURE CALLBACK JPEG: data.length = " + data.length);
  String filename = timeStampFormat.format(new Date());
  
  String baseDir = "/sdcard/";
  
  if (new File("/sdcard/dcim/Camera/").exists())
  {
	  baseDir = "/sdcard/dcim/Camera/";
  }
  
  currentPhotoFile = baseDir + filename + ".jpg";
  
  
  
  try
  {
	  FileOutputStream file = new FileOutputStream(new File(currentPhotoFile));
	  file.write(data);
	  
	  sendPicture();
  }
  catch (Exception e){
	  e.printStackTrace();
  }
}
};

Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
public void onShutter() {
Log.e(getClass().getSimpleName(), "SHUTTER CALLBACK");

Camera.Parameters params = camera.getParameters();
params.setPictureFormat(PixelFormat.JPEG);
params.setPictureSize(PHOTO_WIDTH,PHOTO_HEIGHT);
camera.setParameters(params);


}
};


public boolean onKeyDown(int keyCode, KeyEvent event)
{

if (keyCode == KeyEvent.KEYCODE_BACK) {
  return super.onKeyDown(keyCode, event);
}

if (keyCode == KeyEvent.KEYCODE_CAMERA) {
  takePicture();
  
  return true;
}

return false;
}

protected void onResume()
{
Log.e(getClass().getSimpleName(), "onResume");
super.onResume();
}

protected void onSaveInstanceState(Bundle outState)
{
super.onSaveInstanceState(outState);
}

protected void onStop()
{
Log.e(getClass().getSimpleName(), "onStop");
super.onStop();
}

public void surfaceCreated(SurfaceHolder holder)
{
Log.e(getClass().getSimpleName(), "surfaceCreated");

if (camera != null)
{
	camera.stopPreview();
	isPreviewRunning = false;

	camera.release();
}

camera = Camera.open();
camera.setPreviewCallback(this);


}

public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
{
Log.e(getClass().getSimpleName(), "surfaceChanged");
if (isPreviewRunning) {
  camera.stopPreview();
}

camera.setPreviewDisplay(holder);
camera.startPreview();
isPreviewRunning = true;
}

public void surfaceDestroyed(SurfaceHolder holder)
{
Log.e(getClass().getSimpleName(), "surfaceDestroyed");
camera.stopPreview();
isPreviewRunning = false;
camera.release();
}
}
