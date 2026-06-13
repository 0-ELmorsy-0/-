package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceEntity>)

    @Query("DELETE FROM services")
    suspend fun deleteAllServices()

    @Transaction
    suspend fun replaceAll(services: List<ServiceEntity>) {
        deleteAllServices()
        insertAll(services)
    }

    @Query("DELETE FROM services WHERE id = :id")
    suspend fun deleteService(id: String)
}
