/*
 *
 *  * Copyright (c) 2018.
 *  * Created by Josua Lengwenath
 *
 */

@file:Suppress("NAME_SHADOWING", "DEPRECATION")

package com.dertyp7214.qrcodedialog.helpers

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import com.dertyp7214.qrcodedialog.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*

object QRCodeHelper {

    private const val QRCodeDimension = 1000

    fun generateQRCode(context: Context, tintBitmap: Bitmap?, Value: String): Bitmap {
        val tintBitmap = tintBitmap
                ?: { val bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); bmp.eraseColor(Color.BLACK); bmp }()
        val bitMatrix: BitMatrix

        try {
            val hintMap = HashMap<EncodeHintType, ErrorCorrectionLevel>()
            hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            bitMatrix = MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRCodeDimension, QRCodeDimension, hintMap
            )
        } catch (e: Exception) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        (0 until bitMatrixHeight).forEach { y ->
            val offset = y * bitMatrixWidth
            (0 until bitMatrixWidth).forEach { x ->
                pixels[offset + x] = if (bitMatrix.get(x, y))
                    context.resources.getColor(R.color.QRCodeBlackColor)
                else
                    context
                            .resources.getColor(R.color.QRCodeWhiteColor)
            }
        }

        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, QRCodeDimension, 0, 0, bitMatrixWidth, bitMatrixHeight)

        return imageTint(bitmap, tintBitmap)
    }

    private fun imageTint(bitmap: Bitmap, tintBitmap: Bitmap?): Bitmap {
        val whitePane = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        whitePane.eraseColor(Color.BLACK)

        val tintImage = mergeBitmaps(Bitmap.createScaledBitmap(tintBitmap!!, (bitmap.width * 0.8f).toInt(),
                (bitmap.height * 0.8f).toInt(), false), whitePane, bitmap.width,
                1.2f)

        (0 until bitmap.width).forEach { x ->
            (0 until bitmap.height).forEach { y ->
                val isBlack = bitmap.getPixel(x, y) == Color.BLACK
                if (isBlack) {
                    bitmap.setPixel(x, y, darkenColor(tintImage.getPixel(x, y)))
                }
            }
        }
        return bitmap
    }

    private fun darkenColor(@ColorInt color: Int): Int {
        var color = color
        while (isBrightColor(color)) color = manipulateColor(color, 0.99f)
        return color
    }

    private fun manipulateColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = Math.round(Color.red(color) * factor)
        val g = Math.round(Color.green(color) * factor)
        val b = Math.round(Color.blue(color) * factor)
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255))
    }

    private fun isBrightColor(color: Int): Boolean {
        if (android.R.color.transparent == color)
            return true
        var rtnValue = false
        val rgb = intArrayOf(Color.red(color), Color.green(color), Color.blue(color))

        val brightness = Math.sqrt(rgb[0].toDouble() * rgb[0].toDouble() * .241 + (rgb[1].toDouble()
                * rgb[1].toDouble() * .691) + rgb[2].toDouble() * rgb[2].toDouble() * .068).toInt()

        if (brightness >= 180) {
            rtnValue = true
        }
        return rtnValue
    }

    private fun mergeBitmaps(overlay: Bitmap, bitmap: Bitmap, bigDim: Int, overlayMetric: Float): Bitmap {
        var overlay = overlay
        val height = bitmap.height
        val width = bitmap.width

        overlay = Bitmap.createScaledBitmap(overlay, (bigDim / overlayMetric).toInt(),
                (bigDim / overlayMetric).toInt(),
                false)

        val combined = Bitmap.createBitmap(width, height, bitmap.config)
        val canvas = Canvas(combined)
        val canvasWidth = canvas.width
        val canvasHeight = canvas.height

        canvas.drawBitmap(bitmap, Matrix(), null)

        val centreX = (canvasWidth - overlay.width) / 2
        val centreY = (canvasHeight - overlay.height) / 2
        canvas.drawBitmap(overlay, centreX.toFloat(), centreY.toFloat(), null)

        return combined
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1,
                    Bitmap.Config.ARGB_8888
            )
        } else {
            Bitmap
                    .createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight,
                            Bitmap.Config.ARGB_8888
                    )
        }

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
