package com.ceslab.team7_ble_meet.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import id.zelory.compressor.Compressor
import java.io.File


class CompressImageUtils {
    private val TAG = "CompressImageUtils"
    private val MAX_IMAGE_WIDTH = 800
    private val MAX_IMAGE_HEIGHT = 600

    fun compress(actualImage: File?, context: Context?): File? {
        val compressedImage: File
        if (actualImage == null) {
            return null
        } else {
            val bitmap = BitmapFactory.decodeFile(actualImage.absolutePath)
            val width = bitmap.width
            val height = bitmap.height
            val maxHeight =
                if (height > MAX_IMAGE_HEIGHT) MAX_IMAGE_HEIGHT.toDouble() else height.toDouble()
            val maxWidth = width.toDouble() / height.toDouble() * maxHeight
            Log.i(TAG, "compress: " + height.toFloat() / width.toFloat())
            Log.i(TAG, "compress: " + maxWidth.toInt() + ", " + maxHeight.toInt())
            Log.i(TAG, "compress: $width, $height")
            compressedImage = Compressor.Builder(context)
                .setMaxWidth(maxWidth.toInt().toFloat())
                .setMaxHeight(maxHeight.toInt().toFloat())
                .build()
                .compressToFile(actualImage)
            val bitmap1 = BitmapFactory.decodeFile(compressedImage.absolutePath)
            Log.i(TAG, "compress: " + bitmap1.byteCount)
        }
        return compressedImage
    }
}