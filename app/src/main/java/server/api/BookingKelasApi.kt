package server.api

class BookingKelasApi {
    companion object{
        val BASE_URL = MainIpApi.MAIN_IP

        val ADD_DATA = BASE_URL + "bookingKelas"
        val GET_BY_ID_MEMBER = BASE_URL + "bookingKelas/"
        val GET_BY_ID_JADWAL_HARIAN = BASE_URL + "bookingKelas/getByIdJadwalHarian/"
        val CANCEL_BOOKING = BASE_URL + "cancelBookingKelas/"
    }
}