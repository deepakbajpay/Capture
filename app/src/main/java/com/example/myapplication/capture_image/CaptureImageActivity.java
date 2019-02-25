package com.example.myapplication.capture_image;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.utils.Utilities;
import com.example.myapplication.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.myapplication.utils.Constants.GALLERY_PERMISSIONS;

public class CaptureImageActivity extends BaseActivity {

    public static final int MULTIPLE_PERMISSIONS = 199;
    private static final int TAKE_PICTURE = 198;
    ImageView previewIv, captureIv;
    private boolean documentSupport = false;
    private boolean showRemoveOption = false;
    private Uri photoURI;
    static IntentBuilder intentBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);


        checkPermission(false, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkPermission(false, false);
    }

    public CaptureImageActivity getCaptureImageActivity(){
        return this;
    }
    public void checkPermission(boolean documentSupport, boolean showRemoveOption) {
        this.showRemoveOption = showRemoveOption;

        this.documentSupport = documentSupport;
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : GALLERY_PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PERMISSION_GRANTED)
                listPermissionsNeeded.add(p);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            }
        } else
            openCameraFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0) {
                int result;
                List<String> listPermissionsNeeded = new ArrayList<>();
                for (int grantResult : grantResults) {
                    for (String p : permissions) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            listPermissionsNeeded.add(p);
                            if (Utilities.shouldShowRequestPermissionRationale(this, p))
                                return;
                        }
                    }
                }

                if (!listPermissionsNeeded.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(this, "Can not proceed without permissions", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    openCameraFragment();
                }
            }
        }
    }


    public void openCameraFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new CameraFragment()).commitAllowingStateLoss();
    }

    public static class IntentBuilder{

        private static final String FLASH_MODE = "flash_mode";
        private static final String CAMERA_FACING = "facing";
        static final String IMAGE_PATH = "image_path";

        public static final int FLASH_AUTO =0;
        public static final int FLASH_ON =1;
        public static final int FLASH_OFF =2;
        public static final int FLASH_TORCH =3;

        public static final int FACING_FRONT =0;
        public static final int FACING_BACK =1;
        private int flashMode=0;
        private int cameraFacing=1;

        public int getFlashMode() {
            return flashMode;
        }

        public int getCameraFacing() {
            return cameraFacing;
        }

        public String getStoragePath() {
            return storagePath;
        }

        private Context context;
        private String storagePath;

        public IntentBuilder(Context context,String storagePath){
            this.context = context;
            this.storagePath = storagePath;
        }

        public IntentBuilder setFlashMode(int flashMode){
            this.flashMode = flashMode;
            return this;
        }

        public IntentBuilder setFacing(int facing){
            this.cameraFacing = facing;
            return this;
        }

        public Intent build(){
            Intent intent = new Intent(context,CaptureImageActivity.class);
            intent.putExtra(FLASH_MODE,flashMode);
            intent.putExtra(CAMERA_FACING,cameraFacing);
            intentBuilder = this;
            return intent;
        }

    }

}
