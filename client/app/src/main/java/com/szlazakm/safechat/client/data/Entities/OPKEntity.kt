package com.szlazakm.safechat.client.data.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "opk_entity")
data class OPKEntity (
    @PrimaryKey()
    val id : Int,
    val privateOPK : ByteArray,
    val publicOPK : ByteArray,
)