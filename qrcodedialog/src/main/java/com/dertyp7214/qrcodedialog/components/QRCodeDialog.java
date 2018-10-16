/*
 *
 *  * Copyright (c) 2018.
 *  * Created by Josua Lengwenath
 *
 */

package com.dertyp7214.qrcodedialog.components;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import com.dertyp7214.qrcodedialog.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static android.content.ContentValues.TAG;
import static com.dertyp7214.qrcodedialog.helpers.QRCodeHelper.drawableToBitmap;
import static com.dertyp7214.qrcodedialog.helpers.QRCodeHelper.generateQRCode;

public class QRCodeDialog extends Dialog {

    private final Activity activity;

    private ImageView qrCode;
    private ProgressDialog progressDialog;
    private Bitmap bitmap, tintBitmap;

    public QRCodeDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_layout);

        qrCode = findViewById(R.id.qr_code);

        if (bitmap == null) super.dismiss();
        else {
            qrCode.setImageBitmap(bitmap);
            qrCode.setOnClickListener(
                    v -> shareImageUri(saveImage(drawableToBitmap(qrCode.getDrawable()))));
        }
    }

    private Uri saveImage(Bitmap image) {
        File imagesFolder = new File(activity.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider
                    .getUriForFile(activity, "com.dertyp7214.qrcodepopup.fileprovider", file);
        } catch (Exception e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    private void shareImageUri(Uri uri) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.app_name)));
    }

    public void customImageTint(Bitmap tintBitmap) {
        this.tintBitmap = tintBitmap;
    }

    public void show(String content) {
        activity.runOnUiThread(() -> {
            progressDialog = ProgressDialog
                    .show(activity, "", activity.getString(R.string.loading_generating_qr_code));

            new Thread(() -> {
                bitmap = generateQRCode(activity, tintBitmap, content);
                activity.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    super.show();
                });
            }).start();
        });
    }
}
