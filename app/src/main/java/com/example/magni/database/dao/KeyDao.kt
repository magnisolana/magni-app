package com.example.magni.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.magni.model.KeyEntity

@Dao
interface KeyDao {
    @Insert
    suspend fun insertKey(keyEntity: KeyEntity)

    @Query("SELECT * FROM keys WHERE id = :keyId")
    suspend fun getKeyById(keyId: Int): KeyEntity?

    @Query("SELECT * FROM keys ORDER BY id DESC LIMIT 1")
    suspend fun getFirstKey(): KeyEntity?
}
