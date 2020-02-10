package com.techcloud.isecurity.activities;

import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.ScannerView;
import com.techcloud.isecurity.fragments.ShowImageFragment;


public class ScanActivity extends AppCompatActivity implements ScannerView.ResultHandler {

    private Bitmap bitmap;
    private ImageView mImageView;
    private ScannerView scannerView;

    public static final String TAG = ScanActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mImageView = findViewById(R.id.image_view);

        scannerView = new ScannerView(this, this);
        setContentView(scannerView);
        //scannerView.startCamera();
    }

    public void startCam() {
        scannerView.stopCamera();
        scannerView.startCamera();
    }

    private void showImageDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ShowImageFragment showImageDialogFragment = ShowImageFragment.newInstance("Photo Taken");
        showImageDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        showImageDialogFragment.show(fm, "fragment_show_image");
    }

    @Override
    public void handleResult(Bitmap bitmap) {
        HolderClass.theBitmap = bitmap;
        showImageDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(HolderClass.user.equals("admin")) {
            Intent intent = new Intent(ScanActivity.this, AdminHomeActivity.class);
            startActivity(intent);
            finish();
        } else if(HolderClass.user.equals("guard")) {
            Intent intent = new Intent(ScanActivity.this, GuardHomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
