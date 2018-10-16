/*
 *
 *  * Copyright (c) 2018.
 *  * Created by Josua Lengwenath
 *
 */

package com.dertyp7214.qrcodedialog.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.Base64;

import com.dertyp7214.qrcodedialog.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class QRCodeHelper {

    private final static int QRCodeDimension = 1000;

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap generateQRCode(Context context, Bitmap tintBitmap, String Value) {
        BitMatrix bitMatrix;

        try {
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRCodeDimension, QRCodeDimension, hintMap
            );
        } catch (Exception e) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ?
                        context.getResources().getColor(R.color.QRCodeBlackColor) : context
                        .getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }

        Bitmap bitmap =
                Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, QRCodeDimension, 0, 0, bitMatrixWidth, bitMatrixHeight);

        if (tintBitmap == null)
            tintBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

        return imageTint(bitmap, tintBitmap);
    }

    private static Bitmap imageTint(Bitmap bitmap, Bitmap tintBitmap) {

        Bitmap whitePane =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        whitePane.eraseColor(Color.BLACK);

        Bitmap tintImage =
                mergeBitmaps(Bitmap.createScaledBitmap(tintBitmap, (int) (bitmap.getWidth() * 0.8F),
                        (int) (bitmap.getHeight() * 0.8F), false), whitePane, bitmap.getWidth(),
                        1.2F);

        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                boolean isBlack = bitmap.getPixel(x, y) == Color.BLACK;
                if (isBlack) {
                    bitmap.setPixel(x, y, darkenColor(tintImage.getPixel(x, y)));
                }
            }
        }

        return bitmap;
    }

    private static int darkenColor(@ColorInt int color) {
        while (isBrightColor(color)) color = manipulateColor(color, 0.9F);
        return color;
    }

    private static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    private static boolean isBrightColor(int color) {
        if (android.R.color.transparent == color)
            return true;
        boolean rtnValue = false;
        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};

        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);

        if (brightness >= 200) {
            rtnValue = true;
        }
        return rtnValue;
    }

    private static Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap, int bigDim, float overlayMetric) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        overlay = Bitmap.createScaledBitmap(overlay, (int) (bigDim / overlayMetric),
                (int) (bigDim / overlayMetric),
                false);

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    Bitmap.Config.ARGB_8888
            ); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap
                    .createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                            Bitmap.Config.ARGB_8888
                    );
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
