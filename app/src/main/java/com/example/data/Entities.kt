package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salons")
data class Salon(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String,
    val phone: String,
    val defaultWorkingHours: String = "10:00 - 18:00",
    val isApproved: Boolean = false,
    val licenseImageUrl: String? = null
)

@Entity(tableName = "services")
data class SalonService(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val salonId: Int,
    val name: String,
    val durationMinutes: Int,
    val price: String,
    val requiresDeposit: Boolean,
    val isColorService: Boolean = false
)

@Entity(tableName = "booking_requests")
data class BookingRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val salonId: Int,
    val customerPhone: String,
    val customerName: String,
    val serviceId: Int,
    val date: String,
    val time: String,
    val notes: String,
    val selectedColorHex: String? = null,
    val status: String,
    val depositStatus: String = "NO_DEPOSIT"
)
