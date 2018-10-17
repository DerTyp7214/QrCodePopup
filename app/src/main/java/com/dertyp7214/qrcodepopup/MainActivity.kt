package com.dertyp7214.qrcodepopup

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.dertyp7214.qrcodedialog.components.QRCodeDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.textView).setOnClickListener {
            val qrCodeDialog = QRCodeDialog(this)
            qrCodeDialog.customImageTint(
                    BitmapFactory.decodeResource(resources, R.drawable.ic_launcher))
            qrCodeDialog.show(getString(R.string.long_string))
        }
    }
}
