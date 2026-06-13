package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "departments")
data class DepartmentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val serviceIdsText: String // Comma separated list of service IDs
)
