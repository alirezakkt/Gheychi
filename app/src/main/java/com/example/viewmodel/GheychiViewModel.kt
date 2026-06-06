package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GheychiViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = GheychiRepository(database.gheychiDao())

    val userRole = MutableStateFlow<String?>(null) 
    val currentSalonId = MutableStateFlow<Int?>(null)
    
    val allSalons = repository.getAllSalons().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val isSalonApproved = MutableStateFlow<Boolean?>(null)

    fun loginAs(role: String) {
        userRole.value = role
        if (role == "customer") {
            seedInitialData(seedApproved = true)
        } else if (role == "salon_admin") {
            seedInitialData(seedApproved = false)
        } else if (role == "super_admin") {
            seedInitialData(seedApproved = false)
        }
    }

    private fun seedInitialData(seedApproved: Boolean = true) {
        viewModelScope.launch {
            if (currentSalonId.value == null) {
                val newSalonId = repository.insertSalon(
                    Salon(
                        name = "سالن زیبایی VIP الهام",
                        address = "تهران، زعفرانیه، خیابان آصف",
                        phone = "09121234567",
                        isApproved = seedApproved,
                        licenseImageUrl = if (seedApproved) "url_to_image" else null
                    )
                ).toInt()
                
                repository.insertService(
                    SalonService(
                        salonId = newSalonId,
                        name = "رنگ و لایت تخصصی",
                        durationMinutes = 120,
                        price = "پس از مشاوره",
                        requiresDeposit = true,
                        isColorService = true
                    )
                )

                repository.insertService(
                    SalonService(
                        salonId = newSalonId,
                        name = "کوتاهی ژورنالی",
                        durationMinutes = 45,
                        price = "۵۰۰,۰۰۰ تومان",
                        requiresDeposit = false,
                        isColorService = false
                    )
                )
                
                currentSalonId.value = newSalonId
                isSalonApproved.value = seedApproved
            }
        }
    }

    fun updateSalonApprovalStatus(salonId: Int, isApproved: Boolean) {
        viewModelScope.launch {
            val salon = repository.getSalonById(salonId)
            if (salon != null) {
                repository.updateSalon(salon.copy(isApproved = isApproved))
                if (currentSalonId.value == salonId) {
                    isSalonApproved.value = isApproved
                }
            }
        }
    }

    fun submitSalonLicense(salonId: Int) {
        viewModelScope.launch {
            val salon = repository.getSalonById(salonId)
            if (salon != null) {
                repository.updateSalon(salon.copy(licenseImageUrl = "mock_uploaded_license.jpg"))
            }
        }
    }

    fun getServices(salonId: Int): StateFlow<List<SalonService>> {
        return repository.getServicesForSalon(salonId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun getSalonBookings(salonId: Int): StateFlow<List<BookingRequest>> {
        return repository.getBookingsForSalon(salonId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
    
    fun getCustomerBookings(phone: String): StateFlow<List<BookingRequest>> {
        return repository.getBookingsForCustomer(phone).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun submitBookingRequest(salonId: Int, serviceId: Int, phone: String, name: String, date: String, time: String, notes: String, colorHex: String?) {
        viewModelScope.launch {
            repository.insertBookingRequest(
                BookingRequest(
                    salonId = salonId,
                    customerPhone = phone,
                    customerName = name,
                    serviceId = serviceId,
                    date = date,
                    time = time,
                    notes = notes,
                    selectedColorHex = colorHex,
                    status = "PENDING",
                )
            )
        }
    }

    fun updateBookingStatus(request: BookingRequest, newStatus: String, depositStatus: String? = null) {
        viewModelScope.launch {
            val copy = request.copy(
                status = newStatus,
                depositStatus = depositStatus ?: request.depositStatus
            )
            repository.updateBookingRequest(copy)
            
            if (newStatus == "CONFIRMED") {
                val salonName = repository.getSalonById(request.salonId)?.name ?: "سالن"
                scheduleReminder(salonName, request.date, request.time)
            }
        }
    }

    private fun scheduleReminder(salonName: String, date: String, time: String) {
        val workManager = androidx.work.WorkManager.getInstance(getApplication())
        
        val inputData = androidx.work.Data.Builder()
            .putString("salonName", salonName)
            .putString("date", date)
            .putString("time", time)
            .build()
            
        val reminderRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.worker.ReminderWorker>()
            .setInitialDelay(5, java.util.concurrent.TimeUnit.SECONDS) // For testing purposes, trigger shortly after
            .setInputData(inputData)
            .build()
            
        workManager.enqueue(reminderRequest)
    }
}
