package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NurseDao {
    @Query("SELECT * FROM nurses")
    fun getAllNurses(): Flow<List<NurseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNurse(nurse: NurseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(nurses: List<NurseEntity>)

    @Query("DELETE FROM nurses")
    suspend fun deleteAllNurses()

    @Transaction
    suspend fun replaceAll(nurses: List<NurseEntity>) {
        deleteAllNurses()
        insertAll(nurses)
    }

    @Query("DELETE FROM nurses WHERE id = :id")
    suspend fun deleteNurse(id: String)
}
