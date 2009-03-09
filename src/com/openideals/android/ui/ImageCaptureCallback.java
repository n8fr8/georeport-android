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

import java.io.OutputStream;

import android.hardware.Camera;
import android.util.Log;

public class ImageCaptureCallback implements Camera.PictureCallback  {

	private OutputStream filoutputStream;
	public ImageCaptureCallback(OutputStream filoutputStream) {
	this.filoutputStream = filoutputStream;
	}
	
	
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
	try {
	Log.v(getClass().getSimpleName(), "onPictureTaken=" + data + " length = " + data.length);
	filoutputStream.write(data);
	filoutputStream.flush();
	filoutputStream.close();
	} catch(Exception ex) {
	ex.printStackTrace();
	}
	}
	}
