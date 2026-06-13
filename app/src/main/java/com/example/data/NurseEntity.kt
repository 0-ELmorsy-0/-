package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nurses")
data class NurseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val rating: Double,
    val ratingCount: Int,
    val experienceYears: Int,
    val pricePerVisit: Double,
    val gender: String,
    val completedVisits: Int,
    val phone: String,
    val hospitalAffiliation: String,
    val lat: Double,
    val lng: Double,
    val homeDistrict: String
)
