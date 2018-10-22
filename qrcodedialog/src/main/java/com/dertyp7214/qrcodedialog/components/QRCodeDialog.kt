/*
 *
 *  * Copyright (c) 2018.
 *  * Created by Josua Lengwenath
 *
 */

@file:Suppress("DEPRECATION")

package com.dertyp7214.qrcodedialog.components

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.ImageView
import com.dertyp7214.qrcodedialog.R
import com.dertyp7214.qrcodedialog.helpers.QRCodeHelper.drawableToBitmap
import com.dertyp7214.qrcodedialog.helpers.QRCodeHelper.generateQRCode
import java.io.File
import java.io.FileOutputStream
import java.util.*

class QRCodeDialog(private val activity: Activity) : Dialog(activity) {

    private var bitmap: Bitmap? = null
    private var tintBitmap: Bitmap? = null
    private val random: Random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_code_layout)

        val qrCode = findViewById<ImageView>(R.id.qr_code)

        if (bitmap == null)
            super.dismiss()
        else {
            qrCode.setImageBitmap(bitmap)
            qrCode.setOnClickListener { shareImageUri(saveImage(drawableToBitmap(qrCode.drawable)!!)) }
        }
    }

    private fun saveImage(image: Bitmap): Uri? {
        val imagesFolder = File(activity.cacheDir, "images")
        return try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image.png")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            FileProvider
                    .getUriForFile(activity, "com.dertyp7214.qrcodepopup.fileprovider", file)
        } catch (e: Exception) {
            Log.d(TAG, "IOException while trying to write file for sharing: ${e.message}")
            null
        }
    }

    private fun shareImageUri(uri: Uri?) {
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        activity.startActivity(Intent.createChooser(
                intent,
                activity.getString(R.string.app_name)
        ))
    }

    fun customImageTint(tintBitmap: Bitmap) {
        this.tintBitmap = tintBitmap
    }

    fun customColor(@ColorInt color: Int) {
        val bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        bmp.eraseColor(color)
        this.tintBitmap = bmp
    }

    fun show(content: String) {
        activity.runOnUiThread {
            val progressDialog = ProgressDialog
                    .show(
                            activity,
                            "",
                            activity.getString(R.string.loading_generating_qr_code)
                    )
            Thread {
                bitmap = generateQRCode(activity, tintBitmap, content)
                activity.runOnUiThread {
                    progressDialog!!.dismiss()
                    super.show()
                }
            }.start()
        }
    }
}
