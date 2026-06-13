package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serviceName: String,
    val serviceIconName: String,
    val nurseName: String,
    val nurseRating: Double,
    val nurseExperience: Int,
    val price: Double,
    val date: String,
    val timeSlot: String,
    val address: String,
    val notes: String = "",
    val paymentMethod: String,
    val status: String, // "PENDING", "ON_THE_WAY", "IN_PROGRESS", "COMPLETED"
    val timestamp: Long = System.currentTimeMillis(),
    val rating: Float = 0f,
    val reviewComment: String = ""
)
