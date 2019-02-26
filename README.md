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


Finally to call camera in your activity or fragment

	    CaptureImageActivity.IntentBuilder intentBuilder = new CaptureImageActivity.IntentBuilder(getActivity(),DIRECTORY_PATH_TO_STORE_FILE);
                        intentBuilder.setFacing(Capture.FACING_FRONT)
                                .setFlashMode(Capture.FLASH_AUTO);
                        Intent intent = intentBuilder.build();
                        startActivityForResult(intent,CAPTURE_IMAGE_CODE);


         @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

                switch (requestCode) {
                    case CAPTURE_IMAGE_CODE:
                        if (resultCode == RESULT_OK) {
                            photoPath = (Uri) data.getExtras().get(Capture.IMAGE_PATH);
                            }
                        }
                }



