package com.example.composeapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LoginEntity(
    @PrimaryKey(autoGenerate = true) val id : Int,
    @ColumnInfo(name = "login") val login : String,
    @ColumnInfo(name = "update_at") val updateAt : Long,
)
