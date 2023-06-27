package server.model

class BookingKelas(
    var jadwal_harian: JadwalHarian,
    var member: Member,
    var tgl_booking_kelas: String,
    var status: String,
    var created_at:String){
}