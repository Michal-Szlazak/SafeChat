package com.szlazakm.safechat

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.szlazakm.safechat.client.data.dao.ContactDao
import com.szlazakm.safechat.client.data.dao.EncryptionSessionDao
import com.szlazakm.safechat.client.data.dao.MessageDao
import com.szlazakm.safechat.client.data.dao.OPKDao
import com.szlazakm.safechat.client.data.dao.SPKDao
import com.szlazakm.safechat.client.data.dao.UserDao
import com.szlazakm.safechat.client.data.entities.ContactEntity
import com.szlazakm.safechat.client.data.entities.converters.LocalDateTimeConverter
import com.szlazakm.safechat.client.data.entities.EncryptionSessionEntity
import com.szlazakm.safechat.client.data.entities.MessageEntity
import com.szlazakm.safechat.client.data.entities.OPKEntity
import com.szlazakm.safechat.client.data.entities.SPKEntity
import com.szlazakm.safechat.client.data.entities.UserEntity

@Database(
    entities = [
        ContactEntity::class,
        MessageEntity::class,
        UserEntity::class,
        OPKEntity::class,
        SPKEntity::class,
        EncryptionSessionEntity::class
               ],
    version = 12
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao
    abstract fun opkDao(): OPKDao
    abstract fun spkDao(): SPKDao
    abstract fun encryptionSessionDao(): EncryptionSessionDao
}