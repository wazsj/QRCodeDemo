package com.wpf.qrcodescanview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.wpf.qrcodescanview.Listener.OnFinishListener;
import com.wpf.qrcodescanview.View.ScanQRCode;

/**
 * 扫描二维码Activity
 * 返回结果 ResultString
 */
public class ScanQRCodeActivity extends AppCompatActivity implements
        OnFinishListener {

    private ScanQRCode scanQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sacn_qrcode);
        scanQRCode = (ScanQRCode) findViewById(R.id.cameraView);
        scanQRCode.setOnFinishListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        scanQRCode.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onFinish(String result, Bitmap bitmap) {
        Intent intent = getIntent();
        intent.putExtra("ResultString",result);
        setResult(RESULT_OK,intent);
//        Bundle bundle = new Bundle();
//        bundle.putString("ResultString",result);
//        bundle.putParcelable("Bitmap",bitmap);
//        setResult(RESULT_OK,getIntent().putExtra("data",bundle));
        finish();
    }
}
