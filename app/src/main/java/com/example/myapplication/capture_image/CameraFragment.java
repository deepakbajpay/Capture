package com.example.myapplication.capture_image;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.example.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class CameraFragment extends Fragment implements CameraKitView.ImageCallback, View.OnClickListener {

    //    private ImageView ivFlashSwitch;
//    private ImageView ivPhoto;
    private static final String TAG = "CAMERAFRAGMENT";
    CaptureImageActivity captureImageActivity;
    private CameraKitView camera;
    private File pictureFile;
    private Uri photoURI;
    private ImageView ivFlashSwitch;

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        camera = view.findViewById(R.id.camera);
        camera.onStart();
        Button ivCapture = view.findViewById(R.id.button_capture);
        /*ivFlashSwitch = view.findViewById(R.id.ivFlashSwitch);
        ivPhoto = view.findViewById(R.id.ivPhoto);
*/
        ivCapture.setOnClickListener((View v) -> camera.captureImage(this));
        ivFlashSwitch = view.findViewById(R.id.flash_switch_iv);
        ImageView switchCameraIv = view.findViewById(R.id.camera_switch_iv);
        switchCameraIv.setOnClickListener(this);
        ivFlashSwitch.setOnClickListener(this);
        if (CaptureImageActivity.intentBuilder.getCameraFacing() == 0) {
            camera.setFacing(CameraKit.FACING_FRONT);
        } else {
            camera.setFacing(CameraKit.FACING_BACK);
        }

        if (CaptureImageActivity.intentBuilder.getFlashMode() == 0) {
            switchFlashMode(CameraKit.FLASH_AUTO);
        } else if (CaptureImageActivity.intentBuilder.getFlashMode() == 1) {
            switchFlashMode(CameraKit.FLASH_ON);
        } else if (CaptureImageActivity.intentBuilder.getFlashMode() == 2) {
            switchFlashMode(CameraKit.FLASH_OFF);
        } else {
            switchFlashMode(CameraKit.FLASH_TORCH);
        }
        if (camera.getFacing() == CameraKit.FACING_FRONT) {
            switchFlashMode(CameraKit.FLASH_ON);
            ivFlashSwitch.setEnabled(false);
        } else {
            switchFlashMode(CameraKit.FLASH_OFF);
            ivFlashSwitch.setEnabled(true);
        }

//        ivPhoto.setOnClickListener((View v) -> v.setVisibility(View.GONE));

//        setupCamera();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof CaptureImageActivity) {
            captureImageActivity = (CaptureImageActivity) getActivity();
        }
    }

    private void switchFlashMode(int currentFlashMode) {
        @DrawableRes int flashDrawableId;

        switch (currentFlashMode) {

            case CameraKit.FLASH_AUTO:
                flashDrawableId = R.drawable.ic_flash_auto_black_24dp;
                camera.setFlash(CameraKit.FLASH_AUTO);
                ivFlashSwitch.setImageDrawable(ContextCompat.getDrawable(getActivity(), flashDrawableId));
                break;

            case CameraKit.FLASH_ON:
                flashDrawableId = R.drawable.ic_flash_on_black_24dp;
                camera.setFlash(CameraKit.FLASH_ON);
                ivFlashSwitch.setImageDrawable(ContextCompat.getDrawable(getActivity(), flashDrawableId));
                break;

            case CameraKit.FLASH_OFF:
                flashDrawableId = R.drawable.ic_flash_off_black_24dp;
                camera.setFlash(CameraKit.FLASH_OFF);
                ivFlashSwitch.setImageDrawable(ContextCompat.getDrawable(getActivity(), flashDrawableId));
                break;
            case CameraKit.FLASH_TORCH:
                flashDrawableId = R.drawable.ic_flash_torch;
                camera.setFlash(CameraKit.FLASH_TORCH);
                ivFlashSwitch.setImageDrawable(ContextCompat.getDrawable(getActivity(), flashDrawableId));
                break;
            default:
                return;
        }

        ivFlashSwitch.setImageDrawable(ActivityCompat.getDrawable(requireContext(), flashDrawableId));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            camera.onResume();
        }
    }

    @Override
    public void onPause() {
        camera.onStop();
        super.onPause();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onImage(CameraKitView cameraKitView, byte[] data) {

        Bitmap storedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
        Matrix mat = new Matrix();
        // angle is the desired angle you wish to rotate

        storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        storedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        storedBitmap.recycle();

        pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

        if (Build.VERSION.SDK_INT < 24)
            photoURI = Uri.fromFile(pictureFile);
        else
            photoURI = FileProvider.getUriForFile(getActivity()
                    , getString(R.string.file_provider_name), pictureFile);

        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(byteArray);
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        setImagePathAndFinish(photoURI);

    }

    private void setImagePathAndFinish(Uri imagePath) {
        Intent intent = new Intent();
        intent.putExtra(CaptureImageActivity.IntentBuilder.IMAGE_PATH, imagePath);
        getActivity().setResult(RESULT_OK, intent);
        System.out.println("CaptureImageActivity.setImagePathAndFinish");
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        int resourceId = v.getId();
        if (resourceId == R.id.camera_switch_iv) {
            switch (camera.getFacing()) {
                case CameraKit.FACING_BACK:
                    ivFlashSwitch.setEnabled(false);
                    switchFlashMode(CameraKit.FLASH_OFF);
                    camera.setFacing(CameraKit.FACING_FRONT);
                    break;
                case CameraKit.FACING_FRONT:
                    ivFlashSwitch.setEnabled(true);
                    switchFlashMode(CameraKit.FLASH_AUTO);
                    camera.setFacing(CameraKit.FACING_BACK);
                    break;
            }

        } else if (resourceId == R.id.flash_switch_iv) {
            switch (camera.getFlash()) {
                case CameraKit.FLASH_AUTO:
                    switchFlashMode(CameraKit.FLASH_ON);
                    break;
                case CameraKit.FLASH_ON:
                    switchFlashMode(CameraKit.FLASH_OFF);
                    break;
                case CameraKit.FLASH_OFF:
                    switchFlashMode(CameraKit.FLASH_TORCH);
                    break;
                case CameraKit.FLASH_TORCH:
                    switchFlashMode(CameraKit.FLASH_AUTO);
            }
        }
    }
}
