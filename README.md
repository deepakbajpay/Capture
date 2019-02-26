# Capture
a custom android library for image capture

To add this liberary to your project add following to your gradle.
	
      allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	    }
  
      dependencies {
	        implementation 'com.github.deepakbajpay:Capture:1.0.1'
	}
	
Add following to your manifest 
        
	<application>
	
	 <activity android:name="com.deepakbajpay.capture.capture_image.CaptureImageActivity"
            android:screenOrientation="portrait"
            android:configChanges="orientation|screenSize|keyboardHidden"/>
	    
	    </application>
