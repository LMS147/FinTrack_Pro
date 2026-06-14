package com.example.fintrackpro.data

import android.net.Uri
import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converters for Room to handle non-primitive data types.
 * Essential for storing Date objects and Android Uri references.
 */
class Converters {

    // --- Date Converters ---
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // --- Uri Converters (for photo storage) ---
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }

    // --- Additional converters can be added for Currency or Enums as needed ---
}