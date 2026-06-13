package com.example.data

import kotlinx.coroutines.flow.Flow

class BookingRepository(private val bookingDao: BookingDao) {
    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()

    fun getBookingById(id: Int): Flow<BookingEntity?> = bookingDao.getBookingById(id)

    suspend fun insertBooking(booking: BookingEntity): Long = bookingDao.insertBooking(booking)

    suspend fun updateBooking(booking: BookingEntity) = bookingDao.updateBooking(booking)

    suspend fun updateBookingStatus(id: Int, status: String) = bookingDao.updateBookingStatus(id, status)

    suspend fun updateBookingReview(id: Int, rating: Float, comment: String) = bookingDao.updateBookingReview(id, rating, comment)

    suspend fun clearAllBookings() = bookingDao.clearAllBookings()
}
