package server.model

class IzinInstruktur (
    var id_izin: String,
    var id_jadwal_harian: String,
    var id_instruktur: String,
    var id_instruktur_pengganti: String,
    var status_konfirmasi: String,
    var tgl_izin: String
    ) {
}
