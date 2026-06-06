package com.example.scanner.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

// ẢNH ĐƯỢC RESIZE TRƯỚC KHI UPLOAD LÊN CLOUD
fun convertFileToByteArray(context: Context, uri: Uri, maxWidth: Int = 1600): ByteArray {
    val contentResolver = context.contentResolver

    // Cách decode tốt hơn để giữ chất lượng
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    }

    // Tính toán scale
    var scale = 1
    while (options.outWidth / scale > maxWidth) {
        scale *= 2
    }

    options.apply {
        inJustDecodeBounds = false
        inSampleSize = scale
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    val inputStream = contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
    inputStream?.close()

    // Resize mượt mà
    val resizedBitmap = if (bitmap!!.width > maxWidth) {
        val ratio = maxWidth.toFloat() / bitmap.width
        val newHeight = (bitmap.height * ratio).toInt()
        Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true)
    } else {
        bitmap
    }

    val byteArrayOutputStream = ByteArrayOutputStream()

    // Compress với chất lượng cao
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 92, byteArrayOutputStream)

    // Giải phóng bộ nhớ
    if (resizedBitmap != bitmap) resizedBitmap.recycle()
    bitmap.recycle()

    return byteArrayOutputStream.toByteArray()
}
//   CODE LẤY ẢNH GỐC
//fun convertFileToByteArray(context: Context, uri: Uri): ByteArray {
//    val contentResolver = context.contentResolver
//
//    // Decode ảnh gốc mà không resize
//    val options = BitmapFactory.Options().apply {
//        inPreferredConfig = Bitmap.Config.ARGB_8888
//        inSampleSize = 1          // Không scale xuống
//    }
//
//    val inputStream = contentResolver.openInputStream(uri)
//    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
//    inputStream?.close()
//
//    val byteArrayOutputStream = ByteArrayOutputStream()
//
//    // Compress với chất lượng cao
//    bitmap?.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream)
//
//    // Giải phóng bộ nhớ
//    bitmap?.recycle()
//
//    return byteArrayOutputStream.toByteArray()
//}