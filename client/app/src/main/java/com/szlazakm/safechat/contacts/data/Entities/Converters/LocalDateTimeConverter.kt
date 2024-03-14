package com.szlazakm.safechat.contacts.data.Entities.Converters

import androidx.room.TypeConverter
import java.util.Date

object LocalDateTimeConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}