package server.model

class HistoryTransaksiMember(
    var jenis_transaksi: String,
    var id_transaksi: String,
    var id_presensi_kelas: String,
    var nama_kelas: String,
    var status_kehadiran: String,
    var jumlah_transaksi: String,
    var jumlah_deposit_kelas: String,
    var tgl_history:String,
    var jam_history:String) {
}
