package com.szlazakm.safechat

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.szlazakm.safechat.client.data.DAO.ContactDao
import com.szlazakm.safechat.client.data.DAO.MessageDao
import com.szlazakm.safechat.client.data.DAO.OPKDao
import com.szlazakm.safechat.client.data.DAO.SPKDao
import com.szlazakm.safechat.client.data.DAO.UserDao
import com.szlazakm.safechat.client.data.Entities.ContactEntity
import com.szlazakm.safechat.client.data.Entities.Converters.LocalDateTimeConverter
import com.szlazakm.safechat.client.data.Entities.MessageEntity
import com.szlazakm.safechat.client.data.Entities.OPKEntity
import com.szlazakm.safechat.client.data.Entities.SPKEntity
import com.szlazakm.safechat.client.data.Entities.UserEntity

@Database(
    entities = [
        ContactEntity::class,
        MessageEntity::class,
        UserEntity::class,
        OPKEntity::class,
        SPKEntity::class
               ],
    version = 10
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao
    abstract fun opkDao(): OPKDao
    abstract fun spkDao(): SPKDao

}