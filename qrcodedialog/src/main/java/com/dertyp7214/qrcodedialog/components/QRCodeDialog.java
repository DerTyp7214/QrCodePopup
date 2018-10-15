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
import android.widget.ImageView;

import com.dertyp7214.qrcodedialog.R;

import java.io.ByteArrayOutputStream;

import static com.dertyp7214.qrcodedialog.helpers.QRCodeHelper.drawableToBitmap;
import static com.dertyp7214.qrcodedialog.helpers.QRCodeHelper.generateQRCode;

public class QRCodeDialog extends Dialog {

    private final Activity activity;

    private ImageView qrCode;
    private ProgressDialog progressDialog;
    private Bitmap bitmap, logo;

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
            qrCode.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("image/jpeg");
                Bitmap b = drawableToBitmap(qrCode.getDrawable());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media
                        .insertImage(activity.getContentResolver(), b, "Title", null);
                Uri imageUri = Uri.parse(path);
                sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                activity.startActivity(
                        Intent.createChooser(sendIntent, activity.getString(R.string.app_name)));
            });
        }
    }

    public void customLogo(Bitmap logo) {
        this.logo = logo;
    }

    public void show(String content) {
        activity.runOnUiThread(() -> {
            progressDialog = ProgressDialog
                    .show(activity, "", activity.getString(R.string.loading_generating_qr_code));

            new Thread(() -> {
                bitmap = generateQRCode(activity, logo, content);
                activity.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    super.show();
                });
            }).start();
        });
    }
}
