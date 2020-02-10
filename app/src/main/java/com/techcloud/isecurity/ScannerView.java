package com.techcloud.isecurity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;

import com.techcloud.isecurity.camera.CardScannerView;
import com.techcloud.isecurity.camera.DisplayUtils;

public class ScannerView extends CardScannerView {
    private static final String TAG = "ScannerView";

    public interface ResultHandler {
        void handleResult(Bitmap bitmap);
    }

    private ResultHandler mResultHandler;


    public ScannerView(Context context, ResultHandler handler) {
        super(context);
        mResultHandler = handler;
    }

    public ScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Log.d(TAG, "Bitmap width: " + bitmap.getWidth() + "\nBitmap Height: " + bitmap.getHeight());
        int left = bitmap.getWidth() / 3;
        System.out.println("Left: " + left);
        int top = bitmap.getHeight() / 6 + 100;
        System.out.println("Top: " + top);
        int width = bitmap.getWidth() / 3 + 50;
        System.out.println("Width: " + width);
        int height = bitmap.getHeight() - 900;
        System.out.println("Height: " + height);
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.postRotate(90);
        // Note that because of the rotation x and width cover the horizontal position of bitmap and y and height cover the vertical
        //Bitmap newBitmap = Bitmap.createBitmap(bitmap, 1000, 500, 1050, 1500, rotationMatrix, false);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, left, top, width, height, rotationMatrix, false);
        mResultHandler.handleResult(newBitmap);
    }

    public void resumeCameraPreview(){
        super.resumeCameraPreview();
    }
}
