package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val longDescription: String,
    val basePrice: Double,
    val duration: String,
    val iconName: String,
    val imageUrl: String
)
