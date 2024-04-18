package com.szlazakm.safechat

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.szlazakm.safechat.client.data.DAO.ContactDao
import com.szlazakm.safechat.client.data.DAO.MessageDao
import com.szlazakm.safechat.client.data.DAO.UserDao
import com.szlazakm.safechat.client.data.Entities.ContactEntity
import com.szlazakm.safechat.client.data.Entities.Converters.LocalDateTimeConverter
import com.szlazakm.safechat.client.data.Entities.MessageEntity
import com.szlazakm.safechat.client.data.Entities.UserEntity

@Database(
    entities = [ContactEntity::class, MessageEntity::class, UserEntity::class],
    version = 7
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao

}