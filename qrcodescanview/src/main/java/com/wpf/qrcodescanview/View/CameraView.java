package com.wpf.qrcodescanview.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.util.List;

/**
 * Created by 王朋飞 on 6-20-0020.
 * 摄像头View---5.0以下
 */

public abstract class CameraView extends SurfaceView implements
        SurfaceHolder.Callback2 ,
        Camera.PreviewCallback ,
        Camera.AutoFocusCallback {

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Camera.Parameters parameters;
    private Camera.Size size;
    private boolean isFocus;
    private QRCodeReader qrCodeReader = new QRCodeReader();

    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    private void initCamera() {
        camera = Camera.open();
        if (camera == null) return;
        try {
            setParameters();
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.setPreviewCallback(this);
        camera.autoFocus(this);
        camera.startPreview();
    }

    private void setParameters() {
        parameters = camera.getParameters();
        size = getSupportedPreviewSizes();
        parameters.setRotation(90);
        parameters.setPictureSize(size.width,size.height);
        camera.setParameters(parameters);
    }

    private Camera.Size getSupportedPreviewSizes() {
        List<Camera.Size> previewSize = camera.getParameters().getSupportedPreviewSizes();
        return previewSize.get(0);
    }

    private void scan(byte[] data) {
        if (ScanQRCode.mRect != null) {
            int imageWidth = size.width, imageHeight = size.height;
            int left = ScanQRCode.mRect.left * imageWidth / getWidth();
            int top = ScanQRCode.mRect.top * imageHeight / getHeight();
            int width = (int) (ScanQRCode.mRect.width() * imageWidth * 1.5) / getWidth();
            int height = (int) (ScanQRCode.mRect.height() * imageHeight * 1.5) / getHeight();
            PlanarYUVLuminanceSource source =
                    new PlanarYUVLuminanceSource(data, imageWidth, imageHeight,
                            left, top, width, height, false);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            String rawResult = getDecodeResult(bitmap);
            if (!rawResult.isEmpty()) onSuccess(rawResult, null);
        }
    }

    private String getDecodeResult(BinaryBitmap bitmap) {
        Result rawResult = null;
        try {
            rawResult = qrCodeReader.decode(bitmap);
        } catch (NotFoundException | ChecksumException | FormatException ignored) {
        } finally {
            qrCodeReader.reset();
        }
        return rawResult == null ? "" : rawResult.getText();
    }

    private void close() {
        try {
            camera.reconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        close();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        scan(bytes);
    }

    @Override
    public void onAutoFocus(boolean b, Camera camera) {

    }

    public abstract void onSuccess(String result, Bitmap bitmap);
}