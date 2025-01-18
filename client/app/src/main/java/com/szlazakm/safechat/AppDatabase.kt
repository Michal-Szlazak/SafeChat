package com.szlazakm.safechat

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.szlazakm.safechat.client.data.dao.ContactDao
import com.szlazakm.safechat.client.data.dao.EphemeralRatchetEccKeyPairDao
import com.szlazakm.safechat.client.data.dao.IdentityKeyDao
import com.szlazakm.safechat.client.data.dao.MessageDao
import com.szlazakm.safechat.client.data.dao.MessageKeysDao
import com.szlazakm.safechat.client.data.dao.OPKDao
import com.szlazakm.safechat.client.data.dao.ReceiverChainKeyDao
import com.szlazakm.safechat.client.data.dao.RootKeyDao
import com.szlazakm.safechat.client.data.dao.SPKDao
import com.szlazakm.safechat.client.data.dao.SenderChainKeyDao
import com.szlazakm.safechat.client.data.dao.UserDao
import com.szlazakm.safechat.client.data.entities.ContactEntity
import com.szlazakm.safechat.client.data.entities.converters.LocalDateTimeConverter
import com.szlazakm.safechat.client.data.entities.EncryptionSession
import com.szlazakm.safechat.client.data.entities.EphemeralRatchetEccKeyPairEntity
import com.szlazakm.safechat.client.data.entities.IdentityKeyEntity
import com.szlazakm.safechat.client.data.entities.MessageEntity
import com.szlazakm.safechat.client.data.entities.MessageKeysEntity
import com.szlazakm.safechat.client.data.entities.OPKEntity
import com.szlazakm.safechat.client.data.entities.ReceiverChainKeyEntity
import com.szlazakm.safechat.client.data.entities.RootKeyEntity
import com.szlazakm.safechat.client.data.entities.SPKEntity
import com.szlazakm.safechat.client.data.entities.SenderChainKeyEntity
import com.szlazakm.safechat.client.data.entities.UserEntity

@Database(
    entities = [
        ContactEntity::class,
        MessageEntity::class,
        UserEntity::class,
        OPKEntity::class,
        SPKEntity::class,
        RootKeyEntity::class,
        SenderChainKeyEntity::class,
        ReceiverChainKeyEntity::class,
        EphemeralRatchetEccKeyPairEntity::class,
        MessageKeysEntity::class,
        IdentityKeyEntity::class
               ],
    version = 18
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao
    abstract fun opkDao(): OPKDao
    abstract fun spkDao(): SPKDao
    abstract fun rootKeyDao(): RootKeyDao
    abstract fun senderChainKeyDao(): SenderChainKeyDao
    abstract fun receiverChainKeyDao(): ReceiverChainKeyDao
    abstract fun ephemeralRatchetEccKeyPairDao(): EphemeralRatchetEccKeyPairDao
    abstract fun messageKeysDao(): MessageKeysDao
    abstract fun identityKeyDao(): IdentityKeyDao

}