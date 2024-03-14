package com.szlazakm.safechat

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.szlazakm.safechat.contacts.data.DAO.ContactDao
import com.szlazakm.safechat.contacts.data.DAO.MessageDao
import com.szlazakm.safechat.contacts.data.Entities.ContactEntity
import com.szlazakm.safechat.contacts.data.Entities.Converters.LocalDateTimeConverter
import com.szlazakm.safechat.contacts.data.Entities.MessageEntity

@Database(
    entities = [ContactEntity::class, MessageEntity::class],
    version = 3
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao

}