package com.example.fintrackpro.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoHelper {
    fun createImageFile(context: Context): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageDir = File(context.filesDir, "photos")
        if (!imageDir.exists()) imageDir.mkdirs()
        return try {
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", imageDir)
        } catch (e: IOException) {
            null
        }
    }

    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val outputFile = createImageFile(context) ?: return null
            val outputStream = FileOutputStream(outputFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(outputFile).toString()
        } catch (e: Exception) {
            null
        }
    }
}
