package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartmentDao {
    @Query("SELECT * FROM departments")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(department: DepartmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(departments: List<DepartmentEntity>)

    @Query("DELETE FROM departments")
    suspend fun deleteAllDepartments()

    @Transaction
    suspend fun replaceAll(departments: List<DepartmentEntity>) {
        deleteAllDepartments()
        insertAll(departments)
    }

    @Query("DELETE FROM departments WHERE id = :id")
    suspend fun deleteDepartment(id: String)
}
