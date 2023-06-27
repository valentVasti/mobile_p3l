package server.api

class HistoryApi {
    companion object{
        val BASE_URL = MainIpApi.MAIN_IP

        val GET_HISTORY_TRANSAKSI = BASE_URL + "historyTransaksi/"
        val GET_HISTORY_KELAS = BASE_URL + "historyKelas/"
        val GET_HISTORY_INSTRUKTUR = BASE_URL + "historyInstruktur/"
    }
}