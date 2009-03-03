
//original from: http://itp.nyu.edu/~dbo3/blog/?p=122

package com.openideals.android.media;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

public class CameraHat extends Activity implements Camera.PreviewCallback, Camera.ShutterCallback, Camera.PictureCallback {

final String TAG = "CameraHat";

Camera mCamera;

private Preview mPreview;

boolean freeToSend = true;

ShutterThread myShutterThread;

class ShutterThread extends Thread {

public void run(){
while (true){
if (freeToSend){
freeToSend = false;
Log.v(TAG,"thread ready");
takePicture();
}
}
}
}
public void takePicture(){
mCamera.takePicture(this, null, this);
}
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);

// Hide the window title.
requestWindowFeature(Window.FEATURE_NO_TITLE);

// Create our Preview view and set it as the content of our activity.
mCamera = Camera.open();
Camera.Parameters p = mCamera.getParameters();
int CAM_W = 160;
int CAM_H = 120;
p.setPictureSize(CAM_W, CAM_H);
p.setPictureFormat(PixelFormat.JPEG);
//p.setPreviewFormat(PixelFormat.JPEG);
mCamera.setParameters(p);
//mCamera.setPreviewCallback(this);
mPreview = new Preview(this);
setContentView(mPreview);
myShutterThread = new ShutterThread();
myShutterThread.start();
}

// ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ-
@Override
public boolean onKeyDown(int keycode, KeyEvent event) {
Log.v(TAG, "KeyDown" + keycode);
if (keycode == KeyEvent.KEYCODE_CAMERA || keycode == 8) {
mCamera.takePicture(this, null, this);
mCamera.startPreview();
}

super.onKeyDown(keycode, event);
return true;
}

// private Camera.ShutterCallback sillyDelay = new Camera.ShutterCallback() {
public void onPreviewFrame(byte[] data, Camera camera) {


if (freeToSend){

freeToSend = false;
Log.v(TAG,"preview");
new Uploader("http://itp.nyu.edu/~dbo3/up.php", data, "androidTest.jpg");
}
}

public void onShutter() {
Camera.Parameters p = mCamera.getParameters();
int CAM_W = 160;
int CAM_H = 120;
p.setPictureSize(CAM_W, CAM_H);
p.setPictureFormat(PixelFormat.JPEG);
//p.setPreviewFormat(PixelFormat.JPEG);
mCamera.setParameters(p);
}

// };

// private Camera.PictureCallback doSomeThingWithIt = new Camera.PictureCallback() {

public void onPictureTaken(byte[] data, Camera camera) {
Log.v(TAG, "Took Picture" + data.length);
mCamera.stopPreview();
new Uploader("http://itp.nyu.edu/~dbo3/up.php", data, "androidTest.jpg");
mCamera.startPreview();
// doPost("http://itp.nyu.edu/~dbo3/upity.php", test.getBytes());

}

// };

class Preview extends SurfaceView implements SurfaceHolder.Callback {
SurfaceHolder mHolder;

Preview(Context context) {
super(context);

// Install a SurfaceHolder.Callback so we get notified when the
// underlying surface is created and destroyed.
mHolder = getHolder();
mHolder.addCallback(this);
mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
}

public void surfaceCreated(SurfaceHolder holder) {
// The Surface has been created, acquire the camera and tell it where
// to draw.

mCamera.setPreviewDisplay(holder);

}

public void surfaceDestroyed(SurfaceHolder holder) {
// Surface will be destroyed when we return, so stop the preview.
// Because the CameraDevice object is not a shared resource, it's very
// important to release it when the activity is paused.
mCamera.stopPreview();
mCamera = null;
}

public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
// Now that the size is known, set up the camera parameters and begin
// the preview.
//Camera.Parameters parameters = mCamera.getParameters();
///parameters.setPreviewSize(w, h);
//mCamera.setParameters(parameters);
mCamera.startPreview();
}

}

public class Uploader extends Thread {
String urlString;

byte[] payload;

String fileNameOnServer;

public Uploader(String _urlString, byte[] _payload, String _fileNameOnServer) {
urlString = _urlString;
payload = _payload;
fileNameOnServer = _fileNameOnServer;
start();
}

public void run() {
HttpURLConnection conn = null;
DataOutputStream dos = null;
DataInputStream inStream = null;

String lineEnd = "\r\n";
String twoHyphens = "Ð";
String boundary = "ÑÑÑÑÑÑÑÑÑÐ29772313742745";

try {
// ÑÑÑÑÑÑ CLIENT REQUEST

URL url = new URL(urlString);
// Open a HTTP connection to the URL
conn = (HttpURLConnection) url.openConnection();

// Allow Inputs
conn.setDoInput(true);
// Allow Outputs
conn.setDoOutput(true);
// Don't use a cached copy.
conn.setUseCaches(false);
// Use a post method.
conn.setRequestMethod("POST");
conn.setRequestProperty("Connection", "Keep-Alive");
// conn.setRequestProperty("Cookie", "JSESSIONID="+PlayList.getSessionId());
conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

dos = new DataOutputStream(conn.getOutputStream());

dos.writeBytes(twoHyphens + boundary + lineEnd);
// dos.writeBytes("Content-Disposition: form-data; name=\"fileNameOnServer\""+lineEnd+URLEncoder.encode(fileNameOnServer,"UTF-8") + lineEnd);
//dos.writeBytes(("Content-Disposition: form-data; name=\"data_file\"; filename=\"" + lineEnd + URLEncoder.encode(fileNameOnServer, "UTF-8) + "\r\n"));
dos.writeBytes("Content-Type: " + "image/jpg" + " \r\n");
dos.writeBytes(lineEnd);
//Log.v(TAG, fileNameOnServer);

Log.v(TAG, "Headers are written");

// create a buffer of maximum size
dos.write(payload);

dos.writeBytes(lineEnd);
dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

// close streams
Log.v(TAG, "File is written");

dos.flush();
dos.close();

} catch (MalformedURLException ex) {
Log.v(TAG, "error: " + ex.getMessage(), ex);
}

catch (Exception ioe) {
Log.v(TAG, "error: " + ioe.getMessage(), ioe);
}

// ÑÑÑÑÑÑ read the SERVER RESPONSE

try {
inStream = new DataInputStream(conn.getInputStream());
String str;

while ((str = inStream.readLine()) != null) {
Log.v(TAG, "Server Response" + str);
}
inStream.close();
freeToSend = true;
Log.v(TAG, "FreeToSend " + freeToSend);

} catch (Exception ioex) {
Log.v(TAG, "error: " + ioex.getMessage(), ioex);
}

}
}

}
