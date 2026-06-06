package com.example.data

class GheychiRepository(private val dao: GheychiDao) {
    fun getAllSalons() = dao.getAllSalons()
    suspend fun getSalonById(id: Int) = dao.getSalonById(id)
    suspend fun insertSalon(salon: Salon) = dao.insertSalon(salon)
    suspend fun updateSalon(salon: Salon) = dao.updateSalon(salon)

    fun getServicesForSalon(salonId: Int) = dao.getServicesForSalon(salonId)
    suspend fun insertService(service: SalonService) = dao.insertService(service)

    fun getBookingsForSalon(salonId: Int) = dao.getBookingsForSalon(salonId)
    fun getBookingsForCustomer(phone: String) = dao.getBookingsForCustomer(phone)
    suspend fun insertBookingRequest(request: BookingRequest) = dao.insertBookingRequest(request)
    suspend fun updateBookingRequest(request: BookingRequest) = dao.updateBookingRequest(request)
}
