package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GheychiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalon(salon: Salon): Long

    @Update
    suspend fun updateSalon(salon: Salon)

    @Query("SELECT * FROM salons")
    fun getAllSalons(): Flow<List<Salon>>

    @Query("SELECT * FROM salons WHERE id = :id LIMIT 1")
    suspend fun getSalonById(id: Int): Salon?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: SalonService): Long

    @Query("SELECT * FROM services WHERE salonId = :salonId")
    fun getServicesForSalon(salonId: Int): Flow<List<SalonService>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookingRequest(request: BookingRequest): Long

    @Update
    suspend fun updateBookingRequest(request: BookingRequest)

    @Query("SELECT * FROM booking_requests WHERE salonId = :salonId ORDER BY date DESC, time DESC")
    fun getBookingsForSalon(salonId: Int): Flow<List<BookingRequest>>

    @Query("SELECT * FROM booking_requests WHERE customerPhone = :phone ORDER BY date DESC, time DESC")
    fun getBookingsForCustomer(phone: String): Flow<List<BookingRequest>>
}
