package server.model

class JadwalHarian(
    var id_jadwal_harian: String,
    var instruktur: Instruktur,
    var kelas: Kelas,
    var hari_kelas_harian: String,
    var jam_mulai: String,
    var jam_selesai: String,
    var kuota: String,
    var keterangan: String,
    var tgl_kelas: String) {
}