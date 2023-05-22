package com.example.composeapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface LoginDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(login: LoginEntity)

    @Query("SELECT * FROM logins ORDER BY update_at DESC")
    fun loadAll(): Flow<List<LoginEntity>>
}
