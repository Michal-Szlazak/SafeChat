package com.szlazakm.safechat

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.szlazakm.safechat.contacts.data.DAO.ContactDao
import com.szlazakm.safechat.contacts.data.DAO.MessageDao
import com.szlazakm.safechat.contacts.data.DAO.UserDao
import com.szlazakm.safechat.contacts.data.Entities.ContactEntity
import com.szlazakm.safechat.contacts.data.Entities.Converters.LocalDateTimeConverter
import com.szlazakm.safechat.contacts.data.Entities.MessageEntity
import com.szlazakm.safechat.contacts.data.Entities.UserEntity

@Database(
    entities = [ContactEntity::class, MessageEntity::class, UserEntity::class],
    version = 4
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao

    abstract fun userDao(): UserDao

}