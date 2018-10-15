package com.dertyp7214.qrcodepopup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dertyp7214.qrcodedialog.components.QRCodeDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.textView).setOnClickListener(v -> {
            QRCodeDialog qrCodeDialog = new QRCodeDialog(this);
            qrCodeDialog.show("Hallo");
        });
    }
}
